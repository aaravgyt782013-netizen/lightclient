# Licensing and third-party notices

Ar Client branding, UI artwork and original feature code should be created independently.

## Upstream Android launcher

Before integrating or redistributing PojavLauncher or ZalithLauncher, inspect the exact upstream revision's license and bundled dependency notices. If the selected code is licensed under LGPL-3.0, comply with LGPL-3.0: preserve copyright and license notices, provide the license text, disclose modifications, make the corresponding covered source available under the applicable terms, and do not impose restrictions that prevent users from exercising LGPL rights. Follow the license's requirements for relinking/replacement of the covered library and provide installation information where required for a User Product.

Do not assume every file or dependency in an upstream repository has the same license. Audit submodules, assets, native libraries and dependencies individually. This note is guidance, not a substitute for checking the exact license files.

## Minecraft and Microsoft

Do not bundle Minecraft assets, game JARs, launcher credentials or user tokens. Users should authenticate through the upstream launcher's supported Microsoft flow and have a valid entitlement. Do not use Mojang/Microsoft marks in a way that implies official endorsement.

## Fabric mod

Keep Fabric Loader/API notices and comply with their individual licenses. Include a license for original Ar Client code before public distribution; do not label the entire repository LGPL unless that is actually the chosen license for every applicable component.
