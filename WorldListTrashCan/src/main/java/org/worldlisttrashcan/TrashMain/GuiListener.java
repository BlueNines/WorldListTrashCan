package org.worldlisttrashcan.TrashMain;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.worldlisttrashcan.message;

import static org.worldlisttrashcan.message.*;
import java.util.HashMap;

import static org.worldlisttrashcan.Method.Method.getItemStackAllString;
import static org.worldlisttrashcan.WorldListTrashCan.GlobalTrashList;
import static org.worldlisttrashcan.WorldListTrashCan.main;
import static org.worldlisttrashcan.log.logFlag;
import static org.worldlisttrashcan.log.startLogToFileTask;


public class GuiListener implements Listener {






    //记录玩家点击的时间戳
    public static HashMap<Player, Long> playerClickTimeStamp = new HashMap<>();



    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
//        System.out.println("0");
        // 检查事件是否发生在我们的菜单中

//        if (event.getView().getTitle().equals(mainGui.MainMenuName) || event.getView().getTitle().equals(mainGui.cauldronMenu.MenuName) ||
//                event.getView().getTitle().equals(mainGui.leatherChestplateMenu.MenuName) || event.getView().getTitle().equals(mainGui.netheriteChestplateMenu.MenuName) ||
//                event.getView().getTitle().equals(mainGui.oakSaplingMenu.MenuName) || event.getView().getTitle().equals(mainGui.tippedArrowMenu.MenuName)
//        ) {
        Player player = (Player) event.getWhoClicked();
        Inventory inventory = event.getInventory();


//        System.out.println("-----------");
//        System.out.println("event.getHotbarButton()"+event.getHotbarButton());
//        System.out.println("event.getRawSlot()"+event.getRawSlot());
//        System.out.println("event.getRawSlot()"+event.getRawSlot());
//        System.out.println("-----------");

        String playerName = player.getName();



        int PageIndex = GlobalTrashList.indexOf(inventory);
        if(PageIndex!=-1){

            //无论如何都不允许玩家直接拖动，所以直接取消
            event.setCancelled(true);

            int count = event.getRawSlot();
            if(count>=45 && count<=53){
                //上一页
                if(count==46 && PageIndex>0){
//                if(count==46&&inventory.getItem(count).getType()==Material.ARROW){
                    player.openInventory(GlobalTrashList.get(PageIndex-1));
                //下一页
                }else if(count==52 && PageIndex<GlobalTrashList.size()-1){
//                }else if(count==52&&inventory.getItem(count).getType()==Material.ARROW){
                    player.openInventory(GlobalTrashList.get(PageIndex+1));
                }
            }else if (count>=0 && count<=44) {

                // 如果玩家没有使用权限
                if (!player.isOp()&&!player.hasPermission("WorldListTrashCan.GlobalTrashTakeItem")) {
//                    System.out.println("layer.hasPermission(\"WorldListTrashCan.GlobalTrashTakeItem\"))");
                    return;
                }


                ItemStack itemStack = inventory.getItem(count);
                if (itemStack != null && itemStack.getType() != Material.AIR) {
                    //记录原数量
                    int amount = itemStack.getAmount();
                    //如果只加了一半，也返回false



                    Long time = playerClickTimeStamp.getOrDefault(player,0L);
//                    if(time==null){
//                        time = 0L;
//                        playerClickTimeStamp.put(player, System.currentTimeMillis());
//                    }
                    long timeConfig = main.getConfig().getLong("Set.GlobalTrash.Delay");
                    //如果获取到的time加上timeConfig秒，大于当前时间戳
                    if (time+timeConfig>=System.currentTimeMillis()){
                        long shengyu = (timeConfig - (System.currentTimeMillis() - time))/100L;
                        String timeStr = String.valueOf(shengyu);
//                        timeStr = timeStr.equals("0") ?"0" : timeStr.substring(0, timeStr.length() - 1) + "." + timeStr.substring(timeStr.length() - 1);
                        timeStr = shengyu<10 ?"0."+timeStr : timeStr.substring(0, timeStr.length() - 1) + "." + timeStr.substring(timeStr.length() - 1);
//                        System.out.println("当前时间戳: " + System.currentTimeMillis());
//                        System.out.println("Redis 时间戳: " + time);
//                        System.out.println("当前时间戳减Redis时间戳 " + (System.currentTimeMillis() - time));
//                        System.out.println("冷却时间配置: " + timeConfig*1000L);
//                        System.out.println("剩余冷却时间: " + shengyu);
                        message.consoleSay(player, message.find("GlobalTrashPickupDelay").replace("%time%",timeStr));
                        return;
                    }

                    HashMap<Integer, ItemStack> integerItemStackHashMap = player.getInventory().addItem(itemStack);

                    playerClickTimeStamp.put(player, System.currentTimeMillis());


                    if (integerItemStackHashMap.isEmpty()) {
                        String finalItem = getItemStackAllString(itemStack,amount);
                        itemStack.setAmount(0);
                        if(logFlag) {
                            startLogToFileTask(playerName, "-global", finalItem);
                        }
                    }else {
                        //没加进去的物品
                        ItemStack itemStack1 = integerItemStackHashMap.get(0);
                        //剩余物品数量
                        int shengyu = itemStack1.getAmount();
                        if (amount-shengyu>0){
                            String finalItem = getItemStackAllString(itemStack,amount-shengyu);

                            if(logFlag){
                                startLogToFileTask(playerName, "-global", finalItem);
                            }
                        }
                    }
                }

            }else if (count>=54 && count<=89 && main.getConfig().getBoolean("Set.GlobalTrash.AllowPutItemInGlobalTrash",true)) {

                // 如果玩家没有放入权限
                if (!player.isOp()&&!player.hasPermission("WorldListTrashCan.GlobalTrashPutItem")) {
                    return;
                }

                ItemStack itemStack = player.getInventory().getItem(event.getSlot());
                if (itemStack != null && itemStack.getType() != Material.AIR) {

                    //记录原数量
                    int amount = itemStack.getAmount();
                    HashMap<Integer, ItemStack> integerItemStackHashMap = inventory.addItem(itemStack);

                    if (integerItemStackHashMap.isEmpty()) {

                        String finalItem = getItemStackAllString(itemStack,amount);
                        itemStack.setAmount(0);
                        if(logFlag) {
                            startLogToFileTask(playerName, "+global", finalItem);
                        }
                    }else {
                        //没加进去的物品
                        ItemStack itemStack1 = integerItemStackHashMap.get(0);
                        //剩余物品数量
                        int shengyu = itemStack1.getAmount();
                        if (amount-shengyu>0){
                            String finalItem = getItemStackAllString(itemStack,amount-shengyu);
                            if(logFlag){
                                startLogToFileTask(playerName, "+global", finalItem);
                            }
                        }
                    }
                }

            }
            // 取消玩家点击事件


        }

    }
}
