package net.brutewars.sandbox.utils;

import net.brutewars.sandbox.BWorldPlugin;
import org.bukkit.Bukkit;

public final class Logging {

    public static void info(String s) {
        Bukkit.getLogger().info(s);
    }

    public static void severe(String s) {
        Bukkit.getLogger().severe(s);
    }

    public static void debug(final BWorldPlugin plugin, String s) {
        if (!plugin.getConfigSettings().debug) return;
        info("[" + plugin.getName() + "] " + s);
    }
}
