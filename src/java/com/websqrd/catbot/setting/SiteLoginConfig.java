package com.websqrd.catbot.setting;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jdom.Element;

import com.websqrd.catbot.util.urlDecode;




public class SiteLoginConfig {
	
	private String url;
	private String method="get";
	private Map<String, String> urlParam;		
	public SiteLoginConfig(){ }
	
	public SiteLoginConfig(Element root, String charSet){
		method = root.getAttributeValue("method", "get");
		Element urlEl = (Element)root.getChild("url");
		this. url = urlDecode.getDecodedUrl(urlEl.getValue(), charSet);
		List<Element> paramList = (List<Element>)root.getChildren("url-params");	
		
		if(paramList != null){
			urlParam = new HashMap<String, String>();
			for(Element node : paramList) {
				String key = node.getAttributeValue("key");
				String value = node.getValue();
				urlParam.put(key, value);
			}
		}		
	}
	
	public String getUrl() {
		return url;
	}
	public String getMethod() {
		return method;
	}
	public Map<String, String> getUrlParam() {
		return urlParam;
	}	
}
