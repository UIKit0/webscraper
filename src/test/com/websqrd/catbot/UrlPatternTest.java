package com.websqrd.catbot;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import junit.framework.TestCase;

public class UrlPatternTest extends TestCase {
	public void test1(){
		String titlePat = "<title>(.*)</title>";
		String bodyPat = "<body[^>]*>([\\s\\S]*)</body>";
		String urlPat = "<a\\s+href\\s*=\\s*[\"|\']?([^\"\'\\s<>\\|#;\\(\\)\\\\@]*?)[\"|>|\']";
		Pattern p = Pattern.compile(bodyPat,Pattern.CASE_INSENSITIVE);
//		String contents = "<ul>   <a href=\"#aa\">   <a href=\"depart/a.jsp\">  	<li><img src=\"http://www.gndi.re.kr/images/common/lnb_openmenu02.gif\" alt=\"연구부서\" /></li>            <li><a href=\"http://www.gndi.re.kr/depart/dep01.php?topMenu=02&leftMenu=01&tabMenu=01&dep=01\"><img src=\"http://www.gndi.re.kr/images/common/lnb_openmenu02_01.gif\" alt=\"연구기획조정실\" /></a></li>            <li><a href=\"http://www.gndi.re.kr/depart/dep02.php?topMenu=02&leftMenu=02&tabMenu=01&dep=02\"><img src=\"http://www.gndi.re.kr/images/common/lnb_openmenu02_02.gif\" alt=\"경제산업연구실\" /></a></li>            <li><a href=\"http://www.gndi.re.kr/depart/dep03.php?topMenu=02&leftMenu=03&tabMenu=01&dep=04\"><img src=\"http://www.gndi.re.kr/images/common/lnb_openmenu02_03.gif\" alt=\"도시지역연구실\" /></a></li>            <li><a href=\"http://www.gndi.re.kr/depart/dep04.php?topMenu=02&leftMenu=04&tabMenu=01&dep=05\"><img src=\"http://www.gndi.re.kr/images/common/lnb_openmenu02_04.gif\" alt=\"환경교통연구실\" /></a></li>            <li><a href=\"http://www.gndi.re.kr/depart/dep05.php?topMenu=02&leftMenu=05&tabMenu=01&dep=06\"><img src=\"http://www.gndi.re.kr/images/common/lnb_openmenu02_05.gif\" alt=\"사회정책연구실\" /></a></li>            <li><a href=\"http://www.gndi.re.kr/depart/dep06.php?topMenu=02&leftMenu=06&tabMenu=01&dep=07\"><img src=\"http://www.gndi.re.kr/images/common/lnb_openmenu02_06.gif\" alt=\"여성가족정책센터\" /></a></li>            <li><a href=\"http://www.gnchc.re.kr\" target=\"_blank\" title=\"새창으로열림\"><img src=\"http://www.gndi.re.kr/images/common/lnb_openmenu02_07.gif\" alt=\"역사문화센터\" /></a></li>            <li><a href=\"http://www.gndi.re.kr/depart/dep07.php?topMenu=02&leftMenu=08&tabMenu=01&dep=08\"><img src=\"http://www.gndi.re.kr/images/common/lnb_openmenu02_08.gif\" alt=\"사무국\" /></a></li>            <li><a href=\"http://www.gndi.re.kr/depart/dep08.php?topMenu=02&leftMenu=09&tabMenu=01&dep=09\"><img src=\"http://www.gndi.re.kr/images/common/lnb_openmenu02_09.gif\" alt=\"도정연구관\" /></a></li>        </ul>        <ul>";
		
		StringBuilder sb = new StringBuilder();
//		try {
//			byte[] buf = new byte[1024];
//			File f= new File("d:/test.html");
//			System.out.println(f.exists()+" , "+f.length());
//			InputStream is = new FileInputStream(f);
//			int n = 0;
//			while((n = is.read(buf)) > 0){
//				sb.append(new String(buf, 0, n));
////				System.out.println(new String(buf, 0, n));
//			}
////			sb.append(r.readLine());
//			
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		String contents = sb.toString();
		contents = "<body>\nasdfas\nsfsdf\ndfsdf</body>";
		System.out.println(contents);
		Matcher m = p.matcher(contents);
		for(int i=0;m.find();i++) {
			String link = m.group(1).trim();
			System.out.println("matchs = "+contents.substring(m.start(), m.end()));
			System.out.println("###link = "+link);
		}
	}
	
	public void test2(){
		String pat2 = "(\\w+)=(\\w+)";
		String pat1 = "(\\w+)=(\\w+)";
		String link = "http://www.getfastcat.org/?mid=notice&page=1&document_srl=883";
		
		Pattern pat = Pattern.compile(pat1);
		Matcher m = pat.matcher(link);
		while(m.find()){
			int c = m.groupCount();
			for (int i = 0; i <= c; i++) {
				System.out.println(i+" = "+m.group(i));
			}
		}
		
		
		
	}
	
	
	public void test3(){
		String link = "&a=1&b==8&c=2";
		String[] params = link.split("&");
		for (int i = 0; i < params.length; i++) {
			System.out.println(">>>>" +params[i]);
			String[] kv = params[i].split("=");
			for (int j = 0; j < kv.length; j++) {
				System.out.println("kv-"+j+" = "+kv[j]);
			}
		}
		
	}
	
	public void test4(){
		String link = "http://a.com/?a=1&b=28&c=2&amp;d=4";
		try {
			URL url = new URL(link);
			System.out.println("url >> "+url);
			System.out.println(url.getQuery());
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		
		
	}
}
