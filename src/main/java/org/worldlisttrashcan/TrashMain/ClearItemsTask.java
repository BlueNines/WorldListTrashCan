package org.worldlisttrashcan.TrashMain;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.worldlisttrashcan.data;
import org.worldlisttrashcan.message;

import java.util.*;

import static org.worldlisttrashcan.AutoTrashMain.AutoTrashListener.*;
import static org.worldlisttrashcan.TrashMain.GlobalTrashGui.ClearContainer;
import static org.worldlisttrashcan.TrashMain.TrashListener.GlobalItemSetString;
import static org.worldlisttrashcan.WorldListTrashCan.*;
import static org.worldlisttrashcan.message.color;
//import static org.worldlisttrashcan.TrashMain.getInventory.getState;

public class ClearItemsTask {
    BukkitRunnable bukkitRunnable;
    List<World> WorldList = new ArrayList<>();


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



    public ClearItemsTask() {




        boolean ChatFlag = main.getConfig().getBoolean("Set.ChatFlag");
        boolean TitleFlag = main.getConfig().getBoolean("Set.TitleFlag");
        boolean ActionBarFlag = main.getConfig().getBoolean("Set.ActionBarFlag");


        Map<Integer,String> ChatIntToMessage = new HashMap<>();
        for (String message : main.getConfig().getStringList("Set.ChatMessageForCount")) {
            String[] strings= message.split(";");
            ChatIntToMessage.put(Integer.parseInt(strings[0]),color(strings[1]));
        }

        Map<Integer,String> ActionBarIntToMessage = new HashMap<>();
        for (String message : main.getConfig().getStringList("Set.ActionBarMessageForCount")) {
            String[] strings= message.split(";");
//            ActionBarIntToMessage.put(Integer.parseInt(strings[0]),color(strings[1]));
            ActionBarIntToMessage.put(Integer.parseInt(strings[0]),strings[1]);
        }

//        //test
//        for (Integer i : ActionBarIntToMessage.keySet()) {
//            Bukkit.broadcastMessage("ActionBarIntToMessage.get(i) "+ActionBarIntToMessage.get(i));
//        }



        Map<Integer,String> TitleIntToMessage = new HashMap<>();
        for (String message : main.getConfig().getStringList("Set.TitleMessageForCount")) {
            String[] strings= message.split(";");
            if(strings.length>2){
                TitleIntToMessage.put(Integer.parseInt(strings[0]),color(strings[1])+";"+color(strings[2]));
            }else {
                TitleIntToMessage.put(Integer.parseInt(strings[0]),color(strings[1]));

            }

        }

        boolean ClearMonster = main.getConfig().getBoolean("Set.ClearEntity.ClearMonster");
        boolean ClearAnimals = main.getConfig().getBoolean("Set.ClearEntity.ClearAnimals");
        boolean ClearReNameEntity = main.getConfig().getBoolean("Set.ClearEntity.ClearReNameEntity");

        List<String> WhiteNameList = main.getConfig().getStringList("Set.ClearEntity.WhiteNameList");
        List<String> BlackNameList = main.getConfig().getStringList("Set.ClearEntity.BlackNameList");



        boolean ClearEntityFlag = main.getConfig().getBoolean("Set.ClearEntity.Flag");




        int EveryClearGlobalTrash = main.getConfig().getInt("Set.GlobalTrash.EveryClearGlobalTrash");


        bukkitRunnable = new BukkitRunnable() {
            final int finalCount = main.getConfig().getInt("Set.SecondCount");
            int count = finalCount;
//            List<Integer> integerList = main.getConfig().getIntegerList("Set.MessageCount");
//            Map<Integer,String> intToMessage = new HashMap<>();




//            final String message = main.getConfig().getString("Set.Message");


            int GlobalTrashItemSum = 0, EntitySum = 0;
            int ClearCount = 0;

            public void PrintCountMessage(int count) {
                if (ChatIntToMessage.containsKey(count) && ChatFlag) {
//                    if(ChatFlag){
                    Bukkit.broadcastMessage(ChatIntToMessage.get(count).replace("%ItemSum%", GlobalTrashItemSum + "").replace("%EntitySum%", EntitySum + "").replace("%ClearGlobalCount%", EveryClearGlobalTrash - ClearCount + ""));
                }

                if (ActionBarIntToMessage.containsKey(count) && ActionBarFlag) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        Player player1 = (Player) player;

//                        sendMessageAbstract.sendActionBar(player, color(ActionBarIntToMessage.get(count)
//                                .replace("%ItemSum%", GlobalTrashItemSum + "")
//                                .replace("%EntitySum%", EntitySum + "")
//                                .replace("%ClearGlobalCount%", EveryClearGlobalTrash - ClearCount + ""),true));

                        sendMessageAbstract.sendActionBar(player,ActionBarIntToMessage.get(count)
                                .replace("%ItemSum%", GlobalTrashItemSum + "")
                                .replace("%EntitySum%", EntitySum + "")
                                .replace("%ClearGlobalCount%", EveryClearGlobalTrash - ClearCount + ""));

//                        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(
//                                ActionBarIntToMessage.get(count)
//                                        .replace("%ItemSum%", GlobalTrashItemSum + "")
//                                        .replace("%EntitySum%", EntitySum + "")
//                                        .replace("%ClearGlobalCount%", EveryClearGlobalTrash - ClearCount + "")));

                    }
                }
                if (TitleIntToMessage.containsKey(count) && TitleFlag) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        if (TitleIntToMessage.get(count).contains(";")) {
                            String[] strings = TitleIntToMessage.get(count).split(";");
                            player.sendTitle(strings[0].replace("%ItemSum%", GlobalTrashItemSum + "").replace("%EntitySum%", EntitySum + "").replace("%ClearGlobalCount%", EveryClearGlobalTrash - ClearCount + ""),
                                    strings[1].replace("%ItemSum%", GlobalTrashItemSum + "").replace("%EntitySum%", EntitySum + "").replace("%ClearGlobalCount%", EveryClearGlobalTrash - ClearCount + ""), 10, 70, 20);



                        } else {


                            player.sendTitle(TitleIntToMessage.get(count).replace("%ItemSum%", GlobalTrashItemSum + "").replace("%EntitySum%", EntitySum + "").replace("%ClearGlobalCount%", EveryClearGlobalTrash - ClearCount + ""), "", 10, 70, 20);
                        }

                    }

                }


            }
            @Override
            public void run() {


                if (count == 0) {

                    WorldList.clear();
                    WorldList = Bukkit.getWorlds();

                    GlobalTrashItemSum = 0;EntitySum = 0;
                    ClearCount++;
                    if (ClearCount==EveryClearGlobalTrash) {

                        //清理公共垃圾桶
                        ClearCount=0;
                        ClearContainer(GlobalTrashList);
                        new BukkitRunnable(){
                            @Override
                            public void run() {
                                PrintCountMessage(-2);
                            }
                        }.runTaskLater(main,60L);


                    }else {
                        new BukkitRunnable(){
                            @Override
                            public void run() {
                                PrintCountMessage(-1);
                            }
                        }.runTaskLater(main,60L);
                    }


                    for (World world : WorldList) {
                        Set<Location> locationSet = null;
//                        List<Inventory> inventoryList = new ArrayList<>();

                        if (WorldToLocation.get(world) != null) {
                            locationSet = WorldToLocation.get(world).getLocationSet();
                        }

//                        if (locationSet != null && !locationSet.isEmpty()) {
//                            if (main.getConfig().getBoolean("Set.Debug")) {
//                                for (Location location : locationSet) {
//                                    main.getLogger().info(ChatColor.BLUE + "location为: " + location.toString());
//                                }
//                            }
//                        }
//                        if (main.getConfig().getBoolean("Set.Debug")) {
//                            for (Inventory inventory : inventoryList) {
//                                System.out.println("inventory: " + inventory.getLocation().toString());
//                            }
//                        }
//                        Iterator<Inventory> iterator = inventorySet.iterator();

//                        List<Inventory> iteratorList = new ArrayList<>();
//                        iteratorSet = inventorySet;


                        for (Entity entity : world.getEntities()) {


                            if (entity instanceof Item) {
                                Item item = (Item) entity;

//                                if (main.getConfig().getBoolean("Set.Debug")) {
//                                    main.getLogger().info(ChatColor.BLUE + " " + world.getName() + "世界 清理了 :" + item.getItemStack().getType() + "x" + item.getItemStack().getAmount() + "个");
//                                }


                                ItemStack itemStack = item.getItemStack();


                                // 不处理带有关键lore的物品
                                if (NoClearItemFlag(itemStack)) {
                                    continue;
                                }


                                String itemStackTypeString = itemStack.getType().toString();
                                // 这个flag指的是这个物品是否被处理过

                                boolean flag = true;
//                                System.out.println("--1");
                                if (locationSet != null && !locationSet.isEmpty()) {
//                                    System.out.println("-1");


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

//                                    }
                                }


                                // 如果物品带有玩家uuid，则是玩家主动丢弃的物品，优先进入玩家个人垃圾桶
                                // 如果物品没有被处理过且开启了 NoWorldTrashCanEnterPersonalTrashCan
                                if(flag && VersionFlag){

                                    ItemMeta meta = itemStack.getItemMeta();
                                    NamespacedKey namespacedKey = new NamespacedKey(main,"PlayerUUID");
                                    String PlayerUUID = meta.getPersistentDataContainer().get(namespacedKey, PersistentDataType.STRING);
                                    System.out.println("meta.getPersistentDataContainer().getKeys().toString() "+meta.getPersistentDataContainer().getKeys().toString());

                                    if (PlayerUUID != null) {

                                        Player player = Bukkit.getPlayer(UUID.fromString(PlayerUUID));
                                        // 如果丢弃的所属玩家在线
                                        if (player !=null){

                                            Inventory inventory = PlayerToInventory.get(player);
                                            if(inventory==null){

                                                PlayerToInventory.put(player, InitPlayerInv(player));
                                            }else {
                                                if (inventory.addItem(itemStack).isEmpty()) {
                                                    //加进去了
                                                    flag = false;
                                                }else {
                                                    //加不进去就清空个人垃圾桶
                                                    if(main.getConfig().getBoolean("Set.PersonalTrashCan.OriginalFeatureClearItemAddGlobalTrash.Model2.AutoClear")){
                                                        inventory.clear();
                                                        player.sendMessage(message.find("PlayerTrashCanFilled"));
                                                        if (inventory.addItem(itemStack).isEmpty()) {
                                                            //加进去了
                                                            flag = false;
                                                        }
                                                    }

                                                }
                                            }
                                        }
                                    }
                                }


                                //如果物品不在全局世界垃圾桶ban表里
                                if (GlobalTrashGuiFlag && !GlobalItemSetString.contains(itemStackTypeString)) {
                                    //如果全球垃圾桶开启，且该物品没有被普通的世界垃圾桶回收过
                                    if (flag) {
//                                        System.out.println("2");
                                        for (Inventory inventory : GlobalTrashList) {
//                                            System.out.println("3");
                                            if (inventory.addItem(itemStack).isEmpty()) {
                                                //加进去到公共垃圾桶了
                                                GlobalTrashItemSum++;


//                                                System.out.println("4");
                                                break;
                                            }
                                        }
                                    }
                                }

                                item.remove();
//                                blockState.update();
                            } else {
                                if (ClearEntityFlag) {
//                                    if(entity.getType().toString().equals("FLAMMPFEIL.SLASHBLADE_BLADESTAND")){
//                                        entity.remove();
//                                    }

//                                    System.out.println("实体: "+entity.getType().toString());

                                    if (BlackNameList.contains(entity.getType().toString())||
                                            BlackNameList.contains(entity.getName())
                                    ) {
//                                        System.out.println("黑名单: "+entity.getType().toString());
                                        entity.remove();
                                        EntitySum++;
                                        continue;
                                    }

                                    if (WhiteNameList.contains(entity.getType().toString())||
                                            WhiteNameList.contains(entity.getName())
                                    ) {
//                                        System.out.println("白名单: "+entity.getType().toString());
                                        continue;
                                    }


                                    if(entity==null){
                                        continue;
                                    }


                                    //如果生物被命名过
//                                    if (!ClearReNameEntity&&(entity!=null&&entity.getCustomName()!=null&&!entity.getCustomName().isEmpty())) {
                                    if (!ClearReNameEntity&&(
                                            entity!=null&&
                                                    entity.getCustomName()!=null
                                                    &&
                                                    !entity.getCustomName().isEmpty())
                                    ) {

                                        continue;
                                    }

//                                    Component customName = entity.customName();
//                                    boolean hasCustomName = customName != null && !customName.toString().isEmpty();
//                                    if (!ClearReNameEntity && hasCustomName) {
//                                        continue;
//                                    }

                                    if (entity instanceof org.bukkit.entity.Animals) {
                                        if (ClearAnimals) {
                                            entity.remove();
//                                            System.out.println("ClearAnimals: "+entity.getType().toString());
                                            EntitySum++;
                                            continue;
                                        }
                                    } else if (entity instanceof org.bukkit.entity.Monster) {
                                        if (ClearMonster) {
                                            entity.remove();
//                                            System.out.println("ClearMonster: "+entity.getType().toString());
                                            EntitySum++;
                                            continue;
                                        }
                                    }


//                                    if (BlackNameList.contains(entity.getType().toString())) {
////                                        System.out.println("黑名单: "+entity.getType().toString());
//                                        entity.remove();
//                                        EntitySum++;
//                                    }


                                }

                            }
                        }
                    }

//                    System.out.println("2");

//                    new BukkitRunnable(){
//                        @Override
//                        public void run() {
                            PrintCountMessage(count);
//                        }
//                    }.runTaskLater(main,20L);


                    count = finalCount;

                }

//                System.out.println("count="+count);
                //
                PrintCountMessage(count);

                count--;

            }
        };


    }
    public void Start(){
        bukkitRunnable.runTaskTimer(main,20L,20L);
    }
    public void Stop(){
        bukkitRunnable.cancel();
    }






}
