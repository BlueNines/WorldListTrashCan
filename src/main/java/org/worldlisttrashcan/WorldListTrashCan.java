package org.worldlisttrashcan;

import io.papermc.paper.event.entity.EntityMoveEvent;
import io.papermc.paper.threadedregions.scheduler.EntityScheduler;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.checkerframework.checker.units.qual.A;
import org.worldlisttrashcan.AutoTrashMain.AutoTrashListener;
import org.worldlisttrashcan.DropSystem.DropLimitListener;
import org.worldlisttrashcan.SimpleChange.NotPickArrowListener;
import org.worldlisttrashcan.SpeakSystem.QuickSpeakListener;
import org.worldlisttrashcan.SpeakSystem.QuickUseCommandListener;
import org.worldlisttrashcan.SimpleChange.TreadingFarmLandListener;
import org.worldlisttrashcan.TrashMain.*;
import org.worldlisttrashcan.WorldLimitEntityCount.BukkitClearGatherEntityTask;
//import org.worldlisttrashcan.WorldLimitEntityCount.BukkitEntityMoveEvent;
import org.worldlisttrashcan.WorldLimitEntityCount.LimitMain;
import org.worldlisttrashcan.WorldLimitEntityCount.PaperEntityMoveEvent;
//import org.worldlisttrashcan.WorldLimitEntityCount.PaperEntityMoveEvent;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static org.worldlisttrashcan.AutoTrashMain.AutoTrashListener.OriginalFeatureClearItemAddGlobalTrashModel;
import static org.worldlisttrashcan.DropSystem.DropLimitListener.PlayerDropList;
import static org.worldlisttrashcan.IsVersion.*;
import static org.worldlisttrashcan.TrashMain.TrashListener.GlobalItemSetString;
import static org.worldlisttrashcan.WorldLimitEntityCount.LimitMain.*;
import static org.worldlisttrashcan.WorldLimitEntityCount.removeEntity.ItemDropFlag;
import static org.worldlisttrashcan.message.AllMessageLoad;
import static org.worldlisttrashcan.message.chanceMessage;

public final class WorldListTrashCan extends JavaPlugin {

    public static Plugin main;
    public static WorldListTrashCan worldListTrashCan;

    ClearItemsTask clearItemsTask;
    FoliaClearItemsTask foliaClearItemsTask;
    public static GlobalTrashGui globalTrashGui;


//    public static GlobalTrashGui globalTrashGui;



    //公共垃圾桶
    public static List<Inventory> GlobalTrashList = new ArrayList<>();



    //tab补全专项代码
    //commands:
    //  WorldListTrashCan:
    //    description: WorldListTrashCan插件主指令
    //  WTC:
    //    description: WorldListTrashCan插件副指令
    //permissions:
    //  WorldListTrashCan.BanGui:
    //    description: 将不回收的物品加入，如果重复加入同一物品则是取消不回收该物品
    //    default: true
    //  WorldListTrashCan.GlobalTrashOpen:
    //    description: 打开全球垃圾桶
    //    default: true
    //  WorldListTrashCan.help:
    //    description: 帮助
    //    default: true
    //  WorldListTrashCan.GlobalBan:
    //    description: 全球垃圾桶黑名单gui
    //    default: false
    //  WorldListTrashCan.Look:
    //    description: 点击实体获取实体类型
    //    default: false
    //  WorldListTrashCan.DropMode:
    //    description: 打开禁止丢弃模式
    //    default: true
    //  WorldListTrashCan.PlayerTrash:
    //    description: 玩家打开个人垃圾桶的权限
    //    default: true



    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        List<String> allSubCommands = Arrays.asList("PlayerTrash","DropMode","look","GlobalBan","help","reload","GlobalTrash","ban","add");
        if (command.getName().equalsIgnoreCase("WorldListTrashCan")||command.getName().equalsIgnoreCase("wtc")) {
            if (args.length == 1) {
                // 如果输入的是第一个参数，提供一些补全建议
                for (String subCommand : allSubCommands) {
                    completions.add(subCommand.toLowerCase());
                }
//                completions.add("PlayerTrash");
//                completions.add("DropMode");
//                completions.add("Look");
//                completions.add("GlobalBan");
//                completions.add("help");
//                completions.add("GlobalTrashOpen");
//                completions.add("BanGui");
            }
//            else if (args.length == 2) {
//                System.out.println("1");
//                String partial = args[0].toLowerCase();
//                for (String subCommand : allSubCommands) {
//                    if (subCommand.startsWith(partial)) {
//                        completions.add(subCommand);
//                    }
//                }
//            }



//                    String partial = args[0].toLowerCase();
//                    // 根据输入的部分提供匹配的命令
//                    if ("help".startsWith(partial)) {
//                        completions.add("help");
//                    }


        }

