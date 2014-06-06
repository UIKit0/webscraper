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

var update_event_url = "/admin/management/moniteringService.jsp";
var SE_UPDATE_FAIL_COMM = "이벤트내역 업데이트에 실패했습니다.";

// login form class change
function chgClass(cv){
	var cid = document.getElementById("id");
	var cpw = document.getElementById("pw");
	if (cv == "id") cid.className = "inp_log";
	else{
		if (cid.value != "") cid.className = "inp_log";
		else cid.className = "inp_log_id";
	}
	if (cv == "pw") cpw.className = "inp_log";
	else{
		if (cpw.value != "") cpw.className = "inp_log";
		else cpw.className = "inp_log_pw";
	}
}

//popup    
var tip={
	$:function(ele){ 
	    if(typeof(ele)=="object") 
	        return ele; 
	    else if(typeof(ele)=="string"||typeof(ele)=="number") 
	        return document.getElementById(ele.toString()); 
	        return null; 
    }, 
    mousePos:function(e){ 
        var x,y; 
        var e = e||window.event; 
        return{x:e.clientX+document.body.scrollLeft+document.documentElement.scrollLeft,y:e.clientY+document.body.scrollTop+document.documentElement.scrollTop}; 
    }, 
    start:function(obj){ 
        var self = this; 
        var t = self.$("mjs:tip"); 
        obj.onmousemove=function(e){ 
            var mouse = self.mousePos(e);
            var wx;
            var selectSize = $("#sizeSelector").find("option:selected").val();
			switch (selectSize){
			   case "1240":
			     wx = 320;
			     break;
			   case "1024":
			     wx = 420;
			     break;
			   case "100":
			     wx = -10;
			     break;
			   case "90":
			     wx = 80;
			     break;
			   default:
			}
            t.style.left = mouse.x - wx + 'px'; 
            t.style.top = mouse.y - 120 + 'px'; 
            var tips = obj.getAttribute("tips");
			if("" == tips){
				return;
			}
            t.innerHTML = tips;
            t.style.display = ''; 
        } 
        obj.onmouseout=function(){ 
            t.style.display = 'none'; 
        } 
    } 
}
   
function handleEvent(id){
		$.ajax({
		    url: update_event_url,
		    data: {id: id, status:"T", cmd:0},
		    success: function (data){
		    	if($.trim(data) == "success"){
		    		$("#td_"+id).html("처리됨");
		    	}else{
		    		alert(SE_UPDATE_FAIL_COMM);
		    	}
		    }
    	});
		return;
} 

function expandEvent(id){
		var stacktrace = $("#tips_"+id).attr("tips");
		var tb = $("#td_"+id).parent();
		var nextTb = tb.next();
		if(nextTb.children().length > 1 || nextTb.children().length == 0){
			tb.after("<tr style='background:#e3e3e3;'><td colspan=5 style='text-align:left;'>"+stacktrace+"</td></tr>");
		}else{
			nextTb.remove();
		}
}

