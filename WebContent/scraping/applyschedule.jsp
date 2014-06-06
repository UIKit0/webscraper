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
<%@page import="com.websqrd.catbot.setting.CatbotSettings"%>
<%@page import="com.websqrd.catbot.setting.*"%>
<%@page import="com.websqrd.catbot.control.*"%>
<%@page import="com.websqrd.catbot.db.*"%>

<%@page contentType="text/html; charset=UTF-8"%>
<%@page import="com.websqrd.libs.common.Formatter"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Date"%>
<%@page import="java.sql.Timestamp"%>
<%@page import="java.util.Calendar"%>
<%@page import="java.util.*"%>
<%@page import="java.util.Map.Entry"%>

<%@include file="../common.jsp"%>

<%
	CatbotConfig irConfig = CatbotSettings.getGlobalConfig(true);
	List<String> siteList = irConfig.getSiteList();
	DBHandler dbHandler = DBHandler.getInstance();
	boolean isActive = false;
	String cmd = WebUtils.getString(request.getParameter("cmd"), "");

	if (cmd.equals("1")) {
		String slcSite = request.getParameter("slc_site");
		String category = request.getParameter("slc_category");
		int p_day = WebUtils.getInt(request.getParameter("p_day"), 0);
		int p_hour = WebUtils.getInt(request.getParameter("p_hour"), 0);
		int p_minute = WebUtils.getInt(request.getParameter("p_minute"), 0);
		int s_hour = WebUtils.getInt(request.getParameter("s_hour"), -1);
		int s_minute = WebUtils.getInt(request.getParameter("s_minute"), -1);
		int period = p_day * 60 * 60 * 24 + p_hour * 60 * 60 + p_minute * 60;
		
		if (period > 0 && s_hour >= 0 && s_minute >= 0) {
			try {
				Calendar cal = Calendar.getInstance();
				cal.setTime(new Date());
				cal.set(Calendar.HOUR_OF_DAY, s_hour);
				cal.set(Calendar.MINUTE, s_minute);
				
				Timestamp startTime = new Timestamp(cal.getTimeInMillis());
				int rsCount = dbHandler.getScrapingSchedule().updateOrInsert(slcSite, category, period, startTime, isActive);
				
				if (rsCount > 0)
					out.print(0);
				else
					out.print(1);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		JobController.getInstance().toggleIndexingSchedule(slcSite, category, isActive);
	}

	else if (cmd.equals("2")) {
		String slcSite = request.getParameter("slc_site");
		String category = request.getParameter("slc_category");
		String isActiveStr = WebUtils.getString(request.getParameter("isActive"), "0");
		if (isActiveStr.equals("1")) {
			isActive = true;
		}	
		
		try {
			int rsCount = dbHandler.getScrapingSchedule().updateStatus(slcSite, category, isActive);
			if (rsCount > 0)
				{
				if ( isActive )
					out.print(0);
				}
			else
				out.print(1);
		} catch (Exception e) {
			e.printStackTrace();
		}

		//JobScheduler RELOAD!
		JobController.getInstance().toggleIndexingSchedule(slcSite, category, isActive);

	}
%>
