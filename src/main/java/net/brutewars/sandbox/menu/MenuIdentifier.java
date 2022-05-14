package net.brutewars.sandbox.menu;

import lombok.Getter;

public enum MenuIdentifier {
    CREATE("create"),
    RECRUIT("recruit"),
    SETTINGS("settings");

    @Getter private final String identifier;

    MenuIdentifier(final String identifier) {
        this.identifier = identifier;
    }

}