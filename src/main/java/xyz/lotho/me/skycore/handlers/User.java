package xyz.lotho.me.skycore.handlers;

import com.google.gson.JsonObject;
import xyz.lotho.me.skycore.SkyCore;
import xyz.lotho.me.skycore.utils.Tasks;

import java.sql.SQLException;
import java.util.UUID;

public class User {

    private final SkyCore instance;

    private int id;
    private UUID uuid;
    private String rank;
    private long firstLogin;

    boolean loaded;

    public User(SkyCore instance, UUID uuid) {
        this.instance = instance;

        this.id = 0;
        this.uuid = uuid;
        this.rank = null;
        this.firstLogin = System.currentTimeMillis();

        this.loaded = false;
    }

    public void load() {
        this.instance.sql.select("SELECT id, `rank`, firstLogin FROM skycore_users WHERE uuid=?", loadSet -> {
            try {
                if (loadSet.next()) {
                    System.out.println("hey");
                    this.id = loadSet.getInt("id");
                    this.rank = loadSet.getString("rank");
                    this.firstLogin = loadSet.getLong("firstLogin");
                } else {
                    this.instance.sql.select("SELECT max(id) FROM skycore_users", createSet -> {
                        try {
                            if (createSet.next()) {
                                System.out.println("hey2");
                                int entries = createSet.getInt(1);
                                this.instance.sql.execute("INSERT INTO skycore_users (id, uuid, `rank`, firstLogin) VALUES (?, ?, ?, ?)",
                                        entries + 1, this.uuid.toString(), this.instance.rankManager.getDefaultRank().getName(), this.firstLogin
                                );

                                Tasks.runAsyncLater(this.instance, this::load, 20L);
                            }
                        } catch (SQLException exception) {
                            exception.printStackTrace();
                        }
                    });
                }
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        }, this.uuid.toString());

        this.setLoaded(true);
    }

    public void update(JsonObject jsonObject) {
        // todo: add;
    }

    public void save() {
        if (this.rank == null) this.rank = this.instance.rankManager.getDefaultRank().getName();

        this.instance.sql.execute("UPDATE skycore_users SET `rank`=?, firstLogin=? WHERE uuid=?",
                this.rank, this.firstLogin, this.uuid.toString()
        );
    }

    public boolean isLoaded() {
        return this.loaded;
    }

    public void setLoaded(boolean loaded) {
        this.loaded = loaded;
    }

    public int getID () {
        return this.id;
    }

    public void setID(int id) {
        this.id = id;
    }

    public UUID getUUID() {
        return this.uuid;
    }

    public void setUUID(UUID uuid) {
        this.uuid = uuid;
    }

    public long getFirstLogin() {
        return this.firstLogin;
    }

    public void setFirstLogin(long firstLogin) {
        this.firstLogin = firstLogin;
    }
}
