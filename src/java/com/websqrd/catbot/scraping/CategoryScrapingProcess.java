package com.websqrd.catbot.scraping;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.el.ValueSuffix;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.conn.ConnectionPoolTimeoutException;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EncodingUtils;
import org.apache.http.util.EntityUtils;
import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.PrettyXmlSerializer;
import org.htmlcleaner.TagNode;
import org.jdom.JDOMException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.websqrd.catbot.db.RepositoryHandler;
import com.websqrd.catbot.db.ScrapingDAO;
import com.websqrd.catbot.exception.CatbotException;
import com.websqrd.catbot.scraping.modifier.PrefixModifier;
import com.websqrd.catbot.scraping.modifier.ScrapModifier;
import com.websqrd.catbot.scraping.modifier.SuffixModifier;
import com.websqrd.catbot.setting.Block;
import com.websqrd.catbot.setting.CatbotConfig;
import com.websqrd.catbot.setting.CatbotSettings;
import com.websqrd.catbot.setting.CategoryConfig;
import com.websqrd.catbot.setting.Page;
import com.websqrd.catbot.setting.Process;
import com.websqrd.catbot.setting.SiteConfig;
import com.websqrd.catbot.util.TextExtract;
import com.websqrd.catbot.util.urlDecode;
import com.websqrd.catbot.util.urlEncode;
import com.websqrd.catbot.web.PooledHttpClientManager;

import com.websqrd.libs.common.Formatter;
import com.websqrd.libs.xml.XmlUtils;

/**
 * 스크랩핑을 실제 수행하는 로직이 들어있다. 페이지 하나에 대해서만 수행한다.
 * 
 * @author swsong
 * 
 */
public class CategoryScrapingProcess {
	private enum BuildFieldState {
		SUCCESS, FAILED, DUPLICATE, SKIP, COMPLETE;
	};

	private enum PostProcessResult {
		SUCCESS, FAILED, REACH_MAX, NO_MORE_CONTENT;
	};

	private final int SCRAPOK = 1;
	private final int SCRAPFAILD = 0;
	private final int SCRAPDUPLICATE = -1;

	private final int MAX_TEST_COUNT = 3;

	private static Logger scrapingLogger = LoggerFactory.getLogger("SCRAPING_LOG");
	public static int i = 0;
	private final static Logger logger = LoggerFactory.getLogger(CategoryScrapingProcess.class);
	private CatbotConfig globalConfig;
	private SiteConfig siteConfig;
	private CategoryConfig categoryConfig;
	protected HttpClient httpClient;
	protected HttpContext httpContext;
	private String pkFieldId;
	private boolean isPkNumeric;
	private HttpUriRequest request = null;
	private HtmlCleaner htmlCleaner;
	private PrettyXmlSerializer xmlSerializer;
	private String siteCharset;
	private Process process;
	private ScrapingDAO scrapingDAO;
	private SimpleDateFormat defaultDatetimeFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private String variablePatternString = "\\$\\{(\\w*?)\\.([\\w.]*?)\\}";
	private Pattern variablePattern;
	private Pattern resourcePathPattern = Pattern.compile("src=[\"|'](.+?)[\"|']");

	private String domainUrl;
	private String pageRelativeUrl;

	private String encoding;
	private String lastPkValue; // 지난번 수집했던 게시물의 최상 아이디값.

	private String maxPK; // 고정공지글의 최신 pk값.
	private boolean isFixedHeader; // 현재 수집글이 고정공지글인지 여부.
	private Set<String> scrapingPkList; // 수집한 pk의 set을 유지한다. 수집게시물의 중복체크에
	                                    // 사용된다.
	                                    // 셋팅이 잘 되어있으면 중복은 없지만, 사용자의 실수를 대비해
	                                    // double-check를 수행한다.

	private int scrapingIntervalDelayMaxTime;
	private boolean isFixed;
	private Random random = new Random(System.currentTimeMillis());

	private FileCacheRepository fileCacheRepository;
	private FileGoneList fileGoneList;

	private List<String> dupCheckList; // 중복 문서 제거. 타이틀 기준.
	private int dupCheckMaxSize; // 최대 몇건까지 확인할건지..

	private HashMap<String, String> prevResult; // 바로이전 수집한 데이터셋.

	int count = 0; // 총 수집된 데이터 갯수.
	private boolean cacheUsed; // 파일 캐시를 탓는지 여부
	long lap = 0;// 소요 시간 측정을 위한 변수

	static private AtomicInteger refCount = new AtomicInteger();

	public boolean isTest;
	public int testCount = 2;	

	public CategoryScrapingProcess(SiteConfig siteConfig, CategoryConfig categoryConfig, HttpContext httpContext, boolean test) throws Exception {
		globalConfig = CatbotSettings.getGlobalConfig();
		this.siteConfig = siteConfig;
		this.categoryConfig = categoryConfig;
		isFixed = false;

		// 사이트에서 인터벌을 정의 했다면 사이트에서 정의된 인터벌을 사용한다.
		if (siteConfig.getPropertyInt("interval.delay.time.max") != -1) {
			this.scrapingIntervalDelayMaxTime = siteConfig.getPropertyInt("interval.delay.time.max") * 1000;
			String intervalType = siteConfig.getPropertyString("interval.delay.time.type");
			if ("static".equalsIgnoreCase(intervalType))
				isFixed = true;
			// interval 딜레이 타입이 static이 아닐 경우는 랜덤으로 간주한다.
		} else {
			this.scrapingIntervalDelayMaxTime = globalConfig.getInt("interval.delay.time.max") * 1000;
			String intervalType = globalConfig.getProperty("interval.delay.time.type");
			if ("static".equalsIgnoreCase(intervalType))
				isFixed = true;
			// interval 딜레이 타입이 static이 아닐 경우는 랜덤으로 간주한다.
		}

		this.httpContext = httpContext;
		siteCharset = siteConfig.getCharset();
		process = categoryConfig.getProcess();
		process.referenceTemplate(siteConfig.getProcessTemplate());
		CleanerProperties props = new CleanerProperties();
		props.setTranslateSpecialEntities(true);
		props.setOmitComments(true);
		props.setNamespacesAware(false);
		htmlCleaner = new HtmlCleaner(props);
		xmlSerializer = new PrettyXmlSerializer(props);
		scrapingPkList = new HashSet<String>();
		this.isTest = test;

		if (test) {
			scrapingDAO = new MemoryScrapingData();
			scrapingDAO.open();
			scrapingIntervalDelayMaxTime = 500;
			isFixed = true;
		} else if (globalConfig.isDebug()) {
			scrapingDAO = new DebugScrapingData(siteConfig.getSiteName(), categoryConfig.getCategoryName());
			logger.debug("globalConfig.isDebug() = {}, {}", globalConfig.isDebug(), scrapingDAO);
			scrapingDAO.open();
		} else {
			String dataHandlerName = siteConfig.getDataHandler();
			scrapingDAO = RepositoryHandler.getInstance().getScrapingDAO(dataHandlerName);
			if (scrapingDAO == null) {
				throw new CatbotException("dataHandler를 찾을 수 없습니다. DB연결이 가능한지 또는 데이터핸들러가 site.xml에 설정이 되어있는지 확인하십시오. dataHandlerName=" + dataHandlerName);
			}
		}
		// 타이틀이 같은 것은 재 수집하지 않도록 도입된 리스트.
		dupCheckMaxSize = globalConfig.getInt("dup.check.max.size");
		if (dupCheckMaxSize < 0) {
			dupCheckMaxSize = 20;
		}
		dupCheckList = new ArrayList<String>(dupCheckMaxSize);

		httpClient = PooledHttpClientManager.getInstance().getHttpClient(siteConfig.getTimeout());
		logger.trace(" http Socket/Connection Time Out : {}", siteConfig.getTimeout());

		String agent = siteConfig.getAgent();
		if (agent == null) {
			agent = globalConfig.getAgent();
		}
		if (agent != null) {
			httpClient.getParams().setParameter(CoreProtocolPNames.USER_AGENT, agent);
			logger.debug("Set category httpclient agent {}", agent);
		} else {
			// TODO 디폴트 agent
		}
		HttpClientParams.setCookiePolicy(httpClient.getParams(), CookiePolicy.BROWSER_COMPATIBILITY);

		// 변수 패턴등록.
		variablePattern = Pattern.compile(variablePatternString);

		fileCacheRepository = FileCacheRepository.getInstance(siteConfig.getSiteName(), categoryConfig.getCategoryName());
	}

