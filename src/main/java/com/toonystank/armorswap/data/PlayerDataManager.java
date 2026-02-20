package com.toonystank.armorswap.data;

import com.toonystank.armorswap.ArmorSwap;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerDataManager {

    private final ArmorSwap plugin;
    private File file;
    private FileConfiguration dataConfig;
    private final Map<UUID, Boolean> toggleCache = new HashMap<>();

    public PlayerDataManager(ArmorSwap plugin) {
        this.plugin = plugin;
        load();
    }

    public void load() {
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }

        file = new File(plugin.getDataFolder(), "data.yml");

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        dataConfig = YamlConfiguration.loadConfiguration(file);

        // Load existing data into cache
        if (dataConfig.contains("Toggles")) {
            for (String uuidStr : dataConfig.getConfigurationSection("Toggles").getKeys(false)) {
                try {
                    UUID uuid = UUID.fromString(uuidStr);
                    boolean isEnabled = dataConfig.getBoolean("Toggles." + uuidStr);
                    toggleCache.put(uuid, isEnabled);
                } catch (IllegalArgumentException ignored) {
                }
            }
        }
    }

    public void save() {
        for (Map.Entry<UUID, Boolean> entry : toggleCache.entrySet()) {
            dataConfig.set("Toggles." + entry.getKey().toString(), entry.getValue());
        }
        try {
            dataConfig.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isEnabled(UUID uuid) {
        return toggleCache.getOrDefault(uuid, true);
    }

    public void setEnabled(UUID uuid, boolean enabled) {
        toggleCache.put(uuid, enabled);
    }
}
