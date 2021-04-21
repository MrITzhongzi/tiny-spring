package cn.haitaoss.tinyioc.loop3;

import net.sf.cglib.proxy.Factory;

/**
 * @author haitao.chen
 * @email haitaoss@aliyun.com
 * @date 2021-04-20 10:35
 *
 */
public class Parent {
    private Son son;

    public Son getSon() {
        return son;
    }

    public void setSon(Son son) {
        this.son = son;
    }

    public void init() {
        System.out.println("Parent..init");
    }

    public void say() {
        System.out.println("son 是不是代理对象 " + (son instanceof Factory));
    }
}
