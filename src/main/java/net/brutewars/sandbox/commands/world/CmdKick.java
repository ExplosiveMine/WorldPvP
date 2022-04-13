package net.brutewars.sandbox.commands.world;

import net.brutewars.sandbox.BWorldPlugin;
import net.brutewars.sandbox.commands.CommandArguments;
import net.brutewars.sandbox.commands.CommandTabCompletes;
import net.brutewars.sandbox.commands.ICommand;
import net.brutewars.sandbox.config.Lang;
import net.brutewars.sandbox.player.BPlayer;
import net.brutewars.sandbox.bworld.BWorld;
import net.brutewars.sandbox.utils.Pair;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;


public final class CmdKick implements ICommand {
    @Override
    public List<String> getAliases() {
        return Collections.singletonList("kick");
    }

    @Override
    public String getPermission() {
        return "";
    }

    @Override
    public String getUsage() {
        return "kick <player-name>";
    }

    @Override
    public String getDescription() {
        return "Kick a player from your world.";
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
        final Pair<BWorld, BPlayer> pair = CommandArguments.getPair(plugin, sender);

        final BPlayer owner = pair.getValue();
        final BWorld bWorld = pair.getKey();

        if (bWorld == null)
            return;

        final BPlayer toBeKicked = CommandArguments.getBPlayer(plugin, sender, args[1]);
        if (toBeKicked == null)
            return;

        if (toBeKicked.getUuid().equals(owner.getUuid())) {
            Lang.CANNOT_KICK_YOURSELF.send(owner);
            return;
        }

        if (!toBeKicked.isInBWorld(bWorld, false)) {
            Lang.PLAYER_NOT_IN_WORLD.send(owner);
            return;
        }

        bWorld.removePlayer(toBeKicked);

        Lang.OWNER_KICK_PLAYER.send(bWorld, toBeKicked.getName());
        Lang.PLAYER_KICKED.send(toBeKicked, owner.getName());
    }

    @Override
    public List<String> tabComplete(BWorldPlugin plugin, CommandSender sender, String[] args) {
        return CommandTabCompletes.getPlayersToKick(plugin, sender);
    }

}