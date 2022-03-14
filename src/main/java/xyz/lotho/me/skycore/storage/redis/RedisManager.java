package xyz.lotho.me.skycore.storage.redis;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisPubSub;
import xyz.lotho.me.skycore.SkyCore;
import xyz.lotho.me.skycore.storage.redis.impl.player.GlobalPlayerUpdatePacket;
import xyz.lotho.me.skycore.storage.redis.impl.server.ServerCommandPacket;
import xyz.lotho.me.skycore.storage.redis.impl.server.ServerStatusUpdatePacket;
import xyz.lotho.me.skycore.storage.redis.impl.server.ServerUpdatePacket;
import xyz.lotho.me.skycore.utils.Utilities;

import java.util.concurrent.ForkJoinPool;

public class RedisManager {

    private final SkyCore instance;

    private String password;
    private boolean auth;

    private String channel;
    private JedisPool subscriberPool, publisherPool;

    private long lastConnect = -1;

    private JedisPubSub pubsub;
    private final JsonParser PARSER = new JsonParser();

    public RedisManager(SkyCore instance) {
        this.instance = instance;
    }

    public void connect() {
        String host = this.instance.config.get().getString("redis.host");
        int port = this.instance.config.get().getInt("redis.port");
        this.password = this.instance.config.get().getString("redis.password");
        this.auth = this.password != null && this.password.length() > 0;

        Utilities.log("[REDIS] Attempting to connect to Redis Database (" + host + ":" + port + ")");
        Utilities.adminLog(this.instance, "&c[ADMIN] &a[REDIS] &eAttempting to connect to Redis Database &7(" + host + ":" + port + ")");

        this.channel = this.instance.config.get().getString("redis.channel");

        try {
            this.subscriberPool = this.publisherPool = new JedisPool(new JedisPoolConfig(), host, port, 30_000, this.password.isEmpty() ? null : this.password, 0, null);
            this.subscriberPool.getResource();

            this.setupPubSub();

            try {
                Thread.sleep(1500L);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception exception) {
            this.subscriberPool = null;
            this.publisherPool = null;

            exception.printStackTrace();
        }

        if (this.isRedisConnected()) {
            Utilities.log("[REDIS] Redis is now connected and sync is ready.");

            this.lastConnect = System.currentTimeMillis();
        }
    }

    public void setupPubSub() {
        this.pubsub = new JedisPubSub() {
            @Override
            public void onMessage(String channel, String message) {
                if (!channel.equalsIgnoreCase(RedisManager.this.channel)) return;
                JsonObject jsonObject = new JsonObject();

                try {
                    jsonObject = PARSER.parse(message).getAsJsonObject();
                } catch (JsonSyntaxException exception) {
                    exception.printStackTrace();
                }

                String id = jsonObject.get("id").getAsString();

                if (id.equals("ServerStatusPacket")) {
                    new ServerStatusUpdatePacket(RedisManager.this.instance).receive(jsonObject);
                }
                if (id.equals("ServerUpdateStatusPacket")) {
                    new ServerUpdatePacket(RedisManager.this.instance).receive(jsonObject);
                }
                if (id.equals("ServerCommandPacket")) {
                    new ServerCommandPacket(RedisManager.this.instance).receive(jsonObject);
                }
                if (id.equals("GlobalPlayerUpdatePacket")) {
                    new GlobalPlayerUpdatePacket(RedisManager.this.instance).receive(jsonObject);
                }

            }
        };

        ForkJoinPool.commonPool().execute(() -> {
            try (Jedis jedis = this.subscriberPool.getResource()) {
                jedis.subscribe(this.pubsub, this.channel);
            } catch (Exception exception) {
                exception.printStackTrace();

                this.subscriberPool = null;
                this.publisherPool = null;
            }
        });
    }

    public void send(String channel, JsonObject object) {
        try {
            if (object == null) throw new IllegalStateException("Object being passed into pool was null.");

            try (Jedis jedis = this.publisherPool.getResource()) {
                if (this.auth) jedis.auth(this.password);

                jedis.publish(channel, object.toString());
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public String getChannel() {
        return this.channel;
    }

    public boolean isRedisConnected() {
        return this.subscriberPool != null && !this.subscriberPool.isClosed() && this.publisherPool != null && !this.publisherPool.isClosed();
    }

    public void close() {
        try {
            this.subscriberPool.close();
            this.publisherPool.close();
            this.pubsub = null;
            this.channel = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
