# Claude Code handover: Codeling / Halison Code for Minecraft

Date: 24 November 2026. Repository: `halisonworks/mod-codeling`. Planning PR: [#411](https://github.com/halisonworks/mod-codeling/pull/411), merged to `develop` on 25 November 2026. This is a maintainer handover, not permission to merge, publish, rename or change security policy.

## 1. Mission and immediate priorities

Take over the existing project and its issue plan. Stop expanding the backlog speculatively. First reconcile the live repository and issue state, finish the planning PR review, then work towards the reliable 0.1.0 public beta under the current Codeling identity. Implement or verify only genuine gaps. Preserve concurrent maintainer work. Use English (UK spelling for new planning prose), and Norwegian for the Norwegian player guide and localisation. Follow repository text checks.

Start by reading [ROADMAP.md](ROADMAP.md), [CONTRIBUTING.md](../CONTRIBUTING.md), [README.md](../README.md), [architecture.md](architecture.md), [CHANGELOG.md](../CHANGELOG.md), [open-source-readiness.md](open-source-readiness.md), both player guides and [SECURITY.md](../SECURITY.md). Read current issue bodies and linked PRs, not just titles. The old readiness report and early issue descriptions are historical; later evidence may supersede them.

## 2. Baseline and planning state

- 0.0.5 was published as a pre-release on 24 November 2026: tag `v0.0.5` on `main` at `da47097`, 20 JARs and SHA256SUMS. Its changes are in the 0.0.5 section of the changelog. The development version is 0.0.6 (#412). Verify actual tags/releases; do not equate a development version with a published artefact.
- Roadmap creation base: develop commit `868555e8383eb2cab9991a32fae789a521b39453`. The roadmap allocation commit was `fffc9d99cca334653ebf8e5f96411292addc5e36`. Fetch current heads; these are evidence references, not checkout targets for new work.
- Final planning snapshot: 192 open issues, 150 closed; 112 core, 67 optional with proposed targets, 13 deferred. All 192 allocated exactly once in ROADMAP. Explicit dependency graph after #23 correction: 55 unique active edges, no later-version blockers or core-to-optional blockers. Previous 56-edge graph had no detected cycles; removing the #23 edge cannot introduce one. Canonical hierarchy check found no violations. Historical lines beginning Parent or Blocked by must not be parsed as active relations. Prose and conditional dependencies still need engineering review.
- Planning changed issues and documentation only. No product code, new runtime tests, release build, publication, trademark clearance, native milestone creation or native sub-issue setup was performed in the planning work of #411 itself. Milestones and sub-issues were set afterwards, see section 6.
- Old catch-all #63 is closed as a planning consolidation, not as implemented work. Duplicate plans #330-335 and #337-341 were closed in favour of canonical #343/#344 and their children. #336/#342 remain active. Do not recreate those plans.
- PR #411 is merged and its branch is deleted. Change the plan in new PRs from `develop`. Do not force-push or alter another session's branch.

## 3. Product decisions and boundaries

Halison Code is the proposed editor identity, Halison Code for Minecraft the proposed mod identity, Kintro the proposed robot identity. Owner agreed the product direction; naming clearance and distribution compliance still require #58/#355. Retain Codeling for the 0.1.0 beta unless the owner changes that decision. Do not rename mod IDs, commands, packages, repository, origin, storage keys or saved entity IDs simply to match branding.

General Minecraft programming is a first-class capability, not all code requires a robot. Kintro is a resource-aware survival automation capability. Establish logical boundaries and capability registration first; a physical split into two JARs, editor repository extraction or a second target needs separate evidence and approval. Preserve existing projects, inventory, robot identity and server authority.

Named robots already exist. Extend explicit reference/selection in Blocks (#372-375), not global mutable default selection. Keep legacy agent semantics. Distinguish concurrent branches, per-robot queues, actual command completion and safe stop. Same-owner robot collaboration precedes optional cross-player control (#98). Recovery must reconcile uncertain world changes; never blindly repeat destructive work.

Current execution runs user code in the local browser PXT simulator; typed commands are relayed to the client and validated by the server. Do not introduce arbitrary server-side code, unrestricted commands, forced chunk loading, cloud dependencies or unattended execution. #65 is an optional decision gate, not an implementation instruction. AI assistance #390 is optional, reviewable, never auto-run and requires separate privacy approval. Narratimo Worlds #388 is optional; keep both products independent and do not trust programme-declared success as authoritative world evidence.

## 4. First execution tranche: 0.1.0

Use #57 as release gate and the 0.1.0 rows in ROADMAP as the scope register. Start with evidence and residual gaps, not new architecture.

| Workstream | Issues | What to do first |
| --- | --- | --- |
| Relay and first programme | #1, #6, #3, #8 | Verify real client/browser/robot round trip, blocked move, stop, disconnect and busy port; document safe manual recovery and project retention. Historical branch/test counts are not current proof. |
| Editor baseline | #10, #20, #22 | Verify supported language examples and orientation (-180 north reported in 0.0.4); save/open and round trips. Advanced MakeCode parity #31/#370/#395 is later. |
| Server authority and limits | #50, #51, #52, #53 | Inspect actual permissions, config and resource caps; verify allowed/denied cases, protection and measured performance. One claim mod is tried on both loaders (#55); item pickup in a claim and other claim mods are not. Do not change survival semantics to satisfy stale wording. |
| Dedicated multiplayer | #55 | Results and a checklist are on the issue, with loader, version and claim mod. Remaining: the open points of the checklist, players on separate machines, and other Minecraft versions than 26.3. |
| Licence | #59 | Unreleased changelog reports replacement of previously unlicensed Norwegian strings. Verify source provenance and packaged notices; closure requires evidence, not changelog alone. |
| Release polish | #278, #279, #284 | 0.0.4 reports message, diagnostic and animation improvements; compare remaining original criteria and actual tests. Check report redaction and measured performance. Accept or explicitly disposition residual scope with maintainer. |
| Documentation and listing | #198, #272, #273, #277 | Minimum truthful operator/install guidance, commands and rules, approved artwork and listing. Current-name compliance and disclaimer; do not promise unsupported versions. |
| Release | #62, #57 | Candidate artefacts, checksums, packaged notices, fresh installation, supported upgrades and rollback on copies. Obtain explicit owner go/no-go before publication. |

P0 #1/#3 first; P1 safety and distribution checks may run in parallel. Optional #7/#9 conveniences, #56 noncritical stale links, #79/#81/#82 feedback, #285 measurement and #297 pause rule do not automatically block beta. Any actual critical safety, data-loss or core-function defect blocks release irrespective of original scope.

Evidence already reported: #133 whole-chain automation, #138 load test, #139 fuzzing, #191 token command-line protection, #197 release rehearsal, #267 private vulnerability reporting, #283 bounded editor log, #293 default robot naming, #326 Fabric connection documentation and #371/#381 NeoForge connection fix. These are regression inputs. A modded client joining a server without Codeling was tried again after #381 with the released 0.0.5: it joins on both loaders; on NeoForge the editor is also told that the server lacks Codeling (the editor message was not tried again on Fabric, which #381 does not change). Two-player isolation is reported separately on #55: programs at once, also with the same robot name, spawn protection, stop and leaving, on 26.3 for both loaders, driven by a script on one machine in offline mode. Claims are reported for Open Parties and Claims 0.33.0 on both loaders. The comments and the checklist on #55 state what was not tried.

## 5. Architecture and test workflow

Check live CONTRIBUTING before using commands; it is authoritative. At snapshot: Gradle wrapper 9.8.2, Node 24, JDK 21+ for wrapper, daemon Java 25 with provisioned 21/17 toolchains as applicable. core is Minecraft-free Java; common holds shared Minecraft code; fabric and neoforge loader modules use Stonecutter, active source version 26.3. Ten build targets per loader are listed in settings.gradle.kts: 26.3, 26.2, 26.1.2, 1.21.11, 1.21.10, 1.21.8, 1.21.5, 1.21.4, 1.21.3, 1.21.1. Actual compatibility ranges must be verified from metadata and tests, not inferred from target names.

Editor in target/; e2e/ for browser regression fixtures. Build editor first: npm ci in target, npm run build twice when API changes, npm run staticpkg. Never use npx pxt. target/libs/core/sims.d.ts is generated from target/sim/api.ts; edit authoritative source, not generated declarations by hand. ns.ts defines namespaces. Preserve block IDs and legacy upgrade paths. Keep Stonecutter conditional branches and active source version intact.

Useful commands (verify live build scripts):

```sh
bash scripts/check-text.sh
./gradlew :core:test
./gradlew :fabric:26.3:test
./gradlew :fabric:26.3:runGameTest
./gradlew :neoforge:26.3:runGameTestServer
./gradlew :core:relayCompatTest
./gradlew :fabric:26.3:editorTest
./gradlew :fabric:26.3:runClientGameTest
```

Editor/relay compatibility and client tests need built editor; Playwright setup and real-window requirements are in CONTRIBUTING. Use corresponding 1.21.1 and other supported target tests for affected code. Run client GameTests before PRs affecting relay/client/UI/camera because normal CI may run them only on main/manual. Use temporary test worlds, no production saves. Manual two-player/claim-mod checks need real evidence; request maintainer participation if unavailable. Report what ran, what failed, what was skipped and why.

## 6. Planning metadata migration with gh

The owner approved a preview, and the migration was done on 25 November 2026 with `gh`. Read back from GitHub afterwards:

- 11 milestones were created: 0.1.0 to 1.0.0 as in ROADMAP, and `Deferred (unscheduled)`. All 192 open issues have the milestone of their ROADMAP target.
- The seven old milestones are kept and still open. They hold only closed issues, and no closed issue was changed.
- All 176 open issues with a Parent line have that parent as native sub-issue parent: 72 new, 22 moved, 82 already right. The first Parent line is the canonical one. The 16 epics have no parent.
- Native blockers were not changed. Of the 53 explicit active edges found in the issue text in this check (the planning review counted 55, and the difference is not explained), 21 are not native blockers. 8 native blockers are not in the text. Review these before changing them.

Keep ROADMAP and the native metadata aligned when issues change. Commands to read the state:

```sh
gh auth status
gh repo view halisonworks/mod-codeling
gh api --paginate 'repos/halisonworks/mod-codeling/milestones?state=all&per_page=100'
gh issue list -R halisonworks/mod-codeling --state open --limit 500 --json number,title,body,labels,milestone
```

For later changes: inspect the current state, preview the exact mutations and get approval before bulk changes. Use actual milestone numbers. Map canonical Parent lines to actual issue IDs, and do not mistake historical review sections for current Parent lines. Keep one parent per active non-epic; do not use parenthood as a blanket blocking edge. Verify by re-reading live state, and record exceptions. Native dependencies should mirror only actual hard dependencies, not related or conditional links.

## 7. Maintainer workflow and issue quality

Follow CONTRIBUTING: branch from develop, one topic per PR, link issue, green CI, maintainer review/merge; only release PR from develop to main. Never push directly to main. Inspect git status and current worktrees, open PRs, assignees and comments before claiming an issue. Do not overwrite or revert unrelated changes. For large work or protocol/editor API/saved-data changes, obtain agreed plan first. Keep credentials and tokens out of logs and public issue text; report vulnerabilities privately per SECURITY.md.

Each issue should contain context, JTBD, player value, scope/non-goals, explicit parent and actual blockers, priority rationale, proposed version, testable acceptance and evidence/definition of done. Epic > Feature > Story > Task/Bug. P1 is high within its milestone, not permission to skip prerequisites. Preserve prior evidence when changing scope. Do not close a feature simply because its parent moved or changelog mentions it. If already implemented, verify residual acceptance against code and tests and link evidence; if duplicate, link canonical issue and distinguish consolidation from completion.

When handing back a tranche, state completed issues and PRs, exact commit and tests, unresolved blockers, decisions needed, and next recommended issue. Keep work small enough for independent review. No automatic merge, release, destructive migration or external service changes.

## 8. Remaining decisions and known gaps

- Confirm core vs optional scope in ROADMAP, especially the 0.4 logistics prerequisites (#311-314), adaptive sensing #88/#158, 0.7 library and tutorial minimum, and whether conditional #23 teleport is necessary for a required example. No explicit graph conflict remains, but prose is not an executable dependency specification.
- Some older issue text remains historical or inconsistent with later review sections. For example #97 original single-robot-era wording, #214 historical missing events, #1 early branch status, #51 permission wording and older native milestones. Resolve when touching each issue; do not erase useful history.
- Final naming clearance, Minecraft branding and platform rules, exact supported compatibility matrix, claim-mod results, thresholds for tick/frame/memory performance, minimum state persistence contract and 1.0 scope require actual evidence/owner decisions.
- ROADMAP defines 0.8 optional extensions, not a mandatory gate or promise to release that version. Defer camera/webcam, invisibility, legacy 1.20.1 and optional NPC/attack work unless separately approved.
- No implementation estimates or delivery dates were established. Do not invent them. Reassess when actual residual work is known.

## 9. Suggested first Claude Code session

1. Read the documents and inspect current repository state, issue #57 and the merged planning PR #411.
2. Produce a concise delta report since the planning snapshot, including any issue or release changes.
3. Review roadmap allocation and explicit/conditional dependency conflicts against the native metadata, which is already migrated (section 6). Do not make release or naming decisions.
4. With maintainer agreement, choose one P0/P1 0.1.0 issue not already being worked on, preferably #3/#6 evidence or #53/#55 if relay is already accepted. Create a focused branch, verify before changing code, record tests and open a reviewable PR.
5. Continue through the 0.1.0 release gate. Escalate missing manual evidence or policy approval rather than silently lowering acceptance criteria.

The objective is to ship a reliable product, not to keep growing the planning backlog.
