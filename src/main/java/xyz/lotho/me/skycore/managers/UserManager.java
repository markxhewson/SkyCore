package xyz.lotho.me.skycore.managers;

import xyz.lotho.me.skycore.SkyCore;
import xyz.lotho.me.skycore.handlers.User;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UserManager {

    private final SkyCore instance;
    private final Map<UUID, User> usersMap = new HashMap<>();

    public UserManager(SkyCore instance) {
        this.instance = instance;
    }

    public Map<UUID, User> getUsersMap() {
        return this.usersMap;
    }

    public void handleUserCreation(UUID uuid) {
        if (this.usersMap.containsKey(uuid)) return;

        this.usersMap.put(uuid, new User(this.instance, uuid));
    }

    public User getUser(UUID uuid) {
        if (this.usersMap.get(uuid) == null) this.handleUserCreation(uuid);
        return this.usersMap.get(uuid);
    }

}
