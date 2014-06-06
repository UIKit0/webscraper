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
<%@page import="com.websqrd.catbot.web.WebUtils"%>
<%@include file="../common.jsp" %>

<%
	String message = URLDecoder.decode(WebUtils.getString(request.getParameter("message"), ""),"utf-8");
	boolean settingReload = WebUtils.getBoolean(request.getParameter("settingReload"), false); 
	CatbotConfig catbotConfig = CatbotSettings.getGlobalConfig(settingReload);
	List<String> siteList = catbotConfig.getSiteList();
	
	ScrapingResult FullIndexingResult = DBHandler.getInstance().getScrapingResult();
	ScrapingResult IncIndexingResult = DBHandler.getInstance().getScrapingResult();		
	
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
<script language="JavaScript" type="text/javascript" src="<%=CATBOT_MANAGE_ROOT%>js/jquery-1.4.4.min.js"></script>
<script type="text/javascript" src="<%=CATBOT_MANAGE_ROOT%>js/common.js"></script>
<script type="text/javascript">
	
	$(document).ready(function() {
		var message = "<%=message %>";
		if(message != "") {
			alert(message);
		}
		
		$("#allSiteCheck").click(function() {
			var siteLst = $(".clsSiteList");
			var param = [];
			for(var inx=0;inx<siteLst.length;inx++) {
				var siteRecord = siteLst.get(inx);
				var site = siteRecord.getAttribute("site");
				var category = siteRecord.getAttribute("category");
				
	 			$("#site_"+site+"_"+category)[0].checked=$(this)[0].checked;
			}
		});
		
		$(".clsSiteChk").click(function() {
			var site = $(this).val();
			var cateLst = $(".clsCateChk");
			for(var inx=0;inx<cateLst.length;inx++) {
				var cateElement = cateLst.get(inx);
				if(cateElement.value == site) {
					cateElement.checked = $(this)[0].checked;
					if($(this).attr("checked")) {
						cateElement.checked=true;
					} else {
						cateElement.checked=false;
					}
				}
			}
		});
	});
	
	function scrapingChecked(checkType){
		if(checkType=="ALL") {
			if ( confirm("전체 수집을 실행 할 경우 해당 서버에서 공격으로 오인하여 중간에 끊어 질 수가 있습니다. 그래도 하시겠습니까?") == false )
				return ;
		}
			
		var siteLst = $(".clsSiteList");
		var param = [];
		var checkcnt = 0;
		for(var inx=0;inx<siteLst.length;inx++) {
			var siteRecord = siteLst.get(inx);
			var site = siteRecord.getAttribute("site");
			var category = siteRecord.getAttribute("category");			
			
 			if($("#site_"+site+"_"+category)[0].checked) {
				param[param.length]={"site":site,"cate":category,"action":"GETALL"}
				checkcnt= checkcnt + 1;
 			}
		}
		
		if ( checkcnt == siteLst.length )
			{
			if ( confirm("전체 수집을 실행 할 경우 해당 서버에서 공격으로 오인하여 중간에 끊어 질 수가 있습니다. 그래도 하시겠습니까?") == false )
				return ;
			}
		
		$.ajax({
			url:"scrapingService.jsp",			
			method:"post",
			data:{ checkType:checkType,param:JSON.stringify(param) },
				success:function(data) {
				//data=data.replace(/^\s\s*/,"");				
			}
		});
		var site="";
		var category="";
			
		var siteLst = $(".clsSiteList");
		for(var inx=0;inx<siteLst.length;inx++) {
			var siteRecord = siteLst.get(inx);
			var site = siteRecord.getAttribute("site");
			var category = siteRecord.getAttribute("category");
 			if($("#site_"+site+"_"+category)[0].checked || checkType=="ALL") {
				$("#"+site+"_"+category+"_status").html("수집중");
				$("#"+site+"_"+category+"_docSize").html("");
				$("#"+site+"_"+category+"_schedule").html("수동");
				$("#"+site+"_"+category+"_stime").html("");				
				$("#"+site+"_"+category+"_dtime").html("");
				$("#"+site+"_"+category+"_status").css("background","#00FF00").css("color","#FF0000");
 			}
		}
		if(checkType=="ALL") {
			alert("전체 수집을 수행 합니다.");
		} else {
			alert("선택된 사이트의 수집을 수행 합니다.");
		}

	}
	
	function scraping(action, site, category){
		$.ajax({
			url:"scrapingService.jsp",			
			method:"post",
			data:{param:JSON.stringify(
				[{action:action,site:site,cate:category}]
			)},
				success:function(data) {
				data=data.replace(/^\s\s*/,"");
			}
		});
		alert(site + "[" + category +"]" + " " + (action.trim() == "GETALL" ? "전체" : "증분")+ " 수집을 수행 합니다.");
		$("#"+site+"_"+category+"_status").html("수집중");
		$("#"+site+"_"+category+"_docSize").html("");
		$("#"+site+"_"+category+"_schedule").html("수동");
		$("#"+site+"_"+category+"_stime").html("");				
		$("#"+site+"_"+category+"_dtime").html("");
		$("#"+site+"_"+category+"_status").css("background","#00FF00").css("color","#FF0000");
	}
	
	function clearCategoryChecked(checkType){
		var siteLst = $(".clsSiteList");
		var param = [];
		for(var inx=0;inx<siteLst.length;inx++) {
			var siteRecord = siteLst.get(inx);
			var site = siteRecord.getAttribute("site");
			var category = siteRecord.getAttribute("category");
			
 			if($("#site_"+site+"_"+category)[0].checked) {
				param[param.length]={"site":site,"cate":category}
 			}
		}

		$.ajax({
			url:"../site/categoryInit.jsp",
			method:"post",
			data:{ checkType:checkType,param:JSON.stringify(param) },
				success:function(data) {
				//data=data.replace(/^\s\s*/,"");
			}
		});
		var site="";
		var category="";
			
		var siteLst = $(".clsSiteList");
		for(var inx=0;inx<siteLst.length;inx++) {
			var siteRecord = siteLst.get(inx);
			var site = siteRecord.getAttribute("site");
			var category = siteRecord.getAttribute("category");
 			if($("#site_"+site+"_"+category)[0].checked || checkType=="ALL") {
				$("#"+site+"_"+category+"_status").html("");
				$("#"+site+"_"+category+"_docSize").html("");
				$("#"+site+"_"+category+"_schedule").html("");
				$("#"+site+"_"+category+"_stime").html("");				
				$("#"+site+"_"+category+"_dtime").html("");
				$("#"+site+"_"+category+"_status").css("background","#FFFFFF").css("color","#000000");
 			}
		}
		if(checkType=="ALL") {
			alert("전체 초기화를 수행 합니다.");
		} else {
			alert("선택된 사이트들을 초기화 합니다.");
		}
	}

	function clearCategory(site, category){
		$.ajax({
			url:"../site/categoryInit.jsp",			
			method:"post",
			data:{param:JSON.stringify(
				[{site:site,cate:category}]
			)},			
				success:function(data) {
				data=data.replace(/^\s\s*/,"");
			}
		});
		alert(site + "[" + category +"]" + " 를 초기화 합니다.");
		$("#"+site+"_"+category+"_status").html("");
		$("#"+site+"_"+category+"_docSize").html("");
		$("#"+site+"_"+category+"_schedule").html("");
		$("#"+site+"_"+category+"_stime").html("");				
		$("#"+site+"_"+category+"_dtime").html("");
		$("#"+site+"_"+category+"_status").css("background","#FFFFFF").css("color","#000000");
	}
