# step-1-container-register-and-get

1. 定义BeanDefinition
    - 统一所有对象的信息
2. 定义BeanFactory
    - 存储所有bean的容器

# step-2-abstract-beanfactory-and-do-bean-initilizing-in-it

1. 修改BeanDefinition
2. 接口化BeanFactory

# step-3-inject-bean-with-property

1. 修改BeanDefinition

- 增加PropertyValues属性，里面存放bean 的初始化属性的值

2. 修改AutowireCapableBeanFactory

- 增加读取beanDefinition 中propertyValues中的属性值，赋值给对应的bean

# step-4-config-beanfactory-with-xml

读取xml 文件来初始化bean

# step-5-inject-bean-to-bean

为bean注入bean。通过BeanReference 表示这个属性是一个bean对象，他的值应该从IOC容器中获取