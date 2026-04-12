package com.comonier.netheritesilkspawners;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;

/**
 * Handles spawner placement with strict permission checks 
 * and protection against NullPointerExceptions (NBT Safety).
 */
public class SpawnerPlaceListener implements Listener {
    private final Main plugin;

    public SpawnerPlaceListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onSpawnerPlace(BlockPlaceEvent event) {
        Block block = event.getBlock();
        if (block.getType() != Material.SPAWNER) return;

        Player player = event.getPlayer();

        // 1. Territory Protection Check (WorldGuard, GP, RedProtect)
        if (!plugin.canBuildHere(player, block)) {
            player.sendMessage(plugin.getMsg("no-permission-region"));
            event.setCancelled(true);
            return;
        }

        ItemStack item = event.getItemInHand();
        if (item.getItemMeta() instanceof BlockStateMeta meta && meta.getBlockState() instanceof CreatureSpawner spawnerMeta) {
            
            // NBT Safety: Check if SpawnedType exists, default to PIG if null
            EntityType entityType = spawnerMeta.getSpawnedType();
            String type = (entityType != null) ? entityType.name().toLowerCase() : "pig";

            // 2. Specific Permission Check
            boolean hasPlacePerm = !plugin.getConfig().getBoolean("require-place-permission") 
                    || (player.hasPermission("nss.place.all") || player.hasPermission("nss.place." + type));

            if (hasPlacePerm) {
                if (block.getState() instanceof CreatureSpawner spawner) {
                    // Force the spawner block to have the mob type from the item NBT
                    spawner.setSpawnedType(entityType != null ? entityType : EntityType.PIG);
                    spawner.update();
                    plugin.handleAction(player, "PLACE", type);
                }
            } else {
                // Block placement and send Smart Deny status
                player.sendMessage(plugin.getMsg("deny-header"));
                player.sendMessage(plugin.getMsg("deny-perm")
                        .replace("%type%", type)
                        .replace("%status%", plugin.getMsg("status-off")));
                event.setCancelled(true);
            }
        } else {
            // Handle generic spawner items (no metadata)
            boolean hasGenericPerm = !plugin.getConfig().getBoolean("require-place-permission") 
                    || player.hasPermission("nss.place.all");
            
            if (!hasGenericPerm) {
                player.sendMessage(plugin.getMsg("no-permission"));
                event.setCancelled(true);
            }
        }
    }
}
