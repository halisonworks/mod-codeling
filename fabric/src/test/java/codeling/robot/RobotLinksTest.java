package codeling.robot;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import java.util.UUID;
import org.junit.jupiter.api.Test;

/** robots.dat must keep its links: a lost link makes every robot stale on load. */
class RobotLinksTest {
    private static RobotLinks decode(JsonElement json) {
        return RobotLinks.CODEC.parse(JsonOps.INSTANCE, json).getOrThrow();
    }

    @Test
    void linksAndOrphansSurviveSaveAndLoad() {
        UUID owner = UUID.randomUUID(), robot = UUID.randomUUID(), orphan = UUID.randomUUID();
        RobotLinks links = decode(new JsonObject());
        links.set(owner, robot);
        links.orphan(orphan);
        RobotLinks loaded = decode(RobotLinks.CODEC.encodeStart(JsonOps.INSTANCE, links).getOrThrow());
        assertEquals(robot, loaded.robot(owner));
        assertTrue(loaded.takeOrphan(orphan));
        assertFalse(loaded.takeOrphan(orphan), "an orphan gives its items back once");
    }

    // Issue #140: named robots are saved beside the default robots, which keep the old format.
    @Test
    void namedRobotsSurviveSaveAndLoad() {
        UUID owner = UUID.randomUUID(), robot = UUID.randomUUID(), miner = UUID.randomUUID(), digger = UUID.randomUUID();
        RobotLinks links = decode(new JsonObject());
        links.set(owner, robot);
        links.set(new RobotLinks.Key(owner, "Miner"), miner);
        links.set(new RobotLinks.Key(owner, "Digger"), digger);
        JsonObject saved = RobotLinks.CODEC.encodeStart(JsonOps.INSTANCE, links).getOrThrow().getAsJsonObject();
        assertEquals(robot.toString(), saved.getAsJsonObject("links").get(owner.toString()).getAsString(), "the default robot as before");
        RobotLinks loaded = decode(saved);
        assertEquals(robot, loaded.robot(owner));
        assertEquals(miner, loaded.robot(new RobotLinks.Key(owner, "Miner")));
        assertEquals(new RobotLinks.Key(owner, "Digger"), loaded.key(digger));
        assertEquals(java.util.List.of("Digger", "Miner"), loaded.names(owner));
        assertEquals("Miner", loaded.sameName(owner, "MINER"));
        loaded.remove(new RobotLinks.Key(owner, "Miner"));
        assertNull(loaded.key(miner), "a removed robot is no one's");
    }

    // A world saved before named robots: links and orphans only.
    @Test
    void readsLinksWithoutNamedRobots() {
        UUID owner = UUID.randomUUID(), robot = UUID.randomUUID();
        JsonObject links = new JsonObject();
        links.addProperty(owner.toString(), robot.toString());
        JsonObject old = new JsonObject();
        old.add("links", links);
        old.add("orphans", new com.google.gson.JsonArray());
        RobotLinks loaded = decode(old);
        assertEquals(robot, loaded.robot(owner));
        assertEquals(new RobotLinks.Key(owner, ""), loaded.key(robot));
        assertTrue(loaded.names(owner).isEmpty());
    }

    // Issue #293: the default robot's own name, beside the links, which stay as they were.
    @Test
    void theDefaultRobotsNameSurvivesSaveAndLoad() {
        UUID owner = UUID.randomUUID(), robot = UUID.randomUUID();
        RobotLinks links = decode(new JsonObject());
        assertTrue(links.commandNames(owner).isEmpty(), "no robot, nothing to suggest");
        links.set(owner, robot);
        assertEquals(java.util.List.of("agent"), links.commandNames(owner));
        links.setDefaultName(owner, "Bob");
        links.set(new RobotLinks.Key(owner, "Miner"), UUID.randomUUID());
        RobotLinks loaded = decode(RobotLinks.CODEC.encodeStart(JsonOps.INSTANCE, links).getOrThrow());
        assertEquals("Bob", loaded.defaultName(owner));
        assertEquals(new RobotLinks.Key(owner, ""), loaded.key(robot), "its key is still the empty name");
        assertEquals(java.util.List.of("agent", "Bob", "Miner"), loaded.commandNames(owner));
        assertEquals("", loaded.resolve(owner, "agent"));
        assertEquals("", loaded.resolve(owner, "Bob"));
        assertEquals("Miner", loaded.resolve(owner, "Miner"));
        assertEquals("Other", loaded.resolve(owner, "Other"));
        assertEquals("Bob", loaded.sameName(owner, "bob"));
        assertNull(loaded.sameName(owner, "bob", new RobotLinks.Key(owner, "")), "a robot may keep its own name");
        loaded.set(new RobotLinks.Key(owner, "agent"), UUID.randomUUID());
        assertEquals("agent", loaded.resolve(owner, "agent"), "a named robot goes first");
    }

