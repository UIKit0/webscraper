package com.websqrd.catbot.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public class urlDecode {
	public static  synchronized String getDecodedUrl(String urlString, String encoding) {
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
}
