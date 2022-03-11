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
        else if (args.length >= 4 && args[0].equalsIgnoreCase("execute")) {
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
        return Utilities.color("&cInvalid arguments! Usage: /servermanager <args> <optional>\n &8- &7args: list, execute\n &8- &7optional: command");
    }

    public String getServersFormatted() {
        StringBuilder stringBuilder = new StringBuilder(Utilities.color("\n&e&lServer Manager"));

        this.instance.serverManager.getServers().forEach((serverName, serverData) -> {
            String online = serverData.get("online").getAsBoolean() ? "&aOnline" : "&cOffline";
            String version = serverData.get("spigotVersion").getAsString();

            int onlineCount = serverData.get("onlinePlayers").getAsInt();
            int maxPlayers = serverData.get("maxPlayers").getAsInt();

            float tps = serverData.get("tps1").getAsFloat();

            String updatedAgo = new DecimalFormat("#.##").format((float) (System.currentTimeMillis() - serverData.get("lastUpdated").getAsLong()) / 1000);

            String line = " &e» &a" + serverName + " &7(" + onlineCount + "/" + maxPlayers + ") : " + online + " &7" + version + " " + "(" + TPSManager.getTPSStatus(tps) + " TPS&7)" + " &8(Updated " + updatedAgo + "s ago)";
            stringBuilder.append("\n").append(Utilities.color(line));
        });

        stringBuilder.append("\n ");
        return stringBuilder.toString();
    }
}
