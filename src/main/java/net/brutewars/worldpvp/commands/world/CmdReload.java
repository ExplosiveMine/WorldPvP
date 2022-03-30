package net.brutewars.worldpvp.commands.world;

import net.brutewars.worldpvp.BWorldPlugin;
import net.brutewars.worldpvp.commands.ICommand;
import net.brutewars.worldpvp.config.Lang;
import net.brutewars.worldpvp.world.WorldSize;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CmdReload implements ICommand {
    @Override
    public List<String> getAliases() {
        return Collections.singletonList("reload");
    }

    @Override
    public String getPermission() {
        return "worldpvp.reload";
    }

    @Override
    public String getUsage() {
        return "reload";
    }

    @Override
    public String getDescription() {
        return "Reload plugin messages.";
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
        return true;
    }

    @Override
    public void execute(BWorldPlugin plugin, CommandSender sender, String[] args) {
        Lang.reload(plugin);
        WorldSize.reload(plugin);
        Lang.RELOADED_CONFIG.send(sender);
    }

    @Override
    public List<String> tabComplete(BWorldPlugin plugin, CommandSender sender, String[] args) {
        return new ArrayList<>();
    }
}
