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

<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.Calendar"%>
<%@page contentType="text/html; charset=UTF-8"%>
<%@page import="com.websqrd.catbot.web.WebUtils"%>
<%@page import="java.net.URLConnection"%>
<%@page import="java.net.URL"%>
<%@page import="java.io.BufferedReader"%>
<%@page import="java.io.InputStreamReader"%>
<%@page import="com.websqrd.catbot.db.JobHistory"%>
<%@page import="com.websqrd.catbot.db.DBHandler"%>
<%@page import="com.websqrd.catbot.setting.*"%>
<%@page import="com.websqrd.catbot.db.*"%>
<%@page import="com.websqrd.libs.common.Formatter"%>
<%@page import="java.util.*"%>
<%@page import="org.json.*"%>
<%@page import="java.net.URLDecoder"%>
<%@include file="../common.jsp" %>
<%
	String cmd = request.getParameter("cmd");
	String type = request.getParameter("type");
	String site = WebUtils.getString(request.getParameter("site"), "");
	String category = WebUtils.getString(request.getParameter("category"), "");
	boolean hasDocs = WebUtils.getBoolean(request.getParameter("hasDocs"), true);

	int pg = WebUtils.getInt(request.getParameter("pg"), 1);
	int pgSize = 30;
	int counterWidth = 10;
	int startRow = (pg - 1) * pgSize;
	
	DBHandler dbHandler = DBHandler.getInstance();
	
	
	SiteConfig siteConfig = null;
	CategoryConfig categoryConfig = null;
	Map<String, CategoryConfig> siteCategoryConfigList = null;
	if(site != null){
		siteConfig = CatbotSettings.getSiteConfig(site);
		if(category != null){
			categoryConfig = CatbotSettings.getCategoryConfig(site, category);
		}
		siteCategoryConfigList = CatbotSettings.getSiteCategoryConfigList(site);
	}
	
	
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>웹수집기 관리도구</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<link href="<%=CATBOT_MANAGE_ROOT%>css/reset.css" rel="stylesheet" type="text/css" />
<link href="<%=CATBOT_MANAGE_ROOT%>css/style.css" rel="stylesheet" type="text/css" />
<link href="<%=CATBOT_MANAGE_ROOT%>css/jquery-ui.css" rel="stylesheet" type="text/css" media="screen" />
<script src="<%=CATBOT_MANAGE_ROOT%>js/amcharts/amcharts.js" type="text/javascript"></script>
<script src="<%=CATBOT_MANAGE_ROOT%>js/amcharts/raphael.js" type="text/javascript"></script>
<!--[if lte IE 6]>
<link href="<%=CATBOT_MANAGE_ROOT%>css/style_ie.css" rel="stylesheet" type="text/css" />
<![endif]-->
<script type="text/javascript" src="<%=CATBOT_MANAGE_ROOT%>js/jquery-1.4.4.min.js"></script>
<script type="text/javascript" src="<%=CATBOT_MANAGE_ROOT%>js/common.js"></script>
<script type="text/javascript" src="<%=CATBOT_MANAGE_ROOT%>js/jquery-ui-1.8.9.min.js"></script>
<script type="text/javascript" src="<%=CATBOT_MANAGE_ROOT%>js/calendar.js"></script>
<!-- <script type="text/javascript" src="<%=CATBOT_MANAGE_ROOT%>js/searchStat.js"></script>-->
<script>

	//$(document).ready(function(){
	//		if(typeTag == "hour"){
	//			$('#showType option:eq(0)').attr('selected', true);
	//		}else if(typeTag == "day"){
	//			$('#showType option:eq(1)').attr('selected', true);
	//		}
	//		$( "#selectDate" ).datepicker({
 	//			onSelect: function(dateText, inst) {
 	//				//changeSelect();  
 	//				if(typeTag == "week")
 	//					$("#selectWeek").html(week(dateText+" 00:00:00")[0].substring(0,10)+"~"+week(dateText+" 00:00:00")[1].substring(0,10));
 	//				init();
	//		 	}
	//		});
	//		$( "#selectDate" ).datepicker("option", "dateFormat", "yy-mm-dd");
	//		$("#selectDate").val($.datepicker.formatDate("yy-mm-dd",new Date()));
			
	//});
			
	function changeSiteSelect(){
		location.href="?site="+$("#site").val();
	}
	
	function changeCateSelect(){
		location.href="?site="+$("#site").val()+"&category="+$("#category").val();
	}
</script>
</head>
<body>
<div id="container">
<!-- header -->
<%@include file="../header.jsp" %>

<div id="sidebar">
	<div class="sidebox">
		<h3>통계</h3>
			<ul class="latest">
			<li><a href="<%=CATBOT_MANAGE_ROOT%>stat/scrapingStat.jsp" class="selected">수집현황</a></li>
			<li><a href="<%=CATBOT_MANAGE_ROOT%>stat/history.jsp">수집히스토리</a></li>
			</ul>
	</div>
</div><!-- E : #sidebar -->
	
<div id="mainContent">
	<h2>수집현황</h2>
		<p>
		<%
			CatbotConfig catbotConfig = CatbotSettings.getGlobalConfig();
			List<String> siteList = catbotConfig.getSiteList();
			
		%>
		사이트: 
		<select name="site" id="site" onchange="changeSiteSelect();" size="10">
			<option value="" <%=site == null||site.length() == 0 ? "selected" : "" %>>전체</option>
			<% for(int k=0; k<siteList.size(); k++){ 
				String siteId = siteList.get(k);
				SiteConfig sConfig = CatbotSettings.getSiteConfig(siteId);
			%>
			<option value="<%=siteId %>" <%=siteId.equals(site) ? "selected" : "" %> ><%=sConfig.getDescription() %></option>
			<% } %>
		</select>
		<%
		if(siteCategoryConfigList != null){
			Iterator it = siteCategoryConfigList.entrySet().iterator();
		%>
		게시판: 
		<select name="category" id="category" onchange="changeCateSelect();" size="10">
			<% while (it.hasNext()) {   
			         Map.Entry entry = (Map.Entry) it.next();   
			         CategoryConfig cateConfig = (CategoryConfig)entry.getValue();   
			         String cateName= cateConfig.getCategoryName();
			%>
			<option value="<%=cateName %>" <%=cateName.equals(category) ? "selected" : "" %> ><%=cateConfig.getDescription() %></option>
			<% } %>
		</select>
		<%
		}
		%>
		
		<input type="checkbox" id="hasDocs" <%=hasDocs ? "checked":"" %>/><label for="hasDocs">0건 수집기록제외</label> 
		</p>
		
<%
		ScrapingHistory scrapingHistory = dbHandler.getScrapingHistory();
		List<ScrapingHistory> list = scrapingHistory.select(startRow, pgSize, site, category, hasDocs);
		int totalCount = scrapingHistory.count(site, category, hasDocs);
		%>	

<br/>
<p>총 <%=totalCount%>개의 수집기록중 <%=startRow+1%> - <%=startRow+list.size()%>  </p>
	<div id="paging" class="fl">
	<%
		int maxPage = totalCount / pgSize;
			if(totalCount % pgSize > 0) 
		maxPage++;
			if(pg < maxPage){
		int lastRec = totalCount % pgSize;
			    out.println("<span class='num'><a href='?pg="+maxPage+"&site="+site+"&category="+category+"'>마지막</a></span>");
			}
			if(pg < maxPage){ 
		int nextPage = pg + 1;
			    out.println("<span class='num'><a href='?pg="+nextPage+"&site="+site+"&category="+category+"'>이전</a></span>");
			}
			if(pg != 1){
			    int prevStart = pg - 1;
			    out.println("<span class='num'><a href='?pg="+prevStart+"&site="+site+"&category="+category+"'>다음</a></span>");
			    out.println("<span class='num'><a href='?pg=1&site="+site+"&category="+category+"'>최신</a></span>");
			}
	%>
	</div>

	<script type="text/javascript">
        var chart;
		var chartData;
        function drawChart() {
               // first we generate some random data
               //generateChartData();

               // SERIAL CHART
               chart = new AmCharts.AmSerialChart();
               chart.pathToImages = "../js/amcharts/images/";
               chart.panEventsEnabled = true;
               chart.marginRight = 30;                
               chart.dataProvider = chartData;
               chart.categoryField = "date";

               // AXES
               // Category
               var categoryAxis = chart.categoryAxis;
               //categoryAxis.parseDates = true; // in order char to understand dates, we should set parseDates to true
               //categoryAxis.dateFormats = [{period:'ss',format:'YYYY-MM-DD hh:mm:ss'}];
               //categoryAxis.minPeriod = "ss"; // as we have data with minute interval, we have to set "mm" here.			 
               categoryAxis.gridAlpha = 0.07;
               categoryAxis.startOnAxis = true;
               categoryAxis.axisColor = "#DADADA";

               // Value
               var valueAxis = new AmCharts.ValueAxis();
               valueAxis.gridAlpha = 0.07;
               valueAxis.title = "수집문서수";
               valueAxis.titleBold = false;
			   valueAxis.axisColor = "#DADADA";                
               chart.addValueAxis(valueAxis);

               // GRAPH
               var graph = new AmCharts.AmGraph();
               graph.type = "column"; // try to change it to "column"
               graph.title = "red line";
               graph.valueField = "docs"//"visits";//docs
               graph.lineAlpha = 1;
               graph.lineColor = "#d1cf2a";
               graph.fillAlphas = 0.3; // setting fillAlphas to > 0 value makes it area graph
               chart.addGraph(graph);

               // CURSOR
               var chartCursor = new AmCharts.ChartCursor();
               chartCursor.cursorPosition = "mouse";
               chartCursor.categoryBalloonDateFormat = "YY-MM-DD JJ:NN";
               chart.addChartCursor(chartCursor);


               // WRITE
               chart.write("chartdiv");
               chart.validateData();
           }

           // generate some random data, quite different range 
           function generateChartData() {
               // current date
               var firstDate = new Date();
               // now set 1000 minutes back                 
               firstDate.setMinutes(firstDate.getDate() - 1000);

               // and generate 1000 data items
               for (var i = 0; i < 1000; i++) {
                   var newDate = new Date(firstDate);
                   // each time we add one minute
                   newDate.setMinutes(newDate.getMinutes() + i);
                   // some random number      
                   var visits = Math.round(Math.random() * 40) + 10;
                   // add data item to the array                          
                   chartData.push({
                       date: newDate,
                       visits: visits
                   });
               }
           }

           // this method is called when chart is first inited as we listen for "dataUpdated" event
           function zoomChart() {
               // different zoom methods can be used - zoomToIndexes, zoomToDates, zoomToCategoryValues
               chart.zoomToIndexes(chartData.length - 40, chartData.length - 1);
           }
       </script>
       
	<div id="chartdiv" style="width:100%; height:300px;"></div>
		

	<div id="tbDiv" style="overflow: -moz-scrollbars-vertical;overflow-y:scroll; width:100%; height:300px">
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
		<th>실행시간</th>
	</tr>
	</thead>
	<tbody>
<%
	
	for(int i = 0; i < list.size(); i++){
		ScrapingHistory indexingHistory = list.get(i);
		String siteId = indexingHistory.site;
		String cateId = indexingHistory.category;
		String siteName = siteId;
		String categoryName = cateId;
		SiteConfig sConfig = CatbotSettings.getSiteConfig(siteId);
		CategoryConfig cateConfig = CatbotSettings.getCategoryConfig(siteId, cateId);
		if(sConfig != null){
			siteName = sConfig.getDescription();
		}
		if(cateConfig != null){
			categoryName = cateConfig.getDescription();
		}
		
%>
	<tr>
		<td class="first"><%=totalCount - startRow - i%></td>
		<td><strong class="small tb"><%=siteName%><br/> ><%=categoryName%></strong></td>
		<td><%=indexingHistory.isSuccess ? "성공" : "실패" %></td>
		<td><%=indexingHistory.docSize%></td>
		<td><%=indexingHistory.isScheduled ? "자동" : "수동" %></td>
		<td><%=indexingHistory.startTime%></td>
		<td><%=Formatter.getFormatTime((long)indexingHistory.duration)%></td>
	</tr>
<%
	}
	JSONArray arr = new JSONArray();
	for(int i = list.size()-1; i >= 0 ; i--){
		ScrapingHistory indexingHistory = list.get(i);
		String siteId = indexingHistory.site;
		String cateId = indexingHistory.category;
		String siteName = siteId;
		String categoryName = cateId;
		SiteConfig sConfig = CatbotSettings.getSiteConfig(siteId);
		CategoryConfig cateConfig = CatbotSettings.getCategoryConfig(siteId, cateId);
		if(sConfig != null){
			siteName = sConfig.getDescription();
		}
		if(cateConfig != null){
			categoryName = cateConfig.getDescription();
		}
		JSONObject o = new JSONObject();
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
		SimpleDateFormat sdf2 = new SimpleDateFormat("MM-dd");
		Date d = new Date(indexingHistory.startTime.getTime());
		try {
			o.put("date", sdf2.format(d)+"\\n"+sdf.format(d));
			o.put("docs", ""+indexingHistory.docSize);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		arr.put(o);
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
	</div>
	
<!-- E : #mainContent -->
</div>
<script type="text/javascript">
$(document).ready(function() {
	  chartData = eval('(<%=arr.toString() %>)');
	  drawChart();
});
</script>
<!-- footer -->
<%@include file="../footer.jsp" %>
	
</div><!-- //E : #container -->
<form action="/stat/csv" method="post" name="csvForm" style="display:none">
	<input type=hidden name="data" value="" />
	<input type=hidden name="filename" value="" />
</form>
</body>
</html>
