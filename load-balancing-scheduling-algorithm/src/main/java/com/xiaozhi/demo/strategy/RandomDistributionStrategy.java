package com.xiaozhi.demo.strategy;

import com.xiaozhi.demo.DistributionStrategy;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Random;

/**
 * 随机分发策略
 *
 * @author DD
 */
@AllArgsConstructor
public class RandomDistributionStrategy implements DistributionStrategy {

    private final Random random = new Random();

    private List<String> serverList;

    @Override
    public String getServerIp(String ip) {
        return serverList.get(random.nextInt(0, serverList.size() - 1));
    }
}
