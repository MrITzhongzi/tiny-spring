package cn.haitaoss.tinyioc;

import java.util.ArrayList;
import java.util.List;

/**
 * @author haitao.chen
 * @email haitaoss@aliyun.com
 * @date 2021-04-17 13:12
 * 保存一个bean 里面的所有propertyValue
 * 为什么封装而不是直接在BeanDefinition 中使用List ?因为可以封装一些操作。
 */
public class PropertyValues {
    private final List<PropertyValue> propertyValueList = new ArrayList<>();

    public void addPropertyValue(PropertyValue propertyValue) {
        // TODO: 待补充这里没有对同名字的propertyName 进行去重判断
        this.propertyValueList.add(propertyValue);
    }

    public List<PropertyValue> getPropertyValueList() {
        return this.propertyValueList;
    }

}
