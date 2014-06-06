package com.websqrd.catbot.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.SocketTimeoutException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import com.websqrd.catbot.web.HttpClientWrapper;

import junit.framework.TestCase;

public class multiTest extends TestCase {
	public void test() throws IllegalStateException, IOException
	{
		String[] urls={"http://www.iam-magazine.com/Blog/Detail.aspx?g=509b4e21-db03-48d4-b5fd-de10cbf81f64",
					    "http://www.iam-magazine.com/Blog/Detail.aspx?g=600bb100-33f9-4476-a4d6-7fa3cfc5a0a5",
					    "http://www.iam-magazine.com/Blog/Detail.aspx?g=58fd6cd2-eee5-4059-bbd1-30e2d6745fcc",
					    "http://www.iam-magazine.com/Blog/Detail.aspx?g=78559d9a-7330-49e7-937b-8b1e9d73c644"};
		
		for ( int i = 0 ; i < urls.length ; i ++ )
			urlConnect(urls[i]);
		
	}
	
	public void urlConnect(String url) throws IllegalStateException, IOException
	{		
		String key = "Cookie";
		String value = "IAMFreeTrial=6; iam.LoginTicket=44506D4596788289DE70D2B8C73872E38C9BD94626AC0E9952B552B2C8A370B8081BAB29A259FEAE2E16111236CE0975AC792EF2C67068D4935B3759949E7BDA8AFEC639F9E5F8D0DD05F2AD3AF79E2B4F7E127AFB19CD53C3BD632961AF4B7DA8741E2D9B562E24D19CA9DD0FDC6E7FF9A87423DD49CD8B7A20DC7F3751A0FC396491C8E7597D139F68640C1CCC677C92366E3EA5CE3320DA2FB6715DCADC6F2D83213E; AspxAutoDetectCookieSupport=1;";
		String charset = "utf-8";
		String searchKeyword ="Gowoon";
		String searchKeyword2 = "authorbox";
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
			
			if(line.contains(searchKeyword2)){
				System.out.println("############Found >> "+line);
				line = br.readLine();
				System.out.println("############Found >> "+line);
				line = br.readLine();
				System.out.println("############Found >> "+line);
				line = br.readLine();
				System.out.println("############Found >> "+line);
				line = br.readLine();
				System.out.println("############Found >> "+line);
				line = br.readLine();
				System.out.println("############Found >> "+line);
				line = br.readLine();
				System.out.println("############Found >> "+line);
			}
//			System.out.println(line);
		}
		is.close();
		br.close();
	}
}
