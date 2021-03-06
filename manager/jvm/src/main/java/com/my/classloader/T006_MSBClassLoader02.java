package com.my.classloader;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;

public class T006_MSBClassLoader02 extends ClassLoader {

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        File f = new File("H:/javalianxi/manager/target/classes/", name.replace(".", "/").concat(".class"));
        try {
            FileInputStream fis = new FileInputStream(f);

            byte[] bytes=new byte[fis.available()];
            fis.read(bytes);
            fis.close();//可以写的更加严谨
            return defineClass(name, bytes, 0, bytes.length);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return super.findClass(name); //throws ClassNotFoundException
    }

    public static void main(String[] args) throws Exception {
        Class clazz = new T006_MSBClassLoader02().loadClass("com.my.Person");
        Class clazz1 = new T006_MSBClassLoader02().loadClass("com.my.Person");

        System.out.println(clazz == clazz1);

    }
}
