package cn.haitaoss.tinyioc;

/**
 * @author haitao.chen
 * @email 1486504210@qq.com
 * @date 2021-04-17 11:15
 *
 */
public class BeanDefinition {
    private Object bean;

    public BeanDefinition(Object bean) {
        this.bean = bean;
    }

    public Object getBean() {
        return bean;
    }
}
