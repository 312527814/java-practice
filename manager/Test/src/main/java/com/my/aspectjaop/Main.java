package com.my.aspectjaop;

import org.springframework.cglib.core.DebuggingClassWriter;

public class Main {
    public static void main(String[] args) {
        System.setProperty(DebuggingClassWriter.DEBUG_LOCATION_PROPERTY, "e:\\class2");
        MyService myService = new MyService();
//        myService.performTask();
        int result = myService.calculateSum(5, 3);


        System.out.println("Result: " + result);
    }
}