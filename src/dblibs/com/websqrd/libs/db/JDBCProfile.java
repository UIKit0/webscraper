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



import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public abstract class JDBCProfile {
	
	protected String database;
	protected String host;
	protected String port;
	protected String username;
	protected String password;
	protected String encoding;
	protected String userUrl;
	protected Map<String, String> parameterMap;
	
	
	/*기존의 생성자, 도메인, 포트번호, 데이타베이스명, 사용자 이름, 암호를 입력 받는다. 
	 * */
	public JDBCProfile(String database, String host, String port, String username, String password){
		this.database = database;
		this.host = host;
		this.port = port;
		this.username = username;
		this.password = password;
		this.encoding = null;
		this.parameterMap = new HashMap<String, String>();
		this.userUrl = null;
	}
	
	
	/*추가된 생성자
	 * JDBC url(도메인,데이타베이스명,포트번호, 외 다른 정보를 토함), 사용자 이름, 암호를 입력 받는다.
	 * 위의 생성자와는 상호배제적인 관계. 
	 * */
	public JDBCProfile(String userUrl, String username, String password){
		this.database = "";
		this.host = "";
		this.port = "";
		this.username = username;
		this.password = password;
		this.encoding = null;
		this.parameterMap = new HashMap<String, String>();
		this.userUrl = userUrl;
	}
	
	public void addParameter(String key, String value){
		if(key != null && value != null){
			parameterMap.put(key, value);
		}
	}
	
	public String getPort(){
		if(port == null)
			return getDefaultPort();
		else 
			return port;
	}
	
	public abstract String getUrl();
	public abstract String getDriverClassName();
	public abstract String getValidationSQL();
	protected abstract String getDefaultPort();
	
	@Override
	public String toString(){
		return getUrl();
	}
	
	/////
	// Derby
	////
	public static class Derby extends JDBCProfile{
		public Derby(String database, String host, String port, String username, String password){
			super(database, host, port, username, password);
		}
		
		public Derby(String userUrl, String username, String password){
			super(userUrl, username, password);
		}
		
		@Override
		public String getUrl() {
			String url = "";
			if ( userUrl != null && userUrl.trim().length() != 0 )
				url = userUrl;
			else
				url = "jdbc:derby://"+database;
			
			if(parameterMap.size() > 0){
				Iterator<String> iter = parameterMap.keySet().iterator();
				int cnt = 0;
				while(iter.hasNext()){
					String key = iter.next();
					String value = parameterMap.get(key);
					if(key != null && value != null){
						if(cnt == 0){
							url += "?";
						}else{
							url += "&";
						}
						url += key+"="+value;
						cnt++;
					}
				}
			}
			
			return url;
		}

		@Override
		public String getDriverClassName() {
			return "org.apache.derby.EmbeddedDriver";
		}
		
		@Override
		public String getValidationSQL() {
			return "Values 1'";
		}
		@Override
		protected String getDefaultPort() {
			return "";
		}
	}

	
	/////
	// MYSQL
	////
	public static class MYSQL extends JDBCProfile{
		public MYSQL(String database, String host, String port, String username, String password){
			super(database, host, port, username, password);
		}
	
		public MYSQL(String userUrl, String username, String password){
			super(userUrl, username, password);
		}
		
		@Override
		public String getUrl() {
			String url = "";
			if ( userUrl != null && userUrl.trim().length() != 0 )
				url = userUrl;
			else
				url = "jdbc:mysql://"+host+":"+getPort()+"/"+database;
			
			if(parameterMap.size() > 0){
				Iterator<String> iter = parameterMap.keySet().iterator();
				int cnt = 0;
				while(iter.hasNext()){
					String key = iter.next();
					String value = parameterMap.get(key);
					if(key != null && value != null){
						if(cnt == 0){
							url += "?";
						}else{
							url += "&";
						}
						url += key+"="+value;
						cnt++;
					}
				}
			}
			
			return url;
		}

		@Override
		public String getDriverClassName() {
			return "com.mysql.jdbc.Driver";
		}
		
		@Override
		public String getValidationSQL() {
			return "SELECT NOW()";
		}
		@Override
		protected String getDefaultPort() {
			return "3306";
		}
	}

	/////
	// ORACLE
	////
	public static class ORACLE extends JDBCProfile{

		public ORACLE(String database, String host, String port, String username, String password){
			super(database, host, port, username, password);
		}
		
		public ORACLE(String userUrl, String username, String password){
			super(userUrl, username, password);
		}
		@Override
		public String getUrl() {
			//jdbc:oracle:thin:@192.168.2.37:1521:DNWRAC1
			String url = "";
			if ( userUrl != null && userUrl.trim().length() != 0 )
				url = userUrl;
			else
				url = "jdbc:oracle:thin:@"+host+":"+getPort()+":"+database;
		
			return url;
		}

		@Override
		public String getDriverClassName() {
			return "oracle.jdbc.driver.OracleDriver";
		}
		
		@Override
		public String getValidationSQL() {
			return "SELECT SYSDATE FROM DUAL";
		}
		@Override
		protected String getDefaultPort() {
			return "1521";
		}
	}
	
	/////
	// SQLSERVER
	////
	public static class SQLSERVER extends JDBCProfile{
		public SQLSERVER(String database, String host, String port, String username, String password){
			super(database, host, port, username, password);
		}
		
		public SQLSERVER(String userUrl, String username, String password){
			super(userUrl, username, password);
		}
		@Override
		public String getUrl() {
			//jdbc:microsoft:sqlserver://<SERVERNAME>:1433;DatabaseName=
			
			String url = "";
			if ( userUrl != null && userUrl.trim().length() != 0 )
				url = userUrl;
			else
				url = "jdbc:microsoft:sqlserver://"+host+":"+getPort()+";DatabaseName="+database;
			
			if(parameterMap.size() > 0){
				Iterator<String> iter = parameterMap.keySet().iterator();
				while(iter.hasNext()){
					String key = iter.next();
					String value = parameterMap.get(key);
					if(key != null && value != null){
						url += ";"+key+"="+value;
					}
				}
			}
			return url;
		}

		@Override
		public String getDriverClassName() {
			return "com.microsoft.jdbc.sqlserver.SQLServerDriver";
		}
		
		@Override
		public String getValidationSQL() {
			return "SELECT GETDATE()";
		}
		@Override
		protected String getDefaultPort() {
			return "1433";
		}
	}
	
	
	/////
	// SQLSERVER_JTDS
	////
	public static class SQLSERVER_JTDS extends JDBCProfile{
		public SQLSERVER_JTDS(String database, String host, String port, String username, String password){
			super(database, host, port, username, password);
		}
		public SQLSERVER_JTDS(String userUrl, String username, String password){
			super(userUrl, username, password);
		}
		@Override
		public String getUrl() {
			//jdbc:jtds:<server_type>://<server>[:<port>][/<database>][;<property>=<value>[;...]]
			String url = "";
			if ( userUrl != null && userUrl.trim().length() != 0 )
				url = userUrl;
			else
				url = "jdbc:jtds:sqlserver://"+host+":"+getPort()+"/"+database;
			
			if(parameterMap.size() > 0){
				Iterator<String> iter = parameterMap.keySet().iterator();
				while(iter.hasNext()){
					String key = iter.next();
					String value = parameterMap.get(key);
					if(key != null && value != null){
						url += ";"+key+"="+value;
					}
				}
			}
			return url;
		}

		@Override
		public String getDriverClassName() {
			return "net.sourceforge.jtds.jdbc.Driver";
		}
		
		@Override
		public String getValidationSQL() {
			return "SELECT GETDATE()";
		}
		@Override
		protected String getDefaultPort() {
			return "1433";
		}
	}
	
	/////
	// DB2
	////
	public static class DB2 extends JDBCProfile{
		public DB2(String database, String host, String port, String username, String password){
			super(database, host, port, username, password);
		}
		
		public DB2(String userUrl, String username, String password){
			super(userUrl, username, password);
		}
		
		@Override
		public String getUrl() {
			//jdbc:db2://127.0.0.1:50000/SAMPLE
			String url = "";
			if ( userUrl != null && userUrl.trim().length() != 0 )
				url = userUrl;
			else
				url = "jdbc:db2://"+host+":"+getPort()+"/"+database;
		
			return url;
		}

		@Override
		//db2jcc.jar and db2jcc_license_cu.jar 
		public String getDriverClassName() {
			return "com.ibm.db2.jcc.DB2Driver";
		}
		
		@Override
		public String getValidationSQL() {
			return "SELECT DISTINCT(CURRENT TIMESTAMP) FROM SYSIBM.SYSTABLES";
		}
		@Override
		protected String getDefaultPort() {
			return "50000";
		}
	}
	
	/////
	// CUBRID
	////
	public static class CUBRID extends JDBCProfile{
		public CUBRID(String database, String host, String port, String username, String password){
			super(database, host, port, username, password);
		}
		
		public CUBRID(String userUrl, String username, String password){
			super(userUrl, username, password);
		}
		
		@Override
		public String getUrl() {
			//jdbc:cubrid:localhost:30000:demodb:::
			String url = "";
			if ( userUrl != null && userUrl.trim().length() != 0 )
				url = userUrl;
			else
				url = "jdbc:cubrid:"+host+":"+getPort()+":"+database+":::";
			return url;
		}

		@Override
		public String getDriverClassName() {
			return "cubrid.jdbc.driver.CUBRIDDriver";
		}
		
		@Override
		public String getValidationSQL() {
			return "SELECT SYSTIME";
		}
		@Override
		protected String getDefaultPort() {
			return "30000";
		}
	}
	
	/////
	// POSTGRES
	////
	public static class POSTGRES extends JDBCProfile{
		public POSTGRES(String database, String host, String port, String username, String password){
			super(database, host, port, username, password);
		}
		
		public POSTGRES(String userUrl, String username, String password){
			super(userUrl, username, password);
		}
		
		@Override
		public String getUrl() {
			//jdbc:postgresql://host:port/database?charSet=LATIN1
			String url = "";
			if ( userUrl != null && userUrl.trim().length() != 0 )
				url = userUrl;
			else
				url = "jdbc:postgresql:"+host+":"+getPort()+"/"+database;
			
			if(parameterMap.size() > 0){
				Iterator<String> iter = parameterMap.keySet().iterator();
				int cnt = 0;
				while(iter.hasNext()){
					String key = iter.next();
					String value = parameterMap.get(key);
					if(key != null && value != null){
						if(cnt == 0){
							url += "?";
						}else{
							url += "&";
						}
						url += key+"="+value;
						cnt++;
					}
				}
			}
			return url;
		}

		@Override
		public String getDriverClassName() {
			return "org.postgresql.Driver";
		}
		
		@Override
		public String getValidationSQL() {
			return "SELECT NOW()";
		}
		@Override
		protected String getDefaultPort() {
			return "5432";
		}
	}
	
}
