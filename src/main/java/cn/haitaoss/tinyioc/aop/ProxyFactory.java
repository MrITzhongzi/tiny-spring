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
        return new Cglib2AopProxy(this);
    }
}
