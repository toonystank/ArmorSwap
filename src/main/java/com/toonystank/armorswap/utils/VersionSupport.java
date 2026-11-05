package com.toonystank.armorswap.utils;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Server;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Method;
import java.util.Locale;
import java.util.function.Consumer;

public class VersionSupport {

    private static final boolean HAS_OFF_HAND;
    private static final boolean HAS_IS_AIR;
    private static final boolean HAS_IS_INTERACTABLE;
    private static final boolean HAS_PDC;
    private static final boolean HAS_EQUIPMENT_LOCK;
    private static final boolean IS_FOLIA;
    private static final Enchantment BINDING_CURSE;

    private static Method legacyPlayerGetHand;
    private static Method legacyPlayerSetHand;
    private static Method legacyStandGetHand;
    private static Method legacyStandSetHand;
    private static final Method FRAME_IS_FIXED;
    private static final Method SOUND_VALUE_OF;

    private static String cachedSoundKey;
    private static Sound cachedSound;

    static {
        boolean offHand = false;
        try {
            PlayerInteractEvent.class.getMethod("getHand");
            PlayerInventory.class.getMethod("getItemInMainHand");
            offHand = true;
        } catch (NoSuchMethodException e) {
            try {
                legacyPlayerGetHand = PlayerInventory.class.getMethod("getItemInHand");
                legacyPlayerSetHand = PlayerInventory.class.getMethod("setItemInHand", ItemStack.class);
                legacyStandGetHand = ArmorStand.class.getMethod("getItemInHand");
                legacyStandSetHand = ArmorStand.class.getMethod("setItemInHand", ItemStack.class);
            } catch (NoSuchMethodException ignored) {
            }
        }
        HAS_OFF_HAND = offHand;

        HAS_IS_AIR = hasMethod(Material.class, "isAir");
        HAS_IS_INTERACTABLE = hasMethod(Material.class, "isInteractable");

        boolean pdc = false;
        try {
            Class.forName("org.bukkit.persistence.PersistentDataContainer");
            pdc = true;
        } catch (ClassNotFoundException ignored) {
        }
        HAS_PDC = pdc;

        boolean lock = false;
        try {
            Class<?> lockType = Class.forName("org.bukkit.entity.ArmorStand$LockType");
            ArmorStand.class.getMethod("hasEquipmentLock", EquipmentSlot.class, lockType);
            lock = true;
        } catch (ClassNotFoundException | NoSuchMethodException ignored) {
        }
        HAS_EQUIPMENT_LOCK = lock;

        Method isFixed = null;
        try {
            isFixed = ItemFrame.class.getMethod("isFixed");
        } catch (NoSuchMethodException ignored) {
        }
        FRAME_IS_FIXED = isFixed;

        Method valueOf = null;
        try {
            valueOf = Sound.class.getMethod("valueOf", String.class);
        } catch (NoSuchMethodException ignored) {
        }
        SOUND_VALUE_OF = valueOf;

        boolean folia = false;
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            folia = true;
        } catch (ClassNotFoundException ignored) {
        }
        IS_FOLIA = folia;

