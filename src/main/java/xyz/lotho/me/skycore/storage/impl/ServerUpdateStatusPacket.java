package xyz.lotho.me.skycore.storage.impl;

import com.google.gson.JsonObject;
import xyz.lotho.me.skycore.SkyCore;
import xyz.lotho.me.skycore.handlers.Server;
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

        if (online) Utilities.adminLog(this.instance, "&c[ADMIN] &a" + serverName + " &ehas just came online. &7(STATUS: CONNECTABLE)");
        else Utilities.adminLog(this.instance, "&c[ADMIN] &a" + serverName + " &ehas just went offline&e! &7(STATUS: NOT CONNECTABLE)");

        Server server = this.instance.serverManager.getServer(serverName);
        server.setOnline(online);
    }

    public void send(JsonObject jsonObject) {
        jsonObject.addProperty("id", this.getId());

        this.instance.redisManager.sendRequest(this.instance.redisManager.getChannel(), jsonObject);
    }

    public String getId() {
        return "ServerUpdateStatusPacket";
    }
}
