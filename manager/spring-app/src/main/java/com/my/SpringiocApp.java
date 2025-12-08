package com.my;

import com.my.pojo.FactoryBeanTest;
import com.my.pojo.Person;
import com.my.pojo.Room;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.concurrent.CountDownLatch;

/**
 * Hello world!
 */
public class SpringiocApp {
    public static void main(String[] args) throws InterruptedException {

//        System.setProperty(DebuggingClassWriter.DEBUG_LOCATION_PROPERTY, "e:\\class");
        ClassPathXmlApplicationContext ac = new ClassPathXmlApplicationContext("applicationContext.xml");
        for (String s : ac.getBeanDefinitionNames()) {
            System.out.println("\r\n" + s);
        }
        Object p = ac.getBean(Room.class);
//        Object factoryBeanTest1 = ac.getBean("factoryBeanTest");
//
//        Object people2 = ac.getBean("flower");
////        Object people = ac.getBean("teacher");
//        Object factoryBeanTest = ac.getBean("factoryBeanTest");
//        Object factoryBeanTest2 = ac.getBean("factoryBeanTest");
//        Object factoryBean = ac.getBean("&factoryBeanTest");
//
        Object pp = ac.getBean(Person.class);
        Object fac = ac.getBean(FactoryBeanTest.class);
        ac.getBeanFactory().destroyBean(pp);

        new CountDownLatch(1).await();

//


    }


}
