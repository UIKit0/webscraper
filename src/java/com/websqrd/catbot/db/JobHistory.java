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

public class JobHistory extends AbstractDAO{
	private static Logger logger = LoggerFactory.getLogger(JobHistory.class);
	
	public long id;
	public long jobId;
	public String jobClassName;
	public String args;
	public boolean isSuccess;
	public String resultStr;
	public boolean isScheduled;
	public Timestamp startTime;
	public Timestamp endTime;
	public int duration;
	
	public JobHistory(){ }
	
	public JobHistory(Connection conn){ 
		super(conn);
	}
	
	
	public int create() throws SQLException{
		String createSQL = "create table "+tableName+"(id int primary key, jobId bigint, jobClassName varchar(200), args varchar(3000), isSuccess smallint, resultStr varchar(3000), isScheduled smallint, startTime timestamp, endTime timestamp, duration int)";
		String prodName = conn.getMetaData().getDatabaseProductName();
		if("MySQL".equals(prodName)) { createSQL+=" character set = utf8"; }
		Statement stmt = conn.createStatement();
		return stmt.executeUpdate(createSQL);
	}
	
	public int insert(long jobId,
			String jobClassName, String args, boolean isSuccess, String resultStr, boolean isScheduled,
			Timestamp startTime, Timestamp endTime, int duration) {
		
		PreparedStatement pstmt = null;
		try{
			String insertSQL = "insert into "+tableName+"(id, jobId, jobClassName, args, isSuccess, resultStr, isScheduled, startTime, endTime, duration) (select case when max(id) is null then 0 else max(id)+1 end,?,?,?,?,?,?,?,?,? from "+tableName+")";
			pstmt = conn.prepareStatement(insertSQL);
			int parameterIndex = 1;
			pstmt.setLong(parameterIndex++, jobId);
			pstmt.setString(parameterIndex++, jobClassName);
			pstmt.setString(parameterIndex++, args);
			pstmt.setBoolean(parameterIndex++, isSuccess);
			pstmt.setString(parameterIndex++, resultStr);
			pstmt.setBoolean(parameterIndex++, isScheduled);
			pstmt.setTimestamp(parameterIndex++, startTime);
			pstmt.setTimestamp(parameterIndex++, endTime);
			pstmt.setInt(parameterIndex++, duration);
			return pstmt.executeUpdate();
		}catch(SQLException e){
			logger.error(e.getMessage(),e);
			return -1;
		}finally{
			if(pstmt!=null) try { pstmt.close(); } catch (SQLException e) { }
		}
	}

	public int count() {
		try{
			String countSQL = "SELECT count(id) FROM "+tableName+"";
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(countSQL);
			int totalCount = 0;
			if(rs.next()){
				totalCount = rs.getInt(1);
			}
			rs.close();
			stmt.close();
		
			return totalCount;
			
		}catch(SQLException e){
			logger.error(e.getMessage(),e);
			return 0;
		}
	}
	
	public List<JobHistory> select(int startRow, int length) {
		List<JobHistory> result = new ArrayList<JobHistory>();
		try{
			
			String countSQL = "SELECT count(id) FROM "+tableName+"";
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(countSQL);
			int totalCount = 0;
			if(rs.next()){
				totalCount = rs.getInt(1);
			}
			rs.close();
			stmt.close();
		
			String selectSQL = null;
			String prodName = conn.getMetaData().getDatabaseProductName();
			if("Apache Derby".equals(prodName)) {
				selectSQL = "SELECT id, jobId, jobClassName, args, isSuccess, resultStr, isScheduled, startTime, endTime, duration" +
					" FROM ( SELECT ROW_NUMBER() OVER() AS rownum, "+tableName+".* FROM "+tableName+" ) AS tmp WHERE rownum > ? and rownum <= ? order by id desc";
			} else if("MySQL".equals(prodName)) { //MYSQL
				selectSQL = "SELECT id, jobId, jobClassName, args, isSuccess, resultStr, isScheduled, startTime, endTime, duration" +
					" FROM ( SELECT @ROWNUM:=@ROWNUM+1 AS rownum, "+tableName+".* FROM "+tableName+", (select @ROWNUM:=0)r ) AS tmp WHERE rownum > ? and rownum <= ? order by id desc";
			} else if("".equals(prodName)) { //ORACLE
			} else if("".equals(prodName)) { //MSSQL
			}
			PreparedStatement pstmt = conn.prepareStatement(selectSQL);
			int parameterIndex = 1;
			pstmt.setInt(parameterIndex++, totalCount - startRow - length);
			pstmt.setInt(parameterIndex++, totalCount - startRow);
			rs = pstmt.executeQuery();
			logger.debug("Start = "+(totalCount - length)+ "~"+(totalCount - startRow));
			while(rs.next()){
				JobHistory r = new JobHistory();
				
				parameterIndex = 1;
				r.id = rs.getInt(parameterIndex++);
				r.jobId = rs.getLong(parameterIndex++);
				r.jobClassName = rs.getString(parameterIndex++);
				r.args = rs.getString(parameterIndex++);
				r.isSuccess = rs.getBoolean(parameterIndex++);
				r.resultStr = rs.getString(parameterIndex++);
				r.isScheduled = rs.getBoolean(parameterIndex++);
				r.startTime = rs.getTimestamp(parameterIndex++);
				r.endTime = rs.getTimestamp(parameterIndex++);
				r.duration = rs.getInt(parameterIndex++);
				
				result.add(r);
			}
			
			pstmt.close();
			rs.close();
			
		}catch(SQLException e){
			logger.error(e.getMessage(),e);
		}
		
		return result;
	}
	
	public int testAndCreate() throws SQLException {
		try {
			conn.prepareStatement("select count(*) from "+tableName+"").executeQuery().next();
			return 0;
		} catch (SQLException e) {
			create();
			return 1;
		}
	}
}
