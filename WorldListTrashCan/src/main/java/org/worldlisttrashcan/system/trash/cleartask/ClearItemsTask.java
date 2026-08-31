package org.worldlisttrashcan.system.trash.cleartask;
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
import org.bukkit.scheduler.BukkitRunnable;
import org.worldlisttrashcan.WorldListTrashCan;
import org.worldlisttrashcan.system.trash.ChestGetInventory;
import org.worldlisttrashcan.utils.DataSys;
import org.worldlisttrashcan.utils.EntityCleanMatcher;
import java.util.*;
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
public class ClearItemsTask {
    BukkitRunnable bukkitRunnable;
    List<World> WorldList = new ArrayList<>();
    int finalCount;
    int publicTime = 0;

    /**
     * 物品种类统计信息
     */
    private static class MaterialStats {
        Material material;                  // 物品种类
        int totalAmount;                    // 物品总数量（用于计算原始组数）
        int minDurability;                  // 最小耐久（用于排序）
        int minOrder;                       // 最早加入时间（用于排序）
        List<ItemStack> originalStacks;     // 原始 ItemStack 列表（用于耐久、加入顺序排序）
        int maxStackSize;                   // 最大堆叠数

        // 动态属性（用于削减）
        int newAmount;                      // 调整后的总数量（削减后）
        int newSlots;                       // 调整后的组数（削减后）
    }

