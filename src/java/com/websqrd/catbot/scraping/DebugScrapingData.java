package com.websqrd.catbot.scraping;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.sql.Connection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.websqrd.catbot.db.ScrapingDAO;
import com.websqrd.catbot.setting.CatbotSettings;
import com.websqrd.catbot.setting.ShowField;

public class DebugScrapingData implements ScrapingDAO {
	private String filename = null;
	private PrintWriter writer = null;
	
	public DebugScrapingData(String site, String category){
		filename = CatbotSettings.getSiteKey(site, "data_"+category+".txt");
	}
	
	public boolean open() {
		try {
			writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(filename)));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}
	public boolean close() {
		writer.close();
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
			writer.print(entry.getKey());
			writer.print(" : ");
			writer.println(entry.getValue());
		}
		writer.println("--------------------------");
		writer.flush();
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

	public boolean cleanCategory(Map<String, String> data) {
	        // TODO Auto-generated method stub
	        return false;
        }

	public boolean afterInit(Map<String, String> data) {
	        // TODO Auto-generated method stub
	        return false;
        }
	

}
