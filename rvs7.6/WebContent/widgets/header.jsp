<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page language="java" pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" isELIgnored="false"%>

<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css" href="css/lte-style.css">
<script type="text/javascript" src="js/common/message_detail.js"></script>

<style>
.menulink {
	font-size: 16px;
	color: white;
	float: right;
	right: 4px;
	margin: 4px;
}

.menulink:hover {
	color: #FFB300;
	cursor: pointer;
}

.menulink.icon-calendar-2 {
	position:relative;
}

.menulink.icon-calendar-2 .expland-menu {
	position:absolute;
background-color: #93c3cd;
/*background-image: linear-gradient(45deg, transparent 50%, rgba(255,255,255,.5) 50%);
background-size: 10px 60px;*/
	z-index: 100;
}

.menulink.icon-calendar-2 .expland-menu > div {
	float: left;
}

.littleball {
	font-size: 10px;
	-moz-border-radius-topleft: 8px;
	-webkit-border-top-left-radius: 8px;
	-khtml-border-top-left-radius: 8px;
	border-top-left-radius: 8px;
	-moz-border-radius-bottomleft: 8px;
	-webkit-border-bottom-left-radius: 8px;
	-khtml-border-bottom-left-radius: 8px;
	border-bottom-left-radius: 8px;
	-moz-border-radius-topright: 8px;
	-webkit-border-top-right-radius: 8px;
	-khtml-border-top-right-radius: 8px;
	border-top-right-radius: 8px;
	-moz-border-radius-bottomright: 8px;
	-webkit-border-bottom-right-radius: 8px;
	-khtml-border-bottom-right-radius: 8px;
	border-bottom-right-radius: 8px; //
	width: 180px;
	height: 18px;
	text-align: center; //
	background-color: green;
	padding: 1px;
}

.littleball.blinking {
	background-color: #FFB300;
	display: inline-block;
	width:1.4em;
	height:1.4em;
}

.finish_time_left {
	text-align:center;
	line-height:22px;
	width:48px;
	margin:0 auto;
	border-radius:10px;
	border-style:solid;
	border-width:1px;
	border-color:blue;
	color:green;
	background-color:white;
}
.time_overed {
	color:white;
	background-color:rgb(228,178,64);
}
</style>

	<% String sMessageType = (String)request.getAttribute("message_type"); %>

<% if (sMessageType != null) {  %>
<style>
.lightBox {
	position:fixed;
	right:4px;
	top:96px;
	background-color:white;
	padding: 0.2em 1em;
	border: 2px solid rgb(147, 195, 205);
	border-radius: .5em;
	opacity: 0.8;
	cursor: pointer;
	z-index:4;
	-moz-user-select:none;
	-webkit-user-select:none;
	user-select:none;
	box-shadow: 0 0 0 2px white;
}
.lightBox:hover {
	opacity: 1;
}
.lightBox table {
	border-collapse: collapse;
}
.lightBox.overtime {
	border: 2px solid rgb(255, 128, 0);
}
.lightBox .your {
	background-color: rgb(147, 195, 205);
}
.lightBox.overtime .your {
	background-color: rgb(255, 210, 176);
}
.lightBox.overtime td.overtime {
	background-color: red;
}
.lightBox tbody td {
	padding:0 .3em;
}
</style>
<% }  %>

	<div class="areabase">
		<img src="images/logo-rvs.png" style="margin-top: 7px; float: left"></img>
		<div>
			<div class="menulink icon-switch">退出</div>
			<div class="menulink icon-home">首页
			<input type="hidden" id="op_id" value="${userdata.operator_id}"/>
			<input type="hidden" id="submenu" value="${retSub}"/>
			</div>
			<div class="menulink icon-help">查询</div>
			<div class="menulink icon-list">点检</div>
			${(retPartialLink eq "1") ? '<div class="menulink icon-cog">零件</div>' : ''}
			${(needMenu eq "1") ? '<div class="menulink icon-calendar-2">菜单<div class="expland-menu ui-accordion" id="accordion"></div></div>' : ''}
			<div style="height:29px;line-height:29px;float:right;margin-right: 16px;">
				<span style="color:white;font-size:14px;">您好! ${userdata.name}${userdata.role_name}。</span>
				<span style="color:white;font-size:14px;cursor:pointer;" id="userPosition">${userPosition}</span>
			</div>
			${(needMessageBox eq "1") ? '<div class="clear menulink icon-bell">提示信息<span class="littleball">0</span>条</div>' : ''}

