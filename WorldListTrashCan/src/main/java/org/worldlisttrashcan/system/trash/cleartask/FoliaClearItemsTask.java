package org.worldlisttrashcan.system.trash.cleartask;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.worldlisttrashcan.system.trash.ChestGetInventory;
import org.worldlisttrashcan.utils.DataSys;
import org.worldlisttrashcan.utils.EntityCleanMatcher;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import static org.worldlisttrashcan.system.autotrash.AutoTrashListener.*;
import static org.worldlisttrashcan.system.autotrash.HeightVersionPlayerDropItemListener.RemoveItemTag;
import static org.worldlisttrashcan.utils.Method.isMonster;
import static org.worldlisttrashcan.utils.Method.papiReplace;
import static org.worldlisttrashcan.system.trash.GlobalTrashGui.ClearContainer;
import static org.worldlisttrashcan.system.trash.TrashListener.GlobalItemSetString;
import static org.worldlisttrashcan.WorldListTrashCan.*;
import static org.worldlisttrashcan.utils.LogSys.customLogToFile;
import static org.worldlisttrashcan.utils.LogSys.logFlag;
import static org.worldlisttrashcan.utils.Message.*;

public class FoliaClearItemsTask {
    List<World> WorldList = new ArrayList<>();

    int publicTime = 0;

    public int getPublicTime() {
        return publicTime;
    }


    int finalCount;

    //    ConcurrentMap<Entity,Object> ChunkMap = new ConcurrentHashMap<>();

