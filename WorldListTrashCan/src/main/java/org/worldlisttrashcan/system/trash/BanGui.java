package org.worldlisttrashcan.system.trash;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.worldlisttrashcan.utils.Message;

public class BanGui implements InventoryHolder {

    @Override
    public Inventory getInventory() {
        return Bukkit.createInventory(this, 54, "全局世界垃圾桶物品Ban表");
    }

    public Inventory getInventory(Player player) {
        return Bukkit.createInventory(this, 54, Message.find("BanChestInventoryName").replace("%world%",player.getWorld().getName()));
    }
    public Inventory getGlobalInventory(Player player) {
        return Bukkit.createInventory(this, 54, Message.find("GlobalBanChestInventoryName"));
    }
}
