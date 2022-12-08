package net.brutewars.sandbox.world.holograms;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

public final class RainbowHologram extends DynamicHologram {
    public final static TextColor[] textColors = new TextColor[] {
            TextColor.color(255, 0 , 0),
            TextColor.color(255, 127, 0),
            TextColor.color(255, 255, 0),
            TextColor.color(0, 255, 0),
            TextColor.color(0, 0, 255),
            TextColor.color(75, 0, 130),
            TextColor.color(148, 0, 211),
            TextColor.color(148, 0, 211),
            TextColor.color(75, 0, 130),
            TextColor.color(0, 0, 255),
            TextColor.color(0, 255, 0),
            TextColor.color(255, 255, 0),
            TextColor.color(255, 127, 0),
            TextColor.color(255, 0 , 0)
    };

    // The index of the colour code in the array which has been used at index 0 in the string
    private int colourIndex = 0;
    private int step = 0;

    public RainbowHologram(Location loc, String text) {
        super(loc, text);
    }

    @Override
    public void tick() {
        Entity ent = getEntity();
        if (ent == null)
            return;

        TextColor current = textColors[colourIndex];

        int nextColour = colourIndex + 1;
        if (nextColour > colourIndex + 1)
            nextColour = 0;

        TextColor next = textColors[nextColour];

        // goes from 0 to 1. We multiply by 0.025 because we want to transition in two seconds -> 1 transition / 40 ticks = 0.025
        double ratio = step * 0.025;
        int red = (int) Math.abs((ratio * next.red()) + ((1 - ratio) * current.red()));
        int green = (int) Math.abs((ratio * next.green()) + ((1 - ratio) * current.green()));
        int blue = (int) Math.abs((ratio * next.blue()) + ((1 - ratio) * current.blue()));

        ent.customName(Component.text(getText()).color(TextColor.color(red, green, blue)));

        step++;
        if (step >= 40) {
            step = 0;
            // increment for next time
            colourIndex++;
            if (colourIndex >= textColors.length-1)
                colourIndex = 0;
        }
    }

}