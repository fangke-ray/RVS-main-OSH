/** 一览数据对象 */
var listdata = {};

/** 服务器处理路径 */
var servicePath = "position_panel_snout.do";
var hasPcs = (typeof pcsO === "object");

// 已启动作业时间
var p_time = 0;
// 定时处理对象
var oInterval, ttInterval, wtInterval;
// 定时处理间隔（1分钟）
var iInterval = 60000;
// 取到的标准作业时间
var leagal_overline;
var t_operator_cost = 0;
var t_run_cost = 0;
var p_start_at = 0;
var pauseOptions = "";
var pauseComments = {};
var breakOptions = "";
var stepOptions = "";
var dryProcesses = "";

var $pause_clock = null;
var device_safety_guide = {};

/** 中断信息弹出框 */
var makeBreakDialog = function(jBreakDialog) {
	jBreakDialog.dialog({
		title : "中断生成",
		width : 480,
		show: "blind",
		height : 'auto' ,
		resizable : false,
		modal : true,
		minHeight : 200,
		close : function(){
			jBreakDialog.html("");
		},
		buttons : {
			"确定":function(){
				if ($("#breakForm").valid()) {
					var data = {
						reason : $("#break_reason").val(),
						comments : $("#edit_comments").val(),
						serial_no : $("#material_details td:eq(5)").text()
					}

					if (hasPcs) {
						pcsO.valuePcs(data);
					}

					// 烘干
					if ($("#break_reason").val() == 99) {
						var $drying_job_tr = jBreakDialog.find("tr.ui-state-highlight");
						if ($drying_job_tr.length > 0) {
							data.drying_job_id = $drying_job_tr.attr("drying_job_id");
							data.device_manage_id = $drying_job_tr.children("td[device_manage_id]").attr("device_manage_id");
							data.slot = jBreakDialog.find(".slot_select").val();
							if (!data.slot && jBreakDialog.find(".slot_select option").length > 0) {
								errorPop(WORKINFO.chooseDryingProcessStock);
								return;	
							}
						} else {
							errorPop(WORKINFO.chooseDryingProcess);
							return;	
						}
					}

					// Ajax提交
					$.ajax({
						beforeSend : ajaxRequestType,
						async : false,
						url : servicePath + '?method=dobreak',
						cache : false,
						data : data,
						type : "post",
						dataType : "json",
						success : ajaxSuccessCheck,
						error : ajaxError,
						complete : function(xhrobj, textStatus) {
							doFinish_ajaxSuccess(xhrobj, textStatus);
							try {
								// 以Object形式读取JSON
								eval('resInfo =' + xhrobj.responseText);
								if (resInfo.errors.length === 0) {
									jBreakDialog.dialog("close");
								} else {
									// 共通出错信息框
									treatBackMessages(null, resInfo.errors);
								}
							} catch (e) {
								alert("name: " + e.name + " message: " + e.message + " lineNumber: "
										+ e.lineNumber + " fileName: " + e.fileName);
							};
						}
					});
				}
			}, "关闭" : function(){ $(this).dialog("close"); }
		}
	});
}

