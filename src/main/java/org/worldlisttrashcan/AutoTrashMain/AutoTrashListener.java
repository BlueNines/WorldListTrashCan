package org.worldlisttrashcan.AutoTrashMain;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.worldlisttrashcan.message;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import static org.worldlisttrashcan.IsVersion.IsFoliaServer;
import static org.worldlisttrashcan.IsVersion.compareVersions;
import static org.worldlisttrashcan.WorldListTrashCan.*;

public class AutoTrashListener implements Listener {

    public static int OriginalFeatureClearItemAddGlobalTrashModel;
    public static Boolean NoWorldTrashCanEnterPersonalTrashCan;

    public static Map<Player,Inventory> PlayerToInventory = new HashMap<>();

    public static Map<Item,Player> ItemToPlayer = new HashMap<>();


    public Map<Player, Inventory> getPlayerToInventory() {
        return PlayerToInventory;
    }

    public static Boolean VersionFlag = false;

    public AutoTrashListener(){
        VersionFlag = !compareVersions("1.14.0");
//        VersionFlag = true;
    }


    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
//        System.out.println("1");

        Player player = (Player) event.getWhoClicked();
//        Inventory inventory = event.getClickedInventory();
        Inventory inventory = event.getInventory();

        if (PlayerToInventory.containsValue(inventory)) {


            //点击的物品
            ItemStack itemStack = event.getCurrentItem();




            double Coins = main.getConfig().getDouble("Set.PersonalTrashCan.OriginalFeatureClearItemAddGlobalTrash.Model2.Coins");


            //拖动的物品
//            event.getCursor();
//            ItemStack itemStack = event.getCursor();
//            if(!itemStack.getType().isEmpty()&&Coins>0){
//                event.setCancelled(true);
//                return;
//            }


//            System.out.println("1");
            if(main.getConfig().getDouble("Set.PersonalTrashCan.OriginalFeatureClearItemAddGlobalTrash.Model2.Coins")>0){
                //保证点到的一定是Gui里的
                if(itemStack==null||!PlayerToInventory.containsValue(event.getClickedInventory())){
//                    System.out.println("a");
                    event.setCancelled(true);
                    return;
                }
//                System.out.println("2");
                if(UseMoney(player,Coins)){
//                    System.out.println("12");
                    //使用成功

                    //                    // 创建 DecimalFormat 对象，指定格式为两位小数
                    //                    DecimalFormat decimalFormat = new DecimalFormat("#.##");
                    //
                    //                    // 使用 format 方法将 double 格式化为字符串
                    //                    String formattedValue = decimalFormat.format(yourDoubleValue);



                    if (player.getInventory().addItem(itemStack).isEmpty()) {
                        //加进去了
                        DecimalFormat decimalFormat = new DecimalFormat("#.#");
                        String formattedValue = decimalFormat.format(Coins);
                        player.sendMessage(message.find("PlayerTrashCanBuySuccessfully").replace("%count%",formattedValue));
                    }else {
                        //没加进去 ，背包满了
                        //UseMoney  负数来给钱
//                        UseMoney(player,);
//                        player.sendMessage(message.find(""));

//                        World world = player.getWorld();
//                        itemStack.getItemMeta().asItem;
//                        world.spawnEntity(player.getLocation(),itemStack.getItem);
                        player.getWorld().dropItem(player.getLocation(),itemStack);
                    }

//                    itemStack.setType(Material.AIR);
                    int clickedSlot = event.getSlot();
//                    PlayerToInventory.get(player).removeItem(itemStack);
                    PlayerToInventory.get(player).clear(clickedSlot);

//                    event.setCancelled(true);
                }else {
                    //使用失败
                    event.setCancelled(true);
                    player.sendMessage(message.find("PlayerTrashCanBuyFail"));
                }

            }else {
                return;
            }

//            System.out.println("你点了这里");
        }
    }





    @EventHandler
    public void PlayerDropItemEvent(PlayerDropItemEvent event){
        Item item = event.getItemDrop();

        Player player = event.getPlayer();

        //测试Vault插件是否可用
//        testVault(player);

        // 当版本大于1.14 且 NoWorldTrashCanEnterPersonalTrashCan 打开
        // 将玩家UUID存入Item中
        if(NoWorldTrashCanEnterPersonalTrashCan && VersionFlag){
            ItemStack itemStack = item.getItemStack();

            ItemMeta meta = itemStack.getItemMeta();
            NamespacedKey namespacedKey = new NamespacedKey(main,"PlayerUUID");
            meta.getPersistentDataContainer().set(namespacedKey, PersistentDataType.STRING,player.getUniqueId().toString());
            itemStack.setItemMeta(meta);
            item.setItemStack(itemStack);

//            System.out.println("版本大于1.14 可以使用");
        }



//        System.out.println("1");
        if(ItemToPlayer.containsKey(item)){
//            System.out.println("2");
            return;
        }else {
//            System.out.println("3");
            ItemToPlayer.put(item,player);

            if(IsFoliaServer){
                FoliaRunable foliaRunable = new FoliaRunable();
                foliaRunable.FoliaTask(event);
            }else {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (ItemToPlayer.get(item) != null) {
                            ItemToPlayer.remove(item);
                        }
                    }
                }.runTaskLater(main, main.getConfig().getInt("Set.PersonalTrashCan.OriginalFeatureClearItemAddGlobalTrash.Delay") * 20L);

            }


        }


    }




    public boolean UseMoney(Player player,double Count) {


        // 获取经济系统实例
        Economy economy = getEcon();


        // 检查经济系统是否可用
        if (economy != null) {
            // 扣除玩家的金额
//            double Count = 10.0; // 你想要扣除的金额

            if(Count>0){
                EconomyResponse response = economy.withdrawPlayer(player.getName(), Count);

                // 检查交易是否成功
                //                player.sendMessage("成功扣除 " + economy.format(Count) + "，剩余金额：" + economy.format(response.balance));
                //                player.sendMessage("扣除失败，原因：" + response.errorMessage);
                return response.transactionSuccess();
            }else {
                economy.depositPlayer(player, Count);
                return true;

            }
        } else {
            // 处理经济系统不可用的情况
//            player.sendMessage("经济系统不可用！");
            main.getLogger().info(message.find("VaultError"));
            return true;
        }

    }




    public static Inventory InitPlayerInv(Player player){
        return Bukkit.createInventory(null, 54, message.find("PlayerTrashCan").replace("%Player%",player.getName()));
    }


    @EventHandler
    public void ClearItemEvent(EntityDamageEvent event){
        Entity entity = event.getEntity();
//        System.out.println("4");
        if(entity instanceof Item){
            Item item = (Item) entity;
//            System.out.println("Item is "+item.getType()+" x "+item.getItemStack().getAmount());

            //且这个物品是玩家丢的
            Player player = ItemToPlayer.get(item);
//            System.out.println("5");
            if(player!=null){
//                System.out.println("6");
                ItemToPlayer.remove(item);
                ItemStack itemStack = item.getItemStack();
                if(OriginalFeatureClearItemAddGlobalTrashModel==1){
//                    System.out.println("7");
                    for (Inventory inventory : GlobalTrashList) {
                        if (inventory.addItem(itemStack).isEmpty()) {
                            //加进去到公共垃圾桶了
                            break;
                        }
                    }

                }else if (OriginalFeatureClearItemAddGlobalTrashModel==2){
//                    System.out.println("8");
                    Inventory inventory = PlayerToInventory.get(player);
                    if(inventory==null){
                        PlayerToInventory.put(player, InitPlayerInv(player));
                    }else {


                        if (inventory.addItem(itemStack).isEmpty()) {
                            //加进去了
                        }else {
                            //加不进去就清空个人垃圾桶
                            if(main.getConfig().getBoolean("Set.PersonalTrashCan.OriginalFeatureClearItemAddGlobalTrash.Model2.AutoClear")){
                                inventory.clear();
                                player.sendMessage(message.find("PlayerTrashCanFilled"));
                                inventory.addItem(itemStack);
                            }

                        }
                    }

                }else if (OriginalFeatureClearItemAddGlobalTrashModel>=3){
//                    System.out.println("9");
                    return;
                }

                item.remove();
            }

        }

    }

}
