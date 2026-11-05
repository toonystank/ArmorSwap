package com.toonystank.armorswap.Events;

import com.toonystank.armorswap.ArmorSwap;
import com.toonystank.armorswap.enums.ConfigDataType;
import com.toonystank.armorswap.enums.EquipmentDataType;
import com.toonystank.armorswap.utils.ConfigData;
import com.toonystank.armorswap.utils.PlayerData;
import com.toonystank.armorswap.utils.VersionSupport;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.io.IOException;

public class ClickEvent implements Listener {

    private final ConfigData configData;
    private final ArmorSwap plugin;

    public ClickEvent(ConfigData configData, ArmorSwap plugin) throws IOException {
        this.configData = configData;
        this.plugin = plugin;
    }

    //
    // ARMOR STAND EVENTS STARTS HERE
    //
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onArmorStandRightClickEvent(PlayerInteractAtEntityEvent event) {
        if (!ConfigDataType.ARMOR_STAND_SWAP.getBoolean()) return;
        if (!VersionSupport.isMainHand(event)) return;
        Player player = event.getPlayer();
        if (!player.isSneaking()) return;
        Entity entity = event.getRightClicked();
        if (!(entity instanceof ArmorStand)) return;
        if (!PlayerData.isSwapEnabled(player)) return;
        if (!player.hasPermission(ConfigDataType.ARMOR_STAND_SWAP_PERMISSION.getString())) return;

        ArmorStand stand = (ArmorStand) entity;
        EntityEquipment standEquipment = stand.getEquipment();
        if (standEquipment == null || !stand.isVisible()) return;
        if (VersionSupport.hasEquipmentLock(stand, stand.hasArms())) return;
        event.setCancelled(true);

        ItemStack[] standArmor = standEquipment.getArmorContents();
        ItemStack[] playerArmor = player.getInventory().getArmorContents();

        for (ItemStack itemStack : standArmor) {
            if (!canMove(itemStack, player)) return;
        }
        for (ItemStack itemStack : playerArmor) {
            if (!canMove(itemStack, player)) return;
        }

        if (stand.hasArms()) {
            ItemStack playerHand = VersionSupport.getItemInMainHand(player);
            ItemStack standHand = VersionSupport.getStandMainHand(stand);
            if (!canMove(playerHand, player) || !canMove(standHand, player)) return;
            VersionSupport.setItemInMainHand(player, standHand);
            VersionSupport.setStandMainHand(stand, playerHand);
        }

        player.getInventory().setArmorContents(standArmor);
        standEquipment.setArmorContents(playerArmor);
        VersionSupport.playSound(player, ConfigDataType.SOUND.getString());
    }
    //
    // ARMOR STAND EVENT ENDS HERE
    //
    //
    // ITEM FRAME EVENT STARTS HERE
    //
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onItemFrameRightClickEvent(PlayerInteractEntityEvent event) {
        if (!ConfigDataType.ITEM_FRAME_SWAP.getBoolean()) return;
        if (!VersionSupport.isMainHand(event)) return;
        Player player = event.getPlayer();
        if (!player.isSneaking()) return;
        Entity entity = event.getRightClicked();
        if (!(entity instanceof ItemFrame)) return;
        if (!PlayerData.isSwapEnabled(player)) return;
        if (!player.hasPermission(ConfigDataType.ITEM_FRAME_SWAP_PERMISSION.getString())) return;

        ItemFrame clickedFrame = (ItemFrame) entity;
        if (VersionSupport.isFixed(clickedFrame)) return;
        event.setCancelled(true);
        ItemStack playerItem = VersionSupport.getItemInMainHand(player);
        ItemStack itemOnFrame = clickedFrame.getItem();

        boolean playerEmpty = VersionSupport.isAir(playerItem);
        EquipmentDataType frameSlot = getArmorSlot(itemOnFrame);
        if (playerEmpty && frameSlot != null) {
            if (swapFrameWithWornArmor(player, clickedFrame, itemOnFrame, frameSlot)) {
                VersionSupport.playSound(player, ConfigDataType.SOUND.getString());
            }
            return;
        }

        boolean frameEmpty = VersionSupport.isAir(itemOnFrame);
        if (playerEmpty && frameEmpty) return;

        if ((playerEmpty || playerItem.getAmount() == 1) && (frameEmpty || itemOnFrame.getAmount() == 1)) {
            clickedFrame.setItem(playerEmpty ? null : playerItem);
            VersionSupport.setItemInMainHand(player, frameEmpty ? null : itemOnFrame);
            VersionSupport.playSound(player, ConfigDataType.SOUND.getString());
        } else {
            if (hasInventorySpace(player.getInventory(), itemOnFrame)) {
                giveItem(playerItem, itemOnFrame, clickedFrame, player);
                VersionSupport.playSound(player, ConfigDataType.SOUND.getString());
                return;
            }
            player.sendMessage(ChatColor.RED + "Inventory full");
        }
    }

    private boolean swapFrameWithWornArmor(Player player, ItemFrame frame, ItemStack frameItem, EquipmentDataType slot) {
        if (!canMove(frameItem, player)) return false;
        PlayerInventory inv = player.getInventory();
        ItemStack worn = getWornItem(inv, slot);
        if (!canMove(worn, player)) return false;

        setWornItem(inv, slot, frameItem);
        frame.setItem(VersionSupport.isAir(worn) ? null : worn);
        return true;
    }

