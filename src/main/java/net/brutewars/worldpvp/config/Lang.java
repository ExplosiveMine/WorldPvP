package net.brutewars.worldpvp.config;

import net.brutewars.worldpvp.BWorldPlugin;
import net.brutewars.worldpvp.player.BPlayer;
import net.brutewars.worldpvp.utils.Logging;
import net.brutewars.worldpvp.utils.StringUtils;
import net.brutewars.worldpvp.world.BWorld;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public enum Lang {
    CHANNEL_SWITCH,
    COMMAND_COOLDOWN,
    COMMAND_USAGE,
    CONSOLE_NO_PERMISSION,
    WORLD_LOADING,
    WORLD_LOADED,
    PLAYER_NO_PERMISSION,
    PLAYER_NO_WORLD,
    WORLD_CHAT_FORMAT,
    HELP_HEADER,
    HELP_LINE,
    HELP_NEXT_PAGE,
    HELP_FOOTER,
    INVALID_AMOUNT,
    MEMBER_LEAVE,
    LEFT_WORLD,
    OWNER_FAIL_LEAVE,
    NO_PERMISSION_RESET,
    CONFIRM_RESET,
    RESET_SUCCESS,
    NO_PERMISSION_INVITE,
    INVITED_PLAYER_IS_MEMBER,
    PLAYER_ALREADY_INVITED,
    PLAYER_INVITED,
    ACCEPT_INVITE_TOOLTIP,
    DENY_INVITE_TOOLTIP,
    SUCCESSFULLY_INVITED_PLAYER,
    INVALID_PLAYER,
    INVALID_WORLD,
    NO_INVITE,
    ALREADY_HAVE_WORLD,
    PLAYER_ACCEPTED_INVITE,
    SUCCESSFULLY_JOINED_WORLD,
    NEW_MEMBER,
    PLAYER_DENIED_INVITE,
    INVITE_DENIED,
    WORLD_CREATING,
    NO_PERMISSION_KICK,
    CANNOT_KICK_YOURSELF,
    OWNER_KICK_PLAYER,
    PLAYER_KICKED,
    SUCCESSFUL_HOOK,
    WORLD_BORDER_UPDATE,
    HOW_TO_CREATE_WORLD,
    WORLD_CREATED,
    PLAYER_NOT_IN_WORLD,
    RELOADED_CONFIG,
    WORLD_INFO,
    MEMBER_LIST;


    private static YamlConfiguration langCfg;

    private static final Map<Lang, Message> messages = new HashMap<>();

    public static void reload(final BWorldPlugin plugin) {
        Logging.info("Reloading messages...");
        final long startTime = System.currentTimeMillis();

        final File langFile = new File(plugin.getDataFolder(), "lang.yml");
        if (!langFile.exists())
            plugin.saveResource("lang.yml", false);

        langCfg = YamlConfiguration.loadConfiguration(langFile);

        Arrays.stream(values()).forEach(lang -> messages.put(lang, new Message(langCfg.getString(lang.name(), ""))));

        Logging.info(StringUtils.replaceArgs("Messages have been reloaded. This took {0} ms.", (System.currentTimeMillis()-startTime)));
    }

    public void send(CommandSender sender, Object... objects) {
        messages.get(this).send(sender, objects);
    }

    public void send(BPlayer bPlayer, Object... objects) {
        bPlayer.runIfOnline(player -> send(player, objects));
    }

    public void send(BWorld bWorld, Object... objects) {
        bWorld.getPlayers(true).forEach(bPlayer -> send(bPlayer, objects));
    }

    public String get(Object... objects) {
        return StringUtils.colour(StringUtils.replaceArgs(messages.get(this).getMessage(), objects));
    }

    private static final class Message {
        private final String message;

        Message(String message) {
            this.message = message;
        }

        String getMessage() {
            return message;
        }

        void send(CommandSender sender, Object... objects) {
            if (message != null && !message.isEmpty())
                sender.sendMessage(StringUtils.colour(StringUtils.replaceArgs(message, objects)));
        }
    }

}