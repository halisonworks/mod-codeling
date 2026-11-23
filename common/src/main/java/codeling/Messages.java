package codeling;

import codeling.robot.RobotLinks;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

/**
 * Issue #278: everything Codeling says to a player goes through here, so it looks and reads like the game's own
 * messages (docs/architecture.md section 11 lists the kinds). PlayerMessageTest fails when other code sends one itself.
 */
public final class Messages {
    private static final JsonObject ENGLISH = english();

    private Messages() {
    }

    private static JsonObject english() {
        try (InputStream in = Messages.class.getResourceAsStream("/assets/codeling/lang/en_us.json")) {
            return JsonParser.parseReader(new InputStreamReader(in, StandardCharsets.UTF_8)).getAsJsonObject();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /** A message in the reader's language. It carries the English text, for vanilla clients that have no Codeling keys. */
    public static MutableComponent of(String key, Object... args) {
        return Component.translatableWithFallback(key, ENGLISH.get(key).getAsString(), args);
    }

    /** Success: white, and silent when the game rule sendCommandFeedback is off. Only the caller sees it. */
    public static void success(CommandSourceStack source, Component text) {
        source.sendSuccess(() -> text, false);
    }

    /** Success of an operator's command for the whole server: other operators are told too, as vanilla does. */
    public static void operatorSuccess(CommandSourceStack source, Component text) {
        source.sendSuccess(() -> text, true);
    }

    /** Refusal: red. Says what happened and what to do next. Answers 0, a failed command's result. */
    public static int failure(CommandSourceStack source, Component text) {
        source.sendFailure(text);
        return 0;
    }

    /** A wrong argument: red, as the game's own argument errors. */
    public static DynamicCommandExceptionType argumentError(String key) {
        return new DynamicCommandExceptionType(value -> of(key, value));
    }

    /** A refusal of something the player did with the mouse: above the hotbar, as for a locked chest. */
    public static void actionBar(ServerPlayer player, Component text) {
        player.sendSystemMessage(text, true);
    }

    /** One message of several lines. */
    public static Component lines(List<Component> lines) {
        MutableComponent all = Component.empty();
        for (int i = 0; i < lines.size(); i++) {
            if (i > 0) all.append("\n");
            all.append(lines.get(i));
        }
        return all;
    }

    /** "Robot" or "Robot Miner". */
    public static Component robot(String name) {
        return name.isEmpty() ? of("entity.codeling.robot") : of("message.codeling.robot.named", name);
    }

    /** "/robot spawn" or "/robot spawn Miner". */
    public static String spawnCommand(String name) {
        return name.isEmpty() ? "/robot spawn" : "/robot spawn " + name;
    }

    /** Why a robot that is not in the loaded world cannot be used, and what to do; the same words everywhere. */
    public static Component absent(MinecraftServer server, UUID owner, String name) {
        if (RobotLinks.get(server).robot(new RobotLinks.Key(owner, name)) != null) {
            return of("message.codeling.robot.unloaded", robot(name), spawnCommand(name));
        }
        return name.isEmpty() ? of("message.codeling.robot.none", spawnCommand(name)) : of("message.codeling.robot.unknown", name);
    }
}