/** 正常中断生成 */
var makeStep = function() {

	var jBreakDialog = $("#break_dialog");
	if (jBreakDialog.length === 0) {
		$("body.outer").append("<div id='break_dialog'/>");
		jBreakDialog = $("#break_dialog");
	}

	jBreakDialog.hide();

	// 导入中断画面
	jBreakDialog.load("widget.do?method=breakoperator",
		function(responseText, textStatus, XMLHttpRequest) {
		// 设定中断理由
		var $break_reason = $("#break_reason");
		var allStepOptions = stepOptions + (dryProcesses ? ("<option value=\"99\">烘干作业 》</option>") : "")
		$break_reason.html(allStepOptions).select2Buttons();
		$break_reason.change(function() {
			if ($break_reason.val() == "99") {
				jBreakDialog.dialog( "option", "width", 800 );
				jBreakDialog.find(".ui-state-highlight").removeClass("ui-state-highlight");
				$("#breakForm .drying_jobs").show();
				$("#breakForm .drying_slots").hide();
			} else {
				jBreakDialog.dialog( "option", "width", 480 );
				$("#breakForm .drying_jobs, #breakForm .drying_slots").hide();
			}
		});

		// 烘干
		$("#breakForm .drying_jobs").hide()
			.children("td:eq(1)").html(dryProcesses)
			.find("tr").click(function(){
				var $tr = $(this);
				var drying_job_id = $tr.attr("drying_job_id");
				jBreakDialog.find(".ui-state-highlight").removeClass("ui-state-highlight");
				$tr.addClass("ui-state-highlight");
				var device_manage_id = $tr.children("td[device_manage_id]").attr("device_manage_id");
				if(device_manage_id) {
					// Ajax提交
					$.ajax({
						beforeSend : ajaxRequestType,
						async : false,
						url : 'drying_process.do?method=getSlotsByDevice',
						cache : false,
						data : {device_manage_id : device_manage_id, status : "self"},
						type : "post",
						dataType : "json",
						success : ajaxSuccessCheck,
						error : ajaxError,
						complete : function(xhr, status) {
					    	var resInfo = $.parseJSON(xhr.responseText);
					    	if (resInfo.slotsByDevice && resInfo.slotsByDevice.length) {
					    		var slotsByDevice = resInfo.slotsByDevice[0];
					    		var slot_count = slotsByDevice.slot;
					    		var using_slots = slotsByDevice.using_slots;
					    		var qualified_slots = $tr.children("td[slots]").attr("slots");
					    		var $slotSelect = $("<select class='slot_select'/>");
					    		for (var islot = 1; islot <= slot_count; islot++) {
					    			$slotSelect.append("<option value = "+ islot +">" + islot + "</option>");
					    		}
					    		if (using_slots) {
					    			var using_slot_array = using_slots.split(",");
					    			for (var iUsa in using_slot_array) {
					    				var using_slot = using_slot_array[iUsa];
										$slotSelect.children("option[value=" + using_slot + "]").attr("disabled", true);
					    			}
					    		}
					    		if (qualified_slots) {
					    			var qualified_slot_array = qualified_slots.split(",");
					    			for (var iQsa in qualified_slot_array) {
					    				var qualified_slot = qualified_slot_array[iQsa];
										$slotSelect.children("option[value=" + qualified_slot + "]").addClass("qualified_slot");
					    			}
					    			$slotSelect.children("option").not(".qualified_slot").remove();
					    		}
					    		var $usables = $slotSelect.children("option").not("[disabled]");
					    		if ($usables.length > 0) {
						    		$usables.eq(0).attr("selected", true);
					    		} else {
					    			$slotSelect.val("");
					    		}

					    		$("#breakForm .drying_slots").show()
					    			.children("td:eq(1)").html($slotSelect)
					    			.children("select").select2Buttons();
					    	}
	    				}
					});
				} else {
		    		$("#breakForm .drying_slots").hide();
				}
			});

		$("#breakForm").validate({
			rules : {
				break_reason : {
					required : true
				}
			}
		});
		makeBreakDialog(jBreakDialog);

	});
	$("#break_dialog").show();
};

/** 不良中断生成 */
var makeBreak = function() {

	var jBreakDialog = $("#break_dialog");
	if (jBreakDialog.length === 0) {
		$("body.outer").append("<div id='break_dialog'/>");
		jBreakDialog = $("#break_dialog");
	}

	jBreakDialog.hide();

	// 导入中断画面
	jBreakDialog.load("widget.do?method=breakoperator",
		function(responseText, textStatus, XMLHttpRequest) {

		// 设定中断理由
		$("#break_reason").html(breakOptions);
		$("#break_reason").select2Buttons();

		$("#breakForm").validate({
			rules : {
				break_reason : {
					required : true
				},
				comments : {
					required : function() {
						return ($("#break_reason").val() != null && $("#break_reason").val() != "" && parseInt($("#break_reason").val()) < 10);
					}
				}
			}
		});
		makeBreakDialog(jBreakDialog);

	});
	$("#break_dialog").show();
};

