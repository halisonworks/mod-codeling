package halisoncode.robot;

import halisoncode.McCompat;
import halisoncode.HalisonCode;
import halisoncode.Messages;
import halisoncode.Permission;
import halisoncode.Report;
import halisoncode.log.Log;
import halisoncode.api.Signals;
import halisoncode.net.PlayerEvents;
import halisoncode.net.RelayPayloads;
import halisoncode.platform.Platform;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

/**
 * {@code /robot} server commands. The robot actions use the same validator and queue as the
 * network commands of step 2; until the relay is wired they are the manual test hook.
 * Issue #140: every command for one robot takes an optional robot name at the end; without it the command is for the
 * player's default robot, as before. Issue #293: agent, and the name the player gave it, mean the default robot too.
 * Only the player's own robots are ever named, so no one controls another's robot.
 */
public final class RobotCommands {
    private static final String[] DIRECTIONS = {"forward", "back", "left", "right", "up", "down"}; // SixDirection order
    private static final String ROBOT = "robot";
    private static final SuggestionProvider<CommandSourceStack> MY_ROBOTS = (c, builder) -> c.getSource().getPlayer() == null
            ? builder.buildFuture()
            : SharedSuggestionProvider.suggest(RobotLinks.get(c.getSource().getServer()).commandNames(c.getSource().getPlayer().getUUID()), builder);

    /** What /robot give takes: our block names and the item ids, without those this Minecraft version lacks. */
    private static final SuggestionProvider<CommandSourceStack> ITEMS = (c, builder) -> SharedSuggestionProvider.suggest(giveNames(), builder);
    private static List<String> giveNames; // made once: the suggestions are asked for at every key press
    private static final DynamicCommandExceptionType UNKNOWN_ITEM = Messages.argumentError("commands.halisoncode.give.unknown");
    private static final DynamicCommandExceptionType BAD_COLOR = Messages.argumentError("commands.halisoncode.color.invalid");
    private static final DynamicCommandExceptionType BAD_SIGNAL = Messages.argumentError("commands.halisoncode.signal.invalid");