        return completions;
    }




    //这个是 物品碰到仙人掌，碰到岩浆，虚无，而消失的情况的垃圾桶
    //应群友要求，我本来想把以上情况写到公共垃圾桶里的
//    public static List<Inventory> RemoveGlobalTrashList = new ArrayList<>();



    static public boolean EconomyFlag = false;

    static public Map<World, RashCanInformation> WorldToLocation = new HashMap<>();
    static public List<Player> UseEntityBarPlayerList = new ArrayList<>();
//    LimitMain limitMain = new LimitMain();
    @Override
    public void onEnable() {



        // Plugin startup logic
        main = this;
        worldListTrashCan = this;
        Bukkit.getPluginManager().registerEvents(new TrashListener(), this);
        Bukkit.getPluginManager().registerEvents(new GuiListener(), this);
        IsFoliaSever = Bukkit.getServer().getVersion().contains("Folia");
//        System.out.println("Bukkit.getServer().getVersion().contains "+Bukkit.getServer().getVersion());
        IsPaperSever = Bukkit.getServer().getVersion().contains("Paper");


        if (!setupEconomy() ) {
            getLogger().severe(String.format("[%s] - 没有找到Vault插件，已自动关闭相关功能", getDescription().getName()));
//            getServer().getPluginManager().disablePlugin(this);
//            return;
        }else {
            EconomyFlag = true;
        }



        reload();
//        Bukkit.getPluginManager().registerEvents(new LimitMain(), this);
        LimitMain limitMain = new LimitMain();
        Bukkit.getPluginManager().registerEvents(limitMain, this);


        //警告：folia服务器只能用上面的，不能用bukkit延时任务来检查密集实体
        if((IsPaperSever&&!compareVersions("1.13.0"))||IsFoliaSever){
//            System.out.println("1");
            Bukkit.getPluginManager().registerEvents(new PaperEntityMoveEvent(), this);
        }else {
//            System.out.println("2");
            if(bukkitClearGatherEntityTask!=null){
                bukkitClearGatherEntityTask.Stop();
            }

            bukkitClearGatherEntityTask = new BukkitClearGatherEntityTask();
//            Bukkit.getPluginManager().registerEvents(bukkitClearGatherEntityTask, this);
            bukkitClearGatherEntityTask.Start();
        }







    }