/** 暂停信息弹出框 */
var makePauseDialog = function(jBreakDialog) {
	jBreakDialog.dialog({
		title : "暂停信息编辑",
		width : 760,
		show: "blind",
		height : 'auto' ,
		resizable : false,
		modal : true,
		minHeight : 200,
		close : function(){
			jBreakDialog.html("");
		},
		buttons : {
			"确定":function(){
				if ($("#pauseo_edit").parent().valid()) {
					var data = {
						reason : $("#pauseo_edit_pause_reason").val()
					}

					if ($("#pauseo_edit_comments").is(":hidden")) {
						data.comments = $("#pauseo_edit_comments_select").val();
					} else {
						data.comments = $("#pauseo_edit_comments").val()
					}

					// Ajax提交
					$.ajax({
						beforeSend : ajaxRequestType,
						async : false,
						url : servicePath + '?method=dopause',
						cache : false,
						data : data,
						type : "post",
						dataType : "json",
						success : ajaxSuccessCheck,
						error : ajaxError,
						complete : function(xhr, status) {
							jBreakDialog.dialog("close");
							doFinish_ajaxSuccess(xhr, status);
						}
					});
				}
			}, "关闭" : function(){ $(this).dialog("close"); }
		}
	});
}

/** 暂停信息 */
var makePause = function() {
	var jBreakDialog = $("#break_dialog");
	if (jBreakDialog.length === 0) {
		$("body.outer").append("<div id='break_dialog'/>");
		jBreakDialog = $("#break_dialog");
	}

	jBreakDialog.hide();
	// 导入暂停画面
	jBreakDialog.load("widget.do?method=pauseoperator",
		function(responseText, textStatus, XMLHttpRequest) {
			// 设定暂停理由
			$("#pauseo_edit").show();
			$("#pauseo_show").hide();
			$("#pauseo_edit_pause_reason").html(pauseOptions);
			$("#pauseo_edit_pause_reason").select2Buttons().change(function(){
				var thisval = this.value;
				if (pauseComments[thisval]) {
					$("#pauseo_edit_comments_select")
					.next(".select2Buttons").remove()
					.end()
					.show().html(createCommentOptions(pauseComments[thisval]))
					.select2Buttons();
					$("#pauseo_edit_comments").hide();
				} else {
					$("#pauseo_edit_comments_select").hide()
					.next(".select2Buttons").remove()
					.end()
					.html("");
					$("#pauseo_edit_comments").show();
				}
			});

			$("#pauseo_edit_pause_reason").val("100").trigger("change");

			makePauseDialog(jBreakDialog);
		});
};

/** 暂停重开 */
var endPause = function() {

	// 作业等待时间
	if ($(".opd_re_comment").length > 0) {
		var pause_start_time = new Date($(".opd_re_comment").attr("pause_start_time"));
		var leak = $(".opd_re_comment").attr("leak");
		if((leak && leak != "0") && !header_today_holiday 
			&& new Date().getTime() - pause_start_time.getTime() > 120000) {
			errorPop(WORKINFO.needFillBreak);
			return;
		}
	}

	// 重新读入刚才暂停的维修对象
	var data = {
		serial_no : $("#material_details td:eq(5)").text()
	}

	// 无论如何关闭暂停窗口
	if ($("#break_dialog").html() != "") {
		$("#break_dialog").dialog("close");
	}

	// Ajax提交
	$.ajax({
		beforeSend : ajaxRequestType,
		async : false,
		url : servicePath + '?method=doendpause',
		cache : false,
		data : data,
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : doStart_ajaxSuccess
	});
};

var treatPause = function(resInfo) {

	$("#storagearea").hide();
	$("#manualarea").hide();
	$("#material_details").show();
	$("#position_status").text("暂停中");
	$("#position_status").css("color", "#0080FF");
	$(".opd_re_comment").show();
	$("#pausebutton").hide();
	$("#finishbutton").disable();
	$("#breakbutton").disable();
	$("#stepbutton").disable();
	$("#continuebutton").show();
	$("#p_rate div:animated").stop();

	if (resInfo) {
		$("#material_details td:eq(1)").text(resInfo.snout_origin);
		$("#material_details td:eq(3)").text(resInfo.model_name).attr("model_id", resInfo.model_id);
		$("#material_details td:eq(5)").text(resInfo.serial_no);

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
	
		$("#dtl_process_time label").text(minuteFormat(resInfo.spent_mins));
		var frate = parseInt((resInfo.spent_mins) / leagal_overline * 100);
		if (frate > 99) {
			frate = 99;
		}
		$("#p_rate").html("<div class='tube-liquid tube-green' style='width:"+ frate +"%;text-align:right;'></div>");
	
		var p_operator_cost = $("#p_operator_cost").text();
	
		// 工程检查票
		if (resInfo.pcses && resInfo.pcses.length > 0 && hasPcs) {
			pcsO.generate(resInfo.pcses);
		}

		if (resInfo.processingPauseStart) {
			setPauseClock(resInfo.processingPauseStart);
		}
	}

	clearInterval(oInterval);
}

