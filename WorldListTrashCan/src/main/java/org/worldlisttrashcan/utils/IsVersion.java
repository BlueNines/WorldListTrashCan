package org.worldlisttrashcan.utils;

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
    public static boolean Is1_21_1_21_XServer;
    public static boolean Is1_21_1_21_5Server;
    public static boolean Is1_21_5_21_NServer;


    public static Map<String,Boolean> VersionToBoolean = new HashMap<>();

    //如果版本小于1.13.0
    //compareVersions("1.13.0")
//    public static boolean isServerVersionLowerThan(String version) {
//        if(VersionToBoolean.get(version)==null){
//            String string = Bukkit.getVersion();
////            System.out.println("getVersion "+string);
//            Pattern pattern = Pattern.compile("\\(MC: (\\d+\\.\\d+\\.\\d+)\\)");
//            Matcher matcher = pattern.matcher(string);
//
//            if (matcher.find()) {
//                String minecraftVersion = matcher.group(1);
//                String[] parts1 = minecraftVersion.split("\\.");
//                String[] parts2 = (version).split("\\.");
//
//                int length = Math.max(parts1.length, parts2.length);
//                for (int i = 0; i < length; i++) {
//                    int v1 = (i < parts1.length) ? Integer.parseInt(parts1[i]) : 0;
//                    int v2 = (i < parts2.length) ? Integer.parseInt(parts2[i]) : 0;
//
//                    //如果目前版本  对于  需求版本
//                    if (v1 < v2) {
//                        VersionToBoolean.put(version,true);
//                        return true;
//                    }
//                }
//                // Versions are equal
//            }
//            VersionToBoolean.put(version,false);
//            return false;
//        }else {
//            return VersionToBoolean.get(version);
//        }
//
//    }

    public static boolean isServerVersionLowerThan(String version) {
        if (VersionToBoolean.containsKey(version)) {
            return VersionToBoolean.get(version);
        }

        String serverVersionString = Bukkit.getVersion(); // 例如: "git-Paper-403 (MC: 1.21)"
        Pattern pattern = Pattern.compile("\\(MC: (\\d+(\\.\\d+){0,2})\\)");
        Matcher matcher = pattern.matcher(serverVersionString);

        boolean isLower = false;

        if (matcher.find()) {
            String serverVersion = matcher.group(1);
            String[] parts1 = serverVersion.split("\\.");
            String[] parts2 = version.split("\\.");

            // 补齐版本号到三个部分
            int maxLength = Math.max(parts1.length, parts2.length);
            int[] v1 = new int[3];
            int[] v2 = new int[3];

            for (int i = 0; i < 3; i++) {
                v1[i] = (i < parts1.length) ? Integer.parseInt(parts1[i]) : 0;
                v2[i] = (i < parts2.length) ? Integer.parseInt(parts2[i]) : 0;
            }

            // 开始比较
            for (int i = 0; i < 3; i++) {
                if (v1[i] < v2[i]) {
                    isLower = true;
                    break;
                } else if (v1[i] > v2[i]) {
                    break;
                }
            }
        }

        VersionToBoolean.put(version, isLower);
        return isLower;
    }

}
