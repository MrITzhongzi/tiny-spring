package cn.haitaoss.tinyioc.aop;

import java.lang.reflect.Method;

/**
 * @author haitao.chen
 * email haitaoss@aliyun.com
 * date 2021-04-17 17:59
 *
 */
public interface MethodMatcher {
    boolean matches(Method method, Class targetClass);
}