var treatStart = function(resInfo) {

	$("#storagearea").hide();
	$("#manualarea").hide();
	$("#material_details").show();
	$("#position_status").text("处理中");
	$("#position_status").css("color", "#58b848");
	$(".opd_re_comment").hide();
	clearInterval(wtInterval);
	if ($pause_clock != null) $pause_clock.text("");

	$("#material_details td:eq(1)").text(resInfo.snout_origin);
	$("#material_details td:eq(3)").text(resInfo.model_name).attr("model_id", resInfo.model_id);
	$("#material_details td:eq(5)").text(resInfo.serial_no);

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

	$("#dtl_process_time label").text(minuteFormat(resInfo.spent_mins));
	var frate = parseInt((resInfo.spent_mins) / leagal_overline * 100);
	if (frate > 99) {
		frate = 99;
	}
	$("#p_rate").html("<div class='tube-liquid tube-green' style='width:"+ frate +"%;text-align:right;'></div>");
	p_time = resInfo.spent_mins - 1;

	$("#p_operator_cost").text(resInfo.spent_mins);
	var p_operator_cost = $("#p_operator_cost").text();

//	if (p_operator_cost.indexOf(':') < 0) {
//		t_operator_cost = p_operator_cost;
//	} else {
		t_operator_cost = convertMinute(p_operator_cost);// + resInfo.spent_mins;
//	}

	$("#continuebutton").hide();
	$("#finishbutton").enable();
	$("#breakbutton").enable();
	$("#stepbutton").enable();
	$("#pausebutton").show();
	ctime();
	oInterval = setInterval(ctime,iInterval);

	if (resInfo.material_comment || (device_safety_guide && device_safety_guide.length)) {
		showSidebar(resInfo.material_comment);
	} else {
		$("#comments_sidebar").hide();
	}

	// 工程检查票
	if (resInfo.pcses && resInfo.pcses.length > 0 && hasPcs) {
		pcsO.generate(resInfo.pcses);
	}
	$("#scanner_inputer").focus();
}

var doInit_ajaxSuccess = function(xhrobj, textStatus){
	//return;
	var resInfo = null;
//	try {
		// 以Object形式读取JSON
		eval('resInfo =' + xhrobj.responseText);

		if (resInfo.errors.length > 0) {
			// 共通出错信息框
			treatBackMessages(null, resInfo.errors);
		} else {
			// 建立等待区一览
			showWaitings(resInfo.waitings);

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
			$("#input_model_id").html(resInfo.modelOptions).select2Buttons();
			pauseOptions = resInfo.pauseOptions;
			pauseComments = $.parseJSON(resInfo.pauseComments);
			breakOptions = resInfo.breakOptions;
			stepOptions = resInfo.stepOptions;
			dryProcesses = getDryProcessesTable(resInfo.dryProcesses);
			if (stepOptions == "") {
				$("#stepbutton").hide();
			}
			if (breakOptions == "") {
				$("#breakbutton").hide();
			}

			
			// 设备安全手册信息
			if (resInfo.position_hcsgs) device_safety_guide = resInfo.position_hcsgs;

			loadJs(
			"js/data/operator-detail.js",
			function(){
				opd_load($("#workarea table td:contains('暂停时间')"),
				function(){
					// 间隔工作时间
					if (resInfo.processingPauseStart) {
						setPauseClock(resInfo.processingPauseStart);
					}

					if (resInfo.workstauts == 1) {
						treatStart(resInfo);
					} else if (resInfo.workstauts == 2) {
						treatPause(resInfo);
					} else {
						$("#input_model_id").disable();
						$("#input_snout_no").disable();
						$("#startbutton").disable();

						if (device_safety_guide && device_safety_guide.length) {
							showSidebar(null);
						} else {
							$("#comments_sidebar").hide();
						}
					}

				});
			});

		}
//	} catch (e) {
//		alert("name: " + e.name + " message: " + e.message + " lineNumber: "
//				+ e.lineNumber + " fileName: " + e.fileName);
//	};
};

