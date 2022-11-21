package net.brutewars.sandbox.thread;

import net.brutewars.sandbox.BWorldPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class Executor {
    public static void async(BWorldPlugin plugin, Consumer<BukkitRunnable> consumer) {
        new BukkitRunnable() {
            @Override
            public void run() {
                consumer.accept(this);
            }
        }.runTaskAsynchronously(plugin);
    }

    public static int sync(BWorldPlugin plugin, Consumer<BukkitRunnable> consumer, long...args) {
        BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {
                consumer.accept(this);
            }
        };

        if (args.length == 0)
            runnable.runTaskLater(plugin, 1L);
        else if (args.length == 1)
            runnable.runTaskLater(plugin, args[0] * 20L);
        else if (args.length == 2)
            runnable.runTaskTimer(plugin, args[0] * 20L, args[1]);

        return runnable.getTaskId();
    }

    public static ComplexTask<Void> create() {
        ComplexTask<Void> task = new ComplexTask<>();
        task.completableFuture.complete(null);
        return task;
    }

    public static final class ComplexTask<T> {
        private final CompletableFuture<T> completableFuture = new CompletableFuture<>();

        private ComplexTask() {
        }

        public ComplexTask<Void> sync(BWorldPlugin plugin, Consumer<T> consumer, long...args) {
            ComplexTask<Void> task = new ComplexTask<>();
            completableFuture.whenComplete((t, throwable) -> Executor.sync(plugin, runnable -> {
                consumer.accept(t);
                task.completableFuture.complete(null);
            }, args));
            return task;
        }

        public ComplexTask<Void> async(BWorldPlugin plugin, Consumer<T> consumer) {
            ComplexTask<Void> task = new ComplexTask<>();
            completableFuture.whenComplete((t, throwable) -> Executor.async(plugin, unused -> {
                consumer.accept(t);
                task.completableFuture.complete(null);
            }));
            return task;
        }

        public <R> ComplexTask<R> async(BWorldPlugin plugin, Supplier<R> supplier) {
            ComplexTask<R> task = new ComplexTask<>();
            completableFuture.whenComplete((t, throwable) -> Executor.async(plugin, unused -> task.completableFuture.complete(supplier.get())));
            return task;
        }

    }

}