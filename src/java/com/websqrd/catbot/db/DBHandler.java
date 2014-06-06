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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.websqrd.catbot.service.CatBotServiceComponent;
import com.websqrd.catbot.service.ServiceException;
import com.websqrd.catbot.setting.CatbotSettings;

public class DBHandler extends CatBotServiceComponent {

	public final static String DB_NAME = "db";
	public final static String FETCH_DB_NAME = "fetchDB";
	private String JDBC_URL;
	private Connection conn = null;
	private static DBHandler instance = new DBHandler();
	
	public static DBHandler getInstance() {
		return instance;
	}

	private DBHandler() {}

	protected boolean start0() throws ServiceException {
		JDBC_URL = "jdbc:derby:" + CatbotSettings.path(DB_NAME);
		
		try {
			Class.forName("org.apache.derby.jdbc.EmbeddedDriver").newInstance();
		} catch (Exception e) {
			throw new ServiceException("Cannot load driver class!", e);
		}
		
		try {
			conn = DriverManager.getConnection(JDBC_URL);
		} catch (SQLException e) {
			logger.info("DBHandler create and init DB!");
			// if DB is not created.
			conn = createDB(JDBC_URL,null,null);
			
			if (conn == null) {
				throw new ServiceException("내부 DB로의 연결을 생성할수 없습니다. DB를 이미 사용중인 프로세스가 있는지 확인필요.", e);
			} else {
				try {
					conn.setAutoCommit(true);
					initDB(conn);
				} catch (SQLException e1) {
					throw new ServiceException(e1);
				}
			}
		}
		
		try {
			testAndInitDB(conn);
		} catch (SQLException e) {
			throw new ServiceException(e);
		}
		
		logger.info("DBHandler started! " + conn);
		return true;
			
	}

	private void initDB(Connection conn) throws SQLException {
		// make all tables
		new ScrapingResult(conn).create();
		new ScrapingSchedule(conn).create();
		new ScrapingHistory(conn).create();
		new JobHistory(conn).create();
	}
	
	private void testAndInitDB(Connection conn) throws SQLException {
		// make all tables
		new ScrapingResult(conn).testAndCreate();
		new ScrapingSchedule(conn).testAndCreate();
		new ScrapingHistory(conn).testAndCreate();
		new JobHistory(conn).testAndCreate();
	}
	
	private Connection createDB(String jdbcurl, String jdbcuser, String jdbcpass) {
		try {
			logger.info("Creating Fastcat DB = " + jdbcurl);
			if(jdbcuser!=null && jdbcpass!=null) {
				return DriverManager.getConnection(jdbcurl + ";create=true",jdbcuser,jdbcpass);
			} else {
				return DriverManager.getConnection(jdbcurl + ";create=true");
			}
		} catch (SQLException e) {

		}
		return null;
	}

	protected boolean shutdown0() throws ServiceException {
		try {
			logger.info("DBHandler shutdown! " + conn);
			conn.close();
//			connFetchDB.close();
		} catch (SQLException e) {
			logger.error(e.getMessage(),e);
		}
		return true;
	}

	
	//db
	public int updateOrInsertSQL(String sql) throws SQLException{
		Statement stmt = conn.createStatement();
		int n = stmt.executeUpdate(sql);
		return n;
	}
	
	public ResultSet selectSQL(String sql) throws SQLException{
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(sql);
		return rs;
	}
	
	public ScrapingResult getScrapingResult(){
		return new ScrapingResult(conn);
	}
	
	public JobHistory getJobHistory(){
		return new JobHistory(conn);
	}
	
	public ScrapingSchedule getScrapingSchedule(){
		return new ScrapingSchedule(conn);
	}
	
	public ScrapingHistory getScrapingHistory(){
		return new ScrapingHistory(conn);
	}
	
}
