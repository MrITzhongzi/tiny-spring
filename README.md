# 来源
原作者：https://github.com/code4craft/tiny-spring
参考的博客：https://blog.csdn.net/w8253497062015/article/details/90274387#%E7%90%86%E8%A7%A3%E5%8A%A8%E6%80%81%E4%BB%A3%E7%90%86%E8%AE%BE%E8%AE%A1%E6%A8%A1%E5%BC%8F


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


# 使用三级缓存解决循环引用缺少代理对象的问题
> 基础知识

firstCache：存储的是完整初始化过后的对象
secondCache：存储的是属性值有问题的对象（待容器初始化完全后，会拿出这里的对象重新赋值）
thirdCache：存储的是无参构造器初始化的对象

> 功能

firstCache
    - 判断当前bean是否应该放入二级缓存
    - 容器完成初始化之后，需要对二级缓存里面的bean的属性重新赋值。值保存在三级缓存中（其实也能从beanFactory.beanDefinitionMap 中拿）
secondCache
    - 保存属性赋值有问题的bean
thirdCache
    - 校验一个bean 是否应该放入二级缓存
    - 记录这个bean 没有被代理之前的类型，为bean 重新赋值的时候需要反射获取原始类型


> 流程

1. 利用无参构造器初始化对象之后将这个对象存入thirdCache
2. 给对象属性赋值时，如果当前属性是引用属性，需要判断这个属性值在三级缓存中是否存在，不存在就将该对象存入secondCache，表示这个对象的属性值并不完整后续需要修改
3. 对象完成完整的初始化流程之后，要把这个对象存入firstCache
4. 所有的对象已经操作完了。遍历secondCache，如果有值说明这个对象里面的属性需要重新赋值。找到这个对象里面属性值类型为ref的属性。从firstCache 取出来赋值进去。

> 缺点

通过反射赋值的时候有问题，因为类型匹配不对。解决办法对象的属性类型写实现类别写接口
```java
 Method declaredMethod = realClassInvokeBean.getClass().getDeclaredMethod("set" + propertyValue.getName().substring(0, 1).toUpperCase()
                            + propertyValue.getName().substring(1), realClassRefBean.getClass());
 // Method declaredMethod = realClassInvokeBean.getClass().getDeclaredMethod("set" + propertyValue.getName().substring(0, 1).toUpperCase() + propertyValue.getName().substring(1), realClassRefBean.getClass().getInterfaces()[0]);
```
```java
public class GrandSonImp implements GrandSon{
    private Parent parent;

    public void setParent(ParentImpl parent) {
        this.parent = parent;
    }

    @Override
    public void say() {
        System.out.println("parent 是不是代理对象 "+ (parent instanceof Proxy || parent instanceof Factory));
    }
}
```
# 项目中用到的依赖是什么
## aopalliance
```xml
<dependency>
    <groupId>aopalliance</groupId>
    <artifactId>aopalliance</artifactId>
    <version>1.0</version>
</dependency>
```
AOP Alliance 是AOP的接口标准，定义了 AOP 中的基础概念(Advice、CutPoint、Advisor等)，目标是为各种AOP实现提供统一的接口，本身并不是一种 AOP 的实现。Spring AOP, GUICE等都采用了AOP Alliance中定义的接口，因而这些lib都需要依赖 aopalliance.jar

## aspectjweaver
```xml
<dependency>
    <groupId>org.aspectj</groupId>
    <artifactId>aspectjweaver</artifactId>
    <version>1.6.11</version>
</dependency>
```
可以帮我们校验切入点表达式。比如`execution(* cn.haitaoss.tinyioc.HelloWorldService.sayHello(..))`

## cglib
比jdk代理强，能代理类 

