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
<%@ page contentType="text/html; charset=UTF-8"%> 

<%@page import="com.websqrd.libs.common.Formatter"%>
<%@page import="com.websqrd.catbot.setting.*"%>
<%@page import="com.websqrd.catbot.db.*" %>

<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.*"%>
<%@page import="java.util.Date"%>
<%@page import="java.util.Calendar"%>
<%@page import="java.net.URLEncoder"%>
<%@page import="java.net.URLDecoder"%>
<%@page import="org.jsoup.Jsoup"%>
<%@page import="org.jsoup.safety.Whitelist"%>
<%@page import="com.websqrd.catbot.web.WebUtils"%>
<%@page import="org.json.*"%>
 
<%@include file="../common.jsp" %>

<%
	int pgSize = 10;
	int counterWidth = 10;
	String site = request.getParameter("site");
	String category = request.getParameter("category");
	//For
	String pg = request.getParameter("pg");

	String message = URLDecoder.decode(WebUtils.getString(request.getParameter("message"), ""),"utf-8");
	boolean settingReload = WebUtils.getBoolean(request.getParameter("settingReload"), false); 
	CatbotConfig catbotConfig = CatbotSettings.getGlobalConfig(settingReload);
	List<String> siteList = catbotConfig.getSiteList();

	//지정된 사이트가 없다면 기본적인 사이트를 지정한다.
	if(site == null){
		site = siteList.get(0);
	}

	//지정된 카테고리가 없다면 기본적인 카테고리를 선택하도록 한다.
	if(category == null){
		Map<String, CategoryConfig> map = CatbotSettings.getSiteCategoryConfigList(site,true);

		Iterator<CategoryConfig> it = map.values().iterator();
		if (it.hasNext()) {   
	         CategoryConfig cateConfig = it.next();
	         category= cateConfig.getCategoryName();
	    }
	}
	if(pg == null) {
		pg = "1";
	}
	
	SiteConfig siteConfig = CatbotSettings.getSiteConfig(site, true);
	String dataHandlerName = siteConfig.getDataHandler();
	DataHandlerConfig dataHandlerConfig = CatbotSettings.getDataHandlerConfig(dataHandlerName, true);
	List<String> idFieldNames = null;
	if ( dataHandlerConfig != null ) {
 		idFieldNames = dataHandlerConfig.getIdFieldNames();	
	}
 	JSONObject jsonFields = new JSONObject();
 	
 	if ( idFieldNames != null ) {
	 	for ( int i = 0 ; i < idFieldNames.size() ; i ++ ) {
	 		jsonFields.append("fields", idFieldNames.get(i));
	 	}
 	}
	
	DBHandler dbHandler = DBHandler.getInstance();
	CategoryConfig categoryConfig = CatbotSettings.getCategoryConfig(site, category);
	List<Page> pageList = categoryConfig.getPageList();
	Page p = null;
	String url = "";
	if(pageList != null){
		p = pageList.get(0); 
		url = p.getUrl().replaceAll("\\$\\{i\\}", p.getFrom()+"");
	}
	
	ScrapingDAO scrapingDAO = RepositoryHandler.getInstance().getScrapingDAO(siteConfig.getDataHandler());
	if(scrapingDAO == null){
		out.println("Repository에 연결할수 없습니다.");
		return;
	}
	Map<String,String> data = new HashMap<String,String>();
	data.put("site", siteConfig.getSiteName());
	data.put("category", categoryConfig.getCategoryName());
	
	int totalCount = 0;
	
	try {
		totalCount = scrapingDAO.getTotalCount(data);
	} catch ( Exception e) {
		e.printStackTrace();
		totalCount = 0;
	}
	
	int maxPage = totalCount / pgSize;
	
	if(totalCount % pgSize > 0) {
		maxPage++;
	}
	
	int ipg = 1;
	try{
		ipg = Integer.parseInt(pg);
	} catch(Exception e){ }
	
	int listSequenceStart = totalCount - ((ipg - 1) * pgSize);
	int startRow = (ipg - 1) * pgSize; 
	
	List<Map<String, String>> list =null;
	try {
		list = scrapingDAO.listData(ipg, pgSize, data);
	} catch ( Exception e ) {
		out.println(e.toString());
		list = null;
	}
	scrapingDAO.close();
	List<ShowField> fieldNameList = null;
			
	try {
		fieldNameList = scrapingDAO.getFieldNameList();
	} catch ( Exception e) {
		fieldNameList = null;
	}
			
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
<script type="text/javascript">

	function deleteData(site, category, id){
		var r=confirm("데이터를 삭제하시겠습니까?");
		if(r){
			window.location = "dataService.jsp?cmd=0&site="+site+"&category="+category+"&id="+id;
		}
	}
	
	function changeIncInfoData(site, category, infoId){
		var r=confirm("수집아이디를 '"+infoId+"'로 수정합니다.");
		if(r){
			window.location = "dataService.jsp?cmd=3&site="+site+"&category="+category+"&infoId="+infoId;
		}
	}
	
	function initData(site,category){
		var r=confirm("데이터를 초기화 하시겠습니까?");
		if(r){
			window.location = "dataService.jsp?cmd=2&site="+site+"&category="+category;
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
			<% for(int i = 0; i < siteList.size(); i++){ %>
				<% String siteName = siteList.get(i); %>
				<% SiteConfig sConfig = CatbotSettings.getSiteConfig(siteName); %>
				<% Map<String, CategoryConfig> map = CatbotSettings.getSiteCategoryConfigList(siteName); %>
				<% Iterator<CategoryConfig> it = map.values().iterator(); %>
				<li <%=siteName.equals(site) ? "class='selected'" : "" %>>
				<%=sConfig.getDescription() %>
				<% while (it.hasNext()) { %>
			        <% CategoryConfig cateConfig = it.next(); %>
			        <% String cateName= cateConfig.getCategoryName(); %>
					<div><a href="?site=<%=siteName %>&category=<%=cateName%>">&gt;<%=cateConfig.getDescription() %></a></div>
				<% } %>
				</li>
			<% } %>
			</ul>
	</div>
</div><!-- E : #sidebar -->

<div id="mainContent">
<h2>수집데이터</h2>
<p>* <%=siteConfig.getDescription()%> &gt; <a href="<%=url%>" target="_viewRealPage"><%=categoryConfig.getDescription()%>(<%=categoryConfig.getCategoryName()%>)</a></p>
<div id="btnBox2">
<a style="" href="javascript:initData('<%=site%>','<%=category%>');" class="btn_s">데이터초기화</a>
</div>
<div>총 <%=totalCount %>개중 <%=pg %>페이지 / <%=maxPage %> </div>
<div class="fbox">
<% if ( fieldNameList == null  ) { %>
	field name is null
<% } %>
<% if ( list == null ) { %>
	list is null
<% } %>
<table class="tbl01">
	<colgroup><col width="60" /><col width="100" /><col width="*" /><col width="100" /></colgroup>
	<tbody>
	<% HashMap<String,String> pair = new HashMap<String,String>(); %>
	
	<% //JSONObject kvObj = new JSONObject(); %>
	
	<% for(int i=0; i<list.size(); i++) { %>
		<% Map<String, String> map = list.get(i); %>
		<% int fieldSize = fieldNameList.size(); %>
		<% if ( fieldNameList != null && list != null ) { %>
			<% Iterator<String> itr = idFieldNames.iterator(); %>
			<% JSONStringer jstringer = new JSONStringer();	 %>
			<% jstringer.object(); %>
			<% if ( itr != null ) { %>
				<% while ( itr.hasNext() ) { %>
					<% //JSONObject element = new JSONObject(); %>
					<% String key = (String)itr.next(); %>
					<% String value = map.get(key); %>
					<% jstringer.key(key).value(value); %>
				<% } %>
			<% } %>
			<% jstringer.endObject(); %>
			
			<% for(int k=0; k<fieldNameList.size(); k++) { %>
				<% ShowField showField = fieldNameList.get(k); %>
				<% String key = showField.getValue(); %>
				<% String value = map.get(key); %>
				<% String idUpdateControl = ""; %>
				<% if ( idFieldNames.contains(key) ) { %>
					<% idUpdateControl = "";//"<a href=\"javascript:changeIncInfoData('"+site+"','"+category+"','"+jstringer.toString().replaceAll("\"","&quot;")+"')\">업데이트</a>"; %>
				<% } %>
				<% String caption = showField.getCaption(); %>
				<% if(value == null) { %>
					<% value = "(NULL)"; %>
				<% } else { %>
					<% Whitelist whitelist = new Whitelist(); %>
					<% whitelist.addTags("br").addTags("p"); %>
					<% value = Jsoup.clean(value, whitelist); %>
				<% } %>
				<tr>
					<% if(k==0){ %>
					<th rowspan="<%=fieldSize%>"><%=listSequenceStart - i %></th>
					<% }%>
					<td><%=caption %></td>
					<% if(value.length() > 300 || value.contains("<img") ||  value.contains("<table") ){ %>
					<td style="text-align:left;padding-left:30px;">
						<div style="width:100%; height:200px; overflow:scroll;">
						<%=value.replaceAll("\r\n", "<br/>").replaceAll("[\n\r]", "<br/>") %>
						</div>
					</td>
					<% } else { %>
						<td style="text-align:left;padding-left:30px;"><%=value.replaceAll("\r\n", "<br/>").replaceAll("[\n\r]", "<br/>") %> <%=idUpdateControl %></td>
					<%  } %>
					<% if(k==0){ %>
						<% String encodedId = URLEncoder.encode(jstringer.toString(),"UTF-8");%>		
						<td rowspan="<%=fieldSize%>"><a href="javascript:deleteData('<%=site%>','<%=category%>','<%=encodedId%>');" class="btn_s">삭제</a><br/><br/><a href="dataEditor.jsp?site=<%=site%>&category=<%=category%>&id=<%=encodedId%>" class="btn_s">편집</a></td>
					<% } %>
				</tr>
			<% } %>
		<% } %>
	<% } %>
	</tbody>
</table>
</div>
	
<p class="clear"></p>
	<div class="list_bottom">
		<div id="paging" class="fl">
		<% int counterStart = ((ipg - 1) / counterWidth) * counterWidth + 1; %>
		<% int counterEnd = counterStart + counterWidth; %>
			
		<% if(ipg != 1){ %>
		    <span class="num"><a href="?site=<%=site%>&category=<%=category%>&pg=1">처음</a></span>
		<% } %>
		
		<% int prevStart = counterStart - counterWidth; %>
		<% if(prevStart > 0){ %>
		    <span class="num"><a href="?site=<%=site%>&category=<%=category%>&pg=<%=prevStart%>">이전</a></span>
		<% } %>
		<% for(int c = counterStart; c < counterEnd && c <=maxPage; c++){ %>
			<% if(c <= maxPage){ %>
				<% if(c == ipg){ %>
				    <span class="num"><a href="?site=<%=site%>&category=<%=category%>&pg=<%=c%>" class="selected"><%=c%></a></span>
				<% }else{ %>
				    <span class="num"><a href="?site=<%=site%>&category=<%=category%>&pg=<%=c%>"><%=c%></a></span>
				<% } %>
			<% } %>
		<% } %>
		
		<% int nextPage = counterStart + counterWidth; %>
		<% if(nextPage <= maxPage){  %>
		    <span class="num"><a href="?site=<%=site%>&category=<%=category%>&pg=<%=nextPage%>">다음</a></span>
		<% } %>

		<% if(ipg < maxPage){ %>
		    <span class="num"><a href="?site=<%=site%>&category=<%=category%>&pg=<%=maxPage%>">마지막</a></span>
		<% } %>
		</div>
	</div>
<!-- E : #mainContent --></div>
<!-- footer -->
<%@include file="../footer.jsp" %>
</div><!-- //E : #container -->
</body>
</html>
