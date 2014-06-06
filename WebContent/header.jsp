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
<%@page import="com.websqrd.catbot.setting.CatbotSettings"%>

<%
Object authObj = session.getAttribute("authorized");
boolean isAuthorized = (authObj != null) ? true : false;
Object accessLogObj = session.getAttribute("lastAccessLog");
String[] accessLog = null;
if(accessLogObj != null){
	accessLog = (String[])accessLogObj;
}

if(!CatbotSettings.isAuthUsed()){
	isAuthorized = true;
	authObj = "익명사용자";
	accessLog = new String[]{"",""};
}
%>

<% { %>
<% String __CATBOT_MANAGE_ROOT__ = (String)application.getAttribute("CATBOT_MANAGE_ROOT")+"/"; %>
<div id="header">
	<div><img src="<%=__CATBOT_MANAGE_ROOT__%>images/fastcatsearch-logo.gif" /></div>
	<div id="loginbar">
		<% if(isAuthorized) { %>
		<!-- 로그온시 정보 -->
		<div id="login_box">
			<p class="logname"><%=(String)authObj %>님 <input type="button" class="btn" value="로그아웃" onclick="javascript:logout()"/></p>
		</div>
		<% } %>
	</div>
	<!-- GNB -->
	<div class="menucontainer">
		<div class="menu">
		<ul>
			<!-- 
			<li><a href="<%=__CATBOT_MANAGE_ROOT__%>main.jsp">홈</a></li>
			 -->
			<li><a href="<%=__CATBOT_MANAGE_ROOT__%>repository/main.jsp">저장소</a></li>
			<li><a href="<%=__CATBOT_MANAGE_ROOT__%>site/main.jsp">사이트관리</a>
				<table><tr><td>
					<ul>
					</ul>
				</td></tr></table>
			</li>
			<li><a href="<%=__CATBOT_MANAGE_ROOT__%>data/main.jsp">데이터관리</a>
			<li><a href="<%=__CATBOT_MANAGE_ROOT__%>scraping/schedule.jsp">스케쥴</a></li>
			<li><a href="<%=__CATBOT_MANAGE_ROOT__%>stat/scrapingStat.jsp">통계</a>
				<table><tr><td>
					<ul>
						<li><a href="<%=__CATBOT_MANAGE_ROOT__%>stat/scrapingStat.jsp">수집현황</a></li>
						<li><a href="<%=__CATBOT_MANAGE_ROOT__%>stat/history.jsp">수집히스토리</a></li>
					</ul>
				</td></tr></table>
			</li>
			<li><a class="drop" href="<%=__CATBOT_MANAGE_ROOT__%>monitoring/main.jsp">모니터링</a></li>
			
			<li><a href="<%=__CATBOT_MANAGE_ROOT__%>management/main.jsp">환경설정</a>
				<table><tr><td>
					<ul>
						<li><a href="<%=__CATBOT_MANAGE_ROOT__%>management/main.jsp">시스템정보</a></li>
						<li><a href="<%=__CATBOT_MANAGE_ROOT__%>management/account.jsp">계정관리</a></li>
						<li><a href="<%=__CATBOT_MANAGE_ROOT__%>management/config.jsp">사용자설정</a></li>
					</ul>
				</td></tr></table>
			</li>
		</ul>
		</div><!-- // E : menu -->
	</div><!-- // E : menucontainer -->
	<script type="text/javascript">
		function logout(){
			location.href="<%=__CATBOT_MANAGE_ROOT__%>index.jsp?cmd=logout";
		}
	</script>
</div><!-- // E : #header -->
<% } %>
