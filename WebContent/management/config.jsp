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
<%@page import="com.websqrd.catbot.web.WebUtils"%>
<%@page import="java.util.Date"%>
<%@page import="java.util.Calendar"%>
<%@page import="java.util.List"%>
<%@page import="java.net.URLDecoder"%>

<%@include file="../common.jsp" %>

<%
	CatbotServerConfig irConfig = CatbotSettings.getCatbotServerConfig(true);
	String message = URLDecoder.decode(WebUtils.getString(request.getParameter("message"), ""),"utf-8");
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
<script type="text/javascript" src="<%=CATBOT_MANAGE_ROOT%>js/jquery-1.4.4.min.js"></script>
<script src="<%=CATBOT_MANAGE_ROOT%>js/jquery.validate.min.js" type="text/javascript"></script>
	<script src="<%=CATBOT_MANAGE_ROOT%>js/validate.messages_ko.js" type="text/javascript"></script>
	<script src="<%=CATBOT_MANAGE_ROOT%>js/help.js" type="text/javascript"></script>
	<script type="text/javascript">
	
	$(document).ready(function() {
		$("#configForm").validate({
			errorClass : "invalidValue",
			rules: {
				"server.port" : {required: true, number: true, min: 8000, max: 12000}
			}
		});

		var message = "<%=message %>";
		if(message != "")
			alert(message);
		
	});

	function configSubmit(){
		if($("#configForm").valid()){
			$("#configForm").submit();
		}
	}
	</script>
</head>

<body>

<div id="container">
<!-- header -->
<%@include file="../header.jsp" %>

<div id="sidebar">
	<div class="sidebox">
		<h3>관리</h3>
			<ul class="latest">
			<li><a href="<%=CATBOT_MANAGE_ROOT%>management/main.jsp">시스템상태</a></li>
			<li><a href="<%=CATBOT_MANAGE_ROOT%>management/account.jsp">계정관리</a></li>
			<li><a href="<%=CATBOT_MANAGE_ROOT%>management/config.jsp" class="selected">사용자설정</a></li>
			</ul>
	</div>
</div><!-- E : #sidebar -->

<div id="mainContent">
	
<form action="managementService.jsp" method="post" name="configForm" id="configForm">
<input type="hidden" name="cmd" value="1" />

	<h2>서버</h2>
	<div class="fbox">
	<table summary="서버" class="tbl01">
	<colgroup><col width="33%" /><col width="" /></colgroup>
	<tbody>
		<tr>
		<th><span id="server_port" class="help">포트(PORT)</span></th>
		<td style="text-align:left;"><input type="text" name="server.port" value="<%=irConfig.getString("server.port") %>" size='20' maxlength='5' class='inp02' /></td>
		</tr>
	</tbody>
	</table>
	</div>
	
	<h2>동적클래스패스</h2>
	<div class="fbox">
	<table summary="서버" class="tbl01">
	<colgroup><col width="33%" /><col width="" /></colgroup>
	<tbody>
		<tr>
		<th><span id="dynamic_classpath" class="help">클래스패스</span></th>
		<td><input type="text" name="dynamic.classpath" value="<%=irConfig.getString("dynamic.classpath") %>" size='50' class='inp02' /></td>
		</tr>
	</tbody>
	</table>
	</div>
	
	<div id="btnBox">
	<a href="javascript:configSubmit()" class="btn">저장</a>
	</div>

</form>
	<!-- E : #mainContent --></div>

<!-- footer -->
<%@include file="../footer.jsp" %>
	
</div><!-- //E : #container -->
	

</body>

</html>

