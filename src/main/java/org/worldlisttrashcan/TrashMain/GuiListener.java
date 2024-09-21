package org.worldlisttrashcan.TrashMain;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.worldlisttrashcan.WorldListTrashCan.GlobalTrashList;


public class GuiListener implements Listener {

    List<Integer> int1_9 = Arrays.asList(45, 46, 47, 48, 49, 50, 51, 52, 53);

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
//        System.out.println("0");
        // 检查事件是否发生在我们的菜单中

//        if (event.getView().getTitle().equals(mainGui.MainMenuName) || event.getView().getTitle().equals(mainGui.cauldronMenu.MenuName) ||
//                event.getView().getTitle().equals(mainGui.leatherChestplateMenu.MenuName) || event.getView().getTitle().equals(mainGui.netheriteChestplateMenu.MenuName) ||
//                event.getView().getTitle().equals(mainGui.oakSaplingMenu.MenuName) || event.getView().getTitle().equals(mainGui.tippedArrowMenu.MenuName)
//        ) {
        Player player = (Player) event.getWhoClicked();
        Inventory inventory = event.getInventory();
        int PageIndex = GlobalTrashList.indexOf(inventory);
        if(PageIndex!=-1){
            int count = event.getSlot();
            if(int1_9.contains(count)){
//                System.out.println("1");
                event.setCancelled(true);
                //上一页
                if(count==46&&inventory.getItem(count).getType()==Material.ARROW){
                    player.openInventory(GlobalTrashList.get(PageIndex-1));
                //下一页
                }else if(count==52&&inventory.getItem(count).getType()==Material.ARROW){
                    player.openInventory(GlobalTrashList.get(PageIndex+1));
                }
            }
            // 取消玩家点击事件


//            Player player = (Player) event.getWhoClicked();
//            World world = player.getWorld();
//            Location location = player.getLocation();
//            // 检查点击的是哪个物品
//            ItemStack clickedItem = event.getCurrentItem();
//            if (clickedItem != null) {
//                if (event.getView().getTitle().equals(mainGui.getMenuName())) {
//                    if (clickedItem.getType() == Material.CAULDRON) {
//                        player.openInventory(mainGui.cauldronChangeMenu.getInventory());
//                        world.playSound(location, Sound.ENTITY_SPLASH_POTION_THROW, 1.0f, 1.0f);
//                        return;
//                    } else if (clickedItem.getType() == Material.OAK_SAPLING) {
//                        player.openInventory(mainGui.oakSaplingMenu.getInventory());
//                        world.playSound(location, Sound.ENTITY_SPLASH_POTION_THROW, 1.0f, 1.0f);
//                        return;
//                    } else if (clickedItem.getType() == Material.TIPPED_ARROW) {
//                        player.openInventory(mainGui.tippedArrowMenu.getInventory());
//                        world.playSound(location, Sound.ENTITY_SPLASH_POTION_THROW, 1.0f, 1.0f);
//                        return;
//                    } else if (clickedItem.getType() == Material.LEATHER_CHESTPLATE) {
//                        player.openInventory(mainGui.leatherChestplateMenu.getInventory());
//                        world.playSound(location, Sound.ENTITY_SPLASH_POTION_THROW, 1.0f, 1.0f);
//                        return;
//                    } else if (clickedItem.getType() == Material.NETHERITE_CHESTPLATE) {
//                        player.openInventory(mainGui.netheriteChestplateMenu.getInventory());
//                        world.playSound(location, Sound.ENTITY_SPLASH_POTION_THROW, 1.0f, 1.0f);
//                        return;
//                    }
//
//                }
//                if (event.getView().getTitle().equals(mainGui.armourMenu.getMenuName())) {
//                    if (clickedItem.getType() == Material.BARRIER) {
//                        player.openInventory(mainGui.getCauldronChangeMenu().getInventory());
//                        world.playSound(location, Sound.ENTITY_SPLASH_POTION_THROW, 1.0f, 1.0f);
//                        return;
//                    }
//                }
//                if (event.getView().getTitle().equals(mainGui.cauldronChangeMenu.getMenuName())) {
//                    if (clickedItem.getType() == Material.BARRIER) {
//                        player.openInventory(mainGui.getInventory());
//                        world.playSound(location, Sound.ENTITY_SPLASH_POTION_THROW, 1.0f, 1.0f);
//                        return;
//                    }else if (clickedItem.getType() == Material.GOLDEN_HELMET) {
//                        player.openInventory(mainGui.getArmourMenu().getInventory());
//                        world.playSound(location, Sound.ENTITY_SPLASH_POTION_THROW, 1.0f, 1.0f);
//                        return;
//                    }else if (clickedItem.getType() == Material.PAPER) {
//                        player.openInventory(mainGui.getCauldronMenu().getInventory());
//                        world.playSound(location, Sound.ENTITY_SPLASH_POTION_THROW, 1.0f, 1.0f);
//                        return;
//                    }
//                }
//                if (event.getView().getTitle().equals(mainGui.cauldronMenu.getMenuName())) {
//                    if (clickedItem.getType() == Material.BARRIER) {
//                        player.openInventory(mainGui.getCauldronChangeMenu().getInventory());
//                        world.playSound(location, Sound.ENTITY_SPLASH_POTION_THROW, 1.0f, 1.0f);
//                        return;
//                    }
//                }
//                if (event.getView().getTitle().equals(mainGui.leatherChestplateMenu.getMenuName())) {
//                    if (clickedItem.getType() == Material.BARRIER) {
//                        player.openInventory(mainGui.getInventory());
//                        world.playSound(location, Sound.ENTITY_SPLASH_POTION_THROW, 1.0f, 1.0f);
//                        return;
//                    }
//                }
//                if (event.getView().getTitle().equals(mainGui.netheriteChestplateMenu.getMenuName())) {
//                    if (clickedItem.getType() == Material.BARRIER) {
//                        player.openInventory(mainGui.getInventory());
//                        world.playSound(location, Sound.ENTITY_SPLASH_POTION_THROW, 1.0f, 1.0f);
//                        return;
//                    }
//                }
//                if (event.getView().getTitle().equals(mainGui.oakSaplingMenu.getMenuName())) {
//                    if (clickedItem.getType() == Material.BARRIER) {
//                        player.openInventory(mainGui.getInventory());
//                        world.playSound(location, Sound.ENTITY_SPLASH_POTION_THROW, 1.0f, 1.0f);
//                        return;
//                    }
//                }
//                if (event.getView().getTitle().equals(mainGui.tippedArrowMenu.getMenuName())) {
//                    if (clickedItem.getType() == Material.BARRIER) {
//                        player.openInventory(mainGui.getInventory());
//                        world.playSound(location, Sound.ENTITY_SPLASH_POTION_THROW, 1.0f, 1.0f);
//                        return;
//                    }
//                }
//                world.playSound(location, Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
//            }


        }

    }
}
