package net.brutewars.sandbox.commands.world;

import net.brutewars.sandbox.BWorldPlugin;
import net.brutewars.sandbox.commands.CommandArguments;
import net.brutewars.sandbox.commands.CommandTabCompletes;
import net.brutewars.sandbox.commands.ICommand;
import net.brutewars.sandbox.config.parser.Lang;
import net.brutewars.sandbox.player.BPlayer;
import net.brutewars.sandbox.bworld.BWorld;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public final class CmdLeave implements ICommand {
    @Override
    public List<String> getAliases() {
        return Collections.singletonList("leave");
    }

    @Override
    public String getPermission() {
        return "";
    }

    @Override
    public String getUsage() {
        return "leave <player-name>";
    }

    @Override
    public String getDescription() {
        return "Leave another player's world.";
    }

    @Override
    public int getMinArgs() {
        return 2;
    }

    @Override
    public int getMaxArgs() {
        return 2;
    }

    @Override
    public boolean canBeExecutedByConsole() {
        return false;
    }

    @Override
    public void execute(BWorldPlugin plugin, CommandSender sender, String[] args) {
        final BPlayer bPlayer = plugin.getBPlayerManager().getBPlayer((Player) sender);

        final BPlayer bWorldOwner = CommandArguments.getBPlayer(plugin, sender, args[1]);
        if (bWorldOwner == null)
            return;

        final BWorld leavingBWorld = bWorldOwner.getBWorld();
        if (!bPlayer.isInBWorld(leavingBWorld, false)) {
            Lang.NOT_IN_WORLD.send(bPlayer, bWorldOwner.getName());
            return;
        }

        leavingBWorld.removePlayer(bPlayer);
        Lang.MEMBER_LEAVE.send(leavingBWorld, bPlayer.getName());
        Lang.LEFT_WORLD.send(bPlayer, leavingBWorld.getAlias());
    }

    @Override
    public List<String> tabComplete(BWorldPlugin plugin, CommandSender sender, String[] args) {
        return CommandTabCompletes.getWorldsToLeave(plugin, sender);
    }

}