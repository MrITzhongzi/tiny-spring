package cn.haitaoss.tinyioc.beans.factory;

import cn.haitaoss.tinyioc.BeanDefinition;
import cn.haitaoss.tinyioc.beans.BeanPostProcessor;

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
    private final List<String> beanDefinitionNames = new ArrayList<>();  // 记录所有bean的名字，用于提前创建bean还有就是收集beanpostProcessor
    private final Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>(); // 容器
    private List<BeanPostProcessor> beanPostProcessors = new ArrayList<>(); // 记录容器中所有的beanPostProcessor

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
        Object bean = beanDefinition.getBean(); // null
        if (bean == null) {
            bean = doCreateBean(beanDefinition);
            bean = initializeBean(bean, name); // 代理操作
            // 将操作过的bean重新设置到beanDefinition中
            beanDefinition.setBean(bean); // 修改beandefinition 里面的bean
        }
        return bean;
    }

    protected Object initializeBean(Object bean, String name) throws Exception {
        //  初始化前操作
        for (BeanPostProcessor beanPostProcessor : beanPostProcessors) { // beanPostProcessors.size() = 0
            bean = beanPostProcessor.postProcessBeforeInitialization(bean, name);
        }
        // 省略 bean 的初始化操作

        // 初始化后的操作
        for (BeanPostProcessor beanPostProcessor : beanPostProcessors) {
            bean = beanPostProcessor.postProcessAfterInitialization(bean, name);
        }
        return bean;
    }

    protected Object createBeanInstance(BeanDefinition beanDefinition) throws Exception {
        return beanDefinition.getBeanClass().newInstance();
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

    protected Object doCreateBean(BeanDefinition beanDefinition) throws Exception {
        Object bean = createBeanInstance(beanDefinition);
        beanDefinition.setBean(bean);
        // a ref b
        // a ref b , b ref c ,c ref a
        applyPropertyValues(bean, beanDefinition);
        return bean;
    }

    protected void applyPropertyValues(Object bean, BeanDefinition beanDefinition) throws Exception {

    }

    public void addBeanPostProcessor(BeanPostProcessor beanPostProcessor) throws Exception {
        this.beanPostProcessors.add(beanPostProcessor);
    }

    /**
     * 从容器中获取BeanPostProcessor 的子类或者子接口
     * @author haitao.chen
     * @email
     * @date 2021/4/17 9:28 下午
     * @param type
     * @return java.util.List
     */
    public List getBeansForType(Class type) throws Exception {
        // BeanPostProcessor.class
        List beans = new ArrayList<Object>();
        for (String beanDefinitionName : beanDefinitionNames) {
            // class2是不是class1的子类或者子接口
            // class1.isAssignableFrom(class2) AspectJAwareAdvisorAutoProxyCreator
            if (type.isAssignableFrom(beanDefinitionMap.get(beanDefinitionName).getBeanClass())) {
                beans.add(getBean(beanDefinitionName));
            }
        }
        return beans;
    }
}
