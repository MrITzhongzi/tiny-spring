package cn.haitaoss.tinyioc.context;

import cn.haitaoss.tinyioc.beans.BeanPostProcessor;
import cn.haitaoss.tinyioc.beans.factory.AbstractBeanFactory;

import java.util.List;

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
    }

    protected abstract void loadBeanDefinitions(AbstractBeanFactory beanFactory) throws Exception;

    @Override
    public Object getBean(String name) throws Exception {
        return beanFactory.getBean(name);
    }
}
