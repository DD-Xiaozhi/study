package com.xiaozhi.demo.strategy;

import com.xiaozhi.demo.DistributionStrategy;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * 平滑加权轮询分发策略
 * 根据权重平均分发给服务器
 *
 * @author DD
 */
public class RoundRobinWeightDistributionStrategy implements DistributionStrategy {

    /**
     * 总权重值
     */
    private int totalWeight;

    /**
     * 固定权重值
     */
    private Map<String, Integer> serverWeightMap;

    /**
     * 动态权重值
     */
    private Map<String, Integer> currentServerWeightMap;

    public RoundRobinWeightDistributionStrategy(Map<String, Integer> serverWeightMap) {
        this.serverWeightMap = serverWeightMap;
        this.currentServerWeightMap = new HashMap<>(serverWeightMap);
        // 计算总权重
        this.totalWeight = serverWeightMap.values().stream().mapToInt(Integer::intValue).sum();
    }

    @Override
    public String getServerIp(String ip) {
        Set<Map.Entry<String, Integer>> entrySet = currentServerWeightMap.entrySet();

        // 获取当前权重最大服务器
        String server = entrySet.stream().max(Map.Entry.comparingByValue()).get().getKey();

        // 当前执行请求的服务器权重减去总权重
        for (Map.Entry<String, Integer> item : entrySet) {
            if (Objects.equals(server, item.getKey())) {
                item.setValue(item.getValue() - totalWeight);
                continue;
            }

            // 其他的加上初始权重
            item.setValue(item.getValue() + serverWeightMap.get(item.getKey()));
        }
        return server;
    }
}
