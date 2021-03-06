

import jdk.internal.org.objectweb.asm.ClassWriter;
import jdk.internal.org.objectweb.asm.MethodVisitor;
import jdk.internal.org.objectweb.asm.Opcodes;

import java.util.ArrayList;
import java.util.List;

public class T15_FullGC_Problem7 {

    public static void main(String[] args) throws InterruptedException {



        for (int j = 0; j <100000000 ; j++) {
            Thread.sleep(1000);
            T15_FullGC_Problem7.Metaspace.createClasses();
        }
    }

    static class Metaspace extends ClassLoader {

        public static List<Class<?>> createClasses() throws InterruptedException {
            // 类持有
            List<Class<?>> classes = new ArrayList<Class<?>>();
            // 循环1000w次生成1000w个不同的类。
            for (int i = 0; i < 10000000; ++i) {
                Thread.sleep(1);
                ClassWriter cw = new ClassWriter(0);
                // 定义一个类名称为Class{i}，它的访问域为public，父类为java.lang.Object，不实现任何接口
                cw.visit(Opcodes.V1_1, Opcodes.ACC_PUBLIC, "Class" + i, null,
                        "java/lang/Object", null);
                // 定义构造函数<init>方法
                MethodVisitor mw = cw.visitMethod(Opcodes.ACC_PUBLIC, "<init>",
                        "()V", null, null);
                // 第一个指令为加载this
                mw.visitVarInsn(Opcodes.ALOAD, 0);
                // 第二个指令为调用父类Object的构造函数
                mw.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/Object",
                        "<init>", "()V");
                // 第三条指令为return
                mw.visitInsn(Opcodes.RETURN);
                mw.visitMaxs(1, 1);
                mw.visitEnd();
                T15_FullGC_Problem7.Metaspace test = new T15_FullGC_Problem7.Metaspace();
                byte[] code = cw.toByteArray();
                // 定义类
                Class<?> exampleClass = test.defineClass("Class" + i, code, 0, code.length);
                classes.add(exampleClass);
            }
            return classes;
        }
    }

}
