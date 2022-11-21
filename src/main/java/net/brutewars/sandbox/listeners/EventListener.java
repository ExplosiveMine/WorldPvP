package net.brutewars.sandbox.listeners;

import net.brutewars.sandbox.BWorldPlugin;
import org.bukkit.event.Listener;

public abstract class EventListener implements Listener {
    protected final BWorldPlugin plugin;

    public EventListener(BWorldPlugin plugin) {
        this.plugin = plugin;
    }

}