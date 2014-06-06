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

package com.websqrd.catbot.scraping;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.websqrd.libs.io.StorableInputStream;

public class HttpHandler {
	
	private final static Logger logger = LoggerFactory.getLogger(HttpHandler.class);
	
	public static String executeGet(HttpClient httpclient, String url, StorableInputStream is) throws IOException {
		String type = "text";
		HttpGet httget = new HttpGet(url);
		HttpResponse response = httpclient.execute(httget);
		HttpEntity entity = response.getEntity();
		Header header = entity.getContentType();
		if (header != null) {
			type = header.getValue().toLowerCase();
		}
		is.addStream(entity.getContent());
		is.rewind();
		return type;
	}
	
	/**
	 * 메모리기반 웹페이지 수집.
	 */
	public static String executeGet2(HttpClient httpclient, String url, String encoding, String[] a) throws IOException {
		String type = "text";
		HttpGet httget = new HttpGet(url);
		HttpResponse response = httpclient.execute(httget);
		HttpEntity entity = response.getEntity();
		Header header = entity.getContentType();
		if (header != null) {
			type = header.getValue().toLowerCase();
		}
		
		//InputStream to String
		InputStream is = entity.getContent();
		Writer writer = new StringWriter();
		char[] buffer = new char[1024];
		try {
			Reader reader = new BufferedReader(new InputStreamReader(is, encoding));
			int n;
			while ((n = reader.read(buffer)) != -1) {
				writer.write(buffer, 0, n);
			}
		} finally {
			is.close();
		}
		a[0] = writer.toString();
		return type;
	}
	
	public static String executeGet(String url,String encoding){
		String resultString = "";
		HttpClient httpclient = new DefaultHttpClient();
		InputStream is = null;
		ByteArrayOutputStream baos = null;
		try {
			HttpGet httget = new HttpGet(url);
			HttpResponse response = httpclient.execute(httget);
			HttpEntity entity = response.getEntity();
			Header header = entity.getContentType();
			String type = header.getValue().toLowerCase();
			baos = new ByteArrayOutputStream();
			if (type.startsWith("text/html")) {
				byte[] buf = new byte[1024];
				is = entity.getContent();
				for (int rlen = 0; (rlen = is.read(buf, 0, buf.length)) > 0;) {
					baos.write(buf, 0, rlen);
				}
				resultString = new String(baos.toByteArray(), encoding);
			}
			
		} catch (Exception e) {
			logger.error("",e);
		} finally {
			if(is != null) try { is.close(); } catch (IOException e) { }
			if(baos != null) try { baos.close(); } catch (IOException e) { }
		}
		
		return resultString;
	}
	
//	public static void executePost(){
//		
//		HttpClient httpclient = new DefaultHttpClient();
//		ResponseHandler<String> responseHandler = new BasicResponseHandler();
//		HttpPost httpost = new HttpPost("http://www.naver.com/");
//		
//		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
//		Exception ex = null;
//		
//		try {
//			httpost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
//			
//			String responseBody = httpclient.execute(httpost,responseHandler);
//
//			//System.out.println(HTMLTagRemover.clean(responseBody));
//			
//		} catch (UnsupportedEncodingException e) { ex = e;
//		} catch (ClientProtocolException e) { ex = e;
//		} catch (IOException e) { ex = e;
//		}finally {
//			if(ex!=null) {
//				logger.error(null,ex);
//			}
//			// When HttpClient instance is no longer needed,
//			// shut down the connection manager to ensure
//			// immediate deallocation of all system resources
//			httpclient.getConnectionManager().shutdown();
//		}
//		
//	}
}
