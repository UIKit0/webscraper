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

<%@page import="java.util.Iterator"%>
<%@page import="java.util.Map"%>
<%@page import="com.websqrd.catbot.setting.SiteConfig"%>
<%@page import="java.util.List"%>
<%@page import="com.websqrd.catbot.exception.CatbotException"%>
<%@page import="com.websqrd.catbot.CatBot"%>
<%@page import="com.websqrd.catbot.setting.CatbotConfig"%>
<%@page import="com.websqrd.catbot.setting.CategoryConfig"%>
<%@page import="com.websqrd.catbot.job.WebScrapingJob"%>
<%@page import="com.websqrd.catbot.control.JobController"%>
<%@page import="java.net.URLEncoder"%>
<%@page import="org.json.JSONObject"%>
<%@page import="org.json.JSONArray"%>
<%@page import="org.json.JSONTokener"%>
<%@page import="org.json.JSONException"%>
<%@page contentType="text/html; charset=UTF-8"%>

<%@include file="../common.jsp" %>

<%
// 	String site = request.getParameter("site");
// 	String category = request.getParameter("category");
// 	String scrapAction = request.getParameter("scrapAction");

	String param = request.getParameter("param");
	String checkType = request.getParameter("checkType");
	
	try { 
		/**
		 * [{"site":"carenuri","cate":"notice","action":"GETNEW"},{...},{...}]
		 */

		 //전체실행 일경우.
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
					String category = entry.getKey();
					String scrapAction = CategoryConfig.SCRAPGETNEW;
					
					WebScrapingJob job = new WebScrapingJob();
					job.setArgs(new String[]{siteId, category, scrapAction});
					JobController.getInstance().offer(job);
					CategoryConfig categoryConfig = entry.getValue();
				}
			}
		} else {
		//체크된 항목만 실행하는 경우.
			JSONTokener jtokener = new JSONTokener(param);
			JSONArray jarray = new JSONArray(jtokener);
			
			for(int inx=0;inx<jarray.length(); inx++) {
				
				JSONObject jobject = jarray.getJSONObject(inx);
	
				String site = jobject.getString("site");
				String category = jobject.getString("cate");
				String scrapAction = jobject.optString("action",CategoryConfig.SCRAPGETNEW);
				
				WebScrapingJob job = new WebScrapingJob();
				job.setArgs(new String[]{site, category, scrapAction});
				JobController.getInstance().offer(job);
			}
		}
	} catch (JSONException e) {
	}
	//response.sendRedirect("main.jsp?message="+URLEncoder.encode(URLEncoder.encode("컬렉션 "+site+"의 수집작업을 등록하였습니다.", "utf-8"),"utf-8"));
%>
