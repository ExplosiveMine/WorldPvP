package net.brutewars.sandbox.world.holograms;

import net.brutewars.sandbox.BWorldPlugin;
import net.brutewars.sandbox.config.parser.Lang;
import net.brutewars.sandbox.utils.PersistentDataUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.persistence.PersistentDataType;

import java.util.UUID;

public final class HologramManager {
    private final BWorldPlugin plugin;

    public HologramManager(BWorldPlugin plugin) {
        this.plugin = plugin;
    }

    public void spawnHologram(Location loc, String text) {
        World world = loc.getWorld();
        ArmorStand holo = world.spawn(loc, ArmorStand.class);
        holo.setInvisible(true);
        holo.setSilent(true);
        holo.setCustomNameVisible(true);
        holo.setCanMove(false);
        holo.setGravity(false);
        holo.setCollidable(false);
        holo.setInvulnerable(true);
        holo.setDisabledSlots(EquipmentSlot.values());
        holo.customName(Component.text(Lang.CUSTOM.get(text)));

        int holograms = getNumHolograms(world);
        PersistentDataUtils.storeData(plugin, world, PersistentDataType.STRING,"hologram_" + (holograms++), holo.getUniqueId().toString());
        PersistentDataUtils.storeData(plugin, world, PersistentDataType.INTEGER, "hologram_number", holograms);
    }

    public void createActionHologram(Location loc, String text, String key) {
        World world = loc.getWorld();
        PersistentDataUtils.storeData(plugin, world, PersistentDataType.INTEGER,"hologram_" + key, getNumHolograms(world));
        spawnHologram(loc, text);
    }

    public boolean isActionHologram(World world, String key, UUID entity) {
        int id = PersistentDataUtils.getData(plugin, world, PersistentDataType.INTEGER, "hologram_" + key, -1);
        if (id == -1)
            return false;

        String uuid = PersistentDataUtils.getData(plugin, world, PersistentDataType.STRING, "hologram_" + id, "");
        return uuid.equals(entity.toString());
    }

    public int getNumHolograms(World world) {
        return PersistentDataUtils.getData(plugin, world, PersistentDataType.INTEGER, "hologram_number", 0);
    }

    public void removeHolograms(World world) {
        int holograms = getNumHolograms(world);
        for (int i = 0; i < holograms; i++) {
            String uuid = PersistentDataUtils.getData(plugin, world, PersistentDataType.STRING, "hologram_" + i, "");
            Entity holo = world.getEntity(UUID.fromString(uuid));
            if (holo != null)
                holo.remove();

            PersistentDataUtils.remove(plugin, world, "hologram_" + i);
        }
    }

}
