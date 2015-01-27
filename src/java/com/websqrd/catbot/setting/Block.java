package com.websqrd.catbot.setting;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.jdom.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ==우선순위 정의==
 * 1. xpath적용해서 데이터 가져오기
 * 2. modifier 적용 
 * 3. prefix, suffix 적용
 * 4. linkPage 적용
 * 5. 
 * @author swsong
 *
 */
public class Block {
	protected final static Logger logger = LoggerFactory.getLogger(Block.class);
	public static final String LINK_METHOD_GET = "GET";
	public static final String LINK_METHOD_POST = "POST";
	
	public static final String LINK_TYPE_URL = "URL";
	public static final String LINK_TYPE_JAVASCRIPT = "JAVASCRIPT";
	
	
	protected String id; //블록이름.
	protected boolean isPk; // pk여부
	protected boolean isDocument; //document 여부를 확인한다. 
	protected boolean isNumeric; // 숫자 타입 여부. pk비교시 숫자형으로 비교를 수행한다.
	protected String xpath;// 적용할 xpath 
	private String selector;// 적용할 jsoup selector
	protected String json; //FIXME 적용할 json 식.
	protected String regexp; //적용할 regexp
	protected String value; //데이터를 바로 셋팅해준다.
	protected ArrayList<Block> children = new ArrayList<Block>(); //하위 자식 객체들..
	protected boolean isField; //필드일 경우에만 writeHandler에 기록한다.
	protected String modify; //모디파이어 명
	protected String method; //모디파이어 명
	protected String removeAll; //replaceAll로 지울 내용.
	protected String validation; //검증 함수. 통과못하면 데이터수집 안함.
	protected String headerValidation; //검증 함수. 통과못해도 진행함. 단 pk값이 신규일때 기록하고, old이면 다음 게시물로 진행.
	protected String args; //validation에서 사용되는 파라미터
	protected String dependOn; //의존필드 값이 존재하면 진행하고 의존필드값이 없으면, 빈값을 적용한다.
	protected boolean multiNode; //다중 값을 가지는지.
	//
	// 간단히 적용할수 있는 기 정의된 함수들
	//
	protected boolean linkPage; //true이면 링크로 인식해서 페이지소스를 가져와서 적용한다.
	protected String linkMethod = LINK_METHOD_GET; //링크를 탐색해서 데이터를 가져올때 method를 get를 사용할지 post를 사용할지 결정.
	protected boolean parseJavascript;//parseJavascript = true이면, 파라미터를 파싱해서 ${0} 와 같이 value에서 사용할수 있다.
	protected boolean parseUrl; //parseUrl = true이면, 파라미터를 파싱해서 ${파라미터명}을 value에서 사용할수 있다.
	protected String datetimeParseFormat; // 데이터를 읽어들일 날짜포맷. 
	protected String datetimeFormat; //날짜데이터를 출력할 포맷. null이면 yyyy-MM-dd HH:mm:ss로 변환한다.
	protected String format; //숫자출력포맷.
	protected String prefix; // 결과내용에 붙여줄 prefix
	protected String suffix; //결과내용에 붙여줄 suffix
	protected String dateLocale;
	protected String fileNameEncoding;
	protected boolean tagRemove; //결과데이터의 html태그를 제거할지 여부.
	protected boolean tagRemove2; //본문 내용중에 태그를 <p>제목</p> 과 같이 적지 않고, &lt;제목&lt/p>과 같이 적을 경우, 태그가 남아있게 된다. 이런 내용까지도 제거하기 위함. 
	protected boolean multiLines; //수집시 원본데이터의 개행문자(줄바꿈 or <br>태그)를 유지할지 여부. true시 br태그는 \n으로 치환되며, 여러개가 중복되어 출현시 한개만 남는다.
	private Set<String> tagWhiteList; //여기에 허용된 태그는 제거하지 않는다.
	protected SimpleDateFormat datetimeFormatParser; //날짜파서
	protected SimpleDateFormat datetimeFormatter; //날짜 포맷터
	protected Formatter formatter;
	protected boolean isFile; //필드가 파일url이라면 다운로드 받도록한다. 
	protected String fileName; //다운로드 파일명
	protected String fileCopyToPath; //다운로드 파일의 저장위치.
	protected boolean resourcePathRewrite; //img 와 같이 경로를 포함한 태그의 경우 경로를 외부에서 접근가능토록 절대경로로 재 가공한다.
	protected boolean setCurrentTimeWhenNULL;
	protected boolean jsessionRemove;
	protected String replaceAll; //모든 문장을 바꾼다.(정규식)
	protected String replaceTo; //바꿀 문장.
	private boolean setDataWithPrevValueWhenNull; //값이 널일때 바로 이전에 수집한 데이터를 넣어준다. 
	protected boolean cvtGMT2Local;
	protected boolean checkDuplicateTitle;
	protected int maxLength;
	private boolean isSequencial;
	