</script>
</head>

<body>

<div id="container">
<!-- header -->
<%@include file="../header.jsp" %>

<div id="sidebar">
	<div class="sidebox">
		<h3>사이트 리스트</h3>
			<ul class="site">
			
			<%
			for(int i = 0; i < siteList.size(); i++){
				String siteName = siteList.get(i);
				SiteConfig sConfig = CatbotSettings.getSiteConfig(siteName);
				Map<String, CategoryConfig> map = CatbotSettings.getSiteCategoryConfigList(siteName);
				Iterator it = map.entrySet().iterator();   
			%>
			<li>
				<%=sConfig.getDescription() %>
				<% 
				 while (it.hasNext()) {   
			         Map.Entry entry = (Map.Entry) it.next();   
			         CategoryConfig cateConfig = (CategoryConfig)entry.getValue();   
			         String cateName= cateConfig.getCategoryName();
				%>
				<ul>
					<a href= "dataView.jsp?site=<%=siteName %>&category=<%=cateName %>">&gt;<%=cateConfig.getDescription() %></a>
				</ul>
				<% } %>
			</li>
			<%
			}
			%>
			
			</ul>
	</div>
</div><!-- E : #sidebar -->


<div id="mainContent">
	<h2>수집관리</h2>
	<div>
	<a href="?settingReload=true" class="btn_s">셋팅 재로딩</a>
	<a href="javascript:scrapingChecked('ALL')" class="btn_s">전체 실행</a>
	<a href="javascript:clearCategoryChecked('ALL')" class="btn_s">전체 초기화</a>
	<a href="javascript:scrapingChecked()" class="btn_s">선택 실행</a>
	<a href="javascript:clearCategoryChecked()" class="btn_s">선택 초기화</a>
	</div><br/>
	<div class="fbox">
	<table summary="수집관리" class="tbl02">
	<colgroup>
		<col width="2%" />
		<col width="2%" />
		<col width="2%" />
		<col width="27%" />
		<col width="9%" />
		<col width="9%" />
		<col width="8%" />
		<col width="8%" />
		<col width="8%" />
		<col width="8%" />
		<col width="10%" />
		<col width="8%" />
	</colgroup>
	<thead>
	<tr>
		<th class="first"></th>
		<th><input type="checkbox" id="allSiteCheck"/></th>
		<th>No.</th>
		<th>사이트 > 게시판</th>		
		<th>전체수집실행</th>
		<th>증분수집실행</th>
		<th>초기화</th>
		<th>성공여부</th>
		<th align=right>문서수</th>
		<th>스케줄링</th>
		<th>시작시각</th>
		<th>실행시간</th>
	</tr>
	</thead>
	<tbody>
