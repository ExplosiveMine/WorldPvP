package net.brutewars.sandbox.world.holograms;

import net.brutewars.sandbox.BWorldPlugin;
import net.brutewars.sandbox.config.parser.Lang;
import net.brutewars.sandbox.thread.Executor;
import net.brutewars.sandbox.utils.Pair;
import net.brutewars.sandbox.utils.PersistentDataUtils;
import net.brutewars.sandbox.utils.StringUtils;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public final class HologramManager {
    private final BWorldPlugin plugin;

    private final Map<Pair<UUID, String>, DynamicHologram> dynamicHolograms = new HashMap<>();

    public HologramManager(BWorldPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Called to initiate the ticking of dynamic holograms.
     */
    public void init() {
        Executor.sync(plugin, bukkitRunnable -> {
            for (DynamicHologram holo : dynamicHolograms.values())
                holo.update();
        }, 0L, 1L);
    }

    /**
     * Spawns a hologram and stores the uuid with the key in the world's {@link org.bukkit.persistence.PersistentDataContainer}
     * only if the key is unique
     * @param key appends the parameter so that the final id is in the format: hologram_key
     */
    public void spawnHologram(Location loc, String key, Lang text) {
        World world = loc.getWorld();
        // Check if hologram with this key already exists
        String id = PersistentDataUtils.getData(plugin, world, PersistentDataType.STRING,"hologram_" + key, "");
        if (!id.isEmpty())
            return;

        ArmorStand armorStand = new Hologram(loc, text.get()).spawn(plugin);
        PersistentDataUtils.storeData(plugin, world, PersistentDataType.STRING,"hologram_" + key, armorStand.getUniqueId().toString());
    }

    /**
     * Spawns a hologram with a sequential id; it is incremented from 0 and when {@link #removeIncrementalHolograms(World)} is
     * called, these holograms are moved.
     */
    public void spawnHologram(Location loc, Lang text) {
        World world = loc.getWorld();
        int holograms = getNumHolograms(world);
        spawnHologram(loc, String.valueOf(holograms++), text);
        PersistentDataUtils.storeData(plugin, world, PersistentDataType.INTEGER, "hologram_number", holograms);
    }

    public void addDynamicHologram(DynamicHologram holo, String key) {
        Pair<UUID, String> pair = Pair.of(holo.getLoc().getWorld().getUID(), key);
        dynamicHolograms.put(pair, holo);
    }

    public int getNumHolograms(World world) {
        return PersistentDataUtils.getData(plugin, world, PersistentDataType.INTEGER, "hologram_number", 0);
    }

    /**
     * @param world the world in which the hologram has been spawned previously
     * @param key the id of the hologram
     * @return the entity which makes up the hologram
     */
    public @Nullable Entity getEntity(World world, String key) {
        String uuid = PersistentDataUtils.getData(plugin, world, PersistentDataType.STRING, "hologram_" + key, "");
        if (uuid.isEmpty() || !StringUtils.isUUID(uuid))
            return null;

        return world.getEntity(UUID.fromString(uuid));
    }

    /**
     * Attempts to despawn the hologram - whether static or dynamic
     * @param world the world in which the hologram can be found
     * @param key its identifier
     */
    public void removeHologram(World world, String key) {
        // hologram can be either dynamic or static
        Pair<UUID, String> pair = Pair.of(world.getUID(), key);
        if (dynamicHolograms.containsKey(pair)) {
            dynamicHolograms.get(pair).deSpawn();
            dynamicHolograms.remove(pair);
        } else {
            Entity ent = getEntity(world, key);
            if (ent != null)
                ent.remove();
        }

        PersistentDataUtils.remove(plugin, world, "hologram_" + key);
    }

    /**
     * Remove all the holograms which use incremental id
     */
    public void removeIncrementalHolograms(World world) {
        int holograms = getNumHolograms(world);
        for (int i = 0; i < holograms; i++)
            removeHologram(world, String.valueOf(i));
    }

    public void removeDynamicHolograms() {
        for (DynamicHologram holo : dynamicHolograms.values())
            holo.deSpawn();
    }

    // EVENTS

    /**
     * When a chunk is loaded, we spawn dynamic holograms
     */
    public void onChunkLoad(Chunk chunk) {
        for (Map.Entry<Pair<UUID, String>, DynamicHologram> entry : dynamicHolograms.entrySet()) {
            DynamicHologram holo = entry.getValue();
            if (holo.getChunkKey() == chunk.getChunkKey())
                holo.spawn(plugin);
        }
    }

    /**
     * When a chunk is unloaded, we de-spawn dynamic holograms
     */
    public void onChunkUnload(Chunk chunk) {
        for (Map.Entry<Pair<UUID, String>, DynamicHologram> entry : dynamicHolograms.entrySet()) {
            if (entry.getValue().getChunkKey() == chunk.getChunkKey())
                dynamicHolograms.get(entry.getKey()).deSpawn();
        }
    }

    /**
     * When the world is unloaded, we simply remove the dynamic hologram in the world.
     * Dynamic holograms must be re-created once the world is loaded again.
     */
    public void onWorldUnload(World world) {
        for (Iterator<Pair<UUID, String>> iter = dynamicHolograms.keySet().iterator(); iter.hasNext();) {
            Pair<UUID, String> pair = iter.next();
            if (world.getUID().equals(pair.getKey())) {
                System.out.println("world unload");
                dynamicHolograms.get(pair).deSpawn();
                iter.remove();
            }
        }
    }

    public void onEntityDamageEntity(EntityDamageEvent event) {
        // prevent breaking invulnerable armorstands in creative mode if its this plugin's holograms
        if (!(event.getEntity() instanceof ArmorStand armorStand) || !armorStand.isInvulnerable())
            return;

        String data = armorStand.getPersistentDataContainer().get(new NamespacedKey(plugin, "hologram"), PersistentDataType.STRING);
        if (data != null)
            event.setCancelled(true);
    }

}