package com.websqrd.catbot.scraping.handler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.websqrd.catbot.exception.CatbotException;
import com.websqrd.catbot.setting.CatbotSettings;
/**
 * @deprecated
 * */
public abstract class WriteHandler {
	private final static Logger logger = LoggerFactory.getLogger(WriteHandler.class);
	protected Properties settings;
	protected String home;
	
	public WriteHandler(){ }
	
	public void init(String site, String settingName) throws CatbotException{
		this.home = CatbotSettings.HOME;
		if(settingName != null){
			settings = new Properties();
			File file = new File(CatbotSettings.getSiteKey(site, settingName));
			try {
				settings.load(new FileInputStream(file));
			} catch (FileNotFoundException e) {
				throw new CatbotException("해당 Handler셋팅을 찾을 수가 없습니다. path = "+file.getAbsolutePath(), e);
			} catch (IOException e) {
				throw new CatbotException("셋팅로딩도중 에러발생!", e);
			}
		}
	}
	
	public abstract void write(Map<String, String> fieldData) throws CatbotException;
	
	public abstract void close() throws CatbotException;
	
	protected int getIntSetting(String key) {
		String str = settings.getProperty(key);
		if(str == null){
			logger.error("");
			return -1;
		}
			
		int i = -1;
		try{
			i = Integer.parseInt(str);
		}catch(NumberFormatException e){
			logger.error(key+"가 숫자형이 아닙니다. => "+str, e);
		}
		
		return i;
	}
	
	protected String getStringSetting(String key) {
		return settings.getProperty(key);
	}
}
