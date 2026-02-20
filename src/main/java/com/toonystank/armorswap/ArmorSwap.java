package com.toonystank.armorswap;

import com.toonystank.armorswap.command.ArmorSwapCommand;
import com.toonystank.armorswap.command.HatCommand;
import com.toonystank.armorswap.data.PlayerDataManager;
import com.toonystank.armorswap.events.ArmorSwapListener;
import com.toonystank.armorswap.hooks.WorldGuardHook;
import com.toonystank.armorswap.utils.MainConfig;
import com.toonystank.armorswap.utils.MessageUtils;
import com.toonystank.armorswap.utils.Metrics;
import com.toonystank.armorswap.utils.UpdateChecker;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public final class ArmorSwap extends JavaPlugin {

    private static ArmorSwap instance;
    private MainConfig mainConfig;
    private PlayerDataManager playerDataManager;
    private WorldGuardHook worldGuardHook;

    public static ArmorSwap getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;

        // Initialize Managers
        this.mainConfig = new MainConfig(this);
        this.playerDataManager = new PlayerDataManager(this);

        // Setup hooks
        setupWorldGuardHook();

        // Register Commands
        new ArmorSwapCommand(this);
        new HatCommand(this);

        // Register Events
        getServer().getPluginManager().registerEvents(new ArmorSwapListener(this), this);

        // Update Checker
        new UpdateChecker(this, 97332).getVersion(version -> {
            if (this.getDescription().getVersion().equals(version)) {
                getLogger().info("There is not a new update available.");
            } else {
                getLogger().info("There is a new update available." + "\n" + ChatColor.RED
                        + "https://www.spigotmc.org/resources/armorswap-swap-items-by-right-clicking.97332/");
            }
        });

        // bStats metrics
        new Metrics(this, 14140);

        getLogger().info(ChatColor.AQUA + "ArmorSwap " + ChatColor.UNDERLINE + "Successfully loaded.");
    }

    @Override
    public void onDisable() {
        if (playerDataManager != null) {
            playerDataManager.save();
        }
    }

    private void setupWorldGuardHook() {
        if (getServer().getPluginManager().getPlugin("WorldGuard") != null) {
            this.worldGuardHook = new WorldGuardHook(this);
            MessageUtils.toConsole("WorldGuard found and hooked successfully.", false);
        }
    }

    public MainConfig getMainConfig() {
        return mainConfig;
    }

    public PlayerDataManager getPlayerDataManager() {
        return playerDataManager;
    }

    public WorldGuardHook getWorldGuardHook() {
        return worldGuardHook;
    }
}
