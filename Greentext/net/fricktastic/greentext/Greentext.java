/*    */ package net.fricktastic.greentext;
/*    */ 
/*    */ import java.util.regex.Pattern;
/*    */ import org.bukkit.ChatColor;
/*    */ import org.bukkit.entity.Player;
/*    */ import org.bukkit.event.EventHandler;
/*    */ import org.bukkit.event.Listener;
/*    */ import org.bukkit.event.player.AsyncPlayerChatEvent;
/*    */ import org.bukkit.plugin.Plugin;
/*    */ import org.bukkit.plugin.java.JavaPlugin;
/*    */ 
/*    */ public final class Greentext
/*    */   extends JavaPlugin implements Listener {
/* 14 */   private static final Pattern STRIP_COLOR_PATTERN = Pattern.compile("(?i)&[0-9A-FK-OR]");
/*    */ 
/*    */ 
/*    */   
/*    */   public void onEnable() {
/* 19 */     getServer().getPluginManager().registerEvents(this, (Plugin)this);
/*    */   }
/*    */ 
/*    */   
/*    */   @EventHandler
/*    */   public void onPlayerChat(AsyncPlayerChatEvent event) {
/* 25 */     Player player = event.getPlayer();
/* 26 */     String message = event.getMessage();
/*    */     
/* 28 */     if (!player.isOp() && !player.hasPermission("greentext.allowcolorcodes")) {
/* 29 */       message = stripColors(message);
/*    */     }
/*    */     
/* 32 */     System.out.println(message);
/*    */     
/* 34 */     if (event.getMessage().startsWith(">")) {
/* 35 */       event.setMessage(ChatColor.GREEN + message);
/*    */     } else {
/* 37 */       event.setMessage(message);
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   private static String stripColors(String message) {
/* 43 */     return STRIP_COLOR_PATTERN.matcher(ChatColor.stripColor(message)).replaceAll("");
/*    */   }
/*    */ }


/* Location:              C:\Users\Lenovo\Desktop\Greentext-1.0-SNAPSHOT.jar!\net\fricktastic\greentext\Greentext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */