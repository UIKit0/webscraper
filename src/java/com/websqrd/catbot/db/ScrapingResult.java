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
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScrapingResult extends AbstractDAO {
	private static Logger logger = LoggerFactory.getLogger(ScrapingResult.class);

	public static int STATUS_FAIL = -1;
	public static int STATUS_SUCCESS = 0;
	public static int STATUS_RUNNING = 1;

	public String site;
	public String category;
	// public String type;
	public int status;
	public int docSize;
	public int updateSize;
	public int deleteSize;
	public boolean isScheduled;
	public Timestamp startTime;
	public Timestamp endTime;
	public int duration;

	public ScrapingResult(Connection conn) {
		super(conn);
	}

	public int create() throws SQLException {
		String createSQL = "create table "
		                + tableName
		                + "(site varchar(20), category varchar(20), status smallint, docSize int, updateSize int, deleteSize int, isScheduled smallint, startTime timestamp, endTime timestamp, duration int)";
		Statement stmt = conn.createStatement();
		return stmt.executeUpdate(createSQL);
	}

	public int updateRunning2Failed() {
		int attachCount = 0;
		try {
			String checkSQL = "update " + tableName + " set status = " + STATUS_FAIL + " where status = " + STATUS_RUNNING;
			PreparedStatement pstmt = conn.prepareStatement(checkSQL);
			attachCount = pstmt.executeUpdate();

		} catch (SQLException e) {
			logger.error(e.getMessage(), e);
			return -1;
		}
		return attachCount;

	}

	public int updateOrInsert(String site, String category, int status, int docSize, int updateSize, int deleteSize, boolean isScheduled, Timestamp startTime, Timestamp endTime, int duration) {
		try {
			String checkSQL = "select count(site) from " + tableName + " " + "where site=? and category=?";
			PreparedStatement pstmt = conn.prepareStatement(checkSQL);
			int parameterIndex = 1;
			pstmt.setString(parameterIndex++, site);
			pstmt.setString(parameterIndex++, category);
			ResultSet rs = pstmt.executeQuery();
			int count = 0;
			if (rs.next()) {
				count = rs.getInt(1);
			}

			if (count > 0) {
				return update(site, category, status, docSize, updateSize, deleteSize, isScheduled, startTime, endTime, duration);
			} else {
				return insert(site, category, status, docSize, updateSize, deleteSize, isScheduled, startTime, endTime, duration);
			}

		} catch (SQLException e) {
			logger.error(e.getMessage(), e);
			return -1;
		}
	}

	public int insert(String site, String category, int status, int docSize, int updateSize, int deleteSize, boolean isScheduled, Timestamp startTime, Timestamp endTime, int duration) {
		try {
			String insertSQL = "insert into " + tableName
			                + "(site, category, status, docSize, updateSize, deleteSize, isScheduled, startTime, endTime, duration) values (?,?,?,?,?,?,?,?,?,?)";
			PreparedStatement pstmt = conn.prepareStatement(insertSQL);
			int parameterIndex = 1;
			pstmt.setString(parameterIndex++, site);
			pstmt.setString(parameterIndex++, category);
			pstmt.setShort(parameterIndex++, (short) status);
			pstmt.setInt(parameterIndex++, docSize);
			pstmt.setInt(parameterIndex++, updateSize);
			pstmt.setInt(parameterIndex++, deleteSize);
			pstmt.setBoolean(parameterIndex++, isScheduled);
			pstmt.setTimestamp(parameterIndex++, startTime);
			pstmt.setTimestamp(parameterIndex++, endTime);
			pstmt.setInt(parameterIndex++, duration);
			return pstmt.executeUpdate();
		} catch (SQLException e) {
			logger.error(e.getMessage(), e);
			return -1;
		}
	}

	public int update(String site, String category, int status, int docSize, int updateSize, int deleteSize, boolean isScheduled, Timestamp startTime, Timestamp endTime, int duration) {
		try {
			String updateSQL = "update " + tableName + " set status=?, docSize=?, updateSize=?, deleteSize=?, isScheduled=?, startTime=?, endTime=?, duration=? "
			                + "where site=? and category=?";
			PreparedStatement pstmt = conn.prepareStatement(updateSQL);
			int parameterIndex = 1;
			pstmt.setShort(parameterIndex++, (short) status);
			pstmt.setInt(parameterIndex++, docSize);
			pstmt.setInt(parameterIndex++, updateSize);
			pstmt.setInt(parameterIndex++, deleteSize);
			pstmt.setBoolean(parameterIndex++, isScheduled);
			pstmt.setTimestamp(parameterIndex++, startTime);
			pstmt.setTimestamp(parameterIndex++, endTime);
			pstmt.setInt(parameterIndex++, duration);
			pstmt.setString(parameterIndex++, site);
			pstmt.setString(parameterIndex++, category);
			return pstmt.executeUpdate();
		} catch (SQLException e) {
			logger.error(e.getMessage(), e);
			return -1;
		}
	}

	public int select(String site, String category) {
		int count = 0;
		try {
			String selectSQL = "select site, category, status, docSize, updateSize, deleteSize, isScheduled, startTime, endTime, duration from " + tableName + " "
			                + "where site=? and category=?";
			PreparedStatement pstmt = conn.prepareStatement(selectSQL);
			int parameterIndex = 1;
			pstmt.setString(parameterIndex++, site);
			pstmt.setString(parameterIndex++, category);
			ResultSet rs = pstmt.executeQuery();

			while (rs.next()) {
				parameterIndex = 1;
				this.site = rs.getString(parameterIndex++);
				this.category = rs.getString(parameterIndex++);
				this.status = rs.getShort(parameterIndex++);
				this.docSize = rs.getInt(parameterIndex++);
				this.updateSize = rs.getInt(parameterIndex++);
				this.deleteSize = rs.getInt(parameterIndex++);
				this.isScheduled = rs.getBoolean(parameterIndex++);
				this.startTime = rs.getTimestamp(parameterIndex++);
				this.endTime = rs.getTimestamp(parameterIndex++);
				this.duration = rs.getInt(parameterIndex++);
				count++;
			}

		} catch (SQLException e) {
			logger.error(e.getMessage(), e);
		}

		return count;
	}

	public int delete(String site, String category, String type) {
		try {
			String deleteSQL = "delete from " + tableName + " where site=? and category=?";
			PreparedStatement pstmt = conn.prepareStatement(deleteSQL);
			int parameterIndex = 1;
			pstmt.setString(parameterIndex++, site);
			pstmt.setString(parameterIndex++, category);
			return pstmt.executeUpdate();

		} catch (SQLException e) {
			logger.error(e.getMessage(), e);
		}

		return 0;
	}

	// 최근업데이트건의 최근 시간만 가져온다.
	public Timestamp isUpdated(Timestamp lastTime) {
		try {
			String checkSQL = "select endTime from " + tableName + " " + "where endTime > ? " + "order by endTime desc";
			PreparedStatement pstmt = conn.prepareStatement(checkSQL);
			pstmt.setTimestamp(1, lastTime);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				return rs.getTimestamp(1);
			}

			return null;
		} catch (SQLException e) {
			logger.error(e.getMessage(), e);
			return null;
		}
	}

	public int testAndCreate() throws SQLException {
		try {
			conn.prepareStatement("select site, category, status, docSize, updateSize, deleteSize, isScheduled, startTime, endTime, duration from " + tableName + " where site = '0'")
			                .executeQuery().next();
			return 0;
		} catch (SQLException e) {
			drop();
			create();
			return 1;
		}
	}

	private void drop() {
		PreparedStatement pstmt = null;
		try {
			String insertSQL = "drop table " + tableName;
			pstmt = conn.prepareStatement(insertSQL);
			pstmt.executeUpdate();
			logger.info(insertSQL);
		} catch (SQLException e) {
			logger.error(e.getMessage(), e);
		} finally {
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
				}
		}

	}
}
