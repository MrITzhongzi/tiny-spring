package cn.haitaoss.tinyioc.aop;

import cn.haitaoss.tinyioc.HelloWorldService;
import cn.haitaoss.tinyioc.HelloWorldServiceImpl;
import org.junit.Test;

/**
 * @author haitao.chen
 * @email haitaoss@aliyun.com
 * @date 2021-04-17 20:35
 *
 */
public class AspectJExpressionPointcutTest {
    @Test
    public void testClassFilter() {
        String expression = "execution(* cn.haitaoss.tinyioc.*.*(..))";
        AspectJExpressionPointcut aspectJExpressionPointcut = new AspectJExpressionPointcut();
        aspectJExpressionPointcut.setExpression(expression);
        boolean matches = aspectJExpressionPointcut.getClassFilter().matches(HelloWorldService.class);
        System.out.println(matches);
    }

    @Test
    public void testMethodInterceptor() throws NoSuchMethodException {
        String expression = "execution(* cn.haitaoss.tinyioc.*.*(..))";
        AspectJExpressionPointcut aspectJExpressionPointcut = new AspectJExpressionPointcut();
        aspectJExpressionPointcut.setExpression(expression);
        boolean matches = aspectJExpressionPointcut
                .getMethodMatcher()
                .matches(HelloWorldServiceImpl.class.getDeclaredMethod("helloWorld"), HelloWorldServiceImpl.class);
        System.out.println(matches);
    }
}
