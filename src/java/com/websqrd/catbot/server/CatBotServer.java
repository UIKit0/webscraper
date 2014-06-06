/*
 * Copyright (C) 2011 WebSquared Inc. http://websqrd.com
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package com.websqrd.catbot.server;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.websqrd.catbot.control.JobController;
import com.websqrd.catbot.db.DBHandler;
import com.websqrd.catbot.db.RepositoryHandler;
import com.websqrd.catbot.scraping.modifier.ScrapModifierLoader;
import com.websqrd.catbot.service.ServiceException;
import com.websqrd.catbot.service.ServiceHandler;
import com.websqrd.catbot.setting.CatbotConfig;
import com.websqrd.catbot.setting.CatbotSettings;

public class CatBotServer {
	
	private ServiceHandler serviceHandler;
	private DBHandler dbHandler;
	private RepositoryHandler repositoryHandler;
	private JobController jobController;
	public static long startTime;
	public static CatBotServer instance;
	private static Logger logger;
	private boolean isRunning;
	
	public static void main(String... args) throws ServiceException{
		CatBotServer server = new CatBotServer();
		server.init(args);
		server.start();
	}
	
	public boolean isRunning(){
		return isRunning;
	}
	
	public static CatBotServer getInstance(){
		return instance;
	}
	
	public CatBotServer(){ }
		
	public void init(String[] args) throws ServiceException{
		//검색엔진으로 전달되는 args를 받아서 셋팅해준다.
		//대부분 -D옵션을 통해 전달받으므로 아직까지는 셋팅할 내용은 없다.
	}
	
	public boolean start() throws ServiceException{
		//초기화 및 서비스시작을 start로 옮김.
		//초기화로직이 init에 존재할 경우, 관리도구에서 검색엔진을 재시작할때, init을 호출하지 않으므로, 초기화를 건너뛰게 됨.
		
		instance = this;
		String ServerHome = System.getProperty("server.home");
		if(ServerHome == null){
			System.err.println("Warning! Please set env variable \"server.home\".");
			System.out.println("Usage : java com.websqrd.catbot.server.CatbotServer -Dserver.home=[home path]");
			System.exit(1);
		}		
		
		File f = new File(ServerHome);
		if(!f.exists()){
			System.err.println("Warning! Path \""+ServerHome+"\" is not exist!");
			System.out.println("Usage : java com.websqrd.catbot.server.CatbotServer -Dserver.home=[home path]");
			System.exit(1);
		}
		
		CatbotSettings.setHome(ServerHome);
		
		try{
			dbHandler = DBHandler.getInstance();
			repositoryHandler = RepositoryHandler.getInstance();
			serviceHandler = ServiceHandler.getInstance();
			jobController = JobController.getInstance();
		}catch(Exception e){
			throw new ServiceException("서비스를 초기화하지 못했습니다.",e);
		}
		
		logger = LoggerFactory.getLogger(CatBotServer.class);
		
		try{
			dbHandler.start();
			if(repositoryHandler != null){
				try{
					repositoryHandler.start();
				}catch(Exception e){
					e.printStackTrace();
					logger.error("RepositoryHandler를 시작하지 못했습니다.");
				}
			}
			if(jobController != null)
				jobController.start();
			if(serviceHandler != null)
				serviceHandler.start();
			
		}catch(ServiceException e){
			logger.error("CatbotServer 시작에 실패했습니다.", e);
			stop();
			return false;
		}
		//수집기의 뛰우기 전에 실행 됐던 작업에 대해서는 실패로 셋팅
		dbHandler.getScrapingResult().updateRunning2Failed();
		
		CatbotConfig config = CatbotSettings.getGlobalConfig();
		ScrapModifierLoader.load(config.getModifierMap());
		
		startTime = System.currentTimeMillis();
		logger.info("CatbotServer started!");
		isRunning = true;
		return true;
	}
	
	public boolean stop() throws ServiceException{
		jobController.shutdown();
		if(repositoryHandler != null)
			repositoryHandler.shutdown();
		if(serviceHandler != null)
			serviceHandler.shutdown();
		dbHandler.shutdown();
		logger.info("CatbotServer shutdown!");
		isRunning = false;
		return true;
	}
	
	public void destroy() throws ServiceException{
		dbHandler = null;
		serviceHandler = null;
		repositoryHandler = null;
		jobController = null;
	}
	
}
