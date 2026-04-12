package com.comonier.netheritesilkspawners;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Handles spawner mob type changes using Spawn Eggs with strict 
 * permission and territory validation.
 */
public class SpawnerChangeListener implements Listener {
    private final Main plugin;

    public SpawnerChangeListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onSpawnerChange(PlayerInteractEvent event) {
        // Global toggle check
        if (!plugin.getConfig().getBoolean("allow-egg-change")) return;

        // Ensure the player is right-clicking a block
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        Block block = event.getClickedBlock();
        if (block == null || block.getType() != Material.SPAWNER) return;

        ItemStack item = event.getItem();
        // Check if the item held is a valid Spawn Egg
        if (item == null || !item.getType().name().contains("_SPAWN_EGG")) return;

        Player player = event.getPlayer();

        // 1. Territory Protection Check (WorldGuard, GP, RedProtect)
        if (!plugin.canBuildHere(player, block)) {
            player.sendMessage(plugin.getMsg("no-permission-region"));
            event.setCancelled(true);
            return;
        }

        // Identify the mob type from the Egg material name
        String typeName = item.getType().name().replace("_SPAWN_EGG", "").toLowerCase();
        
        // 2. Permission Check (Global or Specific Mob Type)
        boolean hasChangePerm = !plugin.getConfig().getBoolean("require-change-permission") 
                || (player.hasPermission("nss.change.all") || player.hasPermission("nss.change." + typeName));

        if (hasChangePerm) {
            if (block.getState() instanceof CreatureSpawner spawner) {
                try {
                    EntityType type = EntityType.valueOf(typeName.toUpperCase());
                    spawner.setSpawnedType(type);
                    spawner.update();

                    // Consume the egg if not in Creative mode
                    if (player.getGameMode() != GameMode.CREATIVE) {
                        item.setAmount(item.getAmount() - 1);
                    }

                    plugin.handleAction(player, "CHANGE", typeName);
                    
                    // Cancel event to prevent vanilla spawner egg placement/replacement
                    event.setCancelled(true);
                } catch (IllegalArgumentException ignored) {
                    // Silently fail if the material name doesn't match an EntityType
                }
            }
        } else {
            // Block the action and notify the player via Smart Status
            player.sendMessage(plugin.getMsg("deny-header"));
            player.sendMessage(plugin.getMsg("deny-perm")
                    .replace("%type%", typeName)
                    .replace("%status%", plugin.getMsg("status-off")));
            
            event.setCancelled(true);
        }
    }
}