        BINDING_CURSE = resolveBindingCurse();
    }

    private static boolean hasMethod(Class<?> clazz, String name, Class<?>... params) {
        try {
            clazz.getMethod(name, params);
            return true;
        } catch (NoSuchMethodException e) {
            return false;
        }
    }

    private static Enchantment resolveBindingCurse() {
        try {
            Method getByKey = Enchantment.class.getMethod("getByKey", NamespacedKey.class);
            Object result = getByKey.invoke(null, NamespacedKey.minecraft("binding_curse"));
            if (result != null) {
                return (Enchantment) result;
            }
        } catch (Throwable ignored) {
        }
        try {
            Method getByName = Enchantment.class.getMethod("getByName", String.class);
            Object result = getByName.invoke(null, "BINDING_CURSE");
            if (result != null) {
                return (Enchantment) result;
            }
        } catch (Throwable ignored) {
        }
        return null;
    }

    public static boolean hasPdc() {
        return HAS_PDC;
    }

    public static boolean isFolia() {
        return IS_FOLIA;
    }

    public static boolean isMainHand(PlayerInteractEvent event) {
        return !HAS_OFF_HAND || HandHelper.isMainHand(event);
    }

    public static boolean isMainHand(PlayerInteractEntityEvent event) {
        return !HAS_OFF_HAND || HandHelper.isMainHand(event);
    }

    public static ItemStack getItemInMainHand(Player player) {
        if (HAS_OFF_HAND) {
            return HandHelper.getPlayerMainHand(player);
        }
        try {
            return (ItemStack) legacyPlayerGetHand.invoke(player.getInventory());
        } catch (Exception e) {
            return null;
        }
    }

    public static void setItemInMainHand(Player player, ItemStack item) {
        if (HAS_OFF_HAND) {
            HandHelper.setPlayerMainHand(player, item);
            return;
        }
        try {
            legacyPlayerSetHand.invoke(player.getInventory(), item);
        } catch (Exception ignored) {
        }
    }

    public static ItemStack getStandMainHand(ArmorStand stand) {
        if (HAS_OFF_HAND) {
            return HandHelper.getStandMainHand(stand);
        }
        try {
            return (ItemStack) legacyStandGetHand.invoke(stand);
        } catch (Exception e) {
            return null;
        }
    }

    public static void setStandMainHand(ArmorStand stand, ItemStack item) {
        if (HAS_OFF_HAND) {
            HandHelper.setStandMainHand(stand, item);
            return;
        }
        try {
            legacyStandSetHand.invoke(stand, item);
        } catch (Exception ignored) {
        }
    }

    public static boolean isFixed(ItemFrame frame) {
        if (FRAME_IS_FIXED == null) {
            return false;
        }
        try {
            return (Boolean) FRAME_IS_FIXED.invoke(frame);
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean hasEquipmentLock(ArmorStand stand, boolean includeHand) {
        return HAS_EQUIPMENT_LOCK && LockHelper.hasAnyLock(stand, includeHand);
    }

    public static boolean isAir(ItemStack item) {
        if (item == null || item.getAmount() <= 0) {
            return true;
        }
        Material type = item.getType();
        if (type == Material.AIR) {
            return true;
        }
        return HAS_IS_AIR && MaterialHelper.isAir(type);
    }

    public static boolean hasBindingCurse(ItemStack item) {
        return BINDING_CURSE != null && item != null && item.containsEnchantment(BINDING_CURSE);
    }

    public static boolean isInteractable(Block block) {
        if (block == null) {
            return false;
        }
        Material type = block.getType();
        String name = type.name();
        if (HAS_IS_INTERACTABLE) {
            if (name.endsWith("_STAIRS")
                    || name.endsWith("_FENCE")
                    || name.startsWith("IRON_")
                    || name.endsWith("REDSTONE_ORE")
                    || name.equals("REDSTONE_WIRE")
                    || name.equals("MOVING_PISTON")
                    || name.equals("PUMPKIN")) {
                return false;
            }
            return MaterialHelper.isInteractable(type);
        }
        if (name.startsWith("IRON_")) {
            return false;
        }
        return name.contains("CHEST")
                || name.contains("DOOR")
                || name.contains("GATE")
                || name.contains("BUTTON")
                || name.contains("FURNACE")
                || name.contains("ANVIL")
                || name.contains("SHULKER_BOX")
                || name.contains("COMPARATOR")
                || name.contains("DIODE")
                || name.contains("COMMAND")
                || name.equals("LEVER")
                || name.equals("WORKBENCH")
                || name.equals("ENCHANTMENT_TABLE")
                || name.equals("BREWING_STAND")
                || name.equals("BEACON")
                || name.equals("HOPPER")
                || name.equals("DROPPER")
                || name.equals("DISPENSER")
                || name.equals("NOTE_BLOCK")
                || name.equals("JUKEBOX")
                || name.equals("CAKE_BLOCK")
                || name.equals("DAYLIGHT_DETECTOR")
                || name.equals("DAYLIGHT_DETECTOR_INVERTED")
                || name.equals("DRAGON_EGG")
                || name.equals("FLOWER_POT")
                || name.equals("BED_BLOCK");
    }

    public static void playSound(Player player, String soundName) {
        if (soundName == null || soundName.isEmpty()) {
            return;
        }
        Sound sound = resolveSound(soundName);
        if (sound != null) {
            player.playSound(player.getLocation(), sound, 1.0F, 1.0F);
        }
    }

    private static Sound resolveSound(String soundName) {
        if (soundName.equals(cachedSoundKey)) {
            return cachedSound;
        }
        String[] candidates = {
                soundName.toUpperCase(Locale.ROOT),
                "ITEM_ARMOR_EQUIP_GENERIC",
                "ITEM_ARMOR_EQUIP_DIAMOND",
                "HORSE_ARMOR",
                "ORB_PICKUP",
                "CLICK"
        };
        Sound resolved = null;
        if (SOUND_VALUE_OF != null) {
            for (String candidate : candidates) {
                try {
                    Object value = SOUND_VALUE_OF.invoke(null, candidate);
                    if (value != null) {
                        resolved = (Sound) value;
                        break;
                    }
                } catch (Throwable ignored) {
                }
            }
        }
        cachedSoundKey = soundName;
        cachedSound = resolved;
        return resolved;
    }

    public static void runSync(Plugin plugin, Runnable task) {
        if (IS_FOLIA) {
            try {
                Object scheduler = Server.class.getMethod("getGlobalRegionScheduler").invoke(Bukkit.getServer());
                Class.forName("io.papermc.paper.threadedregions.scheduler.GlobalRegionScheduler")
                        .getMethod("execute", Plugin.class, Runnable.class)
                        .invoke(scheduler, plugin, task);
                return;
            } catch (Exception e) {
                return;
            }
        }
        Bukkit.getScheduler().runTask(plugin, task);
    }

    public static void runAsync(Plugin plugin, Runnable task) {
        if (IS_FOLIA) {
            try {
                Object scheduler = Server.class.getMethod("getAsyncScheduler").invoke(Bukkit.getServer());
                Method runNow = Class.forName("io.papermc.paper.threadedregions.scheduler.AsyncScheduler")
                        .getMethod("runNow", Plugin.class, Consumer.class);
                runNow.invoke(scheduler, plugin, (Consumer<Object>) t -> task.run());
                return;
            } catch (Exception e) {
                Thread thread = new Thread(task, plugin.getName() + "-async");
                thread.setDaemon(true);
                thread.start();
                return;
            }
        }
        Bukkit.getScheduler().runTaskAsynchronously(plugin, task);
    }

    private static class HandHelper {
        static boolean isMainHand(PlayerInteractEvent event) {
            return event.getHand() == EquipmentSlot.HAND;
        }

        static boolean isMainHand(PlayerInteractEntityEvent event) {
            return event.getHand() == EquipmentSlot.HAND;
        }

        static ItemStack getPlayerMainHand(Player player) {
            return player.getInventory().getItemInMainHand();
        }

        static void setPlayerMainHand(Player player, ItemStack item) {
            player.getInventory().setItemInMainHand(item);
        }

        static ItemStack getStandMainHand(ArmorStand stand) {
            EntityEquipment equipment = stand.getEquipment();
            return equipment != null ? equipment.getItemInMainHand() : null;
        }

        static void setStandMainHand(ArmorStand stand, ItemStack item) {
            EntityEquipment equipment = stand.getEquipment();
            if (equipment != null) {
                equipment.setItemInMainHand(item);
            }
        }
    }

    private static class LockHelper {
        private static final EquipmentSlot[] ARMOR_SLOTS = {
                EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET
        };

        static boolean hasAnyLock(ArmorStand stand, boolean includeHand) {
            for (EquipmentSlot slot : ARMOR_SLOTS) {
                if (isLocked(stand, slot)) return true;
            }
            return includeHand && isLocked(stand, EquipmentSlot.HAND);
        }

        private static boolean isLocked(ArmorStand stand, EquipmentSlot slot) {
            for (ArmorStand.LockType type : ArmorStand.LockType.values()) {
                if (stand.hasEquipmentLock(slot, type)) return true;
            }
            return false;
        }
    }

    private static class MaterialHelper {
        static boolean isAir(Material type) {
            return type.isAir();
        }

        static boolean isInteractable(Material type) {
            return type.isInteractable();
        }
    }
}
