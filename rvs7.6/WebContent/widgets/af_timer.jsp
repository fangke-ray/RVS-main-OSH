<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page language="java" pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" isELIgnored="false"%>

<style>
#af_timer {
	width:120px;
	height:120px;
	border-radius: 60px;
	position:fixed;
	opacity: 0.75;
	cursor: pointer;
	-moz-user-select:none;
	-webkit-user-select:none;
	user-select:none;
	top:0;
	z-index:999;
}
#af_timer:hover {
	opacity: 1;
}
#af_holder {
	width:100%;
	height:100%;
	border:1px solid lightgray;
	background-color: white;
	border-radius: 60px;
	position: absolute;
	overflow: hidden;
}
#af_holder div {
	background-color : #ADEDFF;
}
#af_holder div[process="1"] {
	background-color : #E7FDC9;
	box-shadow : 1px -1.5px 1px #96F713;
}
#af_holder div[process="2"] {
	background-color : #FEFEB3;
	box-shadow : 1px -1.5px 1px #FCFC0C;
}
#af_holder div[process="3"] {
	background-color : #FFCBA1;
	box-shadow : 1px -1.5px 1px #F69240;
}
#af_timer .prod {
	position: absolute;
	font-size: 16px;
	display: block;
	text-align: center;
	pointer-events: none;
}
#af_timer .prod[line="1"] {
	top: 48px;
	left:1px;
	width:116px;
}
#af_timer .prod[line="2"] {
	top: 40px;
	left:4px;
	width:110px;
}
#af_timer .prod[line="3"] {
	top: 30px;
	left: 8px;
	width: 108px;
}
#af_process, #af_standard {
	position: absolute;
	display: inline;
	padding: 0 0.5em;
}
#af_standard {
	border : 1px solid #B7DEE8;
	border-top : 0;
	border-radius: 10px;
	pointer-events: none;
	box-shadow: 1px 1px 3px inset;
}

#af_standard:empty {
	display: none;
}
#af_process {
	background-color: lightblue;
}
#af_process[process="1"] {
	background-color : #E7FDC9;
}
#af_process[process="2"] {
	background-color : #FEFEB3;
}
#af_process[process="3"] {
	background-color : #FFCBA1;
}
#af_timer li {
	list-style: none;
	border-radius: 6px;
	text-align:left;
	margin: 0.5em;
	overflow: hidden;
	height: 1.5em;
	filter: drop-shadow(0 5px 1px gray);
}
#af_timer li.selected {
	filter: brightness(66%);
}
#af_timer li:hover {
	background-color : #f0f0f0;
}
#af_timer[moving] ul,
#af_timer[switch="no"] ul {
	display: none;
}
#af_pause_reason_group, #af_abilities_group {
	position: absolute;
	width: 120px;
	padding : 0.5em 0 0.5em 0;
	font-size: 18px;
}
#af_pause_reason, #af_abilities {
	position: absolute;
	width: 180px;
	padding : 0.5em 0 0.5em 0;
	font-size: 16px;
}
#af_abilities_group li, #af_abilities li {
	border: 1px solid #839C51;
	background-color: #C1D39D;
}
#af_pause_reason_group li {
	border: 1px solid #4D7399;
	background-color: #96B2D3;
	text-align:center;
}
#af_abilities_group li[len],
#af_pause_reason_group li[len] {
	font-size: 16px;
}
#af_pause_reason li {
	border: 1px solid #62A2B3;
	background-color: #98C9D6;
}
#af_pause_reason_group li[group="-1"] {
	background-color: #000000;
	color:white;
}
#af_timer[vdir="down"] #af_abilities_group {
	top : -30px; bottom : auto;
}
#af_timer[hdir="left"] #af_abilities_group {
	left : 120px; right : auto;
}
#af_timer[vdir="up"] #af_abilities_group {
	bottom : -30px; top : auto;
}
#af_timer[hdir="right"] #af_abilities_group {
	right : 120px; left : auto;
}
#af_timer[vdir="down"] #af_abilities {
	top : -30px; bottom : auto;
}
#af_timer[hdir="left"] #af_abilities {
	left : 240px; right : auto;
}
#af_timer[vdir="up"] #af_abilities {
	bottom : -30px; top : auto;
}
#af_timer[hdir="right"] #af_abilities {
	right : 240px; left : auto;
}
#af_timer[vdir="down"] #af_pause_reason_group {
	top : 90px; bottom : auto;
}
#af_timer[vdir="up"] #af_pause_reason_group {
	bottom : 110px; top : auto;
}
#af_timer .af_state_tool {
	position: absolute;
	display: none;
	width: 25%;
	height: 25%;
	background-color : #FFB300;
	color: white;
	text-align: center;
	left: 38.2%;
	top: 61.8%;
	border-radius: 50%;
}
#af_pause_reason_group {
	left : 0;
}
#af_timer[vdir="down"] #af_pause_reason {
	top : 90px; bottom : auto;
}
#af_timer[hdir="left"] #af_pause_reason {
	left : 240px; right : auto;
}
#af_timer[vdir="up"] #af_pause_reason {
	bottom : 90px; top : auto;
}
#af_timer[hdir="right"] #af_pause_reason {
	right : 240px; left : auto;
}
#af_part_order_editor > table > tbody > tr > td {
	border : 1px solid darkgray;
	vertical-align: top;
}
#af_part_order_self th, #af_part_order_sap th {
	width : 80px;
}
#af_part_order_self td, #af_part_order_sap td {
	text-align : center;
}
#af_part_order_editor .part_order_highlight {
	background-color : paleturquoise;
}
</style>

