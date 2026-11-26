<h1 align="center">Halison Code</h1>

<!-- Issue #198: when the logo is in docs/images, this replaces the heading above.
<p align="center">
  <picture>
    <source media="(prefers-color-scheme: dark)" srcset="docs/images/logo-dark.svg">
    <img alt="Halison Code" src="docs/images/logo-light.svg" width="360">
  </picture>
</p>
-->

<h3 align="center">Program a robot in Minecraft with blocks, JavaScript or Python</h3>

<p align="center">
  <a href="https://github.com/halisonworks/mod-halisoncode/releases">Download</a> ·
  <a href="docs/player/guide-en.md">Player guide</a> ·
  <a href="docs/player/guide-nb.md">Norsk veiledning</a> ·
  <a href="docs/server-admin.md">For servers and schools</a>
</p>

<p align="center">
  <a href="https://github.com/halisonworks/mod-halisoncode/releases"><img alt="Release" src="https://img.shields.io/github/v/release/halisonworks/mod-halisoncode?include_prereleases"></a>
  <a href="https://github.com/halisonworks/mod-halisoncode/actions/workflows/ci.yml"><img alt="CI" src="https://img.shields.io/github/actions/workflow/status/halisonworks/mod-halisoncode/ci.yml?branch=develop&logo=github"></a>
  <img alt="Minecraft 1.21.1 to 26.3" src="https://img.shields.io/badge/Minecraft-1.21.1%20to%2026.3-62B47A">
  <img alt="Fabric and NeoForge" src="https://img.shields.io/badge/loader-Fabric%20%7C%20NeoForge-DBD0B4">
  <a href="LICENSE"><img alt="MIT licence" src="https://img.shields.io/github/license/halisonworks/mod-halisoncode"></a>
  <!-- After issue #62 (Modrinth): https://img.shields.io/modrinth/dt/<slug>?logo=modrinth -->
</p>

<!-- Issue #198: a GIF of the editor and the robot, about 10 seconds.
<p align="center">
  <img alt="Blocks in the editor on the left, the robot Kintro building a staircase in Minecraft on the right" src="docs/images/hero.gif" width="720">
  <br><em>Drag blocks in the editor, press Run, and Kintro builds in your world.</em>
</p>
-->

Halison Code is a Minecraft Java mod for children who are learning to program, and for the adults who help them.
You write a program for your robot, Kintro, in an editor in your browser, in the style of Microsoft MakeCode. The
robot does it in your world: it walks, builds, digs, crafts and fills chests.

> [!NOTE]
> Halison Code is a pre-release (0.0.6) for testing. Up to 0.0.5 it was called Codeling; robots and projects from
> then are not carried over.

## ✨ What you can do

- 🧱 **Three ways to code.** Blocks, JavaScript and Python, and you can switch between them in the same project.
- 🤖 **A robot that works in your world.** Move, build, dig, find blocks, fill shapes, craft, smelt and sort chests.
- 👥 **Several robots.** Give them names and let them work at the same time.
- 🐞 **See what happens.** Step through a program block by block, with a robot log and a speech bubble over the robot.
- 🎮 **React to the game.** Chat commands, a player who dies, a block that breaks, redstone and daybreak.
- 🏫 **Safe for classes and servers.** Robots follow claims, spawn protection, game modes and permission mods.
- 🇳🇴 **The editor in English and Norwegian**, and a player guide in both.

## 📥 Install

You need Minecraft Java Edition, a computer (Windows, macOS or Linux) and a web browser. A parent can do the install;
it takes about ten minutes.

1. Install a mod loader: [Fabric](https://fabricmc.net/use/) with [Fabric API](https://modrinth.com/mod/fabric-api),
   or [NeoForge](https://neoforged.net/).
2. Download the Halison Code `.jar` for your loader and Minecraft version from
   [the releases](https://github.com/halisonworks/mod-halisoncode/releases).
3. Put the `.jar` in your `mods` folder and start the game.

The [player guide](docs/player/guide-en.md#install) has every step, with the file for each version.

| Minecraft | Fabric | NeoForge | Java |
|---|:---:|:---:|:---:|
| 26.1 to 26.3 | ✅ | ✅ | 25 |
| 1.21.1 to 1.21.11 | ✅ | ✅ | 21 |

## 🚀 Your first program

1. In a world, type `/robot spawn`. Kintro appears at your feet.
2. Type `/halisoncode`. The editor opens in your browser.
3. Drag **agent move forward by 1** into the workspace, change 1 to 3, and press ▶️ Run.

```js
agent.move(SixDirection.Forward, 3)
agent.turn(TurnDirection.Right)
```

Then open one of the eight examples on the editor's start page: a staircase, a tunnel, a guard patrol, torches in the
dark, and more. The [guide](docs/player/guide-en.md) explains every block and command.

## 💬 Help

| Where | Best for |
|---|---|
| [Player guide](docs/player/guide-en.md) ([norsk](docs/player/guide-nb.md)) | Installing, your first programs, every command |
| [Server and school guide](docs/server-admin.md) | Game rules, permissions, multiplayer |
| [Report a bug](https://github.com/halisonworks/mod-halisoncode/issues/new?template=bug_report.yml) | Something does not work |
| [Suggest an idea](https://github.com/halisonworks/mod-halisoncode/issues/new?template=feature_request.yml) | Something you miss |
| [SECURITY.md](SECURITY.md) | Security problems, reported privately |

## 🛠️ Contribute

Bug reports, ideas, texts, translations and code are welcome, in English or Norwegian.
[CONTRIBUTING.md](CONTRIBUTING.md) says how to build, test and open a pull request, and
[docs/development.md](docs/development.md) describes the code: the modules, the build, the editor and its API.

## 📜 Licence and credits

MIT, see [LICENSE](LICENSE). The editor is built on [Microsoft MakeCode (pxt)](https://github.com/microsoft/pxt) and
Blockly, both MIT; [THIRD-PARTY-NOTICES.md](THIRD-PARTY-NOTICES.md) has the notices and licence texts.

Not an official Minecraft product. Not approved by or associated with Mojang or Microsoft.
