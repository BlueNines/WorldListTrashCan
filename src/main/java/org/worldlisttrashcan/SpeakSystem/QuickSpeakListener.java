package org.worldlisttrashcan.SpeakSystem;



import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.worldlisttrashcan.WorldListTrashCan;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static org.worldlisttrashcan.IsVersion.IsFoliaSever;
import static org.worldlisttrashcan.WorldListTrashCan.main;

public class QuickSpeakListener implements Listener {
    public static Map<Player,String> PlayerToChatMessage = new HashMap<>();

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
    public void PlayerOnSpeak(PlayerChatEvent event){
        Player player =event.getPlayer();
        if(event.isCancelled()||player.isOp()){
            return;
        }

        if(PlayerToChatMessage.get(player)!=null){
            event.setCancelled(true);
            player.sendMessage(NotSpeakMessage);
        }else {
            PlayerToChatMessage.put(player,event.getMessage());
            if(IsFoliaSever){
                player.getScheduler().runDelayed(main, new Consumer<ScheduledTask>() {
                    @Override
                    public void accept(ScheduledTask scheduledTask) {
                        PlayerToChatMessage.remove(player);
                    }
                }, () -> main.getLogger().info("Error,Player is null"),(long) (20*Time));
            }else {
                new BukkitRunnable(){
                    @Override
                    public void run() {
                        PlayerToChatMessage.remove(player);
                    }
                }.runTaskLater(WorldListTrashCan.main, (long) (20*Time));
            }
        }
    }


}
