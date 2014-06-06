package com.websqrd.catbot.scraping;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import junit.framework.TestCase;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HttpContext;
import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.HtmlCleaner;

import com.websqrd.catbot.util.TextExtract;
import com.websqrd.catbot.web.HttpClientWrapper;

public class FetchTest extends TestCase{

	protected static HttpClient httpClient = HttpClientWrapper.wrapClient(new DefaultHttpClient());
	HttpContext httpContext;
	
	public void testFetch() {
		InputStream is = null;
		String strUrl = "http://edu.uw21.net/section/community/notice_list.jsp";
		try {
			HttpUriRequest request = new HttpGet(strUrl);
			request.addHeader("Content-Type",
				    "charset=euc-kr");
			HttpResponse response = httpClient.execute(request, httpContext);
			is = response.getEntity().getContent();
			StringBuffer out = new StringBuffer();
		     byte[] b = new byte[4096];
		     for (int n; (n = is.read(b)) != -1;) {
		         out.append(new String(b, 0, n));
		     }
			System.out.println(out.toString());
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(is!=null) try { is.close(); } catch (Exception e) { }
		}
	}
	
	public void testHtmlCleaner(){
		String str = "<td height=\"300\" valign=\"top\" colspan=\"4\" style=\"padding:10px;\">" +
				"				<br>&nbsp; ◆ 교육과정 및 입학 안내◆<br><br>&nbsp;&nbsp;&nbsp; ㆍ원서접수 : 2012년 2월 29일까지<br>&nbsp;&nbsp;&nbsp;<br>&nbsp;&nbsp;&nbsp;&nbsp;ㆍ교육과정 : 인문계 교육과정 2년<br>&nbsp;&nbsp;&nbsp;<br>&nbsp;&nbsp; &nbsp;ㆍ입학 및 졸업 : 2012년 3월 2일 입학 -&nbsp;2014년 2월 졸업<br>&nbsp;&nbsp;&nbsp;<br>&nbsp;&nbsp;&nbsp;&nbsp;ㆍ지원자격 : 만 17세이상 중학교졸업 자격을&nbsp;가진 자<br>&nbsp;&nbsp;&nbsp;<br>&nbsp;&nbsp; &nbsp;ㆍ지원방법 : 준비서류 지참 후&nbsp;학교 방문<br>&nbsp;&nbsp;&nbsp;<br>&nbsp;&nbsp; &nbsp;ㆍ준비서류 : 중학교 졸업증명서 또는 중졸검정고시 합격증명서 1통,<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;주민등록등본 1통, 사진2매, 원서료 4000원<br>&nbsp;&nbsp;&nbsp;<br>&nbsp;&nbsp; &nbsp;ㆍ소 재 지&nbsp;: 경기도 수원시 이목동 104-1번지 계명고등학교<br>&nbsp;&nbsp;&nbsp;<br>&nbsp;&nbsp; &nbsp;ㆍ문&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;의&nbsp;: 031)258-0231~3<br>&nbsp;&nbsp;" +
				"			</td>";
//		System.out.println(str);
		String value = null;
		str = str.replaceAll("&amp;", "&").replaceAll("&nbsp;", " ").replaceAll("&lt;", "<").replaceAll("&gt;", ">");
		value = TextExtract.clean(str, true, true, null);
//		System.out.println(value);
//		value = Jsoup.clean(str, Whitelist.none());
		System.out.println(str);
		System.out.println("result=>"+value);
//		CleanerProperties props = new CleanerProperties();
//		props.setTranslateSpecialEntities(true);
//		props.setOmitComments(false);
//		props.setNamespacesAware(false);
//		HtmlCleaner cl = new HtmlCleaner(props);
//		String pageContent = null;
//		try {
//			TagNode tagNode = cl.clean(str);
//			pageContent = new PrettyXmlSerializer(props).getXmlAsString(tagNode);
//			System.out.println(pageContent);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		
//		try {
//			List<String> nodes = XmlUtils.getNodes("/textarea", new ByteArrayInputStream(pageContent.getBytes()));
//			System.out.println("size= "+nodes.size());
//			Iterator<String> it = nodes.iterator();
//			
//			while (it.hasNext()) {
//				String nodeContent = it.next();
//				Map<String, String> result = new HashMap<String, String>();
//				parse(result, blockList, nodeContent, isFull);
//				if(result.size() > 0){
//					resultList.add(result);
//				}
//			}
//			
//		} catch (JDOMException e) {
//			logger.error("",e);
//		} catch (IOException e) {
//			logger.error("",e);
//		}
	}
	
	public void testHtmlCleaner2(){
		CleanerProperties props = new CleanerProperties();
		props.setTranslateSpecialEntities(true);
		props.setOmitComments(false);
		props.setNamespacesAware(false);
		HtmlCleaner cl = new HtmlCleaner(props);
		try {
			cl.clean(new URL(""));
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
