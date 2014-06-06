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

<%@page import="java.util.Date"%>
<%@page import="java.util.Calendar"%>
<%@page import="java.net.URLDecoder"%>

<%@include file="../common.jsp" %>

<%
	String message = URLDecoder.decode(WebUtils.getString(request.getParameter("message"), ""),"utf-8");

	CatbotServerConfig irConfig = CatbotSettings.getConfig(true);
	String siteListStr = irConfig.getString("site.list");
	String[] siteList = siteListStr.split(",");
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
	<script src="<%=CATBOT_MANAGE_ROOT%>js/jquery-1.4.4.min.js" type="text/javascript"></script>
	<script src="<%=CATBOT_MANAGE_ROOT%>js/jquery.validate.min.js" type="text/javascript"></script>
	<script src="<%=CATBOT_MANAGE_ROOT%>js/validate.messages_ko.js" type="text/javascript"></script>
	<script src="<%=CATBOT_MANAGE_ROOT%>js/validate.additional.js" type="text/javascript"></script>
	<script type="text/javascript">
	$(document).ready(function() {
		$("#collectionForm").validate({
			errorClass : "invalidValue",
			wrapper : "li",
			errorLabelContainer: "#messageBox",
			submitHandler: function(form) {
				form.submit();
				return true;
			}
		});
	});
	
	function alertMessage(){
		var message = "<%=message %>";
		if(message != "")
			alert(message);
	}

	function addSite(){
		$("#cmd").val("0");
		$("#siteForm").submit();
	}

	function removeSite(){
		var x = document.getElementsByName("selectSite");
		site = "";
		for(i=0;i<x.length;i++){
			if(x[i].checked){
				site = x[i].value;
				break;
			}
		}

        if(site == "")
            alert("삭제할 사이트를 선택해주세요.");
        else
			location.href = "siteService.jsp?cmd=1&site="+site;
		
	}
	</script>
</head>

<body onload="alertMessage()">
<div id="container">
<!-- header -->
<%@include file="../header.jsp" %>

<div id="sidebar">
	<div class="sidebox">
		<h3>사이트</h3>
			<ul class="latest">
			<li><a href="<%=CATBOT_MANAGE_ROOT%>site/main.jsp" class="selected">사이트관리</a></li>
			<li><a href="<%=CATBOT_MANAGE_ROOT%>site/schema.jsp">사이트설정</a></li>
			</ul>
	</div>
	<div class="sidebox">
		<h3>사이트</h3>
			<ul class="latest">
			<li>사이트에 대한 정보입니다.</li>
			</ul>
	</div>
</div><!-- E : #sidebar -->

<div id="mainContent">
	<h2>사이트 셋팅</h2>

	<div id="btnBox">
	<form id="siteForm" method="get" action="siteService.jsp">
	<input type="hidden" id="cmd" name="cmd" />
	<a class="btn" onclick="javascript:addSite()">페이지추가</a>
	<div id="messageBox" style="width:400px; margin-left:220px; text-align: left;"> </div>
	</form>
	</div>
	
	<!-- E : #mainContent --></div>

<!-- footer -->
<%@include file="../footer.jsp" %>
	
</div><!-- //E : #container -->

</body>

</html>