<% if (sMessageType != null) {  %>
			<input type="hidden" id="ro_mt_id" value="<%=sMessageType%>"/>
			<div class="lightBox" style="display:none;">
				<table>
					<thead><tr>
						<th colspan="3">等待作业相关中小修</th>
					</tr></thead>
					<tbody></tbody>
				</table>
			</div>
<script type="text/javascript">
	var dragged = false;
	var xM = 0;
	var yM = 0;
	var $lightBox = $(".lightBox");
	$lightBox
	.bind("mousedown", function(evt){
		dragged = true;xM = evt.pageX;yM = evt.pageY;} );
	$("body")
	.bind("mouseup", function(){dragged = false} )
	.bind("mousemove", function(evt){
		if (dragged) {
			var xD = evt.pageX - xM;
			var yD = evt.pageY - yM;
			
			$lightBox.css("right",(parseInt($lightBox.css("right")) - xD) + "px");
			$lightBox.css("top",(parseInt($lightBox.css("top")) + yD) + "px");

			xM = evt.pageX;
			yM = evt.pageY;
		}
	} );
	var refreshLightWaiting = function(list){
		if (!list || list.length == 0) {
			$lightBox.hide();
			return;
		} else {
			$lightBox.show();
			var tBodyContent = "";
			for (var idx in list) {
				var item = list[idx];
				if (item.assigned_operator_id) {
					tBodyContent += "<tr class='your'>";
				} else {
					tBodyContent += "<tr>";
				}
				tBodyContent += "<td material_id='" + item.material_id +"'>"+item.omr_notifi_no+"</td><td position_id='" + item.position_id +"'>"+item.process_code+"</td>";
				tBodyContent += checkLWTime(item.in_place_time);
<% if ("le".equals(sMessageType)) {  %>
				var tempAssignedOperatorOption = "";
				var belongsOperatorOptions = "";
				for (var iB in header_belongs) {
					var bOperator = header_belongs[iB];
					var belongsOperatorOption = '<option value="' + bOperator.operator_id + '">' + bOperator.name + '</option>';
					if (bOperator.operator_id == item.assigned_operator_id) {
						if (item.assigned_flg == 0) {
							tempAssignedOperatorOption = '<option value="0">*' + bOperator.name + '</option>';
						} else {
							belongsOperatorOption = '<option value="' + bOperator.operator_id + '" selected>' + bOperator.name + '</option>';
						}
					}
					belongsOperatorOptions += belongsOperatorOption;
				}
				tBodyContent += '<td><select>' + tempAssignedOperatorOption + belongsOperatorOptions + '</select></td>';
<% }  %>
				tBodyContent += "</tr>";
			}
			$lightBox.find("tbody").html(tBodyContent);
<% if ("le".equals(sMessageType)) {  %>
			$lightBox.on("change", "select", function(){
				var bAssignOperatorId = this.value;
				if (bAssignOperatorId == "0") return;
				var $bl_ATr = $(this).parent().parent();
				if(operator_ws) operator_ws.send('assignOperator:{"material_id":"'+$bl_ATr.children().eq(0).attr("material_id")
					+'","position_id":"'+$bl_ATr.children().eq(1).attr("position_id")+'","operator_id":"'+bAssignOperatorId+'"}');
			})
<% }  %>
			if (tBodyContent.indexOf("overtime") >= 0) {
				$lightBox.addClass("overtime");
			} else {
				$lightBox.removeClass("overtime");
			} 
		}
	}
	var checkLWTime = function(wTime) {
		var now = new Date().getTime();
		var wTimeDate = new Date(wTime);
		var wTimelimit = 1200000;
<% if ("le".equals(sMessageType)) {  %>
		wTimelimit = 3600000;
<% }  %>
		if (now - wTime > wTimelimit) {
			return "<td class='overtime'>" + fillZero("" + wTimeDate.getHours()) + ":" + fillZero("" + wTimeDate.getMinutes()) + "</td>";
		} else {
			return "<td>" + fillZero("" + wTimeDate.getHours()) + ":" + fillZero("" + wTimeDate.getMinutes()) + "</td>";
		}
	}
</script>
<% }  %>

		</div>
	</div>
	<div class="clear" style="height: 10px;"></div>

	<div class="hidemessage" id="hidemessage">
		<div id="messagecontainner" style="float: left;display: none;">
			<div id="messagearea">
				<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser dwidth-right">
					<span class="areatitle">消息一览</span>
				</div>
				<div class="ui-state-default dwidth-right" id="message_contents">
				</div>
				<div class="clear"></div>
			</div>
		</div>
	</div>
	<div id="process_resign"/>