//    static public AutoTrashListener autoTrashListener;

    static public BukkitClearGatherEntityTask bukkitClearGatherEntityTask;

    public static Map<Player,World> PlayerToWorld = new HashMap<>();


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
//        System.out.println(command.toString()+"："+args.toString());
        // 检查是否是管理员 (OP) 使用命令
        if (command.getName().equalsIgnoreCase("WorldListTrashCan")||command.getName().equalsIgnoreCase("WTC")) {
            if (args.length == 0) {
                sender.sendMessage(ChatColor.GREEN + "WorldListTrashCan " + ChatColor.BLUE + "作者QQ：2831508831");
                sender.sendMessage(ChatColor.YELLOW + "/WorldListTrashCan help" + ChatColor.BLUE + "帮助");
            } else if (args[0].equalsIgnoreCase("reload")) {
//                getLogger().info("发送指令者是: "+sender.getName());
//                getLogger().info("是否有权限: "+sender.hasPermission("WorldListTrashCan.reload"));

                if (sender.isOp()) {
                    reload();
                    sender.sendMessage(message.find("ReloadInformation"));
                }
            } else if (args[0].equalsIgnoreCase("help")) {
//                sender.sendMessage("§0——————§7【§bWorldListTrashCan§7】§0——————");
//                sender.sendMessage("§6/WorldListTrashCan help"+"§d查看帮助");
//                sender.sendMessage("§6/WorldListTrashCan ban"+"§d打开本世界垃圾黑名单Gui");
//                sender.sendMessage("§6/WorldListTrashCan GlobalBan"+"§d打开全局世界垃圾黑名单Gui");
//                sender.sendMessage("§6/WorldListTrashCan add [世界名] <数量>"+"§d设置世界名垃圾桶最大数量(不填则为脚下世界)");
                if(sender.hasPermission("WorldListTrashCan.help")||sender.isOp()){
                    for (String string : message.getConfig().getStringList("HelpTitle")) {
                        sender.sendMessage(string.replace("&","§"));
                    }
                }else {
                    sender.sendMessage(message.find("NotHavePermission").replace("%permission%","WorldListTrashCan.help"));
                }

//                sender.sendMessage("§6/WorldListTrashCan "+"§d");
//                getLogger().info("发送指令者是: "+sender.getName());
//                getLogger().info("是否有权限: "+sender.hasPermission("WorldListTrashCan.reload"));

//                if (sender instanceof Player) {
//                    if (sender.isOp()) {
//                        Player player = ((Player) sender).getPlayer();
//                        ItemStack itemStack = player.getInventory().getItemInMainHand();
//                        sender.sendMessage(ChatColor.GREEN + "你手上拿的是：" + itemStack.getType().toString());
//                    }
//                } else {
//                    sender.sendMessage(ChatColor.RED + "只有玩家能够使用这个命令");
//                }
            } else if (args[0].equalsIgnoreCase("Ban")) {
//                getLogger().info("发送指令者是: "+sender.getName());
//                getLogger().info("是否有权限: "+sender.hasPermission("WorldListTrashCan.reload"));

                if (sender instanceof Player) {
                    if (sender.hasPermission("WorldListTrashCan.BanGui")||sender.isOp()) {
                        Player player = ((Player) sender).getPlayer();

                        PlayerToWorld.put(player,player.getWorld());
                        sender.sendMessage(message.find("SecondCountdown"));
                        if(IsFoliaSever){
                            ScheduledTask task = player.getScheduler().runDelayed(main, new Consumer<ScheduledTask>() {
//                                int count = 0;

                                @Override
                                public void accept(ScheduledTask scheduledTask) {
//                                    if(count>3){
                                        if(PlayerToWorld.get(player)!=null){
                                            PlayerToWorld.remove(player);
                                            sender.sendMessage(message.find("SecondCountdownEnd"));
                                        }else {

                                        }
                                        //stop
//                                    }

//                                    count++;
                                }
                            }, () -> main.getLogger().info("Error,Player is null"),60L);
                        }else {
                            new BukkitRunnable(){

                                int count = 0;
                                @Override
                                public void run() {
                                    if(count>3){
                                        if(PlayerToWorld.get(player)!=null){
                                            PlayerToWorld.remove(player);
                                            sender.sendMessage(message.find("SecondCountdownEnd"));
                                        }else {

                                        }
                                        cancel();
                                    }

                                    count++;
                                }
                            }.runTaskTimer(this,0L,60L);
                        }
//                        Inventory inventory = (new BanGui()).getInventory(player);
//                        player.openInventory(inventory);
//                        for (ItemStack itemStack : inventory) {
//                            if(itemStack!=null){
//                                player.sendMessage(ChatColor.BLUE+"物品："+itemStack.getType());
//                            }
//                        }
                    }else {
                        sender.sendMessage(message.find("NotHavePermission").replace("%permission%","WorldListTrashCan.BanGui"));
                    }
                } else {
                    sender.sendMessage(message.find("NotIsPlayer"));
                }
            } else if (args[0].equalsIgnoreCase("GlobalBan")) {
//                getLogger().info("发送指令者是: "+sender.getName());
//                getLogger().info("是否有权限: "+sender.hasPermission("WorldListTrashCan.reload"));

                if (sender instanceof Player) {
                    if (sender.hasPermission("WorldListTrashCan.GlobalBan")||sender.isOp()) {
                        Player player = ((Player) sender).getPlayer();
                        Inventory inventory = (new BanGui()).getGlobalInventory(player);
                        player.openInventory(inventory);
//                        for (ItemStack itemStack : inventory) {
//                            if(itemStack!=null){
//                                player.sendMessage(ChatColor.BLUE+"物品："+itemStack.getType());
//                            }
//                        }
                    }else {
                        sender.sendMessage(message.find("NotHavePermission").replace("%permission%","WorldListTrashCan.GlobalBan"));
                    }
                } else {
                    sender.sendMessage(message.find("NotIsPlayer"));
                }
            }else if (args[0].equalsIgnoreCase("PlayerTrash")) {
//                getLogger().info("发送指令者是: "+sender.getName());
//                getLogger().info("是否有权限: "+sender.hasPermission("WorldListTrashCan.reload"));

                if (sender instanceof Player) {
                    if (sender.hasPermission("WorldListTrashCan.PlayerTrash")||sender.isOp()) {
                        Player player = ((Player) sender).getPlayer();
                        Inventory inventory = autoTrashListener.getPlayerToInventory().get(player);
                        if(inventory==null){
                            //PlayerToInventory.put(player, Bukkit.createInventory((InventoryHolder) this, 54, message.find("PlayerTrashCan").replace("%Player%",player.getName())));
                            //
                            inventory = autoTrashListener.InitPlayerInv(player);
                            autoTrashListener.getPlayerToInventory().put(player,inventory);
                        }
                        player.openInventory(inventory);
//                        for (ItemStack itemStack : inventory) {
//                            if(itemStack!=null){
//                                player.sendMessage(ChatColor.BLUE+"物品："+itemStack.getType());
//                            }
//                        }
                    }else {
                        sender.sendMessage(message.find("NotHavePermission").replace("%permission%","WorldListTrashCan.PlayerTrash"));
                    }
                } else {
                    sender.sendMessage(message.find("NotIsPlayer"));
                }
            }else if (args[0].equalsIgnoreCase("GlobalTrash")||args[0].equalsIgnoreCase("Trash")) {
//                getLogger().info("发送指令者是: "+sender.getName());
//                getLogger().info("是否有权限: "+sender.hasPermission("WorldListTrashCan.reload"));

                if (sender instanceof Player) {
                    if (sender.hasPermission("WorldListTrashCan.GlobalTrashOpen")||sender.isOp()) {
                        Player player = ((Player) sender).getPlayer();
                        Inventory inventory = globalTrashGui.getInventory();
                        player.openInventory(inventory);
                    }else {
                        sender.sendMessage(message.find("NotHavePermission").replace("%permission%","WorldListTrashCan.GlobalTrashOpen"));
                    }
                } else {
                    sender.sendMessage(message.find("NotIsPlayer"));
                }
            }else if (args[0].equalsIgnoreCase("DropMode")) {
//                getLogger().info("发送指令者是: "+sender.getName());
//                getLogger().info("是否有权限: "+sender.hasPermission("WorldListTrashCan.reload"));
                if(!getConfig().getBoolean("DropItemCheck.Flag")){
                    return true;
                }

                if (sender instanceof Player) {
                    if (sender.hasPermission("WorldListTrashCan.GlobalTrashOpen")||sender.isOp()) {
                        Player player = ((Player) sender).getPlayer();
                        //处理玩家
                        if(PlayerDropList.contains(player)){
                            PlayerDropList.remove(player);
                            player.sendMessage(message.find("OffDropMode"));
                        }else {
                            PlayerDropList.add(player);
                            player.sendMessage(message.find("OpenDropMode"));
                        }

                    }else {
                        sender.sendMessage(message.find("NotHavePermission").replace("%permission%","WorldListTrashCan.DropMode"));
                    }
                } else {
                    sender.sendMessage(message.find("NotIsPlayer"));
                }
            }else if (args[0].equalsIgnoreCase("look")) {
//                getLogger().info("发送指令者是: "+sender.getName());
//                getLogger().info("是否有权限: "+sender.hasPermission("WorldListTrashCan.reload"));

                if (sender instanceof Player) {
                    if (sender.hasPermission("WorldListTrashCan.Look")||sender.isOp()) {
                        Player player = ((Player) sender).getPlayer();
                        UseEntityBarPlayerList.add(player);
                        player.sendMessage(message.find("PleaseRightEntity"));
                    }else {
                        sender.sendMessage(message.find("NotHavePermission").replace("%permission%","WorldListTrashCan.Look"));
                    }
                } else {
                    sender.sendMessage(message.find("NotIsPlayer"));
                }
            }
//            else if (args[0].equalsIgnoreCase("action")) {
////                getLogger().info("发送指令者是: "+sender.getName());
////                getLogger().info("是否有权限: "+sender.hasPermission("WorldListTrashCan.reload"));
//
//                    if (sender instanceof Player) {
//                        if (sender.hasPermission("WorldListTrashCan.Look")||sender.isOp()) {
//                            Player player = ((Player) sender).getPlayer();
//                            Chunk chunk = player.getChunk();
//                            for (Entity entity : chunk.getEntities()) {
//                                if(entity instanceof Player){
//                                    continue;
//                                }
//                                player.sendMessage("entity type "+entity.getType());
//                                entity.remove();
//                            }
//                        }else {
//                            sender.sendMessage(message.find("NotHavePermission").replace("%permission%","WorldListTrashCan.Look"));
//                        }
//                    } else {
//                        sender.sendMessage(message.find("NotIsPlayer"));
//                    }
//            }
            else if (args[0].equalsIgnoreCase("add")) {
//                getLogger().info("发送指令者是: "+sender.getName());
//                getLogger().info("是否有权限: "+sender.hasPermission("WorldListTrashCan.reload"));
//                data.getConfig().getInt("WorldData." + world.getName() + ".RashMaxCount")


                if (args.length > 2) {
                    if (sender.isOp()) {
                        World world = Bukkit.getWorld(args[1]);
                        if (world != null) {
                            try {

                                int inputCount = Integer.parseInt(args[2]);

                                int count = data.RashMaxCountAdd(world, inputCount);
                                sender.sendMessage(message.find("AddRashMaxCountTrue")
                                        .replace("%world%", world.getName())
                                        .replace("%count%", count+""));

                            } catch (Exception e) {
                                //不是整数
                                sender.sendMessage(message.find("NotInt"));
                            }
                        } else {
                            sender.sendMessage(message.find("NotFindWorld"));

                        }
                    } else {
                        sender.sendMessage(message.find("NotIsOp"));
                    }
                } else if (args.length > 1) {
                    if (sender instanceof Player) {
                        Player player = ((Player) sender).getPlayer();
                        World world = player.getWorld();
                        try {
//                                System.out.println("1");
                            int inputCount = Integer.parseInt(args[1]);

                            int count = data.RashMaxCountAdd(world, inputCount);
                            sender.sendMessage(message.find("AddRashMaxCountTrue")
                                    .replace("%world%", world.getName())
                                    .replace("%count%", count + ""));


                        } catch (Exception e) {
                            //不是整数
                            sender.sendMessage(message.find("NotInt"));
                        }
                    } else {
//                        sender.sendMessage(ChatColor.RED + "只有玩家能够不写世界名字这个参数来使用这个命令");
                        sender.sendMessage(message.find("NotInputArgAdd"));
                        sender.sendMessage(message.find("WorldListTrashCanAdd"));
                    }
                } else {
                    sender.sendMessage(message.find("WorldListTrashCanAdd"));
                }


            } else {
                sender.sendMessage(ChatColor.GREEN + "WorldListTrashCan " + ChatColor.BLUE + "作者QQ：2831508831");
//                sender.sendMessage(ChatColor.YELLOW + "/WorldListTrashCan reload" + ChatColor.BLUE + "重载插件");
                sender.sendMessage(ChatColor.YELLOW + "/WorldListTrashCan help" + ChatColor.BLUE + "插件帮助");
            }
        }
        return true;
    }
    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static void ConfigStringReplace(ConfigurationSection config, String target, String replacement) {
        for (String key : config.getKeys(false)) {
            if (config.isConfigurationSection(key)) {
                // 递归处理子节
                ConfigStringReplace(config.getConfigurationSection(key), target, replacement);
            } else if (config.isString(key)) {
                // 替换字符串值
                String originalValue = config.getString(key);
                String modifiedValue = originalValue.replace(target, replacement);
                config.set(key, modifiedValue);
            }
        }
    }



    public static boolean GlobalTrashGuiFlag = false;
