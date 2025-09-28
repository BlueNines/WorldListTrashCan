package org.worldlisttrashcan.TrashMain;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.worldlisttrashcan.IsVersion;
import org.worldlisttrashcan.data;
import org.worldlisttrashcan.message;
//import org.json.simple.ItemList;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

import static org.worldlisttrashcan.IsVersion.IsFoliaServer;
import static org.worldlisttrashcan.WorldListTrashCan.*;
//import static org.worldlisttrashcan.data.Data;
import static org.worldlisttrashcan.data.getConfig;
import static org.worldlisttrashcan.message.consoleSay;

public class TrashListener implements Listener {

    @EventHandler
    public void PlayerPlaceBlock(BlockBreakEvent event){
        Player player = event.getPlayer();
        if(PlayerToWorld.get(player)!=null){
            if(event.isCancelled()){
                //没权限
                player.sendMessage(message.find("NotWorldPlaceOrBreakFlag"));
                PlayerToWorld.remove(player);
            }else {
                //有权限
                player.sendMessage(message.find("HaveFlag"));
                event.setCancelled(true);
                Inventory inventory = (new BanGui()).getInventory(player);
                player.openInventory(inventory);
                PlayerToWorld.remove(player);
            }
        }
    }

    @EventHandler
    public void PlayerPlaceBlock(BlockPlaceEvent event){
        Player player = event.getPlayer();
        if(PlayerToWorld.get(player)!=null){
            if(event.isCancelled()){
                //没权限
                player.sendMessage(message.find("NotWorldPlaceOrBreakFlag"));
                PlayerToWorld.remove(player);
            }else {
                //有权限
                player.sendMessage(message.find("HaveFlag"));
                event.setCancelled(true);
                Inventory inventory = (new BanGui()).getInventory(player);
                player.openInventory(inventory);
                PlayerToWorld.remove(player);
            }
        }
    }