<script type="text/javascript">
var headerServicePath = "header.do";
var selectedMaterial = {};
var header_belongs = {};
var header_holidays = ${header_holidays};
var header_today_holiday = ${today_holiday};
var header_getInholidays = function(month){
	if (month)
		$.ajax({
			beforeSend : ajaxRequestType,
			async : false,
			url : 'holiday.do?method=getHolidays',
			cache : false,
			data : {month : month},
			type : "post",
			dataType : "json",
			success : ajaxSuccessCheck,
			error : ajaxError,
			complete : function(xhjObj) {
				var resInfo = $.parseJSON(xhjObj.responseText);
				header_holidays[month] = resInfo.signed;
			}
		});
};

var closePostMessage = function(post_message_id){
	$.ajax({
		beforeSend : ajaxRequestType,
		async : true,
		url : headerServicePath + '?method=doReadPostMessage',
		cache : false,
		data : {post_message_id : post_message_id.replace("p_", "")},
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : getMessage_complete
	});
};

$("#message_contents").on("mouseenter", "a.areacloser", 
	function() {$(this).addClass("ui-state-hover");}
);
$("#message_contents").on("mouseleave", "a.areacloser", 
	function() {$(this).removeClass("ui-state-hover");}
);
$("#message_contents").on("click", "a.areacloser", 
	function() {
		var alarm_messsage_id = $(this).attr("refid");
		if (alarm_messsage_id.indexOf("p") == 0) {
			closePostMessage(alarm_messsage_id);
		} else {
			popMessageDetail(alarm_messsage_id, true);
		}
	}
);
$("#message_contents").on("mouseenter", "div > .m_title", function(){
	$(this).next().show("blind");
});

$("#message_contents").mouseleave(function(){
	$("#message_contents > div > .m_content").hide("blind");
});

var alarm_counts = 0; // -1
var getMessage_complete = function(xhrobj, textStatus) {
	var resInfo = null;
	try {
		// 以Object形式读取JSON
		eval('resInfo =' + xhrobj.responseText);
		$(".littleball").text(resInfo.alarm_counts);
		var new_alarm_counts = parseInt(resInfo.alarm_counts)
		if (new_alarm_counts > alarm_counts) {
			// if (alarm_counts >= 0 || new_alarm_counts > 8)
				$(".littleball").addClass("blinking");
		}
		alarm_counts = new_alarm_counts;

		var alarm_show_counts = resInfo.alarms.length;

		if (alarm_show_counts === 0) {
			$("#message_contents").html("<span>没有未处理的警告。</span>");
		} else {
			$("#message_contents").html(resInfo.alarms);
			$("#message_contents > div > .m_content").hide();
		}
	} catch (e) {
		alert("name: " + e.name + " message: " + e.message + " lineNumber: "
				+ e.lineNumber + " fileName: " + e.fileName);
	};
};