//    static public data data;

    public static List<String> NoClearContainerLore = new ArrayList<>();
    public static List<String> NoClearContainerType = new ArrayList<>();
    public void reload(){
//        reloadConfig();

        saveDefaultConfig();
        reloadConfig();
        ConfigStringReplace(getConfig(),"&","§");

        AllMessageLoad();
        chanceMessage = getConfig().getString("Set.Lang");
        message.reloadMessage();

        data.LoadData();
//        AllMessageLoad();
//        chanceMessage = getConfig().getString("Set.Lang");
//        message.reloadMessage();
        GlobalItemSetString = new HashSet<>(main.getConfig().getStringList("GlobalBanItem"));
        int MaxCount = main.getConfig().getInt("Set.GlobalTrash.MaxPage");
        globalTrashGui = new GlobalTrashGui(GlobalTrashList,MaxCount);







        if(IsFoliaSever){
            if(foliaClearItemsTask!=null){
                foliaClearItemsTask.Stop();
            }
            foliaClearItemsTask = new FoliaClearItemsTask();
            foliaClearItemsTask.Start();
        }else {
            if(clearItemsTask!=null){
                clearItemsTask.Stop();
            }
            clearItemsTask = new ClearItemsTask();
            clearItemsTask.Start();
        }



        GlobalTrashGuiFlag = getConfig().getBoolean("Set.GlobalTrash.Flag");




        if(getConfig().getBoolean("SimpleOptimize.NotPickArrow")){
            HandlerList.unregisterAll(notPickArrowListener);
            Bukkit.getPluginManager().registerEvents(notPickArrowListener, this);
        }else {
            HandlerList.unregisterAll(notPickArrowListener);
        }

        if(getConfig().getBoolean("SimpleOptimize.NotTreadingFarmLand")){
            HandlerList.unregisterAll(treadingFarmLandListener);
            Bukkit.getPluginManager().registerEvents(treadingFarmLandListener, this);
        }else {
            HandlerList.unregisterAll(treadingFarmLandListener);
        }




//        if(getConfig().getBoolean("WorldEntityLimitCount.Flag")){
//        HandlerList.unregisterAll(limitMain);

//        }else {
//            HandlerList.unregisterAll(limitMain);
//        }

        if(getConfig().getBoolean("ChatSet.QuickSendMessage.Flag")){
            HandlerList.unregisterAll(quickSpeakListener);
            quickSpeakListener.Init();
            Bukkit.getPluginManager().registerEvents(quickSpeakListener, this);
        }else {
            HandlerList.unregisterAll(quickSpeakListener);
        }

        if(getConfig().getBoolean("ChatSet.QuickUseCommand.Flag")){
            HandlerList.unregisterAll(quickUseCommandListener);
            quickUseCommandListener.Init();
            Bukkit.getPluginManager().registerEvents(quickUseCommandListener, this);
        }else {
            HandlerList.unregisterAll(quickUseCommandListener);
        }


        if(getConfig().getBoolean("DropItemCheck.Flag")){
            HandlerList.unregisterAll(dropLimitListener);
            Bukkit.getPluginManager().registerEvents(dropLimitListener, this);
        }else {
            HandlerList.unregisterAll(dropLimitListener);
        }



        OriginalFeatureClearItemAddGlobalTrashModel =main.getConfig().getInt("Set.GlobalTrash.OriginalFeatureClearItemAddGlobalTrash.UseModel");
        if(getConfig().getInt("Set.GlobalTrash.OriginalFeatureClearItemAddGlobalTrash.UseModel")!=3){
            HandlerList.unregisterAll(autoTrashListener);
//            int Model = main.getConfig().getInt("Set.GlobalTrash.OriginalFeatureClearItemAddGlobalTrash.UseModel");
////            System.out.println("Model "+Model);
//            autoTrashListener.setOriginalFeatureClearItemAddGlobalTrashModel(Model);

            Bukkit.getPluginManager().registerEvents(autoTrashListener, this);
        }else {
            HandlerList.unregisterAll(autoTrashListener);
        }

        autoTrashListener = new AutoTrashListener();
        Bukkit.getPluginManager().registerEvents(autoTrashListener, this);

//        TreadingFarmLandListener
        LoadWorldLimitEntityConfig();
        NoClearContainerLore = main.getConfig().getStringList("Set.NoClearContainerLore");
        NoClearContainerType = main.getConfig().getStringList("Set.NoClearContainerType");
    }

    AutoTrashListener autoTrashListener = new AutoTrashListener();
    DropLimitListener dropLimitListener = new DropLimitListener();
    NotPickArrowListener notPickArrowListener = new NotPickArrowListener();
    TreadingFarmLandListener treadingFarmLandListener = new TreadingFarmLandListener();
    QuickSpeakListener quickSpeakListener = new QuickSpeakListener();
    QuickUseCommandListener quickUseCommandListener = new QuickUseCommandListener();



    public void LoadWorldLimitEntityConfig(){
        if(!worldLimits.isEmpty()){
            worldLimits.clear();
        }
//        if(!globalLimits.isEmpty()){
//            globalLimits.clear();
//        }
        WorldLimitFlag = main.getConfig().getBoolean("WorldEntityLimitCount.Flag");
        GatherLimitFlag = main.getConfig().getBoolean("GatherEntityLimitCount.Flag");
        if(WorldLimitFlag){
            for(String EntityToCountStr : main.getConfig().getStringList("WorldEntityLimitCount.DefaultCount")){
                String[] strings = EntityToCountStr.split(";");
//            strings[0]
                EntityType entityType = getEntityType(strings[0]);
                if (entityType == null) {
                    //EntityCountSetOK: "%PluginTitle% 成功设置各世界该 %Entity% 实体默认为 %Count% 个"
                    //EntityCountSetError: "%PluginTitle% 实体类型错误！可选的实体类型包括：%EntityTypes%"
                    main.getLogger().info(message.find("EntityCountSetError").replace("%EntityName%",strings[0]).replace("%EntityTypes%",getEntityTypes()));
                    continue;
                }
                int limit = Integer.parseInt(strings[1]);
                worldLimits.put(entityType.name(), limit);
//            main.getLogger().info(ChatColor.GREEN + "成功设置 " + entityType.name() + " 的数量限制为 " + limit);
                main.getLogger().info(message.find("EntityCountSetOK").replace("%Count%",limit+"").replace("%Entity%",entityType.name()));
            }
            BanWorlds.addAll(main.getConfig().getStringList("WorldEntityLimitCount.BanWorldNameList"));
        }














        if(GatherLimitFlag){
            for(String EntityToCountStrAndLimitRange : main.getConfig().getStringList("GatherEntityLimitCount.DefaultCount")){
                String[] strings = EntityToCountStrAndLimitRange.split(";");
//            strings[0]
                EntityType entityType = getEntityType(strings[0].toUpperCase());
                if (entityType == null) {
                    //EntityCountSetOK: "%PluginTitle% 成功设置各世界该 %Entity% 实体默认为 %Count% 个"
                    //EntityCountSetError: "%PluginTitle% 实体类型错误！可选的实体类型包括：%EntityTypes%"
//                main.getLogger().info(message.find("EntityCountSetError").replace("%EntityName%",strings[0]).replace("%EntityTypes%",getEntityTypes()));
                    continue;
                }
                int limit = Integer.parseInt(strings[1]);
                int range = Integer.parseInt(strings[2]);
                int clearCount = Integer.parseInt(strings[3]);
                GatherLimits.put(entityType.name(), new int[]{limit, range, clearCount});
//            main.getLogger().info(ChatColor.GREEN + "成功设置 " + entityType.name() + " 的数量限制为 " + limit);
//                main.getLogger().info(message.find("EntityCountSetOK").replace("%Count%",limit+"").replace("%Entity%",entityType.name()));
            }
            GatherBanWorlds.addAll(main.getConfig().getStringList("GatherEntityLimitCount.BanWorldNameList"));



            ItemDropFlag = main.getConfig().getBoolean("GatherEntityLimitCount.ItemDropFlag");
        }










    }
































    public static Economy econ = null;

    public static Economy getEcon() {
        return econ;
    }

    public boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }















}
