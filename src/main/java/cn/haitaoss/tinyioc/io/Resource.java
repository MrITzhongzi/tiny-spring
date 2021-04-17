package cn.haitaoss.tinyioc.io;

import java.io.InputStream;

/**
 * @author haitao.chen
 * @email haitaoss@aliyun.com
 * @date 2021-04-17 14:15
 * Resource是spring内部定位资源的接口。
 */
public interface Resource {
    InputStream getInputStream() throws Exception;
}
