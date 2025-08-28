package com.xiaozhi.demo.strategy;

import com.xiaozhi.demo.DistributionStrategy;

/**
 * 平滑加权轮询分发策略
 * 根据权重平均分发给服务器
 *
 * @author DD
 */
public class RoundRobinWeightDistributionStrategy implements DistributionStrategy {

    @Override
    public String getServerIp() {
        return "";
    }
}
