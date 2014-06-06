package com.websqrd.catbot.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import oracle.net.aso.e;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.websqrd.catbot.setting.DBColumn;
import com.websqrd.catbot.setting.DataHandlerConfig;
import com.websqrd.catbot.setting.ShowField;

public class ScrapingDB implements ScrapingDAO {
	private static Logger scrapingLogger = LoggerFactory
			.getLogger("SCRAPING_LOG");
	private final static Logger logger = LoggerFactory
			.getLogger(ScrapingDB.class);

	private static AtomicInteger count = new AtomicInteger();
	private static AtomicInteger totalCount = new AtomicInteger();

	public List<String> fieldList;
	public List<String> typeList;
	public String table;
	public PreparedStatement selectPstmt;
	public PreparedStatement selectPstmtById;
	public PreparedStatement insertPstmt;
	public List<PreparedStatement> insertPstmtList;
	public PreparedStatement updatePstmt;
	public PreparedStatement deletePstmt;
	public PreparedStatement cleanCategoryPstmt;
	public PreparedStatement afterInitPstmt;
	
	public PreparedStatement truncateTablePstmt;
	public PreparedStatement totalCountPstmt;
	// private String[] insertSqlFields;
	private List<DBColumn[]> insertSqlFieldList;
	private String[] updateSqlFields;
	private String[] deleteSqlFields;
	private String[] truncateSqlFields;
	private String[] totalCountSqlFields;
	private String[] selectByIdSqlFields;
	private String[] cleanCategorySqlFields;
	private List<String> selectSqlFields;
	private List<ShowField> showFields;

	//private Connection conn;
	private RepositoryHandler repositoryHandler;
	private DataHandlerConfig dataHandlerConfig;
	private List<Boolean> isInsertSqlMultiple;

	public ScrapingDB(RepositoryHandler repositoryHandler,
			DataHandlerConfig dataHandlerConfig) {
		this.repositoryHandler = repositoryHandler;
		this.dataHandlerConfig = dataHandlerConfig;

		// INSERT
		// insertSqlFields = dataHandlerConfig.getInsertSqlFields();
		insertSqlFieldList = dataHandlerConfig.getInsertSqlParamsList();
		isInsertSqlMultiple = dataHandlerConfig.getIsInsertSqlMultiple();
		// UPDATE
		updateSqlFields = dataHandlerConfig.getUpdateSqlFields();
		// DELETE
		deleteSqlFields = dataHandlerConfig.getDeleteSqlFields();

		truncateSqlFields = dataHandlerConfig.getTruncateSqlFields();

		totalCountSqlFields = dataHandlerConfig.getTotalCountSqlFields();

		selectSqlFields = dataHandlerConfig.getSelectSqlFields();

		selectByIdSqlFields = dataHandlerConfig.getSelectByIdSqlFields();

		showFields = dataHandlerConfig.getShowFields();
		
		cleanCategorySqlFields = dataHandlerConfig.getCleanCategorySqlFields();
	}

	public synchronized boolean open() {
		return true;
	}

	private void prepareStatement() {
	}

	public synchronized boolean close() {
		return true;
	}

	public List<ShowField> getFieldNameList() {
		return showFields;
	}

	public int getTotalCount(Map<String, String> data) {
		if ( data == null )
			return 0;
		
		if ( dataHandlerConfig.getTotalCountSql().trim().length() == 0 )
			return 0;	
		Connection conn = repositoryHandler.getConnection();
		try {
			logger.trace("TatalCount SQL >> {}",
					dataHandlerConfig.getTotalCountSql());	
			
			totalCountPstmt = conn.prepareStatement(dataHandlerConfig.getTotalCountSql());
					
			for (int i = 0; i < totalCountSqlFields.length; i++) {
				String fieldName = totalCountSqlFields[i];
				String value = data.get(fieldName);
				totalCountPstmt.setString(i + 1, value);
				logger.debug("Param-{} : {}", i + 1, value);
			}
			ResultSet rs = totalCountPstmt.executeQuery();
			try {
				if (rs.next()) {
					return rs.getInt(1);
				}
			} finally {
				rs.close();
			}
		} catch (SQLException e) {
		} finally
		{
			repositoryHandler.closePreparedStatement(totalCountPstmt);
			repositoryHandler.closeConnection(conn);
		}
		return 0;
	}

