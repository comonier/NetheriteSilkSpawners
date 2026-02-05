package me.seunome.netheritesilkspawners;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        saveDefaultConfig(); // Cria o config.yml se não existir
        getServer().getPluginManager().registerEvents(this, this);
        getLogger().info("NetheriteSilkSpawners v2.0 carregado!");
    }

    @EventHandler
    public void onSpawnerBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        if (block.getType() != Material.SPAWNER) return;

        // Lendo configurações
        String reqTool = getConfig().getString("required-pickaxe", "NETHERITE_PICKAXE");
        boolean reqSilk = getConfig().getBoolean("require-silk-touch", true);

        ItemStack item = event.getPlayer().getInventory().getItemInMainHand();

        // Verificação de Ferramenta e Encantamento
        if (item.getType().name().equals(reqTool)) {
            if (!reqSilk || item.containsEnchantment(Enchantment.SILK_TOUCH)) {
                
                if (block.getState() instanceof CreatureSpawner spawner) {
                    dropSpawner(block, spawner);
                    event.setExpToDrop(0);
                    
                    String msg = getConfig().getString("collect-message");
                    if (msg != null && !msg.isEmpty()) {
                        event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', 
                            msg.replace("%type%", spawner.getSpawnedType().name())));
                    }
                }
            }
        }
    }

    // --- PROTEÇÃO CONTRA EXPLOSÕES (TNT, Creeper, Wither, etc) ---
    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        if (getConfig().getBoolean("prevent-explosion-drop")) {
            // Remove spawners da lista de blocos que seriam destruídos pela explosão
            event.blockList().removeIf(block -> block.getType() == Material.SPAWNER);
        }
    }

    @EventHandler
    public void onBlockExplode(BlockExplodeEvent event) {
        if (getConfig().getBoolean("prevent-explosion-drop")) {
            event.blockList().removeIf(block -> block.getType() == Material.SPAWNER);
        }
    }

    private void dropSpawner(Block block, CreatureSpawner spawner) {
        ItemStack item = new ItemStack(Material.SPAWNER);
        BlockStateMeta meta = (BlockStateMeta) item.getItemMeta();
        if (meta != null) {
            CreatureSpawner state = (CreatureSpawner) meta.getBlockState();
            state.setSpawnedType(spawner.getSpawnedType());
            meta.setBlockState(state);
            item.setItemMeta(meta);
            block.getWorld().dropItemNaturally(block.getLocation(), item);
        }
    }
}
