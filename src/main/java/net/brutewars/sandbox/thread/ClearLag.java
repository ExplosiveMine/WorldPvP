package net.brutewars.sandbox.thread;

import net.brutewars.sandbox.BWorldPlugin;
import net.brutewars.sandbox.config.parser.Lang;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.Duration;

public final class ClearLag extends BukkitRunnable {
    private final BWorldPlugin plugin;
    private long countdown;
    private long interval;

    public ClearLag(BWorldPlugin plugin) {
        this.plugin = plugin;
    }

    public void startThread() {
        interval = plugin.getConfigSettings().getConfigParser().getClearLagInterval();
        countdown = interval;

        runTaskTimer(plugin,  60 * 20L, 20L);
    }

    @Override
    public void run() {
        if (--countdown == 0) {
            plugin.getServer().getWorlds().forEach(world -> world.getEntities().stream()
                            .filter(entity -> entity instanceof Item).forEach(Entity::remove));

            plugin.getServer().getOnlinePlayers().forEach(player -> {
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 2);
                player.sendActionBar(Component.text(Lang.CLEAR_LAG_ACTIONBAR.get()));
                player.showTitle(
                        Title.title(Component.text(" "),
                        Component.text(Lang.CLEAR_LAG_SUBTITLE.get()),
                        Title.Times.times(Duration.ofMillis(750), Duration.ofSeconds(1), Duration.ofMillis(500)))
                );
            });
            countdown = interval;
        } else if (countdown == 15) {
            plugin.getServer().getOnlinePlayers().forEach(player -> {
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 0);
                player.sendActionBar(Component.text(Lang.CLEAR_LAG_ACTIONBAR_15.get()));
                player.showTitle(
                        Title.title(Component.text(" "),
                                Component.text(Lang.CLEAR_LAG_SUBTITLE_15.get()),
                                Title.Times.times(Duration.ofMillis(750), Duration.ofSeconds(1), Duration.ofMillis(500)))
                );
            });
        } else if (countdown > 0 && countdown <= 3) {
            plugin.getServer().getOnlinePlayers().forEach(player -> {
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 2);
                player.sendActionBar(Component.text(Lang.valueOf("CLEAR_LAG_ACTIONBAR_" + countdown).get()));
            });
        }
    }

}