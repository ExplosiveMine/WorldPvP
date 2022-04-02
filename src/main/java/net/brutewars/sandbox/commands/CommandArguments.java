package net.brutewars.sandbox.commands;

import net.brutewars.sandbox.BWorldPlugin;
import net.brutewars.sandbox.config.Lang;
import net.brutewars.sandbox.player.BPlayer;
import net.brutewars.sandbox.utils.Pair;
import net.brutewars.sandbox.bworld.BWorld;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class CommandArguments {
    private CommandArguments() { }

    /**
     * @apiNote This method is to be used only in the case where the sender
     * needs to have a BWorld. Since a message saying that the sender does not have a BWorld
     * is sent if they don't.
     *
     * Use {@link CommandArguments#getBPlayer(BWorldPlugin, CommandSender)} to simply get the player
     * and the BWorld afterwards.
     */
    public static Pair<BWorld, BPlayer> getPair(BWorldPlugin plugin, CommandSender sender) {
        final BPlayer bPlayer = plugin.getBPlayerManager().getBPlayer((Player) sender);
        final BWorld bWorld = bPlayer.getBWorld();

        if (bWorld == null)
            Lang.PLAYER_NO_WORLD.send(sender);

        return new Pair<>(bWorld, bPlayer);
    }

    public static BPlayer getBPlayer(BWorldPlugin plugin, CommandSender sender) {
        return plugin.getBPlayerManager().getBPlayer((Player) sender);
    }

    public static BPlayer getBPlayer(BWorldPlugin plugin, BPlayer bPlayer, String playerName) {
        final Player player = plugin.getServer().getPlayer(playerName);
        if (player == null) {
            Lang.INVALID_PLAYER.send(bPlayer);
            return null;
        }

        return plugin.getBPlayerManager().getBPlayer(player);
    }

    public static BWorld getBWorld(BWorldPlugin plugin, CommandSender sender, String worldOwnerName) {
        final BWorld bWorld = plugin.getBPlayerManager().getBPlayer(worldOwnerName).getBWorld();
        if (bWorld == null)
            Lang.INVALID_WORLD.send(sender);

        return bWorld;
    }

}