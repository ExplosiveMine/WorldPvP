package net.brutewars.sandbox.commands;

import net.brutewars.sandbox.BWorldPlugin;
import net.brutewars.sandbox.player.BPlayer;
import net.brutewars.sandbox.bworld.BWorld;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

public final class CommandTabCompletes {
    private CommandTabCompletes() { }

    public static List<String> getWorldInvitations(BWorldPlugin plugin, CommandSender sender) {
        final BPlayer invitee = plugin.getBPlayerManager().getBPlayer((Player) sender);
        return plugin.getBWorldManager().getBWorlds().stream()
                .filter(bWorld -> bWorld.isInvited(invitee))
                .map(bWorld -> bWorld.getOwner().getName())
                .collect(Collectors.toList());
    }

    public static List<String> getOnlinePlayersWithoutWorlds(BWorldPlugin plugin) {
        return plugin.getBPlayerManager().getBPlayers().stream()
                .filter(BPlayer::isOnline)
                .filter(bPlayer -> bPlayer.getBWorld() == null)
                .map(BPlayer::getName)
                .collect(Collectors.toList());
    }

    public static List<String> getPlayersToKick(BWorld bWorld) {
        return bWorld.getPlayers(false).stream()
                .map(BPlayer::getName)
                .collect(Collectors.toList());
    }

}