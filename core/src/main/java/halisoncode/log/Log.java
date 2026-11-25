package halisoncode.log;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.LongSupplier;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MessageFormatter;

/**
 * Everything Halison Code writes to the game's log goes through here (issue #279; docs/architecture.md section 11 has the
 * standard, and LogUseTest fails when other code logs by itself). Normal play is quiet. Extended logging writes the
 * details too, for a limited time, at info so nobody has to change the game's log configuration. The newest lines are
 * kept for the report. Never log a token, an editor address with a token, chat, spoken text or program source.
 * Any thread.
 */
public final class Log {
    public static final int DEFAULT_MINUTES = 15, MAX_MINUTES = 60;
    /** Extended logging switches itself off after this many lines, so it cannot fill a disk. */
    static final int MAX_DETAILS = 50_000;
    /** The same line (its text before the arguments) is written this many times per window; the rest are counted. Not while extended logging is on. */
    static final int SAME = 20;
    static final long WINDOW_NANOS = 10_000_000_000L;
    /** The report has this many of the newest lines. */
    static final int RECENT = 500;
    private static final int MAX_PEOPLE = 1000, MAX_LINE = 500;
    private static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");
    private static final Pattern TOKEN = Pattern.compile("local_token=[A-Za-z0-9_-]+");
    private static final Pattern UUID = Pattern.compile("[0-9a-fA-F]{8}(?:-[0-9a-fA-F]{4}){3}-[0-9a-fA-F]{12}");
    private static final Pattern ROBOT = Pattern.compile("robot \"[^\"]*\"");

    /** Tests set the time. */
    static LongSupplier clock = System::nanoTime;

    private static final Object LOCK = new Object();
    private static final ArrayDeque<String> LINES = new ArrayDeque<>();
    private static final Map<String, Repeats> REPEATS = new HashMap<>();
    private static final Set<String> PEOPLE = new LinkedHashSet<>();
    private static final Log OWN = of("halisoncode");
    private static volatile boolean extended;
    private static volatile long until;
    private static int details;
    private static long written;

    private static final class Repeats {
        final Log log;
        final String text;
        long since;
        int count, held;

        Repeats(Log log, String text) {
            this.log = log;
            this.text = text;
        }
    }

    private enum Level { ERROR, WARN, INFO }

    private final String name;
    private final Logger logger;

    private Log(String name) {
        this.name = name;
        logger = LoggerFactory.getLogger(name);
    }

    public static Log of(String name) {
        return new Log(name);
    }

    /** Halison Code cannot do something it is there for. */
    public void error(String text, Object... args) {
        limited(Level.ERROR, text, args);
    }

    /** Something is wrong, and Halison Code goes on. */
    public void warn(String text, Object... args) {
        limited(Level.WARN, text, args);
    }

    /** Start, stop, connection and refusal. */
    public void info(String text, Object... args) {
        limited(Level.INFO, text, args);
    }

    /** True when {@link #detail} is written, so a busy caller can skip making the arguments. */
    public boolean detailed() {
        return extended() || logger.isDebugEnabled();
    }

    /** A step of one command and the like: debug, or info while extended logging is on. */
    public void detail(String text, Object... args) {
        if (!extended()) {
            logger.debug(text, args);
            return;
        }
        synchronized (LOCK) {
            if (extended) extendedLine(Level.INFO, "[detail] " + text, args);
        }
    }

    private void extendedLine(Level level, String text, Object[] args) {
        write(level, text, args);
        if (++details >= MAX_DETAILS) extendedOff("it has written " + MAX_DETAILS + " lines");
    }

    /** Switches extended logging on, or on for a new time. */
    public static void extendedOn(int minutes) {
        synchronized (LOCK) {
            details = 0;
            until = clock.getAsLong() + minutes * 60_000_000_000L;
            extended = true;
            OWN.write(Level.INFO, "Extended logging is on for {} minutes. It then goes off by itself.", minutes);
        }
    }

