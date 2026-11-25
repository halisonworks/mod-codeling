package halisoncode.gametest;

import halisoncode.HalisonCode;
import halisoncode.net.RelayPayloads;
import halisoncode.robot.RobotCommand;
import halisoncode.robot.RobotEntity;
import halisoncode.robot.RobotLinks;
import halisoncode.robot.RobotManager;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * Issue #358: saved worlds keep their robots. The files in saved/ are what 0.0.5 (tag v0.0.5) saved on Minecraft 26.3
 * and 1.21.1, with the entity id that the rename gave the robot (issue #422). Text components are saved as text before 1.21.5,
 * so those versions read the 1.21.1 files. The links are in RobotLinksTest.
 */
public class SavedRobotGameTests {
    private static final BlockPos START = new BlockPos(1, 2, 1);
    //? if >=1.21.5 {
    private static final String SAVED = "/saved/26.3/";
    private static final BlockPos HOME = new BlockPos(11895134, 8, -14369438);
    //?} else {
    /*private static final String SAVED = "/saved/1.21.1/";
    private static final BlockPos HOME = new BlockPos(-6414930, -58, 9352890);
    *///?}

    private static CompoundTag saved(String name) throws Exception {
        try (InputStream in = SavedRobotGameTests.class.getResourceAsStream(SAVED + name + ".snbt")) {
            String text = new String(in.readAllBytes(), StandardCharsets.UTF_8);
            //? if >=1.21.5 {
            return TagParser.parseCompoundFully(text);
            //?} else
            /*return TagParser.parseTag(text);*/
        }
    }

    /** Puts a saved robot into the world as a chunk load does, here and as the robot the links know. */
    private static RobotEntity load(GameTestHelper helper, CompoundTag tag, UUID owner, String name, BlockPos at) {
        RobotEntity linked = RobotManager.spawn(helper.getLevel(), new RobotLinks.Key(owner, name), Vec3.atBottomCenterOf(helper.absolutePos(at)), Direction.SOUTH);
        CompoundTag here = GameTestCompat.save(linked, helper.getLevel().registryAccess());
        tag.put("UUID", here.get("UUID"));
        tag.put("Pos", here.get("Pos"));
        linked.remove(Entity.RemovalReason.UNLOADED_TO_CHUNK);
        String id = tag.get("id").toString().replace("\"", "");
        helper.assertTrue(BuiltInRegistries.ENTITY_TYPE.getOptional(Identifier.parse(id)).orElse(null) == HalisonCode.ROBOT.get(), "the saved id " + id + " is the robot");
        RobotEntity robot = new RobotEntity(HalisonCode.ROBOT.get(), helper.getLevel());
        GameTestCompat.load(robot, tag, helper.getLevel().registryAccess());
        helper.getLevel().addFreshEntity(robot);
        helper.assertTrue(RobotManager.robot(new RobotLinks.Key(owner, name)) == robot, "the saved robot is the player's robot");
        return robot;
    }

    @GameTest(maxTicks = 100)
    public void savedRobotsKeepEverything(GameTestHelper helper) throws Exception {
        CompoundTag agentTag = saved("default"), minerTag = saved("miner");
        UUID owner = UUIDUtil.uuidFromIntArray(((IntArrayTag) agentTag.get("Owner")).getAsIntArray());
        RobotEntity agent = load(helper, agentTag, owner, "", START);
        RobotEntity miner = load(helper, minerTag, owner, "Miner", START.east(2));

        helper.assertValueEqual(agent.owner(), owner, "owner");
        helper.assertValueEqual(agent.ownerName(), "test-mock-player", "owner's name");
        helper.assertValueEqual(agent.name(), "", "the default robot");
        helper.assertValueEqual(agent.getCustomName().getString(), "Bob", "its name");
        helper.assertTrue(agent.isCustomNameVisible(), "its name tag shows");
        helper.assertValueEqual(agent.skin(), "mint", "its skin");
        helper.assertValueEqual(agent.primaryColor(), -1, "no colour");
        helper.assertValueEqual(agent.selectedSlot(), 1, "its slot");
        helper.assertTrue(!agent.placeOnMove() && !agent.gravityMode() && agent.home() == null, "its settings");
        helper.assertTrue(agent.inventory().getItem(0).is(Items.STONE) && agent.inventory().getItem(0).getCount() == 4, "its items");

        helper.assertValueEqual(miner.owner(), owner, "Miner's owner");
        helper.assertValueEqual(miner.name(), "Miner", "Miner");
        helper.assertValueEqual(miner.getCustomName().getString(), "Miner", "Miner's name");
        helper.assertFalse(miner.isCustomNameVisible(), "Miner's name tag is off");
        helper.assertValueEqual(miner.skin(), "industrial", "Miner's skin");
        helper.assertValueEqual(miner.primaryColor(), 0x3366FF, "Miner's colour");
        helper.assertValueEqual(miner.selectedSlot(), 3, "Miner's slot");
        helper.assertTrue(miner.placeOnMove() && miner.gravityMode(), "Miner's settings");
        helper.assertValueEqual(miner.home(), GlobalPos.of(Level.OVERWORLD, HOME), "Miner's home");
        helper.assertValueEqual(miner.getDirection(), Direction.EAST, "Miner's direction");
        helper.assertTrue(miner.inventory().getItem(0).is(Items.COBBLESTONE) && miner.inventory().getItem(0).getCount() == 64, "Miner's stone");
        helper.assertTrue(miner.inventory().getItem(8).is(Items.OAK_LOG) && miner.inventory().getItem(8).getCount() == 5, "Miner's logs");
        ItemStack pick = miner.inventory().getItem(26);
        helper.assertTrue(pick.is(Items.DIAMOND_PICKAXE) && pick.getDamageValue() == 17, "Miner's pickaxe");
        helper.assertValueEqual(pick.get(DataComponents.CUSTOM_NAME).getString(), "Old pick", "the pickaxe's name");

        try {
            RobotManager.submit(owner, RobotManager.queue(owner).run(), RobotCommand.parse("agent.move", List.of(0, 1)), r -> { });
        } catch (Exception e) {
            helper.fail("a command for the saved robot: " + e);
        }
        helper.succeedWhen(() -> {
            helper.assertValueEqual(agent.blockPosition(), helper.absolutePos(START.south()), "it still takes commands");
            for (RobotEntity robot : List.of(agent, miner)) {
                robot.inventory().clearContent();
                robot.discard();
            }
        });
    }

    // What worlds, servers and players' habits hold on to. A change here needs a migration.
    @GameTest(maxTicks = 20)
    public void identifiersStay(GameTestHelper helper) {
        helper.assertValueEqual(BuiltInRegistries.ENTITY_TYPE.getKey(HalisonCode.ROBOT.get()).toString(), "halisoncode:kintro", "entity");
        helper.assertValueEqual(BuiltInRegistries.MENU.getKey(HalisonCode.ROBOT_MENU.get()).toString(), "halisoncode:robot", "menu");
        helper.assertValueEqual(RelayPayloads.ToServer.TYPE.id().toString(), "halisoncode:to_server", "payload to the server");
        helper.assertValueEqual(RelayPayloads.ToClient.TYPE.id().toString(), "halisoncode:to_client", "payload to the client");
        helper.assertValueEqual(RelayPayloads.Camera.TYPE.id().toString(), "halisoncode:camera", "camera payload");
        //? if >=1.21.11 {
        helper.assertValueEqual(BuiltInRegistries.GAME_RULE.getKey(HalisonCode.FREE_ITEMS.get()).toString(), "halisoncode:free_items", "free_items");
        helper.assertValueEqual(BuiltInRegistries.GAME_RULE.getKey(HalisonCode.ROBOT_CAMERA.get()).toString(), "halisoncode:robot_camera", "robot_camera");
        helper.assertValueEqual(BuiltInRegistries.GAME_RULE.getKey(HalisonCode.MAX_ROBOTS.get()).toString(), "halisoncode:max_robots", "max_robots");
        helper.assertValueEqual(BuiltInRegistries.GAME_RULE.getKey(HalisonCode.PAUSE_ON_LOST_FOCUS.get()).toString(), "halisoncode:pause_on_lost_focus", "pause_on_lost_focus");
        //?} else {
        /*helper.assertValueEqual(HalisonCode.FREE_ITEMS.get().getId(), "halisoncode:free_items", "free_items");
        helper.assertValueEqual(HalisonCode.ROBOT_CAMERA.get().getId(), "halisoncode:robot_camera", "robot_camera");
        helper.assertValueEqual(HalisonCode.MAX_ROBOTS.get().getId(), "halisoncode:max_robots", "max_robots");
        helper.assertValueEqual(HalisonCode.PAUSE_ON_LOST_FOCUS.get().getId(), "halisoncode:pause_on_lost_focus", "pause_on_lost_focus");
        *///?}
        var commands = helper.getLevel().getServer().getCommands().getDispatcher().getRoot();
        helper.assertTrue(commands.getChild("robot") != null && commands.getChild("halisoncode") != null, "the commands /robot and /halisoncode");
        helper.succeed();
    }
}
