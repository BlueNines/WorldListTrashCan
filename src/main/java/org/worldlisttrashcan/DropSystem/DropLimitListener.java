package org.worldlisttrashcan.DropSystem;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.worldlisttrashcan.message;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DropLimitListener implements Listener {
    public static List<Player> PlayerDropList = new ArrayList<>();
    @EventHandler
    public void PlayerOnDropItem(PlayerDropItemEvent event){
        Player player = event.getPlayer();
        if(PlayerDropList.contains(player)){
            event.setCancelled(true);
            player.sendMessage(message.find("LimitDropItem"));
        }

    }
}
