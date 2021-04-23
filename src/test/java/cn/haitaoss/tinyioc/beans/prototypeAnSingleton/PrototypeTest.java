package cn.haitaoss.tinyioc.beans.prototypeAnSingleton;

import cn.haitaoss.tinyioc.context.ApplicationContext;
import cn.haitaoss.tinyioc.context.ClassPathXmlApplicationContext;
import org.junit.Test;

/**
 * @author haitao.chen
 * @email haitaoss@aliyun.com
 * @date 2021-04-22 22:17
 *
 */
public class PrototypeTest {
    @Test
    public void testPrototype() throws Exception {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("prototype.xml");
        for (int i = 0; i < 3; i++) {
            System.out.println(applicationContext.getBean("carPrototype").hashCode());
        }
        for (int i = 0; i < 3; i++)
            System.out.println(applicationContext.getBean("carSingleton").hashCode());
    }
}
