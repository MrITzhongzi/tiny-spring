package cn.haitaoss.tinyioc.loop2;


import net.sf.cglib.proxy.Factory;

import java.lang.reflect.Proxy;

/**
 * @author haitao.chen
 * email haitaoss@aliyun.com
 * date 2021-04-18 16:26
 *
 */
public class SonImpl implements Son {
    private Parent parent;

    public void setParent(ParentImpl parent) {
        this.parent = parent;
    }

    @Override
    public void say() {
        System.out.println("parent 是不是代理对象 " + (parent instanceof Proxy || parent instanceof Factory));
        parent.say();
    }
}