var showWaitings = function(waitings){

	var $waiting_html = $(getWaitingHtml(waitings));

 	// 计算剩余时间
	var $divs = $waiting_html.find("div.finish_time_left");
	$divs.each(function(){
		var $this = $(this);
		var id = $this.parent().parent().attr("id");

		var start_time = $this.attr("start_time");
		if (start_time != null && start_time != "") {
			date_time_list.add({id: id, target:$this, date_time:start_time, naturaltime:true, drying_time:$this.attr("drying_time")});
		}
	});

	// 一键开始
	$waiting_html.find("input:button").button().click(function(){
		var $tube = $(this).parent().parent();
		var chosedData = {
			model_id : $tube.attr("model_id"),
			model_name : $tube.attr("model_name"),
			serial_no : $tube.attr("serial_no"),
			confirmed : ($tube.find(".finish_time_left").length > 0)
		}
		doStart(null, chosedData);
	});

	$("#waitings").html($waiting_html);

	refreshTargetTimeLeft(1);
};

var getWaitingHtml = function(waitings) {
	var reason = "";
	var waiting_html = "";
	for (var iwaiting = 0; iwaiting < waitings.length; iwaiting++) {
		var waiting = waitings[iwaiting];
		if (reason != waiting.waitingat) {
			reason = waiting.waitingat;
			waiting_html += '<div class="ui-state-default w_group">'+ reason +':</div>';
		}
		waiting_html += '<div class="waiting tube" model_id="' + waiting.model_id + '" model_name="' + waiting.model_name + '" serial_no="' + waiting.serial_no + '"' +
						' id="w_' + waiting.model_id + "_" + waiting.serial_no + '">' +
							'<div class="tube-liquid' + expeditedColor(waiting.expedited)  + '">' +
								(waiting.sorc_no == null ? "" : waiting.sorc_no + ' | ') + waiting.model_name + ' | ' + waiting.serial_no +
								getDryingTime(waiting.drying_process) +   
							'</div>' +
						 '<div class="click_start"><input type="button" value="》开始"></div>' +
						'</div>'
	}

	return waiting_html;
}

var getDryingTime = function(dryingProcess) {
	var time_html = "";
	if (dryingProcess) {
		var title = "烘干详细信息：" + dryingProcess.content + " 硬化条件：" + dryingProcess.hardening_condition;
		if (dryingProcess.manage_code) {
			title += " 烘干设备：" + dryingProcess.manage_code + " " + dryingProcess.slot + "号库位";
		}
		time_html += '<div start_time="' + dryingProcess.start_time + '" class="finish_time_left" title="' + title + '"' 
			+ "drying_time='" + dryingProcess.drying_time + "'></div>";
	}
	return time_html;
}

var expeditedColor = function(expedited) {
	if (expedited == -1) return ' tube-gray'; // 普通
	if (expedited == 1) return ' tube-blue'; // 加急
	return ' tube-green'; // 当日
}

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

	$("#startbutton").click(doStart);
	$("#finishbutton").click(doFinish);
	$("#pausebutton").click(makePause);
	$("#breakbutton").click(makeBreak);
	$("#stepbutton").click(makeStep);
	$("#continuebutton").click(endPause);

	// 输入框触发，配合浏览器
	$("#scanner_inputer").keypress(function(){
	if (this.value.length === 11) {
		doSetOrigin();
	}
	});
	$("#scanner_inputer").keyup(function(){
	if (this.value.length >= 11) {
		doSetOrigin();
	}
	});

});

var doSetOrigin = function() {
	var data = {
		material_id : $("#scanner_inputer").val()
	}
	$("#scanner_inputer").attr("value", "");

	// Ajax提交
	$.ajax({
		beforeSend : ajaxRequestType,
		async : false,
		url : servicePath + '?method=checkScan',
		cache : false,
		data : data,
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : doSetOrigin_ajaxSuccess
	});
}

