package cn.haitaoss.tinyioc.beans;

/**
 * @author haitao.chen
 * email haitaoss@aliyun.com
 * date 2021-04-17 20:52
 *
 */
public interface BeanPostProcessor {
    Object postProcessBeforeInitialization(Object bean, String beanName) throws Exception;

    Object postProcessAfterInitialization(Object bean, String beanName) throws Exception;
}
