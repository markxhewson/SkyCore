package xyz.lotho.me.skycore.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import xyz.lotho.me.skycore.SkyCore;
import xyz.lotho.me.skycore.handlers.User;
import xyz.lotho.me.skycore.utils.Tasks;

public class PlayerListener implements Listener {

    private SkyCore instance;

    public PlayerListener(SkyCore instance) {
        this.instance = instance;
    }

    @EventHandler
    public void handleAsyncLogin(AsyncPlayerPreLoginEvent event) {
        this.instance.userManager.handleUserCreation(event.getUniqueId());

        User user = this.instance.userManager.getUser(event.getUniqueId());
        Tasks.runAsync(this.instance, user::load);
    }
}
