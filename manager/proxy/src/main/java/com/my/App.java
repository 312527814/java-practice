package com.my;

import com.my.cglib.CglibBoss;
import com.my.cglib.CglibSecretary;
import com.my.cglib.MyBoss;
import com.my.jdk.Boss;
import com.my.jdk.IBoss;
import com.my.jdk.Secretary;
import net.sf.cglib.core.DebuggingClassWriter;
import net.sf.cglib.proxy.Enhancer;
import org.junit.Test;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

/**
 * Hello world!
 */
public class App {

    @Test
    public void main() {
        //生成动态代理类的class文件
        System.setProperty(DebuggingClassWriter.DEBUG_LOCATION_PROPERTY, "e:\\class2");
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(CglibBoss.class);
        CglibSecretary cglibSecretary = new CglibSecretary();
        cglibSecretary.setO(new CglibBoss());

        enhancer.setCallback(cglibSecretary);

        CglibBoss boss = (CglibBoss) enhancer.create();

        boss.Say("dsds");
        //main3();
    }

    public static void main3() {

        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(MyBoss.class);
        CglibSecretary cglibSecretary = new CglibSecretary();
        cglibSecretary.setO(new MyBoss());

        enhancer.setCallback(cglibSecretary);

        MyBoss boss = (MyBoss) enhancer.create();

        boss.Say("dsds");
    }

    public static void main4(String[] args) {
        System.getProperties().put("sun.misc.ProxyGenerator.saveGeneratedFiles", "true");
        List<Class> list = new ArrayList<>();
        Secretary mishu = new Secretary();
        mishu.setO(new Boss());
        //第一个参数:反射时使用的类加载器
        //第二个参数:Proxy需要实现什么接口
        //第三个参数:通过接口对象调用方法时,需要调用哪个类的invoke方法
        IBoss boss = (IBoss) Proxy.newProxyInstance(IBoss.class.getClassLoader(), new Class[]{IBoss.class}, mishu);
        list.add(boss.getClass());
        System.out.println(boss.getClass());
        boss.Say("我是你大哥");

    }
    public static void main2(String[] args) {
        System.setProperty(DebuggingClassWriter.DEBUG_LOCATION_PROPERTY, "e:\\class2");
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(IBoss.class);
        CglibSecretary cglibSecretary = new CglibSecretary();
        cglibSecretary.setO(new Boss());

        enhancer.setCallback(cglibSecretary);

        IBoss boss = (IBoss) enhancer.create();

        boss.Say("dsds");
    }
}
