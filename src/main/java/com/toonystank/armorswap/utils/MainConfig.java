package com.toonystank.armorswap.utils;

import com.toonystank.armorswap.ArmorSwap;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.List;

public class MainConfig {

    private final ArmorSwap plugin;
    private File file;
    private FileConfiguration config;

    public MainConfig(ArmorSwap plugin) {
        this.plugin = plugin;
        load();
    }

    public void load() {
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }

        file = new File(plugin.getDataFolder(), "config.yml");

        if (!file.exists()) {
            try (InputStream in = plugin.getResource("config.yml")) {
                if (in != null) {
                    Files.copy(in, file.toPath());
                } else {
                    file.createNewFile();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        config = YamlConfiguration.loadConfiguration(file);
    }

    public void reload() {
        load();
    }

    public void save() {
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public FileConfiguration getConfig() {
        return config;
    }

    // Configuration getters
    public boolean isFeatureEnabled(String feature) {
        return config.getBoolean("Features." + feature, true);
    }

    public String getMessage(String path) {
        return config.getString("Messages." + path, "Message missing: " + path);
    }

    public List<String> getMessageList(String path) {
        return config.getStringList("Messages." + path);
    }
}
