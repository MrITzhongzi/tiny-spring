package cn.haitaoss.tinyioc.context;

import cn.haitaoss.tinyioc.beans.BeanDefinition;
import cn.haitaoss.tinyioc.beans.BeanPostProcessor;
import cn.haitaoss.tinyioc.beans.PropertyValue;
import cn.haitaoss.tinyioc.beans.factory.AbstractBeanFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
 * @author haitao.chen
 * @email haitaoss@aliyun.com
 * @date 2021-04-17 16:34
 *
 */
public abstract class AbstractApplicationContext implements ApplicationContext {
    protected AbstractBeanFactory beanFactory;

    public AbstractApplicationContext(AbstractBeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    public void refresh() throws Exception {
        // 将读取xml 获取的容器复制到 beanFactory中
        loadBeanDefinitions(beanFactory);
        // 注册beanPostProcessor
        registerBeanPostProcessors(beanFactory);
        // 创建出ioc容器中所有的对象
        onRefresh();
    }

    protected void registerBeanPostProcessors(AbstractBeanFactory beanFactory) throws Exception {
        // 从容器中获取所有的BeanPostProcessor
        List beanPostProcessors = beanFactory.getBeansForType(BeanPostProcessor.class);

        // beanPostProcessor
        for (Object beanPostProcessor : beanPostProcessors) {
            beanFactory.addBeanPostProcessor((BeanPostProcessor) beanPostProcessor);
        }
    }

    protected void onRefresh() throws Exception {
        beanFactory.preInstantiateSingletons();
        checkoutAll();
    }

    private void checkoutAll() {
        Map<String, Object> secondCache = beanFactory.getSecondCache();
        Map<String, BeanDefinition> beanDefinitionMap = beanFactory.getBeanDefinitionMap();
        // 二级缓存中 key就是属性不完整的a，value
        // son parent
        for (Map.Entry<String, Object> entry : secondCache.entrySet()) {
            String invokeBeanName = entry.getKey();
            BeanDefinition beanDefinition = beanDefinitionMap.get(invokeBeanName);
            try {
                resetReference(invokeBeanName, beanDefinition);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // 重置依赖，这边用到了动态类型转换。因为原类型的setter在代理类中已经无法使用了。
    private void resetReference(String invokeBeanName, BeanDefinition beanDefinition) throws Exception {
        Map<String, Object> thirdCache = beanFactory.getThirdCache();
        Map<String, Object> firstCache = beanFactory.getFirstCache();
        for (PropertyValue propertyValue : beanDefinition.getPropertyValues().getPropertyValueList()) {
            String refName = propertyValue.getName();
            // 我们只关心ref 类型的属性
            if (firstCache.containsKey(refName)) {
                Object exceptedValue = firstCache.get(refName); // 完全初始化的对象
                Object invokeBean = beanDefinition.getBean(); // 需要修改这个对象里面的属性

                Object realClassInvokeBean = thirdCache.get(invokeBeanName);
                Object realClassRefBean = thirdCache.get(refName);
                // JdkDynamicObject extend Proxy implement xx
                try {
                    Method declaredMethod = realClassInvokeBean.getClass().getDeclaredMethod(
                            "set" +
                                    propertyValue.getName().substring(0, 1).toUpperCase() +
                                    propertyValue.getName().substring(1),
                            realClassRefBean.getClass()
                    );

                    declaredMethod.setAccessible(true);
                    declaredMethod.invoke((realClassInvokeBean.getClass().cast(invokeBean)), (realClassRefBean.getClass().cast(exceptedValue)));
                } catch (NoSuchMethodException e) {
                    Field declaredField = realClassInvokeBean.getClass().getDeclaredField(propertyValue.getName());
                    declaredField.setAccessible(true);
                    declaredField.set((realClassInvokeBean.getClass().cast(invokeBean)), (realClassRefBean.getClass().cast(exceptedValue)));
                }
            }
        }
    }

    protected abstract void loadBeanDefinitions(AbstractBeanFactory beanFactory) throws Exception;

    @Override
    public Object getBean(String name) throws Exception {
        return beanFactory.getBean(name);
    }
}
