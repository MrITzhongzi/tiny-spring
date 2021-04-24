package cn.haitaoss.tinyioc.context;

import cn.haitaoss.tinyioc.beans.BeanDefinition;
import cn.haitaoss.tinyioc.beans.annotation.annotationParser.AnnotationParser;
import cn.haitaoss.tinyioc.beans.factory.AbstractBeanFactory;
import cn.haitaoss.tinyioc.beans.factory.AutowireCapableBeanFactory;
import cn.haitaoss.tinyioc.beans.io.ResourceLoader;
import cn.haitaoss.tinyioc.beans.lifecycle.DisposableBean;
import cn.haitaoss.tinyioc.beans.xml.XmlBeanDefinitionReader;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * @author haitao.chen
 * email haitaoss@aliyun.com
 * date 2021-04-17 16:36
 *
 */
public class ClassPathXmlApplicationContext extends AbstractApplicationContext {
    private final String configLocation;

    public ClassPathXmlApplicationContext(String configLocation) throws Exception {
        this(configLocation, new AutowireCapableBeanFactory(), null);
    }

    public ClassPathXmlApplicationContext(ApplicationContext parent, String configLocation) throws Exception {
        this(configLocation, new AutowireCapableBeanFactory(), parent);
    }

    public ClassPathXmlApplicationContext(String configLocation, AbstractBeanFactory beanFactory, ApplicationContext parent) throws Exception {
        super(beanFactory);
        this.setParent(parent);
        this.configLocation = configLocation;
        refresh();
    }

    @Override
    public void loadBeanDefinitions(AbstractBeanFactory beanFactory) throws Exception {
        // 读取xml配置文件注册 BeanDefinition
        XmlBeanDefinitionReader xmlBeanDefinitionReader = new XmlBeanDefinitionReader(new ResourceLoader());
        xmlBeanDefinitionReader.loadBeanDefinitions(configLocation);

        for (Map.Entry<String, BeanDefinition> entry : xmlBeanDefinitionReader.getRegistry().entrySet()) {
            beanFactory.registerBeanDefinition(entry.getKey(), entry.getValue());
        }

        // 扫描主包下面的类注册 BeanDefinition
        String packageName = xmlBeanDefinitionReader.getPackageName();
        if (packageName == null || packageName.length() == 0) {
            return;
        }
        AnnotationParser annotationParser = new AnnotationParser();
        annotationParser.annotationBeanDefinitionReader(packageName);
        for (Map.Entry<String, BeanDefinition> entry : annotationParser.getRegistry().entrySet()) {
            beanFactory.registerBeanDefinition(entry.getKey(), entry.getValue());
        }
    }

    public void close() {
        for (Map.Entry<String, BeanDefinition> entry : beanFactory.getBeanDefinitionMap().entrySet()) {
            Object realClassInvokeBean = entry.getValue().getBean();
            if (realClassInvokeBean instanceof DisposableBean) {
                ((DisposableBean) realClassInvokeBean).destroy();
            }
            try {
                Method method = realClassInvokeBean.getClass().getMethod("destroy_method", null);
                method.invoke(realClassInvokeBean, null);
            } catch (Exception e) {

            }
        }
    }

    @Override
    public AbstractBeanFactory getBeanFactory() {
        return beanFactory;
    }
}
