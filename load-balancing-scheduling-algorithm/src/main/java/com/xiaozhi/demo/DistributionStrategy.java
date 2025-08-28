package com.xiaozhi.demo;

/**
 * @author DD
 */
public interface DistributionStrategy {

    /**
     * 获取处理请求的服务器 ip
     *
     * @return 处理请求的服务器 ip
     */
    String getServerIp();
}
