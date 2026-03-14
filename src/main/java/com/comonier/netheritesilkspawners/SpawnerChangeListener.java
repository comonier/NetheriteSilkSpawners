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

public class SpawnerChangeListener implements Listener {
    private final Main plugin;
    public SpawnerChangeListener(Main plugin) { this.plugin = plugin; }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onSpawnerChange(PlayerInteractEvent event) {
        if (plugin.getConfig().getBoolean("allow-egg-change") == false) return;
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        Block block = event.getClickedBlock();
        if (block == null || block.getType() != Material.SPAWNER) return;
        ItemStack item = event.getItem();
        if (item == null || item.getType().name().contains("_SPAWN_EGG") == false) return;

        Player player = event.getPlayer();
        if (plugin.canBuildHere(player, block) == false) {
            player.sendMessage(plugin.getMsg("no-permission-region"));
            event.setCancelled(true);
            return;
        }

        String typeName = item.getType().name().replace("_SPAWN_EGG", "").toLowerCase();
        boolean hasChangePerm = !plugin.getConfig().getBoolean("require-change-permission") || (player.hasPermission("nss.change.all") || player.hasPermission("nss.change." + typeName));

        if (hasChangePerm) {
            if (block.getState() instanceof CreatureSpawner spawner) {
                try {
                    EntityType type = EntityType.valueOf(typeName.toUpperCase());
                    spawner.setSpawnedType(type);
                    spawner.update();
                    if (player.getGameMode() != GameMode.CREATIVE) item.setAmount(item.getAmount() - 1);
                    plugin.handleAction(player, "CHANGE", typeName);
                    event.setCancelled(true);
                } catch (Exception ignored) {}
            }
        } else {
            player.sendMessage(plugin.getMsg("deny-header"));
            player.sendMessage(plugin.getMsg("deny-perm").replace("%type%", typeName).replace("%status%", plugin.getMsg("status-off")));
            event.setCancelled(true);
        }
    }
}
