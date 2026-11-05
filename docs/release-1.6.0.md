# ArmorSwap 1.6.0 Release Notes

## Markdown (GitHub / BuiltByBit)

### ArmorSwap 1.6.0 - 1.8 to 1.21+ & Folia Support

**Changes:**
- Added support for Minecraft 1.8.8 through 1.21+ in a single jar.
- Added Folia support (`folia-supported: true`).
- Removed shaded ACF dependency and switched to native Bukkit commands (`~50 KB` jar size).
- Updated Item Frame shift right-click so clicking a frame holding armor with an empty hand swaps directly with your worn armor slot.
- Added checks for 1.16+ Armor Stand equipment locks and fixed Item Frames.
- Fixed off-hand double triggering on 1.9+.
- Fixed Curse of Binding check and added Curse of Binding check to `/armorswap head`.
- Fixed `/armorswap reload` not refreshing cached config values.
- Removed debug block message on right-click.

---

## SpigotMC BBCode

**Title:** `1.6.0 - 1.8 to 1.21+ & Folia Support`

```bbcode
[B][SIZE=5]ArmorSwap 1.6.0[/SIZE][/B]

[LIST]
[*]Added support for Minecraft 1.8.8 - 1.21+ in a single jar.
[*]Added Folia support.
[*]Removed ACF dependency and switched to native commands to keep the jar lightweight.
[*]Updated Item Frame shift right-click: clicking a frame holding armor with an empty hand now swaps directly with your equipped armor slot.
[*]Added support for 1.16+ Armor Stand equipment locks and fixed Item Frames.
[*]Fixed off-hand double swap issue on 1.9+.
[*]Fixed Curse of Binding check and added check to /armorswap head.
[*]Fixed /armorswap reload not applying updated config values right away.
[*]Removed leftover debug message when right-clicking blocks.
[/LIST]
```
