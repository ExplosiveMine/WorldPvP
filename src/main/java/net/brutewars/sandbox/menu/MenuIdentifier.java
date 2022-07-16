package net.brutewars.sandbox.menu;

import lombok.Getter;

public enum MenuIdentifier {
    CREATE("create"),
    SETTINGS("settings"),
    RECRUIT("recruit", SETTINGS),
    CREATING_ANIMATION("creating_animation");

    private final String identifier;
    @Getter private final MenuIdentifier parentIdentifier;

    MenuIdentifier(String identifier) {
        this.identifier = identifier;
        this.parentIdentifier = null;
    }

    MenuIdentifier(String identifier, MenuIdentifier parentIdentifier) {
        this.identifier = identifier;
        this.parentIdentifier = parentIdentifier;
    }

    public String getIdentifier() {
        return (parentIdentifier == null) ? identifier : parentIdentifier.getIdentifier() + "." + identifier;
    }

}