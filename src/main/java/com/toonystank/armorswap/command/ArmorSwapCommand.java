package com.toonystank.armorswap.command;

import com.toonystank.armorswap.ArmorSwap;
import com.toonystank.armorswap.command.manager.BaseCommand;
import com.toonystank.armorswap.command.subcommands.HelpCommand;
import com.toonystank.armorswap.command.subcommands.ReloadCommand;
import com.toonystank.armorswap.command.subcommands.ToggleCommand;
import org.bukkit.command.CommandSender;

import java.util.Arrays;

public class ArmorSwapCommand extends BaseCommand {

    public ArmorSwapCommand(ArmorSwap plugin) {
        super(plugin, "armorswap", "Main command for ArmorSwap", "/armorswap", Arrays.asList("as", "armor"));

        registerSubCommand(new HelpCommand(this));
        registerSubCommand(new ReloadCommand(plugin));
        registerSubCommand(new ToggleCommand(plugin));
    }

    @Override
    public void onDefaultExecute(CommandSender sender, String label, String[] args) {
        new HelpCommand(this).execute(sender, args);
    }
}
