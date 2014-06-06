package org.apache.jsp.collection;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;
import com.websqrd.fastcat.ir.search.CollectionHandler;
import com.websqrd.fastcat.service.IRService;
import com.websqrd.fastcat.ir.config.IRSettings;
import com.websqrd.fastcat.ir.config.Schema;
import com.websqrd.fastcat.ir.config.IRConfig;
import com.websqrd.fastcat.ir.config.DataSourceSetting;
import com.websqrd.libs.common.Formatter;
import com.websqrd.fastcat.web.WebUtils;
import java.util.Date;
import java.util.Calendar;
import java.net.URLDecoder;
import com.websqrd.catbot.setting.CatbotSettings;
import java.net.URLEncoder;
import com.websqrd.catbot.web.WebUtils;
import com.websqrd.catbot.setting.CatbotSettings;

public final class main_jsp extends org.apache.jasper.runtime.HttpJspBase
    implements org.apache.jasper.runtime.JspSourceDependent {

  private static java.util.List _jspx_dependants;

  static {
    _jspx_dependants = new java.util.ArrayList(4);
    _jspx_dependants.add("/collection/../common.jsp");
    _jspx_dependants.add("/collection/../webroot.jsp");
    _jspx_dependants.add("/collection/../header.jsp");
    _jspx_dependants.add("/collection/../footer.jsp");
  }

  public Object getDependants() {
    return _jspx_dependants;
  }

  public void _jspService(HttpServletRequest request, HttpServletResponse response)
        throws java.io.IOException, ServletException {

    JspFactory _jspxFactory = null;
    PageContext pageContext = null;
    HttpSession session = null;
    ServletContext application = null;
    ServletConfig config = null;
    JspWriter out = null;
    Object page = this;
    JspWriter _jspx_out = null;
    PageContext _jspx_page_context = null;


    try {
      _jspxFactory = JspFactory.getDefaultFactory();
      response.setContentType("text/html; charset=UTF-8");
      pageContext = _jspxFactory.getPageContext(this, request, response,
      			null, true, 8192, true);
      _jspx_page_context = pageContext;
      application = pageContext.getServletContext();
      config = pageContext.getServletConfig();
      session = pageContext.getSession();
      out = pageContext.getOut();
      _jspx_out = out;

      out.write("\r\n");
      out.write("\r\n");
      out.write(" \r\n");
      out.write("\r\n");
      out.write("\r\n");
      out.write("\r\n");
      out.write("\r\n");
      out.write("\r\n");
      out.write("\r\n");
      out.write("\r\n");
      out.write("\r\n");
      out.write("\r\n");
      out.write("\r\n");
      out.write("\r\n");
      out.write("\r\n");
      out.write("\r\n");
      out.write("\r\n");
      out.write("\r\n");
      out.write("\r\n");
      out.write("\r\n");
      out.write("\r\n");
      out.write("\r\n");
      out.write("\r\n");
      out.write("\n");
      out.write("\n");
      out.write(" \n");

	String CATBOT_MANAGE_ROOT = request.getContextPath() + "/";
	{
		if(application.getAttribute("CATBOT_MANAGE_ROOT")!=null) {
			CATBOT_MANAGE_ROOT = (String)application.getAttribute("CATBOT_MANAGE_ROOT")+"/";
		}
	}

      out.write('\n');
      out.write('\r');
      out.write('\n');

	request.setCharacterEncoding("UTF-8"); 

      out.write("\r\n");
      out.write("\r\n");

	if (CatbotSettings.isAuthUsed() && session.getAttribute("authorized") == null) {
		response.sendRedirect(CATBOT_MANAGE_ROOT+"index.jsp?message="+URLEncoder.encode(URLEncoder.encode("로그인이 필요합니다.", "utf-8"),"utf-8"));
		return;
	}

      out.write('\r');
      out.write('\n');
      out.write("\r\n");
      out.write("\r\n");

	String message = URLDecoder.decode(WebUtils.getString(request.getParameter("message"), ""),"utf-8");

	BoardService irService = BoardService.getInstance();
	IRConfig irConfig = IRSettings.getConfig(true);
	String collectinListStr = irConfig.getString("collection.list");
	String[] colletionList = collectinListStr.split(",");

      out.write("\r\n");
      out.write("\r\n");
      out.write("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\r\n");
      out.write("<html xmlns=\"http://www.w3.org/1999/xhtml\">\r\n");
      out.write("<head>\r\n");
      out.write("<title>FASTCAT 검색엔진 관리도구</title>\r\n");
      out.write("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />\r\n");
      out.write("<link href=\"");
      out.print(FASTCAT_MANAGE_ROOT);
      out.write("css/reset.css\" rel=\"stylesheet\" type=\"text/css\" />\r\n");
      out.write("<link href=\"");
      out.print(FASTCAT_MANAGE_ROOT);
      out.write("css/style.css\" rel=\"stylesheet\" type=\"text/css\" />\r\n");
      out.write("<!--[if lte IE 6]>\r\n");
      out.write("<link href=\"");
      out.print(FASTCAT_MANAGE_ROOT);
      out.write("css/style_ie.css\" rel=\"stylesheet\" type=\"text/css\" />\r\n");
      out.write("<![endif]-->\r\n");
      out.write("\t<script src=\"");
      out.print(FASTCAT_MANAGE_ROOT);
      out.write("js/jquery-1.4.4.min.js\" type=\"text/javascript\"></script>\r\n");
      out.write("\t<script src=\"");
      out.print(FASTCAT_MANAGE_ROOT);
      out.write("js/jquery.validate.min.js\" type=\"text/javascript\"></script>\r\n");
      out.write("\t<script src=\"");
      out.print(FASTCAT_MANAGE_ROOT);
      out.write("js/validate.messages_ko.js\" type=\"text/javascript\"></script>\r\n");
      out.write("\t<script src=\"");
      out.print(FASTCAT_MANAGE_ROOT);
      out.write("js/validate.additional.js\" type=\"text/javascript\"></script>\r\n");
      out.write("\t<script type=\"text/javascript\">\r\n");
      out.write("\t$(document).ready(function() {\r\n");
      out.write("\t\t$(\"#collectionForm\").validate({\r\n");
      out.write("\t\t\terrorClass : \"invalidValue\",\r\n");
      out.write("\t\t\twrapper : \"li\",\r\n");
      out.write("\t\t\terrorLabelContainer: \"#messageBox\",\r\n");
      out.write("\t\t\tsubmitHandler: function(form) {\r\n");
      out.write("\t\t\t\tform.submit();\r\n");
      out.write("\t\t\t\treturn true;\r\n");
      out.write("\t\t\t}\r\n");
      out.write("\t\t});\r\n");
      out.write("\t});\r\n");
      out.write("\t\r\n");
      out.write("\tfunction alertMessage(){\r\n");
      out.write("\t\tvar message = \"");
      out.print(message );
      out.write("\";\r\n");
      out.write("\t\tif(message != \"\")\r\n");
      out.write("\t\t\talert(message);\r\n");
      out.write("\t}\r\n");
      out.write("\r\n");
      out.write("\tfunction addCollection(){\r\n");
      out.write("\t\t$(\"#cmd\").val(\"2\");\r\n");
      out.write("\t\t$(\"#collectionForm\").submit();\r\n");
      out.write("\t}\r\n");
      out.write("\r\n");
      out.write("\tfunction removeCollection(){\r\n");
      out.write("\t\tvar x = document.getElementsByName(\"selectCollection\");\r\n");
      out.write("\t\tcollection = \"\";\r\n");
      out.write("\t\tfor(i=0;i<x.length;i++){\r\n");
      out.write("\t\t\tif(x[i].checked){\r\n");
      out.write("\t\t\t\tcollection = x[i].value;\r\n");
      out.write("\t\t\t\tbreak;\r\n");
      out.write("\t\t\t}\r\n");
      out.write("\t\t}\r\n");
      out.write("\r\n");
      out.write("        if(collection == \"\")\r\n");
      out.write("            alert(\"삭제할 컬렉션을 선택해주세요.\");\r\n");
      out.write("        else\r\n");
      out.write("\t\t\tlocation.href = \"collectionService.jsp?cmd=3&collection=\"+collection;\r\n");
      out.write("\t\t\r\n");
      out.write("\t}\r\n");
      out.write("\t</script>\r\n");
      out.write("</head>\r\n");
      out.write("\r\n");
      out.write("<body onload=\"alertMessage()\">\r\n");
      out.write("<div id=\"container\">\r\n");
      out.write("<!-- header -->\r\n");
      out.write("\r\n");
      out.write("\r\n");
      out.write(" \r\n");
      out.write("\r\n");
      out.write("\r\n");

Object authObj = session.getAttribute("authorized");
boolean isAuthorized = (authObj != null) ? true : false;
Object accessLogObj = session.getAttribute("lastAccessLog");
String[] accessLog = null;
if(accessLogObj != null){
	accessLog = (String[])accessLogObj;
}

if(!CatbotSettings.isAuthUsed()){
	isAuthorized = true;
	authObj = "익명사용자";
	accessLog = new String[]{"",""};
}

      out.write("\r\n");
      out.write("\r\n");
 { 
      out.write('\r');
      out.write('\n');
 String __CATBOT_MANAGE_ROOT__ = (String)application.getAttribute("CATBOT_MANAGE_ROOT")+"/"; 
      out.write("\r\n");
      out.write("<div id=\"header\">\r\n");
      out.write("\t<div><img src=\"");
      out.print(__CATBOT_MANAGE_ROOT__);
      out.write("images/fastcatsearch-logo.gif\" /></div>\r\n");
      out.write("\t<div id=\"loginbar\">\r\n");
      out.write("\t\t");
 if(isAuthorized) { 
      out.write("\r\n");
      out.write("\t\t<!-- 로그온시 정보 -->\r\n");
      out.write("\t\t<div id=\"login_box\">\r\n");
      out.write("\t\t\t<p class=\"logname\">");
      out.print((String)authObj );
      out.write("님 <input type=\"button\" class=\"btn\" value=\"로그아웃\" onclick=\"javascript:logout()\"/></p>\r\n");
      out.write("\t\t</div>\r\n");
      out.write("\t\t");
 } 
      out.write("\r\n");
      out.write("\t</div>\r\n");
      out.write("\t<!-- GNB -->\r\n");
      out.write("\t<div class=\"menucontainer\">\r\n");
      out.write("\t\t<div class=\"menu\">\r\n");
      out.write("\t\t<ul>\r\n");
      out.write("\t\t\t<li><a href=\"");
      out.print(__CATBOT_MANAGE_ROOT__);
      out.write("main.jsp\">홈</a></li>\r\n");
      out.write("\t\t\t<li><a href=\"");
      out.print(__CATBOT_MANAGE_ROOT__);
      out.write("collection/main.jsp\">사이트</a>\r\n");
      out.write("\t\t\t\t<table><tr><td>\r\n");
      out.write("\t\t\t\t\t<ul>\r\n");
      out.write("\t\t\t\t\t\t<li><a href=\"");
      out.print(__CATBOT_MANAGE_ROOT__);
      out.write("collection/main.jsp\">사이트정보</a></li>\r\n");
      out.write("\t\t\t\t\t\t<li><a href=\"");
      out.print(__CATBOT_MANAGE_ROOT__);
      out.write("collection/schema.jsp\">사이트설정</a></li>\r\n");
      out.write("\t\t\t\t\t</ul>\r\n");
      out.write("\t\t\t\t</td></tr></table>\r\n");
      out.write("\t\t\t</li>\r\n");
      out.write("\t\t\t<li><a class=\"drop\" href=\"");
      out.print(__CATBOT_MANAGE_ROOT__);
      out.write("indexing/main.jsp\">수집</a>\r\n");
      out.write("\t\t\t\t<table><tr><td>\r\n");
      out.write("\t\t\t\t\t<ul>\r\n");
      out.write("\t\t\t\t\t\t<li><a href=\"");
      out.print(__CATBOT_MANAGE_ROOT__);
      out.write("crawling/action.jsp\">수집관리</a></li>\r\n");
      out.write("\t\t\t\t\t\t<li><a href=\"");
      out.print(__CATBOT_MANAGE_ROOT__);
      out.write("crawling/schedule.jsp\">수집주기설정</a></li>\r\n");
      out.write("\t\t\t\t\t\t<li><a href=\"");
      out.print(__CATBOT_MANAGE_ROOT__);
      out.write("crawling/history.jsp\">수집히스토리</a></li>\r\n");
      out.write("\t\t\t\t\t</ul>\r\n");
      out.write("\t\t\t\t</td></tr></table>\r\n");
      out.write("\t\t\t</li>\r\n");
      out.write("\t\t\t<li><a href=\"");
      out.print(__CATBOT_MANAGE_ROOT__);
      out.write("management/main.jsp\">관리</a>\r\n");
      out.write("\t\t\t\t<table><tr><td>\r\n");
      out.write("\t\t\t\t\t<ul>\r\n");
      out.write("\t\t\t\t\t\t<li><a href=\"");
      out.print(__CATBOT_MANAGE_ROOT__);
      out.write("management/account.jsp\">계정관리</a></li>\r\n");
      out.write("\t\t\t\t\t\t<li><a href=\"");
      out.print(__CATBOT_MANAGE_ROOT__);
      out.write("management/config.jsp\">사용자설정</a></li>\r\n");
      out.write("\t\t\t\t\t\t<li><a href=\"");
      out.print(__CATBOT_MANAGE_ROOT__);
      out.write("management/advConfig.jsp\">고급설정보기</a></li>\r\n");
      out.write("\t\t\t\t\t</ul>\r\n");
      out.write("\t\t\t\t</td></tr></table>\r\n");
      out.write("\t\t\t</li>\r\n");
      out.write("\t\t</ul>\r\n");
      out.write("\t\t</div><!-- // E : menu -->\r\n");
      out.write("\t</div><!-- // E : menucontainer -->\r\n");
      out.write("\t<script type=\"text/javascript\">\r\n");
      out.write("\t\tfunction logout(){\r\n");
      out.write("\t\t\tlocation.href=\"");
      out.print(__CATBOT_MANAGE_ROOT__);
      out.write("index.jsp?cmd=logout\";\r\n");
      out.write("\t\t}\r\n");
      out.write("\t</script>\r\n");
      out.write("</div><!-- // E : #header -->\r\n");
 } 
      out.write('\r');
      out.write('\n');
      out.write("\r\n");
      out.write("\r\n");
      out.write("<div id=\"sidebar\">\r\n");
      out.write("\t<div class=\"sidebox\">\r\n");
      out.write("\t\t<h3>컬렉션</h3>\r\n");
      out.write("\t\t\t<ul class=\"latest\">\r\n");
      out.write("\t\t\t<li><a href=\"");
      out.print(FASTCAT_MANAGE_ROOT);
      out.write("collection/main.jsp\" class=\"selected\">컬렉션정보</a></li>\r\n");
      out.write("\t\t\t<li><a href=\"");
      out.print(FASTCAT_MANAGE_ROOT);
      out.write("collection/schema.jsp\">스키마설정</a></li>\r\n");
      out.write("\t\t\t<li><a href=\"");
      out.print(FASTCAT_MANAGE_ROOT);
      out.write("collection/datasource.jsp\">데이터소스설정</a></li>\r\n");
      out.write("\t\t\t</ul>\r\n");
      out.write("\t</div>\r\n");
      out.write("\t<div class=\"sidebox\">\r\n");
      out.write("\t\t<h3>컬렉션</h3>\r\n");
      out.write("\t\t\t<ul class=\"latest\">\r\n");
      out.write("\t\t\t<li>각 컬렉션에 대한 정보입니다.</li>\r\n");
      out.write("\t\t\t<li>데이터소스타입 : 수집파일로 부터 색인을 할 경우 FILE, DB로 데이터부터 색인을 할 경우 DB 선택.</li>\r\n");
      out.write("\t\t\t<li>실행 : 각 컬렉션별로 서비스 여부를 선택할 수 있다.</li>\r\n");
      out.write("\t\t\t<li>컬렉션을 추가할 때는 컬렉션이름을 기입하고 추가버튼을 누른다.</li>\r\n");
      out.write("\t\t\t<li>컬렉션을 삭제할 때는 해당 컬렉션의 선택버튼을 클릭하고 삭제버튼을 누른다.</li>\r\n");
      out.write("\t\t\t</ul>\r\n");
      out.write("\t</div>\r\n");
      out.write("</div><!-- E : #sidebar -->\r\n");
      out.write("\r\n");
      out.write("<div id=\"mainContent\">\r\n");
      out.write("\t<h2>컬렉션 정보</h2>\r\n");
      out.write("\t<div class=\"fbox\">\r\n");
      out.write("\t<table summary=\"컬렉션 정보\" class=\"tbl02\">\r\n");
      out.write("\t<colgroup><col width=\"5%\" /><col width=\"5%\" /><col width=\"10%\" /><col width=\"8%\" /><col width=\"8%\" /><col width=\"8%\" /><col width=\"8%\" /><col width=\"8%\" /><col width=\"8%\" /><col width=\"7%\" /><col width=\"8%\" /><col width=\"8%\" /></colgroup>\r\n");
      out.write("\t<thead>\r\n");
      out.write("\t<tr>\r\n");
      out.write("\t\t<th rowspan = \"2\" class=\"first\">&nbsp;</th>\r\n");
      out.write("\t\t<th rowspan = \"2\">No.</th>\r\n");
      out.write("\t\t<th rowspan=\"2\">컬렉션명</th>\r\n");
      out.write("\t\t<th colspan=\"5\">필드갯수</th>\r\n");
      out.write("\t\t<th rowspan=\"2\">데이터<br />\r\n");
      out.write("\t\t\t소스타입</th>\r\n");
      out.write("\t\t<th rowspan=\"2\">상태</th>\r\n");
      out.write("\t\t<th rowspan=\"2\">실행시간</th>\r\n");
      out.write("\t\t<th rowspan=\"2\">실행</th>\r\n");
      out.write("\t</tr>\r\n");
      out.write("\t<tr>\r\n");
      out.write("\t\t<th>총필드수</th>\r\n");
      out.write("\t\t<th>색인필드수</th>\r\n");
      out.write("\t\t<th>필터필드수</th>\r\n");
      out.write("\t\t<th>그룹필드수</th>\r\n");
      out.write("\t\t<th>정렬필드수</th>\r\n");
      out.write("\t</tr>\r\n");
      out.write("\t</thead>\r\n");
      out.write("\t<tbody>\r\n");
      out.write("\t");

	for(int i = 0;i<colletionList.length;i++){
		String collection = colletionList[i];
		CollectionHandler collectionHandler = irService.getCollectionHandler(collection);
		boolean isRunning = false;
		String startTimeStr = "";
		String durationStr = "";
		if(collectionHandler == null){
			isRunning = false;
		}else{
			isRunning = true;
			long startTime = collectionHandler.getStartedTime();
			long duration  = System.currentTimeMillis() - startTime;
			startTimeStr = new Date(startTime).toString();
			durationStr = Formatter.getFormatTime(duration);
		}
		
		Schema schema = IRSettings.getSchema(collection, true);
		DataSourceSetting dataSourceSetting = IRSettings.getDatasource(collection, true);
		String sourceType = dataSourceSetting.sourceType;

      out.write("\r\n");
      out.write("\t<tr>\r\n");
      out.write("\t\t<td class=\"first\"><input type=\"radio\" name=\"selectCollection\" value=\"");
      out.print(collection);
      out.write("\" /></td>\r\n");
      out.write("\t\t<td>");
      out.print(i+1 );
      out.write("</td>\r\n");
      out.write("\t\t<td><a href=\"schema.jsp?collection=");
      out.print(collection);
      out.write("\"><strong class=\"small tb\">");
      out.print(collection);
      out.write("</strong></a></td>\r\n");
      out.write("\t\t<td>");
      out.print(schema.getFieldSettingList().size());
      out.write("</td>\r\n");
      out.write("\t\t<td>");
      out.print(schema.getIndexSettingList().size());
      out.write("</td>\r\n");
      out.write("\t\t<td>");
      out.print(schema.getFilterSettingList().size());
      out.write("</td>\r\n");
      out.write("\t\t<td>");
      out.print(schema.getGroupSettingList().size());
      out.write("</td>\r\n");
      out.write("\t\t<td>");
      out.print(schema.getSortSettingList().size());
      out.write("</td>\r\n");
      out.write("\t\t<td><a href=\"datasource.jsp?collection=");
      out.print(collection);
      out.write('"');
      out.write('>');
      out.print(sourceType);
      out.write("</a></td>\r\n");
      out.write("\t\t<td>");
      out.print(isRunning ? "실행중" : "정지");
      out.write("</td>\r\n");
      out.write("\t\t<td>");
      out.print(durationStr);
      out.write("&nbsp;</td>\r\n");
      out.write("\t\t<td>");

		if(isRunning){
			
      out.write("\r\n");
      out.write("\t\t\t<a class=\"btn_s\" href=\"collectionService.jsp?cmd=0&collection=");
      out.print(collection);
      out.write("\">정지</a>\r\n");
      out.write("\t\t\t");

		}else{
			
      out.write("\r\n");
      out.write("\t\t\t<a class=\"btn_s\" href=\"collectionService.jsp?cmd=1&collection=");
      out.print(collection);
      out.write("\">시작</a>\r\n");
      out.write("\t\t\t");
	
		}
		
      out.write("\r\n");
      out.write("\t\t</td>\r\n");
      out.write("\t</tr>\r\n");

	}

      out.write("\r\n");
      out.write("\t</tbody>\r\n");
      out.write("\t</table>\r\n");
      out.write("\t</div>\r\n");
      out.write("\r\n");
      out.write("\t<div id=\"btnBox\">\r\n");
      out.write("\t<form id=\"collectionForm\" method=\"get\" action=\"collectionService.jsp\">\r\n");
      out.write("\t<input type=\"hidden\" id=\"cmd\" name=\"cmd\" />\r\n");
      out.write("\t<input type=\"text\" id=\"collection\" name=\"collection\" class=\"inp02 required alphanumeric\" size=\"20\" minlength=\"2\" maxlength=\"20\"></input> \r\n");
      out.write("\t<a class=\"btn\" onclick=\"javascript:addCollection()\">컬렉션추가</a>\r\n");
      out.write("\t<a class=\"btn\" onclick=\"javascript:removeCollection()\">컬렉션삭제</a>\r\n");
      out.write("\t<div id=\"messageBox\" style=\"width:400px; margin-left:220px; text-align: left;\"> </div>\r\n");
      out.write("\t</form>\r\n");
      out.write("\t</div>\r\n");
      out.write("\t\r\n");
      out.write("\t<!-- E : #mainContent --></div>\r\n");
      out.write("\r\n");
      out.write("<!-- footer -->\r\n");
      out.write("\r\n");
      out.write("\r\n");
      out.write(" \r\n");
      out.write("\r\n");
      out.write("<div id=\"footer\">\r\n");
      out.write("\t<p class=\"copy\">Enterprise CrawlingEngine, CatBot</p>\r\n");
      out.write("<!-- E : #footer --></div>\r\n");
      out.write("\r\n");
      out.write("\t\r\n");
      out.write("</div><!-- //E : #container -->\r\n");
      out.write("\r\n");
      out.write("</body>\r\n");
      out.write("\r\n");
      out.write("</html>\r\n");
    } catch (Throwable t) {
      if (!(t instanceof SkipPageException)){
        out = _jspx_out;
        if (out != null && out.getBufferSize() != 0)
          out.clearBuffer();
        if (_jspx_page_context != null) _jspx_page_context.handlePageException(t);
      }
    } finally {
      if (_jspxFactory != null) _jspxFactory.releasePageContext(_jspx_page_context);
    }
  }
}
