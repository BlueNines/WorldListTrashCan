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
