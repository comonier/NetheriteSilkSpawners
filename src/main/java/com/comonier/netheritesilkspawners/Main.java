package com.comonier.netheritesilkspawners;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import me.ryanhamshire.GriefPrevention.Claim;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
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

    @Override
    public void onEnable() {
        saveDefaultConfig();
        saveResource("messages_pt.yml", false);
        saveResource("messages_en.yml", false);
        loadMessages();
        this.discord = new DiscordWebhook(this);
        File logFolder = new File(getDataFolder(), "logs");
        if (logFolder.exists() == false) logFolder.mkdirs();
        getServer().getPluginManager().registerEvents(new SpawnerBreakListener(this), this);
        getServer().getPluginManager().registerEvents(new SpawnerChangeListener(this), this);
        getServer().getPluginManager().registerEvents(new SpawnerPlaceListener(this), this);
        getServer().getPluginManager().registerEvents(new ExplosionListener(this), this);
        if (getCommand("nss") != null) {
            getCommand("nss").setExecutor(this);
            getCommand("nss").setTabCompleter(this);
        }
    }

    public void loadMessages() {
        reloadConfig();
        String lang = getConfig().getString("language", "pt");
        File langFile = new File(getDataFolder(), "messages_" + lang + ".yml");
        if (langFile.exists() == false) langFile = new File(getDataFolder(), "messages_en.yml");
        messages = YamlConfiguration.loadConfiguration(langFile);
    }

    public String getMsg(String path) {
        String prefix = messages.getString("prefix", "");
        String msg = messages.getString(path, "Message not found: " + path);
        return ChatColor.translateAlternateColorCodes('&', prefix + msg);
    }

    public boolean canBuildHere(Player player, Block block) {
        if (player.hasPermission("nss.admin")) return true;
        if (Bukkit.getPluginManager().isPluginEnabled("WorldGuard")) {
            RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
            RegionQuery query = container.createQuery();
            if (query.testBuild(BukkitAdapter.adapt(block.getLocation()), WorldGuardPlugin.inst().wrapPlayer(player)) == false) return false;
        }
        if (Bukkit.getPluginManager().isPluginEnabled("GriefPrevention")) {
            Claim claim = GriefPrevention.instance.dataStore.getClaimAt(block.getLocation(), false, null);
            if (claim != null && claim.allowBuild(player, Material.SPAWNER) != null) return false;
        }
        return true;
    }

    public void handleAction(Player player, String actionType, String type) {
        String playerName = (player != null) ? player.getName() : "Natural/Explosion";
        String spawnerName = type.replace("_", " ").toLowerCase();
        if (player != null) logAction(player, actionType, type);
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
        try (PrintWriter out = new PrintWriter(new FileWriter(userLog, true))) { out.println(entry); } catch (IOException ignored) {}
        File globalLogFile = new File(getDataFolder(), "logs.yml");
        FileConfiguration globalLog = YamlConfiguration.loadConfiguration(globalLogFile);
        String path = "logs." + System.currentTimeMillis();
        globalLog.set(path + ".player", player.getName());
        globalLog.set(path + ".action", action);
        globalLog.set(path + ".type", spawnerType);
        try { globalLog.save(globalLogFile); } catch (IOException ignored) {}
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            if (sender.hasPermission("nss.admin") == false) {
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
