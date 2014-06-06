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

<%@ page contentType="text/html; charset=UTF-8"%> 

<%@page import="com.websqrd.libs.common.Formatter"%>
<%@page import="com.websqrd.catbot.setting.*"%>
<%@page import="com.websqrd.catbot.web.WebUtils"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.*"%>
<%@page import="java.util.Date"%>
<%@page import="java.util.Calendar"%>
<%@page import="java.net.URLDecoder"%>

<%@include file="../common.jsp" %>

<%
	CatbotConfig irConfig = CatbotSettings.getGlobalConfig(true);
	String message = URLDecoder.decode(WebUtils.getString(request.getParameter("message"), ""),"utf-8");
	String id = WebUtils.getString(request.getParameter("id"), "default");
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
<script type="text/javascript" src="<%=CATBOT_MANAGE_ROOT%>js/jquery-1.4.4.min.js"></script>
<script src="<%=CATBOT_MANAGE_ROOT%>js/jquery.validate.min.js" type="text/javascript"></script>
<script src="<%=CATBOT_MANAGE_ROOT%>js/validate.messages_ko.js" type="text/javascript"></script>
<script type="text/javascript">
	
	$(document).ready(function() {
		$("#repoForm").validate({
			errorClass : "invalidValue",
			rules: {
				"id" : {required: true},
				"vendor" : {required: true},
				"db" : {required: true},
				"host" : {required: true},
				"port" : {required: true, number: true},
				"user" : {required: true},
				"password" : {required: true}
			}
		});

		var message = "<%=message %>";
		if(message != "")
			alert(message);
		
	});

	function repoSubmit(){
		if($("#repoForm").valid()){
			$("#repoForm").submit();
		}
	}
	function repoSubmitDelete(){
		if($("#repoForm").valid()){
			$("#cmd").val("2");
			$("#repoForm").submit();
		}
	}
	function testConnectSubmit(){
			$("#cmd").val("4");
			$("#repoForm").submit();
	}
</script>
</head>

<body>

<div id="container">
<!-- header -->
<%@include file="../header.jsp" %>

<div id="sidebar">
	<div class="sidebox">
		<h3>저장소</h3>
			<ul class="latest">
			<li><a href="<%=CATBOT_MANAGE_ROOT%>repository/main.jsp">저장소설정</a></li>
			</ul>
	</div>
</div><!-- E : #sidebar -->

<%
	Map<String, RepositorySetting> settingList = CatbotSettings.getRepositoryList(true);
	String repoFilename = "repository_"+id+".xml";
	RepositorySetting setting = settingList.get(repoFilename);
%>
<script type="text/javascript">
$(document).ready(function() {
	var vendor = "<%=setting.vendor %>";
	document.getElementById("vendor").value = vendor;
});
</script>
<div id="mainContent">

<form action="repoService.jsp" method="post" name="repoForm" id="repoForm">
	<input type="hidden" name="cmd" id="cmd" value="1" />
	<h2>저장소편집</h2>
	<div class="fbox">
	<table summary="서버" class="tbl01">
	<colgroup><col width="33%" /><col width="" /></colgroup>
	<tbody>
		<tr>
		<th>저장소이름</th>
		<td style="text-align:left;"><input type="text" name="id" id="id" value="<%=setting.id %>" size='20' class='inp02' /></td>
		</tr>
		<tr>
		<th>DB벤더</th>
		<td style="text-align:left;">
		<select name="vendor" id="vendor" value="">
			<option value="" >선택</option>
			<option value="DERBY" >DERBY</option>
			<option value="MYSQL" >MYSQL</option>
			<option value="MSSQLSERVER" >MSSQLSERVER</option>
			<option value="CUBRID" >CUBRID</option>
			<option value="ORACLE" >ORACLE</option>
		</select>
		</td>
		</tr>
		<tr>
		<th>DB명</th>
		<td style="text-align:left;"><input type="text" name="db" id="db" value="<%=setting.db %>" size='20' class='inp02' /></td>
		</tr>
		<tr>
		<th>HOST</th>
		<td style="text-align:left;"><input type="text" name="host" id="host" value="<%=setting.host %>" size='20' class='inp02' /></td>
		</tr>
		<tr>
		<th>PORT</th>
		<td style="text-align:left;"><input type="text" name="port" id="port" value="<%=setting.port %>" size='20' class='inp02' /></td>
		</tr>
		<tr>
		<th>사용자아이디</th>
		<td style="text-align:left;"><input type="text" name="user" id="user" value="<%=setting.user %>" size='20' class='inp02' /></td>
		</tr>
		<tr>
		<th>사용자비번</th>
		<td style="text-align:left;"><input type="text" name="password" id="password" value="<%=setting.password %>" size='20' class='inp02' /></td>
		</tr>
		<tr>
		<th>인코딩</th>
		<td style="text-align:left;"><input type="text" name="encoding" id="encoding" value="<%=setting.encoding %>" size='20' class='inp02' /></td>
		</tr>
		<tr>
		<th>JDBC 속성값</th>
		<td style="text-align:left;"><textarea style="width:100%; height:100px;" name="parameter" id="parameter" class='textbox' ><%=setting.parameter != null ? setting.parameter : ""%></textarea></td>
		</tr>
	</tbody>
	</table>
	</div>
	
	<div id="btnBox">
	<a href="javascript:repoSubmit()" class="btn">저장</a>
	<a href="javascript:repoSubmitDelete()" class="btn">삭제</a>
	<a href="javascript:testConnectSubmit()" class="btn">연결 테스트</a>
	</div>

</form>
	
	<!-- E : #mainContent --></div>
	
<!-- footer -->
<%@include file="../footer.jsp" %>
	
</div><!-- //E : #container -->
	
</body>

</html>
