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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScrapingSchedule extends AbstractDAO {
	private static Logger logger = LoggerFactory.getLogger(ScrapingSchedule.class);

	public String site;
	public String category;
//	public String type;
	public int period;
	public Timestamp startTime;
	public boolean isActive;
	
	public ScrapingSchedule(){ }
	
	public ScrapingSchedule(Connection conn){ 
		super(conn);
	}
	
	public int create() throws SQLException{
		String createSQL = "create table "+tableName+"(site varchar(20), category varchar(20), period int, startTime timestamp, isActive smallint)";
		Statement stmt = conn.createStatement();
		return stmt.executeUpdate(createSQL);
	}
	
	public int updateOrInsert(String site, String category, int period, Timestamp startTime, boolean isActive) {
		try{
			String checkSQL = "select count(site) from "+tableName+" " +
					"where site=? and category=?";
			PreparedStatement pstmt = conn.prepareStatement(checkSQL);
			int parameterIndex = 1;
			pstmt.setString(parameterIndex++, site);
			pstmt.setString(parameterIndex++, category);

			ResultSet rs = pstmt.executeQuery();
			int count = 0;
			if(rs.next()){
				count = rs.getInt(1);
			}
			
			if(count > 0){
				return update(site, category, period, startTime, isActive);
			}else{
				return insert(site, category, period, startTime, isActive);
			}
			
		}catch(SQLException e){
			logger.error(e.getMessage(),e);
			return -1;
		}
	}
	public int updateStatus(String site, String category, boolean isActive) {
		try{
			String updateSQL = "update "+tableName+" set isActive=? " +
			"where site=? and category=?";
			PreparedStatement pstmt = conn.prepareStatement(updateSQL);
			int parameterIndex = 1;
			pstmt.setBoolean(parameterIndex++, isActive);
			pstmt.setString(parameterIndex++, site);
			pstmt.setString(parameterIndex++, category);

			return pstmt.executeUpdate();
		}catch(SQLException e){
			logger.error(e.getMessage(),e);
			return -1;
		}
	}

	public int insert(String site, String category, int period, Timestamp startTime, boolean isActive) {
		try{
			String insertSQL = "insert into "+tableName+"(site, category, period, startTime, isActive) values (?,?,?,?,?)";
			PreparedStatement pstmt = conn.prepareStatement(insertSQL);
			int parameterIndex = 1;
			pstmt.setString(parameterIndex++, site);
			pstmt.setString(parameterIndex++, category);

			pstmt.setInt(parameterIndex++, period);
			pstmt.setTimestamp(parameterIndex++, startTime);
			pstmt.setBoolean(parameterIndex++, isActive);
			return pstmt.executeUpdate();
		}catch(SQLException e){
			logger.error(e.getMessage(),e);
			return -1;
		}
	}
	
	public int update(String site, String category, int period, Timestamp startTime, boolean isActive) {
		try{
			String updateSQL = "update "+tableName+" set period=?, startTime=?, isActive=? " +
					"where site=? and category=?";
			PreparedStatement pstmt = conn.prepareStatement(updateSQL);
			int parameterIndex = 1;
			pstmt.setInt(parameterIndex++, period);
			pstmt.setTimestamp(parameterIndex++, startTime);
			pstmt.setBoolean(parameterIndex++, isActive);
			pstmt.setString(parameterIndex++, site);
			pstmt.setString(parameterIndex++, category);

			return pstmt.executeUpdate();
		}catch(SQLException e){
			logger.error(e.getMessage(),e);
			return -1;
		}
	}
	
	public int select(String site, String category) {
		int count = 0;
		try{
			String selectSQL = "select site, category, period, startTime, isActive from "+tableName+" " +
					"where site=? and category=?";
			PreparedStatement pstmt = conn.prepareStatement(selectSQL);
			
			int parameterIndex = 1;
			pstmt.setString(parameterIndex++, site);
			pstmt.setString(parameterIndex++, category);
			ResultSet rs = pstmt.executeQuery();
			
			if(rs.next()){
				parameterIndex = 1;
				this.site = rs.getString(parameterIndex++);
				this.category = rs.getString(parameterIndex++);
//				this.type = rs.getString(parameterIndex++);
				this.period = rs.getInt(parameterIndex++);
				this.startTime = rs.getTimestamp(parameterIndex++);
				this.isActive = rs.getBoolean(parameterIndex++);
				count++;
			}
		
		}catch(SQLException e){
			logger.error(e.getMessage(),e);
		}
		
		return count;
	}
	
	public List<ScrapingSchedule> selectAll() {
		List<ScrapingSchedule> result = new ArrayList<ScrapingSchedule>();
		try{
			String selectSQL = "select site, category, period, startTime, isActive from "+tableName+" where isActive = 1";
			PreparedStatement pstmt = conn.prepareStatement(selectSQL);
			ResultSet rs = pstmt.executeQuery();
			
			while(rs.next()){
				ScrapingSchedule r = new ScrapingSchedule();
				
				int parameterIndex = 1;
				r.site = rs.getString(parameterIndex++);
				r.category = rs.getString(parameterIndex++);
//				r.type = rs.getString(parameterIndex++);
				r.period = rs.getInt(parameterIndex++);
				r.startTime = rs.getTimestamp(parameterIndex++);
				r.isActive = rs.getBoolean(parameterIndex++);
				
				result.add(r);
			}
		}catch(SQLException e){
			logger.error(e.getMessage(),e);
		}
		
		return result;
	}
	
	public int testAndCreate() throws SQLException {
		try {
			conn.prepareStatement("select count(*) from "+tableName).executeQuery().next();
			return 0;
		} catch (SQLException e) {
			create();
			return 1;
		}
	}
}
