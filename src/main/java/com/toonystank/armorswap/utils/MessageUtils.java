package com.toonystank.armorswap.utils;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageUtils {

    public static String color(String message) {
        if (message == null)
            return null;
        message = ChatColor.translateAlternateColorCodes('&', message);

        // Hex support for 1.16+
        try {
            Pattern hexPattern = Pattern.compile("&#([A-Fa-f0-9]{6})");
            Matcher matcher = hexPattern.matcher(message);
            StringBuffer buffer = new StringBuffer();
            while (matcher.find()) {
                String group = matcher.group(1);
                try {
                    Class<?> netmd5 = Class.forName("net.md_5.bungee.api.ChatColor");
                    Method ofMethod = netmd5.getMethod("of", String.class);
                    Object color = ofMethod.invoke(null, "#" + group);
                    matcher.appendReplacement(buffer, color.toString());
                } catch (Exception e) {
                    matcher.appendReplacement(buffer, "&#" + group);
                }
            }
            matcher.appendTail(buffer);
            return buffer.toString();
        } catch (Exception e) {
            return message;
        }
    }

    public static void sendMessage(CommandSender sender, String message) {
        if (message == null || message.isEmpty())
            return;
        sender.sendMessage(color(message));
    }

    public static void toConsole(String message, boolean error) {
        if (error) {
            org.bukkit.Bukkit.getConsoleSender().sendMessage(color("&c[ArmorSwap] " + message));
        } else {
            org.bukkit.Bukkit.getConsoleSender().sendMessage(color("&a[ArmorSwap] " + message));
        }
    }
}
