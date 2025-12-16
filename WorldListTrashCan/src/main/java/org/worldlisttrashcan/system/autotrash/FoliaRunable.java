package org.worldlisttrashcan.system.autotrash;

import org.bukkit.entity.Item;
import org.bukkit.event.player.PlayerDropItemEvent;

import static org.worldlisttrashcan.system.autotrash.AutoTrashListener.ItemToPlayer;
import static org.worldlisttrashcan.WorldListTrashCan.main;
import static org.worldlisttrashcan.utils.Message.consoleSay;

public class FoliaRunable {
    //这个folia的任务必须写在非监听器外，不然会出现错误
    public void FoliaTask(PlayerDropItemEvent event){
        event.getPlayer().getScheduler().runDelayed(main, scheduledTask -> {
            Item item = event.getItemDrop();

            if(ItemToPlayer.get(item)!=null){
                ItemToPlayer.remove(item);
            }
        }, () -> consoleSay("Error,Player is null"),main.getConfig().getInt("Set.PersonalTrashCan.OriginalFeatureClearItemAddGlobalTrash.Delay")*20L);

    }
}
