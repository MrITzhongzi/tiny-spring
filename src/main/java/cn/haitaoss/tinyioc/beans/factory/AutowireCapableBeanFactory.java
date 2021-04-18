package cn.haitaoss.tinyioc.beans.factory;

import cn.haitaoss.tinyioc.BeanDefinition;
import cn.haitaoss.tinyioc.aop.BeanFactoryAware;
import cn.haitaoss.tinyioc.beans.BeanReference;
import cn.haitaoss.tinyioc.beans.PropertyValue;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @author haitao.chen
 * @email haitaoss@aliyun.com
 * @date 2021-04-17 12:54
 *  可自动装配内容的BeanFactory
 */
public class AutowireCapableBeanFactory extends AbstractBeanFactory {
    /**
     * 设置属性的值
     * @author haitao.chen
     * @email
     * @date 2021/4/17 1:26 下午
     * @param instance
     * @param beanDefinition
     */
    protected void applyPropertyValues(Object instance, BeanDefinition beanDefinition) throws Exception {
        // 这一步是为了实现 aop
        if (instance instanceof BeanFactoryAware) {
            ((BeanFactoryAware) instance).setBeanFactory(this);
        }

        for (PropertyValue propertyValue : beanDefinition.getPropertyValues().getPropertyValueList()) {
            Object value = propertyValue.getValue();
            if (value instanceof BeanReference) {
                BeanReference beanReference = (BeanReference) value;
                value = getBean(beanReference.getName());
            }
            try {
                Method declaredMethod = instance.getClass().getDeclaredMethod(
                        "set" + propertyValue.getName().substring(0, 1).toUpperCase()
                                + propertyValue.getName().substring(1), value.getClass());
                declaredMethod.setAccessible(true);

                declaredMethod.invoke(instance, value);
            } catch (NoSuchMethodException e) {
                // 没有提供set方法也设置
                Field declaredField = instance.getClass().getDeclaredField(propertyValue.getName());
                declaredField.setAccessible(true);
                declaredField.set(instance, value);
            }
        }
    }
}
