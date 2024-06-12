package org.worldlisttrashcan.WorldLimitEntityCount;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.worldlisttrashcan.message;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import java.util.ArrayList;
import java.util.List;

import static org.worldlisttrashcan.WorldLimitEntityCount.LimitMain.GatherLimits;

public class removeEntity {
    public static boolean ItemDropFlag = false;
    public static void removeLivingEntity(LivingEntity livingEntity){
        if(ItemDropFlag){
            livingEntity.setHealth(0);
        }else {
            livingEntity.remove();
        }
    }




    public void dealEntity(Entity entity){
        EntityType entityType = entity.getType();

        int limit = GatherLimits.get(entityType.name())[0];
        int range = GatherLimits.get(entityType.name())[1];
        int clearCount = GatherLimits.get(entityType.name())[2];
//                int count = 0;
        List<org.bukkit.entity.Entity> entityList = new ArrayList<>();
        List<Player> PlayerList = new ArrayList<>();

        for (org.bukkit.entity.Entity NearEntity : entity.getNearbyEntities(range, range, range)) {
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
                org.bukkit.entity.Entity entity1 = entityList.get(i);
                if(entity1 instanceof LivingEntity){
                    LivingEntity livingEntity = (LivingEntity)entity1;
//                            livingEntity.setHealth(0);
                    removeLivingEntity(livingEntity);
                }else {
                    entity1.remove();
                }

//                        entity1.remove();
            }

        }
        }
}
