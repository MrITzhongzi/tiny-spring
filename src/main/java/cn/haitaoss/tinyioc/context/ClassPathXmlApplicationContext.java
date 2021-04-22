package cn.haitaoss.tinyioc.context;

import cn.haitaoss.tinyioc.beans.BeanDefinition;
import cn.haitaoss.tinyioc.beans.factory.AbstractBeanFactory;
import cn.haitaoss.tinyioc.beans.factory.AutowireCapableBeanFactory;
import cn.haitaoss.tinyioc.beans.io.ResourceLoader;
import cn.haitaoss.tinyioc.beans.lifecycle.DisposableBean;
import cn.haitaoss.tinyioc.beans.xml.XmlBeanDefinitionReader;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * @author haitao.chen
 * @email haitaoss@aliyun.com
 * @date 2021-04-17 16:36
 *
 */
public class ClassPathXmlApplicationContext extends AbstractApplicationContext {
    private final String configLocation;

    public ClassPathXmlApplicationContext(String configLocation) throws Exception {
        this(configLocation, new AutowireCapableBeanFactory());
    }

    public ClassPathXmlApplicationContext(String configLocation, AbstractBeanFactory beanFactory) throws Exception {
        super(beanFactory);
        this.configLocation = configLocation;
        refresh();
    }

    @Override
    public void loadBeanDefinitions(AbstractBeanFactory beanFactory) throws Exception {
        XmlBeanDefinitionReader xmlBeanDefinitionReader = new XmlBeanDefinitionReader(new ResourceLoader());
        xmlBeanDefinitionReader.loadBeanDefinitions(configLocation);

        for (Map.Entry<String, BeanDefinition> entry : xmlBeanDefinitionReader.getRegistry().entrySet()) {
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
}