	public Block(Element blockEl) {
		fill(blockEl);
	}
	
	public void fill(Element node){
		String nodeName = node.getName();
		if ("field".equals(nodeName)) {
			isField = true;
		}
		
		if ( "document".equals(nodeName) )
			isDocument = true;
		
		String id = node.getAttributeValue("id");
		if(id!=null && !"".equals(id)) { this.id = id; }
		
		String pk = node.getAttributeValue("pk");
		if(pk!=null && "true".equals(pk)) { 
			this.isPk = true;
		}
		String setNow = node.getAttributeValue("setCurrentTimeWhenNULL");
		if ( setNow!=null && "true".equals(setNow) )
			this.setCurrentTimeWhenNULL = true;
		else
			this.setCurrentTimeWhenNULL = false;
		
		String strGMT2Local = node.getAttributeValue("setGMT2Local");
		if ( strGMT2Local != null && "true".equalsIgnoreCase(strGMT2Local) )
			this.cvtGMT2Local = true;
		else
			this.cvtGMT2Local = false;
		
		String v = node.getAttributeValue("setDataWithPrevValueWhenNull");
		if ( v!=null && "true".equals(v))
			this.setDataWithPrevValueWhenNull = true;
		else
			this.setDataWithPrevValueWhenNull = false;
		
		String number = node.getAttributeValue("number");
		if(number!=null && "true".equals(number)) { 
			this.isNumeric = true;
		}
		
		String jSession = node.getAttributeValue("jsessionRemove");
		if ( jSession != null && "true".equals(jSession) ){
			this.jsessionRemove = true;
		}
		else
			jsessionRemove = false;			
		
		String xpath = node.getAttributeValue("xpath");
		if(xpath!=null && !"".equals(xpath)) { this.xpath = xpath; }
		
		String selector = node.getAttributeValue("selector");
		if(selector!=null && !"".equals(selector)) { this.selector = selector; }
		
		String regexp = node.getAttributeValue("regexp");
		if(regexp!=null && !"".equals(regexp)) { this.regexp = regexp; }
		
		String json = node.getAttributeValue("json");
		if(json!=null && !"".equals(json)) { this.json = json; }
		
		//value는 빈문자도 허용한다.
		String value = node.getAttributeValue("value");
		if(value!=null) { this.value = value; }
		
		String modify = node.getAttributeValue("modify");
		if(modify!=null && !"".equals(modify)) { this.modify = modify; }
		
		String linkPage = node.getAttributeValue("linkPage");
		if(linkPage!=null && "true".equals(linkPage)) { this.linkPage = true; }
		
		String parseJavascript = node.getAttributeValue("parseJavascript");
		if(parseJavascript!=null && "true".equals(parseJavascript)) { this.parseJavascript = true; }
		
		String parseUrl = node.getAttributeValue("parseUrl");
		if(parseUrl!=null && "true".equals(parseUrl)) { this.parseUrl = true; }
		
		String linkMethod = node.getAttributeValue("linkMethod");
		if(linkMethod!=null && !"".equals(linkMethod)) { this.linkMethod = linkMethod; }
		
		String dateLocale = node.getAttributeValue("dateLocale");
		if (dateLocale !=null && !"".equals(dateLocale)) {this.dateLocale = dateLocale;}
		else
			this.dateLocale = "Locale.KOREA";
		
		String dtParseFormat = node.getAttributeValue("datetimeParseFormat");
		if(dtParseFormat!=null && !"".equals(dtParseFormat)) { 
			datetimeParseFormat = dtParseFormat;
			if ( this.dateLocale.equals("Locale.KOREA") || this.dateLocale == null 	)
				datetimeFormatParser = new SimpleDateFormat(datetimeParseFormat);
			else if ( this.dateLocale.equals("Locale.US") )
				datetimeFormatParser = new SimpleDateFormat(datetimeParseFormat, Locale.US);
		}
		
		String dtFormat = node.getAttributeValue("datetimeFormat");
		if(dtFormat!=null && !"".equals(dtFormat)) { 
			//파싱후 출력포맷을 결정한다.
			this.datetimeFormat = dtFormat;
			datetimeFormatter = new SimpleDateFormat(datetimeFormat);
		}
		
		String format = node.getAttributeValue("format");
		if(format!=null && !"".equals(format)) { 
			//파싱후 출력포맷을 결정한다.
			this.format = format;
			try {
				formatter = new Formatter(format);
			} catch (Exception e) { }
		}
		
		String prefix = node.getAttributeValue("prefix");
		if(prefix!=null && !"".equals(prefix)) { this.prefix = prefix; }
		
		String suffix = node.getAttributeValue("suffix");
		if(suffix!=null && !"".equals(suffix)) { this.suffix = suffix; }		
		
		String multiLines = node.getAttributeValue("multiLines");
		if(multiLines!=null && "true".equals(multiLines)) { this.multiLines = true; }
		
		String tagRemove = node.getAttributeValue("tagRemove");
		if(tagRemove!=null && "true".equals(tagRemove)) { this.tagRemove = true; }
		
		String tagRemove2 = node.getAttributeValue("tagRemove2");
		if(tagRemove2!=null && "true".equals(tagRemove2)) { this.tagRemove2 = true; }
		
		String tagWhiteList = node.getAttributeValue("tagWhiteList");
		if(tagWhiteList!=null && !"".equals(tagWhiteList)) { 
			this.tagWhiteList = new HashSet<String>();
			String[] list = tagWhiteList.split(",");
			for (int i = 0; i < list.length; i++) {
				String tag = list[i].trim();
				this.tagWhiteList.add(tag);
			}
		}
		
		String resourcePathRewrite = node.getAttributeValue("resourcePathRewrite");
		if(resourcePathRewrite!=null && "true".equals(resourcePathRewrite)) { this.resourcePathRewrite = true; }
		
		String removeAll = node.getAttributeValue("removeAll");
		if(removeAll!=null && !"".equals(removeAll)) { this.removeAll = removeAll; }
		
		String file = node.getAttributeValue("file");
		if(file!=null && "true".equals(file)) { 
			this.isFile = true;
			//파일형식일 경우에만 파일명과 파일경로 속성을 확인한다.
			String fileName = node.getAttributeValue("fileName");
			if(fileName!=null && !"".equals(fileName)) { this.fileName = fileName; }
			String copyTo = node.getAttributeValue("copyTo");
			if(copyTo!=null && !"".equals(copyTo)) { this.fileCopyToPath = copyTo; }
		}
		
		String fileNameEncoding = node.getAttributeValue("fileNameEncoding");
		if ( fileNameEncoding != null && !"".equals(fileNameEncoding))	{this.fileNameEncoding = fileNameEncoding;}
		
		String validation = node.getAttributeValue("validation");
		if(validation!=null && !"".equals(validation)) { this.validation = validation; }
		//headerValidation
		String headerValidation = node.getAttributeValue("headerValidation");
		if(headerValidation!=null && !"".equals(headerValidation)) { this.headerValidation = headerValidation; }
		
		String args = node.getAttributeValue("args");
		if(args!=null && !"".equals(args)) { this.args = args; }
		
		String dependOn = node.getAttributeValue("dependOn");
		if(dependOn!=null && !"".equals(dependOn)) { this.dependOn = dependOn; }
		
		String multiNode = node.getAttributeValue("multiNode");
		if(multiNode!=null && "true".equals(multiNode)) { this.multiNode = true; }
		
		String rplAll = node.getAttributeValue("replaceAll");
		if ( rplAll != null && rplAll.trim().length() > 0)
			replaceAll = rplAll.trim();
		
		
		String rplTo = node.getAttributeValue("replaceTo");
		if ( rplTo != null )
			replaceTo = rplTo.trim();	
		
		//	children
		List<Element> childrenList = (List<Element>)node.getChildren();
		if(childrenList != null) {
			for(Element childEl : childrenList) {
				children.add(new Block(childEl));
			}
		}
		
		this.checkDuplicateTitle = "true".equals(node.getAttributeValue("checkDuplicateTitle"));
		this.maxLength = -1;
		try {
			String thisValue = node.getAttributeValue("maxLength");
			if(thisValue != null && !"".equals(thisValue)) {
				maxLength = Integer.parseInt(thisValue);
			}
		} catch (NumberFormatException ignore) { }
		
		this.isSequencial = "true".equals(node.getAttributeValue("sequencial"));
	}
	
