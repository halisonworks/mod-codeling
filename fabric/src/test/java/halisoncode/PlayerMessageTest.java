package halisoncode;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/** Issue #278: every message to a player goes through {@code halisoncode.Messages}, with a key in en_us.json. */
class PlayerMessageTest {
    private static final Pattern SENDS = Pattern.compile(
            "Component\\.(literal|translatable\\w*)\\(|\\.(sendFailure|sendSuccess|sendSystemMessage|sendOverlayMessage|displayClientMessage)\\(|SimpleCommandExceptionType|new DynamicCommandExceptionType");
    /** Text that is not Halison Code's own: the program's words, and the name the player gave a robot. The camera HUD draws. */
    private static final List<String> ALLOWED = List.of(
            "player.sendSystemMessage(Component.literal(c.text()))",
            "lines.add(Component.literal(entity.said()))",
            "if (Component.literal(name).equals(getCustomName())) return;",
            "setCustomName(Component.literal(name))",
            "Component.translatable(\"hud.halisoncode.camera\", robot.getDisplayName())");
    private static final Pattern KEY = Pattern.compile("\"((?:commands|message)\\.halisoncode\\.[a-z_.]+)\"");

    private static Path root() {
        Path dir = Path.of("").toAbsolutePath();
        while (!Files.exists(dir.resolve("settings.gradle.kts"))) dir = dir.getParent();
        return dir;
    }

    /** Every Java file of the mod that can talk to a player, by path. */
    private static TreeMap<String, String> sources() throws IOException {
        TreeMap<String, String> sources = new TreeMap<>();
        for (String dir : List.of("common/src/main/java", "common/src/client/java", "fabric/src/main/java", "fabric/src/client/java", "neoforge/src/main/java")) {
            Path path = root().resolve(dir);
            if (!Files.exists(path)) continue;
            try (Stream<Path> files = Files.walk(path)) {
                for (Path file : files.filter(f -> f.toString().endsWith(".java")).toList()) {
                    sources.put(root().relativize(file).toString().replace('\\', '/'), Files.readString(file));
                }
            }
        }
        assertTrue(sources.containsKey("common/src/main/java/halisoncode/Messages.java"), "sources found: " + sources.keySet());
        return sources;
    }

    private static JsonObject english() throws IOException {
        try (InputStream in = PlayerMessageTest.class.getResourceAsStream("/assets/halisoncode/lang/en_us.json")) {
            return JsonParser.parseReader(new InputStreamReader(in, StandardCharsets.UTF_8)).getAsJsonObject();
        }
    }

    @Test
    void onlyMessagesTalksToPlayers() throws IOException {
        List<String> found = new ArrayList<>();
        sources().forEach((file, text) -> {
            if (file.endsWith("/halisoncode/Messages.java")) return;
            for (String line : text.split("\n")) {
                String rest = line;
                for (String allowed : ALLOWED) rest = rest.replace(allowed, "");
                if (SENDS.matcher(rest).find()) found.add(file + ": " + line.strip());
            }
        });
        assertEquals(List.of(), found, "send it with halisoncode.Messages and a key in en_us.json");
    }

    @Test
    void everyKeyHasATextAndEveryTextIsUsed() throws IOException {
        JsonObject english = english();
        Set<String> used = new HashSet<>();
        for (String text : sources().values()) {
            Matcher m = KEY.matcher(text);
            while (m.find()) used.add(m.group(1));
        }
        List<String> missing = used.stream().filter(k -> !k.endsWith(".") && !english.has(k)).sorted().toList();
        assertEquals(List.of(), missing, "keys without a text");
        List<String> unused = english.keySet().stream().filter(k -> k.startsWith("commands.") || k.startsWith("message."))
                .filter(k -> !used.contains(k) && !used.contains(k.substring(0, k.lastIndexOf('.') + 1))).sorted().toList();
        assertEquals(List.of(), unused, "texts no code uses");
    }

    @Test
    void textsReadLikeTheGames() throws IOException {
        for (var e : english().entrySet()) {
            String text = e.getValue().getAsString();
            assertFalse(text.endsWith("."), e.getKey() + ": the game's messages have no full stop at the end");
            assertFalse(Pattern.compile("agent\\.|[A-Z]+_[A-Z]+|\\b(UNLOADED|STOPPED|PROTECTED)\\b").matcher(text).find(), e.getKey() + ": no internal codes: " + text);
            assertFalse(Pattern.compile("%(?!s)").matcher(text).find(), e.getKey() + ": only %s works in a translation");
        }
    }
}
