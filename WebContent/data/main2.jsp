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
<%@page import="java.net.URLDecoder"%>

<%@include file="../common.jsp" %>

<%
	int pgSize = 10;
	int counterWidth = 10;
	String site = request.getParameter("site");
	String pg = request.getParameter("pg");

	CatbotServerConfig irConfig = CatbotSettings.getConfig(true);
	String siteListStr = irConfig.getString("site.list");
	String[] siteList = siteListStr.split(",");
	
	if(site == null){
		site = siteList[0];
	}
	if(pg == null){
		pg = "1";
	}
	
	FetchDB fetchDB = DBHandler.getInstance().getFetchDB();
	ScrapingDB scrapingData = fetchDB.getScrapingDB(site);
	int totalCount = scrapingData.getTotalCount();
	int maxPage = totalCount / pgSize;
	if(totalCount % pgSize > 0) 
		maxPage++;
	
	int ipg = 1;
	try{
		ipg = Integer.parseInt(pg);
	}catch(Exception e){ }
	
	int startRow = (ipg - 1) * pgSize; 
	
	List<Map<String, String>> list = scrapingData.listData(ipg, pgSize);
	List<String> fieldNameList = scrapingData.getFieldNameList();
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
<script src="<%=CATBOT_MANAGE_ROOT%>js/jquery-1.4.4.min.js"></script>
<script src="<%=CATBOT_MANAGE_ROOT%>js/jquery-ui-1.8.9.min.js"></script>
<script src="<%=CATBOT_MANAGE_ROOT%>js/jquery.cookie.js"></script>
<script type="text/javascript" src="<%=CATBOT_MANAGE_ROOT%>js/tree.jquery.js"></script>
<link type="text/css" rel="stylesheet" href="<%=CATBOT_MANAGE_ROOT%>css/jqtree.css"/>

</head>
<body>

<div id="container">
<!-- header -->
<%@include file="../header.jsp" %>

<div id="sidebar">
<div class="sidebox" id="tree1">
		<h3>사이트 리스트</h3>
		
</div>
</div><!-- E : #sidebar -->
<Script>
var data = [
            {
                label: 'node1',
                children: [
                    { label: 'child1' },
                    { label: 'child2' }
                ]
            },
            {
                label: 'node2',
                children: [
                    { label: 'child3' }
                ]
            }
        ];
        $('#tree1').tree({
        	data: data
        	,saveState: true
        	,autoOpen: true	
        });
        
        $('#tree1').bind(
       	    'tree.click',
       	    function(event) {
       	        // The clicked node is 'event.node'
       	        var node = event.node;
       	        alert(node.name);
       	    }
       	);
</Script>

<div id="mainContent">
<h2>수집데이터</h2>

<h4>데이터조회</h4>
<span>총 <%=totalCount %>개중 <%=pg %>페이지 / <%=maxPage %></span>
<div class="fbox">
<table class="tbl01">
	<colgroup><col width="60" /><col width="100" /><col width="*" /></colgroup>
	<tbody>
	<%
	for(int i=0; i<list.size(); i++){
		Map<String, String> map = list.get(i);
		int fieldSize = fieldNameList.size() - 1;
		for(int k=1; k<fieldNameList.size(); k++){
			
			String key = fieldNameList.get(k);
			String value = map.get(key);
		
	%>
		<tr>
		<% if(k==1){ %>
		<th rowspan="<%=fieldSize%>"><%=map.get("id") %></th>
		<% } %>
		<td><%=key %></td>
		<td style="text-align:left;padding-left:30px;">
			<%=value %>
		</td>
		</tr>
	<%
		}
	}
	%>
	
	</tbody>
</table>
</div>
	
<p class="clear"></p>
	<div class="list_bottom">
		<div id="paging" class="fl">
		<%
		int counterStart = ((ipg - 1) / counterWidth) * counterWidth + 1;
		int counterEnd = counterStart + counterWidth;
			
		if(ipg != 1){
		    out.println("<span class='num'><a href='?site="+site+"&pg=1'>처음</a></span>");
		}
		
		int prevStart = counterStart - counterWidth;
		if(prevStart > 0){
		    out.println("<span class='num'><a href='?site="+site+"&pg="+prevStart+"'>이전</a></span>");
		}
		for(int c = counterStart; c < counterEnd; c++){
			if(c <= maxPage){
				if(c == ipg){
					out.println("<span class='num'><a href='?site="+site+"&pg="+c+"' class='selected'>"+c+"</a></span>");
				}else{
					out.println("<span class='num'><a href='?site="+site+"&pg="+c+"'>"+c+"</a></span>");
				}
			}else{
				break;
			}
		}
		
		int nextPage = counterStart + counterWidth;
		if(nextPage <= maxPage){ 
		    out.println("<span class='num'><a href=?site="+site+"&pg="+nextPage+">다음</a></span>");
		}

		if(ipg < maxPage){
		    out.println("<span class='num'><a href=?site="+site+"&pg="+maxPage+">마지막</a></span>");
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
