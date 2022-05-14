package net.brutewars.sandbox.menu.bmenu.animation;

import net.brutewars.sandbox.menu.items.BaseMenuItem;
import net.brutewars.sandbox.player.BPlayer;
import org.bukkit.inventory.Inventory;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

public final class MenuAnimation implements Iterator<Frame> {
    private final List<Frame> frames;
    private final Map<String, BaseMenuItem> items;

    private int frameCounter = -1;

    public MenuAnimation(final List<Frame> frames, final Map<String, BaseMenuItem> items) {
        this.frames = frames;
        this.items = items;
    }

    @Override
    public boolean hasNext() {
        return frameCounter + 1 < frames.size();
    }

    @Override
    public Frame next() {
        frameCounter = frameCounter + 1;
        return frames.get(frameCounter);
    }

    public boolean playNext(final BPlayer bPlayer, final Inventory inventory) {
        if (!hasNext()) return false;

        next().play(bPlayer, inventory, items);
        return true;
    }

}