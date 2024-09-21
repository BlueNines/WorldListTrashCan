package org.worldlisttrashcan.Method;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.worldlisttrashcan.message;

import static org.worldlisttrashcan.WorldListTrashCan.main;
import static org.worldlisttrashcan.message.consoleSay;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
public class Method {















    /**
     * 检查类是否存在
     *
     * @param className 要检查的类的全限定名
     * @return 如果类存在，则返回 true，否则返回 false
     */
    public static boolean isClassPresent(String className) {
        try {
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
    public static boolean AutoCheckFoliaServer(){

        // 插件启动逻辑
        if (isClassPresent("io.papermc.paper.threadedregions.scheduler.FoliaRegionScheduler")) {
//            Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN+"检测到服务器拥有Folia的API，正在适用目前最佳的方法");
//            Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN+"检测到服务器拥有Folia的API，正在适用目前最佳的方法");
            message.consoleSay("&a检测到服务器拥有Folia的API，正在适用目前最佳的方法");
            return true;

        } else {
//            Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN+"插件正在自动适应");

        }

        return false;
    }
    public static boolean AutoCheckPaperServer(){

        // 插件启动逻辑
//        if (isClassPresent("io.papermc.paper.event.entity.EntityMoveEvent")) {
        if (isClassPresent("io.papermc.paper.threadedregions.scheduler.ScheduledTask")) {
//            Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN+"检测到服务器拥有Paper的API，正在适用目前最佳的方法");
            message.consoleSay("&a检测到服务器拥有Paper的API，正在适用目前最佳的方法");
            return true;

        } else {
//            Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN+"插件正在自动适应");
        }

        return false;
    }
    public static boolean AutoCheckEntityMoveEventServer(){

        // 插件启动逻辑
        if (isClassPresent("io.papermc.paper.event.entity.EntityMoveEvent")) {
//        if (isClassPresent("io.papermc.paper.threadedregions.scheduler.ScheduledTask")) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN+"检测到服务器拥有Paper的EntityMoveEvent，正在适用目前最佳的方法");
//            message.consoleSay("&a检测到服务器拥有Paper的API，正在适用目前最佳的方法");
            return true;

        } else {
//            Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN+"插件正在自动适应");
        }

        return false;
    }




}
