package net.brutewars.worldpvp.commands.world;

import net.brutewars.worldpvp.BWorldPlugin;
import net.brutewars.worldpvp.commands.ICommand;
import net.brutewars.worldpvp.config.Lang;
import net.brutewars.worldpvp.player.BPlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class CmdCreate implements ICommand {
    final long cooldown;

    public CmdCreate(final BWorldPlugin plugin) {
        this.cooldown = plugin.getConfig().getLong("commands.cooldown.create") * 1000;
    }

    @Override
    public List<String> getAliases() {
        return Collections.singletonList("create");
    }

    @Override
    public String getPermission() {
        return "";
    }

    @Override
    public String getUsage() {
        return "create";
    }

    @Override
    public String getDescription() {
        return "Create your own world";
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
    public void execute(BWorldPlugin plugin, CommandSender sender, String[] args) {
        final BPlayer bPlayer = plugin.getBPlayerManager().getBPlayer((Player) sender);

        if (bPlayer.getBWorld() != null) {
            Lang.ALREADY_HAVE_WORLD.send(sender);
            return;
        }

        Lang.WORLD_CREATING.send(sender);
        plugin.getBWorldManager().createBWorld(bPlayer);
    }

    @Override
    public List<String> tabComplete(BWorldPlugin plugin, CommandSender sender, String[] args) {
        return new ArrayList<>();
    }
}
