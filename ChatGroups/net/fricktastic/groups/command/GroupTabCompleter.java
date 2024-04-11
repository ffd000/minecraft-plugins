/*    */ package net.fricktastic.groups.command;
/*    */ 
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ import org.bukkit.command.Command;
/*    */ import org.bukkit.command.CommandSender;
/*    */ import org.bukkit.command.TabCompleter;
/*    */ import org.bukkit.entity.Player;
/*    */ import org.bukkit.metadata.MetadataValue;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class GroupTabCompleter
/*    */   implements TabCompleter
/*    */ {
/*    */   public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
/* 17 */     List<String> variants = new ArrayList<>();
/*    */     
/* 19 */     if (!(sender instanceof Player)) return variants;
/*    */     
/* 21 */     Player player = (Player)sender;
/*    */     
/* 23 */     switch (args.length) {
/*    */       case 1:
/* 25 */         variants.add("create");
/* 26 */         variants.add("leave");
/* 27 */         variants.add("info");
/* 28 */         variants.add("list");
/*    */         
/* 30 */         if (player.isOp() || player.hasPermission("groups.admin")) {
/* 31 */           variants.add("reload");
/* 32 */           variants.add("spy");
/* 33 */           variants.add("purge");
/*    */         } 
/*    */         
/* 36 */         if (player.hasMetadata("chat_group_rank")) {
/* 37 */           int rank = ((MetadataValue)player.getMetadata("chat_group_rank").get(0)).asInt();
/*    */           
/* 39 */           if (rank > 0) {
/* 40 */             variants.add("invite");
/* 41 */             variants.add("kick");
/*    */           } 
/*    */           
/* 44 */           if (rank == 2) {
/* 45 */             variants.add("disband");
/* 46 */             variants.add("demote");
/* 47 */             variants.add("promote");
/* 48 */             variants.add("transfer");
/*    */           } 
/*    */         } 
/*    */         break;
/*    */       
/*    */       case 2:
/* 54 */         switch (args[0].toLowerCase()) { case "transfer":
/*    */           case "demote":
/*    */           case "promote":
/*    */           case "invite":
/*    */           case "kick":
/* 59 */             variants = null; break; }
/*    */         
/*    */         break;
/*    */     } 
/* 63 */     if (variants != null) variants = adopt(args[args.length - 1], variants);
/*    */     
/* 65 */     return variants;
/*    */   }
/*    */ 
/*    */   
/*    */   static List<String> adopt(String last, List<String> variants) {
/* 70 */     List<String> variantsList = new ArrayList<>(variants);
/* 71 */     for (String variant : variantsList) { if (!variant.startsWith(last)) variants.remove(variant);  }
/*    */     
/* 73 */     return variants;
/*    */   }
/*    */ }


/* Location:              D:\Java\Frick\plugins\Groups-1.5.jar!\net\fricktastic\groups\command\GroupTabCompleter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */