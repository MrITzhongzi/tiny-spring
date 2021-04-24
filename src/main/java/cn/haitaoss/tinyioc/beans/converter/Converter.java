package cn.haitaoss.tinyioc.beans.converter;

import java.lang.reflect.Type;

/**
 * @author haitao.chen
 * email haitaoss@aliyun.com
 * date 2021-04-22 19:31
 *
 */
public interface Converter<T> {
    Type getType();

    String print(T fieldValue);

    //  会调用 parse方法的返回值就是属性的值
    T parse(String clientValue) throws Exception;
}
