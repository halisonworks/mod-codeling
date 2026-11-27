# The editor target: build, versions and boundaries

Issue #342 (parent #336). This page records how the Halison Code editor (the pxt target in `target/`) is rebuilt from a
clean checkout, which versions it is pinned to, and which parts belong to Halison Code and which could serve another
target. Build commands are in [CONTRIBUTING.md, Build](../CONTRIBUTING.md#build); this page does not repeat them.

## Evidence: a clean checkout (2026-11-28)

A fresh `git clone` of `develop` at `58aef43` (release 0.0.7), on Windows 11, Node 26.13.0, npm 11.10.0, Gradle 9.8.2 (daemon on Java 25). No cache from
the working copy: `target/node_modules` and `target/built` did not exist before.

| Step | Result | Time |
|---|---|---|
| `npm ci` in `target/` | OK | 195 s |
| `npm run build` (first) | OK | 27 s |
| `npm run build` (second) | OK | 17 s |
| `npm run staticpkg` | OK, 89 entries in `target/built/packaged`, 53 MB | 21 s |
| `./gradlew :fabric:26.3:editorTest` | OK: 129 blocks, 8 example programs, 7 fixtures, 298 checks, 0 failed | 909 s (most of it Gradle setting up a fresh checkout) |

CI does the same on every push (`.github/workflows/ci.yml`, job `build`, Node 24), and the release workflow
(`release.yml`) builds the editor the same way before the jars.

## Pinned versions

| What | Version | Where it is pinned |
|---|---|---|
| pxt-core | 13.3.1, exact | `target/package.json`, `target/package-lock.json` (resolved from npm) |
| TypeScript (sim build) | 4.8.3, exact | `target/package.json` devDependencies; the version pxt-core 13.3.1 expects |
| semantic-ui-less | 2.4.1 | `target/package.json` devDependencies (pxt's theme build) |
| Blockly, Monaco | as bundled by pxt-core 13.3.1 | not ours to pin; they come inside pxt-core |
| Node.js | 24 in CI; 26 worked here | `.github/workflows/ci.yml`, `release.yml` |
| Target version | `target/package.json` `version` | shown in pxt's About box; not the mod version |

`npm ci` installs exactly the lock file, so two clean builds of the same commit use the same pxt. A pxt upgrade is a
change to `package.json` and the lock file in one pull request, and the build fails if one of the `main.js` patches
in `gradle/loader.gradle.kts` no longer matches (see below).

## Offline

The packaged editor makes no requests to other hosts:

- `pxtarget.json` turns off every cloud feature (`cloud.workspace`, `sharing`, `packages`, `githubPackages`,
  `importing`, `publishing`, `thumbnails`, `showBadges`), live translations (`disableLiveTranslations`), feedback and
  the Immersive Reader.
- `target/docfiles/apptracking.html` replaces pxt's telemetry snippet, and the jar leaves out `ai.2.min.js`.
- The editor suite checks it: the gallery section records every request and fails on one to another origin.

The editor is served by the relay on `127.0.0.1` from the jar (`halisoncode-editor/`), so it works without internet.
Projects are kept in the browser (IndexedDB, pxt's own storage).

## Simulator transport

pxt runs a program in its simulator iframe. Ours has no simulated world: `target/sim/simulator.ts` (`class Relay`)
opens a WebSocket to `ws://<editor host>/relay` and sends each robot call to the game, which answers it
([architecture.md](architecture.md), "Message protocol" and "Auth and token flow"). The calls themselves are
`target/sim/api.ts`, with the block annotations; pxt generates `target/libs/core/sims.d.ts` from it (hence the
second `npm run build`).

## Project upgrades

Projects saved by an older editor are upgraded when they load:

- `target/editor/extension.ts` (pxt's `blocklyPatch`, loaded as `editor.js` because `appTheme.extendEditor` is on)
  rewrites Blocks XML from before commit d506004 (issue #125).
- The editor suite loads the saved projects in `e2e/fixtures/` (an old Blocks file, its TypeScript, and an IndexedDB
  dump saved by 0.0.1) and checks that their values survive.

A change that renames a block, an enum member or a field needs an upgrade here and a fixture that proves it.

## Responsibilities

| Part | Files | Owner |
|---|---|---|
| The Minecraft API, its blocks and enums | `target/sim/api.ts`, `target/libs/core/*.d.ts` (`blocks.d.ts` and `items.d.ts` are generated from `BlockIds`) | Halison Code |
| Branding, theme, About page, examples | `target/pxtarget.json` `appTheme`, `target/theme/`, `target/docs/` | Halison Code |
| Translations | `target/locales/nb/` | Halison Code |
| Project upgrades | `target/editor/extension.ts`, `e2e/fixtures/` | Halison Code |
| Patches to pxt's bundled `main.js` | `gradle/loader.gradle.kts` (six patterns: MakeCode branding, Norwegian locale codes, debug-mode restart, issues #16, #217, #315) | Halison Code, but each patch is a candidate to report upstream to pxt |
| The relay transport in the simulator | `target/sim/simulator.ts` `Relay` | Reusable in principle: any pxt target that runs its programs in a host application over a local WebSocket |
| No-telemetry, no-cloud settings | `target/pxtarget.json` `cloud`, `target/docfiles/apptracking.html` | Reusable as a recipe, not as code |
| The relay server and protocol | `core/src/main/java/halisoncode/relay`, `core/.../net/Protocol.java` | Halison Code; plain Java with no Minecraft classes, so it could serve another host |

## Extraction gate

Nothing is extracted into a shared package or another repository now. A part in the "reusable" rows above moves out
only when all of these hold:

1. A second consumer exists and has asked for it (another target or host), not a hypothetical one.
2. The part has its own tests that run without the Halison Code target, so the split cannot break it silently.
3. The owner approves the split, its repository and its licence notices (pxt is MIT; Blockly inside it is Apache-2.0).
4. The editor suite and the fixtures pass unchanged on both sides of the split.

Until then the parts stay in this repository, and this table is where a future split starts.
