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

# step-6-invite-application-context

# step-7-method-interceptor-by-jdk-dynamic-proxy
- AdvisedSupport 里面有TargetSource 和 MethodInterceptor
  - TargetSource：里面包含被代理对象 和 被代理对象的接口
  - MethodInterceptor：当前被代理对象需要增加的功能
- 因为每个被代理对象需要增加的功能可能不一样，所以我们把被代理对象需要增加的功能放到advise 中
```java
public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        MethodInterceptor methodInterceptor = advise.getMethodInterceptor();
        return methodInterceptor.invoke(new ReflectiveMethodInvocation(advise.getTargetSource().getTarget(), method, args));
    }
```

# step-8-invite-pointcut-and-aspectj
加入切入点和 beanPostProcessor

# step-9-auto-create-aop-proxy
通过BeanPostProcessor来实现aop 和 spring的整合。

# step-10-invite-cglib-and-aopproxy-factory
使用cglib代理

# 解决循环引用出现属性值赋值不完全的问题
通过三级缓存解决

# 补充文件
修改一下readme文件

# 优化代理
- 优化代码，如果被代理对象有实现接口，就使用jdk代码。否则使用cglib代理
- 向三级缓存插入数据的位置调整

问题：

    -  使用cglib进行代理，只能通过set方法注入属性。field.set() 设置的是代理对象的属性不是被代理对象的 
    -  使用jdk进行代理，无法通过反射设置值。method.invoke() filed.set() 都出错。

# 实现构造器的解析

```java
protected Object doCreateBean(String name, BeanDefinition beanDefinition) throws Exception {
        Object bean = createBeanInstance(beanDefinition);
        // thirdCache中放置的全是空构造方法构造出的实例
        thirdCache.put(name, bean);
        beanDefinition.setBean(bean);
        applyPropertyValues(name, bean, beanDefinition);
        // 解析类里面的注解
        injectAnnotation(name, bean, beanDefinition);
        // 生命周期
        if (bean instanceof InitializingBean) {
            ((InitializingBean) bean).afterPropertiesSet();
        }
        return bean;
    }
```

```java
private Object createBeanInstance(BeanDefinition beanDefinition) throws Exception {
        if (beanDefinition.getConstructorArgument().isEmpty()) {
            return beanDefinition.getBeanClass().newInstance();
        } else {
            List<ConstructorArgument.ValueHolder> valueHolders = beanDefinition.getConstructorArgument().getArgumentValues();
            Class clazz = Class.forName(beanDefinition.getBeanClassName());
            Constructor[] cons = clazz.getConstructors();
            for (Constructor constructor : cons) {
                // 这里省去判断参数类型
                if (constructor.getParameterCount() == valueHolders.size()) {
                    Object[] params = new Object[valueHolders.size()];
                    for (int i = 0; i < params.length; i++) {
                        params[i] = valueHolders.get(i).getValue();
                        if (params[i] instanceof BeanReference) {
                            BeanReference ref = (BeanReference) params[i];
                            String refName = ref.getName();
                            if (thirdCache.containsKey(refName) && !firstCache.containsKey(refName)) {
                                throw new IllegalAccessException("构造函数循环依赖" + refName);
                            } else {
                                params[i] = getBean(refName);
                            }
                        }
                    }
                    return constructor.newInstance(params);
                }
            }
        }
        return null;
    }
```

# 增加类型解析器，解析复杂类型的属性
- 我们自定义的类型的属性的赋值。我们可能有一些定制化需求。所以我们可以通过向容器中加入Converter来实现。Converter接口的parse方法的返回值，就是最终注入个属性的值
- 容器刷新时，注册converter到ConverterFactory中
```java
public void refresh() throws Exception {
        // 将读取xml 获取的容器复制到 beanFactory中
        loadBeanDefinitions(beanFactory);
        // 注册类型转换器
        registerConverter(beanFactory);
        // 注册beanPostProcessor
        registerBeanPostProcessors(beanFactory);
        // 创建出ioc容器中所有的对象
        onRefresh();
    }

    protected void registerConverter(AbstractBeanFactory beanFactory) throws Exception {
        List beanConverters = beanFactory.getBeansForType(Converter.class);
        for (Object converter : beanConverters) {
            Type type = ((Converter) converter).getType();
            beanFactory.getConverterFactory().getConverterMap().put(type, (Converter) converter);
        }
    }
```
- 创建bean，属性解析时。如果不是BeanDefinition 类型而且不是string类型的属性。我们应该通过conver进行解析
```java
// 非ref字段，对value进行处理，将string转化成对应类型
Field field = instance.getClass().getDeclaredField(propertyValue.getName());// 获得name对应的字段
if (field.getType().toString().equals("class java.lang.String"))
    convertedValue = value;
else
    convertedValue = this.converterFactory.getConverterMap().get(field.getType()).parse((String) value);
```