    private RobotCommands() {
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralArgumentBuilder<CommandSourceStack> move = Commands.literal("move");
        for (int i = 0; i < DIRECTIONS.length; i++) {
            int direction = i;
            move.then(Commands.literal(DIRECTIONS[i]).then(named(Commands.argument("blocks", IntegerArgumentType.integer(1, RobotCommand.MAX_MOVE)),
                    c -> send(c, "agent.move", List.of(direction, IntegerArgumentType.getInteger(c, "blocks")),
                            either(done("commands.halisoncode.move", robot(c), direction(DIRECTIONS[direction]), IntegerArgumentType.getInteger(c, "blocks")),
                                    refused("commands.halisoncode.move.blocked", robot(c), direction(DIRECTIONS[direction])))))));
        }
        LiteralArgumentBuilder<CommandSourceStack> place = Commands.literal("place");
        LiteralArgumentBuilder<CommandSourceStack> destroy = Commands.literal("destroy");
        LiteralArgumentBuilder<CommandSourceStack> inspect = Commands.literal("inspect");
        LiteralArgumentBuilder<CommandSourceStack> detect = Commands.literal("detect");
        LiteralArgumentBuilder<CommandSourceStack> drop = Commands.literal("drop");
        for (int i = 0; i < DIRECTIONS.length; i++) {
            int d = i;
            place.then(named(Commands.literal(DIRECTIONS[i]), c -> send(c, "agent.place", List.of(d), either(
                    done("commands.halisoncode.place", robot(c), direction(DIRECTIONS[d])), refused("commands.halisoncode.place.failed", robot(c), direction(DIRECTIONS[d]))))));
            destroy.then(named(Commands.literal(DIRECTIONS[i]), c -> send(c, "agent.destroy", List.of(d), either(
                    done("commands.halisoncode.destroy", robot(c), direction(DIRECTIONS[d])), refused("commands.halisoncode.destroy.failed", robot(c), direction(DIRECTIONS[d]))))));
            inspect.then(named(Commands.literal(DIRECTIONS[i]), c -> send(c, "agent.inspect", List.of(RobotCommand.KIND_BLOCK, d), code -> {
                Component block = RobotManager.blockName((Integer) code);
                return block == null ? done("commands.halisoncode.inspect.unknown", direction(DIRECTIONS[d])) : done("commands.halisoncode.inspect", direction(DIRECTIONS[d]), block);
            })));
            detect.then(named(Commands.literal(DIRECTIONS[i]), c -> send(c, "agent.detect", List.of(RobotCommand.KIND_BLOCK, d), either(
                    done("commands.halisoncode.detect.yes", direction(DIRECTIONS[d])), done("commands.halisoncode.detect.no", direction(DIRECTIONS[d]))))));
            drop.then(named(Commands.literal(DIRECTIONS[i]), c -> send(c, "agent.dropAll", List.of(d), either(
                    done("commands.halisoncode.drop", robot(c), direction(DIRECTIONS[d])), refused("commands.halisoncode.drop.failed", robot(c))))));
        }
        LiteralArgumentBuilder<CommandSourceStack> skin = Commands.literal("skin");
        for (String name : RobotAppearance.SKINS) skin.then(named(Commands.literal(name), c -> skin(c, name)));
        LiteralArgumentBuilder<CommandSourceStack> animation = Commands.literal("animation");
        for (String name : RobotAppearance.ANIMATIONS) animation.then(named(Commands.literal(name), c -> animation(c, name)));
        // Issue #27: a chat line for the caller's own program, for servers whose chat plugins take chat lines away. No
        // operator rights needed: it reaches nobody but the caller's program. /halisoncode alone is the client's command.
        // Issue #249: a signal for the programs of the players named, for operators, command blocks and map makers.
        dispatcher.register(Commands.literal("halisoncode").then(Commands.literal("chat")
                .then(Commands.argument("text", StringArgumentType.greedyString()).executes(RobotCommands::chat)))
                .then(Commands.literal("signal").requires(McCompat::isGamemaster).then(Commands.argument("players", EntityArgument.players())
                        .then(Commands.argument("name", StringArgumentType.string()).executes(c -> signal(c, 0))
                                .then(Commands.argument("number", DoubleArgumentType.doubleArg()).executes(c -> signal(c, DoubleArgumentType.getDouble(c, "number")))))))
                // Issue #279: the server's own log; /halisoncode log and /halisoncode report are the client's commands.
                .then(Commands.literal("server").requires(McCompat::isGamemaster)
                        .then(Commands.literal("log")
                                .then(Commands.literal("on").executes(c -> serverLog(c, Log.DEFAULT_MINUTES))
                                        .then(Commands.argument("minutes", IntegerArgumentType.integer(1, Log.MAX_MINUTES))
                                                .executes(c -> serverLog(c, IntegerArgumentType.getInteger(c, "minutes")))))
                                .then(Commands.literal("off").executes(c -> serverLog(c, 0))))
                        .then(Commands.literal("report").executes(RobotCommands::serverReport))));
        // The slot comes before the robot name, so "give stone 5 3" is slot 3 (robot names start with a letter).
        Command<CommandSourceStack> giveToSlot = c -> give(c, IntegerArgumentType.getInteger(c, "slot"));
        // Issue #51: without the permission /robot is an unknown command, as the game's own are. The console may.
        dispatcher.register(Commands.literal("robot").requires(s -> !(s.getEntity() instanceof ServerPlayer p) || Permission.ROBOT.allows(p))
                .then(place).then(destroy).then(inspect).then(detect).then(drop)
                .then(named(Commands.literal("collect"), c -> send(c, "agent.collectAll", List.of(), v -> done("commands.halisoncode.collect", robot(c)))))
                .then(Commands.literal("slot").then(named(Commands.argument("slot", IntegerArgumentType.integer(1, RobotCommand.SLOTS)),
                        c -> send(c, "agent.setSlot", List.of(IntegerArgumentType.getInteger(c, "slot")),
                                v -> done("commands.halisoncode.inventory.slot", robot(c), IntegerArgumentType.getInteger(c, "slot"))))))
                .then(Commands.literal("give").then(Commands.argument("block", StringArgumentType.word()).suggests(ITEMS)
                        .then(Commands.argument("count", IntegerArgumentType.integer(1, RobotCommand.MAX_QUANTITY))
                                .executes(c -> give(c, selectedSlot(c)))
                                .then(named(Commands.argument("slot", IntegerArgumentType.integer(1, RobotCommand.SLOTS)), giveToSlot))
                                .then(robotName().executes(c -> give(c, selectedSlot(c)))))))
                .then(Commands.literal("assist").then(named(Commands.argument("on", BoolArgumentType.bool()),
                        c -> send(c, "agent.setAssist", List.of(RobotCommand.PLACE_ON_MOVE, BoolArgumentType.getBool(c, "on")),
                                v -> done(BoolArgumentType.getBool(c, "on") ? "commands.halisoncode.assist.on" : "commands.halisoncode.assist.off", robot(c))))))
                .then(Commands.literal("gravity").then(named(Commands.argument("on", BoolArgumentType.bool()),
                        c -> send(c, "agent.setGravity", List.of(BoolArgumentType.getBool(c, "on")),
                                v -> done(BoolArgumentType.getBool(c, "on") ? "commands.halisoncode.gravity.on" : "commands.halisoncode.gravity.off", robot(c))))))
                .then(named(Commands.literal("inventory"), RobotCommands::inventory))
                .then(named(Commands.literal("sethome"), c -> send(c, "agent.setHome", List.of(), v -> done("commands.halisoncode.sethome", robot(c)))))
                .then(named(Commands.literal("come"), RobotCommands::come))
                .then(named(Commands.literal("camera"), c -> camera(c, false))
                        .then(named(Commands.literal("follow"), c -> camera(c, true)))
                        .then(Commands.literal("off").executes(c -> {
                            RobotCamera.stop(c.getSource().getPlayerOrException(), "/robot camera off (also sent by the camera key)");
                            return 1;
                        })))
                .then(named(Commands.literal("spawn"), RobotCommands::spawn))
                .then(named(Commands.literal("stop"), RobotCommands::stop)
                        //? if >=1.21.11 {
                        .then(Commands.literal("all").requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                        //?} else
                        /*.then(Commands.literal("all").requires(source -> source.hasPermission(Commands.LEVEL_GAMEMASTERS))*/
                                .executes(c -> {
                                    RobotManager.stopAll();
                                    Messages.operatorSuccess(c.getSource(), Messages.of("commands.halisoncode.stop.all"));
                                    return 1;
                                })))
                .then(named(Commands.literal("status"), RobotCommands::status))
                .then(Commands.literal("list").executes(RobotCommands::list))
                .then(Commands.literal("remove").then(robotName().executes(RobotCommands::remove)))
                // rename <name>: the default robot's name; rename <robot> <name>: that robot's name.
                .then(Commands.literal("rename").then(robotName().executes(RobotCommands::renameDefault)
                        .then(Commands.argument("name", StringArgumentType.word())
                                .executes(c -> rename(c, name(c), StringArgumentType.getString(c, "name"))))))
                .then(Commands.literal("nametag")
                        .then(named(Commands.literal("on"), c -> nametag(c, true)))
                        .then(named(Commands.literal("off"), c -> nametag(c, false))))
                .then(skin)
                .then(Commands.literal("color").then(named(Commands.argument("color", StringArgumentType.word())
                        .suggests((c, builder) -> SharedSuggestionProvider.suggest(List.of("reset"), builder)), RobotCommands::color)))
                .then(animation)
                .then(move)
                .then(Commands.literal("turn")
                        .then(named(Commands.literal("left"), c -> send(c, "agent.turn", List.of(RobotCommand.LEFT_TURN), v -> done("commands.halisoncode.turn", robot(c), direction("left")))))
                        .then(named(Commands.literal("right"), c -> send(c, "agent.turn", List.of(RobotCommand.RIGHT_TURN), v -> done("commands.halisoncode.turn", robot(c), direction("right")))))));
    }

