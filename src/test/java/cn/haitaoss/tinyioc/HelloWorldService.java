package cn.haitaoss.tinyioc;

/**
 * @author haitao.chen
 * @email haitaoss@aliyun.com
 * @date 2021-04-17 11:24
 *
 */
public class HelloWorldService {
    private String text;
    private OutputService outputService;

    public void setText(String text) {
        this.text = text;
    }

    public void helloWorld() {
        outputService.output(text);
    }

    public void setOutputService(OutputService outputService) {
        this.outputService = outputService;
    }
}
