package com.websqrd.catbot.setting;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jdom.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/** 수집할 페이지
 *  루프속성을 가지고 있으므로 여러 페이지를 표현할수 있다.
 * @author swsong
 *
 */
public class Page {
	private static Logger logger = LoggerFactory.getLogger(Page.class);
	
	//loop 속성
	private long from;
	private long to;
	private long step = 1;// default is 1
	
	//페이지속성.
	private String encoding;
	private String method;
	private String url;
	private Map<String, String> urlParam = new HashMap<String, String>();

	public String toString(){
		return "url="+url+", from="+from+", to="+to+", step="+step+", encoding="+encoding+", method="+method+", urlParam="+urlParam.toString();
	}
	public Page(Element pageEl){
//		logger.debug("create page "+pageEl);
		String from = pageEl.getAttributeValue("from");
		if(from!=null && !"".equals(from)) { this.from = Long.parseLong(from); }
		
		String to = pageEl.getAttributeValue("to");
		if(to!=null && !"".equals(to)) { this.to = Long.parseLong(to); }
		
		String step = pageEl.getAttributeValue("step");
		if(step!=null && !"".equals(step)) { this.step = Long.parseLong(step); }
		
		this.encoding = pageEl.getAttributeValue("encoding");
		
		this.method = pageEl.getAttributeValue("method");
		if (this.method == null || "".equals(this.method))
			this.method = "get";
		
		List<Element> thirdLevel = (List<Element>)pageEl.getChildren();
		if(thirdLevel != null) {
			for(Element elem : thirdLevel) {
				 if("url".equals(elem.getName())){
					 this.url = elem.getValue();
				 }else if("url-param".equals(elem.getName())){
					String key = elem.getAttributeValue("key");
					String value = elem.getValue();
					urlParam.put(key, value);
				 }
			}
		}
			
	}
	
	public long getFrom() {
		return from;
	}
	public long getTo() {
		return to;
	}
	public long getStep() {
		return step;
	}
	public String getEncoding() {
		return encoding;
	}
	public String getMethod() {
		return method;
	}
	public String getUrl() {
		return url;
	}
	public Map<String, String> getUrlParam() {
		return urlParam;
	}
}