	public void setGoneList(FileGoneList goneList) {
		this.fileGoneList = goneList;
	}

	private boolean isDupTitle(String title) {
		if (title == null) {
			return false;
		}
		boolean result = dupCheckList.contains(title);
		if (dupCheckList.size() >= dupCheckMaxSize) {
			dupCheckList.remove(0);
		}
		dupCheckList.add(title);
		return result;
	}

	public int doProcess() throws Exception {
		try {
			scrapingLogger.info("[{}][{}] Scraping Start.. ", siteConfig.getSiteName(), categoryConfig.getCategoryName());
			long st = System.currentTimeMillis();
			//
			// HTTP Headers 초기화
			//
			HttpParams httpParams = new BasicHttpParams();
			// 1. 글로벌 설정의 http header설정.
			Map<String, String> httpHeader = globalConfig.getHttpHeader();
			if (httpHeader != null) {
				Iterator httpHeaderIter = httpHeader.entrySet().iterator();
				while (httpHeaderIter.hasNext()) {
					Map.Entry<String, String> entry = (Map.Entry) httpHeaderIter.next();
					String key = entry.getKey();
					String val = entry.getValue();
					httpParams.setParameter(key, val);
				}
			}
			// 2. Site 설정의 http header설정. 글로벌 설정에 우선한다.
			Map<String, String> siteHttpHeader = siteConfig.getHttpHeader();
			if (siteHttpHeader != null) {
				Iterator siteHttpHeaderIter = siteHttpHeader.entrySet().iterator();
				while (siteHttpHeaderIter.hasNext()) {
					Map.Entry<String, String> entry = (Map.Entry) siteHttpHeaderIter.next();
					String key = entry.getKey();
					String val = entry.getValue();
					httpParams.setParameter(key, val);
				}
			}

			// int count = 0; //총 수집된 데이터 갯수.
			count = 0;
			List<Page> pageList = categoryConfig.getPageList();
			logger.info("PageList Size = " + pageList.size());

			MAIN_LOOP: for (int pageInx = 0; pageInx < pageList.size(); pageInx++) {
				Page page = pageList.get(pageInx);
				logger.debug("Page{} {}", pageInx, page);
				long from = page.getFrom();
				long to = page.getTo();
				long step = page.getStep();
				encoding = page.getEncoding();
				if (encoding == null) {
					if (siteCharset != null) {
						encoding = siteCharset;
					} else {
						encoding = "utf-8";
					}
				}
				String pageHttpMethod = page.getMethod();
				Map<String, String> urlParam = page.getUrlParam();
				Set urlParamSet = urlParam.entrySet();

				// 기존의 마지막 수집 pk값.
				lastPkValue = CatbotSettings.getIncInfo(siteConfig.getSiteName(), categoryConfig.getCategoryName());
				logger.debug("lastPkValue >> {}", lastPkValue);

				// maxPK를 이전 pk로 셋팅해줌으로써 수집이 안되고 끝날때에도 이전값 보존.
				maxPK = lastPkValue;
				testCount = 0;

				// from, to가 같게 셋팅된경우처리.
				if (from == to) {
					to = from + step;
				}

				// inx != to는 from, to 가 역순일 경우 처리하기 위함.
				for (long inx = from; inx != to; inx += step) {
					//
					// URL 초기화
					//
					String url = page.getUrl();
					if (url == null || "".equals(url.trim()))
						break;

					if (url.contains("${i}")) {
						url = url.replaceAll("\\$\\{i\\}", "" + inx);
					}
					logger.info("Page url >> {}", url);

					//
					// 파라미터 초기화
					//
					List<NameValuePair> formParams = new ArrayList<NameValuePair>();
					Iterator urlParamIter = urlParamSet.iterator();
					while (urlParamIter.hasNext()) {
						Map.Entry<String, String> entry = (Map.Entry) urlParamIter.next();
						String key = entry.getKey();
						String val = entry.getValue();
						if (val.contains("${i}")) {
							val = val.replaceAll("\\$\\{i\\}", "" + inx);
						}
						logger.info("url-param >> {}={}", key, val);
						formParams.add(new BasicNameValuePair(key, val));
					}

					if (!doProcessUrl(url, pageHttpMethod, formParams)) {
						break MAIN_LOOP;
					}

				}// page for loop

			}// MAIN_LOOP
			 // 수집 게시물들 중에서 가장 큰 pk를 기록한다.
			if (maxPK != null && isPkLargerThan(maxPK, lastPkValue)) {
				// 디버그가 아닐때에만 pk를 기록한다.
				// 디버그일때 매번 info내용을 지워주는것이 번거롭기 때문에..
				if (!globalConfig.isDebug() && !isTest) {
					CatbotSettings.saveIncInfo(siteConfig.getSiteName(), categoryConfig.getCategoryName(), maxPK);
				}
			}
			logger.info("[{}] {} 수집이 끝났습니다. 문서수={}, maxPK={}, lastPkValue ={}", new Object[] { siteConfig.getSiteName(), categoryConfig.getCategoryName(), count, maxPK, lastPkValue });
			scrapingLogger.info("[{}][{}] Scraping Finish.. elapsed = {}, 문서수={}, maxPK={}, lastPkValue ={}", new Object[] { siteConfig.getSiteName(), categoryConfig.getCategoryName(),
			                Formatter.getFormatTime(System.currentTimeMillis() - st), count, maxPK, lastPkValue });
			return count;
		} catch (Exception e) {
			logger.error("수집시 에러발생!", e);
			scrapingLogger.info("[{}][{}] Error Finished! err={}", new Object[] { siteConfig.getSiteName(), categoryConfig.getCategoryName(), e });
			return -1;
		}
	}

	public int doProcess(String url) throws Exception {
		count = 0;
		testCount = 0;
		doProcessUrl(url, "GET", new ArrayList<NameValuePair>());
		return count;
	}