var refreshMes = function(){
	$.ajax({
		beforeSend : ajaxRequestType,
		async : true,
		url : headerServicePath + '?method=getMessage',
		cache : false,
		data : null,
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : getMessage_complete
	});
}

$(function() {
	setInterval(refreshTargetTimeLeft, 60000);

	$(".areabase .icon-switch").click(function() {
		window.location.href = "login.do?method=logoff";
	});
	$(".areabase .icon-home").click(function() {
		window.location.href = "panel.do";
	});
	$(".areabase .icon-help").click(function() {
		window.location.href = "material.do";
	});
	if ($(".areabase .icon-calendar-2").length > 0) {
		$(".areabase .icon-calendar-2").click(function() {
			if (!$(".expland-menu").html()) {
				var $explandMenu = $(".expland-menu");
				$explandMenu.load("appmenu.do?method=" + ($("#submenu").val() || "") + "init&ex=true", function(){
					var $explandMenuItem = $explandMenu.children("div");
					var menu_count = $explandMenuItem.length;

					if (menu_count >= 4){
						$explandMenu.css({
						left: "-560px",
						width: "896px"
						})
					} else if (menu_count == 3){
						$explandMenu.css({
						left: "-336px",
						width: "672px"
						})
					} else if (menu_count == 2){
						$explandMenu.css({
						left: "-112px",
						width: "448px"
						})
					}
					
					var maxHeight = 0;
					for (var iM = 0;iM < menu_count;iM++ ) {
						var tHeight = $explandMenuItem.eq(iM).children("div").height();
						if (tHeight > maxHeight) {
							maxHeight = tHeight;
						}
						if (iM % 4 == 3 || iM == menu_count - 1) {
							var iN = iM - iM % 4;
							while (iN <= iM) {
								$explandMenuItem.eq(iN).children("div").css("height", maxHeight);
								iN++;
							}
							maxHeight = 0;
						}
					};
					$explandMenu.css("display", "none");
					$explandMenu.toggle("fold");
				});
			} else {
				$(".expland-menu").toggle("fold");
			}
		});
	}
	$(".areabase .icon-bell, #messagearea div:first").click(function() {
		$(".littleball").removeClass("blinking");
		$("#messagecontainner").toggle("fold");
	});
	$(".areabase .icon-cog").click(function() {
		window.location.href = "header.do?method=pinit";
	});
	$(".areabase .icon-list").click(function() {
		window.location.href = "header.do?method=iinit";
	});

	$("#userPosition").click(function(){
		var txt = $(this).text();
		if (txt && txt.indexOf("工位") >= 0) {
			window.location.href = "position_panel.do";
		}
	});

	refreshMes();
});
</script>



<script type="text/javascript">
var operator_ws = null;

