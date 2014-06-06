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
<%@page import="com.websqrd.catbot.db.DBHandler"%>
<%@page import="java.sql.ResultSet"%>
<%@page import="java.sql.SQLException"%>
<%@page import="java.io.StringWriter"%>
<%@page import="com.websqrd.libs.common.Formatter"%>
<%@page import="java.sql.ResultSetMetaData"%>

<%@include file="../common.jsp" %>

<%
	String dbType = WebUtils.getString(request.getParameter("dbType"),"");
	String dbName = WebUtils.getString(request.getParameter("dbname"),"");
	String collection = WebUtils.getString(request.getParameter("collection"),"");
	
	String userSQL = WebUtils.getString(request.getParameter("userSQL"),"").trim();
	String executeType = WebUtils.getString(request.getParameter("executeType"),"U");
	String message = "";
	String resultString = "";
	ResultSet rs = null;
	int n = 0;

%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<%@page import="java.sql.Driver"%>
<%@page import="java.sql.DriverManager"%>
<%@page import="java.util.Properties"%>
<%@page import="java.sql.Connection"%>
<%@page import="java.sql.Statement"%><html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>catbot 검색엔진 관리도구</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
<link href="<%=CATBOT_MANAGE_ROOT%>css/reset.css" rel="stylesheet" type="text/css" />
<link href="<%=CATBOT_MANAGE_ROOT%>css/style.css" rel="stylesheet" type="text/css" />
<link href="<%=CATBOT_MANAGE_ROOT%>css/jquery-ui.css" type="text/css" rel="stylesheet" />
<link href="<%=CATBOT_MANAGE_ROOT%>css/ui.jqgrid.css" type="text/css" rel="stylesheet" />
<!--[if lte IE 6]>
<link href="<%=CATBOT_MANAGE_ROOT%>css/style_ie.css" rel="stylesheet" type="text/css" />
<![endif]-->
<script type="text/javascript" src="<%=CATBOT_MANAGE_ROOT%>js/common.js"></script>
	<script type="text/javascript" src="<%=CATBOT_MANAGE_ROOT%>js/jquery-1.4.4.min.js"></script>
	<script type="text/javascript" src="<%=CATBOT_MANAGE_ROOT%>js/grid.locale-en.js"></script> 
	<script type="text/javascript" src="<%=CATBOT_MANAGE_ROOT%>js/jquery.jqGrid.min.js"></script> 
	<script type="text/javascript">
		
		function updateQuery(){
			if($("#userSQL").val() == ''){
				alert("SQL문이 없습니다.");
				return;
			}
			
			if($("#dbType").val() == 'datasource'){
				$("#executeType").val('U_D');
			}else if($("#dbType").val() == 'catbot'){
				$("#executeType").val('U');
			}else{
				if($("#dbname").val() == 'catbot'){
					$("#executeType").val('U_DB');
				}else{
					$("#executeType").val('U_MONDB');
				}
			}
			
			$("#analyzerForm").submit();
		}
		function selectQuery(){
			if($("#userSQL").val() == ''){
				alert("SQL문이 없습니다.");
				return;
			}
			
			if($("#dbType").val() == 'datasource'){
				$("#executeType").val('S_D');
			}else if($("#dbType").val() == 'catbot'){
				$("#executeType").val('S');
			}else{
				if($("#dbname").val() == 'db'){
					$("#executeType").val('S_DB');
				}else{
					$("#executeType").val('S_MONDB');
				}
			}
			$("#analyzerForm").submit();
		}

		function selectOption(){
			$("#executeType").val('N');
			$("#analyzerForm").submit();
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
		<h3>테스트</h3>
			<ul class="latest">
			<li><a href="<%=CATBOT_MANAGE_ROOT%>tester/dbTester.jsp" class="selected">DB테스트</a></li>
			</ul>
	</div>
</div><!-- E : #sidebar -->


<div id="mainContent">

<h2>DB테스트</h2>
<form action="dbTester.jsp" method="post" id="analyzerForm">
<input type="hidden" name="executeType" id="executeType"/>
<div class="fbox">
<table class="tbl01">
<colgroup><col width="15%" /><col width="" /></colgroup>
<tbody>
	<tr>
		<th>DB 선택</th>
		<td style="text-align:left">
		<select name="dbType" id="dbType" onchange="javascript:selectOption()">
		<option value="catbot" <%=dbType.equals("catbot") ? "selected" : "" %> >패스트캣 내부 DB</option>
		<option value=datasource <%=dbType.equals("datasource") ? "selected" : "" %> >데이터소스</option>
		</select>
		<%
			if(dbType.equals("datasource")){
		%>
		<select name="collection" id="collection" onchange="javascript:selectOption()">
		<option value="fetchDB" >fetchDB</option>
		</select>
		<%
			}
		
		if(dbType.equals("datasource")){
			%>
			<i>※외부데이터 조회테스트는 100건으로 제한됩니다.</i> 
			<%
		}
		%>
		</td>
	</tr>
	<tr>
		<th>실행할 SQL</th>
		<td style="text-align:left"><textarea name="userSQL" cols="99" rows="10" id="userSQL"><%=userSQL %></textarea></td>
	</tr>
	<tr>
		<td colspan="2"><a href="javascript:selectQuery()" class="btn">SELECT 실행</a>
		<a href="javascript:updateQuery()" class="btn">업데이트</a></td>
	</tr>
	executeType: <%=executeType  %>, userSQL: <%=userSQL %>
	<%
		Connection extConn = null;
			
		if(userSQL.length() > 0){
			DBHandler dbHandler = DBHandler.getInstance();
			if(executeType.equalsIgnoreCase("U")){
				try{
					long st = System.currentTimeMillis();
					n = dbHandler.updateOrInsertSQL(userSQL);
					resultString = n+" 개의 행이 적용되었습니다. "+Formatter.getFormatTime(System.currentTimeMillis() - st);
				}catch(SQLException e){
					message = e.getMessage();
				}
			}else if(executeType.equalsIgnoreCase("S_DB")){
				try{
					long st = System.currentTimeMillis();
					rs = dbHandler.selectSQL(userSQL);
					resultString = "성공적으로 실행되었습니다. "+Formatter.getFormatTime(System.currentTimeMillis() - st);
				}catch(SQLException e){
					message = e.getMessage();
				}
			}else if(executeType.equalsIgnoreCase("U_D")){
			
			}
	%>
		<tr>
		<th>메시지</th>
		<td style='text-align:left'>
			<%=message %>
		</td>
		</tr>
		<tr>
		<th>실행결과</th>
		<td style='text-align:left'><%=resultString%></td>
		</tr>
		<%
	}
	%>
</tbody>
</table>
</div>

<%
if(executeType.startsWith("S_")){
%>
	<table id="sqlResult"></table>
<%
}
%>
</form>
	
<!-- E : #mainContent --></div>
	
<!-- footer -->
<%@include file="../footer.jsp" %>
	
</div><!-- //E : #container -->

</body>

</html>
<%
if(executeType.startsWith("S_")){
	if(rs != null){
		ResultSetMetaData meta = rs.getMetaData();
		int cc = meta.getColumnCount();
		
		StringBuffer sb = new StringBuffer(); 
		StringBuffer sb2 = new StringBuffer(); 
		for(int i=1; i<=cc; i++){
			//colNames
			sb.append("'");
			sb.append(meta.getColumnName(i));
			sb.append("'");
			if(i < cc)
				sb.append(",");
			
			//colModel
			sb2.append("{name:'");
			sb2.append(meta.getColumnName(i));
			sb2.append("'}");
			if(i < cc)
				sb2.append(",");
		}
		out.println("<script>");
		out.println("colNames=[\n"+sb.toString()+"\n];");
		out.println("colModel=[\n"+sb2.toString()+"\n];");
		out.println("</script>");
		//
		sb = new StringBuffer(); 
		while(rs.next()){
			sb.append("{");
			for(int i=1; i<=cc; i++){
				sb.append("\""+meta.getColumnName(i)+"\"");
				sb.append(":\"");
				String str = rs.getString(i);

				if(str != null){
					sb.append(str.replaceAll("\t","&#09;").replaceAll("\r\n","&#13;&#10;").replaceAll("\r","&#13;&#10;").replaceAll("\n","&#13;&#10;").replaceAll("\\\\","&#92;").replaceAll("\"","&#34;").replaceAll("\'","&#39;"));
				}else{
					sb.append(str);
				}
				sb.append("\"");
				if(i < cc)
					sb.append(",");
			}
			sb.append("},\n");
		}
		String str = sb.toString();
		out.println("<script>");
		out.println("mydata=[\n");
		if(str.length() > 0){
			out.println(str.substring(0, str.length()-2));
		}
		out.println("\n]");
		%>
		$("#sqlResult").jqGrid({
			datatype: "local",
			height: 250,
		   	colNames:colNames,
		   	colModel:colModel,
		   	width: 740
		});
		
		for(var i=0; i<=mydata.length; i++){
			if(mydata[i]){
				$("#sqlResult").jqGrid('addRowData',i+1,mydata[i]);
			}
		}

		<%
		out.println("</script>");
	}
}
if(rs != null){
	try{
		rs.close();
	}catch(Exception e){ }
}
if(extConn != null){
	try{
		extConn.close();
	}catch(Exception e){ }
}
%>
