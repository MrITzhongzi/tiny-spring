package cn.haitaoss.tinyioc.beans.factory;

import cn.haitaoss.tinyioc.BeanDefinition;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author haitao.chen
 * @email haitaoss@aliyun.com
 * @date 2021-04-17 11:48
 *
 */
public abstract class AbstractBeanFactory implements BeanFactory {
    private final List<String> beanDefinitionNames = new ArrayList<>();
    private final Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();

    /**
     * 创建单例bean
     * @author haitao.chen
     * @email
     * @date 2021/4/17 3:14 下午
     * @param name
     * @return java.lang.Object
     */
    @Override
    public Object getBean(String name) throws Exception {
        BeanDefinition beanDefinition = beanDefinitionMap.get(name);
        if (beanDefinition == null) {
            throw new IllegalArgumentException("No bean named " + name + "is defined");
        }
        Object bean = beanDefinition.getBean();
        if (bean == null) {
            bean = doCreateBean(beanDefinition);
        }
        return bean;
    }

    public void registerBeanDefinition(String name, BeanDefinition beanDefinition) throws Exception {
        beanDefinitionMap.put(name, beanDefinition);
        beanDefinitionNames.add(name);
    }

    public void preInstantiateSingletons() throws Exception {
        for (String beanName : beanDefinitionNames) {
            getBean(beanName);
        }

    }

    /**
     * 初始化bean
     * @author haitao.chen
     * @email
     * @date 2021/4/17 11:50 上午
     * @param beanDefinition
     */
    protected abstract Object doCreateBean(BeanDefinition beanDefinition) throws Exception;
}
