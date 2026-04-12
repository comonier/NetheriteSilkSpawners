package com.comonier.netheritesilkspawners;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.persistence.PersistentDataType;
import java.util.List;

/**
 * Handles spawner mining and ensures the legitimate tag follows the item.
 */
public class SpawnerBreakListener implements Listener {
    private final Main plugin;

    public SpawnerBreakListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onSpawnerBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        if (block.getType() != Material.SPAWNER) return;

        Player player = event.getPlayer();
        
        if (!plugin.canBuildHere(player, block)) {
            player.sendMessage(plugin.getMsg("no-permission-region"));
            event.setCancelled(true);
            return;
        }

        if (block.getState() instanceof CreatureSpawner spawner) {
            String type = spawner.getSpawnedType() != null ? spawner.getSpawnedType().name().toLowerCase() : "pig";
            ItemStack itemHand = player.getInventory().getItemInMainHand();
            List<String> allowedTools = plugin.getConfig().getStringList("allowed-pickaxes");
            String toolUsed = itemHand.getType().name();

            boolean hasTool = allowedTools.contains(toolUsed);
            boolean hasToolPerm = !plugin.getConfig().getBoolean("require-tool-permission") || player.hasPermission("nss.tool." + toolUsed.toLowerCase());
            boolean hasSilk = !plugin.getConfig().getBoolean("require-silk-touch") || itemHand.containsEnchantment(Enchantment.SILK_TOUCH);
            boolean hasCollectPerm = !plugin.getConfig().getBoolean("require-collect-permission") || (player.hasPermission("nss.collect.all") || player.hasPermission("nss.collect." + type));

            if (hasTool && hasToolPerm && hasSilk && hasCollectPerm) {
                if (plugin.getConfig().getBoolean("auto-inventory")) {
                    if (player.getInventory().firstEmpty() == -1) {
                        player.sendMessage(plugin.getMsg("inventory-full"));
                        event.setCancelled(true);
                        return;
                    }
                    event.setExpToDrop(0);
                    event.setDropItems(false);
                    giveSpawnerDirectly(player, spawner);
                    plugin.handleAction(player, "COLLECT", type);
                } else {
                    event.setExpToDrop(0);
                    event.setDropItems(false);
                    dropSpawnerOnGround(block, spawner);
                    plugin.handleAction(player, "COLLECT", type);
                }
            } else {
                sendSmartDeny(player, type, toolUsed, hasTool && hasToolPerm, hasSilk, hasCollectPerm);
                event.setCancelled(true);
            }
        }
    }

    private void giveSpawnerDirectly(Player player, CreatureSpawner spawner) {
        ItemStack item = createSpawnerItem(spawner);
        player.getInventory().addItem(item);
    }

    private void dropSpawnerOnGround(Block block, CreatureSpawner spawner) {
        ItemStack item = createSpawnerItem(spawner);
        block.getWorld().dropItemNaturally(block.getLocation(), item);
    }

    private ItemStack createSpawnerItem(CreatureSpawner spawner) {
        ItemStack item = new ItemStack(Material.SPAWNER);
        BlockStateMeta meta = (BlockStateMeta) item.getItemMeta();
        if (meta != null) {
            CreatureSpawner state = (CreatureSpawner) meta.getBlockState();
            state.setSpawnedType(spawner.getSpawnedType());
            
            // Critical: Pass the legitimacy tag from the block to the item PDC
            state.getPersistentDataContainer().set(Main.SPAWNER_KEY, PersistentDataType.BYTE, (byte) 1);
            
            meta.setBlockState(state);
            item.setItemMeta(meta);
        }
        return item;
    }

    private void sendSmartDeny(Player player, String type, String tool, boolean toolOk, boolean silkOk, boolean permOk) {
        String on = plugin.getRawMsg("status-on");
        String off = plugin.getRawMsg("status-off");
        player.sendMessage(plugin.getMsg("deny-header"));
        player.sendMessage(plugin.getMsg("deny-tool").replace("%tool%", tool.replace("_", " ")).replace("%status%", toolOk ? on : off));
        player.sendMessage(plugin.getMsg("deny-perm").replace("%type%", type).replace("%status%", permOk ? on : off));
        player.sendMessage(plugin.getMsg("deny-silk").replace("%status%", silkOk ? on : off));
    }
}
