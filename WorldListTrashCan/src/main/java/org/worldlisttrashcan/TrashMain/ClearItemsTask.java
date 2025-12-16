package org.worldlisttrashcan.TrashMain;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.worldlisttrashcan.data;
import org.worldlisttrashcan.message;

import java.util.*;

import static org.worldlisttrashcan.AutoTrashMain.AutoTrashListener.*;
import static org.worldlisttrashcan.AutoTrashMain.HeightVersionPlayerDropItemListener.RemoveItemTag;
import static org.worldlisttrashcan.Method.Method.isMonster;
import static org.worldlisttrashcan.Method.Method.papiReplace;
import static org.worldlisttrashcan.TrashMain.GlobalTrashGui.ClearContainer;
import static org.worldlisttrashcan.TrashMain.TrashListener.GlobalItemSetString;
import static org.worldlisttrashcan.WorldListTrashCan.*;
import static org.worldlisttrashcan.log.customLogToFile;
import static org.worldlisttrashcan.log.logFlag;
import static org.worldlisttrashcan.message.*;
//import static org.worldlisttrashcan.TrashMain.getInventory.getState;

public class ClearItemsTask {
    BukkitRunnable bukkitRunnable;
    List<World> WorldList = new ArrayList<>();
    int finalCount;
    int publicTime = 0;

    public int getPublicTime() {
        return publicTime;
    }
    public BukkitRunnable getBukkitRunnable() {
        return bukkitRunnable;
    }

