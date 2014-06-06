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

<%@page import="com.websqrd.catbot.job.WebScrapingJob"%>
<%@page import="com.websqrd.catbot.control.JobController"%>
<%@page contentType="text/html; charset=UTF-8"%>

<%@page import="java.util.Date"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.io.File"%>
<%@page import="java.io.FileOutputStream"%>
<%@page import="java.util.Properties"%>
<%@page import="java.net.URLEncoder"%>
<%@page import="java.io.*"%>
<%@page import="java.net.*"%>
<%@page import="java.util.*"%>
<%@page import="org.apache.commons.io.FileUtils"%>

<%@page import="com.websqrd.catbot.web.WebUtils"%>
<%@page import="com.websqrd.catbot.setting.*"%>
<%@page import="com.websqrd.libs.xml.*"%>
<%@page import="com.websqrd.libs.web.HttpUtils"%>
<%@page import="com.websqrd.catbot.control.*"%>
<%@page import="com.websqrd.catbot.job.*"%>
<%@page import="org.htmlcleaner.*"%>
<%@page import="com.websqrd.catbot.db.*"%>

<%
	String siteId = request.getParameter("site");
	String categoryId = request.getParameter("category");
	SiteConfig siteConfig = CatbotSettings.getSiteConfig(siteId);
	CategoryConfig categoryConfig = CatbotSettings.getCategoryConfig(siteId, categoryId);
	ScrapingDAO scrapingDAO = RepositoryHandler.getInstance().getScrapingDAO(siteConfig.getDataHandler());

	//data 초기화	
	if (scrapingDAO != null) {
		HashMap<String, String> data = new HashMap<String, String>();
		data.put("site", siteId);
		data.put("category", categoryId);

		scrapingDAO.truncate(data);
		scrapingDAO.afterInit(data);
		scrapingDAO.close();
	}

	//FILE 정리
	String SiteHomePath = CatbotSettings.getSiteHome(siteId);

	Map<String, CategoryConfig> categoryMap = CatbotSettings.getSiteCategoryConfigList(siteId);
	Iterator itr = categoryMap.entrySet().iterator();

	while (itr.hasNext()) {
		Map.Entry entry = (Map.Entry) itr.next();
		CategoryConfig cateConfig = (CategoryConfig) entry.getValue();
		String cateName = cateConfig.getCategoryName();

		String cateIncFileName = SiteHomePath + cateName + "_inc.info";
		File file = new File(cateIncFileName);
		if (file.exists() == true)
			file.delete();
	}
	//----------------	    	

	//FILEREPO 정리
	//----------------
	String FILEREPOPath = CatbotSettings.getHOME() + "FILEREPO" + System.getProperty("file.separator") + siteId ;
%>
<%=FILEREPOPath%>
<%
	File repoFP = new File(FILEREPOPath);
	if (repoFP.exists()) {
		FileUtils.deleteDirectory(repoFP);
%>
<%=" Exists"%>
<%
	} else {
%>
<%="Not Exists"%>
<%
	}
	//----------------
%>
