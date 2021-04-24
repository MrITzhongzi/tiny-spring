package cn.haitaoss.tinyioc.beans.lifecycle;

/**
 * @author haitao.chen
 * email haitaoss@aliyun.com
 * date 2021-04-22 21:37
 *
 */
public interface InitializingBean {
    void afterPropertiesSet();
}
