package org.worldlisttrashcan.SpeakSystem;



import io.papermc.paper.event.player.AsyncChatEvent;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.worldlisttrashcan.WorldListTrashCan;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import static org.worldlisttrashcan.IsVersion.IsFoliaServer;
import static org.worldlisttrashcan.WorldListTrashCan.main;

public class QuickSpeakListener implements Listener {
    public static Map<Player,Long> PlayerToChatMessage = new HashMap<>();

    String NotSpeakMessage = "不要刷屏";

    double Time = 2;

//ChatSet:
//  QuickSendMessage:
//    Flag: true
//    #两秒允许发送一次聊天消息
//    Time: 2
//    #提醒语句
//    Message: "&c请不要刷屏"
    public void Init(){
        NotSpeakMessage = WorldListTrashCan.main.getConfig().getString("ChatSet.QuickSendMessage.Message");
        Time = WorldListTrashCan.main.getConfig().getDouble("ChatSet.QuickSendMessage.Time");
        PlayerToChatMessage.clear();
    }

    @EventHandler
    //只有低版本bukkit有
//    public void PlayerOnSpeak(PlayerChatEvent event){
    //中高版本有
    public void PlayerOnSpeak(AsyncPlayerChatEvent event){
        Player player =event.getPlayer();
        if(event.isCancelled()||player.isOp()){

            return;
        }

        if(PlayerToChatMessage.get(player)!=null){

            //计算时间戳与当前时间戳的间隔（单位秒）
            long interval = (System.currentTimeMillis() - PlayerToChatMessage.get(player)) / 1000;
            //如果时间间隔小于限制时间
            if (interval < Time) {
                event.setCancelled(true);
                player.sendMessage(NotSpeakMessage);
            }else {
                PlayerToChatMessage.put(player,System.currentTimeMillis());
            }
        }else {
            //记录玩家当前聊天的时间戳
            PlayerToChatMessage.put(player,System.currentTimeMillis());
        }
    }

    //如果玩家离开游戏，则清理相关信息
    @EventHandler
    public void PlayerLeaveGame(PlayerQuitEvent event){
        Player player =event.getPlayer();
        if(PlayerToChatMessage.get(player)!=null){
            PlayerToChatMessage.remove(player);
        }
    }


}
