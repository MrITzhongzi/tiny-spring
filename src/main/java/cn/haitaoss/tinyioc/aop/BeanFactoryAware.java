package cn.haitaoss.tinyioc.aop;

import cn.haitaoss.tinyioc.beans.factory.BeanFactory;

/**
 * @author haitao.chen
 * email haitaoss@aliyun.com
 * date 2021-04-17 21:39
 *
 */
public interface BeanFactoryAware {
    void setBeanFactory(BeanFactory beanFactory) throws Exception;
}
