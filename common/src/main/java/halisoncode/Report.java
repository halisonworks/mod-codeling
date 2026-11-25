package halisoncode;

import halisoncode.log.Log;
import halisoncode.platform.Platform;
import halisoncode.robot.RobotManager;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.server.MinecraftServer;

/** Issue #279: what a bug report needs, in one file the player reads through and sends. */
public final class Report {
    /** Inside the game's or the server's folder. */
    public static final String FILE = "logs/halisoncode-report.txt";

    private Report() {
    }

    /**
     * Writes the report into {@code folder} and answers its text. {@code server} is null on a client in no world or on
     * somebody else's server; {@code where} and {@code more} are lines about this side.
     */
    public static String write(Path folder, MinecraftServer server, String where, String... more) throws IOException {
        String text = text(server, where, more);
        Path file = folder.resolve(FILE);
        Files.createDirectories(file.getParent());
        Files.writeString(file, text);
        return text;
    }

    static String text(MinecraftServer server, String where, String... more) {
        List<String> lines = new ArrayList<>(List.of("Halison Code report",
                "Read through this before you send it. Player names, robot names and ids are left out where Halison Code knows them.", "",
                "Time: " + Instant.now().truncatedTo(ChronoUnit.SECONDS)));
        Platform.INSTANCE.versions().forEach((name, version) -> lines.add(name + ": " + version));
        lines.add("Java: " + System.getProperty("java.version") + " (" + System.getProperty("java.vendor") + ")");
        lines.add("Operating system: " + System.getProperty("os.name") + " " + System.getProperty("os.version") + " " + System.getProperty("os.arch"));
        lines.add("Where: " + where);
        lines.addAll(List.of(more));
        if (server == null) {
            lines.add("Game rules: not known here (no world, or somebody else's server)");
        } else {
            lines.add("Game rules: free_items " + HalisonCode.freeItems(server) + ", max_robots " + HalisonCode.maxRobots(server)
                    + ", robot_camera " + HalisonCode.robotCamera(server)
                    + ", pause_on_lost_focus " + HalisonCode.pauseOnLostFocus(server));
            lines.add("Server limits: " + HalisonCode.limits());
            lines.add("Robots in the loaded world: " + RobotManager.loadedCount() + ", programs running: " + RobotManager.runCount());
        }
        lines.add("Extended logging: " + (Log.extended() ? "on" : "off"));
        lines.add("");
        lines.add("The newest Halison Code log lines:");
        lines.addAll(Log.report());
        return String.join("\n", lines) + "\n";
    }
}
