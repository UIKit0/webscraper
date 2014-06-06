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
<%@page import="com.websqrd.catbot.setting.CatbotSettings"%>
<%@page import="com.websqrd.catbot.setting.*"%>
<%@page import="com.websqrd.catbot.db.*"%>

<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.List"%>
<%@page import="java.util.*"%>
<%@page import="java.util.Map.Entry"%>
<%@page import="java.util.Calendar"%>
<%@page import="java.net.URLDecoder"%>

<%@include file="../common.jsp" %>

<%
	String message = URLDecoder.decode(WebUtils.getString(request.getParameter("message"), ""),"utf-8");
	boolean settingReload = WebUtils.getBoolean(request.getParameter("settingReload"), false); 
	CatbotConfig catbotConfig = CatbotSettings.getGlobalConfig(settingReload);
	List<String> siteList = catbotConfig.getSiteList();
	
	DBHandler dbHandler = DBHandler.getInstance();
	
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" />
<head>
<title>웹수집기 관리도구</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<link href="<%=CATBOT_MANAGE_ROOT%>css/reset.css" rel="stylesheet" type="text/css" />
<link href="<%=CATBOT_MANAGE_ROOT%>css/style.css" rel="stylesheet" type="text/css" />
<!--[if lte IE 6]>
<link href="<%=CATBOT_MANAGE_ROOT%>css/style_ie.css" rel="stylesheet" type="text/css" />
<![endif]-->
<script type="text/javascript" src="<%=CATBOT_MANAGE_ROOT%>js/common.js"></script>
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
		<h3>수집설정</h3>
			<ul class="latest">
			<li><a href="<%=CATBOT_MANAGE_ROOT%>scraping/main.jsp" class="selected">수집관리</a></li>
			<li><a href="<%=CATBOT_MANAGE_ROOT%>scraping/schedule.jsp">수집주기설정</a></li>
			<li><a href="<%=CATBOT_MANAGE_ROOT%>scraping/history.jsp">수집히스토리</a></li>
			</ul>
	</div>
	<div class="sidebox">
		<h3>도움말</h3>
			<ul class="latest">
			<li>각 사이트에 대한 수집결과를 확인합니다.</li>
			<li>주기설정에 따라 자동으로 수집된 결과를 볼 수 있습니다.</li>
			<li>즉시 실행하고자 하면 수집버튼을 눌러 수동으로 실행할 수 있습니다.</li>
			</ul>
	</div>
</div><!-- E : #sidebar -->


<div id="mainContent">
	<h2>수집관리</h2>
	<div><a href="?settingReload=true" class="btn_s">셋팅 재로딩</a></div><br/>
	<div class="fbox">
	<table summary="수집관리" class="tbl02">
	<colgroup><col width="3%" /><col width="10%" /><col width="10%" /><col width="6%" /><col width="8%" /><col width="8%" /><col width="10%" /><col width="10%" /><col width="7%" /><col width="7%" /></colgroup>
	<thead>
	<tr>
		<th class="first">No.</th>
		<th>사이트 > 게시판</th>
		<th>수집타입</th>
		<th>성공여부</th>
		<th>문서수</th>
		<th>스케줄링</th>
		<th>시작시각</th>
		<th>종료시각</th>
		<th>실행시간</th>
		<th>실행</th>
	</tr>
	</thead>
	<tbody>
<%
	for(int i = 0; i < siteList.size(); i++){
		String siteId = siteList.get(i);
		SiteConfig siteConfig = CatbotSettings.getSiteConfig(siteId, settingReload);
		Map<String, CategoryConfig> categoryConfigList = CatbotSettings.getSiteCategoryConfigList(siteId, settingReload);
		if(categoryConfigList == null){
			break;
		}
		Iterator<Entry<String, CategoryConfig>> iter = categoryConfigList.entrySet().iterator();
		
		while(iter.hasNext()){
	Entry<String, CategoryConfig> entry = iter.next();
	String category = entry.getKey();
	CategoryConfig categoryConfig = entry.getValue();
	
	ScrapingResult fullIndexingResult = dbHandler.getScrapingResult();
	fullIndexingResult.select(siteId, category, "F");
	ScrapingResult incIndexingResult = dbHandler.getScrapingResult();
	incIndexingResult.select(siteId, category, "I");
	
	String startTimeStr = "";
	String durationStr = "";
%>
		<tr>
			<td rowspan="2" class="first"><%=i+1%></td>
			<td rowspan="2"><strong class="small tb"><%=siteConfig.getDescription()%><br/>&nbsp;&gt;<%=categoryConfig.getDescription()%></strong></td>
			<td>전체수집</td>
			<%
				if(fullIndexingResult != null){
			%>
			<td><%
				if(fullIndexingResult.status == ScrapingResult.STATUS_SUCCESS){ 
					out.println("성공");
				}else if(fullIndexingResult.status == ScrapingResult.STATUS_FAIL){ 
					out.println("실패");
				}else if(fullIndexingResult.status == ScrapingResult.STATUS_RUNNING){ 
					out.println("수집중");
				}
			%></td>
			<td><%=fullIndexingResult.docSize%></td>
			<td><%=fullIndexingResult.isScheduled ? "자동" : "수동"%></td>
			<td><%=fullIndexingResult.startTime%></td>
			<td><%=fullIndexingResult.endTime%></td>
			<td><%=Formatter.getFormatTime((long)fullIndexingResult.duration)%></td>
			<%
				}else{
			%>
			<td>-</td>
			<td>-</td>
			<td>-</td>
			<td>-</td>
			<td>-</td>
			<td>-</td>
			<%
				}
			%>
			<td><a href="scrapingService.jsp?cmd=0&site=<%=siteId%>&category=<%=category%>" class="btn_s">실행</a></td>
		</tr>
		<tr>
			<td>증분수집</td>
			<%
				if(incIndexingResult != null){
			%>
			<td><%
				if(incIndexingResult.status == ScrapingResult.STATUS_SUCCESS){ 
					out.println("성공");
				}else if(incIndexingResult.status == ScrapingResult.STATUS_FAIL){ 
					out.println("실패");
				}else if(incIndexingResult.status == ScrapingResult.STATUS_RUNNING){ 
					out.println("수집중");
				}
			%></td>
			<td><%=incIndexingResult.docSize%></td>
			<td><%=incIndexingResult.isScheduled ? "자동" : "수동" %></td>
			<td><%=incIndexingResult.startTime%></td>
			<td><%=incIndexingResult.endTime%></td>
			<td><%=Formatter.getFormatTime((long)incIndexingResult.duration)%></td>
			<%
			}else{
			%>
			<td>-</td>
			<td>-</td>
			<td>-</td>
			<td>-</td>
			<td>-</td>
			<td>-</td>
			<%
			}
			%>
			<td><a href="scrapingService.jsp?cmd=1&site=<%=siteId%>&category=<%=category%>" class="btn_s">실행</a></td>
		</tr>
<%
		}
	}
%>
	</tbody>
	</table>
	</div>
	
	<!-- E : #mainContent --></div>
	
<!-- footer -->
<%@include file="../footer.jsp" %>
	
</div><!-- //E : #container -->
	
</body>

</html>
