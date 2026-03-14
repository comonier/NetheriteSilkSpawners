package com.comonier.netheritesilkspawners;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;

public class SpawnerPlaceListener implements Listener {
    private final Main plugin;
    public SpawnerPlaceListener(Main plugin) { this.plugin = plugin; }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onSpawnerPlace(BlockPlaceEvent event) {
        Block block = event.getBlock();
        if (block.getType() != Material.SPAWNER) return;

        Player player = event.getPlayer();
        if (plugin.canBuildHere(player, block) == false) {
            player.sendMessage(plugin.getMsg("no-permission-region"));
            event.setCancelled(true);
            return;
        }

        ItemStack item = event.getItemInHand();
        if (item.getItemMeta() instanceof BlockStateMeta meta && meta.getBlockState() instanceof CreatureSpawner spawnerMeta) {
            String type = spawnerMeta.getSpawnedType().name().toLowerCase();
            boolean hasPlacePerm = !plugin.getConfig().getBoolean("require-place-permission") || (player.hasPermission("nss.place.all") || player.hasPermission("nss.place." + type));

            if (hasPlacePerm) {
                if (block.getState() instanceof CreatureSpawner spawner) {
                    spawner.setSpawnedType(spawnerMeta.getSpawnedType());
                    spawner.update();
                    plugin.handleAction(player, "PLACE", type);
                }
            } else {
                player.sendMessage(plugin.getMsg("deny-header"));
                player.sendMessage(plugin.getMsg("deny-perm").replace("%type%", type).replace("%status%", plugin.getMsg("status-off")));
                event.setCancelled(true);
            }
        }
    }
}
