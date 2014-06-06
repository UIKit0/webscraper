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
<%@page import="com.websqrd.catbot.setting.*"%>

<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.*"%>
<%@page import="java.util.Date"%>
<%@page import="java.util.Calendar"%>
<%@page import="java.net.URLDecoder"%>
<%@page import="com.websqrd.catbot.web.WebUtils"%>
<%@include file="../common.jsp" %>

<%
	CatbotConfig irConfig = CatbotSettings.getGlobalConfig(true);
	String message = URLDecoder.decode(WebUtils.getString(request.getParameter("message"), ""),"utf-8");
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" />
<head>
<title>웹수집기 관리도구</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<link href="<%=CATBOT_MANAGE_ROOT%>css/reset.css" rel="stylesheet" type="text/css" />
<link href="<%=CATBOT_MANAGE_ROOT%>css/style.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="<%=CATBOT_MANAGE_ROOT%>js/jquery-1.4.4.min.js"></script>
<!--[if lte IE 6]>
<link href="<%=CATBOT_MANAGE_ROOT%>css/style_ie.css" rel="stylesheet" type="text/css" />
<![endif]-->
<script type="text/javascript" src="<%=CATBOT_MANAGE_ROOT%>js/common.js"></script>
<script type="text/javascript">
	$(document).ready(function() {
		var message = "<%=message %>";
		if(message != "")
			alert(message);
	});
	function updateRepo(){
		$("#repoForm").submit();
	}
</script>
</head>

<body>

<div id="container">
<!-- header -->
<%@include file="../header.jsp" %>

<div id="sidebar">
	<div class="sidebox">
		<h3>저장소</h3>
			<ul class="latest">
			<li><a href="<%=CATBOT_MANAGE_ROOT%>repository/main.jsp">저장소설정</a></li>
			</ul>
	</div>
</div><!-- E : #sidebar -->

<%
	Map<String, RepositorySetting> settingList = CatbotSettings.getRepositoryList(true);
	String repositoryName = irConfig.getRepository();
%>
<div id="mainContent">
<form action="repoService.jsp" method="post" name="repoForm" id="repoForm">
	<input type="hidden" name="cmd" id="cmd" value="3" />
	<h2>저장소설정</h2>
	<div id="btnBox2">
	<a style="" href="addRepo.jsp" class="btn_s">+저장소추가</a>
	</div>
	<br/>
	<div class="fbox">
	<table summary="색인결과" class="tbl02">
	<colgroup><col width="3%" /><col width="10%" /><col width="10%" /><col width="6%" /><col width="8%" /><col width="8%" /><col width="10%" /><col width="10%" /><col width="7%" /><col width="7%" /></colgroup>
	<thead>
	<tr>
		<th class="first">선택</th>
		<th>저장소이름</th>
		<th>DB벤더</th>
		<th>DB명</th>
		<th>HOST</th>
		<th>PORT</th>
		<th>사용자아이디</th>
	</tr>
	</thead>
	<tbody>
<%
	Iterator<String> iter = settingList.keySet().iterator();

	while(iter.hasNext()){
		String key = iter.next();
		RepositorySetting setting = settingList.get(key);
		
%>
	<tr>
		<td class="first"><input type="radio" name="useId" id="useId" value="<%=setting.id%>" 
		<%=repositoryName.equals(setting.id) ? "checked" : "" %>
		/></td>
		<td><a href="repoEditor.jsp?id=<%=setting.id%>" ><strong class="small tb"><%=setting.id%></strong></a></td>
		<td><%=setting.vendor %></td>
		<td><%=setting.db %></td>
		<td><%=setting.host %></td>
		<td><%=setting.port %></td>
		<td><%=setting.user %></td>
	</tr>
<%
	}
%>
	</tbody>
	</table>
	</div>
	 
	<div id="btnBox">
	<a href="javascript:updateRepo();" class="btn">확인</a>
	</div>
</form>
	<!-- E : #mainContent --></div>
	
<!-- footer -->
<%@include file="../footer.jsp" %>
	
</div><!-- //E : #container -->
	
</body>

</html>
