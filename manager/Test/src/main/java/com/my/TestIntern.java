package com.my;

import sun.applet.AppletClassLoader;

import java.lang.reflect.Method;
import java.net.URL;
import java.util.UUID;

/**
 * @program:
 * @description:
 * @author: liang.liu
 * @create: 2021-08-25 14:47
 */
public class TestIntern {

//    public static void main(String[] args)  {
//        String s1 = new String("1");
//        String s2 = s1;
//        s1.intern();
//        System.out.println("s1==s2:" +(s1 == s2) );
//    }
// 1
//    public static void main(String[] args) {
//        String s = new String("1");
//        s.intern();
//        String s2 = "1";
//        System.out.println(s == s2);
//
//        String s3 = new String("1") + new String("1");
//        s3.intern();
//        String s4 = "11";
//        System.out.println(s3 == s4);
//    }


    //2
//    public static void main(String[] args) {
//        String s = new String("1");
//        String s2 = "1";
//        s.intern();
//        System.out.println(s == s2);
//
//        String s3 = new String("1") + new String("1");
//        String s4 = "11";
//        s3.intern();
//        System.out.println(s3 == s4);
//    }


//    public static void main(String[] args) {
//        String s3 = new String("1") + new String("1");
//        String s4 = "11";
//        String s5 = s3.intern();
////        System.out.println(s3 == s4);
//        System.out.println("s3 == s4:" + (s3 == s4));
//
//        System.out.println("s5 == s4:" + (s5 == s4));
//
//        Thread.currentThread().getStackTrace();
//    }

//    public static void main(String[] args) {
//        String str1 = "xy";
//        String str2 = "z";
//        String str3 = "xyz";
//        String str4 = str1 + str2;
//        String str5 = str4.intern();
//        String str6 = "xy" + "z";
//
//        System.out.println(str3 == str4); //f
//        System.out.println(str3 == str5); //t
//        System.out.println(str3 == str6); //t
//    }

    public static void main(String[] args) throws Exception {

        ClassLoader parent = TestIntern.class.getClassLoader();
//        URL url = new URL();
//        AppletClassLoader appletClassLoader = new AppletClassLoader();
        Class<?> aClass = parent.loadClass("com.my.App");
        Object o = aClass.newInstance();
        System.out.println(o.getClass().getClassLoader());

        Method sss = aClass.getMethod("sss");
        sss.invoke(o);


        for (long i = 0; i < Long.MAX_VALUE; i++) {
            String string = UUID.randomUUID().toString().intern();

            if (1 % 1000 == 0) {
                System.out.println(string.hashCode());
            }
        }
    }
}
