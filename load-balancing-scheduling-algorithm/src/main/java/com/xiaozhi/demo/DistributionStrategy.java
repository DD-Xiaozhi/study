package com.xiaozhi.demo;

/**
 * @author DD
 */
public interface DistributionStrategy {

    /**
     * 获取处理请求的服务器 ip
     *
     * @param ip 客户端 ip 地址
     * @return 处理请求的服务器 ip
     */
    String getServerIp(String ip);
}