<div id="af_timer" switch="no" hdir="left" vdir="down">
<div id="af_holder"><div></div></div>
<div id="af_standard"></div>
<div id="af_process"></div>
<span class="prod" line=1></span>
<ul id="af_pause_reason_group">
	<li group="WY">间接作业</li>
	<li group="MD">管理时间</li>
	<li group="MW">等待时间</li>
	<li group="H">休息离线</li>
	<li group="-1"><label>◑ 下班</label></li>
</ul>
<ul id="af_pause_reason">
</ul>
<ul id="af_abilities_group"></ul>
<ul id="af_abilities">
</ul>
<div class="af_state_tool" for="221">▲</div>
<div class="af_state_tool" for="164">▲</div>
</div>

<script type="text/javascript">
var afObj = (function() {

var afInit = function() {
	if (typeof(ajaxRequestType) == "undefined") {
		setTimeout(function(){refreshAf("init");}, 1000); // setTimeout(afInit, 1000);
	} else {
		refreshAf("init");
	}
} 

var refreshAf = function(init) {
	$.ajax({
		beforeSend : ajaxRequestType,
		async : true,
		url : 'af_production_feature.do?method=getByOperator',
		cache : false,
		data : {"init": init},
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : function(xhjObj) {
			var resInfo = $.parseJSON(xhjObj.responseText);
			if (resInfo.afAbilities) setAfAbilities(resInfo.afAbilities);
			if (resInfo.pauseReasonGroup) {
				var prGrhtml = "";
				var prAidHtml = "";
				for (var prGr in resInfo.pauseReasonGroup) {
					var prOfGr = resInfo.pauseReasonGroup[prGr];
					if (prGr === "WDT") {
						for (var prCode in prOfGr) {
							var prName = prOfGr[prCode].split(":");
							prAidHtml += "<li group='" + prGr + "' code='" + prCode + "'>" + prName[1] + "</li>"
						}
					} else {
						for (var prCode in prOfGr) {
							var prName = prOfGr[prCode].split(":");
							prGrhtml += "<li group='" + prGr + "' code='" + prCode + "'>" + prName[1] + "</li>"
						}
					}
				}
				$("#af_pause_reason").html(prGrhtml)
					.find("li").hide();
				$("#af_abilities").append(prAidHtml);
			}

			if(resInfo.isManager) {
				$afTimer.data("is_manager", resInfo.isManager);
				if (resInfo.isManager == true) {
					$("#af_pause_reason_group li[group='-1']").text("◑ 关闭计时");
				}
			}

			clearInterval(af_clockTo);

			if(resInfo.processForm) {
				isClosed = false;
				setProcessForm(resInfo.processForm);
			} else {
				isClosed = true;
				showStop();
			}
		}
	});
}

var setProcessForm = function(processForm){
	var type_code = processForm.production_type;
	if (processForm.is_working === "0") {
		setProdText(type_code, "p");
		$("#af_pause_reason_group li[group='-1']").show();
	} else {
		setProdText(type_code, "a");
		if ($afTimer.data("is_manager") != true) {
			$("#af_pause_reason_group li[group='-1']").hide();
		}
	}
	now_standard = processForm.standard_minutes;
	$("#af_standard").text(now_standard || "");

	$afTimer.data({
		"action_time": new Date(processForm.action_time),
		"is_working": processForm.is_working
	});

	if (type_code == "221" || type_code == "164") {
		$(".af_state_tool[for='" + type_code + "']").show();
	} else {
		$(".af_state_tool").hide();
	}
	refreshRate();
	af_clockTo = setInterval(refreshRate, 60000);
}

var refreshRate= function() {
	var actmin = parseInt((new Date().getTime() - $afTimer.data("action_time").getTime()) / 60000);
	$("#af_process").text(actmin);
	if (!now_standard) {
		var rate = actmin / 30;
		setProcessClock(rate);
		$("#af_holder div").css({"height": "100%", "marginTop": "0%"})
			.removeAttr("process");
		$("#af_process").removeAttr("process");
	} else {
		var rate = actmin / now_standard;
		setProcessClock(rate);
		var percent = parseInt(rate * 100);
		if (percent > 100) percent = 100;
		var process = 0;
		if (percent > 90) {
			process = 3;
		} else if (percent > 75) {
			process = 2;
		} else {
			process = 1;
		}
		$("#af_holder div").css({"height": percent + "%", "marginTop": (100 - percent) + "%"})
			.attr("process", process);
		$("#af_process").attr("process", process);
	}
}
var setProdText = function(type_code, from) {
	var type_name = "";
	if (from === "p") {
		type_name = $("#af_pause_reason li[code=" + type_code + "]").text();
		if (!type_name) {
			type_name = $("#af_abilities li[group='WDT'][code=" + type_code + "]").text();
		}
	} else if (from === "a") {
		type_name = $("#af_abilities li[code=" + type_code + "]").eq(0).text();
	}
	var $afProd = $afTimer.find(".prod");
	$afProd.text(type_name);
	var iLineHeight = parseInt($afProd.css("lineHeight"));
	if (isNaN(iLineHeight)) {
		iLineHeight = parseInt($afProd.css("fontSize")) + 4;
	}
	var linec = parseInt($afProd.height() / iLineHeight);

	$afProd.attr("line", linec);
	if (linec > 1) {
		linec = parseInt($afProd.height() / iLineHeight);
		$afProd.attr("line", linec);
	}
	$afTimer.attr({"type_code": type_code, "from": from});
}

var setAfAbilities = function(afAbilities) {
	var afGrhtml = "";
	var afhtml = "";
	var groups = {};
	for (var idx in afAbilities) {
		var ab = afAbilities[idx];
		afhtml += "<li group='" + ab["line_name"] + "' code='" + ab["position_id"] + "'>" + ab["process_code"] + "</li>";
		groups[ab["line_name"]] = idx;
	}
	$("#af_abilities").html(afhtml)
		.find("li").hide();
	for (var group in groups) {
		afGrhtml += "<li group='" + group + (group.length > 5 ? "' len='" : "") + "'>" + group + "</li>";
	}
	afGrhtml += '<li group="WDT">辅助作业</li>';
	$("#af_abilities_group").html(afGrhtml);
}

var setInpagePos = function(){
	var aw = document.documentElement.clientWidth / 2
	if (aw < parseInt($afTimer.css("right")) + parseInt($afTimer.css("width"))) {
		$afTimer.attr("hdir", "left");
	} else {
		$afTimer.attr("hdir", "right");
	}

	var ah = document.documentElement.clientHeight / 2
	if (ah < parseInt($afTimer.css("top")) + parseInt($afTimer.css("height"))) {
		$afTimer.attr("vdir", "up");
	} else {
		$afTimer.attr("vdir", "down");
	}
}

var stateTool = function(){
	var type_code = $(this).attr("for");
	
	if($afTimer.data("is_working") == "1"){
		switch(type_code) {
			case "221" : showPartialOrderList();
				break;
		}
	}else{
		switch(type_code) {
			case "164" : showWastePartialList();
				break;
		}
	}
}

	var af_dragged = false;
	var af_xM = 0, saf_xM = 0;
	var af_yM = 0, saf_yM = 0;
	var $afTimer = $("#af_timer");
	var $afHolder = $("#af_holder");
	var now_standard = 0;
	var af_clockTo = null;
	var isClosed = false;

	$afHolder
	.bind("mousedown", function(evt){
		$afTimer.attr("moving", true);
		af_dragged = true;saf_xM = af_xM = evt.pageX;saf_yM = af_yM = evt.pageY;} )
	.bind("mouseup", function(){
		setInpagePos();
		if (Math.abs(af_xM - saf_xM) < 10 && Math.abs(af_yM - saf_yM) < 10 && !swtiching) {
			if ($afTimer.attr("switch") === "no") {
				if (!isClosed || $afTimer.data("is_manager") == true) {
					$("#af_pause_reason").find("li").hide();
					$("#af_abilities").find("li").hide();
					$("#af_abilities_group li.selected, #af_pause_reason_group li.selected").removeClass("selected");
					$afTimer.attr("switch", "yes");
				}
			} else {
				$afTimer.attr("switch", "no");
			}
		}
		$afTimer.removeAttr("moving");
		af_dragged = false;
		localStorage.setItem("aft_r", $afTimer.css("right"));
		localStorage.setItem("aft_t", $afTimer.css("top"));
	});
	$("body")
	.bind("mousemove", function(evt){
		if (af_dragged) {
			if (evt.pageX > document.documentElement.clientWidth - 20 ||
				evt.pageX < 20) {
				af_dragged = false;
			}

			if (evt.pageY - window.scrollY > document.documentElement.clientHeight - 20 ||
				evt.pageY - window.scrollY < 20) {
				af_dragged = false;
			}

			if (af_dragged) {
				var xD = evt.pageX - af_xM;
				var yD = evt.pageY - af_yM;

				var nRight = parseInt($afTimer.css("right")) - xD;
				var nTop = parseInt($afTimer.css("top")) + yD;
				if (nRight < -20) nRight = -20;
				if (nTop < -20) nTop = -20;

				$afTimer.css("right", nRight + "px");
				$afTimer.css("top", nTop + "px");

				af_xM = evt.pageX;
				af_yM = evt.pageY;
			}
		}
	} );

	$("#af_abilities_group").on("click", "li", function(){
		var group = $(this).attr("group");
		$("#af_pause_reason").find("li").hide();
		$("#af_abilities").find("li").hide()
			.filter("[group='" + group + "']").show();
		$("#af_abilities_group li.selected, #af_pause_reason_group li.selected").removeClass("selected");
		$(this).addClass("selected");
	});

	$("#af_abilities").on("click", "li", function(){
		var code = $(this).attr("code");
		var group = $(this).attr("group");
		if (group === "WDT") {
			doSwitch("0", code);
		} else {
			doSwitch("1", code);
		}

		$afTimer.attr("switch", "no");
	});

	$("#af_pause_reason_group").on("click", "li", function(){
		var group = $(this).attr("group");
		if (group == "-1") {
			closeWork();
			return;
		}

		$("#af_abilities").find("li").hide();
		$("#af_pause_reason").find("li").hide()
			.filter("[group='" + group + "']").show();
		$("#af_abilities_group li.selected, #af_pause_reason_group li.selected").removeClass("selected");
		$(this).addClass("selected");
	});

	$("#af_pause_reason").on("click", "li", function(){
		var code = $(this).attr("code");
		doSwitch("0", code);
		$afTimer.attr("switch", "no");
	});

	var obradius = $("#af_timer").width() / 2;
	$("#af_standard").css({"left" : (obradius * 0.75) + "px", "top" : "0px"});

	var initAft_r = parseInt(localStorage.getItem("aft_r"));
	// document.defaultView.screen.availWidth - 120 * 2
	if (initAft_r > 1126) initAft_r = 1126;
	else if (initAft_r < 0) initAft_r = 0;
	var initAft_t = parseInt(localStorage.getItem("aft_t"));
	if (initAft_t > 528) initAft_t = 528;
	else if (initAft_t < 0) initAft_t = 0;

	$afTimer.css("right", initAft_r + "px");
	$afTimer.css("top", initAft_t + "px");

	$afTimer.find(".af_state_tool").click(stateTool);
	afInit();

var setProcessClock = function(rate) {
	var posX = getClockPosX(rate) + 0.75;
	var posY = getClockPosY(rate) + 0.75;
	$("#af_process").css({"left" : (obradius * posX) + "px", "top" : (obradius * posY) + "px"});
}
var getClockPosX = function(rate){
	return Math.sin(rate*2*Math.PI);
}
var getClockPosY = function(rate){
	return -Math.cos(rate*2*Math.PI);
}

var closeWork = function(){
	if ($afTimer.data("is_manager") == true) {
		doEnd();
	} else {
		warningConfirm("您是否要完成当天所有的作业？", doEnd, null, "结束当前作业");
	}
}

var swtiching = false;
var doEnd = function(is_working, production_type, saraniCallback) {
	if (swtiching) return;

	swtiching = true;

	$afTimer.attr("switch", "no");

	$.ajax({
		beforeSend : ajaxRequestType,
		async : true,
		url : 'af_production_feature.do?method=doEnd',
		cache : false,
		data : null,
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : function(xhjObj) {
			var resInfo = $.parseJSON(xhjObj.responseText);

			showStop();

			swtiching = false;

			clearInterval(af_clockTo);

			if (typeof(saraniCallback) == "function") saraniCallback();
		}
	});
}

var showStop = function(){
	$afTimer.removeAttr("process").removeAttr("from");
	$("#af_timer .prod").text("停止计时").attr("line", 1);
	isClosed = true;
	now_standard = 0;
	$("#af_standard").text("");
	$("#af_process").text("");
	$("#af_holder div").css({"height": "0%", "marginTop": "100%"})
		.removeAttr("process");
	$("#af_pause_reason_group li[group='-1']").hide();
	$(".af_state_tool").hide();
}

var doSwitch = function(is_working, production_type, saraniCallback) {
	if (swtiching) return;

	swtiching = true;

	var isWorkingFromTo = $afTimer.data("is_working") + ">" + is_working;
	var postData = {"is_working" : isWorkingFromTo,  // 切换自直接/间接作业 + // 切换到直接/间接作业
		"production_type" : production_type // 作业内容
	};

	$.ajax({
		beforeSend : ajaxRequestType,
		async : true,
		url : 'af_production_feature.do?method=doSwitch',
		cache : false,
		data : postData,
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : function(xhjObj) {
			var resInfo = $.parseJSON(xhjObj.responseText);
			clearInterval(af_clockTo);
			if (resInfo.errors.length == 0) {
				setProcessForm(resInfo.processForm);
			}
			swtiching = false;
			if (typeof(saraniCallback) == "function") saraniCallback();
		}
	});
}

var showPartialOrderList = function(){
	$.ajax({
		beforeSend : ajaxRequestType,
		async : true,
		url : 'af_production_feature.do?method=getPartialOrderList',
		cache : false,
		data : null,
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : function(xhjObj) {
			var resInfo = $.parseJSON(xhjObj.responseText);
			if (resInfo.errors.length == 0) {
				if ($("#af_part_order_editor").length == 0) {
					$("body").append("<div id='af_part_order_editor'></div>")
				}

				var $part_order_list = $("<table><tbody><tr><th>本人今日编辑记录</th><th>SAP 编辑记录</th></tr><tr><td><table id='af_part_order_self'></table></td><td><table id='af_part_order_sap'></table></td></tr></tbody></table>");
				if (resInfo.today_partial_order_edit_by_self) {
					var af_part_order_html = "<tr><th>修理单号</th><th>型号</th><th>维修等级</th><th>编辑次数</th></tr>";
					for (var iSelf in resInfo.today_partial_order_edit_by_self) {
						var rcd = resInfo.today_partial_order_edit_by_self[iSelf];
						af_part_order_html += "<tr><td>" + rcd.omr_notifi_no + "</td><td>" + rcd.model_name + "</td><td>" + (rcd.level_name || "") + "</td><td>" + rcd.quantity + "</td></tr>";
					}
					$part_order_list.find("#af_part_order_self").html(af_part_order_html);
				}
				if (resInfo.today_partial_order_edit_from_sap) {
					var af_part_order_html = "<tr><th>修理单号</th><th>型号</th><th>维修等级</th><th>编辑次数</th></tr>";
					for (var iSap in resInfo.today_partial_order_edit_from_sap) {
						var rcd = resInfo.today_partial_order_edit_from_sap[iSap];
						af_part_order_html += "<tr><td>" + rcd.omr_notifi_no + "</td><td>" + rcd.model_name + "</td><td>" + (rcd.level_name || "") + "</td><td>" + rcd.quantity + "</td></tr>";
					}
					$part_order_list.find("#af_part_order_sap").html(af_part_order_html);
				}

				var $af_PartOrderEditor = $("#af_part_order_editor").html("");
				$af_PartOrderEditor.append("<input id='af_mp_omr_notifi_no' type='text'><input type='button' value='登记'>")
					.append($part_order_list)
					.find("input:button").button()
						.click(function(){
							if ($("#af_mp_omr_notifi_no").val()) {
								var $thisbutton = $(this);
								$thisbutton.disable();
								$.ajax({
									beforeSend : ajaxRequestType,
									async : true,
									url : 'af_production_feature.do?method=doPartialOrder',
									cache : false,
									data : {omr_notifi_no : $("#af_mp_omr_notifi_no").val().trim()},
									type : "post",
									dataType : "json",
									success : ajaxSuccessCheck,
									error : ajaxError,
									complete : function(xhjObj) {
										var resInfo = $.parseJSON(xhjObj.responseText);
										if (resInfo.errors.length > 0) {
											$("#af_mp_omr_notifi_no").focus();
											$thisbutton.enable();
											// 共通出错信息框
											treatBackMessages("#af_part_order_editor", resInfo.errors);
										} else {
											$af_PartOrderEditor.dialog("close");
											setTimeout(showPartialOrderList, 720);
										}
									}
								})
							}
						});
				$("#af_mp_omr_notifi_no").bind("keyup", function(evt){
					if(evt.keyCode === 13) {
						if (this.className.indexOf("error") >= 0) return;
						$(this).next().trigger("click")
					}
					this.className = "";
					if(this.value.length > 2) {
						var checkvalue = this.value;
						$("#af_part_order_self tr,#af_part_order_sap tr").find("td:eq(0)").each(function(){
							var $td = $(this);
							var this_text = ($td.text());
							var idxv = this_text.indexOf(checkvalue);
							if (idxv >= 0) {
								$td.html("<span>" + this_text.substring(0, idxv) + "</span><span class='part_order_highlight'>" + this_text.substring(idxv, idxv + checkvalue.length) + "</span><span>" + this_text.substring(idxv + checkvalue.length) + "</span>");	
							} else {
								$td.text(this_text);
							}
						});
					}
				});
				$af_PartOrderEditor.dialog({
					modal : true, 
					title : "零件订购单编辑记录表", 
					width : 680,
					buttons : {
						"关闭" : function(){$af_PartOrderEditor.dialog("close");}
					}
				});
			}
		}
	});
};

var showWastePartialList = function(){
	var $this_dialog = $("#waste_partial_editor");
	if ($this_dialog.length === 0) {
		$("body.outer").append("<div id='waste_partial_editor'/>");
		$this_dialog = $("#waste_partial_editor");
	}
	$this_dialog.hide();
	
	$this_dialog.load("widgets/partial/waste_partial.jsp", function(responseText, textStatus, XMLHttpRequest) {
		$this_dialog.dialog({
			modal : true, 
			title : "废弃零件回收", 
			width : 1100,
			height: 700,
			resizable:false,
			buttons : {
				"关闭" : function(){$this_dialog.dialog("close");}
			}
		});
	});
};

return {
	applyProcess : function(process_type_code, call_obj, call_method, call_params) {
		if (process_type_code == null) {
			call_method.apply(call_obj, call_params);
			return;
		}

		if (isClosed) {
			if ($afTimer.data("is_manager") != true) {
				errorPop("您已下班，当天不能再处理需要计时的作业。");
				return;
			} else {
				var $toLi = $("#af_abilities li[code='" + process_type_code + "']");
				if ($toLi.length == 0) {
					errorPop("您没有操作此功能的权限。");
					return;
				}
				if (process_type_code == 103 || process_type_code == 213 || process_type_code == 214 || process_type_code == 241) { // 必须记录
					doSwitch("1", process_type_code, function(){
						call_method.apply(call_obj, call_params);
					});
				} else {
					call_method.apply(call_obj, call_params); // 直接执行
				}
			}
			return;
		}

		var fromTcode = $afTimer.attr("type_code");
		var codeMatch = false;
		if (process_type_code instanceof Array) {
			for (var ic in process_type_code) {
				if (fromTcode == process_type_code[ic]) {
					codeMatch = true; break;
				}
			}
		} else {
			codeMatch = (fromTcode == process_type_code);
		}
		if ($afTimer.attr("from") === "a" && codeMatch) {
			call_method.apply(call_obj, call_params); // 直接执行
		} else {
			var fromText = "", toText = "";
			if ($afTimer.attr("from") === "a") {
				var $fromLi = $("#af_abilities li[code='" + fromTcode + "']");
				fromText = $fromLi.attr("group") + ":" + $fromLi.text();
			} else {
				var $fromLi = $("#af_pause_reason li[code='" + fromTcode + "']");
				fromText = $("#af_pause_reason_group li[group=" + $fromLi.attr("group") + "]").text()
					+ ":" + $fromLi.text();
			}
			if (process_type_code instanceof Array) {
				for (var ic in process_type_code) {
					var $toLi = $("#af_abilities li[code='" + process_type_code + "']");
					if ($toLi.length > 0) {
						toText += $toLi.attr("group") + ":" + $toLi.text();
					}
				}
				if (toText.length == 0) {
					errorPop("您没有操作此功能的权限。");
					return;
				}
				toText += "之一";
			} else {
				var $toLi = $("#af_abilities li[code='" + process_type_code + "']");
				if ($toLi.length == 0) {
					errorPop("您没有操作此功能的权限。");
					return;
				}
				toText = $toLi.attr("group") + ":" + $toLi.text();
			}

			var warnData = "当前您的作业状态是“" + fromText 
				+ "”，<BR>是否将作业状态切换到“" + toText + "”并继续操作？";
			warningConfirm(warnData, function() {
				var post_production_type;
				if (process_type_code instanceof Array) {
					// TODO
				} else {
					post_production_type = process_type_code;
				}
				doSwitch("1", post_production_type, function(){
					call_method.apply(call_obj, call_params);
				});
			}, null, "计时中作业状态与实际操作不符", "切换", "放弃操作");
		}
	},
	refresh : function(){
		refreshAf("notInit");
	}
}
})();

</script>
