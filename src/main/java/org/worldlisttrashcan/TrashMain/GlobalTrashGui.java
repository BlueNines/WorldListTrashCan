package org.worldlisttrashcan.TrashMain;

import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.Dye;
import org.worldlisttrashcan.message;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

import static org.worldlisttrashcan.IsVersion.compareVersions;
import static org.worldlisttrashcan.WorldListTrashCan.*;

public class GlobalTrashGui implements InventoryHolder {



    public List<Inventory> TrashList = new ArrayList<>();


    public void setTrashList(List<Inventory> trashList) {
        TrashList = trashList;
    }

    public static void ClearContainer(List<Inventory> TrashList){


        for (Inventory inventory : TrashList) {
            for(int i=0;i<45;i++){
                inventory.clear(i);
            }

        }
    }

    public GlobalTrashGui(List<Inventory> TrashList,int MaxCount){
        setTrashList(TrashList);
        InitGlobalList(TrashList,MaxCount);
    }
    @Override
    public Inventory getInventory() {
        return this.TrashList.get(0);
    }

//    public Inventory getInventory(Player player) {
//        return Bukkit.createInventory(this, 54, message.find("BanChestInventoryName").replace("%world%",player.getWorld().getName()));
//    }
//    public Inventory getGlobalInventory(Player player) {
//        return Bukkit.createInventory(this, 54, message.find("GlobalBanChestInventoryName"));
//    }

    public void InitGlobalList( List<Inventory> TrashList,int MaxCount){
        if(!TrashList.isEmpty()){
            TrashList.clear();
        }


        for(int i=0;i<MaxCount;i++){
            Inventory inventory = CreateMenuItemMap(i,MaxCount-1);

//            System.out.println("1");

            TrashList.add(inventory);
        }

    }
    public ItemStack CreateItem(Material BackGroundMaterial, String strings, String name){
        ItemStack itemStack= new ItemStack(BackGroundMaterial);
        List<String> stringList = new ArrayList<>();
        for (String lore : strings.split(";")) {
            stringList.add(lore);
        }
        ItemMeta BackGroundItemMeta = itemStack.getItemMeta();
        BackGroundItemMeta.setLore(stringList);
        BackGroundItemMeta.setDisplayName(name);
        itemStack.setItemMeta(BackGroundItemMeta);
        return itemStack;
    }
    public Inventory CreateMenuItemMap(int PageCount,int PageMaxCount){
        Material ShowBorder;
        ItemStack ShowBorderItemStack;
        //如果版本小于这个1.13.0
        if(compareVersions("1.13.0")){
            ShowBorder = Material.matchMaterial("STAINED_GLASS_PANE");
            ShowBorderItemStack = CreateItem(ShowBorder,"","§§");
            ShowBorderItemStack.setDurability((short) 15);
        }else {
            ShowBorder = Material.matchMaterial("BLACK_STAINED_GLASS_PANE");
            ShowBorderItemStack = CreateItem(ShowBorder,"","§§");
        }



        Inventory Menu = Bukkit.createInventory((InventoryHolder) this, 54, message.find("TrashMenuTitle"));

        String integerStrings =
                        "x;x;x;x;x;x;x;x;x;" +
                        "x;x;x;x;x;x;x;x;x;" +
                        "x;x;x;x;x;x;x;x;x;" +
                        "x;x;x;x;x;x;x;x;x;" +
                        "x;x;x;x;x;x;x;x;x;" +
                        "y;a;y;y;y;y;y;b;y" ;
        int i = 0;
        for(String s:integerStrings.split(";")){
            if(s.equals("a")){
//                System.out.println("a: "+i);

                if(PageMaxCount==1){
                    continue;
                }
                //如果不是第一页
                if(PageCount!=0){
                    Menu.setItem(i,CreateItem(Material.ARROW,"",message.find("TrashMenuUpPage")));
                }else {
                    Menu.setItem(i,ShowBorderItemStack);
                }

            }else if(s.equals("b")){
                if(PageMaxCount==1){
                    continue;
                }
//                System.out.println("b: "+i);
                //如果不是最后一页
                if(PageCount!=PageMaxCount){
//                    System.out.println("PageCount: "+PageCount + " PageMaxCount: "+PageMaxCount);
                    Menu.setItem(i,CreateItem(Material.ARROW,"",message.find("TrashMenuDownPage")));
                }else {
                    Menu.setItem(i,ShowBorderItemStack);
                }
            }
            else if(s.equals("y")){
                //名字：§§是防重叠用的
                Menu.setItem(i,ShowBorderItemStack);
            }
            i++;
        }
        return Menu;
    }

}
