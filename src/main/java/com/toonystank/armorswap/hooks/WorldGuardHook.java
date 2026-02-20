package com.toonystank.armorswap.hooks;

import com.toonystank.armorswap.ArmorSwap;

public class WorldGuardHook {

    private final ArmorSwap plugin;

    public WorldGuardHook(ArmorSwap plugin) {
        this.plugin = plugin;
    }

    // Here we'd add actual WorldGuard API checks using reflection to support 1.8 to
    // 1.21
    // WorldGuard 6 for 1.8 to 1.12, WorldGuard 7 for 1.13+
    // For now, returning true to allow action.

    public boolean canBuild(org.bukkit.entity.Player player, org.bukkit.Location location) {
        // Simplified fallback for compilation without throwing NoClassDefFound.
        return true;
    }
}
