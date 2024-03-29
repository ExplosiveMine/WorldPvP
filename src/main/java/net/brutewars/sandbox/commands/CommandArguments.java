package net.brutewars.sandbox.commands;

import net.brutewars.sandbox.BWorldPlugin;
import net.brutewars.sandbox.config.parser.Lang;
import net.brutewars.sandbox.player.BPlayer;
import net.brutewars.sandbox.utils.Pair;
import net.brutewars.sandbox.bworld.BWorld;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class CommandArguments {
    private CommandArguments() { }

    /**
     * @apiNote This method is to be used only in the case where the sender
     * needs to have a BWorld. Since a message saying that the sender does not have a BWorld
     * is sent if they don't.
     * <p>
     * Use {@link CommandArguments#getBPlayer(BWorldPlugin, CommandSender)} to simply getMenu the player
     * and the BWorld afterwards.
     */
    public static Pair<BWorld, BPlayer> getPair(BWorldPlugin plugin, CommandSender sender) {
        BPlayer bPlayer = plugin.getBPlayerManager().get((Player) sender);
        BWorld bWorld = bPlayer.getBWorld();

        if (bWorld == null)
            Lang.PLAYER_NO_WORLD.send(sender);

        return new Pair<>(bWorld, bPlayer);
    }

    public static BPlayer getBPlayer(BWorldPlugin plugin, CommandSender sender) {
        return plugin.getBPlayerManager().get((Player) sender);
    }

    public static BPlayer getBPlayer(BWorldPlugin plugin, CommandSender sender, String playerName) {
        Player player = plugin.getServer().getPlayer(playerName);
        if (player == null || !player.isOnline()) {
            Lang.INVALID_PLAYER.send(sender);
            return null;
        }

        return plugin.getBPlayerManager().get(player);
    }

    public static BWorld getBWorld(BWorldPlugin plugin, CommandSender sender, String worldOwnerName) {
        OfflinePlayer player = plugin.getServer().getOfflinePlayer(worldOwnerName);

        if (!player.hasPlayedBefore()) {
            Lang.INVALID_WORLD.send(sender);
            return null;
        }

        BWorld bWorld = plugin.getBPlayerManager().get(player.getUniqueId()).getBWorld();
        if (bWorld == null)
            Lang.INVALID_WORLD.send(sender);

        return bWorld;
    }

}