    public boolean NoClearItemFlag(ItemStack itemStack){

        for (String type : NoClearContainerType) {
            if(itemStack.getType().toString().equals(type)){
                return true;
            }
        }
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta != null && itemMeta.getLore() != null) {
            List<String> strings = itemMeta.getLore();
            for (String lore : NoClearContainerLore) {
                for (String string : strings) {
                    if(string.contains(lore)){
                        return true;
                    }
                }

            }
        }
        if (itemMeta != null && itemMeta.getDisplayName() != null) {
            for (String customName : NoClearContainerName) {
                if (itemMeta.getDisplayName().contains(customName)){
                    return true;
                }
            }
        }
        return false;
    }



    public ClearItemsTask(int amount) {

        finalCount = amount;



        boolean BossBarFlag = main.getConfig().getBoolean("Set.BossBarFlag");
        boolean ChatFlag = main.getConfig().getBoolean("Set.ChatFlag");
        String ChatClickCommand = main.getConfig().getString("Set.ChatClickCommand");
        boolean ChatConsoleLogFlag = main.getConfig().getBoolean("Set.ChatConsoleLogFlag");
        boolean SoundFlag = main.getConfig().getBoolean("Set.SoundFlag");
        boolean TitleFlag = main.getConfig().getBoolean("Set.TitleFlag");
        boolean ActionBarFlag = main.getConfig().getBoolean("Set.ActionBarFlag");
        boolean CommandFlag = main.getConfig().getBoolean("Set.CommandFlag");

        Map<Integer,String> BossBarToMessage = new HashMap<>();
        for (String message : main.getConfig().getStringList("Set.BossBarMessageForCount")) {
            String[] strings= message.split(";");
            BossBarToMessage.put(Integer.parseInt(strings[0]),
                    color(strings[1]+";"+strings[2]+";"+strings[3])
            );
        }

        Map<Integer,String> ChatIntToMessage = new HashMap<>();
        for (String message : main.getConfig().getStringList("Set.ChatMessageForCount")) {
            String[] strings= message.split(";");
//            ChatIntToMessage.put(Integer.parseInt(strings[0]),color(strings[1]));
            ChatIntToMessage.put(Integer.parseInt(strings[0]),strings[1]);
        }

        Map<Integer,String> SoundIntToMessage = new HashMap<>();
        for (String message : main.getConfig().getStringList("Set.SoundForCount")) {
            String[] strings= message.split(";");
            SoundIntToMessage.put(Integer.parseInt(strings[0]),strings[1]);
        }

        Map<Integer,String> ActionBarIntToMessage = new HashMap<>();
        for (String message : main.getConfig().getStringList("Set.ActionBarMessageForCount")) {
            String[] strings= message.split(";");
//            ActionBarIntToMessage.put(Integer.parseInt(strings[0]),color(strings[1]));
            ActionBarIntToMessage.put(Integer.parseInt(strings[0]),strings[1]);
        }

        Map<Integer,List<String>> CommandIntToMessage = new HashMap<>();
        for (String message : main.getConfig().getStringList("Set.CommandForCount")) {
            String[] strings= message.split(";");
//            ActionBarIntToMessage.put(Integer.parseInt(strings[0]),color(strings[1]));
            List<String> stringList = new ArrayList<>();
            for (int i = 1; i < strings.length; i++) {
                if(strings[i].isEmpty()||strings[i].equals(" ")){
                    continue;
                }
                stringList.add(color(strings[i]));
            }
            if (stringList.isEmpty()){
                continue;
            }

            CommandIntToMessage.put(Integer.parseInt(strings[0]),stringList);
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

        boolean ClearExpBottle = main.getConfig().getBoolean("Set.ClearEntity.ClearExpBottle");
        boolean ClearMonster = main.getConfig().getBoolean("Set.ClearEntity.ClearMonster");
        boolean ClearAnimals = main.getConfig().getBoolean("Set.ClearEntity.ClearAnimals");
        boolean ClearProjectile = main.getConfig().getBoolean("Set.ClearEntity.ClearProjectile");
        boolean ClearReNameEntity = main.getConfig().getBoolean("Set.ClearEntity.ClearReNameEntity");
        boolean IgnoreEntitiesInBoat = main.getConfig().getBoolean("Set.ClearEntity.IgnoreEntitiesInBoat");

        List<String> WhiteNameList = main.getConfig().getStringList("Set.ClearEntity.WhiteNameList");
        List<String> BlackNameList = main.getConfig().getStringList("Set.ClearEntity.BlackNameList");

        List<String> WorldClearWhiteList = main.getConfig().getStringList("Set.WorldClearWhiteList");

        //全部转换为小写
        BlackNameList.replaceAll(String::toLowerCase);
        WhiteNameList.replaceAll(String::toLowerCase);


        boolean ClearEntityFlag = main.getConfig().getBoolean("Set.ClearEntity.Flag");

        int bossBarMaxInt;
        bossBarMaxInt = Integer.parseInt(main.getConfig().getStringList("Set.BossBarMessageForCount").get(0).split(";")[0]);



        int EveryClearGlobalTrash = main.getConfig().getInt("Set.GlobalTrash.EveryClearGlobalTrash");


        bukkitRunnable = new BukkitRunnable() {
//            final int finalCount = main.getConfig().getInt("Set.SecondCount");
            int count = finalCount;
//            List<Integer> integerList = main.getConfig().getIntegerList("Set.MessageCount");
//            Map<Integer,String> intToMessage = new HashMap<>();



//            final String message = main.getConfig().getString("Set.Message");


            int GlobalTrashItemSum = 0, DealItemSum=0,EntitySum = 0;
            int ClearCount = 0;

            public void PrintCountMessage(int count) {
                //如果EveryClearGlobalTrash==-1，且count==-1，则不提示
                if (EveryClearGlobalTrash == -1 && count == -1) {
                    return;
                }
                if (BossBarFlag && BossBarToMessage.containsKey(count)) {
                    String string = BossBarToMessage.get(count);
                    String[] strings = string.split(";");
                    String message = (strings[0]
                            .replace("%GlobalTrashAddSum%", GlobalTrashItemSum + "").replace("%DealItemSum%", DealItemSum + "")
                            .replace("%EntitySum%", EntitySum + "")
                            .replace("%ClearGlobalCount%", EveryClearGlobalTrash - ClearCount + ""));
//                    for (OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
//                        if(offlinePlayer==null){
//                            continue;
//                        }
//                        Player player = offlinePlayer.getPlayer();
                        if(bossBar.getPlayers().contains(player)){
                            //如果这个玩家有bossbar了
                        }else {
                            //玩家没有bossbar
                            if (player != null) {
                                bossBar.addPlayer(player);
                            }else {
                            }
                        }
//                        bossBar.setTitle(message);
                        bossBar.setTitle(papiReplace(message,player));
                        bossBar.setColor(BarColor.valueOf(strings[2]));
                        bossBar.setStyle(BarStyle.valueOf(strings[1]));
                        double ct = ((double) count )/bossBarMaxInt;
                        if(ct>0){
                            bossBar.setProgress(ct);
                        }else {
                            bossBar.setProgress(1);
                        }
                    }
                }

                if (ChatFlag && ChatIntToMessage.containsKey(count)) {
//                    Bukkit.broadcastMessage(ChatIntToMessage.get(count).replace("%GlobalTrashAddSum%", GlobalTrashItemSum + "").replace("%DealItemSum%", DealItemSum + "").replace("%EntitySum%", EntitySum + "").replace("%ClearGlobalCount%", EveryClearGlobalTrash - ClearCount + ""));
//                    message.consoleSay(ChatIntToMessage.get(count).replace("%GlobalTrashAddSum%", GlobalTrashItemSum + "").replace("%DealItemSum%", DealItemSum + "").replace("%EntitySum%", EntitySum + "").replace("%ClearGlobalCount%", EveryClearGlobalTrash - ClearCount + ""));

                    for (Player player : Bukkit.getOnlinePlayers()) {
                        String text = ChatIntToMessage.get(count).replace("%GlobalTrashAddSum%", GlobalTrashItemSum + "")
                                .replace("%DealItemSum%", DealItemSum + "").replace("%EntitySum%", EntitySum + "")
                                .replace("%ClearGlobalCount%", EveryClearGlobalTrash - ClearCount + "");
                        text = papiReplace(text,player);

                        //如果是最后一次播报
                        if(count==0&&(ChatClickCommand!=null&&!ChatClickCommand.isEmpty())){
//                            TextComponent message = new TextComponent(components);
//                            message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, ChatClickCommand));
//                            player.spigot().sendMessage(message);
                            sendChatMessageToAction(player,text,ClickEvent.Action.RUN_COMMAND,ChatClickCommand);
                            if(logFlag){
                                customLogToFile(text);
                            }
                        }else {
                            player.sendMessage(color(text));
                        }


                    }
                    if (ChatConsoleLogFlag){
                        message.consoleSay(ChatIntToMessage.get(count).replace("%GlobalTrashAddSum%", GlobalTrashItemSum + "").replace("%DealItemSum%", DealItemSum + "").replace("%EntitySum%", EntitySum + "").replace("%ClearGlobalCount%", EveryClearGlobalTrash - ClearCount + ""));
                    }
                }

                if (SoundFlag && SoundIntToMessage.containsKey(count)) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        sendMessageAbstract.sendSound(player, SoundIntToMessage.get(count));
                    }
                }

                if (ActionBarFlag && ActionBarIntToMessage.containsKey(count)) {
                    for (Player player : Bukkit.getOnlinePlayers()) {

                        String actionbarMessage = ActionBarIntToMessage.get(count)
                                .replace("%GlobalTrashAddSum%", GlobalTrashItemSum + "").replace("%DealItemSum%", DealItemSum + "")
                                .replace("%EntitySum%", EntitySum + "")
                                .replace("%ClearGlobalCount%", EveryClearGlobalTrash - ClearCount + "");
                        actionbarMessage = papiReplace(actionbarMessage,player);
                        sendMessageAbstract.sendActionBar(player,actionbarMessage);

                    }
                }

                if (CommandFlag && CommandIntToMessage.containsKey(count)) {

                    for (String command : CommandIntToMessage.get(count)) {
                        if(command==null||command.isEmpty()){
                            continue;
                        }
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command
                                .replace("%GlobalTrashAddSum%", GlobalTrashItemSum + "")
                                .replace("%DealItemSum%", DealItemSum + "")
                                .replace("%EntitySum%", EntitySum + "")
                                .replace("%ClearGlobalCount%", EveryClearGlobalTrash - ClearCount + ""));

                    }

                }


                if (TitleFlag && TitleIntToMessage.containsKey(count)) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        if (TitleIntToMessage.get(count).contains(";")) {
                            String[] strings = TitleIntToMessage.get(count).split(";");
                            String titleBigMessage = strings[0].replace("%GlobalTrashAddSum%", GlobalTrashItemSum + "").
                                            replace("%DealItemSum%", DealItemSum + "").
                                            replace("%EntitySum%", EntitySum + "").
                                            replace("%ClearGlobalCount%", EveryClearGlobalTrash - ClearCount + "");
                            String titleSmallMessage = strings[1].replace("%GlobalTrashAddSum%", GlobalTrashItemSum + "").
                                    replace("%DealItemSum%", DealItemSum + "").
                                    replace("%EntitySum%", EntitySum + "").
                                    replace("%ClearGlobalCount%", EveryClearGlobalTrash - ClearCount + "");

                            titleBigMessage = papiReplace(titleBigMessage,player);
                            titleSmallMessage = papiReplace(titleSmallMessage,player);

                            player.sendTitle( titleBigMessage,titleSmallMessage
                                    , 10, 70, 20);
                        } else {
                            String titleBigMessage = TitleIntToMessage.get(count).
                                    replace("%GlobalTrashAddSum%", GlobalTrashItemSum + "").
                                    replace("%DealItemSum%", DealItemSum + "").
                                    replace("%EntitySum%", EntitySum + "").
                                    replace("%ClearGlobalCount%", EveryClearGlobalTrash - ClearCount + "");
                            titleBigMessage = papiReplace(titleBigMessage,player);
                            player.sendTitle(titleBigMessage, "", 10, 70, 20);
                        }

                    }

                }


            }
            @Override
            public void run() {


                if (count == 0) {
                    try {
                        WorldList.clear();
                        WorldList = Bukkit.getWorlds();

                        GlobalTrashItemSum = 0;
                        DealItemSum = 0;
                        EntitySum = 0;
                        ClearCount++;

                        if (finalCount != 0) {

                            if (ClearCount == EveryClearGlobalTrash) {

                                //清理公共垃圾桶
                                ClearCount = 0;
                                ClearContainer(GlobalTrashList);
                                new BukkitRunnable() {
                                    @Override
                                    public void run() {
                                        PrintCountMessage(-2);
                                        new BukkitRunnable() {
                                            @Override
                                            public void run() {
                                                bossBar.removeAll();
                                            }
                                        }.runTaskLater(main, 90L);
                                    }
                                }.runTaskLater(main, 60L);


                            } else {
                                new BukkitRunnable() {
                                    @Override
                                    public void run() {
                                        PrintCountMessage(-1);
                                        new BukkitRunnable() {
                                            @Override
                                            public void run() {
                                                bossBar.removeAll();
                                            }
                                        }.runTaskLater(main, 90L);
                                    }
                                }.runTaskLater(main, 60L);
                            }
                        }


                        for (World world : WorldList) {

                            if (!WorldClearWhiteList.isEmpty() && WorldClearWhiteList.contains(world.getName())) {
                                continue;
                            }

                            Set<Location> locationSet = null;
//                        List<Inventory> inventoryList = new ArrayList<>();

                            if (WorldToLocation.get(world) != null) {
                                locationSet = WorldToLocation.get(world).getLocationSet();
                            }

//                        if (locationSet != null && !locationSet.isEmpty()) {
//                            if (main.getConfig().getBoolean("Set.Debug")) {
//                                for (Location location : locationSet) {
//                                    consoleSay(ChatColor.BLUE + "location为: " + location.toString());
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
                                if (entity instanceof ExperienceOrb) {
                                    if (ClearExpBottle) {
                                        entity.remove();
                                        EntitySum++;
                                        continue;
                                    }
                                }


                                if (entity instanceof Item) {
                                    Item item = (Item) entity;

//                                if (main.getConfig().getBoolean("Set.Debug")) {
//                                    consoleSay(ChatColor.BLUE + " " + world.getName() + "世界 清理了 :" + item.getItemStack().getType() + "x" + item.getItemStack().getAmount() + "个");
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
//                                            consoleSay(ChatColor.BLUE + "还剩 " + inventoryList.size() + " 个箱子");
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
//                                                    consoleSay(ChatColor.RED + "由于没有找到箱子，自动从存储中移除了该" + location.toString() + "位置");
                                                    String locationString = world.getName() + ": " + location.getBlockX() + "," + location.getBlockY() + "," + location.getBlockZ();
                                                    consoleSay(org.worldlisttrashcan.message.find("NotFindChest").replace("%location%", locationString));
                                                }
                                            }
                                        }

//                                    }
                                    }


                                    // 如果物品带有玩家uuid，则是玩家主动丢弃的物品，优先进入玩家个人垃圾桶
                                    // 如果物品没有被处理过且开启了 NoWorldTrashCanEnterPersonalTrashCan
                                    if (flag && VersionFlag) {

                                        ItemMeta meta = itemStack.getItemMeta();
                                        if (meta != null) {
                                            NamespacedKey namespacedKey = new NamespacedKey(main, "PlayerUUID");
                                            String PlayerUUID = meta.getPersistentDataContainer().get(namespacedKey, PersistentDataType.STRING);
//                                    System.out.println("meta.getPersistentDataContainer().getKeys().toString() "+meta.getPersistentDataContainer().getKeys().toString());

                                            if (PlayerUUID != null) {

                                                Player player = Bukkit.getPlayer(UUID.fromString(PlayerUUID));
                                                // 如果丢弃的所属玩家在线
                                                if (player != null) {

                                                    Inventory inventory = PlayerToInventory.get(player);
                                                    if (inventory == null) {

                                                        PlayerToInventory.put(player, InitPlayerInv(player));
                                                    } else {
                                                        RemoveItemTag(itemStack);
                                                        if (inventory.addItem(itemStack).isEmpty()) {
                                                            //加进去了
                                                            flag = false;
                                                        } else {
                                                            //加不进去就清空个人垃圾桶
                                                            if (main.getConfig().getBoolean("Set.PersonalTrashCan.OriginalFeatureClearItemAddGlobalTrash.Model2.AutoClear")) {
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
                                    DealItemSum++;
                                    item.remove();

//                                blockState.update();
                                } else {
                                    if (ClearEntityFlag) {
//                                    if(entity.getType().toString().equals("FLAMMPFEIL.SLASHBLADE_BLADESTAND")){
//                                        entity.remove();
//                                    }

//                                    System.out.println("实体: "+entity.getType().toString());

                                        //检查实体是否在船上
                                        if (IgnoreEntitiesInBoat && entity.isInsideVehicle()) {
                                            Entity vehicle = entity.getVehicle();
                                            if (vehicle instanceof Boat) {
                                                //如果实体在船上，跳过清理
                                                continue;
                                            }
                                        }

                                        if (BlackNameList.contains(entity.getType().toString().toLowerCase()) ||
                                                BlackNameList.contains(entity.getName().toLowerCase())
                                        ) {
//                                        System.out.println("黑名单: "+entity.getType().toString());
                                            entity.remove();
                                            EntitySum++;
                                            continue;
                                        }

                                        if (WhiteNameList.contains(entity.getType().toString().toLowerCase()) ||
                                                WhiteNameList.contains(entity.getName().toLowerCase())
                                        ) {

//                                        System.out.println("白名单: "+entity.getType().toString());
                                            continue;
                                        }


                                        if (entity == null) {
                                            continue;
                                        }


                                        //如果生物被命名过
//                                    if (!ClearReNameEntity&&(entity!=null&&entity.getCustomName()!=null&&!entity.getCustomName().isEmpty())) {

                                        try {
                                            if (!ClearReNameEntity && (
                                                    entity != null &&
                                                            entity.getCustomName() != null
                                                            &&
                                                            !entity.getCustomName().isEmpty())
                                            ) {

                                                continue;
                                            }
                                        }catch (Exception e){
//                                            continue;
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
                                        } else if (isMonster(entity)) {
                                            if (ClearMonster) {
                                                entity.remove();
//                                            System.out.println("ClearMonster: "+entity.getType().toString());
                                                EntitySum++;
                                                continue;
                                            }
                                        }else if (entity instanceof Projectile) {
                                            if (ClearProjectile) {
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

                    }catch (Exception e){
                        consoleSay(ChatColor.RED+"该服务器环境似乎不兼容此插件的某些功能，请将报错截图发送至作者QQ 2831508831");
                        throw e;
                    }finally {

                        PrintCountMessage(count);
                        count = finalCount;
                    }
                }

                if (finalCount == 0) {
                    return;
                }else {
                    PrintCountMessage(count);
                }

                publicTime = count;
                count--;

            }
        };


    }
    public void Start(){
        bukkitRunnable.runTaskTimer(main,20L,20L);
        bossBar.removeAll();
    }
    public void Stop(){
        bukkitRunnable.cancel();
    }




}
