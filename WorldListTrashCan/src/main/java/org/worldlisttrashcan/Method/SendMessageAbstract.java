package org.worldlisttrashcan.Method;

import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import static org.worldlisttrashcan.IsVersion.*;
import static org.worldlisttrashcan.message.color;

public class SendMessageAbstract {

    private BukkitAudiences adventure = null;
    private JavaPlugin plugin;

//    boolean dontSupport;

    public SendMessageAbstract(JavaPlugin plugin) {
        this.plugin = plugin;
        if (!IsPaperServer) {
            this.adventure = BukkitAudiences.create(plugin);
        }
    }
    public void sendActionBar(Player player, String msg) {

        try {

            // 在1.21.5以上的版本暂时不支持rgb，因为adventure-platform-bukkit 最高支持到1.21.5
            if (Is1_21_5_21_NServer){
//                System.out.println("aaaaa1");
                player.sendActionBar(color(msg, true));
                return;
            }


            if (Is1_12_1_16Server) {
//            System.out.println("1");
//                System.out.println("aaaaa2");
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new net.md_5.bungee.api.chat.TextComponent(color(msg)));
                return;
            }
            if (this.adventure == null) {
//                System.out.println("aaaaa3");
                player.sendActionBar(color(msg, true));
            } else {
//                System.out.println(color(msg, false).toString());
//                this.adventure.player(player).sendActionBar(color(msg, false));
//                System.out.println("aaaaa4");
                this.adventure.player(player).sendActionBar(color(msg, true));
            }
        } catch (Throwable t) {
//            System.out.println("4");
//            System.out.println("aaaaa5 "+t);
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new net.md_5.bungee.api.chat.TextComponent(color(msg)));
        }
    }

    public void sendSound(Player player, String soundStr) {
        //entity.experience_orb.pickup,1,1,1
        String[] soundStrArr = soundStr.split(",");
        String sound = soundStrArr[0];
        float volume = Float.parseFloat(soundStrArr[1]);
        float pitch = Float.parseFloat(soundStrArr[2]);
        player.playSound(player.getLocation(), sound, volume, pitch);
    }

}