    private static int chat(CommandContext<CommandSourceStack> c) throws CommandSyntaxException {
        ServerPlayer player = c.getSource().getPlayerOrException();
        String text = StringArgumentType.getString(c, "text");
        if (PlayerEvents.chat(player, text)) return 1;
        return Messages.failure(c.getSource(), PlayerEvents.listensForChat(player.getUUID())
                ? Messages.of("commands.halisoncode.chat.unknown", text.strip().split("\\s+")[0])
                : Messages.of("commands.halisoncode.chat.no_program"));
    }

    // Answers how many programs got it (the command's result, for command blocks) and names the players it did not
    // reach, so a map maker sees a typo at once.
    private static int signal(CommandContext<CommandSourceStack> c, double value) throws CommandSyntaxException {
        String name = StringArgumentType.getString(c, "name");
        if (Signals.nameProblem(name) != null) throw BAD_SIGNAL.create(name);
        int reached = 0;
        List<String> missed = new ArrayList<>();
        for (ServerPlayer player : EntityArgument.getPlayers(c, "players")) {
            if (PlayerEvents.signal(player.getUUID(), name, value)) reached++;
            else missed.add(player.getGameProfile().name());
        }
        List<Component> lines = new ArrayList<>();
        lines.add(reached == 0 ? Messages.of("commands.halisoncode.signal.none", name)
                : reached == 1 ? Messages.of("commands.halisoncode.signal.single", name) : Messages.of("commands.halisoncode.signal.multiple", name, reached));
        if (!missed.isEmpty()) lines.add(Messages.of("commands.halisoncode.signal.missed", String.join(", ", missed), RobotQueue.MAX_LENGTH));
        if (reached == 0) return Messages.failure(c.getSource(), Messages.lines(lines));
        Messages.success(c.getSource(), Messages.lines(lines));
        return reached;
    }

