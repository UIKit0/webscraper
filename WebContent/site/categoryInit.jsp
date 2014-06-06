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

<%@page import="org.json.JSONException"%>
<%@page import="org.json.JSONObject"%>
<%@page import="org.json.JSONTokener"%>
<%@page import="org.json.JSONArray"%>
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
<%!

void clearCategory(String siteId, String categoryId, JspWriter out) throws IOException {
	
	String sep = System.getProperty("file.separator");
	
	SiteConfig siteConfig =  CatbotSettings.getSiteConfig(siteId);
	CategoryConfig categoryConfig = CatbotSettings.getCategoryConfig(siteId, categoryId);	    	
	ScrapingDAO scrapingDAO = RepositoryHandler.getInstance().getScrapingDAO(siteConfig.getDataHandler());	
	ScrapingResult scrapingResult = DBHandler.getInstance().getScrapingResult();
	
	//data 초기화	
	if ( scrapingDAO != null ) {
		HashMap<String, String> data = new HashMap<String, String>();
		data.put("site", siteId);
		data.put("category", categoryId);
		
		if (  CatbotSettings.getSiteCategoryConfigList(siteId).size() == 1 )
			scrapingDAO.truncate(data);
		else		
			scrapingDAO.cleanCategory(data) ;
		
		scrapingDAO.afterInit(data);
		scrapingDAO.close();
		scrapingResult.delete(siteId, categoryId, CategoryConfig.SCRAPGETNEW);
		scrapingResult.delete(siteId, categoryId, CategoryConfig.SCRAPGETALL);
		scrapingDAO.close();
	}
	
	//FILE 정리
	String SiteHomePath = CatbotSettings.getSiteHome(siteId);
	String cateIncFileName = SiteHomePath + categoryId + "_inc.info"; 
	File file = new File(cateIncFileName);
	
	if ( file.exists() == true ) {
		file.delete();
	}
	
	//FILEREPO 정리
	//----------------
	String FILEREPOPath = CatbotSettings.path("FILEREPO" + sep + siteId+ sep + categoryId);
	out.println(FILEREPOPath);
	File repoFP = new File(FILEREPOPath);
	if ( repoFP.exists()) {
		try {
			FileUtils.forceDelete(repoFP);
			//FileUtils.deleteDirectory(repoFP);
		} catch (IOException e) { 
			out.print(e.getMessage());
		}
		out.println("Exists");
	} else {
		out.println("Not Exists");
	}
	//----------------	    	
}
%>
<%
	String param = request.getParameter("param");
	String checkType = request.getParameter("checkType");
	try { 
		if("ALL".equals(checkType)) {
			boolean settingReload = WebUtils.getBoolean(request.getParameter("settingReload"), false); 
			CatbotConfig catbotConfig = CatbotSettings.getGlobalConfig(settingReload);
			List<String> siteList = catbotConfig.getSiteList();
			for(int i = 0; i < siteList.size(); i++){
				String siteId = siteList.get(i);
				SiteConfig siteConfig = CatbotSettings.getSiteConfig(siteId, settingReload);
				Map<String, CategoryConfig> categoryConfigList = CatbotSettings.getSiteCategoryConfigList(siteId, settingReload);
				if(categoryConfigList == null){
					break;
				}
				Iterator<Map.Entry<String, CategoryConfig>> iter = categoryConfigList.entrySet().iterator();
				
				for(int cateInx=0;iter.hasNext();cateInx++){
					Map.Entry<String, CategoryConfig> entry = iter.next();
					String categoryId = entry.getKey();
					clearCategory(siteId,categoryId,out);
				}
			}
			
		} else {
			
			JSONTokener jtokener = new JSONTokener(param);
			JSONArray jarray = new JSONArray(jtokener);
			
			for(int inx=0;inx<jarray.length(); inx++) {
				
				JSONObject jobject = jarray.getJSONObject(inx);
	
				String siteId = jobject.getString("site");
				String categoryId = jobject.getString("cate");
				
				clearCategory(siteId,categoryId,out);
			}
		}
	} catch (JSONException e) {
		out.print(e.getMessage());
	}
%>
