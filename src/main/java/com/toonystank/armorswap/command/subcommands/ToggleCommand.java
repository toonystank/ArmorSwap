package com.toonystank.armorswap.command.subcommands;

import com.toonystank.armorswap.ArmorSwap;
import com.toonystank.armorswap.command.manager.SubCommand;
import com.toonystank.armorswap.utils.MessageUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ToggleCommand extends SubCommand {

    private final ArmorSwap plugin;

    public ToggleCommand(ArmorSwap plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "toggle";
    }

    @Override
    public String getDescription() {
        return "Toggles ArmorSwap on or off for yourself";
    }

    @Override
    public String getSyntax() {
        return "/armorswap toggle";
    }

    @Override
    public String getPermission() {
        return "armorswap.command.toggle";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            MessageUtils.sendMessage(sender, "&cOnly players can use this command.");
            return;
        }

        Player player = (Player) sender;
        boolean currentState = plugin.getPlayerDataManager().isEnabled(player.getUniqueId());
        plugin.getPlayerDataManager().setEnabled(player.getUniqueId(), !currentState);

        if (!currentState) {
            MessageUtils.sendMessage(player, plugin.getMainConfig().getMessage("ArmorSwap-Enable"));
        } else {
            MessageUtils.sendMessage(player, plugin.getMainConfig().getMessage("ArmorSwap-Disable"));
        }
    }
}
