package net.brutewars.sandbox.dependencies;

import net.brutewars.sandbox.BWorldPlugin;
import net.brutewars.sandbox.config.parser.Lang;
import net.brutewars.sandbox.utils.Logging;
import net.brutewars.sandbox.utils.StringUtils;

public abstract class Dependency {
    protected final BWorldPlugin plugin;
    protected final String name;

    public Dependency(BWorldPlugin plugin, String name) {
        this.plugin = plugin;
        this.name = name;

        if (!setup()) {
            Logging.severe(StringUtils.replaceArgs("{0} - Disabled plugin since {1} dependency was not found!", plugin.getDescription().getName(), name));
            plugin.getServer().getPluginManager().disablePlugin(plugin);
            return;
        }

        Logging.info(Lang.DEPENDENCY_FOUND.get(name));
    }

    public abstract boolean setup();
}