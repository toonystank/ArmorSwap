package com.toonystank.armorswap.utils;

import com.toonystank.armorswap.ArmorSwap;
import com.toonystank.armorswap.enums.ConfigDataType;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class PlayerData {

    private static final Set<UUID> disabledPlayers = new HashSet<>();
    private static File legacyFile;
    private static FileConfiguration legacyConfig;

    private static FileConfiguration getLegacyConfig() {
        if (legacyConfig == null) {
            ArmorSwap plugin = ArmorSwap.getPlugin();
            if (!plugin.getDataFolder().exists()) {
                plugin.getDataFolder().mkdirs();
            }
            legacyFile = new File(plugin.getDataFolder(), "playerdata.yml");
            if (!legacyFile.exists()) {
                try {
                    legacyFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            legacyConfig = YamlConfiguration.loadConfiguration(legacyFile);
        }
        return legacyConfig;
    }

    private static void saveLegacyConfig() {
        if (legacyConfig != null && legacyFile != null) {
            try {
                legacyConfig.save(legacyFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void initPlayer(Player player) {
        UUID uuid = player.getUniqueId();
        if (VersionSupport.hasPdc()) {
            Integer state = PdcData.get(player);
            if (state == null) {
                player.sendMessage(ConfigDataType.ENABLE.getString());
                PdcData.set(player, 1);
                disabledPlayers.remove(uuid);
            } else if (state == 0) {
                disabledPlayers.add(uuid);
            } else {
                disabledPlayers.remove(uuid);
            }
        } else {
            FileConfiguration cfg = getLegacyConfig();
            String key = uuid.toString();
            if (!cfg.contains(key)) {
                player.sendMessage(ConfigDataType.ENABLE.getString());
                cfg.set(key, 1);
                saveLegacyConfig();
                disabledPlayers.remove(uuid);
            } else if (cfg.getInt(key, 1) == 0) {
                disabledPlayers.add(uuid);
            } else {
                disabledPlayers.remove(uuid);
            }
        }
    }

    public static boolean isSwapEnabled(Player player) {
        return !disabledPlayers.contains(player.getUniqueId());
    }

    public static void unloadPlayer(Player player) {
        disabledPlayers.remove(player.getUniqueId());
    }

    public static boolean toggle(Player player) {
        boolean enable = disabledPlayers.contains(player.getUniqueId());
        setSwapEnabled(player, enable);
        return enable;
    }

    public static void setSwapEnabled(Player player, boolean enabled) {
        UUID uuid = player.getUniqueId();
        if (enabled) {
            disabledPlayers.remove(uuid);
        } else {
            disabledPlayers.add(uuid);
        }
        int val = enabled ? 1 : 0;
        if (VersionSupport.hasPdc()) {
            PdcData.set(player, val);
        } else {
            FileConfiguration cfg = getLegacyConfig();
            cfg.set(uuid.toString(), val);
            saveLegacyConfig();
        }
    }

    private static class PdcData {
        private static NamespacedKey key;

        private static NamespacedKey getKey() {
            if (key == null) {
                key = new NamespacedKey(ArmorSwap.getPlugin(), "ArmorSwapEnabled");
            }
            return key;
        }

        static Integer get(Player player) {
            PersistentDataContainer data = player.getPersistentDataContainer();
            return data.get(getKey(), PersistentDataType.INTEGER);
        }

        static void set(Player player, int value) {
            PersistentDataContainer data = player.getPersistentDataContainer();
            data.set(getKey(), PersistentDataType.INTEGER, value);
        }
    }
}
