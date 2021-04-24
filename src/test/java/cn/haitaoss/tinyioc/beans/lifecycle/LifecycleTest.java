package cn.haitaoss.tinyioc.beans.lifecycle;

import cn.haitaoss.tinyioc.context.ApplicationContext;
import cn.haitaoss.tinyioc.context.ClassPathXmlApplicationContext;
import org.junit.Test;

/**
 * @author haitao.chen
 * email haitaoss@aliyun.com
 * date 2021-04-22 21:41
 *
 */
public class LifecycleTest {
    @Test
    public void testLifecycle() throws Exception {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("life.xml");
        Life life = (Life) applicationContext.getBean("life");
        System.out.println(life.toString());
        ((ClassPathXmlApplicationContext) applicationContext).close();
    }
}