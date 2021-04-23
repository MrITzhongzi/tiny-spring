package cn.haitaoss.tinyioc;

import cn.haitaoss.tinyioc.beans.annotation.Autowired;
import cn.haitaoss.tinyioc.beans.annotation.Service;
import cn.haitaoss.tinyioc.beans.annotation.Value;

/**
 * @author haitao.chen
 * @email haitaoss@aliyun.com
 * @date 2021-04-22 22:12
 *
 */
@Service
public class School {
    @Autowired
    private Student student;
    @Value("njust")
    private String name;
    @Value("11711001")
    private int price;

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

}