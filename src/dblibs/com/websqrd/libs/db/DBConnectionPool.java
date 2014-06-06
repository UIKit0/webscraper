package com.websqrd.libs.db;
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



import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * 
 * */
public class DBConnectionPool {
	
	private Logger logger = LoggerFactory.getLogger(DBConnectionPool.class); 
	
	private LinkedBlockingQueue<Connection> idlePool;
	private Set<Connection> activePool;
	private ConcurrentHashMap<Connection, Long> totalMap; //생성시간을 저장하는 map. 생성된 총 커넥션 갯수를 확인할수 있다.

	private String poolName;
	private JDBCProfile profile;
	private Settings poolSetting;	
	
	public DBConnectionPool(String poolName, JDBCProfile profile, Settings poolSetting) {
		this.poolName = poolName;
		this.profile = profile;
		this.poolSetting = poolSetting;
		idlePool = new LinkedBlockingQueue<Connection>();
		activePool = Collections.synchronizedSet(new HashSet<Connection>());
		totalMap = new ConcurrentHashMap<Connection, Long>();
		//드라이버 로딩.
		try {
			Class.forName(profile.getDriverClassName());
		} catch (ClassNotFoundException e) {
			logger.error(e.getMessage(),e);
		}
		
		logger.info("{}]Connection Pool 생성! {}", poolName, poolSetting);
	}
	
	public JDBCProfile getProfileSetting(){
		return profile;
	}
	public Settings getPoolSetting(){
		return poolSetting;
	}
	
	public LinkedBlockingQueue<Connection> getIdlePool(){
		return idlePool;
	}
	
	public Set<Connection> getActivePool(){
		return activePool;
	}
	
	public ConcurrentHashMap<Connection, Long> getAgeMap(){
		return totalMap;
	}
	
	private Connection tryGetConnection(){
		Connection conn = null;
		if(totalMap.size() < poolSetting.maxTotal){
			//사용중인 연결이 maxActive보다 작으면 생성.
			conn = createConnection();
		}else{
			//생성할수 없다면 대기한다.
			try {
				logger.debug("{}] 연결얻기 대기..", poolName);
				long st = System.currentTimeMillis();
				conn = idlePool.poll(poolSetting.maxWait, TimeUnit.SECONDS);
				if(conn == null){
					logger.debug("{}] 대기시간={}s안에 연결을 얻지 못했습니다.", poolName, poolSetting.maxWait); 
				}else{
					logger.debug("{}] 연결획득성공. elapsed={}, conn={}", new Object[]{poolName, System.currentTimeMillis() - st, conn});
				}
			} catch (InterruptedException e2) {
			}
		}
		return conn;
	}
	
	public Connection getConnection(){
		long time = System.currentTimeMillis();
		//일단 큐에서 가능한 커넥션 얻기.
		logger.debug("{}] getConnection", poolName);
		Connection conn = idlePool.poll();
		
		if(conn == null){
			logger.debug("{}] 유휴 연결이 없습니다. total={}, idle={}", new Object[]{poolName, totalMap.size(), idlePool.size()});
			conn = tryGetConnection();
		}
		
		//연결 검증하기 
		while(conn != null){
			//time : 생성시각.
			Long timeLong =  totalMap.get(conn);
			if(timeLong != null){
				time = timeLong.longValue();
				if(isOldConnection(time)){
					//오래된 연결이라면 닫고 다음 커넥션을 받아온다.
					logger.debug("{}] Pool의 객체가 오래된 연결이라 폐기합니다. age={}, conn={}", new Object[]{poolName, System.currentTimeMillis() - time, conn});
					
					closeConnection(conn);
					conn = null;
				}else{
					//사용가능한 커넥션 확인.
					if(isValid(conn)){
						//사용가능!
						activePool.add(conn);
						logger.debug("{}] 유효함! age={}, conn={}", new Object[]{poolName, System.currentTimeMillis() - time, conn});
						return conn;
					}else{
						//유효하지 않음.
						conn = null;
					}
				}
			}else{
				//알수없는 커넥션이라면 버린다.
				logger.debug("{}] 알수없는 연결이라 폐기합니다. conn={}", poolName, conn);
				closeConnection(conn);
				conn = null;
			}
			
			//문제가 있는 커넥션이 발견되었다면 위에서 conn이 null로 셋팅된다.
			if(conn == null){
				conn = tryGetConnection();
			}
		}//while
			
		
		//여기까지 왔다면 connection 할당 실패이다.
		return conn;
	}
	private boolean isValid(Connection conn){
		if(conn == null){
			return false;
		}
		
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery(profile.getValidationSQL());
			return true;
		} catch (Exception e) {
			logger.warn("{}] DB 커넥션이 유효하지 않음 conn={}", poolName, conn);
			closeConnection(conn);
		} finally {
			if(stmt != null){
				try {
					stmt.close();
				} catch (SQLException e1) {
				}
			}
			if(rs != null){
				try {
					rs.close();
				} catch (SQLException e1) {
				}
			}
		}
		
