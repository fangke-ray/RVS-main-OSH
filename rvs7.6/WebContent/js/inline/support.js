/** 一览数据对象 */
var listdata = {};

/** 服务器处理路径 */
var servicePath = "support.do";
var hasPcs = (typeof pcsO === "object");

// 已启动作业时间
var p_time = 0;
// 定时处理对象
var oInterval, ttInterval;
// 定时处理间隔（1分钟）
var iInterval = 60000;
// 取到的标准作业时间
var leagal_overline;
var t_operator_cost = 0;
var t_run_cost = 0;

var treatList = function(resInfo) {
	$("#sections label").html(resInfo.section_name);

	var workinglist = resInfo.workinglist;
	var position_flag = "";
	var postionhtml = "";
	var personhtml = "";
	for (var iworkinglist = 0; iworkinglist < workinglist.length; iworkinglist++) {
		var working = workinglist[iworkinglist];
		if (position_flag != working.position_id) {
			postionhtml += '<label for="position_'+working.process_code+'"><span>' + working.process_code + ' '+working.position_name+'</span></label>';
			postionhtml += '<input type="radio" name="position" id="position_'+working.process_code+'" position_id="'+working.position_id+'">';
		
			position_flag = working.position_id;
		}
		personhtml += '<label for="person_'+working.job_no+'" style="display:none;"><span>'+working.operator_name+'</span></label>';
		personhtml += '<input type="radio" name="person_" id="person_'+working.job_no+'" position_id="'+working.position_id+'" operator_id="'+working.operator_id+'" >';
	}
	$("#positions").html(postionhtml);
	$("#persons").html(personhtml);
	$("#positions").buttonset();
	$("#persons").buttonset();
	$("#positions").find("input").click(function() {
		var p_id = $(this).attr("position_id");
		$("#persons").find("input").prev("label").hide();
		$("#persons").find("input[position_id=" + p_id + "]").prev("label").show();
	});
}

var treatStart = function(resInfo) {
	$("#scanner_container").hide();
	$("#material_details").show();

	$("#material_details td:eq(0) input:hidden").val(resInfo.mform.material_id);
	$("#material_details td:eq(3)").text(resInfo.main_line);
	$("#material_details td:eq(5)").text(resInfo.main_position);
	$("#material_details td:eq(11)").text(resInfo.main_operator);
	$("#material_details td:eq(13)").text(resInfo.mform.sorc_no);
	$("#material_details td:eq(15)").text(resInfo.mform.model_name);
	$("#material_details td:eq(17)").text(resInfo.mform.serial_no);

	if (resInfo.action_time) {
		$("#material_details td:eq(7)").text(resInfo.action_time);
	} else {
		var thistime=new Date();
		var hours=thistime.getHours() 
		var minutes=thistime.getMinutes() 

		$("#material_details td:eq(7)").text(prevzero(hours) + ":" + prevzero(minutes));
	}
	$("#material_details td:eq(9)").text(minuteFormat(resInfo.leagal_overline) + ":00");
	leagal_overline = resInfo.leagal_overline;

	$("#dtl_process_time").text(minuteFormat(resInfo.spent_mins));
	var frate = parseInt((resInfo.spent_mins) / leagal_overline * 100);
	if (frate > 99) {
		frate = 99;
	}
	$("#p_rate").html("<div class='tube-liquid tube-green' style='width:"+ frate +"%;text-align:right;'></div>");
	p_time = resInfo.spent_mins - 1;

	var p_operator_cost = $("#p_operator_cost").text();

//	if (p_operator_cost.indexOf(':') < 0) {
//		t_operator_cost = p_operator_cost;
//	} else {
		t_operator_cost = convertMinute(p_operator_cost);// + resInfo.spent_mins;
//	}

	$("#continuebutton").hide();
	$("#finishbutton").enable();
	$("#breakbutton").enable();
	$("#pausebutton").show();
	ctime();
	oInterval = setInterval(ctime,iInterval);
	$("#w_" + resInfo.mform.material_id).hide("drop", {direction: 'right'}, function() {
		var jthis = $(this);
		var jGroup = jthis.prevAll(".w_group");
		jthis.remove();
		if (jGroup.nextUntil(".w_group").length == 0) {
			jGroup.hide("fade", function() {
				jGroup.remove();
			})
		}
	});

	// 工程检查票
	if (resInfo.pcses && resInfo.pcses.length > 0 && hasPcs) {
		pcsO.generate(resInfo.pcses);
	}
}

var doInit_ajaxSuccess = function(xhrobj, textStatus){

	var resInfo = null;
//	try {
		// 以Object形式读取JSON
		eval('resInfo =' + xhrobj.responseText);

		if (resInfo.errors.length > 0) {
			// 共通出错信息框
			treatBackMessages(null, resInfo.errors);
		} else {

			// 计算当前用时
			var p_operator_cost = $("#p_operator_cost").text();
			if (p_operator_cost.indexOf(':') < 0) {
				t_operator_cost = p_operator_cost;
				$("#p_operator_cost").text(minuteFormat(t_operator_cost));
			}

			// 计算总用时
			var p_run_cost = $("#p_run_cost").text();
			if (p_run_cost.indexOf(':') < 0) {
				if (p_run_cost != "0" && p_run_cost != "") {
					t_run_cost = p_run_cost;
					$("#p_run_cost").text(minuteFormat(t_run_cost));
					ttInterval = setInterval(ttime, iInterval);
				}
			}

			// 暂停理由
			pauseOptions = resInfo.pauseOptions;

			if (resInfo.workstauts == 1) {
				treatStart(resInfo);
			} else if (resInfo.workstauts == 2) {
				treatPause(resInfo);
			} else if (resInfo.workstauts == 0) {
				treatList(resInfo);
			}
		}
//	} catch (e) {
//		alert("name: " + e.name + " message: " + e.message + " lineNumber: "
//				+ e.lineNumber + " fileName: " + e.fileName);
//	};
};

