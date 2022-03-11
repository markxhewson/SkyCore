package xyz.lotho.me.skycore.utils;

import org.bukkit.scheduler.BukkitTask;
import xyz.lotho.me.skycore.SkyCore;

public class Tasks {

    public static void run(SkyCore instance, Callable callable) {
        if (!instance.isEnabled()) {
            callable.call();
            return;
        }
        instance.getServer().getScheduler().runTask(instance, callable::call);
    }

    public static void runAsync(SkyCore instance, Callable callable) {
        if (!instance.isEnabled()) {
            callable.call();
            return;
        }
        instance.getServer().getScheduler().runTaskAsynchronously(instance, callable::call);
    }

    public static void runLater(SkyCore instance, Callable callable, long delay) {
        if (!instance.isEnabled()) {
            callable.call();
            return;
        }
        instance.getServer().getScheduler().runTaskLater(instance, callable::call, delay);
    }

    public static BukkitTask runAsyncLater(SkyCore instance, Callable callable, long delay) {
        if (!instance.isEnabled()) {
            callable.call();
            return null;
        }
        return instance.getServer().getScheduler().runTaskLaterAsynchronously(instance, callable::call, delay);
    }

    public static void runTimer(SkyCore instance, Callable callable, long delay, long interval) {
        if (!instance.isEnabled()) {
            callable.call();
            return;
        }
        instance.getServer().getScheduler().runTaskTimer(instance, callable::call, delay, interval);
    }

    public static void runAsyncTimer(SkyCore instance, Callable callable, long delay, long interval) {
        if (!instance.isEnabled()) {
            callable.call();
            return;
        }
        instance.getServer().getScheduler().runTaskTimerAsynchronously(instance, callable::call, delay, interval);
    }

    public interface Callable {

        void call();
    }
}