# 完善bean的声明周期
```java
if (bean == null) {
    bean = doCreateBean(name, beanDefinition);
    bean = initializeBean(bean, name); // 代理操作
    // 将操作过的bean重新设置到beanDefinition中
    beanDefinition.setBean(bean); // 修改beandefinition 里面的bean
}
```
- doCreateBean：给bean的属性赋值之后会调用bean的afterPropertiesSet 初始化bean
```java
applyPropertyValues(name, bean, beanDefinition);
// 生命周期
if(bean instanceof InitializingBean){
    ((InitializingBean) bean).afterPropertiesSet();
}
```

- initializeBean：调用BeanPostProcessor 的before方法后会获取bean的 init_method 初始化
- applicationContext.close()：调用bean的销毁方法

# 实现扫描注解加载bean

## 解析类里面的注解，注入属性

```java
 protected Object doCreateBean(String name, BeanDefinition beanDefinition) throws Exception {
        Object bean = createBeanInstance(beanDefinition);
        // thirdCache中放置的全是空构造方法构造出的实例
        thirdCache.put(name, bean);
        beanDefinition.setBean(bean);
        applyPropertyValues(name, bean, beanDefinition);
   		
        // 解析类里面的注解
        injectAnnotation(bean, beanDefinition);

        // 生命周期
        if (bean instanceof InitializingBean) {
            ((InitializingBean) bean).afterPropertiesSet();
        }
        return bean;
    }
```
## 设置单例bean的标记

```java
private void processBeanDefinition(Element ele) {
        String name = ele.getAttribute("id");
        String className = ele.getAttribute("class");
        BeanDefinition beanDefinition = new BeanDefinition();
        // 解析是否是单例
        String scope = ele.getAttribute("scope");
        if (scope == null || scope.length() == 0 || scope.equals("singleton")) {
            beanDefinition.setSingleton(true);
        } else {
            beanDefinition.setSingleton(false);
        }
        // 解析构造器参数
        processConstructorArgument(ele, beanDefinition);
        processProperty(ele, beanDefinition);
        beanDefinition.setBeanClassName(className);
        getRegistry().put(name, beanDefinition);

    }
```
## 判断是不是单例bean

```java
public Object getBean(String name) throws Exception {
        BeanDefinition beanDefinition = beanDefinitionMap.get(name);
        if (beanDefinition == null) {
            throw new IllegalArgumentException("No bean named " + name + "is defined");
        }
        Object bean = beanDefinition.getBean(); // null
        // 如果bean为null 或者不是单例bean
        if (bean == null || !beanDefinition.isSingleton()) {
            bean = doCreateBean(name, beanDefinition);
            bean = initializeBean(bean, name); // 代理操作
            // 将操作过的bean重新设置到beanDefinition中
            beanDefinition.setBean(bean); // 修改beandefinition 里面的bean
        }
        return bean;
    }
```
## AnnotationParser

1. 根据packageName 收集当前包和子包下面的所有非内部类(Set<String>)

2. 遍历Set，Class.forName(className).isAnnotation(xx)。判断当前class 是否有Component、Repository、Service、Controller 注解

3. 存在注解，那就创建BeanDefinition。然后将生成BeanDefinition 添加到map中临时存储
    - 获取BeanDefinition的name值。annotation.value(); 获取不到就取当前类(首字母小写)作为name的值
    - 获取当前class 里面所有的属性，判断是否标注了@Value注解，如果有就创建成PropertyValue，添加到BeanDefinition的PropertyValues中

4. 使用AnnotationParser

    - 修改loadBeanDefinitions 方法的内容

      ```java
       public void refresh() throws Exception {
              // 将读取xml 获取的容器复制到 beanFactory中
              loadBeanDefinitions(beanFactory);
              // 注册类型转换器
              registerConverter(beanFactory);
              // 注册beanPostProcessor
              registerBeanPostProcessors(beanFactory);
              // 创建出ioc容器中所有的对象
              onRefresh();
          }
      ```

      

    - 不仅仅会注册读取xml得到的BeanDefinition，而且也要注册通过扫描注解生成的BeanDefinition

    ```java
        @Override
        public void loadBeanDefinitions(AbstractBeanFactory beanFactory) throws Exception {
            // 去取xml配置文件注册 BeanDefinition
            XmlBeanDefinitionReader xmlBeanDefinitionReader = new XmlBeanDefinitionReader(new ResourceLoader());
            xmlBeanDefinitionReader.loadBeanDefinitions(configLocation);
    
            for (Map.Entry<String, BeanDefinition> entry : xmlBeanDefinitionReader.getRegistry().entrySet()) {
                beanFactory.registerBeanDefinition(entry.getKey(), entry.getValue());
            }
    
            // 扫描主包下面的类注册 BeanDefinition
            String packageName = xmlBeanDefinitionReader.getPackageName();
            if (packageName == null) {
                return;
            }
            AnnotationParser annotationParser = new AnnotationParser();
            annotationParser.annotationBeanDefinitionReader(packageName);
            for (Map.Entry<String, BeanDefinition> entry : annotationParser.getRegistry().entrySet()) {
                beanFactory.registerBeanDefinition(entry.getKey(), entry.getValue());
            }
        }
    
    
    ```

    

