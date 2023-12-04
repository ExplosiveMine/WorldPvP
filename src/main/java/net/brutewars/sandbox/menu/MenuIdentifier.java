package net.brutewars.sandbox.menu;

import lombok.Getter;

public enum MenuIdentifier {
    CREATE(),
    CREATING_ANIMATION(),
    JOIN_WORLD(),
    SETTINGS(),
    RECRUIT(SETTINGS),
    SOCIAL(),
    STRUCTURE_GEN(CREATE);

    @Getter private final MenuIdentifier parentIdentifier;

    MenuIdentifier() {
        this(null);
    }

    MenuIdentifier(MenuIdentifier parentIdentifier) {
        this.parentIdentifier = parentIdentifier;
    }

}