    private final boolean enableSorting;
    private final boolean enableReduction;

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
        if (itemMeta != null) {
            itemMeta.getDisplayName();
            for (String customName : NoClearContainerName) {
                if (itemMeta.getDisplayName().contains(customName)) {
                    return true;
                }
            }
        }
        return false;
    }
    public ClearItemsTask(int amount, boolean enableSorting, boolean enableReduction) {
        finalCount = amount;
        boolean BossBarFlag = main.getConfig().getBoolean("Set.BossBarFlag");
        boolean ChatFlag = main.getConfig().getBoolean("Set.ChatFlag");
        String ChatClickCommand = main.getConfig().getString("Set.ChatClickCommand");
        boolean ChatConsoleLogFlag = main.getConfig().getBoolean("Set.ChatConsoleLogFlag");
        boolean SoundFlag = main.getConfig().getBoolean("Set.SoundFlag");
        boolean TitleFlag = main.getConfig().getBoolean("Set.TitleFlag");
        boolean ActionBarFlag = main.getConfig().getBoolean("Set.ActionBarFlag");
        boolean CommandFlag = main.getConfig().getBoolean("Set.CommandFlag");
        this.enableSorting = enableSorting;
        this.enableReduction = enableReduction;

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
        //全部转换为小写
        BlackNameList.replaceAll(String::toLowerCase);
        WhiteNameList.replaceAll(String::toLowerCase);
        EntityCleanMatcher matcher = new EntityCleanMatcher(
                // 白名单
                WhiteNameList,
                // 黑名单
                BlackNameList
        );
        List<String> WorldClearWhiteList = main.getConfig().getStringList("Set.WorldClearWhiteList");
        boolean ClearEntityFlag = main.getConfig().getBoolean("Set.ClearEntity.Flag");
        int bossBarMaxInt;
        bossBarMaxInt = Integer.parseInt(main.getConfig().getStringList("Set.BossBarMessageForCount").get(0).split(";")[0]);
        int EveryClearGlobalTrash = main.getConfig().getInt("Set.GlobalTrash.EveryClearGlobalTrash");


        bukkitRunnable = new BukkitRunnable() {
//            final int finalCount = main.getConfig().getInt("Set.SecondCount");
            int count = finalCount;
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
                        if(!bossBar.getPlayers().contains(player)){
                            //玩家没有bossbar
                            if (player != null) {
                                bossBar.addPlayer(player);
                            }
                        }
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
                    String text = ChatIntToMessage.get(count).replace("%GlobalTrashAddSum%", GlobalTrashItemSum + "")
                            .replace("%DealItemSum%", DealItemSum + "").replace("%EntitySum%", EntitySum + "")
                            .replace("%ClearGlobalCount%", EveryClearGlobalTrash - ClearCount + "");
                    //如果是最后一次播报
                    if(count==0&&(ChatClickCommand!=null&&!ChatClickCommand.isEmpty())){
                        if(logFlag){
                            customLogToFile(text);
                        }
                    }
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        text = papiReplace(text,player);
                        //如果是最后一次播报
                        if(count==0&&(ChatClickCommand!=null&&!ChatClickCommand.isEmpty())){
                            sendChatMessageToAction(player,text,ClickEvent.Action.RUN_COMMAND,ChatClickCommand);
                        }else {
                            player.sendMessage(color(text));
                        }
                    }
                    if (ChatConsoleLogFlag){
                        org.worldlisttrashcan.utils.Message.consoleSay(text);
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

                        // 提前清空公共垃圾桶（如果需要）
                        if (finalCount != 0) {
                            if (ClearCount == EveryClearGlobalTrash) {
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

                        // 收集待加入公共垃圾桶的物品
                        List<ItemStack> globalPendingItems = new ArrayList<>();

                        for (World world : WorldList) {
                            if (!WorldClearWhiteList.isEmpty() && WorldClearWhiteList.contains(world.getName())) {
                                continue;
                            }
                            Set<Location> locationSet = null;
                            if (WorldToLocation.get(world) != null) {
                                locationSet = WorldToLocation.get(world).getLocationSet();
                            }

                            for (Entity entity : world.getEntities()) {
                                if (entity instanceof Player) {
                                    continue;
                                }
                                if (entity instanceof ExperienceOrb) {
                                    if (ClearExpBottle) {
                                        entity.remove();
                                        EntitySum++;
                                        continue;
                                    }
                                }
                                if (entity instanceof Item) {
                                    Item item = (Item) entity;
                                    ItemStack itemStack = item.getItemStack();

                                    // 如果有装rosestacker，进行堆叠物品数量获取，否则不更新真实数量
                                    // 方法见rosestacker的api文档
                                    int realAmount = itemStack.getAmount(); // 默认1
                                    if (WorldListTrashCan.isRoseStackerEnabled()) {
                                        try {
                                            dev.rosewood.rosestacker.api.RoseStackerAPI rsAPI = WorldListTrashCan.getRoseStackerAPI();
                                            if (rsAPI.isItemStacked(item)) {
                                                dev.rosewood.rosestacker.stack.StackedItem stackedItem = rsAPI.getStackedItem(item);
                                                if (stackedItem != null) {
                                                    realAmount = stackedItem.getStackSize();
                                                }
                                            }
                                        } catch (Exception ignored) {}
                                    }

                                    // 由于要按照堆叠组数进行排序，将大于堆叠上限的物品数量拆分为符合原版堆叠上限的多个ItemStack
                                    List<ItemStack> splitStacks = splitToMaxStack(itemStack, realAmount);

                                    // 对每个拆分后的ItemStack执行原有逻辑
                                    for (ItemStack currentStack : splitStacks) {
                                        // 不处理带有关键lore的物品
                                        if (NoClearItemFlag(currentStack)) {
                                            continue;
                                        }

                                        String itemStackTypeString = currentStack.getType().toString();
                                        boolean flag = true; // 是否已被世界/个人垃圾桶处理

                                        // 1. 世界垃圾桶处理
                                        if (locationSet != null && !locationSet.isEmpty()) {
                                            if (!WorldToLocation.get(world).getBanItemSet().contains(itemStackTypeString)) {
                                                for (Location location : locationSet) {
                                                    Inventory inventory = ChestGetInventory.getInventory(location.getBlock());
                                                    if (inventory != null) {
                                                        if (inventory.addItem(currentStack).isEmpty()) {
                                                            flag = false;
                                                            break;
                                                        }
                                                    } else {
                                                        Set<Location> locationSet1 = WorldToLocation.get(world).getLocationSet();
                                                        locationSet1.remove(location);
                                                        DataSys.dataPut(world, locationSet1);
                                                        String locationString = world.getName() + ": " + location.getBlockX() + "," + location.getBlockY() + "," + location.getBlockZ();
                                                        consoleSay(org.worldlisttrashcan.utils.Message.find("NotFindChest").replace("%location%", locationString));
                                                    }
                                                }
                                            }
                                        }

                                        // 2. 个人垃圾桶处理
                                        if (flag && VersionFlag) {
                                            ItemMeta meta = currentStack.getItemMeta();
                                            if (meta != null) {
                                                NamespacedKey namespacedKey = new NamespacedKey(main, "PlayerUUID");
                                                String PlayerUUID = meta.getPersistentDataContainer().get(namespacedKey, PersistentDataType.STRING);
                                                if (PlayerUUID != null) {
                                                    Player player = Bukkit.getPlayer(UUID.fromString(PlayerUUID));
                                                    if (player != null) {
                                                        Inventory inventory = PlayerToInventory.get(player);
                                                        if (inventory == null) {
                                                            PlayerToInventory.put(player, InitPlayerInv(player));
                                                            inventory = PlayerToInventory.get(player);
                                                        }
                                                        RemoveItemTag(currentStack);
                                                        if (inventory.addItem(currentStack).isEmpty()) {
                                                            flag = false;
                                                        } else {
                                                            if (main.getConfig().getBoolean("Set.PersonalTrashCan.OriginalFeatureClearItemAddGlobalTrash.Model2.AutoClear")) {
                                                                inventory.clear();
                                                                player.sendMessage(org.worldlisttrashcan.utils.Message.find("PlayerTrashCanFilled"));
                                                                if (inventory.addItem(currentStack).isEmpty()) {
                                                                    flag = false;
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }

                                        // 3. 公共垃圾桶处理（收集）
                                        if (flag && GlobalTrashGuiFlag && !GlobalItemSetString.contains(itemStackTypeString)) {
                                            globalPendingItems.add(currentStack.clone());
                                        }
                                        // 累加处理物品数量（按实际物品数量，用于DealItemSum）
                                        DealItemSum += currentStack.getAmount();
                                    }
                                    // 实体计数：每个Item实体增加1（无论拆分多少）
                                    GlobalTrashItemSum++;
                                    // 移除原实体
                                    item.remove();
                                } else {
                                    // 清理其他实体
                                    if (ClearEntityFlag) {
                                        if (IgnoreEntitiesInBoat && entity.isInsideVehicle()) {
                                            Entity vehicle = entity.getVehicle();
                                            if (vehicle instanceof Boat) {
                                                continue;
                                            }
                                        }
                                        Boolean checkClean = matcher.checkClean(entity.getType().toString(), entity.getName());
                                        if (checkClean != null) {
                                            if (checkClean) {
                                                entity.remove();
                                                EntitySum++;
                                            }
                                            continue;
                                        }
                                        try {
                                            if (!ClearReNameEntity && entity.getCustomName() != null && !entity.getCustomName().isEmpty()) {
                                                continue;
                                            }
                                        } catch (Exception ignored) {}
                                        if (entity instanceof LivingEntity) {
                                            if (ClearMonster && isMonster(entity)) {
                                                entity.remove();
                                                EntitySum++;
                                            } else {
                                                if (ClearAnimals) {
                                                    entity.remove();
                                                    EntitySum++;
                                                }
                                            }
                                        } else if (entity instanceof Projectile) {
                                            if (ClearProjectile) {
                                                entity.remove();
                                                EntitySum++;
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // ========== 统一处理公共垃圾桶 ==========
                        if (!globalPendingItems.isEmpty()) {

                            List<ItemStack> finalItems;
                            if (enableSorting) {
                                // 1. 分组排序
                                List<MaterialStats> statsList = groupAndSort(globalPendingItems);
                                // 2. 获取空闲格子数
                                int freeSlots = getFreeSlots();
                                // 3. 溢出削减
                                if (enableReduction) {
                                    reduceIfNeeded(statsList, freeSlots);
                                } else {
                                    for (MaterialStats stats : statsList) {
                                        if (stats.newAmount == 0) {
                                            stats.newAmount = stats.totalAmount;
                                            stats.newSlots = (stats.totalAmount + stats.maxStackSize - 1) / stats.maxStackSize;
                                        }
                                    }
                                }
                                // 4. 重建物品列表
                                finalItems = rebuildItems(statsList);
                            } else {
                                // 若不启用排序，则直接使用收集到的物品列表
                                finalItems = new ArrayList<>(globalPendingItems);
                            }

                            // 5. 添加到公共垃圾桶
                            for (ItemStack item : finalItems) {
                                for (Inventory inv : GlobalTrashList) {
                                    Map<Integer, ItemStack> remaining = inv.addItem(item);
                                    if (remaining.isEmpty()) {
                                        break;
                                    } else {
                                        ItemStack leftover = remaining.get(0);
                                        if (leftover.getAmount() == 0) {
                                            break;
                                        }
                                        item = leftover;
                                    }
                                }
                            }
                        }

                    } catch (Exception e) {
                        consoleSay(ChatColor.RED + "该服务器环境似乎不兼容此插件的某些功能，请将报错截图发送至作者QQ 2831508831");
                        throw e;
                    } finally {
                        PrintCountMessage(count);
                        count = finalCount;
                    }
                }
                if (finalCount == 0) {
                    return;
                } else {
                    PrintCountMessage(count);
                }
                publicTime = count;
                count--;
            }
        };
    }

    /**
     * 将总数量拆分为符合原版堆叠上限的多个ItemStack
     * @param original 原始ItemStack（用于克隆）
     * @param totalAmount 实际总数量
     * @return 拆分后的ItemStack列表
     */
    private List<ItemStack> splitToMaxStack(ItemStack original, int totalAmount) {
        List<ItemStack> result = new ArrayList<>();
        int maxStack = original.getType().getMaxStackSize();
        int remaining = totalAmount;
        while (remaining > 0) {
            int chunk = Math.min(remaining, maxStack);
            ItemStack copy = original.clone();
            copy.setAmount(chunk);
            result.add(copy);
            remaining -= chunk;
        }
        return result;
    }

    /**
     * 获取物品耐久值（无耐久物品返回0）
     */
    private int getDurability(ItemStack item) {
        if (item.getType().getMaxDurability() == 0) return 0;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return 0;
        if (meta instanceof org.bukkit.inventory.meta.Damageable) {
            return ((org.bukkit.inventory.meta.Damageable) meta).getDamage();
        }
        return 0;
    }

    /**
     * 计算公共垃圾桶当前空闲格子总数
     */
    private int getFreeSlots() {
        int free = 0;
        for (Inventory inv : GlobalTrashList) {
            int size = inv.getSize();
            int occupied = 0;
            for (ItemStack item : inv.getContents()) {
                if (item != null && !item.getType().isAir()) {
                    occupied++;
                }
            }
            free += (size - occupied);
        }
        return free;
    }

    /**
     * 将物品列表按物品种类分组、统计，并排序（按组数升序、耐久升序、加入时间从小到大排序）
     */
    private List<MaterialStats> groupAndSort(List<ItemStack> items) {
        Map<Material, MaterialStats> map = new HashMap<>();
        int order = 0;
        for (ItemStack is : items) {
            Material mat = is.getType();
            MaterialStats stats = map.get(mat);
            if (stats == null) {
                stats = new MaterialStats();
                stats.material = mat;
                stats.totalAmount = 0;
                stats.minDurability = Integer.MAX_VALUE;
                stats.minOrder = Integer.MAX_VALUE;
                stats.originalStacks = new ArrayList<>();
                stats.maxStackSize = mat.getMaxStackSize();
                map.put(mat, stats);
            }
            stats.totalAmount += is.getAmount();
            int dur = getDurability(is);
            if (dur < stats.minDurability) stats.minDurability = dur;
            if (order < stats.minOrder) stats.minOrder = order;
            stats.originalStacks.add(is.clone());
            order++;
        }

        // 对每个种类的原始列表排序（耐久升序、加入顺序升序）
        for (MaterialStats stats : map.values()) {
            stats.originalStacks.sort((a, b) -> {
                return Integer.compare(getDurability(a), getDurability(b));
                // 注意：order信息已丢失，可忽略或扩展字段
            });
        }

        List<MaterialStats> list = new ArrayList<>(map.values());
        // 排序：按堆叠数升序 → 总数量升序 → 耐久升序 → 加入时间升序
        list.sort((a, b) -> {
            int slotsA = (a.totalAmount + a.maxStackSize - 1) / a.maxStackSize;
            int slotsB = (b.totalAmount + b.maxStackSize - 1) / b.maxStackSize;
            int cmp = Integer.compare(slotsA, slotsB);
            if (cmp != 0) return cmp;
            cmp = Integer.compare(a.totalAmount, b.totalAmount);
            if (cmp != 0) return cmp;
            cmp = Integer.compare(a.minDurability, b.minDurability);
            if (cmp != 0) return cmp;
            return Integer.compare(a.minOrder, b.minOrder);
        });
        return list;
    }

    /**
     * 溢出削减算法（基于组数，从右侧开始削减差值的一半）
     */
    private void reduceIfNeeded(List<MaterialStats> statsList, int freeSlots) {
        // 获取原始组数
        int[] slots = new int[statsList.size()];
        for (int i = 0; i < statsList.size(); i++) {
            slots[i] = (statsList.get(i).totalAmount + statsList.get(i).maxStackSize - 1) / statsList.get(i).maxStackSize;
        }

        int totalSlots = Arrays.stream(slots).sum();
        if (totalSlots <= freeSlots) {
            for (int i = 0; i < statsList.size(); i++) {
                statsList.get(i).newAmount = statsList.get(i).totalAmount;
                statsList.get(i).newSlots = slots[i];
            }
            return;
        }

        boolean changed;
        do {
            changed = false;
            for (int i = slots.length - 1; i >= 1; i--) {
                int diff = slots[i] - slots[i-1];
                if (diff > 0) {
                    int reduce = diff / 2;
                    if (reduce > 0) {
                        // 避免削减后组数小于 1
                        int newSlots = slots[i] - reduce;
                        if (newSlots < 1 && slots[i] > 0) newSlots = 1; // 保留至少一组
                        if (newSlots != slots[i]) {
                            slots[i] = newSlots;
                            changed = true;
                            totalSlots = Arrays.stream(slots).sum();
                            if (totalSlots <= freeSlots) break;
                        }
                    }
                }
            }
        } while (changed && totalSlots > freeSlots);

        for (int i = 0; i < statsList.size(); i++) {
            MaterialStats stats = statsList.get(i);
            stats.newSlots = slots[i];
            stats.newAmount = Math.min(stats.totalAmount, slots[i] * stats.maxStackSize);
        }
    }

    /**
     * 根据调整后的数量重建物品列表（按种类排序，并保持耐久低、加入早的物品优先）
     */
    private List<ItemStack> rebuildItems(List<MaterialStats> statsList) {
        List<ItemStack> result = new ArrayList<>();
        for (MaterialStats stats : statsList) {
            int targetAmount = stats.newAmount;
            if (targetAmount <= 0) continue;
            int remaining = targetAmount;
            // 从原始列表中依次取出物品，优先取耐久低、加入早的
            for (ItemStack original : stats.originalStacks) {
                if (remaining <= 0) break;
                int amount = original.getAmount();
                if (amount <= remaining) {
                    result.add(original.clone());
                    remaining -= amount;
                } else {
                    ItemStack split = original.clone();
                    split.setAmount(remaining);
                    result.add(split);
                    remaining = 0;
                }
            }
            // 如果原始列表不够（理论不应发生），则创建新物品
            if (remaining > 0) {
                ItemStack newStack = new ItemStack(stats.material, remaining);
                result.add(newStack);
            }
        }
        return result;
    }

    public void Start(){
        bukkitRunnable.runTaskTimer(main,20L,20L);
        bossBar.removeAll();
    }
    public void Stop(){
        if (bukkitRunnable.isCancelled()) {
            return;
        }
        bukkitRunnable.cancel();
    }
}
