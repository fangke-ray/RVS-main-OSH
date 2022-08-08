var selectedMaterial = {};

var treat_nogood = function() {
	$("#nogood_treat").hide();
	// 导入不良处置画面
	$("#nogood_treat").load("widget.do?method=nogoodedit", function(responseText, textStatus, XMLHttpRequest) {
		var selectedId = $("#list").getGridParam("selrow");
		var rowData = $("#list").getRowData(selectedId);
		var data = {
			material_id : rowData["material_id"]
		};
		// Ajax提交
		$.ajax({
			beforeSend : ajaxRequestType,
			async : false,
			url : 'scheduleProcessing.do?method=getwarning',
			cache : false,
			data : data,
			type : "post",
			dataType : "json",
			success : ajaxSuccessCheck,
			error : ajaxError,
			complete : function(xhrobj, textStatus) {
				try {
					// 以Object形式读取JSON
					eval('resInfo =' + xhrobj.responseText);
					$("#nogood_id").val(resInfo.warning.id);
					$("#nogood_occur_time").text(resInfo.warning.occur_time);
					$("#nogood_sorc_no").text(resInfo.warning.sorc_no);
					$("#nogood_model_name").text(resInfo.warning.model_name);
					$("#nogood_serial_no").text(resInfo.warning.serial_no);
					$("#nogood_line_name").text(resInfo.warning.line_name);
					$("#nogood_process_code").text(resInfo.warning.process_code);
					$("#nogood_reason").text(resInfo.warning.reason);
					$("#nogood_comment_other").text(resInfo.warning.comment);
					$("#nogood_comment").val(resInfo.warning.myComment);
					selectedMaterial.sorc_no = resInfo.warning.sorc_no;
					selectedMaterial.model_name = resInfo.warning.model_name;
					selectedMaterial.serial_no = resInfo.warning.serial_no;
					selectedMaterial.material_id = rowData["material_id"];
					selectedMaterial.position_id = resInfo.warning.position_id;
					selectedMaterial.comment = $("#nogood_comment").val();
					selectedMaterial.alarm_messsage_id = $("#nogood_id").val();

					$("#nogood_treat").dialog({
						// position : [ 800, 20 ],
						title : "不良信息及处置",
						width : 468,
						show : "blind",
						height : 'auto', //450,
						resizable : false,
						modal : true,
						minHeight : 200,
						close : function() {
							selectedMaterial.comment = $("#nogood_comment").val();
							selectedMaterial.append_parts = ($("#append_parts_y").attr("checked") ? 1 : 0);
							$("#nogood_treat").html("");
						},
						buttons : {}
					});
					$("#nogood_treat").show();
				} catch (e) {
					alert("name: " + e.name + " message: " + e.message + " lineNumber: "
							+ e.lineNumber + " fileName: " + e.fileName);
				};
			}
		});
	});
};

var redo_ccd = function() {
	$("#nogood_treat").hide();
	// 导入不良处置画面
	$("#nogood_treat").load("widget.do?method=nogoodedit", function(responseText, textStatus, XMLHttpRequest) {
		var selectedId = $("#list").getGridParam("selrow");
		var rowData = $("#list").getRowData(selectedId);
		var data = {
			material_id : rowData["material_id"]
		};
		// Ajax提交
		$.ajax({
			beforeSend : ajaxRequestType,
			async : false,
			url : 'material.do?method=doreccd',
			cache : false,
			data : data,
			type : "post",
			dataType : "json",
			success : ajaxSuccessCheck,
			error : ajaxError,
			complete : function(xhrobj, textStatus) {
				try {
					// 以Object形式读取JSON
					eval('resInfo =' + xhrobj.responseText);
					if (resInfo.errors.length == 0) {
						infoPop("已经可以开始CCD盖玻璃更换作业！");
					} else {
						treatBackMessages(null, resInfo.errors);
					}
				} catch (e) {
					alert("name: " + e.name + " message: " + e.message + " lineNumber: "
							+ e.lineNumber + " fileName: " + e.fileName);
				};
			}
		});
	});
};

