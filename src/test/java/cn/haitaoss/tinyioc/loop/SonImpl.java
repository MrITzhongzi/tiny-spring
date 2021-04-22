package cn.haitaoss.tinyioc.loop;


import net.sf.cglib.proxy.Factory;

import java.lang.reflect.Proxy;

/**
 * @author haitao.chen
 * @email haitaoss@aliyun.com
 * @date 2021-04-18 16:26
 *
 */
public class SonImpl implements Son {
    private GrandSon grandSon;

    @Override
    public void say() {
        System.out.println("grandSon 是不是代理对象 " + (grandSon instanceof Proxy || grandSon instanceof Factory));
    }
}
