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
import static org.worldlisttrashcan.message.consoleSay;

public class data {

    private static File dataFile;

    private static FileConfiguration Data;


    public static FileConfiguration getConfig() {
        return Data;
    }



    public static void LoadData() {
        dataFile = new File(main.getDataFolder(), "data" + File.separator + "data.yml");
        if (!dataFile.exists())
            main.saveResource("data" + File.separator + "data.yml",false);

        Data = YamlConfiguration.loadConfiguration(dataFile);
        

        WorldToLocation.clear();


        ConfigurationSection WorldDataSection = Data.getConfigurationSection("WorldData");
        boolean flag = false;
        for (String WorldName : WorldDataSection.getKeys(false)) {
            Set<String> locStrSet = new HashSet<>(getConfig().getStringList("WorldData."+WorldName+".SignLocation"));


            Set<String> BanItemSet = new HashSet<>(getConfig().getStringList("WorldData." + WorldName + ".BanItem"));
            World world = Bukkit.getWorld(WorldName);
            Set<Location> LocationSet = new HashSet<>();

            if (!locStrSet.isEmpty()){
                for (String locStr : locStrSet) {
                    String[] strings = locStr.split(",");



                    if(world==null||strings.length!=3){
//                    consoleSay(ChatColor.RED+"配置文件中有一个空的世界名或者不正常的坐标");
//                    consoleSay(ChatColor.RED+"世界名为："+WorldName+"坐标为："+locStr);
                        consoleSay(message.find("ConfigError").replace("%world%",WorldName).replace("%location%",locStr));
                        flag = true;
                        continue;
                    }
                    double x = Double.parseDouble(strings[0]);
                    double y = Double.parseDouble(strings[1]);
                    double z = Double.parseDouble(strings[2]);
                    Location location = new Location(world,x,y,z);
                    LocationSet.add(location);
                }
            }


            WorldToLocation.put(world,new RashCanInformation(LocationSet,BanItemSet));

        }
        if(flag){
            consoleSay(message.find("HaveHomePlugin"));
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
                consoleSay("worldname2: "+world.getName());
                consoleSay("Count: "+Count+" nowCount: "+nowCount);

            }
            data.getConfig().set("WorldData."+world.getName()+".RashMaxCount",Count+nowCount);
            data.saveData();
            return Count+nowCount;
        }else {
            if (main.getConfig().getBoolean("Set.Debug")) {
                consoleSay("worldname1: "+world.getName());
                consoleSay("Count: "+Count+" nowCount: "+nowCount);
            }
            data.getConfig().set("WorldData."+world.getName()+".RashMaxCount",defaultCount);
            data.saveData();
            return defaultCount;
        }
    }

    //非ban表，正常dataPut
    public static void dataPut(World world, Set<Location> locationSet) {
        List<String> locStrList = new ArrayList<>();

        String WorldName = world.getName();

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
        data.saveData();

    }

    //加入ban表系列dataPut
    public static void dataPut(String WorldName,Set<String> NewItemString) {
            data.getConfig().set("WorldData." + WorldName+".BanItem", NewItemString.toArray());
        data.saveData();
    }

    public static void saveData() {
        try {
            Data.save(dataFile);
        } catch (IOException var2) {
            Bukkit.getLogger().info(message.find("NotFindConfig"));
        }
    }


}
