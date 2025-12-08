package com.my;

public class AppTest2 {
    static {
        System.out.println("AppTest2..........");
    }
    public void  sss(){
        ReadTest readTest = new ReadTest();
        System.out.println(readTest.getClass().getClassLoader());
    }
}
