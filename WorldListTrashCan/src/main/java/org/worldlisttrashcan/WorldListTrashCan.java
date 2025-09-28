package org.worldlisttrashcan;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.worldlisttrashcan.AutoTrashMain.AutoTrashListener;
import org.worldlisttrashcan.AutoTrashMain.HeightVersionPlayerDropItemListener;
import org.worldlisttrashcan.Bstats.Metrics;
import org.worldlisttrashcan.DropSystem.DropLimitListener;
import org.worldlisttrashcan.Method.Method;
import org.worldlisttrashcan.Method.SendMessageAbstract;
import org.worldlisttrashcan.Papi.Papi;
import org.worldlisttrashcan.SimpleChange.NotPickArrowListener;
import org.worldlisttrashcan.SimpleChange.TreadingFarmLandListener;
import org.worldlisttrashcan.SpeakSystem.QuickSpeakListener;
import org.worldlisttrashcan.SpeakSystem.QuickUseCommandListener;
import org.worldlisttrashcan.TrashMain.*;
import org.worldlisttrashcan.WorldLimitEntityCount.BukkitClearGatherEntityTask;
import org.worldlisttrashcan.WorldLimitEntityCount.BukkitPlayerMoveEvent;
import org.worldlisttrashcan.WorldLimitEntityCount.LimitMain;
import org.worldlisttrashcan.WorldLimitEntityCount.PaperEntityMoveEvent;

import java.util.*;
import java.util.function.Consumer;

import static org.worldlisttrashcan.AutoTrashMain.AutoTrashListener.NoWorldTrashCanEnterPersonalTrashCan;
import static org.worldlisttrashcan.AutoTrashMain.AutoTrashListener.OriginalFeatureClearItemAddGlobalTrashModel;
import static org.worldlisttrashcan.DropSystem.DropLimitListener.PlayerDropList;
import static org.worldlisttrashcan.IsVersion.*;
import static org.worldlisttrashcan.Method.Method.*;
import static org.worldlisttrashcan.TrashMain.TrashListener.GlobalItemSetString;
import static org.worldlisttrashcan.WorldLimitEntityCount.LimitMain.*;
import static org.worldlisttrashcan.WorldLimitEntityCount.removeEntity.ItemDropFlag;
import static org.worldlisttrashcan.log.logFlag;
import static org.worldlisttrashcan.message.*;
public final class WorldListTrashCan extends JavaPlugin {

    public static Plugin main;


    public static BossBar bossBar = Bukkit.createBossBar("default", BarColor.BLUE, BarStyle.SOLID);

    public static WorldListTrashCan worldListTrashCan;

    public ClearItemsTask clearItemsTask;

    public ClearItemsTask getClearItemsTask(){
        return clearItemsTask;
    }

    public FoliaClearItemsTask foliaClearItemsTask;

    public FoliaClearItemsTask getFoliaClearItemsTask(){
        return foliaClearItemsTask;
    }
    public static GlobalTrashGui globalTrashGui;


//    public static GlobalTrashGui globalTrashGui;


