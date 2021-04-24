package cn.haitaoss.tinyioc.context.controller;

import cn.haitaoss.tinyioc.beans.annotation.Autowired;
import cn.haitaoss.tinyioc.beans.annotation.Controller;
import cn.haitaoss.tinyioc.beans.annotation.Value;
import cn.haitaoss.tinyioc.context.service.Son;

/**
 * @author haitao.chen
 * email haitaoss@aliyun.com
 * date 2021-04-23 14:19
 *
 */
@Controller
public class Father {
    @Value("father")
    private String name;
    @Value("100")
    private int age;
    @Autowired
    private Son son;

    public Son getSon() {
        return son;
    }

    public void setSon(Son son) {
        this.son = son;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "Father{" +
                "name='" + name + '\'' +
                ", age=" + age +
                '}';
    }
}
