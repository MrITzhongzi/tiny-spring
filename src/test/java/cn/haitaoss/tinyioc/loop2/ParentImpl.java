package cn.haitaoss.tinyioc.loop2;

import net.sf.cglib.proxy.Factory;

import java.lang.reflect.Proxy;

/**
 * @author haitao.chen
 * email haitaoss@aliyun.com
 * date 2021-04-18 16:28
 *
 */
public class ParentImpl implements Parent {
    private Son son;

    public void setSon(Son son) {
        this.son = son;
    }

    @Override
    public void say() {
        System.out.println("son 是不是代理对象 " + (son instanceof Proxy || son instanceof Factory));
    }
}
