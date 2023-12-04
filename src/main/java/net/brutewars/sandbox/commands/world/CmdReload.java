package net.brutewars.sandbox.commands.world;

import net.brutewars.sandbox.BWorldPlugin;
import net.brutewars.sandbox.commands.Command;
import net.brutewars.sandbox.config.parser.Lang;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class CmdReload implements Command {
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
  //todo test if this works fine
        plugin.getConfigSettings().reload();
//        Lang.reload(plugin);
//        WorldSize.reload(plugin);
        Lang.RELOADED_CONFIG.send(sender);
    }

    @Override
    public List<String> tabComplete(BWorldPlugin plugin, CommandSender sender, String[] args) {
        return new ArrayList<>();
    }

}