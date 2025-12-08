package com.my.redis;

import java.util.List;

class ScanResult {
    private int nextCursor;
    private List<String> keys;

    public ScanResult(int nextCursor, List<String> keys) {
        this.nextCursor = nextCursor;
        this.keys = keys;
    }

    public int getNextCursor() {
        return nextCursor;
    }

    public List<String> getKeys() {
        return keys;
    }

    @Override
    public String toString() {
        return "Cursor: " + nextCursor + ", Keys: " + keys;
    }
}
