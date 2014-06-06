package com.websqrd.catbot;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import junit.framework.TestCase;

public class URLDecodeTest extends TestCase {
	public void test1() throws UnsupportedEncodingException{
		String url = "aa?aa=1&amp;bb=2";
		String a = URLDecoder.decode(url, "utf-8");
		System.out.println(a);
	}
	
	public void test2() {
		String url = "aa?aa=1&amp;bb=2&amp;cc=3";
		System.out.println(url.replace("&amp;", "&"));
	}
}
