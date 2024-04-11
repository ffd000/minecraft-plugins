package net.fricktastic.chestprotector;

import me.lucko.luckperms.api.LuckPermsApi;
import net.fricktastic.chestprotector.command.ChestCommand;
import net.fricktastic.chestprotector.command.ChestTabCompleter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public final class ChestProtector extends JavaPlugin
{
    FileConfiguration config;
    private FileConfiguration messages;

    private BukkitTask regenerationTask;

    public LuckPermsApi luckperms;

    @Override
    public void onEnable()
    {
        getCommand("chest").setExecutor(new ChestCommand(this));
        getCommand("chest").setTabCompleter(new ChestTabCompleter());

        getServer().getPluginManager().registerEvents(new EventListener(this), this);

        new ChestManager(this);

        getLogger().info(ChatColor.GREEN + "Successfully enabled.");

        try {
            ChestStore.connectionString = "jdbc:sqlite:" + getDataFolder() + File.separator + "chests.db";
            ChestStore.getConnection();

            getLogger().info(ChatColor.AQUA + "Connection to database has been established. Migrating tables...");
            ChestStore.migrate();

        } catch (SQLException e) {
            getLogger().warning(e.getMessage());
        }

        getDataFolder().mkdirs();

        loadConfig();
        loadLanguageConfig();

        int interval = 20 * config.getInt("regenerationInterval");
        regenerationTask = new ChestRegenerationTask(this).runTaskTimer(this, 0L, interval);

        RegisteredServiceProvider<LuckPermsApi> provider = Bukkit.getServicesManager().getRegistration(LuckPermsApi.class);
        if (provider != null) {
            luckperms = provider.getProvider();
        } else {
            getLogger().warning("Failed to load LuckPerms API");
        }
    }

    @Override
    public void onDisable()
    {
        regenerationTask.cancel();

        ChestStore.close();
    }

    public String getMessage(String path)
    {
        return messages.isSet(path) ? messages.getString(path).replaceAll("&", "§") : path;
    }

    public void loadConfig()
    {
        File configFile = new File(getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            saveResource("config.yml", false);
        }

        config = new YamlConfiguration();
        try {
            config.load(configFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadLanguageConfig()
    {
        File messagesFile = new File(getDataFolder(), "messages.yml");
        if (!messagesFile.exists()) {
            saveResource("messages.yml", false);
        }

        messages = new YamlConfiguration();
        try {
            messages.load(messagesFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadOldChestData(int fileIndex)
    {
        File dataFile = new File(getDataFolder(), fileIndex + ".yml");
        if (!dataFile.exists()) {
            saveResource(fileIndex + ".yml", false);
        }

        FileConfiguration dataConfig = new YamlConfiguration();
        try {
            dataConfig.load(dataFile);

            Set<String> keys = dataConfig.getConfigurationSection("chests").getKeys(false);
            int total = keys.size();
            int processed = 0;
            double p = 0;
            getLogger().info(ChatColor.DARK_AQUA + "Processed 0/" + total + " | " + ChatColor.AQUA + "[0%]");
            for (String key : keys) {
                if (ChestStore.chestExists(key)) continue;

                String playersString = dataConfig.getString("chests." + key + ".allowedPlayers");
                List<String> players = new ArrayList<>();

                if (!playersString.equals("[]")) {
                    Collections.addAll(players, playersString.split(","));
                }

                String serializedPlayers = String.join(",", players) + ",";

                ChestStore.addPrivateChest(key, dataConfig.getString("chests." + key + ".owner"), dataConfig.getBoolean("chests." + key + ".private"), serializedPlayers);

                processed++;
                int percent = processed * 100 / total;
                if (p == (p = Math.round(percent * 10.0) / 10.0)) continue;

                if (p % 5.0 == 0) {
                    getLogger().info(String.format(ChatColor.DARK_AQUA + "Processed %d/%d | " + ChatColor.AQUA + "[%d%%]", processed, total, (int)p));
                }
            }
        } catch (Exception e) {
            getLogger().warning(e.getMessage());
            e.printStackTrace();
        }
    }
}