		return false;
	}
	public void returnConnection(Connection conn){
		if(conn == null) 
			return;
		
		boolean exists = activePool.remove(conn);
		if(exists){
			Long time = totalMap.get(conn);
			if(isOldConnection(time)){
				//오래된 커넥션은 닫아준다.
				logger.debug("{}] 반환된 객체가 오래된 연결이라 폐기합니다. age={}, conn={}", new Object[]{poolName, System.currentTimeMillis() - time, conn});
				closeConnection(conn);
			}else{
				//젊은 커넥션은 풀에 넣어준다.
				synchronized(this){
					if(totalMap.size() < poolSetting.maxTotal){
						//대기하고 있는 풀 사이즈가 coreSize보다 작을때에만 남는 커넥션을 유지한다.
						if(idlePool.size() < poolSetting.maxIdle){
							try {
								idlePool.put(conn);
								logger.debug("{}] 반환된 연결을 대기풀에 넣습니다. idlePool={}, conn={}", new Object[]{poolName, idlePool.size(), conn});
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}else{
							logger.debug("{}] 대기연결갯수가 maxIdle={}보다 커서 반환된 연결을 폐기합니다.conn={}", new Object[]{poolName, poolSetting.maxIdle, conn});
							closeConnection(conn);
						}
					}else{
						logger.debug("{}] 총연결갯수가 maxTotal={}보다 커서 반환된 연결을 폐기합니다.conn={}", new Object[]{poolName, poolSetting.maxTotal, conn});
						closeConnection(conn);
					}
				}
			}
		}else{
			//알수없는 커넥션.
			logger.debug("{}] 반환된 객체가 알수없는 연결이라 폐기합니다. conn={}", poolName, conn);
			closeConnection(conn);
			conn = null;
		}
	}
	
	private boolean isOldConnection(Long time) {
		if(time == null)
			return true;
		
		return (System.currentTimeMillis() - time) > poolSetting.maxAge;
	}
	
	private Connection createConnection(){
		Connection conn = null;
		if(totalMap.size() < poolSetting.maxTotal){
			try {
				long st = System.currentTimeMillis();
				logger.debug("{}] 새연결생성시도 username={}, password={}", new Object[]{poolName, profile.username, profile.password});
				conn = DriverManager.getConnection(profile.getUrl(), profile.username, profile.password);
				if(conn != null){
					totalMap.put(conn, System.currentTimeMillis());
					logger.debug("{}] 새연결생성성공. conn={}, 총 {} / {}개, elapsed={}ms", new Object[]{poolName, conn, totalMap.size(), poolSetting.maxTotal, System.currentTimeMillis() - st});
					return conn;
				}
			} catch (SQLException e) {
				logger.error(e.getMessage(),e);
			}
			logger.debug("{}] 새연결생성실패!", poolName);
		}else{
			logger.error("{}] 최대 연결 허용갯수 {}개를 초과하여 연결을 새로 생성할수 없습니다. active={}, idle={}", new Object[]{poolName, poolSetting.maxTotal, activePool.size(), idlePool.size()});
		}
		return conn;
		
	}
	
	private void closeConnection(Connection conn){
		if(conn != null){
			try {
				conn.close();
				totalMap.remove(conn);
			} catch (SQLException e1) {
			}
		}
	}
	
	public void closePool() {
		Object[] list = idlePool.toArray();
		idlePool.clear();
		for (int i = 0; i < list.length; i++) {
			try {
				((Connection)list[i]).close();
			} catch (SQLException e) {
			}
		}
		list = activePool.toArray();
		activePool.clear();
		for (int i = 0; i < list.length; i++) {
			try {
				((Connection)list[i]).close();
			} catch (SQLException e) {
			}
		}
		logger.debug("{}] Connection pool {}을 종료했습니다.", poolName, this);
	}
	
	public String getStatusString() {
		StringBuilder builder = new StringBuilder();
		builder.append("== ").append(poolName).append(" ==\n");
		builder.append("Total connection : "+ (idlePool.size() + activePool.size()));
		builder.append("\n");
		builder.append("Idle connection : "+ idlePool.size());
		builder.append("\n");
		builder.append("Active connection : "+ activePool.size());
		builder.append("\n");
		Object[] list = activePool.toArray();
		for (int i = 0; i < list.length; i++) {
			Connection conn = ((Connection)list[i]);
			Long time = totalMap.get(conn);
			builder.append("Active-"+(i+1)+": "+conn.toString()+", Birth: "+new Date(time)+", Age: "+(System.currentTimeMillis() - time)+"ms");
			builder.append("\n");
		}
		list = idlePool.toArray();
		for (int i = 0; i < list.length; i++) {
			Connection conn = ((Connection)list[i]);
			Long time = totalMap.get(conn);
			builder.append("Idle-"+(i+1)+": "+conn.toString()+", Birth: "+new Date(time)+", Age: "+(System.currentTimeMillis() - time)+"ms");
			builder.append("\n");
		}
		builder.append("===================\n");
		return builder.toString();
	}
	
	
	public static class Settings{
		protected int maxTotal;
		protected int maxIdle;
		protected int maxWait;
		protected int maxAge;
		
		
		/**
		 * @param maxTotal 최대 생성 커넥션 갯수
		 * @param maxIdle 대기중인 커넥션의 최대갯수. 반환된 커넥션의 갯수가 maxIdle을 넘으면 닫고 버린다.
		 * @param maxWait 커넥션을 얻을때의 최대대기시간. 초단위
		 * @param maxAge 생성된 커넥션을 사용할 최대시간. maxAge을 넘으면 해당 커넥션을 닫고 버린다. 초단위 
		 */
		public Settings(int maxTotal, int maxIdle, int maxWait, int maxAge){
			this.maxTotal = maxTotal;
			this.maxIdle = maxIdle;
			this.maxWait = maxWait;
			this.maxAge = maxAge * 1000; //초를 ms로 변환한다.
		}
		
		public void setup(int maxTotal, int maxIdle, int maxWait, int maxAge){
			this.maxTotal = maxTotal;
			this.maxIdle = maxIdle;
			this.maxWait = maxWait;
			this.maxAge = maxAge * 1000; //초를 ms로 변환한다.
		}
		
		public String toString(){
			return "최대총갯수:"+maxTotal+", 최대대기갯수:"+maxIdle+", 최대연결대기시간(s):"+maxWait+", 최대생명시간:"+maxAge;
		}
	}

	
}
