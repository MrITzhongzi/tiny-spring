package cn.haitaoss.tinyioc.aop;

import org.aopalliance.intercept.MethodInterceptor;

/**
 * 代理相关的元数据
 * @author haitao.chen
 * email haitaoss@aliyun.com
 * date 2021-04-17 17:02
 *
 */
public class AdvisedSupport {
    private TargetSource targetSource;
    private MethodInterceptor methodInterceptor;
    private MethodMatcher methodMatcher; // 切面编程需要的东西

    public MethodMatcher getMethodMatcher() {
        return methodMatcher;
    }

    public void setMethodMatcher(MethodMatcher methodMatcher) {
        this.methodMatcher = methodMatcher;
    }

    public TargetSource getTargetSource() {
        return targetSource;
    }

    public void setTargetSource(TargetSource targetSource) {
        this.targetSource = targetSource;
    }

    public MethodInterceptor getMethodInterceptor() {
        return methodInterceptor;
    }

    public void setMethodInterceptor(MethodInterceptor methodInterceptor) {
        this.methodInterceptor = methodInterceptor;
    }
}
