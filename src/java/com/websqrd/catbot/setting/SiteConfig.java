/*
 * Copyright (C) 2011 WebSquared Inc. http://websqrd.com
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package com.websqrd.catbot.setting;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jdom.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SiteConfig {
	
	private final static Logger logger = LoggerFactory.getLogger(SiteConfig.class);
	
	private String siteName;
	private String siteDescription;
	private String dataHandler;
	private String agent;
	private Map<String, String> httpHeader = new HashMap<String, String>();
	private int workerCount;
	private boolean debug;
	private SiteLoginConfig siteLoginConfig;
	private Map<String, Process> processTemplate = new HashMap<String, Process>();
	private Map<String, String> propertyList = new HashMap<String, String>();
	private Map<String, String> usedCookieList = new HashMap<String, String>();
	private Map<String, String> headParam;	
	private String charset;	
	public static final int DEFAULT_TIME_OUT=5000;
	private int timeOut =DEFAULT_TIME_OUT;
	
	public SiteConfig(String site, Element root) {
		if(root == null){
			return;
		}
		
		if(!root.getName().equals("site")){
			logger.error("셋팅파일의 Root엘리먼트는 site이어야 합니다.");
			return;
		}
		this.siteName = root.getAttributeValue("name");
		this.siteDescription = root.getAttributeValue("description");
		
		List<Element> paramList = (List<Element>)root.getChildren("head-params");	
		
		if(paramList != null){
			headParam = new HashMap<String, String>();
			for(Element node : paramList) {
				String key = node.getAttributeValue("key");
				String value = node.getValue();
				headParam.put(key, value);
			}
		}		
		
		List<Element> firstLevel = (List<Element>)root.getChildren();
		List<Element> cookieList = (List<Element>)root.getChildren("cookie");
		
		if(cookieList != null){
			usedCookieList = new HashMap<String, String>();
			for(Element node : cookieList) {
				String key = node.getAttributeValue("key");
				String value = node.getValue();
				usedCookieList.put(key ,value);
			}
		}
		
		for(Element fnode : firstLevel) {
			String nodeName = fnode.getName();
		
			if("dataHandler".equals(nodeName)) {
				String dataHandler = fnode.getValue();
				if(dataHandler == null || "".equals(dataHandler)) { 
					this.dataHandler = "default"; 
				}else{
					this.dataHandler = dataHandler;
				}
			}else if("agent".equals(nodeName)) {
				String agentString = fnode.getValue();
				if(agentString == null || "".equals(agentString)) { 
					agentString = CatbotSettings.DEFAULT_AGENT; 
				}else{
					this.agent = agentString;
				}
			}else if("http".equals(nodeName)) {
				List<Element> secondLevel = (List<Element>)fnode.getChildren();
				for(Element node : secondLevel) {
					nodeName = node.getName();
					if("http-header".equals(nodeName)) {
						String key = node.getAttributeValue("key");
						String value = node.getValue();
						httpHeader.put(key, value);
					}
				}
			}else if ( "timeOut".equals(nodeName) )
			{
				String timeOutStr = fnode.getValue();
				if ( timeOutStr == null || "".equals(timeOutStr))
				{
					this.timeOut = DEFAULT_TIME_OUT;
				}
				else
				{
					try
					{
						this.timeOut = Integer.parseInt(timeOutStr);
					}
					catch(Exception E)
					{
						this.timeOut = DEFAULT_TIME_OUT;
					}
				}
				this.timeOut = Math.max(DEFAULT_TIME_OUT, this.timeOut);
				//TimeOut 값이 5초 보다 작으면 기본값 5초를 설정하게 한다.
				logger.trace("Set TimeOut {}", this.timeOut );
			}
			else if("workerCount".equals(nodeName)) {
				String workerCountStr = fnode.getValue();
				if(workerCountStr == null || "".equals(workerCountStr)) {
					this.workerCount = 1;
				}else{
					try{
						this.workerCount = Integer.parseInt(workerCountStr);
					}catch(NumberFormatException e){
						this.workerCount = 1;
					}
				}
			}else if("charset".equals(nodeName)) {
				this.charset = fnode.getValue();
			}else if("debug".equals(nodeName)){
				String debug = fnode.getValue();
				if ("true".equalsIgnoreCase(debug)) {
					this.debug = true;
				}
			}else if("login".equals(nodeName)) {
				siteLoginConfig = new SiteLoginConfig(fnode, this.charset);
			}else if("property".equals(nodeName)) {
				propertyList.put(fnode.getAttributeValue("key"), fnode.getValue());
			}else if("templateList".equals(nodeName)){
				List<Element> secondLevel = (List<Element>)fnode.getChildren("template");
				for(Element node : secondLevel) {
					Element processEl = node.getChild("process");
					String templateId = node.getAttributeValue("id");
					processTemplate.put(templateId, new Process(processEl));
				}
			}
		}
		if ( this.agent == null || this.agent.equals("") )
		{
			CatbotConfig config = CatbotSettings.getGlobalConfig();
			this.agent = config.getAgent();
		}
	}
	
	public String getSiteName() {
		return siteName;
	}
	
	public String getDescription() {
		return siteDescription;
	}
	
	public String getAgent() {
		return agent;
	}
	
	public Map<String, String> getHttpHeader(){
		return httpHeader;
	}
	
	public int getWorkerCount() {
		return workerCount;
	}
	
//	public boolean isDebug() {
//		return debug;
//	}
	
	public SiteLoginConfig getLogin(){
		return siteLoginConfig;
	}
	
	public Map<String, Process> getProcessTemplate(){
		return processTemplate;
	}
	public String getCharset() {
		return charset;
	}

	public String getDataHandler() {
		return dataHandler;
	}

	public Map<String, String> getProperties() {
		return propertyList;
	}
	
	public Map<String, String> getCookieList() {
		return usedCookieList;
	}
	
	public int getTimeout()
	{
		return timeOut;
	}
	
	public Map<String, String> getHeadParam() {
		return headParam;
	}	
	
	public String getPropertyString(String key){
		return propertyList.get(key);
	}
	
	public int getPropertyInt(String key){
		String val = propertyList.get(key);
		if(val != null){
			return Integer.parseInt(val);
		}
		return -1;
	}
	
}