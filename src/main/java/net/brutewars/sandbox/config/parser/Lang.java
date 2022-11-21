package net.brutewars.sandbox.config.parser;

import net.brutewars.sandbox.BWorldPlugin;
import net.brutewars.sandbox.player.BPlayer;
import net.brutewars.sandbox.utils.Logging;
import net.brutewars.sandbox.utils.StringUtils;
import net.brutewars.sandbox.bworld.BWorld;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public enum Lang {
    ACCEPT_INVITE_TOOLTIP,
    ALREADY_HAVE_WORLD,
    CANNOT_KICK_YOURSELF,
    CLICK_TO_INVITE,
    COMMAND_COOLDOWN,
    COMMAND_USAGE,
    CONFIRM_RESET,
    CONSOLE_NO_PERMISSION,
    CREATE_MENU,
    CREATING_ANIMATION,
    DENY_INVITE_TOOLTIP,
    DEPENDENCY_FOUND,
    DISCORD_LINK,
    EAST,
    HELP_FOOTER,
    HELP_HEADER,
    HELP_LINE,
    HELP_NEXT_PAGE,
    INVALID_AMOUNT,
    INVALID_PLAYER,
    INVALID_WORLD,
    INVITED_PLAYER_IS_MEMBER,
    INVITE_DENIED,
    LEFT_WORLD,
    MEMBER_LEAVE,
    MEMBER_LIST,
    NEW_MEMBER,
    NORTH,
    NOT_IN_WORLD,
    NO_INVITE,
    ON_RESET_VISITOR,
    OWNER_KICK_PLAYER,
    PLAYER_ACCEPTED_INVITE,
    PLAYER_ALREADY_INVITED,
    PLAYER_DENIED_INVITE,
    PLAYER_INVITED,
    PLAYER_KICKED,
    PLAYER_NOT_IN_WORLD,
    PLAYER_NO_PERMISSION,
    PLAYER_NO_WORLD,
    RECRUIT_MENU,
    RELOADED_CONFIG,
    RESET_SUCCESS,
    SETTINGS_MENU,
    SOCIAL_MEDIA_HOLOGRAM,
    SOCIAL_MENU,
    SOUTH,
    SUCCESSFULLY_INVITED_PLAYER,
    SUCCESSFULLY_JOINED_WORLD,
    SUCCESSFUL_HOOK,
    WEBSITE_LINK,
    WEST,
    WORLD_BORDER_UPDATE,
    WORLD_CREATED,
    WORLD_CREATING,
    WORLD_INFO,
    WORLD_LOADED,
    WORLD_LOADING,
    CUSTOM {
        @Override
        public void send(CommandSender sender, Object... objects) {
            if (objects.length > 0)
                sender.sendMessage(StringUtils.replaceArgs("{0}", objects));
        }

        @Override
        public String get(Object... objects) {
            return StringUtils.replaceArgs("{0}", objects);
        }
    };

    private static YamlConfiguration langCfg;

    private static final Map<Lang, Message> messages = new HashMap<>();

    public static void reload(BWorldPlugin plugin) {
        Logging.info("Reloading messages...");
        long startTime = System.currentTimeMillis();

        File langFile = new File(plugin.getDataFolder(), "lang.yml");
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