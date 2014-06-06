package com.websqrd.libs.xml;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.stream.StreamResult;

import net.sf.saxon.om.Item;
import net.sf.saxon.om.NodeInfo;
import net.sf.saxon.query.QueryResult;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.type.Type;

import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Text;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class XmlUtils {

	private static Logger logger = LoggerFactory.getLogger(XmlUtils.class);

	//한페이지에 여러개의 아이템이 있을때 사용됨.
	public static List<String> getNodes(String xPathExpression, InputStream in) throws JDOMException, IOException{
		
		ArrayList<String> resultList = new ArrayList<String>();
		SAXBuilder sb = new SAXBuilder();
		Document doc = sb.build(in);
		XPath xpath = XPath.newInstance(xPathExpression);
		Element root = doc.getRootElement();
		List list = xpath.selectNodes(root);
		for (int i = 0; i < list.size(); i++) {
			Element element = (Element) list.get(i);
			XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
			resultList.add(outputter.outputString(element));
		}
		
		return resultList;
	}
		
	public static String getSingleNodeText(String xPathExpression, InputStream in) throws JDOMException, IOException{
		
		SAXBuilder sb = new SAXBuilder();
		Document doc = sb.build(in);
		Element root = doc.getRootElement();
		XPath xpath = XPath.newInstance(xPathExpression);
		Object obj = xpath.selectSingleNode(root);
		
		return getNodeText(obj);
	}
	
	public static String[] getMultiNodeText(String xPathExpression, InputStream in) throws JDOMException, IOException{
		
		SAXBuilder sb = new SAXBuilder();
		Document doc = sb.build(in);
		Element root = doc.getRootElement();
		XPath xpath = XPath.newInstance(xPathExpression);
		List list = xpath.selectNodes(root);
		
		String[] result = new String[list.size()];
		for (int i = 0; i < result.length; i++) {
			Object obj = list.get(i);
			result[i] = getNodeText(obj);
		}
		
		return result;
	}
	
	private static String getNodeText(Object obj){
		XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
		String resultText = null;
		if (obj instanceof Attribute) {
			resultText = ((Attribute) obj).getValue();
		}else if(obj instanceof Element){
			resultText = outputter.outputString(((Element) obj));
		}else if(obj instanceof Text){
			resultText = ((Text) obj).getValue();
		}
		
		if (resultText == null) {
			resultText = "";
		}
		
		return resultText;
	}

	public static String getSingleNodeText(String xPathExpression, Document block) throws JDOMException, IOException{
		
		String resultText = ((Text) XPath.selectSingleNode(block, xPathExpression)).getValue();
		
		if (resultText == null) {
			resultText = "";
		}

		return resultText;
	}
	
    /**
     * Serializes item after XPath or XQuery processor execution using Saxon.
     */
    public static String serializeItem(Item item) throws XPathException {
    	if (item instanceof NodeInfo) {
    		int type = ((NodeInfo)item).getNodeKind();
            if (type == Type.DOCUMENT || type == Type.ELEMENT) {
	            Properties props = new Properties();
	            props.setProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
	            props.setProperty(OutputKeys.INDENT, "yes");

                StringWriter stringWriter = new java.io.StringWriter();
                QueryResult.serialize((NodeInfo)item, new StreamResult(stringWriter), props);
                stringWriter.flush();
              
                return stringWriter.toString().replaceAll(" xmlns=\"http\\://www.w3.org/1999/xhtml\"", "");
            }
    	}
    	
    	return item.getStringValue();
    }

}
