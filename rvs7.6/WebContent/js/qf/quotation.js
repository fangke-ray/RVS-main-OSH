/** 一览数据对象 */
var listdata = {};
var quotation_listdata = {};
/** 服务器处理路径 */
var servicePath = "quotation.do";
var hasPcs = null;

var wip_location = "";

// 取到的标准作业时间
var leagal_overline;

var lOptions = {};
var oOptions = {};
var dOptions = {};
var sOptions = {};
var tOptions = {};
var bOptions = {};
var pauseOptions = "";
var pauseComments = {};
var stepOptions = "";

/** 医院autocomplete **/
var customers = {};

//var showWipEmpty=function() {
//	$.ajax({
//		beforeSend : ajaxRequestType,
//		async : true,
//		url : "wip.do" + '?method=getwipempty',
//		cache : false,
//		data : null,
//		type : "post",
//		dataType : "json",
//		success : ajaxSuccessCheck,
//		error : ajaxError,
//		complete : function(xhrobj) {
//			var resInfo = null;
//			try {
//				// 以Object形式读取JSON
//				eval('resInfo =' + xhrobj.responseText);
//				if (resInfo.errors.length > 0) {
//					// 共通出错信息框
//					treatBackMessages(null, resInfo.errors);
//				} else {
//					pop_wip(doFinishConfirm, resInfo);
//				}
//			} catch (e) {
//				alert("name: " + e.name + " message: " + e.message + " lineNumber: "
//						+ e.lineNumber + " fileName: " + e.fileName);
//			};
//		}
//	});
//}


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
					};

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
							var resInfo = $.parseJSON(xhr.responseText)
							treatPause(resInfo);
						}
					});
				}
			}, "关闭" : function(){ $(this).dialog("close"); }
		}
	});
}

//var pop_wip = function(call_back, resInfo){
//	var quotation_pop = $("#quotation_pop");
//	quotation_pop.hide();
//	quotation_pop.load("widgets/qf/wip_map.jsp", function(responseText, textStatus, XMLHttpRequest) {
//		 //新增
//		if ($("#anml_attendtion").length > 0) {
//			quotation_pop.find("div.shelf_model[for!='anml_exp']").hide();
//		} else {
//			quotation_pop.find("div.shelf_model[for='anml_exp']").hide();
//		}
//
//		quotation_pop.dialog({
//			position : [ 800, 20 ],
//			title : "WIP 入库选择",
//			width : 1000,
//			show: "blind",
//			height : 640,// 'auto' ,
//			resizable : false,
//			modal : true,
//			minHeight : 200,
//			buttons : {}
//		});
//
//		quotation_pop.find("td").addClass("wip-empty");
//		for (var iheap in resInfo.heaps) {
//			quotation_pop.find("td[wipid="+resInfo.heaps[iheap]+"]").removeClass("wip-empty").addClass("ui-storage-highlight wip-heaped");
//		}
//
//		//$("#quotation_pop").css("cursor", "pointer");
//		quotation_pop.find(".ui-widget-content").click(function(e){
//			if ("TD" == e.target.tagName) {
//				var $td = $(e.target);
//				if (!$td.hasClass("wip-heaped")) {
//					wip_location = $td.attr("wipid");
//					var putAnml_exp = $("#anml_attendtion").length > 0;
//					var isAnml_exp = $td.is("[anml_exp]");
//
//					if (putAnml_exp && !isAnml_exp) {
//						errorPop("请将动物实验用维修品放入专门库位。")
//						return;
//					} else if (!putAnml_exp && isAnml_exp) {
//						errorPop("请不要将普通维修品放入动物实验用专门库位。")
//						return;
//					}
//
//					call_back();
//					quotation_pop.dialog("close");
//				}
//			}
//		});
//
//		quotation_pop.show();
//
//		if ($("#devicearea").length > 0) {
//		setTimeout(function(){quotation_pop[0].scrollTop = 300}, 200);
//		}
//	});
//}

/** 中断信息弹出框 */
var makeBreakDialog = function(jBreakDialog) {
	var b_request = {
		sorc_no : $("#edit_sorc_no").val(),
		esas_no : $("#edit_esas_no").val(),
		model_id : $("#editform td[model_id]").attr("model_id"),
		ocm : $("#edit_ocm").val(),
		customer_name : $("#edit_customer_name").val(),
		ocm_rank : $("#edit_ocm_rank").val(),
		ocm_deliver_date : $("#edit_ocm_deliver_date").val(),
		agreed_date : $("#edit_agreed_date").val(),
		level : $("#edit_level").val(),
		fix_type : $("#edit_fix_type").val(),
		service_repair_flg : $("#edit_service_repair_flg").val(),
		comment : $("#edit_comment").val(),
		selectable : ($("#partake").attr("checked") ? "1" : "0")
	}
	b_request.bound_out_ocm = $("#edit_bound_out_ocm option:selected").val();
	if ($("#edit_direct_flg").text()) {
		b_request.direct_flg = 1;
		if ($("#direct_rapid").attr("checked")) {
			b_request.scheduled_expedited = "2";
		} else {
			b_request.scheduled_expedited = "-1";
		}
	}

	var submitBreak = function(){
		b_request.wip_location = wip_location;

		if (hasPcs) {
			pcsO.valuePcs(b_request, true);
		}

		if (!$("#light_pat_button").is(":visible")) {
			b_request.pat_id = "00000000000";
		}

		if ($("#major_pat").is(":visible")) {
			if ($("#major_pat").attr("value") == $("#major_pat").attr("derive_id")) {
				b_request.pat_id = $("#major_pat").attr("value");
			}
		}

		if ($("#editform #tag_ccd").next("label").is(":visible")) {
			b_request.tag_ccd = $("#editform #tag_ccd").is(":checked") ? "1" : "-1";
		}

		// Ajax提交
		$.ajax({
			beforeSend : ajaxRequestType,
			async : false,
			url : servicePath + '?method=dobreak',
			cache : false,
			data : b_request,
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
					}
				} catch (e) {
					alert("name: " + e.name + " message: " + e.message + " lineNumber: "
							+ e.lineNumber + " fileName: " + e.fileName);
				};
			}
		});
	};

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
					b_request.reason = $("#break_reason").val();
					b_request.comments = $("#edit_comments").val();

					if (parseInt($("#break_reason").val()) > 70) {
						if ($("#break_reason").val() == 73) {
							if($("#edit_wip_location").val() != null && $("#edit_wip_location").val() != "") {
								errorPop("已经放入WIP"+$("#edit_wip_location").val()+"了");
								$(this).dialog("close");
								return;
							}
							showChooseMap($("#quotation_pop"), "WIP 入库选择", { 
								occupied : 1,
								for_agreed: -1,
								material_id : $("#hide_material_id").val()
							}, 
							function(sel_wip_location){
								wip_location = sel_wip_location;
								submitBreak();
							});
						} else {
							submitBreak();
						}
					} else {
						submitBreak();
					}
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

			$("#pauseo_edit_pause_reason").val("").trigger("change");

			makePauseDialog(jBreakDialog);
		});
};

