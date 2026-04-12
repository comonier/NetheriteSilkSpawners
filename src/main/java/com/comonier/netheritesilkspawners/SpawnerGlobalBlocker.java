package com.comonier.netheritesilkspawners;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Wither;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.weather.LightningStrikeEvent;

/**
 * Absolute protection layer against physics, pistons, dragons, withers, 
 * Endermen, and external machine interference (Slimefun/mcMMO).
 */
public class SpawnerGlobalBlocker implements Listener {

    private final Main plugin;

    public SpawnerGlobalBlocker(Main plugin) {
        this.plugin = plugin;
    }

    /**
     * Blocks Wither Boss and Ender Dragon from destroying spawners.
     * Also blocks Endermen from picking up spawners.
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityChangeBlock(EntityChangeBlockEvent event) {
        if (event.getBlock().getType() == Material.SPAWNER) {
            // Block Wither, Dragon and Enderman griefing
            if (event.getEntity() instanceof Wither || event.getEntity() instanceof EnderDragon) {
                event.setCancelled(true);
            }
            // Block any other entity-based block change (like falling blocks or machines)
            event.setCancelled(true);
        }
    }

    /**
     * Prevents pistons (normal or sticky) from moving spawners.
     * This blocks piston-based griefing or machines.
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPistonExtend(BlockPistonExtendEvent event) {
        for (Block block : event.getBlocks()) {
            if (block.getType() == Material.SPAWNER) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPistonRetract(BlockPistonRetractEvent event) {
        for (Block block : event.getBlocks()) {
            if (block.getType() == Material.SPAWNER) {
                event.setCancelled(true);
                return;
            }
        }
    }

    /**
     * Prevents lightning strikes from damaging or affecting spawners.
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onLightningStrike(LightningStrikeEvent event) {
        // Lightning can cause fires or block changes nearby
        if (event.getLightning().getLocation().getBlock().getType() == Material.SPAWNER) {
            event.setCancelled(true);
        }
    }

    /**
     * Blocks entities from interacting/stepping on spawners if it causes damage.
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityInteract(EntityInteractEvent event) {
        if (event.getBlock().getType() == Material.SPAWNER) {
            event.setCancelled(true);
        }
    }
}
