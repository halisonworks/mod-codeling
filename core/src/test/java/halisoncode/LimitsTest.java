package halisoncode;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;

import com.google.gson.JsonParser;
import halisoncode.log.Log;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Issue #52: config/halisoncode-server.json. */
class LimitsTest {
    private static final Log LOG = Log.of("halisoncode");

    @TempDir
    Path dir;

    private Limits read(String json) throws IOException {
        Files.writeString(dir.resolve(Limits.FILE), json);
        return Limits.load(dir, LOG);
    }

    @Test
    void writesTheDefaultsWhenThereIsNoFile() throws IOException {
        Path config = dir.resolve("new");
        assertSame(Limits.DEFAULTS, Limits.load(config, LOG));
        String written = Files.readString(config.resolve(Limits.FILE));
        assertEquals(64, JsonParser.parseString(written).getAsJsonObject().get("actionsPerTick").getAsInt());
        assertEquals("the defaults", Limits.load(config, LOG).toString(), "what it wrote reads as the defaults");
    }

    @Test
    void readsWhatIsSetAndKeepsTheRest() throws IOException {
        Limits limits = read("{\"buildBlocksPerTick\": 32, \"queueLength\": 4.0, \"unknown\": 1}");
        assertEquals(32, limits.buildBlocksPerTick());
        assertEquals(4, limits.queueLength());
        assertEquals(8192, limits.scanBlocksPerTick());
        assertEquals("buildBlocksPerTick 32, queueLength 4", limits.toString());
    }

    @Test
    void outOfRangeIsTheNearestAllowed() throws IOException {
        Limits limits = read("{\"millisPerTick\": 0, \"scanBlocksPerTick\": 1, \"actionsPerTick\": 1e99, \"ticksPerBlock\": -3}");
        assertEquals(1, limits.millisPerTick());
        assertEquals(64, limits.scanBlocksPerTick());
        assertEquals(1024, limits.actionsPerTick());
        assertEquals(1, limits.ticksPerBlock());
    }

    @Test
    void notAWholeNumberIsTheDefault() throws IOException {
        for (String v : new String[] {"1.5", "\"8\"", "null", "true", "[]", "{}"}) {
            assertEquals(16, read("{\"queueLength\": " + v + "}").queueLength(), v);
        }
    }

    @Test
    void aBadFileIsTheDefaultsAndIsLeftAlone() throws IOException {
        for (String text : new String[] {"", "{", "[]", "hello", "{\"queueLength\": }"}) {
            assertEquals("the defaults", read(text).toString(), text);
            assertEquals(text, Files.readString(dir.resolve(Limits.FILE)));
        }
        Path blocked = dir.resolve(Limits.FILE).resolve("config");
        assertSame(Limits.DEFAULTS, Limits.load(blocked, LOG), "a folder that cannot be made");
        assertFalse(Files.exists(blocked));
    }

    @Test
    void anEqualShareAndTheRestForTheOthers() {
        assertEquals(50, Limits.share(100, 2, 1));
        assertEquals(64, Limits.share(100, 10, 64), "at least what one command needs");
        assertEquals(30, Limits.share(30, 10, 64), "never more than is left");
        assertEquals(0, Limits.share(0, 3, 1));
        assertEquals(0, Limits.share(-5, 1, 1), "a path search may have used some more");
    }
}
