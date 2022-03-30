package net.brutewars.worldpvp.commands.world;

import net.brutewars.worldpvp.BWorldPlugin;
import net.brutewars.worldpvp.commands.IPermissibleCommand;
import net.brutewars.worldpvp.config.Lang;
import net.brutewars.worldpvp.player.BPlayer;
import net.brutewars.worldpvp.world.BWorld;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public final class CmdLeave implements IPermissibleCommand {
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
        return "leave";
    }

    @Override
    public String getDescription() {
        return "Leave the world.";
    }

    @Override
    public int getMinArgs() {
        return 1;
    }

    @Override
    public int getMaxArgs() {
        return 1;
    }

    @Override
    public boolean canBeExecutedByConsole() {
        return false;
    }

    @Override
    public Consumer<BPlayer> getPermissionLackAction() {
        return Lang.OWNER_FAIL_LEAVE::send;
    }

    @Override
    public Predicate<BPlayer> getPredicate() {
        return bPlayer -> !bPlayer.getRank().isOwner();
    }

    @Override
    public void execute(BWorldPlugin plugin, BPlayer bPlayer, BWorld bWorld, String[] args) {
        bWorld.removePlayer(bPlayer);
        Lang.MEMBER_LEAVE.send(bWorld, bPlayer.getName());
        Lang.LEFT_WORLD.send(bPlayer, bWorld.getOwner().getName());
    }

}