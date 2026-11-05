package com.toonystank.armorswap.utils;

import com.toonystank.armorswap.enums.ConfigDataType;
import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class ConfigData extends ConfigManager {

    public ConfigData(Plugin plugin) throws IOException {
        super(plugin, "config.yml", false, true);
    }

    public String getMessage(String path) {
        String raw = getString(path);
        return raw != null ? ChatColor.translateAlternateColorCodes('&', raw) : "";
    }

    @Override
    public void reload() throws IOException, InvalidConfigurationException {
        super.reload();
        ConfigDataType.refreshCache();
    }

    @Override
    public void updateConfig(@NotNull String currentVersion, @NotNull String versionPath) throws IOException, InvalidConfigurationException {
        super.updateConfig(currentVersion, versionPath);
        ConfigDataType.refreshCache();
    }
}
