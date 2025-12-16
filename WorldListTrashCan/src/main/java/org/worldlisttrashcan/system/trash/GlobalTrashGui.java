package org.worldlisttrashcan.system.trash;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.worldlisttrashcan.WorldListTrashCan;
import org.worldlisttrashcan.utils.Message;

import java.util.ArrayList;
import java.util.List;

import static org.worldlisttrashcan.utils.IsVersion.isServerVersionLowerThan;

public class GlobalTrashGui implements InventoryHolder {


    // 上一页的material
    public ItemStack backItemStack;
    // 下一页的material
    public ItemStack nextItemStack;
    // 背景板的material
    public ItemStack backgroundItemStack;


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
        // 初始化这两个配置
        FileConfiguration config = WorldListTrashCan.main.getConfig();
        String backString = config.getString("Set.GlobalTrash.GlobalItems.BackItem.Material","ARROW");
        String nextString = config.getString("Set.GlobalTrash.GlobalItems.NextItem.Material","ARROW");
        String backgroundString = config.getString("Set.GlobalTrash.GlobalItems.BackgroundItem.Material");

//        System.out.println("backgroundString "+backgroundString);
//        System.out.println("backString "+backString);
//        System.out.println("nextString "+nextString);

        if (backgroundString == null || backgroundString.isEmpty() || backgroundString.equals("") || backgroundString.equals(" ")){
            Material ShowBorder;
            ItemStack ShowBorderItemStack;
            //如果版本小于这个1.13.0
            if(isServerVersionLowerThan("1.13.0")){
                ShowBorder = Material.matchMaterial("STAINED_GLASS_PANE");
                ShowBorderItemStack = CreateItem(ShowBorder,"","§§");
                ShowBorderItemStack.setDurability((short) 15);
            }else {
                ShowBorder = Material.matchMaterial("BLACK_STAINED_GLASS_PANE");
                ShowBorderItemStack = CreateItem(ShowBorder,"","§§");
            }
            backgroundItemStack = ShowBorderItemStack;
        }else {
//            System.out.println("backgroundString "+backgroundString);
            Material material1 = Material.matchMaterial(backgroundString);
            backgroundItemStack = CreateItem(material1,"","§§");
        }

        Material material2 = Material.matchMaterial(backString);
        backItemStack = CreateItem(material2,"", Message.find("TrashMenuUpPage"));

        Material material3 = Material.matchMaterial(nextString);
        nextItemStack = CreateItem(material3,"", Message.find("TrashMenuDownPage"));

        setModelId(backgroundItemStack,
                config.getInt("Set.GlobalTrash.GlobalItems.BackgroundItem.ModelId",-1));
        setModelId(backItemStack,
                config.getInt("Set.GlobalTrash.GlobalItems.BackItem.ModelId",-1));
        setModelId(nextItemStack,
                config.getInt("Set.GlobalTrash.GlobalItems.NextItem.ModelId",-1));


        setTrashList(TrashList);
        InitGlobalList(TrashList,MaxCount);
    }

    // 给物品设置modleId
    public ItemStack setModelId(ItemStack itemStack,int modelId){
        if (modelId<0){
            return itemStack;
        }

        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setCustomModelData(modelId);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
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

        Inventory Menu = Bukkit.createInventory((InventoryHolder) this, 54, Message.find("TrashMenuTitle"));

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
                    Menu.setItem(i, backItemStack);
                }else {
                    Menu.setItem(i,backgroundItemStack);
                }

            }else if(s.equals("b")){
                if(PageMaxCount==1){
                    continue;
                }
//                System.out.println("b: "+i);
                //如果不是最后一页
                if(PageCount!=PageMaxCount){
//                    System.out.println("PageCount: "+PageCount + " PageMaxCount: "+PageMaxCount);
//                    Menu.setItem(i,CreateItem(Material.ARROW,"",message.find("TrashMenuDownPage")));
//                    Menu.setItem(i,CreateItem(Material.ARROW,"",message.find("TrashMenuDownPage")));
                    Menu.setItem(i,nextItemStack);
                }else {
                    Menu.setItem(i,backgroundItemStack);
                }
            }
            else if(s.equals("y")){
                //名字：§§是防重叠用的
                Menu.setItem(i,backgroundItemStack);
            }
            i++;
        }
        return Menu;
    }

}
