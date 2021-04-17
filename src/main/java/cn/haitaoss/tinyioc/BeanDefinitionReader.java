package cn.haitaoss.tinyioc;

/**
 * @author haitao.chen
 * @email haitaoss@aliyun.com
 * @date 2021-04-17 14:10
 * 从配置中读取BeanDefinitionReader
 */
public interface BeanDefinitionReader {
    void loadBeanDefinitions(String location) throws Exception;
}
