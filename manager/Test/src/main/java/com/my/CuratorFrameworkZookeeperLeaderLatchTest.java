package com.secbro.learn.curator;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.leader.LeaderLatch;
import org.apache.curator.framework.recipes.leader.LeaderLatchListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.CloseableUtils;

import java.util.ArrayList;
import java.util.List;

public class CuratorFrameworkZookeeperLeaderLatchTest {
    private static final String PATH = "/lock/leader";
    public static void main(String[] args) {
//        List<LeaderLatch> latchList = new ArrayList<>();
//        List<CuratorFramework> clients = new ArrayList<>();
        CuratorFramework client = null;
        try {
             client = getClient();
            final LeaderLatch leaderLatch = new LeaderLatch(client, PATH);
            leaderLatch.addListener(new LeaderLatchListener() {
                @Override
                public void isLeader() {
                    System.out.println(leaderLatch.getId() +  ":I am leader. I am doing jobs!");
                }
                @Override
                public void notLeader() {
                    System.out.println(leaderLatch.getId() +  ":I am not leader. I will do nothing!");
                }
            });
            leaderLatch.start();
            while (true){
//                Thread.sleep(1000*10);
//                System.out.println(".......client.close();.........");
//                client.close();
//                Thread.sleep(1000*10);
//
//                System.out.println("..client.start();...........");
//                client.start();
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            client.close();
        }
    }
    private static CuratorFramework getClient() {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        CuratorFramework client = CuratorFrameworkFactory.builder()
                .connectString("localhost:7811")
                .retryPolicy(retryPolicy)
                .sessionTimeoutMs(6000)
                .connectionTimeoutMs(3000)
                .namespace("demo")
                .build();
        client.start();
        return client;
    }
}