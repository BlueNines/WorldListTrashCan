package org.worldlisttrashcan.TrashMain;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.worldlisttrashcan.data;
import org.worldlisttrashcan.message;

import java.util.*;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import static org.worldlisttrashcan.AutoTrashMain.AutoTrashListener.*;
import static org.worldlisttrashcan.Method.Method.isMonster;
import static org.worldlisttrashcan.TrashMain.GlobalTrashGui.ClearContainer;
import static org.worldlisttrashcan.TrashMain.TrashListener.GlobalItemSetString;
import static org.worldlisttrashcan.WorldListTrashCan.*;
import static org.worldlisttrashcan.message.color;
import static org.worldlisttrashcan.message.consoleSay;

public class FoliaClearItemsTask2 {
    List<World> WorldList = new ArrayList<>();

    int publicTime = 0;

    public int getPublicTime() {
        return publicTime;
    }


    int finalCount;

    //    ConcurrentMap<Entity,Object> ChunkMap = new ConcurrentHashMap<>();

    public ConcurrentSkipListMap<World, List<ItemStack>> WorldToItemStackList = new ConcurrentSkipListMap<>();
    public AtomicInteger handleChunkCount = new AtomicInteger();


    public AtomicInteger GlobalTrashItemSum = new AtomicInteger();
    public AtomicInteger DealItemSum = new AtomicInteger();
    public AtomicInteger EntitySum = new AtomicInteger();
    public AtomicInteger ClearCount = new AtomicInteger();
    public AtomicInteger count = new AtomicInteger();
    public AtomicInteger EveryClearGlobalTrash = new AtomicInteger();

