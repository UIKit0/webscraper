<%@ page contentType="text/html; charset=UTF-8"%>
<html debug="true">

<head>

<!--<script type="text/javascript" src="https://getfirebug.com/firebug-lite.js"></script>-->
<script type="text/javascript" src="http://fbug.googlecode.com/svn/lite/branches/firebug1.4/content/firebug-lite-dev.js"></script>
</head>

<body leftmargin=0 topmargin=0>
<table width='100%'><tr><td align='center' valign='top'>

<table width="100%" cellpadding=0 cellspacing=0 border=0 bgcolor=fefce3>
	<tr>
		<td>

<!-- 카테고리/검색 start -->
			<table width="100%" border="0" cellspacing="0" cellpadding="8" style="border:4px #f0f0f0 solid">			
			<form name="form1" action="list.php" method="get">
			<tr>			 
				
				
				<td align="center">
				<table border="0" cellspacing="0" cellpadding="0">				  
					<tr>
					<td>
						<!--						<table border="0" cellspacing="0" cellpadding="3">
							<tr>
							<td width="76" style="padding-top:4px"><B>· 분류선택</B></td>
								<td width="120">
									<select name="category" onChange="this.form.submit();" style="background:fefce3;color:ffffff;font:8pt 돋움">
											<option value="">--분류전체--</option>
									 											<option value="2" >일반</option>
																			</select></td>
							</tr>
						</table>
						-->					</td>			
					

					<td align="center">
						<table border="0" cellspacing="0" cellpadding="3">
							<tr>
								<td width="60"><B>· 검색어</B></td>
								<td>
									<select name="search" class="box" align="absmiddle" style="font:8pt 돋움">
										<option value="subject" selected>제 목</option>
										<option value="content" >내 용</option>
										<option value="name" >글쓴이</option>
								 </select></td>
								<td><input type="text" class="box" name="key_word" value="" align="absmiddle"></td>
								<td><input type="image" src="skin/default/image/search_btn.gif" name="image" align="absmiddle" border="0"></td>
							</tr>
						</table>					  
					</td>
					
				</tr>
				</table>
			</td>				 
			</tr>
		<input type=hidden name="board_id" value="0401">
		</form>
		</table>
<!-- 카테고리/검색 end -->
		<br>		


<!-- 로그인 기능 사용 start -->
		<!--		<script>
		function mini_login()	{
			window.open('/board/member/mini_login.php','','width=500,height=178');
		}
		function mini_logout()	{
			window.open('/board/member/post.php?mode=mini_logout','','');
		}
		function mini_join()	{
			window.open('/board/member/join.php','','width=660,height=600,scrollbars=yes');
		}
		function mini_modify()	{
			window.open('/board/member/join.php?member_type=modify','','width=660,height=600,scrollbars=yes');
		}
		</script>
		<table width="100%" border="0" cellspacing="0" cellpadding="0">		
			<tr>
				<td>
				<table width=100% cellpadding=0 cellspacing=0 border=0><tr><td><a href=javascript:mini_login();><img src='/board/member/image/mini_login_btn.gif' border=0 align=absmiddle></a></td><td align=right><a href=javascript:mini_join();><img src='/board/member/image/mini_join.gif' border=0 align=absmiddle></a></td></tr></table>				</td>
			</tr>
			<tr><td height=5></td></tr>
		</table>
		-->		
<!-- 로그인 기능 사용 end -->


		
		<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<form name=check_form action="post.php" method=post onSubmit="return del_check_script()">
			<tr>
				<td height="3" bgcolor="#cccccc" colspan="5"></td>
			</tr>
			<tr align="center" height="26" bgcolor="fefce3">
				<td width="8%" style = 'font-size:8pt;font-weight:bold;color:#666666;border-left:#cccccc 1px solid;'>번호</td>					
				<td width="55%" style = 'font-size:8pt;font-weight:bold;color:#666666;border-left:#cccccc 1px solid;'>제목</td>					
				<td width="15%" style = 'font-size:8pt;font-weight:bold;color:#666666;border-left:#cccccc 1px solid;'>글쓴이</td>
				<td width="15%" style = 'font-size:8pt;font-weight:bold;color:#666666;border-left:#cccccc 1px solid;'>날짜</td>
				<td width="7%" style = 'font-size:8pt;font-weight:bold;color:#666666;border-left:#cccccc 1px solid;' style="border-right:#cccccc 1px solid;">조회</td>
			</tr>
			<tr>
				<td height="1" bgcolor="#cccccc" colspan="5"></td>
			</tr>
			<tr>
				<td height="2" colspan="5"></td>
			</tr>


