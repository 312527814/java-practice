package com.my;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

import java.util.concurrent.CountDownLatch;

public class CuratorFrameworkZookeeper {
    public static void main(String[] args) {
        // ZooKeeper 服务器的地址
        String connectionString = "192.168.16.129:2181";

        // 创建 CuratorFramework 客户端
        CuratorFramework client = CuratorFrameworkFactory.newClient(connectionString, new ExponentialBackoffRetry(1000, 3));

        // 启动客户端
        client.start();

        try {
            // 要创建的临时节点的路径
            String path = "/lock2/ephemeralNode";

            // 创建临时节点，初始内容为 "initialContent"
            client.create().creatingParentContainersIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(path, "initialContent".getBytes());


            System.out.println("临时节点创建成功: " + path);

            // 在这里可以进行其他操作，比如读取节点内容、设置监听器等

            // 注意：由于这是一个示例，所以程序在创建节点后会立即退出。在实际应用中，你可能希望保持会话打开，以便临时节点保持存活。
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 关闭客户端
//            client.close();
        }

        try {
            new CountDownLatch(1).await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
