package cn.haitaoss.tinyioc.aop;

/**
 * @author haitao.chen
 * email haitaoss@aliyun.com
 * date 2021-04-17 17:59
 *
 */
public interface ClassFilter {
    boolean matches(Class targetClass);
}
