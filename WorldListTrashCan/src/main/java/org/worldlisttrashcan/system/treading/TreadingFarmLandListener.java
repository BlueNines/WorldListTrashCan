package org.worldlisttrashcan.system.treading;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.worldlisttrashcan.utils.Enums;

import static org.bukkit.event.block.Action.PHYSICAL;

public class TreadingFarmLandListener implements Listener {


        private static final Material FARMLAND = (Material) Enums.oneOf(Material.class, new String[] { "FARMLAND", "SOIL" });


        @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
        public void onEntityInteract(EntityInteractEvent event) {
            Block block = event.getBlock();
            if (block.getType() == FARMLAND)
                event.setCancelled(true);
        }

        @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
        public void onPlayerInteract(PlayerInteractEvent event) {
            Block block;
            if(event.getAction() == PHYSICAL) {
                    block = event.getClickedBlock();
                    if (block.getType() == FARMLAND)
                        event.setCancelled(true);

            }
        }


}
