package net.brutewars.sandbox.bworld.settings;

import lombok.Getter;
import lombok.Setter;
import net.brutewars.sandbox.bworld.world.size.BorderSize;
import net.brutewars.sandbox.bworld.world.size.WorldSizes;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;

public final class WorldSettings {

    @Getter @Setter private BorderSize borderSize = WorldSizes.getDefaultSize();
    @Getter @Setter private Difficulty difficulty = Difficulty.NORMAL;
    @Getter @Setter private GameMode defaultGameMode = GameMode.SURVIVAL;
    @Getter @Setter private boolean animals = true;
    @Getter @Setter private boolean aggressiveMonsters = true;
    @Getter @Setter private boolean playersCanBuild = false;
    @Getter @Setter private boolean keepInventory = false;

}