var doSetOrigin_ajaxSuccess = function(xhrobj, textStatus){
	var resInfo = $.parseJSON(xhrobj.responseText);
	
	if (resInfo.errors.length > 0) {
		// 共通出错信息框
		treatBackMessages(null, resInfo.errors);
	} else {
		var mForm = resInfo.mForm;
		$("#scanner_inputer").val(mForm.material_id).disable();
		$("#input_model_id").val(mForm.model_id).enable().trigger("change");
		$("#input_snout_no").val(mForm.serial_no).enable();
		$("#startbutton").enable();
	}
}

var doStart_ajaxSuccess = function(xhrobj, textStatus){
	var resInfo = null;
	try {
		// 以Object形式读取JSON
		eval('resInfo =' + xhrobj.responseText);

		if (resInfo.errors.length > 0) {
			// 共通出错信息框
			treatBackMessages(null, resInfo.errors);
		} else {
			$("#scanner_inputer").val("");
			$("#input_model_id").attr("value", "").trigger("change");
			$("#input_snout_no").attr("value", "");

			treatStart(resInfo);
			dryProcesses = getDryProcessesTable(resInfo.dryProcesses);
		}
	} catch (e) {
		alert("name: " + e.name + " message: " + e.message + " lineNumber: "
				+ e.lineNumber + " fileName: " + e.fileName);
	};
};

var doStart=function(evt, chosedData){

	// 作业等待时间
	if ($(".opd_re_comment").length > 0) {
		var pause_start_time = new Date($(".opd_re_comment").attr("pause_start_time"));
		var leak = $(".opd_re_comment").attr("leak");
		if((leak && leak != "0") && !header_today_holiday 
			&& new Date().getTime() - pause_start_time.getTime() > 120000) {
			errorPop(WORKINFO.needFillBreak);
			return;
		}
	}

	var data = {
		material_id : $("#scanner_inputer").val(),
		model_id : $("#input_model_id").val(),
		model_name : toLabelValue($("#input_model_id")),
		serial_no : $("#input_snout_no").val()
	}
	// Ajax提交
	$.ajax({
		beforeSend : ajaxRequestType,
		async : false,
		url : servicePath + '?method=dostart',
		cache : false,
		data : chosedData || data,
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : doStart_ajaxSuccess
	});
};

var showSidebar = function(material_comment) {

	var $ul = $("<ul/>");
	var $content = $("<div class='tip_pages'/>");
	
	if (material_comment) {
		$ul.append("<li><input type='radio' id='st_material_comment' name='showTips'><label for='st_material_comment'>维修对象备注<label></li>");
		$content.append("<div class='tip_page' for='st_material_comment'><textarea readonly>" + material_comment + "</textarea></div>");
	}
	if (device_safety_guide) {
		for (var idsg in device_safety_guide) {
			var device_type = device_safety_guide[idsg];
			$ul.append("<li><input type='radio' id='st_" + device_type.devices_type_id 
				+ "' name='showTips'><label for='st_" + device_type.devices_type_id + "'>安全操作手顺: <br>" + device_type.name + "<label></li>");
			$content.append("<div class='tip_page' for='st_" + device_type.devices_type_id + "'>" 
				+ (device_type.safety_guide ? "<img src='http://" + document.location.hostname + "/photos/safety_guide/" + device_type.devices_type_id + "'></img>" : "")
				+ "</div>");
		}
	}
	$content.children().hide();

	$("#comments_sidebar .comments_area").html("")
		.append($content)
		.append($ul)
		.find("ul").buttonset()
		.find("input:radio").change(function(){
			$("#comments_sidebar .tip_page").hide();
			$("#comments_sidebar .tip_page[for='" + $(this).attr("id") + "']").show();
		})
		.end().find("input:radio:eq(0)").attr("checked", "checked").trigger("change");
	$("#comments_sidebar .comments_area").hide();
	$("#comments_sidebar").removeClass("shown").css({width:"30px",opacity:".5"});
	$("#comments_sidebar .ui-widget-header span").removeClass("icon-enter-2").addClass("icon-share");

	$("#comments_sidebar").show();

	if (material_comment && !$("#comments_sidebar").hasClass("shown")) {
		$("#comments_sidebar .ui-widget-header span").trigger("click");
	}
}