	public List<Map<String, String>> listData(int pg, int pgSize,
			Map<String, String> data) {
		List<Map<String, String>> result = new ArrayList<Map<String, String>>(
				pgSize);
		int parameterIndex = 1;
		int start = (pg - 1) * pgSize;
		Connection conn = repositoryHandler.getConnection();
		try {			
			selectPstmt = conn.prepareStatement(dataHandlerConfig.getSelectSql());
			
			for (int i = 0; i < selectSqlFields.size(); i++) {
				String fieldName = selectSqlFields.get(i);
				String value = data.get(fieldName);
				logger.debug("fieldName = {}, value={}", fieldName, value);
				selectPstmt.setString(parameterIndex++, value);
			}
			selectPstmt.setInt(parameterIndex++, start);
			selectPstmt.setInt(parameterIndex++, start + pgSize);
			logger.debug("start={}", start);
			logger.debug("end={}", start + pgSize);
			logger.debug("selectPstmt sql={}", dataHandlerConfig.getSelectSql());
			ResultSet rs = selectPstmt.executeQuery();
			while (rs.next()) {

				Map<String, String> map = new HashMap<String, String>();
				for (int i = 0; i < showFields.size(); i++) {
					String key = showFields.get(i).getValue();
//					logger.debug("key={} , value={}", key, rs.getString(key));
					map.put(key, rs.getString(key));
				}
				result.add(map);
			}
			rs.close();
		} catch (SQLException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}finally
		{
			repositoryHandler.closePreparedStatement(selectPstmt);
			repositoryHandler.closeConnection(conn);
		}
		

		return result;
	}

	public boolean write(Map<String, String> data) {

		// 맵 data를 insertSql에 setParameter해준다.
				// dataHandler 필드 이름 확인후 값으로 치환해준다.
				List<String> insertSqlList = dataHandlerConfig.getInsertSqlList();
				HashMap<String, String> inputData = new HashMap<String, String>();
				
				Connection conn = null;
				int currentsqlIdx = 0;				
				try{
					conn = repositoryHandler.getConnection();
					if ( conn == null )
						return false;
					
					currentsqlIdx=0;
					
					INSERT_LOOP:				
					for (int i = 0; i < insertSqlList.size(); i++,currentsqlIdx++) {
						//
						// 필드속성이 not null 인것이 있다면 체크하여 insert 진행 여부를 판단한다.
						//
						DBColumn[] temps = insertSqlFieldList.get(i);
						PreparedStatement ps = null;
						try{
							ps = conn.prepareStatement(insertSqlList.get(i));
							boolean isMulitple = isInsertSqlMultiple.get(i);
							int limit = isMulitple ? Integer.MAX_VALUE : 1;
							inputData.clear();
				
				//				logger.trace("DB write >> {}", dataHandlerConfig.getInsertSqlList().get(i));
							SEQUENCE_LOOP: 
							for (int sequence = 1; sequence <= limit; sequence++) {
				
								for (int j = 0; j < temps.length; j++) {
									DBColumn column = temps[j];
									boolean notNull = column.notNull;
									String fieldName = column.name;
				
									String value = null;
				
									if (isMulitple && fieldName.contains("#")) {
										String fieldName2 = fieldName.replace("#", "." + sequence + "");
										value = data.get(fieldName2);
										logger.trace("Value '{}' >> '{}'", fieldName2, value);
										if (value == null) {
											// 빈값이 아닌, null인 경우는 해당 필드가 존재하지 않는것으로 본다.
											// 이경우 멀티값으로 설정했지만 경우에 따라서 스키마에서 multiNode로 설정하지
											// 않았을 경우 .# 가 안붙어서 들어오게 된다.
											// 그러므로 시퀀스가 1일 경우 멀티가 아닐수 있으므로 .#없이 한번더 테스트해본다.
											if (sequence == 1) {
												fieldName2 = fieldName.replace("#", "");
												value = data.get(fieldName2);
												logger.trace("Retry Value '{}' >> '{}'", fieldName2, value);
												if (value == null) {
													// 정말 필드가 없는 경우이므로 스킵.
													logger.trace("필드데이터가 null이므로 다음으로 skip. {} >> {}", fieldName2, value);
													continue INSERT_LOOP;
												} else {
													// 시퀀스없는 필드를 찾았다. OK!
												}
											} else {
												// 실패.
												logger.trace("더 이상의 시퀀스가 없으므로 다음 sql으로 skip. {} >> {}", fieldName2, value);
												continue INSERT_LOOP;
											}
										}
									} else {
										value = data.get(fieldName);
										if (value == null)
											value = "";
									}
				
									if (notNull) {
										// Not null 조건의 validation을 통과하지 못할 경우 insert를 포기하고
										// 다음 sql문으로 넘어간다.
										if (value.length() == 0) {
											logger.trace("{} not null 조건 실패로 다음으로 스킵..", fieldName);
											continue SEQUENCE_LOOP; // 다음 sequence 로 이동한다.
										}
									}
									ps.setString(j + 1, value);
									inputData.put(fieldName + ":"+j+1+"", value);
								}								
								ps.executeUpdate();								
							}// SEQUENCE_LOOP
						}finally{
							repositoryHandler.closePreparedStatement(ps);
						}
					}//INSERT_LOOP
				} catch (SQLException e) {
					logger.error("SQL에러.", e);
					//DB 입력시 오류 발생하면 필드 값을 출력하게 한다.
					Iterator itr =  data.keySet().iterator();
					while ( itr.hasNext() )
					{
						String key = (String)itr.next();
						String value = data.get(key);
						logger.error("key = {} value = {}", key, value);
					}
					
					logger.error("real Input Data");
					logger.error("{}th SQL has exception ",currentsqlIdx);
					itr = inputData.keySet().iterator();
					while ( itr.hasNext() )
					{
						String key = (String)itr.next();
						String value = inputData.get(key);
						logger.error("key = {} value = {}", key, value);
					}					
					e.printStackTrace();
					return false;
				} finally {
					repositoryHandler.closeConnection(conn);
				}				

				return true;
	}