# cglib代理和jdk代理
原文：https://blog.csdn.net/w8253497062015/article/details/90274387#%E7%90%86%E8%A7%A3%E5%8A%A8%E6%80%81%E4%BB%A3%E7%90%86%E8%AE%BE%E8%AE%A1%E6%A8%A1%E5%BC%8F
- CGlib的原理是通过对字节码的操作，可以动态的生成一个目标实例类的子类，这个子类和目标实例的子类相同的基础上，还增加了代理代码或者叫advice。代理类 = 被代理类+增强逻辑

    - CGlib动态代理

      ```java
      class Student{
          private String name = "zhang san";
      
          public String getName() {
              System.out.println(name);
              return name;
          }
      
          public void setName(String name) {
              this.name = name;
          }
      }
      public class CglibMthodTwo implements MethodInterceptor {
          public Object getProxy(Class clazz){
              Enhancer en = new Enhancer();
              en.setSuperclass(clazz);
              en.setCallback(this);
              Object proxy = en.create();
              return proxy;
          }
          @Override
          public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
              System.out.println("前");
              Object res =  methodProxy.invokeSuper(o,objects);
              System.out.println("后");
              return res;
          }
      
          public static void main(String[] args) {
              CglibMthodTwo cglibMthodTwo = new CglibMthodTwo();
              ((Student)cglibMthodTwo.getProxy(Student.class)).getName();
      
          }
      
      }
      
      
      ```

    - jdk动态代理

      ```java
      public class JdkDynamicAopProxy extends AbstractAopProxy implements InvocationHandler {
      
          public JdkDynamicAopProxy(AdvisedSupport advised) {
              super(advised);
          }
      
          @Override
          public Object getProxy() {
              return Proxy.newProxyInstance(getClass().getClassLoader(), advised.getTargetSource().getInterfaces(), this);
          }
      
          @Override
          public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
              MethodInterceptor methodInterceptor = advised.getMethodInterceptor();
              Object res = null;
              if (advised.getMethodMatcher() != null
                      && advised.getMethodMatcher().matches(method, advised.getTargetSource().getTarget().getClass())) {
                  res = methodInterceptor.invoke(new ReflectiveMethodInvocation(advised.getTargetSource().getTarget(),
                          method, args));
              } else {
                  res = method.invoke(advised.getTargetSource().getTarget(), args);
      
              }
              return res;
          }
      
      }
      
      ```

      

- 与JDK动态代理的区别

    - 原理上JDK没有修改字节码，而是采用$proxyn extend Proxy implements InterfaceXXX的方式创建了一个被代理接口的实现类，然后在**运行期写class文件，再用classloader加载**。而CGlib却是操作字节码，将被代理类的字节码增强成一个子类，因此要导入ASM包。
    - 操作上，JDK动态代理创建为Proxy类实例，且必须要传入InvocationHandler类，而Cglib创建为Enhancer类实例，且必须传入MethodInterceptor类（注意包的问题，这个MethodInterceptor是CGlib中的）。
    - Advice即代理代码的实现上，JDK动态代理可以在InvocationHandler中重写invoke实现，或者在InvocationHandler.invoke中调用methodInterceptor.invoke（methodInvocation），将代理的业务代码交给methodInterceptor去做，被代理实例方法的运行通过参数methodInvocation.proceed()实现。而在CGlib中，通过methodInterceptor.intercept()实现代理增强，值得注意的是，这个方法内部有四个参数，包括一个被代理实例，而JDK的InvocationHandler.invoke却不包含被代理实例。
    
# Spring总结

## 核心功能

- XmlBeanDefinitionReader。读取xml配置文件 将每一个bean 封装成 BeanDefinition。如果有component-scan标签就保存base-package 的值。
- AnnotationParser。以 base-package 为主目录，递归收集所有非内部类的class。遍历这些class找到使用了（Component、Repository、Service、Controller）注解的class，封装成 BeanDefinition。
- BeanPostProcessor。Bean的初始化会调用这个接口里面的方法。
- AspectJAwareAdvisorAutoProxyCreator。他其实就是BeanPostProcessor 的实现，借助BeanPostProcessor 实现aop 和 ioc容器初始化的织入。
- Converter。类型转换器，对于非基本数据类型、非BeanReference类型的属性的赋值。我们需要实现这个接口自定义属性的值。
- ProxyFactory。返回代理对象，至于是jdk代理还是cglib代理看配置。
- AspectJExpressionPointcutAdvisor。校验切入点表达式、增强方法

## 关键的类

BeanDefinition：保存bean的元数据

```java
public class BeanDefinition {
    private Object bean;
    private Class beanClass;
    private String beanClassName;
    private PropertyValues propertyValues = new PropertyValues();
    private ConstructorArgument constructorArgument = new ConstructorArgument();
    private boolean isSingleton;
}
```

AbstractBeanFactory：就是applicationContext

```java
public abstract class AbstractBeanFactory implements BeanFactory {
    private final List<String> beanDefinitionNames = new ArrayList<>();  // 记录所有bean的名字，用于提前创建bean还有就是收集beanpostProcessor
    private final Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>(); // 容器
    private final List<BeanPostProcessor> beanPostProcessors = new ArrayList<>(); // 记录容器中所有的beanPostProcessor
    // 三级缓存 解决循环引用缺少代理的问题
    protected Map<String, Object> secondCache = new HashMap<>();
    protected Map<String, Object> thirdCache = new HashMap<>();
    protected Map<String, Object> firstCache = new HashMap<>();
    // 解析基本数据类型
    protected ConverterFactory converterFactory = new ConverterFactory();
    protected AbstractApplicationContext context;
}
```

