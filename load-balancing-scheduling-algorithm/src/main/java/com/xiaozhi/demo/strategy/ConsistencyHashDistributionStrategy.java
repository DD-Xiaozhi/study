package com.xiaozhi.demo.strategy;

import com.xiaozhi.demo.DistributionStrategy;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

/**
 * 一致性 hash 算法
 * 首先通过 hash 算法为每台服务器计算唯一的 hash 值
 * 然后将待分配的值和最大的节点的hash值做模运算，定位到 hash 环上的某个点
 * hash 环本质上是一条取模后的直线，所有 hash 值都分布在这条直线上
 * 逻辑上可以将这条直线看作一个环，实现一致性分布。
 *
 * 参考：dubbo 的一致性 hash 实现
 *
 * @author DD
 */
public class ConsistencyHashDistributionStrategy implements DistributionStrategy {

    private final TreeMap<Integer, String> virtualNodes;

    public ConsistencyHashDistributionStrategy(List<String> serverList) {
        virtualNodes = new TreeMap<>();
        serverList.forEach(server -> virtualNodes.put(getHashCode(server), server));
    }

    @Override
    public String getServerIp(String ip) {
        int hashCode = getHashCode(ip);
        // 获取最相近的服务节点来处理
        Map.Entry<Integer, String> entry = virtualNodes.ceilingEntry(hashCode);

        // 如果没有找到比当前请求的 hashCode 大的节点则返回第一个节点
        if (Objects.isNull(entry)) {
            return virtualNodes.firstEntry().getValue();
        }

        return entry.getValue();
    }

    /**
     * 哈希方法：用于计算一个 IP 的哈希值
     * @param ip ip
     * @return hash 值
     */
    public static int getHashCode(String ip) {
        final int p = 1904390101;
        int hash = (int) 1901102097L;
        for (int i = 0; i < ip.length(); i++)
            hash = (hash ^ ip.charAt(i)) * p;
        hash += hash << 13;
        hash ^= hash >> 7;
        hash += hash << 3;
        hash ^= hash >> 17;
        hash += hash << 5;

        // 如果算出来的值为负数则取其绝对值
        if (hash < 0)
            hash = Math.abs(hash);
        return hash;
    }

}
