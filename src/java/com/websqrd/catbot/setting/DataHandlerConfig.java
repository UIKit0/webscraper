package com.websqrd.catbot.setting;

import java.util.ArrayList;
import java.util.List;

import org.jdom.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/** 
 * 수집한 데이터를 실제 DB 테이블에 넣어주는 핸들러의 설정파일이다.
 * @author swsong
 *
 */
public class DataHandlerConfig {
	private static Logger logger = LoggerFactory.getLogger(DataHandlerConfig.class);

	private String name;
	private List<String> idFieldNames;
	private List<String> insertSqlList;	
	
	private List<Boolean> isInsertSqlMultiple;
	private List<DBColumn[]> insertSqlParamsList;
	private String insertSql;
	private String[] insertSqlParams;
	private String updateSql;
	private String[] updateSqlFields;
	private String deleteSql;
	private String[] deleteSqlFields;
	private String truncateSql;
	private String[] truncateSqlFields;
	private String totalCountSql;
	private String[] totalCountSqlFields;
	private String selectSql;
	private List<String> selectSqlFields;
	private List<ShowField> showFields;
	private String selectByIdSql;
	private String[] selectByIdSqlFields;
	
	//카테고리 초기화
	private String cleanCategorySql;
	private String[] cleanCategorySqlParams;
	
	//초기화 후반 작업 코드
	private List<String> afterInitSqlList;
	
	private boolean isDebug;
	private boolean rollbackOnError;
	
	public DataHandlerConfig(String dataHandler, Element root) {
		idFieldNames = new ArrayList<String>();
		String rootName = root.getName();
		if(!rootName.equals("dataHandler")){
			logger.error("데이터 핸들러 설정의 root 요소 이름은 dataHandler이어야 합니다.");
			return;
		}
		name = root.getAttributeValue("name");
		//INSERT
		// 여러 sql을 받도록 수정..
		String debugStr = root.getChildText("debug");
		if(debugStr != null){
			isDebug = Boolean.parseBoolean(debugStr);
		}
		
		String rollbackOnErrorStr = root.getChildText("rollbackOnError");
		if(rollbackOnErrorStr != null){
			rollbackOnError = Boolean.parseBoolean(rollbackOnErrorStr);
		}
		
		Element element = root.getChild("insertSql");
		List<Element> childrenElem = element.getChildren();
		List<DBColumn> paramList = new ArrayList<DBColumn>();
		insertSqlList = new ArrayList<String>();
		afterInitSqlList = new ArrayList<String>();
		isInsertSqlMultiple = new ArrayList<Boolean>();
		
		insertSqlParamsList = new ArrayList<DBColumn[]>();
		boolean flag = false;
		for (int i = 0; i < childrenElem.size(); i++) {
			Element e = childrenElem.get(i);
			String name = e.getName();
			String value = e.getValue();
			if ("sql".equals(name)) {
				insertSqlList.add(value);
				String isMultiple = e.getAttributeValue("multiple");
				if(isMultiple != null && isMultiple.equals("true")){
					isInsertSqlMultiple.add(true);
				}else{
					isInsertSqlMultiple.add(false);
				}
				flag = true;
			}else if ("field".equals(name)) {
				if (flag) {
					if (paramList.size() > 0) {
						DBColumn[] insertSqlParams = new DBColumn[paramList.size()];
						for (int j = 0; j < paramList.size(); j++) {
							insertSqlParams[j] = paramList.get(j);
						}
						insertSqlParamsList.add(insertSqlParams);
						paramList.clear();
					} 
					
					DBColumn column = new DBColumn(e);
					paramList.add(column);
					flag = false;
				}else{
					DBColumn column = new DBColumn(e);
					paramList.add(column);
					flag = false;
				}
			}
		}
		if (paramList.size() > 0) {
			DBColumn[] insertSqlParams = new DBColumn[paramList.size()];
			for (int j = 0; j < paramList.size(); j++) {
				insertSqlParams[j] = paramList.get(j);
			}
			insertSqlParamsList.add(insertSqlParams);
			paramList.clear();
		}		
		
		//UPDATE
		Element el = root.getChild("updateSql");
		Element sqlEl = el.getChild("sql");
		updateSql = sqlEl.getValue();
		List<Element> fieldListEl = el.getChildren("field");
		updateSqlFields = new String[fieldListEl.size()];
		if(fieldListEl != null){
			for (int i = 0; i < updateSqlFields.length; i++) {
				updateSqlFields[i] = fieldListEl.get(i).getValue();
			}
		}
		
		//DELETE
		el = root.getChild("deleteSql");
		sqlEl = el.getChild("sql");
		deleteSql = sqlEl.getValue();
		fieldListEl = el.getChildren("field");
		deleteSqlFields = new String[fieldListEl.size()];
		if(fieldListEl != null){
			for (int i = 0; i < deleteSqlFields.length; i++) {
				deleteSqlFields[i] = fieldListEl.get(i).getValue();
			}
		}
		
		//cleanCategory
		el = root.getChild("cleanCategorySql");
		if ( el   !=  null )
		{
			sqlEl = el.getChild("sql");
			cleanCategorySql = sqlEl.getValue();
			fieldListEl = el.getChildren("field");
			cleanCategorySqlParams = new String[fieldListEl.size()];
			if(fieldListEl != null){
				for (int i = 0; i < cleanCategorySqlParams.length; i++) {
					cleanCategorySqlParams[i] = fieldListEl.get(i).getValue();
				}
			}
		}
		
		//afterInit
		el = root.getChild("afterInitSql");		
		if ( el   !=  null )
		{
			childrenElem = el.getChildren();
			afterInitSqlList = new ArrayList<String>();
			for ( int i = 0 ; i < childrenElem.size() ; i ++ )
			{
				Element e = childrenElem.get(i);
				String name = e.getName();
				String value = e.getValue();
				if ( "sql".equals(name) )
				{
					afterInitSqlList.add(value);
				}
			}			
		}
				
		//TRUNCATE
		el = root.getChild("truncateSql");
		sqlEl = el.getChild("sql");
		truncateSql = sqlEl.getValue();
		fieldListEl = el.getChildren("field");
		truncateSqlFields = new String[fieldListEl.size()];
		if(fieldListEl != null){
			for (int i = 0; i < truncateSqlFields.length; i++) {
				truncateSqlFields[i] = fieldListEl.get(i).getValue();
			}
		}
		//TOTALCOUNT
		el = root.getChild("totalCountSql");
		sqlEl = el.getChild("sql");
		totalCountSql = sqlEl.getValue();
		fieldListEl = el.getChildren("field");
		totalCountSqlFields = new String[fieldListEl.size()];
		if(fieldListEl != null){
			for (int i = 0; i < totalCountSqlFields.length; i++) {
				totalCountSqlFields[i] = fieldListEl.get(i).getValue();
			}
		}
		//SELECT
		el = root.getChild("selectSql");
		sqlEl = el.getChild("sql");
		selectSql = sqlEl.getValue();
		fieldListEl = el.getChildren("field");
		selectSqlFields = new ArrayList<String>();
		if(fieldListEl != null){
			for (int i = 0; i < fieldListEl.size(); i++) {
//				selectSqlFields[i] = fieldListEl.get(i).getValue();
				selectSqlFields.add(fieldListEl.get(i).getValue());
			}
		}
		
		//show fields
		el = root.getChild("showFields");
		fieldListEl = el.getChildren("field");
		showFields = new ArrayList<ShowField>();
		if(fieldListEl != null){
			for (int i = 0; i < fieldListEl.size(); i++) {
				Element elem = fieldListEl.get(i);
				String attrVal = elem.getAttributeValue("id");
				String caption = elem.getAttributeValue("caption");
				if(caption == null){
					caption = elem.getValue();
				}
				boolean isID = false;
				if (attrVal != null && "true".equals(attrVal)) {
					idFieldNames.add(elem.getValue());
					isID = true;
				}
//				logger.debug("elem.getValue() = {}", elem.getValue());
				showFields.add(new ShowField(elem.getValue(), caption, isID));
			}
		}

		
		//selectByIdSql
		el = root.getChild("selectByIdSql");
		sqlEl = el.getChild("sql");
		selectByIdSql = sqlEl.getValue();
		fieldListEl = el.getChildren("field");
		selectByIdSqlFields = new String[fieldListEl.size()];
		if(fieldListEl != null){
			for (int i = 0; i < selectByIdSqlFields.length; i++) {
				selectByIdSqlFields[i] = fieldListEl.get(i).getValue();
			}
		}
	}
	