var doFinish_ajaxSuccess = function(xhrobj, textStatus){
	var resInfo = null;
	try {
		// 以Object形式读取JSON
		eval('resInfo =' + xhrobj.responseText);
		$('div#errstring').dialog("close");

		if (resInfo.errors.length > 0) {
			// 共通出错信息框
			treatBackMessages(null, resInfo.errors);
		} else {
			if (resInfo.workstauts == 2) {
				treatPause(resInfo);
			} else {
				$("#scanner_inputer").attr("value", "");
				$("#storagearea").show();
				$("#manualarea").show();
				$("#material_details").hide();
				$("#position_status").text("准备中");
				$("#position_status").css("color", "#0080FF");
				$(".opd_re_comment").show();
				$("#manualdetailarea").hide();
	
				$("#material_details td:eq(7)").text("");
				$("#dtl_process_time label").text("");
				$("#p_rate").html("");
				p_time = 0;
				clearInterval(oInterval);
				$("#pauseo_edit").hide();
				$("#pauseo_show").show();
				$("#pauseo_show_sorc_no").text($("#material_details td:eq(1)").text());
				$("#pauseo_show_pause_reason").text(toLabelValue($("#pauseo_edit_pause_reason")));
				$("#pauseo_show_comments").text(toLabelValue($("#pauseo_edit_comments")));
				if (!$("#break_dialog").is(":empty")) {
					$("#break_dialog").dialog("option", "buttons", {
							"再开":function(){
								endPause();
							}
						}
					);
				}
				$("#scanner_inputer").val("").focus().enable();
				$("#input_model_id").disable();
				$("#input_snout_no").disable();
				if (device_safety_guide && device_safety_guide.length) {
					$("#comments_sidebar .comments_area").val("");
					$("#comments_sidebar").hide();
				}

				if (resInfo.processingPauseStart) {
					setPauseClock(resInfo.processingPauseStart);
				}

				if (device_safety_guide && device_safety_guide.length) {
					showSidebar(null);
					if ($("#comments_sidebar").hasClass("shown")) {
						$("#comments_sidebar .ui-widget-header span").trigger("click");
					}
				} else {
					$("#comments_sidebar").hide();
				}
			}
		}
	} catch (e) {
		console.log("name: " + e.name + " message: " + e.message + " lineNumber: "
				+ e.lineNumber + " fileName: " + e.fileName);
	};
}

var doFinishPost=function(data){
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
}


var doFinish=function(){
	var data = {};
	var empty = false;
	if (hasPcs) {
		empty = pcsO.valuePcs(data);
	}

	if (empty) {
		warningConfirm("存在没有填的工程检查票选项，可以就这样提交吗？"
		, function(){doFinishStorage(data)}
		, function(){
			$('div#errstring').dialog("close");
		});
	}

	if (!empty) {
		doFinishStorage(data);
	}
};

var doFinishStorage = function(data) {
	
	var postModelId = $("#material_details td[model_id]").attr("model_id");

	if (postModelId) {
		var postData = {"model_id": postModelId};
	
		$.ajax({
			beforeSend : ajaxRequestType,
			async : true,
			url : 'snouts.do?method=getStorage',
			cache : false,
			data : postData,
			type : "post",
			dataType : "json",
			success : ajaxSuccessCheck,
			error : ajaxError,
			complete : function(xhrObj){
				showMoveStorage(xhrObj, data);
			}
		});
	} else {
		doFinishPost(data);
	}
}