    private static com.mojang.brigadier.builder.RequiredArgumentBuilder<CommandSourceStack, String> robotName() {
        return Commands.argument(ROBOT, StringArgumentType.word()).suggests(MY_ROBOTS);
    }

    /** The command, and the same command with a robot name after it. */
    private static <T extends ArgumentBuilder<CommandSourceStack, T>> T named(T node, Command<CommandSourceStack> command) {
        return node.executes(command).then(robotName().executes(command));
    }

    /** The robot the command is for: its name, or "" for the default robot (no name, agent, or the name it was given). */
    private static String name(CommandContext<CommandSourceStack> c) {
        ServerPlayer player = c.getSource().getPlayer();
        return player == null ? typed(c) : RobotLinks.get(c.getSource().getServer()).resolve(player.getUUID(), typed(c));
    }

    /** The robot name the command was given as it was written, or "" when there is none. */
    private static String typed(CommandContext<CommandSourceStack> c) {
        try {
            return StringArgumentType.getString(c, ROBOT);
        } catch (IllegalArgumentException e) {
            return "";
        }
    }

    private static RobotLinks.Key key(CommandContext<CommandSourceStack> c) throws CommandSyntaxException {
        return new RobotLinks.Key(c.getSource().getPlayerOrException().getUUID(), name(c));
    }

    private static Component robot(CommandContext<CommandSourceStack> c) {
        ServerPlayer player = c.getSource().getPlayer();
        return player == null ? Messages.robot(name(c)) : Messages.robot(c.getSource().getServer(), player.getUUID(), name(c));
    }

    /** Answers why there is no loaded robot for this command. */
    private static int absent(CommandContext<CommandSourceStack> c) throws CommandSyntaxException {
        return Messages.failure(c.getSource(), Messages.absent(c.getSource().getServer(), key(c).owner(), name(c)));
    }

    private static int skin(CommandContext<CommandSourceStack> c, String skin) throws CommandSyntaxException {
        RobotEntity robot = RobotManager.robot(key(c));
        if (robot == null) return absent(c);
        robot.setSkin(skin);
        Messages.success(c.getSource(), Messages.of("commands.halisoncode.skin", robot(c), skin));
        return 1;
    }

