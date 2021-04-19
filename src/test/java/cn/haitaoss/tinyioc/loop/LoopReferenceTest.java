package cn.haitaoss.tinyioc.loop;

import cn.haitaoss.tinyioc.context.ClassPathXmlApplicationContext;
import org.junit.Test;

/**
 * @author haitao.chen
 * @email haitaoss@aliyun.com
 * @date 2021-04-18 16:23
 * 测试循环应用
 */
public class LoopReferenceTest {
    @Test
    public void test() throws Exception {
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("tinyioc-loopreference.xml");
        Parent parent = (Parent) applicationContext.getBean("parent");
        parent.say();

        System.out.println("---------------------------------------------");
        Son son = (Son) applicationContext.getBean("son");
        son.say();

        System.out.println("---------------------------------------------");
        GrandSon grandSon = (GrandSon) applicationContext.getBean("grandSon");
        grandSon.say();
    }
}
