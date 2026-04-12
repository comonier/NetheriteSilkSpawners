package com.comonier.netheritesilkspawners;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Listener dedicated to hard-locking spawner modifications 
 * that happen outside of the standard plugin flow.
 */
public class SpawnerSecurityListener implements Listener {

    private final Main plugin;

    public SpawnerSecurityListener(Main plugin) {
        this.plugin = plugin;
    }

    /**
     * Prevents any spawner from being renamed or modified inside an Anvil.
     * This blocks players from trying to change spawner NBT or names illegally.
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onAnvilPrepare(PrepareAnvilEvent event) {
        ItemStack result = event.getResult();
        if (result == null || result.getType() != Material.SPAWNER) return;

        // Check if any of the input items are spawners
        for (ItemStack item : event.getInventory().getContents()) {
            if (item != null && item.getType() == Material.SPAWNER) {
                // Block the resulting item and notify the player (if any)
                event.setResult(null);
                if (event.getViewers().size() > 0) {
                    event.getViewers().get(0).sendMessage(plugin.getMsg("deny-anvil"));
                }
                break;
            }
        }
    }
}
