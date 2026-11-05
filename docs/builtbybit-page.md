<div align="center">

![ArmorSwap Banner](https://files.catbox.moe/7keezl.png)

# ArmorSwap

**Supports Minecraft 1.8 - 1.21+ (Spigot, Paper, Purpur, Folia)**

</div>

## About ArmorSwap

A lightweight plugin that lets players swap armor from their main hand, swap full armor sets with Armor Stands, and swap held or worn armor with Item Frames across Minecraft 1.8 to 1.21+.

## Features

- **Right-Click Armor & Elytra Swap**: Right-click while holding armor or an Elytra to swap it with what you are wearing. Clicking chests, doors, furnaces, and other interactable blocks opens them normally unless sneaking.
- **Armor Stand Swap (Shift + Right-Click)**: Sneak and right-click an Armor Stand to swap your full armor set (plus main-hand item if the stand has arms). Ignores invisible and locked stands.
- **Item Frame Swap (Shift + Right-Click)**: Sneak and right-click an Item Frame to swap your held item with the frame, or shift right-click an armor piece in a frame with an empty hand to swap it directly with the armor you are wearing.
- **Head Command (`/armorswap head`)**: Put the item in your main hand onto your helmet slot.
- **Per-Player Toggle (`/armorswap`)**: Players can toggle armor swapping on or off for themselves.
- **Protection & Curse of Binding Support**: Works with WorldGuard, GriefPrevention, Kingdoms, and respects Curse of Binding (with an optional bypass permission).
- **Multi-Version & Folia Support**: Runs on 1.8.8 through 1.21+ and Folia.

## Showcase

<div align="center">

### Right-Click Main-Hand Swap
![Main-Hand Swap](https://files.catbox.moe/nuerdu.gif)

### Armor Stand Swap (Shift + Right-Click)
![Armor Stand Swap](https://files.catbox.moe/d4gq8u.gif)

### Item Frame Swap (Shift + Right-Click)
![Item Frame Swap](https://files.catbox.moe/2g19zi.gif)

</div>

## Commands & Permissions

Aliases: `/armorswap`, `/armor`, `/as`

| Command | Description | Permission |
| :--- | :--- | :--- |
| `/armorswap` | Toggle armor swapping on or off | `armorswap.command.use` |
| `/armorswap head` | Swap the item in your main hand with your helmet slot | `armorswap.command.head` |
| `/armorswap reload` | Reload `config.yml` | `armorswap.command.reload` |
| `/armorswap help` | Show command help | None |

### Feature Permissions (configurable in `config.yml`)

| Permission | Description | Config Option |
| :--- | :--- | :--- |
| `armorswap.swap.mainhand` | Right-click main-hand armor & Elytra swapping | `Main_hand_swap: true` |
| `armorswap.swap.armorstand` | Shift + Right-Click Armor Stand swapping | `Armor_stand_swap: true` |
| `armorswap.swap.itemframe` | Shift + Right-Click Item Frame swapping | `Item_frame_swap: true` |
| `armorswap.bypass.enchantment` | Bypass Curse of Binding when swapping | `Bypass_enchantment: false` |

## Default `config.yml`

```yaml
# Enable/disable swapping item to armor stand
Armor_stand_swap: true
#Permission to use swapping items to armor stand
Armor_Stand_Swap_Permission: "armorswap.swap.armorstand"

# Enable/disable swapping item to armor stand
Main_hand_swap: true
#Permission to use swapping items to main hand
Main_Hand_Swap_Permission: "armorswap.swap.mainhand"

# Enable/Disable swapping item to item frames
Item_frame_swap: true
#Permission to use swapping items to item frames
Item_Frame_Swap_Permission: "armorswap.swap.itemframe"

#sound to play on swap
Sound: ITEM_ARMOR_EQUIP_TURTLE

#Bypass curse of binding
Bypass_enchantment: false

#Permission to bypass enchantment
Bypass_permission: "armorswap.bypass.enchantment"

#----------------------------------------------------------------------------------------------------------------
#
#				                 Language
#
#----------------------------------------------------------------------------------------------------------------
# prefix
Prefix: "&bArmorSwap &7-"
# when player runs /armorswap
Enable: "&bArmor swap is now enabled for you"
Disable: "&bArmor swap is now disabled for you"
# on wrong usage
usageError: "&bUse &7/armorswap&b to enable armor swap"

#DO NOT TOUCH
Version: 1.5.9
```
