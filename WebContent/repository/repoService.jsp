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
<%@page import="java.util.Properties"%>
<%@page import="java.net.URLEncoder"%>
<%@page import="java.sql.*"%>
<%@page import="com.websqrd.catbot.web.WebUtils"%>
<%@page import="com.websqrd.catbot.setting.CatbotSettings"%>
<%@page import="com.websqrd.catbot.setting.CatbotServerConfig"%>
<%@page import="com.websqrd.catbot.service.*"%>
<%@page import="com.websqrd.catbot.control.*"%>
<%@page import="com.websqrd.catbot.db.*"%>
<%@page import="com.websqrd.catbot.server.*"%>
<%@page import="com.websqrd.catbot.setting.*"%>

<%@include file="../common.jsp" %>

<%
	int cmd = Integer.parseInt(request.getParameter("cmd"));
	

switch(cmd){
	case 0:
		// 저장소설정을 추가한다.
	{
		String id = WebUtils.getString(request.getParameter("id"), "");
		String vendor = WebUtils.getString(request.getParameter("vendor"), "");
		String db = WebUtils.getString(request.getParameter("db"), "");
		String host = WebUtils.getString(request.getParameter("host"), "");
		String port = WebUtils.getString(request.getParameter("port"), "");
		String user = WebUtils.getString(request.getParameter("user"), "");
		String password = WebUtils.getString(request.getParameter("password"), "");
		String encoding = WebUtils.getString(request.getParameter("encoding"), "");
		String parameter = WebUtils.getString(request.getParameter("parameter"), "");
		if(CatbotSettings.existsRepo(id)){
			response.sendRedirect("main.jsp?message="+URLEncoder.encode(id+"저장소는 이미 존재합니다.", "utf-8"));
		}else{
			CatbotSettings.initRepo(id);
			CatbotSettings.updateRepo(id, "vendor", vendor);
			CatbotSettings.updateRepo(id, "db", db);
			CatbotSettings.updateRepo(id, "host", host);
			CatbotSettings.updateRepo(id, "port", port);
			CatbotSettings.updateRepo(id, "user", user);
			CatbotSettings.updateRepo(id, "password", password);
			CatbotSettings.updateRepo(id, "encoding", encoding);
			CatbotSettings.updateRepo(id, "parameter", parameter);
			response.sendRedirect("main.jsp?message="+URLEncoder.encode("추가되었습니다.", "utf-8"));
		}
		
		break;
	}
	
	case 1:
		// 저장소설정을 업데이트한다.
	{
		String id = WebUtils.getString(request.getParameter("id"), "");
		String vendor = WebUtils.getString(request.getParameter("vendor"), "");
		String db = WebUtils.getString(request.getParameter("db"), "");
		String host = WebUtils.getString(request.getParameter("host"), "");
		String port = WebUtils.getString(request.getParameter("port"), "");
		String user = WebUtils.getString(request.getParameter("user"), "");
		String password = WebUtils.getString(request.getParameter("password"), "");
		String encoding = WebUtils.getString(request.getParameter("encoding"), "");
		String parameter = WebUtils.getString(request.getParameter("parameter"), "");
		if(CatbotSettings.existsRepo(id)){
			CatbotSettings.updateRepo(id, "vendor", vendor);
			CatbotSettings.updateRepo(id, "db", db);
			CatbotSettings.updateRepo(id, "host", host);
			CatbotSettings.updateRepo(id, "port", port);
			CatbotSettings.updateRepo(id, "user", user);
			CatbotSettings.updateRepo(id, "password", password);
			CatbotSettings.updateRepo(id, "encoding", encoding);
			CatbotSettings.updateRepo(id, "parameter", parameter);
			response.sendRedirect("main.jsp?message="+URLEncoder.encode("업데이트 되었습니다.", "utf-8"));
			
		}else{
			response.sendRedirect("main.jsp?message="+URLEncoder.encode(id+"저장소는 존재하지 않습니다.", "utf-8"));
		}
		
		break;
	}
		
	case 2:
		// 저장소설정을 삭제한다.
	{
		String id = WebUtils.getString(request.getParameter("id"), "");
		
		if(CatbotSettings.existsRepo(id)){
			CatbotSettings.deleteRepo(id);
			response.sendRedirect("main.jsp?message="+URLEncoder.encode("삭제되었습니다.", "utf-8"));
		}else{
			response.sendRedirect("main.jsp?message="+URLEncoder.encode(id+"저장소는 존재하지 않습니다.", "utf-8"));
		}
		
		break;
	}
		
	case 3:
		// catbot.xml repository업데이트
	{
		String id = WebUtils.getString(request.getParameter("useId"), "");
		
		if(CatbotSettings.existsRepo(id)){
			CatbotSettings.setUseRepo(id);
			response.sendRedirect("main.jsp?message="+URLEncoder.encode("업데이트 되었습니다.", "utf-8"));
		}else{
			response.sendRedirect("main.jsp?message="+URLEncoder.encode(id+"저장소는 존재하지 않습니다.", "utf-8"));
		}
		
		break;
	}
		
	case 4:
		// 연결  테스트
	{
		String id = WebUtils.getString(request.getParameter("id"), "");
		String vendor = WebUtils.getString(request.getParameter("vendor"), "");
		String db = WebUtils.getString(request.getParameter("db"), "");
		String host = WebUtils.getString(request.getParameter("host"), "");
		String port = WebUtils.getString(request.getParameter("port"), "");
		String user = WebUtils.getString(request.getParameter("user"), "");
		String password = WebUtils.getString(request.getParameter("password"), "");
		String encoding = WebUtils.getString(request.getParameter("encoding"), "");
		String parameter = WebUtils.getString(request.getParameter("parameter"), "");
		if(CatbotSettings.existsRepo(id)){
			
			String JDBC_CLASS = null;
			String JDBC_URL = null;
			if(vendor.equals(RepositorySetting.VENDOR_DERBY)){
				JDBC_URL = "jdbc:derby:" + CatbotSettings.path(db);
				
				if ( parameter.isEmpty() == false )
					JDBC_URL = JDBC_URL + "?" + parameter.replaceAll(",", "&");
				
				JDBC_CLASS = "org.apache.derby.jdbc.EmbeddedDriver";
			}else if(vendor.equals(RepositorySetting.VENDOR_MYSQL)){
				JDBC_URL="jdbc:mysql://"+host+":"+port+"/"+db+"?useUnicode=yes&characterEncoding="+encoding;
				if ( parameter.isEmpty() == false )
					JDBC_URL = JDBC_URL + "&" + parameter.replaceAll(",", "&");
				JDBC_CLASS = "org.gjt.mm.mysql.Driver";
			}else if(vendor.equals(RepositorySetting.VENDOR_MSSQLSERVER)){
				JDBC_URL = "jdbc:jtds:sqlserver://"+host+":"+port+"/"+db;
				if ( parameter.isEmpty() == false )
					JDBC_URL = JDBC_URL + "?" + parameter.replaceAll(",", "&");
				JDBC_CLASS = "net.sourceforge.jtds.jdbc.Driver";
			}else if(vendor.equals(RepositorySetting.VENDOR_CUBRID)){
				JDBC_URL = "jdbc:CUBRID:"+host+":"+port+":"+db+":::";
				if ( parameter.isEmpty() == false )
					JDBC_URL = JDBC_URL + "?" + parameter.replaceAll(",", "&");
				JDBC_CLASS = "cubrid.jdbc.driver.CUBRIDDriver";
			}else if(vendor.equals(RepositorySetting.VENDOR_ORACLE)){
				JDBC_URL="jdbc:oracle:thin:@"+host+":"+port+":"+db;
				if ( parameter.isEmpty() == false )
					JDBC_URL = JDBC_URL + "?" + parameter.replaceAll(",", "&");
				JDBC_CLASS = "oracle.jdbc.driver.OracleDriver";
			}
			try {
				Class.forName(JDBC_CLASS).newInstance();
				DriverManager.getConnection(JDBC_URL, user, password);
			} catch (Exception e) {
				response.sendRedirect("repoEditor.jsp?id="+id+"&message="+URLEncoder.encode(id+ ":" + JDBC_URL+" Connection fail: ", "utf-8"));
				return;
			}
			
			response.sendRedirect("repoEditor.jsp?id="+id+"&message="+URLEncoder.encode("Connection Successful.", "utf-8"));
			
		}else{
			response.sendRedirect("repoEditor.jsp?id="+id+"&message="+URLEncoder.encode(id+"저장소는 존재하지 않습니다.", "utf-8"));
		}
		
		break;
	}
}
%>
