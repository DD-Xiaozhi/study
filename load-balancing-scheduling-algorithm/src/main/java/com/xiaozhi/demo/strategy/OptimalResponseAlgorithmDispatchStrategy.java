package com.xiaozhi.demo.strategy;

import com.xiaozhi.demo.DistributionStrategy;
import lombok.SneakyThrows;

import java.time.Duration;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 最优响应算法分发策略
 *
 * 给每一台服务节点发送 Ping 请求，最先返回的则优先处理请求
 *
 * @author DD
 */
public class OptimalResponseAlgorithmDispatchStrategy implements DistributionStrategy {

    private final Random random = new Random();
    private final List<String> serverList;
    private final ExecutorService executor;

    public OptimalResponseAlgorithmDispatchStrategy(List<String> serverList) {
        this.serverList = serverList;
        executor = Executors.newFixedThreadPool(serverList.size());
    }

    @Override
    public String getServerIp(String ip) {
        AtomicReference<String> resultIp = new AtomicReference<>();
        CompletableFuture<String>[] futureList = serverList.stream()
                .map(it -> CompletableFuture.supplyAsync(() -> this.mockPingRequest(it)))
                .toArray(CompletableFuture[]::new);
        CompletableFuture<Object> future = CompletableFuture.anyOf(futureList);
        // 监听执行完成的回调
        future.thenAccept(result -> {
            System.out.println("先执行完成, IP: " + result);
            resultIp.set((String) result);
        });

        // 阻塞到 CompletableFuture 完成
        future.join();
        return resultIp.get();
    }

    @SneakyThrows
    private String mockPingRequest(String ip) {
        Thread.sleep(Duration.ofSeconds(random.nextInt(10)));
        return ip;
    }

}
