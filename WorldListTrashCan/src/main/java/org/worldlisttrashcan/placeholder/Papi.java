package org.worldlisttrashcan.placeholder;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.worldlisttrashcan.system.trash.cleartask.ClearItemsTask;
import org.worldlisttrashcan.system.trash.cleartask.FoliaClearItemsTask;
import org.worldlisttrashcan.WorldListTrashCan;

public class Papi extends PlaceholderExpansion{
    WorldListTrashCan plugin; // This instance is assigned in canRegister()
    public Papi(WorldListTrashCan plugin) {
//        System.out.println("register!!");
        this.plugin = plugin;
    }
    @Override
    public boolean persist() {
        return true; // This is required or else PlaceholderAPI will unregister the Expansion on reload
    }
    @Override
    public String getAuthor() {
        return "BlueNine";
    }
    @Override
    public String getIdentifier() {
        return "Wtc";
    }


    @Override
    public String getVersion() {
        return "1.0.0";
    }

    @Override
    public String onRequest(OfflinePlayer player, String params) {
        if(params.startsWith("ClearTime")){
            ClearItemsTask clearItemsTask = plugin.getClearItemsTask();
            FoliaClearItemsTask foliaClearItemsTask = plugin.getFoliaClearItemsTask();
            if (clearItemsTask!=null) {
                //获取扫地的时间
                return clearItemsTask.getPublicTime()+"";
            }
            if (foliaClearItemsTask!=null) {
                //获取扫地的时间
                return foliaClearItemsTask.getPublicTime()+"";
            }
        }
//        Player player1 = player.getPlayer();
//        if(player1!=null&&WorldNameToReplaceName.get(player1.getWorld().getName())!=null){
////            return WorldNameToReplaceName.get(player1.getWorld().getName());
//            return (WorldNameToReplaceName.get(player1.getWorld().getName()));
//        }else {
//            return otherWorld;
//        }
        return "null";

    }

}