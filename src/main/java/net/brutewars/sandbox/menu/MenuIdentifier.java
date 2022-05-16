package net.brutewars.sandbox.menu;

import lombok.Getter;

public enum MenuIdentifier {
    CREATE("create"),
    RECRUIT("recruit"),
    SETTINGS("settings"),
    CREATING_ANIMATION("creating_animation");

    @Getter private final String identifier;

    MenuIdentifier(String identifier) {
        this.identifier = identifier;
    }

}