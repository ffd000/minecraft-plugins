package net.fricktastic.chestprotector;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.metadata.FixedMetadataValue;

import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class ChestManager
{
    private static ChestProtector plugin;

    ChestManager(ChestProtector plugin) { ChestManager.plugin = plugin; }

    static boolean removePrivateChest(String location)
    {
        try {
            ChestStore.removePrivateChest(location);

            return true;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();

            return false;
        }
    }

    static void setChestHealth(Chest chest)
    {
        double health = calculateChestValue(chest);

        if (chest.hasMetadata("maxHealth")) {
            if (health == chest.getMetadata("maxHealth").get(0).asDouble()) return;
        }

        chest.setMetadata("health", new FixedMetadataValue(plugin, health));
        chest.setMetadata("maxHealth", new FixedMetadataValue(plugin, health));
    }

    static String getPlayerNames(List<UUID> players)
    {
        return players.stream()
                .map(ChestManager::getOfflinePlayerName)
                .collect(Collectors.joining(", "));
    }

    static List<String> serializePlayers(List<UUID> players)
    {
        return players.stream()
                .map(UUID::toString)
                .collect(Collectors.toList());
    }

    static void setChestPrivacy(Chest chest, boolean isPrivate) throws SQLException
    {
        InventoryHolder holder = chest.getInventory().getHolder();
        if (holder instanceof DoubleChest) {
            Chest leftSide = (Chest) ((DoubleChest) holder).getLeftSide();
            Chest rightSide = (Chest) ((DoubleChest) holder).getRightSide();

            if (leftSide != null) {
                leftSide.setMetadata("private", new FixedMetadataValue(plugin, isPrivate));
                ChestStore.changeChestPrivacy(serializeChestLocation(leftSide.getLocation()), isPrivate);
            }

            if (rightSide != null) {
                rightSide.setMetadata("private", new FixedMetadataValue(plugin, isPrivate));
                ChestStore.changeChestPrivacy(serializeChestLocation(rightSide.getLocation()), isPrivate);
            }
        } else {
            chest.setMetadata("private", new FixedMetadataValue(plugin, isPrivate));
            ChestStore.changeChestPrivacy(serializeChestLocation(chest.getLocation()), isPrivate);
        }
    }

    static boolean initSingleChestData(Chest chest)
    {
        try {
            List<Object> data = ChestStore.getChestData(serializeChestLocation(chest.getLocation()));
            if (data == null) return false;

            double health = calculateChestValue(chest);

            chest.setMetadata("owner", new FixedMetadataValue(plugin, data.get(0)));
            chest.setMetadata("health", new FixedMetadataValue(plugin, health));
            chest.setMetadata("maxHealth", new FixedMetadataValue(plugin, health));
            chest.setMetadata("private", new FixedMetadataValue(plugin, data.get(1)));
            chest.setMetadata("allowedPlayers", new FixedMetadataValue(plugin, data.get(2)));
            chest.setMetadata("group", new FixedMetadataValue(plugin, data.get(4)));

            if ((int)data.get(3) == 1) {
                chest.setMetadata("flag_redstone", new FixedMetadataValue(plugin, true));
            }

            return true;
        } catch (SQLException e) {
            plugin.getLogger().warning(e.getMessage());
            e.printStackTrace();

            return false;
        }
    }

    static boolean initChestData(Chest firstSide)
    {
        if (!firstSide.hasMetadata("owner")) {
            InventoryHolder holder = firstSide.getInventory().getHolder();
            if (holder instanceof DoubleChest) {
                Chest leftSide = (Chest)((DoubleChest) holder).getLeftSide();
                Chest rightSide = (Chest)((DoubleChest) holder).getRightSide();

                boolean result = false;

                if (leftSide != null)
                    result = ChestManager.initSingleChestData(leftSide);

                if (rightSide != null)
                    result = result && ChestManager.initSingleChestData(rightSide);

                return result;
            } else {
                return ChestManager.initSingleChestData(firstSide);
            }
        }
        return true;
    }

    private static double calculateChestValue(Chest chest)
    {
        ItemStack[] items = chest.getBlockInventory().getContents();
        double totalValue = 0.0;
        for (ItemStack item : items) {
            if (item == null) continue; // Empty slot

            String itemName = item.getType().toString().toLowerCase();
            if (!plugin.config.isSet("items.materials." + itemName)) {
                itemName = "other";
            }

            double value = plugin.config.getDouble("items.materials." + itemName);
            int amount = item.getAmount();
            totalValue += value * amount;

            Map<Enchantment, Integer> enchants;
            if(item.getItemMeta() instanceof EnchantmentStorageMeta) {
                EnchantmentStorageMeta meta = (EnchantmentStorageMeta) item.getItemMeta();
                enchants = meta.getStoredEnchants();
            } else {
                enchants = item.getEnchantments();
            }

            for (Map.Entry<Enchantment, Integer> entry : enchants.entrySet()) {
                String enchName = entry.getKey().getKey().toString().replaceAll("minecraft:", "").toLowerCase();
                if (!plugin.config.isSet("items.enchants." + enchName)) {
                    enchName = "other";
                }
                int enchValue = plugin.config.getInt("items.enchants." + enchName);
                int level = entry.getValue();
                totalValue += enchValue * level;
            }
        }

        return totalValue;
    }

    public static String getOfflinePlayerName(UUID uuid)
    {
        OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);

        return player == null ? "null" : player.getName();
    }

    static void placeChest(Chest chest, UUID uuid, boolean privacy, ArrayList<String> players, String group) throws SQLException
    {
        ChestStore.addPrivateChest(
                ChestManager.serializeChestLocation(chest.getLocation()),
                uuid.toString()
        );

        chest.setMetadata("owner", new FixedMetadataValue(plugin, uuid));
        chest.setMetadata("health", new FixedMetadataValue(plugin, 0.0));
        chest.setMetadata("maxHealth", new FixedMetadataValue(plugin, 0.0));
        chest.setMetadata("private", new FixedMetadataValue(plugin, privacy));
        chest.setMetadata("allowedPlayers", new FixedMetadataValue(plugin, players));
        chest.setMetadata("group", new FixedMetadataValue(plugin, group));
    }

    static void broadcastToAllStaff(String message)
    {
        System.out.println(message);

        for (Player player : plugin.getServer().getOnlinePlayers()) {
            if (!player.hasPermission("chestprotector.notify")) continue;

            player.sendMessage(message);
        }
    }

    static String serializeChestLocation(Location loc)
    {
        return loc.getWorld().getName() + "," +
                loc.getBlockX() + "," +
                loc.getBlockY() + "," +
                loc.getBlockZ();
    }

    static boolean isOwner(Chest chest, UUID uuid)
    {
        return chest.getMetadata("owner").get(0).value().equals(uuid);
    }

    static Block getInventoryBlock(Inventory inventory)
    {
        InventoryHolder holder = inventory.getHolder();
        if (holder instanceof BlockState) {
            return ((BlockState) holder).getBlock();
        }

        if (holder instanceof DoubleChest) {
            InventoryHolder leftHolder = ((DoubleChest) holder).getLeftSide();
            if (leftHolder instanceof BlockState) {
                return ((BlockState) leftHolder).getBlock();
            }
        }
        return null;
    }
}
