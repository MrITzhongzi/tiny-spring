package cn.haitaoss.tinyioc;

import cn.haitaoss.tinyioc.beans.BeanDefinition;
import cn.haitaoss.tinyioc.beans.factory.AbstractBeanFactory;
import cn.haitaoss.tinyioc.beans.factory.AutowireCapableBeanFactory;
import cn.haitaoss.tinyioc.beans.io.ResourceLoader;
import cn.haitaoss.tinyioc.beans.xml.XmlBeanDefinitionReader;
import org.junit.Test;

import java.util.Map;

/**
 * @author haitao.chen
 * email haitaoss@aliyun.com
 * date 2021-04-17 11:23
 *
 */
public class BeanFactoryTest {
    @Test
    public void testLazyLoad() throws Exception {
        // 1.读取配置
        XmlBeanDefinitionReader xmlBeanDefinitionReader = new XmlBeanDefinitionReader(new ResourceLoader());
        xmlBeanDefinitionReader.loadBeanDefinitions("tinyioc.xml");

        // 2.初始化BeanFactory并注册bean
        AbstractBeanFactory beanFactory = new AutowireCapableBeanFactory();
        for (Map.Entry<String, BeanDefinition> beanDefinitionEntry : xmlBeanDefinitionReader.getRegistry().entrySet()) {
            beanFactory.registerBeanDefinition(beanDefinitionEntry.getKey(), beanDefinitionEntry.getValue());
        }

        // 3.获取bean
        HelloWorldService helloWorldService = (HelloWorldService) beanFactory.getBean("helloWorldService");
        helloWorldService.helloWorld();
    }

    @Test
    public void testPreInstantiate() throws Exception {
        // 读取配置
        XmlBeanDefinitionReader xmlBeanDefinitionReader = new XmlBeanDefinitionReader(new ResourceLoader());
        xmlBeanDefinitionReader.loadBeanDefinitions("tinyioc.xml");

        // 初始化BeanFactory 并注册bean
        AutowireCapableBeanFactory beanFactory = new AutowireCapableBeanFactory();
        for (Map.Entry<String, BeanDefinition> entry : xmlBeanDefinitionReader.getRegistry().entrySet()) {
            beanFactory.registerBeanDefinition(entry.getKey(), entry.getValue());
        }

        // 初始化bean
        beanFactory.preInstantiateSingletons();

        // 获取bean
        HelloWorldService helloWorldService = (HelloWorldService) beanFactory.getBean("helloWorldService");
        helloWorldService.helloWorld();

    }
}
