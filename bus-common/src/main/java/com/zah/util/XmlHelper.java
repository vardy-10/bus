package com.zah.util;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.net.URI;

/**
 * XML操作类
 * <p>
 * 使用须知：<br>
 * 1.目前该类只支持XML解析及处理等相关操作，不包括XML编译，以后可以根据实际情况追加操作。
 * 2.使用的逻辑是实例化该类后先调用parse方法解析XML，然后可以根据需求调用其它方法进行相应操作。
 */
public class XmlHelper {

	public Document document;// XML DOM

	/**
	 * XML解析
	 * <p>
	 * 使用须知：<br>
	 * 1.该方法的参数类型是输入源，输入源的获取方法有很多种，这里还提供了一些其它参数类型的同名方法来帮助转换。<br>
	 * 2.该方法解析成功后会将解析结果存放在类属性中，可调用其它获取方法获取相应值。
	 * 
	 * @param XML
	 *            XML输入源
	 * @return 是否解析成功
	 */
	public boolean parse(InputSource XML) {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			document = builder.parse(XML);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * XML解析（详见同名方法）
	 * 
	 * @param XML
	 *            XML资源
	 */
	public boolean parse(URI XML) {
		if (XML == null) {
			return false;// 资源不能为空
		}
		InputSource in = new InputSource(XML.toASCIIString());
		return parse(in);
	}

	/**
	 * XML解析（详见同名方法）
	 * 
	 * @param XML
	 *            XML文件
	 */
	public boolean parse(File XML) {
		if (XML == null) {
			return false;// 文件不能为空
		}
		InputSource in = new InputSource(XML.toURI().toASCIIString());
		return parse(in);
	}

	/**
	 * XML解析（详见同名方法）
	 * 
	 * @param XML
	 *            XML输入流
	 */
	public boolean parse(InputStream XML) {
		if (XML == null) {
			return false;// 文件流不能为空
		}
		InputSource in = new InputSource(XML);
		return parse(in);
	}

	/**
	 * XML解析（详见同名方法）
	 * 
	 * @param XML
	 *            XML字符串
	 */
	public boolean parse(String XML) {
		if (XML == null) {
			return false;// 字符串不能为空
		}
		StringReader reader = null;
		try {
			reader = new StringReader(XML);
			InputSource in = new InputSource(reader);
			return parse(in);
		} catch (Exception e) {
			// e.printStackTrace();
			return false;
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
	}

	/**
	 * 获取整个XML中指定标签名的节点列表
	 * <p>
	 * 使用须知：<br>
	 * 1.使用时必须存在XML DOM对象，没有时可先通过parse方法将XML解析出来。<br>
	 * 2.无论要获取的标签名是否存在都将返回节点列表，可以通过getSize方法获取列表项的具体数量或通过getElement方法获取单个节点元素
	 * 。<br>
	 * 3.存在同标签名嵌套时，将被分别获取。
	 * 
	 * @param TagName
	 *            标签名
	 * @return 节点列表，无法获取时返回null
	 */
	public NodeList getNodeList(String TagName) {
		if (TagName == null || document == null)
			return null;
		return document.getElementsByTagName(TagName);
	}

	/**
	 * 获取指定节点元素中指定标签名的节点列表
	 * <p>
	 * 使用须知：<br>
	 * 1.使用时必须存在所在节点元素，没有时可先通过getElement方法获取。<br>
	 * 2.无论要获取的标签名是否存在都将返回节点列表，可以通过getSize方法获取列表项的具体数量或通过getElement方法获取单个节点元素
	 * 。<br>
	 * 3.存在同标签名嵌套时，将被分别获取。
	 * 
	 * @param node
	 *            所在节点元素
	 * @param TagName
	 *            标签名
	 * @return 节点列表，无法获取时返回null
	 */
	public NodeList getNodeList(Element element, String TagName) {
		if (TagName == null || element == null)
			return null;
		return element.getElementsByTagName(TagName);
	}

	/**
	 * 获取指定节点元素中的子节点列表
	 * <p>
	 * 使用须知：<br>
	 * 1.使用时必须存在所在节点元素，没有时可先通过getElement方法获取。<br>
	 * 2.无论要获取的标签名是否存在都将返回节点列表，可以通过getSize方法获取列表项的具体数量或通过getElement方法获取单个节点元素。
	 * 
	 * @param element
	 *            所在节点元素
	 * @return 节点列表，无法获取时返回null
	 */
	public NodeList getNodeList(Element element) {
		if (element == null)
			return null;
		return element.getChildNodes();
	}

	/**
	 * 获取整个XML中的根节点元素
	 * <p>
	 * 使用须知：<br>
	 * 1.使用时必须存在XML DOM对象，没有时可先通过parse方法将XML解析出来。<br>
	 * 2.获取到元素后可以通过getAttr方法获取元素属性或通过getText获取元素中的文本内容或通过getXML获取元素及其中内容的XML字符串。
	 * 
	 * @return 节点元素，无法获取时返回null
	 */
	public Element getElement() {
		if (document == null)
			return null;
		return document.getDocumentElement();
	}

	/**
	 * 获取节点列表中的节点元素
	 * <p>
	 * 使用须知：<br>
	 * 1.使用时必须存在节点列表，没有时可先通过getNodeList方法获取。<br>
	 * 2.获取到元素后可以通过getAttr方法获取元素属性或通过getText获取元素中的文本内容或通过getXML获取元素及其中内容的XML字符串。
	 * <br>
	 * 3.节点列表中的节点存在嵌套关系的，索引顺序由外到内。
	 * 
	 * @param nodeList
	 *            节点列表
	 * @param index
	 *            节点列表中节点元素的索引，0表示第一个，正数类推，负数即反方向倒数，-1表示倒数第一
	 * @return 节点元素，无法获取或获取不到时返回null
	 */
	public Element getElement(NodeList nodeList, int index) {
		if (nodeList == null)
			return null;
		int length = nodeList.getLength();
		if (index < 0)
			index = length + index;
		if (index < 0 || index >= length)
			return null;
		return (Element) nodeList.item(index);
	}

	/**
	 * 获取节点元素的父节点元素
	 * <p>
	 * 使用须知：<br>
	 * 1.使用时必须存在所在节点元素，没有时可先通过getElement方法获取。<br>
	 * 2.获取到元素后可以通过getAttr方法获取元素属性或通过getText获取元素中的文本内容或通过getXML获取元素及其中内容的XML字符串。
	 * 
	 * @param element
	 *            节点元素
	 * @return 节点元素，无法获取或获取不到时返回null
	 */
	public Element getElement(Element element) {
		if (element == null)
			return null;
		try {
			Node node = element.getParentNode();
			return (Element) node;
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 获取节点元素指定属性名的属性值
	 * <p>
	 * 使用须知：<br>
	 * 1.使用时必须存在所在节点元素，没有时可先通过getElement方法获取。
	 * 
	 * @param element
	 *            所在节点元素
	 * @param attrName
	 *            属性名
	 * @return 属性值
	 */
	public String getAttr(Element element, String attrName) {
		if (element == null)
			return null;
		return element.getAttribute(attrName);
	}

	/**
	 * 获取整个XML的文本内容
	 * <p>
	 * 使用须知：<br>
	 * 1.使用时必须存在XML DOM对象，没有时可先通过parse方法将XML解析出来。<br>
	 * 2.不会输出标签，而只会输出标签中的文本内容。
	 * 
	 * @return 文本内容，无法获取时返回null
	 */
	public String getText() {
		if (document == null)
			return null;
		return document.getFirstChild().getTextContent();
	}

	/**
	 * 获取节点元素中的文本内容
	 * <p>
	 * 使用须知：<br>
	 * 1.使用时必须存在所在节点元素，没有时可先通过getElement方法获取。<br>
	 * 2.无论指定标签中是否有嵌套标签，都不会输出标签，而只会输出标签中的文本内容。
	 * 
	 * @param element
	 *            所在节点元素
	 * @return 文本内容，无法获取时返回null
	 */
	public String getText(Element element) {
		if (element == null)
			return null;
		return element.getTextContent();
	}

	/**
	 * 一步获取XML标签中的文本内容
	 * <p>
	 * 使用须知：<br>
	 * 1.使用时必须存在XML DOM对象，没有时可先通过parse方法将XML解析出来。<br>
	 * 2.无论指定标签中是否有嵌套标签，都不会输出标签，而只会输出标签中的文本内容。<br>
	 * 3.标签嵌套时索引顺序由外到内。
	 * 
	 * @param name
	 *            XML标签名
	 * @param index
	 *            多个相同标签名中的索引，0表示第一个，正数类推，负数即反方向倒数，-1表示倒数第一
	 * @return 指定标签中的文本内容，无法获取或不存在指定标签时返回null
	 */
	public String getText(String name, int index) {
		if (name == null || document == null)
			return null;
		NodeList books = document.getElementsByTagName(name);
		if (books == null)
			return null;
		int length = books.getLength();
		if (index < 0)
			index = length + index;
		if (index < 0 || index >= length)
			return null;
		return books.item(index).getTextContent();
	}

	/**
	 * 一步获取XML标签中的文本内容（默认取同名标签中的第一个，详见同名带参方法）
	 */
	public String getText(String name) {
		return getText(name, 0);
	}

	/**
	 * 获取整个XML格式文本
	 * 
	 * @return XML格式文本，无法获取时返回null
	 */
	public String getXML() {
		if (document == null)
			return null;
		StringWriter stringWriter = null;
		try {
			Source source = new DOMSource(document);
			stringWriter = new StringWriter();
			Result result = new StreamResult(stringWriter);
			TransformerFactory factory = TransformerFactory.newInstance();
			Transformer transformer = factory.newTransformer();
			// transformer.setOutputProperty(OutputKeys.INDENT, "yes");//标签自动换行
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");// 省略XML声明
			transformer.transform(source, result);
			return stringWriter.getBuffer().toString();
		} catch (Exception e) {
			// e.printStackTrace();
			return null;
		} finally {
			if (stringWriter != null) {
				try {
					stringWriter.close();
				} catch (IOException e) {
					// e.printStackTrace();
					return null;
				}
			}
		}
	}

	/**
	 * 获取节点元素中的XML格式文本
	 * <p>
	 * 使用须知：<br>
	 * 1.使用时必须存在所在节点元素，没有时可先通过getElement方法获取。<br>
	 * 2.XML格式文本中包括查找标签的自身标签。
	 * 
	 * @param element
	 *            所在节点元素
	 * @return XML格式文本，无法获取时返回null
	 */
	public String getXML(Element element) {
		if (element == null)
			return null;
		StringWriter stringWriter = null;
		try {
			Source source = new DOMSource(element);
			stringWriter = new StringWriter();
			Result result = new StreamResult(stringWriter);
			TransformerFactory factory = TransformerFactory.newInstance();
			Transformer transformer = factory.newTransformer();
			// transformer.setOutputProperty(OutputKeys.INDENT, "yes");//标签自动换行
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");// 省略XML声明
			transformer.transform(source, result);
			return stringWriter.getBuffer().toString();
		} catch (Exception e) {
			// e.printStackTrace();
			return null;
		} finally {
			if (stringWriter != null) {
				try {
					stringWriter.close();
				} catch (IOException e) {
					// e.printStackTrace();
					return null;
				}
			}
		}
	}

	/**
	 * 一步获取XML标签的XML格式文本
	 * <p>
	 * 使用须知：<br>
	 * 1.使用时必须存在XML DOM对象，没有时可先通过parse方法将XML解析出来。<br>
	 * 2.XML格式文本中包括查找标签的自身标签。<br>
	 * 3.标签嵌套时索引顺序由外到内。
	 * 
	 * @param name
	 *            XML标签名
	 * @param index
	 *            多个相同标签名中的索引，0表示第一个，正数类推，负数即反方向倒数，-1表示倒数第一
	 * @return 指定标签的XML格式文本，无法获取或不存在指定标签时返回null
	 */
	public String getXML(String name, int index) {
		if (name == null || document == null)
			return null;
		NodeList books = document.getElementsByTagName(name);
		if (books == null)
			return null;
		int length = books.getLength();
		if (index < 0)
			index = length + index;
		if (index < 0 || index >= length)
			return null;
		Node book = books.item(index);
		StringWriter stringWriter = null;
		try {
			Source source = new DOMSource(book);
			stringWriter = new StringWriter();
			Result result = new StreamResult(stringWriter);
			TransformerFactory factory = TransformerFactory.newInstance();
			Transformer transformer = factory.newTransformer();
			// transformer.setOutputProperty(OutputKeys.INDENT, "yes");//标签自动换行
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");// 省略XML声明
			transformer.transform(source, result);
			return stringWriter.getBuffer().toString();
		} catch (Exception e) {
			// e.printStackTrace();
			return null;
		} finally {
			if (stringWriter != null) {
				try {
					stringWriter.close();
				} catch (IOException e) {
					// e.printStackTrace();
					return null;
				}
			}
		}
	}

	/**
	 * 一步获取XML标签的XML格式文本（默认取同名标签中的第一个，详见同名带参方法）
	 */
	public String getXML(String name) {
		return getXML(name, 0);
	}

	/**
	 * 获取节点列表中的节点数
	 * <p>
	 * 使用须知：<br>
	 * 1.使用时必须存在节点列表，没有时可先通过getNodeList方法获取。
	 * 
	 * @param nodeList
	 *            节点列表
	 * @return 节点列表的节点数，无法获取时返回0
	 */
	public int getSize(NodeList nodeList) {
		if (nodeList == null)
			return 0;
		return nodeList.getLength();
	}

	/**
	 * 获取XML中指定标签的数量
	 * <p>
	 * 使用须知：<br>
	 * 1.使用时必须存在XML DOM对象，没有时可先通过parse方法将XML解析出来。<br>
	 * 
	 * @param name
	 *            XML标签名
	 * @return 指定标签的数量，无法获取时返回0
	 */
	public int getSize(String tagName) {
		if (tagName == null || document == null)
			return 0;
		NodeList nodeList = document.getElementsByTagName(tagName);
		if (nodeList == null)
			return 0;
		return nodeList.getLength();
	}

	/**
	 * 保存为XML文件
	 * 
	 * @param filePath
	 *            文件路径
	 * @return 是否保存成功
	 */
	public boolean save(String filePath) {
		if (document == null)
			return false;
		try {
			Source source = new DOMSource(document);
			Result result = new StreamResult(new File(filePath));
			TransformerFactory factory = TransformerFactory.newInstance();
			Transformer transformer = factory.newTransformer();
			// transformer.setOutputProperty(OutputKeys.INDENT, "yes");//标签自动换行
			transformer.transform(source, result);
			return true;
		} catch (Exception e) {
			// e.printStackTrace();
			return false;
		}
	}

}
