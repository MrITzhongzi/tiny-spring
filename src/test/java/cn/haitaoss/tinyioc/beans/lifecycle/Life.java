package cn.haitaoss.tinyioc.beans.lifecycle;

/**
 * @author haitao.chen
 * email haitaoss@aliyun.com
 * date 2021-04-22 21:40
 *
 */
public class Life implements InitializingBean, DisposableBean {
    private int age;

    public Life() {
        System.out.println("构造方法");
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void init_method() {
        System.out.println("This is init-method！");
    }

    public void destroy_method() {
        System.out.println("This is destroy-method！");
    }

    @Override
    public void destroy() {
        System.out.println("This is destory() from DisposableBean Interface!");
    }

    @Override
    public void afterPropertiesSet() {
        System.out.println("This is afterPropertiesSet() from InitializingBean Interface!");
    }

    @Override
    public String toString() {
        return "Life{" +
                "age=" + age +
                '}';
    }
}
