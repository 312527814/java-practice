package com.my;

import com.my.pojp.FactoryBeanTest;
import com.my.pojp.Person;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Hello world!
 */
public class SpringiocApp {
    public static void main(String[] args) {

//        System.setProperty(DebuggingClassWriter.DEBUG_LOCATION_PROPERTY, "e:\\class");
        ClassPathXmlApplicationContext ac = new ClassPathXmlApplicationContext("applicationContext.xml");
        for (String s : ac.getBeanDefinitionNames()) {
            System.out.println("\r\n" + s);
        }



        Object people2 = ac.getBean("flower");
        Object people = ac.getBean("teacher");
        Object factoryBeanTest = ac.getBean("factoryBeanTest");
        Object factoryBeanTest2 = ac.getBean("factoryBeanTest");
        Object factoryBean = ac.getBean("&factoryBeanTest");

        Object pp = ac.getBeansOfType(Person.class);
        Object fac = ac.getBean(FactoryBeanTest.class);

//


    }


}