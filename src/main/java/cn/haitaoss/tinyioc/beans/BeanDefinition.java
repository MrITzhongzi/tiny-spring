package cn.haitaoss.tinyioc.beans;

/**
 * @author haitao.chen
 * @email haitaoss@aliyun.com
 * @date 2021-04-17 11:15
 * bean的内容及元数据，包装bean的实体
 */
public class BeanDefinition {
    private Object bean;
    private Class beanClass;
    private String beanClassName;
    private PropertyValues propertyValues = new PropertyValues();
    private ConstructorArgument constructorArgument = new ConstructorArgument();
    private boolean isSingleton;

    public BeanDefinition() {
    }

    public boolean isSingleton() {
        return isSingleton;
    }

    public void setSingleton(boolean singleton) {
        isSingleton = singleton;
    }

    public ConstructorArgument getConstructorArgument() {
        return constructorArgument;
    }

    public void setConstructorArgument(ConstructorArgument constructorArgument) {
        this.constructorArgument = constructorArgument;
    }

    public Object getBean() {
        return bean;
    }

    public void setBean(Object bean) {
        this.bean = bean;
    }

    public Class getBeanClass() {
        return beanClass;
    }

    public void setBeanClass(Class beanClass) {
        this.beanClass = beanClass;
    }

    public String getBeanClassName() {
        return beanClassName;
    }

    public void setBeanClassName(String beanClassName) {
        this.beanClassName = beanClassName;
        try {
            this.beanClass = Class.forName(beanClassName);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    public PropertyValues getPropertyValues() {
        return propertyValues;
    }

    public void setPropertyValues(PropertyValues propertyValues) {
        this.propertyValues = propertyValues;
    }
}
