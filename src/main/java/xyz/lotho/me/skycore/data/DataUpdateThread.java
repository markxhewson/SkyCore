package xyz.lotho.me.skycore.data;

import com.google.gson.JsonObject;
import xyz.lotho.me.skycore.SkyCore;
import xyz.lotho.me.skycore.managers.TPSManager;
import xyz.lotho.me.skycore.storage.impl.ServerStatusPacket;
import xyz.lotho.me.skycore.storage.impl.ServerUpdateStatusPacket;

public class DataUpdateThread extends Thread {

    private SkyCore instance;

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
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("serverName", this.instance.config.get().getString("server.name"));
        jsonObject.addProperty("onlinePlayers", this.instance.getServer().getOnlinePlayers().size());
        jsonObject.addProperty("maxPlayers", this.instance.getServer().getMaxPlayers());
        jsonObject.addProperty("spigotVersion", this.instance.getServer().getVersion());
        jsonObject.addProperty("whitelisted", this.instance.getServer().hasWhitelist());
        jsonObject.addProperty("tps1", TPSManager.getRecentTps()[0]);
        jsonObject.addProperty("tps2", TPSManager.getRecentTps()[1]);
        jsonObject.addProperty("tps3", TPSManager.getRecentTps()[2]);
        jsonObject.addProperty("lastUpdated", System.currentTimeMillis());
        jsonObject.addProperty("online", true);

        new ServerStatusPacket(this.instance).send(jsonObject);
    }
}
