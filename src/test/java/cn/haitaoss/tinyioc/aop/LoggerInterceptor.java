package cn.haitaoss.tinyioc.aop;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

/**
 * @author haitao.chen
 * @email haitaoss@aliyun.com
 * @date 2021-04-23 15:03
 *
 */
public class LoggerInterceptor implements MethodInterceptor {

    @Override
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        System.out.println("logger start!");
        Object proceed = methodInvocation.proceed();
        System.out.println("logger end ");
        return proceed;
    }
}
