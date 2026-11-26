/**
 * Robot commands
 */
//% color=#2E7D32 weight=100 icon="\uf1b9"
//% groups='["Movement", "Building", "Inspecting", "Sensors", "Measuring", "Inventory", "Containers", "Crafting", "Feedback"]'
namespace agent {
    /**
     * Why follow path stopped, to compare with what it answers.
     * @param stop the reason, eg: PathStop.Junction
     */
    //% shim=TD_ID blockId=agent_path_stop block="%stop"
    //% group="Movement" weight=93
    export function pathStop(stop: PathStop): number {
        return stop;
    }

    /**
     * A liquid, to compare with what detect liquid answers.
     * @param liquid the liquid, eg: Liquid.Lava
     */
    //% shim=TD_ID blockId=agent_liquid block="%liquid"
    //% group="Sensors" weight=58
    export function liquid(liquid: Liquid): number {
        return liquid;
    }

    // Issue #433. These actions answer a value, so their own blocks are reporters, and a reporter cannot stand alone
    // under "on start". Each statement block below has the same label and writes the same call (blockAliasFor), so
    // `agent.goHome()` on its own line in JavaScript or Python becomes this block, and the block writes it back. The
    // texts come from the aliased call (also the translations), and JavaScript completion leaves these names out.

    /**
     * Walk or fly to a position by itself, around walls and through wooden doors.
     * @param position where to go, eg: pos(0, 0, 5)
     */
    //% blockId=agent_move_to_statement block="agent move to %position=positions_pos"
    //% group="Movement" weight=97 blockAliasFor="agent.moveTo"
    export function _moveToStatement(position: Position): void {
        agent.moveTo(position);
    }

    /**
     * Walk or fly home by itself, the same way as move to.
     */
    //% blockId=agent_go_home_statement block="agent go home"
    //% group="Movement" weight=95 blockAliasFor="agent.goHome"
    export function _goHomeStatement(): void {
        agent.goHome();
    }

    /**
     * Walk or fly to another of your robots, the same way as move to, and stop on a free block next to it.
     * @param robot which of your robots to go to
     */
    //% blockId=agent_go_to_robot_statement block="agent go to %robot=robots_get"
    //% group="Movement" weight=95 blockAliasFor="agent.goToRobot"
    export function _goToRobotStatement(robot: Robot): void {
        agent.goToRobot(robot);
    }

    /**
     * Walk along a path of one kind of block (dirt path, gravel, planks, ...) until it ends or splits.
     * @param block the path block, eg: Block.Gravel
     * @param side the side to keep to, eg: TurnDirection.Left
     */
    //% blockId=agent_follow_path_statement block="agent follow path||of %block|keep %side"
    //% expandableArgumentMode="enabled"
    //% group="Movement" weight=94 blockAliasFor="agent.followPath"
    export function _followPathStatement(block?: Block, side?: TurnDirection): void {
        agent.followPath(block, side);
    }

    /**
     * Put items from the robot into the container next to it, as a hopper would.
     * @param direction where the container is, eg: SixDirection.Forward
     * @param slot robot slot 1 to 27, or 0 for all slots, eg: 0
     */
    //% blockId=agent_store_items_statement block="agent store items %direction|from slot %slot"
    //% slot.min=0 slot.max=27
    //% group="Containers" weight=60 blockAliasFor="agent.storeItems"
    export function _storeItemsStatement(direction: SixDirection, slot: number): void {
        agent.storeItems(direction, slot);
    }

    /**
     * Take items from one stack in the container next to the robot.
     * @param direction where the container is, eg: SixDirection.Forward
     * @param slot container slot, or 0 for the first stack, eg: 0
     * @param count how many at most, eg: 64
     */
    //% blockId=agent_take_items_statement block="agent take items %direction|from slot %slot|amount %count"
    //% slot.min=0 slot.max=54 count.min=1 count.max=64
    //% group="Containers" weight=59 blockAliasFor="agent.takeItems"
    export function _takeItemsStatement(direction: SixDirection, slot: number, count: number): void {
        agent.takeItems(direction, slot, count);
    }

    /**
     * Take one kind of item from any slot of the container next to the robot.
     * @param direction where the container is, eg: SixDirection.Forward
     * @param item what to take, eg: Item.Cobblestone
     * @param count how many at most, eg: 64
     */
    //% blockId=agent_take_item_statement block="agent take from %direction|item %item|amount %count"
    //% item.shadow=halisoncode_item count.min=1 count.max=64
    //% group="Containers" weight=58 blockAliasFor="agent.takeItem"
    export function _takeItemStatement(direction: SixDirection, item: number, count: number): void {
        agent.takeItem(direction, item, count);
    }

    /**
     * Craft an item from the robot's own inventory, like a player.
     * @param item what to make, eg: Item.OakPlanks
     * @param count how many at least, eg: 4
     */
    //% blockId=agent_craft_statement block="agent craft %item|amount %count"
    //% item.shadow=halisoncode_item count.min=1 count.max=64
    //% group="Crafting" weight=54 blockAliasFor="agent.craft"
    export function _craftStatement(item: number, count: number): void {
        agent.craft(item, count);
    }

