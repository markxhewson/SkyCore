package xyz.lotho.me.skycore.storage.redis.impl.player;

import com.google.gson.JsonObject;
import xyz.lotho.me.skycore.SkyCore;
import xyz.lotho.me.skycore.handlers.User;
import xyz.lotho.me.skycore.utils.Tasks;

import java.util.UUID;

public class GlobalPlayerUpdatePacket {

    private final SkyCore instance;

    public GlobalPlayerUpdatePacket(SkyCore instance) {
        this.instance = instance;
    }

    public void receive(JsonObject jsonObject) {
        if (this.instance.isDisabling()) return;

        UUID uuid = UUID.fromString(jsonObject.get("uuid").getAsString());

        User user = this.instance.userManager.getUser(uuid);
        if (!user.isLoaded()) Tasks.runAsync(this.instance, user::load);

        user.update(jsonObject);
        Tasks.runAsync(this.instance, user::save);
    }

    public void send(JsonObject jsonObject) {
        jsonObject.addProperty("id", this.getId());
        this.instance.redisManager.send(this.instance.redisManager.getChannel(), jsonObject);
    }

    public String getId() {
        return "GlobalPlayerUpdatePacket";
    }
}