	public boolean isPk(){
		return isPk;
	}
	
	//id type xpath json value children modify linkPage datetimeFormat  prefix suffix
	public String getId() {
		return id;
	}
	public String getXpath() {
		return xpath;
	}
	public String getSelector() {
		return selector;
	}
	public String getJson() {
		return json;
	}
	public String getValue() {
		return value;
	}
	public boolean isNumeric() {
		return isNumeric;
	}
	public List<Block> getChildren() {
		return children;
	}
	public boolean isField() {
		return isField;
	}
	public String getModify() {
		return modify;
	}
	public boolean isLinkPage() {
		return linkPage;
	}
	public boolean parseJavascript(){
		return parseJavascript;
	}
	public boolean parseUrl(){
		return parseUrl;
	}
	public String getLinkMethod() {
		return linkMethod;
	}
	public String getNumberFormat() {
		return format;
	}
	public String getDatetimeFormat() {
		return datetimeFormat;
	}
	public String getDatetimeParseFormat() {
		return datetimeParseFormat;
	}
	public SimpleDateFormat getDatetimeFormatParser() {
		return datetimeFormatParser;
	}
	public SimpleDateFormat getDatetimeFormatter() {
		return datetimeFormatter;
	}
	public String getPrefix() {
		return prefix;
	}
	public String getSuffix() {
		return suffix;
	}
	public boolean isMultiLines() {
		return multiLines;
	}
	public String getRegexp() {
		return regexp;
	}

