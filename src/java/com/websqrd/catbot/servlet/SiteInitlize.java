package com.websqrd.catbot.servlet;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONStringer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.websqrd.catbot.db.RepositoryHandler;
import com.websqrd.catbot.db.ScrapingDAO;
import com.websqrd.catbot.setting.CatbotSettings;
import com.websqrd.catbot.setting.CategoryConfig;
import com.websqrd.catbot.setting.SiteConfig;

public class SiteInitlize extends HttpServlet {
	private static Logger logger = LoggerFactory.getLogger(SiteInitlize.class);
	
	public void init()
	{
		
	}
	
	public SiteInitlize()
	{
		
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    	doGet(request, response);
	    }
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    	
		String siteId = request.getParameter("site");
	    	SiteConfig siteConfig =  CatbotSettings.getSiteConfig(siteId);
	    	ScrapingDAO scrapingDAO = RepositoryHandler.getInstance().getScrapingDAO(siteConfig.getDataHandler());
	    	
	    	//data 초기화
	    	HashMap<String, String> data = new HashMap<String, String>();
	    	data.put("site", siteId);
	    	scrapingDAO.truncate(data) ;
	    	scrapingDAO.close();
	    	
	    	//file 초기화
	    	//INC 파일 정리
	    	//----------------	    	
	    	String SiteHomePath = CatbotSettings.getSiteHome(siteId);
	    	
	    	Map<String, CategoryConfig> categoryMap = CatbotSettings.getSiteCategoryConfigList(siteId);
	    	Iterator itr = categoryMap.entrySet().iterator();
	    	
	    	while ( itr.hasNext() )
	    	{
	    		Map.Entry entry = (Map.Entry)itr.next();
	    		CategoryConfig cateConfig = (CategoryConfig)entry.getValue();
	    		String cateName = cateConfig.getCategoryName();
	    		
	    		String cateIncFileName = SiteHomePath + "/schema_" + cateName + ".xml"; 
	    		File file = new File(cateIncFileName);
	    		file.delete();
	    	}
	    	//----------------	    	
	    	
	    	//FILEREPO 정리
	    	//----------------
	    	String FILEREPOPath = SiteHomePath + "/FILEREPO/" + siteId;
	    	File repoFP = new File(FILEREPOPath);
	    	if ( repoFP.isDirectory())
	    		repoFP.delete();
	    	//----------------	    	
	    	
	}		
}
