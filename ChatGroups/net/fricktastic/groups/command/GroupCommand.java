/*     */ package net.fricktastic.groups.command;
/*     */ 
/*     */ import java.sql.SQLException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.UUID;
/*     */ import net.fricktastic.groups.GroupStore;
/*     */ import net.fricktastic.groups.Groups;
/*     */ import net.md_5.bungee.api.chat.ClickEvent;
/*     */ import net.md_5.bungee.api.chat.ComponentBuilder;
/*     */ import net.md_5.bungee.api.chat.HoverEvent;
/*     */ import net.md_5.bungee.api.chat.TextComponent;
/*     */ import org.apache.commons.lang.StringUtils;
/*     */ import org.bukkit.Bukkit;
/*     */ import org.bukkit.ChatColor;
/*     */ import org.bukkit.command.Command;
/*     */ import org.bukkit.command.CommandExecutor;
/*     */ import org.bukkit.command.CommandSender;
/*     */ import org.bukkit.entity.Player;
/*     */ import org.bukkit.metadata.FixedMetadataValue;
/*     */ import org.bukkit.metadata.MetadataValue;
/*     */ import org.bukkit.plugin.Plugin;
/*     */ 
/*     */ public class GroupCommand
/*     */   implements CommandExecutor {
/*     */   private final Groups plugin;
/*     */   
/*     */   public GroupCommand(Groups plugin) {
/*  29 */     this.plugin = plugin;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
/*  34 */     if (!(sender instanceof Player)) {
/*  35 */       if (args.length > 0 && 
/*  36 */         args[0].equalsIgnoreCase("reload")) {
/*  37 */         this.plugin.reloadConfigs();
/*  38 */         sender.sendMessage(ChatColor.AQUA + "Configs reloaded.");
/*     */         
/*  40 */         return true;
/*     */       } 
/*     */       
/*  43 */       sender.sendMessage(ChatColor.RED + "You can only use this command in-game.");
/*     */       
/*  45 */       return true;
/*     */     } 
/*     */     
/*  48 */     Player player = (Player)sender;
/*     */     
/*  50 */     if (args.length > 0)
/*  51 */     { int pageNumber; Player member; String listedGroup; String group; switch (args[0].toLowerCase())
/*     */       { case "purge":
/*  53 */           if (player.isOp() || player.hasPermission("groups.admin")) {
/*     */             try {
/*  55 */               int count = GroupStore.purgeGroups();
/*     */               
/*  57 */               player.sendMessage(ChatColor.LIGHT_PURPLE + "Purged " + count + " groups.");
/*  58 */             } catch (SQLException e) {
/*  59 */               e.printStackTrace();
/*  60 */               player.sendMessage(ChatColor.DARK_RED + "An unknown error occurred.");
/*     */             } 
/*     */           } else {
/*  63 */             sender.sendMessage(ChatColor.RED + "You do not have permission to perform this command.");
/*     */           } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 530 */           return true;case "spy": if (player.isOp() || player.hasPermission("groups.admin")) { if (this.plugin.spies.contains(player.getName())) { this.plugin.spies.remove(player.getName()); sender.sendMessage(ChatColor.AQUA + "Spy disabled."); } else { this.plugin.spies.add(player.getName()); sender.sendMessage(ChatColor.AQUA + "Spy enabled."); }  } else { sender.sendMessage(ChatColor.RED + "You do not have permission to perform this command."); }  return true;case "list": pageNumber = 0; if (args.length == 2) { if (!StringUtils.isNumeric(args[1])) { sender.sendMessage(ChatColor.RED + "Invalid page number."); return true; }  pageNumber = Integer.parseInt(args[1]) - 1; }  try { int count = GroupStore.getGroupsCount(); List<List<Object>> groups = GroupStore.listGroups(pageNumber); if (groups.isEmpty()) { sender.sendMessage(ChatColor.RED + "No groups were found."); return true; }  player.sendMessage(String.format(this.plugin.getMessage("groupListLabel"), new Object[] { Integer.valueOf(pageNumber + 1), Integer.valueOf((int)Math.ceil((count / 10)) + 1) })); for (List<Object> list : groups) { sender.sendMessage(String.format(this.plugin.getMessage("groupEntry"), new Object[] { list.get(0), list.get(1) })); }  } catch (SQLException e) { e.printStackTrace(); return true; }  return true;case "reload": if (player.isOp() || player.hasPermission("groups.admin")) { this.plugin.reloadConfigs(); sender.sendMessage(ChatColor.AQUA + "Configs reloaded."); } else { sender.sendMessage(ChatColor.RED + "You do not have permission to perform this command."); }  return true;case "transfer": member = verify(player, args, 2); if (member != null) { if (member.getName().equals(sender.getName())) { player.sendMessage(this.plugin.getMessage("transferSelf")); return true; }  String groupName = ((MetadataValue)player.getMetadata("chat_group").get(0)).asString(); if (!member.hasMetadata("chat_group") || !((MetadataValue)member.getMetadata("chat_group").get(0)).asString().equals(groupName)) { player.sendMessage(this.plugin.getMessage("transferOwnerFailed")); return true; }  String name = member.getName(); this.plugin.transferOwner(player, member, groupName); member.sendMessage(String.format(this.plugin.getMessage("ownershipTransfer"), new Object[] { player.getName(), groupName })); this.plugin.broadcastToAllMembers(groupName, String.format(this.plugin.getMessage("ownershipTransferred"), new Object[] { groupName, name })); }  return true;case "info": if (args.length == 2) { listedGroup = args[1]; } else { if (!player.hasMetadata("chat_group")) { player.sendMessage(this.plugin.getMessage("noGroup")); return true; }  listedGroup = ((MetadataValue)player.getMetadata("chat_group").get(0)).asString(); }  try { List<Object> data = GroupStore.fetchGroupData(listedGroup); if (data == null) { player.sendMessage(ChatColor.DARK_RED + "This group does not exist."); return true; }  player.sendMessage(String.format(this.plugin.getMessage("groupInfo"), new Object[] { data.get(0), this.plugin.getPlayerName((UUID)data.get(1)), this.plugin.serializePlayers((List)data.get(2)), this.plugin.serializePlayers((List)data.get(3)) })); } catch (SQLException e) { e.printStackTrace(); player.sendMessage(ChatColor.DARK_RED + "An unknown error occurred."); }  return true;case "promote": member = verify(player, args, 2); if (member != null) { if (member.getName().equals(sender.getName())) { player.sendMessage(this.plugin.getMessage("promoteSelf")); return true; }  String str = ((MetadataValue)player.getMetadata("chat_group").get(0)).asString(); int result = this.plugin.promote(member, str); if (result == 1) { member.sendMessage(String.format(this.plugin.getMessage("memberPromotion"), new Object[] { str })); this.plugin.broadcastToAllMembers(str, String.format(this.plugin.getMessage("memberPromoted"), new Object[] { member.getName() })); } else if (result == 0) { player.sendMessage(this.plugin.getMessage("adminsLimit")); } else { player.sendMessage(String.format(this.plugin.getMessage("promotionFailed"), new Object[] { member.getName() })); }  }  return true;case "demote": member = verify(player, args, 2); if (member != null) { if (member.getName().equals(sender.getName())) { player.sendMessage(this.plugin.getMessage("demoteSelf")); return true; }  String str = ((MetadataValue)player.getMetadata("chat_group").get(0)).asString(); int result = this.plugin.demote(member, str); if (result == 1) { member.sendMessage(String.format(this.plugin.getMessage("adminDemotion"), new Object[] { str })); this.plugin.broadcastToAllMembers(str, String.format(this.plugin.getMessage("adminDemoted"), new Object[] { member.getName() })); } else if (result == 0) { player.sendMessage(this.plugin.getMessage("membersLimit")); } else { player.sendMessage(String.format(this.plugin.getMessage("demotionFailed"), new Object[] { member.getName() })); }  }  return true;case "accept": if (this.plugin.invitations.containsKey(player.getName())) { if (player.hasMetadata("chat_group")) { player.sendMessage(this.plugin.getMessage("groupJoinError")); return true; }  String name = player.getName(); List<Object> arguments = (List<Object>)this.plugin.invitations.get(name); String str1 = (String)arguments.get(0); this.plugin.invitations.remove(name); if (!this.plugin.joinGroup(player, str1)) { player.sendMessage(this.plugin.getMessage("membersLimit")); return true; }  player.sendMessage(this.plugin.getMessage("inviteAccepted")); this.plugin.broadcastToAllMembers(str1, String.format(this.plugin.getMessage("inviteAcceptedBroadcast"), new Object[] { name })); } else { player.sendMessage(this.plugin.getMessage("noInvites")); }  return true;case "decline": if (this.plugin.invitations.containsKey(player.getName())) { String name = player.getName(); List<Object> arguments = (List<Object>)this.plugin.invitations.get(name); Player inviter = (Player)arguments.get(1); this.plugin.invitations.remove(name); player.sendMessage(this.plugin.getMessage("inviteDeclined")); inviter.sendMessage(String.format(this.plugin.getMessage("inviterInviteDeclined"), new Object[] { name })); } else { player.sendMessage(this.plugin.getMessage("noInvites")); }  return true;case "invite": member = verify(player, args, 1); if (member != null) { if (member.getName().equals(sender.getName())) { player.sendMessage(this.plugin.getMessage("inviteSelf")); return true; }  String groupName = ((MetadataValue)player.getMetadata("chat_group").get(0)).asString(); if (member.hasMetadata("chat_group") && ((MetadataValue)member.getMetadata("chat_group").get(0)).asString().equals(groupName)) { player.sendMessage(this.plugin.getMessage("inviteSameGroup")); return true; }  String name = member.getName(); if (this.plugin.cooldowns.containsKey(player.getName()) && ((String)this.plugin.cooldowns.get(player.getName())).equals(name)) { player.sendMessage(ChatColor.RED + "You already sent this player an invitation."); return true; }  List<Object> arguments = new ArrayList(); arguments.add(groupName); arguments.add(player); this.plugin.invitations.put(name, arguments); this.plugin.cooldowns.put(player.getName(), name); ComponentBuilder message = new ComponentBuilder(String.format(this.plugin.getMessage("inviteRequest"), new Object[] { groupName })); message.append(this.plugin.getMessage("inviteRequestYes")); message.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/g accept")); message.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText("§aClick to accept."))); message.append(this.plugin.getMessage("inviteRequestNo")); message.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/g decline")); message.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText("§cClick to decline."))); member.spigot().sendMessage(message.create()); player.sendMessage(String.format(this.plugin.getMessage("inviteSuccess"), new Object[] { name })); this.plugin.getServer().getScheduler().scheduleSyncDelayedTask((Plugin)this.plugin, () -> (List)this.plugin.invitations.remove(name), 600L); this.plugin.getServer().getScheduler().scheduleSyncDelayedTask((Plugin)this.plugin, () -> (String)this.plugin.cooldowns.remove(player.getName()), 600L); }  return true;case "kick": member = verify(player, args, 1); if (member != null) { if (member.getName().equals(sender.getName())) { player.sendMessage(this.plugin.getMessage("kickSelf")); return true; }  if (((MetadataValue)player.getMetadata("chat_group_rank").get(0)).asInt() == 1) { int rank = ((MetadataValue)member.getMetadata("chat_group_rank").get(0)).asInt(); if (rank == 2) { player.sendMessage(this.plugin.getMessage("kickOwner")); return true; }  if (rank == 1) { player.sendMessage(this.plugin.getMessage("kickAdmin")); return true; }  }  String name = member.getName(); String groupName = ((MetadataValue)player.getMetadata("chat_group").get(0)).asString(); this.plugin.leaveGroup(member, groupName); member.sendMessage(String.format(this.plugin.getMessage("kickedUser"), new Object[] { groupName })); this.plugin.broadcastToAllMembers(groupName, String.format(this.plugin.getMessage("kickSuccess"), new Object[] { name, groupName })); }  return true;case "create": if (player.hasMetadata("chat_group")) { player.sendMessage(this.plugin.getMessage("groupCreateError")); return true; }  if (args.length == 2) { int min = this.plugin.config.getInt("minChar"); int max = this.plugin.config.getInt("maxChar"); if (args[1].length() < min || args[1].length() > max) { sender.sendMessage(String.format(this.plugin.getMessage("groupNameLength"), new Object[] { Integer.valueOf(min), Integer.valueOf(max) })); return true; }  if (this.plugin.blacklist.stream().anyMatch(x -> args[1].toLowerCase().contains(x))) { sender.sendMessage(this.plugin.getMessage("nameBlacklisted")); return true; }  if (!args[1].matches("^[a-zA-Z0-9]*$")) { sender.sendMessage(this.plugin.getMessage("nameInvalid")); return true; }  try { if (this.plugin.creationCooldowns.contains(player.getName())) { player.sendMessage(this.plugin.getMessage("creationCooldown")); return true; }  String name = args[1]; if (GroupStore.groupExists(name.toLowerCase())) { player.sendMessage(ChatColor.RED + "This group already exists."); return true; }  UUID owner = player.getUniqueId(); GroupStore.createGroup(name, owner.toString()); player.setMetadata("chat_group", (MetadataValue)new FixedMetadataValue((Plugin)this.plugin, name)); player.setMetadata("chat_group_rank", (MetadataValue)new FixedMetadataValue((Plugin)this.plugin, Integer.valueOf(2))); player.setMetadata("chat_channel", (MetadataValue)new FixedMetadataValue((Plugin)this.plugin, Groups.Channel.Global)); Bukkit.broadcastMessage(String.format(this.plugin.getMessage("groupCreatedBroadcast"), new Object[] { name })); player.sendMessage(String.format(this.plugin.getMessage("groupCreated"), new Object[] { name })); this.plugin.creationCooldowns.add(player.getName()); this.plugin.getServer().getScheduler().scheduleSyncDelayedTask((Plugin)this.plugin, () -> this.plugin.creationCooldowns.remove(player.getName()), 600L); } catch (SQLException e) { e.printStackTrace(); player.sendMessage(ChatColor.RED + "An unknown error occurred."); return true; }  } else { return false; }  return true;case "disband": if (args.length == 2 && (player.isOp() || player.hasPermission("groups.admin"))) { String str = args[1]; try { List<Object> data = GroupStore.fetchGroupData(str); if (data == null) { player.sendMessage(ChatColor.RED + "Group '" + str + "' does not exist."); return true; }  GroupStore.disbandGroup(str); this.plugin.kickAll(data); this.plugin.getServer().dispatchCommand((CommandSender)this.plugin.getServer().getConsoleSender(), "chest groupdisband " + str); Bukkit.broadcastMessage(String.format(this.plugin.getMessage("groupDisbanded"), new Object[] { str })); } catch (SQLException e) { e.printStackTrace(); player.sendMessage(ChatColor.DARK_RED + "An unknown error occurred."); return false; }  return true; }  if (!player.hasMetadata("chat_group")) { player.sendMessage(this.plugin.getMessage("noGroup")); return true; }  if (((MetadataValue)player.getMetadata("chat_group_rank").get(0)).asInt() != 2) { player.sendMessage(this.plugin.getMessage("groupDisbandError")); return true; }  group = ((MetadataValue)player.getMetadata("chat_group").get(0)).asString(); try { List<Object> data = GroupStore.fetchGroupData(group); GroupStore.disbandGroup(group); this.plugin.kickAll(data); this.plugin.getServer().dispatchCommand((CommandSender)this.plugin.getServer().getConsoleSender(), "chest groupdisband " + group); Bukkit.broadcastMessage(String.format(this.plugin.getMessage("groupDisbanded"), new Object[] { group })); } catch (SQLException e) { e.printStackTrace(); player.sendMessage(ChatColor.DARK_RED + "An unknown error occurred."); return false; }  return true;case "leave": if (!player.hasMetadata("chat_group")) { player.sendMessage(this.plugin.getMessage("noGroup")); return true; }  if (((MetadataValue)player.getMetadata("chat_group_rank").get(0)).asInt() == 2) { player.sendMessage(this.plugin.getMessage("groupLeaveOwner")); return true; }  group = ((MetadataValue)player.getMetadata("chat_group").get(0)).asString(); this.plugin.leaveGroup(player, group); player.sendMessage(String.format(this.plugin.getMessage("groupLeft"), new Object[] { group })); this.plugin.broadcastToAllMembers(group, String.format(this.plugin.getMessage("groupLeftBroadcast"), new Object[] { player.getName(), group })); return true; }  return false; }  if (!player.hasMetadata("chat_channel")) { player.sendMessage(this.plugin.getMessage("noGroup")); return true; }  Groups.Channel channel = (Groups.Channel)((MetadataValue)player.getMetadata("chat_channel").get(0)).value(); switch (channel) { case Global: player.setMetadata("chat_channel", (MetadataValue)new FixedMetadataValue((Plugin)this.plugin, Groups.Channel.Group)); player.sendMessage(String.format(this.plugin.getMessage("channelSwitched"), new Object[] { "GROUP" })); break;case Group: player.setMetadata("chat_channel", (MetadataValue)new FixedMetadataValue((Plugin)this.plugin, Groups.Channel.Global)); player.sendMessage(String.format(this.plugin.getMessage("channelSwitched"), new Object[] { "GLOBAL" })); break; }  return true;
/*     */   }
/*     */ 
/*     */   
/*     */   private Player verify(Player player, String[] args, int requiredRank) {
/* 535 */     if (args.length < 2) {
/* 536 */       return null;
/*     */     }
/*     */     
/* 539 */     if (!player.hasMetadata("chat_group")) {
/* 540 */       player.sendMessage(this.plugin.getMessage("noGroup"));
/*     */       
/* 542 */       return null;
/*     */     } 
/*     */     
/* 545 */     if (((MetadataValue)player.getMetadata("chat_group_rank").get(0)).asInt() < requiredRank) {
/* 546 */       if (requiredRank == 2) {
/* 547 */         player.sendMessage(this.plugin.getMessage("noPermissionOwner"));
/*     */       } else {
/* 549 */         player.sendMessage(this.plugin.getMessage("noPermissionAdmin"));
/*     */       } 
/*     */       
/* 552 */       return null;
/*     */     } 
/*     */     
/* 555 */     Player member = getPlayer(args[1]);
/* 556 */     if (member == null) {
/* 557 */       player.sendMessage(String.format(this.plugin.getMessage("playerNotFound"), new Object[] { args[1] }));
/*     */     }
/* 559 */     return member;
/*     */   }
/*     */   
/*     */   private Player getPlayer(String playerName) {
/* 563 */     List<Player> players = this.plugin.getServer().matchPlayer(playerName);
/*     */     
/* 565 */     if (players.isEmpty()) {
/* 566 */       return null;
/*     */     }
/* 568 */     return players.get(0);
/*     */   }
/*     */ }


/* Location:              D:\Java\Frick\plugins\Groups-1.5.jar!\net\fricktastic\groups\command\GroupCommand.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */