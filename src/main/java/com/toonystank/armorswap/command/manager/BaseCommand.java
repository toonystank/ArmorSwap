package com.toonystank.armorswap.command.manager;

import com.toonystank.armorswap.ArmorSwap;
import com.toonystank.armorswap.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class BaseCommand extends BukkitCommand {

    protected final ArmorSwap plugin;
    private final List<SubCommand> subCommands = new ArrayList<>();

    protected BaseCommand(ArmorSwap plugin, String name, String description, String usageMessage,
            List<String> aliases) {
        super(name, description, usageMessage, aliases);
        this.plugin = plugin;
        registerCommand();
    }

    private void registerCommand() {
        try {
            Field commandMapField = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            commandMapField.setAccessible(true);
            CommandMap commandMap = (CommandMap) commandMapField.get(Bukkit.getServer());
            // The fallback prefix acts as a namespace (e.g., target /armorswap instead of
            // requiring plugin name)
            commandMap.register(plugin.getName().toLowerCase(), this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void registerSubCommand(SubCommand subCommand) {
        subCommands.add(subCommand);
    }

    public List<SubCommand> getSubCommands() {
        return subCommands;
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (args.length == 0) {
            onDefaultExecute(sender, label, args);
            return true;
        }

        for (SubCommand subCommand : subCommands) {
            if (subCommand.getName().equalsIgnoreCase(args[0])
                    || subCommand.getAliases().contains(args[0].toLowerCase())) {
                if (subCommand.getPermission() != null && !subCommand.getPermission().isEmpty()
                        && !sender.hasPermission(subCommand.getPermission())) {
                    MessageUtils.sendMessage(sender, plugin.getMainConfig().getMessage("No-Permission"));
                    return true;
                }
                subCommand.execute(sender, Arrays.copyOfRange(args, 1, args.length));
                return true;
            }
        }

        onDefaultExecute(sender, label, args);
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        if (args.length == 1) {
            List<String> completions = new ArrayList<>();
            for (SubCommand subCommand : subCommands) {
                if (subCommand.getPermission() == null || subCommand.getPermission().isEmpty()
                        || sender.hasPermission(subCommand.getPermission())) {
                    if (subCommand.getName().toLowerCase().startsWith(args[0].toLowerCase())) {
                        completions.add(subCommand.getName());
                    }
                }
            }
            return completions;
        }

        for (SubCommand subCommand : subCommands) {
            if (subCommand.getName().equalsIgnoreCase(args[0])
                    || subCommand.getAliases().contains(args[0].toLowerCase())) {
                if (subCommand.getPermission() == null || subCommand.getPermission().isEmpty()
                        || sender.hasPermission(subCommand.getPermission())) {
                    return subCommand.tabComplete(sender, Arrays.copyOfRange(args, 1, args.length));
                }
            }
        }

        return new ArrayList<>();
    }

    public abstract void onDefaultExecute(CommandSender sender, String label, String[] args);
}
