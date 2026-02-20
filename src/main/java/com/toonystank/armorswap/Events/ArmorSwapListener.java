package com.toonystank.armorswap.events;

import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import com.cryptomorin.xseries.XSound;
import com.toonystank.armorswap.ArmorSwap;
import com.toonystank.armorswap.compatibility.VersionUtils;

public class ArmorSwapListener implements Listener {

    private final ArmorSwap plugin;

    public ArmorSwapListener(ArmorSwap plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!VersionUtils.isMainHand(event))
            return;

        Action action = event.getAction();
        if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
            Player player = event.getPlayer();

            if (!plugin.getPlayerDataManager().isEnabled(player.getUniqueId()))
                return;
            if (!plugin.getMainConfig().isFeatureEnabled("Swap_Held_Armor"))
                return;

            ItemStack handItem = VersionUtils.getItemInHand(player);
            if (handItem == null || handItem.getType() == Material.AIR)
                return;

            String typeName = handItem.getType().name();
            // Shulker Box implementation using reflection for cross-version support
            if (plugin.getMainConfig().isFeatureEnabled("View_ShulkerBox") && typeName.contains("SHULKER_BOX")) {
                if (player.hasPermission("armorswap.view.shulker")) {
                    event.setCancelled(true);
                    openShulkerBox(player, handItem);
                    return;
                }
            }

