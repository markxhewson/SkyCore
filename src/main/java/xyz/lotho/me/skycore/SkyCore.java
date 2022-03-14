package xyz.lotho.me.skycore;

import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.lotho.me.skycore.commands.ServerManagerCommand;
import xyz.lotho.me.skycore.data.DataUpdateThread;
import xyz.lotho.me.skycore.handlers.User;
import xyz.lotho.me.skycore.listeners.PlayerListener;
import xyz.lotho.me.skycore.managers.RankManager;
import xyz.lotho.me.skycore.managers.ServerManager;
import xyz.lotho.me.skycore.managers.UserManager;
import xyz.lotho.me.skycore.storage.redis.RedisManager;
import xyz.lotho.me.skycore.storage.redis.impl.server.ServerUpdatePacket;
import xyz.lotho.me.skycore.storage.sql.MySQL;
import xyz.lotho.me.skycore.storage.sql.utils.SQL;
import xyz.lotho.me.skycore.utils.Config;
import xyz.lotho.me.skycore.utils.Tasks;
import xyz.lotho.me.skycore.utils.Utilities;

public final class SkyCore extends JavaPlugin {

    public final Config config = new Config(this, "config.yml");
    public final Config ranks = new Config(this, "ranks.yml");
    public final Config lang = new Config(this, "lang.yml");

    public RedisManager redisManager;

    public final RankManager rankManager = new RankManager(this);
    public final ServerManager serverManager = new ServerManager(this);
    public final UserManager userManager = new UserManager(this);

    private DataUpdateThread dataUpdateThread;

    public MySQL mySQL;
    public SQL sql;

    boolean disabling = false;

    @Override
    public void onEnable() {
        Utilities.log("Server is starting.");

        registerCommands();
        registerListeners();

        this.mySQL = new MySQL(this);
        this.sql = new SQL(this);

        this.sql.createTable("skycore_users", "id INTEGER PRIMARY KEY NOT NULL, uuid VARCHAR(36) NOT NULL, `rank` TEXT NOT NULL, firstLogin BIGINT NOT NULL");
        this.sql.createTable("skycore_ranks", "id INTEGER PRIMARY KEY NOT NULL, name VARCHAR(20) NOT NULL, prefix TEXT NOT NULL, `default` TINYINT(1) NOT NULL, weight int NOT NULL, `type` VARCHAR(10) NOT NULL");
        this.sql.createTable("skycore_rank_permissions", "id INTEGER PRIMARY KEY NOT NULL REFERENCES skycore_ranks, permission TEXT NOT NULL");

        Tasks.runAsyncLater(this, rankManager::loadRanks, 50L);

        this.redisManager = new RedisManager(this);
        this.redisManager.connect();

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("serverName", this.config.get().getString("server.name"));
        jsonObject.addProperty("online", true);

        new ServerUpdatePacket(this).send(jsonObject);

        this.getServer().getOnlinePlayers().forEach((player) -> {
            this.userManager.handleUserCreation(player.getUniqueId());
            User user = this.userManager.getUser(player.getUniqueId());

            System.out.println(user);

            Tasks.runAsync(this, user::load);
        });

        this.dataUpdateThread = new DataUpdateThread(this);
    }

    @Override
    public void onDisable() {
        this.disabling = true;

        this.mySQL.disconnect();

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("serverName", this.config.get().getString("server.name"));
        jsonObject.addProperty("online", false);

        new ServerUpdatePacket(this).send(jsonObject);

        this.redisManager.close();
    }

    public void registerCommands() {
        this.getCommand("servermanager").setExecutor(new ServerManagerCommand(this));
    }

    public void registerListeners() {
        this.getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
    }

    public boolean isDisabling() {
        return this.disabling;
    }

}
