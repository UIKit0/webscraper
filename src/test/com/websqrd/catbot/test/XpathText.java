package com.websqrd.catbot.test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import junit.framework.TestCase;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.xpath.XPath;

public class XpathText extends TestCase {
	public void testEmpty() throws  IOException, JDOMException{
		InputStream in = new ByteArrayInputStream("<html></html>".getBytes());
		SAXBuilder sb = new SAXBuilder();
		Document doc = null;
		try {
			doc = sb.build(in);
		} catch (JDOMException e) {
			e.printStackTrace();
		}
		if(doc != null){
			Element root = doc.getRootElement();
			XPath xpath = XPath.newInstance("//body");
			List list = xpath.selectNodes(root);
			System.out.println(list);
		}
	}
}
