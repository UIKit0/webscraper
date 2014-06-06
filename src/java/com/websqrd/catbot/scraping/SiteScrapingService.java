package com.websqrd.catbot.scraping;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.websqrd.catbot.exception.CatbotException;
import com.websqrd.catbot.setting.CatbotConfig;
import com.websqrd.catbot.setting.CatbotSettings;
import com.websqrd.catbot.setting.CategoryConfig;
import com.websqrd.catbot.setting.SiteConfig;
import com.websqrd.catbot.setting.SiteLoginConfig;
import com.websqrd.catbot.util.urlDecode;
import com.websqrd.catbot.web.HttpClientWrapper;
import com.websqrd.catbot.web.PooledHttpClientManager;


public class SiteScrapingService {
	protected final static Logger logger = LoggerFactory.getLogger(SiteScrapingService.class);
	protected SiteConfig siteConfig;
	protected HttpClient httpClient = null;//HttpClientWrapper.wrapClient(new DefaultHttpClient());
	protected HttpContext httpContext = new BasicHttpContext();
	private Map<String, CategoryConfig> categoryConfigList;
	private String testBuffer;
	
	public SiteScrapingService(SiteConfig siteConfig) throws CatbotException {
		CatbotConfig globalConfig = null;
		try {
			globalConfig = CatbotSettings.getGlobalConfig();
		} catch (Exception e1) {
			throw new CatbotException("셋팅로딩중 에러발생.", e1);
		}
		String site = siteConfig.getSiteName();
		this.siteConfig = siteConfig;

		//
		// httpClient 초기화.
		//
		
		httpClient = PooledHttpClientManager.getInstance().getHttpClient(siteConfig.getTimeout());
		logger.trace(" http Socket/Connection Time Out : {}", siteConfig.getTimeout());
		
		String agent = siteConfig.getAgent();
		if (agent == null) {
			agent = globalConfig.getAgent();
		}
		if (agent != null) {
			httpClient.getParams().setParameter(CoreProtocolPNames.USER_AGENT, agent);
			logger.debug("Set agent {}", agent);
		} else {
			// TODO 디폴트 agent
		}
		//
		// 카테고리 셋팅 리스트를 가져온다.
		//
		categoryConfigList = CatbotSettings.getSiteCategoryConfigList(site, true);
		int categorySize = categoryConfigList.size();

		//
		// HTTP Cookie 설정.(로그인 세션유지시 필요)
		//
		// local 쿠키 저장소를 준비한다.
		// Create local HTTP context
		// Bind custom cookie store to the local context

		BasicCookieStore cookieinputStore = new BasicCookieStore();
		Map<String, String> cookies = siteConfig.getCookieList();
		// 어차피 쿠키는 생성해서 넣어야 하기 때문에 무조건 셋팅한다.
		// 다만 쿠키 설정값이 있으면 값을 추가 해서 입력 한다.
		if (cookies != null) {
			Iterator<String> itr = cookies.keySet().iterator();

			while (itr.hasNext()) {
				String key = (String) itr.next();
				String value = cookies.get(key);
				cookieinputStore.addCookie(new BasicClientCookie(key, value));
				logger.debug("set cookie>> {} >> {}", key, value);
			}
		}
		httpContext.setAttribute(ClientContext.COOKIE_STORE, cookieinputStore);

		SiteLoginConfig loginConfig = siteConfig.getLogin();

		if (loginConfig != null) {
			try {
				doLoginWithCookie(loginConfig);
			} catch (Exception e) {
				throw new CatbotException("로그인중 에러발생.", e);
			}
		}
		HttpClientParams.setCookiePolicy(httpClient.getParams(),CookiePolicy.BROWSER_COMPATIBILITY);
	}

	private void doLoginWithCookie(SiteLoginConfig loginConfig) throws Exception {
		String charset = siteConfig.getCharset();
		String loginUrl = loginConfig.getUrl();
		loginUrl = urlDecode.getDecodedUrl(loginUrl, charset);
		logger.debug("loginUrl = {}", loginUrl);
		// 파라미터를 준비한다.
		List<NameValuePair> formParams = new ArrayList<NameValuePair>();
		Map<String, String> map = loginConfig.getUrlParam();
		if (map != null) {
			Iterator<Map.Entry<String, String>> paramIter = map.entrySet().iterator();
			while (paramIter.hasNext()) {
				Entry<String, String> entry = (Entry<String, String>) paramIter.next();
				formParams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
			}
		}

		//
		// 파라미터 셋팅.
		//
		String method = loginConfig.getMethod();
		HttpUriRequest httpRequest = null;
		if (method.equalsIgnoreCase("GET")) {
			HttpGet httpGet = new HttpGet();
			String paramStr = convertParamToQueryString(formParams, charset);
			httpGet.setURI(new URI(loginUrl + paramStr));
			httpRequest = (HttpUriRequest) httpGet;
		} else if (method.equalsIgnoreCase("POST")) {
			HttpPost httpPost = new HttpPost(loginConfig.getUrl());
			UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formParams, charset);
			httpPost.setEntity(entity);
			httpRequest = (HttpUriRequest) httpPost;
		}

		// ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------
		
		BasicCookieStore cookieinputStore = (BasicCookieStore) httpContext.getAttribute(ClientContext.COOKIE_STORE);

		Map<String, String> cookies = siteConfig.getCookieList();
		if (cookies != null) {
			Iterator<String> itr = cookies.keySet().iterator();

			while (itr.hasNext()) {
				String key = (String) itr.next();
				String value = cookies.get(key);
				cookieinputStore.addCookie(new BasicClientCookie(key, value));
			}
		}
//		httpContext.setAttribute(ClientContext.COOKIE_STORE, cookieinputStore);
		// / 쿠키 스토어는 기본적으로 추가 한다.
		// 실제 값이 있을 경우에는 값을 할당하여 추가 하고,
		// 그렇지 않을 경우에는 빈 공간으로 추가 한다.
		// ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------

		HttpResponse response = httpClient.execute(httpRequest, httpContext);
		logger.debug("Login Status >> {}", response.getStatusLine());
		List<Cookie> list = cookieinputStore.getCookies();
		for (int i = 0; i < list.size(); i++) {
			Cookie c = list.get(i);
			logger.trace("cookie {} >> {}", c.getName(), c.getValue());
		}
		InputStream is = new BufferedHttpEntity(response.getEntity()).getContent();
		Document document = Jsoup.parse(is, siteConfig.getCharset(), "");
		String result = document.toString();
		is.close();		
	}


	protected static String convertParamToQueryString(List<NameValuePair> nvp,
			String encoding) throws UnsupportedEncodingException {
		String ret = "";
		if (nvp.size() > 0) {
			ret = "?";
			for (int inx = 0; inx < nvp.size(); inx++) {
				if (inx != 0) {
					ret += "&";
				}
				ret += URLEncoder.encode(nvp.get(inx).getName(), encoding)
						+ "="
						+ URLEncoder.encode(nvp.get(inx).getValue(), encoding);
			}
		}
		return ret;
	}
	public int doService(String category, String url, String scrapAction) throws CatbotException {
		return doService(category, url, scrapAction, false);
	}
	public int doService(String category, String url, String scrapAction, boolean test) throws CatbotException {
		int totalCount = 0;

		CategoryConfig categoryConfig = categoryConfigList.get(category);
		
		//FIXME 전영역에서공유하는 객체인 categoryConfigList 에 action을 셋팅하는 방식은 위험하므로 차후에 수정하도록 한다.  
		if ( scrapAction != null && scrapAction.equals("") == false )
			categoryConfig.setScrapAction(scrapAction);
		
		logger.debug("doService catenme,configname={},  scrapAction = {}", category+categoryConfig.getCategoryName(), categoryConfig.getScrapAction());		

		// logger.debug("page => {}",
		// categoryConfig.getPageList().get(0).getUrl());

		CategoryScrapingProcess processor = null;
		try {
			processor = new CategoryScrapingProcess(siteConfig, categoryConfig, httpContext, test);
		} catch (Exception e) {
			throw new CatbotException("수집프로세스 생성중 에러발생.", e);
		}
		
		FileGoneList fileGoneList = null;
		if(!test){ 
			fileGoneList = FileGoneList.getInstance(siteConfig.getSiteName(), categoryConfig.getCategoryName());
			processor.setGoneList(fileGoneList);
		}
		
		try {
			// totalCount = processor.doProcess(isFull);
			if (url == null) {
				totalCount = processor.doProcess();
			} else {
				totalCount = processor.doProcess(url);
			}
		} catch (Exception e) {
			throw new CatbotException("수집프로세스 진행중 에러발생.", e);
		} finally {
			if(fileGoneList != null){
				fileGoneList.storeToFile();
			}
			processor.close();
			fileGoneList = null;			
		}

		if(test){
			testBuffer = processor.getTestBuffer();
		}
		return totalCount;
	}
	public String getTestBuffer(){
		return testBuffer;
	}
	public static void main(String[] args) {

	}

}