	public boolean doProcessUrl(String url, String pageHttpMethod, List<NameValuePair> formParams) throws UnsupportedEncodingException, InterruptedException {

		int startPos = url.indexOf("//") + 2;
		int pos = url.indexOf('/', startPos);
		int pos2 = url.lastIndexOf('/') + 1;

		domainUrl = url.substring(0, pos);
		pageRelativeUrl = url.substring(0, pos2);

		logger.debug("domainUrl = {}", domainUrl);
		logger.debug("pageRelativeUrl = {}", pageRelativeUrl);

		String paramStr = SiteScrapingService.convertParamToQueryString(formParams, encoding);
		if (formParams.size() > 0) {
			logger.debug("param request Url = {} param = {} ", url, paramStr);
			url = url + paramStr;
		}

		logger.debug("request Url = {} ", url);

		//
		// 페이지 소스를 읽어온다.
		//
		String pageContent = null;
		Document document = null;
		HttpResponse response = null;

		try {

			// JSOUP을 사용해서 파싱하게되면, <한글> 과 같은 unknown태그는 &lt;한글&gt;와
			// 같이 변환하게
			// 된다.
			//
			response = getHttpResponse(url, pageHttpMethod);

			if (response == null) {
				logger.error("웹페이지에 문제가 있어 수집을 종료합니다. status={}", url);
				return false;
			}

			logger.debug("Entry Status >> {}", response.getStatusLine());
			if (response.getStatusLine().getStatusCode() >= 400) {
				// 결과에 문제가 있음.
				logger.error("웹페이지에 문제가 있어 수집을 종료합니다. status={}", response.getStatusLine());
				scrapingLogger.info("[{}][{}] Error Http Response: status={} url={}", new Object[] { siteConfig.getSiteName(), categoryConfig.getCategoryName(),
				                response.getStatusLine().getStatusCode(), url });
				// break MAIN_LOOP;
				HttpEntity entity = response.getEntity();
				EntityUtils.consume(entity);
				entity.getContent().close();
				return false;
			}
			// InputStream is = new
			// BufferedHttpEntity(response.getEntity()).getContent();
			// document = Jsoup.parse(is, encoding, pageRelativeUrl,
			// Parser.xmlParser());
			// is.close();
			// pageContent = document.toString();
			pageContent = StreamToString(response.getEntity().getContent(), encoding);
			// pageContent = response.getEntity().
			// logger.debug("pageContent >> {}", pageContent);
		} catch (ClientProtocolException e) {
			logger.error(e.getMessage());
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		} finally {
			try {
				request.abort();
				if (response != null)
					response.getEntity().getContent().close();
			} catch (IOException E) {
				logger.error(E.getMessage());
			}
			// logger.trace("-----------------------------------------------------------------------------------------------------------------ref Count  Release: {}",
			// refCount.addAndGet(-1));
			response = null;
		}

		if (pageContent == null) {
			// 에러발생으로 페이지를 못읽어오면 그대로 종료.
			// break MAIN_LOOP;
			scrapingLogger.info("[{}][{}] Error 페이지읽기실패!: url={}", new Object[] { siteConfig.getSiteName(), categoryConfig.getCategoryName(), url });
			return false;
		}

		try {
			TagNode tagNode = htmlCleaner.clean(pageContent);
			pageContent = xmlSerializer.getXmlAsString(tagNode);
		} catch (IOException e) {
			logger.error("xml변환시 에러발생.{}\n{}", url, e);
			scrapingLogger.info("[{}][{}] Error XML변환에러: url={}", new Object[] { siteConfig.getSiteName(), categoryConfig.getCategoryName(), url });
		}
		//
		// xml을 정의된 process에 태워 필드데이터를 뽑아내어 저장한다.
		//

		List<Block> blockList = process.getBlockList();
		// 결과 맵에 결과 채워주기. loop를 돌린다...
		String rootPath = process.getRootPath();
		pkFieldId = process.getPkFieldId();
		isPkNumeric = process.isPkNumeric();
		try {
			List<String> nodes = XmlUtils.getNodes(rootPath, new ByteArrayInputStream(pageContent.getBytes()));
			logger.debug("{} >> 요소 {}개", rootPath, nodes.size());
			Iterator<String> it = nodes.iterator();

			long lap = System.currentTimeMillis();

			while (it.hasNext()) {
				String nodeContent = it.next();
				nodeContent = removeControllSpace(nodeContent);

				logger.trace("nodeContent = {}", nodeContent);

				HashMap<String, String> result = new HashMap<String, String>();
				result.put("rootUrl_", url);
				// 고정공지글이 아님으로 먼저 셋팅. makeFieldValue수행후 값이 변경됨.
				isFixedHeader = false;
				// logger.debug("{}에서 계산중 오류",
				// blockList.get(0).toString());
				BuildFieldState bfs = makeFieldValue(result, blockList, nodeContent, null);

				// 파싱이 제대로 안된경우이므로 모든 게시물을 확인해보게 된다.
				// 그러므로 여기서 바로 끝낸다.
				if (process.getHasDocument() == false && result.size() == 0) {
					logger.error("파싱에 문제가 있어 수집을 종료합니다. 수집된 필드데이터가 없습니다. >> {}", nodeContent);
					scrapingLogger.info("[{}][{}] Error 수집필드없음: url={}", new Object[] { siteConfig.getSiteName(), categoryConfig.getCategoryName(), url });
					// break MAIN_LOOP;
					return false;
				}
				if (bfs == BuildFieldState.SKIP)
					continue;				

				prevResult = result;

				String thisPkValue = result.get(pkFieldId);
				logger.debug("thisPkValue = {}", thisPkValue);
				
				if ( bfs == BuildFieldState.COMPLETE )				
				{
				if (!checkContinueToScraping(thisPkValue)) {
					// 더이상 최신게시물이 없어서 끝날수도 있지만,
					// 이전 기록이 없으면서 initialSize가 0이기
					// 때문에 끝나는 경우는, 현재 pk를
					// 기록해주어야 한다.
					if (isMaxPK(thisPkValue)) {
						maxPK = thisPkValue;
					}
				}
				return false;
				}
				
				// false리턴시 다음문서로..
				// true리턴시 데이터를 기록하기 위해 아래로 진행.
				if (bfs == BuildFieldState.FAILED) {					
					// 수행실패시 다음으로 스킵한다.
					logger.debug("makeFieldValue 수행실패. continue.");
					continue;
				}

				if (bfs == BuildFieldState.DUPLICATE) {

					logger.debug("증분 색인 작업 도중 중복 항목 발견 종료");
					if (!checkContinueToScraping(thisPkValue)) {
						// 더이상 최신게시물이 없어서 끝날수도 있지만,
						// 이전 기록이 없으면서 initialSize가 0이기
						// 때문에 끝나는 경우는, 현재 pk를
						// 기록해주어야 한다.
						if (isMaxPK(thisPkValue)) {
							maxPK = thisPkValue;
						}
					}
					return false;
				}

				if (process.getHasDocument() == false) {
					PostProcessResult bResult = postProcess(thisPkValue, result);
					if ((bResult == PostProcessResult.FAILED) || (bResult == PostProcessResult.REACH_MAX) || (bResult == PostProcessResult.NO_MORE_CONTENT))
						return false;
				}
			}

		} catch (JDOMException e) {
			logger.error("", e);
		} catch (IOException e) {
			logger.error("", e);
		}

		return true;
	}

	private PostProcessResult postProcess(String thisPkValue, HashMap<String, String> result) {

		if (!checkContinueToScraping(thisPkValue)) {
			// 더이상 최신게시물이 없어서 끝날수도 있지만,
			// 이전 기록이 없으면서 initialSize가 0이기 때문에 끝나는 경우는, 현재 pk를
			// 기록해주어야 한다.
			if (isMaxPK(thisPkValue)) {
				maxPK = thisPkValue;
			}
			// break MAIN_LOOP;
			logger.error("더이상 최신 내용이 없어서 종료", globalConfig.getInitialSize(), count);
			return PostProcessResult.NO_MORE_CONTENT;
		}

		if (scrapingDAO.write(result)) {
			// logger.debug("after write");
			if (isTest) {
				testCount++;
				if (testCount >= MAX_TEST_COUNT) {
					count = testCount;
					return PostProcessResult.REACH_MAX;
				} else
					return PostProcessResult.SUCCESS;
			}

			count++;
			logger.debug("before scrapingPkList.add(thisPkValue); ");
			scrapingPkList.add(thisPkValue);
			logger.debug("after ");
			logger.trace("게시물 기록 PK={}, cnt={}", thisPkValue, count);
			// 기록에 성공했으면 maxPK 후보가 된다.
			if (isMaxPK(thisPkValue)) {
				maxPK = thisPkValue;
			}
			if (count % 20 == 0) {
				scrapingLogger.info("[{}][{}] Scraping count = {}..",
				                new Object[] { siteConfig.getSiteName(), categoryConfig.getCategoryName(), count, Formatter.getFormatTime(System.currentTimeMillis() - lap) });
				return PostProcessResult.SUCCESS;
			}
			lap = System.currentTimeMillis();
		} else {
			logger.debug("after write");
			// 기록 에러가 발생하면 수집프로세스를 종료한다.
			logger.error("ScrapingDao기록에 실패하여, 수집을 종료합니다.");
			scrapingLogger.info("[{}][{}] Error DB기록실패: thisPkValue={}", new Object[] { siteConfig.getSiteName(), categoryConfig.getCategoryName(), thisPkValue });
			// break MAIN_LOOP;
			return PostProcessResult.FAILED;
		}

		if (lastPkValue == null || lastPkValue.trim().length() == 0) {
			if (globalConfig.getInitialSize() <= count) {
				logger.error("{} <= {}", globalConfig.getInitialSize(), count);
				// 갯수를 채우면 끝낸다.
				// break MAIN_LOOP;
				return PostProcessResult.REACH_MAX;
			}
		}

		return PostProcessResult.SUCCESS;
	}

