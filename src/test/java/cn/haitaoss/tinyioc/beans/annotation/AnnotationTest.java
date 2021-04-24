package cn.haitaoss.tinyioc.beans.annotation;

import cn.haitaoss.tinyioc.School;
import cn.haitaoss.tinyioc.Student;
import cn.haitaoss.tinyioc.context.ApplicationContext;
import cn.haitaoss.tinyioc.context.ClassPathXmlApplicationContext;
import org.junit.Test;

/**
 * @author haitao.chen
 * email haitaoss@aliyun.com
 * date 2021-04-22 22:14
 *
 */
public class AnnotationTest {
    @Test
    public void testAnnotation() throws Exception {
        //1.在xml中配置实例及属性2.在类中用@Autowired注解标注依赖的bean3.依赖和被依赖的实例都要在xml中配置
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("annotation.xml");
        School school = (School) applicationContext.getBean("school");
        Student student = (Student) applicationContext.getBean("student");
        System.out.println(school.getStudent());
        System.out.println(student.getSchool());
    }
}
