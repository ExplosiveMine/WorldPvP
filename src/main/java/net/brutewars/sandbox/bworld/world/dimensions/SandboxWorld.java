package net.brutewars.sandbox.bworld.world.dimensions;

import net.brutewars.sandbox.BWorldPlugin;
import net.brutewars.sandbox.player.BPlayer;
import net.brutewars.sandbox.bworld.world.size.BorderSize;
import org.bukkit.*;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

public abstract class SandboxWorld extends Dimension {
    public SandboxWorld(BWorldPlugin plugin, World.Environment environment, WorldType worldType, String name) {
        super(plugin, environment, worldType, name);
    }

    @Override
    public void onWorldLoad(World world) {
        PersistentDataContainer pdc = world.getPersistentDataContainer();
        NamespacedKey key = new NamespacedKey(getPlugin(), "generated_spawn");

        if (pdc.has(key) && pdc.getOrDefault(key, PersistentDataType.STRING, "false").equals("true"))
            return;

        pdc.set(key, PersistentDataType.STRING, String.valueOf(createSpawn()));
    }

    /**
     * @return whether it was successful or needs to be generated at another time instead
     */
    protected abstract boolean createSpawn();

    public abstract void findSpawnLocation();

    protected void setSpawnLocation(Location spawnLocation) {
        getWorld().setSpawnLocation(spawnLocation);
    }

    public void setWorldBorder(BorderSize borderSize) {
        World world = getWorld();
        WorldBorder worldBorder = world.getWorldBorder();
        worldBorder.setCenter(world.getSpawnLocation());
        worldBorder.setSize(borderSize.getSize());
    }

    public List<BPlayer> getPlayers() {
        return getWorld().getPlayers().stream()
                .map(player -> getPlugin().getBPlayerManager().get(player))
                .collect(Collectors.toList());
    }

    public File getWorldFile() {
        return new File(getName());
    }

}