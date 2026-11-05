package com.toonystank.armorswap.Commands;

import com.toonystank.armorswap.Events.ClickEvent;
import com.toonystank.armorswap.enums.ConfigDataType;
import com.toonystank.armorswap.utils.ConfigData;
import com.toonystank.armorswap.utils.PlayerData;
import com.toonystank.armorswap.utils.VersionSupport;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.Plugin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class CommandManager implements CommandExecutor, TabCompleter {

    private final ConfigData configData;
    private final Plugin plugin;

    public CommandManager(ConfigData configData, Plugin plugin) {
        this.configData = configData;
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            onDefault(sender);
            return true;
        }

        String sub = args[0].toLowerCase(Locale.ROOT);
        switch (sub) {
            case "reload":
                if (!sender.hasPermission("armorswap.command.reload")) {
                    sender.sendMessage(ConfigDataType.PREFIX.getString() + " " + ChatColor.RED + "You don't have permission to run this command.");
                    return true;
                }
                onReload(sender);
                return true;
            case "head":
                if (!sender.hasPermission("armorswap.command.head")) {
                    sender.sendMessage(ConfigDataType.PREFIX.getString() + " " + ChatColor.RED + "You don't have permission to run this command.");
                    return true;
                }
                onHead(sender);
                return true;
            case "help":
                onHelp(sender);
                return true;
            default:
                sender.sendMessage(ConfigDataType.WRONG_USAGE.getString());
                return true;
        }
    }

    public void onReload(CommandSender sender) {
        if (configData == null) {
            return;
        }
        try {
            configData.reload();
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
            return;
        }
        if (sender instanceof Player) {
            sender.sendMessage(ConfigDataType.PREFIX.getString() + "" + ChatColor.AQUA + " ArmorSwap is reloaded");
        } else {
            plugin.getLogger().info(ConfigDataType.PREFIX.getString() + "" + ChatColor.AQUA + " ArmorSwap is reloaded");
        }
    }

    public void onHead(CommandSender sender) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            PlayerInventory inv = player.getInventory();
            ItemStack itemForHead = VersionSupport.getItemInMainHand(player);
            ItemStack itemForHand = inv.getHelmet();
            if (VersionSupport.isAir(itemForHead) && VersionSupport.isAir(itemForHand)) {
                return;
            }
            if (!ClickEvent.canMove(itemForHand, player)) {
                return;
            }
            if (!VersionSupport.isAir(itemForHead) && itemForHead.getAmount() > 1) {
                if (!VersionSupport.isAir(itemForHand) && inv.firstEmpty() == -1) {
                    player.sendMessage(ChatColor.RED + "Inventory full");
                    return;
                }
                ItemStack single = itemForHead.clone();
                single.setAmount(1);
                ItemStack remaining = itemForHead.clone();
                remaining.setAmount(itemForHead.getAmount() - 1);
                inv.setHelmet(single);
                VersionSupport.setItemInMainHand(player, remaining);
                if (!VersionSupport.isAir(itemForHand)) {
                    inv.addItem(itemForHand);
                }
            } else {
                inv.setHelmet(VersionSupport.isAir(itemForHead) ? null : itemForHead);
                VersionSupport.setItemInMainHand(player, VersionSupport.isAir(itemForHand) ? null : itemForHand);
            }
            VersionSupport.playSound(player, ConfigDataType.SOUND.getString());
        } else {
            plugin.getLogger().info(ConfigDataType.PREFIX.getString() + "" + ChatColor.RED + " Must be an player to run this command.");
        }
    }

    public void onHelp(CommandSender sender) {
        String helpMessage = ChatColor.AQUA + "" + ChatColor.RESET + "" + ChatColor.STRIKETHROUGH + "|              |" + "\n" + "\n" + ChatColor.YELLOW + "* /Armorswap reload - Reloads the plugin" + "\n" + "* /Armorswap head - Replace player head with holding item" + "\n" + "\n" + ChatColor.AQUA + "" + ChatColor.RESET + "" + "|              |";
        if (sender instanceof Player) {
            sender.sendMessage(helpMessage);
        } else {
            plugin.getLogger().info(helpMessage);
        }
    }

    public void onDefault(CommandSender sender) {
        if (!sender.hasPermission("armorswap.command.use")) {
            sender.sendMessage(ConfigDataType.PREFIX.getString() + " " + ChatColor.RED + "You don't have permission to run this command.");
            return;
        }
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (PlayerData.toggle(player)) {
                player.sendMessage(ConfigDataType.ENABLE.getString());
            } else {
                player.sendMessage(ConfigDataType.DISABLE.getString());
            }
        } else {
            plugin.getLogger().info(ConfigDataType.PREFIX.getString() + "" + ChatColor.RED + " Must be an player to run this command.");
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length != 1) {
            return Collections.emptyList();
        }
        String input = args[0].toLowerCase(Locale.ROOT);
        List<String> completions = new ArrayList<>();
        if ("help".startsWith(input)) {
            completions.add("help");
        }
        if ("head".startsWith(input) && sender.hasPermission("armorswap.command.head")) {
            completions.add("head");
        }
        if ("reload".startsWith(input) && sender.hasPermission("armorswap.command.reload")) {
            completions.add("reload");
        }
        return completions;
    }
}
