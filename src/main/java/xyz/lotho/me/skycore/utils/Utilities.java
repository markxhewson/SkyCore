package xyz.lotho.me.skycore.utils;

import org.bukkit.ChatColor;
import xyz.lotho.me.skycore.SkyCore;

import java.util.Arrays;

public class Utilities {

    public static void log(String message) {
        System.out.println("[LOG] " + message);
    }

    public static String color(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static String buildMessage(String[] args, int start) {
        if (start >= args.length) {
            return "";
        }
        return ChatColor.stripColor(String.join(" ", Arrays.copyOfRange(args, start, args.length)));
    }

    public static void adminLog(SkyCore instance, String message) {
        instance.getServer().getOnlinePlayers().stream().filter((player) -> player.hasPermission(instance.config.get().getString("utils.adminPerm"))).forEach((player) -> {
            player.sendMessage(color(message));
        });
    }
}
