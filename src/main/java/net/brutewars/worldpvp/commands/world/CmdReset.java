package net.brutewars.worldpvp.commands.world;

import net.brutewars.worldpvp.BWorldPlugin;
import net.brutewars.worldpvp.commands.IPermissibleCommand;
import net.brutewars.worldpvp.config.Lang;
import net.brutewars.worldpvp.player.BPlayer;
import net.brutewars.worldpvp.world.BWorld;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public final class CmdReset implements IPermissibleCommand {
    final long cooldown;

    public CmdReset(final BWorldPlugin plugin) {
        this.cooldown = plugin.getConfig().getLong("commands.cooldown.reset") * 1000;
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("reset", "delete");
    }

    @Override
    public String getPermission() {
        return "";
    }

    @Override
    public String getUsage() {
        return "reset";
    }

    @Override
    public String getDescription() {
        return "Reset your world permanently.";
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
    public long getCooldown() {
        return cooldown;
    }

    @Override
    public boolean displayCommand() {
        return true;
    }

    @Override
    public Consumer<BPlayer> getPermissionLackAction() {
        return Lang.OWNER_FAIL_LEAVE::send;
    }

    @Override
    public Predicate<BPlayer> getPredicate() {
        return bPlayer -> bPlayer.getRank().isOwner();
    }

    @Override
    public void execute(BWorldPlugin plugin, BPlayer bPlayer, BWorld bWorld, String[] args) {
        Lang.CONFIRM_RESET.send(bPlayer);
        bWorld.initialiseReset();
    }

}