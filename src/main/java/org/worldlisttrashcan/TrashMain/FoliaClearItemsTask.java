package org.worldlisttrashcan.TrashMain;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.worldlisttrashcan.data;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static org.worldlisttrashcan.IsVersion.IsFoliaSever;
import static org.worldlisttrashcan.TrashMain.GlobalTrashGui.ClearContainer;
import static org.worldlisttrashcan.TrashMain.TrashListener.GlobalItemSetString;
import static org.worldlisttrashcan.WorldListTrashCan.*;

public class FoliaClearItemsTask {
    List<World> WorldList = new ArrayList<>();

//    ConcurrentMap<Entity,Object> ChunkMap = new ConcurrentHashMap<>();

    public boolean NoClearItemFlag(ItemStack itemStack){
        for (String type : NoClearContainerType) {
            if(itemStack.getType().toString().equals(type)){
                return true;
            }
        }
//        ItemStack itemStack = item.getItemStack();
        ItemMeta itemMeta = itemStack.getItemMeta();
//        boolean flag = false;
//        System.out.println("1");
        if (itemMeta != null && itemMeta.getLore() != null) {
            List<String> strings = itemMeta.getLore();
            for (String lore : NoClearContainerLore) {
//                System.out.println("lore "+lore);
//                System.out.println("lores "+itemMeta.getLore());
                for (String string : strings) {
                    if(string.contains(lore)){
//                    flag = true;
//                    break;
//                        System.out.println("12");
                        return true;
                    }
                }

            }
        }
        return false;
    }



    public FoliaClearItemsTask() {
        boolean ChatFlag = main.getConfig().getBoolean("Set.ChatFlag");
        boolean TitleFlag = main.getConfig().getBoolean("Set.TitleFlag");
        boolean ActionBarFlag = main.getConfig().getBoolean("Set.ActionBarFlag");


        Map<Integer,String> ChatIntToMessage = new HashMap<>();
        for (String message : main.getConfig().getStringList("Set.ChatMessageForCount")) {
            String[] strings= message.split(";");
            ChatIntToMessage.put(Integer.parseInt(strings[0]),strings[1].replace("&","§"));
        }

        Map<Integer,String> ActionBarIntToMessage = new HashMap<>();
        for (String message : main.getConfig().getStringList("Set.ActionBarMessageForCount")) {
            String[] strings= message.split(";");
            ActionBarIntToMessage.put(Integer.parseInt(strings[0]),strings[1].replace("&","§"));
        }

        Map<Integer,String> TitleIntToMessage = new HashMap<>();
        for (String message : main.getConfig().getStringList("Set.TitleMessageForCount")) {
            String[] strings= message.split(";");
            if(strings.length>2){
                TitleIntToMessage.put(Integer.parseInt(strings[0]),strings[1].replace("&","§")+";"+strings[2].replace("&","§"));
            }else {
                TitleIntToMessage.put(Integer.parseInt(strings[0]),strings[1].replace("&","§"));

            }

        }

        boolean ClearMonster = main.getConfig().getBoolean("Set.ClearEntity.ClearMonster");
        boolean ClearAnimals = main.getConfig().getBoolean("Set.ClearEntity.ClearAnimals");
        boolean ClearReNameEntity = main.getConfig().getBoolean("Set.ClearEntity.ClearReNameEntity");

        List<String> WhiteNameList = main.getConfig().getStringList("Set.ClearEntity.WhiteNameList");
        List<String> BlackNameList = main.getConfig().getStringList("Set.ClearEntity.BlackNameList");



        boolean ClearEntityFlag = main.getConfig().getBoolean("Set.ClearEntity.Flag");




        int EveryClearGlobalTrash = main.getConfig().getInt("Set.GlobalTrash.EveryClearGlobalTrash");


        foliaRunnable = new java.util.function.Consumer<ScheduledTask>() {
            final int finalCount = main.getConfig().getInt("Set.SecondCount");
            int count = finalCount;
            int GlobalTrashItemSum = 0, EntitySum = 0;
            int ClearCount = 0;




            public void PrintCountMessage(int count){
                if (ChatIntToMessage.containsKey(count)) {
                    if(ChatFlag){
//                        Bukkit.broadcastMessage(ChatIntToMessage.get(count).replace("%ItemSum%",GlobalTrashItemSum+"").replace("%EntitySum%",EntitySum+"").replace("%ClearGlobalCount%",EveryClearGlobalTrash-ClearCount+""));
                        for (Player player : Bukkit.getOnlinePlayers()) {
                            player.sendMessage(ChatIntToMessage.get(count).replace("%ItemSum%",GlobalTrashItemSum+"").replace("%EntitySum%",EntitySum+"").replace("%ClearGlobalCount%",EveryClearGlobalTrash-ClearCount+""));
                            main.getLogger().info(ChatIntToMessage.get(count).replace("%ItemSum%",GlobalTrashItemSum+"").replace("%EntitySum%",EntitySum+"").replace("%ClearGlobalCount%",EveryClearGlobalTrash-ClearCount+""));


                        }
                    }
                    if (ActionBarFlag){
                        for (Player player : Bukkit.getOnlinePlayers()) {
//                        Player player1 = (Player) player;
                            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ActionBarIntToMessage.get(count).replace("%ItemSum%",GlobalTrashItemSum+"").replace("%EntitySum%",EntitySum+"").replace("%ClearGlobalCount%",EveryClearGlobalTrash-ClearCount+"")));

                        }
                    }
                    if(TitleFlag){
                        for (Player player : Bukkit.getOnlinePlayers()) {
                            if(TitleIntToMessage.get(count).contains(";")){
                                String[] strings = TitleIntToMessage.get(count).split(";");
                                player.sendTitle(strings[0].replace("%ItemSum%",GlobalTrashItemSum+"").replace("%EntitySum%",EntitySum+"").replace("%ClearGlobalCount%",EveryClearGlobalTrash-ClearCount+""),
                                        strings[1].replace("%ItemSum%",GlobalTrashItemSum+"").replace("%EntitySum%",EntitySum+"").replace("%ClearGlobalCount%",EveryClearGlobalTrash-ClearCount+""), 10, 70, 20);
                            }else {
                                player.sendTitle(TitleIntToMessage.get(count).replace("%ItemSum%",GlobalTrashItemSum+"").replace("%EntitySum%",EntitySum+"").replace("%ClearGlobalCount%",EveryClearGlobalTrash-ClearCount+""), "", 10, 70, 20);
                            }

                        }
                    }

                }
            }

