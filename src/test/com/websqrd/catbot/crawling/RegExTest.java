package com.websqrd.catbot.crawling;

import junit.framework.TestCase;

public class RegExTest extends TestCase {
	public void test1(){
		String link = "http://www.getfastcat.org/document/846";
		String regex = "http://[\\s.]+/[\\s]/\\n+"; 
		if(link.matches(regex)){
			System.out.println("OK");
		}
	}
}