	private void delay() throws InterruptedException {
		int delayTime = scrapingIntervalDelayMaxTime;
		if (!isFixed) {
			delayTime = random.nextInt(scrapingIntervalDelayMaxTime);
		}
		logger.info("[{}][{}] Waiting {}ms.. cnt={}", new Object[] { siteConfig.getSiteName(), categoryConfig.getCategoryName(), delayTime, count });
		Thread.sleep(delayTime);
	}

	private boolean checkContinueToScraping(String myPkValue) {
		// 테스트이면 무조건진행.
		if (isTest)
			return true;

		if (lastPkValue == null || lastPkValue.trim().length() == 0) {
			//
			// 이전 기록이 없으면 최신데이터들을 initialSize만큼만 기록하고 끝냄.
			//
			// initialSize가 0이면 바로끝냄.
			if (globalConfig.getInitialSize() == 0) {
				return false;
			} else {
				// 계속 진행!
				return true;
			}
		} else {
			// 기존의 pk값보다 큰 값이 들어오면 기록하고 작거나 같으면 수집을 종료한다.
			// 정수일 경우에는 long으로 변환하여 비교하고 문자타입일 경우에는 스트링 비교를 수행한다.
			logger.trace("myPkValue={} : lastPkValue={}, maxPkValue={}", new Object[]{myPkValue, lastPkValue, maxPK});
			if (isPkLargerThan(myPkValue, lastPkValue)) {
				// 이전 pk보다 크므로 새로운 게시물이다!!.
				// 이번에 수집된 게시물들중에 중복된 것이 있는지 확인.
				if (scrapingPkList.contains(myPkValue)) {
					logger.debug("중복된PK의 게시물이 발견되었습니다.pk={}", myPkValue);
					return false;
				} else {
					// 최신 게시물이면서 중복이 아닌 게시물일경우.
					return true;
				}
			} else {
				return false;
			}
		}

	}