$(function() {
	if (typeof (WebSocket) == "undefined") {
		return;
	}

	try {
	// 创建WebSocket  
	operator_ws = new WebSocket(wsPath + "/operator");
	// 收到消息时在消息框内显示  
	operator_ws.onmessage = function(evt) {
    	var resInfo = {};
    	try {
    		eval("resInfo=" + evt.data);
    		if ("conrrupted" == resInfo.method) {
    			var allowed = false;
    			var errorData = "RVS系统不支持单个用户在多窗口工作，请保持只有一个窗口登录。";
				if ($('div#errstring').length == 0) {
					$("body").append("<div id='errstring'/>");
				}
				$('div#errstring').show();
				$('div#errstring').dialog({dialogClass : 'ui-error-dialog', modal : true, width : 450, title : "在线冲突", 
					buttons : {"重新连接" : function() { allowed = true; $(this).dialog("close"); operator_ws.send("entach:"+$("#op_id").val() + ($("#ro_mt_id").val() ? ("+" + $("#ro_mt_id").val()) : ""));},
						"退出登录" : function() { $(this).dialog("close"); }},
					close : function() { if(!allowed) window.location.href = "login.do?method=logoff" }});
				$('div#errstring').html("<span class='errorarea'>" + errorData + "</span>");
    		} else if ("connectted" == resInfo.method) {
    			if (resInfo.belongs) {
    				header_belongs = resInfo.belongs;
    			}
    		} else if ("ping" == resInfo.method) {
    			operator_ws.send("pong:"+resInfo.id + "+" + operator_ws.readyState);
    		} else if ("message" == resInfo.method) {
    			refreshMes();
    		} else if ("afRefresh" == resInfo.method) {
    			afObj.refresh();
    		} else if ("light" == resInfo.method) {
    			if (typeof(refreshLightWaiting) === "function") refreshLightWaiting(resInfo.list);
    		}
    	} catch(e) {
    	}
        // $('#msgBox').append(evt.data);  
        // $('#msgBox').append('</br>');  
	};  
	// 断开时会走这个方法  
	operator_ws.onclose = function() {   
	};  
	// 连接上时走这个方法  
	operator_ws.onopen = function() {     
		operator_ws.send("entach:"+$("#op_id").val() + ($("#ro_mt_id").val() ? ("+" + $("#ro_mt_id").val()) : ""));
<% if (sMessageType != null) {  %>
		operator_ws.send("callLight:");
<% } %>
	}; 
	} catch(e) {
	}
});
</script>

<script type="text/javascript">
var date_time_list = [];

date_time_list.add = function($target) {
	if (!$target.id) {
		this.push($target);
	} else {
		for (var iDtl in date_time_list) {
			var dtl = date_time_list[iDtl]
			if (dtl.id == $target.id) {
				this.splice(iDtl, 1, $target);
				return;
			}
		}
		this.push($target);
	}
}

var refreshTargetTimeLeft = function(init_flg){
	var current_time = new Date();
	var obj_time = null;
	var start_time = null;
	var end_time = null;
	var disp_flg = "";

	//是否为休息鈤或休息时间
	if ((!header_today_holiday && isWorkDayTime(current_time)) || init_flg == 1) {
		$.each(date_time_list, function(idx,item) {
			if(!item.naturaltime){
				obj_time = new Date(item.date_time);
				if (item.permitMinutes > 0) {
					disp_flg = "permit";
					start_time = obj_time;
					end_time = current_time;
				} else {
					start_time = current_time;
					end_time = obj_time;
				}

				//是否为当日
				var ret = compareWithToday(obj_time, current_time);
				if (ret == 0) {
					var miniutes = getDiffMinutes(start_time, end_time);
					var hours_left = parseInt(Math.abs(miniutes) / 60);
					var miniutes_left = Math.abs(miniutes) % 60;
					item.target.text(hours_left + ":" + (miniutes_left < 10 ? ("0" + miniutes_left) : miniutes_left));
					if (disp_flg == "permit") {
						if (miniutes > item.permitMinutes) {
							item.target.addClass("time_overed");
						}
					} else {
						if (miniutes < 0) {
							item.target.addClass("time_overed");
						}
					}
				} else if (ret == -1) {
					item.target.text(item.date_time.substring(5,10));
					item.target.addClass("time_overed");
				} else if (ret == 1) {
					item.target.text(item.date_time.substring(5,10));
				}
			}
		});
	}
	
	//自然计时
	$.each(date_time_list, function(idx,item) {
		obj_time = new Date(item.date_time);
		if (item.naturaltime) {
			var end_minutes = current_time.getHours() * 60 + current_time.getMinutes();
			var start_minutes = obj_time.getHours() * 60 + obj_time.getMinutes();
			
			var miniutes = end_minutes - start_minutes;
			var hours_left = parseInt(Math.abs(miniutes) / 60);
			var miniutes_left = Math.abs(miniutes) % 60;
			item.target.text(hours_left + ":" + (miniutes_left < 10 ? ("0" + miniutes_left) : miniutes_left));
			
			var drying_time = parseInt(item.drying_time);
			if(miniutes > drying_time){
				item.target.addClass("time_overed");
			}
			
		}
	});
	
};