    private static int color(CommandContext<CommandSourceStack> c) throws CommandSyntaxException {
        String text = StringArgumentType.getString(c, "color");
        int color;
        try {
            color = RobotAppearance.color(text);
        } catch (IllegalArgumentException exception) {
            throw BAD_COLOR.create(text);
        }
        RobotEntity robot = RobotManager.robot(key(c));
        if (robot == null) return absent(c);
        robot.setPrimaryColor(color);
        Messages.success(c.getSource(), Messages.of("commands.halisoncode.color", robot(c), text));
        return 1;
    }

    private static int animation(CommandContext<CommandSourceStack> c, String animation) throws CommandSyntaxException {
        RobotEntity robot = RobotManager.robot(key(c));
        if (robot == null) return absent(c);
        robot.animate(animation);
        Messages.success(c.getSource(), Messages.of("commands.halisoncode.animation", robot(c), animation));
        return 1;
    }

    private static int serverLog(CommandContext<CommandSourceStack> c, int minutes) {
        if (minutes == 0) {
            Log.extendedOff("/halisoncode server log off");
            Messages.operatorSuccess(c.getSource(), Messages.of("commands.halisoncode.log.off"));
        } else {
            Log.extendedOn(minutes);
            Messages.operatorSuccess(c.getSource(), Messages.of("commands.halisoncode.log.on", minutes, "/halisoncode server report"));
        }
        return 1;
    }

    private static int serverReport(CommandContext<CommandSourceStack> c) {
        MinecraftServer server = c.getSource().getServer();
        server.getPlayerList().getPlayers().forEach(p -> Log.person(p.getGameProfile().name()));
        try {
            Report.write(server.getServerDirectory(), server, server.isDedicatedServer() ? "a dedicated server" : "the server in a game");
        } catch (IOException e) {
            halisoncode.HalisonCode.LOG.error("Could not write the report", e);
            return Messages.failure(c.getSource(), Messages.of("commands.halisoncode.report.failed", Report.FILE));
        }
        Messages.success(c.getSource(), Messages.of("commands.halisoncode.report", Report.FILE));
        return 1;
    }

    private static int spawn(CommandContext<CommandSourceStack> c) throws CommandSyntaxException {
        ServerPlayer player = c.getSource().getPlayerOrException();
        String name = name(c);
        Component refusal = RobotManager.spawnRefusal(c.getSource().getServer(), player.getUUID(), name);
        if (refusal != null) return Messages.failure(c.getSource(), refusal);
        halisoncode.HalisonCode.LOG.detail("spawn: {} typed /robot spawn{} while standing at x={} y={} z={} (block {}) in {}, looking {}.", player.getGameProfile().name(),
                name.isEmpty() ? "" : " for robot \"" + name + "\"", String.format(java.util.Locale.ROOT, "%.2f", player.getX()), String.format(java.util.Locale.ROOT, "%.2f", player.getY()),
                String.format(java.util.Locale.ROOT, "%.2f", player.getZ()), player.blockPosition().toShortString().replace(",", ""), McCompat.dimension(McCompat.level(player)).getPath(),
                player.getDirection().getName());
        RobotManager.spawn(McCompat.level(player), key(c), Vec3.atBottomCenterOf(RobotManager.spawnBlock(McCompat.level(player), player.blockPosition())), player.getDirection());
        Messages.success(c.getSource(), Messages.of("commands.halisoncode.spawn", robot(c), direction(player.getDirection().getName())));
        return 1;
    }

    // Issue #77: walks to the player's block, or teleports there when there is no path; same dimension only.
    private static int come(CommandContext<CommandSourceStack> c) throws CommandSyntaxException {
        ServerPlayer player = c.getSource().getPlayerOrException();
        RobotEntity robot = RobotManager.robot(key(c));
        if (robot != null && robot.level() != player.level()) {
            return Messages.failure(c.getSource(), Messages.of("commands.halisoncode.come.dimension", robot(c), Messages.spawnCommand(name(c))));
        }
        BlockPos at = player.blockPosition();
        return send(c, new RobotCommand(RobotCommand.Op.COME, at.getX(), at.getY(), at.getZ()), value -> "walked".equals(value)
                ? done("commands.halisoncode.come.walked", robot(c)) : "teleported".equals(value) ? done("commands.halisoncode.come.teleported", robot(c))
                : refused("commands.halisoncode.come.blocked", robot(c)));
    }

