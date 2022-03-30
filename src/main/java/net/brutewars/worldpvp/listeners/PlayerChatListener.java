package net.brutewars.worldpvp.listeners;

import net.brutewars.worldpvp.BWorldPlugin;
import net.brutewars.worldpvp.config.Lang;
import net.brutewars.worldpvp.player.BPlayer;
import net.brutewars.worldpvp.player.PlayerChat;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public final class PlayerChatListener implements Listener {
    private final BWorldPlugin plugin;

    public PlayerChatListener(final BWorldPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true)
    public void onChat(AsyncPlayerChatEvent event) {
        final Player player = event.getPlayer();
        final BPlayer bPlayer = plugin.getBPlayerManager().getBPlayer(player);

        if (bPlayer == null)
            return;

        if (bPlayer.getPlayerChat().equals(PlayerChat.GLOBAL))
            return;

        if (bPlayer.getBWorld() == null) {
            bPlayer.setPlayerChat(PlayerChat.GLOBAL);
            return;
        }

        if (bPlayer.getPlayerChat().equals(PlayerChat.WORLD))
            Lang.WORLD_CHAT_FORMAT.send(bPlayer.getBWorld(), player.getName(), bPlayer.getRank().getName(), event.getMessage());

        event.setCancelled(true);
    }

}