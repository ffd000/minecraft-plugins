package net.fricktastic.chestprotector;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.*;
import org.bukkit.entity.Player;
import org.bukkit.entity.minecart.HopperMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.metadata.FixedMetadataValue;

import java.sql.SQLException;
import java.util.*;

public class EventListener implements Listener
{
    private final ChestProtector plugin;

    public static Map<String, List<Object>> chestUpdate = new HashMap<>();

    public enum ChestState {
        SetFlag,
        Inspect,
        Delete,
        TransferOwner,
        SetPrivate,
        SetPublic,
        AllowPlayer,
        RemovePlayer,
        AddGroup,
        RemoveGroup
    }

    EventListener(ChestProtector plugin) { this.plugin = plugin; }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event)
    {
        Block block = event.getBlockPlaced();
        if (block.getType() == Material.CHEST
        || block.getType() == Material.TRAPPED_CHEST) {
            Player player = event.getPlayer();
            Chest chest = (Chest)block.getState();

            List<Block> blocksAround = Arrays.asList(
                    block.getRelative(BlockFace.WEST),
                    block.getRelative(BlockFace.NORTH),
                    block.getRelative(BlockFace.EAST),
                    block.getRelative(BlockFace.SOUTH)
            );

            boolean foundProtectedChest = false;
            Chest chestSide = null;
            // Check if there is a chest around the block we're placing.
            for (Block blockAround : blocksAround) {
                if (blockAround.getType() == Material.CHEST) {
                    if (blockAround.hasMetadata("owner")) {
                        if (!ChestManager.isOwner((Chest)blockAround.getState(), player.getUniqueId())) {
                            foundProtectedChest = true;
                            break;
                        } else {
                            chestSide = (Chest)blockAround.getState();
                        }
                    }
                }
            }

            if (foundProtectedChest) {
                player.sendMessage(plugin.getMessage("cannotPlaceChest"));

                event.setCancelled(true);
            } else {
                UUID uuid = player.getUniqueId();
                try {
                    if (chestSide != null) {
                        ChestManager.placeChest(chest, uuid,
                                chestSide.getMetadata("private").get(0).asBoolean(),
                                (ArrayList<String>)chestSide.getMetadata("allowedPlayers").get(0).value(),
                                chestSide.getMetadata("group").get(0).asString()
                        );
                    } else {
                        ChestManager.placeChest(chest, uuid, true, new ArrayList<>(), "");
                    }

                    player.sendMessage(plugin.getMessage("chestPlaced"));
                } catch (SQLException e) {
                    if (ChestManager.removePrivateChest(ChestManager.serializeChestLocation(chest.getLocation()))) {
                        try {
                            ChestManager.placeChest(chest, uuid, true, new ArrayList<>(), "");

                            player.sendMessage(plugin.getMessage("chestPlaced"));
                        } catch (SQLException ex) {
                            ex.printStackTrace();

                            player.sendMessage(plugin.getMessage("unknownError"));

                            event.setCancelled(true);
                        }
                    } else {
                        player.sendMessage(plugin.getMessage("unknownError"));
                    }
                }
            }
        } else if (event.getBlock().getType() == Material.HOPPER) {
            Player player = event.getPlayer();
            Block bannedBlock = event.getBlock();

            Block blockAbove = player.getWorld().getBlockAt(bannedBlock.getLocation().add(0, 1, 0));
            if (blockAbove.getType() == Material.CHEST) {
                if (blockAbove.hasMetadata("owner")) {
                    if (blockAbove.getMetadata("owner").get(0).value().equals(player.getUniqueId())) {
                        return;
                    }
                    player.sendMessage(plugin.getMessage("cannotPlaceBlock"));

                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event)
    {
        Block block = event.getBlock();
        if (block.getType() == Material.CHEST
        || block.getType() == Material.TRAPPED_CHEST) {
            Player player = event.getPlayer();
            Chest chest = (Chest)block.getState();

            if (!ChestManager.initChestData(chest)) {
                if (!ChestManager.removePrivateChest(ChestManager.serializeChestLocation(chest.getLocation()))) {
                    player.sendMessage(plugin.getMessage("unknownError"));
                }
                return;
            }

            if (ChestManager.isOwner(chest, player.getUniqueId())) {
                if (ChestManager.removePrivateChest(ChestManager.serializeChestLocation(chest.getLocation()))) {
                    if (chest.getMetadata("private").get(0).asBoolean()) {
                        player.sendMessage(plugin.getMessage("chestRemoved"));
                    } else {
                        player.sendMessage(plugin.getMessage("publicChestRemoved"));
                    }

                    return;
                } else {
                    player.sendMessage(plugin.getMessage("unknownError"));
                }
            } else {
                if (!chest.getMetadata("private").get(0).asBoolean()) {
                    ChestRegenerationTask.chests.remove(chest);
                    if (ChestManager.removePrivateChest(ChestManager.serializeChestLocation(chest.getLocation()))) {
                        player.sendMessage(plugin.getMessage("publicChestBroken"));
                    } else {
                        player.sendMessage(plugin.getMessage("unknownError"));
                    }

                    return;
                }

                List<String> allowedPlayers = (List<String>)chest.getMetadata("allowedPlayers").get(0).value();
                if (allowedPlayers.contains(player.getUniqueId().toString())) {
                    ChestRegenerationTask.chests.remove(chest);

                    if (ChestManager.removePrivateChest(ChestManager.serializeChestLocation(chest.getLocation()))) {
                        player.sendMessage(String.format(plugin.getMessage("chestBroken"), ChestManager.getOfflinePlayerName((UUID)chest.getMetadata("owner").get(0).value())));
                    } else {
                        player.sendMessage(plugin.getMessage("unknownError"));
                    }
                    return;
                }

                float health = chest.getMetadata("health").get(0).asFloat();
                float maxHealth = chest.getMetadata("maxHealth").get(0).asFloat();

                if (health == maxHealth) {
                    ChestRegenerationTask.tryAddChest(chest);
                }

                chest.setMetadata("health", new FixedMetadataValue(plugin, --health));

                if (health > 0) {
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(String.format(plugin.getMessage("chestHealth"), ((health / maxHealth) * 100))));
                } else {
                    ChestRegenerationTask.chests.remove(chest);
                    if (ChestManager.removePrivateChest(ChestManager.serializeChestLocation(chest.getLocation()))) {
                        player.sendMessage(String.format(plugin.getMessage("chestBroken"), Bukkit.getOfflinePlayer((UUID)chest.getMetadata("owner").get(0).value()).getName()));
                    } else {
                        player.sendMessage(plugin.getMessage("unknownError"));
                    }
                    return;
                }
            }
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event)
    {
        InventoryHolder holder = event.getInventory().getHolder();
        if (holder instanceof DoubleChest) {
            ChestManager.setChestHealth((Chest)((DoubleChest) holder).getLeftSide());
            ChestManager.setChestHealth((Chest)((DoubleChest) holder).getRightSide());
        } else if (holder instanceof Chest) {
            ChestManager.setChestHealth((Chest)holder);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent event)
    {
        if (chestUpdate.containsKey(event.getPlayer().getName())) {
            event.setCancelled(true);

            Player player = event.getPlayer();
            String name = player.getName();

            String location = ChestManager.serializeChestLocation(event.getClickedBlock().getLocation());

            List<Object> arguments = chestUpdate.get(name);
            ChestState state = (ChestState)arguments.get(0);

            try {
                if (state == ChestState.Delete) {
                    if (ChestManager.removePrivateChest(location)) {
                        player.sendMessage("&7" + location + " deleted.");
                    } else {
                        player.sendMessage("&7Could not delete " + location);
                    }

                    chestUpdate.remove(name);
                    return;
                }

                if (Objects.requireNonNull(event.getClickedBlock()).getType() != Material.CHEST
                && Objects.requireNonNull(event.getClickedBlock()).getType() != Material.TRAPPED_CHEST) {
                    player.sendMessage(ChatColor.RED + "Block is not a chest.");

                    chestUpdate.remove(name);
                    return;
                }

                Chest chest = (Chest)event.getClickedBlock().getState();

                // Attempt to load chest data.
                if (!ChestManager.initChestData(chest)) {
                    player.sendMessage(plugin.getMessage("chestInvalidData"));

                    chestUpdate.remove(name);
                    return;
                }

                if (state == ChestState.Inspect) {
                    String owner = ChestManager.getOfflinePlayerName((UUID) chest.getMetadata("owner").get(0).value());
                    List<UUID> allowedPlayers = (List<UUID>) chest.getMetadata("allowedPlayers").get(0).value();
                    String privacy = chest.getMetadata("private").get(0).asBoolean() ? "private" : "public";

                    player.sendMessage(String.format(plugin.getMessage("Inspect"),
                            owner,
                            owner,
                            privacy,
                            ChestManager.getPlayerNames(allowedPlayers),
                            chest.getMetadata("group").get(0).asString(),
                            chest.getMetadata("health").get(0).asFloat(),
                            chest.getMetadata("maxHealth").get(0).asFloat()));

                    chestUpdate.remove(name);
                    return;
                }

                if (!ChestManager.isOwner(chest, player.getUniqueId())
                && !((player.isOp() || player.hasPermission("chestprotector.admin") && state == ChestState.TransferOwner))) {
                    player.sendMessage(String.format(plugin.getMessage("chestInteractProtected"), ChestManager.getOfflinePlayerName((UUID)chest.getMetadata("owner").get(0).value())));

                    chestUpdate.remove(name);
                    return;
                }

                switch (state) {
                    case AddGroup:
                        String groupName = (String)arguments.get(1);

                        try {
                            InventoryHolder holder = chest.getInventory().getHolder();
                            if (holder instanceof DoubleChest) {
                                Chest leftSide = (Chest)((DoubleChest) holder).getLeftSide();
                                Chest rightSide = (Chest)((DoubleChest) holder).getRightSide();

                                leftSide.setMetadata("group", new FixedMetadataValue(plugin, groupName));
                                rightSide.setMetadata("group", new FixedMetadataValue(plugin, groupName));

                                ChestStore.allowGroup(groupName, ChestManager.serializeChestLocation(leftSide.getLocation()));
                                ChestStore.allowGroup(groupName, ChestManager.serializeChestLocation(rightSide.getLocation()));
                            } else {
                                chest.setMetadata("group", new FixedMetadataValue(plugin, groupName));

                                ChestStore.allowGroup(groupName, location);
                            }
                            player.sendMessage(String.format(plugin.getMessage("groupAccess"), groupName));
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }

                        break;
                    case RemoveGroup:
                        if (chest.hasMetadata("group")) {
                            try {
                                InventoryHolder holder = chest.getInventory().getHolder();
                                if (holder instanceof DoubleChest) {
                                    Chest leftSide = (Chest)((DoubleChest) holder).getLeftSide();
                                    Chest rightSide = (Chest)((DoubleChest) holder).getRightSide();

                                    leftSide.removeMetadata("group", plugin);
                                    rightSide.removeMetadata("group", plugin);

                                    ChestStore.disallowGroup(ChestManager.serializeChestLocation(leftSide.getLocation()));
                                    ChestStore.disallowGroup(ChestManager.serializeChestLocation(rightSide.getLocation()));
                                } else {
                                    chest.removeMetadata("group", plugin);

                                    ChestStore.disallowGroup(location);
                                }

                                player.sendMessage(plugin.getMessage("groupAccessRevoked"));
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        } else {
                            player.sendMessage(plugin.getMessage("groupAccessError"));
                        }

                        break;
                    case SetFlag:
                        boolean flag = (boolean)arguments.get(1);

                        if (flag) {
                            if (chest.hasMetadata("flag_redstone")) {
                                player.sendMessage(plugin.getMessage("chestFlagAlreadySet"));
                            } else {
                                chest.setMetadata("flag_redstone", new FixedMetadataValue(plugin, true));
                                try {
                                    ChestStore.setFlag(location);
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }
                                player.sendMessage(plugin.getMessage("chestFlagSet"));
                            }
                        } else {
                            if (chest.hasMetadata("flag_redstone")) {
                                chest.removeMetadata("flag_redstone", plugin);
                                try {
                                    ChestStore.unsetFlag(location);
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }
                                player.sendMessage(plugin.getMessage("chestFlagUnset"));
                            } else {
                                player.sendMessage(plugin.getMessage("chestFlagAlreadyUnset"));
                            }
                        }

                        break;
                    case TransferOwner:
                        UUID owner = (UUID) arguments.get(1);
                        String ownerName = (String)arguments.get(2);

                        if (player.isOp() || player.hasPermission("chestprotector.admin")) {
                            ChestManager.broadcastToAllStaff(String.format(plugin.getMessage("adminTransfer"), player.getName(), ChestManager.getOfflinePlayerName((UUID)chest.getMetadata("owner").get(0).value()), ownerName));
                        }

                        InventoryHolder holder = chest.getInventory().getHolder();
                        if (holder instanceof DoubleChest) {
                            DoubleChest doubleChest = (DoubleChest)holder;
                            Chest leftSide = (Chest)doubleChest.getLeftSide();
                            Chest rightSide = (Chest)doubleChest.getRightSide();

                            leftSide.setMetadata("owner", new FixedMetadataValue(plugin, owner));
                            ChestStore.updateOwner(location, owner.toString());

                            rightSide.setMetadata("owner", new FixedMetadataValue(plugin, owner));
                            ChestStore.updateOwner(location, owner.toString());
                        } else {
                            chest.setMetadata("owner", new FixedMetadataValue(plugin, owner));
                            ChestStore.updateOwner(location, owner.toString());
                        }

                        player.sendMessage(String.format(plugin.getMessage("chestTransferred"), ownerName));

                        break;
                    case SetPrivate:
                        if (!chest.getMetadata("private").get(0).asBoolean()) {
                            ChestManager.setChestPrivacy(chest, true);

                            player.sendMessage(plugin.getMessage("chestPrivate"));
                        } else {
                            player.sendMessage(plugin.getMessage("chestAlreadyPrivate"));
                        }

                        break;
                    case SetPublic:
                        if (chest.getMetadata("private").get(0).asBoolean()) {
                            ChestManager.setChestPrivacy(chest, false);

                            player.sendMessage(plugin.getMessage("chestPublic"));
                        } else {
                            player.sendMessage(plugin.getMessage("chestAlreadyPublic"));
                        }

                        break;
                    case AllowPlayer:
                        ArrayList<UUID> allowedPlayers = (ArrayList<UUID>) chest.getMetadata("allowedPlayers").get(0).value();
                        UUID allowedPlayer = (UUID) arguments.get(1);
                        String serializedAllowedPlayer = allowedPlayer.toString();

                        if (allowedPlayers.size() == plugin.config.getInt("allowedPlayersLimit")) {
                            player.sendMessage(plugin.getMessage("playersLimit"));
                        } else if (allowedPlayers.contains(allowedPlayer)) {
                            player.sendMessage(plugin.getMessage("chestPlayerAlreadyHasAccess"));
                        } else {
                            allowedPlayers.add(allowedPlayer);

                            holder = chest.getInventory().getHolder();
                            if (holder instanceof DoubleChest) {
                                Chest leftSide = (Chest) ((DoubleChest) holder).getLeftSide();
                                Chest rightSide = (Chest) ((DoubleChest) holder).getRightSide();

                                leftSide.setMetadata("allowedPlayers", new FixedMetadataValue(plugin, allowedPlayers));
                                ChestStore.allowPlayer(ChestManager.serializeChestLocation(leftSide.getLocation()), serializedAllowedPlayer);

                                rightSide.setMetadata("allowedPlayers", new FixedMetadataValue(plugin, allowedPlayers));
                                ChestStore.allowPlayer(ChestManager.serializeChestLocation(rightSide.getLocation()), serializedAllowedPlayer);
                            } else {
                                chest.setMetadata("allowedPlayers", new FixedMetadataValue(plugin, allowedPlayers));
                                ChestStore.allowPlayer(location, serializedAllowedPlayer);
                            }

                            player.sendMessage(plugin.getMessage("chestPlayerAllowed"));
                        }

                        break;
                    case RemovePlayer:
                        allowedPlayers = (ArrayList<UUID>) chest.getMetadata("allowedPlayers").get(0).value();
                        UUID removedPlayer = (UUID) arguments.get(1);

                        if (!allowedPlayers.contains(removedPlayer)) {
                            player.sendMessage(plugin.getMessage("chestPlayerNotAllowed"));
                        } else {
                            allowedPlayers.remove(removedPlayer);
                            String serializedPlayers = String.join(",", ChestManager.serializePlayers(allowedPlayers));

                            holder = chest.getInventory().getHolder();
                            if (holder instanceof DoubleChest) {
                                DoubleChest doubleChest = (DoubleChest)holder;
                                Chest leftSide = (Chest)doubleChest.getLeftSide();
                                Chest rightSide = (Chest)doubleChest.getRightSide();

                                leftSide.setMetadata("allowedPlayers", new FixedMetadataValue(plugin, allowedPlayers));
                                ChestStore.updateAllowedPlayers(ChestManager.serializeChestLocation(leftSide.getLocation()), serializedPlayers);

                                rightSide.setMetadata("allowedPlayers", new FixedMetadataValue(plugin, allowedPlayers));
                                ChestStore.updateAllowedPlayers(ChestManager.serializeChestLocation(rightSide.getLocation()), serializedPlayers);
                            } else {
                                chest.setMetadata("allowedPlayers", new FixedMetadataValue(plugin, allowedPlayers));
                                ChestStore.updateAllowedPlayers(location, serializedPlayers + ",");
                            }

                            player.sendMessage(plugin.getMessage("chestPlayerRemoved"));
                        }

                        break;
                }
            } catch (SQLException e) {
                player.sendMessage(plugin.getMessage("unknownError"));
                e.printStackTrace();
            }
            chestUpdate.remove(name);
            return;
        }

        if(event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            Block clickedBlock = event.getClickedBlock();
            if (clickedBlock == null) return;

            if (clickedBlock.getType() == Material.CHEST
            || clickedBlock.getType() == Material.TRAPPED_CHEST) {
                Player player = event.getPlayer();

                Chest chest = (Chest)clickedBlock.getState();

                // Attempt to load chest data.
                if (!ChestManager.initChestData(chest)) {
                    player.sendMessage(plugin.getMessage("chestInvalidData"));

                    return;
                }

                if (player.isSneaking()) {
                    if (player.getInventory().getItemInMainHand().getType() == Material.AIR) {
                        event.setCancelled(true);
                    }
                    return;
                }

                if (!ChestManager.isOwner(chest, player.getUniqueId())) {
                    // Chest is public.
                    if (!chest.getMetadata("private").get(0).asBoolean()) {
                        return;
                    }
                    // We are allowed to access this chest.
                    List<UUID> allowedPlayers = (List<UUID>)chest.getMetadata("allowedPlayers").get(0).value();
                    if (allowedPlayers.contains(player.getUniqueId())) {
                        return;
                    }
                    // We are in the group allowed to access this chest.
                    if (chest.hasMetadata("group") && player.hasMetadata("chat_group")) {
                        if (chest.getMetadata("group").get(0).asString().equalsIgnoreCase(player.getMetadata("chat_group").get(0).asString())) {
                            return;
                        }
                    }
                    // We have permission to bypass the protection.
                    if (player.isOp() || player.hasPermission("chestprotector.admin")) {
                        String ownerName = ChestManager.getOfflinePlayerName((UUID)chest.getMetadata("owner").get(0).value());
                        ChestManager.broadcastToAllStaff(String.format(plugin.getMessage("adminChestOpened"), player.getName(), ownerName));

                        if (!player.hasPermission("chestprotector.notify")) {
                            player.sendMessage(String.format(plugin.getMessage("chestInteractBypass"), ownerName));
                        }

                        return;
                    }
                    // Chest is protected from us.
                    player.sendMessage(String.format(plugin.getMessage("chestInteractProtected"), ChestManager.getOfflinePlayerName((UUID)chest.getMetadata("owner").get(0).value())));

                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler(ignoreCancelled=true)
    public void onInventoryMoveItem(InventoryMoveItemEvent event)
    {
        Inventory source = event.getSource();
        Inventory destination = event.getDestination();

        Block from = ChestManager.getInventoryBlock(source);

        if (destination.getHolder() instanceof HopperMinecart) {
            InventoryHolder holder = source.getHolder();
            if (holder instanceof Chest || holder instanceof DoubleChest) {
                if (!(from.hasMetadata("flag_redstone") && from.getMetadata("flag_redstone").get(0).asBoolean())) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event)
    {
        chestUpdate.remove(event.getPlayer().getName());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockExplode(EntityExplodeEvent event)
    {
        event.blockList().removeIf(block -> block.getType() == Material.CHEST);
    }
}