<%
    int idx = 0;
	for(int i = 0; i < siteList.size(); i++){
		String siteId = siteList.get(i);
		SiteConfig siteConfig = CatbotSettings.getSiteConfig(siteId, settingReload);
		Map<String, CategoryConfig> categoryConfigList = CatbotSettings.getSiteCategoryConfigList(siteId, settingReload);
		if(categoryConfigList == null){
			break;
		}
		Iterator<Entry<String, CategoryConfig>> iter = categoryConfigList.entrySet().iterator();
		
		Map<String,CategoryConfig> smap = new HashMap<String, CategoryConfig>();
		
		for(int cateInx=0;iter.hasNext();cateInx++){
			Entry<String, CategoryConfig> entry = iter.next();
			String category = entry.getKey();
			CategoryConfig categoryConfig = entry.getValue();
			smap.put(category, categoryConfig);
		}
		Iterator<String> kiter = new TreeSet<String>(smap.keySet()).iterator();
		
		for(int cateInx=0;kiter.hasNext();cateInx++){
			String category = kiter.next();
			CategoryConfig categoryConfig = smap.get(category);
			idx++;
			int IndexingCount = FullIndexingResult.select(siteId, category);
			
			String startTimeStr = "";
			String durationStr = "";
%>
		<tr class="clsSiteList" site="<%=siteId%>" category="<%=category%>">
			<%if(cateInx==0) { %>
			<td class="first"  rowspan="<%=smap.size() %>">
				<input class="clsSiteChk" id="site_<%=siteId%>" type="checkbox" value="<%=siteId%>"/>
			</td>
			<% } %>
			<td><input class="clsCateChk" id="site_<%=siteId%>_<%=category%>" type="checkbox" value="<%=siteId%>"/></td>
			<td><%=idx%></td>
			<td style="text-align:left;padding-left:5px;"><strong class="small tb"><%=siteConfig.getDescription()%><br/>&nbsp;&gt;<%=categoryConfig.getDescription()%></strong></td>
			<td><a href="javascript:scraping('GETALL', '<%=siteId%>', '<%=category%>');"  class="btn_s">실행</a></td>
			<td><a href="javascript:scraping('GETNEW', '<%=siteId%>', '<%=category%>');" class="btn_s">실행</a></td>
			<td><a href="javascript:clearCategory('<%=siteId%>','<%=category%>')" class="btn_s">초기화</a></td>
			<%
			if (IndexingCount == 0 )
				{
				%>
				<td id='<%=siteId%>_<%=category%>_status'></td>	
				<td id="<%=siteId%>_<%=category%>_docSize"> </td>
				<td id="<%=siteId%>_<%=category%>_schedule"></td>
				<td id="<%=siteId%>_<%=category%>_stime"></td>
				<td id="<%=siteId%>_<%=category%>_dtime"></td>
				<%
				}
				else
				{
				out.print("<td id='"+siteId+"_"+category+"_status'");
			   FullIndexingResult.select(siteId, category);
				if(FullIndexingResult.status == ScrapingResult.STATUS_FAIL) 
					out.print(" class=warning>실패</td>");
				else if ( FullIndexingResult.status == ScrapingResult.STATUS_RUNNING)
					out.print(" class=running>수집중</td>");
				else if(FullIndexingResult.status == ScrapingResult.STATUS_SUCCESS)				
					out.print(" class=success>성공</td>");				 
			%>			
			<td id="<%=siteId%>_<%=category%>_docSize"><%=FullIndexingResult.docSize%></td>
			<td id="<%=siteId%>_<%=category%>_schedule"><%=FullIndexingResult.isScheduled ? "자동" : "수동" %></td>
			<td id="<%=siteId%>_<%=category%>_stime"><%=FullIndexingResult.startTime%></td>
			<td id="<%=siteId%>_<%=category%>_dtime"><%=Formatter.getFormatTime((long)FullIndexingResult.duration)%></td>			
			
		</tr>
<%
	      }
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
