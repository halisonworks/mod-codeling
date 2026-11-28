# Capability contract: Minecraft programming and Kintro

Issue #363 (parent #352, epic #344). Architecture decision record.

- **Status:** proposed, waiting for the owner's review.
- **Date:** 2026-11-27.
- **Base:** `develop` at 58aef43 (release 0.0.7). Line numbers refer to that commit.
- **Input:** the capability matrix of #361 (`docs/capability-matrix.md`, PR #465), which lists every editor operation,
  who acts and whether it needs a robot.

This record decides how the server tells general Minecraft programming apart from robot programming: which code may
depend on which, how a capability is registered and found, what versions mean, who acts for each call, how calls are
routed and limited, and what a program sees when a capability is missing. It changes no code. The work it leads to is
listed in [Follow-on work](#follow-on-work).

It keeps what the epic asks to keep: the relay, the PXT runtime, one jar per loader, the protocol messages and every
saved program. It does not decide who may build without a robot, or with which blocks. That is #367.

## Terms

- **Capability:** a named group of editor calls that the server implements together, with one version number.
  This record defines two: `world` and `kintro`.
- **Op:** the string in a `cmd` message, for example `agent.move` or `player.say` (`Protocol.java:80`).
- **Actor:** who the server acts as or about when it runs an op: the real player, a robot, or nobody (the editor alone).
- **Route:** the path an op takes on the server from `ServerRelay.handle` to its answer, with its own checks and limits.
- **Platform:** the code both capabilities share: relay, protocol, sessions and runs, permissions, limits and the log.

## Today

Facts from the code and the matrix that the decisions below rest on.

1. **Routing is a chain of special cases.** `ServerRelay.handle` (`ServerRelay.java:113`) tries `positions.toWorld`,
   then the player calls (`PlayerApi.submit`), then requires `halisoncode.robot` for everything else, then
   `halisoncode.build` for building ops, then `agent.camera`, and sends the rest to a robot queue. Which route an op
   takes is spread over four places.
2. **The run belongs to the robots.** The run id lives in `RobotManager.queue(owner)` (`RobotManager.java:211`,
   `:350`). The player path checks it there (`PlayerApi.java:52`, `:59`), as do `positions.toWorld`
   (`ServerRelay.java:162`) and `agent.camera` (`ServerRelay.java:180`). So code with no robot in it imports the robot
   package to know whether a run is current.
3. **Shared types sit in the robot package.** `Result`, `RobotException` and `RobotCommand` are in
   `halisoncode.robot` (`core`), but the player path, the protocol and the relay all use them.
4. **Events reach into the robot.** `PlayerEvents` watches redstone at the default robot (`PlayerEvents.java:64`,
   `:145`) and so imports `RobotManager` and `RobotEntity`.
5. **The editor cannot ask what the server offers.** The `welcome` message is written by the client's relay, not by
   the server (`RelayServer.java:733`). It has the protocol version and a few limits, and nothing about the server.
   The server's first own answer is the reply to `control start` (`ServerRelay.java:197`).
6. **An unknown op is a bad argument.** `RobotCommand.parse` answers an op it does not know with `BAD_ARGS: unknown op`
   (`RobotCommand.java:454`). A program cannot tell "this server does not have that" from "you called it wrong".
7. **Building needs a robot only incidentally.** `blocks.*`, `shapes.*` and the builder go through the default robot,
   although their targets are world coordinates (matrix, summary 2).
8. **Missing robot and missing permission look different.** With no spawned robot a robot op gets `UNLOADED`
   (`RobotManager.java:342`). Without `halisoncode.robot` it gets `NO_PERMISSION` (`ServerRelay.java:130`). Both
   reach the program as catchable errors (`simulator.ts:218`).

## Decision 1: two capabilities, one dependency direction

The server has two logical modules, in the one jar the mod ships today.

| Capability | What it holds | Calls (matrix rows) |
|---|---|---|
| `world` | Programming the world and the player, with no robot: the player's own calls, gameplay, positions, signals and the events about the player and the world. Later, robot-free building (#366, under the policy of #367). | `player.*`, `gameplay.*`, `positions.toWorld`, `signals.send`; events `chat`, `playerDied`, `blockBroken`, `dayStart`, `signal` |
| `kintro` | The robot: spawning and owning robots, their queues, the FakePlayer, inventory, sensors and camera, and every call that acts at the robot. | `agent.*`, `Robot.<call>`, today's `blocks.fill` and `shapes.*` through the default robot, event `redstonePowered` |

The rules:

- **`kintro` may depend on `world` and on the platform. `world` never depends on `kintro`.** No class of `world` or of
  the platform imports `halisoncode.robot` classes that are about robots (`RobotManager`, `RobotEntity`,
  `RobotQueue`, `RobotLinks`, `RobotCamera`).
- **The platform owns the run.** Starting, checking and ending a run, and the heartbeat, move out of `RobotManager`
  into the platform. `kintro` hears "run started" and "run ended" and clears its queues. So does `world` for its
  events.
- **Shared types move to the platform.** `Result`, `RobotException` (as a general refusal) and the op parsing that is
  not about robots move out of `halisoncode.robot`. Their package names change. Their wire form (the codes and the
  messages) does not.
- **Redstone stays with the robot.** `agent.onRedstonePowered` measures at the robot, so its watcher moves from
  `PlayerEvents` into `kintro`. The `event` message and its name `redstonePowered` stay as they are.
- **A build check enforces the direction**, as `checkCommonImports` and `checkPureImports` already do for loaders and
  Minecraft. The check lands with the refactor that makes it pass (follow-on 3), not before.

Why: the epic wants general programming to work, and to keep working, without a robot (#344). Today a change to robot
queues can break `player.say`, because the run lives there. A one-way dependency makes "no robot" a state the code
cannot get wrong by accident, and it is the boundary a later optional Kintro jar would need (#353). Nothing here
requires that jar.

Rejected:

- **A third capability now for building** (`build`). Who acts and which blocks it uses is not decided (#367). Building
  stays in `kintro` until then, and moves when #366 gives it a robot-free route. Its op strings do not change.
- **One capability per editor namespace** (`player`, `gameplay`, `positions`, ...). Namespaces are how the toolbox is
  arranged, not what the server implements together. They would each need a version for no gain.

## Decision 2: registration

Each capability registers its ops with the platform once, when the mod starts on the server (in `common`, for both
loaders). A registration names, for each op:

| Field | Meaning | Example |
|---|---|---|
| op string | As on the wire, unchanged | `agent.move` |
| capability | `world` or `kintro` | `kintro` |
| since | The capability version that added the op | 1 |
| parser | Checks the arguments, as `RobotCommand.parse` does now | `agent.move`: direction, 1 to `MAX_MOVE` blocks |
| actor | `player`, `robot` or `none` (see decision 4) | `robot` |
| route | Which route runs it (see decision 5) | robot queue |
| permissions | The permission nodes it needs | `halisoncode.robot` |

- The registry is fixed for the server's lifetime. Nothing registers or unregisters while the server runs.
- Other mods cannot register ops. The ways in for other mods stay as they are: signals (`halisoncode.api.Signals`,
  #249) and the FakePlayer contract (#246). A public event API is #247, and this record does not widen it.
- An op string belongs to exactly one capability. Registering it twice fails at start-up, in a unit test.
- Ops that the editor answers alone (`console.log`, `loops.pause`, the builder's moves, `robots.get`) are not
  registered. They never reach the server.

Why: one table makes the route, the permission and the limits of an op something a test can read, instead of an
order of `if`s in `ServerRelay.handle`. A fixed registry keeps the threat model the same as today: the set of things a
browser can ask for is known when the server starts.

## Decision 3: discovery

The server tells the editor what it offers in its answer to `control start`, with a new optional field `caps`:

```json
{"v": 2, "t": "control", "op": "start", "run": 7, "caps": {"world": 1, "kintro": 1}}
```

- `caps` maps each capability this player can use on this server to its version. It comes from the server, because
  only the server knows what it runs. The client's `welcome` cannot say it (today, fact 5).
- A capability is left out when the server does not have it or when the player may not use it at all at the start
  of the run: without `halisoncode.robot`, `kintro` is left out.
- `caps` is a hint for the editor. The server still checks every command (decision 6), so a permission that changes
  during the run is enforced on the next command, as today.
- **No `caps` field** means a server from before this contract. The editor then assumes `{"world": 1, "kintro": 1}`,
  the ops of 0.0.7.
- The editor uses `caps` to refuse a call it knows the server lacks, before sending it, with the same error the
  server would give (decision 6). It may also grey out or hide toolbox categories. That is a later UX decision, not
  part of this contract.

Why `control start` and not `welcome` or a new message: the start reply already comes from the server on every run,
and an older editor ignores fields it does not know (the same rule `subscribe` uses, architecture "Message protocol").
So it needs no new message type and no protocol version change.

## Decision 4: actors and authority

Every registered op has one actor, decided when it is registered and never changed at run time.

| Actor | Acts as or about | Today |
|---|---|---|
| `player` | The real player who sent the message | `PlayerApi`, `positions.toWorld`, `agent.camera` |
| `robot` | A robot of the sender, through its queue and its FakePlayer with the owner's UUID | `RobotManager` |
| `none` | Nobody: the answer is computed, nothing in the world is touched | none yet on the server |

The rules:

- **The owner comes from the game connection, never from the message.** A robot name is looked up among the sender's
  robots only (`ServerRelay.java:127`). This holds for every capability.
- **No silent actor switch.** An op never acts as the robot when one exists and as the player when not, or the other
  way round. A program that worked on one server must not, on another, take blocks from a different inventory or be
  seen differently by claim mods. If robot-free building comes (#366), it uses new op strings or a capability version
  the editor asks for explicitly, and today's `blocks.fill` keeps acting as the robot.
- **An actor brings its own protection rules.** `robot` keeps the FakePlayer rules (spawn protection, game mode, claim
  mods see the owner; matrix "F"). Which rules a robot-free edit by the `player` actor follows is #367's decision. The
  contract only requires that #367 writes them down per op before any such op is registered.

## Decision 5: routes and limits

Every op runs on one of these routes. A new op must use one of them. A new route needs a change to this record.

| Route | Runs | Limits |
|---|---|---|
| immediate | At once, in the message's tick | Run must be current; the inbox limit `messagesPerTick` before routing |
| player queue | One call per player per tick | Run must be current; `queueLength` waiting per player; player online |
| robot queue | On the tick, round robin over robots | Run must be current; `queueLength` per robot; shared `actionsPerTick`, `millisPerTick`, `scanBlocksPerTick`, `pathNodesPerTick`, `buildBlocksPerTick`, fair between players |

- Today `positions.toWorld` and `agent.camera` are immediate, the player calls use the player queue, and every robot
  op uses the robot queue.
- The inbox limit (`ServerRelay.java:59`) applies to every message before any route, so no capability can be used to
  get around it.
- Stop, pause and resume act on every route of the run. Today they only reach robot queues; the player queue drops
  its waiting calls when the run ends (`PlayerApi.java:59`). That stays.
- Robot-free building will need a route that runs over many ticks with the build budget but without a robot. Whether
  that is a per-player build queue or something else is for #367 and #366. This record only rules out a route without
  a per-tick limit.

## Decision 6: refusals

What the program sees, in the order the server checks:

| Situation | Code | Who answers | Program |
|---|---|---|---|
| Message too big, not JSON, wrong shape | `BAD_ARGS` | Server | Run ends with a dialog when there is no `id`, otherwise catchable |
| Protocol version differs | `VERSION` | Relay or server | Run ends; the editor asks to reload |
| Op unknown to this server, or its capability is missing or too old | `UNSUPPORTED` (new) | Editor first (from `caps`), server as a backstop | Catchable error: `UNSUPPORTED: this server does not have <capability> <version>` |
| Arguments wrong for a known op | `BAD_ARGS` | Server | Catchable |
| Permission missing | `NO_PERMISSION` | Server | Catchable; the message names the node |
| No robot spawned, or the robot is not loaded | `UNLOADED` | Server | Catchable; the message says to use `/robot spawn` when none exists |
| The run is no longer current | `STOPPED` | Server | Run ends |
| Over a queue limit | `RATE_LIMITED` | Server | Catchable |

- `UNSUPPORTED` is a new code in `Result.Code`. An older editor that gets it treats it as any other error tied to a
  waiting command: it throws `UNSUPPORTED: message` into the program (`simulator.ts:218`). So it needs no protocol
  version change.
- A server keeps answering an unknown op with `BAD_ARGS` until it has the registry. After that, an op string that no
  capability registered gets `UNSUPPORTED`. A program that catches `BAD_ARGS` for an unknown op does not exist in
  practice: the editor only sends ops it declares.
- `UNSUPPORTED` and `NO_PERMISSION` stay different on purpose: the first is about the server, the second about the
  player, and a player can only do something about the second.
- No robot is not `UNSUPPORTED`. The `kintro` capability is there; the player has not spawned a robot yet.

## Decision 7: versions

Three numbers, each for one thing:

| Number | Where | Changes when | Mismatch |
|---|---|---|---|
| Protocol version | `Protocol.VERSION`, every message's `v` | The message envelope or a message's required fields change | `VERSION`, run ends (as today) |
| Capability version | `caps` | An op is added to the capability (+1), or an op's meaning changes so a saved program would behave differently (+1, and the old op string is kept or refused with `UNSUPPORTED`) | Editor refuses the newer ops with `UNSUPPORTED`; older ones still work |
| Mod version | `mod_version`, the jar | Every release | The loaders' network check (see below) |

- A capability version only grows. The editor knows, from its own declarations, the `since` version of each op, and
  compares it with `caps`.
- An op is never removed from a capability while saved programs may use it. If it has to go, the capability version
  goes up and the op answers `UNSUPPORTED` with a message that says what replaces it.
- Mod version and server: on NeoForge the payloads are registered as optional with network version `"1"`
  (`HalisonCodeNeoForge.java:39`, `:103`), and the server admin guide asks every player to use the same mod version.
  This record does not change either. Capability versions make it possible to loosen that later, but that is a
  separate decision.

## Threats and compatibility

| Concern | Assessment |
|---|---|
| `caps` tells the browser what the server runs | It says only what the player could already find out by calling ops. It names no other players, no config and no permission nodes. |
| A browser ignores `caps` and sends a `kintro` op anyway | The server checks capability, permission and robot on every command. `caps` is never trusted. |
| A new route bypasses tick limits | Decision 5: every op is on a route with a per-tick limit, and the inbox limit is in front of all routes. |
| Moving the run out of `RobotManager` | Risk of stop or heartbeat regressions (#458, #460 are recent ones). Follow-on 2 makes the move with no change in behaviour and runs the relay GameTests before and after. |
| Old editor (0.0.7) on a new server | It ignores `caps` and sends the same ops. Unknown op codes cannot happen, because it only sends ops 0.0.7 had. |
| New editor on an old server (0.0.7) | No `caps`: the editor assumes the 0.0.7 set and sends nothing newer. A newer op that slips through gets `BAD_ARGS: unknown op`, which is still catchable. |
| Saved programs | Op strings, arguments and answers do not change. Building keeps the robot as actor (decision 4). |
| Narratimo bridge (#249, #261) | Signals belong to `world`. The API in `halisoncode.api.Signals` does not change. |
| Other mods that watch the robot (#246) | The FakePlayer contract belongs to `kintro` and does not change. |
| Privacy | Unchanged. No capability sends other players' data. A future robot-free `radar` would need its own decision (matrix, question 14). |

## Test plan

With and without a robot, on both loaders.

| Test | Kind | Shows |
|---|---|---|
| No robot at all: every `world` op and event works | GameTest, Fabric and NeoForge | `world` does not depend on a spawned robot (matrix, question 10: today every relay GameTest spawns one first) |
| No robot: a `kintro` op gets `UNLOADED` with the `/robot spawn` hint | GameTest | The refusal for a missing robot |
| Without `halisoncode.robot`: `kintro` is not in `caps`, a `kintro` op gets `NO_PERMISSION`, `world` ops work | GameTest | Permission and discovery agree |
| `caps` in the start reply, and a start reply without `caps` | `ProtocolTest`, editor test | Encoding, and the fallback to the 0.0.7 set |
| An op no capability registered gets `UNSUPPORTED` | `ProtocolTest`, GameTest | The new code reaches the program and is catchable |
| The editor refuses an op whose capability is missing or too old before it sends it | `editor-suite.mjs` | Editor-side check with a fake start reply |
| Every op string is registered once, with actor, route and permissions | Unit test over the registry | No op without limits; no duplicates |
| `world` and platform classes import no robot classes | Build check, like `checkCommonImports` | The dependency direction |
| Stop, pause, resume and heartbeat with and without a robot | Existing relay GameTests, run on NeoForge too | Moving the run did not change run control |

Today only the Fabric GameTests cover relay behaviour; NeoForge has smoke tests (matrix, question 12). The GameTests
above run on both.

## Follow-on work

Each step is its own pull request, and each leaves the mod working. None changes what a program sees until step 4.

1. **A GameTest with no robot** for the `world` ops and events (matrix, question 10). It passes today, and guards
   the later steps.
2. **The platform owns the run.** Move run start, check, end and heartbeat out of `RobotManager`, with no change in
   behaviour. Move `Result` and the general refusal out of `halisoncode.robot`.
3. **The registry and the route table.** `ServerRelay.handle` routes by the registry. Move the redstone watcher into
   `kintro`. Add the import check.
4. **`caps` and `UNSUPPORTED`.** In the start reply, in `Result.Code`, in the editor's check and in
   `docs/architecture.md` ("Message protocol").
5. **Docs.** `docs/architecture.md` sections 3 and 5 describe the two capabilities; `CONTRIBUTING.md` "Adding an op"
   says how to register one.

Robot-free building (#366, #391) comes after #367, on top of step 3.

## Questions for the owner

1. **Names on the wire.** `world` and `kintro` for the capabilities, and `UNSUPPORTED` for the new code. Kintro is the
   approved robot name (#355); `world` is new.
2. **Turning Kintro off.** Should a server be able to turn `kintro` off for everyone in its config, not only per
   player with a permission mod? The contract supports it (the capability is left out of `caps`), but adds no setting.
3. **The toolbox.** Should the editor hide the robot categories when `kintro` is missing, or show them and refuse the
   calls? This record only requires the refusal.
