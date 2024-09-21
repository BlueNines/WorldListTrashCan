package org.worldlisttrashcan;

import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IsVersion {
    //compareVersions(1.12.2)
    //如果版本小于1.12.2


    public static boolean IsFoliaServer;
    public static boolean IsPaperServer;
    public static boolean Is1_12_1_16Server;
    public static boolean Is1_16_1_20Server;


    public static Map<String,Boolean> VersionToBoolean = new HashMap<>();

    //如果版本小于1.13.0
    //compareVersions("1.13.0")
    public static boolean compareVersions(String version) {
        if(VersionToBoolean.get(version)==null){
            String string = Bukkit.getVersion();
            Pattern pattern = Pattern.compile("\\(MC: (\\d+\\.\\d+\\.\\d+)\\)");
            Matcher matcher = pattern.matcher(string);

            if (matcher.find()) {
                String minecraftVersion = matcher.group(1);
                String[] parts1 = minecraftVersion.split("\\.");
                String[] parts2 = (version).split("\\.");

                int length = Math.max(parts1.length, parts2.length);
                for (int i = 0; i < length; i++) {
                    int v1 = (i < parts1.length) ? Integer.parseInt(parts1[i]) : 0;
                    int v2 = (i < parts2.length) ? Integer.parseInt(parts2[i]) : 0;

                    //如果目前版本  对于  需求版本
                    if (v1 < v2) {
                        VersionToBoolean.put(version,true);
                        return true;
                    }
                }
                // Versions are equal
            }
            VersionToBoolean.put(version,false);
            return false;
        }else {
            return VersionToBoolean.get(version);
        }

    }
}
