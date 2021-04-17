package cn.haitaoss.tinyioc.beans.io;

import java.net.URL;

/**
 * @author haitao.chen
 * @email haitaoss@aliyun.com
 * @date 2021-04-17 14:07
 *
 */
public class ResourceLoader {
    public Resource getResource(String location) throws Exception {
        URL resource = this.getClass().getClassLoader().getResource(location);
        return new URLResource(resource);
    }
}
