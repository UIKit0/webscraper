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

package com.websqrd.catbot.setting;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;

public class RepositorySetting {
	// VENDOR
	public static final String VENDOR_DERBY = "DERBY";
	public static final String VENDOR_MYSQL = "MYSQL";
	public static final String VENDOR_MSSQLSERVER = "MSSQLSERVER";
	public static final String VENDOR_CUBRID = "CUBRID";
	public static final String VENDOR_ORACLE = "ORACLE";

	private Properties props;
	public String id;
	public String encoding;
	public String vendor;
	public String host;
	public String port;
	public String db;
	public String user;
	public String password;
	public String parameter;
	public HashMap<String, String> propertyMap = new HashMap<String, String>();

	public RepositorySetting() {
		propertyMap.clear();
	}

	public RepositorySetting(Properties props) {
		this.props = props;
		id = props.getProperty("id");
		encoding = props.getProperty("encoding");

		vendor = props.getProperty("vendor");
		host = props.getProperty("host");
		port = props.getProperty("port");
		db = props.getProperty("db");
		user = props.getProperty("user");
		password = props.getProperty("password");
		parameter = props.getProperty("parameter");

		if (parameter != null) {
			String[] elements = parameter.split("&");
			Iterator itr = props.keySet().iterator();

			String key = "";
			String value = "";
			for (String element : elements) {
				String values[] = element.split("=");
				if (values.length == 2)
					propertyMap.put(values[0], values[1]);
			}
		}
	}

	public Properties getProperties() {
		return props;
	}

	public static void init(Properties props) {
		props.setProperty("encoding", "");
		props.setProperty("vendor", "");
		props.setProperty("host", "");
		props.setProperty("port", "");
		props.setProperty("db", "");
		props.setProperty("user", "");
		props.setProperty("password", "");
		props.setProperty("parameter", "");
	}
}
