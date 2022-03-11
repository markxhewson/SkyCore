package xyz.lotho.me.skycore.storage.impl;

import com.google.gson.JsonObject;
import xyz.lotho.me.skycore.SkyCore;
import xyz.lotho.me.skycore.utils.Utilities;

public class ServerUpdateStatusPacket {

    private final SkyCore instance;

    public ServerUpdateStatusPacket(SkyCore instance) {
        this.instance = instance;
    }

    public void receive(JsonObject jsonObject) {
        String serverName = jsonObject.get("serverName").getAsString();
        boolean online = jsonObject.get("online").getAsBoolean();

        if (!this.instance.serverManager.serverExists(serverName)) this.instance.serverManager.addServer(serverName);

        this.instance.getServer().getOnlinePlayers().stream().filter((player) -> player.hasPermission(this.instance.config.get().getString("utils.adminPerm"))).forEach((player) -> {

            if (online) player.sendMessage(Utilities.color("&c[ADMIN] &a" + serverName + " &ehas just came online. &7(STATUS: CONNECTABLE)"));
            else player.sendMessage(Utilities.color("&c[ADMIN] &a" + serverName + " &ehas just went offline&e! &7(STATUS: NOT CONNECTABLE)"));

            this.instance.serverManager.setServerOnlineStatus(serverName, online);
        });
    }

    public void send(JsonObject jsonObject) {
        jsonObject.addProperty("id", this.getId());

        this.instance.redisManager.sendRequest(this.instance.redisManager.getChannel(), jsonObject);
    }

    public String getId() {
        return "ServerUpdateStatusPacket";
    }
}
