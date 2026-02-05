package me.comonier.netheritesilkspawners; // Certifique-se que 'comonier' é seu nick do GitHub

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        // Registra os eventos para que o servidor saiba que deve ouvir este plugin
        getServer().getPluginManager().registerEvents(this, this);
        getLogger().info("NetheriteSilkSpawners ativado com sucesso!");
    }

    @EventHandler
    public void onSpawnerBreak(BlockBreakEvent event) {
        Block block = event.getBlock();

        // 1. Verifica se o bloco é um Spawner
        if (block.getType() != Material.SPAWNER) return;

        Player player = event.getPlayer();
        ItemStack itemInHand = player.getInventory().getItemInMainHand();

        // 2. Verifica se está usando Picareta de Netherite com Silk Touch (Toque de Seda)
        if (itemInHand.getType() == Material.NETHERITE_PICKAXE && 
            itemInHand.containsEnchantment(Enchantment.SILK_TOUCH)) {

            // Obtém os dados do Spawner (qual mob ele gera)
            if (block.getState() instanceof CreatureSpawner spawner) {
                
                // Cria o item do Spawner para dropar
                ItemStack spawnerItem = new ItemStack(Material.SPAWNER);
                BlockStateMeta meta = (BlockStateMeta) spawnerItem.getItemMeta();
                
                if (meta != null) {
                    // Copia o tipo de mob do bloco para o item
                    CreatureSpawner state = (CreatureSpawner) meta.getBlockState();
                    state.setSpawnedType(spawner.getSpawnedType());
                    meta.setBlockState(state);
                    
                    // Aplica as informações no item
                    spawnerItem.setItemMeta(meta);
                    
                    // Cancela o drop de XP (opcional) e dropa o item no chão
                    event.setExpToDrop(0);
                    block.getWorld().dropItemNaturally(block.getLocation(), spawnerItem);
                }
            }
        }
    }
}
