package com.toonystank.armorswap.command.subcommands;

import com.toonystank.armorswap.ArmorSwap;
import com.toonystank.armorswap.command.manager.SubCommand;
import com.toonystank.armorswap.utils.MessageUtils;
import org.bukkit.command.CommandSender;

public class ReloadCommand extends SubCommand {

    private final ArmorSwap plugin;

    public ReloadCommand(ArmorSwap plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "reload";
    }

    @Override
    public String getDescription() {
        return "Reloads the plugin configuration";
    }

    @Override
    public String getSyntax() {
        return "/armorswap reload";
    }

    @Override
    public String getPermission() {
        return "armorswap.command.reload";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        plugin.getMainConfig().reload();
        plugin.getPlayerDataManager().load(); // Reload data config as well
        MessageUtils.sendMessage(sender, plugin.getMainConfig().getMessage("Reload"));
    }
}
