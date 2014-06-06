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
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IndexingSchedule {
	private static Logger logger = LoggerFactory.getLogger(IndexingSchedule.class);

	public String site;
	public String category;
	public String type;
	public int period;
	public Timestamp startTime;
	public boolean isActive;
	
	public IndexingSchedule(){ }
	
	public static int create(Connection conn) throws SQLException{
		String createSQL = "create table IndexingSchedule(site varchar(20), category varchar(20), type char(1), period int, startTime timestamp, isActive smallint)";
		Statement stmt = conn.createStatement();
		return stmt.executeUpdate(createSQL);
	}
	
	public static int updateOrInsert(Connection conn, String site, String category, String type, int period, Timestamp startTime, boolean isActive) {
		try{
			String checkSQL = "select count(site) from IndexingSchedule " +
					"where site=? and category=? and type=?";
			PreparedStatement pstmt = conn.prepareStatement(checkSQL);
			int parameterIndex = 1;
			pstmt.setString(parameterIndex++, site);
			pstmt.setString(parameterIndex++, category);
			pstmt.setString(parameterIndex++, type);
			ResultSet rs = pstmt.executeQuery();
			int count = 0;
			if(rs.next()){
				count = rs.getInt(1);
			}
//			logger.debug("schedule>>> "+new Date(startTime.getTime()));
			if(count > 0){
				return update(conn, site, category, type, period, startTime, isActive);
			}else{
				return insert(conn, site, category, type, period, startTime, isActive);
			}
			
		}catch(SQLException e){
			logger.error(e.getMessage(),e);
			return -1;
		}
	}
	public static int updateStatus(Connection conn, String site, String category, String type, boolean isActive) {
		try{
			String updateSQL = "update IndexingSchedule set isActive=? " +
			"where site=? and category=? and type=?";
			PreparedStatement pstmt = conn.prepareStatement(updateSQL);
			int parameterIndex = 1;
			pstmt.setBoolean(parameterIndex++, isActive);
			pstmt.setString(parameterIndex++, site);
			pstmt.setString(parameterIndex++, category);
			pstmt.setString(parameterIndex++, type);
			return pstmt.executeUpdate();
		}catch(SQLException e){
			logger.error(e.getMessage(),e);
			return -1;
		}
	}

	public static int insert(Connection conn, String site, String category, String type, int period, Timestamp startTime, boolean isActive) {
		try{
			String insertSQL = "insert into IndexingSchedule(site, category, type, period, startTime, isActive) values (?,?,?,?,?,?)";
			PreparedStatement pstmt = conn.prepareStatement(insertSQL);
			int parameterIndex = 1;
			pstmt.setString(parameterIndex++, site);
			pstmt.setString(parameterIndex++, category);
			pstmt.setString(parameterIndex++, type);
			pstmt.setInt(parameterIndex++, period);
			pstmt.setTimestamp(parameterIndex++, startTime);
			pstmt.setBoolean(parameterIndex++, isActive);
			return pstmt.executeUpdate();
		}catch(SQLException e){
			logger.error(e.getMessage(),e);
			return -1;
		}
	}
	
	public static int update(Connection conn, String site, String category, String type, int period, Timestamp startTime, boolean isActive) {
		try{
			String updateSQL = "update IndexingSchedule set period=?, startTime=?, isActive=? " +
					"where site=? and category=? and type=?";
			PreparedStatement pstmt = conn.prepareStatement(updateSQL);
			int parameterIndex = 1;
			pstmt.setInt(parameterIndex++, period);
			pstmt.setTimestamp(parameterIndex++, startTime);
			pstmt.setBoolean(parameterIndex++, isActive);
			pstmt.setString(parameterIndex++, site);
			pstmt.setString(parameterIndex++, category);
			pstmt.setString(parameterIndex++, type);
			return pstmt.executeUpdate();
		}catch(SQLException e){
			logger.error(e.getMessage(),e);
			return -1;
		}
	}
	
	public static IndexingSchedule select(Connection conn, String site, String category, String type) {
		IndexingSchedule r = null;
		try{
			String selectSQL = "select site, category, type, period, startTime, isActive from IndexingSchedule " +
					"where site=? and category=? and type=?";
			PreparedStatement pstmt = conn.prepareStatement(selectSQL);
			
			int parameterIndex = 1;
			pstmt.setString(parameterIndex++, site);
			pstmt.setString(parameterIndex++, category);
			pstmt.setString(parameterIndex++, type);
			ResultSet rs = pstmt.executeQuery();
			
			while(rs.next()){
				r = new IndexingSchedule();
				
				parameterIndex = 1;
				r.site = rs.getString(parameterIndex++);
				r.category = rs.getString(parameterIndex++);
				r.type = rs.getString(parameterIndex++);
				r.period = rs.getInt(parameterIndex++);
				r.startTime = rs.getTimestamp(parameterIndex++);
				r.isActive = rs.getBoolean(parameterIndex++);
			}
		
		}catch(SQLException e){
			logger.error(e.getMessage(),e);
		}
		
		return r;
	}
	
	public static List<IndexingSchedule> selectAll(Connection conn) {
		List<IndexingSchedule> result = new ArrayList<IndexingSchedule>();
		try{
			String selectSQL = "select site, category, type, period, startTime, isActive from IndexingSchedule where isActive = 1";
			PreparedStatement pstmt = conn.prepareStatement(selectSQL);
			ResultSet rs = pstmt.executeQuery();
			
			while(rs.next()){
				IndexingSchedule r = new IndexingSchedule();
				
				int parameterIndex = 1;
				r.site = rs.getString(parameterIndex++);
				r.category = rs.getString(parameterIndex++);
				r.type = rs.getString(parameterIndex++);
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
	
	public static int testAndCreate(Connection conn) throws SQLException {
		try {
			conn.prepareStatement("select count(*) from IndexingSchedule").executeQuery().next();
			return 0;
		} catch (SQLException e) {
			create(conn);
			return 1;
		}
	}
}
