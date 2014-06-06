package org.apache.jsp.crawling;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;
import java.net.URLEncoder;
import com.websqrd.catbot.job.IncCrawlingJob;
import com.websqrd.catbot.job.FullCrawlingJob;
import com.websqrd.catbot.control.JobController;
import com.websqrd.catbot.setting.CatbotSettings;
import java.net.URLEncoder;
import com.websqrd.catbot.web.WebUtils;

public final class crawlingService_jsp extends org.apache.jasper.runtime.HttpJspBase
    implements org.apache.jasper.runtime.JspSourceDependent {

  private static java.util.List _jspx_dependants;

  static {
    _jspx_dependants = new java.util.ArrayList(2);
    _jspx_dependants.add("/crawling/../common.jsp");
    _jspx_dependants.add("/crawling/../webroot.jsp");
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

	int cmd = Integer.parseInt(request.getParameter("cmd"));
String site = request.getParameter("site");

switch(cmd){
	//start full indexing 
	case 0:
	{
		FullCrawlingJob job = new FullCrawlingJob();
		job.setArgs(new String[]{site});
		JobController.getInstance().offer(job);
		response.sendRedirect("action.jsp?message="+URLEncoder.encode(URLEncoder.encode("컬렉션 "+site+"의 전체색인 작업을 등록하였습니다.", "utf-8"),"utf-8"));
		break;
	}
	//start incremental indexing 
	case 1:
	{
		IncCrawlingJob job = new IncCrawlingJob();
		job.setArgs(new String[]{site});
		JobController.getInstance().offer(job);
		response.sendRedirect("action.jsp?message="+URLEncoder.encode(URLEncoder.encode("컬렉션 "+site+"의 증분색인 작업을 등록하였습니다.", "utf-8"),"utf-8"));
		break;
	}
	//apply indexing schedule 
	case 2:
	{
		IncCrawlingJob job = new IncCrawlingJob();
		job.setArgs(new String[]{site});
		JobController.getInstance().offer(job);
		response.sendRedirect("action.jsp?message="+URLEncoder.encode(URLEncoder.encode("컬렉션 "+site+"의 증분색인 작업을 등록하였습니다.", "utf-8"),"utf-8"));
		break;
	}
}

      out.write('\r');
      out.write('\n');
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
