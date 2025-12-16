package org.worldlisttrashcan.system.limitentity;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.scheduler.BukkitRunnable;

import static org.worldlisttrashcan.system.limitentity.LimitMain.*;
import static org.worldlisttrashcan.system.limitentity.LimitMain.GatherLimits;
import static org.worldlisttrashcan.system.limitentity.removeEntity.dealEntity;
import static org.worldlisttrashcan.WorldListTrashCan.*;
//import static org.worldlisttrashcan.TrashMain.getInventory.getState;

public class BukkitClearGatherEntityTask {
    BukkitRunnable bukkitRunnable;

    public BukkitClearGatherEntityTask() {
        bukkitRunnable = new BukkitRunnable() {

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
//                                System.out.println(entity.getName());


                                if(GatherLimitFlag){
//                                    String worldName = entity.getWorld().getName();
//                                    EntityType entityType = entity.getType();
                                    String entityType = entity.getName();


//                                    if (GatherLimits.containsKey(entityType.name())) {
                                    if (GatherLimits.containsKey(entityType.toUpperCase())) {
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
