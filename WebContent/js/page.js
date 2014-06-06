/*
 * Copyright (C) 2011 WebSquared Inc. http://websqrd.com
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

function addField(obj){
	thisId = obj.attr('id');
	//nextId = makeNextSiblingId(thisId);
	obj.after('<tr>'+
				'<td class="first">▶</td>'+
				'<td>'+
				'	<pre><select style="width:73px;" id="fieldType" onchange="javascript:selectSite(this)">'+
				'	<option value="field" selected >필드</option>'+
				'	<option value="block" >블럭</option>'+
				'	<option value="linkBlock" >링크블럭</option>'+
				'	</select></pre>'+
				'</td>'+
				'<td>'+
				'	<input style="float:left;" class="inp_full" size="10" type="text" id="fieldName" name="fieldName" >'+
				'</td>'+
			    '<td>'+
			   	'	<input style="float:left;" class="inp_full" size="22" type="text" id="xpath" name="xpath" >'+
			    '</td>'+
			    '<td>'+
			   	'	<a href="javascript:testXPath()" class="btn_edit">T</a>'+
			   	'	<a href="javascript:stepInto()" class="btn_edit">Into</a>'+
			   	'	<a href="javascript:addField()" class="btn_edit">[+]</a>'+
			   	'</td>'+
			   	'</tr>');
}

function makeNextSiblingId(id){
	tmp = makeNextSiblingId.splt("_");
	tmp.length
}

function fillSource(url, enc){
	$.post("siteService.jsp", {cmd:2, url: $("#url").val(), enc: $("#enc").val()},  
		function(data) {
			data = $.trim(data);
	        $("#sourceAreaCode").val(data);
	        $("#sourceAreaCount").text("1");
		}
	);
}

function closeTestArea(){
	$("#testArea").hide();
}

function testXPath(){
	var xpath = $("#rootpath").val();
	var source = $("#sourceAreaCode").val();
    $.ajax({
		type: 'POST',
	    url: "siteService.jsp",
	    data: {cmd:4, xpath: xpath, source:source},
	    success: function(data){
	    	result = parseResult($.trim(data));
	    	$("#testAreaCount").text(result[0]);
	    	$("#testAreaCode").text(result[1]);
	  	},
	  	error:function(data){
	    	alert(data);
	  	}
	});
}

function stepInto(){
	var xpath = $("#rootpath").val();
	var source = $("#sourceAreaCode").val();
    $.ajax({
		type: 'POST',
	    url: "siteService.jsp",
	    data: {cmd:4, xpath: xpath, source:source},
	    success: function(data){
	    	result = parseResult($.trim(data));
	    	$("#sourceAreaCount").text(result[0]);
	    	$("#sourceAreaCode").val(result[1]);
	    	$("#testAreaCount").text("0");
	    	$("#testAreaCode").text("");
	    	$("#testArea").hide();
	  	},
	  	error:function(data){
	    	alert(data);
	  	}
	});
}

function parseResult(data){
	var result = {};
	if(document.all) { // IE
		p = data.indexOf("\r\n");
	}else { //Mozilla
		p = data.indexOf("\n");
	}
	
	if(p == -1){
		result[0] = 0;
		result[1] = "";
	}else{
		result[0] = data.substr(0, p);
		result[1] = data.substr(p + 1);
	}
	return result;
}
 


