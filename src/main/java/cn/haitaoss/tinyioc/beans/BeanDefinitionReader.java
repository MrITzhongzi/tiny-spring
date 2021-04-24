package cn.haitaoss.tinyioc.beans;

/**
 * @author haitao.chen
 * email haitaoss@aliyun.com
 * date 2021-04-17 14:10
 * 从配置中读取BeanDefinition
 */
public interface BeanDefinitionReader {
    void loadBeanDefinitions(String location) throws Exception;
}
