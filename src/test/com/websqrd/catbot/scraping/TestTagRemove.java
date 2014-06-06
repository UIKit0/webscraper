package com.websqrd.catbot.scraping;

import java.io.IOException;

import junit.framework.TestCase;

import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.PrettyXmlSerializer;
import org.htmlcleaner.TagNode;

public class TestTagRemove extends TestCase {
	public void test1() throws IOException{
		CleanerProperties props = new CleanerProperties();
		props.setTranslateSpecialEntities(true);
		props.setOmitComments(true);
		props.setNamespacesAware(false);
		HtmlCleaner htmlCleaner = new HtmlCleaner(props);	
		PrettyXmlSerializer xmlSerializer = new PrettyXmlSerializer(props);
		String pageContent = "alksjdf<b>asdf</b> <p>asdf &lt;내손도서관> <br>";
		TagNode tagNode = htmlCleaner.clean(pageContent);
		pageContent = xmlSerializer.getXmlAsString(tagNode);
		System.out.println(pageContent);
	}
}
