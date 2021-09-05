<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page language="java" pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" isELIgnored="false"%>
<style>
#headarea .menulink.icon-share {
	position: relative;
}

#headarea .menulink.icon-share > .expland-offpos {
	background-color : darkblue;
	color:white;
	position: absolute;
	top: 1.8em;
	left: -50px;	
	width: 160px;
	height: 100px;
	border: 2px solid white;
	border-radius:5px;
	z-index: 100;
	font-size: 12px;
	padding:2px;
	display:none;
}
#headarea .menulink.icon-share > .expland-offpos > input {
	margin-top:2px;
}
#offpos_ticket {
	position:fixed;
	top: 20%;
	left:20%;
	width:60%;
	height:60%;
	border: 6px solid white;
	background-color:lightblue;
	text-align: center;
	font-size: 56px;
	line-height: 1.5;
	display:none;
}
#offpos_ticket .olInfo {
	font-size: 16px;
	cursor:pointer;
	padding: 0 1em;
}
#offpos_ticket .olInfo:hover {
	color : darkblue;
	background-color : gold;
}
</style>

<div class="menulink icon-share">离岗<div class="expland-offpos">
离岗证申请，目前状况为：
<div id="offpos_status"></div>
离岗证不绑定作业及间隔时间<br>
请另行操作后再点击离岗。
<input type="button" class="ui-button" value="离岗" style="display:none;">
</div></div>

<div id="offpos_ticket">
	<div>离岗中……<span class="olInfo">点击回归</span></div>
	<div></div>
	<div></div>
	<div></div>
</div>

<script type="text/javascript">

$(function() {

var olTickTO = null;
var olMonitorTO = null;

var showOffPos = function() {
	var $explandOffpos = $("#headarea .menulink.icon-share > .expland-offpos");
	$.ajax({
		beforeSend : ajaxRequestType,
		async : true,
		url : headerServicePath + '?method=checkOffPos',
		cache : false,
		data : null,
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : function(xhjObj){
			var resInfo = $.parseJSON(xhjObj.responseText);
			if (resInfo.notExistsPos) {
				$explandOffpos.hide();
				errorPop("当前使用者没有选择课室与工程，不能领用离岗证。");
				return;
			}
			if (resInfo.self) {
				showOffPosPanel(resInfo.self, resInfo.off_time);
				return;
			}
			if (resInfo.permit) {
				$("#offpos_status").html("当前有离岗证可用(" + resInfo.permit + ")。")
					.nextAll().show();
			} else {
				$("#offpos_status").html("当前无离岗证可用，请稍待。")
					.nextAll().hide();
			}
		}
	});
	if ($explandOffpos.is(":visible")) {
		olMonitorTO = setTimeout(showOffPos, 20000);
	}
}

var doRegistOffPos = function() {
	$.ajax({
		beforeSend : ajaxRequestType,
		async : true,
		url : headerServicePath + '?method=doRegistOffPos',
		cache : false,
		data : null,
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : function(xhjObj){
			var resInfo = $.parseJSON(xhjObj.responseText);
			$("#headarea .menulink.icon-share > .expland-offpos").hide();

			if (resInfo.errors && resInfo.errors.length) {
				errorPop(resInfo.errors[0].errmsg);
			} else {
				showOffPosPanel(resInfo.self, resInfo.off_time);
			}
		}
	});
}

var showOffPosPanel = function(self, off_time) {
	$(window).overlay();
	var overlayIndex = parseInt($(".overlay").css("zIndex") || 1000);
	var $ticket = $("#offpos_ticket");
	$ticket.show().css("zIndex", overlayIndex + 1);
	var $ticketDiv = $ticket.children("div");
	$ticketDiv.eq(1).text(self);
	$ticketDiv.eq(2).text(off_time);
	refreshPass();
}

var refreshPass = function(){
	var $timeStart = $("#offpos_ticket > div:eq(2)").text();
	var startSeconds = parseInt($timeStart.substring(0,2)) * 60 + parseInt($timeStart.substring(3,5));
	startSeconds = startSeconds * 60 + parseInt($timeStart.substring(6,8));

	var now = new Date();
	var nowseconds = (now.getHours() * 60 + now.getMinutes()) * 60 + now.getSeconds() ;

	var timePass = nowseconds - startSeconds;
	var elapseText = parseInt(timePass / 3600);
	timePass = timePass - elapseText * 3600;
	elapseText = fillZero(elapseText) + ":" + fillZero(parseInt(timePass / 60)) + ":" + fillZero(parseInt(timePass % 60));

	var $timePass = $("#offpos_ticket > div:eq(3)");
	$timePass.text(elapseText);
	if ($("#offpos_ticket").is(":visible")) {
		olTickTO = setTimeout(refreshPass, 1000);
	}
};

$("#headarea .menulink.icon-share").click(function(evt){
	if (evt.target.tagName === "INPUT") {
		doRegistOffPos();
		if (olMonitorTO != null) clearTimeout(olMonitorTO);
		return;
	}
	var $explandOffpos = $(this).children(".expland-offpos");
	if ($explandOffpos.is(":visible")) {
		$explandOffpos.hide("fade");
		if (olMonitorTO != null) clearTimeout(olMonitorTO);
	} else {
		$explandOffpos.show("fade");
		showOffPos();
	}
});

$("#offpos_ticket .olInfo").click(function(){
	$.ajax({
		beforeSend : ajaxRequestType,
		async : true,
		url : headerServicePath + '?method=doFinishOffPos',
		cache : false,
		data : null,
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : function(xhjObj){
			$(window).overlay({action:"close"});
			$("#offpos_ticket").hide();
		}
	});	
});

showOffPos();

});
</script>