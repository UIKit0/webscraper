package com.websqrd.catbot.scraping;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.websqrd.catbot.setting.CatbotSettings;
import com.websqrd.catbot.util.urlDecode;
import com.websqrd.libs.crypt.MD5;

public class FileGoneList {
	private final static Logger logger = LoggerFactory.getLogger(FileGoneList.class);
	private final String goneFile = "goneList.txt";
	private final Map<String, String> goneList;
	private String site;
	private String category;
//	private static Map<String, FileGoneList> instanceList = new HashMap<String, FileGoneList>();
	
	public static FileGoneList getInstance(String site, String category)
	{
		String key = site+"|"+category;
//		FileGoneList instance = instanceList.get(key);
//		if ( instance == null )
//		{
			return new  FileGoneList(site, category);
//			instanceList.put(key, instance);
//		}
//		return instance;
	}
	
	public static void releaseInstance(String site, String category)
	{
//		String key = site+"|"+category;
//		instanceList.put(key, null);		
	}

	private  FileGoneList(String site, String category) {
		this.site = site;
		this.category = category;
		File dir = new File(CatbotSettings.path("FILEREPO/" + this.site + "/" + this.category));
		dir.mkdirs();

		File fpGoneFile = new File(dir, goneFile);
		goneList = new ConcurrentHashMap<String, String>();
		if (fpGoneFile.exists()) {
			BufferedReader br = null;
			try {
				br = new BufferedReader(new InputStreamReader(new FileInputStream(fpGoneFile)));
				String line = null;

				while ((line = br.readLine()) != null) {
					String lines[] = line.split("\t");
					if (lines.length > 1)
						goneList.put(lines[0].trim(), lines[1].trim().toLowerCase());
					// lines[0] : key
					// lines[1] : value;
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (br != null) {
					try {
						br.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	public boolean isExists(String url, String encoding) {
		url = url.trim().toLowerCase();
		url = urlDecode.getDecodedUrl(url, encoding);			
		String key = MD5.getMD5String(url);
		String value = goneList.get(key);
		logger.trace("fileList count {}", goneList.size());
		if ( value == null || value.trim().length() == 0 )
			return false;
		else
			return true;
	}

	public void appendUrl(String url, String encoding) {
		//GoneList에 디코딩된 URL 추가.
		url = url.trim().toLowerCase();
		url = urlDecode.getDecodedUrl(url, encoding);				
		String key = MD5.getMD5String(url);
		goneList.put(key, url);
		logger.debug("goneList에 URL 추가 {}, Count : {}", url,goneList.size());
	}
	
	public void storeToFile()
	{
		File dir = new File(CatbotSettings.path("FILEREPO/" + this.site + "/" + this.category));
		dir.mkdirs();

		File fpGoneFile = new File(dir, goneFile);
		
		try
		{
			BufferedWriter bw = null;
			bw = new BufferedWriter( new OutputStreamWriter(new FileOutputStream(fpGoneFile)));
			
			Iterator key_itr = goneList.keySet().iterator();
			
			while ( key_itr.hasNext() )
			{
				String key = (String)key_itr.next();
				String value = goneList.get(key);
				bw.write(key + "\t" + value + "\n");				
			}
			bw.flush();
			bw.close();
		}
		catch ( Exception e )
		{
		logger.debug("Error was happedn while writing  [{}][{}]GoneLIst", site, category);
		e.printStackTrace();
		}
	}

}
