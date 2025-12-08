package com.my.redis;

import java.util.ArrayList;
import java.util.List;

// 简化的哈希表实现
class RedisHashTable {
    private int size;
    private List<List<String>> buckets;
    private int count;

    public RedisHashTable(int size) {
        this.size = size;
        this.buckets = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            buckets.add(new ArrayList<>());
        }
        this.count = 0;
    }

    public int hash(String key) {
        return Math.abs(key.hashCode()) % size;
    }

    public void add(String key) {
        int index = hash(key);
        buckets.get(index).add(key);
        count++;
    }

    public List<String> getBucket(int index) {
        if (index >= 0 && index < size) {
            return buckets.get(index);
        }
        return new ArrayList<>();
    }

    public int getSize() {
        return size;
    }

    public int getCount() {
        return count;
    }
}
