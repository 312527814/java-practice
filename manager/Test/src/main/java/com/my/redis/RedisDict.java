package com.my.redis;

import java.util.ArrayList;
import java.util.List;

// 简化的字典实现（单哈希表，无rehashing）
class RedisDict {
    private RedisHashTable ht;

    public RedisDict(int initialSize) {
        this.ht = new RedisHashTable(initialSize);
    }

    public void addKey(String key) {
        ht.add(key);
    }

    public ScanResult scan(int cursor, String pattern, int count) {
        return scanInternal(cursor, pattern, count);
    }

    private ScanResult scanInternal(int cursor, String pattern, int count) {
        List<String> results = new ArrayList<>();
        int nextCursor = 0;
        int currentBucket = cursor;
        int bucketsScanned = 0;
        int keysCollected = 0;

        // 遍历桶直到收集到足够数量的键或遍历完所有桶
        while (currentBucket < ht.getSize() && keysCollected < count) {
            List<String> bucketKeys = ht.getBucket(currentBucket);

            // 应用模式匹配过滤
            for (String key : bucketKeys) {
                if (matchesPattern(key, pattern)) {
                    results.add(key);
                    keysCollected++;
                    if (keysCollected >= count) {
                        break;
                    }
                }
            }

            bucketsScanned++;
            currentBucket++;

            // 如果已经扫描完所有桶，游标返回0
            if (currentBucket >= ht.getSize()) {
                nextCursor = 0;
                break;
            } else {
                nextCursor = currentBucket;
            }
        }

        return new ScanResult(nextCursor, results);
    }

    private boolean matchesPattern(String key, String pattern) {
        if (pattern == null || pattern.equals("*")) {
            return true;
        }

        // 简化的模式匹配（只支持*通配符）
        if (pattern.endsWith("*")) {
            String prefix = pattern.substring(0, pattern.length() - 1);
            return key.startsWith(prefix);
        }

        return key.equals(pattern);
    }

    public int getKeyCount() {
        return ht.getCount();
    }
}