	public String getName() {
		return name;
	}

	public String getInsertSql() {
		return insertSql;
	}

	public String[] getInsertSqlFields() {
		return insertSqlParams;
	}

	public String getUpdateSql() {
		return updateSql;
	}

	public String[] getUpdateSqlFields() {
		return updateSqlFields;
	}

	public String getDeleteSql() {
		return deleteSql;
	}

	public String[] getDeleteSqlFields() {
		return deleteSqlFields;
	}

	public String getTruncateSql() {
		return truncateSql;
	}

	public String[] getTruncateSqlFields() {
		return truncateSqlFields;
	}

	public String getTotalCountSql() {
		return totalCountSql;
	}
	public String[] getTotalCountSqlFields() {
		return totalCountSqlFields;
	}
	public String getSelectSql() {
		return selectSql;
	}
	public List<String> getSelectSqlFields() {
		return selectSqlFields;
	}
	public List<ShowField> getShowFields() {
		return showFields;
	}
	
	public String getSelectByIdSql() {
		return selectByIdSql;
	}
	
	public List<String> getIdFieldNames() {
		return idFieldNames;
	}
	
	public String[] getSelectByIdSqlFields() {
		return selectByIdSqlFields;
	}
	
	public List<String> getInsertSqlList() {
		return insertSqlList;
	}
	public List<Boolean> getIsInsertSqlMultiple() {
		return isInsertSqlMultiple;
	}
	public List<DBColumn[]> getInsertSqlParamsList() {
		return insertSqlParamsList;
	}
	
	public boolean isDebug(){
		return isDebug;
	}
	
	public boolean rollbackOnError(){
		return rollbackOnError;
	}
	
	public String getCleanCategorySQL()
	{
		return cleanCategorySql;
	}
	
	public String[] getCleanCategorySqlFields()
	{
		return cleanCategorySqlParams;
	}
	
	public List<String> getAfterInitSql()
	{
		return afterInitSqlList;
	}
}
