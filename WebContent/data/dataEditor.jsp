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
<%@page import="com.websqrd.catbot.db.DBHandler"%>
<%@page import="com.websqrd.libs.common.Formatter"%>
<%@page import="com.websqrd.catbot.setting.*"%>
<%@page import="com.websqrd.catbot.db.*" %>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.*"%>
<%@page import="java.util.Date"%>
<%@page import="java.util.Calendar"%>
<%@page import="java.net.URLDecoder"%>
<%@page import="java.net.URLEncoder"%>
<%@page import="org.json.*"%>
<%@page contentType="text/html; charset=UTF-8"%> 
<%@include file="../common.jsp" %>
<%
	int id = -1;
	String site = request.getParameter("site");
	String category = request.getParameter("category");
	String idStr =URLDecoder.decode(request.getParameter("id"), "UTF-8");
		
	JSONObject idObj = new JSONObject(idStr);
	
	String message = URLDecoder.decode(WebUtils.getString(request.getParameter("message"), ""),"utf-8");
	boolean settingReload = WebUtils.getBoolean(request.getParameter("settingReload"), false); 
	CatbotConfig catbotConfig = CatbotSettings.getGlobalConfig(settingReload);
	
	List<String> siteList = catbotConfig.getSiteList();
	DBHandler dbHandler = DBHandler.getInstance();
	
	SiteConfig siteConfig =  CatbotSettings.getSiteConfig(site);
	CategoryConfig categoryConfig = CatbotSettings.getCategoryConfig(site, category);
	String dataHandlerName = siteConfig.getDataHandler();
	
	DataHandlerConfig dataHandlerConfig = CatbotSettings.getDataHandlerConfig(dataHandlerName, true);
	
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
<script type="text/javascript" src="<%=CATBOT_MANAGE_ROOT%>js/jquery-1.4.4.min.js"></script>
<script type="text/javascript">

	function deleteData(site, id){
		
	}
	
	function editData(site, id){
		
	}
	function saveData(){
		var r=confirm("수정된 데이터를 저장하시겠습니까?");
		if(r){		
			document.saveForm.submit();	
		}
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
			<% int siteSize = siteList.size(); %>
			<% for(int i = 0; i < siteSize; i++){ %>
			<li><a href="main.jsp?site=<%=siteList.get(i) %>"><%=siteList.get(i) %></a></li>
			<% } %>
			</ul>
	</div>
</div><!-- E : #sidebar -->

<div id="mainContent">
<h2>수집데이터</h2>

<h4>데이터수정</h4>
<span>사이트 <%=site %></span>
<div class="fbox">
<form action="dataService.jsp" method="post" name="saveForm" style="padding:0;margin:0;">
<table class="tbl01">
	<colgroup><col width="100" /><col width="*" /></colgroup>
	<tbody>
	<% String idFieldName = ""; %>
	<% String idFieldValue = ""; %>
	<% ScrapingDAO scrapingDAO = RepositoryHandler.getInstance().getScrapingDAO(siteConfig.getDataHandler()); %>
	<% if(scrapingDAO != null){ %>
		<% List<ShowField> fieldNameList = scrapingDAO.getFieldNameList(); %>
		<% Map<String, String> data = new HashMap<String, String>(); %>
	
		<% if ( dataHandlerConfig != null ) { %>
			<% String[] selectByIdSqlFields = dataHandlerConfig.getSelectByIdSqlFields(); %>
			<% if ( selectByIdSqlFields == null ) { %>
				<%="selectByIdSqlFields is null" %>
			<% return; } %>
			
			
			<% if ( selectByIdSqlFields.length < 1) { %>
				<%="selectByIdSqlFields length is less than 1" %>
			<% return; } %>
			<% data.put(selectByIdSqlFields[0], site); %>
			<% data.put(selectByIdSqlFields[1], category); %>
			<% if ( idObj != null ) { %>
				<% Iterator<String>iter = idObj.keys(); %>
				<% while(iter.hasNext()) { %>
					<% String key = iter.next(); %>
					<% String value = idObj.getString(key); %>
					<% data.put(key, value); %>
				<% } %>
			<% } %>
		<% } %>	
		

		<% Map<String, String> map = scrapingDAO.getData(data); %>	
		
		<% int fieldSize = fieldNameList.size(); %>
		<% String readOnly = ""; %>
		<% for(int k=0; k<fieldSize; k++){ %>
			<% String caption = fieldNameList.get(k).getCaption(); %>
			<% if ( "".equals(caption) ) %>
			<%  caption =  fieldNameList.get(k).getValue(); %>
			<% if(fieldNameList.get(k).isID()){ %>										
				<% readOnly = "readOnly='ReadOnly'"; %>
			<% }else { %>
				<% readOnly = ""; %>
			<% } %>
		<tr>
		<td><%=caption %></td>
		<td style="text-align:left;padding-left:30px;">
			<textarea name="<%= fieldNameList.get(k).getValue() %>" id="<%= fieldNameList.get(k).getValue() %>" style="width:520px;height:50px;" <%=readOnly %>><%=map.get(fieldNameList.get(k).getValue()) %></textarea>
		</td>
		</tr>
		<% }//for %>
		<% scrapingDAO.close(); %>
	<% } %>
	</tbody>
</table>
<input type="hidden" id="<%=idFieldName%>" name="<%=idFieldName%>" value="<%=idFieldValue %>" />
<input type="hidden" id="cmd" name="cmd" value="1" />
<input type="hidden" id="site" name="site" value="<%=site%>" />
<input type="hidden" id="category" name="category" value="<%=category%>" />
<input type="hidden" id="id" name="id" value="<%=idStr.replaceAll("\"","&quot;")%>" />
</form>
</div>
<div id="btnBox">
	<a class="btn" onclick="javascript:saveData();">저장</a>
	<a class="btn" href="main.jsp?site=<%=site%>">취소</a>
</div>
<p class="clear"></p>
<!-- E : #mainContent --></div>
<!-- footer -->
<%@include file="../footer.jsp" %>
</div><!-- //E : #container -->
</body>
</html>