    // Issues #176 and #177: toggles the view through (or, with follow, behind) the player's robot; the other mode or
    // another robot switches over. The client's keys use /robot camera off.
    private static int camera(CommandContext<CommandSourceStack> c, boolean follow) throws CommandSyntaxException {
        ServerPlayer player = c.getSource().getPlayerOrException();
        RobotEntity robot = RobotManager.robot(key(c));
        if (robot != null && RobotCamera.viewing(player) == robot && RobotCamera.following(player) == follow) return RobotCamera.stop(player, "toggled off with /robot camera") ? 1 : 0;
        if (!Platform.INSTANCE.canSend(player, RelayPayloads.Camera.TYPE)) {
            return Messages.failure(c.getSource(), Messages.of("commands.halisoncode.camera.no_client"));
        }
        if (robot == null && RobotCamera.ruleRefusal(player) == null) return absent(c);
        Component refusal = RobotCamera.start(player, robot, follow);
        return refusal == null ? 1 : Messages.failure(c.getSource(), refusal);
    }

    // Without a name every robot of the player stops (and the editor's run ends); with a name only that robot's queue.
    private static int stop(CommandContext<CommandSourceStack> c) throws CommandSyntaxException {
        UUID owner = c.getSource().getPlayerOrException().getUUID();
        if (typed(c).isEmpty()) {
            for (RobotQueue queue : RobotManager.queues(owner)) queue.clear(Result.Code.STOPPED, "stopped with /robot stop"); // the editor shows why
            RobotManager.stop(owner, "/robot stop");
        } else {
            if (RobotLinks.get(c.getSource().getServer()).robot(key(c)) == null) return absent(c);
            RobotManager.queue(key(c)).clear(Result.Code.STOPPED, "stopped with " + (name(c).isEmpty() ? "/robot stop agent" : "/robot stop " + name(c)));
        }
        Messages.success(c.getSource(), Messages.of("commands.halisoncode.stop", typed(c).isEmpty() ? Messages.robot("") : robot(c)));
        return 1;
    }

    private static int status(CommandContext<CommandSourceStack> c) throws CommandSyntaxException {
        RobotLinks.Key key = key(c);
        RobotEntity robot = RobotManager.robot(key);
        if (robot == null) return absent(c);
        RobotQueue queue = RobotManager.queue(key);
        Messages.success(c.getSource(), Messages.of("commands.halisoncode.status", robot(c), robot.blockPosition().toShortString(),
                McCompat.dimension(robot.level()).toString(), direction(robot.getDirection().getName()), queue.size()));
        return 1;
    }

    // Issue #140: the player's robots, where they are, and how many the world allows.
    private static int list(CommandContext<CommandSourceStack> c) throws CommandSyntaxException {
        UUID owner = c.getSource().getPlayerOrException().getUUID();
        RobotLinks links = RobotLinks.get(c.getSource().getServer());
        List<String> names = new java.util.ArrayList<>(links.names(owner));
        if (links.robot(owner) != null) names.addFirst("");
        if (names.isEmpty()) {
            Messages.success(c.getSource(), Messages.of("message.halisoncode.robot.none", Messages.spawnCommand("")));
            return 0;
        }
        List<Component> lines = new ArrayList<>();
        lines.add(Messages.of("commands.halisoncode.list", names.size(), HalisonCode.maxRobots(c.getSource().getServer())));
        for (String name : names) {
            RobotEntity robot = RobotManager.robot(new RobotLinks.Key(owner, name));
            Object shown = !name.isEmpty() ? name : links.defaultName(owner).isEmpty() ? Messages.of("commands.halisoncode.list.default")
                    : Messages.of("commands.halisoncode.list.default.named", links.defaultName(owner));
            lines.add(robot == null ? Messages.of("commands.halisoncode.list.unloaded", shown)
                    : Messages.of("commands.halisoncode.list.at", shown, robot.blockPosition().toShortString(), McCompat.dimension(robot.level()).toString()));
        }
        Messages.success(c.getSource(), Messages.lines(lines));
        return names.size();
    }

