package cn.haitaoss.tinyioc.aop;

/**
 * @author haitao.chen
 * email haitaoss@aliyun.com
 * date 2021-04-17 17:05
 * 被代理的对象
 */
public class TargetSource {
    private final Object target;
    private final Class<?> targetClass;
    private final Class<?>[] interfaces;

    public TargetSource(Object target, Class targetClass, Class<?>... interfaces) {
        this.targetClass = targetClass;
        this.target = target;
        this.interfaces = interfaces;
    }

    public Class<?>[] getInterfaces() {
        return interfaces;
    }

    public Class<?> getTargetClass() {
        return targetClass;
    }

    public Object getTarget() {
        return target;
    }
}
