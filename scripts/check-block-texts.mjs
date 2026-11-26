// Every block label has a Norwegian text (issue #434). Run after the pxt target is built (target/node_modules and
// target/libs/core/_locales exist): node scripts/check-block-texts.mjs
//   1. pxt's own blocks (loops, logic, math, variables, ...): every pxt.Util.blf("...") label in pxt-core's editor has
//      an entry in target/locales/nb/strings.json.
//   2. Our blocks: every "|block" string pxt writes for target/libs/core has an entry in
//      target/locales/nb/halisoncode/core-strings.json.
// Prints the missing labels and exits 1 when there is one.
import { readFileSync } from "node:fs";
import { dirname, join } from "node:path";
import { fileURLToPath } from "node:url";

const root = join(dirname(fileURLToPath(import.meta.url)), "..");
const read = path => readFileSync(join(root, path), "utf8");

const missing = [];

const pxtapp = read("target/node_modules/pxt-core/built/web/pxtapp.js");
const pxtStrings = JSON.parse(read("target/locales/nb/strings.json"));
const call = /pxt\.Util\.blf\("((?:[^"\\]|\\.)*)"\)/g;
const pxtLabels = new Set([...pxtapp.matchAll(call)].map(m => JSON.parse(`"${m[1]}"`)));
for (const label of pxtLabels) if (!(label in pxtStrings)) missing.push(`target/locales/nb/strings.json: ${JSON.stringify(label)}`);

const ours = JSON.parse(read("target/libs/core/_locales/core-strings.json"));
const ourStrings = JSON.parse(read("target/locales/nb/halisoncode/core-strings.json"));
// Left out: item names (ItemEnumTest checks the ones that are also blocks), namespace names (the category name comes
// from {id:category}), statement aliases (blockAliasFor: pxt shows the aliased call's text) and labels that are only
// arguments, such as "%item".
const needsText = key => key.endsWith("|block") && !key.startsWith("Item.") && !/\._\w+Statement\|block$/.test(key)
    && !key.startsWith("_py|") && (key.includes(".") || /[%$]/.test(ours[key])) && /[A-Za-z]/.test(ours[key].replace(/[%$][\w=]+/g, ""));
const blockKeys = Object.keys(ours).filter(needsText);
for (const key of blockKeys) if (!(key in ourStrings)) missing.push(`target/locales/nb/halisoncode/core-strings.json: ${JSON.stringify(key)}`);

if (missing.length) {
    console.error(`check-block-texts: ${missing.length} block labels have no Norwegian text:\n  ${missing.join("\n  ")}`);
    process.exit(1);
}
console.log(`check-block-texts: ok (${pxtLabels.size} pxt labels, ${blockKeys.length} of our block labels)`);
