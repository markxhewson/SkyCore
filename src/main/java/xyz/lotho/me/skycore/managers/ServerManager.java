package xyz.lotho.me.skycore.managers;

import com.google.gson.JsonObject;
import xyz.lotho.me.skycore.SkyCore;

import java.util.HashMap;

public class ServerManager {

    private final SkyCore instance;

    private HashMap<String, JsonObject> servers = new HashMap<>();

    public ServerManager(SkyCore instance) {
        this.instance = instance;
    }

    public HashMap<String, JsonObject> getServers() {
        return this.servers;
    }

    public JsonObject getServer(String serverName) {
        return this.getServers().get(serverName);
    }

    public boolean serverExists(String serverName) {
        return this.servers.containsKey(serverName);
    }

    public void addServer(String serverName) {
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("serverName", "");
        jsonObject.addProperty("onlinePlayers", 0);
        jsonObject.addProperty("maxPlayers", 0);
        jsonObject.addProperty("spigotVersion", "");
        jsonObject.addProperty("tps1", 0);
        jsonObject.addProperty("tps2", 0);
        jsonObject.addProperty("tps3", 0);
        jsonObject.addProperty("whitelisted", false);
        jsonObject.addProperty("lastUpdated", System.currentTimeMillis());
        jsonObject.addProperty("online", false);

        this.servers.put(serverName, jsonObject);
    }

    public void setServerOnlineStatus(String serverName, boolean serverStatus) {
        JsonObject jsonObject = this.getServer(serverName);
        jsonObject.addProperty("online", serverStatus);
    }

    public void updateServer(String serverName, JsonObject newJsonObject) {
        if (!this.serverExists(serverName)) this.addServer(serverName);

        JsonObject serverData = this.getServers().get(serverName).getAsJsonObject();

        serverData.addProperty("serverName", newJsonObject.get("serverName").getAsString());
        serverData.addProperty("onlinePlayers", newJsonObject.get("onlinePlayers").getAsInt());
        serverData.addProperty("maxPlayers", newJsonObject.get("maxPlayers").getAsInt());
        serverData.addProperty("spigotVersion", newJsonObject.get("spigotVersion").getAsString());
        serverData.addProperty("whitelisted", newJsonObject.get("whitelisted").getAsBoolean());
        serverData.addProperty("tps1", newJsonObject.get("tps1").getAsFloat());
        serverData.addProperty("tps2", newJsonObject.get("tps2").getAsFloat());
        serverData.addProperty("tps3", newJsonObject.get("tps3").getAsFloat());
        serverData.addProperty("lastUpdated", newJsonObject.get("lastUpdated").getAsLong());
        serverData.addProperty("online", newJsonObject.get("online").getAsBoolean());
    }
}
