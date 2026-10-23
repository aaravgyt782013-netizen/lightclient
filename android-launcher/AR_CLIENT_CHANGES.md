# Ar Client changes in the Android launcher

This file tracks **original Ar Client integration code**, not changes to PojavLauncher itself.

## Change policy

1. Prefer new files with an `ArClient` prefix/package over edits to upstream files.
2. For every necessary upstream modification in the `lightclient-pojav` fork, add an inline `AR CLIENT CHANGE:` comment and record the path, reason, and upstream base revision below.
3. Preserve all existing copyright headers, LICENSE files, NOTICE files, and third-party attribution.
4. Keep the fork's LGPL-3.0 compliance materials and corresponding source available. Review each dependency's own license; do not assume every dependency uses LGPL.
5. Do not commit Minecraft game assets/JARs, Microsoft credentials, or session tokens.

## Integration ledger

No Ar Client-specific runtime source patches have been recorded yet. The current upstream submodule is still pointed at the original PojavLauncher repository, so do not treat it as the writable Ar Client fork.

| Path / component | Change | Status |
| --- | --- | --- |
| `android-launcher/upstream-pojav` | PojavLauncher source dependency | Temporary upstream submodule; switch to `aaravgyt782013-netizen/lightclient-pojav` after the fork exists |
| `client-mod/` | Separate Fabric mod project | Kept separate from launcher runtime |
