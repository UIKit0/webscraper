package com.websqrd.catbot.db;

import java.util.List;
import java.util.Map;

import com.websqrd.catbot.setting.ShowField;

public interface ScrapingDAO {
	public List<ShowField> getFieldNameList();
	public int getTotalCount(Map<String, String> data);
	public List<Map<String, String>> listData(int pg, int pgSize, Map<String, String> data);
	public boolean write(Map<String, String> data);
	public boolean update(Map<String, String> data);
	public boolean delete(Map<String, String> data);
	public boolean truncate(Map<String, String> data);
	public boolean cleanCategory(Map<String, String> data);
	public boolean afterInit(Map<String, String> data);
	public Map<String, String> getData(Map<String, String> data);
	public boolean close();
	public boolean open();
	
}
