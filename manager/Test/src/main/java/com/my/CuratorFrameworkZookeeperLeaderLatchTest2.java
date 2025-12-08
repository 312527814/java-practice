package com.my;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.leader.LeaderLatch;
import org.apache.curator.framework.recipes.leader.LeaderLatchListener;
import org.apache.curator.framework.recipes.leader.LeaderSelector;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListenerAdapter;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.util.concurrent.CountDownLatch;

public class CuratorFrameworkZookeeperLeaderLatchTest2 {
    private static final String PATH = "/lock/leader";

    public static void main(String[] args) throws InterruptedException {
//        List<LeaderLatch> latchList = new ArrayList<>();
//        List<CuratorFramework> clients = new ArrayList<>();
        CuratorFramework client = null;
        client = getClient();


        LeaderSelector leaderSelector = new LeaderSelector(client, PATH, new LeaderSelectorListenerAdapter() {
            @Override
            public void takeLeadership(CuratorFramework curatorFramework) throws Exception {
                System.out.println("i am master");
            }
        });
        leaderSelector.autoRequeue();
        leaderSelector.start();

        new CountDownLatch(1).await();
    }

    private static CuratorFramework getClient() {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        CuratorFramework client = CuratorFrameworkFactory.builder()
                .connectString("192.168.16.129:2181")
                .retryPolicy(retryPolicy)
                .sessionTimeoutMs(6000)
                .connectionTimeoutMs(3000)
                .namespace("demo")
                .build();
        client.start();
        return client;
    }
}