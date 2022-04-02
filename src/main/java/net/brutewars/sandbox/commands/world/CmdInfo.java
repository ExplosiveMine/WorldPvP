package net.brutewars.sandbox.commands.world;

import net.brutewars.sandbox.BWorldPlugin;
import net.brutewars.sandbox.commands.IPermissibleCommand;
import net.brutewars.sandbox.config.Lang;
import net.brutewars.sandbox.player.BPlayer;
import net.brutewars.sandbox.bworld.BWorld;
import net.brutewars.sandbox.utils.Logging;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public final class CmdInfo implements IPermissibleCommand {
    @Override
    public List<String> getAliases() {
        return Collections.singletonList("info");
    }

    @Override
    public String getPermission() {
        return "";
    }

    @Override
    public String getUsage() {
        return "info";
    }

    @Override
    public String getDescription() {
        return "Get info about your world.";
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
        return Lang.PLAYER_NO_WORLD::send;
    }

    @Override
    public Predicate<BPlayer> getPredicate() {
        return bPlayer -> bPlayer.getBWorld() != null;
    }

    @Override
    public void execute(BWorldPlugin plugin, BPlayer bPlayer, BWorld bWorld, String[] args) {
        Lang.WORLD_INFO.send(bPlayer, bWorld.getOwner().getName(), bWorld.getWorldSize().getValue());
        for (BPlayer _bPlayer : bWorld.getPlayers(false))
            Lang.MEMBER_LIST.send(bPlayer, _bPlayer.getName());
    }

}