package org.worldlisttrashcan.WorldLimitEntityCount;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.function.Consumer;

import static org.worldlisttrashcan.IsVersion.IsFoliaServer;
import static org.worldlisttrashcan.WorldLimitEntityCount.LimitMain.*;
import static org.worldlisttrashcan.WorldLimitEntityCount.removeEntity.dealEntity;
import static org.worldlisttrashcan.WorldListTrashCan.main;


public class BukkitPlayerMoveEvent implements Listener {
    @EventHandler
    public void PlayerMoveEvent(PlayerMoveEvent event){


        Player player = event.getPlayer();
        if(GatherLimitFlag){
            if (GatherBanWorlds.contains(player.getWorld().getName())) {
                return;
            }
//            for(Entity entity : player.getNearbyEntities(10,10,10)){
//                if (entity instanceof LivingEntity) {
//                    //如果没血了就不处理
//                    if (((LivingEntity) entity).getHealth() <= 0) {
//                        return;
//                    }
//                }
//                String entityType = entity.getName();
//                if (GatherLimits.containsKey(entityType.toUpperCase())) {
//                    dealEntity(entity);
//                }
//            }

            if (IsFoliaServer){
                Bukkit.getRegionScheduler().run(main, player.getLocation(), new Consumer<ScheduledTask>() {
                    @Override
                    public void accept(ScheduledTask scheduledTask) {
                        dealPlayer(player);
                    }
                });
            }else {
                dealPlayer(player);
            }



        }
    }

    public void dealPlayer(Player player){
        for(Entity entity : player.getNearbyEntities(10,10,10)){
            if (entity instanceof LivingEntity) {
                //如果没血了就不处理
                if (((LivingEntity) entity).getHealth() <= 0) {
                    return;
                }
            }
            String entityType = entity.getName();
            if (GatherLimits.containsKey(entityType.toUpperCase())) {
                dealEntity(entity);
            }
        }
    }
}