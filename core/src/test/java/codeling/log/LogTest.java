package codeling.log;

import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/** Issue #279: quiet by default, extended logging for a limited time, repeated lines limited, names out of the report. */
class LogTest {
    private static final Log LOG = Log.of("codeling/test");
    private long now = 1;

    @BeforeEach
    void clean() {
        Log.reset();
        Log.clock = () -> now;
    }

    @AfterEach
    void restore() {
        Log.reset();
    }

    private static boolean has(String text) {
        return Log.report().stream().anyMatch(line -> line.contains(text));
    }

    @Test
    void detailsComeOnlyWhileExtendedLoggingIsOnAndTheLogSaysSo() {
        LOG.detail("step {}", 1);
        assertEquals(List.of(), Log.report());
        Log.extendedOn(15);
        assertTrue(has("Extended logging is on for 15 minutes"));
        LOG.detail("step {}", 2);
        assertTrue(has("INFO codeling/test: [detail] step 2"));
        now += 15 * 60_000_000_000L;
        LOG.detail("step {}", 3);
        assertFalse(has("step 3"), "its time is up");
        assertTrue(has("Extended logging is off: its time is up."));
    }

    @Test
    void itGoesOffByItselfWithNoLineToWrite() {
        Log.extendedOn(1);
        now += 60_000_000_000L;
        Log.tick();
        assertTrue(has("Extended logging is off"));
        assertFalse(Log.extended());
    }

    @Test
    void extendedLoggingEndsAtItsMostLines() {
        Log.extendedOn(60);
        for (int i = 0; i < Log.MAX_DETAILS + 100; i++) LOG.detail("step");
        assertFalse(Log.extended());
        assertEquals(Log.MAX_DETAILS + 2, Log.written(), "the details, and the lines that say on and off");
        assertEquals(Log.RECENT, Log.report().size(), "only the newest lines are kept");
    }

    @Test
    void repeatedLinesAreCountedInsteadOfWritten() {
        for (int i = 0; i < Log.SAME + 30; i++) LOG.info("refused {}", i);
        LOG.info("another line");
        assertEquals(Log.SAME + 1, Log.written());
        assertTrue(has("another line"));
        now += Log.WINDOW_NANOS;
        Log.tick();
        assertTrue(has("(30 more lines were left out: \"refused ..\")"), Log.report().toString());
        LOG.info("refused {}", "again");
        assertTrue(has("refused again"));
    }

    @Test
    void nothingIsHeldBackWhileExtendedLoggingIsOn() {
        Log.extendedOn(1);
        for (int i = 0; i < Log.SAME + 30; i++) LOG.info("refused {}", i);
        assertEquals(1 + Log.SAME + 30, Log.written());
    }

    @Test
    void theNextLineAlsoSaysWhatWasHeldBack() {
        for (int i = 0; i < Log.SAME + 3; i++) LOG.warn("broken");
        now += Log.WINDOW_NANOS;
        LOG.warn("broken");
        assertTrue(has("(3 more lines were left out"));
    }

    @Test
    void theReportLeavesOutNamesIdsFoldersAndTokens() {
        String id = "01234567-89ab-cdef-0123-456789abcdef";
        LOG.info("{}'s robot \"Nora\" and Kari2 at {}", Log.person("Kari"), id);
        LOG.error("no file", new java.io.IOException(System.getProperty("user.home") + "/x http://127.0.0.1:25580/#local_token=abc-DEF_1"));
        String all = String.join("\n", Log.report());
        assertTrue(all.contains("(player)'s robot \"(name)\" and Kari2 at (id)"), all);
        assertTrue(all.contains("(folder)/x") && all.contains("local_token=(token)"), all);
        for (String secret : List.of("Nora", id, System.getProperty("user.home"), "abc-DEF_1")) assertFalse(all.contains(secret), secret);
    }
}
