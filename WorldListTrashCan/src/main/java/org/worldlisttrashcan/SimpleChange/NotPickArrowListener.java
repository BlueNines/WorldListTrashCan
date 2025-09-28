package org.worldlisttrashcan.SimpleChange;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;

import java.util.ArrayList;
import java.util.List;

import static org.worldlisttrashcan.IsVersion.isServerVersionLowerThan;

public class NotPickArrowListener implements Listener {

    List<Arrow> ArrowList = new ArrayList<>();
    @EventHandler
    public void onEntityShootBow(EntityShootBowEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof Player && event.getProjectile() instanceof Arrow) {
            if(((Player)entity).getInventory().getItemInMainHand().containsEnchantment(Enchantment.ARROW_INFINITE)){
                ArrowList.add((Arrow) event.getProjectile());
            }else {
                return;
            }
        }else if (entity instanceof Skeleton && event.getProjectile() instanceof Arrow){
            ArrowList.add((Arrow) event.getProjectile());
        }
    }
    //这是一个专门进行优化箭矢的类
    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        // 判断是否为箭矢
        if (event.getEntity() instanceof Arrow) {
            Arrow arrow = (Arrow) event.getEntity();

            // 获取箭矢的拾取状态
//            Arrow.PickupStatus pickupStatus = arrow.getPickupStatus();
            //如果版本小于1.13.0
            if(isServerVersionLowerThan("1.13.0")) {
                // 判断箭矢是否可以被拾取.ALWAYS

                if (ArrowList.contains(arrow)) {
                    ArrowList.remove(arrow);
                    arrow.remove();
                }

//                if(arrow.getShooter() instanceof Entity) {


//                    Entity shooter = (Entity) arrow.getShooter();
//
//                    // 检查射出者是否为实体（例如骷髅）
//                    if (shooter instanceof Skeleton) {
//                        arrow.remove();
//                    }else if(shooter instanceof Player){
//
//                    }
//                }

            }else{
                // 判断箭矢是否可以被拾取.ALWAYS
                if (arrow.getPickupStatus() == Arrow.PickupStatus.ALLOWED) {
                    // 可以被拾取
                    // 在这里进行相应的操作
                } else {
                    // 不可被拾取
                    // 在这里进行相应的操作
                    arrow.remove();
                }
            }
        }
    }

}
