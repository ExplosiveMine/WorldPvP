package net.brutewars.sandbox.commands;

import net.brutewars.sandbox.BWorldPlugin;
import net.brutewars.sandbox.bworld.BWorld;
import net.brutewars.sandbox.player.BPlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

public final class CommandTabCompletes {
    private CommandTabCompletes() { }

    public static List<String> getWorldInvitations(BWorldPlugin plugin, CommandSender sender) {
        BPlayer invitee = plugin.getBPlayerManager().get((Player) sender);
        return plugin.getBWorldManager().getBWorlds().stream()
                .filter(bWorld -> bWorld.isInvited(invitee))
                .map(BWorld::getAlias)
                .collect(Collectors.toList());
    }

    public static List<String> getWorldsToLeave(BWorldPlugin plugin, CommandSender sender) {
        return plugin.getBPlayerManager().get((Player) sender).getAdditionalBWorlds().stream()
                .map(uuid -> plugin.getBWorldManager().getBWorld(uuid).getAlias())
                .collect(Collectors.toList());
    }

    public static List<String> getOnlinePlayers(BWorldPlugin plugin) {
        return plugin.getServer().getOnlinePlayers().stream()
                .map(Player::getName)
                .collect(Collectors.toList());
    }

    public static List<String> getPlayersToKick(BWorldPlugin plugin, CommandSender sender) {
        return plugin.getBPlayerManager().get((Player) sender).getBWorld().getMembers(false).stream()
                .map(BPlayer::getName)
                .collect(Collectors.toList());
    }

    public static List<String> getBWorlds(BWorldPlugin plugin) {
        return plugin.getBWorldManager().getBWorlds().stream()
                .map(BWorld::getAlias)
                .collect(Collectors.toList());
    }

}