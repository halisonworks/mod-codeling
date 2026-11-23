package codeling.robot;

import codeling.McCompat;
import codeling.Codeling;
import codeling.Messages;
import codeling.net.RelayPayloads;
import codeling.platform.Platform;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;

/**
 * Robot camera (issues #175 and #176): the server decides who may look through a robot's eyes and ends the view
 * when that stops being allowed. The client only moves its own camera, so the player's body and chunk loading stay
 * where the player is; the range check keeps the view to chunks the client has loaded anyway. Follow mode (issue #177)
 * is the same view with the client's camera behind the robot instead of in its eye; the same checks apply.
 */
public final class RobotCamera {
    /** The game rule {@code codeling:robot_camera}. Lowercase because the loaders' enum rules use the constant names as values. */
    public enum Mode { off, owner, ops }

    public static final int NONE = -1;
    /** agent.camera's CameraMode in the editor: Off 0, Eyes 1, Follow 2. */
    public static final int OFF = 0, EYES = 1, FOLLOW = 2;
    private record View(RobotEntity robot, boolean follow) {
    }
    private static final Map<UUID, View> VIEWING = new HashMap<>();

    private RobotCamera() {
    }

    public static void playerLeft(ServerPlayer player) {
        if (VIEWING.remove(player.getUUID()) != null) Codeling.LOG.info("camera: {} stopped looking through a robot: the player left the game.", player.getGameProfile().name());
    }

    public static void serverStopped() {
        VIEWING.clear();
    }

    /** Why the game rule keeps {@code player} from the camera, or null. */
    static Component ruleRefusal(ServerPlayer player) {
        Mode mode = Codeling.robotCamera(player.level().getServer());
        if (mode == Mode.off) return Messages.of("commands.codeling.camera.off");
        return mode == Mode.ops && !McCompat.isGamemaster(player) ? Messages.of("commands.codeling.camera.ops") : null;
    }

    /** Why {@code player} may not look through {@code robot} now, or null when it is allowed. */
    public static Component refusal(ServerPlayer player, RobotEntity robot) {
        Component rule = ruleRefusal(player);
        if (rule != null) return rule;
        if (robot == null || robot.isRemoved()) return Messages.of("commands.codeling.camera.no_robot", Messages.spawnCommand(""));
        if (!RobotManager.mayOpen(player, robot)) return Messages.of("message.codeling.not_yours");
        if (robot.level() != player.level()) return Messages.of("commands.codeling.camera.dimension");
        ChunkPos chunk = robot.chunkPosition();
        if (!player.getChunkTrackingView().contains(McCompat.chunkX(chunk), McCompat.chunkZ(chunk))) return Messages.of("commands.codeling.camera.too_far");
        return null;
    }

    /** Starts the view through the robot's eyes, or returns why not. */
    public static Component start(ServerPlayer player, RobotEntity robot) {
        return start(player, robot, false);
    }

    /** Starts the view through the robot's eyes or, with {@code follow}, from behind it; or returns why not. */
    public static Component start(ServerPlayer player, RobotEntity robot, boolean follow) {
        Component refusal = refusal(player, robot);
        if (refusal != null) {
            Codeling.LOG.info("camera: {} could not start the camera: {}", player.getGameProfile().name(), refusal.getString());
            return refusal;
        }
        Codeling.LOG.info("camera: {} is now looking {} {}.", player.getGameProfile().name(), follow ? "from behind" : "through the eyes of", RobotManager.label(robot));
        VIEWING.put(player.getUUID(), new View(robot, follow));
        send(player, robot.getId(), follow);
        return null;
    }

    /** agent.camera: a CameraMode for the player's own default robot; null when done, else why not. */
    public static Component set(ServerPlayer player, int mode) {
        return set(player, RobotManager.robot(player.getUUID()), mode);
    }

    /** agent.camera for one robot (issue #141), with the same checks; null when done, else why not. */
    public static Component set(ServerPlayer player, RobotEntity robot, int mode) {
        if (mode == OFF) {
            stop(player, "the program turned the camera off");
            return null;
        }
        return start(player, robot, mode == FOLLOW);
    }

    /** Ends the view; false when the player was not viewing. */
    public static boolean stop(ServerPlayer player) {
        return stop(player, "stopped");
    }

    /** As {@link #stop(ServerPlayer)}; {@code why} goes to the log. */
    public static boolean stop(ServerPlayer player, String why) {
        if (VIEWING.remove(player.getUUID()) == null) return false;
        Codeling.LOG.info("camera: {} stopped looking through a robot: {}.", player.getGameProfile().name(), why);
        send(player, NONE, false);
        return true;
    }

    /** The robot {@code player} is looking through, or null. */
    public static RobotEntity viewing(ServerPlayer player) {
        View view = VIEWING.get(player.getUUID());
        return view == null ? null : view.robot();
    }

    /** True while {@code player} watches their robot in follow mode. */
    public static boolean following(ServerPlayer player) {
        View view = VIEWING.get(player.getUUID());
        return view != null && view.follow();
    }

    // Every tick, so the view ends at once when the robot is removed, unloads, changes dimension or the rule changes.
    public static void tick(MinecraftServer server) {
        VIEWING.entrySet().removeIf(e -> {
            ServerPlayer player = server.getPlayerList().getPlayer(e.getKey());
            if (player == null) return true;
            Component refusal = refusal(player, e.getValue().robot());
            if (refusal == null) return false;
            Codeling.LOG.info("camera: {} stopped looking through a robot: {}", player.getGameProfile().name(), refusal.getString());
            send(player, NONE, false);
            return true;
        });
    }

    // GameTest mock players have no Codeling client, so nothing is sent to them.
    private static void send(ServerPlayer player, int robotId, boolean follow) {
        if (Platform.INSTANCE.canSend(player, RelayPayloads.Camera.TYPE)) Platform.INSTANCE.send(player, new RelayPayloads.Camera(robotId, follow));
    }
}
