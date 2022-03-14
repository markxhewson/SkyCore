package xyz.lotho.me.skycore.storage.redis.impl.server;

import com.google.gson.JsonObject;
import xyz.lotho.me.skycore.SkyCore;
import xyz.lotho.me.skycore.utils.Tasks;
import xyz.lotho.me.skycore.utils.Utilities;

public class ServerCommandPacket {

    private final SkyCore instance;

    public ServerCommandPacket(SkyCore instance) {
        this.instance = instance;
    }

    public void receive(JsonObject jsonObject) {
        String serverName = jsonObject.get("serverName").getAsString();
        String playerName = jsonObject.get("playerName").getAsString();
        String command = jsonObject.get("command").getAsString();

        if (command.startsWith("/")) command = command.substring(1);

        if (serverName.equalsIgnoreCase("ALL") || this.instance.config.get().getString("server.name").equals(serverName)) {
            Utilities.adminLog(this.instance, "&c[ADMIN] &a" + playerName + " &eis executing command &a/" + command + "! &7(" + serverName + ")");

            String execute = command;
            Tasks.run(this.instance, () -> this.instance.getServer().dispatchCommand(this.instance.getServer().getConsoleSender(), execute));
        }
    }

    public void send(JsonObject jsonObject) {
        jsonObject.addProperty("id", this.getId());
        this.instance.redisManager.send(this.instance.redisManager.getChannel(), jsonObject);
    }

    public String getId() {
        return "ServerCommandPacket";
    }
}