    /**
     * Cut stone with the stonecutter next to the robot, from the robot's own inventory.
     * @param item what to make, eg: Item.StoneBricks
     * @param count how many at least, eg: 4
     */
    //% blockId=agent_stonecut_statement block="agent stonecut %item|amount %count"
    //% item.shadow=halisoncode_item count.min=1 count.max=64
    //% group="Crafting" weight=52 blockAliasFor="agent.stonecut"
    export function _stonecutStatement(item: number, count: number): void {
        agent.stonecut(item, count);
    }
}

/**
 * You, the player who runs the program (issue #26)
 */
//% color=#1565C0 weight=97 icon=""
namespace player {
}

/**
 * The time and weather where you are (issue #87)
 */
//% color=#F9A825 weight=92 icon=""
namespace gameplay {
}

/**
 * Your other robots, by name (issue #141). agent is your default robot.
 */
//% color=#558B2F weight=95 icon="\uf0c0"
namespace robots {
}

/**
 * Named signals to and from other mods on the server, or from /halisoncode signal (issue #249)
 */
//% color=#E65100 weight=85 icon="\uf1eb"
namespace signals {
}

/**
 * Positions in the world
 */
//% color=#00838F weight=90 icon=""
namespace positions {
}

// The MakeCode globals. They carry the blocks, so pasted `pos(0, 0, 1)` lines decompile to blocks.

/**
 * A position relative to your feet, with world axes: x east, y up, z south. Like ~ in commands.
 * @param x blocks east, eg: 0
 * @param y blocks up, eg: 0
 * @param z blocks south, eg: 0
 */
//% blockId=positions_pos block="~%x|~%y|~%z" blockNamespace=positions
//% inlineInputMode=inline weight=100
function pos(x: number, y: number, z: number): Position {
    return positions.create(x, y, z);
}

/**
 * A position that turns with you: x to your left, y up, z forward. Like ^ in commands.
 * @param x blocks to the left, eg: 0
 * @param y blocks up, eg: 0
 * @param z blocks forward, eg: 0
 */
//% blockId=positions_pos_local block="^%x|^%y|^%z" blockNamespace=positions
//% inlineInputMode=inline weight=99
function posLocal(x: number, y: number, z: number): Position {
    return positions.createLocal(x, y, z);
}

/**
 * A fixed position in the world.
 * @param x world x, eg: 0
 * @param y world y, eg: 64
 * @param z world z, eg: 0
 */
//% blockId=positions_world block="world %x|%y|%z" blockNamespace=positions
//% inlineInputMode=inline weight=98
function world(x: number, y: number, z: number): Position {
    return positions.createWorld(x, y, z);
}

/**
 * A random block position in the box between two corners. The same as positions.random.
 */
function randpos(from: Position, to: Position): Position {
    return positions.random(from, to);
}

/**
 * An invisible builder that moves around and builds lines and boxes (issue #90)
 */
//% color=#AD1457 weight=75 icon=""
namespace builder {
}

/**
 * Lines, circles, spheres and mazes of blocks (issues #90 and #182)
 */
//% color=#6A1B9A weight=70 icon=""
namespace shapes {
}

/**
 * Text output in the simulator
 */
//% color=#455A64 weight=50
namespace console {
}

/**
 * Blocks and items as numbers, and filling a box with blocks
 */
//% color=#6D4C41 weight=80 icon=""
namespace blocks {
    /**
     * An item (or block) for the calls that take items: chests, crafting, setItem.
     * @param item the item, eg: Item.Coal
     */
    //% shim=TD_ID blockId=halisoncode_item block="%item" blockHidden=true
    //% item.fieldEditor="gridpicker" item.fieldOptions.hasSearchBar=true
    //% item.fieldOptions.columns=3 item.fieldOptions.width=360 item.fieldOptions.maxRows=8
    export function item(item: Item): number {
        return item;
    }

    /**
     * A block as a number. Programs that pass Block.Stone where an item is wanted show this block.
     * @param block the block, eg: Block.Stone
     */
    //% shim=TD_ID blockId=halisoncode_block block="%block" blockHidden=true
    export function block(block: Block): number {
        return block;
    }
}

// Issue #221. MakeCode Python turns `assert x, "text"` into control.assert and `raise Error("text")` into control.fail,
// so these names make both compile. Both throw like JavaScript `throw`: a try/catch catches the text, and with none
// the program ends with the text in a dialog (debug mode also marks the line or block).
namespace control {
    /**
     * Stop the program with a message when a condition is false, to check that the program did what it should.
     * @param condition what must be true, eg: true
     * @param message what to show when it is false, eg: "check failed"
     */
    // pxt writes Python calls by name, and `control.assert(...)` is not Python (assert is a keyword). So the block
    // writes control.check (blockAliasFor), and control.assert in JavaScript or Python decompiles to the same block.
    //% blockId=control_assert block="check %condition|or stop with %message"
    //% blockNamespace=logic weight=10 blockAliasFor="control.check"
    export function assert(condition: boolean, message: string): void {
        if (!condition) throw message;
    }

    /**
     * Stop the program with a message when a condition is false, to check that the program did what it should.
     * @param condition what must be true, eg: true
     * @param message what to show when it is false, eg: "check failed"
     */
    export function check(condition: boolean, message: string): void {
        assert(condition, message);
    }

    /**
     * Stop the program with an error message.
     * @param message what went wrong, eg: "something went wrong"
     */
    //% blockId=control_fail block="stop with error %message"
    //% blockNamespace=logic weight=9
    export function fail(message: string): void {
        throw message;
    }
}
