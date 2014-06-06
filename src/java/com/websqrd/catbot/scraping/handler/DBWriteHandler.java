package com.websqrd.catbot.scraping.handler;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.websqrd.catbot.db.DBHandler;
import com.websqrd.catbot.exception.CatbotException;
import com.websqrd.catbot.service.ServiceException;
import com.websqrd.catbot.setting.CatbotSettings;
/**
 * @deprecated
 * */
public class DBWriteHandler extends WriteHandler {
	private final static Logger logger = LoggerFactory.getLogger(DBWriteHandler.class);
	private Connection conn;
	public DBWriteHandler(){ }
	
	int count;
	
	public void init(String site, String settingName) throws CatbotException {
		init(site, settingName, 0);
	}
	public void init(String site, String settingName, int index) throws CatbotException {
		super.init(site, settingName);
		
		String driver = settings.getProperty("jdbc.driver");
		String url = settings.getProperty("jdbc.url");
		String user = settings.getProperty("jdbc.username");
		String pass = settings.getProperty("jdbc.password");		

		try{
			//create table
			String createSQL = settings.getProperty("sql.create");
			Statement stmt = conn.createStatement();
			stmt.executeUpdate(createSQL);
		}catch (Exception e) {
			//ignore
		}
	}

	@Override
	public synchronized void write(Map<String, String> fetchData) {
		count++;
		if(count % 10 == 0)
			logger.info("Scraping "+count+"...");
		
		try{
			String insertSQL = settings.getProperty("sql.insert");//"insert into IndexingResult(collection, type, status, docSize, updateSize, deleteSize, isScheduled, startTime, endTime, duration) values (?,?,?,?,?,?,?,?,?,?)";
			PreparedStatement pstmt = conn.prepareStatement(insertSQL);
			
			String fieldStr = settings.getProperty("sql.fields");
			String[] fields = fieldStr.split(",");
			for (int i = 0; i < fields.length; i++) {
				String value= fetchData.get(fields[i]);
				pstmt.setString(i+1, value);
			}
			pstmt.executeUpdate();
		}catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	@Override
	public void close() throws CatbotException {
		// 파일핸들러 닫기
		logger.info("Total Scraping "+count+" docs!");
//		try {
//			conn.close();
//		} catch (SQLException e) {
//			throw new CatbotException("DB연결을 끊을수 없습니다.", e);
//		}
//		parseOutput.close();
	}
	
}