	private void commitDb() {
//		if (count.get() >= 30) {
//			try {
//				Connection conn = repositoryHandler.getConnection();
//				conn.commit();
//				scrapingLogger
//						.info("Global Connection Commit! totalCount = {}, count = {}",
//								totalCount.get(), count.get());
//				count.set(0);
//			} catch (SQLException e) {
//				e.printStackTrace();
//			}			
//		}
	}

	private void commitDb(boolean forceCommit) {
//		if (true == forceCommit) {
//			try {
//				conn.commit();
//				scrapingLogger
//						.info("Global Connection Commit! totalCount = {}, count = {}",
//								totalCount.get(), count.get());
//				count.set(0);
//			} catch (SQLException e) {
//				e.printStackTrace();
//			}
//		} else
//			commitDb();
	}

	public boolean update(Map<String, String> data) {
		Connection conn = null;
		PreparedStatement updatePstmt = null;
		
		try {
			
			if ( dataHandlerConfig.getUpdateSql().trim().length() > 0 ){
				conn = repositoryHandler.getConnection();
				updatePstmt = conn.prepareStatement(dataHandlerConfig.getUpdateSql());
			}else{
				logger.error("Update SQL is empty");
				return false;
			}
		
			//dataHandlerConfig.getUpdateSql()
			int parameterIndex = 1;
			try {
				// 먼저
//				for (int i = 1; i < showFields.size(); i++) {
//					String fieldName = showFields.get(i).getValue();
//					String value = data.get(fieldName);
//					updatePstmt.setString(parameterIndex++, value);
//				}
				// 후
				for (int i = 0; i < updateSqlFields.length; i++) {
					String fieldName = updateSqlFields[i];					
					String value = data.get(fieldName);
					updatePstmt.setString(parameterIndex++, value);
					logger.debug("fieldName = {} , value = {}", fieldName, value);
				}
				
				logger.debug("updatePstmt sql = {}", dataHandlerConfig.getUpdateSql());
				updatePstmt.executeUpdate();
				return true;
			} catch (SQLException e) {
				e.printStackTrace();
				Iterator itr =  data.keySet().iterator();
				while ( itr.hasNext() )
				{
					String key = (String)itr.next();
					String value = data.get(key);
					logger.error("key = {} value = {}", key, value);
				}
			}
		}catch(SQLException e){
			//ignore
		} finally{
			repositoryHandler.closePreparedStatement(updatePstmt);
			repositoryHandler.closeConnection(conn);
		}
		return false;
	}

