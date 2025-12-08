package com.my;

import com.my.config.AppConfig;
import com.my.mapper.flowerMapper;
import com.my.services.DemoSerivce;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Hello world!
 *
 */
public class MybaitsSpringApp
{
    public static void main( String[] args )
    {

        int a=0;
    }

    @Test
    public  void xml(){
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("applicationContext.xml");
        DemoSerivce bean = applicationContext.getBean(DemoSerivce.class);
        bean.test();

    }
    @Test
    public  void  noXml(){
        AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext();
        ac.register(AppConfig.class);
        ac.refresh();
        flowerMapper bean1 = ac.getBean(flowerMapper.class);
        DemoSerivce bean = ac.getBean(DemoSerivce.class);
        bean.test();
    }
}
