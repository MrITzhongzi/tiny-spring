package cn.haitaoss.tinyioc;

/**
 * @author haitao.chen
 * @email haitaoss@aliyun.com
 * @date 2021-04-17 21:54
 *
 */
public class OutputServiceImpl implements OutputService {

    @Override
    public void output(String text) {
        System.out.println(text);
    }

    @Override
    public void sayHello() {
        System.out.println("OutputServiceImpl....sayHello");
    }

}