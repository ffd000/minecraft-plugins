/*     */ package net.fricktastic.groups;
/*     */ import java.io.BufferedReader;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.sql.SQLException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.UUID;
/*     */ import org.bukkit.Bukkit;
/*     */ import org.bukkit.OfflinePlayer;
/*     */ import org.bukkit.configuration.file.FileConfiguration;
/*     */ import org.bukkit.configuration.file.YamlConfiguration;
/*     */ import org.bukkit.entity.Player;
/*     */ import org.bukkit.metadata.FixedMetadataValue;
/*     */ import org.bukkit.metadata.MetadataValue;
/*     */ import org.bukkit.plugin.Plugin;
/*     */ 
/*     */ public final class Groups extends JavaPlugin {
/*     */   public static final int RANK_MEMBER = 0;
/*     */   public static final int RANK_ADMIN = 1;
/*     */   public static final int RANK_OWNER = 2;
/*     */   
/*     */   public enum Channel {
/*  25 */     Global,
/*  26 */     Group;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  33 */   public Map<String, List<Object>> invitations = new HashMap<>();
/*  34 */   public Map<String, String> cooldowns = new HashMap<>();
/*  35 */   public List<String> creationCooldowns = new ArrayList<>();
/*     */   
/*     */   private FileConfiguration messages;
/*     */   public FileConfiguration config;
/*  39 */   public List<String> blacklist = new ArrayList<>();
/*     */   
/*  41 */   public List<String> spies = new ArrayList<>();
/*     */ 
/*     */ 
/*     */   
/*     */   public void onEnable() {
/*  46 */     getCommand("group").setExecutor((CommandExecutor)new GroupCommand(this));
/*  47 */     getCommand("group").setTabCompleter((TabCompleter)new GroupTabCompleter());
/*     */     
/*  49 */     getServer().getPluginManager().registerEvents(new EventListener(this), (Plugin)this);
/*     */     
/*  51 */     getDataFolder().mkdirs();
/*     */     
/*     */     try {
/*  54 */       GroupStore.connectionString = "jdbc:sqlite:" + getDataFolder() + File.separator + "groups.db";
/*  55 */       GroupStore.getConnection();
/*     */       
/*  57 */       getLogger().info(ChatColor.AQUA + "Successfully connected to SQLite.");
/*  58 */       GroupStore.migrate();
/*     */     }
/*  60 */     catch (SQLException e) {
/*  61 */       getLogger().warning(e.getMessage());
/*     */     } 
/*     */     
/*  64 */     loadConfig();
/*  65 */     loadLanguageConfig();
/*  66 */     loadBlacklist();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void onDisable() {
/*  72 */     GroupStore.close();
/*     */   }
/*     */ 
/*     */   
/*     */   public void reloadConfigs() {
/*  77 */     loadConfig();
/*  78 */     loadLanguageConfig();
/*  79 */     loadBlacklist();
/*     */   }
/*     */ 
/*     */   
/*     */   public void broadcastToAllMembers(String group, String message) {
/*  84 */     for (Player player : Bukkit.getOnlinePlayers()) {
/*  85 */       if (!player.hasMetadata("chat_group") || !((MetadataValue)player.getMetadata("chat_group").get(0)).asString().equals(group))
/*     */         continue; 
/*  87 */       player.sendMessage(message);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   void broadcastToAllSpies(String group, String message) {
/*  93 */     for (Player player : Bukkit.getOnlinePlayers()) {
/*  94 */       if (!this.spies.contains(player.getName()) || (
/*  95 */         player.hasMetadata("group") && ((MetadataValue)player.getMetadata("group").get(0)).asString().equals(group)))
/*     */         continue; 
/*  97 */       player.sendMessage("§8[§6GSPY§8] " + message);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public int promote(Player player, String group) {
/* 103 */     int currentRank = ((MetadataValue)player.getMetadata("chat_group_rank").get(0)).asInt();
/* 104 */     if (currentRank == 0) {
/* 105 */       String uuid = player.getUniqueId().toString();
/* 106 */       player.setMetadata("chat_group_rank", (MetadataValue)new FixedMetadataValue((Plugin)this, Integer.valueOf(1)));
/*     */       
/*     */       try {
/* 109 */         GroupStore.removeMember(group, uuid);
/* 110 */         GroupStore.addAdmin(group, uuid);
/*     */         
/* 112 */         return 1;
/* 113 */       } catch (SQLException e) {
/* 114 */         return 0;
/*     */       } 
/*     */     } 
/* 117 */     return -1;
/*     */   }
/*     */ 
/*     */   
/*     */   public int demote(Player player, String group) {
/* 122 */     int currentRank = ((MetadataValue)player.getMetadata("chat_group_rank").get(0)).asInt();
/* 123 */     if (currentRank == 0)
/* 124 */       return -1; 
/* 125 */     if (currentRank == 1) {
/* 126 */       String uuid = player.getUniqueId().toString();
/* 127 */       player.setMetadata("chat_group_rank", (MetadataValue)new FixedMetadataValue((Plugin)this, Integer.valueOf(0)));
/*     */       
/*     */       try {
/* 130 */         GroupStore.removeAdmin(group, uuid);
/* 131 */         GroupStore.addMember(group, uuid);
/*     */         
/* 133 */         return 1;
/* 134 */       } catch (SQLException e) {
/* 135 */         return 0;
/*     */       } 
/*     */     } 
/* 138 */     return -1;
/*     */   }
/*     */ 
/*     */   
/*     */   public void kickAll(List<Object> data) {
/* 143 */     Player owner = Bukkit.getPlayer((UUID)data.get(1));
/* 144 */     if (owner != null) {
/* 145 */       owner.removeMetadata("chat_group", (Plugin)this);
/* 146 */       owner.removeMetadata("chat_group_rank", (Plugin)this);
/* 147 */       owner.removeMetadata("chat_channel", (Plugin)this);
/*     */     } 
/*     */     
/* 150 */     for (UUID uuid : data.get(2)) {
/* 151 */       Player p = Bukkit.getPlayer(uuid);
/* 152 */       if (p == null)
/* 153 */         continue;  p.removeMetadata("chat_group", (Plugin)this);
/* 154 */       p.removeMetadata("chat_group_rank", (Plugin)this);
/* 155 */       p.removeMetadata("chat_channel", (Plugin)this);
/*     */     } 
/*     */     
/* 158 */     for (UUID uuid : data.get(3)) {
/* 159 */       Player p = Bukkit.getPlayer(uuid);
/* 160 */       if (p == null)
/* 161 */         continue;  p.removeMetadata("chat_group", (Plugin)this);
/* 162 */       p.removeMetadata("chat_group_rank", (Plugin)this);
/* 163 */       p.removeMetadata("chat_channel", (Plugin)this);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void transferOwner(Player originalOwner, Player newOwner, String group) {
/* 169 */     String uuid = newOwner.getUniqueId().toString();
/*     */     try {
/* 171 */       int rank = ((MetadataValue)newOwner.getMetadata("chat_group_rank").get(0)).asInt();
/* 172 */       if (rank == 1) {
/* 173 */         GroupStore.removeAdmin(group, uuid);
/*     */       } else {
/* 175 */         GroupStore.removeMember(group, uuid);
/*     */       } 
/* 177 */     } catch (SQLException e) {
/* 178 */       e.printStackTrace();
/*     */     } 
/*     */     
/* 181 */     originalOwner.setMetadata("chat_group_rank", (MetadataValue)new FixedMetadataValue((Plugin)this, Integer.valueOf(1)));
/* 182 */     newOwner.setMetadata("chat_group_rank", (MetadataValue)new FixedMetadataValue((Plugin)this, Integer.valueOf(2)));
/*     */     
/*     */     try {
/* 185 */       GroupStore.transferOwner(group, uuid);
/* 186 */       GroupStore.addAdmin(group, originalOwner.getUniqueId().toString());
/* 187 */     } catch (SQLException e) {
/* 188 */       e.printStackTrace();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void leaveGroup(Player player, String group) {
/* 194 */     int rank = ((MetadataValue)player.getMetadata("chat_group_rank").get(0)).asInt();
/*     */     
/* 196 */     player.removeMetadata("chat_group", (Plugin)this);
/* 197 */     player.removeMetadata("chat_group_rank", (Plugin)this);
/* 198 */     player.removeMetadata("chat_channel", (Plugin)this);
/*     */     
/*     */     try {
/* 201 */       if (rank == 1) {
/* 202 */         GroupStore.removeAdmin(group, player.getUniqueId().toString());
/*     */       } else {
/* 204 */         GroupStore.removeMember(group, player.getUniqueId().toString());
/*     */       } 
/* 206 */     } catch (SQLException e) {
/* 207 */       e.printStackTrace();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean joinGroup(Player player, String group) {
/* 213 */     player.setMetadata("chat_group", (MetadataValue)new FixedMetadataValue((Plugin)this, group));
/* 214 */     player.setMetadata("chat_group_rank", (MetadataValue)new FixedMetadataValue((Plugin)this, Integer.valueOf(0)));
/* 215 */     player.setMetadata("chat_channel", (MetadataValue)new FixedMetadataValue((Plugin)this, Channel.Global));
/*     */     
/*     */     try {
/* 218 */       GroupStore.addMember(group, player.getUniqueId().toString());
/*     */       
/* 220 */       return true;
/* 221 */     } catch (SQLException e) {
/* 222 */       e.printStackTrace();
/*     */       
/* 224 */       return false;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public String serializePlayers(List<UUID> players) {
/* 230 */     return players.stream()
/* 231 */       .map(this::getPlayerName)
/* 232 */       .collect(Collectors.joining(", "));
/*     */   }
/*     */ 
/*     */   
/*     */   public String getPlayerName(UUID uuid) {
/* 237 */     OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
/*     */     
/* 239 */     return (player == null) ? "null" : player.getName();
/*     */   }
/*     */ 
/*     */   
/*     */   private void loadConfig() {
/* 244 */     File file = saveResource("config.yml");
/*     */     
/* 246 */     this.config = (FileConfiguration)new YamlConfiguration();
/*     */     try {
/* 248 */       this.config.load(file);
/* 249 */     } catch (Exception e) {
/* 250 */       e.printStackTrace();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private void loadLanguageConfig() {
/* 256 */     File file = saveResource("messages.yml");
/*     */     
/* 258 */     this.messages = (FileConfiguration)new YamlConfiguration();
/*     */     try {
/* 260 */       this.messages.load(file);
/* 261 */     } catch (Exception e) {
/* 262 */       e.printStackTrace();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private void loadBlacklist() {
/* 268 */     File file = saveResource("blacklist.txt");
/*     */ 
/*     */     
/*     */     try {
/* 272 */       BufferedReader reader = new BufferedReader(new FileReader(file));
/* 273 */       String line = reader.readLine();
/* 274 */       while (line != null) {
/* 275 */         this.blacklist.add(line);
/*     */         
/* 277 */         line = reader.readLine();
/*     */       } 
/* 279 */       reader.close();
/* 280 */     } catch (IOException e) {
/* 281 */       e.printStackTrace();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private File saveResource(String filename) {
/* 287 */     File file = new File(getDataFolder(), filename);
/* 288 */     if (!file.exists()) {
/* 289 */       saveResource(filename, false);
/*     */     }
/*     */     
/* 292 */     return file;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getMessage(String path) {
/* 297 */     return this.messages.isSet(path) ? this.messages.getString(path).replaceAll("&", "§") : path;
/*     */   }
/*     */ }


/* Location:              D:\Java\Frick\plugins\Groups-1.5.jar!\net\fricktastic\groups\Groups.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */