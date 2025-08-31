package com.xiaozhi.demo;

import lombok.Data;

/**
 * 负载均衡器
 * 负责分发请求给对应服务器处理
 *
 * @author DD
 */
@Data
public class LoadBalancer {

    /**
     * 请求分发策略
     */
    private DistributionStrategy distributionStrategy;

    public void handleRequest(String traceId, String ip) {
        System.out.println("服务器 [ %s ] 处理请求, traceId: %s"
                .formatted(distributionStrategy.getServerIp(ip), traceId));
    }

}
