# Product roadmap and milestone allocation

Status: proposed planning baseline, 24 November 2026. Development baseline: 0.0.5, published as a pre-release on 24 November 2026 (tag `v0.0.5`). Snapshot of 192 open issues; no dates or staffing commitments. Owner review is required before release scope is frozen.

## Product intent and boundaries

Halison Code is the name of the mod and the editor ("the Halison Code editor" when the two must be told apart), and Kintro is the robot (owner's decision 2026-11-25, #355). "For Minecraft" is used in descriptions only. Clearance must be recorded on #58 before the names are shown in public. The product serves hobby players, learners and creators who want to programme Minecraft worlds, build structures, automate resources and coordinate robots. The editor supports Blocks, JavaScript and Python within actual PXT capabilities. Minecraft world programming must not require a robot where the capability is genuinely independent. Kintro retains survival resources, server authority and explicit robot selection. Use logical module boundaries before deciding on physical packaging. Narratimo Worlds and other targets remain independent optional integrations.

## How to read this plan

- GitHub issues own implementation scope, JTBD, player value, acceptance criteria and evidence. This document owns the proposed target allocation and release gates, not detailed technical requirements.
- Each open issue in the snapshot has exactly one target; parent membership does not imply the parent's entire scope must close at that version. Core means the relevant minimum outcome is required, with approved decisions or documented residual disposition where specified; optional means not a release gate. A feature and its stories are counted separately, not as independent estimates.
- P0 is critical; P1 high within its milestone; P2 planned; P3 optional/low. Milestone sequence and real prerequisites take precedence over a global priority sort. Parallel work is allowed.
- GitHub has a native milestone for each version and one for deferred issues. Since 25 November 2026 every open issue has the milestone of its target here. The older step and extension milestones are closed and kept for their closed issues; do not delete them without review. Keep the index and the milestones aligned when issues are added, moved or closed.
- This is not a release approval, naming clearance, technical design decision, or proof of implementation. Verify evidence at the actual candidate commit. No mandatory cloud, AI, arbitrary server-side execution, unrestricted command execution, forced chunk loading or physical module split.

## Milestone overview

| Version | Outcome | Core issues | Optional issues |
| --- | --- | ---: | ---: |
| 0.1.0 | Reliable companion | 22 | 8 |
| 0.2.0 | Product identity | 12 | 0 |
| 0.3.0 | Modular Minecraft programming | 19 | 1 |
| 0.4.0 | Resource automation | 23 | 17 |
| 0.5.0 | Intelligent automation | 10 | 13 |
| 0.6.0 | Collaborative systems | 8 | 4 |
| 0.7.0 | Learning and creativity | 12 | 13 |
| 0.8.0 | Optional extensions | 0 | 7 |
| 0.9.0 | Release candidate | 4 | 2 |
| 1.0.0 | Stable release | 2 | 2 |
| Deferred | Unscheduled extensions | 0 | 13 |

Total: 192 open issues; 112 core, 67 optional with proposed targets, 13 deferred. Counts are a snapshot, not completion percentages.

## Release gates and allocation index

### 0.1.0: Reliable companion

Fresh installation, baseline editor connection and supported Blocks/JS/Python example, save/open and safe stop; actual two-player dedicated server isolation and protection, measured bounded execution, licensing and packaged notices, accurate download information, backup/upgrade and explicit publication approval. #278/#279/#284 need acceptance or documented residual-risk disposition; #272/#273 require minimum release documentation, not exhaustive future reference. Optional conveniences are not release gates.

| Issue | Scope | Title |
| --- | --- | --- |
| [#1](https://github.com/halisonworks/mod-codeling/issues/1) | Core | Epic: Browser-to-robot relay (step 2) |
| [#3](https://github.com/halisonworks/mod-codeling/issues/3) | Core | As a player, I run agent.move from the editor and my robot moves |
| [#6](https://github.com/halisonworks/mod-codeling/issues/6) | Core | Relay robustness and convenience |
| [#8](https://github.com/halisonworks/mod-codeling/issues/8) | Core | As a player, I see an in-game error when the editor port is busy |
| [#10](https://github.com/halisonworks/mod-codeling/issues/10) | Core | Epic: Editor API v1 (step 3) |
| [#20](https://github.com/halisonworks/mod-codeling/issues/20) | Core | Agent position and orientation |
| [#22](https://github.com/halisonworks/mod-codeling/issues/22) | Core | As a player, my program knows which way the robot faces |
| [#50](https://github.com/halisonworks/mod-codeling/issues/50) | Core | Epic: Multiplayer hardening (step 4) |
| [#51](https://github.com/halisonworks/mod-codeling/issues/51) | Core | Permission nodes |
| [#52](https://github.com/halisonworks/mod-codeling/issues/52) | Core | Rate limits and measured budgets |
| [#53](https://github.com/halisonworks/mod-codeling/issues/53) | Core | Protection: spawn protection and claim mods |
| [#55](https://github.com/halisonworks/mod-codeling/issues/55) | Core | Dedicated-server test with two players |
| [#57](https://github.com/halisonworks/mod-codeling/issues/57) | Core | Epic: Release 0.1.0 (step 5) |
| [#59](https://github.com/halisonworks/mod-codeling/issues/59) | Core | Reuse rights for the Norwegian pxt strings |
| [#62](https://github.com/halisonworks/mod-codeling/issues/62) | Core | Release workflow and Modrinth page |
| [#198](https://github.com/halisonworks/mod-codeling/issues/198) | Core | Mod icon and README screenshots |
| [#277](https://github.com/halisonworks/mod-codeling/issues/277) | Core | Information pages on the sites where players download mods |
| [#278](https://github.com/halisonworks/mod-codeling/issues/278) | Core | Player messages look and read like Minecraft's own |
| [#279](https://github.com/halisonworks/mod-codeling/issues/279) | Core | Extended logging for troubleshooting in the main release |
| [#284](https://github.com/halisonworks/mod-codeling/issues/284) | Core | Robot animation reads its JSON again for every frame |
| [#272](https://github.com/halisonworks/mod-codeling/issues/272) | Core | Server admin guide |
| [#273](https://github.com/halisonworks/mod-codeling/issues/273) | Core | One reference for commands and game rules |
| [#7](https://github.com/halisonworks/mod-codeling/issues/7) | Optional | As a player, I open the editor with a key instead of typing /codeling |
| [#9](https://github.com/halisonworks/mod-codeling/issues/9) | Optional | As a player, my editor reconnects after the game restarts the relay |
| [#56](https://github.com/halisonworks/mod-codeling/issues/56) | Optional | Clean up stale robot links |
| [#79](https://github.com/halisonworks/mod-codeling/issues/79) | Optional | Visible feedback from the robot |
| [#81](https://github.com/halisonworks/mod-codeling/issues/81) | Optional | As a player, I see and hear what my robot does |
| [#82](https://github.com/halisonworks/mod-codeling/issues/82) | Optional | As a player, I see which block my robot inspects |
| [#285](https://github.com/halisonworks/mod-codeling/issues/285) | Optional | Programs may run slower while Minecraft is in the background: measure first |
| [#297](https://github.com/halisonworks/mod-codeling/issues/297) | Optional | Game rule for pausing when the game window loses focus |

### 0.2.0: Product identity

Reviewed naming evidence and owner decision; approved editor/mod/robot display identity, legacy project and world compatibility, distribution and reversible upgrade path. Future name clearance does not block the existing Codeling beta; if approval is unavailable, retain current identity rather than force a rename.

| Issue | Scope | Title |
| --- | --- | --- |
| [#58](https://github.com/halisonworks/mod-codeling/issues/58) | Core | Trademark and naming clearance for Halison Code and Kintro |
| [#343](https://github.com/halisonworks/mod-codeling/issues/343) | Core | Epic: Product identity and compatible migration from Codeling |
| [#346](https://github.com/halisonworks/mod-codeling/issues/346) | Core | Feature: Naming clearance and public identity |
| [#347](https://github.com/halisonworks/mod-codeling/issues/347) | Core | Feature: Halison Code editor identity |
| [#348](https://github.com/halisonworks/mod-codeling/issues/348) | Core | Feature: Kintro robot identity and saved-world compatibility |
| [#349](https://github.com/halisonworks/mod-codeling/issues/349) | Core | Feature: Minecraft mod name and distribution migration |
| [#355](https://github.com/halisonworks/mod-codeling/issues/355) | Core | Establish the approved Minecraft mod, editor and robot names |
| [#356](https://github.com/halisonworks/mod-codeling/issues/356) | Core | As a player, I recognise Halison Code when I open the editor |
| [#357](https://github.com/halisonworks/mod-codeling/issues/357) | Core | As a player, I reopen my existing projects after the editor rename |
| [#358](https://github.com/halisonworks/mod-codeling/issues/358) | Core | As a player, I keep my named robots and inventories after the Kintro rename |
| [#359](https://github.com/halisonworks/mod-codeling/issues/359) | Core | As a player, I see the approved mod name in Fabric and NeoForge |
| [#360](https://github.com/halisonworks/mod-codeling/issues/360) | Core | As an existing player, I find a safe upgrade path from Codeling |

### 0.3.0: Modular Minecraft programming

Reviewed capability inventory and permission contract, reproducible offline editor, one robot-independent interactive example and approved bounded direct construction scenario, documented MakeCode differences, preserved robot semantics. #353/#364 require a decision, not separate JARs. Scope of direct mutations remains subject to #367 approval.

| Issue | Scope | Title |
| --- | --- | --- |
| [#28](https://github.com/halisonworks/mod-codeling/issues/28) | Core | Blocks API |
| [#29](https://github.com/halisonworks/mod-codeling/issues/29) | Core | Complete block and item id table |
| [#31](https://github.com/halisonworks/mod-codeling/issues/31) | Core | Acceptance programs run unchanged |
| [#336](https://github.com/halisonworks/mod-codeling/issues/336) | Core | Feature: Reproducible Halison Code target boundaries |
| [#342](https://github.com/halisonworks/mod-codeling/issues/342) | Core | As a maintainer, I can rebuild the Halison Code Minecraft target reproducibly |
| [#344](https://github.com/halisonworks/mod-codeling/issues/344) | Core | Epic: Modular Minecraft programming platform |
| [#350](https://github.com/halisonworks/mod-codeling/issues/350) | Core | Feature: Inventory Minecraft and Kintro capability boundaries |
| [#351](https://github.com/halisonworks/mod-codeling/issues/351) | Core | Feature: Robot-independent Minecraft programming |
| [#352](https://github.com/halisonworks/mod-codeling/issues/352) | Core | Feature: Kintro capability registration and logical module boundary |
| [#353](https://github.com/halisonworks/mod-codeling/issues/353) | Core | Feature: Evaluate optional Kintro mod packaging |
| [#361](https://github.com/halisonworks/mod-codeling/issues/361) | Core | Map existing Minecraft APIs to robot-dependent and robot-independent capabilities |
| [#362](https://github.com/halisonworks/mod-codeling/issues/362) | Core | As a player, I create an interactive Minecraft project without spawning a robot |
| [#363](https://github.com/halisonworks/mod-codeling/issues/363) | Core | Define the Minecraft programming and Kintro capability contract |
| [#364](https://github.com/halisonworks/mod-codeling/issues/364) | Core | Compare single-mod and optional Kintro mod distribution |
| [#366](https://github.com/halisonworks/mod-codeling/issues/366) | Core | Feature: Safe Minecraft world construction without Kintro |
| [#367](https://github.com/halisonworks/mod-codeling/issues/367) | Core | Story: Define the authority and resource policy for robot-independent building |
| [#370](https://github.com/halisonworks/mod-codeling/issues/370) | Core | Feature: MakeCode capability coverage and compatibility roadmap |
| [#391](https://github.com/halisonworks/mod-codeling/issues/391) | Core | As a creative player, I build a bounded structure without spawning a robot |
| [#395](https://github.com/halisonworks/mod-codeling/issues/395) | Core | Publish a source-linked MakeCode capability and semantic compatibility matrix |
| [#23](https://github.com/halisonworks/mod-codeling/issues/23) | Optional | Agent teleport |

### 0.4.0: Resource automation

Explicit named robot selection and reusable/parallel functions; representative bounded farm, mining, wood, furnace/crafting, category sorting and parameterised construction workflows, actual item/world assertions, limits and failure tests. #33 existing navigation is a prerequisite only to the extent used. #311/#312/#313/#314 optional unless the minimum logistics implementation demonstrates necessity. Prepared farmland suffices; attack and till are not mandatory.

| Issue | Scope | Title |
| --- | --- | --- |
| [#38](https://github.com/halisonworks/mod-codeling/issues/38) | Core | DESTROY_OBSTACLES assist |
| [#47](https://github.com/halisonworks/mod-codeling/issues/47) | Core | Example programs: chop the nearest tree, walk over terrain |
| [#48](https://github.com/halisonworks/mod-codeling/issues/48) | Core | Tool choice for destroy |
| [#83](https://github.com/halisonworks/mod-codeling/issues/83) | Core | Farming: harvest and plant |
| [#84](https://github.com/halisonworks/mod-codeling/issues/84) | Core | As a player, my robot harvests ripe crops |
| [#85](https://github.com/halisonworks/mod-codeling/issues/85) | Core | As a player, my robot plants seeds |
| [#306](https://github.com/halisonworks/mod-codeling/issues/306) | Core | Epic: Sort chests by item category |
| [#307](https://github.com/halisonworks/mod-codeling/issues/307) | Core | Item categories |
| [#309](https://github.com/halisonworks/mod-codeling/issues/309) | Core | Container calls by item category |
| [#372](https://github.com/halisonworks/mod-codeling/issues/372) | Core | Feature: Explicit robot selection and reusable multi-robot programming in Blocks |
| [#373](https://github.com/halisonworks/mod-codeling/issues/373) | Core | As a player, I select the robot that executes a block sequence |
| [#374](https://github.com/halisonworks/mod-codeling/issues/374) | Core | As a player, I reuse one function for several different robots |
| [#375](https://github.com/halisonworks/mod-codeling/issues/375) | Core | As a player, I divide one project into concurrent robot jobs |
| [#376](https://github.com/halisonworks/mod-codeling/issues/376) | Core | Epic: Kintro resource gathering and production workflows |
| [#377](https://github.com/halisonworks/mod-codeling/issues/377) | Core | Epic: Kintro programmable construction and material planning |
| [#378](https://github.com/halisonworks/mod-codeling/issues/378) | Core | Feature: Repeatable resource gathering with a selected Kintro |
| [#379](https://github.com/halisonworks/mod-codeling/issues/379) | Core | Feature: Repeatable furnace and crafting production workflow |
| [#380](https://github.com/halisonworks/mod-codeling/issues/380) | Core | Feature: Reusable robot construction routines and material estimates |
| [#396](https://github.com/halisonworks/mod-codeling/issues/396) | Core | As a player, I run a bounded mining routine and unload the gathered materials |
| [#397](https://github.com/halisonworks/mod-codeling/issues/397) | Core | As a player, I gather wood and replant a bounded area |
| [#398](https://github.com/halisonworks/mod-codeling/issues/398) | Core | As a player, I process a batch of raw materials into a crafted product |
| [#399](https://github.com/halisonworks/mod-codeling/issues/399) | Core | As a player, I estimate materials and build a parameterised structure with a chosen robot |
| [#408](https://github.com/halisonworks/mod-codeling/issues/408) | Core | Feature: Category-based inventory and container logistics |
| [#30](https://github.com/halisonworks/mod-codeling/issues/30) | Optional | Agent attack, interact and till |
| [#66](https://github.com/halisonworks/mod-codeling/issues/66) | Optional | Remaining inventory ops: getItemDetail and collect(block) |
| [#91](https://github.com/halisonworks/mod-codeling/issues/91) | Optional | Build from a text blueprint |
| [#92](https://github.com/halisonworks/mod-codeling/issues/92) | Optional | Copy and paste an area |
| [#94](https://github.com/halisonworks/mod-codeling/issues/94) | Optional | Use items: bone meal, buckets, shears, feeding |
| [#104](https://github.com/halisonworks/mod-codeling/issues/104) | Optional | As a player, my robot can till soil |
| [#116](https://github.com/halisonworks/mod-codeling/issues/116) | Optional | As a player, my robot uses a smithing table |
| [#152](https://github.com/halisonworks/mod-codeling/issues/152) | Optional | Import a schematic and turn it into code |
| [#153](https://github.com/halisonworks/mod-codeling/issues/153) | Optional | As a player, I import a build in the editor |
| [#154](https://github.com/halisonworks/mod-codeling/issues/154) | Optional | As a player, common schematic formats can be read |
| [#155](https://github.com/halisonworks/mod-codeling/issues/155) | Optional | As a player, an imported build becomes code |
| [#156](https://github.com/halisonworks/mod-codeling/issues/156) | Optional | As a player, blocks from other mods are kept |
| [#157](https://github.com/halisonworks/mod-codeling/issues/157) | Optional | As a player, I see what materials are missing |
| [#311](https://github.com/halisonworks/mod-codeling/issues/311) | Optional | Read the item in a robot or container slot |
| [#312](https://github.com/halisonworks/mod-codeling/issues/312) | Optional | Find all blocks of a kind, as a list |
| [#313](https://github.com/halisonworks/mod-codeling/issues/313) | Optional | Read a container's name |
| [#314](https://github.com/halisonworks/mod-codeling/issues/314) | Optional | Free space in a container |

### 0.5.0: Intelligent automation

Representative adaptive navigation, bounded working area, status and explicit programme assignment using the connected runtime; minimal durable state contract and safe interruption/recovery. Recheck world and permissions; no blind replay or promise of unloaded-chunk work or unattended interpreter. Existing navigation is reused; not every historical navigation extension is required.

| Issue | Scope | Title |
| --- | --- | --- |
| [#33](https://github.com/halisonworks/mod-codeling/issues/33) | Core | Epic: Robot movement and navigation (Codeling extensions) |
| [#43](https://github.com/halisonworks/mod-codeling/issues/43) | Core | Block names and categories in inspect and detect |
| [#88](https://github.com/halisonworks/mod-codeling/issues/88) | Core | Scan the area around the robot |
| [#158](https://github.com/halisonworks/mod-codeling/issues/158) | Core | Sensors and radar |
| [#220](https://github.com/halisonworks/mod-codeling/issues/220) | Core | Stored values between runs |
| [#345](https://github.com/halisonworks/mod-codeling/issues/345) | Core | Epic: Reliable automation jobs and recovery |
| [#354](https://github.com/halisonworks/mod-codeling/issues/354) | Core | Feature: Bounded job progress and interruption recovery |
| [#365](https://github.com/halisonworks/mod-codeling/issues/365) | Core | Story: As a player, I inspect and safely resume an interrupted automation job |
| [#400](https://github.com/halisonworks/mod-codeling/issues/400) | Core | Feature: Assign and monitor a saved programme on selected robots |
| [#401](https://github.com/halisonworks/mod-codeling/issues/401) | Core | Feature: Bound robot work areas and adaptive navigation |
| [#54](https://github.com/halisonworks/mod-codeling/issues/54) | Optional | Undo log for robot block changes |
| [#95](https://github.com/halisonworks/mod-codeling/issues/95) | Optional | More /robot commands: rename, tp, list, speed |
| [#105](https://github.com/halisonworks/mod-codeling/issues/105) | Optional | Doors and redstone |
| [#108](https://github.com/halisonworks/mod-codeling/issues/108) | Optional | As a player, my robot walks through doors on its own |
| [#110](https://github.com/halisonworks/mod-codeling/issues/110) | Optional | As a player, I know which pressure plates my robot triggers |
| [#160](https://github.com/halisonworks/mod-codeling/issues/160) | Optional | As a player, I see a radar view in the editor |
| [#163](https://github.com/halisonworks/mod-codeling/issues/163) | Optional | As a player, my robot reacts to vibrations |
| [#164](https://github.com/halisonworks/mod-codeling/issues/164) | Optional | As a player, my robot raises an alarm when something comes near |
| [#169](https://github.com/halisonworks/mod-codeling/issues/169) | Optional | Robot equipment: lamp and upgrade slots |
| [#170](https://github.com/halisonworks/mod-codeling/issues/170) | Optional | As a player, my robot has equipment slots |
| [#171](https://github.com/halisonworks/mod-codeling/issues/171) | Optional | As a player, my robot carries a lamp |
| [#172](https://github.com/halisonworks/mod-codeling/issues/172) | Optional | As a player, my robot places torches where it is dark |
| [#173](https://github.com/halisonworks/mod-codeling/issues/173) | Optional | As a player with a dynamic lights mod, the lamp uses it |

### 0.6.0: Collaborative systems

Same-owner robots communicate, reserve bounded jobs, hand off items and complete a documented coordinated example; contention, cancellation and uncertain completion tested. Shared ownership and cosmetic extensions optional.

| Issue | Scope | Title |
| --- | --- | --- |
| [#96](https://github.com/halisonworks/mod-codeling/issues/96) | Core | Epic: Multiple robots and collaboration |
| [#97](https://github.com/halisonworks/mod-codeling/issues/97) | Core | Several named robots per player, with messages between them |
| [#142](https://github.com/halisonworks/mod-codeling/issues/142) | Core | As a player, my robots send each other messages |
| [#143](https://github.com/halisonworks/mod-codeling/issues/143) | Core | Roles and work distribution |
| [#144](https://github.com/halisonworks/mod-codeling/issues/144) | Core | As a player, I give each robot a role |
| [#145](https://github.com/halisonworks/mod-codeling/issues/145) | Core | As a player, my robots share a job board |
| [#146](https://github.com/halisonworks/mod-codeling/issues/146) | Core | As a player, my robots hand items to each other |
| [#147](https://github.com/halisonworks/mod-codeling/issues/147) | Core | As a player, I have an example robot team |
| [#98](https://github.com/halisonworks/mod-codeling/issues/98) | Optional | Team robots shared by several players |
| [#148](https://github.com/halisonworks/mod-codeling/issues/148) | Optional | Robot skins and name tags |
| [#149](https://github.com/halisonworks/mod-codeling/issues/149) | Optional | As a player, I choose a skin for each robot |
| [#150](https://github.com/halisonworks/mod-codeling/issues/150) | Optional | As a player, each role has its own look |

### 0.7.0: Learning and creativity

Independent learner can discover, understand, run and modify representative examples in both robot-free and robot paths; accessible contextual help, offline references, optional guided tutorials and reuse, EN/NB documentation. #117 uses existing local project storage unless approved architecture requires #64; entity/global gameplay extensions are conditional.

| Issue | Scope | Title |
| --- | --- | --- |
| [#99](https://github.com/halisonworks/mod-codeling/issues/99) | Core | Epic: Learning and motivation |
| [#100](https://github.com/halisonworks/mod-codeling/issues/100) | Core | In-editor tutorials with goals in the game |
| [#117](https://github.com/halisonworks/mod-codeling/issues/117) | Core | My functions: reuse my own functions in other projects |
| [#118](https://github.com/halisonworks/mod-codeling/issues/118) | Core | As a player, a tutorial teaches me to make my first function |
| [#214](https://github.com/halisonworks/mod-codeling/issues/214) | Core | Epic: Programming concepts for learners |
| [#274](https://github.com/halisonworks/mod-codeling/issues/274) | Core | Editor API reference, with help in the editor |
| [#275](https://github.com/halisonworks/mod-codeling/issues/275) | Core | Game messages in Norwegian |
| [#382](https://github.com/halisonworks/mod-codeling/issues/382) | Core | Feature: Contextual block help and runnable examples in Halison Code |
| [#383](https://github.com/halisonworks/mod-codeling/issues/383) | Core | As a player, I discover concise help by hovering over a block |
| [#384](https://github.com/halisonworks/mod-codeling/issues/384) | Core | As a player, I inspect block parameters, results and examples without leaving my project |
| [#385](https://github.com/halisonworks/mod-codeling/issues/385) | Core | As a player, I open a tested example from block help |
| [#386](https://github.com/halisonworks/mod-codeling/issues/386) | Core | Maintain help coverage when product-owned programming blocks change |
| [#49](https://github.com/halisonworks/mod-codeling/issues/49) | Optional | Spawn the robot in front of the player |
| [#64](https://github.com/halisonworks/mod-codeling/issues/64) | Optional | Projects stored on disk (pxt file workspace) |
| [#101](https://github.com/halisonworks/mod-codeling/issues/101) | Optional | Achievements for robot milestones |
| [#181](https://github.com/halisonworks/mod-codeling/issues/181) | Optional | Mazes: build and solve |
| [#183](https://github.com/halisonworks/mod-codeling/issues/183) | Optional | As a child, I write my own maze generator |
| [#222](https://github.com/halisonworks/mod-codeling/issues/222) | Optional | Editor in its own window over the game (browser app mode) |
| [#280](https://github.com/halisonworks/mod-codeling/issues/280) | Optional | Share code with other players |
| [#290](https://github.com/halisonworks/mod-codeling/issues/290) | Optional | The robot log follows the newest line |
| [#368](https://github.com/halisonworks/mod-codeling/issues/368) | Optional | Feature: Evaluate bounded entity programming for creative gameplay |
| [#369](https://github.com/halisonworks/mod-codeling/issues/369) | Optional | Feature: Evaluate gameplay controls and additional world events |
| [#392](https://github.com/halisonworks/mod-codeling/issues/392) | Optional | As a player, I react to an item interaction or block placement in my own programme |
| [#393](https://github.com/halisonworks/mod-codeling/issues/393) | Optional | As a player, I show challenge feedback without changing global game rules |
| [#394](https://github.com/halisonworks/mod-codeling/issues/394) | Optional | Evaluate a bounded entity-based creative challenge |

### 0.8.0: Optional extensions

No mandatory standalone release gate. Evaluate approved integrations/consumer needs with reproducible evidence and permission boundaries. Defer any option without demonstrated value, safe scope or owner approval; no requirement to ship this version if no optional capability is approved.

| Issue | Scope | Title |
| --- | --- | --- |
| [#65](https://github.com/halisonworks/mod-codeling/issues/65) | Optional | Upload a finished program to the robot |
| [#247](https://github.com/halisonworks/mod-codeling/issues/247) | Optional | Brand-neutral event API for other mods |
| [#261](https://github.com/halisonworks/mod-codeling/issues/261) | Optional | Update the link to the Eira mods: now the Narratimo mods, in mod-narratimo-worlds |
| [#387](https://github.com/halisonworks/mod-codeling/issues/387) | Optional | Epic: Connected Minecraft worlds and optional programming extensions |
| [#388](https://github.com/halisonworks/mod-codeling/issues/388) | Optional | Feature: Validate an optional Narratimo Worlds integration |
| [#389](https://github.com/halisonworks/mod-codeling/issues/389) | Optional | Feature: Evaluate a second Halison Code target using a real consumer |
| [#390](https://github.com/halisonworks/mod-codeling/issues/390) | Optional | Feature: Evaluate optional assisted programming and explainable code suggestions |

### 0.9.0: Release candidate

Freeze required scope, validate exact candidate artefacts and supported matrix, clean installation/upgrade/rollback, core journeys, security, accessibility, performance and documentation. No unresolved critical security, data-loss, crash or core workflow defect; owner accepts candidate and residual risks.

| Issue | Scope | Title |
| --- | --- | --- |
| [#402](https://github.com/halisonworks/mod-codeling/issues/402) | Core | Epic: Release candidate validation and product quality |
| [#404](https://github.com/halisonworks/mod-codeling/issues/404) | Core | Feature: Validate supported installation and upgrade matrix |
| [#405](https://github.com/halisonworks/mod-codeling/issues/405) | Core | Feature: Verify security, multiplayer isolation and performance budgets |
| [#406](https://github.com/halisonworks/mod-codeling/issues/406) | Core | Feature: Validate core player journeys, accessibility and documentation |
| [#286](https://github.com/halisonworks/mod-codeling/issues/286) | Optional | Path search and crafting create many short-lived objects |
| [#287](https://github.com/halisonworks/mod-codeling/issues/287) | Optional | Keep one FakePlayer per robot if the cost is real: measure first |

### 1.0.0: Stable release

Accepted candidate and explicit release approval, verified artefacts/checksums and accurate listings, support/compatibility and vulnerability policy, migration and rollback guidance. Optional integrations, additional distribution channels and deferred features do not block stable standalone release.

| Issue | Scope | Title |
| --- | --- | --- |
| [#403](https://github.com/halisonworks/mod-codeling/issues/403) | Core | Epic: Stable public release and sustainable maintenance |
| [#407](https://github.com/halisonworks/mod-codeling/issues/407) | Core | Feature: Publish verified stable artefacts and maintain support policy |
| [#268](https://github.com/halisonworks/mod-codeling/issues/268) | Optional | Dependabot for Actions, Gradle and npm |
| [#276](https://github.com/halisonworks/mod-codeling/issues/276) | Optional | Publish on CurseForge |

### Deferred: Unscheduled extensions

No target release or commitment. Revisit after evidence of player value, feasibility, safety and maintenance cost. Any future implementation needs an explicit scope decision.

| Issue | Scope | Title |
| --- | --- | --- |
| [#103](https://github.com/halisonworks/mod-codeling/issues/103) | Deferred | As a player, my robot can attack mobs |
| [#119](https://github.com/halisonworks/mod-codeling/issues/119) | Deferred | Villagers and NPCs |
| [#120](https://github.com/halisonworks/mod-codeling/issues/120) | Deferred | As a player, my program sees who stands in front of the robot |
| [#121](https://github.com/halisonworks/mod-codeling/issues/121) | Deferred | As a player, my program sees what a villager sells |
| [#122](https://github.com/halisonworks/mod-codeling/issues/122) | Deferred | As a player, my robot trades with a villager |
| [#123](https://github.com/halisonworks/mod-codeling/issues/123) | Deferred | As a player, my robot gives items to an NPC |
| [#124](https://github.com/halisonworks/mod-codeling/issues/124) | Deferred | As a player, my robot right-clicks an entity |
| [#165](https://github.com/halisonworks/mod-codeling/issues/165) | Deferred | Invisibility and camouflage |
| [#166](https://github.com/halisonworks/mod-codeling/issues/166) | Deferred | As a server owner, I decide how much robots can hide |
| [#167](https://github.com/halisonworks/mod-codeling/issues/167) | Deferred | As a player, my robot can turn invisible |
| [#168](https://github.com/halisonworks/mod-codeling/issues/168) | Deferred | As a player, my robot can disguise itself as a block |
| [#190](https://github.com/halisonworks/mod-codeling/issues/190) | Deferred | Phase 6: Minecraft 1.20.1 (Forge and Fabric), optional |
| [#259](https://github.com/halisonworks/mod-codeling/issues/259) | Deferred | Robot looks at the player through the PC webcam |

## Dependency and decision sequence

Core outcome sequence: baseline relay/editor and server authority (#1, #10, #50, #57); approved identity (#343); logical capability and authority contracts (#344, #350, #352, #367); explicit robot references and workflows (#372, #376, #377, #306); bounded jobs and state (#345, #220); same-owner coordination (#96); learning and creative projects (#99, #214); release candidate (#402); stable release (#403). Optional #387 is not on the standalone critical path. These are outcome dependencies, not a claim that every child of an earlier epic is blocking. In particular, #395 informs later extensions; #220 contract precedes recovery implementation; #65 is an optional runtime decision; #98 is not required for same-owner teams.

## Maintenance and final review

Review the allocation when issues are added, closed, split or moved. Keep exactly one canonical parent for active non-epics and distinguish blockers from related work and historical evidence. Validate explicit dependency edges, conditional prose dependencies and core-to-optional dependencies; use exact commit and test evidence before closing implemented work. Reconcile historical issue text and native milestone/parent metadata when supported. Do not bulk-close issues on changelog evidence alone. The final planning review on 24 November 2026 verified 192 unique allocations, 150 closed issues, no canonical hierarchy violations, 55 unique active explicit dependency edges after the #23 correction, no detected cycles in the preceding 56-edge graph, no later-milestone blockers and no explicit core-to-optional blockers. Historical dependency sections and related links are not hard edges. This is a structural review, not exhaustive semantic validation or release acceptance. Remaining scope ambiguities, actual release evidence, native metadata and owner approval are handed over in [HANDOVER.md](HANDOVER.md).

Sources: [architecture](architecture.md), [open source readiness](open-source-readiness.md), [player guide](player/guide-en.md), [changelog](../CHANGELOG.md), and linked GitHub issues. Snapshot source: open issue listing on 24 November 2026; develop commit at document creation: `868555e8383eb2cab9991a32fae789a521b39453`.
