# Server admin guide

For people who run a Minecraft server with Halison Code (issue #272). Every command and game rule is in
[commands.md](commands.md).

## Install

1. Install the server for your Minecraft version with Fabric Loader and Fabric API, or with NeoForge.
2. Put the Halison Code jar for that loader and version in the server's `mods` folder.
3. Every player who joins needs the same loader and **the same version** of Halison Code. Update the server and the
   clients at the same time: a client with another version is refused when it connects, and the game does not say
   that the mod must be updated.

A client without Halison Code is refused with a message that says what to install. A client with Halison Code can join a
server without it; the editor then says that the server does not have Halison Code.

The editor runs in each player's own browser, on the player's own computer. The server never runs program code: it gets
one command at a time and checks each one.

## Who may do what

- **Robots belong to one player.** Only the owner can program a robot or use it in commands. A robot name only ever picks
  one of the player's own robots. Only the owner and ops can open a robot's inventory.
- **A program hears only its own player:** chat commands, death and broken blocks of that player, its own robots'
  redstone, and signals sent to that player.
- **Ops** can stop every robot with `/robot stop all`, send signals and switch on the server's extended logging.

### Permissions

With a permission mod, such as LuckPerms, the server decides per player. It works through NeoForge's permission API,
Fabric API's (26.1.2 and later) or fabric-permissions-api (Fabric 1.21.x, which LuckPerms carries with it).

| Permission | What it is for | Without a permission mod |
|---|---|---|
| `halisoncode.robot` | `/robot`, and programs that use a robot. Without it `/robot` is an unknown command. | everyone |
| `halisoncode.build` | `blocks.place`, `blocks.fill`, `shapes.line`, `shapes.circle`, `shapes.sphere` and `shapes.maze`. Not `agent.place`. | everyone |
| `halisoncode.free_items` | `agent.setItem`, `/robot give` and building without items | creative mode, or the game rule `halisoncode:free_items` |
| `halisoncode.teleport` | `player.teleport` | the singleplayer host and ops |

The defaults stand without a permission mod, and where it says nothing about the player. For example:
`/lp user <player> permission set halisoncode.build false`. A permission never overrides claims, spawn protection or the
game mode.

## Protection

A robot breaks, places and uses things as if it were its owner (as a fake player with the owner's rights):

- Spawn protection: no changes unless the owner is an op.
- Adventure mode: no placing or breaking. Spectator mode: nothing at all.
- Claim mods that check players, such as Open Parties and Claims, refuse the robot where they would refuse its owner.
  Tested with Open Parties and Claims 0.33.0 on Minecraft 26.3 (issue #55).

## Limits per tick

`config/halisoncode-server.json` says how much robots may do each game tick. It is written with the defaults the first
time the server starts, and read each time it starts. A value that is not a whole number is replaced by the default, a
value out of range is set to the nearest allowed one, and a file that cannot be read gives the defaults; the log says
so each time.

| Setting | Default | Allowed | What it limits |
|---|---|---|---|
| `actionsPerTick` | 64 | 1 to 1024 | Robot actions per tick, for all players together |
| `millisPerTick` | 10 | 1 to 50 | Robots start nothing more in a tick once they have used this long (a tick has 50 ms) |
| `ticksPerBlock` | 4 | 1 to 20 | Ticks a robot takes for one block of a walk |
| `scanBlocksPerTick` | 8192 | 64 to 65536 | Blocks looked at by searches, measuring and building |
| `pathNodesPerTick` | 1024 | 16 to 16384 | Steps of path searches |
| `buildBlocksPerTick` | 256 | 1 to 4096 | Blocks set or cleared by fill, shapes and maze |
| `messagesPerTick` | 64 | 4 to 1024 | Editor messages handled per player; the rest wait |
| `queueLength` | 16 | 1 to 64 | Commands that may wait for one robot, and player calls for one player |

Players share each tick equally, whatever number of robots they have. On a busy server a robot may wait a little before
it goes on. `halisoncode:max_robots` (1 to 16) sets how many robots each player may have.

## Saved data

- Which robot belongs to whom: `data/halisoncode/robots.dat` in the world folder (`data/halisoncode_robots.dat` on
  Minecraft 1.21.x). The robots themselves, with their items, are saved with the world like other entities.
- Game rules are saved with the world. `config/halisoncode-server.json` is the server's own.
- Editor projects are saved in each player's browser, not on the server.

Back up the world before you update Halison Code. Robots, game rules and settings from Codeling 0.0.5 and before are not
carried over.

## Stopping robots

- `/robot stop all` (ops): stops every robot and every running program.
- A program stops when its player leaves, closes the editor, or after 10 seconds without contact with the editor.
- Robots only act in loaded chunks.

## When something goes wrong

1. `/halisoncode server log on` (15 minutes, or 1 to 60) logs every step of each command.
2. Try again, then `/halisoncode server report`. It writes `logs/halisoncode-report.txt` with versions, Java, the operating
   system, the game rules and the newest Halison Code lines, without player names, UUIDs or the home folder.
3. Read the report through, and send it with [a bug report](../CONTRIBUTING.md). Nothing is sent by the mod itself.
