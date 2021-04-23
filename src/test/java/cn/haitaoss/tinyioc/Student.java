package cn.haitaoss.tinyioc;

import cn.haitaoss.tinyioc.beans.annotation.Autowired;
import cn.haitaoss.tinyioc.beans.annotation.Component;
import cn.haitaoss.tinyioc.beans.annotation.Value;

/**
 * @author haitao.chen
 * @email haitaoss@aliyun.com
 * @date 2021-04-22 22:13
 *
 */
@Component
public class Student {
    @Value("huangtianyu")
    private String stuName;
    @Autowired
    private School school;

    public String getStuName() {
        return stuName;
    }

    public void setStuName(String stuName) {
        this.stuName = stuName;
    }

    public School getSchool() {
        return school;
    }

    public void setSchool(School school) {
        this.school = school;
    }

    public void learning() {
        System.out.println("i am learning");
    }
}
