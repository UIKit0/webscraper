package org.apache.jsp.crawling;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;
import com.websqrd.libs.common.Formatter;
import com.websqrd.catbot.setting.CatbotSettings;
import com.websqrd.catbot.setting.CatbotServerConfig;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Date;
import java.util.Calendar;
import java.net.URLDecoder;
import com.websqrd.catbot.setting.CatbotSettings;
import java.net.URLEncoder;
import com.websqrd.catbot.web.WebUtils;
import com.websqrd.catbot.setting.CatbotSettings;

public final class action_jsp extends org.apache.jasper.runtime.HttpJspBase
    implements org.apache.jasper.runtime.JspSourceDependent {

  private static java.util.List _jspx_dependants;

  static {
    _jspx_dependants = new java.util.ArrayList(4);
    _jspx_dependants.add("/crawling/../common.jsp");
    _jspx_dependants.add("/crawling/../webroot.jsp");
    _jspx_dependants.add("/crawling/../header.jsp");
    _jspx_dependants.add("/crawling/../footer.jsp");
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

	CatbotServerConfig irConfig = CatbotSettings.getConfig(true);
	String siteListStr = irConfig.getString("site.list");
	String[] siteList = siteListStr.split(",");
	

      out.write("\r\n");
      out.write("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\r\n");
      out.write("<html xmlns=\"http://www.w3.org/1999/xhtml\" />\r\n");
      out.write("<head>\r\n");
      out.write("<title>CATBOT 관리도구</title>\r\n");
      out.write("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />\r\n");
      out.write("<link href=\"");
      out.print(CATBOT_MANAGE_ROOT);
      out.write("css/reset.css\" rel=\"stylesheet\" type=\"text/css\" />\r\n");
      out.write("<link href=\"");
      out.print(CATBOT_MANAGE_ROOT);
      out.write("css/style.css\" rel=\"stylesheet\" type=\"text/css\" />\r\n");
      out.write("<!--[if lte IE 6]>\r\n");
      out.write("<link href=\"");
      out.print(CATBOT_MANAGE_ROOT);
      out.write("css/style_ie.css\" rel=\"stylesheet\" type=\"text/css\" />\r\n");
      out.write("<![endif]-->\r\n");
      out.write("<script type=\"text/javascript\" src=\"");
      out.print(CATBOT_MANAGE_ROOT);
      out.write("js/common.js\"></script>\r\n");
      out.write("<script type=\"text/javascript\">\r\n");
      out.write("\tfunction alertMessage(){\r\n");
      out.write("\t\tvar message = \"");
      out.print(message );
      out.write("\";\r\n");
      out.write("\t\tif(message != \"\")\r\n");
      out.write("\t\t\talert(message);\r\n");
      out.write("\t}\r\n");
      out.write("</script>\r\n");
      out.write("</head>\r\n");
      out.write("\r\n");
      out.write("<body onload=\"alertMessage()\">\r\n");
      out.write("\r\n");
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
      out.write("\t\t<h3>색인</h3>\r\n");
      out.write("\t\t\t<ul class=\"latest\">\r\n");
      out.write("\t\t\t<li><a href=\"");
      out.print(CATBOT_MANAGE_ROOT);
      out.write("crawling/action.jsp\">수집관리</a></li>\r\n");
      out.write("\t\t\t<li><a href=\"");
      out.print(CATBOT_MANAGE_ROOT);
      out.write("crawling/schedule.jsp\">수집주기설정</a></li>\r\n");
      out.write("\t\t\t<li><a href=\"");
      out.print(CATBOT_MANAGE_ROOT);
      out.write("crawling/history.jsp\">수집히스토리</a></li>\r\n");
      out.write("\t\t\t</ul>\r\n");
      out.write("\t</div>\r\n");
      out.write("\t<div class=\"sidebox\">\r\n");
      out.write("\t\t<h3>도움말</h3>\r\n");
      out.write("\t\t\t<ul class=\"latest\">\r\n");
      out.write("\t\t\t<li>각 컬렉션에 대한 색인결과를 확인합니다.</li>\r\n");
      out.write("\t\t\t<li>주기설정에 따라 자동으로 색인된 결과를 볼 수 있습니다.</li>\r\n");
      out.write("\t\t\t<li>즉시 실행하고자 하면 색인버튼을 눌러 수동으로 실행할 수 있습니다.</li>\r\n");
      out.write("\t\t\t</ul>\r\n");
      out.write("\t</div>\r\n");
      out.write("</div><!-- E : #sidebar -->\r\n");
      out.write("\r\n");
      out.write("\r\n");
      out.write("<div id=\"mainContent\">\r\n");
      out.write("\t<h2>수집결과</h2>\r\n");
      out.write("\t<div class=\"fbox\">\r\n");
      out.write("\t<table summary=\"색인결과\" class=\"tbl02\">\r\n");
      out.write("\t<colgroup><col width=\"3%\" /><col width=\"10%\" /><col width=\"10%\" /><col width=\"6%\" /><col width=\"8%\" /><col width=\"8%\" /><col width=\"10%\" /><col width=\"10%\" /><col width=\"7%\" /><col width=\"7%\" /></colgroup>\r\n");
      out.write("\t<thead>\r\n");
      out.write("\t<tr>\r\n");
      out.write("\t\t<th class=\"first\">No.</th>\r\n");
      out.write("\t\t<th>사이트명</th>\r\n");
      out.write("\t\t<th>수집타입</th>\r\n");
      out.write("\t\t<th>성공여부</th>\r\n");
      out.write("\t\t<th>문서수</th>\r\n");
      out.write("\t\t<th>스케줄링</th>\r\n");
      out.write("\t\t<th>시작시각</th>\r\n");
      out.write("\t\t<th>종료시각</th>\r\n");
      out.write("\t\t<th>실행시간</th>\r\n");
      out.write("\t\t<th>실행</th>\r\n");
      out.write("\t</tr>\r\n");
      out.write("\t</thead>\r\n");
      out.write("\t<tbody>\r\n");

	for(int i = 0; i < siteList.length; i++){
		String site = siteList[i];
		

      out.write("\r\n");
      out.write("\t<tr>\r\n");
      out.write("\t\t<td rowspan=\"2\" class=\"first\">");
      out.print(i+1);
      out.write("</td>\r\n");
      out.write("\t\t<td rowspan=\"2\"><strong class=\"small tb\">");
      out.print(site);
      out.write("</strong></td>\r\n");
      out.write("\t\t<td>전체수집</td>\r\n");
      out.write("\t\t<td>-</td>\r\n");
      out.write("\t\t<td>-</td>\r\n");
      out.write("\t\t<td>-</td>\r\n");
      out.write("\t\t<td>-</td>\r\n");
      out.write("\t\t<td>-</td>\r\n");
      out.write("\t\t<td>-</td>\r\n");
      out.write("\t\t<td><a href=\"crawlingService.jsp?cmd=0&site=");
      out.print(site);
      out.write("\" class=\"btn_s\">실행</a></td>\r\n");
      out.write("\t</tr>\r\n");
      out.write("\t<tr>\r\n");
      out.write("\t\t<td>증분수집</td>\r\n");
      out.write("\t\t<td>-</td>\r\n");
      out.write("\t\t<td>-</td>\r\n");
      out.write("\t\t<td>-</td>\r\n");
      out.write("\t\t<td>-</td>\r\n");
      out.write("\t\t<td>-</td>\r\n");
      out.write("\t\t<td>-</td>\r\n");
      out.write("\t\t<td><a href=\"crawlingService.jsp?cmd=1&site=");
      out.print(site);
      out.write("\" class=\"btn_s\">실행</a></td>\r\n");
      out.write("\t</tr>\r\n");

	}

      out.write("\r\n");
      out.write("\t</tbody>\r\n");
      out.write("\t</table>\r\n");
      out.write("\t</div>\r\n");
      out.write("\t\r\n");
      out.write("\t<!-- E : #mainContent --></div>\r\n");
      out.write("\t\r\n");
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
      out.write("\t\r\n");
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
