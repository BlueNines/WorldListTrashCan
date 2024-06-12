package org.worldlisttrashcan.WorldLimitEntityCount;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.worldlisttrashcan.TrashMain.getInventory;
import org.worldlisttrashcan.data;
import org.worldlisttrashcan.message;

import java.util.*;

import static org.worldlisttrashcan.TrashMain.GlobalTrashGui.ClearContainer;
import static org.worldlisttrashcan.TrashMain.TrashListener.GlobalItemSetString;
import static org.worldlisttrashcan.WorldLimitEntityCount.LimitMain.*;
import static org.worldlisttrashcan.WorldLimitEntityCount.LimitMain.GatherLimits;
import static org.worldlisttrashcan.WorldLimitEntityCount.removeEntity.dealEntity;
import static org.worldlisttrashcan.WorldLimitEntityCount.removeEntity.removeLivingEntity;
import static org.worldlisttrashcan.WorldListTrashCan.*;
//import static org.worldlisttrashcan.TrashMain.getInventory.getState;

public class BukkitClearGatherEntityTask {
    BukkitRunnable bukkitRunnable;

    public BukkitClearGatherEntityTask() {
        bukkitRunnable = new BukkitRunnable() {
            
//            @Override
//            public void run() {
//                int count=1;
//                for (World world : Bukkit.getWorlds()) {
//
//                    count++;
//                    new BukkitRunnable() {
//
//                        @Override
//                        public void run() {
////                            System.out.println("1");
//                            for (Entity entity : world.getEntities()) {
////                                System.out.println("2");
//                                if(GatherLimitFlag){
////                                    System.out.println("3");
////            System.out.println("GatherLimits "+GatherLimits.toString());
////            System.out.println("GatherBanWorlds "+GatherBanWorlds.toString());
////                                    Entity entity = event.getEntity();
//                                    String worldName = entity.getWorld().getName();
//                                    EntityType entityType = entity.getType();
//                                    if (GatherLimits.containsKey(entityType.name())&&!GatherBanWorlds.contains(worldName)) {
//                                        int limit = GatherLimits.get(entityType.name())[0];
//                                        int range = GatherLimits.get(entityType.name())[1];
//                                        int clearCount = GatherLimits.get(entityType.name())[2];
////                int count = 0;
//                                        List<Entity> entityList = new ArrayList<>();
//                                        List<Player> PlayerList = new ArrayList<>();
//
//                                        for (Entity NearEntity : entity.getNearbyEntities(range, range, range)) {
//                                            if(NearEntity.getType() == entity.getType()){
//                                                entityList.add(NearEntity);
//                                            }
//                                            if(NearEntity instanceof Player){
//                                                PlayerList.add((Player) NearEntity);
//                                            }
//                                        }
//                                        int size = entityList.size();
////                System.out.println("size is "+size +"  limit is "+limit);
//                                        if(size>limit-1){
////                    event.setCancelled(true);
//
//                                            for (Player player : PlayerList) {
//
//                                                //你的附近 %range% 格内有 %entityType%x%size%只 , 达到密集实体的要求，已清理
//                                                player.sendMessage(message.find("GatherClearToNearPlayerMessage").replace("%entityType%",entityType+"").replace("%range%",range+"").replace("%size%",size+""));
//
//                                            }
//                                            if(clearCount>size){
//                                                clearCount = size;
//                                            }
//                                            for (int i = 0 ;i<clearCount;i++) {
//                                                Entity entity1 = entityList.get(i);
//                                                if(entity1 instanceof LivingEntity){
//                                                    LivingEntity livingEntity = (LivingEntity)entity1;
////                                                    livingEntity.setHealth(0);
//                                                    removeLivingEntity(livingEntity);
//                                                }else {
//                                                    entity1.remove();
//                                                }
//
////                        entity1.remove();
//                                            }
//
//
//                                        }
//                                    }
//                                }
//                            }
//
//                        }
//                    }.runTaskLater(main,20L*count);
//                }
//            }
//
//        };













            @Override
            public void run() {
                int count=1;
                for (World world : Bukkit.getWorlds()) {

                    count++;
                    new BukkitRunnable() {
                        @Override
                        public void run() {

                            if (GatherBanWorlds.contains(world.getName())) {
                                return;
                            }

                            for (Entity entity : world.getEntities()) {
                                if(GatherLimitFlag){
                                    String worldName = entity.getWorld().getName();
                                    EntityType entityType = entity.getType();
                                    if (GatherLimits.containsKey(entityType.name())) {
                                        dealEntity(entity);
                                    }
                                }
                            }

                        }
                    }.runTaskLater(main,20L*count);
                }
            }

        };








    }
    public void Start(){
        bukkitRunnable.runTaskTimer(main,100L,100L);
    }
    public void Stop(){
        bukkitRunnable.cancel();
    }


}
