package cn.haitaoss.tinyioc.beans;

/**
 * @author haitao.chen
 * @email haitaoss@aliyun.com
 * @date 2021-04-17 21:11
 *
 */
public class BeanInitializeLogger implements BeanPostProcessor {
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws Exception {
        System.out.println("Initialize bean " + beanName + " start!");
        // createDate
        // bean.getClass().getDeclaredField("createDate").set(bean,new Date());
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws Exception {
        System.out.println("Initialize bean " + beanName + " end!");
        return bean;
    }
}
