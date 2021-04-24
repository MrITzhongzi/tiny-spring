package cn.haitaoss.tinyioc.context;

import cn.haitaoss.tinyioc.context.controller.Father;
import org.junit.Test;

/**
 * @author haitao.chen
 * email haitaoss@aliyun.com
 * date 2021-04-23 14:18
 *
 */
public class ParentContextText {
    @Test
    public void testParent() throws Exception {
        ApplicationContext fatherContext = new ClassPathXmlApplicationContext("father.xml");
        ApplicationContext sonContext = new ClassPathXmlApplicationContext(fatherContext, "son.xml");
        Father father = (Father) sonContext.getBean("father");
        System.out.println(father);
    }
}
