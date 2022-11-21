package net.brutewars.sandbox.config.parser;

import lombok.Getter;
import net.brutewars.sandbox.BWorldPlugin;
import org.bukkit.configuration.file.FileConfiguration;

public final class ConfigParser extends SectionParser {
    @Getter private long invitingTime;
    @Getter private long resettingTime;
    @Getter private long unloadingTime;
    @Getter private long worldRenewTime;

    @Getter private int rosterSize;

    @Getter private String worldName;
    @Getter private String spawnLocation;

    @Getter private boolean debug;

    public ConfigParser(BWorldPlugin plugin) {
        super(plugin, null);
    }

    @Override
    public void parse() {
        FileConfiguration config = plugin.getConfig();

        invitingTime = config.getLong("times.invite-expire", 300L);
        unloadingTime = config.getLong("times.world-unload", 600L);
        resettingTime = config.getLong("times.reset-request", 30L);
        worldRenewTime = config.getLong("times.renew-worlds", 7L) * 24 * 3600;

        rosterSize = config.getInt("world.roster-size", 5);

        worldName = config.getString("spawn-world.name");
        spawnLocation = config.getString("spawn-world.location");

        debug = config.getBoolean("debug");
    }

}