    private static int remove(CommandContext<CommandSourceStack> c) throws CommandSyntaxException {
        Component refusal = RobotManager.remove(c.getSource().getPlayerOrException(), name(c));
        if (refusal != null) return Messages.failure(c.getSource(), refusal);
        Messages.success(c.getSource(), Messages.of("commands.halisoncode.remove", robot(c)));
        return 1;
    }

    private static int rename(CommandContext<CommandSourceStack> c, String from, String to) throws CommandSyntaxException {
        UUID owner = c.getSource().getPlayerOrException().getUUID();
        Component before = Messages.robot(c.getSource().getServer(), owner, from);
        Component refusal = RobotManager.rename(c.getSource().getServer(), new RobotLinks.Key(owner, from), to);
        if (refusal != null) return Messages.failure(c.getSource(), refusal);
        Messages.success(c.getSource(), Messages.of("commands.halisoncode.rename", before, to));
        return 1;
    }

    // Issue #293: one word that is one of the player's robots is a rename without the new name, not a name for the default robot.
    private static int renameDefault(CommandContext<CommandSourceStack> c) throws CommandSyntaxException {
        UUID owner = c.getSource().getPlayerOrException().getUUID();
        String word = typed(c);
        RobotLinks links = RobotLinks.get(c.getSource().getServer());
        if (links.robot(new RobotLinks.Key(owner, links.resolve(owner, word))) != null) {
            return Messages.failure(c.getSource(), Messages.of("commands.halisoncode.rename.no_name", "/robot rename " + word + " <name>"));
        }
        return rename(c, "", word);
    }

    // Issue #151: shows or hides the name over the robot; without a name for all of the player's loaded robots.
    private static int nametag(CommandContext<CommandSourceStack> c, boolean on) throws CommandSyntaxException {
        UUID owner = c.getSource().getPlayerOrException().getUUID();
        List<RobotEntity> robots = typed(c).isEmpty() ? RobotManager.robots(owner)
                : RobotManager.robot(key(c)) == null ? List.of() : List.of(RobotManager.robot(key(c)));
        if (robots.isEmpty()) return absent(c);
        robots.forEach(r -> r.setCustomNameVisible(on));
        Messages.success(c.getSource(), Messages.of(on ? "commands.halisoncode.nametag.on" : "commands.halisoncode.nametag.off"));
        return 1;
    }

    private static int selectedSlot(CommandContext<CommandSourceStack> c) throws CommandSyntaxException {
        RobotEntity robot = RobotManager.robot(key(c));
        return robot == null ? 1 : robot.selectedSlot();
    }

    // <block> is one of our block constant names (stone, oak_planks, white_wool), an item id (iron_ingot) or a number.
    private static int give(CommandContext<CommandSourceStack> c, int slot) throws CommandSyntaxException {
        String block = StringArgumentType.getString(c, "block");
        int count = IntegerArgumentType.getInteger(c, "count");
        int code = itemCode(block);
        RobotCommand command;
        try {
            command = RobotCommand.parse("agent.setItem", List.of(code, count, slot));
        } catch (RobotException | NumberFormatException e) {
            throw UNKNOWN_ITEM.create(block); // the count and the slot are in range by their arguments
        }
        return send(c, command, value -> code == 0 ? done("commands.halisoncode.give.empty", robot(c), slot)
                : done("commands.halisoncode.give", robot(c), count, RobotManager.itemName(code), slot));
    }

    private static List<String> giveNames() {
        if (giveNames == null) {
            giveNames = Stream.concat(BlockIds.modernIds().stream(), ItemIds.modernIds().stream()).filter(ItemIds::inThisVersion)
                    .map(id -> id.substring("minecraft:".length())).distinct().filter(id -> itemCode(id) != BlockIds.UNKNOWN).toList();
        }
        return giveNames;
    }

