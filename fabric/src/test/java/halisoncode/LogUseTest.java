package halisoncode;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/** Issue #279: main code logs through halisoncode.log.Log only, and never what must not be in a log. */
class LogUseTest {
    private static final Pattern OTHER = Pattern.compile("LoggerFactory|org\\.slf4j|System\\.(out|err)\\b|printStackTrace|java\\.util\\.logging");
    /** A call of Log with the editor's address or token, or the program's or a player's own words. */
    private static final Pattern PRIVATE = Pattern.compile("LOG\\.\\w+\\(.*(editorUrl\\(|\\btoken\\b|\\.text\\(\\)|\\.said\\(\\)|signedContent|getRawText|, json\\b|, text\\b)");

    private static final Pattern STRINGS_AND_COMMENTS = Pattern.compile("\"(?:\\\\.|[^\"\\\\])*\"|//.*");

    private static Path root() {
        Path dir = Path.of("").toAbsolutePath();
        while (!Files.exists(dir.resolve("settings.gradle.kts"))) dir = dir.getParent();
        return dir;
    }

    @Test
    void mainCodeLogsThroughLogAndNothingPrivate() throws IOException {
        List<String> found = new ArrayList<>();
        int files = 0;
        for (String dir : List.of("core/src/main/java", "common/src/main/java", "common/src/client/java", "fabric/src/main/java", "fabric/src/client/java", "neoforge/src/main/java")) {
            try (Stream<Path> paths = Files.walk(root().resolve(dir))) {
                for (Path path : paths.filter(p -> p.toString().endsWith(".java")).toList()) {
                    files++;
                    String name = path.getFileName().toString();
                    for (String line : Files.readString(path).split("\n")) {
                        // StandaloneRelay is a program of its own: it prints the address for the one who started it.
                        if (!name.equals("Log.java") && !name.equals("StandaloneRelay.java") && OTHER.matcher(line).find()) found.add(name + ": " + line.strip());
                        if (PRIVATE.matcher(STRINGS_AND_COMMENTS.matcher(line).replaceAll("")).find()) found.add(name + ": " + line.strip());
                    }
                }
            }
        }
        assertTrue(files > 50, "files read: " + files);
        assertEquals(List.of(), found);
    }
}
