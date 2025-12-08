package com.my;

import static org.junit.Assert.assertTrue;

import org.apache.catalina.loader.WebappClassLoader;
import org.junit.Test;

import java.lang.reflect.Method;
import java.net.URL;

/**
 * Unit test for simple App.
 */
public class AppTest 
{
    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue() throws Exception {
        ClassLoader parent = AppTest.class.getClassLoader();

        MyClassLoader myClassLoader = new MyClassLoader("F:/my/");
        Class<?> aClass2 = myClassLoader.loadClass("ReadTest");
        Class<?> aClass = myClassLoader.loadClass("AppTest2");
        Object o = aClass.newInstance();
        System.out.println(o.getClass().getClassLoader());

        Method sss = aClass.getMethod("sss");
        sss.invoke(o);

        assertTrue( true );
    }
}
