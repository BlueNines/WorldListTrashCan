package org.worldlisttrashcan.utils;

import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Enemy;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.worldlisttrashcan.utils.IsVersion.*;
import static org.worldlisttrashcan.WorldListTrashCan.main;
import static org.worldlisttrashcan.WorldListTrashCan.papi;

public class Method {
    // 执行命令
    public static void useCommand(Player player,String Command){
        if (Command!=null && !Command.isEmpty()) {
            // 执行命令
            if (IsFoliaServer) {
                Bukkit.getGlobalRegionScheduler().run(main, scheduledTask -> {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), Command
                            .replace("%player%", player.getName()));
                });
            } else {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), Command
                                .replace("%player%", player.getName()));
                    }
                }.runTask(main);
            }
        }
    }



    //获取物品完整描述字符串
    public static String getItemStackAllString(ItemStack itemStack,int amount){
        Material material = itemStack.getType();

        String item = "[material:"+material+" x "+amount+"]";

        ItemMeta meta = itemStack.getItemMeta();
        try {
            if (meta != null) {

                String customName = "";
                try{
                    if(Is1_21_1_21_XServer){
                        // 获取显示名（新版API）
//                        if (meta.hasDisplayName()) {

//                            if (IsPaperServer) {
//                                try {
//                                    Component displayNameComponent = meta.displayName();
//                                    if (displayNameComponent != null) {
////                                        customName = "[name:"+PlainTextComponentSerializer.plainText().serialize(displayNameComponent)+"]";
//                                        // 使用服务器原生的 PlainTextComponentSerializer
//                                        String plainName = net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer.plainText()
//                                                .serialize((net.kyori.adventure.text.Component) displayNameComponent);
//                                        customName = "[name:" + plainName + "]";
//                                    }
//                                }catch (Throwable t){
//                                    String displayName = meta.getDisplayName();
//                                    customName = "[name:"+displayName+"]";
//                                }
//                            }else {
//                                String displayName = meta.getDisplayName();
//                                customName = "[name:"+displayName+"]";
//                            }
                        }
//                    }else {
                        if(meta.hasDisplayName()){
                            customName = "[name:"+meta.getDisplayName()+"]";
                        }
//                    }
                }catch (Throwable t){
                    Message.consoleSay("&c获取物品名称出错 "+t);
                    customName = "[name:errorName]";
                }
                item += customName;

                List<String> loreList = new ArrayList<>();
//                if(Is1_21_1_21_XServer){
//                    // 获取 lore（新版API 仍然是 List<Component>）
//                    if (meta.hasLore()) {
//                        List<Component> lore = meta.lore();
//                        if (lore != null) {
//                            for (Component line : lore) {
//                                loreList.add(PlainTextComponentSerializer.plainText().serialize(line));
//                            }
//                        }
//                    }
//                }else {
                    //旧版API
                    if (meta.hasLore()) {
                        for (String lore : meta.getLore()) {
                            loreList.add(lore);
                        }
                    }
//                }
                if (!loreList.isEmpty()){
                    item += "[lore:"+loreList.toString()+"]";
                }


                List<String> enchantList = new ArrayList<>();

                if(Is1_16_1_20Server){
                    // 附魔 新API
                    Map<Enchantment, Integer> enchants = itemStack.getEnchantments();
                    for (Map.Entry<Enchantment, Integer> entry : enchants.entrySet()) {
                        enchantList.add(entry.getKey().getKey().getKey() + ":" + entry.getValue());
                        // ↑ 获取 enchantment 的简短 ID，例如 "sharpness:5"
                    }
                }else {
                    Map<Enchantment, Integer> enchants = meta.getEnchants();
                    for (Enchantment enchantment : enchants.keySet()) {
                        int level = enchants.get(enchantment);
                        enchantList.add(enchantment.getName() + ":" + level);
                    }
                }
                if (!enchantList.isEmpty()){
                    item += "[enchant:"+enchantList.toString()+"]";
                }
            }
        } catch (Throwable t) {
            Message.consoleSay("&c获取物品描述字符串时出错，但是不影响功能，请将该日志发送给作者解决，也可以选择无视它"+t);
        }
        return item;
    }

    //获取物品完整描述字符串
    public static String getItemStackAllString(ItemStack itemStack){
        return getItemStackAllString(itemStack,itemStack.getAmount());
    }







    private static List<String> monsterTypes;

