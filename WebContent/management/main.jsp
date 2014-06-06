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

<%@page import="com.websqrd.libs.common.Formatter"%>
<%@page import="com.websqrd.catbot.web.WebUtils"%>
<%@page import="com.websqrd.catbot.setting.CatbotSettings"%>
<%@page import="com.websqrd.catbot.setting.CatbotServerConfig"%>
<%@page import="com.websqrd.catbot.service.*"%>
<%@page import="com.websqrd.catbot.control.*"%>
<%@page import="com.websqrd.catbot.db.*"%>
<%@page import="com.websqrd.catbot.server.*"%>

<%@page import="java.util.concurrent.ThreadPoolExecutor"%>
<%@page import="java.sql.Timestamp"%>
<%@page import="java.util.Properties"%>

<%@page import="java.util.Date"%>
<%@page import="java.util.Calendar"%>
<%@page import="java.net.URLDecoder"%>

<%@include file="../common.jsp" %>

<%
	String message = URLDecoder.decode(WebUtils.getString(request.getParameter("message"), ""),"utf-8");
	CatbotServerConfig irConfig = CatbotSettings.getCatbotServerConfig(true);
	Properties systemProps = System.getProperties();
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>웹수집기 관리도구</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<link href="<%=CATBOT_MANAGE_ROOT%>css/reset.css" rel="stylesheet" type="text/css" />
<link href="<%=CATBOT_MANAGE_ROOT%>css/style.css" rel="stylesheet" type="text/css" />
<!--[if lte IE 6]>
<link href="<%=CATBOT_MANAGE_ROOT%>css/style_ie.css" rel="stylesheet" type="text/css" />
<![endif]-->
<script type="text/javascript" src="<%=CATBOT_MANAGE_ROOT%>js/common.js"></script>
<script src="<%=CATBOT_MANAGE_ROOT%>js/jquery-1.4.4.min.js" type="text/javascript"></script>
<script type="text/javascript">
	function alertMessage(){
		var message = "<%=message %>";
		if(message != "")
			alert(message);
	}

	</script>
</head>

<body onload="alertMessage()">

<div id="container">
<!-- header -->
<%@include file="../header.jsp" %>

<div id="sidebar">
	<div class="sidebox">
		<h3>관리</h3>
			<ul class="latest">
			<li><a href="<%=CATBOT_MANAGE_ROOT%>management/main.jsp" class="selected">시스템정보</a></li>
			<li><a href="<%=CATBOT_MANAGE_ROOT%>management/account.jsp">계정관리</a></li>
			<li><a href="<%=CATBOT_MANAGE_ROOT%>management/config.jsp">사용자설정</a></li>
			</ul>
	</div>
</div><!-- E : #sidebar -->

	<div id="mainContent">
			
	<h2>시스템정보</h2>
	<div class="fbox">
	<table summary="시스템상태" class="tbl01">
	<colgroup><col width="20%" /><col width="" /></colgroup>
	<tbody>
	<tr>
		<th>검색엔진 홈</th>
		<td><%=CatbotSettings.HOME %></td>
	</tr>
	<tr>
		<th>시스템시각</th>
		<td><%=new Timestamp(System.currentTimeMillis()) %></td>
	</tr>
	<tr>
		<th>JDK 벤더/버전</th>
		<td><%=systemProps.getProperty("java.vendor") %><br/>
				<%=systemProps.getProperty("java.vm.name") %><br/>
				<%=systemProps.getProperty("java.version") %></td>
	</tr>
	<tr>
		<th>JAVA HOME</th>
		<td><%=systemProps.getProperty("java.home") %></td>
	</tr>
	<tr>
		<th>클래스패스</th>
		<td><textarea style="width:100%" rows="3" readonly="true"><%=systemProps.getProperty("java.class.path") %></textarea></td>
	</tr>
	<tr>
		<th>운영체제</th>
		<td><%=systemProps.getProperty("os.name") %> (<%=systemProps.getProperty("os.arch") %>) <%=systemProps.getProperty("os.version") %></td>
	</tr>
		<tr>
		<th>파일구분자</th>
		<td><%=systemProps.getProperty("file.separator") %></td>
	</tr>
		<tr>
		<th>경로구분자</th>
		<td><%=systemProps.getProperty("path.separator") %></td>
	</tr>
	<tr>
		<th>줄바꿈문자</th>
		<td><%=systemProps.getProperty("line.separator").replaceAll("\\n","&#92;n").replaceAll("\\r","&#92;r") %></td>
	</tr>
	<tr>
		<th>사용자이름</th>
		<td><%=systemProps.getProperty("user.name") %></td>
	</tr>
	</tbody>
	</table>
	</div>
	
	<p class="clear"></p>
	<!-- E : #mainContent --></div>

<!-- footer -->
<%@include file="../footer.jsp" %>
	
</div><!-- //E : #container -->

</body>

</html>
