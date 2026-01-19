package org.worldlisttrashcan.system.limitentity;

import io.papermc.paper.event.entity.EntityMoveEvent;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import static org.worldlisttrashcan.system.limitentity.LimitMain.*;
import static org.worldlisttrashcan.system.limitentity.removeEntity.dealEntity;


public class PaperEntityMoveEvent implements Listener {

    int count = 0;


    @EventHandler
    public void EntityMoveEvent(EntityMoveEvent event){

        count++;

        if (count > 50){
            count = 0;
            if(GatherLimitFlag){

                Entity entity = event.getEntity();

                //如果没血了就不处理
                if (((LivingEntity) entity).getHealth() <= 0) {
//                System.out.println("return 了1");
                    return;
                }

//            System.out.println("000 "+entity.getName());
                if (GatherBanWorlds.contains(entity.getWorld().getName())) {
                    return;
                }
//            EntityType entityType = entity.getType();
                String entityType = entity.getName();
//            System.out.println("entityType.name() "+entityType.name());

//            System.out.println("111 "+entity.getName());

                if (GatherLimits.containsKey(entityType.toUpperCase())) {
                    dealEntity(entity);
                }
            }
        }


    }

}