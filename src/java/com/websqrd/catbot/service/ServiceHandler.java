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

package com.websqrd.catbot.service;

import java.io.File;

import org.apache.derby.iapi.services.io.FileUtil;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.handler.HandlerList;
import org.mortbay.jetty.webapp.WebAppContext;
import org.mortbay.thread.QueuedThreadPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.websqrd.catbot.setting.CatbotServerConfig;
import com.websqrd.catbot.setting.CatbotSettings;

public class ServiceHandler extends CatBotServiceComponent{
	private static Logger logger = LoggerFactory.getLogger(ServiceHandler.class);
	//always use Root context
	private String MANAGE_CONTEXT = "/admin";
	
	private Server server;
	private int SERVER_PORT;
	private static ServiceHandler instance;
	
	public static ServiceHandler getInstance(){
		if(instance == null)
			instance = new ServiceHandler();
		
		return instance;
	}
	//WAS내장시에는 서블릿을 web.xml에 설정하지 않고 코드내에 설정한다.  
	private ServiceHandler() {
		
	}
	
	public static void main(String[] args) throws Exception {
		ServiceHandler s = new ServiceHandler();
		s.start();
		logger.info("ServiceHandler started!");
	}
	
	protected boolean start0() throws ServiceException{
		CatbotServerConfig config = CatbotSettings.getCatbotServerConfig();
		if(System.getProperty("server.port")!=null) {
			SERVER_PORT = Integer.parseInt(System.getProperty("server.port"));
		} else {
			SERVER_PORT = config.getInt("server.port");
		}
			
		server = new Server(SERVER_PORT);
		server.setAttribute("org.mortbay.jetty.Request.maxFormContentSize", new Integer(600000));
		HandlerList handlerList = new HandlerList();
		
		//1. WebAppContext
		String adminAppPath  = CatbotSettings.path(config.getString("server.admin.path"));
		logger.debug("ManageAppPath = "+ new File(adminAppPath).getAbsolutePath());
		WebAppContext webapp = new WebAppContext(adminAppPath, MANAGE_CONTEXT);
		webapp.getServletContext().setAttribute("CATBOT_MANAGE_ROOT", this.MANAGE_CONTEXT);
		//form 파라미터의 크기 제한을 없앰.
		webapp.getServletContext().getContextHandler().setMaxFormContentSize(-1);
		//검색엔진 home디렉토리밑 work디렉토리에 jsp work파일을 만든다. 
		File workDir = new File(CatbotSettings.path("work"));
		if(workDir.exists()){
			FileUtil.removeDirectory(workDir);
		}
		webapp.setTempDirectory(workDir);
		handlerList.addHandler(webapp);
		
		//2.
		
        server.setHandler(handlerList);
        
		try {
			//서버는 예외가 발생해도 시작처리되므로 미리 running 표시필요.
			isRunning = true;
			//stop을 명령하면 즉시 중지되도록.
			server.setStopAtShutdown(true);
			server.start();
			//Jetty는 2초후에 정지된다.
			if( server.getThreadPool() instanceof QueuedThreadPool ){
			   ((QueuedThreadPool) server.getThreadPool()).setMaxIdleTimeMs( 2000 );
			}
			logger.info("ServiceHandler Started! port = "+SERVER_PORT);
		} catch (Exception e) {
			throw new ServiceException(SERVER_PORT+" PORT로 웹서버 시작중 에러발생. ", e);
			
		}
		return true;
	}
	
	protected boolean shutdown0() throws ServiceException{
		try {
			logger.info("ServiceHandler stop requested...");
			server.stop();
			logger.info("Server Stop Ok!");
		} catch (Exception e) {
			throw new ServiceException(e);
		}
		return true;
	}

	public int getClientCount() {
		return server.getConnectors().length;
	}
}
