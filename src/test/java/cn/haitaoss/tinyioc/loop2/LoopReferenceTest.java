package cn.haitaoss.tinyioc.loop2;

import cn.haitaoss.tinyioc.context.ClassPathXmlApplicationContext;
import org.junit.Test;

/**
 * @author haitao.chen
 * email haitaoss@aliyun.com
 * date 2021-04-18 16:23
 * 测试循环应用
 */
public class LoopReferenceTest {
    /**
     * thirdCache 存放所有空构造的bean 也就是容器中所有的bean
     * secondCache 存放会出现问题
     * firstCache 存放所有创建好的bean 也就是容器中所有的bean
     * @author haitao.chen
     * email
     * date 2021/4/19 5:27 下午
     */
    @Test
    public void test() throws Exception {
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("tinyioc-loopreference2.xml");
        /*Parent parent = (Parent) applicationContext.getBean("parent");
        parent.say();*/


        Son son = (Son) applicationContext.getBean("son");
        son.say();
    }
}
