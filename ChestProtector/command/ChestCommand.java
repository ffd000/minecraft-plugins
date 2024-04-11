package net.fricktastic.chestprotector.command;

import me.lucko.luckperms.api.manager.UserManager;
import net.fricktastic.chestprotector.ChestProtector;
import net.fricktastic.chestprotector.ChestStore;
import net.fricktastic.chestprotector.EventListener;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

public class ChestCommand implements CommandExecutor
{
    private final ChestProtector plugin;

    public ChestCommand(ChestProtector plugin)
    {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if (!(sender instanceof Player)) {
            if (args.length > 0) {
                switch (args[0]) {
                    case "reload":
                        plugin.loadConfig();
                        plugin.loadLanguageConfig();

                        sender.sendMessage(ChatColor.AQUA + "Configs reloaded.");

                        return true;
                    case "groupdisband":
                        if (args.length == 2) {
                            try {
                                ChestStore.handleDisbandGroup(args[1]);
                            } catch (SQLException ignored) {
                            }
                        }
                        return true;
                }
            }

            sender.sendMessage(ChatColor.RED + "You can only use this command in-game.");

            return true;
        } else if (args.length < 1) {
            return false;
        }

        List<Object> arguments = new ArrayList<>();

        switch (args[0].toLowerCase()) {
            case "notify":
                if (!(sender.isOp() || sender.hasPermission("chestprotector.admin"))) {
                    sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");

                    return true;
                }

                if (args.length != 2) {
                    sender.sendMessage(ChatColor.RED + "Incorrect arguments.");

                    return true;
                }

                String dispatch;
                switch (args[1]) {
                    case "off":
                    case "false":
                        dispatch = "lp user " + sender.getName() + " permission set chestprotector.notify false";
                        sender.sendMessage(ChatColor.AQUA + "Alerts disabled.");
                        break;
                    default:
                        dispatch = "lp user " + sender.getName() + " permission set chestprotector.notify true";
                        sender.sendMessage(ChatColor.AQUA + "Alerts enabled.");
                        break;
                }
                plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), dispatch);

                return true;
            case "adminhelp":
                if (!(sender.isOp() || sender.hasPermission("chestprotector.admin"))) {
                    sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");

                    return true;
                }

                sender.sendMessage("§d====[{ ChestProtector Admin Commands }]====\n" +
                            " §d /chest §5reload: §bReload the configuration files.\n" +
                            " §d /chest §5delete: §bDelete a chest from the database.\n" +
                            " §d /chest §5notify on|off: §bDisable or enable chat alerts.");
                return true;
            case "reload":
                if (!(sender.isOp() || sender.hasPermission("chestprotector.admin"))) {
                    sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");

                    return true;
                }

                plugin.loadConfig();
                plugin.loadLanguageConfig();

                sender.sendMessage(ChatColor.AQUA + "Configs reloaded.");

                return true;
            case "delete":
                if (!(sender.isOp() || sender.hasPermission("chestprotector.admin"))) {
                    sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");

                    return true;
                }

                sender.sendMessage("§dClick a block to delete it from the records.");

                arguments.add(EventListener.ChestState.Delete);

                break;
            case "setflag":
                if (args.length < 2 || args.length > 3) {
                    return false;
                }

                arguments.add(EventListener.ChestState.SetFlag);

                if (args[1].equalsIgnoreCase("redstone")) {
                    if (args.length == 2) {
                        arguments.add(true);
                    } else {
                        switch (args[2].toLowerCase()) {
                            case "1":
                            case "on":
                            case "true":
                                arguments.add(true); break;
                            case "0":
                            case "off":
                            case "false":
                                arguments.add(false); break;
                            default:
                                sender.sendMessage(plugin.getMessage("commandInvalidFlagValue"));
                                return true;
                        }
                    }
                    sender.sendMessage(String.format(plugin.getMessage("commandSetFlag"), args[1]));
                } else {
                    sender.sendMessage(plugin.getMessage("commandInvalidFlag"));

                    return true;
                }
                break;
            case "inspect":
                arguments.add(EventListener.ChestState.Inspect);
                EventListener.chestUpdate.put(sender.getName(), arguments);

                sender.sendMessage(plugin.getMessage("commandInspect"));
                break;
            case "setprivate":
                arguments.add(EventListener.ChestState.SetPrivate);

                sender.sendMessage(plugin.getMessage("commandSetPrivate"));
                break;
            case "setpublic":
                arguments.add(EventListener.ChestState.SetPublic);
                EventListener.chestUpdate.put(sender.getName(), arguments);

