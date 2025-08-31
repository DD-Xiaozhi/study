package com.xiaozhi.demo.strategy;

import com.xiaozhi.demo.DistributionStrategy;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 最小活跃度度发策略
 *
 * @author DD
 */
@AllArgsConstructor
public class MinActivityDistributionStrategy implements DistributionStrategy {

    /**
     * 服务器活跃度
     */
    private Map<String, Integer> serverActivityMap;

    public MinActivityDistributionStrategy(List<String> serverList) {
        this.serverActivityMap = serverList.stream()
                .collect(Collectors.toMap(Function.identity(), i -> 0));
    }

    @Override
    public String getServerIp(String ip) {
        String minActivityServerIp = serverActivityMap.entrySet().stream()
                .min(Map.Entry.comparingByValue())
                .get().getKey();

        serverActivityMap.put(minActivityServerIp, serverActivityMap.get(minActivityServerIp) + 1);
        return minActivityServerIp;
    }

    private void decrementActive(String serverIp) {
        serverActivityMap.put(serverIp, serverActivityMap.get(serverIp) - 1);
    }
}
