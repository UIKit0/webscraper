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
<%@page import="com.websqrd.catbot.web.WebUtils"%>
<%@page import="com.websqrd.catbot.setting.CatbotSettings"%>
<%@page import="com.websqrd.catbot.setting.CatbotServerConfig"%>
<%@page import="com.websqrd.catbot.service.*"%>
<%@page import="com.websqrd.catbot.control.*"%>
<%@page import="com.websqrd.catbot.db.*"%>
<%@page import="com.websqrd.catbot.server.*"%>

<%@include file="../common.jsp" %>

<%
	int cmd = Integer.parseInt(request.getParameter("cmd"));
String collection = request.getParameter("collection");

switch(cmd){
	//컴포넌트 정지
	case 3:
	{
		int component = Integer.parseInt(request.getParameter("component"));
		int cmd2 = Integer.parseInt(request.getParameter("cmd2"));
		//
		//cmd2 := 0 시작, 1 정지, 2 재시작
		
		if(cmd2 == 0){
	//시작
	if(component == 0){
		//CatBotServer
		if(CatBotServer.getInstance().isRunning()){
			response.sendRedirect("main.jsp?message="+URLEncoder.encode("CatBotServer가 이미 시작되었습니다.", "utf-8"));
		}else{
			if(CatBotServer.getInstance().start()){
				response.sendRedirect("main.jsp");
			}else{
				response.sendRedirect("main.jsp?message="+URLEncoder.encode("CatBotServer 시작에 실패하였습니다.", "utf-8"));
			}
		}
	}else if(component == 1){
		//ServiceHandler
		if(ServiceHandler.getInstance().isRunning()){
			response.sendRedirect("main.jsp?message="+URLEncoder.encode("ServiceHandler가 이미 시작되었습니다.", "utf-8"));
		}else{
			if(ServiceHandler.getInstance().start()){
				response.sendRedirect("main.jsp");
			}else{
				response.sendRedirect("main.jsp?message="+URLEncoder.encode("ServiceHandler 시작에 실패하였습니다.", "utf-8"));
			}
		}
		
	}else if(component == 3){
		//DBHandler
		if(DBHandler.getInstance().isRunning()){
			response.sendRedirect("main.jsp?message="+URLEncoder.encode("DBHandler가 이미 시작되었습니다.", "utf-8"));
		}else{
			if(DBHandler.getInstance().start()){
				response.sendRedirect("main.jsp");
			}else{
				response.sendRedirect("main.jsp?message="+URLEncoder.encode("DBHandler 시작에 실패하였습니다.", "utf-8"));
			}
		}
		
	}else if(component == 4){
		//JobController
		if(JobController.getInstance().isRunning()){
			response.sendRedirect("main.jsp?message="+URLEncoder.encode("JobController가 이미 시작되었습니다.", "utf-8"));
		}else{
			if(JobController.getInstance().start()){
				response.sendRedirect("main.jsp");
			}else{
				response.sendRedirect("main.jsp?message="+URLEncoder.encode("JobController 시작에 실패하였습니다.", "utf-8"));
			}
		}
	}else if(component == 5){
		//RepositoryHandler
		if(RepositoryHandler.getInstance().isRunning()){
			response.sendRedirect("main.jsp?message="+URLEncoder.encode("RepositoryHandler가 이미 시작되었습니다.", "utf-8"));
		}else{
			if(RepositoryHandler.getInstance().start()){
				response.sendRedirect("main.jsp");
			}else{
				response.sendRedirect("main.jsp?message="+URLEncoder.encode("RepositoryHandler 시작에 실패하였습니다.", "utf-8"));
			}
		}
	}
	
		}else if(cmd2 == 1){
	//정지
	if(component == 0){
		//CatBotServer
		if(!CatBotServer.getInstance().isRunning()){
			response.sendRedirect("main.jsp?message="+URLEncoder.encode("CatBotServer가 이미 정지되었습니다.", "utf-8"));
		}else{
			if(CatBotServer.getInstance().stop()){
				response.sendRedirect("main.jsp");
			}else{
				response.sendRedirect("main.jsp?message="+URLEncoder.encode("CatBotServer 정지에 실패하였습니다.", "utf-8"));
			}
		}
	}else if(component == 1){
		//ServiceHandler
		if(!ServiceHandler.getInstance().isRunning()){
			response.sendRedirect("main.jsp?message="+URLEncoder.encode("ServiceHandler가 이미 정지되었습니다.", "utf-8"));
		}else{
			if(ServiceHandler.getInstance().shutdown()){
				response.sendRedirect("main.jsp");
			}else{
				response.sendRedirect("main.jsp?message="+URLEncoder.encode("ServiceHandler 정지에 실패하였습니다.", "utf-8"));
			}
		}
	}else if(component == 3){
		//DBHandler
		if(!DBHandler.getInstance().isRunning()){
			response.sendRedirect("main.jsp?message="+URLEncoder.encode("DBHandler가 이미 정지되었습니다.", "utf-8"));
		}else{
			if(DBHandler.getInstance().shutdown()){
				response.sendRedirect("main.jsp");
			}else{
				response.sendRedirect("main.jsp?message="+URLEncoder.encode("DBHandler 정지에 실패하였습니다.", "utf-8"));
			}
		}
	}else if(component == 4){
		//JobController
		if(!JobController.getInstance().isRunning()){
			response.sendRedirect("main.jsp?message="+URLEncoder.encode("JobController가 이미 정지되었습니다.", "utf-8"));
		}else{
			if(JobController.getInstance().shutdown()){
				response.sendRedirect("main.jsp");
			}else{
				response.sendRedirect("main.jsp?message="+URLEncoder.encode("JobController 정지에 실패하였습니다.", "utf-8"));
			}
		}
	}else if(component == 5){
		//RespositoryHandler
		if(!RepositoryHandler.getInstance().isRunning()){
			response.sendRedirect("main.jsp?message="+URLEncoder.encode("RepositoryHandler가 이미 정지되었습니다.", "utf-8"));
		}else{
			if(RepositoryHandler.getInstance().shutdown()){
				response.sendRedirect("main.jsp");
			}else{
				response.sendRedirect("main.jsp?message="+URLEncoder.encode("RepositoryHandler 정지에 실패하였습니다.", "utf-8"));
			}
		}
	}
	
		}else if(cmd2 == 2){
	//재시작
	if(component == 0){
		//CatBotServer
		if(CatBotServer.getInstance().isRunning()){
			if(CatBotServer.getInstance().stop() && CatBotServer.getInstance().start()){
				response.sendRedirect("main.jsp");
			}else{
				response.sendRedirect("main.jsp?message="+URLEncoder.encode("CatBotServer 재시작에 실패하였습니다.", "utf-8"));
			}
		}else{
			if(CatBotServer.getInstance().start()){
				response.sendRedirect("main.jsp");
			}else{
				response.sendRedirect("main.jsp?message="+URLEncoder.encode("CatBotServer 재시작에 실패하였습니다.", "utf-8"));
			}
		}
	}else if(component == 1){
		//ServiceHandler
		if(ServiceHandler.getInstance().restart()){
			response.sendRedirect("main.jsp");
		}else{
			response.sendRedirect("main.jsp?message="+URLEncoder.encode("ServiceHandler 재시작에 실패하였습니다.", "utf-8"));
		}
	}else if(component == 3){
		//DBHandler
		if(DBHandler.getInstance().restart()){
			response.sendRedirect("main.jsp?message="+URLEncoder.encode("DBHandler가 재시작 되었습니다.", "utf-8"));
		}else{
			response.sendRedirect("main.jsp?message="+URLEncoder.encode("DBHandler 재시작에 실패하였습니다.", "utf-8"));
		}
	}else if(component == 4){
		//JobController
		if(JobController.getInstance().restart()){
			response.sendRedirect("main.jsp?message="+URLEncoder.encode("JobController가 재시작 되었습니다.", "utf-8"));
		}else{
			response.sendRedirect("main.jsp?message="+URLEncoder.encode("JobController 재시작에 실패하였습니다.", "utf-8"));
		}
	}else if(component == 5){
		//RespositoryHandler
		if(RepositoryHandler.getInstance().restart()){
			response.sendRedirect("main.jsp?message="+URLEncoder.encode("RepositoryHandler가 재시작 되었습니다.", "utf-8"));
		}else{
			response.sendRedirect("main.jsp?message="+URLEncoder.encode("RepositoryHandler 재시작에 실패하였습니다.", "utf-8"));
		}
	}
	
		}
		
		break;
	}
	
}
%>