<!-- 리스트 반복 테이블 start -->

						<tr >
			  <td height="7" colspan="5"></td>
			</tr>
			<tr > 
			  <td align="center"><img src="skin/default/image/notice_icon.gif"></td>		   
			  <td colspan=4><a href="view.php?board_id=0401&no=417&category=&pagenum=&search=subject&key_word=">365 어르신 돌봄센터 이용대상자 모집</a></td>		   
			</tr>
			<tr >
			  <td height="4" colspan="5"></td>
			</tr>
			<tr>
			  <td bgcolor="e6e6e6" height="1" colspan="5"></td>
			</tr>

			
			
			<tr >
			  <td height="7" colspan="5"></td>
			</tr>
			<tr > 
			  <td align="center">486</td>		   
			  <td><a href="view.php?board_id=0401&no=520&category=&pagenum=&search=subject&key_word=">노인음악 " 크로마 하프" 수업 참여자 모집</a>   </td>		   
			  <td align="center">안혜란</td>		   
			  <td align="center">2012-03-08</td>		   
			  <td align="center">67</td>
			</tr>
			<tr >
			  <td height="4" colspan="5"></td>
			</tr>
			<tr>
			  <td bgcolor="e6e6e6" height="1" colspan="5"></td>
			</tr>

			
			<tr >
			  <td height="7" colspan="5"></td>
			</tr>
			<tr > 
			  <td align="center">485</td>		   
			  <td><a href="view.php?board_id=0401&no=519&category=&pagenum=&search=subject&key_word=">"노인, 노인을 말하다.3-노년의 삶, 그리고 소통" 참여자 모집</a>   </td>		   
			  <td align="center">김지은</td>		   
			  <td align="center">2012-03-07</td>		   
			  <td align="center">57</td>
			</tr>
			<tr >
			  <td height="4" colspan="5"></td>
			</tr>
			<tr>
			  <td bgcolor="e6e6e6" height="1" colspan="5"></td>
			</tr>

			
			<tr >
			  <td height="7" colspan="5"></td>
			</tr>
			<tr > 
			  <td align="center">484</td>		   
			  <td><a href="view.php?board_id=0401&no=518&category=&pagenum=&search=subject&key_word=">무료이동목욕사업 자원봉사자 모집</a>   </td>		   
			  <td align="center">기태영</td>		   
			  <td align="center">2012-03-06</td>		   
			  <td align="center">53</td>
			</tr>
			<tr >
			  <td height="4" colspan="5"></td>
			</tr>
			<tr>
			  <td bgcolor="e6e6e6" height="1" colspan="5"></td>
			</tr>

			
			<tr >
			  <td height="7" colspan="5"></td>
			</tr>
			<tr > 
			  <td align="center">483</td>		   
			  <td><a href="view.php?board_id=0401&no=517&category=&pagenum=&search=subject&key_word=">아름채 2월 이용자 현황보고(2012.02.29 기준)</a>   </td>		   
			  <td align="center">가진순</td>		   
			  <td align="center">2012-03-06</td>		   
			  <td align="center">56</td>
			</tr>
			<tr >
			  <td height="4" colspan="5"></td>
			</tr>
			<tr>
			  <td bgcolor="e6e6e6" height="1" colspan="5"></td>
			</tr>

			
			<tr >
			  <td height="7" colspan="5"></td>
			</tr>
			<tr > 
			  <td align="center">482</td>		   
			  <td><a href="view.php?board_id=0401&no=516&category=&pagenum=&search=subject&key_word=">3월 9일 금요문화프로그램 안내</a>   </td>		   
			  <td align="center">안혜란</td>		   
			  <td align="center">2012-03-06</td>		   
			  <td align="center">40</td>
			</tr>
			<tr >
			  <td height="4" colspan="5"></td>
			</tr>
			<tr>
			  <td bgcolor="e6e6e6" height="1" colspan="5"></td>
			</tr>

			
			<tr >
			  <td height="7" colspan="5"></td>
			</tr>
			<tr > 
			  <td align="center">481</td>		   
			  <td><a href="view.php?board_id=0401&no=515&category=&pagenum=&search=subject&key_word=">2월 후원현황보고</a>   </td>		   
			  <td align="center">관리자</td>		   
			  <td align="center">2012-03-05</td>		   
			  <td align="center">35</td>
			</tr>
			<tr >
			  <td height="4" colspan="5"></td>
			</tr>
			<tr>
			  <td bgcolor="e6e6e6" height="1" colspan="5"></td>
			</tr>

			
			<tr >
			  <td height="7" colspan="5"></td>
			</tr>
			<tr > 
			  <td align="center">480</td>		   
			  <td><a href="view.php?board_id=0401&no=514&category=&pagenum=&search=subject&key_word=">아름채봉사단 3월 월례회의 일정 안내</a>   </td>		   
			  <td align="center">김지은</td>		   
			  <td align="center">2012-03-03</td>		   
			  <td align="center">55</td>
			</tr>
			<tr >
			  <td height="4" colspan="5"></td>
			</tr>
			<tr>
			  <td bgcolor="e6e6e6" height="1" colspan="5"></td>
			</tr>

			
			<tr >
			  <td height="7" colspan="5"></td>
			</tr>
			<tr > 
			  <td align="center">479</td>		   
			  <td><a href="view.php?board_id=0401&no=513&category=&pagenum=&search=subject&key_word=">전립선 무료 검진</a>   </td>		   
			  <td align="center">김은자</td>		   
			  <td align="center">2012-03-02</td>		   
			  <td align="center">70</td>
			</tr>
			<tr >
			  <td height="4" colspan="5"></td>
			</tr>
			<tr>
			  <td bgcolor="e6e6e6" height="1" colspan="5"></td>
			</tr>

			
			<tr >
			  <td height="7" colspan="5"></td>
			</tr>
			<tr > 
			  <td align="center">478</td>		   
			  <td><a href="view.php?board_id=0401&no=512&category=&pagenum=&search=subject&key_word=">토요복지관 운영 안내</a>   </td>		   
			  <td align="center">김지은</td>		   
			  <td align="center">2012-02-20</td>		   
			  <td align="center">241</td>
			</tr>
			<tr >
			  <td height="4" colspan="5"></td>
			</tr>
			<tr>
			  <td bgcolor="e6e6e6" height="1" colspan="5"></td>
			</tr>

			
			<tr >
			  <td height="7" colspan="5"></td>
			</tr>
			<tr > 
			  <td align="center">477</td>		   
			  <td><a href="view.php?board_id=0401&no=511&category=&pagenum=&search=subject&key_word=">아름채 이용자 현황보고(2012.01.31기준)</a>   </td>		   
			  <td align="center">관리자</td>		   
			  <td align="center">2012-02-17</td>		   
			  <td align="center">165</td>
			</tr>
			<tr >
			  <td height="4" colspan="5"></td>
			</tr>
			<tr>
			  <td bgcolor="e6e6e6" height="1" colspan="5"></td>
			</tr>

			
