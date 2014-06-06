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
<%@page import="com.websqrd.catbot.web.WebUtils"%>
<%@page import="com.websqrd.catbot.setting.CatbotSettings"%>
<%@page import="com.websqrd.catbot.setting.*"%>

<%@page import="java.util.*"%>
<%@page import="java.util.Calendar"%>
<%@page import="java.net.URLDecoder"%>

<%@include file="../common.jsp" %>

<%
	String message = URLDecoder.decode(WebUtils.getString(request.getParameter("message"), ""),"utf-8");
	boolean settingReload = WebUtils.getBoolean(request.getParameter("settingReload"), false); 
	CatbotConfig catbotConfig = CatbotSettings.getGlobalConfig(settingReload);
	List<String> siteList = catbotConfig.getSiteList();
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
				<a href= "siteConfig.jsp?site=<%=siteName %>"><%=sConfig != null?sConfig.getDescription():"" %></a>
				<% 
				 while (it.hasNext()) {   
			         Map.Entry entry = (Map.Entry) it.next();   
			         CategoryConfig cateConfig = (CategoryConfig)entry.getValue();   
			         String cateName= cateConfig.getCategoryName();
				%>
				<ul>
					<a href= "categoryConfig.jsp?site=<%=siteName %>&category=<%=cateName %>">&gt;<%=cateConfig.getDescription() %></a>
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
	<h2>사이트 정보</h2>
	<div><a href="?settingReload=true" class="btn_s">셋팅 재로딩</a></div><br/>
	<div class="fbox">
	<table summary="컬렉션 정보" class="tbl02">
	<colgroup><!--<col width="5%" />--><col width="4%" /><!-- <col width="4%" /> --><col width="10%" /><col width="8%" /><col width="8%" /><col width="8%" /><col width="8%" /><col width="8%" /><col width="7%" /><col width="8%" /><col width="8%" /></colgroup>
	<thead>
	<tr>
		<!--<th rowspan = "2" class="first">선택</th>-->
		<th rowspan = "2" class="first">번호</th>
		<!--
		<th rowspan="2">편집</th>
		-->
		<th rowspan="2">사이트명</th>
		<th rowspan="2">게시판리스트</th>
		<th rowspan="2">게시판수</th>
		<th rowspan="2">캐릭터셋</th>
		<th rowspan="2">데이터핸들러</th>
	</tr>
	</thead>
	<tbody>
	<%
	for(int i = 0;i<siteList.size();i++){
		String site = siteList.get(i);
		SiteConfig siteConfig = CatbotSettings.getSiteConfig(site, settingReload);
		if(siteConfig == null){
			siteConfig = new SiteConfig(site , null);
		}
		Map<String, CategoryConfig> categoryConfList = CatbotSettings.getSiteCategoryConfigList(site, settingReload);
		Iterator<CategoryConfig> iter = categoryConfList.values().iterator();
	%> 
	<tr>
		<!--<td class="first"><input type="radio" name="selectSite" value="<%=site%>" /></td>-->
		<td class="first"><%=i+1 %></td>
		<!-- 
		<td><a href="siteConfig.jsp?site=<%=site%>"><img src="../images/edit.gif"/></a></td>
		-->
		<td><a href="siteConfig.jsp?site=<%=site%>"><strong class="small tb"><u><%=siteConfig.getDescription() %></u></strong></a></td>
		<td>
		<%
		while(iter.hasNext()){
			CategoryConfig categoryConfig = iter.next();
			out.print("<a href='categoryConfig.jsp?site=");
			out.print(site);
			out.print("&category=");
			out.print(categoryConfig.getCategoryName());
			out.print("'><u>");
			out.print(categoryConfig.getDescription());
			out.print("</u></a>");
			out.print("<br/>");
		}
		%>
		</td>
		<td><%=categoryConfList.size() %></td>
		<td><%=siteConfig.getCharset() %></td>
		<td><%=siteConfig.getDataHandler()%></td>
	</tr>
<%
	}
%>
	</tbody>
	</table>
	</div>
<!-- 
	<div id="btnBox">
	<form id="siteForm" method="get" action="siteService.jsp">
	<input type="hidden" id="cmd" name="cmd" />
	<input type="text" id="site" name="site" class="inp02 required alphanumeric" size="20" minlength="2" maxlength="20"></input> 
	<a class="btn" onclick="javascript:addSite()">사이트추가</a>
	<a class="btn" onclick="javascript:removeSite()">사이트삭제</a>
	<div id="messageBox" style="width:400px; margin-left:220px; text-align: left;"> </div>
	</form>
	</div>
 -->
	<!-- E : #mainContent --></div>

<!-- footer -->
<%@include file="../footer.jsp" %>
	
</div><!-- //E : #container -->

</body>

</html>
