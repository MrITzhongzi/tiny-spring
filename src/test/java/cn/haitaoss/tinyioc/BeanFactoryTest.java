package cn.haitaoss.tinyioc;

import cn.haitaoss.tinyioc.factory.AutowireCapableBeanFactory;
import cn.haitaoss.tinyioc.factory.BeanFactory;
import org.junit.Test;

/**
 * @author haitao.chen
 * @email haitaoss@aliyun.com
 * @date 2021-04-17 11:23
 *
 */
public class BeanFactoryTest {
    @Test
    public void test() {
        // 初始化beanFactory
        BeanFactory beanFactory = new AutowireCapableBeanFactory();

        // 注入bean
        BeanDefinition beanDefinition = new BeanDefinition();
        beanDefinition.setBeanClassName("cn.haitaoss.tinyioc.HelloWorldService");
        beanFactory.registerBeanDefinition("helloWorldService", beanDefinition);

        // 获取bean
        HelloWorldService helloWorldService = (HelloWorldService) beanFactory.getBean("helloWorldService");
        helloWorldService.helloWorld();

    }
}
