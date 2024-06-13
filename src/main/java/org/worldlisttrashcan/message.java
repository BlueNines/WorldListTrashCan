package org.worldlisttrashcan;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.worldlisttrashcan.WorldListTrashCan.*;

public class message {

    private static File messageFile;

    private static ConfigurationSection configurationSection;
    private static FileConfiguration Message;

    public static FileConfiguration getConfig() {
        return Message;
    }






    static public String chanceMessage;
    public static void reloadMessage() {
        //new File(main.getDataFolder(), "data" + File.separator + "data.yml");
        messageFile = new File(main.getDataFolder(), "message"+ File.separator +chanceMessage);
        if (!messageFile.exists())
            main.saveResource("message"+ File.separator +chanceMessage, false);
        Message = YamlConfiguration.loadConfiguration(messageFile);

//        ConfigStringReplace(message.getConfig(),"&","§");





        ConfigStringReplace(message.getConfig(),"%PluginTitle%",message.find("PluginTitle"));
//        message.saveData();

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

    public static void AllMessageLoad(){
        List<String> LangList = new ArrayList<>();
        LangList.add("message_zh.yml");LangList.add("message_en.yml");LangList.add("message_zh_TW.yml");
        for (String TheMessage : LangList) {
            File LangFile = new File(main.getDataFolder(), "message"+ File.separator + TheMessage);
            if (!LangFile.exists())
                main.saveResource("message"+ File.separator +TheMessage, false);
        }









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