AdvisedSupport：被代理类的容器类。

```java
public class AdvisedSupport {
    private TargetSource targetSource;
    private MethodInterceptor methodInterceptor;
    private MethodMatcher methodMatcher; // 切面编程需要的东西

    public MethodMatcher getMethodMatcher() {
        return methodMatcher;
    }

    public void setMethodMatcher(MethodMatcher methodMatcher) {
        this.methodMatcher = methodMatcher;
    }

    public TargetSource getTargetSource() {
        return targetSource;
    }

    public void setTargetSource(TargetSource targetSource) {
        this.targetSource = targetSource;
    }

    public MethodInterceptor getMethodInterceptor() {
        return methodInterceptor;
    }

    public void setMethodInterceptor(MethodInterceptor methodInterceptor) {
        this.methodInterceptor = methodInterceptor;
    }
}

```



## IOC容器构建流程

1. 创建IOC容器

```java
public ClassPathXmlApplicationContext(String configLocation, AbstractBeanFactory beanFactory, ApplicationContext parent) throws Exception {
  super(beanFactory);
  this.setParent(parent);
  this.configLocation = configLocation;
  
  // 刷新操作
  refresh();
}
```

2. refresh()

```java
public void refresh() throws Exception {
  beanFactory.setContext(this);
  // 将读取xml、class 获取的容器复制到 beanFactory中
  loadBeanDefinitions(beanFactory);
  // 注册类型转换器
  registerConverter(beanFactory);
  // 注册beanPostProcessor
  registerBeanPostProcessors(beanFactory);
  // 创建出ioc容器中所有剩余的单实例对象
  onRefresh();
}
```

3. loadBeanDefinitions

   1. xmlBeanDefinitionReader
      - 读取xml的原理很简单。通过Document相关的api获取xml的根节点beans。在获取子节点bean或者component-scan。一个bean会对应封装成BeanDefinition，然后保存到一个map中临时存储。
      - bean下面会有property标签。property标签会封装成PropertyValue对象，然后保存到BeanDefinition中
      - property标签有两种情况，value 或者 ref。如果是ref 会创建一个BeanReference对象，表示这个属性的值应该从IOC容器中获取
      - bean下面还会有 constructor-arg 标签。会封装成 ValueHolder对象 保存到BeanDefinition的ConstructorArgument中
   2. AnnotationParser
      - 以packageName为起点，递归遍历所有的目录。收集所有非内部类的class
      - 遍历class 找到包含 {Component.class, Service.class, Repository.class, Controller.class}; 的class实例，一个class 实例对应一个BeanDefinition
      - 遍历class实例里面的所有属性，找到使用@Value注解标注的属性。将这个属性封装成PropertyValue 保存到 BeanDefinition中

   ```java
    @Override
       public void loadBeanDefinitions(AbstractBeanFactory beanFactory) throws Exception {
           // 读取xml配置文件注册 BeanDefinition
           XmlBeanDefinitionReader xmlBeanDefinitionReader = new XmlBeanDefinitionReader(new ResourceLoader());
           xmlBeanDefinitionReader.loadBeanDefinitions(configLocation);
   
           for (Map.Entry<String, BeanDefinition> entry : xmlBeanDefinitionReader.getRegistry().entrySet()) {
               beanFactory.registerBeanDefinition(entry.getKey(), entry.getValue());
           }
   
           // 扫描主包下面的类注册 BeanDefinition
           String packageName = xmlBeanDefinitionReader.getPackageName();
           if (packageName == null || packageName.length() == 0) {
               return;
           }
           AnnotationParser annotationParser = new AnnotationParser();
           annotationParser.annotationBeanDefinitionReader(packageName);
           for (Map.Entry<String, BeanDefinition> entry : annotationParser.getRegistry().entrySet()) {
               beanFactory.registerBeanDefinition(entry.getKey(), entry.getValue());
           }
       }
   ```

   3. 注册 BeanDefiniton 到IOC容器中
      - 遍历xmlBeanDefinitionReader、AnnotationParser 中临时收集内容保存到IOC容器中
      - beanDefinitionNames 保存name的目的是为了后面容易收集东西

   ```java
   public void registerBeanDefinition(String name, BeanDefinition beanDefinition) throws Exception {
           beanDefinitionMap.put(name, beanDefinition);
           beanDefinitionNames.add(name);
       }
   ```

