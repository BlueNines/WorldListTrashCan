package org.worldlisttrashcan.system.autotrash;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.player.PlayerAttemptPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import static org.worldlisttrashcan.system.autotrash.AutoTrashListener.NoWorldTrashCanEnterPersonalTrashCan;
import static org.worldlisttrashcan.system.autotrash.AutoTrashListener.VersionFlag;
import static org.worldlisttrashcan.WorldListTrashCan.main;

public class HeightVersionPlayerDropItemListener implements Listener {








    //玩家即将捡起物品
    @EventHandler
    public void PlayerAttemptPickupItemEvent(PlayerAttemptPickupItemEvent event) {
        ItemStack itemStack = event.getItem().getItemStack();
        RemoveItemTag(itemStack);
    }

    //漏斗捡起物品
    @EventHandler
    public void InventoryPickupItemEvent(InventoryPickupItemEvent event) {
        ItemStack itemStack = event.getItem().getItemStack();
        RemoveItemTag(itemStack);
    }

    //背包切换物品
    @EventHandler
    public void InventoryClick(InventoryClickEvent event) {
        ItemStack itemStack = event.getCurrentItem();
        RemoveItemTag(itemStack);
        itemStack = event.getCursor();
        RemoveItemTag(itemStack);
        itemStack = event.getWhoClicked().getItemOnCursor();
        RemoveItemTag(itemStack);
    }

    //移除物品标签
    public static void RemoveItemTag(ItemStack itemStack) {
        if (itemStack == null)
            return;
        ItemMeta meta = itemStack.getItemMeta();
        if (meta != null) {
            NamespacedKey namespacedKey = new NamespacedKey(main, "PlayerUUID");
            meta.getPersistentDataContainer().remove(namespacedKey);
            itemStack.setItemMeta(meta);
        }
    }

    @EventHandler
    public void PlayerDropItemEvent(PlayerDropItemEvent event) {
        Item item = event.getItemDrop();

        Player player = event.getPlayer();

        //测试Vault插件是否可用
//        testVault(player);

        // 当版本大于1.14 且 NoWorldTrashCanEnterPersonalTrashCan 打开
        // 将玩家UUID存入Item中
        if (NoWorldTrashCanEnterPersonalTrashCan && VersionFlag) {
            ItemStack itemStack = item.getItemStack();

            ItemMeta meta = itemStack.getItemMeta();
            NamespacedKey namespacedKey = new NamespacedKey(main, "PlayerUUID");

            meta.getPersistentDataContainer().set(namespacedKey, PersistentDataType.STRING, player.getUniqueId().toString());
            itemStack.setItemMeta(meta);
            item.setItemStack(itemStack);

        }
    }

}
