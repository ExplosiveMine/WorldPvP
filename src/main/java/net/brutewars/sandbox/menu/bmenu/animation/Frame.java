package net.brutewars.sandbox.menu.bmenu.animation;

import net.brutewars.sandbox.menu.items.MenuItem;
import net.brutewars.sandbox.player.BPlayer;
import org.bukkit.Sound;
import org.bukkit.inventory.Inventory;

import java.util.Map;

public final class Frame {
    private final String[] pattern;

    public Frame(String[] pattern) {
        this.pattern = pattern;
    }

    public void play(BPlayer bPlayer, Inventory inventory, Map<String, MenuItem> items) {
        bPlayer.playSound(Sound.NOTE_STICKS, 0.5f, 0.5f);

        for (int i = 0; i < pattern.length; i++)
            inventory.setItem(i, items.get(pattern[i]).getItem(bPlayer));

        bPlayer.openInventory(inventory);
    }

}