    public Location getChestLocation1_12(Location signLocation){
        Location chestLocation;
        Block sign = signLocation.getBlock();
        int signID = sign.getData();


//        if(sign instanceof Sign){
//            System.out.println("我是牌子1");
//        }else if(sign.getState() instanceof Sign){
//            System.out.println("我是牌子2");
//        }

//        if(sign.getType()==Material.WALL_SIGN){
        if(sign.getType().toString().contains("WALL_SIGN")){
            switch (signID) {
                case 2:
                    return chestLocation = new Location(signLocation.getWorld(), signLocation.getX(), signLocation.getY(), signLocation.getZ() + 1);

                case 3:
                    return chestLocation = new Location(signLocation.getWorld(), signLocation.getX(), signLocation.getY(), signLocation.getZ() - 1);

                case 4:
                    return chestLocation = new Location(signLocation.getWorld(), signLocation.getX() + 1, signLocation.getY(), signLocation.getZ());

                case 5:
                    return chestLocation = new Location(signLocation.getWorld(), signLocation.getX() - 1, signLocation.getY(), signLocation.getZ());

            }
        }else {
            return chestLocation = new Location(signLocation.getWorld(), signLocation.getX() , signLocation.getY()-1 , signLocation.getZ());
        }
        return null;
    }
    public Location getChestLocation1_13(Location signLocation) {
        Location chestLocation;
        Block sign = signLocation.getBlock();
//        int signID = sign.getData();
//        Sign sign1 = (Sign)sign;sign1.getB

//        System.out.println("Sign: "+blockData);
//        if (blockData instanceof Directional) {
//            Directional directional = (Directional) blockData;
//
//            // 输出朝向信息
//            System.out.println("告示牌朝向: " + facing);
        if (sign.getType().toString().contains("WALL_SIGN")) {
//            System.out.println("1");
            Sign sign1 = (Sign) sign.getState();
            BlockData blockData = sign1.getBlockData();
            if (blockData instanceof Directional) {

                // 获取告示牌的朝向
                BlockFace facing = ((Directional) blockData).getFacing().getOppositeFace();

                // 获取贴合的方块坐标
                int xOffset = facing.getModX();
                int yOffset = facing.getModY();
                int zOffset = facing.getModZ();

                // 获取贴合的方块
                Block attachedBlock = signLocation.getBlock().getRelative(xOffset, yOffset, zOffset);
//                System.out.println("2" + attachedBlock.getType());
                return attachedBlock.getLocation();
            }
        } else {
//            System.out.println("3");
            return chestLocation = new Location(signLocation.getWorld(), signLocation.getX(), signLocation.getY() - 1, signLocation.getZ());
        }
//        System.out.println("Sign: "+sign1.getBlockData().getMaterial().getData());


//        if(sign.getType()==Material.WALL_SIGN){

        return null;

    }
    @EventHandler
    public void onSignChange(SignChangeEvent event) {

        // 获取放置告示牌的玩家
        Player player = event.getPlayer();
        if (!player.hasPermission("WorldListTrashCan.Main")) {
            return;
        }
        if (main.getConfig().getBoolean("Set.Debug")) {
            consoleSay("告示牌子ID为"+event.getBlock().getData());
            consoleSay("告示牌type为"+event.getBlock().getType());
        }
        // 获取告示牌的内容
        String[] lines = event.getLines();

        // 检查每一行的内容
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            // 检查玩家输入的内容
            if (line.contains(main.getConfig().getString("Set.SighCheckName"))) {

                //如果是folia服务器则禁用
                if(IsFoliaServer){
                    player.sendMessage(message.find("BanWorldTrashCanInFoliaServer"));
                    continue;
                }


//                player.sendMessage(ChatColor.GREEN + "你尝试创建一个世界垃圾桶");
                // 获取告示牌所依附的方块
                Block sign = event.getBlock();
                Location signLocation = sign.getLocation();
                Location chestLocation;

                if(IsVersion.isServerVersionLowerThan("1.13.0")){
                    chestLocation = getChestLocation1_12(signLocation);
                }else {
                    chestLocation = getChestLocation1_13(signLocation);
                }




                if (chestLocation != null && chestLocation.getBlock().getType() == Material.CHEST) {
                    World world = signLocation.getWorld();

                    if (player.isOp() || !main.getConfig().getStringList("Set.BanWorldNameList").contains(world.getName())) {

                        int RashMax = getConfig().getStringList("WorldData." + world.getName() + ".SignLocation").size();
                        int Count = getConfig().getInt("WorldData." + world.getName() + ".RashMaxCount");
                        if (player.isOp() ||
//                                RashMax <
//                                main.getConfig().getInt("Set.RashCanMax")||
                                        RashMax < Count||
                                        (Count==0&&RashMax<main.getConfig().getInt("Set.DefaultRashCanMax"))


                        ) {
                            if (main.getConfig().getBoolean("Set.Debug")) {
                                consoleSay("目前该世界已创建垃圾桶数量为：" + getConfig().getStringList("WorldData." + world.getName() + ".SignLocation").size());
                                consoleSay("配置文件最大支持垃圾桶数量为：" + main.getConfig().getInt("Set.RashCanMax"));
                            }
                            player.sendMessage(message.find("CreateRashCan").replace("%world%",world.getName()));

//                            System.out.println("在中");


                            event.setLine(i,main.getConfig().getString("Set.SighCheckedName"));

//                            Sign sign1 = (Sign) sign.getState();
//                            sign1.setLine(i,main.getConfig().getString("Set.SighCheckedName"));
//                            sign.setBlockData(sign1.getBlockData());
//                            Sign signnnn =  ((Sign) sign.getState());
//                            signnnn.setLine(i,main.getConfig().getString("Set.SighCheckedName"));
//                            signnnn.setLine(i,"ASdd");

//                            sign.setBlockData(signnnn.getBlockData());

//                            sign = (Block) signnnn;

//                            signnnn.update();
//                            sign.setBlockData(signnnn.getBlockData());
//                            sign.setBlockData((BlockData) signnnn);





                            int n = i;
                            if(IsFoliaServer){
                                player.getScheduler().runDelayed(main, new Consumer<ScheduledTask>() {
                                    @Override
                                    public void accept(ScheduledTask scheduledTask) {
                                        Sign sign1 = (Sign) sign.getState();
                                        sign1.setLine(n,main.getConfig().getString("Set.SighCheckedName"));
                                        sign1.update();
                                    }
                                }, () -> consoleSay("Error,Player is null"),(long) (2L));
                            }else {
                                new BukkitRunnable(){
                                    @Override
                                    public void run() {
//                                    Block playerblock = player.getLocation().getBlock();
//                                    playerblock.setType(Material.OAK_SIGN);
//                                    Sign sign1 = (Sign) playerblock.getState();
                                        Sign sign1 = (Sign) sign.getState();
                                        sign1.setLine(n,main.getConfig().getString("Set.SighCheckedName"));
                                        sign1.update();
                                    }
                                }.runTaskLater(main,2L);
                            }



//                            Block playerblock = sign;



//                            Block playerblock = sign;
//                            playerblock.setType(Material.OAK_WALL_SIGN);
//                            Sign sign1 = (Sign) playerblock.getState();
//                            sign1.setLine(1,"asdasd");
//                            sign1.update();
//                            event.setLine(i,"asdasdasdasdadsad");

//                            sign.getState().setBlockData(signnnn.getBlockData());
//                            BlockState blockState = sign.getState();
//                            blockState.setBlockData(signnnn.getBlockData());




//                            sign.set


//                            for (String s : ((Sign)sign.getState()).getLines()) {
//                                System.out.println("signlist "+s);
//                            }
//
//
//
//                            for (String s : signnnn.getLines()) {
//                                System.out.println("signlist "+s);
//                            }
//                            System.out.println("在中 "+sign.getState());
//                            System.out.println("在中 "+sign.getState().getData());
//                            System.out.println("在中 "+sign.get);


                            if(WorldToLocation.get(world)!=null){
                                Set<Location> locationSet = WorldToLocation.get(world).getLocationSet();
                                locationSet.add(chestLocation);
                                data.dataPut(world, locationSet);
                            }else {
                                Set<Location> locationSet = new HashSet<>();
                                locationSet.add(chestLocation);
                                data.dataPut(world, locationSet);
                            }
                            break;
                        } else {
//                            player.sendMessage(ChatColor.RED + "你不能再创建更多的世界垃圾桶了！");
                            if(main.getConfig().getBoolean("Set.Debug")){
                                consoleSay("RashMax: " +RashMax);
                                consoleSay("Count: " +Count);
                                consoleSay("Default: " +main.getConfig().getInt("Set.DefaultRashCanMax"));
                            }
                            player.sendMessage(message.find("ReachRashCanCount"));

                        }
                    } else {
//                        player.sendMessage(ChatColor.RED + "这个世界禁止普通玩家创建世界垃圾桶");
                        player.sendMessage(message.find("InBanWorldList"));
                    }
                } else {
//                    player.sendMessage(ChatColor.RED + "这不是一个普通箱子，无法充当世界垃圾桶");
                    player.sendMessage(message.find("NotChest"));
                }
            }
        }

    }

    @EventHandler
    public void PlayerBreakSigh(BlockBreakEvent event){
        Player player = event.getPlayer();
        World world = player.getWorld();
        Block block = event.getBlock();
        Location location = block.getLocation();
//        if(block.getType()==Material.WALL_SIGN||block.getType()==Material.SIGN_POST){
        if(block.getState() instanceof Sign){
            Sign sign = (Sign) block.getState();
            for (String line : sign.getLines()) {
                if(line.contains(main.getConfig().getString("Set.SighCheckedName"))){
                    Location chestLocation;
                    //如果版本小于1.13.0
                    if(IsVersion.isServerVersionLowerThan("1.13.0")){
                        chestLocation = getChestLocation1_12(location);
                    }else {
                        chestLocation = getChestLocation1_13(location);
                    }
                    if(chestLocation!=null&&chestLocation.getBlock().getType()==Material.CHEST){
                        if(WorldToLocation.get(world)!=null){
                            Set<Location> locationSet = WorldToLocation.get(world).getLocationSet();
//                            player.sendMessage(ChatColor.GREEN + "你在" + world.getName() + "破坏/删除了一个世界垃圾桶");
                            player.sendMessage(message.find("RashBreak").replace("%world%",world.getName()));
                            locationSet.remove(chestLocation);
                            data.dataPut(world, locationSet);
                            break;
                        }
                    }

                }
            }
        }else if(block.getType()==Material.CHEST){
            if(block.getType()==Material.CHEST){

                if(WorldToLocation.get(world)!=null){
                    if(!WorldToLocation.get(world).getLocationSet().isEmpty()){
                        if(WorldToLocation.get(world).getLocationSet().contains(location)){
                            Set<Location> locationSet1 = WorldToLocation.get(world).getLocationSet();
                            locationSet1.remove(location);
                            data.dataPut(world,locationSet1);
                            player.sendMessage(message.find("RashBreak").replace("%world%",world.getName()));
                        }
                    }

                }
            }
        }

    }







    static public Set<String> GlobalItemSetString;
    @EventHandler
    public void PlayerBanGuiClose(InventoryCloseEvent event){
        Inventory inventory = event.getInventory();
        Player player = (Player) event.getPlayer();
        World world = player.getWorld();
//        if(inventory.getName().equals("玩家"+player.getName()+"的世界垃圾桶物品Ban表")){

        if(event.getView().getTitle().equals(message.find("BanChestInventoryName").replace("%world%",player.getWorld().getName()))){
            Set<String> ItemSetString = null;
            if(WorldToLocation.get(world)!=null&&!WorldToLocation.get(world).getBanItemSet().isEmpty()){
                if(main.getConfig().getBoolean("Set.Debug")){
                    player.sendMessage(ChatColor.BLUE+"WorldToLocation.get(world)!=null1："+(WorldToLocation.get(world)!=null));
                    player.sendMessage(ChatColor.BLUE+"WorldToLocation.get(world).getBanItemSet().isEmpty()1："+(WorldToLocation.get(world).getBanItemSet().isEmpty()));
                }
                ItemSetString = WorldToLocation.get(world).getBanItemSet();

            }else {
                if(main.getConfig().getBoolean("Set.Debug")){
                    player.sendMessage(ChatColor.BLUE+"WorldToLocation.get(world)!=null2："+(WorldToLocation.get(world)!=null));
                    player.sendMessage(ChatColor.BLUE+"WorldToLocation.get(world).getBanItemSet().isEmpty()2："+(WorldToLocation.get(world).getBanItemSet().isEmpty()));
                }
                ItemSetString = new HashSet<>();
            }
            boolean flag = false;
            for (ItemStack itemStack : inventory) {
                if(itemStack!=null){
                    if(main.getConfig().getBoolean("Set.Debug")){
                        player.sendMessage(ChatColor.BLUE+"该物品是："+itemStack.getType());
                    }
                    String StringItemStack = itemStack.getType().toString();
                    if(!ItemSetString.contains(StringItemStack)){
                        ItemSetString.add(StringItemStack);
//                        player.sendMessage(ChatColor.BLUE+"您在世界垃圾桶中添加了物品: "+StringItemStack);
                        player.sendMessage(message.find("BanAddItem").replace("%ItemType%",StringItemStack));
                        if(main.getConfig().getBoolean("Set.Debug")){
                            consoleSay(ChatColor.BLUE+"物品: "+itemStack.getItemMeta().getDisplayName());
                        }
                    }else {
                        ItemSetString.remove(StringItemStack);
                        player.sendMessage(message.find("BanRemoveItem").replace("%ItemType%",StringItemStack));
                    }

                    flag = true;
                    RashCanInformation rashCanInformation = WorldToLocation.get(world);
                    rashCanInformation.setBanItemSet(ItemSetString);
                    WorldToLocation.put(world,rashCanInformation);

                }
            }
//            if(flag&&!ItemSetString.isEmpty()){
            if(flag){
                data.dataPut(world.getName(),ItemSetString);
//                player.sendMessage(ChatColor.GREEN+"目前该世界垃圾桶禁用了如下物品：");
                player.sendMessage(message.find("BanListTitle"));
                for(String itemString : ItemSetString) {
//                    player.sendMessage(ChatColor.BLUE+"["+itemString+"] ");
                    player.sendMessage(message.find("BanItemFormat").replace("%Item%",itemString));
                }
            }else {
//                player.sendMessage(ChatColor.RED+"你没有加入/删除任何物品，因为它是空的");
                player.sendMessage(message.find("BanNull"));
            }
        }


























        else if(event.getView().getTitle().equals(message.find("GlobalBanChestInventoryName").replace("%world%",player.getWorld().getName()))){
             GlobalItemSetString = new HashSet<>(main.getConfig().getStringList("GlobalBanItem"));
             if(main.getConfig().getBoolean("Set.Debug")){
                 for (String string : GlobalItemSetString) {
                     consoleSay("GlobalBan  Item: "+string);
                 }
             }
//            if(WorldToLocation.get(world)!=null&&!WorldToLocation.get(world).getBanItemSet().isEmpty()){
//                if(main.getConfig().getBoolean("Set.Debug")){
//                    player.sendMessage(ChatColor.BLUE+"WorldToLocation.get(world)!=null1："+(WorldToLocation.get(world)!=null));
//                    player.sendMessage(ChatColor.BLUE+"WorldToLocation.get(world).getBanItemSet().isEmpty()1："+(WorldToLocation.get(world).getBanItemSet().isEmpty()));
//                }
//                ItemSetString = WorldToLocation.get(world).getBanItemSet();
//
//            }else {
//                if(main.getConfig().getBoolean("Set.Debug")){
//                    player.sendMessage(ChatColor.BLUE+"WorldToLocation.get(world)!=null2："+(WorldToLocation.get(world)!=null));
//                    player.sendMessage(ChatColor.BLUE+"WorldToLocation.get(world).getBanItemSet().isEmpty()2："+(WorldToLocation.get(world).getBanItemSet().isEmpty()));
//                }
//                ItemSetString = new HashSet<>();
//            }
            boolean flag = false;
            for (ItemStack itemStack : inventory) {
                if(itemStack!=null){
                    if(main.getConfig().getBoolean("Set.Debug")){
                        player.sendMessage(ChatColor.BLUE+"该物品是："+itemStack.getType());
                    }
                    String StringItemStack = itemStack.getType().toString();
                    if(!GlobalItemSetString.contains(StringItemStack)){
                        GlobalItemSetString.add(StringItemStack);
//                        player.sendMessage(ChatColor.BLUE+"您在世界垃圾桶中添加了物品: "+StringItemStack);
                        player.sendMessage(message.find("BanAddItem").replace("%ItemType%",StringItemStack));
                        if(main.getConfig().getBoolean("Set.Debug")){
                            consoleSay(ChatColor.BLUE+"物品: "+itemStack.getItemMeta().getDisplayName());
                        }
                    }else {
                        GlobalItemSetString.remove(StringItemStack);
                        player.sendMessage(message.find("BanRemoveItem").replace("%ItemType%",StringItemStack));
                    }

                    flag = true;
//                    RashCanInformation rashCanInformation = WorldToLocation.get(world);
//                    rashCanInformation.setBanItemSet(ItemSetString);
//                    WorldToLocation.put(world,rashCanInformation);


                }
            }
            if(flag){



//                data.dataPut(world.getName(),ItemSetString);
//                main.reloadConfig();
//                main.getConfig().set("GlobalBanItem",GlobalItemSetString.toArray());
                main.getConfig().set("GlobalBanItem",GlobalItemSetString.toArray());
                main.saveConfig();
                main.reloadConfig();

//                for (String string : GlobalItemSetString) {
//                    System.out.println("GlobalItemSetStringsss: "+string);
//                }




//                player.sendMessage(ChatColor.GREEN+"目前该世界垃圾桶禁用了如下物品：");
                player.sendMessage(message.find("GlobalBanListTitle"));
                for(String itemString : GlobalItemSetString) {
//                    player.sendMessage(ChatColor.BLUE+"["+itemString+"] ");
                    player.sendMessage(message.find("BanItemFormat").replace("%Item%",itemString));
                }
            }else {
//                player.sendMessage(ChatColor.RED+"你没有加入/删除任何物品，因为它是空的");
                player.sendMessage(message.find("BanNull"));
            }

        }
    }




    @EventHandler
    public void PlayerEnterWorld(PlayerChangedWorldEvent event){
//        System.out.println("1");
        World world = event.getPlayer().getWorld();
        String WorldName = world.getName();


        ConfigurationSection WorldDataSection = data.getConfig().getConfigurationSection("WorldData");
        if (WorldToLocation.get(world)==null&&WorldDataSection.getKeys(false).contains(WorldName)) {
//            System.out.println("2");
//            ConfigurationSection WorldSection = WorldDataSection.getConfigurationSection(WorldName);
//            String locStr = WorldSection.getString("WorldData."+WorldName+".SignLocation");
            Set<String> locStrSet = new HashSet<>(getConfig().getStringList("WorldData."+WorldName+".SignLocation"));
            Set<String> BanItemSet = new HashSet<>(getConfig().getStringList("WorldData." + WorldName + ".BanItem"));
//            String PlayerName = getConfig().getString("WorldData."+WorldName+".Player");
//            World world = Bukkit.getWorld(WorldName);
            Set<Location> LocationSet = new HashSet<>();
            for (String locStr : locStrSet) {
                String[] strings = locStr.split(",");

//                System.out.println(WorldSection.toString());
//                System.out.println(WorldName);
//                System.out.println(locStr);
                for (String string : strings) {
                    System.out.println("s "+string);
                }



                if(world==null||strings.length!=3){
//                    consoleSay(ChatColor.RED+"配置文件中有一个空的世界名或者不正常的坐标");
//                    consoleSay(ChatColor.RED+"配置文件中有一个空的世界名或者不正常的坐标");
//                    consoleSay(ChatColor.RED+"世界名为："+WorldName+"坐标为："+locStr);
                    consoleSay(message.find("ConfigError").replace("%world%",WorldName).replace("%location%",locStr));
                    continue;
                }
                double x = Double.parseDouble(strings[0]);
                double y = Double.parseDouble(strings[1]);
                double z = Double.parseDouble(strings[2]);
                Location location = new Location(world,x,y,z);
                LocationSet.add(location);
            }
            WorldToLocation.put(world,new RashCanInformation(LocationSet,BanItemSet));

        }
    }

    @EventHandler
    public void PlayerJoinGame(PlayerJoinEvent event){
//        System.out.println("1");
        World world = event.getPlayer().getWorld();
        String WorldName = world.getName();


        ConfigurationSection WorldDataSection = data.getConfig().getConfigurationSection("WorldData");
        if (WorldToLocation.get(world)==null&&WorldDataSection.getKeys(false).contains(WorldName)) {
//            System.out.println("2");
//            ConfigurationSection WorldSection = WorldDataSection.getConfigurationSection(WorldName);
//            String locStr = WorldSection.getString("WorldData."+WorldName+".SignLocation");
            Set<String> locStrSet = new HashSet<>(getConfig().getStringList("WorldData."+WorldName+".SignLocation"));
            Set<String> BanItemSet = new HashSet<>(getConfig().getStringList("WorldData." + WorldName + ".BanItem"));
//            String PlayerName = getConfig().getString("WorldData."+WorldName+".Player");
//            World world = Bukkit.getWorld(WorldName);
            Set<Location> LocationSet = new HashSet<>();
            for (String locStr : locStrSet) {
                String[] strings = locStr.split(",");

//                System.out.println(WorldSection.toString());
//                System.out.println(WorldName);
//                System.out.println(locStr);
                for (String string : strings) {
                    System.out.println("s "+string);
                }



                if(world==null||strings.length!=3){
//                    consoleSay(ChatColor.RED+"配置文件中有一个空的世界名或者不正常的坐标");
//                    consoleSay(ChatColor.RED+"配置文件中有一个空的世界名或者不正常的坐标");
//                    consoleSay(ChatColor.RED+"世界名为："+WorldName+"坐标为："+locStr);
                    consoleSay(message.find("ConfigError").replace("%world%",WorldName).replace("%location%",locStr));
                    continue;
                }
                double x = Double.parseDouble(strings[0]);
                double y = Double.parseDouble(strings[1]);
                double z = Double.parseDouble(strings[2]);
                Location location = new Location(world,x,y,z);
                LocationSet.add(location);
            }
            WorldToLocation.put(world,new RashCanInformation(LocationSet,BanItemSet));

        }
    }
}
