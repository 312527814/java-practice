package com.my;

import java.io.File;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

public class MyClassLoader  extends URLClassLoader {
    public MyClassLoader(URL[] urls){
        super(urls,getSystemClassLoader());
    }

    public static void main(String[] args) throws Exception {
        //指定加载的class文件路径；
        /**
         * 比如一个class文件的位置：
         * E:/ASD/SDF/GRY/WE/RT/TYU/com/org/entry/Account.class
         * Account类的类路径:com.org.entry.Account
         * 那么root = E:/ASD/SDF/GRY/WE/RT/TYU
         */
        String root  = "F:/java-practice/manager/spring-mvc/target/classes";
        //创建一个类加载器
        MyClassLoader classLoader = new MyClassLoader(new URL[]{new File(root).toURI().toURL()});
        //根据类名加载类
        Class bean = classLoader.loadClass("com.my.controller.MyController");//类全限定名；
        Object o = bean.newInstance();

        Method json = bean.getMethod("json", int.class, String.class);
        Object ee = json.invoke(o, 1, "ee");
        Thread thread = new Thread(()->{},"ee-1");thread.start();

        ClassLoader contextClassLoader = thread.getContextClassLoader();
        System.out.println("contextClassLoader_1:"+contextClassLoader);
    }



}


