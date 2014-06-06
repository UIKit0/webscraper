/*
 * Copyright (C) 2011 Web Squared Inc. http://websqrd.com
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

package com.websqrd.catbot.web;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpUtils {
	private static Logger logger = LoggerFactory.getLogger(HttpUtils.class);
	
	public static String readContentFromFile( File file ) throws IOException {
	    BufferedReader reader = new BufferedReader( new FileReader (file));
	    String line = null;
		StringBuilder stringBuilder = new StringBuilder();
		String ls = System.getProperty("line.separator");

	    while( ( line = reader.readLine() ) != null ) {
	        stringBuilder.append( line );
	        stringBuilder.append( ls );
	    }

	    return stringBuilder.toString();
	}
	
	public static String getAmpEscaped(String str){
		return str.replace("&", "&amp;");
	}
	
	public static void callHttpPost(String url, String urlParams){
		try {
			URL nodeURL = new URL (url);
			byte[] paramData = urlParams.getBytes();
			HttpURLConnection conn = (HttpURLConnection)nodeURL.openConnection();
			
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			conn.setRequestProperty("Content-Length", Integer.toString(paramData.length));
			conn.setUseCaches(false);
			conn.setDoOutput(true);
			conn.setDoInput(true);
		
			OutputStream os = conn.getOutputStream();
			os.write(paramData);
			os.flush();
			os.close();
//			logger.debug("Call "+url+", "+urlParams);
			
			InputStream is = conn.getInputStream();
			is.close();
			
		} catch (IOException e) {
			logger.error(e.getMessage(),e);
			logger.error("Fail : "+url+", "+urlParams);
		}
		
	}
}	
