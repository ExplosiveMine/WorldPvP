package net.brutewars.sandbox.config.parser;

import lombok.Getter;
import net.brutewars.sandbox.BWorldPlugin;
import org.bukkit.configuration.file.FileConfiguration;

public final class ConfigParser extends SectionParser {
    @Getter private long invitingTime;
    @Getter private long resettingTime;
    @Getter private long unloadingTime;

    @Getter private String worldName;
    @Getter private String spawnLocation;

    @Getter private boolean debug;

    public ConfigParser(BWorldPlugin plugin) {
        super(plugin, null);
    }

    @Override
    public void parse() {
        FileConfiguration config = plugin.getConfig();
        invitingTime = config.getLong("invites.expire time");
        resettingTime = config.getLong("world.resetting time");
        unloadingTime = config.getLong("world.unloading time");

        worldName = config.getString("spawnWorld.name");
        spawnLocation = config.getString("spawnWorld.location");

        debug = config.getBoolean("debug");
    }

}