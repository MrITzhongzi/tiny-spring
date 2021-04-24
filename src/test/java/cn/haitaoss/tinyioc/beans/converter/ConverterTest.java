package cn.haitaoss.tinyioc.beans.converter;

import cn.haitaoss.tinyioc.Anything;
import cn.haitaoss.tinyioc.Driveable;
import cn.haitaoss.tinyioc.context.ApplicationContext;
import cn.haitaoss.tinyioc.context.ClassPathXmlApplicationContext;
import org.junit.Test;

/**
 * @author haitao.chen
 * email haitaoss@aliyun.com
 * date 2021-04-22 19:46
 *
 */
public class ConverterTest {
    @Test
    public void testConvert() throws Exception {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("tinyioc.xml");
        Driveable car = (Driveable) applicationContext.getBean("carByConvert");
        System.out.println(car);
    }

    @Test
    public void testConvert2() throws Exception {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("tinyioc.xml");
        Anything anything = (Anything) applicationContext.getBean("anything");
        System.out.println(anything);
    }
}