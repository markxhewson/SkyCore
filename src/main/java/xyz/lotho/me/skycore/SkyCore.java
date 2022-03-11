package xyz.lotho.me.skycore;

import com.google.gson.JsonObject;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.lotho.me.skycore.commands.ServerManagerCommand;
import xyz.lotho.me.skycore.data.DataUpdateThread;
import xyz.lotho.me.skycore.managers.ServerManager;
import xyz.lotho.me.skycore.storage.RedisManager;
import xyz.lotho.me.skycore.storage.impl.ServerStatusPacket;
import xyz.lotho.me.skycore.storage.impl.ServerUpdateStatusPacket;
import xyz.lotho.me.skycore.utils.Config;
import xyz.lotho.me.skycore.utils.Utilities;

public final class SkyCore extends JavaPlugin {

    public final Config config = new Config(this, "config.yml");
    public final Config lang = new Config(this, "lang.yml");

    public final ServerManager serverManager = new ServerManager(this);
    public final RedisManager redisManager = new RedisManager(this);

    private DataUpdateThread dataUpdateThread;

    @Override
    public void onEnable() {
        Utilities.log("Server is starting.");

        registerCommands();

        this.redisManager.connect();

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("serverName", this.config.get().getString("server.name"));
        jsonObject.addProperty("online", true);

        new ServerUpdateStatusPacket(this).send(jsonObject);

        this.dataUpdateThread = new DataUpdateThread(this);
    }

    @Override
    public void onDisable() {
        Utilities.log("Server is disabling.");

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("serverName", this.config.get().getString("server.name"));
        jsonObject.addProperty("online", false);

        new ServerUpdateStatusPacket(this).send(jsonObject);

        this.redisManager.close();
    }

    public void registerCommands() {
        this.getCommand("servermanager").setExecutor(new ServerManagerCommand(this));
    }

}
