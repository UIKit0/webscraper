package org.apache.jsp;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;
import com.websqrd.catbot.web.WebUtils;
import java.net.URLConnection;
import java.net.URL;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import com.websqrd.catbot.setting.CatbotSettings;
import java.net.URLDecoder;

public final class index_jsp extends org.apache.jasper.runtime.HttpJspBase
    implements org.apache.jasper.runtime.JspSourceDependent {

  private static java.util.List _jspx_dependants;

  static {
    _jspx_dependants = new java.util.ArrayList(1);
    _jspx_dependants.add("/webroot.jsp");
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

	String message = URLDecoder.decode(WebUtils.getString(request.getParameter("message"), ""),"utf-8");
	String cmd = request.getParameter("cmd");
	if ("ajax".equals(cmd)) {
		String location = request.getParameter("location");
		try {
			URL url = new URL(location);
			BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), "utf-8"));
			String line;
			while ((line = reader.readLine()) != null) {
				out.println(line);
			}
		} catch (Exception e) { }

		return;
	}else if ("login".equals(cmd)) {
		String username = request.getParameter("username");
		String passwd = request.getParameter("passwd");
		String[] accessLog = CatbotSettings.isCorrectPasswd(username, passwd);
		if(accessLog != null){
			//로긴 성공
			session.setAttribute("authorized", username);
			session.setAttribute("lastAccessLog", accessLog);
			session.setMaxInactiveInterval(60 * 30); //30 minutes
			CatbotSettings.storeAccessLog(username, ""); //ip주소는 공란으로 남겨두고 사용하지 않도록함. 
			//request.getRemoteAddr()로는 제대로된 사용자 ip를 알아낼수 없음.
			//jetty에서는 getHeader("REMOTE_ADDR"); 또는 req.getHeaer("WL-Proxy-Client-IP")+","+req.getHeaer("Proxy-Client-IP")+","+req.getHeaer("X-Forwarded-For")) 등을 제공하지 않는다.
			message = "";
			response.sendRedirect(CATBOT_MANAGE_ROOT+"main.jsp");
			return;
		}else{
			message = "아이디와 비밀번호를 확인해주세요.";
		}
		
	}else if ("logout".equals(cmd)) {
		session.invalidate();
		response.sendRedirect(CATBOT_MANAGE_ROOT+"index.jsp");
		return;
	}
	if (CatbotSettings.isAuthUsed() && "admin".equals(session.getAttribute("authorized"))) {
		response.sendRedirect(CATBOT_MANAGE_ROOT+"main.jsp");
		return;
	}
	if(!CatbotSettings.isAuthUsed()){
		response.sendRedirect(CATBOT_MANAGE_ROOT+"main.jsp");
		return;
	}

      out.write("\r\n");
      out.write("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\r\n");
      out.write("<html xmlns=\"http://www.w3.org/1999/xhtml\">\r\n");
      out.write("<head>\r\n");
      out.write("<title>CATBOT 관리도구</title>\r\n");
      out.write("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />\r\n");
      out.write("<link rel=\"stylesheet\" type=\"text/css\" href=\"css/Login.css\" />\r\n");
      out.write("<script type=\"text/javascript\" src=\"");
      out.print(CATBOT_MANAGE_ROOT);
      out.write("js/common.js\"></script>\r\n");
      out.write("<script type=\"text/javascript\" src=\"");
      out.print(CATBOT_MANAGE_ROOT);
      out.write("js/jquery-1.4.4.min.js\"></script>\r\n");
      out.write("<script>\r\n");
      out.write("\t$(document).ready(function() {\r\n");
      out.write("\t\tvar message = \"");
      out.print(message );
      out.write("\";\r\n");
      out.write("\t\tif(message != \"\")\r\n");
      out.write("\t\t\talert(message);\r\n");
      out.write("\t});\r\n");
      out.write("\r\n");
      out.write("\tfunction logout(){\r\n");
      out.write("\t\tlocation.href=\"?cmd=logout\";\r\n");
      out.write("\t}\r\n");
      out.write("</script>\r\n");
      out.write("</head>\r\n");
      out.write("\r\n");
      out.write("<body>\r\n");
      out.write("\t<div id=\"LoginWrap\">\r\n");
      out.write("\t\t<div class=\"form\">\r\n");
      out.write("\t\t\t<div class=\"inputWrap\">\r\n");
      out.write("\t\t\t<form action=\"index.jsp\" method=\"post\">\r\n");
      out.write("\t\t\t\t<div class=\"left\">\r\n");
      out.write("\t\t\t\t\t<input type=\"hidden\" name=\"cmd\" value=\"login\" />\r\n");
      out.write("\t\t\t\t\t<input type=\"text\" name=\"username\" onFocus=\"this.className='none'\" onBlur=\"if ( this.value == '' ) { this.className='id' }\" class='id' /><br />\r\n");
      out.write("\t\t\t\t\t<input type=\"password\" name=\"passwd\" onFocus=\"this.className='none'\" onBlur=\"if ( this.value == '' ) { this.className='pw' }\" class=\"pw\" />\r\n");
      out.write("\t\t\t\t</div>\r\n");
      out.write("\t\t\t\t&nbsp;&nbsp;<input type=\"image\" src=\"css/images/login/loginBtn.gif\" name=\"Submit\" value=\"Submit\" />\r\n");
      out.write("\t\t\t</form>\r\n");
      out.write("\t\t\t</div>\r\n");
      out.write("\t\t</div>\r\n");
      out.write("\t</div>\r\n");
      out.write("\t<!--/wrap-->\r\n");
      out.write("\r\n");
      out.write("\r\n");
      out.write("\r\n");
      out.write("\t<!--footer-->\r\n");
      out.write("\t<div id=\"footer_wrap\">\r\n");
      out.write("\t\t<div class=\"footer\">\r\n");
      out.write("\t\t\t<div class=\"copy\">\r\n");
      out.write("\t\t\t\t<p class=\"address\">\r\n");
      out.write("\t\t\t\tCopyright(c) Websqrd Co.,Ltd. All rights reserved. contact@websqrd.com<br />\r\n");
      out.write("\t\t\t\t서울특별시 강남구 역삼동 641 경성빌딩 5층 Tel. 02-508-1151 Fax. 02-508-1153 대표자: 송상욱 사업자등록번호 : 220-88-03822\r\n");
      out.write("\t\t\t\t</p>\r\n");
      out.write("\t\t\t</div>\r\n");
      out.write("\t\t</div>\r\n");
      out.write("\t</div>\r\n");
      out.write("</body>\r\n");
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
