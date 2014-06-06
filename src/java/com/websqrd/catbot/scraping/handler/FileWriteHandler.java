package com.websqrd.catbot.scraping.handler;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.websqrd.catbot.exception.CatbotException;
import com.websqrd.catbot.setting.CatbotSettings;
import com.websqrd.libs.crypt.Base64;
import com.websqrd.libs.crypt.MD5;
/**
 * @deprecated
 * */
public class FileWriteHandler extends WriteHandler {
	private final static Logger logger = LoggerFactory.getLogger(FileWriteHandler.class);
	private PrintWriter output;
	private PrintWriter parseOutput;
	public FileWriteHandler(){ }
	
	int count;
	
	public void init(String site, String settingName) throws CatbotException {
		init(site, settingName, 0);
	}
	public void init(String site, String settingName, int index) throws CatbotException {
		super.init(site, settingName);
		//파일경로를 셋팅에서 가져와서
		String filepath = getStringSetting("filepath");
		String encoding = getStringSetting("encoding");
		filepath = CatbotSettings.path(filepath) + "";
		if(index > 0){
			filepath += ("." +index);
		}
		try {
			//파일 생성하고
			//열어준다.
			output = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filepath), encoding)));
		} catch (FileNotFoundException e) {
			throw new CatbotException("파일생성에러 filepath = "+filepath, e);
		}catch (UnsupportedEncodingException e) {
			throw new CatbotException("지원되지 않는 인코딩 = "+encoding, e);
		}
		
	}

	@Override
	public synchronized void write(Map<String, String> fetchData) {
		count++;
		if(count % 10 == 0)
			logger.info("Scraping "+count+"...");
		//
		//1. WRITE FETCH CONTENT
		//
		try{
			//필드별로 형식에 맞게 파일에 기록한다.
			Set<String> set = fetchData.keySet();
			Iterator<String> iter = set.iterator();
			
			output.println("<doc>");
			while(iter.hasNext()){
				String key = (String)iter.next();
				String value= fetchData.get(key);
//				logger.debug(">>>>>>"+key+" : "+value);
				output.println("<"+key+">");
				output.println(value);
				output.println("</"+key+">");
			}
			output.println("</doc>");
			output.println();
			output.flush();
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
	}
	
	@Override
	public void close() throws CatbotException {
		// 파일핸들러 닫기
		logger.info("Total Scraping "+count+" docs!");
		output.close();
//		parseOutput.close();
	}
	
	


}
