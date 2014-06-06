<%--
# Copyright (C) 2011 WebSquared Inc. http://websqrd.com
# 
# This program is free software; you can redistribute it and/or
# modify it under the terms of the GNU General Public License
# as published by the Free Software Foundation; either version 2
# of the License, or (at your option) any later version.
# 
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
# 
# You should have received a copy of the GNU General Public License
# along with this program; if not, write to the Free Software
# Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
--%>

<%@ page contentType="text/html; charset=UTF-8"%>

<%@page import="java.util.Date"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.io.File"%>
<%@page import="java.io.FileOutputStream"%>
<%@page import="java.util.Properties"%>
<%@page import="java.net.URLEncoder"%>
<%@page import="java.net.URLDecoder"%>
<%@page import="java.io.IOException"%>
<%@page import="org.apache.commons.io.FileUtils"%>
<%@page import="com.websqrd.catbot.web.WebUtils"%>
<%@page import="com.websqrd.catbot.setting.CatbotSettings"%>
<%@page import="com.websqrd.catbot.setting.CatbotServerConfig"%>
<%@page import="com.websqrd.libs.xml.*"%>
<%@page import="com.websqrd.libs.web.HttpUtils"%>
<%@page import="org.htmlcleaner.*"%>
<%@page import="com.websqrd.catbot.db.*"%>
<%@page import="com.websqrd.catbot.setting.*"%>
<%@page import="java.util.*"%>
<%@include file="../common.jsp"%>
<%@page import="org.json.*"%>

<%
	int cmd = Integer.parseInt(request.getParameter("cmd"));
	String site = request.getParameter("site");
	String category = request.getParameter("category");
	SiteConfig siteConfig = CatbotSettings.getSiteConfig(site);
	DataHandlerConfig dataHandlerConfig = CatbotSettings.getDataHandlerConfig(siteConfig.getDataHandler(), true);
	ScrapingDAO scrapingDAO = RepositoryHandler.getInstance().getScrapingDAO(siteConfig.getDataHandler());
	List<ShowField> fieldNameList = scrapingDAO.getFieldNameList();
	
	switch (cmd) {
	//delete by id
	
	case 0: {
		String idStr = URLDecoder.decode(request.getParameter("id"), "UTF-8");
		JSONObject idObj = new JSONObject(idStr);		
		siteConfig = CatbotSettings.getSiteConfig(site);		
		
		Map<String, String> data = new HashMap<String, String>();
		
		data.put("site", site);
		data.put("category", category);
		
		for (int i = 0; i < fieldNameList.size(); i++) {
			String key = fieldNameList.get(i).getValue();
			String val = request.getParameter(key) == null ? "" : request.getParameter(key);
			data.put(key, val);						
		}
		
		if(idObj!=null) {
			Iterator<String> iter = idObj.keys();
			while(iter.hasNext()) {
				String key = iter.next();
				String value = idObj.getString(key);
				data.put(key, value);
			}
		}		
		

		try {
			if (scrapingDAO.delete(data)) {
				response.sendRedirect("dataView.jsp?site=" + site + "&category=" + category + "&message="
				                + URLEncoder.encode(URLEncoder.encode("데이터 삭제에 성공했습니다.", "utf-8"), "utf-8"));
			} else {
				response.sendRedirect("dataView.jsp?site=" + site + "&category=" + category + "&message="
				                + URLEncoder.encode(URLEncoder.encode("데이터 삭제에 실패했습니다.", "utf-8"), "utf-8"));
			}
		} finally {
			scrapingDAO.close();
		}

		break;
	}
	//update
	case 1: {
		String idStr = URLDecoder.decode(request.getParameter("id"), "UTF-8");
		JSONObject idObj = new JSONObject(idStr);		
		
		Map<String, String> data = new HashMap<String, String>();
		for (int i = 0; i < fieldNameList.size(); i++) {
			String key = fieldNameList.get(i).getValue();
			String val = request.getParameter(key) == null ? "" : request.getParameter(key);
			data.put(key, val);						
		}
		data.put("site", site);
		data.put("category", category);
		if (idObj != null) {
			Iterator<String> iter = idObj.keys();
			while(iter.hasNext()) {
				String key = iter.next();
				String value = idObj.getString(key);
				data.put(key, value);
			}
		}
		//data.put(idFieldName, request.getParameter(idFieldName) == null ? "" : request.getParameter(idFieldName));
		try {
			if (scrapingDAO.update(data)) {
				response.sendRedirect("dataView.jsp?site=" + site + "&category=" + category + "&message="
				                + URLEncoder.encode(URLEncoder.encode("데이터 저장에 성공했습니다.", "utf-8"), "utf-8"));
			} else {
				response.sendRedirect("dataView.jsp?site=" + site + "&category=" + category + "&message="
				                + URLEncoder.encode(URLEncoder.encode("데이터 저장에 실패했습니다.", "utf-8"), "utf-8"));
			}
		} finally {
			scrapingDAO.close();
		}
		break;
	}
	//truncate table
	case 2: {
				
		Map<String, String> data = new HashMap<String, String>();
		data.put("site", site);
		data.put("category", category);
		try {
			if (scrapingDAO.truncate(data)) {
				response.sendRedirect("dataView.jsp?site=" + site + "&category=" + category + "&message="
				                + URLEncoder.encode(URLEncoder.encode("데이터 초기화에 성공했습니다.", "utf-8"), "utf-8"));
			} else {
				response.sendRedirect("dataView.jsp?site=" + site + "&category=" + category + "&message="
				                + URLEncoder.encode(URLEncoder.encode("데이터 초기화에 실패했습니다.", "utf-8"), "utf-8"));
			}
		} finally {
			scrapingDAO.close();
		}
		//수집기록도 초기화.
		CatbotSettings.saveIncInfo(site, category, "");

		//
		//TODO 수집기록 DB도 초기화 필요..
		//
		break;
	}

	//update 수집 아이디.
	case 3: {
		String idStr = request.getParameter("infoId");
		if (idStr != null && idStr.length() > 0) {
			CatbotSettings.saveIncInfo(site, category, idStr);
		}
		response.sendRedirect("dataView.jsp?site=" + site + "&category=" + category);

		break;
	}

	default:
		break;
	}
%>
