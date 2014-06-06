package com.websqrd.catbot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.websqrd.catbot.exception.CatbotException;
import com.websqrd.catbot.scraping.SiteScrapingService;
import com.websqrd.catbot.setting.CatbotSettings;
import com.websqrd.catbot.setting.CategoryConfig;
import com.websqrd.catbot.setting.SiteConfig;
import com.websqrd.libs.common.Formatter;



public class CatBot {
	private final static Logger logger = LoggerFactory.getLogger(CatBot.class);
	private String testBuffer;
	
	public int doWork(String site, String category, String scrapAction, boolean test) throws CatbotException {
		return doWork(site, category, null, scrapAction, test);
	}
	public int doWork(String site, String category, String url, String scrapAction, boolean test) throws CatbotException {
		//셋팅스키마 객체를 얻는다.
		int totalCount = 0;
		SiteConfig schema = null;
		try{
			schema = CatbotSettings.getSiteConfig(site, true);
		} catch (Exception e) {
			throw new CatbotException("셋팅로딩중 에러발생! = "+site, e);
		}
		
		if (schema != null) {
			long st = System.currentTimeMillis();
			
			//수집전에 셋팅을 재로딩한다.
			CatbotSettings.getCatbotServerConfig(true);
			CatbotSettings.getCategoryConfig(site, category, true);
			SiteScrapingService service = new SiteScrapingService(schema);
			totalCount = service.doService(category, url, scrapAction, test);
			logger.info("수집끝! {}/{} 총 수집문서수 = {}", new Object[]{site, category, totalCount});
			
			logger.info("Running Time = "+Formatter.getFormatTime(System.currentTimeMillis() - st));
			if(test){
//				logger.debug("service.getTestBuffer() >> {}", service.getTestBuffer());
				testBuffer = service.getTestBuffer();
			}
		} else {
			logger.error("해당 스키마 셋팅이 없습니다. site = "+site);
		}
		
		return totalCount;

	}
	public String getTestBuffer(){
		return testBuffer;
	}
	public static void main(String[] args){
		
		if(args == null || args.length < 3){
			System.out.println("Usage : CatBot [home] [sitename] [categoryName] [url] [test]");
			System.exit(0);
		}
		
		String homePath = args[0];
		String siteName = args[1];
		String categoryName = args[2];
		boolean test = false;
		String url = null;
		if(args.length > 3){
			url = args[3];
		}
		if(args.length > 4){
			test = Boolean.parseBoolean(args[4]);
		}
		
		CatbotSettings.setHome(homePath);
		CatBot catbot = new CatBot();
		try {
			catbot.doWork(siteName, categoryName, url, CategoryConfig.SCRAPGETNEW, test);
		} catch (CatbotException e) {
			logger.error("",e);
		}
	}
}
