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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jdom.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.websqrd.libs.db.DBConnectionPool;

/**
 * <catbot>
	<agent>Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1; InfoPath.2; .NET CLR 1.1.4322; .NET CLR 2.0.50727)</agent>
	<http>
		<http-header key=""></http-header>
	</http>
	<workerCount>1</workerCount>
	<debug>true</debug>
 * </catbot>
 *
 */
public class CatbotConfig {
	
	private final static Logger logger = LoggerFactory.getLogger(CatbotConfig.class);
	private String agent;
	private Map<String, String> httpHeader = new HashMap<String, String>();
	private boolean debug;
	private String repository;
	private String siteRootPath;
	private List<String> siteList = new ArrayList<String>();
	private Map<String, String> propertyList = new HashMap<String, String>();
	private Map<String, String> modifierMap = new HashMap<String, String>();
	private DBConnectionPool.Settings poolSettings;
	
	final private int MAX_TOTAL = 5;
	final private int MAX_IDLE = 3;
	final private int MAX_WAIT = 3;
	final private int MAX_AGE = 3600;

	private int iMAX_TOTAL = 5;
	private int iMAX_IDLE = 3;
	private int iMAX_WAIT = 3;
	private int iMAX_AGE = 3600;
	
	private int initialSize; //첫 증분색인시 수집할 문서갯수.
	
	public CatbotConfig(Element root) {
		if(!root.getName().equals("catbot")){
			logger.error("셋팅파일의 Root엘리먼트는 catbot이어야 합니다.");
			return;
		}
		
		

		List<Element> firstLevel = (List<Element>) root.getChildren();

		for (Element fnode : firstLevel) {
			String nodeName = fnode.getName();

			if ("agent".equals(nodeName)) {
				String agentString = fnode.getValue();
				if (agentString == null || "".equals(agentString)) {
					agentString = CatbotSettings.DEFAULT_AGENT;
				}
				this.agent = agentString;
			} else if ("http".equals(nodeName)) {
				List<Element> secondLevel = (List<Element>) fnode.getChildren();
				for (Element node : secondLevel) {
					nodeName = node.getName();
					if ("http-header".equals(nodeName)) {
						String key = node.getAttributeValue("key");
						String value = node.getValue();
						httpHeader.put(key, value);
					}
				}
			} else if ("debug".equals(nodeName)) {
				String debug = fnode.getValue();
				if ("true".equalsIgnoreCase(debug)) {
					this.debug = true;
				}
			} else if ("repository".equals(nodeName)) {
				String repository = fnode.getValue();
				if (repository != null && !"".equals(repository)) {
					this.repository = repository;
				}
			} else if ("siteList".equals(nodeName)) {
				siteRootPath = fnode.getAttributeValue("path");
				// path가 설정되어 있지 않으면 기본디렉토리경로인 site 를 사용한다.
				if (siteRootPath == null) {
					siteRootPath = "site";
				}
				List<Element> listEl = fnode.getChildren("site");
				for (int i = 0; i < listEl.size(); i++) {
					Element el = listEl.get(i);
					String siteId = el.getValue();
					siteList.add(siteId);
				}
			} else if ("property".equals(nodeName)) {
				propertyList.put(fnode.getAttributeValue("key"), fnode.getValue());
			} else if ("initialSize".equals(nodeName)) {
				String initialSize = fnode.getValue();
				if (initialSize != null && !"".equals(initialSize)) {
					try {
						this.initialSize = Integer.parseInt(initialSize);
					} catch (NumberFormatException e) {
					}
				}
			} else if ("modifier".equals(nodeName)) {
				modifierMap.put(fnode.getAttributeValue("key"), fnode.getValue());
			} else if ("dbpool".equals(nodeName)) {
				List<Element> secondLevel = (List<Element>) fnode.getChildren();
				for (Element node : secondLevel) {
					nodeName = node.getName();
					if ("maxTotal".equals(nodeName)) {
						try {
							iMAX_TOTAL = Integer.parseInt(node.getValue());
						} catch (Exception e) {
						}
					} else if ("maxIdle".equals(nodeName)) {
						try {
							iMAX_IDLE = Integer.parseInt(node.getValue());
						} catch (Exception e) {
						}
					} else if ("maxWait".equals(nodeName)) {
						try {
							iMAX_WAIT = Integer.parseInt(node.getValue());
						} catch (Exception e) {
						}
					} else if ("maxAge".equals(nodeName)) {
						try {
							iMAX_AGE = Integer.parseInt(node.getValue());
						} catch (Exception e) {
						}
					}
				}
			}
		}

		poolSettings = new DBConnectionPool.Settings(iMAX_TOTAL, iMAX_IDLE, iMAX_WAIT, iMAX_AGE);
	}
	
//	public int getWorkerCount() {
//		return workerCount;
//	}
	public Map<String, String> getHttpHeader(){
		return httpHeader;
	}
	
	public String getAgent() {
		return agent;
	}
	
	public boolean isDebug() {
		return debug;
	}
	
	public String getRepository() {
		return repository;
	}
	
	public String getSiteRootPath() {
		return siteRootPath;
	}

	public List<String> getSiteList() {
		return siteList;
	}

	public Map<String, String> getProperties() {
		return propertyList;
	}

	public int getInitialSize() {
		return initialSize;
	}
	
	public String getProperty(String key){
		return propertyList.get(key);
	}
	
	public int getInt(String key){
		String val = propertyList.get(key);
		if(val != null){
			return Integer.parseInt(val);
		}
		return -1;
	}

	public Map<String, String> getModifierMap() {
		return modifierMap;
	}
	
	public DBConnectionPool.Settings getPoolSeting()
	{
		return poolSettings;
	}	
	
}	