var show_daily_report = function() {
	
	var $jdialog = $("#daily_report");
	$jdialog.hide();

	// 导入每日KPI报告
	$jdialog.load("scheduleProcessing.do?method=daily_report", function(responseText, textStatus, XMLHttpRequest) {
		$jdialog.dialog({
			// position : [ 800, 20 ],
			title : "每日KPI信息",
			width : 780,
			show : "blind",
			height : 'auto', //450,
			resizable : false,
			modal : true,
			minHeight : 200,
			close : function() {
				$jdialog.html("");
			},
			buttons : {
				"修改" : function() {
					var postData = {};

					var $changed_object = $("#report_of_week input[changed=true]");
					var iV = 0;
					$changed_object.each(
					function(idx,ele) {
						var p_date =
							new Date(Date.parse($("#weekstart").val()) + ($(ele).parent().attr("weekday_index") - 1) * 86400000);
						var s_date = (p_date.getFullYear() + "/" + fillZero(p_date.getMonth()+1, 2) + "/" + fillZero(p_date.getDate(), 2));
						postData["update.count_date[" + iV + "]"] = s_date;
						postData["update.target[" + iV + "]"] = $(ele).parent().parent().attr("for");
						postData["update.val[" + iV + "]"] = ele.value;
						iV++;
					});
					$changed_object = $("#report_of_week textarea[changed=true]");
					$changed_object.each(
					function(idx,ele) {
						var p_date =
							new Date(Date.parse($("#weekstart").val()) + ($(ele).parent().attr("weekday_index") - 1) * 86400000);
						var s_date = (p_date.getFullYear() + "/" + fillZero(p_date.getMonth()+1, 2) + "/" + fillZero(p_date.getDate(), 2));
						postData["update.count_date[" + iV + "]"] = s_date;
						postData["update.target[" + iV + "]"] = "comment";
						postData["update.val[" + iV + "]"] = ele.value;
						iV++;
					});

					postData.weekstart = $("#weekstart").val();
					// postData.comment = $("#for_complete").val();

					// Ajax提交
					$.ajax({
						beforeSend : ajaxRequestType,
						async : false,
						url : 'scheduleProcessing.do?method=doUpdateDailyKpi',
						cache : false,
						data : postData,
						type : "post",
						dataType : "json",
						success : ajaxSuccessCheck,
						error : ajaxError,
						complete : function(xhrobj, textStatus) {
							$jdialog.dialog("close");
						}
					});
				}, "关闭" : function() {
					$jdialog.dialog("close");
				}
			}
		});
	});
};

var clean_position = function() {
	$("#nogood_treat").hide();
	var selectedId = $("#list").getGridParam("selrow");
	var rowData = $("#list").getRowData(selectedId);
	var data = {
		material_id : rowData["material_id"],
		model_name : rowData["model_name"],
		serial_no : rowData["serial_no"]
	};
	// 导入不良处置画面
	$("#nogood_treat").load("widget.do?method=cleanPosition&material_id=" + rowData["material_id"], data, function(responseText, textStatus, XMLHttpRequest) {
		$("#clean_target").select2Buttons();
		$("#nogood_treat").dialog({
			// position : [ 800, 20 ],
			title : "不良信息及处置",
			width : 808,
			show : "blind",
			height : 'auto', //450,
			resizable : false,
			modal : true,
			minHeight : 200,
			close : function() {
				$("#nogood_treat").html("");
			},
			buttons : {
				"确认" : function() {
					// TODO check
					data.position_id = $("#clean_target").val();
					data.comment = $("#clean_comment").val();
					// Ajax提交
					$.ajax({
						beforeSend : ajaxRequestType,
						async : false,
						url : 'pcsFixOrder.do?method=doPcCreate',
						cache : false,
						data : data,
						type : "post",
						dataType : "json",
						success : ajaxSuccessCheck,
						error : ajaxError,
						complete : function(xhrobj, textStatus) {
							try {
								// 以Object形式读取JSON
								eval('resInfo =' + xhrobj.responseText);
								$("#nogood_treat").dialog("close");
							} catch (e) {
								alert("name: " + e.name + " message: " + e.message + " lineNumber: "
										+ e.lineNumber + " fileName: " + e.fileName);
							};
						}
					});
				}
			}
		});
	});
};

