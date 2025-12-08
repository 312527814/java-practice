package com.my.redis;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class SimpleRedisScan {

    // 模拟 Redis 的哈希表，用数组 + 链表（这里用 List 代替）
    private final List<String>[] table;
    private final int tableSize;

    // 用于存储所有 key（模拟 Redis 键空间）
    private final Map<String, String> data;

    @SuppressWarnings("unchecked")
    public SimpleRedisScan(int initialCapacity) {
        this.tableSize = initialCapacity;
        this.table = new List[tableSize];
        for (int i = 0; i < tableSize; i++) {
            table[i] = new ArrayList<>();
        }
        this.data = new ConcurrentHashMap<>();
    }

    // 简单哈希函数
    private int hash(String key) {
        return Math.abs(key.hashCode()) % tableSize;
    }

    // 插入键
    public void put(String key, String value) {
        int index = hash(key);
        if (!table[index].contains(key)) {
            table[index].add(key);
        }
        data.put(key, value);
    }

    // 删除键
    public void remove(String key) {
        int index = hash(key);
        table[index].remove(key);
        data.remove(key);
    }

    // SCAN 核心方法
    // cursor: 当前游标
    // count: 每次返回的元素数量提示（非精确）
    // 返回: Pair<nextCursor, List<String>>，下一个游标和当前批次的 key
    public Pair<Integer, List<String>> scan(int cursor, int count) {
        List<String> result = new ArrayList<>();
        int current = cursor;
        int iterations = 0;

        // 我们最多遍历整个表一次，防止无限循环
        int maxIterations = tableSize;

        do {
            // 1. 获取当前 bucket 的所有 key
            for (String key : table[current]) {
                if (data.containsKey(key)) { // 确保 key 未被删除
                    result.add(key);
                }
            }

            // 2. 计算下一个 bucket 索引（反向二进制递增）
            current = nextCursor(current, tableSize);

            iterations++;

            // 如果已经返回足够多的元素，提前退出
        } while (current != cursor && result.size() < count * 2 && iterations < maxIterations);

        int nextCursor = (current == 0) ? 0 : current;
        return new Pair<>(nextCursor, result);
    }

    // 反向二进制递增：reverse binary increment
    // 例如：000 -> 100 -> 010 -> 110 -> 001 -> ...
    private int nextCursor(int cursor, int size) {
        int mask = 1;
        int result = 0;

        // 找到 size 对应的位数（2 的幂）
        int bitCount = 0;
        int temp = size - 1;
        while (temp > 0) {
            bitCount++;
            temp >>>= 1;
        }
        int bits = bitCount;

        // 反向遍历每一位
        for (int i = 0; i < bits; i++) {
            if ((cursor & mask) != 0) {
                result |= (1 << (bits - i - 1));
            }
            mask <<= 1;
        }

        // 反向加 1
        result = (result + 1) & ((1 << bits) - 1);

        // 再次反转回来
        int finalCursor = 0;
        mask = 1;
        for (int i = 0; i < bits; i++) {
            if ((result & mask) != 0) {
                finalCursor |= (1 << (bits - i - 1));
            }
            mask <<= 1;
        }

        return finalCursor;
    }

    // 辅助类：存储游标和结果
    public static class Pair<K, V> {
        public final K key;
        public final V value;

        public Pair(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }

    // 获取总键数（用于测试）
    public int size() {
        return data.size();
    }

    public static void main(String[] args) {
        SimpleRedisScan scanner = new SimpleRedisScan(8); // 哈希表大小为 8

        // 插入一些数据
        scanner.put("user:1", "Alice");
        scanner.put("user:2", "Bob");
        scanner.put("order:100", "paid");
        scanner.put("session:xyz", "active");
        scanner.put("temp:key", "value");
        scanner.put("config:db", "localhost");

        int cursor = 0;
        int count = 2;
        int round = 1;

        System.out.println("Starting SCAN iteration...");

        do {
            SimpleRedisScan.Pair<Integer, List<String>> pair = scanner.scan(cursor, count);
            cursor = pair.key;
            List<String> keys = pair.value;

            System.out.println("Round " + round + " -> Keys: " + keys + ", Next Cursor: " + cursor);

            round++;
        } while (cursor != 0);

        System.out.println("SCAN completed.");
    }
}
