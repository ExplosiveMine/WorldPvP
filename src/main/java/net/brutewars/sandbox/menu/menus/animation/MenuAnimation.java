package net.brutewars.sandbox.menu.menus.animation;

import net.brutewars.sandbox.menu.items.MenuItem;
import net.brutewars.sandbox.player.BPlayer;
import org.bukkit.inventory.Inventory;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

public final class MenuAnimation implements Iterator<Frame> {
    private final List<Frame> frames;
    private final Map<String, MenuItem> items;

    private int frameCounter = -1;

    public MenuAnimation(List<Frame> frames, Map<String, MenuItem> items) {
        this.frames = frames;
        this.items = items;
    }

    @Override
    public boolean hasNext() {
        return frameCounter + 1 < frames.size();
    }

    @Override
    public Frame next() {
        return frames.get(++frameCounter);
    }

    public boolean playNext(BPlayer bPlayer, Inventory inventory) {
        if (!hasNext()) return false;

        next().play(bPlayer, inventory, items::get);
        return true;
    }

}
