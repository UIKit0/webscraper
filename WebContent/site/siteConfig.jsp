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
<%@page import="com.websqrd.catbot.web.HttpUtils"%>
<%@page import="com.websqrd.catbot.setting.CatbotSettings"%>
<%@page import="com.websqrd.catbot.setting.CatbotServerConfig"%>
<%@page import="com.websqrd.catbot.setting.SiteConfig"%>
<%@page import="com.websqrd.catbot.exception.CatbotException"%>
<%@page import="com.websqrd.catbot.setting.*"%>

<%@page import="java.util.Date"%>
<%@page import="java.util.Calendar"%>
<%@page import="java.util.*"%>

<%@include file="../common.jsp" %>

<%
	String message = WebUtils.getString(request.getParameter("message"), "");

	CatbotConfig catbotConfig = CatbotSettings.getGlobalConfig();
	List<String> siteList = catbotConfig.getSiteList();
	
	String site = request.getParameter("site");
	String action = request.getParameter("action");
	
	if("save".equals(action)){
		String setting = request.getParameter("setting");
		CatbotSettings.saveSiteConfigRaw(site, setting);
	}
	
	SiteConfig siteConfig = CatbotSettings.getSiteConfig(site, true);
	if(siteConfig == null)
		siteConfig = new SiteConfig(site, null);
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
<script src="<%=CATBOT_MANAGE_ROOT%>js/schema.js" type="text/javascript"></script> 
<script type="text/javascript">
	function alertMessage(){
		var message = "<%=message%>";
		if(message != "")
			alert(message);
	}
	
	function selectSite(dropdown){
		var myindex  = dropdown.selectedIndex
	    var selValue = dropdown.options[myindex].value
		location.href="?site="+selValue;
		return true;
	}

	function addField(){
		
		if(value != ""){
			location.href = "collectionService.jsp?cmd=4&collection=<%=site%>&field="+field;
		}
	}

	function removeField(){
		var x = document.getElementsByName("selectField");
		field = "";
		for(i=0;i<x.length;i++){
			if(x[i].checked){
				field = x[i].value;
				break;
			}
		}

        if(field == "")
            alert("삭제할 필드를 선택해주세요.");
        else
			location.href = "collectionService.jsp?cmd=5&collection=<%=site%>&field="+field;
		
	}
	$(document).delegate(
		'.textbox',
		'keydown',
		function(e) {
			var keyCode = e.keyCode || e.which;

			if (keyCode == 9) {
				e.preventDefault();
				var start = $(this).get(0).selectionStart;
				var end = $(this).get(0).selectionEnd;

				// set textarea value to: text before caret + tab + text
				// after caret
				$(this).val(
						$(this).val().substring(0, start) + "\t"
								+ $(this).val().substring(end));

				// put caret at right position again
				$(this).get(0).selectionStart = $(this).get(0).selectionEnd = start + 1;
			}
		}
	);
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
			<li <%=siteName.equals(site) ? "class='selected'" : "" %>>
				<a href= "siteConfig.jsp?site=<%=siteName %>"><%=sConfig != null ? sConfig.getDescription() : "null"%></a>
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
	<h2>사이트 설정</h2>
<%
	Map<String, CategoryConfig> categoryConfList = CatbotSettings.getSiteCategoryConfigList(site);
	Iterator<CategoryConfig> iter = categoryConfList.values().iterator();

%>
	<!-- <a href="main.jsp" class="btn_s"><< 사이트 목록으로</a><br/><br/> -->
	<p>* <%=siteConfig.getDescription()%></p>
	<br/>
	
	<h4>설정값</h4>
	<div class="fbox">
	<table summary="시스템상태" class="tbl01">
	<colgroup><col width="20%" /><col width="" /></colgroup>
	<tbody>
	<tr>
		<th>하위게시판 리스트</th>
		<td>
		<%
		while(iter.hasNext()){
			CategoryConfig categoryConfig = iter.next();
			%>
			<a href="categoryConfig.jsp?site=<%=site %>&category=<%=categoryConfig.getCategoryName() %>"><u><%=categoryConfig.getDescription() %></u></a> 
			<%
		}
		%>
		</td>
	</tr>
	<tr>
		<th>아이디</th>
		<td><%=siteConfig.getSiteName() %></td>
	</tr>
	<tr>
		<th>캐릭터셋</th>
		<td><%=siteConfig.getCharset() %></td>
	</tr>
	<tr>
		<th>데이터핸들러명</th>
		<td><%=siteConfig.getDataHandler() %></td>
	</tr>
	<tr>
		<th>로봇 에이전트명</th>
		<td><%=siteConfig.getAgent()!=null?siteConfig.getAgent():"" %></td>
	</tr>
	<tr>
		<th>로그인정보</th>
		<td><%
		if(siteConfig.getLogin()!= null){
			SiteLoginConfig loginConfig = siteConfig.getLogin();
			%>
			주소 : <%=loginConfig.getUrl()%>
			<%
			Map<String, String> map = loginConfig.getUrlParam();
			if(map != null){
			%>
			<br/><br/>파라미터 : <%=map %>	
			<%
			}
		}
		
		%></td>
	</tr>
	<tr>
		<th>사용자 설정값</th>
		<td><%=siteConfig.getProperties()%></td>
	</tr>
	</tbody>
	</table>
	</div>
	
	<h4>원본포맷</h4>
	<div><i>* &amp;는 반드시 &amp;amp;로 작성해야 합니다.</i></div>
	<form action="siteConfig.jsp" method="post">
		<input type="hidden" name="action" value="save" />
		<input type="hidden" name="site" value="<%=site %>" />
		<textarea name="setting" style="width:100%; height:300px;" class="textbox"><%=HttpUtils.getAmpEscaped(CatbotSettings.getSiteConfigRaw(site)) %></textarea>
		<br/><br/>
		<input type="submit" value="저장" class="btn"/>
	</form>
	<!-- E : #mainContent --></div>
	<!-- footer -->
<%@include file="../footer.jsp" %>
</div><!-- //E : #container -->
</body>
</html>