    private static int itemCode(String block) {
        if (block.matches("-?\\d{1,9}")) return Integer.parseInt(block);
        int code = BlockIds.byName(block);
        return code == BlockIds.UNKNOWN ? ItemIds.byName(block) : code;
    }

    // Opens the inventory screen when the robot is within reach (issue #69), else prints the slots.
    private static int inventory(CommandContext<CommandSourceStack> c) throws CommandSyntaxException {
        ServerPlayer player = c.getSource().getPlayerOrException();
        RobotEntity robot = RobotManager.robot(key(c));
        if (robot == null) return absent(c);
        if (robot.level() == player.level() && McCompat.inReach(player, robot, 0) && RobotManager.openInventory(player, robot)) {
            return 1;
        }
        List<Component> lines = new ArrayList<>();
        lines.add(Messages.of("commands.halisoncode.inventory.slot", robot(c), robot.selectedSlot()));
        if (robot.placeOnMove()) lines.add(Messages.of("commands.halisoncode.assist.on", robot(c)));
        boolean empty = true;
        for (int i = 0; i < robot.inventory().getContainerSize(); i++) {
            ItemStack stack = robot.inventory().getItem(i);
            if (stack.isEmpty()) continue;
            empty = false;
            lines.add(Messages.of("commands.halisoncode.inventory.line", i + 1, stack.getCount(), stack.getHoverName()));
        }
        if (empty) lines.add(Messages.of("commands.halisoncode.inventory.empty"));
        Messages.success(c.getSource(), Messages.lines(lines));
        return 1;
    }

    private static Component direction(String name) {
        return Messages.of("message.halisoncode.direction." + name);
    }

    /** What the player is told about a finished action: white when it was done, red when the robot could not do it. */
    private record Reply(boolean done, Component text) {
    }

    private static Reply done(String key, Object... args) {
        return new Reply(true, Messages.of(key, args));
    }

    private static Reply refused(String key, Object... args) {
        return new Reply(false, Messages.of(key, args));
    }

    /** The reply of an action that answers true or false. */
    private static Function<Object, Reply> either(Reply yes, Reply no) {
        return value -> Boolean.TRUE.equals(value) ? yes : no;
    }

    private static int send(CommandContext<CommandSourceStack> c, String op, List<?> args, Function<Object, Reply> reply) throws CommandSyntaxException {
        RobotCommand command;
        try {
            command = RobotCommand.parse(op, args);
        } catch (RobotException e) {
            return failed(c, e.code);
        }
        return send(c, command, reply);
    }

    private static int send(CommandContext<CommandSourceStack> c, RobotCommand command, Function<Object, Reply> reply) throws CommandSyntaxException {
        CommandSourceStack source = c.getSource();
        RobotLinks.Key key = key(c);
        if (RobotManager.robot(key) == null) return absent(c);
        try {
            RobotManager.submit(key, RobotManager.queue(key).run(), command, result -> {
                if (!result.ok()) {
                    failed(c, result.code());
                    return;
                }
                Reply r = reply.apply(result.value());
                if (r.done()) Messages.success(source, r.text());
                else Messages.failure(source, r.text());
            });
            return 1;
        } catch (RobotException e) {
            return failed(c, e.code);
        }
    }

    // The editor shows the code and the validator's text (programs use them, issue #218); a command says it in plain words.
    private static int failed(CommandContext<CommandSourceStack> c, Result.Code code) {
        return Messages.failure(c.getSource(), switch (code) {
            case UNLOADED -> Messages.of("commands.halisoncode.failed.unloaded", robot(c));
            case RATE_LIMITED -> Messages.of("commands.halisoncode.failed.busy", robot(c), "/robot stop");
            case STOPPED -> Messages.of("commands.halisoncode.stop", robot(c));
            case NO_PERMISSION -> Messages.of("commands.halisoncode.give.no_permission", "halisoncode:free_items");
            default -> Messages.of("commands.halisoncode.failed", robot(c));
        });
    }
}
