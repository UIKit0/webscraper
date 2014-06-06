$(function() { 
	$(".help").each(function() {
	    var obj = $(this);
	    var helpTopic = obj.attr("id");
	    var helpId = helpTopic + "_help";
		obj.after("<div id='"+helpId+"' class='helpContents'></div>"); 
		obj.hover(function(){help(helpTopic, helpId)});
	}); 
});


function help(helpTopic, helpId){
	
	var msg = "도움말이 존재하지 않습니다.";
	
	switch(helpTopic){
	
		
	case "server_port": 
		msg = "수집엔진에서 요청을 받아들이는 포트이다. 검색과 웹관리도구를 접근시 사용된다.<br>";
		msg += "기본값은 8080이며, 필요시 8000이상 12000이하의 정수를 설정하도록 한다.";
		break;
		
	case "dynamic_classpath": 
		msg = "사용자가 생성한 외부클래스들을 수집엔진에서 인식할 수 있도록 설정한다.<br>";
		msg += "검색엔진 홈디렉토리 기준 상대경로로 인식하며, 여러 경로를 설정시 : 또는 ; 경로구분자로 구분한다.<br>";
		msg += "예) lib/project1.jar<br>";
		msg += "예) lib/myClass.jar:lib/myRanking.jar"
		break;
			
	/////////////
	//데이터소스설정
	/////////////
		
	case "sourceTypeList": 
		msg = "파일 : 검색대상 원본데이터가 파일로 존재할 경우 선택한다.<br>";
		msg += "DB : 검색대상 원본데이터가 DB에 존재할 경우 선택한다.<br>";
		msg += "사용자지정 : 검색대상 원본데이터가 파일과 DB 이외의 곳에 존재할 경우 데이터소스 Reader를 직접구현하여 수집가능하다.";
		break;
		
	case "sourceModifier": 
		msg = "원본데이터를 Java를 이용해서 변환하려할때 구현한 클래스이름을 입력한다.<br>";
		msg += "데이터의 변환이 필요하지 않으면 공란으로 남겨둔다.<br>";
		msg += "소스모디파이어는 com.websqrd.fastcat.ir.source.SourceModifier를 구현한 클래스이다.<br>";
		msg += "<u>외부 클래스를 사용하려면 반드시 동적클래스패스에 해당 jar파일을 추가해야 한다.</u><br>";
		msg += "예) com.websqrd.fastcat.user.sourcemodifier.SuggestionModifier";
		break;
		
	case "fullFilePath": 
		msg = "이곳에 설정된 파일데이터는 수집엔진에서 전체색인에 사용된다.<br>";
		msg += "파일포맷은 자유로우며, 해당 파일을 해석할 수 있는 수집파일Parser를 아래에서 셋팅하도록 한다.<br>";
		msg += "상대경로 입력시 수집엔진 홈디렉토리를 기준으로 인식하며, /로 시작하는 절대경로로 설정가능하다.<br>";
		msg += "단일 파일을 설정가능할 뿐아니라, 디렉토리경로를 설정하면, 해당 디렉토리내의 모든 파일을 사용한다.<br>";
		msg += "예)collection/sample/testData/fullDir<br>";
		msg += "예)collection/sample/testData/full.txt";
		break;
	case "incFilePath": 
		msg = "이곳에 설정된 파일데이터는 수집엔진에서 전체색인에 사용된다.<br>";
		msg += "설정방법은 위의 전체색인 파일경로의 경우와 같다.<br>";
		msg += "예)collection/sample/testData/incDir<br>";
		break;
	case "fileDocParser": 
		msg = "위에 설정한 수집파일을 해석할 수 있는 Parser이다.<br>";
		msg += "기본파서는 패스트캣 수집파일을 해석할 수 있는 com.websqrd.fastcat.user.parse.FastcatCollectFileParser이다.<br>";
		msg += "모든 파서는 com.websqrd.fastcat.ir.source.SourceReader를 상속받아 구현되며, 수집파일이 패스트캣 포맷이 아닐경우사용자가 직접 구현할 수도 있다.<br>";
		msg += "예) com.websqrd.fastcat.user.parse.FastcatCollectFileParser";
		break;
	case "fileEncoding": 
		msg = "수집파일의 인코딩을 지정한다.<br>";
		msg += "텍스트 수집파일의 경우 인코딩을 잘못 지정하면, 파서가 문서를 제대로 해석할 수 없다.<br>";
		msg += "예) utf-8<br>";
		msg += "<b>>자주쓰이는 인코딩들</b>";
		msg += "<li>utf-8</li><li>euc-kr</li><li>cp949</li>";
		break;
		
	case "driver": 
		msg = "JDBC드라이버 클래스설정<br><u>해당 드라이버를 사용하려면 반드시 동적클래스패스에 드라이버 jar파일을 추가해야 한다.</u><br>";
		msg += "예) org.gjt.mm.mysql.Driver<br>";
		msg += "<b>>자주쓰이는 JDBC드라이버 리스트</b>";
		msg += "<li>MySql : org.gjt.mm.mysql.Driver</li>";
		msg += "<li>MSSQL(jtds) : net.sourceforge.jtds.jdbc.Driver</li>";
		msg += "<li>ORACLE : oracle.jdbc.driver.OracleDriver</li>";
		msg += "<li>PostgreSQL : org.postgresql.Driver</li>";
		msg += "<li>Cubrid : cubrid.jdbc.driver.CUBRIDDriver</li>";
		break;
		
	case "url": 
		msg = "JDBC URL설정<br>접속할 서버주소, 포트, DB명으로 jdbc연결설정.<br>";
		msg += "예) jdbc:mysql://192.168.0.100:3306/fastcat<br>";
		msg += "<b>>자주쓰이는 JDBC URL 리스트</b>";
		msg += "<li>MySql : jdbc:mysql://[host][:port]/[db]</li>";
		msg += "<li>MSSQL(jtds) : jdbc:jtds:sqlserver://[host][:port]/[db]</li>";
		msg += "<li>ORACLE : jdbc:oracle:thin//[host][:port]/SID</li>";
		msg += "<li>PostgreSQL : jdbc:postgresql://[host]:[port]/[db]</li>";
		msg += "<li>Cubrid : jdbc:cubrid:[host]:[port]:[db]:::</li>";
		break;
				
	}
	
	id = $("#"+helpId);
	if(id.length > 0 && msg.length > 0){
		id.html(msg);
		id.toggle();
	}
}