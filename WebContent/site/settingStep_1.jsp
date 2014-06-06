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


<%@page import="java.util.Date"%>
<%@page import="java.util.Calendar"%>
<%@page import="java.util.List"%>

<%@include file="../common.jsp" %>

<%
	//BoardService irService = BoardService.getInstance();
	//String[] colNames = irService.getCollectionNames();
 	//String[][] indexList = irService.getTokenizers();
 	CatbotServerConfig irConfig = CatbotSettings.getConfig(true);
	String siteListStr = irConfig.getString("site.list");
	String[] siteList = siteListStr.split(",");
	
	String site = request.getParameter("site");
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
	
	
	function selectSite(dropdown){
		var myindex  = dropdown.selectedIndex
	    var selValue = dropdown.options[myindex].value
		location.href="?site="+selValue;
		return true;
	}
	
	function saveAndNext(){
		//var enc = $("#enc").val();
		//var url = encodeURIComponent($("#url").val());
		//location.href="?site="+siteStr+"&cmd=2&enc="+enc+"&url="+url;
		//location.href="settingStep_2.jsp?site="+siteStr+"&url="+url;
		$("#settingForm").submit();
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

<div id="mainContent">
	<h2>사이트 설정</h2><br>
	사이트명 :
	<select id="chooseCollection" onchange="javascript:selectSite(this)">
	<%
		for(int i = 0;i < siteList.length;i++){
		String col = siteList[i];
		if(site == null){
			if(i == 0){
		site = col;
			}
		}
	%>
	<option value="<%=col%>" <%=col.equals(site) ? "selected" : ""%> ><%=col%></option>
	<%
		}
	%>
	</select>
	
	<br/><br/>
	
	<%
			String errorMsg = null;
			SiteConfig schema = null;
			try{
				schema = CatbotSettings.getSchema(site,true);
			}catch(CatbotException e){
				errorMsg = e.getMessage();
			}
		%>
	
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
	<% if(schema != null){
		PageList pageList = schema.getPageList();
		Page pp = null;
		if(pageList != null){
			pp = pageList.getList().get(0);
		}
		/* CatbotConfig conf = schema.getConfigSetting();
		Process process = schema.getProcess();  */
	%>
<!-- config -->		
		<div class="fbox">
		<form id="settingForm" action="settingStep_3.jsp" method="get" >
		<table summary="config 설정" class="tbl01">
		<colgroup><col width="23%" /><col width="" /></colgroup>
		<tbody>
		<tr>
			<th>인코딩</th>
			<td style="text-align:left;"><input class="inp02" type="text" id="enc" name="enc" value="<%=pp!=null ? pp.getEncoding() : "" %>" /></td>
		</tr>
		<tr>
			<th>URL</th>
			<td style="text-align:left;"><input class="inp_full" type="text" id="url" name="url" value="<%=pp!=null ? pp.getUrl() : "" %>" /></td>
		</tr>
		</tbody>
		</table>
		<input type="hidden" name="site" value="<%=site %>" />
		</form>
		</div>
<!-- config -->

		<div id="btnBox">
		<a href="javascript:saveAndNext()" class="btn">다음 >></a>
		</div>
	<%}%>
	
	
	<!-- E : #mainContent --></div>
	<!-- footer -->
<%@include file="../footer.jsp" %>
</div><!-- //E : #container -->
</body>
</html>