                sender.sendMessage(plugin.getMessage("commandSetPublic"));
                break;
            case "setowner":
                if (args.length < 2) {
                    return false;
                }

                String name = args[1];

                if (name.equalsIgnoreCase(sender.getName()) && !(sender.isOp() || sender.hasPermission("chestprotector.admin"))) {
                    sender.sendMessage(plugin.getMessage("chestTransferSelf"));

                    return true;
                }

                Player targetPlayer = getPlayer(name);
                UUID uuid;
                arguments.add(EventListener.ChestState.TransferOwner);
                if (targetPlayer == null) {
                    scheduleGetOfflineUuid(
                            (target, targetName) -> {
                                if (target == null) {
                                    sender.sendMessage(String.format(plugin.getMessage("playerNotFound"), targetName));
                                    return;
                                }
                                arguments.add(target);
                                arguments.add(targetName);
                                sender.sendMessage(String.format(plugin.getMessage("commandTransfer"), targetName));
                                EventListener.chestUpdate.put(sender.getName(), arguments);
                            },
                            args[1]
                    );
                    return true;
                } else {
                    uuid = targetPlayer.getUniqueId();
                }
                arguments.add(uuid);
                arguments.add(name);

                sender.sendMessage(String.format(plugin.getMessage("commandTransfer"), name));

                break;
            case "addgroup":
                if (args.length < 2) {
                    return false;
                }

                arguments.add(EventListener.ChestState.AddGroup);
                arguments.add(args[1]);

                sender.sendMessage(String.format(plugin.getMessage("commandAddGroup"), args[1]));

                break;
            case "removegroup":
                arguments.add(EventListener.ChestState.RemoveGroup);

                sender.sendMessage(plugin.getMessage("commandRemoveGroup"));

                break;
            case "add":
                if (args.length < 2) {
                    return false;
                }

                name = args[1];

                if (name.equalsIgnoreCase(sender.getName())) {
                    sender.sendMessage(plugin.getMessage("chestAddSelf"));

                    return true;
                }

                targetPlayer = getPlayer(name);
                arguments.add(EventListener.ChestState.AllowPlayer);
                if (targetPlayer == null) {
                    scheduleGetOfflineUuid(
                            (target, targetName) -> {
                                if (target == null) {
                                    sender.sendMessage(String.format(plugin.getMessage("playerNotFound"), targetName));

                                    return;
                                }
                                arguments.add(target);
                                sender.sendMessage(String.format(plugin.getMessage("allowPlayer"), targetName));
                                EventListener.chestUpdate.put(sender.getName(), arguments);
                            },
                            args[1]
                    );
                    return true;
                } else {
                    uuid = targetPlayer.getUniqueId();
                }
                arguments.add(uuid);

                sender.sendMessage(String.format(plugin.getMessage("allowPlayer"), name));

                break;
            case "remove":
                if (args.length < 2) {
                    return false;
                }

                name = args[1];

                if (name.equals(sender.getName())) {
                    sender.sendMessage(plugin.getMessage("chestRemoveSelf"));

                    return true;
                }

                targetPlayer = getPlayer(name);
                arguments.add(EventListener.ChestState.RemovePlayer);
                if (targetPlayer == null) {
                    scheduleGetOfflineUuid(
                            (target, targetName) -> {
                                if (target == null) {
                                    sender.sendMessage(String.format(plugin.getMessage("playerNotFound"), targetName));

                                    return;
                                }
                                arguments.add(target);
                                sender.sendMessage(String.format(plugin.getMessage("removePlayer"), targetName));
                                EventListener.chestUpdate.put(sender.getName(), arguments);
                            },
                            args[1]
                    );
                    return true;
                } else {
                    uuid = targetPlayer.getUniqueId();
                }
                arguments.add(uuid);

                sender.sendMessage(String.format(plugin.getMessage("removePlayer"), name));

                break;
            default:
                return false;
        }
        EventListener.chestUpdate.put(sender.getName(), arguments);

        return true;
    }

    private void scheduleGetOfflineUuid(BiConsumer<UUID, String> cb, String playerName)
    {
        final BukkitScheduler scheduler = Bukkit.getServer().getScheduler();

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            UserManager userManager = plugin.luckperms.getUserManager();
            CompletableFuture<UUID> future = userManager.lookupUuid(playerName);

            scheduler.runTask(plugin, () -> cb.accept(future.join(), playerName));
        }
        );
    }

    private Player getPlayer(String playerName)
    {
        List<Player> players = plugin.getServer().matchPlayer(playerName);

        if (players.isEmpty()) {
            return null;
        } else {
            return players.get(0);
        }
    }
}
