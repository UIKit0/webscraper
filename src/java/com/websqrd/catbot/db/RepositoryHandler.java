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

package com.websqrd.catbot.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Iterator;

import com.websqrd.catbot.service.CatBotServiceComponent;
import com.websqrd.catbot.service.ServiceException;
import com.websqrd.catbot.setting.CatbotConfig;
import com.websqrd.catbot.setting.CatbotSettings;
import com.websqrd.catbot.setting.DataHandlerConfig;
import com.websqrd.catbot.setting.RepositorySetting;
import com.websqrd.libs.db.DBConnectionPool;
import com.websqrd.libs.db.DBConnectionPoolManager;
import com.websqrd.libs.db.JDBCProfile;

public class RepositoryHandler extends CatBotServiceComponent {

	protected Connection conn = null;
	private static RepositoryHandler instance = new RepositoryHandler();
	private RepositorySetting repositorySetting;
	private static DBConnectionPoolManager dbManager = DBConnectionPoolManager.getInstance();
	private static DBConnectionPool pool = null;
	
	public static RepositoryHandler getInstance() {
		return instance;
	}

	private RepositoryHandler() {}

	protected boolean start0() throws ServiceException {
		CatbotConfig catbotConfig;
		try {
			catbotConfig = CatbotSettings.getGlobalConfig();
		} catch (Exception e) {
			throw new ServiceException("Catbot 설정읽는 도중 에러발생.", e);
		}
		
		String repositoryName = catbotConfig.getRepository();
		repositorySetting = CatbotSettings.getRepositorySetting(repositoryName, true);

		JDBCProfile sqlProfile = null;

		if (repositorySetting.vendor.equals(RepositorySetting.VENDOR_DERBY)) {
			sqlProfile = new JDBCProfile.Derby(repositorySetting.db, repositorySetting.host, repositorySetting.port, repositorySetting.user, repositorySetting.password);
			sqlProfile.addParameter("create", "true");
		} else if (repositorySetting.vendor.equals(RepositorySetting.VENDOR_MYSQL)) {
			sqlProfile = new JDBCProfile.MYSQL(repositorySetting.db, repositorySetting.host, repositorySetting.port, repositorySetting.user, repositorySetting.password);
		} else if (repositorySetting.vendor.equals(RepositorySetting.VENDOR_MSSQLSERVER)) {
			sqlProfile = new JDBCProfile.SQLSERVER(repositorySetting.db, repositorySetting.host, repositorySetting.port, repositorySetting.user, repositorySetting.password);
		} else if (repositorySetting.vendor.equals(RepositorySetting.VENDOR_CUBRID)) {
			sqlProfile = new JDBCProfile.CUBRID(repositorySetting.db, repositorySetting.host, repositorySetting.port, repositorySetting.user, repositorySetting.password);
		} else if (repositorySetting.vendor.equals(RepositorySetting.VENDOR_ORACLE)) {
			sqlProfile = new JDBCProfile.ORACLE(repositorySetting.db, repositorySetting.host, repositorySetting.port, repositorySetting.user, repositorySetting.password);
		}
		
		sqlProfile.addParameter("characterEncoding", repositorySetting.encoding);
		
		Iterator<String> itr = repositorySetting.propertyMap.keySet().iterator();
		String key = "";
		while ( itr.hasNext() )
		{
			key = itr.next();
			if ( key.isEmpty() == false )
				sqlProfile.addParameter(key, repositorySetting.propertyMap.get(key));
		}
		
		dbManager.register(repositoryName, sqlProfile, catbotConfig.getPoolSeting() );
		pool = dbManager.getDBConnectionPool(repositoryName);

		return true;
	}
	
	public Connection getConnection() {
		return pool.getConnection();
	}

	public void closeConnection(Connection conn) {
		pool.returnConnection(conn);
	}
	
	protected void closePreparedStatement(PreparedStatement pstmt) {
		if (pstmt != null) {
			try {
				pstmt.close();
			} catch (SQLException e) {
			}
		}
	}
	
	//데이터핸들러 이름으로 객체를 찾아 리턴한다.
	public synchronized ScrapingDAO getScrapingDAO(String dataHandler){
		DataHandlerConfig dataHandlerConfig = null;
		try {
			dataHandlerConfig = CatbotSettings.getDataHandlerConfig(dataHandler, false);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		ScrapingDAO dao = null;
		if(dataHandlerConfig == null){
			logger.error(dataHandler + ": 데이터핸들러 셋팅을 찾을수 없습니다.");
			return null;
		}
		
		if(dataHandlerConfig.isDebug()){
			dao = new DebugScrapingDB(this, dataHandlerConfig);
		}else{
			dao = new ScrapingDB(this, dataHandlerConfig);
		}
	
		
		if(dao.open()){
			return dao;
		}
		//연결불가
		
		return null;
	}
	
	
	protected boolean shutdown0() throws ServiceException {
		try {
			if(conn != null){
				conn.close();
			}
		} catch (SQLException e) {
			logger.error(this.getClass().toString() + "을 닫는도중 에러발생.",e);
		}
		return true;
	}


}
