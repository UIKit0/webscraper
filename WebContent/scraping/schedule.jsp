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
<%@page import="com.websqrd.catbot.web.WebUtils"%>
<%@page import="com.websqrd.catbot.web.WebUtils"%>
<%@page import="com.websqrd.catbot.setting.CatbotSettings"%>
<%@page import="com.websqrd.catbot.setting.*"%>
<%@page import="com.websqrd.catbot.control.*"%>
<%@page import="com.websqrd.catbot.db.*"%>

<%@ page contentType="text/html; charset=UTF-8"%> 
<%@page import="com.websqrd.libs.common.Formatter"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Date"%>
<%@page import="java.sql.Timestamp"%>
<%@page import="java.util.Calendar"%>
<%@page import="java.util.*"%>
<%@page import="java.util.Map.Entry"%>

<%@include file="../common.jsp" %>

<%
	CatbotConfig irConfig = CatbotSettings.getGlobalConfig(true);
	List<String> siteList = irConfig.getSiteList();
	DBHandler dbHandler = DBHandler.getInstance();
	boolean isActive = false;
	String cmd = WebUtils.getString(request.getParameter("cmd"), "");
	
	if(cmd.equals("1")){
		//apply indexing schedule
		
		String slcSite = request.getParameter("slc_site");
		String category = request.getParameter("slc_category");
		
		int p_day = WebUtils.getInt(request.getParameter("p_day"), -1);
		int p_hour = WebUtils.getInt(request.getParameter("p_hour"), -1);
		int p_minute = WebUtils.getInt(request.getParameter("p_minute"), -1);
		int s_hour = WebUtils.getInt(request.getParameter("s_hour"), -1);
		int s_minute = WebUtils.getInt(request.getParameter("s_minute"), -1);
		int period = p_day * 60 * 60 * 24 + p_hour * 60 * 60 + p_minute * 60;
		
		if( period >= 0 && s_hour >= 0 && s_minute >= 0){
			try{
				Calendar cal = Calendar.getInstance();
				cal.setTime(new Date());
				cal.set(Calendar.HOUR_OF_DAY, s_hour);
				cal.set(Calendar.MINUTE, s_minute);
				Timestamp startTime = new Timestamp(cal.getTimeInMillis());
				dbHandler.getScrapingSchedule().updateOrInsert(slcSite, category, period, startTime, isActive);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
		//stop scheduling
		JobController.getInstance().toggleIndexingSchedule(slcSite, category, isActive);
		
	}else if(cmd.equals("2")){
		//toggle indexing schedule
		
		String slcSite = request.getParameter("slc_site");
		String category = request.getParameter("slc_category");
		String isActiveStr =  WebUtils.getString(request.getParameter("isActive"), "0");
		
		if(isActiveStr.equals("1")){
			isActive = true;
		}
		
		try{
			dbHandler.getScrapingSchedule().updateStatus(slcSite, category, isActive);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		//JobScheduler RELOAD!
		JobController.getInstance().toggleIndexingSchedule(slcSite, category, isActive);
		
	}
	
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy,M,d");
	
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>웹수집기 관리도구</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<link href="<%=CATBOT_MANAGE_ROOT%>css/reset.css" rel="stylesheet" type="text/css" />
<link href="<%=CATBOT_MANAGE_ROOT%>css/style.css" rel="stylesheet" type="text/css" />
<link href="<%=CATBOT_MANAGE_ROOT%>css/jquery-ui.css" rel="stylesheet" type="text/css" media="screen" />
<!--[if lte IE 6]>
<link href="<%=CATBOT_MANAGE_ROOT%>css/style_ie.css" rel="stylesheet" type="text/css" />
<![endif]-->
<!--[if lte IE 6]>
<link href="css/style_ie.css" rel="stylesheet" type="text/css" />
<![endif]-->
<script type="text/javascript" src="<%=CATBOT_MANAGE_ROOT%>js/common.js"></script>
<script type="text/javascript" src="<%=CATBOT_MANAGE_ROOT%>js/jquery-1.4.4.min.js"></script>
<script type="text/javascript" src="<%=CATBOT_MANAGE_ROOT%>js/jquery-ui-1.8.9.min.js"></script>
<script>

function chkSelected (id)
{
	var tmp = document.getElementById ( id).options;
	
	for( i = 0; i < tmp.length; i++)
		if(tmp[i].selected == true)
			return ( tmp[i].value);
	return null;
}

function applyIndexingSchedule(site, category, isActive){

	if($("#p_data_"+site+"_"+category).val() == ""){
		alert("주기를 설정하십시오.");
		return;
	}

	 if($("#s_minute_"+site+"_"+category).val() == "" || $("#s_hour_"+site+"_"+category).val() == ""){
		alert("기준시각의 시/분을 설정하십시오.");
		return;
	} 
	$("#cmd").val("1");
	$("#slc_site").val($("#slc_site_"+site).val());
	$("#slc_category").val($("#slc_category_"+site+"_"+category).val());
	/* $("#type").val(type); */
	if("type_min" == $("#p_type_"+site+"_"+category).val()){
		$("#p_day").val("0");
		$("#p_hour").val("0");
		$("#p_minute").val($("#p_data_"+site+"_"+category).val());
	}else if("type_hou" == $("#p_type_"+site+"_"+category).val()){
		$("#p_day").val("0");
		$("#p_hour").val($("#p_data_"+site+"_"+category).val());
		$("#p_minute").val("0");
	}else if("type_day" == $("#p_type_"+site+"_"+category).val()){
		$("#p_day").val($("#p_data_"+site+"_"+category).val());
		$("#p_hour").val("0");
		$("#p_minute").val("0");
	}	
		
	$("#s_hour").val($("#s_hour_"+site+"_"+category).val());
	$("#s_minute").val($("#s_minute_"+site+"_"+category).val()); 
	$("#isActive").val(isActive);
	
	callAjax(site,category,isActive,"1");
	//$("#applyForm").submit();

}
function toggleIndexingSchedule(site, category, isActive){
	$("#cmd").val("2");
	$("#slc_site").val($("#slc_site_"+site).val());
	$("#slc_category").val($("#slc_category_"+site+"_"+category).val());
	$("#s_minute").val($("#s_minute_"+site+"_"+category).val()); 
	$("#isActive").val(isActive);
	
	//$("#applyForm").submit();
	callAjax(site,category,isActive,"2");
}

function callAjax(site, category, isActive, cmd)
{	
	if ( cmd == "1" ) 
		{		
		$.ajax({
					url:"applyschedule.jsp",
					method:"post",
					async : true,
					data:{						
						slc_site:site,
						slc_category:category,
						cmd:cmd,
						p_day:$("#p_day").val(),
						p_hour:$("#p_hour").val(),
						p_minute:$("#p_minute").val(),						
						s_hour:$("#s_hour_"+site+"_"+category).val(),
						s_minute:$("#s_minute_"+site+"_"+category).val()
						},
						success:function(responseText) {
						responseText=responseText.replace(/^\s\s*/,"");		
												
						if (responseText.trim() == "0" ) 
							{
							$("#btn_"+site+"_"+category).removeClass("on").addClass("off");
							$("#btn_"+site+"_"+category).text("OFF");
							$("#btn_"+site+"_"+category).attr("href","javascript:toggleIndexingSchedule('"+site+"', '"+category+"', 1)");
							}
						else
							{
								alert("색인 스케쥴링 정보 갱신에 실패했습니다.");
							}
						}					
				});
		}
	else if ( cmd == "2" )
	{
		$.ajax({
			url:"applyschedule.jsp",
			async : true,
			method:"post",
			data:{
				cmd:cmd,
				slc_site:site,
				slc_category:category,
				isActive:isActive
				},
			success:function(responseText) {
				responseText=responseText.replace(/^\s\s*/,"");				
		
				if (responseText.trim() == "0" ) 
					{
					console.log("set On Button");
					console.log("site:" + site + "|" + "category:" + category + "|0" );
					console.log("responseText : "+responseText.trim());
					$("#btn_"+site+"_"+category).removeClass("off").addClass("on");
					$("#btn_"+site+"_"+category).text("ON");
					$("#btn_"+site+"_"+category).attr("href","javascript:toggleIndexingSchedule('"+site+"', '"+category+"', 0)");
					}
				else
					{
					console.log("set Off Button");									
					console.log("site:" + site + "|" + "category:" + category + "|1" ); 
					console.log("responseText : "+responseText.trim());
					$("#btn_"+site+"_"+category).removeClass("on").addClass("off");
					$("#btn_"+site+"_"+category).text("OFF");
					$("#btn_"+site+"_"+category).attr("href","javascript:toggleIndexingSchedule('"+site+"', '"+category+"', 1)");					
					}			
			}					
		});
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
		<h3>스케쥴</h3>
			<ul class="latest">
			<li><a href="<%=CATBOT_MANAGE_ROOT%>scraping/schedule.jsp" class="selected">수집주기설정</a></li>
			</ul>
	</div>
	<div class="sidebox">
		<h3>도움말</h3>
			<ul class="latest">
			<li>각 수집에 대하여 수집주기를 설정합니다.</li>
			<li>수집주기를 셋팅하고 저장버튼을 누르면 사용여부버튼이 활성화됩니다.</li>
			<li>사용여부가 ON이 되면 동작을 시작합니다.</li>
			</ul>
	</div>
</div><!-- E : #sidebar -->


<div id="mainContent">
	<h2>수집주기설정</h2>
	<div class="fbox">
	
	<table summary="수집주기설정" class="tbl02">
	<thead>
	<tr>
		<th class="first">No.</th>
		<th>사이트 > 게시판</th>
		<th width="*">주기</th>
		<th width="*">기준시각</th>
		<th>저장</th>
		<th>사용여부</th>
	</tr>
	</thead>
	<tbody>
<%

	ScrapingSchedule scrapingSchedule = dbHandler.getScrapingSchedule();
	int idx = 0;
	for(int i = 0; i < siteList.size(); i++){
		String site = siteList.get(i);
		SiteConfig siteConfig = CatbotSettings.getSiteConfig(site);
		Map<String, CategoryConfig> categoryConfigList = CatbotSettings.getSiteCategoryConfigList(site);
		if(categoryConfigList == null){
			break;
		}
		Iterator<Entry<String, CategoryConfig>> iter = categoryConfigList.entrySet().iterator();
		
		while(iter.hasNext()){
			idx++;
			Entry<String, CategoryConfig> entry = iter.next();
			String category = entry.getKey();
			CategoryConfig categoryConfig = entry.getValue();
			int incIndexingCount = scrapingSchedule.select(site, category);
%>

	<tr>
		<td class="first"><%=idx%></td>
		<td><strong class="small tb"><%=siteConfig.getDescription()%><br/> ><%=categoryConfig.getDescription() %></strong></td>
		<%
		int period = -1;
		int pDay = -1;
		int pHour = -1;
		int pMinute = -1;
		int pData = -1;
		long timestamp = -1;
		int sHour = -1;
		int sMinute = -1;
		isActive = false;
		if(incIndexingCount > 0){
			period = scrapingSchedule.period;
			pDay = period/(3600*24);
			pHour = (period%(3600*24))/3600;
			pMinute = (period%3600)/60;
			if(pDay > 0){
				pData = pDay;
				%>
				<script type="text/javascript">
				$(document).ready(function() {
					document.getElementById("p_type_<%=site%>_<%=category%>").value = "type_day";
				});
				</script>
				<%
			}else if(pHour > 0){
				pData = pHour;
				%>
				<script type="text/javascript">
				$(document).ready(function() {
					document.getElementById("p_type_<%=site%>_<%=category%>").value = "type_hou";
				});
				</script>
				<%
			}else if(pMinute > 0){
				pData = pMinute;
				%>
				<script type="text/javascript">
				$(document).ready(function() {
					document.getElementById("p_type_<%=site%>_<%=category%>").value = "type_min";
				});
				</script>
				<%
			}
			Timestamp ts = scrapingSchedule.startTime;
			timestamp = ts.getTime();
			Calendar cal = Calendar.getInstance();
			cal.setTime(new Date(timestamp));

			sHour = cal.get(Calendar.HOUR_OF_DAY);
			sMinute = cal.get(Calendar.MINUTE);//WebUtils.getInt(request.getParameter("s_minute"), -1);//cal.get(Calendar.MINUTE);
			SimpleDateFormat sdfStartTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String startTimeStr = sdfStartTime.format(new Date(ts.getTime()));
			isActive = scrapingSchedule.isActive;
		}else{
			timestamp = System.currentTimeMillis();
		}
		%>
		<td>
		<input type="hidden" id="slc_site_<%=site %>" value="<%=site %>"/>
		<input type="hidden" id="slc_category_<%=site %>_<%=category %>" value="<%=category %>"/>
		<%if(pData >= 0){%>
		<input id="p_data_<%=site %>_<%=category %>" name="p_data_<%=site %>_<%=category %>" class="inp05" size="4" type="text" value="<%=pData%>" />
		<% }else{%>
		<input id="p_data_<%=site %>_<%=category %>" name="p_data_<%=site %>_<%=category %>" class="inp05" size="4" type="text" value="" />
		<% }%>
		<select name="p_type_<%=site %>_<%=category %>" id="p_type_<%=site %>_<%=category %>" value="">
			<option value="type_min">분</option>
			<option value="type_hou">시</option>
			<option value="type_day">일</option>
		</select>
		</td>
		<td>
			<select name="s_hour_<%=site %>_<%=category %>" id="s_hour_<%=site %>_<%=category %>">
			<% for ( int m = 0; m < 24; m++){
			if ( sHour >= 0 && sHour == m) {%>
			<option value="<%=m%>" selected><%=m%></option>
			<%} else { %>
			<option value="<%=m%>"><%=m%></option>
			<%}}%>
			</select>시

			<select id="s_minute_<%=site %>_<%=category %>" name="s_minute_<%=site %>_<%=category %>">
				<% for ( int m = 0; m < 60; m++){
			if ( sMinute >= 0 && sMinute == m) {%>
			<option value="<%=m%>" selected><%=m%></option>
			<%} else { %>
			<option value="<%=m%>"><%=m%></option>
			<%}}%>
			</select>분
			
		</td>
		<td><a href="javascript:applyIndexingSchedule('<%=site %>','<%=category%>', 0);" class="btn_s">저장</a></td>
		<%if(incIndexingCount > 0){ 
			if(scrapingSchedule.isActive){
		%>
		<td><a id="btn_<%=site%>_<%=category%>" href="javascript:toggleIndexingSchedule('<%=site %>','<%=category%>', 0);" class="btn_s on">ON</a></td>
		<%
			}else{
		%>
		<td><a id="btn_<%=site%>_<%=category%>" href="javascript:toggleIndexingSchedule('<%=site %>','<%=category%>', 1);" class="btn_s off">OFF</a></td>
		<%	
			}
		}else{
		%>
		<td>OFF</td>
		<%} %>
	</tr>
<%
	}//while
	}
%>
	</tbody>
	</table>
	</div>
	
	<!-- E : #mainContent --></div>
	
	
	<form name="applyForm" id="applyForm" action="" method="post">
		<input type="hidden" name="cmd" id="cmd" />
		<input type="hidden" name="slc_site" id="slc_site" />
		<input type="hidden" name="slc_category" id="slc_category" />
		<input type="hidden" name="p_day" id="p_day" />
		<input type="hidden" name="p_hour" id="p_hour" />
		<input type="hidden" name="p_minute" id="p_minute" />
		<input type="hidden" name="s_minute" id="s_minute" />
		<input type="hidden" name="s_hour" id="s_hour" />
		<input type="hidden" name="isActive" id="isActive" />
	</form>
	
<!-- footer -->
<%@include file="../footer.jsp" %>
	
</div><!-- //E : #container -->


</body>

</html>
