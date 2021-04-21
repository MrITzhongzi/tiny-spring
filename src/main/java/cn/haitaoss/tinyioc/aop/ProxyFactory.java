package cn.haitaoss.tinyioc.aop;

/**
 * @author haitao.chen
 * @email haitaoss@aliyun.com
 * @date 2021-04-18 17:06
 *
 */
public class ProxyFactory extends AdvisedSupport implements AopProxy {
    @Override
    public Object getProxy() {
        return createAopProxy().getProxy();
    }

    protected final AopProxy createAopProxy() {
        // return new Cglib2AopProxy(this);
        // 如果不是接口的实现类 就使用cglib实现代理
        if (getTargetSource().getInterfaces() == null || getTargetSource().getInterfaces().length == 0) {
            return new Cglib2AopProxy(this);
        }
        return new JdkDynamicAopProxy(this);
    }
}
