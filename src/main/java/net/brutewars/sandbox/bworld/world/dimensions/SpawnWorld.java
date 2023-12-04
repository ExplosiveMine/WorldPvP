package net.brutewars.sandbox.bworld.world.dimensions;

import lombok.Getter;
import net.brutewars.sandbox.BWorldPlugin;
import net.brutewars.sandbox.bworld.settings.WorldSettings;
import net.brutewars.sandbox.bworld.settings.WorldSettingsContainer;
import net.brutewars.sandbox.bworld.world.LoadingPhase;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.WorldType;

public final class SpawnWorld extends Dimension implements WorldSettingsContainer {
    @Getter private final WorldSettings settings = new WorldSettings();

    public SpawnWorld(BWorldPlugin plugin, String worldName) {
        super(plugin, World.Environment.NORMAL, WorldType.NORMAL, worldName);
        setLoadingPhase(LoadingPhase.LOADED);
        settings.setDefaultGameMode(GameMode.valueOf(plugin.getConfigSettings().getConfigParser().getSpawnDefaultGamemode()));
    }

    @Override
    public void onWorldLoad(World world) {
        //noop
    }

}