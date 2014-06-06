package com.websqrd.catbot.db;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.websqrd.catbot.setting.CatbotSettings;
import com.websqrd.catbot.setting.DataHandlerConfig;
import com.websqrd.catbot.setting.ShowField;

public class DebugScrapingDB extends ScrapingDB {
	private String filename = null;
	private PrintWriter writer = null;
	private int count; 
	public DebugScrapingDB(RepositoryHandler repositoryHandler, DataHandlerConfig dataHandlerConfig){
		super(repositoryHandler, dataHandlerConfig);
		filename = CatbotSettings.getPath("dump", System.currentTimeMillis()+".txt");
	}
	
	public boolean open() {
		try {
			new File(filename).getParentFile().mkdirs();
			writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(filename)));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		}

		return super.open();
	}
	public boolean close() {
		
		writer.close();
		
		if(count == 0){
			//파일을 지워준다.
			new File(filename).delete();
		}
		return super.close();
	}
	public List<ShowField> getFieldNameList() {
		return super.getFieldNameList();
	}

	public int getTotalCount(Map<String, String> data) {
		return super.getTotalCount(data);
	}

	public List<Map<String, String>> listData(int pg, int pgSize, Map<String, String> data) {
		return super.listData(pg, pgSize, data);
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
		count++;
		return super.write(data);
	}

	public boolean update(Map<String, String> data) {
		return super.update(data);
	}

	public boolean delete(Map<String, String> data) {
		return super.delete(data);
	}

	public boolean truncate(Map<String, String> data) {
		return super.truncate(data);
	}

	public Map<String, String> getData(Map<String, String> data) {
		return super.getData(data);
	}
	
}
