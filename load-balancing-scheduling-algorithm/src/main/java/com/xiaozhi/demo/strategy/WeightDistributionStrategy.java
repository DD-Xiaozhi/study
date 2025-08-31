package com.xiaozhi.demo.strategy;

import com.xiaozhi.demo.DistributionStrategy;

import java.util.Map;
import java.util.Random;

/**
 * 随机权重分发策略
 *
 * @author DD
 */
public class WeightDistributionStrategy implements DistributionStrategy {

    private int totalWeight;
    private Map<String, Integer> serverWeightMap;
    private final Random random = new Random();

    public WeightDistributionStrategy(Map<String, Integer> serverWeightMap) {
        this.serverWeightMap = serverWeightMap;
        // 计算总权重
        this.totalWeight = serverWeightMap.values().stream().mapToInt(Integer::intValue).sum();
    }

    @Override
    public String getServerIp(String ip) {
        // 获取随机索引
        int index = random.nextInt(totalWeight);

        for (String server : serverWeightMap.keySet()) {
            // 获取服务权重
            Integer weight = serverWeightMap.get(server);
            if (weight > index) {
                return server;
            }

            // 如果当前随机的索引小于权重，索引减去权重
            index -= weight;
        }

        return null;
    }
}
