package cn.haitaoss.tinyioc.loop3;

import cn.haitaoss.tinyioc.context.ClassPathXmlApplicationContext;
import org.junit.Test;

/**
 * @author haitao.chen
 * email haitaoss@aliyun.com
 * date 2021-04-20 10:38
 *
 */
public class LoopReferenceTest {
    @Test
    public void test() throws Exception {
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("tinyioc-loopreference3.xml");
        System.out.println("-----");
        Son son = (Son) applicationContext.getBean("son");
        son.say();

        System.out.println("-----");
        Parent parent = (Parent) applicationContext.getBean("parent");
        parent.say();

        System.out.println("-----");
        System.out.println(son.getParent() == parent);
        System.out.println(parent.getSon() == son);
    }
}