            if (isArmor(handItem)) {
                if (!player.hasPermission("armorswap.swap.held"))
                    return;

                PlayerInventory inv = player.getInventory();
                ItemStack worn = null;
                boolean isHelmet = typeName.endsWith("HELMET") || typeName.endsWith("SKULL_ITEM")
                        || typeName.endsWith("HEAD") || typeName.equals("CARVED_PUMPKIN");
                boolean isChest = typeName.endsWith("CHESTPLATE") || typeName.equals("ELYTRA");
                boolean isLegs = typeName.endsWith("LEGGINGS");
                boolean isBoots = typeName.endsWith("BOOTS");

                if (isHelmet) {
                    worn = inv.getHelmet();
                    inv.setHelmet(handItem.clone());
                } else if (isChest) {
                    worn = inv.getChestplate();
                    inv.setChestplate(handItem.clone());
                } else if (isLegs) {
                    worn = inv.getLeggings();
                    inv.setLeggings(handItem.clone());
                } else if (isBoots) {
                    worn = inv.getBoots();
                    inv.setBoots(handItem.clone());
                }

                if (isHelmet || isChest || isLegs || isBoots) {
                    event.setCancelled(true);
                    VersionUtils.setItemInHand(player, worn);
                    try {
                        XSound.ITEM_ARMOR_EQUIP_GENERIC.play(player);
                    } catch (Exception ignored) {
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        if (!VersionUtils.isMainHand(event))
            return;

        Player player = event.getPlayer();
        if (!player.isSneaking())
            return;
        if (!plugin.getPlayerDataManager().isEnabled(player.getUniqueId()))
            return;

        if (event.getRightClicked() instanceof ArmorStand) {
            if (!plugin.getMainConfig().isFeatureEnabled("Swap_ArmorStand"))
                return;
            if (!player.hasPermission("armorswap.swap.armorstand"))
                return;

            ArmorStand stand = (ArmorStand) event.getRightClicked();
            event.setCancelled(true);
            swapWithArmorStand(player, stand);

        } else if (event.getRightClicked() instanceof ItemFrame) {
            if (!plugin.getMainConfig().isFeatureEnabled("Swap_ItemFrame"))
                return;
            if (!player.hasPermission("armorswap.swap.itemframe"))
                return;

            ItemFrame frame = (ItemFrame) event.getRightClicked();
            event.setCancelled(true);
            swapWithItemFrame(player, frame);
        }
    }

    private void swapWithArmorStand(Player player, ArmorStand stand) {
        PlayerInventory pInv = player.getInventory();

        ItemStack pHelmet = pInv.getHelmet();
        ItemStack pChest = pInv.getChestplate();
        ItemStack pLegs = pInv.getLeggings();
        ItemStack pBoots = pInv.getBoots();
        ItemStack pHand = VersionUtils.getItemInHand(player);

        ItemStack sHelmet = stand.getHelmet();
        ItemStack sChest = stand.getChestplate();
        ItemStack sLegs = stand.getLeggings();
        ItemStack sBoots = stand.getBoots();
        ItemStack sHand = stand.getItemInHand();

        pInv.setHelmet(sHelmet);
        pInv.setChestplate(sChest);
        pInv.setLeggings(sLegs);
        pInv.setBoots(sBoots);
        VersionUtils.setItemInHand(player, sHand);

        stand.setHelmet(pHelmet);
        stand.setChestplate(pChest);
        stand.setLeggings(pLegs);
        stand.setBoots(pBoots);
        stand.setItemInHand(pHand);

        try {
            XSound.ITEM_ARMOR_EQUIP_GENERIC.play(player);
        } catch (Exception ignored) {
        }
    }

    private void swapWithItemFrame(Player player, ItemFrame frame) {
        ItemStack frameItem = frame.getItem();
        if (frameItem == null || frameItem.getType() == Material.AIR)
            return;

        String typeName = frameItem.getType().name();
        PlayerInventory inv = player.getInventory();
        ItemStack worn = null;

        boolean isHelmet = typeName.endsWith("HELMET") || typeName.endsWith("SKULL_ITEM") || typeName.endsWith("HEAD")
                || typeName.equals("CARVED_PUMPKIN");
        boolean isChest = typeName.endsWith("CHESTPLATE") || typeName.equals("ELYTRA");
        boolean isLegs = typeName.endsWith("LEGGINGS");
        boolean isBoots = typeName.endsWith("BOOTS");

        if (isHelmet) {
            worn = inv.getHelmet();
            inv.setHelmet(frameItem.clone());
        } else if (isChest) {
            worn = inv.getChestplate();
            inv.setChestplate(frameItem.clone());
        } else if (isLegs) {
            worn = inv.getLeggings();
            inv.setLeggings(frameItem.clone());
        } else if (isBoots) {
            worn = inv.getBoots();
            inv.setBoots(frameItem.clone());
        }

        if (isHelmet || isChest || isLegs || isBoots) {
            frame.setItem(worn);
            try {
                XSound.ITEM_ARMOR_EQUIP_GENERIC.play(player);
            } catch (Exception ignored) {
            }
        }
    }

    private boolean isArmor(ItemStack item) {
        String name = item.getType().name();
        return name.endsWith("HELMET") || name.endsWith("CHESTPLATE") || name.endsWith("LEGGINGS")
                || name.endsWith("BOOTS") || name.equals("ELYTRA") || name.endsWith("SKULL_ITEM")
                || name.endsWith("HEAD") || name.equals("CARVED_PUMPKIN");
    }

    private void openShulkerBox(Player player, ItemStack shulkerBox) {
        try {
            Object meta = shulkerBox.getItemMeta();
            if (meta == null)
                return;
            if (meta.getClass().getName().endsWith("BlockStateMeta")) {
                java.lang.reflect.Method getBlockState = meta.getClass().getMethod("getBlockState");
                Object blockState = getBlockState.invoke(meta);
                if (blockState.getClass().getName().endsWith("ShulkerBox")) {
                    java.lang.reflect.Method getInventory = blockState.getClass().getMethod("getInventory");
                    org.bukkit.inventory.Inventory shulkerInv = (org.bukkit.inventory.Inventory) getInventory
                            .invoke(blockState);

                    org.bukkit.inventory.Inventory view = org.bukkit.Bukkit.createInventory(null, 27,
                            "Shulker Box Viewer");
                    view.setContents(shulkerInv.getContents());
                    player.openInventory(view);
                }
            }
        } catch (Exception ignored) {
            // Fails silently if API isn't available
        }
    }

    @EventHandler
    public void onInventoryClick(org.bukkit.event.inventory.InventoryClickEvent event) {
        if (event.getView().getTitle().equals("Shulker Box Viewer")) {
            event.setCancelled(true);
        }
    }
}
