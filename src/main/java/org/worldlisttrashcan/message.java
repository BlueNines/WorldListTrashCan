package org.worldlisttrashcan;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
//import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.worldlisttrashcan.Method.SendMessageAbstract;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.worldlisttrashcan.IsVersion.compareVersions;
import static org.worldlisttrashcan.WorldListTrashCan.*;

public class message {

    private static File messageFile;

    private static ConfigurationSection configurationSection;
    private static FileConfiguration Message;

    public static FileConfiguration getConfig() {
        return Message;
    }


    public static String pluginTitle = "&7|&e⭐&#a1b2ff世界垃圾桶&e⭐&7|";



    static String flag = !compareVersions("1.16.0") ? "":"1.12.2";


    static public String chanceMessage;
    public static void reloadMessage() {

//        System.out.println("flag "+flag);



        //new File(main.getDataFolder(), "data" + File.separator + "data.yml");
        messageFile = new File(main.getDataFolder(), "message"
                //如果版本小于1.16.5，则使用另一种低版本专用的语言文件
//                +flag
                + File.separator +chanceMessage);



        if (!messageFile.exists())
            main.saveResource("message"+ File.separator +chanceMessage, false);
        Message = YamlConfiguration.loadConfiguration(messageFile);

//        ConfigStringReplace(message.getConfig(),"&","§");





        pluginTitle = message.find("PluginTitle");
        ConfigStringReplace(message.getConfig(),"%PluginTitle%",pluginTitle);
//        message.saveData();

    }

    public static void consoleSay(String message){
        Bukkit.getConsoleSender().sendMessage(color(pluginTitle+" "+message));
    }
    public static void consoleSay(CommandSender commandSender, String message) {
        if (commandSender == null) {
            Bukkit.getConsoleSender().sendMessage(color( message.replace("%PluginTitle%",pluginTitle)));
        } else {
            commandSender.sendMessage(color( message.replace("%PluginTitle%",pluginTitle)));
        }

    }



    public static void ConfigStringReplace(ConfigurationSection config, String target, String replacement) {
        for (String key : config.getKeys(false)) {
            if (config.isConfigurationSection(key)) {
                // 递归处理子节
                ConfigStringReplace(config.getConfigurationSection(key), target, replacement);
            } else if (config.isString(key)) {
                // 替换字符串值
                String originalValue = config.getString(key);
                String modifiedValue = color(originalValue.replace(target, replacement));
                config.set(key, modifiedValue);
            }
        }
    }

    public static void ConfigStringReplace(ConfigurationSection config) {
        for (String key : config.getKeys(false)) {
            if (config.isConfigurationSection(key)) {
                // 递归处理子节
                ConfigStringReplace(config.getConfigurationSection(key));
            } else if (config.isString(key)) {
                // 替换字符串值
                String originalValue = config.getString(key);
                String modifiedValue = color(originalValue);
                config.set(key, modifiedValue);
            }
        }
    }

    public static String color(String msg) {
        msg = msg.replaceAll("&#", "#");
        Pattern pattern = Pattern.compile("(&#|#|&)[a-fA-F0-9]{6}");
        Matcher matcher = pattern.matcher(msg);
        while (matcher.find()) {
            String hexCode = msg.substring(matcher.start(), matcher.end());
            String replaceAmp = hexCode.replaceAll("&#", "x");
            String replaceSharp = replaceAmp.replace('#', 'x');



            char[] ch = replaceSharp.toCharArray();
            StringBuilder builder = new StringBuilder();
            for (char c : ch)
                builder.append("&" + c);
            msg = msg.replace(hexCode, builder.toString());
            matcher = pattern.matcher(msg);
        }
        return ChatColor.translateAlternateColorCodes('&', msg);
    }
    public static Component color(String msg,boolean b) {

        //msg的处理
        msg = msg.replaceAll("&#", "##");
        msg = msg.replaceAll("&", "§");
        msg = msg.replaceAll("##", "&#");


        if(b){
            // 正则表达式匹配颜色代码和文本片段
            Pattern pattern = Pattern.compile("(&#[0-9a-fA-F]{6})([^&]*)");
            Matcher matcher = pattern.matcher(msg);

            // 创建一个组件构建器
            Component component = Component.empty();
            boolean flag = false;

            while (matcher.find()) {
                flag = true;
                String colorCode = matcher.group(1).substring(1); // 提取颜色代码并去掉&
                String text = matcher.group(2); // 提取文本片段

                // 生成颜色并应用到文本片段上
                TextColor color = TextColor.fromHexString(colorCode);
                Component coloredText = Component.text(text, Style.style(color));

                // 将有颜色的文本片段添加到组件中
                component = component.append(coloredText);
            }
            if (!flag){
                return Component.text(msg.replace("&","§"));
            }
//            ChatColor.translateAlternateColorCodes('&', xxx);
            return component;
        }
        return null;

//        return MiniMessage.miniMessage().deserialize(msg);
    }

    public static void AllMessageLoad(){
        List<String> LangList = new ArrayList<>();
        LangList.add("message_zh.yml");LangList.add("message_en.yml");LangList.add("message_zh_TW.yml");
        for (String TheMessage : LangList) {
            File LangFile = new File(main.getDataFolder(), "message"+ File.separator + TheMessage);
            if (!LangFile.exists())
                main.saveResource("message"
                        + File.separator
                        +flag
                        +TheMessage, false);

                //保存之后把名字改了
                renameFile(new File(main.getDataFolder(), "message"+ File.separator +flag+ TheMessage),flag);





        }

    }


    /**
     * 将指定文件名中的某个字符串部分移除，并重命名文件。
     *
     * @param file           要重命名的文件
     * @param stringToRemove 要从文件名中移除的字符串
     * @return 如果文件重命名成功，则返回 true；否则返回 false
     */
    public static boolean renameFile(File file, String stringToRemove) {
        // 检查文件是否存在
        if (!file.exists()) {
//            System.out.println("文件不存在。");
            return false;
        }

        // 获取文件的当前名称
        String currentFileName = file.getName();

        // 移除文件名中的指定字符串部分
        String newFileName = currentFileName.replace(stringToRemove, "");

        // 检查移除后的新文件名是否与当前文件名不同
        if (newFileName.equals(currentFileName)) {
//            System.out.println("指定的字符串不在文件名中。");
            return false;
        }

        // 获取文件的父目录路径
        String parentPath = file.getParent();

        // 创建新的文件对象
        File newFile = new File(parentPath, newFileName);

        // 尝试重命名文件
        return file.renameTo(newFile);
    }

    public static String find(String path){
        if(message.getConfig().getString(path)!=null&&!message.getConfig().getString(path).isEmpty()){
            return message.getConfig().getString(path);
        }else {
            main.getLogger().info(message.find("NotFindMessageSlave").replace("%path%",path));
            return " ";
        }
    }

    public static void saveData() {
        try {
            Message.save(messageFile);
        } catch (IOException var2) {
            Bukkit.getLogger().info(message.find("NotFindMessage"));
        }
    }
}
