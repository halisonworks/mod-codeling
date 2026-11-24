package codeling.client;

import codeling.Codeling;
import codeling.Messages;
import codeling.Report;
import codeling.log.Log;
import codeling.net.Protocol;
import codeling.net.RelayPayloads;
import codeling.relay.EditorLauncher;
import codeling.relay.RelayServer;
import codeling.platform.ClientPlatform;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
//? if >=26.3 {
import com.mojang.blaze3d.Blaze3D;
//?} else if >=1.21.11 {
/*import net.minecraft.util.Util;
*///?} else
/*import net.minecraft.Util;*/
import java.io.IOException;

/**
 * The client half of the mod: the editor and relay server. Each loader's client entrypoint registers the renderer,
 * screen, keys and HUD and calls these hooks from its own events (issue #186).
 */
public final class CodelingClient {
    private static RelayServer relay;
    /** Issue #192: the linked editor was told that the game is paused. */
    private static boolean toldPaused;

    private CodelingClient() {
    }

    /** The running relay, or null. For the end-to-end client GameTest: its editor URL holds the token, never log it. */
    public static RelayServer relay() {
        return relay;
    }

    /** The game has started: start the editor and relay server. */
    public static void started(Minecraft client) {
        int port = Integer.getInteger("codeling.port", RelayServer.DEFAULT_PORT);
        EditorLauncher.deleteAll(EditorLauncher.DIR); // left by a crash
        try {
            relay = new RelayServer(port, new GameLink(client)).start();
            Codeling.LOG.info("Codeling editor on http://127.0.0.1:{}/", port);
        } catch (Exception e) {
            // shortcut: log only; show an in-game error when the port is busy (not done in step 2).
            Codeling.LOG.error("Codeling could not start the editor server on port {}", port, e);
        }
    }

    public static void stopping() {
        if (relay != null) relay.close();
        EditorLauncher.deleteAll(EditorLauncher.DIR);
    }

    /** A {@code codeling:to_client} payload goes to the browser session. Runs on the render thread; deliver is thread-safe. */
    public static void receive(String json) {
        if (Codeling.LOG.detailed()) Codeling.LOG.detail("client: from the server: {}", Protocol.describe(json));
        if (relay != null) relay.deliver(json);
    }

    /** True while a program runs from the editor: losing focus to the browser must not pause the game (issue #192). */
    public static boolean keepRunning() {
        return relay != null && relay.linked();
    }

    /**
     * Whether the "pause on lost focus" option counts: not while a program runs (issue #192), and not in a world whose
     * game rule codeling:pause_on_lost_focus is false (issue #297). Only the player who runs the world has the rule here:
     * a server does not send its rules, and it does not stop for this. The option itself is never changed.
     */
    public static boolean mayPauseOnLostFocus() {
        if (keepRunning()) return false;
        Minecraft client = Minecraft.getInstance();
        MinecraftServer server = client.getSingleplayerServer();
        return server == null || client.level == null || Codeling.pauseOnLostFocus(server);
    }

    /**
     * Each client tick (issue #192): when singleplayer is paused (the pause menu is open) while a program runs, the
     * integrated server stops and the robot's commands wait. Tell the editor once, and again when the game goes on;
     * the waiting commands then run.
     */
    public static void tick(Minecraft client) {
        Log.tick();
        boolean paused = keepRunning() && client.isPaused();
        if (paused == toldPaused) return;
        toldPaused = paused;
        relay.deliver(Protocol.control("notice", -1, paused ? "Minecraft is paused. Go back to the game and close the menu."
                : "Minecraft is running again."));
    }

    /** Leaving the world ends the run on the server; tell the browser so the sim does not wait forever. */
    public static void disconnected() {
        if (relay != null) relay.deliver(Protocol.control("stop", -1, "You left the world, so the program stopped."));
    }

    // shortcut: /codeling client command instead of a key binding; add a key when the HUD comes.
    /** The {@code /codeling} client commands, for either loader's own command source. */
    public static <S> LiteralArgumentBuilder<S> command() {
        return LiteralArgumentBuilder.<S>literal("codeling").executes(c -> openEditor())
                .then(LiteralArgumentBuilder.<S>literal("log")
                        .then(LiteralArgumentBuilder.<S>literal("on").executes(c -> extendedLog(Log.DEFAULT_MINUTES))
                                .then(RequiredArgumentBuilder.<S, Integer>argument("minutes", IntegerArgumentType.integer(1, Log.MAX_MINUTES))
                                        .executes(c -> extendedLog(IntegerArgumentType.getInteger(c, "minutes")))))
                        .then(LiteralArgumentBuilder.<S>literal("off").executes(c -> extendedLog(0))))
                .then(LiteralArgumentBuilder.<S>literal("report").executes(c -> report()));
    }

