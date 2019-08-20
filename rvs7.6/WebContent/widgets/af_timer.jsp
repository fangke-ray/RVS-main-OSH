<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page language="java" pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" isELIgnored="false"%>

<style>
#af_timer {
	width:120px;
	height:120px;
	border-radius: 60px;
	position:fixed;
	opacity: 0.65;
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
	border:1px solid black;
	background-color: white;
	border-radius: 60px;
	position: absolute;
	overflow: hidden;
}
#af_holder div {
	background-color : blue;
}
#af_timer .prod {
	position: relative;
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
#af_process, #af_standard {
	position: relative;
	display: inline;
	padding: 0 0.5em;
	border:1px solid black;
}
#af_standard:empty {
	display: none;
}
#af_process {
	background-color: lightblue;
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
	bottom : 90px; top : auto;
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
</style>

<div id="af_timer" switch="no" hdir="left" vdir="down">
<div id="af_holder"><div></div></div>
<span class="prod" line=1>AF Timer</span>
<div id="af_standard"></div>
<div id="af_process">30</div>
<ul id="af_abilities_group"></ul>
<ul id="af_abilities">
</ul>
<ul id="af_pause_reason_group">
	<li group="WDT">辅助作业</li>
	<li group="WY">间接作业</li>
	<li group="MD">管理等待</li>
	<li group="H">休息离线</li>
</ul>
<ul id="af_pause_reason">
</ul>
</div>

<script type="text/javascript">
var afInit = function() {
	$.ajax({
		beforeSend : ajaxRequestType,
		async : true,
		url : 'af_production_feature.do?method=getByOperator',
		cache : false,
		data : null,
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : function(xhjObj) {
			var resInfo = $.parseJSON(xhjObj.responseText);
			if (resInfo.afAbilities) setAfAbilities(resInfo.afAbilities);
			if (resInfo.pauseReasonGroup) {
				var prGrhtml = "";
				for (var prGr in resInfo.pauseReasonGroup) {
					var prOfGr = resInfo.pauseReasonGroup[prGr];
					for (var prCode in prOfGr) {
						var prName = prOfGr[prCode].split(":");
						prGrhtml += "<li group='" + prGr + "' code='" + prCode + "'>" + prName[1] + "</li>"
					}
				}
				$("#af_pause_reason").html(prGrhtml)
					.find("li").hide();
			}
		}
	});
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
	$("#af_abilities_group").html(afGrhtml);
}

var setInpagePos = function(){
	var aw = document.defaultView.screen.availWidth / 2
	if (aw < parseInt($afTimer.css("right")) + parseInt($afTimer.css("width"))) {
		$afTimer.attr("hdir", "left");
	} else {
		$afTimer.attr("hdir", "right");
	}

	var ah = document.defaultView.screen.availHeight / 2
	if (ah < parseInt($afTimer.css("top")) + parseInt($afTimer.css("height"))) {
		$afTimer.attr("vdir", "up");
	} else {
		$afTimer.attr("vdir", "down");
	}
}

	var af_dragged = false;
	var af_xM = 0, saf_xM = 0;
	var af_yM = 0, saf_yM = 0;
	var $afTimer = $("#af_timer");
	var $afHolder = $("#af_holder");
	$afHolder
	.bind("mousedown", function(evt){
		$afTimer.attr("moving", true);
		af_dragged = true;saf_xM = af_xM = evt.pageX;saf_yM = af_yM = evt.pageY;} )
	.bind("mouseup", function(){
		setInpagePos();
		if (Math.abs(af_xM - saf_xM) < 10 && Math.abs(af_yM - saf_yM) < 10) {
			if ($afTimer.attr("switch") === "no") {
				$afTimer.attr("switch", "yes");
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
	} );

	$("#af_abilities_group").on("click", "li", function(){
		var group = $(this).attr("group");
		$("#af_pause_reason").find("li").hide();
		$("#af_abilities").find("li").hide()
			.filter("[group='" + group + "']").show();
	});

	$("#af_pause_reason_group").on("click", "li", function(){
		var group = $(this).attr("group");
		$("#af_abilities").find("li").hide();
		$("#af_pause_reason").find("li").hide()
			.filter("[group='" + group + "']").show();
	});

	var obradius = $("#af_timer").width() / 2;
	$("#af_standard").css({"left" : (obradius * 0.75) + "px", "top" : (obradius * -0.5) + "px"});

	$("#af_holder div").css({"height": "30%", "marginTop": "70%"});
	var initAft_r = parseInt(localStorage.getItem("aft_r"));
	// document.defaultView.screen.availWidth - 120
	if (initAft_r > 1126) initAft_r = 1126;
	else if (initAft_r < 0) initAft_r = 0;
	var initAft_t = parseInt(localStorage.getItem("aft_t"));
	if (initAft_t > 528) initAft_t = 528;
	else if (initAft_t < 0) initAft_t = 0;

	$afTimer.css("right", initAft_r + "px");
	$afTimer.css("top", initAft_t + "px");

	afInit();

var setProcessClock = function(percent) {
	var posX = getClockPosX(percent) + 0.75;
	var posY = getClockPosY(percent) + 0.5;
	$("#af_process").css({"left" : (obradius * posX) + "px", "top" : (obradius * posY) + "px"});
}
var getClockPosX = function(percent){
	return Math.sin(percent*2*Math.PI);
}
var getClockPosY = function(percent){
	return -Math.cos(percent*2*Math.PI);
}

</script>
