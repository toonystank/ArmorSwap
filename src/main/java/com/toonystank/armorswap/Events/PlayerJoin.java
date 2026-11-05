package com.toonystank.armorswap.Events;

import com.toonystank.armorswap.utils.PlayerData;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerJoin implements Listener {

    @EventHandler
    public void onJoinEvent(PlayerJoinEvent event) {
        PlayerData.initPlayer(event.getPlayer());
    }

    @EventHandler
    public void onQuitEvent(PlayerQuitEvent event) {
        PlayerData.unloadPlayer(event.getPlayer());
    }
}
