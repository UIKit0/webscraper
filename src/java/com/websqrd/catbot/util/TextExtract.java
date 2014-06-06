package com.websqrd.catbot.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import com.websqrd.catbot.setting.CatbotSettings;

public class TextExtract {
	private final static Logger logger = LoggerFactory.getLogger(TextExtract.class);
	private  List<String> lines;
	private  List<String> realLines;
	private int blocksWidth;
	private  int threshold;
	private  String html;
	private  boolean flag; //<br>은 줄바꿈으로 변환하는지 여부.
	private  int start;
	private  int end;
	private  StringBuilder text;
	private  ArrayList<Integer> indexDistribution;
	private static Pattern tagNamePattern = Pattern.compile("(?is)<[/?]?([a-z]+)\\s?.*?>");
	private static Pattern tdValuePattern = Pattern.compile("(?is)(<td[^>]*>)(.*?)</td>");
	private static String pStartPatten = "(?is)<p[^>]*>";
	private static String brPatten = "(?is)<[bB][rR][^>]*>";
	public TextExtract() {
		lines = new ArrayList<String>();
		realLines = new ArrayList<String>();
		indexDistribution = new ArrayList<Integer>();
		text = new StringBuilder();
		blocksWidth = 3;
		flag = false;
		threshold	= 70;   
	}
	
	public void setthreshold(int value) {
		threshold = value;
	}

	public String parse(String _html) {
		return parse(_html, false);
	}
	
	public  String parse(String _html, boolean _flag) {
		flag = _flag;
		html = preProcess(_html);
		return getText();
	}
	
	public static String clean(String _html) {
		return clean(_html, true, false, null);
	}
	
	/*
	 * recoverHtmlEntity : 본문 내용중에 태그를 <p>제목</p> 과 같이 적지 않고, &lt;제목&lt/p>과 같이 적을 경우, 태그가 남아있게 된다. 이런 내용까지도 제거하기 위함. 
	 * */
	public static String clean(String dirty, boolean recoverHtmlEntity, boolean retainLineFeed, Set<String> whiteSet) {
		//일단 html태그를 제외한 모든 줄바꿈을 없애고, 차후에 br과 p 태그에 기반하여 줄바꿈을 다시 넣어준다.
		Whitelist whitelist = new Whitelist();
		
		//내부적으로 사용할 허용 태그 
		whitelist.addTags("br").addTags("p");
		//기본 속성 셋팅. tag를 add 하기 전까지는 속성만으로 whiteList에 적용되지는 않는다.
		whitelist.addAttributes("img", "align", "alt", "height", "src", "title", "width", "style")
		.addAttributes("table", "summary", "width", "style")
		.addAttributes("th", "colspan", "rowspan", "width", "style")
		.addAttributes("tr", "colspan", "rowspan", "width", "style")
        .addAttributes("td", "colspan", "rowspan", "width", "style");
		
		if(whiteSet != null){
			Iterator<String> iterator = whiteSet.iterator();
			while(iterator.hasNext()){
				String tag = iterator.next();
				whitelist.addTags(tag);
				if(tag.equalsIgnoreCase("table")){
					whitelist.addTags("tbody").addTags("th").addTags("tr").addTags("td");
				}
			}
		}
		
		dirty = Jsoup.clean(dirty, whitelist);
//		logger.debug("dirty0 = {}", dirty);
		//<br>태그기반의 줄바꿈으로 변경할 것이므로, jsoup에서 자동추가된 줄바꿈 문자는 모두 없애준다. 
		dirty = dirty.replaceAll("[\\r\\n]", "");
		//table 태그를 살린다면 td내부의 p와 br태그도 살려준다.
		String P_START = "_P_TAG_";
		String P_END = "_P_ENDTAG_";
		String BR = "_BR_TAG_";
		if(whiteSet != null && whiteSet.contains("table")){
			
			Matcher matcher = tdValuePattern.matcher(dirty);
			StringBuffer result = new StringBuffer();
			
			while(matcher.find()){
				String tdTag = matcher.group(1);
				String value = matcher.group(2).replaceAll(pStartPatten, P_START).replaceAll("(?is)</p>", P_END).replaceAll(brPatten, BR);
				matcher.appendReplacement(result, tdTag+value+"</td>");
			}
			matcher.appendTail(result);
			//태그가 치환된 스트링.
			dirty = result.toString();
		}
//		dirty = dirty.replaceAll("&nbsp;", " ").replace((char)160, ' ');
		
		//tagRemove2 일 경우 적용.
		if(recoverHtmlEntity){
			dirty = dirty.replaceAll("&lt;", "<").replaceAll("&gt;", ">");
		}
		
		String lineFeed = " ";
		if(retainLineFeed){
			lineFeed = CatbotSettings.LINE_SEPARATOR;
		}
//		logger.debug("dirty1 = {}", dirty);
		dirty = dirty.replaceAll(brPatten, lineFeed);
//		logger.debug("dirty2 = {}", dirty);
		//p 태그를 찾아준다.
		dirty = dirty.replaceAll(pStartPatten, lineFeed);
		//닫는 p태그 제거.
		dirty = dirty.replaceAll("(?is)</p>", "");
		dirty = preProcess(dirty);
//		logger.debug("dirty3 = {}", dirty);
		//글의 처음 나오는 공백은 없애주고, 중간에 나오는 여러 공백은 하나로 치환한다.
		//여러공백을 하나로 변경한뒤 줄바꿈과 공백이 섞여나오는 경우 하나로 치환한다. 순서가 중요!
		dirty = dirty.replaceAll("^\\s{1,}", "").replaceAll("[\\t ]{2,}", " ");
		//줄바꿈 2개이상은 최대 2개로 유지한다.
		dirty = dirty.replaceAll("[\\r\\n]{2,}", CatbotSettings.LINE_SEPARATOR + CatbotSettings.LINE_SEPARATOR);
//		logger.debug("dirty4 = {}", dirty);
		//
		//table>td 복원
		//
		if(whiteSet != null && whiteSet.contains("table")){
			dirty = dirty.replaceAll(P_START, "<p>").replaceAll(P_END, "</p>").replaceAll(BR, "<br/>");
		}
		
		//줄바꿈 모두 없앰.
		if(!retainLineFeed){
			dirty = dirty.replaceAll("[\\r\\n]", " ");
		}else{
			//태그를 사용하면 br 사용한다.
			if(whiteSet != null){
				dirty = dirty.replaceAll(CatbotSettings.LINE_SEPARATOR, "<br/>");
			}
		}
		//태그를 한줄로 모아준다.
		dirty = dirty.replaceAll(">[\\r\\n]+<", "><");
		return dirty.trim();
	}
	
