package org.worldlisttrashcan.system.speak;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.worldlisttrashcan.WorldListTrashCan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.worldlisttrashcan.utils.Method.useCommand;

public class QuickUseCommandListener implements Listener {
//    public static Map<Player,String> PlayerToCommand = new HashMap<>();
    public static Map<Player,Long> PlayerToCommand = new HashMap<>();
    String NotUseCommandMessage = "不要频繁用指令";
    String NotUseCommandCommand = "";

    double Time = 2;
    List<String> WhiteCommands = new ArrayList<>();
//ChatSet:
//  QuickSendMessage:
//    Flag: true
//    #两秒允许发送一次聊天消息
//    Time: 2
//    #提醒语句
//    Message: "&c请不要刷屏"
//  QuickUseCommand:
//    Flag: true
//    #两秒允许使用一次指令
//    Time: 2
//    #提醒语句
//    Message: "&c请不要频繁使用指令"
//    #指令白名单，如果在指令以下名单中，不受限制
//    WhiteList:
//      - tpa
//      - spawn
//      - suicide
    public void Init(){
        NotUseCommandMessage = WorldListTrashCan.main.getConfig().getString("ChatSet.QuickUseCommand.Message");
        NotUseCommandCommand = WorldListTrashCan.main.getConfig().getString("ChatSet.QuickUseCommand.Command");
        Time = WorldListTrashCan.main.getConfig().getDouble("ChatSet.QuickUseCommand.Time");
        WhiteCommands = WorldListTrashCan.main.getConfig().getStringList("ChatSet.QuickUseCommand.WhiteList");
        PlayerToCommand.clear();
    }

    @EventHandler
    public void PlayerUseCommand(PlayerCommandPreprocessEvent event){

        //如果这里还是会出现问题，则改为记录时间戳的形式，不采用延迟删除的形式了


        Player player =event.getPlayer();
        if(event.isCancelled()||player.isOp()){
            return;
        }
        String Command = event.getMessage();
        if(WhiteCommands.contains(Command)){
            return;
        }

        if(event.isCancelled()||player.isOp()){
            return;
        }

        if(PlayerToCommand.get(player)!=null){

            //计算时间戳与当前时间戳的间隔（单位秒）
            long interval = (System.currentTimeMillis() - PlayerToCommand.get(player)) / 1000;
            //如果时间间隔小于限制时间
            if (interval < Time) {
                event.setCancelled(true);
                player.sendMessage(NotUseCommandMessage);


                useCommand(player,NotUseCommandCommand);

            }else {
                PlayerToCommand.put(player,System.currentTimeMillis());
            }
        }else {
            //记录玩家当前聊天的时间戳
            PlayerToCommand.put(player,System.currentTimeMillis());
        }




//        if(PlayerToCommand.get(player)!=null){
////            System.out.println(PlayerToCommand);
//
//            event.setCancelled(true);
//            player.sendMessage(NotUseCommandMessage);
//        }else {
//            PlayerToCommand.put(player,Command);
//            if(IsFoliaServer){
//                player.getScheduler().runDelayed(main, new Consumer<ScheduledTask>() {
//                    @Override
//                    public void accept(ScheduledTask scheduledTask) {
//                        PlayerToCommand.remove(player);
////                        System.out.println("!");
//                    }
//                }, () -> consoleSay("Error,Player is null"),(long) (20*Time));
//            }else {
//                new BukkitRunnable(){
//                    @Override
//                    public void run() {
//                        PlayerToCommand.remove(player);
//                    }
//                }.runTaskLater(WorldListTrashCan.main, (long) (20*Time));
//            }
//
//        }
    }
}
