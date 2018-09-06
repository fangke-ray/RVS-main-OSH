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
});
