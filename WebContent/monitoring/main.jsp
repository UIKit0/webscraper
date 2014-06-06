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
	
	ThreadPoolExecutor executor = JobController.getInstance().getJobExecutor();
	long upTime = System.currentTimeMillis() - CatBotServer.startTime;
	ServiceHandler serviceHandler = ServiceHandler.getInstance();
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

	function restartCatServer(){
		//componentService.jsp?cmd=3&component=0&cmd2=2
		var request = $.ajax({
		  url: "componentService.jsp",
		  type: "GET",
		  data: {cmd : "3", component : "0", cmd2 : "2"},
		  dataType: "html"
		});
		alert("서버를 재시작하였습니다.");
	}

	function restartServiceHandler(){
		//componentService.jsp?cmd=3&component=1&cmd2=2
		var request = $.ajax({
		  url: "componentService.jsp",
		  type: "GET",
		  data: {cmd : "3", component : "1", cmd2 : "2"},
		  dataType: "html"
		});
		alert("ServiceHandler를 재시작하였습니다.");
	}
	</script>
</head>

<body onload="alertMessage()">

<div id="container">
<!-- header -->
<%@include file="../header.jsp" %>

<div id="sidebar">
	<div class="sidebox">
		<h3>모니터링</h3>
			<ul class="latest">
			<li><a href="<%=CATBOT_MANAGE_ROOT%>monitoring/main.jsp" class="selected">시스템상태</a></li>
			</ul>
	</div>
</div><!-- E : #sidebar -->

	<div id="mainContent">
	<h2>시스템상태</h2>
	<h4>컴포넌트상태</h4>
	<div class="fbox">
	<table summary="색인결과" class="tbl02">
	<colgroup><col width="5%" /><col width="35%" /><col width="30%" /><col width="30%" /></colgroup>
	<thead>
	<tr>
		<th class="first">번호</th>
		<th>컴포넌트명</th>
		<th>상태</th>
		<th>동작</th>
	</tr>
	</thead>
	<tbody>
	<tr>
		<td class="first">1</td>
		<td>CatBotServer</td>
		<td><%=CatBotServer.getInstance().isRunning()? "<span class='running'>실행중</span>"  : "<span class='stopped'>정지</span>" %></td>
		<td>
		<a href="javascript:restartCatServer()" class="btn_s">재시작</a>&nbsp;
		</td>
	</tr>
	<tr>
		<td class="first">2</td>
		<td>ServiceHandler</td>
		<td><%=ServiceHandler.getInstance().isRunning()? "<span class='running'>실행중</span>"  : "<span class='stopped'>정지</span>" %></td>
		<td>
			<a href="javascript:restartServiceHandler()" class="btn_s">재시작</a>&nbsp;
		</td>
	</tr>
	<tr>
		<td class="first">3</td>
		<td>DBHandler</td>
		<td><%=DBHandler.getInstance().isRunning()? "<span class='running'>실행중</span>"  : "<span class='stopped'>정지</span>" %></td>
		<td>
			<%
			if(DBHandler.getInstance().isRunning()){
			%>
			<a href="componentService.jsp?cmd=3&component=3&cmd2=1" class="btn_s">정지</a>&nbsp;
			<a href="componentService.jsp?cmd=3&component=3&cmd2=2" class="btn_s">재시작</a>&nbsp;
			<%
				}else{
			%>
			<a href="componentService.jsp?cmd=3&component=3&cmd2=0" class="btn_s">시작</a>&nbsp;		
			<%	
				}
			%>
		</td>
	</tr>
	<tr>
		<td class="first">4</td>
		<td>JobController</td>
		<td><%=JobController.getInstance().isRunning()? "<span class='running'>실행중</span>"  : "<span class='stopped'>정지</span>" %></td>
		<td>
			<%
			if(JobController.getInstance().isRunning()){
			%>
			<a href="componentService.jsp?cmd=3&component=4&cmd2=1" class="btn_s">정지</a>&nbsp;
			<a href="componentService.jsp?cmd=3&component=4&cmd2=2" class="btn_s">재시작</a>&nbsp;
			<%
				}else{
			%>
			<a href="componentService.jsp?cmd=3&component=4&cmd2=0" class="btn_s">시작</a>&nbsp;	
			<%	
				}
			%>
		</td>
	</tr>
	<tr>
		<td class="first">5</td>
		<td>RepositoryHandler</td>
		<td><%=RepositoryHandler.getInstance().isRunning()? "<span class='running'>실행중</span>"  : "<span class='stopped'>정지</span>" %></td>
		<td>
			<%
			if(RepositoryHandler.getInstance().isRunning()){
			%>
			<a href="componentService.jsp?cmd=3&component=5&cmd2=1" class="btn_s">정지</a>&nbsp;
			<a href="componentService.jsp?cmd=3&component=5&cmd2=2" class="btn_s">재시작</a>&nbsp;
			<%
				}else{
			%>
			<a href="componentService.jsp?cmd=3&component=5&cmd2=0" class="btn_s">시작</a>&nbsp;	
			<%	
				}
			%>
		</td>
	</tr>
	</tbody>
	</table>
	</div>
	
	<div class="boxL">
		<h4>서버상태</h4>
		<div class="fbox">
		<table summary="서버상태" class="tbl01">
		<colgroup><col width="40%" /><col width="" /></colgroup>
		<tbody>
		<!-- tr>
			<th>클라이언트 수</th>
			<td><%=serviceHandler.getClientCount() %></td>
		</tr-->
		<tr>
			<th>구동시간</th>
			<td><%=Formatter.getFormatTime(upTime) %></td>
		</tr>
		<tr class="last">
			<th>사용메모리</th>
			<td><%=Formatter.getFormatSize(Runtime.getRuntime().totalMemory()) %></td>
		</tr>
		</tbody>
		</table>
		</div>
	</div>
	
	<div class="boxR">
		<h4>작업실행기 상태</h4>
		<div class="fbox">
		<table summary="데이터소스 설정" class="tbl01">
		<colgroup><col width="40%" /><col width="" /></colgroup>
		<tbody>
		<tr>
			<th>실행중</th>
			<td><%=executor.getActiveCount() %></td>
		</tr>
		<tr>
			<th>POOL 사이즈</th>
			<td><%=executor.getPoolSize() %></td>
		</tr>
		<tr>
			<th>최대 POOL 사이즈</th>
			<td><%=executor.getMaximumPoolSize() %></td>
		</tr>
		<tr class="last">
			<th>수행한 작업수</th>
			<td><%=executor.getCompletedTaskCount() %></td>
		</tr>
		</tbody>
		</table>
		</div>
	</div>
	<p class="clear"></p>
	<!-- E : #mainContent --></div>

<!-- footer -->
<%@include file="../footer.jsp" %>
	
</div><!-- //E : #container -->

</body>

</html>
