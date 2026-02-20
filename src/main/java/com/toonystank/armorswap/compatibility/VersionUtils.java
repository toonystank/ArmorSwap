package com.toonystank.armorswap.compatibility;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.lang.reflect.Method;

public class VersionUtils {

    private static Method getHandInteractMethod;
    private static Method getHandInteractEntityMethod;
    private static Method getItemInMainHandMethod;

    static {
        try {
            getHandInteractMethod = PlayerInteractEvent.class.getMethod("getHand");
        } catch (NoSuchMethodException ignored) {
        }

        try {
            getHandInteractEntityMethod = PlayerInteractEntityEvent.class.getMethod("getHand");
        } catch (NoSuchMethodException ignored) {
        }

        try {
            getItemInMainHandMethod = PlayerInventory.class.getMethod("getItemInMainHand");
        } catch (NoSuchMethodException ignored) {
        }
    }

    public static boolean isMainHand(PlayerInteractEvent event) {
        if (getHandInteractMethod == null) {
            // 1.8.8 only has one hand
            return true;
        }
        try {
            Object hand = getHandInteractMethod.invoke(event);
            return hand != null && hand.toString().equals("HAND");
        } catch (Exception e) {
            return true;
        }
    }

    public static boolean isMainHand(PlayerInteractEntityEvent event) {
        if (getHandInteractEntityMethod == null) {
            // 1.8.8 only has one hand
            return true;
        }
        try {
            Object hand = getHandInteractEntityMethod.invoke(event);
            return hand != null && hand.toString().equals("HAND"); // EquipmentSlot.HAND
        } catch (Exception e) {
            return true;
        }
    }

    public static ItemStack getItemInHand(Player player) {
        if (getItemInMainHandMethod != null) {
            try {
                return (ItemStack) getItemInMainHandMethod.invoke(player.getInventory());
            } catch (Exception e) {
                // Ignore
            }
        }
        return player.getInventory().getItemInHand(); // Fallback for 1.8.8
    }

    public static void setItemInHand(Player player, ItemStack item) {
        if (getItemInMainHandMethod != null) {
            try {
                Method setItemInMainHand = PlayerInventory.class.getMethod("setItemInMainHand", ItemStack.class);
                setItemInMainHand.invoke(player.getInventory(), item);
                return;
            } catch (Exception e) {
                // Ignore
            }
        }
        // Fallback for 1.8.8
        player.getInventory().setItemInHand(item);
    }
}