<!-- 리스트 반복 테이블 end -->

			<tr>
			  <td height="2" bgcolor="e6e6e6" colspan="5"></td>
		  </tr>

			<tr>
				<td colspan=5>


<!-- 하단 페이지그룹 부분 높이 50px start -->
<style>	#mytd {padding: 2 9 0 9; cursor:hand;}</style>

					<table width="100%" border="0" cellspacing="0" cellpadding="0" align=center height=50>
						<tr> 																	
							<td align=center>
								<table border="0" cellspacing="0" cellpadding="0" align=center>
									<tr>

<td  width=1 nowrap bgcolor=#cccccc></td><td id=mytd align=center onMouseOver=this.style.background='#f7f7f7' onMouseOut=this.style.background='' onclick=location.href='list.php?board_id=0401&category=&search=subject&key_word=&pagenum=0'><b><font color=#666666>1</font></b></td><td  width=1 nowrap bgcolor=#cccccc></td><td id=mytd align=center onMouseOver=this.style.background='#f7f7f7' onMouseOut=this.style.background='' onclick=location.href='list.php?board_id=0401&category=&search=subject&key_word=&pagenum=1'>2</td><td  width=1 nowrap bgcolor=#cccccc></td><td id=mytd align=center onMouseOver=this.style.background='#f7f7f7' onMouseOut=this.style.background='' onclick=location.href='list.php?board_id=0401&category=&search=subject&key_word=&pagenum=2'>3</td><td  width=1 nowrap bgcolor=#cccccc></td><td id=mytd align=center onMouseOver=this.style.background='#f7f7f7' onMouseOut=this.style.background='' onclick=location.href='list.php?board_id=0401&category=&search=subject&key_word=&pagenum=3'>4</td><td  width=1 nowrap bgcolor=#cccccc></td><td id=mytd align=center onMouseOver=this.style.background='#f7f7f7' onMouseOut=this.style.background='' onclick=location.href='list.php?board_id=0401&category=&search=subject&key_word=&pagenum=4'>5</td><td  width=1 nowrap bgcolor=#cccccc></td><td id=mytd align=center onMouseOver=this.style.background='#f7f7f7' onMouseOut=this.style.background='' onclick=location.href='list.php?board_id=0401&category=&search=subject&key_word=&pagenum=5'>6</td><td  width=1 nowrap bgcolor=#cccccc></td><td id=mytd align=center onMouseOver=this.style.background='#f7f7f7' onMouseOut=this.style.background='' onclick=location.href='list.php?board_id=0401&category=&search=subject&key_word=&pagenum=6'>7</td><td  width=1 nowrap bgcolor=#cccccc></td><td id=mytd align=center onMouseOver=this.style.background='#f7f7f7' onMouseOut=this.style.background='' onclick=location.href='list.php?board_id=0401&category=&search=subject&key_word=&pagenum=7'>8</td><td  width=1 nowrap bgcolor=#cccccc></td><td id=mytd align=center onMouseOver=this.style.background='#f7f7f7' onMouseOut=this.style.background='' onclick=location.href='list.php?board_id=0401&category=&search=subject&key_word=&pagenum=8'>9</td><td  width=1 nowrap bgcolor=#cccccc></td><td id=mytd align=center onMouseOver=this.style.background='#f7f7f7' onMouseOut=this.style.background='' onclick=location.href='list.php?board_id=0401&category=&search=subject&key_word=&pagenum=9'>10</td><td  width=1 nowrap bgcolor=#cccccc></td><td style='padding-left:9'><a href = 'list.php?board_id=0401&category=&search=subject&key_word=&pagenum=10'><img src='skin/default/image/next.gif' border=0 align=absmiddle></a></td>									</tr>
								</table>
							</td>
						</tr>
					</table>
<!-- 하단 페이지그룹 부분 높이 50px end -->


				</td>
			</tr>

			<tr>
				<td height="1" bgcolor="e6e6e6" colspan="5"></td>
			</tr>

<!-- 버튼정의 및 히든변수 start -->
		  <tr>
			  <td height="50" colspan="5" align="center">
					<table width="97%" border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td width="50%"></td>
							<td width="50%" align="right"></td>
						</tr>
					</table>
				</td>
			</tr>
		 <input type="hidden" name="board_id" value="0401">
		 <input type="hidden" name="category" value="">
		 <input type="hidden" name="search" value="subject">
		 <input type="hidden" name="key_word" value="">
		 <input type="hidden" name="pagenum" value="">
		 <input type="hidden" name="mode" value="del_check">
		 <input type="hidden" name="return_category" value="">
<!-- 버튼정의 및 히든변수 end -->




		</form>
		</table>


		</td>
	</tr>
</table>

</td></tr></table>
</body>

</html>