	private BuildFieldState makeFieldValue(HashMap<String, String> result, List<Block> blockList, String parentSource, String parentSequence) {
		Iterator<Block> it = blockList.iterator();

		while (it.hasNext()) {

			Block block = it.next();
			// field
			// &nbsp;는 일반 space와 다른 문자로, trim으로 제거가 되지 않으므로, 공백으로
			// 치환을 해준다.
			String value = parentSource;
			String id = block.getId();
			String xpath = block.getXpath();
			String selector = block.getSelector();
			String linkMethod = block.getLinkMethod();
			String modify = block.getModify();
			String prefix = block.getPrefix();
			String suffix = block.getSuffix();
			String regexp = block.getRegexp();
			String removeAll = block.getRemoveAll();
			String validation = block.getValidation();
			String headerValidation = block.getHeaderValidation();
			boolean isFinished = false; // 처리할 필요없이 끝내는지 여부.

			// 부모필드가 다중값이면 현재필드도 여러개이므로 이름에 번호를 붙여준다.
			if (parentSequence != null) {
				if (id != null && id.endsWith("_") == false)
					id = id + "." + parentSequence;
			}

			if (block.getDependOn() != null) {
				String[] fields = block.getDependOn().split(",");
				for (String dependField : fields) {
					//
					// dependOn은 같은 레벨의 필드만 참조할 수 있다. 필드참조도
					// 마찬가지.
					if (parentSequence != null) {
						dependField = dependField + "." + parentSequence;
					}
					String v = result.get(dependField.trim());
					if (v == null || v.length() == 0) {
						// 의존필드 값이 없으면 끝낸다.
						logger.debug("{} 의존필드 값이 없습니다. 의존필드={}", id, dependField);
						isFinished = true;
						value = "";
						break;
					}
				}
			}

			String[] multiValues = null;

			if (!isFinished) {

				// linkPage 적용 field 수집해서 기록.
				// field일때 linkPage가 true이면 들어가서 내용 수집.
				if (block.isLinkPage()) {

					if (fileGoneList != null) {
						boolean bExists = fileGoneList.isExists(value.trim(), encoding);

						if (categoryConfig.getScrapAction().equals(categoryConfig.SCRAPGETNEW)) {
							// 수집액션이 신규 수집일 경우
							if (bExists == true) {
								// 기존의 수집 리스트에
								// 포함되어 있다면
								// 카태고리 설정에서
								// 중지이면 중지 시킨다.
								if (categoryConfig.BREAK_WHEN_DUPLICATION.equals(categoryConfig.getDuplicationAction()))
									return BuildFieldState.DUPLICATE;
								// 카태고리 설정에서
								// 스킵이면 현재 문서를
								// 스킵하고 다음으로
								// 넘긴다.
								if (categoryConfig.SKIP_WHEN_DUPLICATION.equals(categoryConfig.getDuplicationAction()))
									return BuildFieldState.SKIP;
								// 혹시 그이외 문제가 생길
								// 경우 중지 시킨다
								return BuildFieldState.DUPLICATE;
							}
						}

						if (categoryConfig.getScrapAction().equals(categoryConfig.SCRAPGETALL)) {
							if (bExists == true) {
								// 기존의 수집 리스트에
								// 포함되어 있다면
								// 카태고리 설정에서
								// 중지이면 중지 시킨다.
								if (categoryConfig.BREAK_WHEN_DUPLICATION.equals(categoryConfig.getDuplicationAction()))
									return BuildFieldState.DUPLICATE;
								// 카태고리 설정에서
								// 스킵이면 현재 문서를
								// 스킵하고 다음으로
								// 넘긴다.
								if (categoryConfig.SKIP_WHEN_DUPLICATION.equals(categoryConfig.getDuplicationAction()))
									return BuildFieldState.SKIP;
								// 혹시 그이외 문제가 생길
								// 경우 중지 시킨다
								return BuildFieldState.DUPLICATE;
							}
						}
					}

					value = getLinkPageContent(value, encoding, linkMethod);
				}

				// xpath적용
				if (xpath != null) {
					try {
						// 값이 없을 경우 하위요소의 값도 빈값이다.
						if (value.length() > 0) {
							if (block.isMultiNode()) {
								multiValues = XmlUtils.getMultiNodeText(xpath, new ByteArrayInputStream(value.getBytes()));
								if (multiValues.length == 0) {
									value = XmlUtils.getSingleNodeText(xpath, new ByteArrayInputStream(value.getBytes()));
								}
								value = ""; // value는
								            // 더이상
								            // 사용하지
								            // 않음을
								            // 명시함.
							} else {
								// block 과
								// field는 구분없이
								// 오직 하나의 요소만
								// 선택해야 한다.
								value = XmlUtils.getSingleNodeText(xpath, new ByteArrayInputStream(value.getBytes()));
							}
						}
						// if(block.isField()){
						// logger.debug("field {} >> {}",
						// block.getId(), value);
						// }
					} catch (JDOMException e) {
						logger.info("id = {}", id);
						logger.error("soure >> {}", (multiValues != null ? multiValues[0] : value));
						logger.error("", e);
					} catch (IOException e) {
						logger.error("", e);
					}
				}

				// selector적용 jsoup 전용
				if (selector != null) {
					// 값이 없을 경우 하위요소의 값도 빈값이다.
					if (value.length() > 0) {
						// block 과 field는 구분없이 오직 하나의
						// 요소만 선택해야 한다.
						Document document = Jsoup.parse(value, encoding, Parser.xmlParser());
						Elements elements = document.select(selector);
						value = elements.toString();
					}
				}
			}

			if (multiValues == null) {
				multiValues = new String[] { value };
			}

			if (block.isMultiNode()) {
				logger.debug("다중값 노드id={} 사이즈 >> {}", block.getId(), multiValues.length);
			}

			//
			// 다중값은 여러번 수행되고, 단일값은 한번만 수행된다.
			//

			for (int sequence = 1; sequence <= multiValues.length; sequence++) {
				// value 재정의됨.
				value = multiValues[sequence - 1];

				String thisSequence = parentSequence;
				if (block.isMultiNode()) {
					// logger.trace("seq-{} >> {}",
					// sequence, value);

					if (parentSequence != null) {
						thisSequence = parentSequence + "." + sequence;
					} else {
						thisSequence = sequence + "";
					}
					// 멀티노드 아이디 재정의
					if (id != null && id.endsWith("_") == false)
						id = block.getId() + "." + thisSequence;
				}

				if (!isFinished) {
					// xpath 결과의 앞뒤 공백제거
					value = value.trim();

					if (block.tagRemove() || block.tagRemove2()) {
						value = TextExtract.clean(value, block.tagRemove2(), block.isMultiLines(), block.getTagWhiteList());
						value = StringEscapeUtils.unescapeHtml4(value);
					}

					//
					// 중요! parseUrl, parseJavascript시 추출된
					// 내용이 없다는것은 적용할 파라미터가
					// 없다는 것이므로 block.getValue()값에 파라미터를
					// 셋팅하지 못한다.
					// 그러므로 이 경우에는 getValue()값을 사용하지 않고,
					// 넘어가도록 한다.
					//
					boolean skipValue = false;
					if ((block.parseJavascript() || block.parseUrl()) && value.length() == 0) {
						skipValue = true;
					}

					// src와 href 를 찾아서 경로를 절대경로로 변환해준다.
					if (block.isResourcePathRewrite() && value != null && value.length() > 0) {
						Matcher matcher = resourcePathPattern.matcher(value);
						StringBuffer newPath = new StringBuffer();
						while (matcher.find()) {
							String url = matcher.group(1);
							if (url != null) {
								if (url.startsWith("http") || url.startsWith("HTTP")) {
									// 이미
									// 절대경로이므로
									// OK!
								} else {
									if (url.startsWith("/")) {
										// 도메인
										// 최상위
										// 경로
										matcher.appendReplacement(newPath, "src=\"" + domainUrl + url + "\"");
									} else {
										// 현재페이지의
										// 상대경로
										matcher.appendReplacement(newPath, "src=\"" + pageRelativeUrl + url + "\"");
									}
								}
							}
						}
						matcher.appendTail(newPath);
						value = newPath.toString();
					}

					// 정규식으로 불필요한 내용 지워주기.
					if (removeAll != null) {
						// logger.debug("removeAll \"{}\", {}",
						// value,removeAll);
						value = value.replaceAll(removeAll, "");
						// logger.debug("removeAll result = '{}'",value);
					}

					if (block.getReplaceAll() != null) {
						logger.debug("replaceAll old = '{}'", value);
						String fromPattern = block.getReplaceAll();
						String toPattern = block.getReplaceTo();
						if (toPattern == null) {
							toPattern = "";
						}

						value = value.replaceAll(fromPattern, toPattern);
						logger.debug("replaceAll result = '{}'", value);
					}

					//
					// 정규식적용시 그룹 찾기.
					//
					if (regexp != null) {
						// logger.debug("regexp = {}",regexp);
						// logger.debug("value = {}",
						// value);
						Pattern p = Pattern.compile(regexp);
						Matcher matcher = p.matcher(value);
						// logger.debug("value1 = {}, matcher.groupCount() ={}",
						// value, matcher.groupCount()
						// );
						if (matcher.find() && matcher.groupCount() > 0) {
							value = matcher.group(1);
						}
						// logger.debug("value2 = {}",value);
					}

					// logger.debug("js url = {}", value);
					String[] args = null;
					// 일반적인 링크가 아닌 javascript와 같은 경우는 별도로
					// 처리한다.
					if (block.parseJavascript() && value.length() > 0) {
						// logger.debug("parseJavascript value >> '{}'",value);
						int s = value.indexOf("(");
						int e = value.lastIndexOf(")");
						if (s != -1 && e != -1) {
							String tmp = value.substring(s + 1, e).trim();

							args = tmp.split(",");

							for (int i = 0; i < args.length; i++) {
								args[i] = args[i].replaceAll("'", "").replaceAll("\"", "").trim();
							}
						}
					}
					Map<String, String> params = null;

					if (block.parseUrl()) {
						params = getQueryParams(value, encoding);
					}

					String userValue = block.getValue();
					if (userValue != null && !skipValue) {

						if (args != null) {
							for (int i = 0; i < args.length; i++) {
								String argKey = "\\$\\{" + i + "\\}";
								String argValue = args[i];
								if (argValue != null) {
									userValue = userValue.replaceAll(argKey, argValue);
								}
							}
						}
						if (params != null) {
							Iterator<Entry<String, String>> iter = params.entrySet().iterator();
							while (iter.hasNext()) {
								Entry<String, String> entry = iter.next();
								String argKey = "\\$\\{" + entry.getKey() + "\\}";
								String argValue = entry.getValue();
								if (argValue != null) {
									userValue = userValue.replaceAll(argKey, argValue);
									// logger.debug("PARAM REPLACE {}, {}",argKey,
									// argValue);
								}
							}
						}

						userValue = replaceValues(userValue, result, thisSequence);
						// 값이 없어서 치환되지 않고 남은변수는 빈값으로
						// 만들어준다.
						// userValue =
						// userValue.replaceAll("\\$\\{.*?\\}",
						// "");

						value = userValue;
					}

					// prefix, suffix 적용
					// 파싱한 값이 존재해야만 prefix, suffix를 수행한다.
					if (prefix != null && value.length() > 0) {
						ScrapModifier modifier = ScrapModifier.get(PrefixModifier.key);
						modifier.init(prefix);
						value = modifier.modify(id, value);
					}

					if (suffix != null && value.length() > 0) {
						ScrapModifier modifier = ScrapModifier.get(SuffixModifier.key);
						modifier.init(suffix);
						value = modifier.modify(id, value);
					}

					// modifier적용
					if (modify != null) {
						ScrapModifier modifier = ScrapModifier.get(modify);
						if (modifier != null)
							value = modifier.modify(id, value);
						else
							logger.error("모디파이어가 없습니다. modifier = " + modify);
					}
					// modify 끝!!

					// 중복 타이틀이면 없앤다.
					if (id != null && id.equals("title")) {
						// logger.warn("중복된 타이틀 체크! >> {}",
						// value);
						if (isDupTitle(value)) {
							// 중복된 타이틀이 수집되었다면 다음으로
							// 넘어간다.
							logger.warn("중복된 타이틀이라서 무시합니다. >> {}", value);
							return BuildFieldState.SKIP;
						}
					}

					// pk이면서 값이 없으면 에러이다!!
					if (block.isPk()) {
						if (value == null || value.trim().length() == 0) {
							logger.error("###################################");
							logger.error("## 치명적에러! : PK값을 찾을수 없습니다!! ");
							logger.error("###################################");
						}
					}

					if (block.isConvertGMT2Local() == true) {
						SimpleDateFormat inputFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss 'GMT'", Locale.US);
						inputFormat.setTimeZone(TimeZone.getTimeZone("Etc/UTC"));
						Date date = null;
						try {
							date = inputFormat.parse(value);
						} catch (Exception E) {
							E.printStackTrace();
						}
						value = defaultDatetimeFormatter.format(date);
					}
					// logger.debug("id={}, block.getDatetimeFormatParser()={}",
					// id, block.getDatetimeFormatParser());
					if (block.getDatetimeParseFormat() != null || block.getDatetimeFormat() != null) {
						try {
							Date date = null;
							if (block.getDatetimeFormatParser() != null) {
								date = block.getDatetimeFormatParser().parse(value);
							} else {
								long timestamp = 0;
								try {
									timestamp = Long.parseLong(value);
								} catch (NumberFormatException e) {
									logger.error("시간타입을 Timestamp 로 파싱중 에러발생. value={}", value, e);
								}
								date = new Date(timestamp);
							}

							if (block.getDatetimeFormatter() != null) {
								value = block.getDatetimeFormatter().format(date);
								// logger.trace("datetime = {}",
								// value);
							} else {
								value = defaultDatetimeFormatter.format(date);
								// logger.trace("datetime = {}",
								// value);
							}
						} catch (ParseException e) {
							// e.printStackTrace();
							logger.error("날짜시간 필드 파싱중 에러발생. format={}, data={}", block.getDatetimeParseFormat(), value);

							// 이전 데이터를 넣어준다.
							if (block.isSetDataWithPrevValueWhenNull()) {
								String prev = prevResult.get(id);
								logger.debug(">>>>prev= {}", prev);
								if (prev == null) {
									if (block.isSetCurrentTimeWhenNULL()) {
										if (block.getDatetimeFormatter() != null) {
											value = block.getDatetimeFormatter().format(value);
										} else {
											value = defaultDatetimeFormatter.format(new Date());
										}
										logger.debug("222>>>>value= {}", value);
									}
								} else {
									value = prev;
								}
							}

							if (value == null && block.isSetCurrentTimeWhenNULL()) {
								if (block.getDatetimeFormatter() != null) {
									value = block.getDatetimeFormatter().format(value);
								} else {
									value = defaultDatetimeFormatter.format(new Date());
								}
							}
						}
					}

					// 파일다운로드
					if (block.isFile() && value != null && value.length() > 0) {
						String fileName = block.getFileName();
						// fileName 에서 값을 치환한다.
						if (fileName != null) {
							fileName = replaceValues(fileName, result, thisSequence);
						}

						// 디렉토리일수도 있고 파일명일수도 있다.
						String copyToPath = block.getCopyTo();
						if (copyToPath == null) {
							copyToPath = System.getProperty("java.io.tmpdir");
						} else {
							// copyToPath에서 ${}값을
							// 치환한다.
							copyToPath = replaceValues(copyToPath, result, thisSequence);
						}

						String localFilePath = null;
						if (fileName != null) {
							localFilePath = copyToPath + CatbotSettings.FILE_SEPARATOR + fileName;
						} else {
							// fileName이 없으면
							// copyToPath를 파일명이 포함된
							// 경로로 간주한다.
							localFilePath = copyToPath;
						}

						logger.debug("다운로드 파일 \"{}\" >> {}", value, localFilePath);

						long downloadFileSize = 0;

						String fileNameEncoding = block.getfileNameEncoding();
						if (fileNameEncoding != null && !"".equals(fileNameEncoding)) {
							try {
								localFilePath = new String(localFilePath.getBytes("UTF-8"), fileNameEncoding);
							} catch (Exception e) {
								logger.error(e.getMessage());
							}
						}

						downloadFileSize = downloadFile(localFilePath, value, linkMethod);

						String fileSizeName = null;
						if (thisSequence != null) {
							fileSizeName = block.getId() + ".fileSize." + thisSequence;
						} else {
							fileSizeName = block.getId() + ".fileSize";
						}

						if (downloadFileSize < 0) {
							logger.error("파일다운로드에 실패했습니다. \"{}\"", value);
							result.put(fileSizeName, "0");
							logger.trace("Put {} >> \"0\"", fileSizeName);
						} else {
							logger.debug("Add File Info >> {} : {}", id + ".fileSize", downloadFileSize + "");
							result.put(fileSizeName, downloadFileSize + "");
							logger.trace("Put {} >> \"{}\"", fileSizeName, downloadFileSize);
						}
					}

					//
					// validation 체크를 수행하고 통과못하면 바로 리턴한다.
					//
					if (validation != null) {
						if (!isValid(validation, value, block.getArgs(), block.isNumeric())) {
							logger.trace("검증실패! validation = {}, value={}", validation, value);
							return BuildFieldState.FAILED;
						}
					}

					if (headerValidation != null) {
						if (!isValid(headerValidation, value, block.getArgs(), block.isNumeric())) {
							logger.trace("고정공지 확인! headerValidation = {}, value={}", headerValidation, value);
							isFixedHeader = true;

							if (lastPkValue == null) {
								// 첫 증분수집에서는
								// 고정공지글을 수집하지
								// 않는다. 고정공지에는
								// 오래된 글이 많아서
								// 이것을 pk로 잡을 경우
								// 문제발생.
								// 고정공지 최신글은 다음
								// 증분색인시 수집되므로
								// 문제없음.
								logger.trace("첫 증분색인시 고정공지는 무시!");
								return BuildFieldState.FAILED;
							}
						}
					}

					// 값이 없을 때
					// isSetDataWithPrevValueWhenNull 가
					// 셋팅되어있으면 이전값을 사용.
					if (id != null && (value == null || value.length() == 0) && block.isSetDataWithPrevValueWhenNull()) {
						if (prevResult != null) {
							String prev = prevResult.get(id);
							if (prev != null) {
								value = prev;
							}
						}

					}

				}// if(!isFinished){

				// 기록할 결과필드 데이터
				if (block.isField()) {

					if (block.isJsessionRemove() && value.contains(";jsessionid=")) {
						String pre = value.substring(0, value.indexOf(";jsessionid="));
						logger.debug("pre {} ", pre);
						String temp = value.substring(value.indexOf(";jsessionid="));
						logger.debug("temp {} ", temp);
						int delimeterPos = temp.indexOf("?");
						if (delimeterPos == -1)
							delimeterPos = temp.indexOf("/");
						if (delimeterPos > 0) {
							temp = temp.substring(delimeterPos);
							logger.debug("temp {} ", temp);
							value = pre + temp;
							logger.debug("value {} ", value);
						} else
							logger.error("jsession 종료 문자 식별 오류. {}", temp);
					}

					result.put(id, value.trim());
					// logger.trace ("Put     {} >> \"{}\"",
					// id, value.trim());
					// logger.debug("block {} >> {}",
					// id,value.trim());
				}

				if (isFixedHeader)
					logger.trace("isFixedHeader = {}", isFixedHeader);

				if (!isFinished) {
					//
					// pk값 확인후 진행여부 판단.
					// pk값을 확인하면서 수집하지 않으면, 수집이 필요없는 데이터 임에도
					// 불구하고 이후의 불필요한 필드들을
					// 처리하므로 필요하지 않는 추가 비용이 발생한다.
					// 특히 다운로드 파일이 진행될 경우, 트래픽문제와 로컬에 파일이
					// 늘어나는 문제가 발생하므로, early termination을
					// 적용한다.
					//
					if (block.isPk()) {
						if (!checkContinueToScraping(value)) {
							if (isFixedHeader) {
								// 메인 리스트에 신규건이
								// 존재할수 있으므로 다음
								// 게시물로 진행.
								return BuildFieldState.FAILED;
							} else {
								// 메인 리스트에서 old
								// 게시물이 발견되었으므로
								// 더 이상 다른 필드값을
								// 채우지 않고 리턴한다.
								logger.debug("더이상 최신데이터가 없어서 수집을 미리종료! this pk={}", value);
								// pk가 담겨있는 리스트를
								// 넘겨주어 정상적으로
								// 종료하도록 유도한다.
								// 그러므로 true를
								// 리턴.
								return BuildFieldState.SUCCESS;
							}
						}
					}
				}

				// children을 파싱한다.
				if (block.getChildren() != null) {
					// 하위객체에서 validation실패시 false를 즉시 리턴하고
					// 종료한다.
					// 시퀀스번호를 넘겨주어, 하위필드이름에도 시퀀스를 붙일수 있도록
					// 한다.
					BuildFieldState bfs = makeFieldValue(result, block.getChildren(), value, thisSequence);
					if (bfs != BuildFieldState.SUCCESS )
						return bfs;
				}
			}// for (int sequence = 0; sequence <
			 // multiValues.length;
			 // sequence++) {
			if (id != null && id.startsWith("document") == true) {
				String idSuffix = id.substring(id.indexOf("document") + "document".length());
				String thispkValue = result.get(pkFieldId + idSuffix);
				HashMap<String, String> storeValue = new HashMap<String, String>();
				List<String> eraseKeys = new LinkedList<String>();

				Iterator<String> keyItr = result.keySet().iterator();
				while (keyItr.hasNext()) {
					String sKey = keyItr.next();
					String sValue = (String) result.get(sKey);
					String temp = "";

					if (sKey.endsWith(idSuffix) == true) {
						eraseKeys.add(sKey);
						temp = sKey.substring(0, sKey.indexOf(idSuffix));
						// field 값 식별
						// field내에서 MultiNode일 경우 가져오기
						// 위한 값
						temp = temp + sKey.substring(sKey.indexOf(idSuffix) + idSuffix.length());
						sKey = temp;
						storeValue.put(sKey, sValue);
					}

					if (sKey.endsWith("_") == true) {
						storeValue.put(sKey, sValue);
					}
				}

				Iterator<String> eItr = eraseKeys.iterator();
				while (eItr.hasNext()) {
					result.put((String) eItr.next(), "");
				}

				PostProcessResult bResult = postProcess(thispkValue, storeValue);
				switch (bResult) {
				case FAILED:
					return BuildFieldState.FAILED;

				case NO_MORE_CONTENT: {
					logger.error("there is no more content");
					return BuildFieldState.COMPLETE;
				}
				case REACH_MAX: {
					if (isTest)
						logger.error("scraping count reach to max [{}/{}]", testCount, MAX_TEST_COUNT);
					else
						logger.error("scraping count reach to max [{}/{}]", count, globalConfig.getInitialSize());
					return BuildFieldState.COMPLETE;
				}

				}
			}
		}// while (it.hasNext()) {

		return BuildFieldState.SUCCESS;
	}

