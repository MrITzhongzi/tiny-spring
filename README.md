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

# 容器初始化流程
## 核心对象

- AbstractBeanFactory： 
    - List<String> beanDefinitionNames = new ArrayList<>();  // 记录所有bean的名字，用于提前创建bean还有就是收集BeanPostProcessor 
    - Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>(); // 容器
    - List<BeanPostProcessor> beanPostProcessors = new ArrayList<>(); // 记录容器中所有的beanPostProcessor
    
- BeanDefinition:
    - private Object bean; // bean对象本身
    - private Class beanClass; // 对象对应的Class实例
    - private String beanClassName; // 对象对应的类全名(比如：cn.haitaoss.HelloWorldService)
    - private PropertyValues propertyValues = new PropertyValues(); // 保存这个bean标签里面所有property子标签的信息
    
- PropertyValue:
    - private final String name; // 属性名
    - private final Object value; // 属性值（有两种情况：value instanceof BeanReference）
    
- BeanReference:
    - private String name; // 对应bean 对象的名字
    - private Object bean;
    
- BeanPostProcessor:
    - 这是一个接口，在初始化容器内所有bean的时候会获取容器中所有的BeanPostProcessor的实现，调用实现类的方法，初始化bean
    
- AdvisedSupport: spring中判断一个对象需要被代理后，会把这个对象封装到AdvisedSupport中，然后代理AdvisedSupport 再将生成的代理对象覆盖掉之前容器的原始对象
    - private TargetSource targetSource; // 被代理对象
    - private MethodInterceptor methodInterceptor; // 增强方法
    - private MethodMatcher methodMatcher; // 保存切入点表达式，在调用方法的时候判断这个方法是否满足表达式
- JdkDynamicAopProxy：jdk代理的想法很简单，你是代理对象那么你应该知道你的增强方法是什么，我去执行即可
```java
 @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        MethodInterceptor methodInterceptor = advised.getMethodInterceptor(); // 获取增强方法

        // 通过切面表达式实现只代理符合切入点表达式的方法
        if (advised.getMethodMatcher() != null
                && advised.getMethodMatcher().matches(method, advised.getTargetSource().getTarget().getClass())) {

            // 被代理
            return methodInterceptor.invoke(new ReflectiveMethodInvocation(advised.getTargetSource().getTarget(),
                    method, args));
        } else {
            // 不被代理
            return method.invoke(advised.getTargetSource().getTarget(), args);
        }
    }
```



## 初始化ioc容器

- 创建applicationContext，构造器里面是执行 `refresh();`
    - loadBeanDefinitions(beanFactory); 
        - 会读取xml 将内容添加到map中。每一个bean标签就是一个BeanDefinition 对象，bean 标签里面的property 标签会封装成PropertyValue 添加到BeanDefinition 的PropertyValues属性中
        - 遍历刚才读取的map中的内容，添加到beanDefinitionMap 和 beanDefinitionNames 中 
    - registerBeanPostProcessors(beanFactory); // 注册beanPostProcessor
      
        - 从容器中获取所有的BeanPostProcessor，获取的时候会创建出对应的bean实例（所以BeanPostProcessor 会先创建好）
    - onRefresh();// 创建出ioc容器中所有的对象
        - 遍历beanDefinitionNames 获取bean 的名字调用 getBean() 方法
            - getBean():
            ```java
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

            - 逻辑是先从beanDefinitionMap 获取BeanDefinition如果没有说明没有定义过直接报错。如果有获取BeanDefinition 中的bean，如果为null说明没有创建，应该自行创建逻辑
            - doCreateBean(beanDefinition); 的代码很简单就是通过BeanDefinition 中的beanClass.newInstance()。然后设置到BeanDefinition中。并且对属性进行初始化
                - applyPropertyValues(bean, beanDefinition);
                    - 会读取beanDefinition 里面的PropertyValues复制到bean对象。 如果`value instanceof BeanReference` 那么他的值应该是getBean(name)
                    - 所以说spring依赖注入的特点是 先创建后注入
                    - 还需要注意的是如果当前 `bean instanceof FactoryAware` 我们需要传入参数给这个bean，方便后续工作
            - initializeBean(bean, name); // 代理操作
                - 遍历beanPostProcessors 对我们的bean进行初始化操作。
                - 秒的地方是 AspectJAwareAdvisorAutoProxyCreator是BeanPostProcessor 的实现类
                - 那我们可以在postProcessAfterInitialization 里面实现**织入**的操作
                    - 拿到容器中所有的增强器类 `List<AspectJExpressionPointcutAdvisor> advisors = beanFactory
                .getBeansForType(AspectJExpressionPointcutAdvisor.class);`
                    - 遍历增强器类 判断当前bean是否符合切入点表达式。
                        - 满足表达式。
                            - 将当前bean、增强方法、表达式方法校验器 封装到 AdvisedSupport中
                            - 通过JdkDynamicAopProxy 代理 AdvisedSupport
                            - 返回JdkDynamicAopProxy 生成的代理对象。
            ```
## 从容器中获取代理对象调用方法

- 从容器中获取bean，调用方法的过程（这里就举例：代理对象的情况）。`((HelloWorldService) context.getBean("helloWorldService")).sayHello();`
    
    - helloWorldService 是通过jdk动态代理产生的代理对象，那么执行方法的时候肯定会执行invocationHandle的invoke方法。下面是invoke方法的实现
    - 因为我们的被代理对象是进行封装过的，具体的增强方法已经封装到被代理对象中了。
        - 从被代理对象advised 中获取增强方法
        - 是否需要调用增强方法还需要进行判断，当前的方法是否符合切入点表达式
    ```java
    
    @Override
public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    
    MethodInterceptor methodInterceptor = advised.getMethodInterceptor(); // 获取增强方法
    
        // 通过切面表达式实现只代理符合切入点表达式的方法
        if (advised.getMethodMatcher() != null
            && advised.getMethodMatcher().matches(method, advised.getTargetSource().getTarget().getClass())) {
    
            // 被代理
            return methodInterceptor.invoke(new ReflectiveMethodInvocation(advised.getTargetSource().getTarget(),
                    method, args));
        } else {
            // 不被代理
            return method.invoke(advised.getTargetSource().getTarget(), args);
        }
    }
    ```