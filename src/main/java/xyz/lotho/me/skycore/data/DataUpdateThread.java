package xyz.lotho.me.skycore.data;

import com.google.gson.JsonObject;
import xyz.lotho.me.skycore.SkyCore;
import xyz.lotho.me.skycore.managers.TPSManager;
import xyz.lotho.me.skycore.storage.redis.impl.player.GlobalPlayerUpdatePacket;
import xyz.lotho.me.skycore.storage.redis.impl.server.ServerStatusUpdatePacket;

public class DataUpdateThread extends Thread {

    private final SkyCore instance;

    public DataUpdateThread(SkyCore instance) {
        this.instance = instance;

        this.start();
    }

    @Override
    public void run() {
        while (true) {
            try {
                check();
            } catch (Exception exception) {
                exception.printStackTrace();
            }

            try {
                sleep(3000);
            } catch (InterruptedException exception) {
                exception.printStackTrace();
            }
        }
    }

    public void check() {
        if (this.instance.isDisabling()) return;

        JsonObject serverJson = new JsonObject();
        serverJson.addProperty("serverName", this.instance.config.get().getString("server.name"));
        serverJson.addProperty("onlinePlayers", this.instance.getServer().getOnlinePlayers().size());
        serverJson.addProperty("maxPlayers", this.instance.getServer().getMaxPlayers());
        serverJson.addProperty("version", this.instance.getServer().getVersion());
        serverJson.addProperty("whitelisted", this.instance.getServer().hasWhitelist());
        serverJson.addProperty("tps1", TPSManager.getRecentTps()[0]);
        serverJson.addProperty("tps2", TPSManager.getRecentTps()[1]);
        serverJson.addProperty("tps3", TPSManager.getRecentTps()[2]);
        serverJson.addProperty("lastUpdated", System.currentTimeMillis());
        serverJson.addProperty("online", true);

        new ServerStatusUpdatePacket(this.instance).send(serverJson);

        this.instance.getServer().getOnlinePlayers().forEach((player) -> {
            JsonObject playerJson = new JsonObject();
            playerJson.addProperty("uuid", player.getUniqueId().toString());
            playerJson.addProperty("firstLogin", System.currentTimeMillis());

            new GlobalPlayerUpdatePacket(this.instance).send(playerJson);
        });
    }
}
