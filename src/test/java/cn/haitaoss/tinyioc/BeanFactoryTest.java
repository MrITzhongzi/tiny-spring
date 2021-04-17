package cn.haitaoss.tinyioc;

import cn.haitaoss.tinyioc.factory.AutowireCapableBeanFactory;
import org.junit.Test;

/**
 * @author haitao.chen
 * @email haitaoss@aliyun.com
 * @date 2021-04-17 11:23
 *
 */
public class BeanFactoryTest {
    @Test
    public void test() throws Exception {
        // 1.初始化beanFactory
        AutowireCapableBeanFactory beanFactory = new AutowireCapableBeanFactory();

        // 2.bean定义
        BeanDefinition beanDefinition = new BeanDefinition();
        beanDefinition.setBeanClassName("cn.haitaoss.tinyioc.HelloWorldService");


        // 3.设置属性
        PropertyValues propertyValues = new PropertyValues();
        propertyValues.addPropertyValue(new PropertyValue("text","hello world!!!!"));
        beanDefinition.setPropertyValues(propertyValues);

        // 4.生成bean
        beanFactory.registerBeanDefinition("helloWorldService", beanDefinition);

        // 5.获取bean
        HelloWorldService helloWorldService = (HelloWorldService) beanFactory.getBean("helloWorldService");
        helloWorldService.helloWorld();

    }
}
