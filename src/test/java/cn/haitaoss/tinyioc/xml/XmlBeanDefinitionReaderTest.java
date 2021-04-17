package cn.haitaoss.tinyioc.xml;

import cn.haitaoss.tinyioc.BeanDefinition;
import cn.haitaoss.tinyioc.io.ResourceLoader;
import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.util.Map;

/**
 * @author haitao.chen
 * @email haitaoss@aliyun.com
 * @date 2021-04-17 14:30
 *
 */
public class XmlBeanDefinitionReaderTest {
    /**
     * 练习Document相关的API
     * @author haitao.chen
     * @email
     * @date 2021/4/17 2:55 下午
     */
    @Test
    public void testDocumentAPI() throws Exception {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

        // 通过this.getClass().getClassLoader() 文件的加载路径就是 classpath目录下开始
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream("tinyioc.xml");
        Document document = documentBuilder.parse(ins);
        Element rootElement = document.getDocumentElement(); // 这是 beans标签

        // 读取bean 标签的内容
        NodeList beanNodeList = rootElement.getChildNodes();
        for (int i = 0; i < beanNodeList.getLength(); i++) {
//            获取标签的实际内容
            Node node = beanNodeList.item(i);
            // System.out.println(node.getClass()); // 有text 和 Element两种类型
            if (node instanceof Element) {
                String name = ((Element) node).getAttribute("name");
                String className = ((Element) node).getAttribute("class");
                System.out.println(name + "---" + className);

                // 如果还有子标签
                NodeList propertyNodeList = ((Element) node).getElementsByTagName("property");
                for (int j = 0; j < propertyNodeList.getLength(); j++) {
                    Node childNode = propertyNodeList.item(j);
                    if (childNode instanceof Element) {
                        String key = ((Element) childNode).getAttribute("name");
                        String value = ((Element) childNode).getAttribute("value");
                        System.out.println(key + "---" + value);
                    }

                }
            }
        }
    }

    @Test
    public void test() throws Exception {
        XmlBeanDefinitionReader xmlBeanDefinitionReader = new XmlBeanDefinitionReader(new ResourceLoader());
        xmlBeanDefinitionReader.loadBeanDefinitions("tinyioc.xml");

        Map<String, BeanDefinition> registry = xmlBeanDefinitionReader.getRegistry();
        Assert.assertTrue(registry.size() > 0);
    }
}
