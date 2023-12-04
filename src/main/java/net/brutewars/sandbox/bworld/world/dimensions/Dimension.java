package net.brutewars.sandbox.bworld.world.dimensions;

import lombok.Getter;
import lombok.Setter;
import net.brutewars.sandbox.BWorldPlugin;
import net.brutewars.sandbox.bworld.world.LoadingPhase;
import net.brutewars.sandbox.player.BPlayer;
import net.brutewars.sandbox.utils.Logging;
import net.kyori.adventure.util.TriState;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.event.player.PlayerTeleportEvent;

public abstract class Dimension {
    @Getter private final BWorldPlugin plugin;

    @Getter @Setter private LoadingPhase loadingPhase = LoadingPhase.UNLOADED;

    @Getter private final String name;

    @Getter private final World.Environment environment;
    @Getter private final WorldType worldType;

    public Dimension(BWorldPlugin plugin, World.Environment environment, WorldType worldType, String name) {
        this.plugin = plugin;
        this.environment = environment;
        this.worldType = worldType;
        this.name = name;
    }

    public World getWorld() {
        if (loadingPhase == LoadingPhase.UNLOADED)
            loadWorld();

        return plugin.getServer().getWorld(name);
    }

    public abstract void onWorldLoad(World world);

    /**
     * @param generate if set to false, the method assumes the world only needs to be loaded
     *                  but will still create the world if it does not exist
     * @return The created/loaded world
     */
    public World create(boolean generate) {
        WorldCreator wc = new WorldCreator(name);
        wc.environment(environment);

        if (generate) {
            Logging.debug(plugin, "Generating new world: " + name);
            wc.type(worldType);
        } else {
            wc.keepSpawnLoaded(TriState.FALSE);
        }

        return plugin.getServer().createWorld(wc);
    }

    /**
     * @return the loaded world
     */
    public World loadWorld() {
        loadingPhase = LoadingPhase.LOADING;

        World world = create(false);
        onWorldLoad(world);

        loadingPhase = LoadingPhase.LOADED;
        return world;
    }

    public void teleportToWorld(BPlayer bPlayer) {
        bPlayer.runIfOnline(player -> player.teleportAsync(getWorld().getSpawnLocation(), PlayerTeleportEvent.TeleportCause.PLUGIN));
    }

}