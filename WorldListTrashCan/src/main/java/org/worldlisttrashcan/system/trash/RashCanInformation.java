package org.worldlisttrashcan.system.trash;

import org.bukkit.Location;

import java.util.HashSet;
import java.util.Set;

public class RashCanInformation {
    Set<Location> locationSet;
    Set<String> BanItemSet;
    int RashMaxCount;
//    String PlayerName;


    public void setBanItemSet(Set<String> banItemSet) {
        BanItemSet = banItemSet;
    }

    public void setLocationSet(Set<Location> locationSet) {
        this.locationSet = locationSet;
    }

    public void setRashMaxCount(int rashMaxCount) {
        RashMaxCount = rashMaxCount;
    }

    public void AddLocation(Location location){
        this.locationSet.add(location);
    }
    public RashCanInformation(Set<Location> locationSet, Set<String> BanItemSet){
        this.locationSet = locationSet;
//        BanItemList = new ArrayList<>();
        if(BanItemSet==null){
            this.BanItemSet = new HashSet<>();
        }else {
            this.BanItemSet = BanItemSet;
        }

//        if(data.getConfig().getInt()){
//
//        }else {
//
//        }

//        this.PlayerName = playerName;
    }

    public Set<String> getBanItemSet() {
        return BanItemSet;
    }

    public Set<Location> getLocationSet() {
        return locationSet;
    }

//    public String getPlayerName() {
//        return PlayerName;
//    }

}
