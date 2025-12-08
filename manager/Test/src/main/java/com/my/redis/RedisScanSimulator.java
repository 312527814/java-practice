package com.my.redis;

import java.util.Arrays;

public class RedisScanSimulator {
    private RedisDict dict;

    public RedisScanSimulator() {
        // 创建初始大小为8的字典（模拟Redis的初始哈希表）
        this.dict = new RedisDict(8);
    }

    public void initializeData() {
        // 添加一些测试数据
        String[] keys = {
                "user:1", "user:2", "user:3", "user:4", "user:5",
                "product:100", "product:101", "product:102",
                "cart:42", "cart:43",
                "session:abc123", "session:def456",
                "order:1001", "order:1002", "order:1003"
        };

        for (String key : keys) {
            dict.addKey(key);
        }
    }

    public void simulateScan() {
        System.out.println("=== 开始模拟 SCAN 命令 ===");
        System.out.println("总键数量: " + dict.getKeyCount());
        System.out.println();

        // 模拟完整的SCAN迭代
        int cursor = 0;
        int iteration = 1;
        int totalKeysScanned = 0;

        do {
            ScanResult result = dict.scan(cursor, "*", 3); // 每次返回大约3个键
            System.out.println("迭代 " + iteration + ": " + result);

            cursor = result.getNextCursor();
            iteration++;
            totalKeysScanned += result.getKeys().size();

        } while (cursor != 0);

        System.out.println();
        System.out.println("扫描完成! 总共扫描了 " + totalKeysScanned + " 个键");
    }

    public void simulateScanWithPattern() {
        System.out.println("\n=== 模拟带模式的 SCAN (user:*) ===");

        int cursor = 0;
        int iteration = 1;

        do {
            ScanResult result = dict.scan(cursor, "user:*", 2);
            System.out.println("迭代 " + iteration + ": " + result);

            cursor = result.getNextCursor();
            iteration++;

        } while (cursor != 0);
    }

    public static void main(String[] args) {
        RedisScanSimulator simulator = new RedisScanSimulator();
        simulator.initializeData();

        simulator.simulateScan();
        simulator.simulateScanWithPattern();
    }
}
