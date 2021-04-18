package cn.haitaoss.tinyioc;

import sun.misc.ProxyGenerator;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class ProxyDemo {
    public static void main(String[] args) {

        //Person proxyInstance = (Person) ProxyFactory.getProxyInstance(new Man());
        //proxyInstance.method1();

        // 将创建的代理类保存到本地
        saveClassFile("manProxy",Man.class);
        Man man = new Man();

        // 拼接字节码，创建对象，再见对象返回  class.getConstructor
        Person manProxy = (Person) Proxy.newProxyInstance(
                ProxyDemo.class.getClassLoader(),
                Man.class.getInterfaces(),
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        System.out.println(".....");
                        return method.invoke(man, args);
                    }
                });

       manProxy.method1();

    }
    /**
     * 保存class文件
     *
     * @param proxyName 保存的文件名字，可随意指定
     * @param clazz     被代理类的class对象
     */
    public static void saveClassFile(String proxyName, Class clazz) {
        // 生成代理对象的字节数组
        byte[] bytes = ProxyGenerator.generateProxyClass(proxyName, clazz.getInterfaces());

        // 保存到文件中
        FileChannel fileChannel = null;
        try {
            // 建立通道
            fileChannel = FileChannel.open(
                    Paths.get(clazz.getResource(".").getPath() + proxyName + ".class"),
                    StandardOpenOption.CREATE, StandardOpenOption.WRITE);
            // 将文件内容包装成buffer
            ByteBuffer buffer = ByteBuffer.wrap(bytes);
            // 写入文件
            fileChannel.write(buffer);
        } catch (IOException e) {
            e.printStackTrace();

        } finally {
            if (fileChannel != null) {
                // 释放资源
                try {
                    fileChannel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


    }
}
interface Person{
    void method1();
}
interface Person2{
    void method2();
}
class Man implements Person,Person2{

    @Override
    public void method1() {
        System.out.println("Person的method1方法....");
    }

    @Override
    public void method2() {

    }
}
/**
 * 代理工厂
 */
class ProxyFactory {
    public static Object getProxyInstance(Object instance) {

        return Proxy.newProxyInstance(
                instance.getClass().getClassLoader(),
                instance.getClass().getInterfaces(),
                new MyInvocationHandler(instance));
    }
}

/**
 * 代理方法
 */
class MyInvocationHandler implements InvocationHandler {
    private Object instance = null;

    public MyInvocationHandler(Object instance) {
        this.instance = instance;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // aop添加功能
        System.out.println("aop...before");
        Object returnVal = null;
        try {
            returnVal = method.invoke(instance, args);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
        return returnVal;
    }
}
