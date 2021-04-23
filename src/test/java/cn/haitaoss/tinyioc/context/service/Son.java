package cn.haitaoss.tinyioc.context.service;

import cn.haitaoss.tinyioc.beans.annotation.Service;
import cn.haitaoss.tinyioc.beans.annotation.Value;

/**
 * @author haitao.chen
 * @email haitaoss@aliyun.com
 * @date 2021-04-23 14:19
 *
 */
@Service
public class Son {
    @Value("son")
    private String name;
    @Value("19")
    private int age;

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
}
