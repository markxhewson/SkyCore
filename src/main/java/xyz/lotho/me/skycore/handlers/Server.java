package xyz.lotho.me.skycore.handlers;

import com.google.gson.JsonObject;

import java.text.DecimalFormat;

public class Server {

    private String serverName;
    private String version;
    private float tps1;
    private float tps2;
    private float tps3;
    private int onlinePlayers;
    private int maxPlayers;
    private boolean online;
    private boolean whitelisted;
    private long lastUpdated;

    public void update(JsonObject jsonObject) {
        this.setServerName(jsonObject.get("serverName").getAsString());
        this.setOnlinePlayers(jsonObject.get("onlinePlayers").getAsInt());
        this.setMaxPlayers(jsonObject.get("maxPlayers").getAsInt());
        this.setVersion(jsonObject.get("version").getAsString());
        this.setWhitelisted(jsonObject.get("whitelisted").getAsBoolean());
        this.setTps1(jsonObject.get("tps1").getAsFloat());
        this.setTps2(jsonObject.get("tps2").getAsFloat());
        this.setTps3(jsonObject.get("tps3").getAsFloat());
        this.setLastUpdated(jsonObject.get("lastUpdated").getAsLong());
        this.setOnline(jsonObject.get("online").getAsBoolean());
    }

    public String getLastUpdatedInSeconds() {
        return new DecimalFormat("#.##").format((float) (System.currentTimeMillis() - this.lastUpdated) / 1000);
    }

    public String getServerName() {
        return this.serverName;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public float getTps1() {
        return tps1;
    }

    public void setTps1(float tps1) {
        this.tps1 = tps1;
    }

    public float getTps2() {
        return tps2;
    }

    public void setTps2(float tps2) {
        this.tps2 = tps2;
    }

    public float getTps3() {
        return tps3;
    }

    public void setTps3(float tps3) {
        this.tps3 = tps3;
    }

    public int getOnlinePlayers() {
        return onlinePlayers;
    }

    public void setOnlinePlayers(int onlinePlayers) {
        this.onlinePlayers = onlinePlayers;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public boolean isOnline() {
        return online;
    }

    public boolean isWhitelisted() {
        return whitelisted;
    }

    public void setWhitelisted(boolean whitelisted) {
        this.whitelisted = whitelisted;
    }

    public long getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}
