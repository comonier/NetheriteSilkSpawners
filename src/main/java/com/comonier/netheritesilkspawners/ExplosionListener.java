package com.comonier.netheritesilkspawners;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

public class ExplosionListener implements Listener {

    private final Main plugin;

    public ExplosionListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityExplode(EntityExplodeEvent event) {
        if (plugin.getConfig().getBoolean("prevent-explosion-drop") == true) {
            event.blockList().removeIf(block -> block.getType() == Material.SPAWNER);
        } else {
            for (Block block : event.blockList()) {
                if (block.getType() == Material.SPAWNER) {
                    plugin.handleAction(null, "EXPLOSION", "entity_explosion");
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockExplode(BlockExplodeEvent event) {
        if (plugin.getConfig().getBoolean("prevent-explosion-drop") == true) {
            event.blockList().removeIf(block -> block.getType() == Material.SPAWNER);
        } else {
            for (Block block : event.blockList()) {
                if (block.getType() == Material.SPAWNER) {
                    plugin.handleAction(null, "EXPLOSION", "block_explosion");
                }
            }
        }
    }
}
