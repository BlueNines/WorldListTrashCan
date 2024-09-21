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
        if (!IsPaperServer) {
            this.adventure = BukkitAudiences.create(plugin);
        }
    }
    public void sendActionBar(Player player, String msg) {

        try {
            if (Is1_12_1_16Server) {
//            System.out.println("1");
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new net.md_5.bungee.api.chat.TextComponent(color(msg)));
                return;
            }
            if (this.adventure == null) {
                player.sendActionBar(color(msg, true));
            } else {
//                System.out.println(color(msg, false).toString());
//                this.adventure.player(player).sendActionBar(color(msg, false));
                this.adventure.player(player).sendActionBar(color(msg, true));
            }
        } catch (Throwable t) {
//            System.out.println("4");
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new net.md_5.bungee.api.chat.TextComponent(color(msg)));
        }
    }

}