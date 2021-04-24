package cn.haitaoss.tinyioc.loop3;

import net.sf.cglib.proxy.Factory;

/**
 * @author haitao.chen
 * email haitaoss@aliyun.com
 * date 2021-04-20 10:36
 *
 */
public class Son {
    private Parent parent;

    public void setParent(Parent parent) {
        this.parent = parent;
    }

    public Parent getParent() {
        return parent;
    }

    public void init() {
        System.out.println("Son...init");
    }

    public void say() {
        System.out.println(parent);
        System.out.println("parent 是不是代理对象 " + (parent instanceof Factory));
    }
}