            //任务总数taskSum
//            int taskSum = 0;
            //当前执行完毕的任务总数nowSum
//            int nowSum = 0;

            Map<Integer,Integer> NowChatIntToInt = new HashMap<>();

            @Override
            public void accept(ScheduledTask scheduledTask) {
                if (count == 0) {
                    WorldList.clear();
                    WorldList = Bukkit.getWorlds();

                    GlobalTrashItemSum = 0;EntitySum = 0;
                    ClearCount++;
                    if (ClearCount==EveryClearGlobalTrash) {

                        //清理公共垃圾桶
                        ClearCount=0;
                        ClearContainer(GlobalTrashList);

//                        Bukkit.getAsyncScheduler().runDelayed(main, new Consumer<ScheduledTask>() {
//                            @Override
//                            public void accept(ScheduledTask scheduledTask) {
//                                PrintCountMessage(-2);
//                            }
//                        },2, TimeUnit.SECONDS);
                        NowChatIntToInt.put(finalCount-3,-2);



                    }else {
//                        Bukkit.getAsyncScheduler().runDelayed(main, new Consumer<ScheduledTask>() {
//                            @Override
//                            public void accept(ScheduledTask scheduledTask) {
//                                PrintCountMessage(-1);
//                            }
//                        },2, TimeUnit.SECONDS);
                        NowChatIntToInt.put(finalCount-3,-1);
                    }


                    for (World world : WorldList) {

                        for (Chunk chunk : world.getLoadedChunks()) {




                            Bukkit.getRegionScheduler().run(main,new Location(chunk.getWorld(), chunk.getX() * 16, 0, chunk.getZ() * 16),new Consumer<ScheduledTask>() {
                                @Override
                                public void accept(ScheduledTask scheduledTask) {
                                    Set<Location> locationSet = null;
                                    if (WorldToLocation.get(world) != null) {
                                        locationSet = WorldToLocation.get(world).getLocationSet();
                                    }
                                    for (Entity entity : chunk.getEntities()) {
//                                        ChunkMap.put(entity,null);

                                        if (entity instanceof Item) {
                                            Item item = (Item) entity;
                                            ItemStack itemStack = item.getItemStack();
                                            if (NoClearItemFlag(itemStack)) {
                                                continue;
                                            }
                                            String itemStackTypeString = itemStack.getType().toString();
                                            boolean flag = true;
                                            if (locationSet != null && !locationSet.isEmpty()) {
                                                //如果物品不在全局世界垃圾桶ban表里
//                                    if(!GlobalItemSetString.contains(itemStackTypeString)) {
                                                //如果物品不在世界垃圾桶ban表里
                                                if (!WorldToLocation.get(world).getBanItemSet().contains(itemStackTypeString)) {
                                                    //这里写个for，如果有成功加入则break即可
//                                        iteratorList = inventoryList;
//                                        if (!inventoryList.isEmpty()) {

//                                        if (main.getConfig().getBoolean("Set.Debug")) {
//                                            main.getLogger().info(ChatColor.BLUE + "还剩 " + inventoryList.size() + " 个箱子");
//                                        }

//                                            System.out.println("0");
                                                    for (Location location : locationSet) {
                                                        Inventory inventory = getInventory.getInventory(location.getBlock());
                                                        if (inventory != null) {
                                                            if (inventory.addItem(itemStack).isEmpty()) {
//                                                    System.out.println("加进去了");
                                                                flag = false;
                                                                break;
                                                            }
//                                                else {
//                                                    inventoryList.remove(0);
//                                                    System.out.println("没加进去");
//                                                    locationSet.remove(location);
//                                                }
                                                        }
                                                        //无法获取到箱子对象的处理
                                                        else {
                                                            Set<Location> locationSet1 = WorldToLocation.get(world).getLocationSet();
                                                            locationSet1.remove(location);
                                                            data.dataPut(world, locationSet1);
//                                                    main.getLogger().info(ChatColor.RED + "由于没有找到箱子，自动从存储中移除了该" + location.toString() + "位置");
                                                            String locationString = world.getName() + ": " + location.getBlockX() + "," + location.getBlockY() + "," + location.getBlockZ();
                                                            main.getLogger().info(org.worldlisttrashcan.message.find("NotFindChest").replace("%location%", locationString));
                                                        }
                                                    }
                                                }
                                            }
                                            //如果物品不在全局世界垃圾桶ban表里
                                            if (GlobalTrashGuiFlag && !GlobalItemSetString.contains(itemStackTypeString)) {
                                                //如果全球垃圾桶开启，且该物品没有被普通的世界垃圾桶回收过
                                                if (flag) {
                                                    for (Inventory inventory : GlobalTrashList) {
                                                        if (inventory.addItem(itemStack).isEmpty()) {
                                                            //加进去到公共垃圾桶了
                                                            GlobalTrashItemSum++;


                                                            break;
                                                        }
                                                    }
                                                }
                                            }
                                            item.remove();


//                                blockState.update();
                                        } else {
                                            if (ClearEntityFlag) {

                                                if (BlackNameList.contains(entity.getType().toString())) {
                                                    entity.remove();
                                                    EntitySum++;
                                                    continue;
                                                }

                                                if (WhiteNameList.contains(entity.getType().toString())) {

                                                    continue;
                                                }

                                                //如果生物被命名过
                                                if (!ClearReNameEntity&&(entity.getCustomName()!=null&&!entity.getCustomName().isEmpty())) {
                                                    continue;
                                                }


                                                if (entity instanceof org.bukkit.entity.Animals) {
                                                    if (ClearAnimals) {
                                                        entity.remove();
                                                        EntitySum++;

                                                        continue;
                                                    }
                                                } else if (entity instanceof org.bukkit.entity.Monster) {
                                                    if (ClearMonster) {
                                                        entity.remove();
                                                        EntitySum++;

                                                        continue;
                                                    }
                                                }


//                                                if (BlackNameList.contains(entity.getType().toString())) {
//                                                    entity.remove();
//                                                    EntitySum++;
//                                                    continue;
//                                                }


                                            }

                                        }
                                    }

                                }


                            });
                        }

                    }


                    //在这一步，直接输出会输出0哥实体和清理的垃圾，因为是瞬时的，而我们的清理是异步的
//                    PrintCountMessage(count);

                    count = finalCount;



//                        PrintCountMessage(count);
                    NowChatIntToInt.put(finalCount-1,0);




                }

                if(!NowChatIntToInt.isEmpty()){
                    boolean Flag = false;
                    for (int i : NowChatIntToInt.keySet()) {
                        if (i==count) {
                            PrintCountMessage(NowChatIntToInt.get(i));
                            Flag = true;
                        }
                    }
                    if(Flag){
                        NowChatIntToInt.remove(count);
                    }
                }


                PrintCountMessage(count);

//                System.out.println("count : "+count);
                count--;

            }
        };


    }
    ScheduledTask foliaTask;
    public void Start(){
        foliaTask = Bukkit.getAsyncScheduler().runAtFixedRate(main,foliaRunnable,1,1, TimeUnit.SECONDS);
    }

    public void Stop(){
        foliaTask.cancel();
    }
    java.util.function.Consumer<ScheduledTask> foliaRunnable;
}