4. registerConverter(beanFactory); 注册类型转换器

   ```java
   protected void registerConverter(AbstractBeanFactory beanFactory) throws Exception {
     			// 收集Converter的时候会创建出这个实例（也就是会比单例bean 提前创建）
           List beanConverters = beanFactory.getBeansForType(Converter.class);
           for (Object converter : beanConverters) {
               Type type = ((Converter) converter).getType();
               ConverterFactory.getConverterMap().put(type, (Converter) converter);
           }
       }
   ```

   - 就是遍历容器里面所以定义好的bean，找到该类型的实现

   ```java
   // 从容器中获取BeanPostProcessor 的子类或者子接口
   public List getBeansForType(Class type) throws Exception {
           // BeanPostProcessor.class
           List beans = new ArrayList<Object>();
           for (String beanDefinitionName : beanDefinitionNames) {
               // class2是不是class1的子类或者子接口
               // class1.isAssignableFrom(class2) AspectJAwareAdvisorAutoProxyCreator
               if (type.isAssignableFrom(beanDefinitionMap.get(beanDefinitionName).getBeanClass())) {
                   // 创建出这个bean
                   beans.add(getBean(beanDefinitionName));
               }
           }
           return beans;
       }
   ```

5. registerBeanPostProcessors(beanFactory); 注册BeanPostProcessor

   ```java
   protected void registerBeanPostProcessors(AbstractBeanFactory beanFactory) throws Exception {
           // 从容器中获取所有的BeanPostProcessor
           List beanPostProcessors = beanFactory.getBeansForType(BeanPostProcessor.class);
   
           // beanPostProcessor
           for (Object beanPostProcessor : beanPostProcessors) {
               beanFactory.addBeanPostProcessor((BeanPostProcessor) beanPostProcessor);
           }
       }
   ```

6. onRefresh();

   ```java
   protected void onRefresh() throws Exception {
     beanFactory.preInstantiateSingletons(); // 预加载容器中所有的bean
     checkoutAll(); // 使用三级缓存解决循环依赖，属性赋值不正确的问题
   }
   ```

   ```java
   public void preInstantiateSingletons() throws Exception {
           for (String beanName : beanDefinitionNames) {
               getBean(beanName);
           }
       }
   ```

   