    public static SendMessageAbstract sendMessageAbstract;
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
        List<String> allSubCommands = Arrays.asList("PlayerTrash","DropMode","look","GlobalBan","help","reload","GlobalTrash","ban","add","clear");
        if (command.getName().equalsIgnoreCase("WorldListTrashCan")||command.getName().equalsIgnoreCase("wtc")) {
            if (args.length == 1) {
                // 如果输入的是第一个参数，提供一些补全建议
                String partial = args[0].toLowerCase();
                for (String subCommand : allSubCommands) {
                    if (subCommand.toLowerCase().startsWith(partial)) {
                        completions.add(subCommand.toLowerCase());
                    }
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


    public static Papi papi = null;


    static public boolean EconomyFlag = false;

    static public Map<World, RashCanInformation> WorldToLocation = new HashMap<>();
    static public List<Player> UseEntityBarPlayerList = new ArrayList<>();
//    LimitMain limitMain = new LimitMain();
    @Override
    public void onEnable() {

        int pluginId = 24350; // <-- Replace with the id of your plugin!
//        Metrics metrics = new Metrics(this, pluginId);
        Metrics metrics = new Metrics(this, pluginId);
        metrics.addCustomChart(new Metrics.SingleLineChart("players",
                () -> Bukkit.getOnlinePlayers().size()
                ));
        metrics.addCustomChart(new Metrics.SingleLineChart("servers", () ->1));
        metrics.addCustomChart(new Metrics.MultiLineChart("players_and_servers", () -> {
            Map<String, Integer> valueMap = new HashMap<>();
            valueMap.put("servers", 1);
            valueMap.put("players", Bukkit.getOnlinePlayers().size());
            return valueMap;
        }));

//        metrics.addCustomChart(new Metrics.SimplePie("chart_id", () -> "My value"));

        // Plugin startup logic
        main = this;
        worldListTrashCan = this;
        Bukkit.getPluginManager().registerEvents(new TrashListener(), this);
        Bukkit.getPluginManager().registerEvents(new GuiListener(), this);
//        IsFoliaServer = Bukkit.getServer().getVersion().contains("Folia");
        IsFoliaServer = AutoCheckFoliaServer();

        hasEnemyClass = Method.isClassPresent("org.bukkit.entity.Enemy");



//        System.out.println("Bukkit.getServer().getVersion().contains "+Bukkit.getServer().getVersion());
//        IsPaperServer = Bukkit.getServer().getVersion().contains("Paper");
        IsPaperServer = AutoCheckPaperServer();


        Is1_12_1_16Server = IsVersion.isServerVersionLowerThan("1.16.0");

        Is1_16_1_20Server = !IsVersion.isServerVersionLowerThan("1.16.0");

        Is1_21_1_21_XServer = !IsVersion.isServerVersionLowerThan("1.21.0");

        Is1_21_1_21_5Server = !IsVersion.isServerVersionLowerThan("1.21.0") && IsVersion.isServerVersionLowerThan("1.21.6");

        Is1_21_5_21_NServer = !IsVersion.isServerVersionLowerThan("1.21.5");

//        System.out.println("Is1_12_1_16Server "+Is1_12_1_16Server);
//        System.out.println("Is1_16_1_20Server "+Is1_16_1_20Server);
//        System.out.println("Is1_21_1_21_XServer "+Is1_21_1_21_XServer);
//        System.out.println("Is1_21_1_21_5Server "+Is1_21_1_21_5Server);
//        System.out.println("Is1_21_5_21_NServer "+Is1_21_5_21_NServer);

        if (!setupEconomy() ) {
            message.consoleSay("&c没有找到Vault插件，已自动关闭相关功能");
        }else {
            EconomyFlag = true;
        }


        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            papi = new Papi(this);
            papi.register();
        }else {
            message.consoleSay("&c没有找到PlaceholderAPI插件，已自动关闭相关功能");
        }



        reload();

        sendMessageAbstract = new SendMessageAbstract((JavaPlugin) main);

//        Bukkit.getPluginManager().registerEvents(new LimitMain(), this);
        //限制整个世界的实体数量的监听器类
        LimitMain limitMain = new LimitMain();
        Bukkit.getPluginManager().registerEvents(limitMain, this);


        //警告：folia服务器只能用上面的，不能用bukkit延时任务来检查密集实体
//        if((IsPaperServer &&!compareVersions("1.13.0")) || IsFoliaServer){
        Bukkit.getPluginManager().registerEvents(bukkitPlayerMoveEvent, this);
        if(AutoCheckEntityMoveEventServer()){
//            System.out.println("1");
            Bukkit.getPluginManager().registerEvents(paperEntityMoveEvent, this);
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
        if (command.getName().equalsIgnoreCase("WorldListTrashCan")|| command.getName().equalsIgnoreCase("WTC")) {
            if (args.length == 0) {
                sender.sendMessage(ChatColor.GREEN + "WorldListTrashCan " + ChatColor.BLUE + " 作者QQ：2831508831");
                sender.sendMessage(ChatColor.YELLOW + "/WorldListTrashCan help" + ChatColor.BLUE + " 帮助");
            } else if (args[0].equalsIgnoreCase("reload")) {
//                getLogger().info("发送指令者是: "+sender.getName());
//                getLogger().info("是否有权限: "+sender.hasPermission("WorldListTrashCan.reload"));

                if (sender.isOp()) {
                    reload();
                    sender.sendMessage(message.find("ReloadInformation"));
                }
            }else if (args[0].equalsIgnoreCase("clear")) {
//                getLogger().info("发送指令者是: "+sender.getName());
//                getLogger().info("是否有权限: "+sender.hasPermission("WorldListTrashCan.reload"));
                if (sender.isOp()) {
                    if(foliaClearItemsTask!=null){
                        foliaClearItemsTask.Stop();
                        foliaClearItemsTask = new FoliaClearItemsTask(0);
                        Bukkit.getAsyncScheduler().runNow(main,foliaClearItemsTask.getFoliaRunnable());
//                        foliaClearItemsTask.getFoliaTask().cancel();
                    }
                    if(clearItemsTask!=null){
                        clearItemsTask.Stop();
                        clearItemsTask = new ClearItemsTask(0);
                        clearItemsTask.getBukkitRunnable().run();
//                        clearItemsTask.getBukkitRunnable().cancel();
                    }


                    if(IsFoliaServer){
//                        if(foliaClearItemsTask!=null){
//                            foliaClearItemsTask.Stop();
//                        }
//                        System.out.println("foliaClearItemsTask ");
                        foliaClearItemsTask = new FoliaClearItemsTask(main.getConfig().getInt("Set.SecondCount"));
                        foliaClearItemsTask.Start();
                    }else {
//                        if(clearItemsTask!=null){
//                            clearItemsTask.Stop();
//                        }
//                        System.out.println("clearItemsTask ");
                        clearItemsTask = new ClearItemsTask(main.getConfig().getInt("Set.SecondCount"));
                        clearItemsTask.Start();
                    }

                }
            } else if (args[0].equalsIgnoreCase("help")) {
//                sender.sendMessage("§0——————§7【§bWorldListTrashCan§7】§0——————");
//                sender.sendMessage("§6/WorldListTrashCan help"+"§d查看帮助");
//                sender.sendMessage("§6/WorldListTrashCan ban"+"§d打开本世界垃圾黑名单Gui");
//                sender.sendMessage("§6/WorldListTrashCan GlobalBan"+"§d打开全局世界垃圾黑名单Gui");
//                sender.sendMessage("§6/WorldListTrashCan add [世界名] <数量>"+"§d设置世界名垃圾桶最大数量(不填则为脚下世界)");
                if(sender.hasPermission("WorldListTrashCan.help")||sender.isOp()){
                    for (String string : message.getConfig().getStringList("HelpTitle")) {
                        sender.sendMessage(color(string));
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
                        if(IsFoliaServer){
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
                            }, () -> consoleSay("Error,Player is null"),60L);
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

                        message.consoleSay(player,message.find("ChunkEntityList"));

                        Entity[] entities = player.getLocation().getChunk().getEntities();
                        Map<String,Integer> entityMap = new HashMap<>();
                        for (Entity entity : entities) {
                            if(entityMap.containsKey(entity.getName())){
                                entityMap.put(entity.getName(),entityMap.get(entity.getName())+1);
                            }else {
                                entityMap.put(entity.getName(),1);
                            }
                        }

                        for (String typename : entityMap.keySet()) {
//                            String typename = entity.getName();
//                            message.consoleSay(sender,"- "+typename);
                            TextComponent clipboardMessage = new TextComponent("- "+typename+(entityMap.get(typename)==1?"":" *"+entityMap.get(typename)));
                            clipboardMessage.setColor(net.md_5.bungee.api.ChatColor.GREEN);
                            // 设置点击事件，点击后复制到聊天框
                            clipboardMessage.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, typename));
                            // 发送消息给玩家
                            player.spigot().sendMessage(clipboardMessage);
                        }

                        String itemStr = player.getInventory().getItemInMainHand().getType().toString();

                        TextComponent clipboardMessage = new TextComponent(message.find("HandItem").replace("%item%",itemStr));
                        clipboardMessage.setColor(net.md_5.bungee.api.ChatColor.GREEN);
                        // 设置点击事件，点击后复制到聊天框
                        clipboardMessage.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, itemStr));
                        // 发送消息给玩家
                        player.spigot().sendMessage(clipboardMessage);


//                        message.consoleSay(player,message.find("HandItem").replace("%item%",player.getInventory().getItemInMainHand().getType().toString()));


//                        for (Entity entity : player.getChunk().getEntities()) {
//                            consoleSay(sender,"entity 1getName "+entity.getName());
//                            consoleSay(sender,"entity 1getName "+entity.getType());
//                            consoleSay(sender,"entity 1getCustomName "+entity.getCustomName());
////                            consoleSay(sender,"entity 1getScoreboardEntryName "+entity.getScoreboardEntryName());
//                            consoleSay(sender,"entity 1getEntitySpawnReason "+entity.getEntitySpawnReason());
//                        }





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

        bossBar.removeAll();

    }

//    public static void ConfigStringReplace(ConfigurationSection config, String target, String replacement) {
//        for (String key : config.getKeys(false)) {
//            if (config.isConfigurationSection(key)) {
//                // 递归处理子节
//                ConfigStringReplace(config.getConfigurationSection(key), target, replacement);
//            } else if (config.isString(key)) {
//                // 替换字符串值
//                String originalValue = config.getString(key);
//                String modifiedValue = originalValue.replace(target, replacement);
//                config.set(key, modifiedValue);
//            }
//        }
//    }



    public static boolean GlobalTrashGuiFlag = false;
//    static public data data;

    public static List<String> NoClearContainerLore = new ArrayList<>();
    public static List<String> NoClearContainerName = new ArrayList<>();
    public static List<String> NoClearContainerType = new ArrayList<>();
    public void reload(){
//        reloadConfig();

        bossBar.removeAll();
        saveDefaultConfig();
        reloadConfig();


        //支持RGB
        ConfigStringReplace(getConfig());

        AllMessageLoad();
        chanceMessage = getConfig().getString("Set.Lang");
        message.reloadMessage();

        data.LoadData();
//        AllMessageLoad();
//        chanceMessage = getConfig().getString("Set.Lang");
//        message.reloadMessage();




//        IsFoliaServer = getConfig().getBoolean("IsFoliaServer");
        logFlag = main.getConfig().getBoolean("Set.GlobalTrash.Log.Enable");
//        refinementLogFlag = main.getConfig().getBoolean("Set.GlobalTrash.Log.EnableRefinementLog");


        GlobalItemSetString = new HashSet<>(main.getConfig().getStringList("GlobalBanItem"));
        int MaxCount = main.getConfig().getInt("Set.GlobalTrash.MaxPage");
        globalTrashGui = new GlobalTrashGui(GlobalTrashList,MaxCount);


        if(IsFoliaServer){
            if(foliaClearItemsTask!=null){
                foliaClearItemsTask.Stop();
            }
            foliaClearItemsTask = new FoliaClearItemsTask(main.getConfig().getInt("Set.SecondCount"));
            foliaClearItemsTask.Start();
        }else {
            if(clearItemsTask!=null){
                clearItemsTask.Stop();
            }
            clearItemsTask = new ClearItemsTask(main.getConfig().getInt("Set.SecondCount"));
            clearItemsTask.Start();
        }



        GlobalTrashGuiFlag = getConfig().getBoolean("Set.GlobalTrash.Flag");




        if(getConfig().getBoolean("SimpleOptimize.NotPickArrow")){
            HandlerList.unregisterAll(notPickArrowListener);
            Bukkit.getPluginManager().registerEvents(notPickArrowListener, this);
        }else {
            HandlerList.unregisterAll(notPickArrowListener);
        }

//        if(getConfig().getBoolean("SimpleOptimize.QuicklyPickExpBall")){
//            HandlerList.unregisterAll(quicklyPickExpBall);
//            Bukkit.getPluginManager().registerEvents(quicklyPickExpBall, this);
//        }else {
//            HandlerList.unregisterAll(quicklyPickExpBall);
//        }

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





        // 这里要兼容低版本wtc的配置文件路径

//        String OriginPath = "Set.GlobalTrash.OriginalFeatureClearItemAddGlobalTrash";
//        if(main.getConfig().get(OriginPath) == null){
//            OriginPath = "Set.PersonalTrashCan.OriginalFeatureClearItemAddGlobalTrash";
//        }
        String OriginPath = "Set.PersonalTrashCan.OriginalFeatureClearItemAddGlobalTrash";

        OriginalFeatureClearItemAddGlobalTrashModel = main.getConfig().getInt(OriginPath+".UseModel");



        NoWorldTrashCanEnterPersonalTrashCan = main.getConfig().getBoolean("Set.PersonalTrashCan.NoWorldTrashCanEnterPersonalTrashCan");

        if(NoWorldTrashCanEnterPersonalTrashCan){
            HandlerList.unregisterAll(heightVersionPlayerDropItemListener);
            Bukkit.getPluginManager().registerEvents(heightVersionPlayerDropItemListener, this);
        }else {
            HandlerList.unregisterAll(heightVersionPlayerDropItemListener);
        }

//        if (OriginalFeatureClearItemAddGlobalTrashModel == 0){
//
//        }

//        if(getConfig().getInt(OriginPath+".UseModel")!=3){
        if(main.getConfig().getBoolean("Set.PersonalTrashCan.Flag")){
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
        NoClearContainerName = main.getConfig().getStringList("Set.NoClearContainerName");
        NoClearContainerType = main.getConfig().getStringList("Set.NoClearContainerType");


//        main = this;

    }

    AutoTrashListener autoTrashListener = new AutoTrashListener();
    HeightVersionPlayerDropItemListener heightVersionPlayerDropItemListener = new HeightVersionPlayerDropItemListener();
    DropLimitListener dropLimitListener = new DropLimitListener();
    NotPickArrowListener notPickArrowListener = new NotPickArrowListener();
    QuickSpeakListener quickSpeakListener = new QuickSpeakListener();
    TreadingFarmLandListener treadingFarmLandListener = new TreadingFarmLandListener();
//    QuicklyPickExpBall quicklyPickExpBall = new QuicklyPickExpBall();
    QuickUseCommandListener quickUseCommandListener = new QuickUseCommandListener();
    BukkitPlayerMoveEvent bukkitPlayerMoveEvent = new BukkitPlayerMoveEvent();
    PaperEntityMoveEvent paperEntityMoveEvent = new PaperEntityMoveEvent();



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
            BanWorlds = new ArrayList<>();
            for(String EntityToCountStr : main.getConfig().getStringList("WorldEntityLimitCount.DefaultCount")){
                String[] strings = EntityToCountStr.split(";");
//            strings[0]
                EntityType entityType = getEntityType(strings[0]);
                if (entityType == null) {
                    //EntityCountSetOK: "%PluginTitle% 成功设置各世界该 %Entity% 实体默认为 %Count% 个"
                    //EntityCountSetError: "%PluginTitle% 实体类型错误！可选的实体类型包括：%EntityTypes%"
                    message.consoleSay(message.find("EntityCountSetError").replace("%EntityName%",strings[0]).replace("%EntityTypes%",getEntityTypes()));
                    continue;
                }
                int limit = Integer.parseInt(strings[1]);
                worldLimits.put(entityType.name().toLowerCase(), limit);
//            consoleSay(ChatColor.GREEN + "成功设置 " + entityType.name() + " 的数量限制为 " + limit);
                message.consoleSay(message.find("EntityCountSetOK").replace("%Count%",limit+"").replace("%Entity%",entityType.name()));
            }
            BanWorlds.addAll(main.getConfig().getStringList("WorldEntityLimitCount.BanWorldNameList"));
        }














        if(GatherLimitFlag){
            GatherBanWorlds = new ArrayList<>();
            for(String EntityToCountStrAndLimitRange : main.getConfig().getStringList("GatherEntityLimitCount.DefaultCount")){
                String[] strings = EntityToCountStrAndLimitRange.split(";");
//            strings[0]
//                EntityType entityType = getEntityType(strings[0].toUpperCase());
                String entityType = strings[0].toUpperCase();
                if (entityType == null) {
                    //EntityCountSetOK: "%PluginTitle% 成功设置各世界该 %Entity% 实体默认为 %Count% 个"
                    //EntityCountSetError: "%PluginTitle% 实体类型错误！可选的实体类型包括：%EntityTypes%"
//                consoleSay(message.find("EntityCountSetError").replace("%EntityName%",strings[0]).replace("%EntityTypes%",getEntityTypes()));
                    continue;
                }
                int limit = Integer.parseInt(strings[1]);
                int range = Integer.parseInt(strings[2]);
                int clearCount = Integer.parseInt(strings[3]);
//                GatherLimits.put(entityType.name().toUpperCase(), new int[]{limit, range, clearCount});
                GatherLimits.put(entityType, new int[]{limit, range, clearCount});
//            consoleSay(ChatColor.GREEN + "成功设置 " + entityType.name() + " 的数量限制为 " + limit);
//                consoleSay(message.find("EntityCountSetOK").replace("%Count%",limit+"").replace("%Entity%",entityType.name()));
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
