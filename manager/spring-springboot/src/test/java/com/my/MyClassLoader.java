package com.my;

import sun.misc.SharedSecrets;
import sun.misc.URLClassPath;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.net.URL;
import java.net.URLClassLoader;

public class MyClassLoader extends ClassLoader {
    private final String byteCodePath;   //byteCodePath

    public MyClassLoader(String byteCodePath) {
        this.byteCodePath = byteCodePath;
    }

    public MyClassLoader(ClassLoader parent, String byteCodePath) {
        super(parent);
        this.byteCodePath = byteCodePath;
    }

    @Override
    protected Class<?> findClass(String className) throws ClassNotFoundException {
        String fileName = byteCodePath.concat(className).concat(".class");  //拼接.class文件的路径
        System.out.println(fileName);
        BufferedInputStream bis = null;
        ByteArrayOutputStream baos = null;
        try {
            bis = new BufferedInputStream(new FileInputStream(fileName));  //创建带缓冲的IO流
            baos = new ByteArrayOutputStream();
            int len;
            byte[] bytes = new byte[1024];
            while ((len = bis.read(bytes)) != -1) {
                baos.write(bytes, 0, len);   //将二进制流写进baos的ByteArray中
            }
            final byte[] byteCodes = baos.toByteArray();
            return defineClass(null, byteCodes, 0, byteCodes.length);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {  //关闭流
            try {
                if (bis != null) {
                    bis.close();
                }
                if (baos != null) {
                    baos.close();
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
        return null;
    }
}
