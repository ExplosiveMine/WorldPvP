package net.brutewars.worldpvp.commands.world;

import net.brutewars.worldpvp.BWorldPlugin;
import net.brutewars.worldpvp.commands.CommandArguments;
import net.brutewars.worldpvp.commands.CommandTabCompletes;
import net.brutewars.worldpvp.commands.IPermissibleCommand;
import net.brutewars.worldpvp.config.Lang;
import net.brutewars.worldpvp.player.BPlayer;
import net.brutewars.worldpvp.world.BWorld;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class CmdKick implements IPermissibleCommand {
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
    public Predicate<BPlayer> getPredicate() {
        return bPlayer -> bPlayer.getRank().isOwner();
    }

    @Override
    public Consumer<BPlayer> getPermissionLackAction() {
        return Lang.NO_PERMISSION_KICK::send;
    }

    @Override
    public void execute(BWorldPlugin plugin, BPlayer owner, BWorld bWorld, String[] args) {
        final BPlayer toBeKicked = CommandArguments.getBPlayer(plugin, owner, args[1]);

        if (toBeKicked == null)
            return;

        if (toBeKicked.getUuid().equals(owner.getUuid())) {
            Lang.CANNOT_KICK_YOURSELF.send(owner);
            return;
        }

        if (!bWorld.getPlayers(false).contains(toBeKicked)) {
            Lang.PLAYER_NOT_IN_WORLD.send(owner);
            return;
        }

        bWorld.removePlayer(toBeKicked);

        Lang.OWNER_KICK_PLAYER.send(bWorld, toBeKicked.getName());
        Lang.PLAYER_KICKED.send(toBeKicked, owner.getName());
    }

    @Override
    public List<String> tabComplete(BWorldPlugin plugin, BPlayer bPlayer, BWorld bWorld, String[] args) {
        return CommandTabCompletes.getPlayersToKick(bWorld);
    }

}