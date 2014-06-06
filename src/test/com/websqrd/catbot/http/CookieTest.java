package com.websqrd.catbot.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.SocketTimeoutException;

import junit.framework.TestCase;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import com.websqrd.catbot.web.HttpClientWrapper;

public class CookieTest extends TestCase{

	public void test() throws IllegalStateException, IOException {

//		String url = "http://www.iam-magazine.com/blog/detail.aspx?g=0b4469ef-377a-4221-aab3-870d05872fc9";
		String url = "http://www.iam-magazine.com/reports/detail.aspx?g=1a21c013-9480-4579-8a0c-fc7d2cf00006";
		String key = "Cookie";
		String value = "IAMFreeTrial=6; iam.LoginTicket=44506D4596788289DE70D2B8C73872E38C9BD94626AC0E9952B552B2C8A370B8081BAB29A259FEAE2E16111236CE0975AC792EF2C67068D4935B3759949E7BDA8AFEC639F9E5F8D0DD05F2AD3AF79E2B4F7E127AFB19CD53C3BD632961AF4B7DA8741E2D9B562E24D19CA9DD0FDC6E7FF9A87423DD49CD8B7A20DC7F3751A0FC396491C8E7597D139F68640C1CCC677C92366E3EA5CE3320DA2FB6715DCADC6F2D83213E; AspxAutoDetectCookieSupport=1;";
		String charset = "utf-8";
		String searchKeyword ="Gowoon";
		
		HttpContext httpContext = new BasicHttpContext();
//		BasicCookieStore cookieinputStore = new BasicCookieStore();
//		cookieinputStore.addCookie(new BasicClientCookie(key, value));
//		httpContext.setAttribute(ClientContext.COOKIE_STORE, cookieinputStore);
		
		HttpClient httpClient = HttpClientWrapper.wrapClient(new DefaultHttpClient());
		HttpResponse response = null;
		try {
			HttpGet request = new HttpGet(url);
			request.setHeader(key, value);
			response = httpClient.execute(request, httpContext);
		} catch (SocketTimeoutException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		InputStream is = response.getEntity().getContent();
		BufferedReader br = new BufferedReader(new InputStreamReader(is, charset));
		String line = null;
		while((line = br.readLine()) != null){
			if(line.contains(searchKeyword)){
				System.out.println("############Found >> "+line);
			}
//			System.out.println(line);
		}
		is.close();
		br.close();
	}
	
}
