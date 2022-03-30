package net.brutewars.worldpvp.commands;

import net.brutewars.worldpvp.BWorldPlugin;
import net.brutewars.worldpvp.player.BPlayer;
import net.brutewars.worldpvp.utils.Pair;
import net.brutewars.worldpvp.world.BWorld;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public interface IPermissibleCommand extends ICommand {
    @Override
    default void execute(BWorldPlugin plugin, CommandSender sender, String[] args) {
        BWorld bWorld = null;
        BPlayer bPlayer = null;

        if (!canBeExecutedByConsole() || sender instanceof Player) {
            Pair<BWorld, BPlayer> arguments = CommandArguments.getSenderBWorld(plugin, sender);

            bWorld = arguments.getKey();

            if (bWorld == null) return;

            bPlayer = arguments.getValue();

            if (!getPredicate().test(bPlayer)) {
                getPermissionLackAction().accept(bPlayer);
                return;
            }
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

        return bPlayer == null || (bWorld != null && getPredicate().test(bPlayer)) ? tabComplete(plugin, bPlayer, bWorld, args) : new ArrayList<>();
    }

    Consumer<BPlayer> getPermissionLackAction();

    void execute(BWorldPlugin plugin, BPlayer bPlayer, BWorld bWorld, String[] args);

    default List<String> tabComplete(BWorldPlugin plugin, BPlayer bPlayer, BWorld bWorld, String[] args) {
        return new ArrayList<>();
    }

    Predicate<BPlayer> getPredicate();

}