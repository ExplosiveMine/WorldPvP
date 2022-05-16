package net.brutewars.sandbox.menu;

import org.bukkit.event.inventory.InventoryEvent;

import java.util.function.Function;

public final class MenuAction<T extends InventoryEvent, R> {
    private final Function<T, R> action;

    public MenuAction(Function<T, R> action) {
        this.action = action;
    }

    public R apply(T event) {
        return action.apply(event);
    }
}
