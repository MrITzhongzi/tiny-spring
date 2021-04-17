package cn.haitaoss.tinyioc;

/**
 * @author haitao.chen
 * @email haitaoss@aliyun.com
 * @date 2021-04-17 11:24
 *
 */
public class HelloWorldService {
    private String text;

    public void setText(String text) {
        this.text = text;
    }

    public void helloWorld() {
        System.out.println(this.text);
    }
}
