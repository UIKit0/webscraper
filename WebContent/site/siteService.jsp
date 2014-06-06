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

<%@page import="com.websqrd.catbot.web.WebUtils"%>
<%@page import="com.websqrd.catbot.job.WebScrapingJob"%>
<%@page import="com.websqrd.catbot.control.JobController"%>
<%@ page contentType="text/html; charset=UTF-8"%> 

<%@page import="java.util.Date"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.io.File"%>
<%@page import="java.io.FileOutputStream"%>
<%@page import="java.util.Properties"%>
<%@page import="java.net.URLEncoder"%>
<%@page import="java.io.*"%>
<%@page import="java.net.*"%>
<%@page import="java.util.*"%>
<%@page import="org.apache.commons.io.FileUtils"%>
<%@page import="com.websqrd.catbot.setting.*"%>
<%@page import="com.websqrd.libs.xml.*"%>
<%@page import="com.websqrd.catbot.web.*"%>
<%@page import="com.websqrd.catbot.control.*"%>
<%@page import="com.websqrd.catbot.job.*"%>
<%@page import="org.htmlcleaner.*"%>

<%@include file="../common.jsp" %>

<%
	int cmd = Integer.parseInt(request.getParameter("cmd"));

	switch (cmd) {
		//ADD site
	case 0: {
		String site = "";
		site = request.getParameter("site");

		final String SITE_LIST_KEY = "site.list";
		CatbotServerConfig irconfig = CatbotSettings.getCatbotServerConfig();
		Properties props = irconfig.getProperties();
		String siteList = props.getProperty(SITE_LIST_KEY);
		String[] list = siteList.split(",");

		boolean isExist = false;

		for (int i = 0; i < list.length; i++) {
			String s = list[i];
			if (site.equalsIgnoreCase(s)) {
				isExist = true;
				break;
			}
		}

		if (!isExist) {
			if (list.length == 0)
				props.put(SITE_LIST_KEY, site);
			else
				props.put(SITE_LIST_KEY, siteList + "," + site);
		
			CatbotSettings.storeServerConfig(irconfig);
				
			String siteHome = CatbotSettings.getSiteHome(site);
			File f = new File(siteHome);
			f.mkdir();
		
			//IRSettings.initDatasource(collection);
			//IRSettings.initSchema(collection);
			CatbotSettings.initSite(site);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String startDt = sdf.format(new Date());
			//IRSettings.storeIndextime(site, "", startDt, startDt, "0", 0);
				
			//BoardService.getInstance().putCollectionHandler(collection, new CollectionHandler(collection));
				
			response.sendRedirect("main.jsp?message=" + URLEncoder.encode(URLEncoder.encode(site + " 사이트를 생성하였습니다.", "utf-8"),"utf-8"));
		} else {
			response.sendRedirect("main.jsp?message=" + URLEncoder.encode(URLEncoder.encode("설정파일에 " + site + " 컬렉션이 존재합니다.", "utf-8"),"utf-8"));
		}

		break;
	}
		//REMOVE site
	case 1: {
		String site = "";
		site = request.getParameter("site");
		
		CatbotConfig catbotConfig = CatbotSettings.getGlobalConfig();
		List<String> siteList = catbotConfig.getSiteList();
		for (int i = 0; i < siteList.size(); i++) {
			String s = siteList.get(i);
			/* if (!(site.equalsIgnoreCase(s))) {
				listString += s;
				listString += ",";
			} */
		}
			
		/* if (listString.length() > 0) {
			listString = listString.substring(0, listString.length() - 1);
		}
		System.out.println("BB > ");
		props.put(SITE_LIST_KEY, listString);
		CatbotSettings.storeServerConfig(irconfig); */
		//컬렉션 파일들 삭제.
		String siteHome = CatbotSettings.getSiteHome(site);
		File siteDirectory = new File(siteHome);
		FileUtils.deleteDirectory(siteDirectory);
			
		response.sendRedirect("main.jsp?message=" + URLEncoder.encode(URLEncoder.encode(site + " 사이트를 삭제하였습니다.","utf-8"),"utf-8"));
			
			//}
		break;
		}
	
	//url에 해당하는 소스를 가져와서 htmlcleaner로 복구한뒤 반환한다.
	case 2: {
		//response.setContentType("text/html");
		String url = request.getParameter("url");
		String enc = request.getParameter("enc");
		String source = WebUtils.getContentByURL(url, enc);
		//System.out.println(url);
		//System.out.println(enc);
		//System.out.println(source);
		try {
			HtmlCleaner cl = new HtmlCleaner();	
			CleanerProperties props = cl.getProperties();
			props.setTranslateSpecialEntities(true);
			props.setOmitComments(false);
			props.setNamespacesAware(false);
			TagNode tagNode = cl.clean(source);
			String cleanedSource = new PrettyXmlSerializer(props).getXmlAsString(tagNode);
			cleanedSource.replaceAll("/^\\s+$", "");
			out.print(cleanedSource);
			System.out.println("2>>"+cleanedSource);
			//pageContent 이미 xml임.
		//			logger.info(pageContent);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		break;
		
	}
	//parsing xpath
	case 4: {
		//response.setContentType("text/html");
		/* String xpath = request.getParameter("xpath");
		String source = request.getParameter("source");
		//source = URLDecoder.decode(source, "euc-kr");
		String[] result = XmlUtils.getBlock(xpath, source);
		out.println(result.length);
		out.println("<root>");
		for(int i=0;i< result.length; i++){
			out.println(result[i]);
			out.println();
		}
		out.println("</root>"); */
		
		break;
	}
	case 5: {
		String site = request.getParameter("site");
		String category = request.getParameter("category");
		WebScrapingJob job = new WebScrapingJob();
		job.setArgs(new String[]{site, category, "GETNEW", "test"});
		JobResult jobResult = JobController.getInstance().offer(job);
		Object result = jobResult.take();
		if(result != null && result instanceof JobResultScraping){
			JobResultScraping r = (JobResultScraping)result;
			out.println(r.testResultBuffer);
		}
		break;
	}
	
	default:
		break;
	}
%>
