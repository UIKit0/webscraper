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
<%@page import="com.websqrd.catbot.setting.CatbotSettings"%>
<%@page import="java.net.URLEncoder"%>
<%@page import="com.websqrd.catbot.web.WebUtils"%>
<%@include file="webroot.jsp"%>
<%
	request.setCharacterEncoding("UTF-8"); 
%>

<%
	if (CatbotSettings.isAuthUsed() && session.getAttribute("authorized") == null) {
		response.sendRedirect(CATBOT_MANAGE_ROOT+"index.jsp?message="+URLEncoder.encode(URLEncoder.encode("로그인이 필요합니다.", "utf-8"),"utf-8"));
		return;
	}
%>