var show_capacity_setting = function(){
	var $jdialog = $("#capacity_setting");
	$jdialog.hide();
	
	$jdialog.load("scheduleProcessing.do?method=capacity_setting",function(responseText,textStatus,XMLHttpRequest){
		$jdialog.dialog({
			title : "产能设定",
			width : 600,
			show  : "blind",
			height: 'auto',
			resizable : false,//不可改变弹出框大小
			modal : true,
			minHeight:200,
			close :function(){
				$jdialog.html("");
			},
			buttons:{
				"修改" : function() {
					
					var postData = {};
					
					var $changed_object = $("#capacity_of_upper_limit input[changed=true]");

					$changed_object.each(function(idx,ele){		
						var $ele = $(ele);
						postData["update.line_id["+idx+"]"] = $ele.attr("line_id");
						postData["update.px["+idx+"]"] = $ele.attr("px");
						postData["update.upper_limit["+idx+"]"] = ele.value;
						postData["update.section_id["+idx+"]"] = $ele.attr("section_id");
						postData["update.level["+idx+"]"] = $ele.attr("light_fix_flg");
					});

					// Ajax提交
					$.ajax({
						beforeSend : ajaxRequestType,
						async : false,
						url : 'scheduleProcessing.do?method=doUpdateUpperLimit',
						cache : false,
						data : postData,
						type : "post",
						dataType : "json",
						success : ajaxSuccessCheck,
						error : ajaxError,
						complete : function(xhrobj, textStatus) {
							$jdialog.dialog("close");
						}
					});
				}, "关闭" : function() {
					$jdialog.dialog("close");
				}
			}
		});
	});	
};

var dispatch_section = function() {
	// Ajax提交
	$.ajax({
		beforeSend : ajaxRequestType,
		async : false,
		url : 'scheduleProcessing.do?method=getSectionDispatch',
		cache : false,
		data : null,
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : function(xhrObj, textStatus) {
			var resInfo = $.parseJSON(xhrObj.responseText);
		
			if (resInfo.errors && resInfo.errors.length > 0) {
				// 共通出错信息框
				treatBackMessages(null, resInfo.errors);
		
				return;
			}
			dispatchSectionShow(resInfo);
		}
	});
}
var dispatchSectionShow = function(resInfo) {
	var $dialog = $("#section_dispatcher");

	var $filter = $dialog.find("select");
	if ($filter.children().length == 0) {
		var optHtml = "<option value>(全部)</option>";
		var headHtml = "<th class='ui-state-default'>作业人员</th>";
		for (var isec in resInfo.resultSectionNames) {
			var secData = resInfo.resultSectionNames[isec];
			optHtml += "<option value=" + secData.section_id 
				+ ">" + secData.name + "</option>";
			headHtml += "<th class='ui-state-default' section_id='" + secData.section_id + "'>" + secData.name + "</th>";
		}
		$filter.html(optHtml).select2Buttons().change(function(){
			if(this.value) {
				$dialog.find("table > tbody > tr").hide()
					.filter("[section_id=" + this.value + "]").show();
			} else {
				$dialog.find("table > tbody > tr").show();
			}
		});
		$dialog.find("table > thead").html(headHtml);
	}

	var bodySecHtml = "";
	$dialog.find("table > thead > th").each(function(idx, ele){
		if (idx > 0) {
			var $ele = $(ele);
			bodySecHtml += "<td section_id='" + $ele.attr("section_id") + "'>" + $ele.text() + "</td>";
		}
	});

	var bodyHtml = "";
	for (var iwo in resInfo.workflgOperators) {
		var opData = resInfo.workflgOperators[iwo];
		bodyHtml += "<tr operator_id='" + opData.operator_id + "' section_id='" + opData.section_id + "'><td>" 
			+ opData.name + "</td>" +  bodySecHtml + "</tr>";
	}
	$dialog.find("table > tbody").html(bodyHtml);
	$dialog.find("table > tbody > tr").each(function(udx, ele){
		var $tr = $(ele);
		var section_id = $tr.attr("section_id");
		$tr.children().each(function(cdx, td){
			if (cdx > 0) {
				$(td).attr("main", $(td).attr("section_id") == section_id);
			}
		});
	});

	for (var idp in resInfo.dispatchedSections) {
		var dispatchedSection = resInfo.dispatchedSections[idp];
		var $tr = $dialog.find("table > tbody > tr[operator_id='" + dispatchedSection.operator_id + "']");
		if ($tr.length == 1) {
			var $secTd = $tr.children().not(":eq(0)").filter("[section_id='" + dispatchedSection.section_id + "']");
			if ($secTd.length > 0) {
				$secTd.attr("active_date", dispatchedSection.active_date);
				$secTd.attr("expire_date", dispatchedSection.expire_date);
			}
		}
	}

	$dialog.dialog({
		title : "课室支援权限调度",
		width : 768,
		show : "blind",
		height : 'auto', //450,
		resizable : false,
		modal : true,
		minHeight : 200,
		buttons : {}
	});
}


