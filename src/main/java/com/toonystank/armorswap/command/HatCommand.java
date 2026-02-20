package com.toonystank.armorswap.command;

import com.toonystank.armorswap.ArmorSwap;
import com.toonystank.armorswap.command.manager.BaseCommand;
import com.toonystank.armorswap.compatibility.VersionUtils;
import com.toonystank.armorswap.utils.MessageUtils;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.Collections;

public class HatCommand extends BaseCommand {

    public HatCommand(ArmorSwap plugin) {
        super(plugin, "hat", "Put the item in your hand on your head", "/hat", Collections.singletonList("head"));
        setPermission("armorswap.command.hat");
    }

    @Override
    public void onDefaultExecute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player)) {
            MessageUtils.sendMessage(sender, "&cOnly players can use this command.");
            return;
        }

        if (!sender.hasPermission("armorswap.command.hat")) {
            MessageUtils.sendMessage(sender, plugin.getMainConfig().getMessage("No-Permission"));
            return;
        }

        Player player = (Player) sender;
        ItemStack handItem = VersionUtils.getItemInHand(player);

        if (handItem == null || handItem.getType() == Material.AIR) {
            MessageUtils.sendMessage(player, plugin.getMainConfig().getMessage("Missing-Item"));
            return;
        }

        PlayerInventory inventory = player.getInventory();
        ItemStack headItem = inventory.getHelmet();

        inventory.setHelmet(handItem);
        VersionUtils.setItemInHand(player, headItem);

        MessageUtils.sendMessage(player, plugin.getMainConfig().getMessage("Hat-Command"));
    }
}
