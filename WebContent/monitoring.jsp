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

<%@page import="com.websqrd.fastcat.db.object.SearchEvent"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@page import="com.websqrd.fastcat.web.WebUtils"%>
<%@page import="java.net.URLConnection"%>
<%@page import="java.net.URL"%>
<%@page import="java.io.BufferedReader"%>
<%@page import="java.io.InputStreamReader"%>
<%@page import="com.websqrd.fastcat.ir.config.IRSettings"%>
<%@page import="com.websqrd.fastcat.db.object.JobHistory"%>
<%@page import="com.websqrd.fastcat.db.DBHandler"%>
<%@page import="com.websqrd.fastcat.log.EventDBLogger"%>
<%@page import="java.util.List"%>
<%@page import="java.net.URLDecoder"%>
<%@include file="common.jsp" %>
<%@page import="com.websqrd.fastcat.ir.config.IRConfig"%>
<%
	String cmd = request.getParameter("cmd");
	String type = request.getParameter("type");
	String collection = WebUtils.getString(request.getParameter("collection"),"");
	if(type == null){
		type = "minute";
	}
	String message = "";
	if ("login".equals(cmd)) {
		String username = request.getParameter("username");
		String passwd = request.getParameter("passwd");
		String[] accessLog = IRSettings.isCorrectPasswd(username, passwd);
		if(accessLog != null){
			//로긴 성공
			session.setAttribute("authorized", username);
			session.setAttribute("lastAccessLog", accessLog);
			session.setMaxInactiveInterval(60 * 30); //30 minutes
			IRSettings.storeAccessLog(username, ""); //ip주소는 공란으로 남겨두고 사용하지 않도록함. 
			//request.getRemoteAddr()로는 제대로된 사용자 ip를 알아낼수 없음.
			//jetty에서는 getHeader("REMOTE_ADDR"); 또는 req.getHeaer("WL-Proxy-Client-IP")+","+req.getHeaer("Proxy-Client-IP")+","+req.getHeaer("X-Forwarded-For")) 등을 제공하지 않는다.
			message = "";
		}else{
			message = "아이디와 비밀번호를 확인해주세요.";
		}
		
	}else if ("logout".equals(cmd)) {
		session.invalidate();
		response.sendRedirect(FASTCAT_MANAGE_ROOT+"index.jsp");
		return;
	}
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>웹수집기 관리도구</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<link href="<%=FASTCAT_MANAGE_ROOT%>css/reset.css" rel="stylesheet" type="text/css" />
<link href="<%=FASTCAT_MANAGE_ROOT%>css/style.css" rel="stylesheet" type="text/css" />
<script src="js/amcharts/amcharts.js" type="text/javascript"></script>
<script src="js/amcharts/raphael.js" type="text/javascript"></script>
<!--[if lte IE 6]>
<link href="<%=FASTCAT_MANAGE_ROOT%>css/style_ie.css" rel="stylesheet" type="text/css" />
<![endif]-->
<script type="text/javascript" src="<%=FASTCAT_MANAGE_ROOT%>js/common.js"></script>
<script type="text/javascript" src="<%=FASTCAT_MANAGE_ROOT%>js/jquery-1.4.4.min.js"></script>
<script type="text/javascript" src="<%=FASTCAT_MANAGE_ROOT%>js/detailMoni.js"></script>
<script>
	var typeTag = "<%=type%>";
	$(document).ready(function() {
		var message = "<%=message %>";
		if(message != "")
			alert(message);
	});

	function logout(){
		location.href="?cmd=logout";
	}
</script>
</head>
<body>
<div id="container">
<!-- header -->
<%@include file="header.jsp" %>
		
<div id="mainContent">
		<div style=width:100%;height:30px;">
			<a class="btn_s"  href="<%=FASTCAT_MANAGE_ROOT%>monitoring.jsp?type=minute">시간별</a>
			<a class="btn_s"  href="<%=FASTCAT_MANAGE_ROOT%>monitoring.jsp?type=hour">일짜별</a>
		</div>
		<div style=width:100%;height:20px;">
			
			<%
				if("minute".equals(type)){
					%>
			<select name="timeMin" id="timeMin" onchange="javascript:refreshChart('minute')">		
					<%
					for(int i=0; i<24; i++){
						%>
						<option value="<%=i%>" ><%=i%>시</option>
						<%
					}
				}else if("hour".equals(type)){
					%>
			<select name="timeHour" id="timeHour" onchange="javascript:refreshChart('hour')">		
			<%
				}
			%>
			</select>
		</div>
		<div style=width:100%;height:200px;">
			<div id="chartJCPUDiv" style=" height:200px; background-color:#FFFFFF"></div>
		</div>
		<div style=width:100%;height:200px;">
			<div id="chartMemDiv" style=" height:200px; background-color:#FFFFFF"></div>
		</div>
		<div style=width:100%;height:200px;">
			<div id="chartLoadDiv" style=" height:200px; background-color:#FFFFFF"></div>
		</div>
		<div style=width:100%;height:20px;">
			<%
					IRConfig irConfig = IRSettings.getConfig();
					String collectinListStr = irConfig.getString("collection.list");
					String[] colletionList = collectinListStr.split(",");
					String refreshStr = "";
					if("minute".equals(type)){
						refreshStr = "minute";
					}else{
						refreshStr = "hour";
					}
			%>
			<select name="collection" id="collection" onchange="javascript:refreshChart('<%=refreshStr%>')">
			<% for(int k=0; k<colletionList.length; k++){ %>
			<option value="<%=colletionList[k] %>" <%=colletionList[k].equals(collection) ? "selected" : "" %> ><%=colletionList[k] %></option>
			<% } %>
			</select>
		</div>
		<div style=width:100%;height:200px;">
			<div id="chartSearchActDiv" style=" height:200px; background-color:#FFFFFF"></div>
		</div>
		<div style=width:100%;height:200px;">
			<div id="chartSearchTimeDiv" style=" height:200px; background-color:#FFFFFF"></div>
		</div>
		<div style=width:100%;height:200px;">
			<div id="chartSearchFailDiv" style=" height:200px; background-color:#FFFFFF"></div>
		</div>
		<div id="ver">fastcat v<%=IRSettings.VERSION %></div>
<!-- E : #mainContent -->
</div>

<!-- footer -->
<%@include file="footer.jsp" %>
	
</div><!-- //E : #container -->
</body>
</html>
