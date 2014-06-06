/*
 * Copyright (C) 2011 WebSquared Inc. http://websqrd.com
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package com.websqrd.catbot.setting;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.websqrd.catbot.exception.CatbotException;
import com.websqrd.catbot.web.HttpUtils;
import com.websqrd.libs.common.DynamicClassLoader;


public class CatbotSettings {
	
	public static final String DEFAULT_AGENT = "WebSquared WebScraper/0.1"; //스키마에 agentstring이 null값일 경우 대체
	public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
	public static final String DEFAULT_REQUEST_METHOD="GET";
	public static final String DEFAULT_URL_ENCODING = "utf-8";
	
	public static String HOME="";
	
	private final static Logger logger = LoggerFactory.getLogger(CatbotSettings.class);
	
	public static String FILE_SEPARATOR = System.getProperty("file.separator");
	public static String PATH_SEPARATOR = System.getProperty("path.separator");
	public static String LINE_SEPARATOR = System.getProperty("line.separator");
	public static String OS_NAME = System.getProperty("os.name");
	
	public final static String catbotConfigFilename = "catbot.xml";
	public final static String siteConfigFilename = "site.xml";
	public final static String categoryPrefix = "schema_";
	public final static String dataHandlerPrefix = "handler_";
	public final static String incInfoFilename = "inc.info";
	
	//configFilename은 standalone 서버일때 사용하나 현재는 필요없음.
	public final static String serverConfigFilename = "webscraper.conf";
	public final static String passwdFilename = "auth";
	
	private static Map<String,Object> settingCache = new HashMap<String,Object>();
	private static Map<String, RepositorySetting> repositorySettingList = new HashMap<String, RepositorySetting>();
	private static Map<String, Map<String, CategoryConfig>> siteCategoryConfigMap = new Hashtable<String, Map<String, CategoryConfig>>();
	private static List<DataHandlerConfig> dataHandlerConfigList = new ArrayList<DataHandlerConfig>();
	public static DynamicClassLoader classLoader;
	
	
	private static String siteRootPath;
	public static void setHome(String homePath){
		HOME = homePath;
		if (HOME.length() > 0 && !HOME.endsWith(FILE_SEPARATOR)) {
			HOME = HOME + FILE_SEPARATOR;
		}
		
		logger.info("Setting Home = "+HOME);
		String pathSeparator = System.getProperty("path.separator");
		
		CatbotServerConfig config = getCatbotServerConfig(true);
		int dynamicClassCount =  config.getInt("dynamic.classpath.count");
		String classPaths="";
		for ( int i = 0 ; i < dynamicClassCount ; i ++ )
		{
			String extClassPath = config.getString("dynamic.classpath."+(i+1));
			if(extClassPath != null && extClassPath.length() > 0){
				if ( classPaths != null && classPaths.trim().length() > 0 )
					classPaths = classPaths+ pathSeparator + extClassPath;
				else
					classPaths = extClassPath; 
					
			}
		}
		
		if(classPaths != null && classPaths.length() >  0){
			classLoader = new DynamicClassLoader(HOME, classPaths);
			logger.info("동적클래스패스 로딩 >> {}", classPaths);
		}else{
			classLoader = new DynamicClassLoader();
		}
		classLoader.start();
		
		
		CatbotServerConfig sererConfig = getCatbotServerConfig(true);
		CatbotConfig catbotConfig = getGlobalConfig(true);
		if(sererConfig == null || catbotConfig == null){
			throw new RuntimeException("수집기 셋팅을 로드하지 못했습니다.sererConfig="+sererConfig+", catbotConfig="+catbotConfig);
		}else{
			siteRootPath = catbotConfig.getSiteRootPath();
		}
		logger.info("수집사이트 셋팅경로 = {}/", siteRootPath);
	}
	
	public static String getHOME()
	{
		return HOME;
	}
	
	public static String path(String path){
		if(path.startsWith("/")){
			return path;
		}
		return HOME + path;
	}
	
	public static CatbotServerConfig getCatbotServerConfig(){
		return getCatbotServerConfig(false);
	}
	public static CatbotServerConfig getCatbotServerConfig(boolean reload){
		if(reload){
			Object obj = getFromCache(serverConfigFilename);
			if(obj != null){
				return new CatbotServerConfig((Properties)obj);
			}
		}
		Properties props = getProperties(serverConfigFilename);
		return new CatbotServerConfig(props);
		
	}
	//webscraper.conf 파일저장 
	public static void storeServerConfig(CatbotServerConfig config){
		if(config == null){
			logger.error("Config file is null.");
		}else{
			Properties props = config.getProperties();
			storeProperties(props, serverConfigFilename);
		}
		
	}
	
	/*
	 * DB 저장소 셋팅파일을 읽어옴.
	 * 
	 * */
	public static Map<String, RepositorySetting> getRepositoryList(){
		return getRepositoryList(false);
	}
	
	public static Map<String, RepositorySetting> getRepositoryList(boolean reload){
		if(reload || repositorySettingList.isEmpty()){
			repositorySettingList.clear();
			
			String dirFile = HOME + "conf";
			Collection list = FileUtils.listFiles(new File(dirFile), new IOFileFilter() {
				
				public boolean accept(File dir, String name) {
					if(name.startsWith("repository")){
						return true;
					}
					return false;
				}
				
				public boolean accept(File file) {
					if(file.getName().startsWith("repository")){
						return true;
					}
					return false;
				}
			}, null);
			
			Iterator iter = list.iterator();
			while(iter.hasNext()){
				File file = (File)iter.next();
				String fileName = file.getName();
				int s= fileName.indexOf('_');
				int e = fileName.indexOf('.');
				String repositoryName = fileName.substring(s+1, e);
				repositorySettingList.put(file.getName(), getRepositorySetting(repositoryName, true));
			}
		}
		return repositorySettingList;
	}
	
	public static void addRepository(String repoId){
		initRepo(repoId);
	}
	
	public static void initRepo(String repoId){
		String repoFilename = "repository_"+repoId+".xml";
		String contents = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+
								"<!DOCTYPE properties SYSTEM \"http://java.sun.com/dtd/properties.dtd\">"+
								"<properties>"+
								"<entry key=\"id\">"+repoId+"</entry>"+
								"<entry key=\"encoding\"></entry>"+
								"<entry key=\"vendor\"></entry>"+
								"<entry key=\"host\"></entry>"+
								"<entry key=\"port\"></entry>"+
								"<entry key=\"db\"></entry>"+
								"<entry key=\"user\"></entry>"+
								"<entry key=\"password\"></entry>"+
								"<entry key=\"parameter\"></entry>"+
								"</properties>";
		String configFile = getKey(repoFilename);
		FileOutputStream writer = null;
		try {
			writer = new FileOutputStream(configFile);
			writer.write(contents.getBytes());
		} catch (IOException e) {
			logger.error(e.getMessage(),e);
		} finally{
			try {
				writer.close();
			} catch (IOException e) {
				//ignore
			}
			
		}	
	}
	public static boolean existsRepo(String id){
		String repoFilename = "repository_"+id+".xml";
		String repoConfigFileDir = getKey(repoFilename);
		File frepo = new File(repoConfigFileDir);
		if(frepo.exists()){
			return true;
		}else{
			return false;
		}
	}
	public static boolean deleteRepo(String id){
		String repoFilename = "repository_"+id+".xml";
		String repoConfigFileDir = getKey(repoFilename);
		File frepo = new File(repoConfigFileDir);
		if(frepo.exists()){
			try {
				FileUtils.forceDelete(frepo);
			} catch (IOException e) {
				logger.error(e.getMessage(),e);
			}
			return true;
		}else{
			return false;
		}
	}
	public static boolean setUseRepo(String useId){
		Element root = null;
		String catbotConfigFileDir = getKey(catbotConfigFilename);
		File fcatbot = new File(catbotConfigFileDir);
		if(fcatbot.exists()){
			root = getXml(catbotConfigFilename);
		}else{
			logger.error("There is no catbot.xml file.");
			return false;
		}
		setUseRepo0(useId,root);
		return true;
	}
	
	private static int setUseRepo0(String useId, Element root) {
		Element ele = root.getChild("repository");
		ele.setText(useId);
		String catbotConfigFileDir = getKey(catbotConfigFilename);
		XMLOutputter xmlOut = new XMLOutputter();
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(new File(catbotConfigFileDir));
			xmlOut.output(root.getDocument(), fos);
		} catch (FileNotFoundException e) {
			logger.error(e.getMessage(),e);
			return 3;
		} catch (IOException e) {
			logger.error(e.getMessage(),e);
			return 3;
		}finally{
			try {
				fos.close();
			} catch (IOException e) {
				logger.error(e.getMessage(),e);
			}
		}
		
		return 0;
	}
	
	public static int updateRepo(String id, String key, String value) throws SettingException {
		Element root = null;
		String repoFilename = "repository_"+id+".xml";
		String repoConfigFileDir = getKey(repoFilename);
		File frepo = new File(repoConfigFileDir);
		if(frepo.exists()){
			root = getXml(repoFilename);
		}else{
			try {
				FileUtils.touch(frepo);
			} catch (IOException e) {
				throw new SettingException(e.getMessage());
			}
			root = getXml(repoFilename);
		}		
		
		if(root == null)
			return 3;
		
		return updateRepo0(id, root, key, value);
	}
	
	private static int updateRepo0(String id, Element root, String key, String value) {
		List fields = root.getChildren("entry");
		String repoFilename = "repository_"+id+".xml";
		String repoConfigFileDir = getKey(repoFilename);
		boolean isExists = false;
		for (int i = 0; i < fields.size(); i++) {
			Element el = (Element)fields.get(i);
			if(key.equalsIgnoreCase(el.getAttributeValue("key"))){
				isExists = true;
				el.setText(value);
				logger.debug("el key = {}, value = {}",key,value);
			}
		}		
		
		if ( isExists == false )
		{//속성이 없으므로 추가.						
			root.getChildren().add(new Element("entry").setAttribute("key", key).setText(value));			
			logger.debug("append new el key = {}, value = {}",key,value);
		}
		
		
		XMLOutputter xmlOut = new XMLOutputter();
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(new File(repoConfigFileDir));
			xmlOut.output(root.getDocument(), fos);
		} catch (FileNotFoundException e) {
			logger.error(e.getMessage(),e);
			return 3;
		} catch (IOException e) {
			logger.error(e.getMessage(),e);
			return 3;
		}finally{
			try {
				fos.close();
			} catch (IOException e) {
				logger.error(e.getMessage(),e);
			}
		}
		
		return 0;
	}

	/*
	 * 특정이름의 DB저장소 셋팅을 읽어온다.
	 * */
	public static RepositorySetting getRepositorySetting(String repositoryName, boolean reload){
		String repositoryFileName = "repository_"+repositoryName+".xml";
		if(!reload){
			Properties p = (Properties) getFromCache(repositoryFileName);
			if(p != null) 
				return new RepositorySetting(p);
		}
		return new RepositorySetting(getXmlProperties(repositoryFileName));
	}
	public static void storeRepositorySetting(Properties props, String repositoryName){
		storeXmlProperties(props, repositoryName+".xml");
	}
	
	
	/*
	 * catbot.xml 셋팅파일을 읽어온다.
	 *  
	 * */
	public static CatbotConfig getGlobalConfig() {
		return getGlobalConfig(false);
	}
	public static CatbotConfig getGlobalConfig(boolean reload) {
		Element root = null;
		CatbotConfig config = null;
		
		if(!reload){
			root = (Element) getFromCache(catbotConfigFilename);
		}
		if (root == null){
			root = getXml(catbotConfigFilename);
		}
		if(root == null)
			return null;
		
		config = new CatbotConfig(root);

		return config;
	}
	
	/*
	 * 각 사이트 하위의 site.xml 셋팅파일을 읽어온다.
	 * */
	public static String getSiteConfigRaw(String site) {
		return readXmlRaw(getSiteKey(site, siteConfigFilename));
	}
	public static boolean saveSiteConfigRaw(String site, String setting) {
		return saveXmlRaw(getSiteKey(site, siteConfigFilename), setting);
	}
	public static SiteConfig getSiteConfig(String site) {
		return getSiteConfig(site,false);
	}
	public static SiteConfig getSiteConfig(String site, boolean reload) {
		Element root = null;
		SiteConfig config = null;
		
		if(!reload){
			root = (Element) getFromCache(site, siteConfigFilename);
		}
		
		if (root == null){
			root = getSiteXml(site, siteConfigFilename);
		}
		
		if(root == null)
			return null;
		
		config = new SiteConfig(site, root);

		return config;
	}
	public static Map<String, CategoryConfig> getSiteCategoryConfigList(String site){
		return getSiteCategoryConfigList(site, false);
	}
	public static Map<String, CategoryConfig> getSiteCategoryConfigList(String site, boolean reload){
		Map<String, CategoryConfig> categoryMap = siteCategoryConfigMap.get(site);
		if(reload || categoryMap == null || categoryMap.isEmpty()){
			categoryMap = new Hashtable<String, CategoryConfig>();
			
			String siteHome = getSiteHome(site);
			File dir = new File(siteHome);
			if(!dir.isDirectory()){
				logger.error("사이트경로 {}가 존재하지 않습니다.", siteHome);
				return null;
			}
			Collection list = FileUtils.listFiles(dir, new IOFileFilter() {
				
				public boolean accept(File dir, String name) {
					if(name.startsWith(categoryPrefix)){
						return true;
					}
					return false;
				}
				
				public boolean accept(File file) {
					if(file.getName().startsWith(categoryPrefix)){
						return true;
					}
					return false;
				}
			}, null);
			
			Iterator iter = list.iterator();
			while(iter.hasNext()){
				File file = (File)iter.next();
 				String fileName = file.getName();
 				
 				if ( fileName.toLowerCase().endsWith(".xml") == false )
 					continue;
 				
				int s= fileName.indexOf('_');
				int e = fileName.indexOf('.');
				String categoryName = fileName.substring(s+1, e);
				try{
					categoryMap.put(categoryName, getCategoryConfig(site, categoryName, true));
				}catch(Exception e1){
					e1.printStackTrace();
					logger.error("카테고리 설정파일을 읽다가 에러발생. {} >> {}", site, categoryName);
				}
			}
			
		}
		siteCategoryConfigMap.put(site, categoryMap);
		return categoryMap;
	}
	/*
	 * 각 사이트 하위의 카테고리별 셋팅파일을 읽어온다.
	 * */
	public static String getCategoryConfigRaw(String site, String category) {
		String categoryConfigFilename = categoryPrefix + category + ".xml";
		return readXmlRaw(getSiteKey(site, categoryConfigFilename));
	}
	public static boolean saveCategoryConfigRaw(String site, String category, String setting) {
		String categoryConfigFilename = categoryPrefix + category + ".xml";
		return saveXmlRaw(getSiteKey(site, categoryConfigFilename), setting);
	}
	public static CategoryConfig getCategoryConfig(String site, String category) {
		return getCategoryConfig(site, category, false);
	}
	public static CategoryConfig getCategoryConfig(String site, String category, boolean reload) {
		Element root = null;
		CategoryConfig config = null;
		String categoryConfigFilename = categoryPrefix + category + ".xml";
		
		if(!reload){
			root = (Element) getFromCache(site, categoryConfigFilename);
		}
		
		if (root == null){
			root = getSiteXml(site, categoryConfigFilename);
		}
		
		if(root == null)
			return null;
		
		config = new CategoryConfig(site, category, root);

		return config;
		
	}
	
	public static DataHandlerConfig getDataHandlerConfig(String dataHandler, boolean reload) {
		Element root = null;
		DataHandlerConfig config = null;
		String dataHandlerConfigFilename = dataHandlerPrefix + dataHandler + ".xml";
		
		if(!reload){
			root = (Element) getFromCache(dataHandlerConfigFilename);
		}
		
		if (root == null){
			root = getXml(dataHandlerConfigFilename);
		}
		
		if(root == null)
			return null;
		
		config = new DataHandlerConfig(dataHandler, root);

		return config;
		
	}
	
	public static List<DataHandlerConfig> getDataHandlerConfigList() {
		return getDataHandlerConfigList(false);
	}
	
	public static List<DataHandlerConfig> getDataHandlerConfigList(boolean reload) {
		if(reload || dataHandlerConfigList.isEmpty()){
			dataHandlerConfigList.clear();
			
			String dirFile = HOME + "conf";
			Collection list = FileUtils.listFiles(new File(dirFile), new IOFileFilter() {
				
				public boolean accept(File dir, String name) {
					if(name.startsWith("handler_")){
						return true;
					}
					return false;
				}
				
				public boolean accept(File file) {
					if(file.getName().startsWith("handler_")){
						return true;
					}
					return false;
				}
			}, null);
			
			Iterator iter = list.iterator();
			while(iter.hasNext()){
				File file = (File)iter.next();
				String fileName = file.getName();
				int s= fileName.indexOf('_');
				int e = fileName.indexOf('.');
				String handlerName = fileName.substring(s+1, e);
				try{
					dataHandlerConfigList.add(getDataHandlerConfig(handlerName, true));
				}catch(Exception e1){
					e1.printStackTrace();
					logger.error("데이터핸들러 설정파일을 읽다가 에러발생. {}", handlerName);
				}
			}
		}
		return dataHandlerConfigList;
	}
	
	private static Element getXml(String filename) {
		String configFile = getKey(filename);
		Element e = readXml(configFile);
		putToCache(e, filename);
		return e;
	}
	private static Element getSiteXml(String site, String filename) {
		String configFile = getSiteKey(site, filename);
		Element e = readXml(configFile);
		putToCache(site, e, filename);
		return e;
	}
	private static String readXmlRaw(String configFile) {
		logger.debug("Read xml = "+configFile);
		try {
			File f = new File(configFile);
			if(!f.exists()){
				return "";
			}else{
				return HttpUtils.readContentFromFile(f);
			}
		} catch (IOException e) {
			logger.error(configFile+" 설정을 읽다가 에러발생.", e);
			return "";
		}
	}
	private static boolean saveXmlRaw(String configFile, String setting) {
		logger.debug("save xml = "+configFile);
		try {
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(configFile)));
			bw.write(setting);
			bw.close();
			return true;
		} catch (IOException e) {
			logger.error(configFile+" 설정을 읽다가 에러발생.", e);
			return false;
		}
	}
	private static Element readXml(String configFile) {
		logger.debug("Read xml = "+configFile);
		Document doc = null;
		try {
			File f = new File(configFile);
			if(!f.exists()){
				return null;
			}
			
			SAXBuilder builder = new SAXBuilder();
			doc = builder.build(f);
			Element e = doc.getRootElement();
			return e;
		} catch(JDOMException e) {
			logger.error("스키마를 읽다가 에러발생.", e);
		} catch(NullPointerException e) {
			logger.error("스키마를 읽다가 에러발생.", e);
		} catch (Exception e) {
			logger.error("스키마를 읽다가 에러발생.", e);
		}
		return null;
		
	}
	
	
	//캐시에서 키이름으로 셋팅객체를 읽어옴.
	private static Object getFromCache(String settingName) {
		String key = HOME + "conf" + FILE_SEPARATOR + settingName;
		synchronized(settingCache){
			return settingCache.get(key);
		}
	}
	private static Object putToCache(Object setting, String settingName) {
		String key = HOME + "conf" + FILE_SEPARATOR + settingName;
		synchronized(settingCache){
			settingCache.put(key, setting);
		}
		return setting;
	}
	//캐시에서 셋팅객체를 읽어옴.
		private static Object getFromCache(String site, String settingName) {
			String key = getSiteKey(site, settingName);
			synchronized(settingCache){
				return settingCache.get(key);
			}
		}
	//셋팅객체를 캐시에 넣음 
	private static Object putToCache(String site, Object setting, String settingName) {
		String key = getSiteKey(site, settingName);
		synchronized(settingCache){
			settingCache.put(key, setting);
		}
		return setting;
	}
	public static String getPath(String dirName, String filename){
		return HOME + dirName + FILE_SEPARATOR + filename;
	}
	public static String getSiteKey(String site, String filename){
		return HOME + siteRootPath + FILE_SEPARATOR + site + FILE_SEPARATOR + filename;
	}
	public static String getKey(String filename){
		return HOME + "conf" + FILE_SEPARATOR + filename;
	}
	
	public static String[] isCorrectPasswd(String username, String passwd){
		if(username == null)
			return null;
		
		Properties props = null;
		Object obj = getFromCache(passwdFilename);
		if(obj != null){
			props = (Properties)obj;
		}else{
			props = getProperties(passwdFilename);
		}
	logger.debug("props = {}, username = {}",props,username);
		String p = props.getProperty(username);
		if(p == null)
			return null;
		
		String p2 = encryptPasswd(passwd);
		if(p2.equalsIgnoreCase((String)p)){
			String[] log = new String[2];
			String ip = props.getProperty(username+".ip");
			if(ip == null){
				log[0] = "";
			}else{
				log[0] = ip;
			}
			
			String time = props.getProperty(username+".time");
			if(time == null){
				log[1] = "";
			}else{
				log[1] = time;
			}
			return log;
		}else{
			return null;
		}
	}
	
	public static boolean isAuthUsed(){
		Properties props = null;
		Object obj = getFromCache(passwdFilename);
		if(obj != null){
			props = (Properties)obj;
		}else{
			props = getProperties(passwdFilename);
		}
		
		return !("false".equalsIgnoreCase(props.getProperty("use")));

	}
	public static void storePasswd(String username, String passwd){
		Properties props = getProperties(passwdFilename);
		props.put(username, encryptPasswd(passwd));
		storeProperties(props, passwdFilename);
		putToCache(props, passwdFilename);
	}
	
	public static void storeAccessLog(String username, String ip){
		Properties props = getProperties(passwdFilename);
		props.setProperty(username+".ip", ip);
		props.setProperty(username+".time", getSimpleDatetime());
		storeProperties(props, passwdFilename);
		putToCache(props, passwdFilename);
	}
	
	private static String encryptPasswd(String passwd){
		String[] hexArray = { "00","01","02","03","04","05","06","07","08","09","0A","0B","0C","0D","0E","0F", "10","11","12","13","14","15","16","17","18","19","1A","1B","1C","1D","1E","1F", "20","21","22","23","24","25","26","27","28","29","2A","2B","2C","2D","2E","2F", "30","31","32","33","34","35","36","37","38","39","3A","3B","3C","3D","3E","3F", "40","41","42","43","44","45","46","47","48","49","4A","4B","4C","4D","4E","4F", "50","51","52","53","54","55","56","57","58","59","5A","5B","5C","5D","5E","5F", "60","61","62","63","64","65","66","67","68","69","6A","6B","6C","6D","6E","6F", "70","71","72","73","74","75","76","77","78","79","7A","7B","7C","7D","7E","7F", "80","81","82","83","84","85","86","87","88","89","8A","8B","8C","8D","8E","8F", "90","91","92","93","94","95","96","97","98","99","9A","9B","9C","9D","9E","9F", "A0","A1","A2","A3","A4","A5","A6","A7","A8","A9","AA","AB","AC","AD","AE","AF", "B0","B1","B2","B3","B4","B5","B6","B7","B8","B9","BA","BB","BC","BD","BE","BF", "C0","C1","C2","C3","C4","C5","C6","C7","C8","C9","CA","CB","CC","CD","CE","CF", "D0","D1","D2","D3","D4","D5","D6","D7","D8","D9","DA","DB","DC","DD","DE","DF", "E0","E1","E2","E3","E4","E5","E6","E7","E8","E9","EA","EB","EC","ED","EE","EF", "F0","F1","F2","F3","F4","F5","F6","F7","F8","F9","FA","FB","FC","FD","FE","FF"};
		StringBuffer sb  = new StringBuffer();
		try {
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			byte[] encData = md5.digest(passwd.getBytes());
			for (int i = 0; i < encData.length; i++) {
				sb.append(hexArray[0xff & encData[i]]);
			}
		} catch (NoSuchAlgorithmException e) { 
			logger.error(e.getMessage(),e);
		}
		return sb.toString();
	}
	private static Properties getXmlProperties(String xmlFilename){
		String configFile = getKey(xmlFilename);
		logger.debug("Read properties = {}", configFile);
		Properties result = new Properties();
		try {
			result.loadFromXML(new FileInputStream(configFile));
			putToCache(result, xmlFilename);
			return result;
		} catch (FileNotFoundException e) {
			logger.error(e.getMessage(),e);
		} catch (IOException e) {
			logger.error(e.getMessage(),e);
		}
		return null;
	}
	
	private static void storeXmlProperties(Properties props, String xmlFilename){
		String configFile = getKey(xmlFilename);
		logger.debug("Store properties = {}", configFile);
		FileOutputStream writer = null;
		try {
			writer = new FileOutputStream(configFile);
			props.storeToXML(writer, new Date().toString());
			putToCache(props, xmlFilename);
		} catch (IOException e) {
			logger.error(e.getMessage(),e);
		} finally{
			try {
				writer.close();
			} catch (IOException e) {
				//ignore
			}
			
		}
	}
	
	private static Properties getProperties(String filename){
		String configFile = HOME + "conf" + FILE_SEPARATOR + filename;
		Properties result = new Properties();
		try {
			File f = new File(configFile);
			if(!f.exists()){
				f.createNewFile();
			}
			result.load(new FileInputStream(f));
			putToCache(result, filename);
			return result;
			
		} catch (FileNotFoundException e) {
			logger.error(e.getMessage(),e);
		} catch (IOException e) {
			logger.error(e.getMessage(),e);
		}
		return null;
	}
	
	private static void storeProperties(Properties props, String filename){
		String configFile = getKey(filename);
		FileOutputStream writer = null;
		try {
			writer = new FileOutputStream(configFile);
			props.store(writer, null);
			putToCache(props, filename);
		} catch (IOException e) {
			logger.error(e.getMessage(),e);
		} finally{
			try {
				writer.close();
			} catch (IOException e) {
				//ignore
			}
			
		}
	}
	
	public static String getSimpleDatetime(){
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
	}
	
	public static String getIncInfo(String site, String category){
		String result = "";
		File f = new File(getSiteKey(site, category + "_" + incInfoFilename));
		if(!f.exists()){
			try {
				f.createNewFile();
			} catch (IOException e) {
				logger.error(e.getMessage(),e);
			}
		}
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
			result = reader.readLine();
			reader.close();
		} catch (FileNotFoundException e) {
			logger.error(e.getMessage(),e);
		} catch (IOException e) {
			logger.error(e.getMessage(),e);
		}
		return result;
	}
	
	public static void saveIncInfo(String site, String category, String incInfo){
		try{
			File f = new File(getSiteKey(site, category + "_" + incInfoFilename));
			PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(f, false)));
			writer.println(incInfo);
			writer.close();
			logger.debug("Save {} / {}  pk >> {} ", new Object[]{site, category, incInfo});
		}catch(IOException e){
			logger.error(e.getMessage(),e);
		}
	}
	
	public static String getSiteHome(String site){
		return HOME + siteRootPath + FILE_SEPARATOR + site + FILE_SEPARATOR;
	}
	
	public static void initSite(String site){
		//schema
		String contents = "<site name=\""+site+"\" version=\"1.0\">"+LINE_SEPARATOR+"</catbot>";
		String configFile = getSiteKey(site, siteConfigFilename);
		FileOutputStream writer = null;
		try {
			writer = new FileOutputStream(configFile);
			writer.write(contents.getBytes());
		} catch (IOException e) {
			logger.error(e.getMessage(),e);
		} finally{
			try {
				writer.close();
			} catch (IOException e) {
				//ignore
			}
			
		}
		//make inc info file
		configFile = getSiteKey(site, incInfoFilename);
		try {
			FileUtils.touch(new File(configFile));
		} catch (IOException e) {
			logger.error(e.getMessage(),e);
		}
	}
	
	//TODO 셋팅내용 추가, 삭제, 업데이트등..
	public static int updateCatbotConfig(String site, String key, String value) throws SettingException, CatbotException {
		Element root = null;
		String schemaFileDir = getSiteKey(site, siteConfigFilename);
		File schemaFile = new File(schemaFileDir);
		if(schemaFile.exists()){
			root = getSiteXml(site, siteConfigFilename);
		}
		if(root == null)
			return 3;
		
		Element pageList = root.getChild("pagelist");
		List pages = pageList.getChildren("page");
		Element ele = (Element)pages.get(0);
		ele.setAttribute(key, value);
		
		return 0;
	}

	
	
	
	
}
