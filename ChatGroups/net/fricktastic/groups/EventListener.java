/*     */ package net.fricktastic.groups;
/*     */ 
/*     */ import java.sql.SQLException;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.UUID;
/*     */ import org.bukkit.entity.Player;
/*     */ import org.bukkit.event.EventHandler;
/*     */ import org.bukkit.event.EventPriority;
/*     */ import org.bukkit.event.Listener;
/*     */ import org.bukkit.event.player.AsyncPlayerChatEvent;
/*     */ import org.bukkit.event.player.PlayerJoinEvent;
/*     */ import org.bukkit.event.player.PlayerQuitEvent;
/*     */ import org.bukkit.metadata.FixedMetadataValue;
/*     */ import org.bukkit.metadata.MetadataValue;
/*     */ import org.bukkit.plugin.Plugin;
/*     */ 
/*     */ public class EventListener
/*     */   implements Listener {
/*     */   private final Groups plugin;
/*     */   
/*     */   EventListener(Groups plugin) {
/*  23 */     this.plugin = plugin;
/*     */   }
/*     */ 
/*     */   
/*     */   @EventHandler(priority = EventPriority.HIGH)
/*     */   public void onPlayerChat(AsyncPlayerChatEvent event) {
/*  29 */     Player player = event.getPlayer();
/*  30 */     String group = null;
/*  31 */     Groups.Channel channel = null;
/*     */     
/*  33 */     if (player.hasMetadata("chat_group")) {
/*  34 */       group = ((MetadataValue)player.getMetadata("chat_group").get(0)).asString();
/*  35 */       channel = (Groups.Channel)((MetadataValue)player.getMetadata("chat_channel").get(0)).value();
/*     */     } 
/*     */     
/*  38 */     for (Iterator<Player> it = event.getRecipients().iterator(); it.hasNext(); ) {
/*  39 */       Player recipient = it.next();
/*  40 */       if (recipient.equals(player))
/*     */         continue; 
/*  42 */       String recipientGroup = null;
/*  43 */       if (recipient.hasMetadata("chat_group")) {
/*  44 */         recipientGroup = ((MetadataValue)recipient.getMetadata("chat_group").get(0)).asString();
/*  45 */         Groups.Channel recipientChannel = (Groups.Channel)((MetadataValue)recipient.getMetadata("chat_channel").get(0)).value();
/*  46 */         if (recipientChannel == Groups.Channel.Group) {
/*  47 */           if (group != null && group.equals(recipientGroup))
/*     */             continue; 
/*  49 */           it.remove();
/*     */           
/*     */           continue;
/*     */         } 
/*     */       } 
/*  54 */       if (channel == null)
/*     */         continue; 
/*  56 */       if (channel != Groups.Channel.Group || (
/*  57 */         recipientGroup != null && 
/*  58 */         group.equals(recipientGroup))) {
/*     */         continue;
/*     */       }
/*     */ 
/*     */       
/*  63 */       it.remove();
/*     */     } 
/*     */ 
/*     */     
/*  67 */     if (group != null) {
/*  68 */       String format = event.getFormat();
/*  69 */       String color = format.substring(0, 2);
/*  70 */       if (color.startsWith("§")) {
/*  71 */         String format1 = color + group + " §8| " + color + format.substring(2);
/*  72 */         if (channel == Groups.Channel.Group) {
/*  73 */           this.plugin.broadcastToAllSpies(group, String.format(format1, new Object[] { player.getDisplayName(), event.getMessage() }));
/*     */           
/*  75 */           event.setFormat("§8[" + color + "G§8] " + format1);
/*     */         } else {
/*  77 */           event.setFormat(format1);
/*     */         } 
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   @EventHandler
/*     */   public void onPlayerJoin(PlayerJoinEvent event) {
/*  86 */     Player player = event.getPlayer();
/*     */     
/*  88 */     if (player.hasMetadata("chat_group"))
/*     */       return; 
/*     */     try {
/*  91 */       List<Object> data = GroupStore.fetchGroupForPlayer(player.getUniqueId().toString());
/*  92 */       if (data == null)
/*     */         return; 
/*  94 */       String name = (String)data.get(0);
/*  95 */       UUID owner = (UUID)data.get(1);
/*  96 */       UUID uuid = player.getUniqueId();
/*     */       
/*  98 */       player.setMetadata("chat_group", (MetadataValue)new FixedMetadataValue((Plugin)this.plugin, name));
/*  99 */       player.setMetadata("chat_channel", (MetadataValue)new FixedMetadataValue((Plugin)this.plugin, Groups.Channel.Global));
/* 100 */       if (owner.equals(uuid)) {
/* 101 */         player.setMetadata("chat_group_rank", (MetadataValue)new FixedMetadataValue((Plugin)this.plugin, Integer.valueOf(2)));
/* 102 */       } else if (((List)data.get(2)).contains(uuid)) {
/* 103 */         player.setMetadata("chat_group_rank", (MetadataValue)new FixedMetadataValue((Plugin)this.plugin, Integer.valueOf(1)));
/*     */       } else {
/* 105 */         player.setMetadata("chat_group_rank", (MetadataValue)new FixedMetadataValue((Plugin)this.plugin, Integer.valueOf(0)));
/*     */       } 
/* 107 */     } catch (SQLException e) {
/* 108 */       e.printStackTrace();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   @EventHandler
/*     */   public void onPlayerQuit(PlayerQuitEvent event) {
/* 115 */     String name = event.getPlayer().getName();
/* 116 */     this.plugin.invitations.remove(name);
/* 117 */     this.plugin.cooldowns.remove(name);
/*     */   }
/*     */ }


/* Location:              D:\Java\Frick\plugins\Groups-1.5.jar!\net\fricktastic\groups\EventListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */