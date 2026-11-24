# Text for the download pages

Text for issue #277. Nothing here is published. The owner approved the text for now on 2026-11-24, and approves the
final text and the pictures before publishing. Modrinth and CurseForge get the same text, English first and then
Norwegian. Change it here before changing a page.

This text was drafted with generative AI. See "Rules of the sites".

## Fields

| Field | Value |
|---|---|
| Title | Codeling |
| Summary (English) | Program a robot in your world with blocks, JavaScript or Python, in an editor in your browser. |
| Summary (Norwegian) | Programmer en robot i verdenen din med blokker, JavaScript eller Python, i en editor i nettleseren. |
| Licence | MIT |
| Environment | Client and server |
| Loaders | Fabric, NeoForge |
| Dependencies | Fabric API (Fabric only) |
| Source, issues | https://github.com/halisonworks/mod-codeling |
| Icon, gallery | From #198: real pictures from the game and the editor |

## English

**NOT AN OFFICIAL MINECRAFT PRODUCT. NOT APPROVED BY OR ASSOCIATED WITH MOJANG OR MICROSOFT.**

> This is a pre-release, for testing. Make a backup of worlds you care about.

Codeling puts a small robot in your world. You program it in your web browser, with blocks you drag together or with
JavaScript or Python. Press Run, and the robot does it in the game.

**What the robot can do**

- Walk, fly, turn, and find its own way to a place.
- Break and place blocks, and build whole shapes.
- Look around: blocks, creatures, light and distance.
- Carry items, use chests and furnaces, and craft.
- React when you type in the chat, when you break a block, or when redstone changes.
- Work together with your other robots, when the world allows more than one.

**Where your code runs**

The editor comes with the mod and opens in the browser on your own computer. It needs no account and works without
internet. Codeling collects nothing and sends nothing by itself. Your program runs in your browser. On a server, only
the robot's commands are sent to it, and the server checks each one.

**Install**

1. Install Fabric Loader and Fabric API, or NeoForge, for your Minecraft version.
2. Put the Codeling file for that loader and version in the `mods` folder.
3. Start the game, open a world, and type `/robot spawn` and then `/codeling` in the chat.

**Good to know**

- Minecraft Java Edition 1.21.1 to 1.21.11 and 26.1 to 26.3. The file name says which versions each file is for.
- On a server, the server and everybody who joins need Codeling, and all need the same version of it.
- Only you can program your robot. It follows your own rules on the server, such as protected areas.
- The game commands and messages are in English. The editor's own blocks are also in Norwegian.

[Player guide](https://github.com/halisonworks/mod-codeling/blob/develop/docs/player/guide-en.md) ·
[Report a problem](https://github.com/halisonworks/mod-codeling/issues/new/choose) ·
[Report a security problem](https://github.com/halisonworks/mod-codeling/blob/develop/SECURITY.md)

Free and open source, under the MIT licence. The editor is built with pxt, Microsoft's open-source toolkit, under the
same licence.

## Norsk

**IKKE ET OFFISIELT MINECRAFT-PRODUKT. IKKE GODKJENT AV ELLER TILKNYTTET MOJANG ELLER MICROSOFT.**

> Dette er en forhåndsutgivelse, til testing. Ta en sikkerhetskopi av verdener du er glad i.

Codeling setter en liten robot inn i verdenen din. Du programmerer den i nettleseren, med blokker du drar sammen,
eller med JavaScript eller Python. Trykk på Kjør, så gjør roboten det i spillet.

**Dette kan roboten**

- Gå, fly, snu seg og finne veien til et sted selv.
- Ødelegge og plassere blokker, og bygge hele former.
- Se seg rundt: blokker, dyr og monstre, lys og avstand.
- Bære ting, bruke kister og ovner, og lage ting.
- Reagere når du skriver i chatten, når du ødelegger en blokk, eller når redstone endrer seg.
- Samarbeide med de andre robotene dine, når verdenen tillater mer enn én.

**Her kjører koden din**

Editoren følger med modden og åpnes i nettleseren på din egen maskin. Den trenger ingen konto og virker uten
internett. Codeling samler ikke inn noe og sender ikke noe på egen hånd. Programmet ditt kjører i nettleseren. På en
server sendes bare kommandoene til roboten, og serveren sjekker hver av dem.

**Installer**

1. Installer Fabric Loader og Fabric API, eller NeoForge, for din versjon av Minecraft.
2. Legg Codeling-filen for den loaderen og versjonen i mappen `mods`.
3. Start spillet, åpne en verden, og skriv `/robot spawn` og så `/codeling` i chatten.

**Greit å vite**

- Minecraft Java Edition 1.21.1 til 1.21.11 og 26.1 til 26.3. Filnavnet sier hvilke versjoner hver fil er for.
- På en server må både serveren og alle som blir med, ha Codeling, og alle må ha samme versjon.
- Bare du kan programmere roboten din. Den følger dine egne regler på serveren, for eksempel vernede områder.
- Kommandoer og meldinger i spillet er på engelsk. Blokkene i editoren finnes også på norsk.

[Spillerguide](https://github.com/halisonworks/mod-codeling/blob/develop/docs/player/guide-nb.md) ·
[Meld om et problem](https://github.com/halisonworks/mod-codeling/issues/new/choose) ·
[Meld om et sikkerhetsproblem](https://github.com/halisonworks/mod-codeling/blob/develop/SECURITY.md)

Gratis og åpen kildekode, med MIT-lisens. Editoren er bygget med pxt, Microsofts åpne verktøy, med samme lisens.

## Rules of the sites

Read on 2026-11-24. Read them again before publishing.

| Site | Rule | What it means here |
|---|---|---|
| [Modrinth](https://modrinth.com/legal/rules), 6 and 6.2 | "Projects cannot be entirely or primarily comprised of content created or derived from generative AI output." "Projects may not be published publicly if the contents are primarily or entirely a product of AI output." | **Open.** The owner decides whether and how this applies, and asks Modrinth before #62. |
| Modrinth, 6.1 | The "Contains AI-generated content" disclosure, when a substantial portion of the code, or any element of the page, relies on AI. | This text alone needs the disclosure. |
| Modrinth, 6.2 | No images on the page that are created or derived from AI output. | Icon and pictures (#198) must not be. |
| Modrinth, 2 and 5 | Say what it does, English text, title without extra words, summary without formatting or the title. | Followed above. |
| [CurseForge](https://support.curseforge.com/en/support/solutions/articles/9000197279-moderation-policies) | Names in English without the game's name or versions. English before other languages. Avatar 400 x 400, no webp. A changelog per file. No external download links. | Followed above, and there are no links to files. Avatar from #198. |
| [Minecraft](https://www.minecraft.net/en-us/usage-guidelines) | A disclaimer in a prominent place: "NOT AN OFFICIAL MINECRAFT [PRODUCT/SERVICE/EVENT/etc.]. NOT APPROVED BY OR ASSOCIATED WITH MOJANG OR MICROSOFT". Our name may not have theirs as the main part, and nothing of their brand in the logo. | Followed above. |

## Before it is published

- [ ] The Modrinth question on AI is answered.
- [ ] Trademark search (#58), icon and pictures (#198), the Norwegian pxt texts (#59).
- [ ] The claims above are tried in the test session (#55), the two about servers first.
- [ ] An account the owner controls, with one more person who can edit, and no personal email on the page.
- [ ] The owner approves the final text and the pictures.
- [ ] README and both guides link to the pages.
