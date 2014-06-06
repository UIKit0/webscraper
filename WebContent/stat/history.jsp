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

<%@page import="com.websqrd.catbot.db.*"%>
<%@page import="com.websqrd.libs.common.Formatter"%>
<%@page import="com.websqrd.catbot.web.WebUtils"%>
<%@page import="com.websqrd.catbot.setting.CatbotSettings"%>
<%@page import="com.websqrd.catbot.setting.*"%>

<%@page import="java.util.List"%>
<%@page import="java.util.Date"%>
<%@page import="java.util.Calendar"%>
<%@page import="com.websqrd.catbot.web.WebUtils"%>
<%@include file="../common.jsp" %>

<%
	int pg = WebUtils.getInt(request.getParameter("pg"), 1);
	int pgSize = 10;
	int counterWidth = 10;
	int startRow = (pg - 1) * pgSize;
	
	DBHandler dbHandler = DBHandler.getInstance();
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
<script type="text/javascript" src="<%=CATBOT_MANAGE_ROOT%>js/common.js"></script>
</head>

<body>

<div id="container">
<!-- header -->
<%@include file="../header.jsp" %>

<div id="sidebar">
	<div class="sidebox">
		<h3>통계</h3>
			<ul class="latest">
			<li><a href="<%=CATBOT_MANAGE_ROOT%>stat/scrapingStat.jsp">수집현황</a></li>
			<li><a href="<%=CATBOT_MANAGE_ROOT%>stat/history.jsp" class="selected">수집히스토리</a></li>
			</ul>
	</div>
</div><!-- E : #sidebar -->


<div id="mainContent">
	<h2>수집히스토리</h2>
	<div class="fbox">
	<table summary="수집히스토리" class="tbl02">
	<thead>
	<tr>
		<th class="first">번호</th>
		<th>사이트 > 게시판</th>
		<th>성공여부</th>
		<th>문서수</th>
		<th>스케쥴링</th>
		<th>시작시각</th>
		<th>종료시각</th>
		<th>실행시간</th>
	</tr>
	</thead>
	<tbody>
<%
	ScrapingHistory scrapingHistory = dbHandler.getScrapingHistory();
	List<ScrapingHistory> list = scrapingHistory.select(startRow, pgSize, false);
	int totalCount = scrapingHistory.count(false);
	
	for(int i = 0; i < list.size(); i++){
		ScrapingHistory history = list.get(i);
		String siteId = history.site;
		String cateId = history.category;
		String siteName = siteId;
		String categoryName = cateId;
		SiteConfig siteConfig = CatbotSettings.getSiteConfig(siteId);
		CategoryConfig categoryConfig = CatbotSettings.getCategoryConfig(siteId, cateId);
		if(siteConfig != null){
			siteName = siteConfig.getDescription();
		}
		if(categoryConfig != null){
			categoryName = categoryConfig.getDescription();
		}
%>
	<tr>
		<td class="first"><%=history.id%></td>
		<td><strong class="small tb"><%=siteName%><br/> ><%=categoryName%></strong></td>
		<td><%=history.isSuccess ? "성공" : "실패" %></td>
		<td><%=history.docSize%></td>
		<td><%=history.isScheduled ? "자동" : "수동" %></td>
		<td><%=history.startTime%></td>
		<td><%=history.endTime%></td>
		<td><%=Formatter.getFormatTime((long)history.duration)%></td>
	</tr>
<%
	}
	
	if(list.size() == 0){
	%>
		<tr><td  class="first" colspan="9">수집 히스토리가 없습니다.</td></tr>
	<%
	}
%>
	</tbody>
	</table>
	</div>
	
<p class="clear"></p>
	<div class="list_bottom">
	<div id="paging" class="fl">
	<%
		int counterStart = ((pg - 1) / counterWidth) * counterWidth + 1;
		int counterEnd = counterStart + counterWidth;
		int maxPage = totalCount / pgSize;
		if(totalCount % pgSize > 0) 
			maxPage++;
		
		if(pg != 1){
		    int prevStart = pg - 1;
		    out.println("<span class='num'><a href='?pg=1'>처음</a></span>");
		    out.println("<span class='num'><a href='?pg="+prevStart+"'>이전</a></span>");
		}
		
		for(int c = counterStart; c < counterEnd; c++){
			if(c <= maxPage){
				if(c == pg){
					out.println("<span class='num'><a href='?pg="+c+"' class='selected'>"+c+"</a></span>");
				}else{
					out.println("<span class='num'><a href='?pg="+c+"'>"+c+"</a></span>");
				}
			}else{
				break;
			}
		}
		
		if(pg < maxPage){ 
			int nextPage = pg + 1;
		    out.println("<span class='num'><a href=?pg="+nextPage+">다음</a></span>");
		}

		if(pg < maxPage){
			int lastRec = totalCount % pgSize;
		    out.println("<span class='num'><a href=?pg="+maxPage+">마지막</a></span>");
		}
		%>
	</div>
	</div>
	
	<!-- E : #mainContent --></div>
	
<!-- footer -->
<%@include file="../footer.jsp" %>
	
</div><!-- //E : #container -->
	
</body>

</html>
