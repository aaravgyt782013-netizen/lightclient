# Ar Client licensing and upstream notices

## Chosen runtime foundation

The initial runtime integration uses the archived upstream repository [PojavLauncherTeam/PojavLauncher](https://github.com/PojavLauncherTeam/PojavLauncher), pinned as a Git submodule at commit `b12ad048157b3aa255d078c235dd4571e1900309`. Its upstream `LICENSE` is the GNU Lesser General Public License, version 3 (LGPL-3.0). Upstream repository: https://github.com/PojavLauncherTeam/PojavLauncher/blob/v3_openjdk/LICENSE

PojavLauncher was archived on 23 September 2025. This is a deliberate LGPL-oriented starting point, not a claim that it is the most actively maintained launcher. Review and test a maintained successor before a public release.

## What must remain available under LGPL-3.0

For PojavLauncher code covered by LGPL-3.0, and modifications to that covered code:

1. Preserve copyright notices, existing license notices, and the complete LGPL-3.0 license text.
2. Clearly mark modified files and state the date/nature of changes in the manner required by the license.
3. Provide the complete corresponding source for the covered LGPL library/work and modifications under LGPL-3.0 when distributing binaries, or provide a compliant written offer where the license permits that route. Keep the exact source revision and build instructions available.
4. If distributing a combined application, comply with LGPL-3.0 section 4, including allowing users to modify the LGPL-covered component for their own use and reverse engineer for debugging those modifications. Use a suitable shared-library/dynamic-linking or relinking arrangement, or provide the Minimal Corresponding Source, Corresponding Application Code, and installation information required by the license's applicable static-linking/relinking route.
5. Do not impose additional terms that restrict recipients' LGPL rights. Keep the upstream source accessible to recipients and do not use DRM or legal terms to block replacement/modification of the covered component.
6. Retain and reproduce required notices for included dependencies, each under its own license. A dependency's license is not automatically the PojavLauncher license.

These are practical compliance notes, not legal advice. Whether a particular combined Android APK meets LGPL-3.0 section 4 depends on the final packaging/linking model and distribution method. Audit the exact pinned source and every bundled native library before release.

## What does not automatically have to be LGPL

Original Ar Client code that is a separate work and does not constitute a derivative work of LGPL-covered code is not automatically LGPL solely because it is distributed alongside PojavLauncher. Keep module boundaries clear, retain provenance, and obtain legal review if you plan to distribute proprietary components in the same app.

## ZalithLauncher alternative

The current ZalithLauncher2 repository is licensed GPL-3.0, not LGPL-3.0: https://github.com/ZalithLauncher/ZalithLauncher2/blob/main/LICENSE. Choosing that code as the basis of a single combined/derivative launcher can require the covered combined work to be distributed under GPL-3.0 and its corresponding-source obligations. Do not select it if your distribution plan depends on keeping derivative launcher code closed-source without obtaining separate permission.

## Minecraft and Microsoft

Do not bundle Minecraft game JARs/assets or Microsoft credentials. Users must authenticate using a legitimate account through the upstream-supported flow and have the required Minecraft: Java Edition entitlement. Ar Client is independent and must not imply endorsement by Mojang or Microsoft.

## Credits

Include visible credits and a third-party notices page in the app and distribution package. At minimum credit PojavLauncher and preserve its upstream notices. Also inventory and retain notices for Boardwalk, OpenJDK, LWJGL, GL4ES, Mesa, and every other dependency actually shipped; upstream README lists mixed licenses and some items whose license is uncertain. Do not guess the license of those items—verify their bundled license files.
