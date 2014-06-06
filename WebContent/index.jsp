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
<%@page import="com.websqrd.catbot.web.WebUtils"%>
<%@page import="java.net.URLConnection"%>
<%@page import="java.net.URL"%>
<%@page import="java.io.BufferedReader"%>
<%@page import="java.io.InputStreamReader"%>
<%@page import="com.websqrd.catbot.setting.CatbotSettings"%>
<%@page import="java.net.URLDecoder"%>
<%@include file="webroot.jsp"%>
<%
	String message = URLDecoder.decode(WebUtils.getString(request.getParameter("message"), ""),"utf-8");
	String cmd = request.getParameter("cmd");
	if ("ajax".equals(cmd)) {
		String location = request.getParameter("location");
		try {
			URL url = new URL(location);
			BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), "utf-8"));
			String line;
			while ((line = reader.readLine()) != null) {
				out.println(line);
			}
		} catch (Exception e) { }

		return;
	}else if ("login".equals(cmd)) {
		String username = request.getParameter("username");
		String passwd = request.getParameter("passwd");
		String[] accessLog = CatbotSettings.isCorrectPasswd(username, passwd);
		if(accessLog != null){
			//로긴 성공
			session.setAttribute("authorized", username);
			session.setAttribute("lastAccessLog", accessLog);
			session.setMaxInactiveInterval(60 * 30); //30 minutes
			CatbotSettings.storeAccessLog(username, ""); //ip주소는 공란으로 남겨두고 사용하지 않도록함. 
			//request.getRemoteAddr()로는 제대로된 사용자 ip를 알아낼수 없음.
			//jetty에서는 getHeader("REMOTE_ADDR"); 또는 req.getHeaer("WL-Proxy-Client-IP")+","+req.getHeaer("Proxy-Client-IP")+","+req.getHeaer("X-Forwarded-For")) 등을 제공하지 않는다.
			message = "";
			response.sendRedirect(CATBOT_MANAGE_ROOT+"main.jsp");
			return;
		}else{
			message = "아이디와 비밀번호를 확인해주세요.";
		}
		
	}else if ("logout".equals(cmd)) {
		session.invalidate();
		response.sendRedirect(CATBOT_MANAGE_ROOT+"index.jsp");
		return;
	}
	if (CatbotSettings.isAuthUsed() && "admin".equals(session.getAttribute("authorized"))) {
		response.sendRedirect(CATBOT_MANAGE_ROOT+"main.jsp");
		return;
	}
	if(!CatbotSettings.isAuthUsed()){
		response.sendRedirect(CATBOT_MANAGE_ROOT+"main.jsp");
		return;
	}
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>웹수집기 관리도구</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<link rel="stylesheet" type="text/css" href="css/Login.css" />
<script type="text/javascript" src="<%=CATBOT_MANAGE_ROOT%>js/jquery-1.4.4.min.js"></script>
<script type="text/javascript" src="<%=CATBOT_MANAGE_ROOT%>js/common.js"></script>
<script>
	$(document).ready(function() {
		var message = "<%=message %>";
		if(message != "")
			alert(message);
	});

	function logout(){
		location.href="?cmd=logout";
	}
</script>
</head>

<body>
	<div id="LoginWrap">
		<div class="form">
			<div class="inputWrap">
			<form action="index.jsp" method="post">
				<div class="left">
					<input type="hidden" name="cmd" value="login" />
					<input type="text" name="username" onFocus="this.className='none'" onBlur="if ( this.value == '' ) { this.className='id' }" class='id' /><br />
					<input type="password" name="passwd" onFocus="this.className='none'" onBlur="if ( this.value == '' ) { this.className='pw' }" class="pw" />
				</div>
				&nbsp;&nbsp;<input type="image" src="css/images/login/loginBtn.gif" name="Submit" value="Submit" />
			</form>
			</div>
		</div>
	</div>
	<!--/wrap-->



	<!--footer-->
	<div id="footer_wrap">
		<div class="footer">
			<div class="copy">
				<p class="address">
				Copyright(c) Websqrd Co.,Ltd. All rights reserved. contact@websqrd.com<br />
				서울특별시 강남구 역삼동 641 경성빌딩 5층 Tel. 02-508-1151 Fax. 02-508-1153 대표자: 송상욱 사업자등록번호 : 220-88-03822
				</p>
			</div>
		</div>
	</div>
</body>
</html>
