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

public class ScrapingHistory extends AbstractDAO {
	private static Logger logger = LoggerFactory.getLogger(ScrapingHistory.class);

	public int id;
	public String site;
	public String category;
//	public String type;
	public boolean isSuccess;
	public int docSize;
	public int updateSize;
	public int deleteSize;
	public boolean isScheduled;
	public Timestamp startTime;
	public Timestamp endTime;
	public int duration;
	
	public ScrapingHistory(){ }
	
	public ScrapingHistory(Connection conn){ 
		super(conn);
	}
	
	public int create() throws SQLException{
		String createSQL = "create table "+tableName+"(id int primary key, site varchar(20), category varchar(20), isSuccess smallint, docSize int, updateSize int, deleteSize int, isScheduled smallint, startTime timestamp, endTime timestamp, duration int)";
		String prodName = conn.getMetaData().getDatabaseProductName();
		if("MySQL".equals(prodName)) { createSQL+=" character set = utf8"; }
		Statement stmt = conn.createStatement();
		return stmt.executeUpdate(createSQL);
	}
	
	public int insert(String site, String category, boolean isSuccess, int docSize, int updateSize, int deleteSize, boolean isScheduled, Timestamp startTime, Timestamp endTime, int duration) {
		PreparedStatement pstmt = null;
		try{
			String insertSQL = "insert into "+tableName+"(id, site, category, isSuccess, docSize, updateSize, deleteSize, isScheduled, startTime, endTime, duration) (select case when max(id) is null then 0 else max(id)+1 end,?,?,?,?,?,?,?,?,?,? from "+tableName+")";
			pstmt = conn.prepareStatement(insertSQL);
			int parameterIndex = 1;
			pstmt.setString(parameterIndex++, site);
			pstmt.setString(parameterIndex++, category);
			pstmt.setBoolean(parameterIndex++, isSuccess);
			pstmt.setInt(parameterIndex++, docSize);
			pstmt.setInt(parameterIndex++, updateSize);
			pstmt.setInt(parameterIndex++, deleteSize);
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
	
	private String getConditionSQL(String site, String category, boolean hasDocs){
		String condition = null;
		if(site != null || category != null){
			
			if(site != null && site.length() > 0){
				condition = " WHERE site='"+site+"'";
			}
			
			if(category != null && category.length() > 0){
				if(condition == null){
					condition = " WHERE category='"+category+"'";
				}else{
					condition += " and category='"+category+"'";
				}
			}
//			logger.debug("condition >> {}, site >> {}, category >> {}, hasDocs >> {}", new Object[]{condition, site, category, hasDocs});
			if(condition == null){
				if(hasDocs)
					condition = " WHERE docSize > 0";
			}else{
				if(hasDocs)
					condition += " and docSize > 0";
			}
		}else{
			if(hasDocs)
				condition = " WHERE docSize > 0";
		}
		
		if(condition == null){
			condition = "";
		}
		logger.debug("getConditionSQL >> {}", condition);
		return condition;
	}
	public int count(boolean hasDocs) {
		return count(null, null, hasDocs);
	}
	public int count(String site, String category, boolean hasDocs) {
		
		String condition = getConditionSQL(site, category, hasDocs);
		
		try{
			String countSQL = "SELECT count(id) FROM "+tableName+""+condition;
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
	public List<ScrapingHistory> select(int startRow, int length, boolean hasDocs) {
		return select(startRow, length, null, null, hasDocs);
	}
	public List<ScrapingHistory> select(int startRow, int length, String site, String category, boolean hasDocs) {
		
		String condition = getConditionSQL(site, category, hasDocs);
		
		List<ScrapingHistory> result = new ArrayList<ScrapingHistory>();
		
		try{
			String countSQL = "SELECT count(id) FROM "+tableName+""+condition;
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(countSQL);
			int totalCount = 0;
			if(rs.next()){
				totalCount = rs.getInt(1);
			}
			rs.close();
			stmt.close();
			logger.debug("select >> {}, {}", totalCount, countSQL);
			if(totalCount - startRow <= 0)
				return result;
			
			String selectSQL = "SELECT id, site, category, isSuccess, docSize, updateSize, deleteSize, isScheduled, startTime, endTime, duration" +
					" FROM ( SELECT ROW_NUMBER() OVER() AS rownum, "+tableName+".* FROM "+tableName+""+condition+" ) AS tmp WHERE rownum > ? and rownum <= ? order by id desc";
			PreparedStatement pstmt = conn.prepareStatement(selectSQL);
			int parameterIndex = 1;
			pstmt.setInt(parameterIndex++, totalCount - startRow - length);
			pstmt.setInt(parameterIndex++, totalCount - startRow);
			rs = pstmt.executeQuery();
//			logger.debug("totalCount = "+totalCount+", startRow="+startRow+", Start = "+(totalCount - startRow - length)+ "~"+(totalCount - startRow));
			while(rs.next()){
				ScrapingHistory r = new ScrapingHistory();
				
				parameterIndex = 1;
				r.id = rs.getInt(parameterIndex++);
				r.site = rs.getString(parameterIndex++);
				r.category = rs.getString(parameterIndex++);
				r.isSuccess = rs.getBoolean(parameterIndex++);
				r.docSize = rs.getInt(parameterIndex++);
				r.updateSize = rs.getInt(parameterIndex++);
				r.deleteSize = rs.getInt(parameterIndex++);
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
			//의미없는 조건을 주어 실제결과를 가져오지 않도록 함.
			conn.prepareStatement("select id, site, category, isSuccess, docSize, updateSize, deleteSize, isScheduled, startTime, endTime, duration from "+tableName+" where id = 0").executeQuery().next();
			return 0;
		} catch (SQLException e) {
			//table에 컬럼이 없을 경우에 exception이 발행하므로, table을 drop하고 재생성한다. 
			drop();
			create();
			return 1;
		}
	}

	private void drop() {
		PreparedStatement pstmt = null;
		try{
			String insertSQL = "drop table "+tableName;
			pstmt = conn.prepareStatement(insertSQL);
			pstmt.executeUpdate();
			logger.info(insertSQL);
		}catch(SQLException e){
			logger.error(e.getMessage(),e);
		}finally{
			if(pstmt!=null) try { pstmt.close(); } catch (SQLException e) { } 
		}
		
	}
}
