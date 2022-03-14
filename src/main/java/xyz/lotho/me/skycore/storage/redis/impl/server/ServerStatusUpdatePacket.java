package xyz.lotho.me.skycore.storage.redis.impl.server;

import com.google.gson.JsonObject;
import xyz.lotho.me.skycore.SkyCore;
import xyz.lotho.me.skycore.handlers.Server;

public class ServerStatusUpdatePacket {

    private final SkyCore instance;

    public ServerStatusUpdatePacket(SkyCore instance) {
        this.instance = instance;
    }

    public void receive(JsonObject jsonObject) {
        String serverName = jsonObject.get("serverName").getAsString();

        if (!this.instance.serverManager.serverExists(serverName)) this.instance.serverManager.addServer(serverName);
        else {
            Server server = this.instance.serverManager.getServer(serverName);
            server.update(jsonObject);
        }
    }

    public void send(JsonObject jsonObject) {
        jsonObject.addProperty("id", this.getId());
        this.instance.redisManager.send(this.instance.redisManager.getChannel(), jsonObject);
    }

    public String getId() {
        return "ServerStatusPacket";
    }
}
