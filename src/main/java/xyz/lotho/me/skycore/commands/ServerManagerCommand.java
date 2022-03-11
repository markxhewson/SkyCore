package xyz.lotho.me.skycore.commands;

import com.google.gson.JsonObject;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.lotho.me.skycore.SkyCore;
import xyz.lotho.me.skycore.managers.TPSManager;
import xyz.lotho.me.skycore.storage.impl.ServerCommandPacket;
import xyz.lotho.me.skycore.utils.Utilities;

import java.text.DecimalFormat;

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
            JsonObject jsonObject = this.instance.serverManager.getServer(args[1]);

            if (jsonObject == null) {
                player.sendMessage(this.getHelp());
                return false;
            }

            player.sendMessage(this.getServerFormatted(jsonObject.get("serverName").getAsString(), jsonObject));
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

    public String getServerFormatted(String serverName, JsonObject jsonObject) {
        String online = jsonObject.get("online").getAsBoolean() ? "&aTrue" : "&cFalse";
        String whitelisted = jsonObject.get("whitelisted").getAsBoolean() ? "&aTrue" : "&cFalse";

        int onlineCount = jsonObject.get("onlinePlayers").getAsInt();
        int maxPlayers = jsonObject.get("maxPlayers").getAsInt();

        float tps = jsonObject.get("tps1").getAsFloat();
        float tps2 = jsonObject.get("tps2").getAsFloat();
        float tps3 = jsonObject.get("tps3").getAsFloat();

        String updatedAgo = this.instance.serverManager.getTimeFromLastUpdate(jsonObject.get("serverName").getAsString(), jsonObject.get("lastUpdated").getAsLong());

        StringBuilder builder = new StringBuilder();

        builder.append(Utilities.color("\n&e&lServer Manager &a")).append(serverName).append("\n");
        builder.append(Utilities.color(" &e» &fOnline: " + online)).append("\n");
        builder.append(Utilities.color(" &e» &fPlayers: &7" + onlineCount + "/" + maxPlayers)).append("\n");
        builder.append(Utilities.color(" &e» &fTPS Data: " + TPSManager.getFormattedTPS(tps) + " " + TPSManager.getFormattedTPS(tps2) + " " + TPSManager.getFormattedTPS(tps3) + " &7(" + TPSManager.getTPSStatus(tps) + "&7)")).append("\n");
        builder.append(Utilities.color(" &e» &fWhitelisted: " + whitelisted)).append("\n");
        builder.append(Utilities.color(" &e» &fVersion: &6" + jsonObject.get("spigotVersion").getAsString())).append("\n");
        builder.append(Utilities.color(" &e» &fLast Updated: &8" + updatedAgo + "s ago")).append("\n");
        builder.append("\n ");

        return builder.toString();
    }

    public String getServersFormatted() {
        StringBuilder stringBuilder = new StringBuilder(Utilities.color("\n&e&lServer Manager"));

        this.instance.serverManager.getServers().forEach((serverName, serverData) -> {
            String online = serverData.get("online").getAsBoolean() ? "&aOnline" : "&cOffline";
            String version = serverData.get("spigotVersion").getAsString();

            int onlineCount = serverData.get("onlinePlayers").getAsInt();
            int maxPlayers = serverData.get("maxPlayers").getAsInt();

            float tps = serverData.get("tps1").getAsFloat();

            String updatedAgo = this.instance.serverManager.getTimeFromLastUpdate(serverName, serverData.get("lastUpdated").getAsLong());

            String line = " &e» &a" + serverName + " &7(" + onlineCount + "/" + maxPlayers + ") : " + online + " &7" + version + " " + "(" + TPSManager.getTPSStatus(tps) + " TPS&7)" + " &8(Updated " + updatedAgo + "s ago)";
            stringBuilder.append("\n").append(Utilities.color(line));
        });

        stringBuilder.append("\n ");
        return stringBuilder.toString();
    }
}
