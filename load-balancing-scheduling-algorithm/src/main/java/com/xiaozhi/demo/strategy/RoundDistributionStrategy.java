package com.xiaozhi.demo.strategy;

import com.xiaozhi.demo.DistributionStrategy;
import lombok.AllArgsConstructor;

import java.util.List;

/**
 * 轮询分发策略
 * 平均分发，一碗水端平
 *
 * @author DD
 */
@AllArgsConstructor
public class RoundDistributionStrategy implements DistributionStrategy {

    /**
     * 轮询计数
     * 多线程状态下使用 AtomicInteger, 到达一定的数可以进行重置
     */
    private static int roundCount = 0;
    private List<String> serverList;

    @Override
    public String getServerIp() {
        return serverList.get(roundCount++ % serverList.size());
    }
}