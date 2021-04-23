package cn.haitaoss.tinyioc.beans;


import cn.haitaoss.tinyioc.beans.io.ResourceLoader;

import java.util.HashMap;
import java.util.Map;

/**
 * @author haitao.chen
 * @email haitaoss@aliyun.com
 * @date 2021-04-17 14:05
 * 从配置中读取BeanDefinition
 */
public abstract class AbstractBeanDefinitionReader implements BeanDefinitionReader {
    private final Map<String, BeanDefinition> registry;
    private final ResourceLoader resourceLoader;
    private String packageName;

    public AbstractBeanDefinitionReader(ResourceLoader resourceLoader) {
        this.registry = new HashMap<>();
        this.resourceLoader = resourceLoader;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public Map<String, BeanDefinition> getRegistry() {
        return registry;
    }

    public ResourceLoader getResourceLoader() {
        return resourceLoader;
    }
}
