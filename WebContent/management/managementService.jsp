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
<%@page import="java.util.Properties"%>
<%@page import="java.net.URLEncoder"%>
<%@page import="com.websqrd.catbot.web.WebUtils"%>
<%@page import="com.websqrd.catbot.setting.CatbotSettings"%>
<%@page import="com.websqrd.catbot.setting.CatbotServerConfig"%>
<%@page import="com.websqrd.catbot.service.*"%>
<%@page import="com.websqrd.catbot.control.*"%>
<%@page import="com.websqrd.catbot.db.*"%>
<%@page import="com.websqrd.catbot.server.*"%>

<%@include file="../common.jsp" %>

<%
	int cmd = Integer.parseInt(request.getParameter("cmd"));
String collection = request.getParameter("collection");

switch(cmd){
	case 1:
		// 사용자설정을 저장한다.
	{
		String[] keyList = {"server.port","dynamic.classpath"};
		CatbotServerConfig irConfig = CatbotSettings.getCatbotServerConfig();
		Properties props = irConfig.getProperties();
		for(int i =0;i<keyList.length ;i++){
	String tmp = WebUtils.getString(request.getParameter(keyList[i]), "");
	props.setProperty(keyList[i], tmp);
		}
		
		CatbotSettings.storeServerConfig(irConfig);
		
		response.sendRedirect("config.jsp?message="+URLEncoder.encode("저장하였습니다.", "utf-8"));
		
		break;
	}
	
}
%>