/** 正常中断信息 */
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
		$("#break_reason").html(stepOptions);
		$("#break_reason").select2Buttons();

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

var paused_list = function(paused) {
	// 建立等待区一览
	var reason = "";
	var waiting_html = "";
	var subCount = 0;
	for (var iwaiting = 0; iwaiting < paused.length; iwaiting++) {
		var waiting = paused[iwaiting];
		if (reason != waiting.operate_result) {
			reason = waiting.operate_result;
			waiting_html = waiting_html.replace('#count#', subCount);
			subCount = 0;
			waiting_html += '<div class="ui-state-default w_group" style="margin-top: 12px; margin-bottom: 8px; padding: 2px;">'+ reason +' #count#件:</div>'
		}
		waiting_html += '<div class="waiting tube" id="w_' + waiting.material_id + '">' +
							'<div class="tube-liquid  ' +
							(waiting.agreed_date ? 'tube-green' : 'tube-gray') +
							'">'
								+ getLevel(waiting.level)
								+ (waiting.sorc_no == null ? "" : waiting.sorc_no + ' | ') 
								+ (waiting.scheduled_expedited >=4 ? ("<span class='top_speed'>" + waiting.model_name + "</span>") : waiting.model_name) + ' | ' 
								+ waiting.serial_no
								+ get302(waiting.processing_position2)
								+ getFlags(waiting.quotation_first, waiting.scheduled_expedited, waiting.direct_flg, waiting.light_fix, waiting.service_repair_flg, waiting.anml_exp) +
							'</div>' +
						'</div>';
		subCount++;
	}
	waiting_html = waiting_html.replace('#count#', subCount);

	$("#wtg_list").html(waiting_html);
}

var getLevel = function(level) {
	if (level) {
		var levelText = "S" + level; // TODO
		if (level == 9 || level == 91 || level == 92 || level == 93 || level == 99) levelText = "D";
		if (level == 94 || level == 96 || level == 97 || level == 98 ) levelText = "M";
		if (level == 56 || level == 57 || level == 58 || level == 59) levelText = "E";
		return "<span class='level level_" + levelText + "'>" + levelText + "</span>";
	}
	return "<span class='level '>Ｘ</span>";
}

var showBreakOfInfect = function(infectString) {
	var $break_dialog = $('#break_dialog');
	$break_dialog.html(decodeClick(decodeText(infectString)));
	var closeButtons = {
		"退出回首页":function() {
			window.location.href = "./panel.do?method=init";
		}
	}
	if (infectString.indexOf("点检") >= 0) {
		$break_dialog.append("<p style='font-weight:bold'>" + WORKINFO.attentionForInfects + "</p>");
		closeButtons ={
			"进行点检":function() {
				window.location.href = "./usage_check.do?from=position";
			},
			"退出回首页":function() {
				window.location.href = "./panel.do?method=init";
			}
		}
	}

	$break_dialog.dialog({
		modal : true,
		resizable:false,
		dialogClass : 'ui-error-dialog',
		width : 'auto',
		title : "工位工作不能进行",
		closeOnEscape: false,
		close: function(){
			window.location.href = "./panel.do?method=init";
		},
		buttons : closeButtons
	});
}

