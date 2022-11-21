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

    public static void debug(BWorldPlugin plugin, String s) {
        if (!plugin.getConfigSettings().getConfigParser().isDebug()) return;
        info("[" + plugin.getName() + "] " + s);
    }
}
