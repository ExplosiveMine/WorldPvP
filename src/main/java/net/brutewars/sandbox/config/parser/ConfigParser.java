package net.brutewars.sandbox.config.parser;

import lombok.Getter;
import net.brutewars.sandbox.BWorldPlugin;
import org.bukkit.configuration.file.FileConfiguration;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

public final class ConfigParser extends SectionParser {
    @Getter private long invitingTime;

    @Getter private long resettingTime;
    @Getter private long unloadingTime;
    @Getter private long clearLagInterval;
    @Getter private Duration worldRenewTime;

    @Getter private int rosterSize;
    @Getter private int spawnLocationAttempts;

    @Getter private String worldName;
    @Getter private String spawnLocation;
    @Getter private String spawnDefaultGamemode;

    @Getter private boolean debug;

    public ConfigParser(BWorldPlugin plugin) {
        super(plugin, null);
    }

    @Override
    public void parse() {
        FileConfiguration config = plugin.getConfig();

        invitingTime = config.getLong("times.invite-expire", 300L) * 20;
        unloadingTime = config.getLong("times.world-unload", 600L) * 20;
        resettingTime = config.getLong("times.reset-request", 30L) * 20;
        clearLagInterval = config.getLong("times.clear-lag", 300L);
        worldRenewTime = Duration.of(config.getLong("times.renew-worlds", 7L), ChronoUnit.DAYS);

        rosterSize = config.getInt("world.roster-size", 5);
        spawnLocationAttempts = config.getInt("world.spawn-location-attempts", 20);

        worldName = config.getString("spawn-world.name");
        spawnLocation = config.getString("spawn-world.location");
        spawnDefaultGamemode = config.getString("spawn-world.default_gamemode", "SURVIVAL");

        debug = config.getBoolean("debug");
    }

}