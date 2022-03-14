package xyz.lotho.me.skycore.managers;

import xyz.lotho.me.skycore.SkyCore;
import xyz.lotho.me.skycore.handlers.Server;

import java.util.HashMap;
import java.util.Map;

public class ServerManager {

    private final SkyCore instance;

    private Map<String, Server> serversMap = new HashMap<>();

    public ServerManager(SkyCore instance) {
        this.instance = instance;
    }

    public Map<String, Server> getServers() {
        return this.serversMap;
    }

    public Server getServer(String serverName) {
        return this.getServers().get(serverName);
    }

    public boolean serverExists(String serverName) {
        return this.serversMap.containsKey(serverName);
    }

    public void addServer(String serverName) {
        this.serversMap.put(serverName, new Server());
    }
}