var showMoveStorage = function(xhrobj, postData){
	var resInfo = $.parseJSON(xhrobj.responseText);

	var $popDialog = $("#shelf_pop"); 
	if ($popDialog.length == 0) {
		$("body").append("<div id='shelf_pop'></div>");
		$popDialog = $("#shelf_pop"); 
	}
	$popDialog.hide();
	$popDialog.html(resInfo.snoutStorageHtml);
		
	$popDialog.dialog({
		title : "完成 D/E 组件入库",
		width : 512,
		show: "blind",
		height : 'auto' ,
		resizable : false,
		modal : true,
		buttons : {"关闭" : function(){$popDialog.dialog("close")}}
	});

	$popDialog.find("td").addClass("wip-empty");
	for (var iheap in resInfo.slots) {
		$popDialog.find("td[slot="+resInfo.slots[iheap]+"]").removeClass("wip-empty").addClass("ui-storage-highlight wip-heaped");
	}

	$popDialog.find(".ui-widget-content .wip-table").not(".close").click(function(e){
		if ("TD" == e.target.tagName) {
			if (!$(e.target).hasClass("wip-heaped")) {
				var selslot = $(e.target).attr("slot");
				if (selslot) {
					$popDialog.dialog("close");
					postData.slot = selslot;
					doFinishPost(postData);
				}
			}
		}
	});
}

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
	$("#dtl_process_time label").text(minuteFormat(p_time));

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
			liquid.addClass("tube-orange");
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

// 暂停中
var wttime = function(){
	var minute = Math.floor((new Date().getTime() - p_start_at) / 60000);
	if (minute < 0) {
		$pause_clock.text("当日下班");
		$(".opd_re_comment").hide();
	} else {
		$pause_clock.text(minuteFormat(minute));
	}
}

var minuteFormat =function(iminute) {
	var hours = parseInt(iminute / 60);
	var minutes = iminute % 60;

	return prevzero(hours) + ":" + prevzero(minutes);
}

var convertMinute =function(sminute) {
	var hours = sminute.replace(/(.*):(.*)/, "$1");
	var minutes = sminute.replace(/(.*):(.*)/, "$2");

	return hours * 60 + parseInt(minutes);
};

var createCommentOptions = function(comments){
	var commentArray = comments.split(";");
	var sRet = "";
	for (var commentIdx in commentArray) {
		var comment = commentArray[commentIdx];
		sRet += "<option value='" + comment + "'>" + comment + "</option>";
	}
	return sRet;
}

var getDryProcessesTable = function(dryProcesses){
	if (dryProcesses && dryProcesses.length) {
		if ("follow" === dryProcesses) return window.dryProcesses;

		var tableStr = "<table class='dryProcessesTable subform'>";
		for (var iDp in dryProcesses) {
			var dryProcess = dryProcesses[iDp];
			tableStr += "<tr drying_job_id='" + dryProcess.drying_job_id + "'>"
				 + "<td>" + dryProcess.content + "</td>"
				 + "<td>" + dryProcess.hardening_condition + "</td>"
				 + (dryProcess.device_manage_id ? 
				 "<td device_manage_id='" + dryProcess.device_manage_id + "'>" + dryProcess.manage_code + " " + dryProcess.model_name + "</td>"
				 + "<td>" + dryProcess.section_name + " " + dryProcess.process_code + "</td>"
				 : "<td colspan=2>自然干燥</td>")
				 + "<td " + (dryProcess.slots ? "slots='" + dryProcess.slots + "'" : "") + "></td>"
				 + "</tr>";
		}
		tableStr += "</table>";
		return tableStr;
	} else {
		return "";
	}
};

// 间隔时间计时
var setPauseClock = function(processingPauseStart) {
	$(".opd_re_comment").html(processingPauseStart.comments || "(未设定)")
		.attr({"pause_start_time": processingPauseStart.pause_start_time,
		leak: processingPauseStart.leak,
		reason: processingPauseStart.reason,
		title: processingPauseStart.comments || ""});
	// 计算暂停用时
	p_start_at = new Date(processingPauseStart.pause_start_time).getTime();
	$pause_clock = $(".opd_re_comment").parent().next();
	wttime();
	wtInterval = setInterval(wttime, 30000);
}

var saveLeakDataCallback = function(data){
	if (!data.leak)  {
		$('.opd_re_comment').attr({leak: 0,
				reason: data.reason, title: data.full_comments})
			.text(data.full_comments);
	} else {
		$('.opd_re_comment').attr({leak: 1,
				reason: null, title: null, pause_start_time: new Date().toString()})
			.text("(未设定)");
		p_start_at = new Date().getTime();
	}
}