    private static int extendedLog(int minutes) {
        if (minutes == 0) {
            Log.extendedOff("/codeling log off");
            show(Messages.of("commands.codeling.log.off"));
        } else {
            Log.extendedOn(minutes);
            show(Messages.of("commands.codeling.log.on", minutes, "/codeling report"));
        }
        return 1;
    }

    /** Writes the report (issue #279); the chat line copies its text for a bug report. */
    private static int report() {
        Minecraft client = Minecraft.getInstance();
        Log.person(client.getUser().getName());
        try {
            String text = Report.write(client.gameDirectory.toPath(), client.getSingleplayerServer(),
                    client.getConnection() == null ? "the game, in no world" : client.hasSingleplayerServer() ? "the game, in its own world" : "the game, on a server",
                    relay == null ? "Editor server: not running" : "Editor server: running on port " + relay.port() + (relay.linked() ? ", a program is running" : ""));
            show(Messages.of("message.codeling.report", Report.FILE, copy("message.codeling.report.copy", text)));
            return 1;
        } catch (IOException e) {
            Codeling.LOG.error("Could not write the report", e);
            show(Messages.of("commands.codeling.report.failed", Report.FILE));
            return 0;
        }
    }

    /**
     * The {@code /codeling} client command: opens the editor in the browser through a one-time launcher file, so the
     * token is never in a command line (issue #191). The chat line copies the link for a browser that cannot read the
     * file, such as a snap or Flatpak browser on Linux (no access to /tmp).
     */
    public static int openEditor() {
        if (relay == null) return 0;
        try {
            //? if >=26.3 {
            EditorLauncher.launch(EditorLauncher.DIR, relay.editorUrl(), Blaze3D::openPath);
            //?} else
            /*EditorLauncher.launch(EditorLauncher.DIR, relay.editorUrl(), Util.getPlatform()::openPath);*/
        } catch (IOException e) {
            Codeling.LOG.error("Codeling could not write the editor launcher", e); // a path, never the token
        }
        show(Messages.of("message.codeling.editor.opening", copy("message.codeling.editor.copy", relay.editorUrl())));
        return 1;
    }

    /** A text whose click copies {@code copied}; the chat log gets only the visible text. */
    private static Component copy(String key, String copied) {
        //? if >=1.21.5 {
        return Messages.of(key).withStyle(s -> s.withClickEvent(new ClickEvent.CopyToClipboard(copied)).withUnderlined(true));
        //?} else
        /*return Messages.of(key).withStyle(s -> s.withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, copied)).withUnderlined(true));*/
    }

    /** A line in this player's chat. */
    private static void show(Component message) {
        Minecraft client = Minecraft.getInstance();
        //? if >=26.2 {
        client.gui.hud.getChat().addClientSystemMessage(message);
        //?} else if >=26.1 {
        /*client.gui.getChat().addClientSystemMessage(message);
        *///?} else {
        /*client.gui.getChat().addMessage(message);
        *///?}
    }

    /** Sends browser messages to the logical server as payloads (singleplayer or a server with Codeling). */
    private record GameLink(Minecraft client) implements RelayServer.Link {
        @Override
        public String playerName() {
            return client.getUser().getName();
        }

        // Called on the relay's Netty thread: checks here are best effort, the send itself runs on the render thread.
        @Override
        public String send(String json) {
            if (client.getConnection() == null) return "Not in a world. Open a world in Minecraft, then run the program again.";
            try {
                if (!ClientPlatform.INSTANCE.canSend(RelayPayloads.ToServer.TYPE)) return "This server does not have Codeling installed.";
            } catch (IllegalStateException e) {
                return "Not in a world. Open a world in Minecraft, then run the program again.";
            }
            client.execute(() -> {
                if (client.getConnection() == null) return;
                if (Codeling.LOG.detailed() && !json.contains("\"heartbeat\"")) Codeling.LOG.detail("client: to the server: {}", Protocol.describe(json));
                ClientPlatform.INSTANCE.send(new RelayPayloads.ToServer(json));
            });
            return null;
        }
    }
}
