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

<%@page import="java.util.ArrayList"%>
<%@page import="com.websqrd.catbot.setting.Process"%>
<%@ page contentType="text/html; charset=UTF-8"%> 

<%@page import="com.websqrd.libs.common.Formatter"%>
<%@page import="com.websqrd.catbot.web.WebUtils"%>
<%@page import="com.websqrd.catbot.setting.CatbotSettings"%>
<%@page import="com.websqrd.catbot.setting.CatbotServerConfig"%>
<%@page import="com.websqrd.catbot.setting.SiteConfig"%>
<%@page import="com.websqrd.catbot.exception.CatbotException"%>
<%@page import="com.websqrd.catbot.setting.*"%>
<%@page import="com.websqrd.libs.web.HttpUtils"%>

<%@page import="java.util.Date"%>
<%@page import="java.util.Calendar"%>
<%@page import="java.util.List"%>

<%@include file="../common.jsp" %>

<%
	String message = WebUtils.getString(request.getParameter("message"), "");

	//BoardService irService = BoardService.getInstance();
	//String[] colNames = irService.getCollectionNames();
 	//String[][] indexList = irService.getTokenizers();
 	CatbotServerConfig irConfig = CatbotSettings.getConfig(true);
	String siteListStr = irConfig.getString("site.list");
	String[] siteList = siteListStr.split(",");
	
	String site = request.getParameter("site");
	String url = request.getParameter("url");
	String enc = request.getParameter("enc");
	if(enc == null)
		enc = "euc-kr";
	//String source = WebUtils.getContentByURL(url, enc);
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
<script src="<%=CATBOT_MANAGE_ROOT%>js/page.js" type="text/javascript"></script> 
<script type="text/javascript">
	$(document).ready(function() {
		url = "<%=url%>";
		enc = "<%=enc%>";
		fillSource(url, enc);
		$("body").click(function(e){
			$('#testArea').hide();
		});
		$(".testBtn").click(function(e) {
			var w = $(window);
			var divTop =  w.scrollTop() + e.clientY - 50 - 300; //상단 좌표
			var divLeft = w.scrollLeft() + e.clientX - 50 - 600; //좌측 좌표
			$('#testArea').css({
				"top": divTop
				,"left": divLeft
				, "position": "absolute"
			}).show();
			e.stopPropagation();
		});
		$("#testArea").click(function(e) {
			//테스트코드 클릭시 창 닫힘방지.
			e.stopPropagation();
		});
	});
	

</script>
</head>

<body>
<div id="container">
<!-- header -->
<%@include file="../header.jsp" %>

<div id="sidebar">
	<div class="sidebox">
		<h3>사이트</h3>
			<ul class="latest">
			<li><a href="<%=CATBOT_MANAGE_ROOT%>site/main.jsp">사이트관리</a></li>
			<li><a href="<%=CATBOT_MANAGE_ROOT%>site/schema.jsp" class="selected">사이트설정</a></li>
			</ul>
	</div>
	<div class="sidebox">
		<h3>사이트</h3>
			<ul class="latest">
			<li>사이트에 대한 정보입니다.</li>
			</ul>
	</div>
</div><!-- E : #sidebar -->

<div id="mainContentLong">
		<h2>사이트설정</h2>
		<div class="fbox">
		<table summary="사이트설정" class="tbl01">
		<colgroup><col width="150" /><col width="*" /></colgroup>
		<tbody>
		<tr>
			<th>사이트명</th><td class="r"><%=site%></td>
		</tr>
		<tr>
			<th>인코딩</th><td class="r"><%=enc%>
			<input type="hidden" id="enc" value="<%=enc%>"/>
			</td>
		</tr>
		<tr>
			<th>주소</th><td class="r"><textarea class="full" rows="3" name="url" id="url"><%=url%></textarea></td>
		</tr>
		<tr>
			<th> </th><td><a href="javascript:fillSource()" class="btn_s">재로딩</a></td>
		</tr>
		<tr>
			<td colspan="2">
				총 <span id="sourceAreaCount">1</span>개의 요소
				<br/>
				<textarea id="sourceAreaCode" class="full" rows="20">this is default.</textarea>
			</td>
		</tr>
		</tbody>
		</table>
		</div>
	
	
	<%
				String errorMsg = null;
			%>
	<%
		SiteConfig workingSchema = null;
	%>
	<% try{ %>
		<% workingSchema = CatbotSettings.getSchema(site,true); %>
	<% }catch(CatbotException e){ %>
		<% errorMsg = e.getMessage(); %>
	<% } %>
	
	<% if(errorMsg != null){ %>
		<div class="fbox" id="hiddenDiv">
		<table summary="스키마 설정" class="tbl02">
		<tbody>
		<tr><td class="first">스키마에러 : <%=errorMsg %></td></tr>
		</tbody>
		</table>
		</div>
		
	<% } %>
	<% if(workingSchema != null){ %>
		<% PageList pageList = workingSchema.getPageList(); %>
		<% CatbotConfig conf = workingSchema.getConfigSetting(); %>
		<% Process process = workingSchema.getProcess(); %>
<!-- config -->
		
		<div class="fbox">
		<form action="" method="get" >
			<table summary="config 설정" class="tbl02">
			<colgroup><col width="10%" /><col width="80px" /><col width="120px" /><col width="*" /><col width="180px" /></colgroup>
			<thead>
				<tr>
					<th class="first">Depth</th>
					<th>유형</th>
					<th>이름</th>
					<th>XPATH</th>
					<th>동작</th>
				</tr>
			</thead>
			<tbody>
			<tr id="rootTR">
				<td class="first">0</td>
				<td>최상위노드</td>
				<td>-</td>
				<td>
					<textarea class="full"id="rootpath" name="rootpath" rows="2">//div</textarea>
				</td>
				<td>
					<a href="javascript:testXPath();" class="btn_edit testBtn">테스트</a>
					<a href="javascript:stepInto()" class="btn_edit">Step Into</a>
					<a href="javascript:addField($('#rootTR'))" class="btn_edit">하위추가</a><br/>
				</td>
			</tr>
			</tbody>
			</table>
			
			<div id="btnBox">
			<a href="javascript:saveAndNext()" class="btn">저장하고 다음단계로 >></a>
			</div>
		</form>
		</div>
		
		<div id="testArea">
			총 <span id="testAreaCount">0</span>개의 요소
			<br/>
			<textarea id="testAreaCode" style="width:100%; height:270px;" readonly="readonly"></textarea>
			<br/>
			<a href="javascript:closeTestArea()">close</a>
		</div>
	<%}%>
	
</div>
<!-- E : #mainContent -->

<!-- footer -->
<%@include file="../footer.jsp" %>
</div><!-- //E : #container -->
</body>
</html>
