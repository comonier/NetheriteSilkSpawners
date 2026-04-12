package com.comonier.netheritesilkspawners;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

/**
 * Manages spawner protection against explosions (TNT, Creepers, Beds, etc).
 * Includes global announcements if a spawner is destroyed.
 */
public class ExplosionListener implements Listener {

    private final Main plugin;

    public ExplosionListener(Main plugin) {
        this.plugin = plugin;
    }

    /**
     * Handles explosions caused by entities (TNT, Creepers, Ghast Fireballs).
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityExplode(EntityExplodeEvent event) {
        if (plugin.getConfig().getBoolean("prevent-explosion-drop")) {
            // Remove spawners from the block list to prevent them from being broken
            event.blockList().removeIf(block -> block.getType() == Material.SPAWNER);
        } else {
            // If protection is off, log and announce the destruction
            for (Block block : event.blockList()) {
                if (block.getType() == Material.SPAWNER) {
                    plugin.handleAction(null, "EXPLOSION", "entity_explosion");
                }
            }
        }
    }

    /**
     * Handles explosions caused by blocks (Respawn Anchors, Beds in other dimensions).
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockExplode(BlockExplodeEvent event) {
        if (plugin.getConfig().getBoolean("prevent-explosion-drop")) {
            // Protect spawners by removing them from the explosion impact list
            event.blockList().removeIf(block -> block.getType() == Material.SPAWNER);
        } else {
            // If protection is off, log and announce the destruction
            for (Block block : event.blockList()) {
                if (block.getType() == Material.SPAWNER) {
                    plugin.handleAction(null, "EXPLOSION", "block_explosion");
                }
            }
        }
    }
}
