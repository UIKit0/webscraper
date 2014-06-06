package com.websqrd.catbot.servlet;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

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

public class CategoryInitlize extends HttpServlet {
	private static Logger logger = LoggerFactory.getLogger(CategoryInitlize.class);
	
	public void init()
	{
		
	}
	
	public CategoryInitlize()
	{
		
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    	doGet(request, response);
	    }
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    	
		String siteId = request.getParameter("site");
	    	String categoryId = request.getParameter("category");
	    	SiteConfig siteConfig =  CatbotSettings.getSiteConfig(siteId);
		CategoryConfig categoryConfig = CatbotSettings.getCategoryConfig(siteId, categoryId);	    	
	    	ScrapingDAO scrapingDAO = RepositoryHandler.getInstance().getScrapingDAO(siteConfig.getDataHandler());
	    	HashMap<String, String> data = new HashMap<String, String>();
	    	
	    	//data 초기화
	    	data.put("site", siteId);
	    	data.put("category", categoryId);
	    	scrapingDAO.delete(data) ;	    	
	    	scrapingDAO.close();
	    	
	    	//FILE 정리
	    	String SiteHomePath = CatbotSettings.getSiteHome(siteId);
	    	String cateIncFileName = SiteHomePath + "/schema_" + categoryId + ".xml"; 
    		File file = new File(cateIncFileName);
    		file.delete();	    	
    		
	    	//FILEREPO 정리
	    	//----------------
	    	String FILEREPOPath = SiteHomePath + "/FILEREPO/" + siteId+"/" + categoryId;
	    	File repoFP = new File(FILEREPOPath);
	    	if ( repoFP.isDirectory())
	    		repoFP.delete();
	    	//----------------	    	
	    	
	}		
}
