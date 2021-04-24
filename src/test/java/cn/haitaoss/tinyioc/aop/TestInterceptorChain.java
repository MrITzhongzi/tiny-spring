package cn.haitaoss.tinyioc.aop;

import cn.haitaoss.tinyioc.Driveable;
import cn.haitaoss.tinyioc.context.ApplicationContext;
import cn.haitaoss.tinyioc.context.ClassPathXmlApplicationContext;
import org.junit.Test;

/**
 * @author haitao.chen
 * email haitaoss@aliyun.com
 * date 2021-04-23 15:06
 *
 */
public class TestInterceptorChain {
    @Test
    public void test() throws Exception {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("tinyioc.xml");
        Driveable car = (Driveable) applicationContext.getBean("car");
        car.running();
      /*  Liveable address = (Liveable) applicationContext.getBean("address");
        address.living();*/
    }
}
