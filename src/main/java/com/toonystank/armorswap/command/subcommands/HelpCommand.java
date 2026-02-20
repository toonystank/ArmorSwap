package com.toonystank.armorswap.command.subcommands;

import com.toonystank.armorswap.command.ArmorSwapCommand;
import com.toonystank.armorswap.command.manager.SubCommand;
import com.toonystank.armorswap.utils.MessageUtils;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

public class HelpCommand extends SubCommand {

    private final ArmorSwapCommand parentCommand;

    public HelpCommand(ArmorSwapCommand parentCommand) {
        this.parentCommand = parentCommand;
    }

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getDescription() {
        return "Shows the help menu";
    }

    @Override
    public String getSyntax() {
        return "/armorswap help";
    }

    @Override
    public String getPermission() {
        return "armorswap.command.help";
    }

    @Override
    public List<String> getAliases() {
        return Collections.singletonList("?");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        MessageUtils.sendMessage(sender, "&8&m-----------------------------------");
        MessageUtils.sendMessage(sender, "&b&lArmorSwap &fHelp");
        MessageUtils.sendMessage(sender, "");
        for (SubCommand subCommand : parentCommand.getSubCommands()) {
            if (subCommand.getPermission() == null || subCommand.getPermission().isEmpty()
                    || sender.hasPermission(subCommand.getPermission())) {
                MessageUtils.sendMessage(sender,
                        "&b" + subCommand.getSyntax() + " &8- &7" + subCommand.getDescription());
            }
        }
        MessageUtils.sendMessage(sender, "&b/hat &8- &7Put the item in your hand on your head");
        MessageUtils.sendMessage(sender, "&8&m-----------------------------------");
    }
}