7. getBean()

   - 从beanDefinitionMap 获取BeanDefinition，拿不到说明这个bean没有声明过
   - 如果当前容器中拿不到，可以从容器中获取。如果遍历的所有上下文容器还是拿不到BeanDefinition，说明并没有定义过，直接抛出异常
   -  beanDefinition.getBean==null 说明这个bean还没有初始化，我们应该创建出来
   - 两种情况需要创建bean：1. 这个bean还没有创建过。2. 这个bean不是单例类型的

   ```java
   public Object getBean(String name) throws Exception {
     BeanDefinition beanDefinition = beanDefinitionMap.get(name);
   
     ApplicationContext context = this.getContext();
     // 当前容器中找不到从父容器中获取bean
     while (beanDefinition == null && context.getParent() != null) {
       ApplicationContext parent = context.getParent();
       Object object = parent.getBean(name);
       if (object != null) {
         return object;
       } else {
         context = parent;
       }
     }
   
     if (beanDefinition == null) {
       throw new IllegalArgumentException("No bean named " + name + " is defined");
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

8. doCreateBean

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

   - createBeanInstance 通过构造器初始化bean

     ```java
     // 没有使用构造器参数，那么就使用无参构造器初始化
     if (beanDefinition.getConstructorArgument().isEmpty()) {
       return beanDefinition.getBeanClass().newInstance();
     } else {
       List<ConstructorArgument.ValueHolder> valueHolders = beanDefinition.getConstructorArgument().getArgumentValues();
       return constructor.newInstance(params);
     }
     ```

   - 将初始化的bean保存到三级缓存中。用于解决循环依赖的问题

   - 将BeanDefinition中收集的bean的属性注入到刚创建出来的bean中

     ```java
     protected void applyPropertyValues(String name, Object instance, BeanDefinition beanDefinition) throws Exception {
       // bean的一些定制接口
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
             secondCache.put(name, null); // key是这个bean对应的属性不完整
           }
         }
         // 非ref字段，对value进行处理，将string转化成对应类型
         else {
           Field field = instance.getClass().getDeclaredField(propertyValue.getName());// 获得name对应的字段
           
           if (field.getType().toString().equals("class java.lang.String"))
             convertedValue = value;
           else
        			// 不是String类型的，我们应该从converterFactory中获取转换器来解析     
             convertedValue = ConverterFactory.getConverterMap().get(field.getType()).parse((String) value);
         }
         // 反射赋值
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
     ```

     

   - 解析bean里面的注解，就是扫描bean里面的 Autowired注解

     ```java
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
     ```

9. initializeBean(bean, name);

   - 完成bean的初始化化操作，最好将初始化好的bean保存到三级缓存中

   ```java
   protected Object initializeBean(Object bean, String name) throws Exception {
     //  初始化前操作
     for (BeanPostProcessor beanPostProcessor : beanPostProcessors) { // beanPostProcessors.size() = 0
       bean = beanPostProcessor.postProcessBeforeInitialization(bean, name);
     }
     // bean 的初始化操作
     try {
       Method method = bean.getClass().getMethod("init_method", null);
       method.invoke(bean, null);
     } catch (Exception e) {
   
     }
   
     // 初始化后的操作
     for (BeanPostProcessor beanPostProcessor : beanPostProcessors) {
       bean = beanPostProcessor.postProcessAfterInitialization(bean, name);
     }
   
     // 初始化完成放入三级缓存
     if (thirdCache.containsKey(name)) {
       firstCache.put(name, bean);
     }
   
     return bean;
   }
   ```

10. AspectJAwareAdvisorAutoProxyCreator 实现了BeanPostProcessor接口，所以能在初始化做工作

    - 如果是切面类直接返回，不需要增强
    - 如果是增强的功能直接返回，不需要增强
    - `getBeansForType(AspectJExpressionPointcutAdvisor.class);`拿到容器里面的所有切面,遍历
      - 如果当前bean 符合切入点表达式，那么应该代理当前bean。
      - 将被代理类、增强方法、MethodMatcher包装成advisedSupport。
        - 封装的目的是，被代理类应该知道他自己挣钱的功能是什么。
        - 还有就是调用被代理类的方法时，需要判断当前方法符不符合切入点表达式，符合才使用增强器来调用方法。
      - 代理advisedSupport。

    ```java
    public Object postProcessAfterInitialization(Object bean, String beanName) throws Exception {
      // 实现对对象的代理操作
      if (bean instanceof AspectJExpressionPointcutAdvisor) {
        return bean;
      }
    
      if (bean instanceof MethodInterceptor) {
        return bean;
      }
    
      // 找到被代理对象的增强方法  将被代理对象和增强方法包转到 AdviseSupport
    
      // 从IOC容器中 获取所有的切面
      List<AspectJExpressionPointcutAdvisor> advisors = beanFactory
        .getBeansForType(AspectJExpressionPointcutAdvisor.class);
    
      for (AspectJExpressionPointcutAdvisor advisor : advisors) {
        // 判断当前对象是否需要增强
        // execution(* cn.haitaoss.tinyioc.*.*(..))
        if (advisor.getPointcut().getClassFilter().matches(bean.getClass())) {
    
          ProxyFactory advisedSupport = new ProxyFactory();
          advisedSupport.setMethodInterceptor((MethodInterceptor) advisor.getAdvice()); // 增强方法
          advisedSupport.setMethodMatcher(advisor.getPointcut().getMethodMatcher()); // 方法校验器
    
          TargetSource targetSource = new TargetSource(bean, bean.getClass(), bean.getClass().getInterfaces());
          advisedSupport.setTargetSource(targetSource);
    
          // 代理对象套娃
          bean = advisedSupport.getProxy();
        }
      }
      return bean;
    }
    ```

    代理对象的InvocationHandler

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

    

11. checkoutAll。给有缺陷的bean重新赋值

    ```java
    // 二级缓存中 key就是属性不完整的bean。我们需要对他的属性重新赋值。
    for (Map.Entry<String, Object> entry : secondCache.entrySet()) {
      String invokeBeanName = entry.getKey();
      BeanDefinition beanDefinition = beanDefinitionMap.get(invokeBeanName);
      try {
        resetReference(invokeBeanName, beanDefinition);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    ```

    一级缓存里面的bean是初始化完整的bean，我们应该获取里面的值，作为bean的属性

    ```java
    for (PropertyValue propertyValue : beanDefinition.getPropertyValues().getPropertyValueList()) {
      String refName = propertyValue.getName();
      // 我们只关心ref 类型的属性
      if (firstCache.containsKey(refName)) {
        Object exceptedValue = firstCache.get(refName); // 完全初始化的对象
        Object invokeBean = beanDefinition.getBean(); // 需要修改这个对象里面的属性
    
        Object realClassInvokeBean = thirdCache.get(invokeBeanName);
        Object realClassRefBean = thirdCache.get(refName);
        // 反射赋值
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
    ```

    

