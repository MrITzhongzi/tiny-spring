package cn.haitaoss.tinyioc.aop;

import org.aopalliance.intercept.MethodInterceptor;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * 基于jdk的动态代理
 * @author haitao.chen
 * @email haitaoss@aliyun.com
 * @date 2021-04-17 17:13
 *
 */
public class JdkDynamicAopProxy implements AopProxy, InvocationHandler {
    private final AdvisedSupport advised;

    public JdkDynamicAopProxy(AdvisedSupport advise) {
        this.advised = advise;
    }

    @Override
    public Object getProxy() {
        return Proxy.newProxyInstance(
                getClass().getClassLoader(),
                advised.getTargetSource().getTargetClass(),
                this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        MethodInterceptor methodInterceptor = advised.getMethodInterceptor(); // 获取增强方法

        // 通过切面表达式实现只代理符合切入点表达式的方法
        if (advised.getMethodMatcher() != null
                && advised.getMethodMatcher().matches(method, advised.getTargetSource().getTarget().getClass())) {

            // 被代理
            return methodInterceptor.invoke(new ReflectiveMethodInvocation(advised.getTargetSource().getTarget(),
                    method, args));
        } else {
            // 不被代理
            return method.invoke(advised.getTargetSource().getTarget(), args);
        }
    }
}
