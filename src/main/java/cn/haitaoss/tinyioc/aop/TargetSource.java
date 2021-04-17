package cn.haitaoss.tinyioc.aop;

/**
 * @author haitao.chen
 * @email haitaoss@aliyun.com
 * @date 2021-04-17 17:05
 * 被代理的对象
 */
public class TargetSource {
    private final Class targetClass;
    private final Object target;

    public TargetSource(Object target, Class<?> targetClass) {
        this.targetClass = targetClass;
        this.target = target;
    }

    public Class getTargetClass() {
        return targetClass;
    }

    public Object getTarget() {
        return target;
    }
}