    public static void extendedOff(String why) {
        synchronized (LOCK) {
            if (!extended) return;
            extended = false;
            OWN.write(Level.INFO, "Extended logging is off: {}.", why);
        }
    }

    public static boolean extended() {
        if (!extended) return false;
        if (clock.getAsLong() - until < 0) return true;
        extendedOff("its time is up");
        return false;
    }

    /** Each tick of the game: says what the limit on repeated lines held back, and ends extended logging on time. */
    public static void tick() {
        extended();
        synchronized (LOCK) {
            if (REPEATS.isEmpty()) return;
            long now = clock.getAsLong();
            REPEATS.values().removeIf(r -> {
                if (now - r.since < WINDOW_NANOS) return false;
                heldBack(r);
                return true;
            });
        }
    }

    /** How many lines were written since the game started (not those at debug). */
    public static long written() {
        synchronized (LOCK) {
            return written;
        }
    }

    /** Remembers a player's name, so the report leaves it out; answers the name. */
    public static String person(String name) {
        synchronized (LOCK) {
            if (name != null && !name.isEmpty() && PEOPLE.size() < MAX_PEOPLE) PEOPLE.add(name);
        }
        return name;
    }

    /** The newest lines for a report others will read: no player names, ids, robot names, home folder or token. */
    public static List<String> report() {
        List<String> lines;
        List<String> people;
        synchronized (LOCK) {
            lines = new ArrayList<>(LINES);
            people = new ArrayList<>(PEOPLE);
        }
        people.sort(Comparator.comparing(String::length).reversed());
        Pattern names = people.isEmpty() ? null
                : Pattern.compile("(?<!\\w)(?:" + String.join("|", people.stream().map(Pattern::quote).toList()) + ")(?!\\w)");
        lines.replaceAll(line -> clean(line, names));
        return lines;
    }

    private static String clean(String line, Pattern names) {
        for (String property : new String[] {"java.io.tmpdir", "user.home"}) {
            String path = System.getProperty(property, "");
            if (path.length() > 3) line = line.replace(path, "(folder)").replace(path.replace('\\', '/'), "(folder)");
        }
        line = TOKEN.matcher(line).replaceAll("local_token=(token)");
        line = UUID.matcher(line).replaceAll("(id)");
        line = ROBOT.matcher(line).replaceAll("robot \"(name)\"");
        return names == null ? line : names.matcher(line).replaceAll("(player)");
    }

    private void limited(Level level, String text, Object[] args) {
        synchronized (LOCK) {
            if (extended()) {
                extendedLine(level, text, args);
                return;
            }
            long now = clock.getAsLong();
            Repeats r = REPEATS.get(name + "\n" + text);
            if (r == null || now - r.since >= WINDOW_NANOS) {
                if (r != null) heldBack(r);
                r = new Repeats(this, text);
                r.since = now;
                REPEATS.put(name + "\n" + text, r);
            }
            if (++r.count > SAME) r.held++;
            else write(level, text, args);
        }
    }

    private static void heldBack(Repeats r) {
        if (r.held > 0) r.log.write(Level.INFO, "({} more lines were left out: \"" + r.text.replace("{}", "..") + "\")", r.held);
    }

    private void write(Level level, String text, Object... args) {
        switch (level) {
            case ERROR -> logger.error(text, args);
            case WARN -> logger.warn(text, args);
            case INFO -> logger.info(text, args);
        }
        FormattingTuple message = MessageFormatter.arrayFormat(text, args);
        String line = LocalTime.now().format(TIME) + " " + level + " " + name + ": " + message.getMessage()
                + (message.getThrowable() == null ? "" : " | " + message.getThrowable());
        written++;
        if (LINES.size() >= RECENT) LINES.removeFirst();
        LINES.add(line.length() > MAX_LINE ? line.substring(0, MAX_LINE) + "..." : line);
    }

    /** Tests start from a clean state. */
    static void reset() {
        synchronized (LOCK) {
            LINES.clear();
            REPEATS.clear();
            PEOPLE.clear();
            extended = false;
            details = 0;
            written = 0;
            clock = System::nanoTime;
        }
    }
}