    public boolean NoClearItemFlag(ItemStack itemStack) {
        for (String type : NoClearContainerType) {
            if (itemStack.getType().toString().equals(type)) {
                return true;
            }
        }
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta != null && itemMeta.getLore() != null) {
            List<String> strings = itemMeta.getLore();
            for (String lore : NoClearContainerLore) {
                for (String string : strings) {
                    if (string.contains(lore)) {
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


    public FoliaClearItemsTask(int amount) {

        finalCount = amount;

        boolean BossBarFlag = main.getConfig().getBoolean("Set.BossBarFlag");
        boolean ChatFlag = main.getConfig().getBoolean("Set.ChatFlag");
        boolean ChatConsoleLogFlag = main.getConfig().getBoolean("Set.ChatConsoleLogFlag");
        String ChatClickCommand = main.getConfig().getString("Set.ChatClickCommand");
        boolean SoundFlag = main.getConfig().getBoolean("Set.SoundFlag");
        boolean TitleFlag = main.getConfig().getBoolean("Set.TitleFlag");
        boolean CommandFlag = main.getConfig().getBoolean("Set.CommandFlag");
        boolean ActionBarFlag = main.getConfig().getBoolean("Set.ActionBarFlag");


        Map<Integer, String> BossBarToMessage = new HashMap<>();
        for (String message : main.getConfig().getStringList("Set.BossBarMessageForCount")) {
            String[] strings = message.split(";");
            BossBarToMessage.put(Integer.parseInt(strings[0]),
                    color(strings[1] + ";" + strings[2] + ";" + strings[3])
            );
        }

        Map<Integer, String> ChatIntToMessage = new HashMap<>();
        for (String message : main.getConfig().getStringList("Set.ChatMessageForCount")) {
            String[] strings = message.split(";");
//            ChatIntToMessage.put(Integer.parseInt(strings[0]), color(strings[1]));
            ChatIntToMessage.put(Integer.parseInt(strings[0]), strings[1]);
        }


        Map<Integer, String> SoundIntToMessage = new HashMap<>();
        for (String message : main.getConfig().getStringList("Set.SoundForCount")) {
            String[] strings = message.split(";");
            SoundIntToMessage.put(Integer.parseInt(strings[0]), strings[1]);
        }

        Map<Integer, String> ActionBarIntToMessage = new HashMap<>();
        for (String message : main.getConfig().getStringList("Set.ActionBarMessageForCount")) {
            String[] strings = message.split(";");
            ActionBarIntToMessage.put(Integer.parseInt(strings[0]), strings[1]);
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

        Map<Integer, String> TitleIntToMessage = new HashMap<>();
        for (String message : main.getConfig().getStringList("Set.TitleMessageForCount")) {
            String[] strings = message.split(";");
            if (strings.length > 2) {
                TitleIntToMessage.put(Integer.parseInt(strings[0]), color(strings[1]) + ";" + color(strings[2]));
            } else {
                TitleIntToMessage.put(Integer.parseInt(strings[0]), color(strings[1]));

            }

        }

        boolean ClearExpBottle = main.getConfig().getBoolean("Set.ClearEntity.ClearExpBottle");
        boolean ClearMonster = main.getConfig().getBoolean("Set.ClearEntity.ClearMonster");
        boolean ClearAnimals = main.getConfig().getBoolean("Set.ClearEntity.ClearAnimals");
        boolean ClearProjectile = main.getConfig().getBoolean("Set.ClearEntity.ClearProjectile");
        boolean ClearReNameEntity = main.getConfig().getBoolean("Set.ClearEntity.ClearReNameEntity");
        boolean IgnoreEntitiesInBoat = main.getConfig().getBoolean("Set.ClearEntity.IgnoreEntitiesInBoat");

        List<String> WorldClearWhiteList = main.getConfig().getStringList("Set.WorldClearWhiteList");

//
//        List<String> WhiteNameList = main.getConfig().getStringList("Set.ClearEntity.WhiteNameList");
//        List<String> BlackNameList = main.getConfig().getStringList("Set.ClearEntity.BlackNameList");
//        //全部转换为小写
//        BlackNameList.replaceAll(String::toLowerCase);
//        WhiteNameList.replaceAll(String::toLowerCase);


        List<String> WhiteNameList = main.getConfig().getStringList("Set.ClearEntity.WhiteNameList");
        List<String> BlackNameList = main.getConfig().getStringList("Set.ClearEntity.BlackNameList");

        //全部转换为小写
        BlackNameList.replaceAll(String::toLowerCase);
        WhiteNameList.replaceAll(String::toLowerCase);

        EntityCleanMatcher matcher = new EntityCleanMatcher(
                // 白名单
                WhiteNameList,
                // 黑名单
                BlackNameList
        );

        boolean ClearEntityFlag = main.getConfig().getBoolean("Set.ClearEntity.Flag");

        int bossBarMaxInt;
        bossBarMaxInt = Integer.parseInt(main.getConfig().getStringList("Set.BossBarMessageForCount").get(0).split(";")[0]);


        int EveryClearGlobalTrash = main.getConfig().getInt("Set.GlobalTrash.EveryClearGlobalTrash");


        foliaRunnable = new java.util.function.Consumer<ScheduledTask>() {
            int count = finalCount;
            AtomicInteger GlobalTrashItemSum = new AtomicInteger(0);
            AtomicInteger DealItemSum = new AtomicInteger(0);
            AtomicInteger EntitySum = new AtomicInteger(0);
            AtomicInteger ClearCount = new AtomicInteger(0);
//            int ClearCount = 0;

            // 当前是否正在清理
//            AtomicBoolean isNowClear = new AtomicBoolean(false);


            public void PrintCountMessage(int count) {

                //如果EveryClearGlobalTrash==-1，且count==-1，则不提示
//                if (EveryClearGlobalTrash == -1 && count == -1) {
//                    return;
//                }


                if (BossBarFlag && BossBarToMessage.containsKey(count)) {
                    String string = BossBarToMessage.get(count);
                    String[] strings = string.split(";");
                    String message = (strings[0]
                            .replace("%GlobalTrashAddSum%", GlobalTrashItemSum + "")
                            .replace("%DealItemSum%", DealItemSum + "")
                            .replace("%EntitySum%", EntitySum + "")
                            .replace("%ClearGlobalCount%", EveryClearGlobalTrash - ClearCount.get() + ""));
//                    for (OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
//                        if (offlinePlayer == null) {
//                            continue;
//                        }
//                        Player player = offlinePlayer.getPlayer();
                        if (bossBar.getPlayers().contains(player)) {
                            //如果这个玩家有bossbar了
                        } else {
                            //玩家没有bossbar
                            if (player != null) {
                                bossBar.addPlayer(player);
                            } else {
                            }
                        }
                        bossBar.setTitle(papiReplace(message,player));
                        bossBar.setColor(BarColor.valueOf(strings[2]));
                        bossBar.setStyle(BarStyle.valueOf(strings[1]));
                        double ct = ((double) count) / bossBarMaxInt;
                        if (ct > 0) {
                            bossBar.setProgress(ct);
                        } else {
                            bossBar.setProgress(1);
                        }
                    }
                }

                if (ChatFlag && ChatIntToMessage.containsKey(count)) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        String text = ChatIntToMessage.get(count).replace("%GlobalTrashAddSum%", GlobalTrashItemSum + "")
                                .replace("%DealItemSum%", DealItemSum + "").replace("%EntitySum%", EntitySum + "")
                                .replace("%ClearGlobalCount%", EveryClearGlobalTrash - ClearCount.get() + "");
                        text = papiReplace(text,player);

//                        BaseComponent[] components = colorToBaseComponent(text);

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
                        consoleSay(ChatIntToMessage.get(count).replace("%GlobalTrashAddSum%", GlobalTrashItemSum + "").replace("%DealItemSum%", DealItemSum + "").replace("%EntitySum%", EntitySum + "").replace("%ClearGlobalCount%", EveryClearGlobalTrash - ClearCount.get() + ""));
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
                                .replace("%ClearGlobalCount%", EveryClearGlobalTrash - ClearCount.get() + "");
                        actionbarMessage = papiReplace(actionbarMessage,player);
                        sendMessageAbstract.sendActionBar(player,actionbarMessage);

                    }
                }

                if (CommandFlag && CommandIntToMessage.containsKey(count)) {

//                    for (String command : CommandIntToMessage.get(count)) {
//                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command
//                                .replace("%GlobalTrashAddSum%", GlobalTrashItemSum + "")
//                                .replace("%DealItemSum%", DealItemSum + "")
//                                .replace("%EntitySum%", EntitySum + "")
//                                .replace("%ClearGlobalCount%", EveryClearGlobalTrash - ClearCount + ""));
//
//                    }

                    Bukkit.getGlobalRegionScheduler().execute(main, () -> {
                        for (String command : CommandIntToMessage.get(count)) {
                            if(command==null||command.isEmpty()){
                                continue;
                            }
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command
                                    .replace("%GlobalTrashAddSum%", GlobalTrashItemSum + "")
                                    .replace("%DealItemSum%", DealItemSum + "")
                                    .replace("%EntitySum%", EntitySum + "")
                                    .replace("%ClearGlobalCount%", EveryClearGlobalTrash - ClearCount.get() + ""));

                        }
                    });

                }


                if (TitleFlag && TitleIntToMessage.containsKey(count)) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        if (TitleIntToMessage.get(count).contains(";")) {
                            String[] strings = TitleIntToMessage.get(count).split(";");
                            String titleBigMessage = strings[0].replace("%GlobalTrashAddSum%", GlobalTrashItemSum + "").
                                    replace("%DealItemSum%", DealItemSum + "").
                                    replace("%EntitySum%", EntitySum + "").
                                    replace("%ClearGlobalCount%", EveryClearGlobalTrash - ClearCount.get() + "");
                            String titleSmallMessage = strings[1].replace("%GlobalTrashAddSum%", GlobalTrashItemSum + "").
                                    replace("%DealItemSum%", DealItemSum + "").
                                    replace("%EntitySum%", EntitySum + "").
                                    replace("%ClearGlobalCount%", EveryClearGlobalTrash - ClearCount.get()+ "");

                            titleBigMessage = papiReplace(titleBigMessage,player);
                            titleSmallMessage = papiReplace(titleSmallMessage,player);

                            player.sendTitle( titleBigMessage,titleSmallMessage
                                    , 10, 70, 20);
                        } else {
                            String titleBigMessage = TitleIntToMessage.get(count).
                                    replace("%GlobalTrashAddSum%", GlobalTrashItemSum + "").
                                    replace("%DealItemSum%", DealItemSum + "").
                                    replace("%EntitySum%", EntitySum + "").
                                    replace("%ClearGlobalCount%", EveryClearGlobalTrash - ClearCount.get() + "");
                            titleBigMessage = papiReplace(titleBigMessage,player);
                            player.sendTitle(titleBigMessage, "", 10, 70, 20);
                        }

                    }
                }
            }

            //任务总数taskSum
//            int taskSum = 0;
            //当前执行完毕的任务总数nowSum
//            int nowSum = 0;

//            Map<Integer, Integer> NowChatIntToInt = new HashMap<>();



            @Override
            public void accept(ScheduledTask scheduledTask) {
                if (count<0){
                    return;
                }

                if (count == 0) {

                    count--;

                    try {

                        if (finalCount!=0) {
//                            Bukkit.getAsyncScheduler().runDelayed(main, scheduledTask1 -> {
                            ClearCount.addAndGet(1);
//                                if (ClearCount.get() == EveryClearGlobalTrash) {
//                                    //清理公共垃圾桶
//                                    ClearCount.set(0);
//                                    ClearContainer(GlobalTrashList);
//                                    PrintCountMessage(-2);
//                                }
//                            }, 3, TimeUnit.SECONDS);

//                            ClearCount.addAndGet(1);

                            if (ClearCount.get() == EveryClearGlobalTrash) {
                                //清理公共垃圾桶
//                                ClearCount.set(0);
                                ClearContainer(GlobalTrashList);
//                                PrintCountMessage(-2);
                            }
                        }






                        WorldList.clear();
                        WorldList = Bukkit.getWorlds();

                        GlobalTrashItemSum.set(0);
                        DealItemSum.set(0);
                        EntitySum.set(0);



                        AtomicInteger allChunkTask = new AtomicInteger(0);

                        for (World world : WorldList) {

                            if (!WorldClearWhiteList.isEmpty() && WorldClearWhiteList.contains(world.getName())) {
                                continue;
                            }

                            for (Chunk chunk : world.getLoadedChunks()) {

                                allChunkTask.addAndGet(1);

                                Bukkit.getRegionScheduler().run(main, new Location(chunk.getWorld(), chunk.getX() * 16, 0, chunk.getZ() * 16), new Consumer<ScheduledTask>() {
                                    @Override
                                    public void accept(ScheduledTask scheduledTask) {
                                        Set<Location> locationSet = null;
                                        if (WorldToLocation.get(world) != null) {
                                            locationSet = WorldToLocation.get(world).getLocationSet();
                                        }
                                        for (Entity entity : chunk.getEntities()) {
//                                        ChunkMap.put(entity,null);
                                            if (entity instanceof ExperienceOrb) {
                                                if (ClearExpBottle) {
                                                    entity.remove();
                                                    EntitySum.addAndGet(1);
                                                    continue;
                                                }
                                            }

                                            if (entity instanceof Item) {
                                                Item item = (Item) entity;
                                                ItemStack itemStack = item.getItemStack();
                                                if (NoClearItemFlag(itemStack)) {
                                                    continue;
                                                }
                                                String itemStackTypeString = itemStack.getType().toString();
                                                boolean flag = true;
                                                if (locationSet != null && !locationSet.isEmpty()) {
                                                    //如果物品不在世界垃圾桶ban表里
                                                    if (!WorldToLocation.get(world).getBanItemSet().contains(itemStackTypeString)) {
//                                        if (main.getConfig().getBoolean("Set.Debug")) {
//                                            consoleSay(ChatColor.BLUE + "还剩 " + inventoryList.size() + " 个箱子");
//                                        }
                                                        for (Location location : locationSet) {

                                                            Inventory inventory = ChestGetInventory.getInventory(location.getBlock());
                                                            if (inventory != null) {
                                                                if (inventory.addItem(itemStack).isEmpty()) {
//                                                    System.out.println("加进去了");
                                                                    flag = false;
                                                                    break;
                                                                }
//                                                    System.out.println("没加进去");
                                                            }
                                                            //无法获取到箱子对象的处理
                                                            else {
                                                                Set<Location> locationSet1 = WorldToLocation.get(world).getLocationSet();
                                                                locationSet1.remove(location);
                                                                DataSys.dataPut(world, locationSet1);
//                                                    consoleSay(ChatColor.RED + "由于没有找到箱子，自动从存储中移除了该" + location.toString() + "位置");
                                                                String locationString = world.getName() + ": " + location.getBlockX() + "," + location.getBlockY() + "," + location.getBlockZ();
                                                                consoleSay(org.worldlisttrashcan.utils.Message.find("NotFindChest").replace("%location%", locationString));
                                                            }
                                                        }
                                                    }
                                                }


                                                // 如果物品带有玩家uuid，则是玩家主动丢弃的物品，优先进入玩家个人垃圾桶
                                                // 如果物品没有被处理过且开启了 NoWorldTrashCanEnterPersonalTrashCan
                                                if (flag && VersionFlag) {

                                                    ItemMeta meta = itemStack.getItemMeta();
                                                    if (meta != null) {
                                                        NamespacedKey namespacedKey = new NamespacedKey(main, "PlayerUUID");
                                                        String PlayerUUID = meta.getPersistentDataContainer().get(namespacedKey, PersistentDataType.STRING);
//                                                System.out.println("meta.getPersistentDataContainer().getKeys().toString() "+meta.getPersistentDataContainer().getKeys().toString());

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
                                                                            player.sendMessage(org.worldlisttrashcan.utils.Message.find("PlayerTrashCanFilled"));


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
                                                        for (Inventory inventory : GlobalTrashList) {
                                                            if (inventory.addItem(itemStack).isEmpty()) {
                                                                //加进去到公共垃圾桶了
                                                                GlobalTrashItemSum.addAndGet(1);

                                                                break;
                                                            }
                                                        }
                                                    }
                                                }
                                                item.remove();


                                                DealItemSum.addAndGet(1);
//                                blockState.update();
                                            } else {
                                                if (ClearEntityFlag) {

                                                    //检查实体是否在船上
                                                    if (IgnoreEntitiesInBoat && entity.isInsideVehicle()) {
                                                        Entity vehicle = entity.getVehicle();
                                                        if (vehicle instanceof Boat) {
                                                            //如果实体在船上，跳过清理
                                                            continue;
                                                        }
                                                    }


                                                    Boolean checkClean = matcher.checkClean(entity.getType().toString(), entity.getName());
                                                    if (checkClean != null) {
                                                        if (checkClean) {
//                                                System.out.println("黑名单: "+entity.getType().toString());
                                                            entity.remove();
                                                            EntitySum.addAndGet(1);
                                                        }
                                                        continue;
                                                    }

//                                                    if (BlackNameList.contains(entity.getType().toString().toLowerCase()) ||
//                                                            BlackNameList.contains(entity.getName().toLowerCase())
//                                                    ) {
////                                        System.out.println("黑名单: "+entity.getType().toString());
//                                                        entity.remove();
//                                                        EntitySum++;
//                                                        continue;
//                                                    }
//
//                                                    if (WhiteNameList.contains(entity.getType().toString().toLowerCase()) ||
//                                                            WhiteNameList.contains(entity.getName().toLowerCase())
//                                                    ) {
////                                        System.out.println("白名单: "+entity.getType().toString());
//                                                        continue;
//                                                    }


//                                                    if (entity == null) {
//                                                        continue;
//                                                    }

                                                    //如果生物被命名过
                                                    if (!ClearReNameEntity && (entity != null && entity.getCustomName() != null && !entity.getCustomName().isEmpty())) {
                                                        continue;
                                                    }

                                                    if (entity instanceof org.bukkit.entity.Animals) {
                                                        if (ClearAnimals) {
                                                            entity.remove();
                                                            EntitySum.addAndGet(1);
                                                            continue;
                                                        }
//                                                    } else if (entity instanceof Enemy) {
//                                                    } else if (entity instanceof Monster) {
                                                    } else if (isMonster(entity)) {
                                                        if (ClearMonster) {
                                                            entity.remove();
                                                            EntitySum.addAndGet(1);

                                                            continue;
                                                        }
                                                    }else if (entity instanceof Projectile) {
                                                        if (ClearProjectile) {
                                                            entity.remove();
//                                            System.out.println("ClearMonster: "+entity.getType().toString());
                                                            EntitySum.addAndGet(1);
                                                            continue;
                                                        }
                                                    }



                                                }

                                            }
                                        }

                                        // TODO
                                        int taskCount = allChunkTask.addAndGet(-1);
                                        if (taskCount <= 0) {
                                            PrintCountMessage(0);

                                            publicTime = 0;
                                            // 延迟1秒后执行

//                                            Bukkit.getAsyncScheduler().runDelayed(main, scheduledTask1 -> {
//                                                PrintCountMessage(-1);
//                                                Bukkit.getAsyncScheduler().runDelayed(main, scheduledTask2 -> {
//                                                    PrintCountMessage(-2);
//                                                    count = finalCount;
//                                                }, 1, TimeUnit.SECONDS);
//                                            }, 1, TimeUnit.SECONDS);


                                            if (finalCount!=0) {
                                                Bukkit.getAsyncScheduler().runDelayed(main, scheduledTask1 -> {

//                                                    ClearCount.addAndGet(1);

                                                    if (ClearCount.get() == EveryClearGlobalTrash) {
                                                        //清理公共垃圾桶
                                                        ClearCount.set(0);
//                                                        ClearContainer(GlobalTrashList);

                                                        PrintCountMessage(-2);

                                                    } else {
                                                        if (EveryClearGlobalTrash!=-1) {
                                                            PrintCountMessage(-1);
                                                        }
                                                    }

                                                    Bukkit.getAsyncScheduler().runDelayed(main, new Consumer<ScheduledTask>() {
                                                        @Override
                                                        public void accept(ScheduledTask scheduledTask) {

                                                            count = finalCount;

                                                            bossBar.removeAll();
                                                        }
                                                    }, 3, TimeUnit.SECONDS);


                                                }, 3, TimeUnit.SECONDS);
                                            }
//                                            else {
//                                                Bukkit.getGlobalRegionScheduler().execute(main, () -> {
//                                                    Stop();
//                                                });
//                                            }
                                        }

                                    }

                                });
                            }

                        }


                        //在这一步，直接输出会输出0哥实体和清理的垃圾，因为是瞬时的，而我们的清理是异步的
//                    PrintCountMessage(count);

                    } catch (Exception e) {
                        consoleSay(ChatColor.RED + "该服务器环境似乎不兼容此插件的某些功能，请将报错截图发送至作者QQ 2831508831");
                        throw e;
                    }
//                    finally {
//                        count = finalCount;
//                        NowChatIntToInt.put(finalCount - 1, 0);
//                    }
                }else {
                    PrintCountMessage(count);
                    publicTime = count;
                    count--;
                }

            }
        };


    }

    ScheduledTask foliaTask;


    public ScheduledTask getFoliaTask() {
        return foliaTask;
    }


//    public boolean started = false;

    public void Start() {
//        if (started) {
//            return;
//        }

//        started = true;
        foliaTask = Bukkit.getAsyncScheduler().runAtFixedRate(main, foliaRunnable, 1, 1, TimeUnit.SECONDS);
        bossBar.removeAll();
    }

    public void Stop() {

//        if (!started){
//            return;
//        }

//        started = false;
        if (foliaTask.isCancelled()) {
            return;
        }
        foliaTask.cancel();
    }

    java.util.function.Consumer<ScheduledTask> foliaRunnable;

    public Consumer<ScheduledTask> getFoliaRunnable() {
        return foliaRunnable;
    }

}
