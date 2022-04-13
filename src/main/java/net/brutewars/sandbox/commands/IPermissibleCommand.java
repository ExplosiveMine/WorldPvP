package net.brutewars.sandbox.commands;

import net.brutewars.sandbox.BWorldPlugin;
import net.brutewars.sandbox.config.Lang;
import net.brutewars.sandbox.player.BPlayer;
import net.brutewars.sandbox.utils.Pair;
import net.brutewars.sandbox.bworld.BWorld;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public interface IPermissibleCommand extends ICommand {
    @Override
    default void execute(BWorldPlugin plugin, CommandSender sender, String[] args) {
        BWorld bWorld = null;
        BPlayer bPlayer = null;

        if (!canBeExecutedByConsole() || sender instanceof Player) {
            Pair<BWorld, BPlayer> arguments = CommandArguments.getPair(plugin, sender);

            bWorld = arguments.getKey();

            if (bWorld == null) {
                Lang.PLAYER_NO_WORLD.send(bPlayer);
                return;
            }

            bPlayer = arguments.getValue();
        }

        execute(plugin, bPlayer, bWorld, args);
    }

    @Override
    default List<String> tabComplete(BWorldPlugin plugin, CommandSender sender, String[] args) {
        BWorld bWorld = null;
        BPlayer bPlayer = null;

        if (!canBeExecutedByConsole() || sender instanceof Player) {
            bPlayer = plugin.getBPlayerManager().getBPlayer((Player) sender);
            bWorld = bPlayer.getBWorld();
        }

        return bPlayer == null || (bWorld != null) ? tabComplete(plugin, bPlayer, bWorld, args) : new ArrayList<>();
    }

    void execute(BWorldPlugin plugin, BPlayer bPlayer, BWorld bWorld, String[] args);

    default List<String> tabComplete(BWorldPlugin plugin, BPlayer bPlayer, BWorld bWorld, String[] args) {
        return new ArrayList<>();
    }

}