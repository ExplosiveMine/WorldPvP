package net.brutewars.sandbox.menu;

import lombok.Getter;

public enum MenuIdentifier {
    CREATE(),
    SETTINGS(),
    RECRUIT(SETTINGS),
    SOCIAL(),
    CREATING_ANIMATION();

    @Getter private final MenuIdentifier parentIdentifier;

    MenuIdentifier() {
        this(null);
    }

    MenuIdentifier(MenuIdentifier parentIdentifier) {
        this.parentIdentifier = parentIdentifier;
    }

}