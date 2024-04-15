package org.worldlisttrashcan.AutoTrashMain;

import org.bukkit.entity.Item;
import org.bukkit.event.player.PlayerDropItemEvent;

import static org.worldlisttrashcan.AutoTrashMain.AutoTrashListener.ItemToPlayer;
import static org.worldlisttrashcan.WorldListTrashCan.main;

public class FoliaRunable {
    //这个folia的任务必须写在非监听器外，不然会出现错误
    public void FoliaTask(PlayerDropItemEvent event){
        event.getPlayer().getScheduler().runDelayed(main, scheduledTask -> {
            Item item = event.getItemDrop();

            if(ItemToPlayer.get(item)!=null){
                ItemToPlayer.remove(item);
            }
        }, () -> main.getLogger().info("Error,Player is null"),main.getConfig().getInt("Set.GlobalTrash.OriginalFeatureClearItemAddGlobalTrash.Delay")*20L);

    }
}