	// 현재 수집에서 가장큰 pk를 확인하는 메소드. 고정공지로 인해 pk 정렬순서가 항상 내림차순이 아니기 때문에 필요하게 됨.
	private boolean isMaxPK(String pk1) {
		logger.trace("thisPk={} : maxPk={} : {}", new Object[] { pk1, maxPK, isPkLargerThan(pk1, maxPK) });
		return isPkLargerThan(pk1, maxPK);
	}

	private boolean isPkLargerThan(String pk1, String pk2) {
		if (pk2 == null) {
			return true;
		} else {
			// 비교하여 큰거 결정.
			if (isPkNumeric) {
				try {
					return Long.parseLong(pk1) > Long.parseLong(pk2);
				} catch (NumberFormatException e) {
					// 무시하고 스트링 비교로 넘어간다.
				}
			}

			return pk1.compareTo(pk2) > 0;
		}
	}

	private String replaceValues(String str, HashMap<String, String> data, String sequence) {
		// group 1은 카테고리
		// group 2는 변수명

		// logger.debug("str = >> {} sequence = >> {} ", str, sequence);
		Matcher matcher = variablePattern.matcher(str);
		StringBuffer result = new StringBuffer();

		while (matcher.find()) {
			String string = matcher.group();
			String category = matcher.group(1);
			String property = matcher.group(2);
			String value = "";

			// 향후에 변수가 많아지면
			// reflection 을 사용해서 getXXX 함수를 호출하게 하는건 어떨까??
			if (category.equals("sys")) {
				if (property.equals("now")) {
					value = System.currentTimeMillis() + "";
				}
			} else if (category.equals("site")) {
				if (property.equals("name")) {
					value = siteConfig.getSiteName();
				} else if (property.equals("description")) {
					value = siteConfig.getDescription();
				}
			} else if (category.equals("category")) {
				if (property.equals("name")) {
					value = categoryConfig.getCategoryName();
				} else if (property.equals("description")) {
					value = categoryConfig.getDescription();
				}
			} else if (category.equals("field")) {
				if (property.endsWith("_") == false) {
					// property가 _로 끝나면 시퀀스를 포함하지 않는다.
					if (sequence != null) {
						property = property + "." + sequence;
					}
					value = data.get(property);
				} else {
					value = data.get(property);
				}
			} else if (category.equals("var")) {
				Map<String, String> siteProps = siteConfig.getProperties();
				value = siteProps.get(property);
				if (value == null) {
					Map<String, String> globalProps = globalConfig.getProperties();
					value = globalProps.get(property);
				}
			}

			if (value == null) {
				value = "";
			}
			if (value.length() == 0) {
				logger.error("알수없는 변수값입니다. {}", string);
			}

			// 해당 키값을 못찾으면 공백으로 치환된다.
			matcher.appendReplacement(result, value);

		}
		matcher.appendTail(result);

		return result.toString();
	}

