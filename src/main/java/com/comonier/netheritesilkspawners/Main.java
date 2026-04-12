package com.comonier.netheritesilkspawners;

import br.net.fabiozumbi12.RedProtect.Bukkit.RedProtect;
import br.net.fabiozumbi12.RedProtect.Bukkit.Region;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Main extends JavaPlugin implements CommandExecutor, TabCompleter {

    private FileConfiguration messages;
    private DiscordWebhook discord;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    /**
     * NamespacedKey used to tag legitimate spawners via PersistentDataContainer.
     */
    public static NamespacedKey SPAWNER_KEY;

    @Override
    public void onEnable() {
        // Initialize the NBT key
        SPAWNER_KEY = new NamespacedKey(this, "legitimate_spawner");

        saveDefaultConfig();
        // Saving language templates
        saveResource("messages_pt.yml", false);
        saveResource("messages_en.yml", false);
        saveResource("messages_es.yml", false);
        saveResource("messages_ru.yml", false);
        
        loadMessages();
        this.discord = new DiscordWebhook(this);
        
        File logFolder = new File(getDataFolder(), "logs");
        if (!logFolder.exists()) logFolder.mkdirs();

        // Registering all armored listeners
        getServer().getPluginManager().registerEvents(new SpawnerBreakListener(this), this);
        getServer().getPluginManager().registerEvents(new SpawnerChangeListener(this), this);
        getServer().getPluginManager().registerEvents(new SpawnerPlaceListener(this), this);
        getServer().getPluginManager().registerEvents(new ExplosionListener(this), this);
        getServer().getPluginManager().registerEvents(new SpawnerSecurityListener(this), this);
        getServer().getPluginManager().registerEvents(new SpawnerGlobalBlocker(this), this);

        if (getCommand("nss") != null) {
            getCommand("nss").setExecutor(this);
            getCommand("nss").setTabCompleter(this);
        }
    }

    public void loadMessages() {
        reloadConfig();
        String lang = getConfig().getString("language", "en");
        File langFile = new File(getDataFolder(), "messages_" + lang + ".yml");
        if (!langFile.exists()) langFile = new File(getDataFolder(), "messages_en.yml");
        messages = YamlConfiguration.loadConfiguration(langFile);
    }

    /**
     * Gets a formatted message with the prefix.
     */
    public String getMsg(String path) {
        String prefix = messages.getString("prefix", "");
        String msg = messages.getString(path, "Message not found: " + path);
        return ChatColor.translateAlternateColorCodes('&', prefix + msg);
    }

    /**
     * Gets a formatted message WITHOUT the prefix.
     * Prevents double prefixing in Smart Status updates.
     */
    public String getRawMsg(String path) {
        String msg = messages.getString(path, path);
        return ChatColor.translateAlternateColorCodes('&', msg);
    }

    /**
     * Advanced protection check for WorldGuard, GriefPrevention, and RedProtect.
     * Validates if a player is authorized to build/break in a specific location.
     */
    public boolean canBuildHere(Player player, Block block) {
        if (player.hasPermission("nss.admin")) return true;

        // WorldGuard Integration
        if (Bukkit.getPluginManager().isPluginEnabled("WorldGuard")) {
            LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(player);
            RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
            RegionQuery query = container.createQuery();
            if (!query.testBuild(BukkitAdapter.adapt(block.getLocation()), localPlayer)) {
                return false;
            }
        }

        // GriefPrevention Integration
        if (Bukkit.getPluginManager().isPluginEnabled("GriefPrevention")) {
            Claim claim = GriefPrevention.instance.dataStore.getClaimAt(block.getLocation(), false, null);
            if (claim != null && claim.allowBuild(player, Material.SPAWNER) != null) {
                return false;
            }
        }

        // RedProtect Integration (v8.x)
        if (Bukkit.getPluginManager().isPluginEnabled("RedProtect")) {
            Region region = RedProtect.get().getAPI().getRegion(block.getLocation());
            if (region != null && !region.canBuild(player)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Handles global actions, broadcasts, and Discord Webhook logging.
     */
    public void handleAction(Player player, String actionType, String type) {
        String playerName = (player != null) ? player.getName() : "Natural/Grief";
        String spawnerName = (type != null) ? type.replace("_", " ").toLowerCase() : "unknown";

        if (player != null) logAction(player, actionType, spawnerName);

        if (getConfig().getBoolean("announce-globally")) {
            String msgKey = "announce-" + actionType.toLowerCase();
            String annMsg = getMsg(msgKey).replace("%player%", playerName).replace("%type%", spawnerName);
            Bukkit.broadcastMessage(annMsg);
        }

        String discordMsg = "**[NSS]** Action: `" + actionType + "` | Target: `" + spawnerName + "` | Origin: `" + playerName + "`";
        discord.sendAsync(discordMsg);

        if (player != null) {
            String feedbackKey = actionType.toLowerCase() + "-success";
            player.sendMessage(getMsg(feedbackKey).replace("%type%", spawnerName));
        }
    }

    public void logAction(Player player, String action, String spawnerType) {
        String time = LocalDateTime.now().format(formatter);
        String entry = "[" + time + "] Action: " + action + " | Type: " + spawnerType;
        
        File userLog = new File(getDataFolder() + "/logs", player.getUniqueId() + ".txt");
        try (PrintWriter out = new PrintWriter(new FileWriter(userLog, true))) {
            out.println(entry);
        } catch (IOException ignored) {}

        File globalLogFile = new File(getDataFolder(), "logs.yml");
        FileConfiguration globalLog = YamlConfiguration.loadConfiguration(globalLogFile);
        String path = "logs." + System.currentTimeMillis();
        globalLog.set(path + ".player", player.getName());
        globalLog.set(path + ".action", action);
        globalLog.set(path + ".type", spawnerType);
        try {
            globalLog.save(globalLogFile);
        } catch (IOException ignored) {}
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("nss.admin")) {
                sender.sendMessage(getMsg("no-permission"));
                return true;
            }
            loadMessages();
            sender.sendMessage(getMsg("reload-success"));
            return true;
        }
        sender.sendMessage(getMsg("invalid-syntax"));
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1 && sender.hasPermission("nss.admin")) {
            if ("reload".startsWith(args[0].toLowerCase())) completions.add("reload");
        }
        return completions;
    }
}
