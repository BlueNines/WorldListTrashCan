package org.worldlisttrashcan.system.trash;
import org.bukkit.Location;
import org.bukkit.block.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class ChestGetInventory {
    public static Inventory getInventory(Block block) {
        BlockState blockState = block.getState();
        if (blockState instanceof InventoryHolder) {
            Inventory inventory = ((InventoryHolder) blockState).getInventory();
            return inventory;
        }
        return null;
    }


    public static Inventory getDoubleChestInventory(Location singleChestLocation) {
        Block block = singleChestLocation.getBlock();
//        if (block.getType() != Material.CHEST) {
//            System.out.println("这个是容器1");
            BlockState blockState = block.getState();


            if (blockState instanceof InventoryHolder) {
//                System.out.println("这个是容器2");

                Inventory inventory = ((InventoryHolder) blockState).getInventory();

                return inventory;
            }

            return null;
//        }
//
//        Chest singleChest = (Chest) singleChestLocation.getBlock().getState();
//        if (singleChest == null) {
//            return null;
//        }
//
//        return getDoubleChest(singleChest);
    }

    private static Inventory getDoubleChest(Chest chest) {
        // 获取相邻的箱子，如果不存在则返回单个箱子
        if (chest.getInventory().getHolder() instanceof DoubleChest) {
            DoubleChest doubleChest = (DoubleChest) chest.getInventory().getHolder();
            return doubleChest.getInventory();
        } else {
            return chest.getInventory();
        }

    }


}