	// validation 체크 함수.
	private boolean isValid(String validation, String value, String args, boolean isNumeric) {
		if (validation.equals("isNumber")) {
			try {
				Long.parseLong(value);
				return true;
			} catch (NumberFormatException e) {
				return false;
			}
		} else if (validation.equals("isEmpty")) {
			if (value == null || value.trim().length() == 0)
				return true;
			else
				return false;
		} else if (validation.equals("isNotEmpty")) {
			if (value == null || value.trim().length() == 0)
				return false;
			else
				return true;
		} else if (validation.equals("eq")) {
			return value.equals(args);
		} else if (validation.equals("not")) {
			logger.debug("val={}, args={}", value, args);
			logger.debug("!value.equals(args)={}", !value.equals(args));
			return !value.equals(args);
		}
		return false;
	}

	public Map<String, String> getQueryParams(String url, String encoding) {
		try {
			Map<String, String> params = new HashMap<String, String>();
			String[] urlParts = url.split("\\?");
			if (urlParts.length > 1) {
				String query = urlParts[1];
				for (String param : query.split("&")) {
					int pos = param.indexOf("=");
					if (pos > 0) {
						String key = URLDecoder.decode(param.substring(0, pos), encoding);
						String value = URLDecoder.decode(param.substring(pos + 1), encoding);
						params.put(key, value);
						// logger.info("key = >> {}",key);
						// logger.info("value = >> {}",value);
					}
				}
			}

			return params;
		} catch (UnsupportedEncodingException ex) {
			throw new AssertionError(ex);
		}
	}

	private String getLinkPageContent(String strUrl, String encoding, String method) {
		logger.debug("Link Page url={}, encoding={}, method={}", new Object[] { strUrl, encoding, method });

		if (fileGoneList != null)
			fileGoneList.appendUrl(strUrl, encoding);
		else {
			if (isTest == false)
				logger.debug("GoneList 객체 미생성 오류");
		}

		InputStream inputStream = fileCacheRepository.getInputStream(strUrl);
		String result = "";
		if (inputStream == null) {
			cacheUsed = false;
			HttpResponse response = getHttpResponse(strUrl, method);

			if (response == null) {
				logger.error("수집실패. {} >> {}", pkFieldId, strUrl);
				scrapingLogger.info("[{}][{}] Error Timeout: pkFieldId={}, url={}", new Object[] { siteConfig.getSiteName(), categoryConfig.getCategoryName(), pkFieldId, strUrl });
				// request.abort();
				return "";
			}

			logger.debug("Link Status >> {}", response.getStatusLine());
			if (response.getStatusLine().getStatusCode() >= 400) {
				// 문제발생.
				logger.error("링크페이지에서 에러가 발생하였습니다. status={}", response.getStatusLine());
				scrapingLogger.info("[{}][{}] Error Http Response: Http Status={}, pkFieldId={}, url={}", new Object[] { siteConfig.getSiteName(), categoryConfig.getCategoryName(),
				                response.getStatusLine().getStatusCode(), pkFieldId, strUrl });
				try {
					EntityUtils.consume(response.getEntity());
					response.getEntity().getContent().close();
				} catch (Exception e) {
					request.abort();
					e.printStackTrace();
				}
				return "";
			}
			logger.error("--------------------------------------------------------");
			try {
				InputStream is = new BufferedHttpEntity(response.getEntity()).getContent();
				fileCacheRepository.put(strUrl, is);
				is.close();
			} catch (IOException e) {
				logger.error("", e);
			} finally {
				request.abort();
			}
			InputStream is = null;
			try {
				is = fileCacheRepository.getInputStream(strUrl);
				result = StreamToString(is, encoding);
			} finally {
				if (is != null) {
					try {
						is.close();
					} catch (IOException e) {
					}
				}
			}
		} else {
			result = StreamToString(inputStream, encoding);
			try {
				inputStream.close();
			} catch (IOException e) {
			}
		}

		// xml로 정리.
		try {
			TagNode tagNode = htmlCleaner.clean(result);
			result = xmlSerializer.getXmlAsString(tagNode);

			Document document = Jsoup.parse(result, pageRelativeUrl, Parser.xmlParser());
			result = document.toString();

			tagNode = htmlCleaner.clean(result);
			result = xmlSerializer.getXmlAsString(tagNode);

		} catch (IOException e) {
			logger.error("", e);
		}
		return removeControllSpace(result);
	}

