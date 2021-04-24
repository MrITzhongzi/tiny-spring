package cn.haitaoss.tinyioc;

/**
 * @author haitao.chen
 * email haitaoss@aliyun.com
 * date 2021-04-17 17:23
 *
 */
public class HelloWorldServiceImpl implements HelloWorldService {
    private String text;
    private OutputService outputService;

    @Override
    public void helloWorld() {
        outputService.output(text);
    }

    @Override
    public void sayHello() {
        System.out.println("只代理这个方法");
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setOutputService(OutputService outputService) {
        this.outputService = outputService;
    }
}
