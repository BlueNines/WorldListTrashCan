package org.worldlisttrashcan.system.drop;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.worldlisttrashcan.utils.Message;

import java.util.ArrayList;
import java.util.List;

public class DropLimitListener implements Listener {
    public static List<Player> PlayerDropList = new ArrayList<>();
    @EventHandler
    public void PlayerOnDropItem(PlayerDropItemEvent event){
        Player player = event.getPlayer();
        if(PlayerDropList.contains(player)){
            event.setCancelled(true);
            player.sendMessage(Message.find("LimitDropItem"));
        }

    }
}