var doInit=function(){
	// Ajax提交
	$.ajax({
		beforeSend : ajaxRequestType,
		async : false,
		url : servicePath + '?method=jsinit',
		cache : false,
		data : {},
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : doInit_ajaxSuccess
	});
};

//$(document).ready(function() {
$(function() {
	$("input.ui-button").button();

	$("#material_details").hide();
	$("#continuebutton").hide();
	$("#manualdetailarea").hide();

	if (hasPcs) {
		pcsO.init($("#manualdetailarea"), false);
	}

	doInit();

	$("#finishbutton").click(doFinish);
});

var doStart_ajaxSuccess = function(xhrobj, textStatus){
	var resInfo = null;
	try {
		// 以Object形式读取JSON
		eval('resInfo =' + xhrobj.responseText);

		if (resInfo.errors.length > 0) {
			// 共通出错信息框
			treatBackMessages(null, resInfo.errors);
		} else {
			treatStart(resInfo);
		}
	} catch (e) {
		alert("name: " + e.name + " message: " + e.message + " lineNumber: "
				+ e.lineNumber + " fileName: " + e.fileName);
	};
};

var doStart=function(){
	var sel_position_id = $("#positions").find("input:checked").attr("position_id");
	var data = {
		position_id : sel_position_id,
		operator_id : $("#persons").find("input[position_id=" + sel_position_id+ "]:checked").attr("operator_id")
	}

	var selected = true;
	if (data.position_id == null) {
		errorPop("请选择要辅助的工位。");
		selected = false;
	}
	if (data.operator_id == null) {
		errorPop("请选择要辅助的人员。");
		selected = false;
	}

	if (selected) {
		// Ajax提交
		$.ajax({
			beforeSend : ajaxRequestType,
			async : false,
			url : servicePath + '?method=dostart',
			cache : false,
			data : data,
			type : "post",
			dataType : "json",
			success : ajaxSuccessCheck,
			error : ajaxError,
			complete : doStart_ajaxSuccess
		});
	}
};

var doFinish_ajaxSuccess = function(xhrobj, textStatus){
	var resInfo = null;
	try {
		// 以Object形式读取JSON
		eval('resInfo =' + xhrobj.responseText);

		if (resInfo.errors.length > 0) {
			// 共通出错信息框
			treatBackMessages(null, resInfo.errors);
		} else {
			$("#material_details").hide();
			$("#supportarea").show();
			$("#scanner_container").show();
			$("#manualdetailarea").hide();

			$("#material_details td:eq(7)").text("");
			$("#dtl_process_time").text("");
			$("#p_rate").html("");
			p_time = 0;
			clearInterval(oInterval);

			$("#working").text("");
			treatList(resInfo);
		}
	} catch (e) {
		alert("name: " + e.name + " message: " + e.message + " lineNumber: "
				+ e.lineNumber + " fileName: " + e.fileName);
	};
}

var doFinish=function(){
	var data = {};

	// Ajax提交
	$.ajax({
		beforeSend : ajaxRequestType,
		async : false,
		url : servicePath + '?method=dofinish',
		cache : false,
		data : data,
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : doFinish_ajaxSuccess
	});
};

var prevzero =function(i) {
	if (i < 10) {
		return "0" + i; 
	} else {
		return "" + i; 
	}
}

// 进行中效果
var ctime=function(){
	p_time++;
	$("#dtl_process_time").text(minuteFormat(p_time));

	var rate = parseInt((p_time + 1) / leagal_overline * 100);
	//var nextrate = 
	if (rate == 99) return;
	if (rate >= 100) rate = 99;
	var liquid = $("#p_rate div");
	liquid.animate({width : rate + "%"}, iInterval, "linear");
	if (rate > 80) {
		liquid.removeClass("tube-green");
		if (rate > 95) {
			liquid.removeClass("tube-yellow");
			liquid.addClass("tube-red");
		} else {
			liquid.addClass("tube-yellow");
		}
	}

	$("#p_operator_cost").text(minuteFormat(t_operator_cost));
	t_operator_cost++;
};

// 进行中效果
var ttime=function(){
	$("#p_run_cost").text(minuteFormat(t_run_cost));
	t_run_cost++;
};

var minuteFormat =function(iminute) {
	var hours = parseInt(iminute / 60);
	var minutes = iminute % 60;

	return prevzero(hours) + ":" + prevzero(minutes);
}

var convertMinute =function(sminute) {
	var hours = sminute.replace(/(.*):(.*)/, "$1");
	var minutes = sminute.replace(/(.*):(.*)/, "$2");

	return hours * 60 + parseInt(minutes);
}
