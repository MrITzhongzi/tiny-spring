package cn.haitaoss.tinyioc.beans.factory;

/**
 * @author haitao.chen
 * email haitaoss@aliyun.com
 * date 2021-04-17 11:47
 *
 */
public interface BeanFactory {
    Object getBean(String name) throws Exception;
}
