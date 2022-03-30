package net.brutewars.worldpvp.commands.chat;

import net.brutewars.worldpvp.BWorldPlugin;
import net.brutewars.worldpvp.commands.CommandHandler;
import net.brutewars.worldpvp.commands.CommandMap;
import net.brutewars.worldpvp.config.Lang;
import net.brutewars.worldpvp.player.BPlayer;
import net.brutewars.worldpvp.player.PlayerChat;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public final class ChatCommandHandler extends CommandHandler {

    public ChatCommandHandler(final BWorldPlugin plugin) {
        super(plugin, "wc", new CommandMap() {
            @Override
            public void loadDefaultCommands(BWorldPlugin plugin) { }
        });

        setCommand(new ChatCommand(label), "worldchat", "wchat");
    }

    private class ChatCommand extends PluginCommand {
        ChatCommand(String label) {
            super(label);
        }

        @Override
        public boolean execute(CommandSender sender, String label, String[] args) {
            if (!(sender instanceof Player)) {
                Lang.CONSOLE_NO_PERMISSION.send(sender);
                return false;
            }

            final BPlayer bPlayer = plugin.getBPlayerManager().getBPlayer((Player) sender);

            if (bPlayer.getBWorld() == null) {
                Lang.PLAYER_NO_WORLD.send(bPlayer);
                bPlayer.setPlayerChat(PlayerChat.GLOBAL);
                return false;
            }

            // /wc
            if (args.length == 0) {
                if (bPlayer.getPlayerChat().equals(PlayerChat.GLOBAL))
                    bPlayer.setPlayerChat(PlayerChat.WORLD);
                else
                    bPlayer.setPlayerChat(PlayerChat.GLOBAL);

                Lang.CHANNEL_SWITCH.send(bPlayer, bPlayer.getPlayerChat());

            // /wc [message]
            } else {
                final StringBuilder sb = new StringBuilder();
                for (String arg : args) sb.append(" ").append(arg);

                bPlayer.setPlayerChat(PlayerChat.WORLD);
                bPlayer.runIfOnline(player -> player.chat(sb.toString()));
                bPlayer.setPlayerChat(PlayerChat.GLOBAL);
            }

            return false;
        }

        @Override
        public List<String> tabComplete(CommandSender sender, String label, String[] args) {
            return Collections.emptyList();
        }
    }

}