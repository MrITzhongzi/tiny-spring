package cn.haitaoss.tinyioc.beans.annotation.annotationParser;

import cn.haitaoss.tinyioc.beans.BeanDefinition;
import cn.haitaoss.tinyioc.beans.PropertyValue;
import cn.haitaoss.tinyioc.beans.annotation.*;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author haitao.chen
 * @email haitaoss@aliyun.com
 * @date 2021-04-22 21:53
 *
 */
public class AnnotationParser {
    private Map<String, BeanDefinition> registry = new HashMap<>();

    public Map<String, BeanDefinition> getRegistry() {
        return registry;
    }

    public void setRegistry(Map<String, BeanDefinition> registry) {
        this.registry = registry;
    }

    public void annotationBeanDefinitionReader(String packageName) throws ClassNotFoundException {
        // 获取主包和子包下面的所有非内部类
        Set<String> classNames = getClassName(packageName, true);
        Class[] types = {Component.class, Service.class, Repository.class, Controller.class};
        for (String className : classNames) {
            Annotation annotation = null;
            int i;
            for (i = 0; i < 4; i++) {
                annotation = Class.forName(className).getAnnotation(types[i]);
                if (annotation != null)
                    break;
            }
            if (annotation != null) {
                String beanName = null;
                switch (i) {
                    case 0:
                        beanName = ((Component) annotation).value();
                        break;
                    case 1:
                        beanName = ((Service) annotation).value();
                        break;
                    case 2:
                        beanName = ((Repository) annotation).value();
                        break;
                    case 3:
                        beanName = ((Controller) annotation).value();
                }
                // 没有设置name，就使用类名首字母小写作为name
                if (beanName == null || beanName.length() == 0) {
                    // 去取类名
                    String[] elements = className.split("\\.");
                    String sampleName = elements[elements.length - 1];
                    beanName = sampleName.substring(0, 1).toLowerCase() + sampleName.substring(1);
                }
                // 创建BeanDefinition
                BeanDefinition beanDefinition = new BeanDefinition();
                beanDefinition.setSingleton(true);
                beanDefinition.setBeanClass(Class.forName(className));
                // 解析里面的注解
                processProperty(className, beanDefinition);
                // 添加到容器中
                registry.put(beanName, beanDefinition);
            }
        }
    }

    private void processProperty(String className, BeanDefinition beanDefinition) throws ClassNotFoundException {
        Field[] fields = Class.forName(className).getDeclaredFields();
        for (Field field : fields) {
            Value value = field.getAnnotation(Value.class);
            if (value == null)
                continue;
            String propertyValue = value.value();
            if (propertyValue != null || propertyValue.length() > 0) {
                beanDefinition.getPropertyValues().addPropertyValue(new PropertyValue(field.getName(), propertyValue));
            }
        }
    }

    private Set<String> getClassName(String packageName, boolean isRecursion) {
        Set<String> classNames = null;
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        String packagePath = packageName.replace(".", "/");

        URL url = classLoader.getResource(packagePath);//从此包下获取资源
        String protocol = url.getProtocol();//返回URL的协议
        if (protocol.equals("file")) {
            classNames = getClassNameFromDir(url.getPath(), packageName, isRecursion);
        }
        return classNames;
    }

    private Set<String> getClassNameFromDir(String path, String packageName, boolean isRecursion) {
        Set<String> classNames = new HashSet<>();
        File file = new File(path);
        File[] files = file.listFiles();
        for (File childFile : files) {
            if (childFile.isDirectory()) {
                classNames.addAll(getClassNameFromDir(childFile.getPath(), packageName + "." + childFile.getName(), isRecursion));
            } else {
                String fileName = childFile.getName();
                // 不收集内部类
                if (fileName.endsWith(".class") && !fileName.contains("$")) {
                    classNames.add(packageName + "." + fileName.replace(".class", ""));
                }
            }
        }
        return classNames;
    }

}