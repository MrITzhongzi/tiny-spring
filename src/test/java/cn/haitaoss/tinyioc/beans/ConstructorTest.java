package cn.haitaoss.tinyioc.beans;

import cn.haitaoss.tinyioc.Driveable;
import cn.haitaoss.tinyioc.context.ApplicationContext;
import cn.haitaoss.tinyioc.context.ClassPathXmlApplicationContext;
import org.junit.Test;

/**
 * @author haitao.chen
 * @email haitaoss@aliyun.com
 * @date 2021-04-22 19:05
 *
 */
public class ConstructorTest {
    @Test
    public void testConstructor() throws Exception {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("tinyioc.xml");
        Driveable car = (Driveable) applicationContext.getBean("carByConstructor");
        System.out.println(car);
    }
}