package cn.haitaoss.tinyioc.beans.annotation;

import cn.haitaoss.tinyioc.Car;
import cn.haitaoss.tinyioc.School;
import cn.haitaoss.tinyioc.Student;
import cn.haitaoss.tinyioc.context.ApplicationContext;
import cn.haitaoss.tinyioc.context.ClassPathXmlApplicationContext;
import org.junit.Test;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

/**
 * @author haitao.chen
 * email haitaoss@aliyun.com
 * date 2021-04-23 11:03
 *
 */
public class AnnotationParserTest {
    @Test
    public void testAnnotationWithNonXml() throws Exception {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("annotation_null.xml");
        School school = (School) applicationContext.getBean("school");
        Student student = (Student) applicationContext.getBean("student");
        System.out.println(school.getStudent().getStuName());
        System.out.println(student.getSchool().getName());
        System.out.println(school.getPrice());
        Car carWithXml = (Car) applicationContext.getBean("carWithXml");
        System.out.println(carWithXml);
        Student studentWithXml = (Student) applicationContext.getBean("studentWithXml");
        System.out.println(studentWithXml);
    }

    /**
     * 测试递归获取文件
     * @author haitao.chen
     * email
     * date 2021/4/23 1:53 下午
     */
    @Test
    public void testScanFile() throws ClassNotFoundException {
        String packageName = "cn.haitaoss";
        Set<String> classs = getClasss(packageName);
        for (String className : classs) {
            System.out.println(className);
        }
    }

    private Set<String> getClasss(String packageName) {
        // 修改文件目录
        packageName = packageName.replaceAll("\\.", "/");
        Set<String> clazzs = new HashSet<>();
        String file = this.getClass().getClassLoader().getResource(packageName).getFile();
        getFiles(clazzs, new File(file));
        return clazzs;
    }

    private void getFiles(Set<String> clazzs, File file) {
        if (file.isDirectory()) {
            for (File tempFile : file.listFiles()) {
                getFiles(clazzs, tempFile);
            }
        } else {
            if (file.getName().endsWith("class") && !file.getName().contains("$")) {
                clazzs.add(file.getAbsolutePath());
            }

        }
    }
}
