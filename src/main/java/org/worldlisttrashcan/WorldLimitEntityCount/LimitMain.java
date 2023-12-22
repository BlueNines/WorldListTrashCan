package org.worldlisttrashcan.WorldLimitEntityCount;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.worldlisttrashcan.message;

import static org.worldlisttrashcan.WorldListTrashCan.UseEntityBarPlayerList;

/* loaded from: 限制世界实体数量，最终版 - 副本.jar:org/example9/javafirstplugin/Main.class */
public class LimitMain implements Listener {
    public static Map<String, Integer> worldLimits = new HashMap<>();
    public static List<String> BanWorlds = new ArrayList<>();
    public static boolean WorldLimitFlag = false;


    public static Map<String, int[]> GatherLimits = new HashMap<String, int[]>();
    public static List<String> GatherBanWorlds = new ArrayList<>();
    public static boolean GatherLimitFlag = false;





//    public static Map<String, Integer> globalLimits = new HashMap<>();

//    public LimitMain() {
//        EntityType entityType = getEntityType("Villager");
//        if (entityType == null) {
//            main.getLogger().info(ChatColor.RED + "实体类型错误！可选的实体类型包括：" + getEntityTypes());
////            return true;
//        }
//        int limit = 3;
//        this.worldLimits.put("world" + "." + entityType.name(), Integer.valueOf(limit));
//        main.getLogger().info(ChatColor.GREEN + "成功设置 " + entityType.name() + " 的数量限制为 " + limit);
//
//    }



//    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
//        if (command.getName().equalsIgnoreCase("limitworld")) {
//            if (!(sender instanceof Player)) {
//                sender.sendMessage(ChatColor.RED + "该命令只能在游戏内使用！");
//                return true;
//            }
//            Player player = (Player) sender;
//            if (!player.isOp()) {
//                player.sendMessage(ChatColor.RED + "你没有权限使用该命令！");
//                return true;
//            } else if (args.length != 2) {
//                player.sendMessage(ChatColor.RED + "命令格式错误！正确格式：/limitworld <数量> <实体类型>");
//                return true;
//            } else {
//                EntityType entityType = getEntityType(args[1]);
//                if (entityType == null) {
//                    player.sendMessage(ChatColor.RED + "实体类型错误！可选的实体类型包括：" + getEntityTypes());
//                    return true;
//                }
//                int limit = Integer.parseInt(args[0]);
//                this.worldLimits.put(player.getWorld().getName() + "." + entityType.name(), Integer.valueOf(limit));
//                player.sendMessage(ChatColor.GREEN + "成功设置 " + entityType.name() + " 的数量限制为 " + limit);
//                saveConfig();
//                return true;
//            }
//        } else if (command.getName().equalsIgnoreCase("limitglobalworld")) {
//            if (!(sender instanceof Player)) {
//                sender.sendMessage(ChatColor.RED + "该命令只能在游戏内使用！");
//                return true;
//            }
//            Player player2 = (Player) sender;
//            if (!player2.isOp()) {
//                player2.sendMessage(ChatColor.RED + "你没有权限使用该命令 ！");
//                return true;
//            } else if (args.length != 2) {
//                player2.sendMessage(ChatColor.RED + "命令格式错误！正确格式：/limitglobalworld <数量> <实体类型>");
//                return true;
//            } else {
//                EntityType entityType2 = getEntityType(args[1]);
//                if (entityType2 == null) {
//                    player2.sendMessage(ChatColor.RED + "实体类型错误！可选的实体类型包括：" + getEntityTypes());
//                    return true;
//                }
//                int limit2 = Integer.parseInt(args[0]);
//                this.globalLimits.put(entityType2.name(), Integer.valueOf(limit2));
//                player2.sendMessage(ChatColor.GREEN + "成功设置 " + entityType2.name() + " 的默认数量限制为 " + limit2);
//                return true;
//            }
//        } else {
//            return false;
//        }
//    }



    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {
//        System.out.println("1");
        Entity entity = event.getEntity();
        if (event.isCancelled()) {
            return;
        }
        EntityType entityType = entity.getType();
        String worldName = event.getLocation().getWorld().getName();
//        System.out.println("1 "+entityType);
//        System.out.println("2 "+worldName);
        if(WorldLimitFlag){
//            System.out.println("worldLimits "+worldLimits.toString());
//            System.out.println("BanWorlds.contains(worldName) "+BanWorlds.toString());
            if (worldLimits.containsKey(entityType.name())&&!BanWorlds.contains(worldName)) {
                int limit = worldLimits.get(entityType.name());
                if (getEntityCount(entityType, worldName) >= limit) {
                    event.setCancelled(true);
                    entity.remove();
                    return;
                }
            }
        }

        if(GatherLimitFlag){
//            System.out.println("GatherLimits "+GatherLimits.toString());
//            System.out.println("GatherBanWorlds "+GatherBanWorlds.toString());
            if (GatherLimits.containsKey(entityType.name())&&!GatherBanWorlds.contains(worldName)) {
                int limit = GatherLimits.get(entityType.name())[0];
                int range = GatherLimits.get(entityType.name())[1];
                int clearCount = GatherLimits.get(entityType.name())[2];
//                int count = 0;
                List<Entity> entityList = new ArrayList<>();
                List<Player> PlayerList = new ArrayList<>();

                for (Entity NearEntity : event.getEntity().getNearbyEntities(range, range, range)) {
                    if(NearEntity.getType() == entity.getType()){
                        entityList.add(NearEntity);
                    }
                    if(NearEntity instanceof Player){
                        PlayerList.add((Player) NearEntity);
                    }
                }
                int size = entityList.size();
//                System.out.println("size is "+size +"  limit is "+limit);
                if(size>limit-1){
//                    event.setCancelled(true);

                    for (Player player : PlayerList) {

                        //你的附近 %range% 格内有 %entityType%x%size%只 , 达到密集实体的要求，已清理
                        player.sendMessage(message.find("GatherClearToNearPlayerMessage").replace("%entityType%",entityType+"").replace("%range%",range+"").replace("%size%",size+""));

                    }
                    if(clearCount>size){
                        clearCount = size;
                    }
                    for (int i = 0 ;i<clearCount;i++) {
                        Entity entity1 = entityList.get(i);
                        if(entity1 instanceof LivingEntity){
                            LivingEntity livingEntity = (LivingEntity)entity1;
                            livingEntity.setHealth(0);
                        }else {
                            entity1.remove();
                        }

//                        entity1.remove();
                    }


                }
            }
        }
//        if(true){
//
//            int count = 0;
//            //附近10格内的实体
//            for (Entity NearEntity : event.getEntity().getNearbyEntities(10, 10, 10)) {
//                if(NearEntity.getType() == entity.getType()){
//                    count++;
//                }
//            }
//            if(count>10){
//                event.setCancelled(true);
//            }
//        }


//        else if (this.globalLimits.containsKey(entityType.name())) {
//            int limit2 = this.globalLimits.get(entityType.name()).intValue();
//            if (getEntityCount(entityType, worldName) >= limit2) {
//                event.setCancelled(true);
//                event.getEntity().remove();
//            }
//        }
    }

//    private void loadWorldLimitConfig() {
//        this.configFile = new File(getDataFolder(), "config.yml");
//        saveDefaultConfig();
//        reloadConfig();
//        this.config = YamlConfiguration.loadConfiguration(this.configFile);
//        this.worldLimits = new HashMap();
//        this.globalLimits = new HashMap();
//        if (this.config.contains("global_limits")) {
//            for (String key : this.config.getConfigurationSection("global_limits").getKeys(false)) {
//                int limit = this.config.getInt("global_limits." + key);
//                this.globalLimits.put(key, Integer.valueOf(limit));
//                getLogger().info("加载全局 " + key + " 数量限制为 " + limit);
//            }
//        }
//        if (this.config.contains("world_limits")) {
//            for (String key2 : this.config.getConfigurationSection("world_limits").getKeys(false)) {
//                for (String keys : this.config.getConfigurationSection("world_limits." + key2).getKeys(false)) {
//                    String[] parts = key2.split("\\.");
//                    String worldName = parts[0];
//                    int limit2 = this.config.getInt("world_limits." + key2 + "." + keys);
//                    this.worldLimits.put(worldName + "." + keys, Integer.valueOf(limit2));
//                    getLogger().info("加载世界 " + worldName + " 中的 " + keys + " 数量限制为 " + limit2);
//                }
//            }
//        }
//    }
//
//    public void saveConfig() {
//        for (String key : this.worldLimits.keySet()) {
//            int limit = this.worldLimits.get(key).intValue();
//            this.config.set("world_limits." + key, Integer.valueOf(limit));
//        }
//        for (String key2 : this.globalLimits.keySet()) {
//            int limit2 = this.globalLimits.get(key2).intValue();
//            this.config.set("global_limits." + key2, Integer.valueOf(limit2));
//        }
//        try {
//            this.config.save(this.configFile);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
    public static EntityType getEntityType(String name) {
        for (EntityType type : EntityType.values()) {
            if (type.name().equalsIgnoreCase(name)) {
                return type;
            }
        }
        return null;
    }
//
    public static String getEntityTypes() {
        StringBuilder sb = new StringBuilder();
        for (EntityType type : EntityType.values()) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(type.name());
        }
        return sb.toString();
    }

    public static int getEntityCount(EntityType entityType, String worldName) {
        int count = 0;
        for (Entity entity : Bukkit.getWorld(worldName).getEntities()) {
            if (entity.getType() == entityType) {
                count++;
            }
        }
        return count;
    }

    @EventHandler
    public void onClickEntity(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        if (UseEntityBarPlayerList.contains(player)) {
            Entity entity = event.getRightClicked();
            if(entity.getType()!=null){
                String typename = entity.getType().name();
                player.sendMessage(message.find("ClickFindEntityType").replace("%EntityType%",typename));

                UseEntityBarPlayerList.remove(player);
            }

        }
    }
}
