package org.worldlisttrashcan.Method;

import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
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
//        dontSupport = true;
        if (!IsPaperServer) {
            this.adventure = BukkitAudiences.create(plugin);
        }
    }

//    public void sendMessage(Player player, Component msg) {
//        if (this.adventure == null) {
//            player.sendMessage(msg);
//        } else {
//            this.adventure.player(player).sendMessage(msg);
//        }
//    }

//    public void sendMessage(CommandSender sender, Component msg) {
//        if (this.adventure == null) {
//            sender.sendMessage(msg);
//        } else {
//            this.adventure.sender(sender).sendMessage(msg);
//        }
//    }

//    public void broadcast(Component msg) {
//        if (this.adventure == null) {
//            plugin.getServer().broadcast(msg);
//        } else {
//            adventure.all().sendMessage(msg);
//        }
//    }

    //    public void sendActionBar(Player player, Component msg) {
//        if (this.adventure == null) {
////            player.sendActionBar(msg);
//            color(String.valueOf(msg),true);
//            player.sendActionBar(msg);
//        } else {
////            this.adventure.player(player).sendActionBar(msg);
//            this.adventure.player(player).sendActionBar(msg);
//        }
//    }
    public void sendActionBar(Player player, String msg) {
//                        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(
//                                ActionBarIntToMessage.get(count)
//                                        .replace("%ItemSum%", GlobalTrashItemSum + "")
//                                        .replace("%EntitySum%", EntitySum + "")
//                                        .replace("%ClearGlobalCount%", EveryClearGlobalTrash - ClearCount + "")));
        try {
            if (Is1_12_1_16Server) {
//            System.out.println("1");
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new net.md_5.bungee.api.chat.TextComponent(color(msg)));
                return;
            }


            if (this.adventure == null) {

//            if(dontSupport){

                player.sendActionBar(color(msg, true));

////                player.sendActionBar(color(msg));
//                    dontSupport = false;

//            }else {
//                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new net.md_5.bungee.api.chat.TextComponent(color(msg)));
//
//            }


            } else {
//            this.adventure.player(player).sendActionBar(msg);
//            System.out.println("3");
                this.adventure.player(player).sendActionBar(color(msg, true));
            }
        } catch (Throwable t) {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new net.md_5.bungee.api.chat.TextComponent(color(msg)));
        }
    }

}