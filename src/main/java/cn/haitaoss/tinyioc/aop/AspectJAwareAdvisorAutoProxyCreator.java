package cn.haitaoss.tinyioc.aop;

import cn.haitaoss.tinyioc.beans.BeanPostProcessor;
import cn.haitaoss.tinyioc.beans.factory.AbstractBeanFactory;
import cn.haitaoss.tinyioc.beans.factory.BeanFactory;
import org.aopalliance.intercept.MethodInterceptor;

import java.util.List;

/**
 * @author haitao.chen
 * @email haitaoss@aliyun.com
 * @date 2021-04-17 21:34
 *
 */
public class AspectJAwareAdvisorAutoProxyCreator implements BeanPostProcessor, BeanFactoryAware {
    private AbstractBeanFactory beanFactory;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws Exception {
        return bean;
    }

    @Override
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

                return advisedSupport.getProxy();
            }
        }
        return bean;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws Exception {
        this.beanFactory = (AbstractBeanFactory) beanFactory;
    }
}
