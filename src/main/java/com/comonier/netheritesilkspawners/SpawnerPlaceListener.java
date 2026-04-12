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
import org.bukkit.persistence.PersistentDataType;

/**
 * Handles spawner placement, NBT safety, and legitimacy tagging.
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

        if (!plugin.canBuildHere(player, block)) {
            player.sendMessage(plugin.getMsg("no-permission-region"));
            event.setCancelled(true);
            return;
        }

        ItemStack item = event.getItemInHand();
        if (item.getItemMeta() instanceof BlockStateMeta meta && meta.getBlockState() instanceof CreatureSpawner spawnerMeta) {
            
            EntityType entityType = spawnerMeta.getSpawnedType();
            String type = (entityType != null) ? entityType.name().toLowerCase() : "pig";

            boolean hasPlacePerm = !plugin.getConfig().getBoolean("require-place-permission") 
                    || (player.hasPermission("nss.place.all") || player.hasPermission("nss.place." + type));

            if (hasPlacePerm) {
                if (block.getState() instanceof CreatureSpawner spawner) {
                    // Force mob type
                    spawner.setSpawnedType(entityType != null ? entityType : EntityType.PIG);
                    
                    // Tag the block as legitimate using PDC
                    spawner.getPersistentDataContainer().set(Main.SPAWNER_KEY, PersistentDataType.BYTE, (byte) 1);
                    
                    spawner.update();
                    plugin.handleAction(player, "PLACE", type);
                }
            } else {
                String off = plugin.getRawMsg("status-off");
                player.sendMessage(plugin.getMsg("deny-header"));
                player.sendMessage(plugin.getMsg("deny-perm")
                        .replace("%type%", type)
                        .replace("%status%", off));
                event.setCancelled(true);
            }
        }
    }
}