var getDiffMinutes = function(start_dateTime, end_dateTime){
	var start_minutes;
	var end_minutes;
	var minutes;
	var nega;

	start_minutes = start_dateTime.getHours() * 60 + start_dateTime.getMinutes();
	end_minutes = end_dateTime.getHours() * 60 + end_dateTime.getMinutes();

	if (start_minutes > end_minutes) {
		var cache = start_minutes;
		start_minutes = end_minutes;
		end_minutes = cache;
		nega = -1;
	} else {
		nega = 1;
	}

	if (start_minutes < 8 * 60 + 45) {
		start_minutes = 8 * 60 + 45;
	}

	if (end_minutes > 17 * 60 + 10) {
		end_minutes = 17 * 60 + 10;
	}

	minutes = end_minutes - start_minutes;

//	if (start_minutes < 11 * 60 && end_minutes > 11 * 60) {
//		if (end_minutes > 11 * 60 + 10) {
//			minutes = minutes - 10;
//		} else {
//			minutes = minutes - (end_minutes - (11 * 60));
//		}
//	}

	if (start_minutes < 11 * 60 + 55 && end_minutes > 11 * 60 + 55) {
		if (end_minutes > 12 * 60 + 45) {
			minutes = minutes - 50;
		} else {
			minutes = minutes - (end_minutes - (11 * 60 + 55));
		}
	}

	if (start_minutes < 15 * 60 + 30 && end_minutes > 15 * 60 + 30) {
		if (end_minutes > 15 * 60 + 40) {
			minutes = minutes - 10;
		} else {
			minutes = minutes - (end_minutes - (15 * 60 + 30));
		}
	}

	return minutes * nega;
};

var compareWithToday = function(d_dateTime, d_currentTime){
	var yesterday = addDaysForDate(d_currentTime, -1).getTime();
	var tomorrow = addDaysForDate(d_currentTime, 1).getTime();
	var expected_date = new Date(d_dateTime.getFullYear(), d_dateTime.getMonth(), d_dateTime.getDate()).getTime();
	if (expected_date <= yesterday) {
		return -1;
	} else if (expected_date >= tomorrow) {
		return 1;
	}
	return 0;
}

var addDaysForDate = function(d_dateTime, i_days){
	var cache_date = new Date();
	cache_date.setTime(d_dateTime.getTime());
	var date = cache_date.getDate();
	date = date + i_days;
	cache_date.setDate(date);
	return new Date(cache_date.getFullYear(), cache_date.getMonth(), cache_date.getDate());
}

var isWorkDayTime = function(d_dateTime){
	var current_minutes = d_dateTime.getHours() * 60 + d_dateTime.getMinutes();

	if (current_minutes < 8 * 60 + 45) {
		return false;
	}

	if (current_minutes > 17 * 60 + 10) {
		return false;
	}

	// 11:00-11:10
	if (current_minutes > 11 * 60 && current_minutes < 11 * 60 + 10) {
		return false;
	}

	// 11:55-12:45
	if (current_minutes > 11 * 60 + 55 && current_minutes < 12 * 60 + 45) {
		return false;
	}

	// 15:30-15:40
	if (current_minutes > 15 * 60 + 30 && current_minutes < 15 * 60 + 40) {
		return false;
	}

	return true;
};


</script>

<% String sIndirectWorker = (String)request.getAttribute("indirect_worker"); %>
<% if ("".equals(sIndirectWorker)) { %>
<script type="text/javascript">
var afObj = (function() {
return {
	applyProcess : function(process_type, call_obj, call_method, call_params) {
		call_method.apply(call_obj, call_params);
	},
	refresh : function() {// do Nothing
	}
}
})();
</script>
<% } else { %>
<jsp:include page="/widgets/af_timer.jsp" flush="true"></jsp:include>
<% } %>
	</div>


