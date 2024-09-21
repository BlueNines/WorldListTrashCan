package org.worldlisttrashcan;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.worldlisttrashcan.TrashMain.RashCanInformation;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.worldlisttrashcan.WorldListTrashCan.WorldToLocation;
import static org.worldlisttrashcan.WorldListTrashCan.main;

public class data {

    private static File dataFile;

    private static ConfigurationSection configurationSection;
    private static FileConfiguration Data;


    public static FileConfiguration getConfig() {

        return Data;
    }



    public static void LoadData() {
//        dataFile = new File(main.getDataFolder(), "data.yml");
        dataFile = new File(main.getDataFolder(), "data" + File.separator + "data.yml");


        if (!dataFile.exists())
//            main.saveResource("data.yml", false);
            main.saveResource("data" + File.separator + "data.yml",false);
        Data = YamlConfiguration.loadConfiguration(dataFile);
        

        WorldToLocation.clear();


        ConfigurationSection WorldDataSection = Data.getConfigurationSection("WorldData");
        boolean flag = false;
        for (String WorldName : WorldDataSection.getKeys(false)) {
//            ConfigurationSection WorldSection = WorldDataSection.getConfigurationSection(WorldName);
//            String locStr = WorldSection.getString("WorldData."+WorldName+".SignLocation");
            Set<String> locStrSet = new HashSet<>(getConfig().getStringList("WorldData."+WorldName+".SignLocation"));
            Set<String> BanItemSet = new HashSet<>(getConfig().getStringList("WorldData." + WorldName + ".BanItem"));
//            String PlayerName = getConfig().getString("WorldData."+WorldName+".Player");
            World world = Bukkit.getWorld(WorldName);
            Set<Location> LocationSet = new HashSet<>();

            for (String locStr : locStrSet) {
                String[] strings = locStr.split(",");

//            System.out.println(WorldSection.toString());
//            System.out.println(WorldName);
//            System.out.println(locStr);
//                for (String string : strings) {
//                    System.out.println("s "+string);
//                }



                if(world==null||strings.length!=3){
//                    main.getLogger().info(ChatColor.RED+"配置文件中有一个空的世界名或者不正常的坐标");
//                    main.getLogger().info(ChatColor.RED+"配置文件中有一个空的世界名或者不正常的坐标");
//                    main.getLogger().info(ChatColor.RED+"世界名为："+WorldName+"坐标为："+locStr);
                    main.getLogger().info(message.find("ConfigError").replace("%world%",WorldName).replace("%location%",locStr));
                    flag = true;
                    continue;
                }
                double x = Double.parseDouble(strings[0]);
                double y = Double.parseDouble(strings[1]);
                double z = Double.parseDouble(strings[2]);
                Location location = new Location(world,x,y,z);
                LocationSet.add(location);
            }
            WorldToLocation.put(world,new RashCanInformation(LocationSet,BanItemSet));




//            long maxTime = playerSection.getLong("MaxTime");
//            int maxKill = playerSection.getInt("MaxKill");
//            playermaxtime.put(playerName, maxTime);
//            playermaxkill.put(playerName, maxKill);

        }
        if(flag){
            main.getLogger().info(message.find("HaveHomePlugin"));
        }
    }



    public static int RashMaxCountAdd(World world , int Count){
        int nowCount = data.getConfig().getInt("WorldData."+world.getName()+".RashMaxCount");
        int defaultCount = main.getConfig().getInt("Set.DefaultRashCanMax");
        if(nowCount==0){
            nowCount = defaultCount;
        }
        if((Count+nowCount)>=defaultCount){
            if (main.getConfig().getBoolean("Set.Debug")) {
                main.getLogger().info("worldname2: "+world.getName());
                main.getLogger().info("Count: "+Count+" nowCount: "+nowCount);

            }
            data.getConfig().set("WorldData."+world.getName()+".RashMaxCount",Count+nowCount);
            data.saveData();
//            System.out.println("a: "+Count);
//            System.out.println("b: "+nowCount);
            return Count+nowCount;
        }else {
            if (main.getConfig().getBoolean("Set.Debug")) {
                main.getLogger().info("worldname1: "+world.getName());
                main.getLogger().info("Count: "+Count+" nowCount: "+nowCount);
            }
            data.getConfig().set("WorldData."+world.getName()+".RashMaxCount",defaultCount);
            data.saveData();
            return defaultCount;
        }
    }

    //非ban表，正常dataPut
    public static void dataPut(World world, Set<Location> locationSet) {
//        if (data.getConfig().contains("WorldData." + WorldName)) {
        List<String> locStrList = new ArrayList<>();
//        List<String> playerList = new ArrayList<>();
//        playerList.add(player.getName());
//        data.getConfig().set("WorldData." + WorldName + ".SignLocation", locStrList);

        String WorldName = world.getName();
//        String PlayerName = player.getName();

        for (Location location : locationSet) {

            String locStr = location.getX() + "," + location.getY() + "," + location.getZ();
            locStrList.add(locStr);
        }

        data.getConfig().set("WorldData." + WorldName + ".SignLocation", locStrList);
        if (WorldToLocation.get(world) == null) {
            WorldToLocation.put(world, new RashCanInformation(locationSet,  null));
        } else {
            WorldToLocation.put(world, new RashCanInformation(locationSet, WorldToLocation.get(world).getBanItemSet()));
        }
//            data.getConfig().set("WorldData." + WorldName + ".MaxKill", 0);
//            main.getLogger().info("111  "+data.getConfig().toString());
        data.saveData();
//            data.LoadData();

    }

    //加入ban表系列dataPut
    public static void dataPut(String WorldName,Set<String> NewItemString) {
//        Set<String> OldItemString = new HashSet<>(data.getConfig().getStringList("WorldData." + WorldName+".BanItem"));
//        if(!OldItemString.isEmpty()){
//            OldItemString.addAll(NewItemString);
//            data.getConfig().set("WorldData." + WorldName+".BanItem", OldItemString.toArray());
//        }else {
//        String WorldName = world.getName();
            data.getConfig().set("WorldData." + WorldName+".BanItem", NewItemString.toArray());
//        }
//        if (WorldToLocation.get(world) == null) {
//            WorldToLocation.put(world, new RashCanInformation(locationSet,  null));
//        } else {
//            WorldToLocation.put(world, new RashCanInformation(locationSet, WorldToLocation.get(world).getBanItemSet()));
//        }
//            data.getConfig().set("WorldData." + WorldName + ".MaxKill", 0);
//            main.getLogger().info("111  "+data.getConfig().toString());
        data.saveData();
//            data.LoadData();
    }

    public static void saveData() {
        try {
            Data.save(dataFile);
        } catch (IOException var2) {
            Bukkit.getLogger().info(message.find("NotFindConfig"));
        }
    }


}
