/*     */ package net.fricktastic.groups;
/*     */ import java.sql.Connection;
/*     */ import java.sql.ResultSet;
/*     */ import java.sql.SQLException;
/*     */ import java.sql.Statement;
/*     */ import java.util.List;
/*     */ import java.util.UUID;
/*     */ 
/*     */ public class GroupStore {
/*  10 */   public static String connectionString = "";
/*     */   
/*  12 */   private static Connection con = null;
/*     */ 
/*     */   
/*     */   static Connection getConnection() throws SQLException {
/*  16 */     if (con == null) {
/*  17 */       con = DriverManager.getConnection(connectionString);
/*     */     }
/*  19 */     return con;
/*     */   }
/*     */ 
/*     */   
/*     */   static void close() {
/*     */     try {
/*  25 */       if (con != null) {
/*  26 */         con.close();
/*     */       }
/*  28 */     } catch (SQLException e) {
/*  29 */       e.printStackTrace();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   static void migrate() throws SQLException {
/*  35 */     Connection con = getConnection();
/*  36 */     Statement stmt = con.createStatement();
/*     */     
/*  38 */     stmt.execute("CREATE TABLE IF NOT EXISTS groups (name VARCHAR(10),owner VARCHAR(36),admins VARCHAR(111) DEFAULT '',members VARCHAR(555) DEFAULT '',UNIQUE(name),CHECK (LENGTH(admins) <= 111), CHECK (LENGTH(members) <= 555) )");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static void addAdmin(String name, String player) throws SQLException {
/*  50 */     Connection con = getConnection();
/*  51 */     Statement stmt = con.createStatement();
/*     */     
/*  53 */     stmt.executeUpdate("UPDATE groups SET admins=admins || '" + player + "' || ',' WHERE name='" + name + "';");
/*     */   }
/*     */ 
/*     */   
/*     */   static void removeAdmin(String name, String player) throws SQLException {
/*  58 */     Connection con = getConnection();
/*  59 */     Statement stmt = con.createStatement();
/*     */     
/*  61 */     stmt.executeUpdate("UPDATE groups SET admins=REPLACE(admins, '" + player + ",', '') WHERE name='" + name + "';");
/*     */   }
/*     */ 
/*     */   
/*     */   static void transferOwner(String name, String player) throws SQLException {
/*  66 */     Connection con = getConnection();
/*  67 */     Statement stmt = con.createStatement();
/*     */     
/*  69 */     stmt.executeUpdate("UPDATE groups SET owner='" + player + "' WHERE name='" + name + "';");
/*     */   }
/*     */ 
/*     */   
/*     */   static void addMember(String name, String player) throws SQLException {
/*  74 */     Connection con = getConnection();
/*  75 */     Statement stmt = con.createStatement();
/*     */     
/*  77 */     stmt.executeUpdate("UPDATE groups SET members=members || '" + player + ",' WHERE name='" + name + "';");
/*     */   }
/*     */ 
/*     */   
/*     */   static void removeMember(String name, String player) throws SQLException {
/*  82 */     Connection con = getConnection();
/*  83 */     Statement stmt = con.createStatement();
/*     */     
/*  85 */     stmt.executeUpdate("UPDATE groups SET members=REPLACE(members, '" + player + ",', '') WHERE name='" + name + "';");
/*     */   }
/*     */ 
/*     */   
/*     */   public static void createGroup(String name, String owner) throws SQLException {
/*  90 */     Connection con = getConnection();
/*  91 */     Statement stmt = con.createStatement();
/*     */     
/*  93 */     stmt.executeUpdate("INSERT INTO groups VALUES ( '" + name + "', '" + owner + "', '', '' );");
/*     */   }
/*     */ 
/*     */   
/*     */   public static void disbandGroup(String name) throws SQLException {
/*  98 */     Connection con = getConnection();
/*  99 */     Statement stmt = con.createStatement();
/*     */     
/* 101 */     stmt.executeUpdate("DELETE FROM groups WHERE name='" + name + "';");
/*     */   }
/*     */ 
/*     */   
/*     */   public static int purgeGroups() throws SQLException {
/* 106 */     Connection con = getConnection();
/* 107 */     Statement stmt = con.createStatement();
/*     */     
/* 109 */     return stmt.executeUpdate("DELETE FROM groups WHERE admins='' AND members='';");
/*     */   }
/*     */ 
/*     */   
/*     */   public static boolean groupExists(String name) throws SQLException {
/* 114 */     Connection con = getConnection();
/* 115 */     Statement stmt = con.createStatement();
/*     */     
/* 117 */     ResultSet rs = stmt.executeQuery("SELECT count(1) FROM groups WHERE name='" + name + "' COLLATE NOCASE;");
/* 118 */     if (rs.next()) {
/* 119 */       return (rs.getInt(1) == 1);
/*     */     }
/* 121 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public static int getGroupsCount() throws SQLException {
/* 126 */     Connection con = getConnection();
/* 127 */     Statement stmt = con.createStatement();
/*     */     
/* 129 */     ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM groups;");
/* 130 */     if (rs.next()) {
/* 131 */       return rs.getInt(1);
/*     */     }
/* 133 */     return 0;
/*     */   }
/*     */ 
/*     */   
/*     */   public static List<List<Object>> listGroups(int offset) throws SQLException {
/* 138 */     Connection con = getConnection();
/* 139 */     Statement stmt = con.createStatement();
/*     */     
/* 141 */     ResultSet rs = stmt.executeQuery("SELECT name, admins, members FROM groups ORDER BY LENGTH(admins) + LENGTH(members) DESC, name COLLATE NOCASE ASC LIMIT 10 OFFSET " + (offset * 10) + ";");
/* 142 */     List<List<Object>> result = new ArrayList<>();
/* 143 */     while (rs.next()) {
/* 144 */       List<Object> data = new ArrayList();
/* 145 */       data.add(rs.getString("name"));
/* 146 */       int count = (rs.getString("admins").length() + rs.getString("members").length()) / 37;
/* 147 */       data.add(Integer.valueOf(++count));
/* 148 */       result.add(data);
/*     */     } 
/* 150 */     return result;
/*     */   }
/*     */ 
/*     */   
/*     */   public static List<Object> fetchGroupData(String name) throws SQLException {
/* 155 */     Connection con = getConnection();
/* 156 */     Statement stmt = con.createStatement();
/*     */     
/* 158 */     ResultSet rs = stmt.executeQuery("SELECT name, owner, admins, members FROM groups WHERE name='" + name + "' COLLATE NOCASE;");
/* 159 */     if (rs.next()) {
/* 160 */       List<Object> result = new ArrayList();
/*     */       
/* 162 */       result.add(rs.getString("name"));
/* 163 */       result.add(UUID.fromString(rs.getString("owner")));
/* 164 */       List<UUID> players = new ArrayList<>();
/* 165 */       for (String uuid : rs.getString("admins").split(",")) {
/* 166 */         if (!uuid.equals(""))
/*     */         {
/* 168 */           players.add(UUID.fromString(uuid)); } 
/*     */       } 
/* 170 */       result.add(players);
/* 171 */       players = new ArrayList<>();
/* 172 */       for (String uuid : rs.getString("members").split(",")) {
/* 173 */         if (!uuid.equals(""))
/*     */         {
/* 175 */           players.add(UUID.fromString(uuid)); } 
/*     */       } 
/* 177 */       result.add(players);
/*     */       
/* 179 */       return result;
/*     */     } 
/* 181 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public static List<Object> fetchGroupForPlayer(String player) throws SQLException {
/* 186 */     Connection con = getConnection();
/* 187 */     Statement stmt = con.createStatement();
/*     */     
/* 189 */     ResultSet rs = stmt.executeQuery("SELECT name, owner, admins FROM groups WHERE owner='" + player + "' OR admins LIKE '%" + player + "%' OR members LIKE '%" + player + "%';");
/* 190 */     if (rs.next()) {
/* 191 */       List<Object> result = new ArrayList();
/*     */       
/* 193 */       result.add(rs.getString("name"));
/* 194 */       result.add(UUID.fromString(rs.getString("owner")));
/* 195 */       List<UUID> players = new ArrayList<>();
/* 196 */       for (String uuid : rs.getString("admins").split(",")) {
/* 197 */         if (!uuid.equals(""))
/*     */         {
/* 199 */           players.add(UUID.fromString(uuid)); } 
/*     */       } 
/* 201 */       result.add(players);
/*     */       
/* 203 */       return result;
/*     */     } 
/* 205 */     return null;
/*     */   }
/*     */ }


/* Location:              D:\Java\Frick\plugins\Groups-1.5.jar!\net\fricktastic\groups\GroupStore.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */