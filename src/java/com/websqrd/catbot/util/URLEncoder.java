package com.websqrd.catbot.util;

import java.io.UnsupportedEncodingException;

public class URLEncoder {
	public static String getEncodedUrl(String urlString, String encoding) {
		int p = urlString.indexOf("?");
		if (p < 0) {
			return getEncodedPath(urlString, encoding);
		} else {
			StringBuffer urlBuffer = new StringBuffer();
			String frontUrl = getEncodedPath(urlString.substring(0, p), encoding);
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
						value = java.net.URLEncoder.encode(value, encoding);
					} catch (UnsupportedEncodingException ignore) { }
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
	
	
	public static String getEncodedPath(String path, String encoding) {
		String[] dirs = path.split("/");
		StringBuilder sb = new StringBuilder();
		for (int inx = 0; inx < dirs.length; inx++) {
			if(inx == 0) {
				sb.append(dirs[0]);
				continue;
			}
			try {
				sb.append("/").append(java.net.URLEncoder.encode(dirs[inx], encoding));
			} catch (UnsupportedEncodingException ignore) { }
		}
		if(path.endsWith("/")) {
			sb.append("/");
		}
		if(sb.length() > 0) {
			path = sb.toString();
		}
		return path;
	}
}
