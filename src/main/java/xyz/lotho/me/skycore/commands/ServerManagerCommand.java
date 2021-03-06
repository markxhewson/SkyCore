package xyz.lotho.me.skycore.commands;

import com.google.gson.JsonObject;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.lotho.me.skycore.SkyCore;
import xyz.lotho.me.skycore.handlers.Server;
import xyz.lotho.me.skycore.managers.TPSManager;
import xyz.lotho.me.skycore.storage.redis.impl.server.ServerCommandPacket;
import xyz.lotho.me.skycore.utils.Utilities;

public class ServerManagerCommand implements CommandExecutor {

    private final SkyCore instance;

    public ServerManagerCommand(SkyCore instance) {
        this.instance = instance;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return false;

        Player player = (Player) sender;
        if (!player.hasPermission(this.instance.config.get().getString("utils.adminPerm"))) {
            player.sendMessage(Utilities.color("&cYou do not have permission!"));
            return false;
        }

        if (args.length == 0) {
            player.sendMessage(this.getHelp());
            return false;
        }
        if (args.length == 1 && args[0].equalsIgnoreCase("list")) {
            player.sendMessage(this.getServersFormatted());
            return true;
        }
        else if (args.length >= 2 && args[0].equalsIgnoreCase("info")) {
            Server server = this.instance.serverManager.getServer(args[1]);

            if (server == null) {
                player.sendMessage(this.getHelp());
                return false;
            }

            player.sendMessage(this.getServerFormatted(server.getServerName(), server));
        }
        else if (args.length >= 3 && args[0].equalsIgnoreCase("execute")) {
            JsonObject jsonObject = new JsonObject();

            jsonObject.addProperty("serverName", args[1]);
            jsonObject.addProperty("playerName", player.getName());
            jsonObject.addProperty("command", Utilities.buildMessage(args, 2));

            new ServerCommandPacket(this.instance).send(jsonObject);
        } else {
            player.sendMessage(this.getHelp());
            return false;
        }

        return true;
    }

    public String getHelp() {
        return Utilities.color("&cInvalid arguments! Usage: /servermanager <args> <optional>\n &8- &7args: list, info, execute\n &8- &7optional: serverName, command");
    }

    public String getServerFormatted(String serverName, Server server) {
        String online = server.isOnline() ? "&aTrue" : "&cFalse";
        String whitelisted = server.isWhitelisted() ? "&aTrue" : "&cFalse";

        int onlineCount = server.getOnlinePlayers();
        int maxPlayers = server.getMaxPlayers();

        float tps = server.getTps1();
        float tps2 = server.getTps2();
        float tps3 = server.getTps3();

        String updatedAgo = server.getLastUpdatedInSeconds();

        StringBuilder builder = new StringBuilder(Utilities.color("\n&e&lServer Manager &a"));

        builder.append(serverName).append("\n");
        builder.append(Utilities.color(" &e?? &fOnline: " + online)).append("\n");
        builder.append(Utilities.color(" &e?? &fPlayers: &7" + onlineCount + "/" + maxPlayers)).append("\n");
        builder.append(Utilities.color(" &e?? &fTPS Data: " + TPSManager.getFormattedTPS(tps) + " " + TPSManager.getFormattedTPS(tps2) + " " + TPSManager.getFormattedTPS(tps3) + " &7(" + TPSManager.getTPSStatus(tps) + "&7)")).append("\n");
        builder.append(Utilities.color(" &e?? &fWhitelisted: " + whitelisted)).append("\n");
        builder.append(Utilities.color(" &e?? &fVersion: &6" + server.getVersion())).append("\n");
        builder.append(Utilities.color(" &e?? &fLast Updated: &8" + updatedAgo + "s ago")).append("\n");
        builder.append("\n ");

        return builder.toString();
    }

    public String getServersFormatted() {
        StringBuilder stringBuilder = new StringBuilder(Utilities.color("\n&e&lServer Manager"));

        this.instance.serverManager.getServersMap().forEach((serverName, server) -> {
            String online = server.isOnline() ? "&aOnline" : "&cOffline";
            String version = server.getVersion();

            int onlineCount = server.getOnlinePlayers();
            int maxPlayers = server.getMaxPlayers();

            float tps = server.getTps1();

            String updatedAgo = server.getLastUpdatedInSeconds();

            String line = " &e?? &a" + serverName + " &7(" + onlineCount + "/" + maxPlayers + ") : " + online + " &7" + version + " " + "(" + TPSManager.getTPSStatus(tps) + " TPS&7)" + " &8(Updated " + updatedAgo + "s ago)";
            stringBuilder.append("\n").append(Utilities.color(line));
        });
        stringBuilder.append("\n ");

        return stringBuilder.toString();
    }
}