//    烈焰人 (BLAZE)
//    沼骸 (GUARDIAN)
//    旋风人 (VEX)
//    苦力怕 (CREEPER)
//    远古守卫者 (ELDER_GUARDIAN)
//    末影螨 (ENDERMITES)
//    唤魔者 (EVOKER)
//    恶魂 (WITHER_SKELETON)
//    守卫者 (GUARDIAN)
//    疣猪兽 (HOGLIN)
//    尸壳 (ZOMBIE_VILLAGER)
//    岩浆怪 (MAGMA_CUBE)
//    幻翼 (PHANTOM)
//    猪灵蛮兵 (PIGLIN_BRUTE)
//    掠夺者 (RAVAGER)
//    劫掠兽 (PIGLIN)
//    潜影贝 (SHULKER)
//    蠹虫 (SILVERFISH)
//    骷髅 (SKELETON)
//    史莱姆 (SLIME)
//    流浪者 (WANDERING_TRADER)
//    恼鬼 (VEX)
//    卫道士 (VINDICATOR)
//    监守者 (WITHER)
//    女巫 (WITCH)
//    凋灵骷髅 (WITHER_SKELETON)
//    僵尸疣猪兽 (ZOMBIFIED_PIGLIN)
//    僵尸 (ZOMBIE)
//    僵尸村民 (ZOMBIE_VILLAGER)
//    嘲讽者 (GOAT)（假设“嘎枝”是指嘲讽者）
//    溺尸 (DROWNED)
//    末影人 (ENDERMAN)
//    蜘蛛 (SPIDER)
//    洞穴蜘蛛 (CAVE_SPIDER)
//    僵尸猪灵 (ZOMBIFIED_PIGLIN)

    static {
        monsterTypes = new ArrayList<>();
        monsterTypes.add("BLAZE");
        monsterTypes.add("GUARDIAN");
        monsterTypes.add("VEX");
        monsterTypes.add("CREEPER");
        monsterTypes.add("ELDER_GUARDIAN");
        monsterTypes.add("ENDERMITE");
        monsterTypes.add("EVOKER");
        monsterTypes.add("WITHER_SKELETON");
        monsterTypes.add("GUARDIAN");
        monsterTypes.add("HOGLIN");
        monsterTypes.add("ZOMBIE_VILLAGER");
        monsterTypes.add("MAGMA_CUBE");
        monsterTypes.add("PHANTOM");
        monsterTypes.add("PIGLIN_BRUTE");
        monsterTypes.add("RAVAGER");
        monsterTypes.add("PIGLIN");
        monsterTypes.add("SHULKER");
        monsterTypes.add("SILVERFISH");
        monsterTypes.add("SKELETON");
        monsterTypes.add("SLIME");
        monsterTypes.add("WANDERING_TRADER");
        monsterTypes.add("VEX");
        monsterTypes.add("VINDICATOR");
        monsterTypes.add("WITHER");
        monsterTypes.add("WITCH");
        monsterTypes.add("WITHER_SKELETON");
        monsterTypes.add("ZOMBIFIED_PIGLIN");
        monsterTypes.add("ZOMBIE");
        monsterTypes.add("ZOMBIE_VILLAGER");
        monsterTypes.add("GOAT");
        monsterTypes.add("DROWNED");
        monsterTypes.add("ENDERMAN");
        monsterTypes.add("SPIDER");
        monsterTypes.add("CAVE_SPIDER");
        monsterTypes.add("ZOMBIFIED_PIGLIN");
    }

    public static Boolean hasEnemyClass = false;

    //    //判断实体是否是怪物
    public static boolean isMonster(Entity entity) {
        String entityType = entity.getType().toString();
        if (monsterTypes.contains(entityType.toLowerCase()))
            return true;
        else if (entity instanceof Monster) {
            return true;
        }
        if (hasEnemyClass) {
//            System.out.println("hasEnemyClass hasEnemyClass hasEnemyClass");
            return entity instanceof Enemy;
        }

        return false;
    }






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
            Message.consoleSay("&a检测到服务器拥有Folia的API，正在适用目前最佳的方法");
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
            Message.consoleSay("&a检测到服务器拥有Paper的API，正在适用目前最佳的方法");
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


    //将字符串替换papi变量
    public static String papiReplace(String message, Player player) {
        if (papi==null) {
            return message;
        }
        //如果papi有的话
        return PlaceholderAPI.setPlaceholders(player, message);
    }




}
