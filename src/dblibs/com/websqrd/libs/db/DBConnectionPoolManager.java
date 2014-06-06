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




import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.websqrd.libs.db.DBConnectionPool.Settings;


public class DBConnectionPoolManager {
	private Logger logger = LoggerFactory.getLogger(DBConnectionPoolManager.class);
	
	private Map<String, DBConnectionPool> poolMap;
	private static DBConnectionPoolManager instance;	
	
	public DBConnectionPoolManager(){ 
		poolMap = new ConcurrentHashMap<String, DBConnectionPool>();
	}
	
	/*
	 * 정확한 동기화를 위해서는 Double Check 가 필요하지만 초기시작시 instace가 생성된다는 가정하에
	 * double check는 사용하지 않는다.
	 * 이를 위해서는 서버시작때 getInstance()호출을 통해 미리 instance를 생성해놓아야한다.
	 */
	public static DBConnectionPoolManager getInstance(){
		if(instance == null){
//			synchronized(lock){
//				if(instance == null){
					instance = new DBConnectionPoolManager();
//				}
//			}
		}
		return instance;
	}
	
	
	public boolean register(String poolName, JDBCProfile profile, DBConnectionPool.Settings poolSettings){
		try {
			if(poolMap.containsKey(poolName)){
				logger.warn("이미 같은 이름의 Pool이 존재합니다. 이름 = {}", poolName);
				return false;
			}
			DBConnectionPool pool = new DBConnectionPool(poolName, profile, poolSettings);
			poolMap.put(poolName, pool);
			return true;
		} catch (Exception e) {
			logger.error("DBConnectionPool 생성시 에러발생.", e);
		}
		return false;
	}
	
	public DBConnectionPool getDBConnectionPool(String poolName){
		return poolMap.get(poolName);
	}


	public void closeAndRemove(String poolName) {
		DBConnectionPool pool = poolMap.remove(poolName);
		if(pool != null){
			pool.closePool();
			logger.debug("Connection pool {} : {}을 제거했습니다.", poolName, pool);
		}else{
			logger.warn("해당 Pool이 존재하지 않습니다. 이름 = {}", poolName);
		}
			
		
	}
	
}
