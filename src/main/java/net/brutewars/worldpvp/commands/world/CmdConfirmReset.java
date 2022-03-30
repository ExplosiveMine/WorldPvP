package net.brutewars.worldpvp.commands.world;

import net.brutewars.worldpvp.BWorldPlugin;
import net.brutewars.worldpvp.commands.IPermissibleCommand;
import net.brutewars.worldpvp.config.Lang;
import net.brutewars.worldpvp.player.BPlayer;
import net.brutewars.worldpvp.utils.StringUtils;
import net.brutewars.worldpvp.world.BWorld;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public final class CmdConfirmReset implements IPermissibleCommand {
    @Override
    public List<String> getAliases() {
        return Collections.singletonList("confirm");
    }

    @Override
    public String getPermission() {
        return "";
    }

    @Override
    public String getUsage() {
        return "";
    }

    @Override
    public String getDescription() {
        return "";
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
    public boolean displayCommand() {
        return false;
    }

    @Override
    public Consumer<BPlayer> getPermissionLackAction() {
        return Lang.NO_PERMISSION_RESET::send;
    }

    @Override
    public Predicate<BPlayer> getPredicate() {
        return bPlayer -> bPlayer.getRank().isOwner();
    }

    @Override
    public void execute(BWorldPlugin plugin, BPlayer bPlayer, BWorld bWorld, String[] args) {
        if (bWorld.getResetting() != -1) {
            Lang.RESET_SUCCESS.send(bWorld, bPlayer.getName());
            plugin.getBWorldManager().removeBWorld(bWorld);
        } else {
            Lang.PLAYER_NO_PERMISSION.send(bPlayer);
        }
    }

}