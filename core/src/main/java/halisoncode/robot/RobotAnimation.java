package halisoncode.robot;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.HashMap;
import java.util.Map;

/** One clip with the same linear animation tracks as the editable model and asset preview, read from JSON once. */
public final class RobotAnimation {
    public record Transform(float x, float y, float z, float pitch, float yaw, float roll) {
        public static final Transform ZERO = new Transform(0, 0, 0, 0, 0, 0);

        public Transform scale(float amount) {
            return new Transform(x * amount, y * amount, z * amount, pitch * amount, yaw * amount, roll * amount);
        }
    }

    private record Bone(Track position, Track rotation) {
    }

    private final float length;
    private final boolean loop;
    private final Map<String, Bone> bones = new HashMap<>();

    public RobotAnimation(JsonObject clip) {
        length = clip.get("length").getAsFloat();
        loop = clip.get("loop").getAsBoolean();
        for (Map.Entry<String, JsonElement> entry : clip.getAsJsonObject("bones").entrySet()) {
            JsonObject tracks = entry.getValue().getAsJsonObject();
            bones.put(entry.getKey(), new Bone(new Track(tracks.getAsJsonArray("position")), new Track(tracks.getAsJsonArray("rotation"))));
        }
    }

    public float length() {
        return length;
    }

    public Transform sample(String bone, float seconds) {
        Bone tracks = bones.get(bone);
        if (tracks == null) return Transform.ZERO;
        float time = loop ? Math.max(0, seconds) % length : Math.min(length, Math.max(0, seconds));
        Track position = tracks.position(), rotation = tracks.rotation();
        int p = position.next(time), r = rotation.next(time);
        return new Transform(position.value(p, time, 0), position.value(p, time, 1), position.value(p, time, 2),
                rotation.value(r, time, 0), rotation.value(r, time, 1), rotation.value(r, time, 2));
    }

    /** The keyframes of one bone's position or rotation, in order; none is zero. */
    private static final class Track {
        private final float[] times;
        private final float[][] values;

        Track(JsonArray frames) {
            int count = frames == null ? 0 : frames.size();
            times = new float[count];
            values = new float[count][3];
            for (int i = 0; i < count; i++) {
                JsonObject frame = frames.get(i).getAsJsonObject();
                times[i] = frame.get("time").getAsFloat();
                for (int axis = 0; axis < 3; axis++) values[i][axis] = frame.getAsJsonArray("value").get(axis).getAsFloat();
            }
        }

        /** The first keyframe at or after the time, or the number of keyframes when there is none. */
        int next(float time) {
            int i = 0;
            while (i < times.length && time > times[i]) i++;
            return i;
        }

        float value(int next, float time, int axis) {
            if (times.length == 0) return 0;
            if (next == 0) return values[0][axis];
            if (next == times.length) return values[next - 1][axis];
            float start = times[next - 1], end = times[next];
            float a = values[next - 1][axis], b = values[next][axis];
            return a + (b - a) * ((time - start) / (end - start));
        }
    }
}