var doInit_ajaxSuccess = function(xhrobj, textStatus){
	var resInfo = null;
	try {
		// 以Object形式读取JSON
		eval('resInfo =' + xhrobj.responseText);

		if (resInfo.errors.length > 0) {
			// 共通出错信息框
			treatBackMessages(null, resInfo.errors);
		} else {

			loadJs(
			"js/data/operator-detail.js",
			function(){
				opd_load($("#exd_listarea .ui-jqgrid-titlebar"));
			});

			if (resInfo.workstauts == -1) {
				showBreakOfInfect(resInfo.infectString);
				return;
			}

			lOptions = resInfo.lOptions;
			oOptions = resInfo.oOptions;
			dOptions = resInfo.dOptions;
			sOptions = resInfo.sOptions;
			tOptions = resInfo.tOptions;
			pauseOptions = resInfo.pauseOptions;
			pauseComments = $.parseJSON(resInfo.pauseComments);

			stepOptions = resInfo.stepOptions;
			if (!stepOptions) {
				$("#stepbutton").hide();
			} else {
				$("#stepbutton").show();
			}
			load_list(resInfo.waitings);
			paused_list(resInfo.paused);
			acceptted_list(resInfo.finished);

// $("#hide_material_id").val(resInfo.mform.material_id);
			// 存在进行中作业的时候
			if(resInfo.workstauts == 1 || resInfo.workstauts == 4) {
				getMaterialInfo(resInfo);
				treatStart(resInfo);
			} else if (resInfo.workstauts == 2 || resInfo.workstauts == 5) {
				getMaterialInfo(resInfo);
				treatPause(resInfo);
			}

			// autocomplete
			if (resInfo.customers) {
				customers = resInfo.customers;
				$("#edit_customer_name").autocomplete({
					source : customers,
					minLength :2,
					delay : 100
				});
			}
			if (resInfo.djLoaning) {
				warningConfirm(resInfo.djLoaning);
			}
		}
	} catch (e) {
		alert("name: " + e.name + " message: " + e.message + " lineNumber: "
				+ e.lineNumber + " fileName: " + e.fileName);
	};
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

var treatPause = function(resInfo) {
	getPeripharalInfo(resInfo);
	$("#editform table tbody").find("input,select,textarea").disable();
	$("#continuebutton").show();
	$("#pausebutton").hide();
	$("#breakbutton, #stepbutton").disable();
	$("#confirmbutton, #wipconfirmbutton").disable();
}

var treatStart = function(resInfo) {
	getPeripharalInfo(resInfo);
	$("#editform table tbody").find("input,select,textarea").enable();
	$("#continuebutton").hide();
	$("#pausebutton").show();
	$("#breakbutton, #stepbutton").enable();

	if (resInfo.workstauts == "4") {
		$("#confirmbutton, #wipconfirmbutton").disable();
	} else {
		$("#confirmbutton, #wipconfirmbutton").enable();
	}

	if (resInfo.instuct_obj) {
		$("#instuct_obj").show();
	}

	if (resInfo.optionalFixLabelText) {
		$("#optional_fix_label").text(resInfo.optionalFixLabelText);
	}
}

var getMaterialInfo = function(resInfo) {
	if (resInfo.patState) {
		$("#major_pat").text(resInfo.patState.pat_name)
			.attr({
				"value": resInfo.patState.pat_id
			});
		if (resInfo.dpResult) {
			$("#major_pat")
				.attr({
					"base_id": resInfo.dpResult.base_id,
					"base_name": resInfo.dpResult.base_name,
					"derive_id": resInfo.dpResult.derive_id,
					"derive_name": resInfo.dpResult.derive_name
				});
		}
	} else {
		$("#major_pat").text("").attr("value", "");
	}

	$("#hide_material_id").val(resInfo.mform.material_id);
	$("#scanner_inputer").attr("value", "");
	$("#scanner_container").hide();
	$("#material_details").show();

	if (!resInfo.finish_check) {
		var scheduled_expedited = resInfo.mform.scheduled_expedited;
		if (scheduled_expedited >= 4) {
			var top_class = (scheduled_expedited < 8 ? "top_taged" : "top_speed");
			var switch_text = (scheduled_expedited < 8 ? "切换成疾速修理" : "出货日期");
			$("#material_details td:eq(1)").html("<span title='切换疾速修理' class='" + top_class + "'>" 
				+ resInfo.mform.model_name + "</span><span class='ui-state-default' style='margin-left: 2em;padding: 4px;'>" + switch_text + 
				"</span><span class='" + top_class + "'>" 
				+ resInfo.mform.outline_time + "</span>")
				.attr("model_id", resInfo.mform.model_id);
		} else {
			$("#material_details td:eq(1)").text(resInfo.mform.model_name).attr("model_id", resInfo.mform.model_id);
		}
		$("#material_details td.td-content:eq(1)").text(resInfo.mform.serial_no);
		$("#edit_sorc_no").val(resInfo.mform.sorc_no);
		$("#edit_esas_no").val(resInfo.mform.esas_no);
		$("#edit_ocm").val("").val(resInfo.mform.ocm).trigger("change");

		if (resInfo.mform.quotation_first == 1) {
			$("#edit_customer_name").val(resInfo.mform.customer_name).addClass("fit2rapid");
		} else {
			$("#edit_customer_name").val(resInfo.mform.customer_name).removeClass("fit2rapid");
		}

		$("#edit_ocm_rank").val("").val(resInfo.mform.ocm_rank).trigger("change");
		$("#edit_ocm_deliver_date").val(resInfo.mform.ocm_deliver_date);
		$("#edit_agreed_date").val(resInfo.mform.agreed_date);
		$("#edit_level").val("").val(resInfo.mform.level).trigger("change");
		$("#edit_fix_type").val("").val(resInfo.mform.fix_type).trigger("change");
		$("#edit_service_repair_flg").val("").val(resInfo.mform.service_repair_flg).trigger("change");
		$("#edit_comment").val(resInfo.mform.comment);
		if (resInfo.mform.scheduled_manager_comment) {
			$("#edit_comment_other").show().val(resInfo.mform.scheduled_manager_comment);
		} else {
			$("#edit_comment_other").hide().val("");
		}
		$("#edit_wip_location").val(resInfo.mform.wip_location);

		$("#edit_bound_out_ocm").val("").val(resInfo.mform.bound_out_ocm).trigger("change");

	//	if (resInfo.mform.direct_flg == 1) {
			$("#edit_bound_out_ocm").parents("tr").show();
	//	} else {
	//		$("#edit_bound_out_ocm").parents("tr").hide();
	//	}

		wip_location = resInfo.mform.wip_location;

		if (resInfo.mform.selectable == 1) {
			$("#partake").attr("checked", "checked").trigger("change").next().next().text("选择式报价");
		} else {
			$("#partake").removeAttr("checked").trigger("change").next().next().text("非选择式报价");
		}

		$("#partake").append("#" + resInfo.mform.selectable);

		$("#anml_attendtion").remove();
		if (resInfo.mform.anml_exp) {
			if ($("#device_rent_area").length == 0) {
				infoPop(WORKINFO.animalExpNotice);
			}
			$("#edit_direct_flg").before("<p id='anml_attendtion'></p>");
		}

		$("#direct_rapid").removeAttr("checked");
		if (resInfo.mform.direct_flg != 1) {
		//	$("table.condform tr:last").hide();
			$("#edit_direct_flg").text("").removeClass("fit2rapid");
			$("#direct_rapid").next().hide();
		} else {
			$("#edit_direct_flg").text("直送").addClass("fit2rapid");
			if (scheduled_expedited == 2 || scheduled_expedited == 6|| scheduled_expedited == 10) {
				$("#direct_rapid").attr("checked", "checked")
					.next().show().children("span").text("快速");
			} else {
				$("#direct_rapid")
					.next().show().children("span").text("普通");
			}
		}
		$("#direct_rapid").trigger("change");

		leagal_overline = resInfo.leagal_overline;

		if (resInfo.mform.wip_location != null) {
			$("#wipconfirmbutton").val("放回WIP");
		} else {
			$("#wipconfirmbutton").val("放入WIP");
		}

		if (resInfo.qa_rank || resInfo.qa_service_free) {
			$("#editform .qa_info").show();
			$("#edit_qa_level").text(resInfo.qa_rank);
			$("#edit_service_free").text(resInfo.qa_service_free);
		} else {
			$("#editform .qa_info").hide();
		}

		if (resInfo.component_setting) {
			$("#editform .component_setting").attr("setting", true);
			if (resInfo.mform.level == 1) {
				$("#editform .component_setting").show();
			} else {
				$("#editform .component_setting").hide();
			}
		} else {
			$("#editform .component_setting").attr("setting", false).hide();
		}
		if (resInfo.special_notice) {
			$("#editform .special_notice").show();
		} else {
			$("#editform .special_notice").hide();
		}
		if (resInfo.tag_ccd == "1") {
			$("#editform #tag_ccd").attr("checked", true).trigger("change")
				.next().show().children("span").text("CCD盖玻璃 ON");
		} else if (resInfo.tag_ccd == "-1") {
			$("#editform #tag_ccd").removeAttr("checked").trigger("change")
				.next().show().children("span").text("CCD盖玻璃 OFF");
		} else {
			$("#editform #tag_ccd").hide().next().hide();
		}
	}

	if (resInfo.workstauts == 4 || resInfo.workstauts == 5) {
		$("#confirmbutton, #wipconfirmbutton").disable();
	} else {
		$("#confirmbutton, #wipconfirmbutton").enable();
	}
}
var getPeripharalInfo = function(resInfo) {
	if (resInfo.peripheralData && resInfo.peripheralData.length > 0) {
		showPeripheral(resInfo);
	}

	if (resInfo.workstauts == 1 || resInfo.workstauts == 2) {
		$("#device_details table tbody").find(".manageCode").disable();
		$("#device_details table tbody").find("input[type=button]").disable();
		$("#finishcheckbutton").disable();
	} else {
		$("#device_details table tbody").find(".manageCode").enable();
		$("#device_details table tbody").find(".manageCode").trigger("change");
	}

	if (resInfo.workstauts == 4 || resInfo.workstauts == 5) {
		hasPcs && pcsO.clear();
	} else {
		// 工程检查票
		if (resInfo.pcses && resInfo.pcses.length > 0 && hasPcs) {
			pcsO.generate(resInfo.pcses, true);
		}
	};
}

var doStart_ajaxSuccess=function(xhrobj){
	var resInfo = null;
	try {
		// 以Object形式读取JSON
		eval('resInfo =' + xhrobj.responseText);

		if (resInfo.errors.length > 0) {
			// 共通出错信息框
			treatBackMessages(null, resInfo.errors);
		} else {
			getMaterialInfo(resInfo);
			treatStart(resInfo);
			if (typeof(getRentListdata) === "function") getRentListdata();
		}
	} catch (e) {
		alert("name: " + e.name + " message: " + e.message + " lineNumber: "
				+ e.lineNumber + " fileName: " + e.fileName);
	};
};

var doStart=function(){
	wip_location = "";

	var data = {
		material_id : $("#scanner_inputer").val()
	}

	$("#scanner_inputer").attr("value", "");

	// Ajax提交
	$.ajax({
		beforeSend : ajaxRequestType,
		async : false,
		url : servicePath + '?method=doscan',
		cache : false,
		data : data,
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : doStart_ajaxSuccess
	});
};


/** 暂停重开 */
var endPause = function() {
	// 重新读入刚才暂停的维修对象
	var data = {
		material_id : $("#hide_material_id").val()
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

var doFinish_ajaxSuccess=function(xhrobj, textStatus, postData){
	var resInfo = null;
	try {
		// 以Object形式读取JSON
		eval('resInfo =' + xhrobj.responseText);

		if (resInfo.errors.length > 0) {
			var error1 = resInfo.errors[0];
			if (error1.errcode === "info.agreedDateChanged.byIf") {
				$("#edit_agreed_date").val(resInfo.agreed_date || "");
				if (resInfo.agreed_date) {
					warningConfirm(error1.errmsg, function() {
						postData.confirmed = true;
						// Ajax提交
						$.ajax({
							beforeSend : ajaxRequestType,
							async : false,
							url : servicePath + '?method=dofinish',
							cache : false,
							data : postData,
							type : "post",
							dataType : "json",
							success : ajaxSuccessCheck,
							error : ajaxError,
							complete : doFinish_ajaxSuccess
						});
					});
				} else {
					treatBackMessages(null, resInfo.errors);
				}
			} else {
				// 共通出错信息框
				treatBackMessages(null, resInfo.errors);
			}
		} else {
			$("#scanner_inputer").attr("value", "");
			$("#material_details").hide();
			$("#instuct_obj").hide();
			$("#scanner_container").show();
			$("#devicearea").hide();
			$("#manualdetailarea").hide();

			load_list(resInfo.waitings);
			paused_list(resInfo.paused);
			acceptted_list(resInfo.finished);

			if ($("#break_dialog").length > 0 && $("#break_dialog").html()) {
				//$("#pauseo_edit").hide;
				//$("#pauseo_edit").hide;
				$("#break_dialog").dialog("close");
			} else {
				checkAnmlAlert();
			}
		}
	} catch (e) {
		alert("name: " + e.name + " message: " + e.message + " lineNumber: "
				+ e.lineNumber + " fileName: " + e.fileName);
	};
};

var doFinishConfirm=function(){
	if ($("#major_pat:visible").length > 0 && parseInt($("#major_pat").attr("value"), 10) == 2) { // 2=粗镜流程
		warningConfirm("当前维修对象是 CCD 线更换对象型号的维修对象，请确定是否不进行 CCD 线更换？",
			function(){doFinish()});
	} else {
		doFinish();
	}
}

var doFinish=function(){
	$("#editform").validate({
		rules : {
			sorc_no : {
				required : true
			},
			esas_no : {
				required : true
			}
		}
	});

	if($("#editform").valid()) {
		var confirmMessages = "";

		var data = {
			sorc_no : $("#edit_sorc_no").val(),
			esas_no : $("#edit_esas_no").val(),
			model_id : $("#editform td[model_id]").attr("model_id"),
			ocm : $("#edit_ocm").val(),
			customer_name : $("#edit_customer_name").val(),
			ocm_rank : $("#edit_ocm_rank").val(),
			ocm_deliver_date : $("#edit_ocm_deliver_date").val(),
			agreed_date : $("#edit_agreed_date").val(),
			level : $("#edit_level").val(),
			fix_type : $("#edit_fix_type").val(),
			service_repair_flg : $("#edit_service_repair_flg").val(),
			wip_location : wip_location,
			comment : $("#edit_comment").val(),
			selectable : ($("#partake").attr("checked") ? "1" : "0"),
			material_id :$("#hide_material_id").val()
		}

		if (!$("#light_pat_button").is(":visible")) {
			data.pat_id = "00000000000";
		}
		if ($("#major_pat").is(":visible")) {
			// 选择的不是派生时,需要确认
			if ($("#major_pat").attr("value") == $("#major_pat").attr("base_id")) {
				confirmMessages = "请确认此维修品是否要进行CCD线更换。";
			} else {
				data.pat_id = $("#major_pat").attr("value");
			}
		}

		if (hasPcs) {
			var empty = pcsO.valuePcs(data);
			if (empty) {
				errorPop("请填写完所有的工程检查票选项后，再完成本工位作业。");
				return;
			}
		}

		if ($("#edit_direct_flg").text()) {
			data.direct_flg = 1;
			if ($("#direct_rapid").attr("checked")) {
				data.scheduled_expedited = "2";
			} else {
				data.scheduled_expedited = "-1";
			}
		}
		data.bound_out_ocm = $("#edit_bound_out_ocm option:selected").val();

		if ($("#editform #tag_ccd").next("label").is(":visible")) {
			data.tag_ccd = $("#editform #tag_ccd").is(":checked") ? "1" : "-1";
		}

		if (confirmMessages.length > 0) {
			warningConfirm(confirmMessages,
			null, 
			function(){doFinishPost(data);}, 
			"流程选择确认", "去选择流程", "继续提交"
			);
		} else {
			doFinishPost(data);
		}
	}
};
function doFinishPost(postData) {
	// Ajax提交
	$.ajax({
		beforeSend : ajaxRequestType,
		async : false,
		url : servicePath + '?method=dofinish',
		cache : false,
		data : postData,
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : function(xhrObj, textStatus) {
			doFinish_ajaxSuccess(xhrObj, textStatus, postData);
		}
	});
}

function acceptted_list(quotation_listdata){
	if ($("#gbox_exd_list").length > 0) {
		$("#exd_list").jqGrid().clearGridData();
		$("#exd_list").jqGrid('setGridParam',{data:quotation_listdata}).trigger("reloadGrid", [{current:false}]);
	} else {
		$("#exd_list").jqGrid({
			data:quotation_listdata,
			//height: 461,
			width: 1248,
			rowheight: 23,
			datatype: "local",
			colNames:['material_id','受理时间','报价时间', '修理单号', 'ESAS No.', '型号 ID', '型号' , '机身号','委托处','同意日期', '等级', '备注', '结果', 'ts','CCD盖玻璃更换','CCD线更换'],
			colModel:[
				{name:'material_id',index:'material_id', hidden:true},
				{name:'reception_time',index:'reception_time', width:70, align:'center',
					sorttype: 'date', formatter: 'date', formatoptions: {srcformat: 'Y/m/d H:i:s', newformat: 'm-d'}},
				{name:'quotation_time',index:'quotation_time', width:70, align:'center',
					sorttype: 'date', formatter: 'date', formatoptions: {srcformat: 'Y/m/d H:i:s', newformat: 'm-d H:i'}},
				{name:'sorc_no',index:'sorc_no', width:105},
				{name:'esas_no',index:'esas_no', width:50, align:'center',hidden:true},
				{name:'model_id',index:'model_id', hidden:true},
				{name:'model_name',index:'model_id', width:125},
				{name:'serial_no',index:'serial_no', width:50, align:'center'},
				{name:'ocm',index:'ocm', width:65, formatter: 'select', editoptions:{value: oOptions}},
				{name:'agreed_date',index:'agreed_date', width:50, align:'center',
					sorttype: 'date', formatter: 'date', formatoptions: {srcformat: 'Y/m/d', newformat: 'm-d'}},
				{name:'level',index:'level', width:35, align:'center', formatter: 'select', editoptions:{value: "0:(无故障);" + lOptions}},
				{name:'fix_type',index:'fix_type', width:120, formatter : function(value, options, rData){
					return rData['remark'];
				}},
				{name:'wip_location',index:'wip_location', width:60},
				{name:'ticket_flg',index:'ticket_flg', hidden:true},
				{name:'scheduled_expedited',index:'scheduled_expedited', formatter: 'select', editoptions:{value: "1:需要;-1:不需要"}, width:50},
				{name:'processing_position2',index:'processing_position2', width:50}
			],
			rowNum: 20,
			pager: "#exd_listpager",
			viewrecords: true,
			caption: "报价成果一览",
			gridview: true, // Speed up
			onSelectRow : enablebuttons2,
//			ondblClickRow : function(rid, iRow, iCol, e) {
//				popMaterialDetail(rid, true);
//			},
			hidegrid: false, 
			pagerpos: 'right',
			pgbuttons: true,
			pginput: false,
			recordpos: 'left',
			viewsortcols : [true,'vertical',true],
			gridComplete: function(){
				$("#printbutton").disable();
				$("#printaddbutton").disable();
				$("#ccdtagbutton").hide();

				var jthis = $("#exd_list");

				var dataIds = jthis.getDataIDs();
				var length = dataIds.length;
				for (var i = 0; i < length; i++) {
					var rowdata = jthis.jqGrid('getRowData', dataIds[i]);

					if (rowdata.level == "0") {
						$("#exd_list tr#" + dataIds[i] + " td[aria\\-describedby='exd_list_level']").css("backgroundColor", "orange");
					}
				}
			}
		});
	}
};

var enablebuttons2 = function(idx) {
	$("#printbutton").enable().val("重新打印小票" + $("#exd_list tr#" + idx).find("td[aria\-describedby='exd_list_ticket_flg']").text()+"份");
	$("#printaddbutton").enable();

	var rowID = $("#exd_list").jqGrid("getGridParam","selrow");
	var rowData = $("#exd_list").getRowData(rowID);
	if (rowData.scheduled_expedited) {
		$("#ccdtagbutton").show();
		if (rowData.scheduled_expedited == "1") {
			$("#ccdtagbutton").val("取消CCD盖玻璃标记");
		} else {
			$("#ccdtagbutton").val("标记CCD盖玻璃");
		}
	} else {
		$("#ccdtagbutton").hide();
	}

	// 报价说明书相关内容，估计永远用不到了
//	if(rowData.symbol1==1){
//		$("#modifybutton").enable();
//		$("#downloadbutton").enable();
//	}
}

/**
 * 小票打印
 */
var printTicket=function(addan) {

	var rowid = $("#exd_list").jqGrid("getGridParam","selrow");
	if (rowid == null || rowid=="") return;

	var rowdata = $("#exd_list").getRowData(rowid);

	var data = {
		material_id : rowdata["material_id"]
	}
	if (addan != 1) {
		data.quotator = 1;
	}
	// Ajax提交
	$.ajax({
		beforeSend: ajaxRequestType,
		async: false,
		url: 'material.do?method=printTicket',
		cache: false,
		data: data,
		type: "post",
		dataType: "json",
		success: ajaxSuccessCheck,
		error: ajaxError,
		complete:  function(xhrobj, textStatus){
			var resInfo = null;

			try {
				// 以Object形式读取JSON
				eval('resInfo =' + xhrobj.responseText);

				if (resInfo.errors.length > 0) {
					// 共通出错信息框
					treatBackMessages(null, resInfo.errors);
				} else {
					if ($("iframe").length > 0) {
						$("iframe").attr("src", "download.do"+"?method=output&fileName="+ rowdata["model_name"] + "-" + rowdata["serial_no"] +"-ticket.pdf&filePath=" + resInfo.tempFile);
					} else {
						var iframe = document.createElement("iframe");
						iframe.src = "download.do"+"?method=output&fileName="+ rowdata["model_name"] + "-" + rowdata["serial_no"] +"-ticket.pdf&filePath=" + resInfo.tempFile;
						iframe.style.display = "none";
						document.body.appendChild(iframe);
					}
				}
			} catch(e) {

			}
		}
	});
};


$(function() {
	$("input.ui-button").button();
	$("a.areacloser").hover(
		function (){$(this).addClass("ui-state-hover");},
		function (){$(this).removeClass("ui-state-hover");}
	);

	// 输入框触发，配合浏览器
	$("#scanner_inputer").keypress(function(){
	if (this.value.length === 11) {
		doStart();
	}
	});
	$("#scanner_inputer").keyup(function(){
	if (this.value.length >= 11) {
		doStart();
	}
	});

	$("#continuebutton").hide();
	$("#pausebutton").show();
	$("#confirmbutton").click(function(){
//		wip_location = "";
		showChooseMap($("#quotation_pop"), "WIP 入库选择", 
			{
				occupied : 1,
				for_agreed : 0,
				material_id : $("#hide_material_id").val()
			}, 
			function(sel_wip_location){
				wip_location = sel_wip_location;
				doFinishConfirm();
			});
//		doFinishConfirm();
	});
	$("#pausebutton").click(makePause);
	$("#continuebutton").click(endPause);
	$("#stepbutton").click(makeStep);
	$("#wipconfirmbutton").click(function(){
		if (this.value == '放入WIP')
			showChooseMap($("#quotation_pop"), "WIP 入库选择", 
				{
					occupied : 1,
					for_agreed : -1,
					material_id : $("#hide_material_id").val()
				}, 
				function(sel_wip_location){
					wip_location = sel_wip_location;
					doFinishConfirm();
				});
//			showWipEmpty();
		else {
			wip_location = $("#edit_wip_location").val();
			doFinishConfirm();
		}
	});

	$("#body-pos select").select2Buttons();
	$("#edit_agreed_date, #edit_ocm_deliver_date").datepicker({
		showButtonPanel: true,
		maxDate: 0,
		currentText: "今天"
	});
	$("#partake").button().click(function(){
		if (this.checked) {
			$(this).next().next().text("选择式报价");
		} else {
			$(this).next().next().text("非选择式报价");
		}
	});
	$("#printbutton").disable();
	$("#printbutton").click(printTicket);
	$("#printaddbutton").disable();
	$("#printaddbutton").click(function(){printTicket(1)});
	$("#ccdtagbutton").hide();
	$("#tejunbutton").button().toggle(function(){$("#tejunbutton > span > div").show()},function(evt){$("#tejunbutton > span > div").hide()})
		.find("a").click(function(){window.open(this.href, "_tejun")});

	$("#edit_customer_name").autocomplete({
		source : customers,
		minLength :2,
		delay : 100
	});

	$("#edit_level").change(function(){
		if (this.value > 5 && this.value < 9) {
			$("#edit_fix_type").val("2").trigger("change");
			$("#light_pat_button").closest("tr").hide();
			$("#major_pat_button").closest("tr").hide();
		} else {
			$("#edit_fix_type").val("1").trigger("change");

			if (this.value[0] == 9 && $("#edit_fix_type").val()==2) {
				$("#light_pat_button").parents("tr").hide();
				$("#major_pat_button").closest("tr").hide();
			}else if (this.value[0] == 9) {
				$("#light_pat_button").parents("tr").show();
				$("#major_pat_button").closest("tr").hide();
			}else {
				var fromLight = $("#light_pat_button").parents("tr").is(":visible");
				$("#light_pat_button").parents("tr").hide();
				var pat_id = $("#major_pat").attr("value");
				if (pat_id && pat_id.length) {
					$("#major_pat_button").closest("tr").show();
					// 中小修理切回大修理时，自动切换更改到大修维修流程
					if (fromLight) {
						$("#major_pat").text($("#major_pat").attr("base_name"))
							.attr("value", $("#major_pat").attr("base_id"));
					}
				} else {
					$("#major_pat_button").closest("tr").hide();
				}
			}
		}
		if ($("#editform .component_setting").attr("setting") == "true") {
			if (this.value == 1) {
				$("#editform .component_setting").show();
			} else {
				$("#editform .component_setting").hide();
			}
		}
	});

	$("#edit_fix_type").change(function(){
		var edit_level = $("#edit_level")[0].value;
//		if(this.value==1 && (edit_level==9 || edit_level==91 || edit_level==92 || edit_level==93)){
		if (this.value==1) {
			if (f_isLightFix(edit_level)) {
				$("#light_pat_button").closest("tr").show();
			} else
			if ($("#major_pat").attr("value")) {
				$("#major_pat_button").closest("tr").show();
			}
			return;
		}

		$("#light_pat_button").parents("tr").hide();
		$("#major_pat_button").closest("tr").hide();
	});

	$("#direct_rapid").click(function(){
		if (this.checked == true) {
			$(this).next().children("span").text("快速");
		} else {
			$(this).next().children("span").text("普通");
		}
	}).next().hide();

	$("#editform #tag_ccd").click(function(){
		var $tag_ccd = $(this);
		if (this.checked == true) {
			warningConfirm("是否要将修理品标记为<b>需要更换CCD盖玻璃</b>？", function(){
				$tag_ccd.next().children("span").text("CCD盖玻璃 ON");
				$tag_ccd.attr("checked", true).trigger("change");
			}, function(){
				$tag_ccd.removeAttr("checked").trigger("change");
			})
		} else {
			warningConfirm("是否要将修理品标记为<b>无须更换CCD盖玻璃</b>？", function(){
				$tag_ccd.next().children("span").text("CCD盖玻璃 OFF");
				$tag_ccd.removeAttr("checked").trigger("change");
			}, function(){
				$tag_ccd.attr("checked", true).trigger("change");
			});
		}
		return false;
	}).next().hide();

	$("#modifybutton").disable();
	$("#downloadbutton").disable();

	$("#makebutton").click(function(){
		var material_id=$("#hide_material_id").val();
		edit_quotistion(material_id);
	});

	$("#modifybutton").click(function(){
		var rowID = $("#exd_list").jqGrid("getGridParam","selrow");
		var rowData = $("#exd_list").getRowData(rowID);
		edit_quotistion(rowData.material_id);
	});

	$("#downloadbutton").click(function(){
		var rowID = $("#exd_list").jqGrid("getGridParam","selrow");
		var rowData = $("#exd_list").getRowData(rowID);
		var data={
			"material_id":rowData.material_id
		}

		$.ajax({
			beforeSend : ajaxRequestType,
			async : true,
			url :'quotaion_prospectus.do?method=report',
			cache : false,
			data : data,
			type : "post",
			dataType : "json",
			success : ajaxSuccessCheck,
			error : ajaxError,
			complete : function(xhjObject) {
				var resInfo = null;
				eval("resInfo=" + xhjObject.responseText);
				if (resInfo && resInfo.fileName) {
					if ($("iframe").length > 0) {
						$("iframe").attr("src", "download.do" + "?method=output&filePath=" + resInfo.filePath+"&fileName="+resInfo.fileName);
					} else {
						var iframe = document.createElement("iframe");
						iframe.src = "download.do" + "?method=output&filePath=" + resInfo.filePath+"&fileName="+resInfo.fileName;
						iframe.style.display = "none";
						document.body.appendChild(iframe);
					}
				} else {
					errorPop("文件导出失败！");
				}
			}
		});
	});

	hasPcs = (typeof pcsO === "object");

	if (hasPcs) {
		$("#manualdetailarea").hide();
		pcsO.init($("#manualdetailarea"), false);
	}

	doInit();

	//设定
	$("#light_pat_button").click(function(){
		setMpaObj.initDialog($("#light_fix_dialog"), $("#hide_material_id").val(), $("#edit_level").val(), 
			$("#material_details td:eq(1)").attr("model_id"), false);
	});

	//设定
	$("#major_pat_button").click(function(){

		var thisPatId = $("#major_pat").attr("value");
		if (thisPatId == $("#major_pat").attr("base_id")) {
			$("#major_pat").text($("#major_pat").attr("derive_name"))
				.attr("value", $("#major_pat").attr("derive_id"));
		} else {
			$("#major_pat").text($("#major_pat").attr("base_name"))
				.attr("value", $("#major_pat").attr("base_id"));
		}
	});

	if (typeof instruct_load_by_working === "function") {
		$("#instuct_obj").click(instruct_load_by_working);
	}

	$("#optional_fix_button").click(function(){
		var $optional_fix_dialog = $("#optional_fix_dialog");
		if ($optional_fix_dialog.length == 0) {
			$("body").append("<div id='optional_fix_dialog'></div>");
			$optional_fix_dialog = $("#optional_fix_dialog");
		}
		setOpfObj.initDialog($optional_fix_dialog, $("#hide_material_id").val(), $("#edit_level").val(), 
			callbackOnChanged = function(resInfo){
				$("#optional_fix_label").text(resInfo.labelText);
				if (resInfo.removeList) {
					for (var i in resInfo.removeList) {
						var removeId = resInfo.removeList[i];
						var removeItem = $("#optional_fix_sel > option[value=" + removeId + "]").text();
						if (removeItem) {
							pcsO.removeByTitle("选择修理-" + removeItem);
						}
					}
				}
				if (resInfo.appendPcses) {
					pcsO.append(resInfo.appendPcses);
				}
			});
	});

	$("#material_details td[model_id]").click(function(evt){
		if (evt.target.tagName === "SPAN") {
			var $top = $(this).find(".top_speed,.top_taged");
			if ($top.length > 0) {
				$.ajax({
					beforeSend : ajaxRequestType,
					async : true,
					url : servicePath + '?method=doSwitchTopSpeed',
					cache : false,
					data : {material_id : $("#hide_material_id").val()},
					type : "post",
					dataType : "json",
					success : ajaxSuccessCheck,
					error : ajaxError,
					complete : function(xhrobj) {
						var resInfo = $.parseJSON(xhrobj.responseText);
						if (resInfo.errors.length > 0) {
							// 共通出错信息框
							treatBackMessages(null, resInfo.errors);
						} else {
							if(!resInfo.isTopSpeed) {
								$top.removeClass("top_speed").addClass("top_taged");
								$top.next(".ui-state-default").text("切换成疾速修理");
							} else {
								$top.removeClass("top_taged").addClass("top_speed");
								$top.next(".ui-state-default").text("出货日期");
							}
						}
					}
				});
			}
		}
	});

	$("#ccdtagbutton").click(ccdTagAssign);

	$("#wtg_list").on('dblclick', ".waiting", function(){
		var material_id = this.id.replace("w_", "");
		showMaterial(material_id);
	});

});

function load_list(listdata){

	if ($("#gbox_uld_list").length > 0) {
		$("#uld_list").jqGrid().clearGridData();
		$("#uld_list").jqGrid('setGridParam',{data:listdata}).trigger("reloadGrid", [{current:false}]);
	} else {
		$("#uld_list").jqGrid({
			data:listdata,
			//height: 461,
			width: 1248,
			rowheight: 23,
			datatype: "local",
			colNames:['material_id','受理时间', '修理单号', 'ESAS No.', '型号 ID', '型号' , '机身号','委托处','同意日期', '优先报价', '等级', '备注'],
			colModel:[
				{name:'material_id',index:'material_id', hidden:true, key:true},
				{name:'reception_time',index:'reception_time', width:70, align:'center',
					sorttype: 'date', formatter: 'date', formatoptions: {srcformat: 'Y/m/d H:i:s', newformat: 'm-d'}},
				{name:'sorc_no',index:'sorc_no', width:80},
				{name:'esas_no',index:'esas_no', width:50, align:'center',hidden:true},
				{name:'model_id',index:'model_id', hidden:true},
				{name:'model_name',index:'model_id', width:125, formatter : function(value, options, rData){
					if (rData['scheduled_expedited'] && rData['scheduled_expedited'] >= 4) {
						return "<span class='top_speed'>" + rData['model_name'] + "</span>";
					} else {
						return rData['model_name'];
					}
				}},
				{name:'serial_no',index:'serial_no', width:50, align:'center'},
				{name:'ocm',index:'ocm', width:65, formatter: 'select', editoptions:{value: oOptions}},
				{name:'agreed_date',index:'agreed_date', width:50, align:'center',
					sorttype: 'date', formatter: 'date', formatoptions: {srcformat: 'Y/m/d', newformat: 'm-d'}},
				{name:'quotation_first',index:'quotation_first', width:35, align:'center', formatter: 'select', editoptions:{value: "0:;1:优先;10:超时;11:超时"}},
				{name:'level',index:'level', width:35, align:'center', formatter: 'select', editoptions:{value: lOptions}},
				{name:'fix_type',index:'fix_type', width:150, formatter : function(value, options, rData){
					return rData['remark'] + (rData['anml_exp'] == '1' ? " 动物实验用" : "") + (rData['scheduled_expedited'] && rData['scheduled_expedited'] >= 4 ? "极速修理" : "");
				}}//formatter: 'select', editoptions:{value: tOptions}}
			],
			rowNum: 20,
			rownumbers : true,
			toppager: false,
			pager: "#uld_listpager",
			viewrecords: true,
			caption: "待报价维修品一览",
			gridview: true, // Speed up
			pagerpos: 'right',
			pgbuttons: true,
			pginput: false,
			ondblClickRow : function(rid, iRow, iCol, e) {
				showMaterial(rid);
			},
			recordpos: 'left',
			viewsortcols : [true,'vertical',true]
			// gridComplete: function(){
		});
	}

};

var get302 = function(processing302) {
	if (processing302) {
		if (processing302 == "已完成") {
			return "<div class='concern concern_end'>已完成</div>";
		} else {
			return "<div class='concern concern_start'>" + processing302 + "</div>";
		}
	}
	return "";
}

var getFlags = function(over_time, expedited, direct_flg, light_fix, f_service_repair_flg, anml_exp) {
	if (expedited >= 4) {
		expedited -= 4;
	}
	if (over_time || expedited || direct_flg || light_fix || anml_exp) {
		var retDiv = "<div class='material_flags'>";
		if (f_service_repair_flg > 0) {
			var f = f_service_repair_flg >= 10;
			var sta = "　";
			var service_repair_flg = f_service_repair_flg % 10;
			if (service_repair_flg == 1) {
				sta = "返";
			} else if (service_repair_flg == 2) {
				sta = "Ｑ";
			} else if (service_repair_flg == 3) {
				sta = "备";
			}
			retDiv += "<div class='service_repair_flg" + (f ? " refee" : "") + "'>" + sta + "</div>";
		}
		if (expedited >= 2) retDiv += "<div class='rapid_direct_flg'><span>直送快速</span></div>";
		else {
			if (expedited == 1) retDiv += "<div class='tube-blue'>急</div>";
			retDiv += (direct_flg == 1 ? "<div class='direct_flg'>直</div>" : "");
		}
		if (light_fix == 1) {
			retDiv += "<div class='light_fix'>小</div>";
		}
		if (over_time == 1) {
			retDiv += "<div class='over_time'>超</div>";
		}
		if (anml_exp == 1) {
			retDiv += "<div class='rapid_direct_flg anml_exp'><span>动物实验</span></div>"
		}
		
		retDiv += "</div>";
		return retDiv;
	} else {
		return "";
	}
}

var createCommentOptions = function(comments){
	var commentArray = comments.split(";");
	var sRet = "";
	for (var commentIdx in commentArray) {
		var comment = commentArray[commentIdx];
		sRet += "<option value='" + comment + "'>" + comment + "</option>";
	}
	return sRet;
}

var checkAnmlAlert = function() {
	if ($("#anml_attendtion").length > 0) {
		errorPop(WORKINFO.animalExpClean);
	}
}

var ccdTagAssign = function(){
	var rowID = $("#exd_list").jqGrid("getGridParam","selrow");
	var rowData = $("#exd_list").getRowData(rowID);

	var postData = {
		material_id : rowData.material_id,
		scheduled_expedited : (($("#ccdtagbutton").val() == "取消CCD盖玻璃标记") ? "-1" : "1") 
	}

	// Ajax提交
	$.ajax({
		beforeSend : ajaxRequestType,
		async : false,
		url : servicePath + '?method=doCcdTagAssign',
		cache : false,
		data : postData,
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : function(xhr, status) {
			var resInfo = $.parseJSON(xhr.responseText)
			
			acceptted_list(resInfo.finished);
		}
	});

}

var showMaterial = function(material_id) {
	var $process_dialog = $("#material_detail_dialog");
	if ($process_dialog.length == 0) {
		$("body").append("<div id='material_detail_dialog'></div>");
		$process_dialog = $("#material_detail_dialog");
	}
	$process_dialog.hide();
	// 导入编辑画面
	$process_dialog.load("widget.do?method=materialDetail&material_id=" + material_id,
		function(responseText, textStatus, XMLHttpRequest) {
			$process_dialog.dialog({
				title : "维修对象详细信息",
				width : 800,
				show : "blind",
				height : 'auto' ,
				resizable : false,
				modal : true,
				minHeight : 200,
				buttons : {
					"关闭": function(){
						$process_dialog.dialog('close');
					}
				}
			});
	});
};
