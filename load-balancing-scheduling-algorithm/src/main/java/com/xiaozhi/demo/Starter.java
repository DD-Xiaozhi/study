package com.xiaozhi.demo;

import com.xiaozhi.demo.strategy.WeightDistributionStrategy;

import java.util.List;
import java.util.Map;

/**
 * @author DD
 */
public class Starter {

    public static void main(String[] args) {
        LoadBalancer loadBalancer = new LoadBalancer();
        List<String> server = List.of(
                "44.22.11.11:8881",
                "44.22.11.11:8882",
                "44.22.11.11:8883",
                "44.22.11.11:8884"
        );

//        loadBalancer.setDistributionStrategy(new RoundDistributionStrategy(server));
//        loadBalancer.setDistributionStrategy(new RandomDistributionStrategy(server));
        loadBalancer.setDistributionStrategy(new WeightDistributionStrategy(Map.of(
                "44.22.11.11:8881", 1,
                "44.22.11.11:8882", 2,
                "44.22.11.11:8883", 3
        )));

        for (int i = 1; i <= 12; i++) {
            loadBalancer.handleRequest("request " + i);
        }
    }
}
