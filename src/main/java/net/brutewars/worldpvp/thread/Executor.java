package net.brutewars.worldpvp.thread;

import net.brutewars.worldpvp.BWorldPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public final class Executor {
    public static int async(final BWorldPlugin plugin, BukkitRunnable runnable) {
        runnable.runTaskAsynchronously(plugin);
        return runnable.getTaskId();
    }

    public static int sync(final BWorldPlugin plugin, BukkitRunnable runnable) {
        runnable.runTaskLater(plugin, 1L);
        return runnable.getTaskId();
    }

    public static int syncTimer(final BWorldPlugin plugin, BukkitRunnable runnable, final long delay) {
        runnable.runTaskLater(plugin, delay * 20L);
        return runnable.getTaskId();
    }

    public static void asyncThenSync(final BWorldPlugin plugin, BukkitRunnable runnable, Consumer<Void> andThen) {
        new CompletableFuture<Void>().whenComplete((unused, throwable) -> Executor.async(plugin, runnable)).thenAccept(andThen);
    }

}