	public boolean delete(Map<String, String> data) {
		Connection conn = null;
		try {
			conn = repositoryHandler.getConnection();
			deletePstmt = conn.prepareStatement(dataHandlerConfig.getDeleteSql());
			for (int i = 0; i < deleteSqlFields.length; i++) {
				String fieldName = deleteSqlFields[i];
				String value = data.get(fieldName);
				deletePstmt.setString(i + 1, value);
				logger.debug("key = {}, value = {}",fieldName, value);
			}
			deletePstmt.executeUpdate();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}finally
		{
			repositoryHandler.closePreparedStatement(deletePstmt);
			repositoryHandler.closeConnection(conn);
		}
		return false;
	}
	
	public boolean cleanCategory(Map<String, String> data) {
		Connection conn = null;
		try {
			conn = repositoryHandler.getConnection();
			cleanCategoryPstmt = conn.prepareStatement(dataHandlerConfig.getCleanCategorySQL());
			for (int i = 0; i < cleanCategorySqlFields.length; i++) {
				String fieldName = cleanCategorySqlFields[i];
				String value = data.get(fieldName);
				cleanCategoryPstmt.setString(i + 1, value);
				logger.debug("key = {}, value = {}",fieldName, value);
			}
			cleanCategoryPstmt.executeUpdate();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}finally
		{
			repositoryHandler.closePreparedStatement(cleanCategoryPstmt);
			repositoryHandler.closeConnection(conn);
		}
		return false;
	}
	

	public boolean truncate(Map<String, String> data) {
		Connection conn = null;
		try {
			conn = repositoryHandler.getConnection();
			truncateTablePstmt = conn.prepareStatement(dataHandlerConfig.getTruncateSql());
			for (int i = 0; i < truncateSqlFields.length; i++) {
				String fieldName = truncateSqlFields[i];
				String value = data.get(fieldName);
				truncateTablePstmt.setString(i + 1, value);
				logger.debug("key = {}, value = {}",fieldName, value);
			}
			truncateTablePstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}finally
		{
			repositoryHandler.closePreparedStatement(truncateTablePstmt);
			repositoryHandler.closeConnection(conn);
		}
		return true;
	}

	// id번호로 하나의 데이터를 가져온다.
	public Map<String, String> getData(Map<String, String> data) {
		Map<String, String> map = null;
		Connection conn = null;
		try {
			conn = repositoryHandler.getConnection();
			selectPstmtById = conn.prepareStatement(dataHandlerConfig.getSelectByIdSql());
			for (int i = 0; i < selectByIdSqlFields.length; i++) {
				String fieldName = selectByIdSqlFields[i];
				String value = data.get(fieldName);
				selectPstmtById.setString(i + 1, value);
				logger.debug("key = {}, value = {}",fieldName, value);
			}
			logger.debug("selectPstmtById sql = {}", dataHandlerConfig.getSelectByIdSql());
			ResultSet rs = selectPstmtById.executeQuery();
			if (rs.next()) {
				map = new HashMap<String, String>();
				for (int i = 0; i < showFields.size(); i++) {
					String key = showFields.get(i).getValue();
					map.put(key, rs.getString(key));
					// logger.debug(">>>>>>>>>>>>>{}",showFields.size());
					 logger.debug("key = {}, value = {}",key,rs.getString(key));
				}
			}
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally
		{
			repositoryHandler.closePreparedStatement(selectPstmtById);
			repositoryHandler.closeConnection(conn);
		}

		return map;
	}

	public boolean afterInit(Map<String, String> data) {
		Connection conn = null;
		try {
			conn = repositoryHandler.getConnection();
			List<String> sqls = dataHandlerConfig.getAfterInitSql();			
			if ( sqls != null && sqls.size() > 0 )
			{
				for (int i = 0; i < sqls.size(); i++) {
					afterInitPstmt = conn.prepareStatement(sqls.get(i));
					logger.debug("afterInit sql = {}", sqls.get(i));
					afterInitPstmt.executeUpdate();
				}
			}			
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}finally
		{
			repositoryHandler.closePreparedStatement(afterInitPstmt);
			repositoryHandler.closeConnection(conn);
		}
		return false;
        }

}
