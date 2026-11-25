# Third-party notices

Halison Code itself is MIT-licensed, see [LICENSE](LICENSE).

## Trademarks and affiliation

Halison Code is not affiliated with, endorsed by or sponsored by Microsoft, Mojang or the Microsoft MakeCode team.
Minecraft is a trademark of Mojang Synergies AB, a Microsoft company.
Microsoft MakeCode is a Microsoft product. Halison Code uses Microsoft's open-source pxt toolkit, which Microsoft publishes under the MIT licence.

## pxt (Microsoft MakeCode toolkit)

The browser editor is built with pxt-core (version 13.3.1, https://github.com/microsoft/pxt) and is bundled in the mod jar under `codeling-editor/`.

```
The MIT License (MIT)

Copyright (c) Microsoft Corporation

All rights reserved.

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```

## pxt-sample

The theme files in `target/theme` started from microsoft/pxt-sample (MIT, Copyright (c) Microsoft Corporation).
Full text: [target/LICENSE-pxt-sample.txt](target/LICENSE-pxt-sample.txt).

## Libraries inside the packaged editor

The static editor in the jar (`codeling-editor/`, built from `target/built/packaged`) contains code from the libraries
below, each under its own licence. The list was made on 2026-11-11 for pxt-core 13.3.1 from:

- the licence headers in the files under `target/built/packaged` (the files `gradle/loader.gradle.kts` excludes from
  the jar are left out),
- the browserify module names that `main.js` and `pxtasseteditor.js` require,
- the `package.json` and licence files of those modules, installed from npm as described below.

pxt-core ships `main.js` and the other `pxt*.js` files prebuilt, without a lock file, so the copies inside them cannot
be read from `target/node_modules`. Checked on 2026-11-23 (issue #199): the versions pxt-core 13.3.1 asks for in its
`package.json` (React, PouchDB, dash.js and so on) were installed as npm resolved them on the day pxt-core 13.3.1 was
published (2026-11-05), browserify listed the modules those packages pull in, and each name was looked up in the
bundles. The bundled copies were not compared byte for byte. Where a package asks for a version range, the version is
the one npm gave on that day. The copyright lines are from the licence files in the installed packages unless a row
says otherwise.

### Files copied as they are

| Path in `codeling-editor/` | Component | Version | Licence | Copyright |
|---|---|---|---|---|
| `blockly/`, `blockly.css`, Blockly code in `main.js` | Blockly | 13.1.1 | Apache-2.0 | Copyright Google LLC |
| `vs/` | Monaco editor | 0.24.0 | MIT | Copyright (c) 2016 - present Microsoft Corporation |
| `typescript.js` | TypeScript | 2.6.1 (`ts.version` in the file) | Apache-2.0 | Copyright (c) Microsoft Corporation |
| `semantic.css`, `rtlsemantic.css`, `semantic.js` | Semantic UI | 2.4.1 (pxt-core pins semantic-ui-less 2.4.1; the files carry no version) | MIT | Semantic Org (the upstream LICENSE.md at 2.4.1 has no copyright line) |
| `semantic.css`, `fonts/`, `icons.css` | Font Awesome Free | 5.15.4 | Icons CC-BY-4.0, fonts OFL-1.1, code MIT | Fonticons, Inc. (https://fontawesome.com) |
| `semantic.css` | normalize.css | 7.0.0 | MIT | Copyright © Nicolas Gallagher and Jonathan Neal |
| `jquery.js` | jQuery | 3.5.0 | MIT | Copyright JS Foundation and other contributors |
| `highlight.js/` | highlight.js | 11.11.1 | BSD-3-Clause | Copyright (c) 2006-2025 Josh Goebel and other contributors |
| `marked/` (licence file included) | marked | 17.0.6 | MIT | Copyright (c) 2018+, MarkedJS; Copyright (c) 2011-2018, Christopher Jeffrey |
| `qrcode/` (licence file included) | QR Code Generator | 1.4.3 (same code as `qrcode.js` in npm qrcode-generator 1.4.3) | MIT | Copyright (c) 2009 Kazuhiko Arase |
| `smoothie/` | Smoothie Charts | 1.36.1 (pinned by pxt-core; the file carries no version) | MIT | Copyright (c) 2010-2013, Joe Walnes; 2013-2018, Drew Noakes |
| `gifjs/` | gif.js | 0.2.0 | MIT | Copyright (c) 2013-2018 Johan Nordberg |
| `pdf-lib/` | pdf-lib | 1.17.1 (from pxt's ThirdPartyNotice at v13.3.1; the file is not identical to the npm build) | MIT | Copyright (c) 2019 Andrew Dillon |
| `zip.js/` | zip.js | 2.4.20 (pxt-core dependency) | BSD-3-Clause | Copyright (c) 2022 Gildas Lormeau |
| `pxtapp.js`, `pxtembed.js`, `pxtworker.js`, `pxtrcdeps.js` | DOMPurify | 3.4.17 | Apache-2.0 or MPL-2.0 (Halison Code uses it under Apache-2.0) | Copyright Cure53 and other contributors |
| `pxtapp.js`, `pxtembed.js`, `pxtworker.js` | LZMA-JS | 2.3.2 (pxt-core dependency) | MIT | Copyright (c) 2016 Nathan Rugg |
| `pxtapp.js`, `pxtembed.js`, `pxtworker.js`, `pxtrcdeps.js` | regenerator-runtime, as a Babel helper | cannot be confirmed (the helper carries no version) | MIT | Copyright (c) 2014-present, Facebook, Inc. (file header) |
| `main.js`, `pxtworker.js` | Fuse.js | 3.2.0 | Apache-2.0 | Copyright (c) 2012-2017 Kirollos Risk |
| `docs/`, `docfiles/`, `images/`, `music-editor/`, `sounds/`, the `pxt*.js` files, `main.js` | pxt-core | 13.3.1 | MIT | Copyright (c) Microsoft Corporation (full text above) |

### Modules bundled into `main.js` and `pxtasseteditor.js`

Where several versions are listed, browserify bundles a copy of each.

| Package | Version | Licence | Copyright |
|---|---|---|---|
| @babel/runtime | 7.29.13 | MIT | Copyright (c) 2014-present Sebastian McKenzie and other contributors |
| @blockly/field-grid-dropdown | 13.1.0 | Apache-2.0 | Copyright Google LLC (file headers in main.js) |
| @blockly/plugin-workspace-search | 13.1.0 | Apache-2.0 | Copyright Google LLC (file headers in main.js) |
| @microsoft/immersive-reader-sdk | 1.1.0 | MIT | Copyright (c) Microsoft Corporation |
| abstract-leveldown | 2.7.2 and 6.2.3 | MIT | Copyright (c) 2013-2017 Rod Vagg; Copyright © 2013-present Rod Vagg and Contributors. |
| argsarray | 0.0.1 | WTFPL | Calvin Metcalf (package author; the licence file has no copyright line) |
| assert | 1.5.1 | MIT | Copyright Joyent, Inc. and other Node contributors. |
| available-typed-arrays | 1.0.7 | MIT | Copyright (c) 2020 Inspect JS |
| base64-js | 1.5.1 | MIT | Copyright (c) 2014 Jameson Little |
| blockly | 13.1.1 | Apache-2.0 | Copyright Google LLC (file headers in main.js) |
| buffer | 5.2.1 | MIT | Copyright (c) Feross Aboukhadijeh, and other contributors. |
| buffer-from | 1.1.2 | MIT | Copyright (c) 2016, 2018 Linus Unnebäck |
| call-bind | 1.0.9 | MIT | Copyright (c) 2020 Jordan Harband |
| call-bind-apply-helpers | 1.0.2 | MIT | Copyright (c) 2024 Jordan Harband |
| call-bound | 1.0.4 | MIT | Copyright (c) 2024 Jordan Harband |
| classnames | 2.5.1 | MIT | Copyright (c) 2018 Jed Watson |
| core-util-is | 1.0.3 | MIT | Copyright Node.js contributors. |
| crypto-js | 4.2.0 | MIT | Copyright (c) 2009-2013 Jeff Mott; Copyright (c) 2013-2016 Evan Vosberg |
| dashjs | 4.7.4 | BSD-3-Clause | Copyright (c) 2015, Dash Industry Forum |
| deferred-leveldown | 5.3.0 | MIT | Copyright © 2013-present Rod Vagg and Contributors. |
| define-data-property | 1.1.4 | MIT | Copyright (c) 2023 Jordan Harband |
| dom-serializer | 0.2.2 | MIT | Copyright (c) 2014 The cheeriojs contributors |
| domelementtype | 1.3.1 and 2.3.0 | BSD-2-Clause | Copyright (c) Felix Böhm |
| domhandler | 2.4.2 | BSD-2-Clause | Copyright (c) Felix Böhm |
| domutils | 1.7.0 | BSD-2-Clause | Copyright (c) Felix Böhm |
| double-ended-queue | 2.1.0-0 | MIT | Copyright (c) 2013 Petka Antonov |
| dunder-proto | 1.0.1 | MIT | Copyright (c) 2024 ECMAScript Shims |
| entities | 1.1.2 and 2.2.0 | BSD-2-Clause | Copyright (c) Felix Böhm |
| errno | 0.1.8 | MIT | Copyright (c) 2012-2015 Rod Vagg (README.md; no LICENSE file in the package) |
| es-define-property | 1.0.1 | MIT | Copyright (c) 2024 Jordan Harband |
| es-errors | 1.3.0 | MIT | Copyright (c) 2024 Jordan Harband |
| es-object-atoms | 1.1.2 | MIT | Copyright (c) 2024 Jordan Harband |
| events | 3.3.0 | MIT | Copyright Joyent, Inc. and other Node contributors. |
| exenv | 1.2.2 | BSD-3-Clause | Copyright (c) 2013-2015, Facebook, Inc. |
| for-each | 0.3.5 | MIT | Copyright (c) 2012 Raynos. |
| function-bind | 1.1.2 | MIT | Copyright (c) 2013 Raynos. |
| functional-red-black-tree | 1.0.1 | MIT | Copyright (c) 2013 Mikola Lysenko |
| generator-function | 2.0.1 | MIT | Copyright (c) 2015 Tiancheng “Timothy” Gu |
| get-intrinsic | 1.3.0 | MIT | Copyright (c) 2020 Jordan Harband |
| get-proto | 1.0.1 | MIT | Copyright (c) 2025 Jordan Harband |
| gopd | 1.2.0 | MIT | Copyright (c) 2022 Jordan Harband |
| has-property-descriptors | 1.0.2 | MIT | Copyright (c) 2022 Inspect JS |
| has-symbols | 1.1.0 | MIT | Copyright (c) 2016 Jordan Harband |
| has-tostringtag | 1.0.2 | MIT | Copyright (c) 2021 Inspect JS |
| hasown | 2.0.4 | MIT | Copyright (c) Jordan Harband and contributors |
| hoist-non-react-statics | 3.3.2 | BSD-3-Clause | Copyright (c) 2015, Yahoo! Inc. |
| htmlparser2 | 3.10.1 | MIT | Copyright 2010, 2011, Chris Winberry <chris@winberry.net>. |
| ieee754 | 1.2.1 | BSD-3-Clause | Copyright 2008 Fair Oaks Labs, Inc. |
| immediate | 3.3.0 | MIT | Copyright (c) 2012 Barnesandnoble.com, llc, Donavon West, Domenic Denicola, Brian Cavalier |
| inherits | 2.0.3 and 2.0.4 | ISC | Copyright (c) Isaac Z. Schlueter |
| is-arguments | 1.2.0 | MIT | Copyright (c) 2014 Jordan Harband |
| is-buffer | 1.1.6 | MIT | Copyright (c) Feross Aboukhadijeh |
| is-callable | 1.2.7 | MIT | Copyright (c) 2015 Jordan Harband |
| is-generator-function | 1.1.2 | MIT | Copyright (c) 2014 Jordan Harband |
| is-regex | 1.2.1 | MIT | Copyright (c) 2014 Jordan Harband |
| is-typed-array | 1.1.15 | MIT | Copyright (c) 2015 Jordan Harband |
| isarray | 0.0.1 and 1.0.0 | MIT | Julian Gruber (package author; no LICENSE file in the package) |
| level-codec | 9.0.2 | MIT | Copyright © 2012-present Contributors. |
| level-errors | 2.0.1 | MIT | Copyright © 2012-present Contributors. |
| level-iterator-stream | 4.0.2 | MIT | Copyright © 2012-present Contributors. |
| level-supports | 1.0.1 | MIT | Copyright © 2019-present Contributors. |
| levelup | 4.4.0 | MIT | Copyright © 2012-present Contributors. |
| ltgt | 2.2.1 | MIT | Copyright (c) 2014 Dominic Tarr |
| marked | 17.0.6 | MIT | Copyright (c) 2018+, MarkedJS (https://github.com/markedjs/) |
| math-intrinsics | 1.1.0 | MIT | Copyright (c) 2024 ECMAScript Shims |
| memdown | 1.4.1 | MIT | Copyright 2013, Rod Vagg (the "Original Author") |
| object-assign | 4.1.1 | MIT | Copyright (c) Sindre Sorhus <sindresorhus@gmail.com> (sindresorhus.com) |
| object-keys | 1.1.1 | MIT | Copyright (C) 2013 Jordan Harband |
| object.assign | 4.1.7 | MIT | Copyright (c) 2014 Jordan Harband |
| possible-typed-array-names | 1.1.0 | MIT | Copyright (c) 2024 Jordan Harband |
| pouchdb | 7.3.1 | Apache-2.0 | no copyright line in the package |
| pouchdb-adapter-leveldb-core | 7.3.1 | Apache-2.0 | no copyright line in the package |
| pouchdb-adapter-memory | 7.3.1 | Apache-2.0 | no copyright line in the package |
| pouchdb-adapter-utils | 7.3.1 | Apache-2.0 | no copyright line in the package |
| pouchdb-binary-utils | 7.3.1 | Apache-2.0 | no copyright line in the package |
| pouchdb-collections | 7.3.1 | Apache-2.0 | no copyright line in the package |
| pouchdb-errors | 7.3.1 | Apache-2.0 | no copyright line in the package |
| pouchdb-json | 7.3.1 | Apache-2.0 | no copyright line in the package |
| pouchdb-md5 | 7.3.1 | Apache-2.0 | no copyright line in the package |
| pouchdb-merge | 7.3.1 | Apache-2.0 | no copyright line in the package |
| pouchdb-utils | 7.3.1 | Apache-2.0 | no copyright line in the package |
| process | 0.11.10 | MIT | Copyright (c) 2013 Roman Shtylman <shtylman@gmail.com> |
| process-nextick-args | 2.0.1 | MIT | Copyright (c) 2015 Calvin Metcalf |
| prop-types | 15.8.1 | MIT | Copyright (c) 2013-present, Facebook, Inc. |
| prr | 1.0.1 | MIT | Copyright (c) 2014 Rod Vagg |
| react | 17.0.2 | MIT | Copyright (c) Facebook, Inc. and its affiliates. |
| react-dom | 17.0.2 | MIT | Copyright (c) Facebook, Inc. and its affiliates. |
| react-is | 16.13.1 and 17.0.2 | MIT | Copyright (c) Facebook, Inc. and its affiliates. |
| react-lifecycles-compat | 3.0.4 | MIT | Copyright (c) 2013-present, Facebook, Inc. |
| react-modal | 3.15.1 | MIT | Copyright (c) 2017 Ryan Florence |
| react-redux | 7.2.9 | MIT | Copyright (c) 2015-present Dan Abramov |
| react-tooltip | 3.9.0 | MIT | Copyright (c) 2015 Wang Zixiao |
| readable-stream | 1.1.14, 2.3.8 and 3.6.2 | MIT | Copyright Joyent, Inc. and other Node contributors.; Copyright Node.js contributors. |
| redux | 4.2.0 | MIT | Copyright (c) 2015-present Dan Abramov |
| regexp-quote | 0.0.0 | MIT | Copyright (c) 2012-2014 Daniel Brockman (MIT-LICENSE in github.com/dbrock/node-regexp-quote; npm lists no licence) |
| safe-buffer | 5.1.2 and 5.2.1 | MIT | Copyright (c) Feross Aboukhadijeh |
| safe-regex-test | 1.1.0 | MIT | Copyright (c) 2022 Jordan Harband |
| sanitize-html-react | 1.13.0 | MIT | Copyright (c) 2013, 2014, 2015 P'unk Avenue LLC |
| scheduler | 0.20.2 | MIT | Copyright (c) Facebook, Inc. and its affiliates. |
| set-function-length | 1.2.2 | MIT | Copyright (c) Jordan Harband and contributors |
| spark-md5 | 3.0.2 | WTFPL or MIT (Halison Code uses it under MIT) | Copyright (C) 2015 André Cruz <amdfcruz@gmail.com> |
| stream-browserify (`stream`) | 3.0.0 | MIT | Copyright (c) James Halliday |
| string_decoder | 0.10.31, 1.1.1 and 1.3.0 | MIT | Copyright Joyent, Inc. and other Node contributors.; Copyright Node.js contributors. |
| sublevel-pouchdb | 7.3.1 | Apache-2.0 | no copyright line in the package |
| through2 | 3.0.2 | MIT | Copyright (c) Rod Vagg (the "Original Author") and additional contributors |
| timers-browserify (`timers`) | 1.4.2 | MIT | Copyright © 2012 J. Ryan Stinnett <jryans@gmail.com>; Copyright Joyent, Inc. and other Node contributors. |
| util | 0.10.4 and 0.12.5 | MIT | Copyright Joyent, Inc. and other Node contributors. |
| util-deprecate | 1.0.2 | MIT | Copyright (c) 2014 Nathan Rajlich <nathan@tootallnate.net> |
| uuid | 8.3.2 | MIT | Copyright (c) 2010-2020 Robert Kieffer and other contributors |
| vuvuzela | 1.0.3 | Apache-2.0 | no copyright line in the package |
| warning | 4.0.3 | MIT | Copyright (c) 2013-present, Facebook, Inc. |
| which-typed-array | 1.1.24 | MIT | Copyright (c) 2015 Jordan Harband |
| xtend | 4.0.2 | MIT | Copyright (c) 2012-2014 Raynos. |

### Notes

- Blockly ships no NOTICE file: neither the root of github.com/RaspberryPiFoundation/blockly nor `packages/blockly`
  has one (checked 2026-11-11), and the npm package has no LICENSE or NOTICE file. Its licence is the Apache License
  2.0 below. The other Apache-2.0 packages in `target/node_modules` (the `@blockly` plugins, DOMPurify) ship no NOTICE
  file either. The PouchDB packages and vuvuzela ship none either.
- TypeScript's `ThirdPartyNoticeText.txt` is not shipped. The one in TypeScript 2.6.1 lists only DefinitelyTyped, whose
  material is in the `lib.d.ts` files. The packaged editor has `typescript.js` and no `lib*.d.ts` file.
- Font Awesome's attribution stays in the header of `semantic.css` and `rtlsemantic.css`.
- The Norwegian pxt UI strings (`target/locales/nb/strings.json`) are this project's own (issue #59). Releases 0.0.1 to
  0.0.4 have a snapshot from makecode.com instead, whose licence for reuse was not found.

### Licence texts

The MIT, ISC, BSD-2-Clause and BSD-3-Clause components use the standard texts below with the copyright lines in the
tables above.

MIT:

```
Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```

ISC:

```
Permission to use, copy, modify, and/or distribute this software for any
purpose with or without fee is hereby granted, provided that the above
copyright notice and this permission notice appear in all copies.

THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
```

BSD-2-Clause and BSD-3-Clause (condition 3 is in BSD-3-Clause only):

```
Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

1. Redistributions of source code must retain the above copyright notice,
   this list of conditions and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright notice,
   this list of conditions and the following disclaimer in the documentation
   and/or other materials provided with the distribution.

3. Neither the name of the copyright holder nor the names of its contributors
   may be used to endorse or promote products derived from this software
   without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
POSSIBILITY OF SUCH DAMAGE.
```

WTFPL (argsarray), MPL-2.0 (DOMPurify's other option), CC-BY-4.0 and OFL-1.1 (Font Awesome icons and fonts): see
https://www.wtfpl.net/txt/copying/, https://www.mozilla.org/MPL/2.0/,
https://creativecommons.org/licenses/by/4.0/legalcode and https://openfontlicense.org/.

Apache License 2.0 (Blockly, its plugins, TypeScript, DOMPurify, Fuse.js, PouchDB, vuvuzela):

```
                                 Apache License
                           Version 2.0, January 2004
                        http://www.apache.org/licenses/

   TERMS AND CONDITIONS FOR USE, REPRODUCTION, AND DISTRIBUTION

   1. Definitions.

      "License" shall mean the terms and conditions for use, reproduction,
      and distribution as defined by Sections 1 through 9 of this document.

      "Licensor" shall mean the copyright owner or entity authorized by
      the copyright owner that is granting the License.

      "Legal Entity" shall mean the union of the acting entity and all
      other entities that control, are controlled by, or are under common
      control with that entity. For the purposes of this definition,
      "control" means (i) the power, direct or indirect, to cause the
      direction or management of such entity, whether by contract or
      otherwise, or (ii) ownership of fifty percent (50%) or more of the
      outstanding shares, or (iii) beneficial ownership of such entity.

      "You" (or "Your") shall mean an individual or Legal Entity
      exercising permissions granted by this License.

      "Source" form shall mean the preferred form for making modifications,
      including but not limited to software source code, documentation
      source, and configuration files.

      "Object" form shall mean any form resulting from mechanical
      transformation or translation of a Source form, including but
      not limited to compiled object code, generated documentation,
      and conversions to other media types.

      "Work" shall mean the work of authorship, whether in Source or
      Object form, made available under the License, as indicated by a
      copyright notice that is included in or attached to the work
      (an example is provided in the Appendix below).

      "Derivative Works" shall mean any work, whether in Source or Object
      form, that is based on (or derived from) the Work and for which the
      editorial revisions, annotations, elaborations, or other modifications
      represent, as a whole, an original work of authorship. For the purposes
      of this License, Derivative Works shall not include works that remain
      separable from, or merely link (or bind by name) to the interfaces of,
      the Work and Derivative Works thereof.

      "Contribution" shall mean any work of authorship, including
      the original version of the Work and any modifications or additions
      to that Work or Derivative Works thereof, that is intentionally
      submitted to Licensor for inclusion in the Work by the copyright owner
      or by an individual or Legal Entity authorized to submit on behalf of
      the copyright owner. For the purposes of this definition, "submitted"
      means any form of electronic, verbal, or written communication sent
      to the Licensor or its representatives, including but not limited to
      communication on electronic mailing lists, source code control systems,
      and issue tracking systems that are managed by, or on behalf of, the
      Licensor for the purpose of discussing and improving the Work, but
      excluding communication that is conspicuously marked or otherwise
      designated in writing by the copyright owner as "Not a Contribution."

      "Contributor" shall mean Licensor and any individual or Legal Entity
      on behalf of whom a Contribution has been received by Licensor and
      subsequently incorporated within the Work.

   2. Grant of Copyright License. Subject to the terms and conditions of
      this License, each Contributor hereby grants to You a perpetual,
      worldwide, non-exclusive, no-charge, royalty-free, irrevocable
      copyright license to reproduce, prepare Derivative Works of,
      publicly display, publicly perform, sublicense, and distribute the
      Work and such Derivative Works in Source or Object form.

   3. Grant of Patent License. Subject to the terms and conditions of
      this License, each Contributor hereby grants to You a perpetual,
      worldwide, non-exclusive, no-charge, royalty-free, irrevocable
      (except as stated in this section) patent license to make, have made,
      use, offer to sell, sell, import, and otherwise transfer the Work,
      where such license applies only to those patent claims licensable
      by such Contributor that are necessarily infringed by their
      Contribution(s) alone or by combination of their Contribution(s)
      with the Work to which such Contribution(s) was submitted. If You
      institute patent litigation against any entity (including a
      cross-claim or counterclaim in a lawsuit) alleging that the Work
      or a Contribution incorporated within the Work constitutes direct
      or contributory patent infringement, then any patent licenses
      granted to You under this License for that Work shall terminate
      as of the date such litigation is filed.

   4. Redistribution. You may reproduce and distribute copies of the
      Work or Derivative Works thereof in any medium, with or without
      modifications, and in Source or Object form, provided that You
      meet the following conditions:

      (a) You must give any other recipients of the Work or
          Derivative Works a copy of this License; and

      (b) You must cause any modified files to carry prominent notices
          stating that You changed the files; and

      (c) You must retain, in the Source form of any Derivative Works
          that You distribute, all copyright, patent, trademark, and
          attribution notices from the Source form of the Work,
          excluding those notices that do not pertain to any part of
          the Derivative Works; and

      (d) If the Work includes a "NOTICE" text file as part of its
          distribution, then any Derivative Works that You distribute must
          include a readable copy of the attribution notices contained
          within such NOTICE file, excluding those notices that do not
          pertain to any part of the Derivative Works, in at least one
          of the following places: within a NOTICE text file distributed
          as part of the Derivative Works; within the Source form or
          documentation, if provided along with the Derivative Works; or,
          within a display generated by the Derivative Works, if and
          wherever such third-party notices normally appear. The contents
          of the NOTICE file are for informational purposes only and
          do not modify the License. You may add Your own attribution
          notices within Derivative Works that You distribute, alongside
          or as an addendum to the NOTICE text from the Work, provided
          that such additional attribution notices cannot be construed
          as modifying the License.

      You may add Your own copyright statement to Your modifications and
      may provide additional or different license terms and conditions
      for use, reproduction, or distribution of Your modifications, or
      for any such Derivative Works as a whole, provided Your use,
      reproduction, and distribution of the Work otherwise complies with
      the conditions stated in this License.

   5. Submission of Contributions. Unless You explicitly state otherwise,
      any Contribution intentionally submitted for inclusion in the Work
      by You to the Licensor shall be under the terms and conditions of
      this License, without any additional terms or conditions.
      Notwithstanding the above, nothing herein shall supersede or modify
      the terms of any separate license agreement you may have executed
      with Licensor regarding such Contributions.

   6. Trademarks. This License does not grant permission to use the trade
      names, trademarks, service marks, or product names of the Licensor,
      except as required for reasonable and customary use in describing the
      origin of the Work and reproducing the content of the NOTICE file.

   7. Disclaimer of Warranty. Unless required by applicable law or
      agreed to in writing, Licensor provides the Work (and each
      Contributor provides its Contributions) on an "AS IS" BASIS,
      WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
      implied, including, without limitation, any warranties or conditions
      of TITLE, NON-INFRINGEMENT, MERCHANTABILITY, or FITNESS FOR A
      PARTICULAR PURPOSE. You are solely responsible for determining the
      appropriateness of using or redistributing the Work and assume any
      risks associated with Your exercise of permissions under this License.

   8. Limitation of Liability. In no event and under no legal theory,
      whether in tort (including negligence), contract, or otherwise,
      unless required by applicable law (such as deliberate and grossly
      negligent acts) or agreed to in writing, shall any Contributor be
      liable to You for damages, including any direct, indirect, special,
      incidental, or consequential damages of any character arising as a
      result of this License or out of the use or inability to use the
      Work (including but not limited to damages for loss of goodwill,
      work stoppage, computer failure or malfunction, or any and all
      other commercial damages or losses), even if such Contributor
      has been advised of the possibility of such damages.

   9. Accepting Warranty or Additional Liability. While redistributing
      the Work or Derivative Works thereof, You may choose to offer,
      and charge a fee for, acceptance of support, warranty, indemnity,
      or other liability obligations and/or rights consistent with this
      License. However, in accepting such obligations, You may act only
      on Your own behalf and on Your sole responsibility, not on behalf
      of any other Contributor, and only if You agree to indemnify,
      defend, and hold each Contributor harmless for any liability
      incurred by, or claims asserted against, such Contributor by reason
      of your accepting any such warranty or additional liability.

   END OF TERMS AND CONDITIONS

   APPENDIX: How to apply the Apache License to your work.

      To apply the Apache License to your work, attach the following
      boilerplate notice, with the fields enclosed by brackets "[]"
      replaced with your own identifying information. (Don't include
      the brackets!)  The text should be enclosed in the appropriate
      comment syntax for the file format. We also recommend that a
      file or class name and description of purpose be included on the
      same "printed page" as the copyright notice for easier
      identification within third-party archives.

   Copyright [yyyy] [name of copyright owner]

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
```

## Fabric

Fabric Loader and Fabric API are not bundled; players install them separately. Both are Apache-2.0.

## NeoForge

NeoForge is not bundled; players install it separately. It is LGPL-2.1, and Halison Code only links against it.
