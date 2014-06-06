package org.apache.jsp.management;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;
import java.util.concurrent.ThreadPoolExecutor;
import java.sql.Timestamp;
import java.util.Properties;
import com.websqrd.fastcat.ir.config.IRSettings;
import com.websqrd.catbot.setting.CatbotSettings;
import java.net.URLEncoder;
import com.websqrd.catbot.web.WebUtils;
import com.websqrd.catbot.setting.CatbotSettings;

public final class account_jsp extends org.apache.jasper.runtime.HttpJspBase
    implements org.apache.jasper.runtime.JspSourceDependent {

  private static java.util.List _jspx_dependants;

  static {
    _jspx_dependants = new java.util.ArrayList(4);
    _jspx_dependants.add("/management/../common.jsp");
    _jspx_dependants.add("/management/../webroot.jsp");
    _jspx_dependants.add("/management/../header.jsp");
    _jspx_dependants.add("/management/../footer.jsp");
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

	String message = "";
	String cmd = request.getParameter("cmd");
	if("apply".equals(cmd)){
		String currentPwd = request.getParameter("currentPwd");
		String newPwd = request.getParameter("newPwd");
		String newPwd2 = request.getParameter("newPwd2");
		if(newPwd.equalsIgnoreCase(newPwd2)){
			String username = (String)session.getAttribute("authorized");
			Object accessLog = IRSettings.isCorrectPasswd(username, currentPwd);
			
			if(accessLog != null){
				IRSettings.storePasswd(username, newPwd);
				message = "패스워드가 성공적으로 변경되었습니다.";
			}else{
				message = "현재 패스워드가 올바르지 않습니다.";
			}
		}else{
			message = "두 패스워드가 일치하지 않습니다.";
		}
		
	}


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
      out.write("<script type=\"text/javascript\" src=\"");
      out.print(FASTCAT_MANAGE_ROOT);
      out.write("js/common.js\"></script>\r\n");
      out.write("<script type=\"text/javascript\" src=\"");
      out.print(FASTCAT_MANAGE_ROOT);
      out.write("js/jquery-1.4.4.min.js\"></script>\r\n");
      out.write("<script type=\"text/javascript\">\r\n");
      out.write("\tfunction applyPasswd(){\r\n");
      out.write("\t\t");

		if(!IRSettings.isAuthUsed()){
			
      out.write("\r\n");
      out.write("\t\t\talert(\"익명사용자는 이 기능을 사용할수없습니다.\");\r\n");
      out.write("\t\t\treturn;\r\n");
      out.write("\t\t");

		}
		
      out.write("\r\n");
      out.write("\t\tvar form = document.accountForm;\r\n");
      out.write("\t\tif(form.newPwd.value == '' || form.newPwd2.value == ''){\r\n");
      out.write("\t\t\talert(\"패스워드를 입력해주십시오.\");\r\n");
      out.write("\t\t\treturn;\r\n");
      out.write("\t\t}\r\n");
      out.write("\t\t\r\n");
      out.write("\t\tform.submit();\r\n");
      out.write("\t}\r\n");
      out.write("\r\n");
      out.write("\t$(document).ready(function() {\r\n");
      out.write("\t\tvar message = \"");
      out.print(message );
      out.write("\";\r\n");
      out.write("\t\tif(message != \"\")\r\n");
      out.write("\t\t\talert(message);\r\n");
      out.write("\t});\r\n");
      out.write("</script>\r\n");
      out.write("</head>\r\n");
      out.write("\r\n");
      out.write("<body>\r\n");
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
      out.write("\t\t<h3>관리</h3>\r\n");
      out.write("\t\t\t<ul class=\"latest\">\r\n");
      out.write("\t\t\t<li><a href=\"");
      out.print(FASTCAT_MANAGE_ROOT);
      out.write("management/main.jsp\">시스템상태</a></li>\r\n");
      out.write("\t\t\t<li><a href=\"");
      out.print(FASTCAT_MANAGE_ROOT);
      out.write("management/searchEvent.jsp\">이벤트내역</a></li>\r\n");
      out.write("\t\t\t<li><a href=\"");
      out.print(FASTCAT_MANAGE_ROOT);
      out.write("management/jobHistory.jsp\">작업히스토리</a></li>\r\n");
      out.write("\t\t\t<li><a href=\"");
      out.print(FASTCAT_MANAGE_ROOT);
      out.write("management/account.jsp\" class=\"selected\">계정관리</a></li>\r\n");
      out.write("\t\t\t<li><a href=\"");
      out.print(FASTCAT_MANAGE_ROOT);
      out.write("management/config.jsp\">사용자설정</a></li>\r\n");
      out.write("\t\t\t<li><a href=\"");
      out.print(FASTCAT_MANAGE_ROOT);
      out.write("management/advConfig.jsp\">고급설정보기</a></li>\r\n");
      out.write("\t\t\t<li><a href=\"");
      out.print(FASTCAT_MANAGE_ROOT);
      out.write("management/restore.jsp\">복원</a></li>\r\n");
      out.write("\t\t\t</ul>\r\n");
      out.write("\t</div>\r\n");
      out.write("\t<div class=\"sidebox\">\r\n");
      out.write("\t\t<h3>도움말</h3>\r\n");
      out.write("\t\t\t<ul class=\"latest\">\r\n");
      out.write("\t\t\t<li>익명사용자는 계정을 관리할 수 없습니다. 계정관리를 위해서는 계정사용을 활성화해주시기 바랍니다.</li>\r\n");
      out.write("\t\t\t<li>활성화방법 : conf/auth파일내에 use=true로 셋팅</li>\r\n");
      out.write("\t\t\t</ul>\r\n");
      out.write("\t</div>\r\n");
      out.write("</div><!-- E : #sidebar -->\r\n");
      out.write("\r\n");
      out.write("<div id=\"mainContent\">\r\n");
      out.write("\t<h2>계정관리</h2>\r\n");
      out.write("\t<form action=\"\" method=\"post\" name=\"accountForm\">\r\n");
      out.write("\t<input type=\"hidden\" name=\"cmd\" value=\"apply\" />\r\n");
      out.write("\t<div class=\"fbox\">\r\n");
      out.write("\t<table summary=\"계정관리\" class=\"tbl01\">\r\n");
      out.write("\t<colgroup><col width=\"23%\" /><col width=\"\" /></colgroup>\r\n");
      out.write("\t<tbody>\r\n");
      out.write("\t<tr>\r\n");
      out.write("\t\t<th>현재 비밀번호</th>\r\n");
      out.write("\t\t<td style=\"text-align:left\"><input type=\"password\" name=\"currentPwd\" class=\"inp02\" size=\"20\" style=\"align:left\"></input></td>\r\n");
      out.write("\t</tr>\r\n");
      out.write("\t<tr>\r\n");
      out.write("\t\t<th>새 비밀번호</th>\r\n");
      out.write("\t\t<td style=\"text-align:left\"><input type=\"password\" name=\"newPwd\" class=\"inp02\" size=\"20\"></input></td>\r\n");
      out.write("\t</tr>\r\n");
      out.write("\t<tr>\r\n");
      out.write("\t\t<th>새 비밀번호(확인)</th>\r\n");
      out.write("\t\t<td style=\"text-align:left\"><input type=\"password\" name=\"newPwd2\" class=\"inp02\" size=\"20\"></input></td>\r\n");
      out.write("\t</tr>\r\n");
      out.write("\t</tbody>\r\n");
      out.write("\t</table>\r\n");
      out.write("\t</div>\r\n");
      out.write("\t\r\n");
      out.write("\t<div id=\"btnBox\">\r\n");
      out.write("\t<a href=\"javascript:applyPasswd()\" class=\"btn\">수정</a>\r\n");
      out.write("\t</div>\r\n");
      out.write("\t</form>\r\n");
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
      out.write("\t\r\n");
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
