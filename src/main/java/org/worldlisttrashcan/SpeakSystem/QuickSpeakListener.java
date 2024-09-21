package org.worldlisttrashcan.SpeakSystem;



import io.papermc.paper.event.player.AsyncChatEvent;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.worldlisttrashcan.WorldListTrashCan;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import static org.worldlisttrashcan.IsVersion.IsFoliaServer;
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
    //只有低版本bukkit有
//    public void PlayerOnSpeak(PlayerChatEvent event){
    //中高版本有
    public void PlayerOnSpeak(AsyncPlayerChatEvent event){
        //未知,可能是paper独有
//    public void PlayerOnSpeak(AsyncChatEvent event){

//        System.out.println("1");
        Player player =event.getPlayer();
        if(event.isCancelled()||player.isOp()){

//            System.out.println("12");
            return;
        }

        if(PlayerToChatMessage.get(player)!=null){

//            System.out.println("13");
            event.setCancelled(true);
            player.sendMessage(NotSpeakMessage);
        }else {
//            PlayerToChatMessage.put(player,event.getMessage());
            PlayerToChatMessage.put(player," ");
            if(IsFoliaServer){
                player.getScheduler().runDelayed(main, new Consumer<ScheduledTask>() {
                    @Override
                    public void accept(ScheduledTask scheduledTask) {
                        PlayerToChatMessage.remove(player);
                    }
                }, () -> main.getLogger().info("Error,Player is null"),(long) (20*Time));
            }else {

//                System.out.println("14");
                new BukkitRunnable(){
                    @Override
                    public void run() {

//                        System.out.println("15");
                        PlayerToChatMessage.remove(player);
                    }
                }.runTaskLater(WorldListTrashCan.main, (long) (20*Time));
            }
        }
    }


}
