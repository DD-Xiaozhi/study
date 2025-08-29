package com.xiaozhi.demo;

import com.xiaozhi.demo.strategy.MinActivityDistributionStrategy;

import java.util.List;

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
//        loadBalancer.setDistributionStrategy(new WeightDistributionStrategy(Map.of(
//                "44.22.11.11:8881", 1,
//                "44.22.11.11:8882", 2,
//                "44.22.11.11:8883", 3
//        )));
//        loadBalancer.setDistributionStrategy(new RoundRobinWeightDistributionStrategy(Map.of(
//                "44.22.11.11:8881", 1,
//                "44.22.11.11:8882", 2,
//                "44.22.11.11:8883", 3
//        )));

        loadBalancer.setDistributionStrategy(new MinActivityDistributionStrategy(server));

        for (int i = 1; i <= 6; i++) {
            loadBalancer.handleRequest("request " + i);
        }
    }
}
