package com.my.aspectjaop;

public class MyService {
    public void performTask() {
        System.out.println("Performing task...");
        try {
            Thread.sleep(1000); // Simulate a delay
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public int calculateSum(int a, int b) {
        System.out.println("Calculating sum...");
        return a + b;
    }
}