	private String removeControllSpace(String str) {
		if (str == null)
			return str;

		return str.replaceAll("&nbsp;", " ").replace((char) 160, ' ').trim();
	}

	private long downloadFile(String localFilePath, String strUrl, String method) {
		logger.trace("downloadFile enter");
		File localFile = new File(localFilePath);
		// 디렉토리 생성
		if (localFile.getParentFile() != null)
			localFile.getParentFile().mkdirs();

		if (localFile.isDirectory()) {
			logger.error("{}는 디렉토리 이므로 파일로 기록할수 없습니다.", localFilePath);
			return -1;
		}
		logger.trace("download url={}, method={}", strUrl, method);

		File cacheFile = fileCacheRepository.getFile(strUrl);
		if (cacheFile == null) {
			HttpResponse response = null;
			try {
				response = getHttpResponse(strUrl, method);
			} catch (Exception e) {
				logger.error("파일다운로드경로 에러.", e);
			}
			if (response == null) {
				logger.error("파일다운로드 에러.");
				return -1;
			}

			logger.debug("Download Status >> {}", response.getStatusLine());

			HttpEntity entity = null;
			try {
				entity = response.getEntity();
				if (entity != null && response.getStatusLine().getStatusCode() == 200) {
					FileOutputStream fos = null;
					try {
						fos = new FileOutputStream(localFile);
						for (int i = 0; i < 5; i++) {
							try {
								entity.writeTo(fos);
								break;
							} catch (SocketTimeoutException E) {
								logger.debug(E.getMessage());
							}
						}
					} catch (Exception e) {
						logger.error("파일다운로드 에러.", e);
					} finally {
						if (fos != null) {
							fos.close();
						}
						entity.getContent().close();
					}

					if (localFile.length() > 0) {
						logger.error("파일사이즈 {}byte", localFile.length());
						fileCacheRepository.put(strUrl, localFile);
					} else {
						logger.debug("다운로드 파일이 0byte입니다. localFile={}", localFile.getName());
						if (localFile.isFile()) {
							localFile.delete();
						}
					}

				} else {
					logger.error("파일다운로드 응답에러 status={}", response.getStatusLine());
				}
			} catch (IOException e) {
				logger.error("파일다운로드중 에러발생.", e);
			} finally {
				if (entity != null)
					try {
						EntityUtils.consume(entity);
						entity.getContent().close();
					} catch (IllegalStateException e) {
						// TODO Auto-generated catch
						// block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch
						// block
						e.printStackTrace();
					} finally {
						request.abort();
					}
			}
			logger.debug("FileSize : {} byte", localFile.length());
		} else {
			// 로컬에서 파일읽어오기.
			try {
				FileUtils.copyFile(cacheFile, localFile);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		logger.trace("downloadFile exit");
		return localFile.length();
	}

	public void close() {
		scrapingDAO.close();
		fileCacheRepository.close();
	}

	public String getTestBuffer() {
		if (scrapingDAO instanceof MemoryScrapingData) {
			return ((MemoryScrapingData) scrapingDAO).getBuffer();
		} else {
			return "";
		}

	}

	public synchronized HttpResponse getHttpResponse(String url, String getMethod) {

		try {
			delay();
		} catch (Exception e) {

		}

		url = urlDecode.getDecodedUrl(url, encoding);
		// 입력된 URL에 대해서 무조건 Decoding 한후 필요에 따라서 Encoding 한다.

		if (getMethod.equalsIgnoreCase(Block.LINK_METHOD_POST)) {
			String postUrl = url.substring(0, url.indexOf("?"));
			// url이 post일때 ? 뒤의 파라메타를 뺸다.
			HttpPost post = new HttpPost(urlEncode.getEncodedUrl(postUrl, encoding));
			// ////////////////////////////////////////////////////////////////
			if (url != null && !"".equals(url)) {
				int pos = url.indexOf("?");
				if (pos > 0) {
					List<NameValuePair> nvps = new ArrayList<NameValuePair>();
					String data = url.substring(url.indexOf("?") + 1);
					String[] tmps = data.split("&");
					for (int i = 0; i < tmps.length; i++) {
						String[] kv = tmps[i].split("=");
						if (kv.length >= 2) {
							logger.trace("post request params: key = {}, val = {}", kv[0], kv[1]);
							nvps.add(new BasicNameValuePair(kv[0], kv[1]));
						}
					}
					try {
						post.setEntity(new UrlEncodedFormEntity(nvps, encoding));
					} catch (UnsupportedEncodingException e) {
						logger.error(e.getMessage(), e);
					}
				}
			}
			request = post;
			url = postUrl;
		} else {
			url = urlEncode.getEncodedUrl(url, encoding);
			request = new HttpGet(url);
		}

		logger.trace("----------------------------------------------------");
		Map<String, String> head = siteConfig.getHeadParam();
		Iterator<String> headItr = head.keySet().iterator();
		while (headItr.hasNext()) {
			String key = (String) headItr.next();
			String value = head.get(key);
			request.setHeader(key, value);
			logger.trace("key : {} ", key);
			logger.trace("value : {} ", value);
		}

		logger.trace("trying to connect method = {}, url = {}", getMethod, url);
		CookieStore cookieStore = (CookieStore) httpContext.getAttribute(ClientContext.COOKIE_STORE);
		List<Cookie> cookieList = cookieStore.getCookies();
		logger.trace("Login Cookie size : {}", cookieList.size());
		for (Cookie c : cookieList) {
			logger.trace("Req Cookie : {}", c.toString());
		}
		logger.trace("----------------------------------------------------");

		HttpResponse response = null;
		for (int retry = 0; retry < 5; retry++) {
			try {
				response = httpClient.execute(request, httpContext);
				break;
			} catch (SocketTimeoutException e) {
				logger.warn("Timeout retry.. {}", retry);
				try {
					if (response != null && response.getEntity() != null) {
						EntityUtils.consume(response.getEntity());
						response.getEntity().getContent().close();
					}
					delay();
				} catch (Exception e1) {
					request.abort();
					logger.error("httpclient.execute TimeOut Exception Happed");
				}

				httpClient.getParams().setIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, siteConfig.getTimeout() * 2);
				httpClient.getParams().setIntParameter(CoreConnectionPNames.SO_TIMEOUT, siteConfig.getTimeout() * 2);

			} catch (ConnectionPoolTimeoutException e) {
				logger.error(" httpclient.execute TimeOut Exception Happened : retry{}", retry + 1);
				response = null;

				try {
					delay();
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			} catch (IOException e) {
				logger.error(" httpclient.execute Exception Happened retry {}", retry + 1);
				logger.error(e.getMessage());
				response = null;
			}
		}

		if (response == null) {
			return null;
		}

		/**
		 * Http Response가 Redirect일때 처리 하는 코드 추가.
		 * */

		if ((response.getStatusLine().getStatusCode() == HttpStatus.SC_MOVED_PERMANENTLY) || (response.getStatusLine().getStatusCode() == HttpStatus.SC_MOVED_TEMPORARILY)
		                || (response.getStatusLine().getStatusCode() == HttpStatus.SC_SEE_OTHER) || (response.getStatusLine().getStatusCode() == HttpStatus.SC_TEMPORARY_REDIRECT)) {
			Header header = response.getFirstHeader("location");
			String reUrl = header.getValue();
			request.abort();
			return getHttpResponse(reUrl, getMethod);
		}
		return response;
	}

	public String StreamToString(InputStream is, String encoding) {

		ByteArrayOutputStream os = new ByteArrayOutputStream();
		byte[] buffer = new byte[4096];
		while (true) {
			try {
				int len = is.read(buffer);
				if (len == -1)
					break;
				os.write(buffer, 0, len);
			} catch (IOException e) {
				logger.error(e.toString());
			}
		}

		return EncodingUtils.getString(os.toByteArray(), encoding);
	}

}
