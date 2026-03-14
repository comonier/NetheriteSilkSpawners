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
import java.util.List;
import java.util.Map;

public class SpawnerBreakListener implements Listener {
    private final Main plugin;
    public SpawnerBreakListener(Main plugin) { this.plugin = plugin; }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onSpawnerBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        if (block.getType() != Material.SPAWNER) return;

        Player player = event.getPlayer();
        if (plugin.canBuildHere(player, block) == false) {
            player.sendMessage(plugin.getMsg("no-permission-region"));
            event.setCancelled(true);
            return;
        }

        if (block.getState() instanceof CreatureSpawner spawner) {
            String type = spawner.getSpawnedType().name().toLowerCase();
            ItemStack itemInHand = player.getInventory().getItemInMainHand();
            List<String> allowedTools = plugin.getConfig().getStringList("allowed-pickaxes");
            String toolUsed = itemInHand.getType().name();

            boolean hasTool = allowedTools.contains(toolUsed);
            boolean hasToolPerm = !plugin.getConfig().getBoolean("require-tool-permission") || player.hasPermission("nss.tool." + toolUsed.toLowerCase());
            boolean hasSilk = !plugin.getConfig().getBoolean("require-silk-touch") || itemInHand.containsEnchantment(Enchantment.SILK_TOUCH);
            boolean hasCollectPerm = !plugin.getConfig().getBoolean("require-collect-permission") || (player.hasPermission("nss.collect.all") || player.hasPermission("nss.collect." + type));

            if (hasTool && hasToolPerm && hasSilk && hasCollectPerm) {
                event.setExpToDrop(0);
                event.setDropItems(false);
                
                // Coleta direta para o inventário
                giveSpawner(player, block, spawner);
                
                plugin.handleAction(player, "COLLECT", type);
            } else {
                String on = plugin.getMsg("status-on");
                String off = plugin.getMsg("status-off");
                player.sendMessage(plugin.getMsg("deny-header"));
                player.sendMessage(plugin.getMsg("deny-tool").replace("%tool%", toolUsed.replace("_", " ")).replace("%status%", (hasTool && hasToolPerm) ? on : off));
                player.sendMessage(plugin.getMsg("deny-perm").replace("%type%", type).replace("%status%", hasCollectPerm ? on : off));
                player.sendMessage(plugin.getMsg("deny-silk").replace("%status%", hasSilk ? on : off));
                event.setCancelled(true);
            }
        }
    }

    private void giveSpawner(Player player, Block block, CreatureSpawner spawner) {
        ItemStack item = new ItemStack(Material.SPAWNER);
        BlockStateMeta meta = (BlockStateMeta) item.getItemMeta();
        if (meta != null) {
            CreatureSpawner state = (CreatureSpawner) meta.getBlockState();
            state.setSpawnedType(spawner.getSpawnedType());
            meta.setBlockState(state);
            item.setItemMeta(meta);
            
            // Tenta adicionar ao inventário
            Map<Integer, ItemStack> leftover = player.getInventory().addItem(item);
            
            // Se houver sobra (inventário cheio), dropa no chão na localização do bloco
            if (leftover.isEmpty() == false) {
                for (ItemStack stack : leftover.values()) {
                    block.getWorld().dropItemNaturally(block.getLocation(), stack);
                }
            }
        }
    }
}
