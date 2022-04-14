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

    public static void sync(final BWorldPlugin plugin, Consumer<Void> consumer) {
        new BukkitRunnable() {
            @Override
            public void run() {
                consumer.accept(null);
            }
        }.runTaskLater(plugin, 0L);
    }

    public static int syncTimer(final BWorldPlugin plugin, Consumer<Void> consumer, final long delay) {
        return new BukkitRunnable() {
            @Override
            public void run() {
                consumer.accept(null);
            }
        }.runTaskLater(plugin, delay * 20L).getTaskId();
    }

    public static ComplexTask<Void> create() {
        return new ComplexTask<>();
    }

    public static final class ComplexTask<T> {
        private final CompletableFuture<T> completableFuture = new CompletableFuture<>();

        private ComplexTask() {
        }

        public void sync(final BWorldPlugin plugin, Consumer<T> consumer) {
            Executor.async(plugin, unused -> completableFuture.whenComplete((t, throwable) -> Executor.sync(plugin, unused1 -> consumer.accept(t))));
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