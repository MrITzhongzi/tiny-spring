package cn.haitaoss.tinyioc.beans.factory;

import cn.haitaoss.tinyioc.aop.BeanFactoryAware;
import cn.haitaoss.tinyioc.beans.BeanDefinition;
import cn.haitaoss.tinyioc.beans.BeanReference;
import cn.haitaoss.tinyioc.beans.PropertyValue;
import cn.haitaoss.tinyioc.beans.annotation.Autowired;
import cn.haitaoss.tinyioc.beans.converter.ConverterFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @author haitao.chen
 * email haitaoss@aliyun.com
 * date 2021-04-17 12:54
 *  可自动装配内容的BeanFactory
 */
public class AutowireCapableBeanFactory extends AbstractBeanFactory {
    protected void injectAnnotation(String name, Object bean, BeanDefinition beanDefinition) throws Exception {
        Field[] fields = bean.getClass().getDeclaredFields();
        for (Field field : fields) {
            Autowired autowired = field.getAnnotation(Autowired.class);
            if (autowired == null)
                continue;
            String refName = autowired.getId();
            if (refName.equals("")) {
                refName = field.getName();
            }
            // 当前引用的属性值可能没有初始化完成，也就是这个属性值需要重新赋值，所以将当前bean存储二级缓存，后面会对其ref属性重新赋值
            if (!firstCache.containsKey(refName)) {
                // 添加到二级缓存
                secondCache.put(name, null);
            }
            field.setAccessible(true);
            field.set(bean, getBean(refName));
        }
    }

    /**
     * 设置属性的值
     * @author haitao.chen
     * email
     * date 2021/4/17 1:26 下午
     * @param instance
     * @param beanDefinition
     */
    protected void applyPropertyValues(String name, Object instance, BeanDefinition beanDefinition) throws Exception {
        // 这一步是为了实现 aop
        if (instance instanceof BeanFactoryAware) {
            ((BeanFactoryAware) instance).setBeanFactory(this);
        }

        for (PropertyValue propertyValue : beanDefinition.getPropertyValues().getPropertyValueList()) {
            Object value = propertyValue.getValue();
            Object convertedValue = null;

            // 如果是BeanReference 类型的
            if (value instanceof BeanReference) {
                // ref 类型的就创建BeanReference
                BeanReference beanReference = (BeanReference) value;
                String refName = beanReference.getName();
                value = getBean(refName);
                convertedValue = value;
                // 说明当前是循环依赖状态
                if (thirdCache.containsKey(refName) && !firstCache.containsKey(refName)) {
                    // 标注a ref b,b ref a中，b是后被循环引用的
                    // 比如 a ref b, b ref a 此时secondCache里面放入的是a，也就是b里面对a的引用是不对的需要修改
                    // beanReference.getName()是 a，instance 是 b，
                    // secondCache.put(refName, instance);
                    secondCache.put(name, null); // key是这个bean对应的属性不完整
                    // secondCache.put(refName, instance);
                }
            }
            // 非ref字段，对value进行处理，将string转化成对应类型
            else {
                Field field = instance.getClass().getDeclaredField(propertyValue.getName());// 获得name对应的字段
                if (field.getType().toString().equals("class java.lang.String"))
                    convertedValue = value;
                else
                    convertedValue = ConverterFactory.getConverterMap().get(field.getType()).parse((String) value);
            }
            try {
                Method declaredMethod = instance.getClass().getDeclaredMethod(
                        "set" + propertyValue.getName().substring(0, 1).toUpperCase()
                                + propertyValue.getName().substring(1), value.getClass());
                declaredMethod.setAccessible(true);

                declaredMethod.invoke(instance, convertedValue);
            } catch (NoSuchMethodException e) {
                // 没有提供set方法也设置
                Field declaredField = instance.getClass().getDeclaredField(propertyValue.getName());
                declaredField.setAccessible(true);
                declaredField.set(instance, convertedValue);
            }
        }
    }
}
