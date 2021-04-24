package cn.haitaoss.tinyioc.aop;

import org.aopalliance.aop.Advice;

/**
 * @author haitao.chen
 * email haitaoss@aliyun.com
 * date 2021-04-17 18:01
 *
 */
public class AspectJExpressionPointcutAdvisor implements PointcutAdvisor {
    private final AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();

    private Advice advice;
    private String expression;

    public void setExpression(String expression) {
        this.pointcut.setExpression(expression);
    }

    @Override
    public Advice getAdvice() {
        return advice;
    }

    public void setAdvice(Advice advice) {
        this.advice = advice;
    }

    @Override
    public Pointcut getPointcut() {
        return pointcut;
    }
}
