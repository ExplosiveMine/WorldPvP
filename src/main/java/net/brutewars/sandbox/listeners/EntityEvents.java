package net.brutewars.sandbox.listeners;

import com.destroystokyo.paper.event.entity.PreCreatureSpawnEvent;
import net.brutewars.sandbox.BWorldPlugin;
import net.brutewars.sandbox.bworld.BWorld;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;

public final class EntityEvents extends EventListener {
    public EntityEvents(BWorldPlugin plugin) {
        super(plugin);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPreCreatureSpawn(PreCreatureSpawnEvent event) {
        BWorld bWorld = plugin.getBWorldManager().getBWorld(event.getSpawnLocation().getWorld());
        if (bWorld == null)
            return;

        if (bWorld.isAnimals())
            return;

        switch (event.getReason()) {
            // we only need to prevent passive mob spawns
            case JOCKEY, NATURAL, BEEHIVE, TRAP, MOUNT, EGG, DISPENSE_EGG, OCELOT_BABY, DEFAULT -> {
                if (!bWorld.shouldEntityExist(event.getType()))
                    event.setCancelled(true);
            }

        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityTargetLivingEntity(EntityTargetLivingEntityEvent event) {
        LivingEntity entity = event.getTarget();
        if (entity == null)
            return;

        if (!(event.getTarget() instanceof Player))
            return;

        BWorld bWorld = plugin.getBWorldManager().getBWorld(event.getTarget().getLocation().getWorld());
        if (bWorld == null)
            return;

        if (bWorld.isAggressiveMonsters())
            return;

        if (event.getEntity().getSpawnCategory() != SpawnCategory.MONSTER)
            return;

        event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        plugin.getHologramManager().onEntityDamageEntity(event);

        if (!(event.getEntity() instanceof Player player))
            return;

        // dont cancel end crystal damage etc
        if (!(event.getDamager() instanceof LivingEntity))
            return;

        BWorld bWorld = plugin.getBWorldManager().getBWorld(player.getLocation().getWorld());
        if (bWorld == null)
            return;

        if (bWorld.isAggressiveMonsters())
            return;

        event.setCancelled(true);
    }

}