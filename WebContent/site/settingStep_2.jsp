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
	CatbotServerConfig irConfig = CatbotSettings.getConfig(true);
	//String siteListStr = irConfig.getString("site.list");
	//String[] siteList = siteListStr.split(",");
	
	String site = request.getParameter("site");
	String url = request.getParameter("url");
	String enc = request.getParameter("enc");
	String source = WebUtils.getContentByURL(url,"euc-kr");
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
	var siteStr = "<%=site%>";
	
	function fillMainSource(url){
		
		
	}
	
	function reset(){
		var url = encodeURIComponent($("#url").val());
		//location.href="?site="+siteStr+"&cmd=2&enc="+enc+"&url="+url;
		location.href="settingStep_2.jsp?site="+siteStr+"&url="+url;
		return true;
	}
	
	function start(){
		var url = encodeURIComponent($("#url").val());
		//location.href="?site="+siteStr+"&cmd=2&enc="+enc+"&url="+url;
		location.href="settingStep_3.jsp?site="+siteStr+"&url="+url;
		return true;
	}
	
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
	<h2>사이트 설정</h2><br/>
	사이트명 : <%=site%><br/>
	인코딩 : <%=enc%><br/>
	주소 : <textarea cols="75" rows="3"><%=url%></textarea> <a href="javascript:reset()" class="btn_s">재로딩</a>
	<br/><br/>
	
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
		<h3 style="color:#663366">작업본</h3>
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
		<table summary="config 설정" class="tbl08">
		<colgroup><col width="13%" /><col width="" /></colgroup>
		<tbody>
		<tr>
			<td><textarea style="width:720px;height:400px;"><%=source%></textarea></td>
		</tr>
		</tbody>
		</table>
		</div>
		
<!-- config -->
		<div id="btnBox">
		<a href="javascript:start()" class="btn">셋팅시작</a>
		</div>
	<%}%>
	
	
	<!-- E : #mainContent --></div>
	<!-- footer -->
<%@include file="../footer.jsp" %>
</div><!-- //E : #container -->
</body>
</html>