    public boolean NoClearItemFlag(ItemStack itemStack) {
        for (String type : NoClearContainerType) {
            if (itemStack.getType().toString().equals(type)) {
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
                    if (string.contains(lore)) {
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


    public FoliaClearItemsTask2(int amount) {

        finalCount = amount;

        boolean BossBarFlag = main.getConfig().getBoolean("Set.BossBarFlag");
        boolean ChatFlag = main.getConfig().getBoolean("Set.ChatFlag");
        boolean SoundFlag = main.getConfig().getBoolean("Set.SoundFlag");
        boolean TitleFlag = main.getConfig().getBoolean("Set.TitleFlag");
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
            ChatIntToMessage.put(Integer.parseInt(strings[0]), color(strings[1]));
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
        boolean ClearReNameEntity = main.getConfig().getBoolean("Set.ClearEntity.ClearReNameEntity");

        List<String> WhiteNameList = main.getConfig().getStringList("Set.ClearEntity.WhiteNameList");
        List<String> BlackNameList = main.getConfig().getStringList("Set.ClearEntity.BlackNameList");
        List<String> WorldClearWhiteList = main.getConfig().getStringList("Set.WorldClearWhiteList");


        //全部转换为小写
        BlackNameList.replaceAll(String::toLowerCase);
        WhiteNameList.replaceAll(String::toLowerCase);

        boolean ClearEntityFlag = main.getConfig().getBoolean("Set.ClearEntity.Flag");

        int bossBarMaxInt;
        bossBarMaxInt = Integer.parseInt(main.getConfig().getStringList("Set.BossBarMessageForCount").get(0).split(";")[0]);


        EveryClearGlobalTrash.set(main.getConfig().getInt("Set.GlobalTrash.EveryClearGlobalTrash"));

        GlobalTrashItemSum.set(0);
        DealItemSum.set(0);
        EntitySum.set(0);
        ClearCount.set(0);
        count.set(finalCount);

        foliaRunnable = new Consumer<ScheduledTask>() {
//            GlobalTrashItemSum = 0, DealItemSum = 0, EntitySum = 0,ClearCount = 0,count = finalCount;



            public void PrintCountMessage(int count) {

                //如果EveryClearGlobalTrash==-1，且count==-1，则不提示
                if (EveryClearGlobalTrash.get() == -1 && count == -1) {
                    return;
                }


                if (BossBarFlag && BossBarToMessage.containsKey(count)) {
                    String string = BossBarToMessage.get(count);

                    String[] strings = string.split(";");
                    String message = (strings[0]
                            .replace("%GlobalTrashAddSum%", GlobalTrashItemSum.get() + "")
                            .replace("%DealItemSum%", DealItemSum.get() + "")
                            .replace("%EntitySum%", EntitySum.get() + "")
                            .replace("%ClearGlobalCount%", EveryClearGlobalTrash.get() - ClearCount.get() + ""));
                    for (OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {
                        if (offlinePlayer == null) {
                            continue;
                        }
                        Player player = offlinePlayer.getPlayer();
                        if (bossBar.getPlayers().contains(player)) {
                            //如果这个玩家有bossbar了
                        } else {
                            //玩家没有bossbar
                            if (player != null) {
                                bossBar.addPlayer(player);
                            } else {
                            }
                        }
                        bossBar.setTitle(message);
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
                        player.sendMessage(ChatIntToMessage.get(count).replace("%GlobalTrashAddSum%", GlobalTrashItemSum.get() + "").replace("%DealItemSum%", DealItemSum.get() + "").replace("%EntitySum%", EntitySum.get() + "").replace("%ClearGlobalCount%", EveryClearGlobalTrash.get() - ClearCount.get() + ""));
                    }
                    consoleSay(ChatIntToMessage.get(count).replace("%GlobalTrashAddSum%", GlobalTrashItemSum.get() + "").replace("%DealItemSum%", DealItemSum.get() + "").replace("%EntitySum%", EntitySum.get() + "").replace("%ClearGlobalCount%", EveryClearGlobalTrash.get() - ClearCount.get() + ""));
                }

                if (SoundFlag && SoundIntToMessage.containsKey(count)) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        sendMessageAbstract.sendSound(player, SoundIntToMessage.get(count));
                    }
                }

                if (ActionBarFlag && ActionBarIntToMessage.containsKey(count)) {
                    for (Player player : Bukkit.getOnlinePlayers()) {

                        sendMessageAbstract.sendActionBar(player, ActionBarIntToMessage.get(count)
                                .replace("%GlobalTrashAddSum%", GlobalTrashItemSum.get() + "")
                                .replace("%DealItemSum%", DealItemSum.get() + "")
                                .replace("%EntitySum%", EntitySum.get() + "")
                                .replace("%ClearGlobalCount%", EveryClearGlobalTrash.get() - ClearCount.get() + ""));


                    }
                }
                if (TitleFlag && TitleIntToMessage.containsKey(count)) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        if (TitleIntToMessage.get(count).contains(";")) {
                            String[] strings = TitleIntToMessage.get(count).split(";");
                            player.sendTitle(strings[0].replace("%GlobalTrashAddSum%", GlobalTrashItemSum.get() + "").replace("%DealItemSum%", DealItemSum.get() + "").replace("%EntitySum%", EntitySum.get() + "").replace("%ClearGlobalCount%", EveryClearGlobalTrash.get() - ClearCount.get() + ""),
                                    strings[1].replace("%GlobalTrashAddSum%", GlobalTrashItemSum.get() + "").replace("%DealItemSum%", DealItemSum.get() + "").replace("%EntitySum%", EntitySum.get() + "").replace("%ClearGlobalCount%", EveryClearGlobalTrash.get() - ClearCount.get() + ""), 10, 70, 20);
                        } else {
                            player.sendTitle(TitleIntToMessage.get(count).replace("%GlobalTrashAddSum%", GlobalTrashItemSum.get() + "").replace("%DealItemSum%", DealItemSum.get() + "").replace("%EntitySum%", EntitySum.get() + "").replace("%ClearGlobalCount%", EveryClearGlobalTrash.get() - ClearCount.get() + ""), "", 10, 70, 20);
                        }

                    }
                }
            }

            //任务总数taskSum
//            int taskSum = 0;
            //当前执行完毕的任务总数nowSum
//            int nowSum = 0;

            Map<Integer, Integer> NowChatIntToInt = new HashMap<>();

            @Override
            public void accept(ScheduledTask scheduledTask) {
                if (count.get() == 0) {
                    try {
                        WorldList.clear();
                        WorldList = Bukkit.getWorlds();

//                        GlobalTrashItemSum = 0;
//                        DealItemSum = 0;
//                        EntitySum = 0;
//                        ClearCount++;
                        GlobalTrashItemSum.set(0);
                        DealItemSum.set(0);
                        EntitySum.set(0);
                        ClearCount.set(ClearCount.get()-1);
                        if (ClearCount.get() == EveryClearGlobalTrash.get()) {

                            //清理公共垃圾桶
                            ClearCount.set(0);
                            ClearContainer(GlobalTrashList);

//                        Bukkit.getAsyncScheduler().runDelayed(main, new Consumer<ScheduledTask>() {
//                            @Override
//                            public void accept(ScheduledTask scheduledTask) {
//                                PrintCountMessage(-2);
//                            }
//                        },2, TimeUnit.SECONDS);
                            Bukkit.getAsyncScheduler().runDelayed(main, new Consumer<ScheduledTask>() {
                                @Override
                                public void accept(ScheduledTask scheduledTask) {
                                    bossBar.removeAll();
                                }
                            }, 7, TimeUnit.SECONDS);

                            NowChatIntToInt.put(finalCount - 3, -2);


                        } else {
//                        Bukkit.getAsyncScheduler().runDelayed(main, new Consumer<ScheduledTask>() {
//                            @Override
//                            public void accept(ScheduledTask scheduledTask) {
//                                PrintCountMessage(-1);
//                            }
//                        },2, TimeUnit.SECONDS);
                            Bukkit.getAsyncScheduler().runDelayed(main, new Consumer<ScheduledTask>() {
                                @Override
                                public void accept(ScheduledTask scheduledTask) {
                                    bossBar.removeAll();
                                }
                            }, 7, TimeUnit.SECONDS);
                            NowChatIntToInt.put(finalCount - 3, -1);
                        }


                        for (World world : WorldList) {

                            if (!WorldClearWhiteList.isEmpty() && WorldClearWhiteList.contains(world.getName())) {
                                continue;
                            }

                            Chunk[] loadedChunks = world.getLoadedChunks();
                            handleChunkCount.set(loadedChunks.length);

                            WorldToItemStackList.clear();


                            for (Chunk chunk : loadedChunks) {
                                Bukkit.getRegionScheduler().run(main, new Location(chunk.getWorld(), chunk.getX() * 16, 0, chunk.getZ() * 16), new Consumer<ScheduledTask>() {
                                    @Override
                                    public void accept(ScheduledTask scheduledTask) {
//                                        Set<Location> locationSet = null;
//                                        if (WorldToLocation.get(world) != null) {
//                                            locationSet = WorldToLocation.get(world).getLocationSet();
//                                        }
                                        for (Entity entity : chunk.getEntities()) {
//                                        ChunkMap.put(entity,null);
                                            if (entity instanceof ExperienceOrb) {
                                                if (ClearExpBottle) {
                                                    entity.remove();
                                                    EntitySum.set(EntitySum.get()+1);
                                                    continue;
                                                }
                                            }

                                            if (entity instanceof Item) {
                                                Item item = (Item) entity;
                                                ItemStack itemStack = item.getItemStack();
                                                if (NoClearItemFlag(itemStack)) {
                                                    continue;
                                                }
//                                                String itemStackTypeString = itemStack.getType().toString();
                                                boolean flag = true;

//                                                if (locationSet != null && !locationSet.isEmpty()) {
//                                                    //如果物品不在世界垃圾桶ban表里
//                                                    if (!WorldToLocation.get(world).getBanItemSet().contains(itemStackTypeString)) {
////                                        if (main.getConfig().getBoolean("Set.Debug")) {
////                                            consoleSay(ChatColor.BLUE + "还剩 " + inventoryList.size() + " 个箱子");
////                                        }
//                                                        for (Location location : locationSet) {
//
//                                                            Inventory inventory = getInventory.getInventory(location.getBlock());
//                                                            if (inventory != null) {
//                                                                if (inventory.addItem(itemStack).isEmpty()) {
////                                                    System.out.println("加进去了");
//                                                                    flag = false;
//                                                                    break;
//                                                                }
////                                                    System.out.println("没加进去");
//                                                            }
//                                                            //无法获取到箱子对象的处理
//                                                            else {
//                                                                Set<Location> locationSet1 = WorldToLocation.get(world).getLocationSet();
//                                                                locationSet1.remove(location);
//                                                                data.dataPut(world, locationSet1);
////                                                    consoleSay(ChatColor.RED + "由于没有找到箱子，自动从存储中移除了该" + location.toString() + "位置");
//                                                                String locationString = world.getName() + ": " + location.getBlockX() + "," + location.getBlockY() + "," + location.getBlockZ();
//                                                                consoleSay(message.find("NotFindChest").replace("%location%", locationString));
//                                                            }
//                                                        }
//                                                    }
//                                                }

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

//                                                //如果物品不在全局世界垃圾桶ban表里
//                                                if (GlobalTrashGuiFlag && !GlobalItemSetString.contains(itemStackTypeString)) {
//                                                    //如果全球垃圾桶开启，且该物品没有被普通的世界垃圾桶回收过
//                                                    if (flag) {
//                                                        for (Inventory inventory : GlobalTrashList) {
//                                                            if (inventory.addItem(itemStack).isEmpty()) {
//                                                                //加进去到公共垃圾桶了
//                                                                GlobalTrashItemSum++;
//
//
//                                                                break;
//                                                            }
//                                                        }
//                                                    }
//                                                }
                                                if (flag) {
                                                    List<ItemStack> itemStackList = WorldToItemStackList.get(world);
                                                    itemStackList.add(itemStack);
                                                    WorldToItemStackList.put(world, itemStackList);
                                                }
                                                item.remove();

                                                DealItemSum.set(DealItemSum.get()+1);

//                                blockState.update();
                                            } else {
                                                if (ClearEntityFlag) {

                                                    if (BlackNameList.contains(entity.getType().toString().toLowerCase()) ||
                                                            BlackNameList.contains(entity.getName().toLowerCase())
                                                    ) {
//                                        System.out.println("黑名单: "+entity.getType().toString());
                                                        entity.remove();

                                                        EntitySum.set(EntitySum.get()+1);
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
                                                    if (!ClearReNameEntity && (entity != null && entity.getCustomName() != null && !entity.getCustomName().isEmpty())) {
                                                        continue;
                                                    }

                                                    if (entity instanceof Animals) {
                                                        if (ClearAnimals) {
                                                            entity.remove();
//                                                            EntitySum++;
                                                            EntitySum.set(EntitySum.get()+1);
                                                            continue;
                                                        }
//                                                    } else if (entity instanceof Enemy) {
//                                                    } else if (entity instanceof Monster) {
                                                    } else if (isMonster(entity)) {
                                                        if (ClearMonster) {
                                                            entity.remove();
//                                                            EntitySum++;
                                                            EntitySum.set(EntitySum.get()+1);

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


                                        int i = handleChunkCount.get() - 1;
                                        handleChunkCount.set(i);


                                        if (handleChunkCount.get() <= 0) {
                                            //最后一个任务
                                            Set<Location> locationSet = null;
                                            if (WorldToLocation.get(world) != null) {
                                                locationSet = new HashSet<>(WorldToLocation.get(world).getLocationSet());


                                                ActionSaveWorldTrashCan(world, new ArrayList<>(locationSet));


                                            }


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
                    } finally {
//                        count = finalCount;
                        count.set(finalCount);
                        NowChatIntToInt.put(finalCount - 1, 0);
                    }
                }

                if (!NowChatIntToInt.isEmpty()) {
                    boolean Flag = false;
                    for (int i : NowChatIntToInt.keySet()) {
                        if (i == count.get()) {
                            PrintCountMessage(NowChatIntToInt.get(i));
                            Flag = true;
                        }
                    }
                    if (Flag) {
                        NowChatIntToInt.remove(count.get());
                    }
                }

//                if (finalCount == 0) {
//                    return;
//                }else {
//                    PrintCountMessage(count);
//                }
                PrintCountMessage(count.get());

                publicTime = count.get();

//                System.out.println("count : "+count);
                count.set(count.get()-1);
//                count--;

            }
        };


    }

    ScheduledTask foliaTask;


    public ScheduledTask getFoliaTask() {
        return foliaTask;
    }

    public void Start() {
        foliaTask = Bukkit.getAsyncScheduler().runAtFixedRate(main, foliaRunnable, 1, 1, TimeUnit.SECONDS);
        bossBar.removeAll();
    }

    public void Stop() {
        foliaTask.cancel();
    }

    Consumer<ScheduledTask> foliaRunnable;

    public Consumer<ScheduledTask> getFoliaRunnable() {
        return foliaRunnable;
    }


    public void ActionSaveWorldTrashCan(World world, List<Location> locationList) {

        if(world==null){
            return;
        }


        List<ItemStack> itemStackList = WorldToItemStackList.get(world);

        if(itemStackList==null || itemStackList.isEmpty()){
            WorldToItemStackList.remove(world);

            //这里也有可能是代码的最后执行

            return;

        }


        if (locationList != null && !locationList.isEmpty()) {
            //从locationSet取出一个
            Location location = locationList.get(0);
            locationList.remove(0);
            Bukkit.getRegionScheduler().run(main, location, new Consumer<ScheduledTask>() {

                @Override
                public void accept(ScheduledTask scheduledTask) {

                    //添加到世界垃圾桶的逻辑
                    /***************/
//                    World world = location.getWorld();


                    Inventory inventory = getInventory.getInventory(location.getBlock());
                    if (inventory != null) {

                        List<ItemStack> itemStackList = WorldToItemStackList.get(world);

                        if (!itemStackList.isEmpty()) {
                            Iterator<ItemStack> iterator = itemStackList.iterator();
                            while (iterator.hasNext()) {
                                ItemStack itemStack = iterator.next();
                                String itemStackTypeString = itemStack.getType().toString();
                                boolean flag = true;
                                //如果物品不在世界垃圾桶ban表里
                                if (!WorldToLocation.get(world).getBanItemSet().contains(itemStackTypeString)) {
                                    if (inventory.addItem(itemStack).isEmpty()) {
//                                                    System.out.println("加进去了");
                                        flag = false;
                                        itemStackList.remove(itemStack);
                                    }
                                }
                            }
                            WorldToItemStackList.put(world, itemStackList);
                        }

                    }
                    //无法获取到箱子对象的处理
                    else {
                        Set<Location> locationSet1 = WorldToLocation.get(world).getLocationSet();
                        locationSet1.remove(location);
                        data.dataPut(world, locationSet1);
//                                                    consoleSay(ChatColor.RED + "由于没有找到箱子，自动从存储中移除了该" + location.toString() + "位置");
                        String locationString = world.getName() + ": " + location.getBlockX() + "," + location.getBlockY() + "," + location.getBlockZ();
                        consoleSay(message.find("NotFindChest").replace("%location%", locationString));

                    }


                    /***************代码结束/
                     *
                     */
                    ActionSaveWorldTrashCan(world, locationList);


                }
            });
        } else {
            //如果已经存完过一次了，就开始存全球垃圾桶

//            List<ItemStack> itemStackList = WorldToItemStackList.get(world);
            if (itemStackList != null && !itemStackList.isEmpty()) {

                for (ItemStack itemStack : itemStackList) {
                    //如果物品不在全局世界垃圾桶ban表里
                    String itemStackTypeString = itemStack.getType().toString();
                    if (GlobalTrashGuiFlag && !GlobalItemSetString.contains(itemStackTypeString)) {
                        //如果全球垃圾桶开启，且该物品没有被普通的世界垃圾桶回收过
                        for (Inventory inventory : GlobalTrashList) {
                            if (inventory.addItem(itemStack).isEmpty()) {
                                //加进去到公共垃圾桶了
//                                GlobalTrashItemSum++;
                                GlobalTrashItemSum.set(GlobalTrashItemSum.get()+1);
                                break;
                            }

                        }
                    }
                }


            }


            //_______________________这里可能出现代码处于最后阶段




        }

    }


}
