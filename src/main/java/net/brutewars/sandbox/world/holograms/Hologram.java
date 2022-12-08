package net.brutewars.sandbox.world.holograms;

import lombok.Getter;
import net.brutewars.sandbox.BWorldPlugin;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

public class Hologram {
    @Getter private final Location loc;
    @Getter private final String text;

    private Entity entity;

    public Hologram(Location loc, String text) {
        this.loc = loc;
        this.text = text;
    }

    public ArmorStand spawn(BWorldPlugin plugin) {
        World world = loc.getWorld();
        ArmorStand armorStand = world.spawn(loc, ArmorStand.class);
        armorStand.setInvisible(true);
        armorStand.setCustomNameVisible(true);
        armorStand.setGravity(false);
        armorStand.setInvulnerable(true);
        armorStand.setDisabledSlots(EquipmentSlot.values());
        armorStand.setRemoveWhenFarAway(false);
        armorStand.customName(Component.text(text));

        // to be able to identify the hologram armorstands
        armorStand.getPersistentDataContainer().set(new NamespacedKey(plugin, "hologram"), PersistentDataType.STRING, "h");

        this.entity = armorStand;
        return armorStand;
    }

    /**
     * Attempts to de-spawn the entity which can only be done if the chunk is loaded
     * @return whether it has been successful
     */
    public boolean deSpawn() {
        if (entity == null)
            return false;

        entity.remove();
        return true;
    }

    public @Nullable Entity getEntity() {
        return entity;
    }

}