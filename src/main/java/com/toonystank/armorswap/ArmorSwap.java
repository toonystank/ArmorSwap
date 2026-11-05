package com.toonystank.armorswap;

import com.toonystank.armorswap.Commands.CommandManager;
import com.toonystank.armorswap.Events.ClickEvent;
import com.toonystank.armorswap.Events.PlayerJoin;
import com.toonystank.armorswap.enums.ConfigDataType;
import com.toonystank.armorswap.utils.ConfigData;
import com.toonystank.armorswap.utils.Metrics;
import com.toonystank.armorswap.utils.PlayerData;
import com.toonystank.armorswap.utils.UpdateChecker;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

public final class ArmorSwap extends JavaPlugin {

    public static ArmorSwap getPlugin() {
        return plugin;
    }

    public String getConfigVersion() {
        return configVersion;
    }

    private final String configVersion = "1.5.9";

    private static ArmorSwap plugin;
    private ConfigData configData;

    public ConfigData getConfigData() {
        return configData;
    }

    public static void setPlugin(ArmorSwap plugin) {
        ArmorSwap.plugin = plugin;
    }

    @Override
    public void onEnable() {
        setPlugin(this);
        try {
            configData = new ConfigData(this);
            configData.updateConfig(configVersion, ConfigDataType.VERSION.getName());
            ConfigDataType.refreshCache();
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        new UpdateChecker(this, 97332).getVersion(version -> {
            if (this.getDescription().getVersion().equals(version)) {
                getLogger().info("There is not a new update available.");
            } else {
                getLogger().info("There is a new update available." + "\n" + ChatColor.RED + "https://www.spigotmc.org/resources/armorswap-swap-items-by-right-clicking.97332/");
            }
        });
        //bStats metrics
        new Metrics(this, 35151); //https://bstats.org/what-is-my-plugin-id

        for (Player online : Bukkit.getOnlinePlayers()) {
            PlayerData.initPlayer(online);
        }

        // Events
        getServer().getPluginManager().registerEvents(new PlayerJoin(), this);
        try {
            getServer().getPluginManager().registerEvents(new ClickEvent(configData, this), this);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Commands
        CommandManager commandManager = new CommandManager(configData, this);
        PluginCommand command = getCommand("Armorswap");
        if (command != null) {
            command.setExecutor(commandManager);
            command.setTabCompleter(commandManager);
        }

        getLogger().info(ChatColor.AQUA + "ArmorSwap " + ChatColor.UNDERLINE + "Successfully loaded.");
    }

}
