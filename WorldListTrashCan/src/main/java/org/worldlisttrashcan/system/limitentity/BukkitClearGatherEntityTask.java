package org.worldlisttrashcan.system.limitentity;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

import static org.worldlisttrashcan.system.limitentity.LimitMain.*;
import static org.worldlisttrashcan.system.limitentity.removeEntity.dealEntity;
import static org.worldlisttrashcan.WorldListTrashCan.*;

/**
 * 优化后的密集实体检查任务
 * 每10 tick为每个世界随机挑选一个实体进行检查，避免遍历所有实体造成的卡顿
 */
public class BukkitClearGatherEntityTask {
    BukkitRunnable bukkitRunnable;

    // 每个世界的实体列表缓存，避免频繁调用 getEntities()
    private final Map<String, List<Entity>> worldEntityCache = new HashMap<>();

    // 每个世界的当前索引位置，用于轮询式选择
    private final Map<String, Integer> worldIndexes = new HashMap<>();

    // 最近处理过的实体UUID，避免短时间内重复处理同一个实体
    private final Set<String> recentlyProcessed = new HashSet<>();

    // 缓存更新计数器，每多少次tick更新一次缓存
    private int cacheUpdateCounter = 0;

    // 缓存更新间隔（tick数）
    private static final int CACHE_UPDATE_INTERVAL = 20; // 每1秒更新一次缓存

    // 最近处理记录清理间隔
    private static final int RECENT_CLEAR_INTERVAL = 100; // 每5秒清理一次最近记录

    private final Random random = new Random();

    public BukkitClearGatherEntityTask() {
        bukkitRunnable = new BukkitRunnable() {
            @Override
            public void run() {
                if (!GatherLimitFlag) {
                    return;
                }

                cacheUpdateCounter++;

                // 定期清理最近处理记录，避免内存泄漏
                if (cacheUpdateCounter % RECENT_CLEAR_INTERVAL == 0) {
                    recentlyProcessed.clear();
                }

                // 定期更新每个世界的实体缓存
                boolean updateCache = (cacheUpdateCounter % CACHE_UPDATE_INTERVAL == 0);

                for (World world : Bukkit.getWorlds()) {
                    String worldName = world.getName();

                    // 跳过黑名单世界
                    if (GatherBanWorlds.contains(worldName)) {
                        continue;
                    }

                    // 跳过没有玩家在线的世界，减少无效检查
                    if (world.getPlayers().isEmpty()) {
                        continue;
                    }

                    // 更新或获取该世界的实体缓存
                    List<Entity> entities = worldEntityCache.get(worldName);
                    if (entities == null || updateCache) {
                        entities = filterValidEntities(world.getEntities());
                        worldEntityCache.put(worldName, entities);
                        // 重置索引
                        worldIndexes.put(worldName, 0);
                    }

                    // 跳过没有实体的世界
                    if (entities.isEmpty()) {
                        continue;
                    }

                    // 选择一个实体进行处理
                    Entity selectedEntity = selectEntity(entities, worldName);

                    if (selectedEntity != null && selectedEntity.isValid()) {
                        String entityTypeName = selectedEntity.getName().toUpperCase();

                        // 检查是否在配置的限制列表中
                        if (GatherLimits.containsKey(entityTypeName)) {
                            String entityKey = worldName + ":" + selectedEntity.getUniqueId();

                            // 检查是否最近处理过
                            if (!recentlyProcessed.contains(entityKey)) {
                                try {
                                    dealEntity(selectedEntity);
                                    recentlyProcessed.add(entityKey);
                                } catch (Exception e) {
                                    // 处理异常，避免因单个实体处理失败影响整体
                                    recentlyProcessed.add(entityKey); // 出错也标记为已处理
                                }
                            }
                        }
                    }
                }

                // 清理已卸载世界的缓存
                if (updateCache) {
                    worldEntityCache.keySet().removeIf(worldName ->
                        Bukkit.getWorld(worldName) == null
                    );
                    worldIndexes.keySet().removeIf(worldName ->
                        Bukkit.getWorld(worldName) == null
                    );
                }
            }
        };
    }

    /**
     * 过滤出有效的实体，排除玩家和已移除的实体
     */
    private List<Entity> filterValidEntities(Collection<Entity> entities) {
        List<Entity> validEntities = new ArrayList<>();
        for (Entity entity : entities) {
            // 跳过玩家和无效实体
            if (entity instanceof Player) {
                continue;
            }
            if (!entity.isValid()) {
                continue;
            }
            validEntities.add(entity);
        }
        return validEntities;
    }

    /**
     * 从实体列表中选择一个实体
     * 使用轮询+随机偏移的策略，既保证覆盖面又增加随机性
     */
    private Entity selectEntity(List<Entity> entities, String worldName) {
        int size = entities.size();
        if (size == 0) {
            return null;
        }

        // 获取当前索引
        int currentIndex = worldIndexes.getOrDefault(worldName, 0);

        // 添加随机偏移，避免总是按相同顺序检查
        // 每次前进 1-5 个位置，增加随机性但保持一定的连续性
        int offset = 1 + random.nextInt(Math.min(5, size));
        currentIndex = (currentIndex + offset) % size;

        // 更新索引
        worldIndexes.put(worldName, currentIndex);

        return entities.get(currentIndex);
    }

    public void Start() {
        // 改为每10 tick执行一次 (0.5秒)
        bukkitRunnable.runTaskTimer(main, 10L, 10L);
    }

    public void Stop() {
        bukkitRunnable.cancel();
        // 清理缓存
        worldEntityCache.clear();
        worldIndexes.clear();
        recentlyProcessed.clear();
    }
}