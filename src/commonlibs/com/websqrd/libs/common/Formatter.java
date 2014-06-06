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

package com.websqrd.libs.common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

/**
 * 객체 / 문자열의 상호변환 등을 위한 유틸
 *
 */
public class Formatter {
	
	/** 파싱을 위한 날자포맷.**/
	public static final SimpleDateFormat DATEFORMAT_DEFAULT_PARSE = new SimpleDateFormat("yyyyMMddHHmmssS");
	/** 포맷팅을 위한 날자포맷. **/
	public static final SimpleDateFormat DATEFORMAT_DEFAULT_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
	/** 날자 중의 특수기호들을 삭제하기 위한 패턴 **/
	public static final Pattern PTN_STRIP_DATE = Pattern.compile("[-\t :.,/]");

	/**
	 * 문자를 파싱하여 Date 객체로 반환한다.
	 * 반드시 년도(4자리) 월(2자리) 일(2자리) 시간(2자리) 분(2자리) 초(자리) 밀리초(3자리) 순으로 들어와야 하며
	 * 앞에서부터 순서대로라면 일부만 들어와도 파싱을 시도한다. (예를들면 20121010 등)
	 * @param data
	 * @return
	 * @throws ParseException
	 */
	public static Date parseDate(String data, Date defaultValue) {
		try {
			return parseDate(data);
		} catch (ParseException e) {
		}
		return defaultValue;
	}
	public static Date parseDate(String data) throws ParseException {
		data=PTN_STRIP_DATE.matcher(data).replaceAll("");
		//일부만 들어와도 되도록 자리맞춤을 위한 0 채움
		for(int strlen=data.length(); strlen < 17; strlen++) { data+="0"; }
		return DATEFORMAT_DEFAULT_PARSE.parse(data);
	}
	/**
	 * 날자를 문자로 변환하여 반환한다.
	 * 형식은 yyyy-MM-dd HH:mm:ss.S
	 * @param date
	 * @return
	 */
	public static String formatDate(Date date) {
		return DATEFORMAT_DEFAULT_FORMAT.format(date);
	}
	//문자형반환
	public static String optStr(Object obj, String option) {
		if(obj!=null) {
			return obj.toString();
		}
		return option;
	}
	//정수형반환
	public static int optInt(Object obj, int option) {
		if(obj!=null) {
			try {
				return Integer.parseInt(obj.toString());
			} catch (NumberFormatException e) { }
		}
		return option;
	}
	//큰정수형반환
	public static long optLong(Object obj, long option) {
		if(obj!=null) {
			try {
				return Integer.parseInt(obj.toString());
			} catch (NumberFormatException e) { }
		}
		return option;
	}
	//단정도실수형반환
	public static float optFloat(Object obj, float option) {
		if(obj!=null) {
			try {
				return Float.parseFloat(obj.toString());
			} catch (NumberFormatException e) { }
		}
		return option;
	}
	//배정도실수형반환
	public static double optDouble(Object obj, double option) {
		if(obj!=null) {
			try {
				return Double.parseDouble(obj.toString());
			} catch (NumberFormatException e) { }
		}
		return option;
	}
	
	private static String CONTROL_CHAR_REGEXP = "["+(char)0+" "+(char)1+" "+(char)2+" "+(char)3+" "+(char)4+" "+(char)5+" "
	+(char)6+" "+(char)7+" "+(char)8+" "+(char)11+" "+(char)12+" "+(char)14+" "+(char)15+" "+(char)16+" "+(char)17+" "
	+(char)18+" "+(char)19+" "+(char)20+" "+(char)21+" "+(char)22+" "+(char)23+" "+(char)24+" "+(char)25+" "+(char)26+" "
	+(char)27+" "+(char)28+" "+(char)29+" "+(char)30+" "+(char)31+"]";
	
	public static String getFormatTime(long t){
		if(t > 1000){
			float a = (float) (t / 1000);
			if(a > 60){
				float b = a / 60;
				
				if(b > 60){
					float c = b / 60;
					return String.format("%.1f h", c);
				}else{
					return String.format("%.1f m", b);
				}
				
			}else{
				return String.format("%.1f s", a);
			}
		}else{
			return t +" ms";
		}
	}
	
	public static String getFormatSize(long s){
		 
		if(s > 1024){
			float a = (float) (s / 1024);
			
			if(a > 1024){
				float b = a / 1024;
				
				if(b > 1024){
					float c = b / 1024;
					return String.format("%.1f GB", c);
				}else{
					return String.format("%.1f MB", b);
				}
				
			}else{
				return String.format("%.1f KB", a);
			}
		}else{
			return s +" B";
		}
	}
	
	public static String removeControlChars(String value){
		return value.replaceAll(CONTROL_CHAR_REGEXP, " ");
	}
	/*
	 * 아래에 Json을 excape하는데 참고할 만한 좋은 글이 있다.
	 * 이대로 구현함.
	 * Why using HTML characters code instead of JavaScript escape solution?
	 * 	In JavaScript to escape a ‘ you should write \’
	 * to escape a ” you should write \”
	 * to escape a  you should write \\
	 * If you are working in a Domino view you will have to write something like \\\\, it’s not very readable.
	 * With the HTML character code you don’t have to worry anymore about how many \ you have to add.
	 * &#39; or &quot; replace ‘
	 * &#92; replace \
	 * &#34; replace ”
	 * With &#39; you save one more character compare to its equivalent &quot;
	 * In a JSON with a lot of data it’s always good to optimize the size.
	 * */
	//CONTROL_CHAR_REGEXP에는 tab(9), cr(10), LF(13) 제외

	public static String escapeJSon(String jsonString){
		return jsonString.replaceAll(CONTROL_CHAR_REGEXP, " ")
			.replaceAll("\t","&#09;")
			.replaceAll("\r\n","&#13;&#10;")
			.replaceAll("\r","&#13;&#10;")
			.replaceAll("\n","&#13;&#10;")
			.replaceAll("\\\\","&#92;")
			.replaceAll("\"","&#34;")
			.replaceAll("\'","&#39;");
	}
	
	public static String escapeXml(String xmlString){
		return xmlString.replaceAll(CONTROL_CHAR_REGEXP, " ")
			.replaceAll("&", "&amp;")
			.replaceAll("\'", "&apos;")
			.replaceAll("\"", "&quot;")
			.replaceAll("<", "&lt;")
			.replaceAll(">", "&gt;");
	}
}
