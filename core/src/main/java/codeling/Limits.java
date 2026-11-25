package codeling;

import codeling.log.Log;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * How much robots and programs may do on this server (issue #52), from {@code config/codeling-server.json}. Read when
 * the server starts; the file is written with the defaults when there is none. The defaults are measured in issue #52.
 */
public final class Limits {
    public static final String FILE = "codeling-server.json";

    private enum Value {
        /** Robot actions for all players together, one per robot. */
        ACTIONS_PER_TICK("actionsPerTick", 64, 1, 1024),
        /** Robots start nothing more in a tick once they have used this long. A tick has 50 ms for the whole game. */
        MILLIS_PER_TICK("millisPerTick", 10, 1, 50),
        /** How long a robot takes for one block of a walk. */
        TICKS_PER_BLOCK("ticksPerBlock", 4, 1, 20),
        /** Blocks looked at by searches, measuring and building. At least 64, what one measuring command looks at. */
        SCAN_BLOCKS_PER_TICK("scanBlocksPerTick", 8192, 64, 65536),
        /** Steps of path searches. */
        PATH_NODES_PER_TICK("pathNodesPerTick", 1024, 16, 16384),
        /** Blocks set or cleared by fill, shapes and maze. */
        BUILD_BLOCKS_PER_TICK("buildBlocksPerTick", 256, 1, 4096),
        /** Messages handled per player; the rest wait for the next tick. */
        MESSAGES_PER_TICK("messagesPerTick", 64, 4, 1024),
        /** Commands that may wait for one robot, and player calls for one player. */
        QUEUE_LENGTH("queueLength", 16, 1, 64);

        final String key;
        final int byDefault, min, max;

        Value(String key, int byDefault, int min, int max) {
            this.key = key;
            this.byDefault = byDefault;
            this.min = min;
            this.max = max;
        }
    }

    public static final Limits DEFAULTS = new Limits(Map.of());

    private final int[] values = new int[Value.values().length];

    private Limits(Map<Value, Integer> set) {
        for (Value v : Value.values()) values[v.ordinal()] = set.getOrDefault(v, v.byDefault);
    }

    public int actionsPerTick() {
        return get(Value.ACTIONS_PER_TICK);
    }

    public int millisPerTick() {
        return get(Value.MILLIS_PER_TICK);
    }

    public int ticksPerBlock() {
        return get(Value.TICKS_PER_BLOCK);
    }

    public int scanBlocksPerTick() {
        return get(Value.SCAN_BLOCKS_PER_TICK);
    }

    public int pathNodesPerTick() {
        return get(Value.PATH_NODES_PER_TICK);
    }

    public int buildBlocksPerTick() {
        return get(Value.BUILD_BLOCKS_PER_TICK);
    }

    public int messagesPerTick() {
        return get(Value.MESSAGES_PER_TICK);
    }

    public int queueLength() {
        return get(Value.QUEUE_LENGTH);
    }

    private int get(Value v) {
        return values[v.ordinal()];
    }

    /** These limits with another time per tick; GameTests use it, also below what the file allows. */
    public Limits withMillisPerTick(int millis) {
        Limits l = new Limits(Map.of());
        System.arraycopy(values, 0, l.values, 0, values.length);
        l.values[Value.MILLIS_PER_TICK.ordinal()] = millis;
        return l;
    }

    /**
     * What the next of {@code players} players may use of {@code left}: an equal part, but at least {@code min}, so a
     * command that needs that much at once does not wait for ever. What a player does not use is left for the others.
     */
    public static int share(int left, int players, int min) {
        return Math.max(0, Math.min(left, Math.max(min, left / players)));
    }

    /** A value that is not a whole number or out of range is replaced, and a file that cannot be read is left alone. */
    public static Limits load(Path dir, Log log) {
        Path file = dir.resolve(FILE);
        try {
            if (!Files.exists(file)) {
                Files.createDirectories(dir);
                Files.writeString(file, DEFAULTS.json());
                return DEFAULTS;
            }
            JsonObject json = JsonParser.parseString(Files.readString(file)).getAsJsonObject();
            Map<Value, Integer> set = new LinkedHashMap<>();
            for (Value v : Value.values()) {
                JsonElement e = json.remove(v.key);
                if (e != null) set.put(v, value(v, e, log));
            }
            for (String unknown : json.keySet()) log.warn("limits: {} in {} is not a setting.", unknown, FILE);
            return new Limits(set);
        } catch (IOException | RuntimeException e) {
            log.warn("limits: {} cannot be read ({}), so the defaults are used.", FILE, e.getClass().getSimpleName());
            return DEFAULTS;
        }
    }

    private static int value(Value v, JsonElement e, Log log) {
        double d = Double.NaN;
        if (e.isJsonPrimitive() && e.getAsJsonPrimitive().isNumber()) {
            try {
                d = e.getAsDouble();
            } catch (NumberFormatException notANumber) {
                // NaN, so the default
            }
        }
        if (d != Math.rint(d) || Double.isInfinite(d)) {
            log.warn("limits: {} in {} must be a whole number, so it is {}.", v.key, FILE, v.byDefault);
            return v.byDefault;
        }
        int in = (int) Math.max(v.min, Math.min(v.max, d));
        if (in != d) log.warn("limits: {} in {} must be {} to {}, so it is {}.", v.key, FILE, v.min, v.max, in);
        return in;
    }

    private String json() {
        JsonObject json = new JsonObject();
        for (Value v : Value.values()) json.addProperty(v.key, get(v));
        return new GsonBuilder().setPrettyPrinting().create().toJson(json) + "\n";
    }

    /** For the report: the settings that are not the defaults. */
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        for (Value v : Value.values()) if (get(v) != v.byDefault) s.append(s.isEmpty() ? "" : ", ").append(v.key).append(' ').append(get(v));
        return s.isEmpty() ? "the defaults" : s.toString();
    }
}
