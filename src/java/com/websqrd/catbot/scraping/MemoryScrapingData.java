package com.websqrd.catbot.scraping;

import java.sql.Connection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.websqrd.catbot.db.ScrapingDAO;
import com.websqrd.catbot.setting.ShowField;

public class MemoryScrapingData implements ScrapingDAO {
	private StringBuilder buffer;
	
	public MemoryScrapingData(){
	}
	
	public boolean open() {
		buffer = new StringBuilder();
		return true;
	}
	public boolean close() {
		//buffer에 쓰기.
		
		
		return true;
	}
	public List<ShowField> getFieldNameList() {
		return null;
	}

	public int getTotalCount(Map<String, String> data) {
		return 0;
	}

	public List<Map<String, String>> listData(int pg, int pgSize, Map<String, String> data) {
		return null;
	}

	public boolean write(Map<String, String> data) {
		Iterator<Entry<String, String>> iter = data.entrySet().iterator();
		while(iter.hasNext()){
			Entry<String, String> entry = iter.next();
			buffer.append("[");
			buffer.append(entry.getKey());
			buffer.append("] : ");
			buffer.append(entry.getValue());
			buffer.append("\n");
		}
		buffer.append("\n--------------------------\n");
		return true;
	}

	public boolean update(Map<String, String> data) {
		return false;
	}

	public boolean delete(Map<String, String> data) {
		return false;
	}

	public boolean truncate(Map<String, String> data) {
		return false;
	}

	public Map<String, String> getData(Map<String, String> data) {
		return null;
	}
	
	public void setConnection(Connection conn) {
		
	}
	
	public String getBuffer(){
		return buffer.toString();
	}

	public boolean cleanCategory(Map<String, String> data) {
	        // TODO Auto-generated method stub
	        return false;
        }

	public boolean afterInit(Map<String, String> data) {
	        // TODO Auto-generated method stub
	        return false;
        }
}
