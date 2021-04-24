package cn.haitaoss.tinyioc.aop;

import org.aopalliance.aop.Advice;

/**
 * @author haitao.chen
 * email haitaoss@aliyun.com
 * date 2021-04-17 17:55
 *
 */
public interface Advisor {
    Advice getAdvice();
}