var show_attendance = function(){
	var $jdialog = $("#attendance_setting");
	if ($jdialog.length == 0) {
		$("body").append("<div id='attendance_setting'></div>");
		$jdialog = $("#attendance_setting");
	}
	$jdialog.hide();
	
	$jdialog.load("scheduleProcessing.do?method=attendance", function(responseText,textStatus,XMLHttpRequest){
		$jdialog.dialog({
			title : "出勤记录",
			width : 600,
			show  : "blind",
			height: 'auto',
			resizable : false,//不可改变弹出框大小
			modal : true,
			minHeight:200,
			close :function(){
				$jdialog.html("");
			},
			buttons:{
				"修改" : function() {
					if (typeof doUpdateAttendance === "function") {
						doUpdateAttendance();
					}
				}, "关闭" : function() {
					$jdialog.dialog("close");
				}
			}
		});
	});	
};

$(document).ready(function() {
	$("#nogoodbutton").disable();
	$("#nogoodbutton").click(function() {
		treat_nogood();
	});
	$("#reccdbutton").disable();
	$("#reccdbutton").click(function() {
		redo_ccd();
	});
	$("#cleanbutton").disable();
	$("#cleanbutton").click(function() {
		clean_position();
	});
	
	//产能设定
	$("#capacity_setting_button").click(function(){
		show_capacity_setting();
	});
	
	$("#daily_report_button").click(function() {
		show_daily_report();
	});

	// 出勤设定
	$("#attendance_button").click(function() {
		show_attendance();
	});

	$("#dispatchbutton").click(dispatch_section);

	$("#section_chooser_active_date, #section_chooser_expire_date").datepicker({
		showButtonPanel:true,
		currentText: "今天"
	});

	$("#section_dispatcher").on("click", "td[main=false]", function(){
		var $ele = $(this);
		var $tr = $ele.closest("tr");
		var operator_id = $tr.attr("operator_id");
		var operator_name = $tr.children().eq(0).text();
		var section_id = $ele.attr("section_id");
		var section_name = $ele.text();
		var active_date = $ele.attr("active_date");
		var expire_date = $ele.attr("expire_date");
		if (!expire_date) {
			active_date = new Date();
			expire_date = new Date(active_date.getTime() + 86400000);
		} else {
			active_date = new Date(parseInt(active_date));
			expire_date = new Date(parseInt(expire_date));
		}
		var $section_chooser = $("#section_chooser");
		$section_chooser.find("table .td-content:eq(0)").text(operator_name);
		$("#section_chooser_name").val(section_name);
		$("#section_chooser_active_date").datepicker("setDate", active_date);
		$("#section_chooser_expire_date").datepicker("setDate", expire_date);

		$section_chooser.dialog({
			title : "设置可支援课室",
			resizable : false,
			modal : true,
			minHeight : 200,
//			open: function(){
//				$("#section_chooser_active_date").blur();
//				$section_chooser.next().find(".ui-dialog-buttonset").find("button:eq(1)").focus();
//			},
			buttons : {
				"取消权限" : function() {
					var postData = {
						"section_id" : section_id,
						"operator_id" : operator_id
					}
					$.ajax({
						beforeSend : ajaxRequestType,
						async : false,
						url : 'scheduleProcessing.do?method=doSetSectionDispatch',
						cache : false,
						data : postData,
						type : "post",
						dataType : "json",
						success : ajaxSuccessCheck,
						error : ajaxError,
						complete : function(xhrobj, textStatus) {
							$ele.removeAttr("active_date");
							$ele.removeAttr("expire_date");
							$section_chooser.dialog("close");
							infoPop("已取消"+operator_name+"于"+section_name+"的切换权限。");
						}
					});
				},
				"设置权限" : function() {
					var postData = {
						"section_id" : section_id,
						"operator_id" : operator_id,
						"active_date" : $("#section_chooser_active_date").val(),
						"expire_date" : $("#section_chooser_expire_date").val()
					}
					$.ajax({
						beforeSend : ajaxRequestType,
						async : false,
						url : 'scheduleProcessing.do?method=doSetSectionDispatch',
						cache : false,
						data : postData,
						type : "post",
						dataType : "json",
						success : ajaxSuccessCheck,
						error : ajaxError,
						complete : function(xhrobj, textStatus) {
							$ele.attr("active_date", new Date(postData.active_date).getTime());
							$ele.attr("expire_date", new Date(postData.expire_date).getTime());
							$section_chooser.dialog("close");
							infoPop("已将"+section_name+"切换权限设置给"+operator_name+"。");
						}
					});
				},
				"关闭" : function() {
					$section_chooser.dialog("close");
				}
			}
		});
	})
});