	public boolean tagRemove() {
		return tagRemove;
	}
	
	public boolean tagRemove2() {
		return tagRemove2;
	}
	
	public String getRemoveAll() {
		return removeAll;
	}
	
	public String getReplaceAll() {
		return replaceAll;
	}
	
	public String getReplaceTo() {
		return replaceTo;
	}
	
	public String getValidation() {
		return validation;
	}
	
	public String getHeaderValidation() {
		return headerValidation;
	}
	
	public String getArgs() {
		return args;
	}
	
	public String getDependOn() {
		return dependOn;
	}

	public boolean isFile() {
		return isFile;
	}
	
	public String getFileName() {
		return fileName;
	}
	
	public String getCopyTo() {
		return fileCopyToPath;
	}
	
	public String getfileNameEncoding() {
		return fileNameEncoding;
	}

	public Set<String> getTagWhiteList() {
		return tagWhiteList;
	}

	public boolean isResourcePathRewrite() {
		return resourcePathRewrite;
	}

	public boolean isMultiNode() {
		return multiNode;
	}

	public boolean isSetCurrentTimeWhenNULL() {
		return setCurrentTimeWhenNULL;
	}
	
	public boolean isJsessionRemove() {
		return jsessionRemove;
	}

	public boolean isSetDataWithPrevValueWhenNull() {
		return setDataWithPrevValueWhenNull;
	}
	
	public boolean isConvertGMT2Local() {
		return cvtGMT2Local;
	}

	public boolean checkDuplicateTitle() {
		return checkDuplicateTitle;
	}
	
	public int getMaxLength() {
		return maxLength;
	}

	public boolean isSequencial() {
		return isSequencial;
	}
}
