package cn.haitaoss.tinyioc.aop;

/**
 * @author haitao.chen
 * email haitaoss@aliyun.com
 * date 2021-04-18 17:04
 *
 */
public abstract class AbstractAopProxy implements AopProxy {
    protected AdvisedSupport advised;

    public AbstractAopProxy(AdvisedSupport advised) {
        this.advised = advised;
    }
}
