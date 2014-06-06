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

import java.util.Properties;

public class CatbotServerConfig {
	private Properties props;
	private final int K = 1024;
	private final int M = K * K;
	private final int G = K * K * K;
	
	public CatbotServerConfig(Properties props){
		this.props = props;
	}
	
	public int getInt(String key){
		String str = props.getProperty(key);
		if(str == null)
			return -1;
			
		str = str.trim();
		int len = str.length();
		if(len > 0){
			char suffix = str.charAt(len - 1);
			if(suffix == 'g' || suffix == 'G'){
				return Integer.parseInt(str.substring(0, len - 1).trim()) * G;
			}else if(suffix == 'm' || suffix == 'M'){
				return Integer.parseInt(str.substring(0, len - 1).trim()) * M;
			}else if(suffix == 'k' || suffix == 'K'){
				return Integer.parseInt(str.substring(0, len - 1).trim()) * K;
			}else if(suffix == 'b' || suffix == 'B'){
				return Integer.parseInt(str.substring(0, len - 1).trim());
			}else{
				return Integer.parseInt(str);
			}
		}else{
			return -1;
		}
	}
	
	public String getString(String key){
		String val = props.getProperty(key);
		if(val != null)
			return val.trim();
		else
			return "";
	}
	
	public boolean getBoolean(String key){
		String val = props.getProperty(key);
		if(val != null)
			return Boolean.parseBoolean(val);
		else
			return false;
	}
	
	public Properties getProperties(){
		return props;
	}
}
