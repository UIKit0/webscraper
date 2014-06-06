package com.websqrd.catbot.scraping;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.websqrd.catbot.setting.CatbotSettings;
import com.websqrd.libs.crypt.MD5;

public class FileCacheRepository {
	private final static Logger logger = LoggerFactory.getLogger(FileCacheRepository.class);
	
	private static Map<String, FileCacheRepository> objMap = new HashMap<String, FileCacheRepository>();
	
	private String myKey;
	private File dir;
	private PrintWriter writer;
	private static String indexFileName = "index.txt";
	
	private FileCacheRepository(String site, String category) {
		this.myKey = getKey(site, category);
		dir = new File(CatbotSettings.path("FILEREPO/"+site+"/"+category));
		dir.mkdirs();
		try {
			writer = new PrintWriter(new File(dir, indexFileName));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public void close(){
		//자신의 repository를 닫는다.
		logger.debug("FileCacheRepository 닫기!");
		FileCacheRepository.objMap.remove(myKey);
		writer.close();
	}
	private static String getKey(String site, String category) {
		return site +"_"+category;
	}
	public static FileCacheRepository getInstance(String site, String category){
		String key = getKey(site, category);
		FileCacheRepository obj = objMap.get(key);
		if(obj == null){
			obj = new FileCacheRepository(site, category);
			logger.debug("FileDumper 생성! {}:{}", site, category);
			objMap.put(key, obj);
		}
		
		return obj;
	}
	
	private synchronized void writeIndex(String urlKey, String url){
		writer.println(urlKey +"\t"+url);
		writer.flush();
	}
	private static String getURLKey(String url) {
		return MD5.getMD5String(url);
	}
	
	public String get(String url){
		String key = getURLKey(url);
		File f = new File(dir, key);
		
		if(f.exists()){
			logger.debug("2파일캐시 저장소에서 페이지 읽어옴. {}", url);
			StringBuffer sb = new StringBuffer();
			BufferedReader br = null;
			try{
				br = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
				String line = null;
				while((line = br.readLine()) != null ){
					sb.append(line);
					sb.append("\n");
				}
				return sb.toString();
				
			}catch(IOException e){
				e.printStackTrace();
			} finally {
				if(br != null){
					try {
						br.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		
		return null;
		
	}
	
	public File getFile(String url){
		String key = getURLKey(url);
		File f = new File(dir, key);
		
		if(f.exists() && f.length() > 0){
			logger.debug("파일캐시 저장소에서 File 읽어옴. {}", url);
			return f;
		}
		return null;
	}
	
	public InputStream getInputStream(String url){
		String key = MD5.getMD5String(url);
		File f = new File(dir, key);
		
		if(f.exists()){
			logger.debug("파일캐시 저장소에서 페이지 읽어옴. {}", url);
			try{
				
				return new FileInputStream(f);
				
			}catch(IOException e){
				e.printStackTrace();
			}
		}
		else
			logger.debug("파일캐시 저장소 파일이 존재하지 않음 {}", url);
		
		return null;
		
	}
	
	public void put(String url, InputStream is){
		String key = MD5.getMD5String(url);
		writeIndex(key, url);
		File f = new File(dir, key);
		FileOutputStream fos = null;
		try{
			fos = new FileOutputStream(f);
			byte[] buf = new byte[65536];
			while(true){
				int nread = is.read(buf);
				if(nread == -1)
					break;
				
				fos.write(buf, 0, nread);
			}
		}catch(IOException e){
			e.printStackTrace();
		} finally {
			if(fos != null){
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public void put(String url, File file){
		String key = getURLKey(url);
		writeIndex(key, url);
		logger.debug("파일캐시기록 {} >> {}", file.getName(), url);
		
		File dest = new File(dir, key);
		try {
			FileUtils.copyFile(file, dest);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
