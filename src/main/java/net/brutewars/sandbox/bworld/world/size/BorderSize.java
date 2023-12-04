package net.brutewars.sandbox.bworld.world.size;

import lombok.Getter;

public final class BorderSize {
    @Getter private final String permission;
    @Getter private final int size;

    public BorderSize(String identifier, int size) {
        this.permission = "world." + identifier;
        this.size = size;
    }

}