    private boolean hasInventorySpace(PlayerInventory inv, ItemStack itemToGive) {
        if (VersionSupport.isAir(itemToGive)) return true;
        if (inv.firstEmpty() != -1) return true;
        for (int i = 0; i < 36; i++) {
            ItemStack item = inv.getItem(i);
            if (VersionSupport.isAir(item)) {
                return true;
            }
            if (item.isSimilar(itemToGive) && item.getAmount() + itemToGive.getAmount() <= item.getMaxStackSize()) {
                return true;
            }
        }
        return false;
    }

    public void giveItem(ItemStack playerItem, ItemStack itemOnFrame, ItemFrame clickedFrame, Player player) {
        ItemStack single = playerItem.clone();
        single.setAmount(1);
        if (playerItem.getAmount() > 1) {
            ItemStack remaining = playerItem.clone();
            remaining.setAmount(playerItem.getAmount() - 1);
            VersionSupport.setItemInMainHand(player, remaining);
        } else {
            VersionSupport.setItemInMainHand(player, null);
        }
        clickedFrame.setItem(single);
        if (!VersionSupport.isAir(itemOnFrame)) {
            player.getInventory().addItem(itemOnFrame);
        }
    }
    //
    // ITEM FRAME EVENT ENDS HERE
    //
    //
    // RIGHT CLICK with ARMOR EVENT START HERE
    //
    @EventHandler
    public void onArmorRightClickEvent(PlayerInteractEvent event) {
        Action action = event.getAction();
        if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        if (!ConfigDataType.MAIN_HAND_SWAP.getBoolean()) return;
        if (!VersionSupport.isMainHand(event)) return;

        Player player = event.getPlayer();
        ItemStack item = VersionSupport.getItemInMainHand(player);
        EquipmentDataType slot = getArmorSlot(item);
        if (slot == null) return;

        if (action == Action.RIGHT_CLICK_BLOCK && !player.isSneaking()) {
            Block block = event.getClickedBlock();
            if (VersionSupport.isInteractable(block)) return;
        }

        PlayerInventory inv = player.getInventory();
        ItemStack returnItem = getWornItem(inv, slot);
        if (VersionSupport.isAir(returnItem)) return;

        if (!PlayerData.isSwapEnabled(player) || !player.hasPermission(ConfigDataType.MAIN_HAND_SWAP_PERMISSION.getString())) {
            event.setCancelled(true);
            return;
        }

        if (!canMove(returnItem, player)) {
            event.setCancelled(true);
            return;
        }

        VersionSupport.setItemInMainHand(player, returnItem);
        setWornItem(inv, slot, item);
        event.setCancelled(true);
        VersionSupport.playSound(player, ConfigDataType.SOUND.getString());
    }
    //
    // RIGHT CLICK ON ARMOR EVENT ENDS HERE
    //

    private EquipmentDataType getArmorSlot(ItemStack item) {
        if (VersionSupport.isAir(item)) return null;
        String itemName = item.getType().name();
        if (itemName.contains(EquipmentDataType.PLAYER_HELMET.getName())) {
            return EquipmentDataType.PLAYER_HELMET;
        }
        if (itemName.contains(EquipmentDataType.PLAYER_CHEST_PLATE.getName())
                || itemName.contains(EquipmentDataType.PLAYER_ELYTRA.getName())) {
            return EquipmentDataType.PLAYER_CHEST_PLATE;
        }
        if (itemName.contains(EquipmentDataType.PLAYER_LEGGINGS.getName())) {
            return EquipmentDataType.PLAYER_LEGGINGS;
        }
        if (itemName.contains(EquipmentDataType.PLAYER_BOOTS.getName())) {
            return EquipmentDataType.PLAYER_BOOTS;
        }
        return null;
    }

    private ItemStack getWornItem(PlayerInventory inv, EquipmentDataType slot) {
        switch (slot) {
            case PLAYER_HELMET:
                return inv.getHelmet();
            case PLAYER_CHEST_PLATE:
                return inv.getChestplate();
            case PLAYER_LEGGINGS:
                return inv.getLeggings();
            case PLAYER_BOOTS:
                return inv.getBoots();
            default:
                return null;
        }
    }

    private void setWornItem(PlayerInventory inv, EquipmentDataType slot, ItemStack item) {
        switch (slot) {
            case PLAYER_HELMET:
                inv.setHelmet(item);
                break;
            case PLAYER_CHEST_PLATE:
                inv.setChestplate(item);
                break;
            case PLAYER_LEGGINGS:
                inv.setLeggings(item);
                break;
            case PLAYER_BOOTS:
                inv.setBoots(item);
                break;
            default:
                break;
        }
    }

    public int getArmorSwapEnabled(Player player) {
        return PlayerData.isSwapEnabled(player) ? 1 : 0;
    }

    public static boolean canMove(ItemStack itemStack, Player player) {
        if (VersionSupport.isAir(itemStack) || !VersionSupport.hasBindingCurse(itemStack)) {
            return true;
        }
        return ConfigDataType.BYPASS_ENCHANTMENT.getBoolean()
                || player.hasPermission(ConfigDataType.BYPASS_PERMISSION.getString());
    }
}
