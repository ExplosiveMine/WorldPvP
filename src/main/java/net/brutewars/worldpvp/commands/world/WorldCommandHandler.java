package net.brutewars.worldpvp.commands.world;

import net.brutewars.worldpvp.BWorldPlugin;
import net.brutewars.worldpvp.commands.CommandHandler;
import net.brutewars.worldpvp.commands.ICommand;
import net.brutewars.worldpvp.config.Lang;
import net.brutewars.worldpvp.player.BPlayer;
import net.brutewars.worldpvp.utils.StringUtils;
import net.brutewars.worldpvp.world.BWorld;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public final class WorldCommandHandler extends CommandHandler {
    private final Map<UUID, Map<String, Long>> commandsCooldown = new HashMap<>();

    public WorldCommandHandler(final BWorldPlugin plugin) {
        super(plugin, "world", new WorldCommandMap());

        setCommand(new WorldCommand(label), "worlds", "worldpvp");
        commandMap.loadDefaultCommands(plugin);
    }
    private class WorldCommand extends PluginCommand {
        WorldCommand(String label) {
            super(label);
        }

        @Override
        public boolean execute(CommandSender sender, String label, String[] args) {
            if (args.length > 0) {
                final ICommand command = commandMap.getCommand(args[0]);
                if (command != null) {
                    if (!(sender instanceof Player) && !command.canBeExecutedByConsole()) {
                        Lang.CONSOLE_NO_PERMISSION.send(sender);
                        return false;
                    }

                    if (!command.getPermission().isEmpty() && !sender.hasPermission(command.getPermission())) {
                        Lang.PLAYER_NO_PERMISSION.send(sender);
                        return false;
                    }

                    if (args.length < command.getMinArgs() || args.length > command.getMaxArgs()) {
                        Lang.COMMAND_USAGE.send(sender, getLabel() + " " + command.getUsage());
                        return false;
                    }

                    final String commandLabel = command.getAliases().get(0);

                    if (sender instanceof Player && command.getCooldown() != 0) {
                        UUID uuid = ((Player) sender).getUniqueId();

                        long timeToExecute = commandsCooldown.containsKey(uuid) && commandsCooldown.get(uuid).containsKey(commandLabel) ?
                                commandsCooldown.get(uuid).get(commandLabel) : -1;

                        long timeNow = System.currentTimeMillis();

                        if (timeNow < timeToExecute) {
                            Lang.COMMAND_COOLDOWN.send(sender, StringUtils.formatTime(timeToExecute - timeNow, TimeUnit.MILLISECONDS));
                            return false;
                        }

                        if (!commandsCooldown.containsKey(uuid)) commandsCooldown.put(uuid, new HashMap<>());

                        commandsCooldown.get(uuid).put(commandLabel, timeNow + command.getCooldown());
                    }

                    command.execute(plugin, sender, args);
                    return false;
                }
            }

            if (sender instanceof Player) {
                final Player player = (Player) sender;
                final BPlayer bPlayer = plugin.getBPlayerManager().getBPlayer(player);

                if (bPlayer.getBWorld() != null && args.length == 0) {
                    final BWorld bWorld = bPlayer.getBWorld();

                    switch (bWorld.getWorldPhase()) {
                        case LOADING:
                            Lang.WORLD_LOADING.send(sender);
                            return false;
                        case CREATING:
                            Lang.WORLD_CREATING.send(sender);
                            return false;
                        case UNLOADED:
                            Lang.WORLD_LOADING.send(bWorld);
                            plugin.getBWorldManager().getWorldFactory().load(bWorld);
                            return false;
                        case UNLOADING:
                            bWorld.cancelUnloading();
                    }

                    bWorld.updateWorldSize();
                    bPlayer.sendToWorld();
                } else if (bPlayer.getBWorld() == null && args.length == 0)
                    Lang.HOW_TO_CREATE_WORLD.send(bPlayer);
                else
                    plugin.getServer().dispatchCommand(sender, label + " help");

                return false;
            }

            Lang.PLAYER_NO_PERMISSION.send(sender);
            return false;
        }

    }

}