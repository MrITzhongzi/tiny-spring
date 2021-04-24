package cn.haitaoss.tinyioc.aop;

import cn.haitaoss.tinyioc.HelloWorldService;
import cn.haitaoss.tinyioc.HelloWorldServiceImpl;
import cn.haitaoss.tinyioc.context.ClassPathXmlApplicationContext;
import org.junit.Test;

/**
 * @author haitao.chen
 * email haitaoss@aliyun.com
 * date 2021-04-17 17:22
 *
 */
public class JdkDynamicAopProxyTest {
    @Test
    public void testInterceptor() throws Exception {
        // 没有使用aop
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("tinyioc.xml");
        HelloWorldService helloWorldService = (HelloWorldService) context.getBean("helloWorldService");
        helloWorldService.helloWorld();

        // 使用aop
        // 1. 设置被代理对象(Joinpoint)
        AdvisedSupport advisedSupport = new AdvisedSupport();
        TargetSource targetSource = new TargetSource(helloWorldService, HelloWorldServiceImpl.class,
                HelloWorldService.class);

        advisedSupport.setTargetSource(targetSource);

        // 2. 设置拦截器(Advice)
        TimerInterceptor timerInterceptor = new TimerInterceptor();
        advisedSupport.setMethodInterceptor(timerInterceptor);

        // 3. 创建代理(Proxy)
        JdkDynamicAopProxy jdkDynamicAopProxy = new JdkDynamicAopProxy(advisedSupport);
        HelloWorldService helloWorldServiceProxy = (HelloWorldService) jdkDynamicAopProxy.getProxy();

        // 4. 基于AOP的调用
        helloWorldServiceProxy.helloWorld();
    }
}
