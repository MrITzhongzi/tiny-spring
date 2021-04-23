package cn.haitaoss.tinyioc.context;

import cn.haitaoss.tinyioc.Anything;
import cn.haitaoss.tinyioc.HelloWorldService;
import cn.haitaoss.tinyioc.beans.factory.AutowireCapableBeanFactory;
import org.junit.Test;

/**
 * @author haitao.chen
 * @email haitaoss@aliyun.com
 * @date 2021-04-17 16:40
 *
 */
public class ApplicationContextTest {
    @Test
    public void test() throws Exception {
        AutowireCapableBeanFactory beanFactory = new AutowireCapableBeanFactory();
        String configLocation = "tinyioc.xml";
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(configLocation);
        Anything anything = (Anything) context.getBean("anything");
        System.out.println(anything.getPoint());
    }

    @Test
    public void testPostBeanProcessor() throws Exception {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("tinyioc-postbeanprocessor.xml");
        HelloWorldService helloWorldService = (HelloWorldService) applicationContext.getBean("helloWorldService");
        helloWorldService.helloWorld();
    }
}
