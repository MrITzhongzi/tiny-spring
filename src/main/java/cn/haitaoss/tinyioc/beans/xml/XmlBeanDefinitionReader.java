package cn.haitaoss.tinyioc.beans.xml;

import cn.haitaoss.tinyioc.beans.AbstractBeanDefinitionReader;
import cn.haitaoss.tinyioc.beans.BeanDefinition;
import cn.haitaoss.tinyioc.beans.BeanReference;
import cn.haitaoss.tinyioc.beans.PropertyValue;
import cn.haitaoss.tinyioc.beans.ConstructorArgument;
import cn.haitaoss.tinyioc.beans.io.ResourceLoader;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;

/**
 * 读取xml文件 得到beanDefinition 信息
 * @author haitao.chen
 * @email haitaoss@aliyun.com
 * @date 2021-04-17 14:12
 *
 */
public class XmlBeanDefinitionReader extends AbstractBeanDefinitionReader {

    public XmlBeanDefinitionReader(ResourceLoader resourceLoader) {
        super(resourceLoader);
    }

    @Override
    public void loadBeanDefinitions(String location) throws Exception {
        // 读取配置文件
        InputStream inputStream = getResourceLoader().getResource(location).getInputStream();
        doLoadBeanDefinitions(inputStream);
    }

    private void doLoadBeanDefinitions(InputStream inputStream) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = factory.newDocumentBuilder();
        Document doc = docBuilder.parse(inputStream);
        // 解析bean
        registerBeanDefinitions(doc);
        inputStream.close();
    }

    private void registerBeanDefinitions(Document doc) {
        Element root = doc.getDocumentElement(); // beans
        parseBeanDefinitions(root);
    }

    private void parseBeanDefinitions(Element root) {
        NodeList nl = root.getChildNodes(); // bean
        for (int i = 0; i < nl.getLength(); i++) {
            Node node = nl.item(i);
            if (node instanceof Element) {
                Element ele = (Element) node;
                processBeanDefinition(ele);
            }
        }

    }

    private void processBeanDefinition(Element ele) {
        String name = ele.getAttribute("id");
        String className = ele.getAttribute("class");
        BeanDefinition beanDefinition = new BeanDefinition();
        // 解析构造器参数
        processConstructorArgument(ele, beanDefinition);
        processProperty(ele, beanDefinition);
        beanDefinition.setBeanClassName(className);
        getRegistry().put(name, beanDefinition);

    }


    private void processConstructorArgument(Element element, BeanDefinition beanDefinition) {
        NodeList constructorNodes = element.getElementsByTagName("constructor-arg");
        for (int i = 0; i < constructorNodes.getLength(); i++) {
            Node node = constructorNodes.item(i);
            if (node instanceof Element) {
                Element constructorElement = (Element) node;
                String name = constructorElement.getAttribute("name");
                String type = constructorElement.getAttribute("type");
                String value = constructorElement.getAttribute("value");
                if (value != null && value.length() > 0) {
                    beanDefinition.getConstructorArgument().addArgumentValue(new ConstructorArgument.ValueHolder(value, type, name));
                } else {
                    String ref = constructorElement.getAttribute("ref");
                    if (ref == null || ref.length() == 0) {
                        throw new IllegalArgumentException("Configuration problem: <constructor-arg> element for property '"
                                + name + "' must specify a ref or value");
                    }
                    BeanReference beanReference = new BeanReference(ref);
                    beanDefinition.getConstructorArgument().addArgumentValue(new ConstructorArgument.ValueHolder(beanReference, type, name));
                }
            }
        }
    }

    private void processProperty(Element ele, BeanDefinition beanDefinition) {
        NodeList propertyNode = ele.getElementsByTagName("property");
        for (int i = 0; i < propertyNode.getLength(); i++) {
            Node node = propertyNode.item(i);
            if (node instanceof Element) {
                Element propertyEle = (Element) node;
                String name = propertyEle.getAttribute("name");
                String value = propertyEle.getAttribute("value");
                if (value != null && value.length() > 0) {
                    beanDefinition.getPropertyValues().addPropertyValue(new PropertyValue(name, value));
                } else {
                    String ref = propertyEle.getAttribute("ref");
                    if (ref == null || ref.length() == 0) {
                        throw new IllegalArgumentException("Configuration problem: <property> element for property '"
                                + name + "' must specify a ref or value");
                    }
                    BeanReference beanReference = new BeanReference(ref); //
                    beanDefinition.getPropertyValues().addPropertyValue(new PropertyValue(name, beanReference));
                }
            }
        }
    }

}
