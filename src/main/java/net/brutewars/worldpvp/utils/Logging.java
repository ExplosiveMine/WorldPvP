package net.brutewars.worldpvp.utils;

import net.brutewars.worldpvp.BWorldPlugin;
import org.bukkit.Bukkit;

public final class Logging {

    public static void info(String s) {
        Bukkit.getLogger().info(s);
    }

    public static void severe(String s) {
        Bukkit.getLogger().severe(s);
    }

    public static void debug(final BWorldPlugin plugin, String s) {
        if (!plugin.getConfig().getBoolean("debug")) return;
        info("[WorldPvP] " + s);
    }
}