    // Issue #324: a removed default robot's name and look are there for the next one, until another robot takes the name.
    @Test
    void aRemovedDefaultRobotsNameAndLookSurviveSaveAndLoad() {
        UUID owner = UUID.randomUUID();
        RobotLinks.Key agent = new RobotLinks.Key(owner, "");
        RobotLinks links = decode(new JsonObject());
        links.set(agent, UUID.randomUUID());
        links.setDefaultName(owner, "Bob");
        links.setAppearance(owner, "mint", 0x3366FF);
        links.remove(agent);
        RobotLinks loaded = decode(RobotLinks.CODEC.encodeStart(JsonOps.INSTANCE, links).getOrThrow());
        assertNull(loaded.robot(owner));
        assertEquals("Bob", loaded.defaultName(owner));
        assertEquals(new RobotLinks.Appearance("mint", 0x3366FF), loaded.previousAppearance(agent));
        assertEquals(new RobotLinks.Appearance("workshop", -1), loaded.previousAppearance(new RobotLinks.Key(owner, "Miner")), "not for a named robot");
        assertTrue(loaded.commandNames(owner).isEmpty(), "nothing to suggest");
        assertNull(loaded.sameName(owner, "Bob"), "the name is free");
        loaded.set(new RobotLinks.Key(owner, "Miner"), UUID.randomUUID());
        assertEquals("Bob", loaded.defaultName(owner), "another name changes nothing");
        loaded.set(new RobotLinks.Key(owner, "bob"), UUID.randomUUID());
        assertEquals("", loaded.defaultName(owner), "taken");
    }

    @Test
    void readsTheOldBareMap() {
        UUID owner = UUID.randomUUID(), robot = UUID.randomUUID();
        JsonObject old = new JsonObject();
        old.addProperty(owner.toString(), robot.toString());
        assertEquals(robot, decode(old).robot(owner));
    }

    // Issue #358: the links files that the released 0.0.5 saved, on 26.x (robots.dat) and before (codeling_robots.dat).
    @Test
    void readsTheFilesSavedBy005() throws java.io.IOException {
        readsTheFileSavedBy005("robots.dat", "62702fca-a121-4a0c-8dc9-faaa3f9cf1ea", "f738df80-6f29-4926-b894-690f16bccb45",
                "4a6f3225-83d2-4eaf-add9-df6323340369");
        readsTheFileSavedBy005("codeling_robots.dat", "21420d8b-e206-4114-a2f3-28aca21e2e57", "7b7126cd-bcc0-4631-aa43-ecb93c0a10ee",
                "431d1b52-522b-4b45-ad73-7214a577a6a9");
    }

    private static void readsTheFileSavedBy005(String file, String player, String defaultRobot, String minerRobot) throws java.io.IOException {
        UUID owner = UUID.fromString(player), robot = UUID.fromString(defaultRobot), miner = UUID.fromString(minerRobot);
        RobotLinks links;
        try (java.io.InputStream in = RobotLinksTest.class.getResourceAsStream("/saved-by-0.0.5/" + file)) {
            links = RobotLinks.CODEC.parse(net.minecraft.nbt.NbtOps.INSTANCE,
                    net.minecraft.nbt.NbtIo.readCompressed(in, net.minecraft.nbt.NbtAccounter.unlimitedHeap()).get("data")).getOrThrow();
        }
        assertEquals(robot, links.robot(owner), file);
        assertEquals(miner, links.robot(new RobotLinks.Key(owner, "Miner")), file);
        assertEquals("Bob", links.defaultName(owner), file);
        assertEquals(new RobotLinks.Appearance("mint", -1), links.appearance(robot), file);
        assertEquals(new RobotLinks.Appearance("industrial", 0x3366FF), links.appearance(miner), file);
    }

    // The file the links are in: a world loses its robots when this changes.
    @Test
    void theLinksStayInTheSameFile() {
        //? if >=26.1 {
        assertEquals("codeling:robots", RobotLinks.TYPE.id().toString());
        assertEquals("codeling_robots.dat", RobotLinks.LEGACY_FILE);
        //?} else if >=1.21.5 {
        /*assertEquals("codeling_robots", RobotLinks.TYPE.id());
        *///?} else
        /*assertEquals("codeling_robots", RobotLinks.FILE);*/
    }

    //? if >=26.1 {
    // A world from 1.21.x keeps its links in data/codeling_robots.dat, which 26.x reads when robots.dat is missing.
    @Test
    void readsTheFileSavedBy1_21(@org.junit.jupiter.api.io.TempDir java.nio.file.Path dir) throws java.io.IOException {
        UUID owner = UUID.randomUUID(), robot = UUID.randomUUID(), miner = UUID.randomUUID();
        net.minecraft.nbt.CompoundTag links = new net.minecraft.nbt.CompoundTag();
        links.putString(owner.toString(), robot.toString());
        net.minecraft.nbt.CompoundTag named = new net.minecraft.nbt.CompoundTag(), names = new net.minecraft.nbt.CompoundTag();
        names.putString("Miner", miner.toString());
        named.put(owner.toString(), names);
        net.minecraft.nbt.CompoundTag data = new net.minecraft.nbt.CompoundTag();
        data.put("links", links);
        data.put("named", named);
        net.minecraft.nbt.CompoundTag file = new net.minecraft.nbt.CompoundTag();
        file.put("data", data);
        file.putInt("DataVersion", 4671);
        java.nio.file.Path path = dir.resolve(RobotLinks.LEGACY_FILE);
        net.minecraft.nbt.NbtIo.writeCompressed(file, path);
        RobotLinks loaded = RobotLinks.readLegacy(path);
        assertEquals(robot, loaded.robot(owner));
        assertEquals(miner, loaded.robot(new RobotLinks.Key(owner, "Miner")));
        assertNull(RobotLinks.readLegacy(dir.resolve("missing.dat")), "no file, nothing to read");
    }
    //?}
}
