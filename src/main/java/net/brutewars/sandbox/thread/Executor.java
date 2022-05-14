package net.brutewars.sandbox.thread;

import net.brutewars.sandbox.BWorldPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class Executor {
    public static void async(final BWorldPlugin plugin, Consumer<Void> consumer) {
        new BukkitRunnable() {
            @Override
            public void run() {
                consumer.accept(null);
            }
        }.runTaskAsynchronously(plugin);
    }

    public static int sync(final BWorldPlugin plugin, Consumer<BukkitRunnable> consumer, final long...args) {
        final BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {
                consumer.accept(this);
            }
        };

        if (args.length == 0)
            runnable.runTaskLater(plugin, 0L);
        else if (args.length == 1)
            runnable.runTaskLater(plugin, args[0] * 20L);
        else if (args.length == 2)
            runnable.runTaskTimer(plugin, args[0] * 20L, args[1]);

        return runnable.getTaskId();
    }

    public static ComplexTask<Void> create() {
        return new ComplexTask<>();
    }

    public static final class ComplexTask<T> {
        private final CompletableFuture<T> completableFuture = new CompletableFuture<>();

        private ComplexTask() {
        }

        public void sync(final BWorldPlugin plugin, Consumer<T> consumer, long...args) {
            Executor.async(plugin, unused -> completableFuture.whenComplete((t, throwable) -> Executor.sync(plugin, unused1 -> consumer.accept(t), args)));
        }

        public ComplexTask<Void> async(final BWorldPlugin plugin, Consumer<T> consumer) {
            Executor.async(plugin, unused -> completableFuture.whenComplete((t, throwable) -> consumer.accept(t)));
            return new ComplexTask<>();
        }

        public <R> ComplexTask<R> async(final BWorldPlugin plugin, Supplier<R> supplier) {
            final ComplexTask<R> task = new ComplexTask<>();
            Executor.async(plugin, unused -> task.completableFuture.complete(supplier.get()));
            return task;
        }
    }

}