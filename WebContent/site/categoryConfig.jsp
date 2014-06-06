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
	String category = request.getParameter("category");
	String action = request.getParameter("action");
	
	if("save".equals(action)){
		String setting = request.getParameter("setting");
		CatbotSettings.saveCategoryConfigRaw(site, category, setting);
	}
	
	SiteConfig siteConfig = CatbotSettings.getSiteConfig(site);
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
	function testScraping(site, category){
		//console.log(site, category);
		$.ajax({
			url : "siteService.jsp",
			data : {cmd: 5, site: site, category:category},
			method : "get",
			success : function(data){ 
				$("#scrapResult").show();
				$("#scrapResult").text($.trim(data));
			}
		});
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
				<a href= "siteConfig.jsp?site=<%=siteName %>"><%=sConfig.getDescription() %></a>
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
<%
	String errorMsg = null;
	CategoryConfig categoryConfig = null;
	
	categoryConfig = CatbotSettings.getCategoryConfig(site, category, true);
	
%>
<div id="mainContent">
	<h2>게시판 설정</h2>
	
	<% if(errorMsg != null){ %>
		<h3 style="color:#663366">게시판 설정</h3>
		<div class="fbox" id="hiddenDiv">
		<table summary="게시판 설정" class="tbl02">
		<tbody>
		<tr><td class="first">게시판 설정 에러 : <%=errorMsg %></td></tr>
		</tbody>
		</table>
		</div>
	<% } %>
	<% if(categoryConfig != null){
		List<Page> pageList = categoryConfig.getPageList();
		Process process = categoryConfig.getProcess();
	%>
	<!-- <a href="javascript:history.back();" class="btn_s"><< 이전으로</a><br/><br/>-->
	
	<p>* <%=siteConfig.getDescription()%> > <%=categoryConfig.getDescription()%>(<%=categoryConfig.getCategoryName()%>)</p>
	<br/>
<!-- pagelist -->		
		<h4>페이지설정</h4>
		<div class="fbox" id="hiddenDiv">
		<table summary="pagelist 설정" class="tbl02">
		<colgroup>
			<col width="5%" />
			<col width="10%" />
			<col width="10%" />
			<col width="10%" />
			<col width="10%" />
			<col width="*" />
		</colgroup>
		<thead>
		<tr>
			<th class="first">No.</th>
			<th>encoding</th>
			<th>from</th>
			<th>to</th>
			<th>method</th>
			<th>url</th>
		</tr>
		</thead>
		<tbody>
		<% 
		if(pageList != null){
			for(int i = 0; i < pageList.size(); i++){
				Page p = pageList.get(i); 
				if(p == null) continue;
				
				String url2 = p.getUrl();
				url2 = url2.replaceAll("\\$\\{i\\}", p.getFrom()+"");
		%>
				<tr>
					<td class="first"><%=i+1%></td>
					<td id="enc<%=i+1%>"><%=p.getEncoding()%></td>
					<td id="from<%=i+1%>"><%=p.getFrom()%></td>
					<td id="to<%=i+1%>"><%=p.getTo()%></td>
					<td id="method<%=i+1%>"><%=p.getMethod()%></td>
					<td id="url<%=i+1%>"><a href="<%=url2%>" target="category_pop"><%=p.getUrl()%></a></td>
				</tr>
		<%
			}
		} 
		%>
		</tbody>
		</table>
		</div>
<!-- pagelist -->	

<!-- process -->		
		<h4>수집프로세스설정</h4>
		<div class="fbox">
		<table summary="root path" class="tbl01">
		<colgroup><col width="14.5%" /><col width="" /></colgroup>
		<tbody>
		<tr>
			<th>root path</th>
			<td style="text-align:left;"><%=process.getRootPath()%></td>
		</tr>
		</tbody>
		</table>
		<table summary="process 설정" class="tbl02">
		<colgroup><col width="60" /><col width="100" /><col width="100" /><col width="*" /><col width="*" /></colgroup>
		<thead>
		<tr>
			<th class="first">번호</th>
			<th>아이디</th>
			<th>타입</th>
			<th>XPath</th>
			<th>Value</th>
		</tr>
		</thead>
		<tbody>
		<% if(process != null){
			List<Block> bl = process.getBlockArrayList();
			
			List<Block> newBL = new ArrayList<Block>();
			
			
			for(int i = 0; i < bl.size(); i++){
				Block block = bl.get(i); 
				String xpath = block.getXpath();
				String value = block.getValue();
		%>
				<tr>
					<td class="first"><%=i+1%></td>
					<td id="id<%=i+1%>"><%
					if(block.isPk()){
						%>
						<b><u><%=block.getId()%></u></b>
						<%
					}else{
						%>
						<%=block.getId()%>
						<%
					}
					%></td>
					<td id="isField<%=i+1%>"><%=block.isField()?"필드":"블럭" %></td>
					<td id="xpath<%=i+1%>"><%=xpath!=null?xpath:"" %></td>
					<td id="value<%=i+1%>"><%=value!=null?value:"" %></td>
				</tr>
		<%
			}
		} 
		%>
		<!--
		<input type="text" value="필드추가" name="sdfsd" class="inp02">
		<input type="button" value="필드삭제" class="btn_s" onclick="javascript:removeField()">
		 -->
		</tbody>
		</table>
		</div>
		
		<h4>원본포맷</h4>
		<div><i>* &amp;는 반드시 &amp;amp;로 작성해야 합니다.</i></div>
		<form action="categoryConfig.jsp" method="post">
			<input type="hidden" name="action" value="save" />
			<input type="hidden" name="site" value="<%=site %>" />
			<input type="hidden" name="category" value="<%=category %>" />
			<textarea name="setting" style="width:100%; height:500px;" class="textbox"><%=HttpUtils.getAmpEscaped(CatbotSettings.getCategoryConfigRaw(site, category)) %></textarea>
			<br/><br/>
			<input type="submit" value="저장" class="btn"/> <input type="button" onClick="javascript:testScraping('<%=site %>', '<%=category %>')" value="테스트" class="btn"/>
		</form>
<!-- process -->
		<br/>		
		<h4>테스트결과</h4>
		<div><i>* 테스트는 최신 3건만 수집하며 DB에 저장되거나 히스토리에 남지 않습니다.</i></div>
		<textarea id="scrapResult" style="width:100%; height:500px;display:none" class="textbox"></textarea>
	<%}%>
	
	
	<!-- E : #mainContent --></div>
	<!-- footer -->
<%@include file="../footer.jsp" %>
</div><!-- //E : #container -->
</body>
</html>
