package cn.haitaoss.tinyioc.beans.factory;

import cn.haitaoss.tinyioc.BeanDefinition;
import cn.haitaoss.tinyioc.beans.BeanReference;
import cn.haitaoss.tinyioc.beans.PropertyValue;

import java.lang.reflect.Field;

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
        Class<?> aClass = instance.getClass();
        for (PropertyValue propertyValue : beanDefinition.getPropertyValues().getPropertyValueList()) {
            Field field = aClass.getDeclaredField(propertyValue.getName());
            field.setAccessible(true);
            Object value = propertyValue.getValue();
            if (value instanceof BeanReference) {
                BeanReference beanReference = (BeanReference) value;
                value = getBean(beanReference.getName());
            }
            field.set(instance, value);
        }
    }
}
