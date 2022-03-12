package xyz.lotho.me.skycore.managers;

import com.google.gson.JsonObject;
import xyz.lotho.me.skycore.SkyCore;
import xyz.lotho.me.skycore.handlers.Server;

import java.util.HashMap;

public class ServerManager {

    private final SkyCore instance;

    private HashMap<String, Server> servers = new HashMap<>();

    public ServerManager(SkyCore instance) {
        this.instance = instance;
    }

    public HashMap<String, Server> getServers() {
        return this.servers;
    }

    public Server getServer(String serverName) {
        return this.getServers().get(serverName);
    }

    public boolean serverExists(String serverName) {
        return this.servers.containsKey(serverName);
    }

    public void addServer(String serverName) {
        this.servers.put(serverName, new Server(this.instance));
    }

    public void updateServer(String serverName, JsonObject jsonObject) {
        if (!this.serverExists(serverName)) this.addServer(serverName);

        Server server = this.getServers().get(serverName);
        server.update(jsonObject);
    }
}
