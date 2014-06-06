package com.websqrd.catbot.web;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebUtils {
	/**
	 * Utility for manipulating link address (like URL or URI)
	 * @author lupfeliz
	 * @author Nuri
	 */
	private final static Logger logger = LoggerFactory.getLogger(WebUtils.class);
	private static String newLine = System.getProperty("line.separator");

	private final static String[][] DEFAULT_REPLACE_SUBDOMAINS = { { "", "www" } };
	private final static Pattern PATTERN_FULL_URL = Pattern.compile("([a-zA-Z0-9]+)[:][/]{2}([a-zA-Z0-9_.-]+)(([/][^?]*)[?]?(.*))?");
	private final static Pattern PATTERN_RELATIVE_URI = Pattern.compile("([/.]?[^?]*)[?]?(.*)");

	public static String getContentByURL(String strUrl, String encoding) {
		BufferedReader br = null;
		InputStream is = null;
		try {
			URL url = new URL(strUrl);
			is = url.openStream();
			br = new BufferedReader(new InputStreamReader(is, encoding));
			String s = "";
			StringBuffer sb = new StringBuffer();
			while ((s = br.readLine()) != null) {
				sb.append(s);
				sb.append(newLine);
			}
			br.close();
			is.close();
			return sb.toString();
		} catch (Exception e) {
			try {
				if (br != null)
					br.close();
				if (is != null)
					is.close();
			} catch (IOException e1) {
			}
			logger.error("", e);
		}
		return "";
	}

	/**
	 * Checks if specified link is full URL.
	 * 
	 * @param link
	 * @return True, if full URl, false otherwise.
	 */
	public static boolean isFullURL(String link) {
		if (PATTERN_FULL_URL.matcher(link).find()) {
			return true;
		}
		return false;
	}

	/**
	 * Calculates full URL for specified page URL and link which could be
	 * full, absolute or relative like there can be found in A or IMG tags.
	 */

	public static String fullURL(String baseUrl, String link) {
		Matcher m1 = PATTERN_FULL_URL.matcher(link);
		Matcher m2 = PATTERN_RELATIVE_URI.matcher(link);
		Matcher m3 = PATTERN_FULL_URL.matcher(baseUrl);

		String schem = "";
		String host = "";
		String uri = "";
		String query = "";
		if (baseUrl != null && !"".equals(baseUrl) && m3.find()) {
			schem = m3.group(1);
			host = m3.group(2);
			uri = m3.group(4);
			if (schem == null) {
				schem = "";
			}
			if (host == null) {
				host = "";
			}
			if (uri == null) {
				uri = "/";
			}
			String[] uriArray = uri.split("/");
			StringBuffer uriBuffer = new StringBuffer();
			for (int inx = 0; inx < uriArray.length - 1; inx++) {
				uriBuffer.append("/").append(uriArray[inx]);
			}
			uri = uriBuffer.toString();
		}
		for (int inx = 0; inx < 2; inx++) {
			if (m1.find()) {
				schem = m1.group(1);
				host = m1.group(2);
				uri = m1.group(4);
				query = m1.group(5);
				if (schem == null) {
					schem = "";
				}
				if (host == null) {
					host = "";
				}
				if (uri == null) {
					uri = "/";
				}
				if (query == null) {
					query = "";
				}
				break;
			} else if (link.startsWith("/")) {
				m1 = PATTERN_FULL_URL.matcher(schem + "://" + host + "/" + link);
				continue;
			} else if (link.startsWith("..")) {
				m1 = PATTERN_FULL_URL.matcher(schem + "://" + host + "/" + uri + "/" + link);
				continue;
			} else {
				m1 = PATTERN_FULL_URL.matcher(schem + "://" + host + "/" + uri + "/" + link);
				continue;
			}
		}

		if (uri.length() > 0 && uri.indexOf("/") != -1) {
			List<String> uriLevels = new ArrayList<String>();
			String[] uriArray = uri.split("/");
			for (String uriElement : uriArray) {
				if ("".equals(uriElement)) {
				} else if (".".equals(uriElement)) {
				} else if ("..".equals(uriElement)) {
					if (uriLevels.size() > 0) {
						uriLevels.remove(uriLevels.size() - 1);
					}
				} else {
					uriLevels.add(uriElement);
				}
			}
			StringBuffer uriBuffer = new StringBuffer();
			for (String uriElement : uriLevels) {
				uriBuffer.append("/").append(uriElement);
			}
			uri = uriBuffer.toString();
		}

		if (query.length() > 0 && query.indexOf("&") != -1) {
			TreeSet<String> queryLevels = new TreeSet<String>();
			String[] queryArray = query.split("&");
			for (String queryElement : queryArray) {
				if (!queryLevels.contains(queryElement)) {
					queryLevels.add(queryElement);
				}
			}
			StringBuffer queryBuffer = new StringBuffer();
			for (String queryElement : queryLevels) {
				queryBuffer.append("&").append(queryElement);
			}
			query = queryBuffer.substring(1);
		}
		String url = schem + "://" + host;
		if (uri.length() > 0) {
			url += uri;
		}
		if (query.length() > 0) {
			url += "?" + query;
		}

		return url;
	}



	/**
	 * 이 메소드는 사용하지 않음.
	 * 엔코딩 결과가 맞지 않고, 같은 역활을 하는 함수가 있음
	 * 같은 역활을 하는 함수 명 : getEncodeUrl
	 * http://abc.com/?a=1&b=%ED%95%9C%EA%B8%80%EC%9E%85%EB%8B%88%EB%8B%A4.&c=5#001
	 * */
//	public static String encodeURL(String url, String charset) throws UnsupportedEncodingException {
//		if (url == null) {
//			return "";
//		}
//
//		int index = url.indexOf("?");
//		if (index >= 0) {
//			String result = url.substring(0, index + 1);
//			String paramsPart = url.substring(index + 1);
//			StringTokenizer tokenizer = new StringTokenizer(paramsPart, "&");
//			while (tokenizer.hasMoreTokens()) {
//				String definition = tokenizer.nextToken();
//				int eqIndex = definition.indexOf("=");
//				if (eqIndex >= 0) {
//					String paramName = definition.substring(0, eqIndex);
//					String paramValue = definition.substring(eqIndex + 1);
//					result += paramName + "=" + encodeURLParam(paramValue, charset) + "&";
//				} else {
//					result += encodeURLParam(definition, charset) + "&";
//				}
//			}
//			if (result.endsWith("&")) {
//				result = result.substring(0, result.length() - 1);
//			}
//			return result;
//		}
//		return url;
//	}
//	private static String encodeURLParam(String value, String charset) throws UnsupportedEncodingException {
//	if (value == null) {
//		return "";
//	}
//
//	try {
//		String decoded = URLDecoder.decode(value, charset);
//		String result = "";
//		for (int i = 0; i < decoded.length(); i++) {
//			char ch = decoded.charAt(i);
//			result += (ch == '#') ? "#" : URLEncoder.encode(String.valueOf(ch), charset);
//		}
//
//		return result;
//	} catch (IllegalArgumentException e) {
//		return value;
//	}
//}	
	

	public static String replaceSubdomain(String link) {
		return replaceSubdomain(link, DEFAULT_REPLACE_SUBDOMAINS);
	}

	public static String replaceSubdomain(String link, String[][] subdomains) {

		Matcher m1 = PATTERN_FULL_URL.matcher(link);

		String schem = "";
		String host = "";
		String uri = "";
		String query = "";
		if (m1.find()) {
			schem = m1.group(1);
			host = m1.group(2);
			uri = m1.group(4);
			query = m1.group(5);
			if (schem == null) {
				schem = "";
			}
			if (host == null) {
				host = "";
			}
			if (uri == null) {
				uri = "";
			}
			if (query == null) {
				query = "";
			}

			if (subdomains != null) {
				for (String[] subdomain : subdomains) {
					for (int sdInx = 0; sdInx < subdomain.length; sdInx++) {
						if (sdInx == 0) {
							continue;
						}
						if (host.startsWith(subdomain[sdInx])) {
							link = schem + "://" + host;
							if (uri.length() > 0) {
								link += uri;
							}
							if (query.length() > 0) {
								link += "?" + query;
							}
							return link;
						}
					}
				}
			}
		}

		return (link);
	}

	public static URL verifyLink(String url) {

		Matcher m1 = PATTERN_FULL_URL.matcher(url);
		URL verifiedUrl = null;
		if (m1.find()) {
			try {
				verifiedUrl = new URL(url);
			} catch (MalformedURLException e) {
			}
		}
		return verifiedUrl;
	}

	public static String normalizeLink(String link) {
		return fullURL("", link);
	}

	public static synchronized String getDecodedUrl(String urlString, String encoding) {
		int p = urlString.indexOf("?");
		if (p < 0) {
			return urlString;
		} else {
			StringBuffer urlBuffer = new StringBuffer();
			String frontUrl = urlString.substring(0, p);
			urlBuffer.append(frontUrl);
			String paramString = urlString.substring(p + 1);
			String[] params = paramString.split("&");
			int i = 0;
			for (String param : params) {
				int p2 = param.indexOf("=");
				String name = null;
				String value = null;
				if (p2 < 0) {
					continue;
				} else {
					name = param.substring(0, p2).trim();
					value = param.substring(p2 + 1).trim();
				}

				if (value.length() > 0) {
					try {
						value = URLDecoder.decode(value, encoding);
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
					if (i == 0) {
						urlBuffer.append("?");
					} else {
						urlBuffer.append("&");
					}
					urlBuffer.append(name);
					urlBuffer.append("=");
					urlBuffer.append(value);
					i++;
				}
			}
			return urlBuffer.toString();

		}
	}

	public static synchronized String getEncodedUrl(String urlString, String encoding) {
		int p = urlString.indexOf("?");
		if (p < 0) {
			return urlString;
		} else {
			StringBuffer urlBuffer = new StringBuffer();
			String frontUrl = urlString.substring(0, p);
			urlBuffer.append(frontUrl);
			String paramString = urlString.substring(p + 1);
			String[] params = paramString.split("&");
			int i = 0;
			for (String param : params) {
				int p2 = param.indexOf("=");
				String name = null;
				String value = null;
				if (p2 < 0) {
					continue;
				} else {
					name = param.substring(0, p2).trim();
					value = param.substring(p2 + 1).trim();
				}

				if (value.length() > 0) {
					try {
						value = URLEncoder.encode(value, encoding);
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
					if (i == 0) {
						urlBuffer.append("?");
					} else {
						urlBuffer.append("&");
					}
					urlBuffer.append(name);
					urlBuffer.append("=");
					urlBuffer.append(value);
					i++;
				}
			}
			return urlBuffer.toString();

		}
	}
	
	public static int getInt(String s, int defaultValue){
		if(s == null){
			return defaultValue;
		}
		
		try{
			return Integer.parseInt(s);
		}catch(NumberFormatException e){
			return defaultValue;
		}
	}
	
	public static float getFloat(String s, float defaultValue){
		if(s == null){
			return defaultValue;
		}
		
		try{
			return Float.parseFloat(s);
		}catch(NumberFormatException e){
			return defaultValue;
		}
	}
	
	public static boolean getBoolean(String s, boolean defaultValue){
		if(s == null){
			return defaultValue;
		}
		
		try{
			return Boolean.parseBoolean(s);
		}catch(NumberFormatException e){
			return defaultValue;
		}
	}
	
	public static String getString(String s, String defaultValue){
		if(s == null || s.length() == 0){
			return defaultValue;
		}
		
		return s;
	}	

}