	private static String preProcess(String html) {
		html = html.replaceAll("(?is)<!DOCTYPE.*?>", "");
		html = html.replaceAll("(?is)<!--.*?-->", "");				// remove html comment
		html = html.replaceAll("(?is)<script.*?>.*?</script>", ""); // remove javascript
		html = html.replaceAll("(?is)<style.*?>.*?</style>", "");   // remove css
		html = html.replaceAll("(?is)<div.*?>", "");
		html = html.replaceAll("</div>", "");   // div css
		html = html.replaceAll("&.{2,5};|&#.{2,5};", " ");			// remove special char
		return html;
	}
	
	private String getText() {
		lines = Arrays.asList(html.split("\n"));
		realLines.addAll(lines);
		indexDistribution.clear();
		
		for (int i = 0; i < lines.size() - blocksWidth; i++) {
			int wordsNum = 0;
			for (int j = i; j < i + blocksWidth; j++) { 
				lines.set(j, lines.get(j));
				wordsNum += lines.get(j).length();
			}
			indexDistribution.add(wordsNum);
//			System.out.println(wordsNum);
		}
		
		start = -1; end = -1;
		boolean boolstart = false, boolend = false;
		text.setLength(0);
		
		for (int i = 0; i < indexDistribution.size() - 1; i++) {
			if (indexDistribution.get(i) > threshold && ! boolstart) {
				if (indexDistribution.get(i+1).intValue() != 0 
					|| indexDistribution.get(i+2).intValue() != 0
					|| indexDistribution.get(i+3).intValue() != 0) {
					boolstart = true;
					start = i;
					continue;
				}
			}
			if (boolstart) {
				if (indexDistribution.get(i).intValue() == 0 
					|| indexDistribution.get(i+1).intValue() == 0) {
					end = i;
					boolend = true;
				}
			}
			StringBuilder tmp = new StringBuilder();
			if (boolend) {
				//System.out.println(start+1 + "\t\t" + end+1);
				for (int ii = start; ii <= end; ii++) {
					if (lines.get(ii).length() < 5) continue;
					tmp.append(realLines.get(ii) + "\n");
				}
				String str = tmp.toString();
				//System.out.println(str);
				if (str.contains("Copyright")  || str.contains("주소") ) continue; 
				text.append(str);
				boolstart = boolend = false;
			}
		}
		return text.toString().replaceAll("\\s{2,}", " ");
	}
}
