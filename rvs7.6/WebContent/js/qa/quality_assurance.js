/** 一览数据对象 */
var listdata = {};
var result_listdata = {};
/** 服务器处理路径 */
var servicePath = "qualityAssurance.do";
var hasPcs = (typeof pcsO === "object");

var lOptions = {};
var oOptions = {};

var pauseOptions = "";
var stepOptions = "";
var pauseComments = {};
var allowScan = false;

var downPdf = function(sorc_no) {
	if ($("iframe").length > 0) {
		$("iframe").attr("src", "download.do"+"?method=output&fileName="+ sorc_no +".zip&from=pcs");
	} else {
		var iframe = document.createElement("iframe");
        iframe.src = "download.do"+"?method=output&fileName="+ sorc_no +".zip&from=pcs";
        iframe.style.display = "none";
        document.body.appendChild(iframe);
	}
}

var showBreakOfInfect = function(infectString) {
	var $break_dialog = $('#break_dialog');
	$break_dialog.html(decodeText(infectString));
	var closeButtons = {
		"退出回首页":function() {
				window.location.href = "./panel.do?method=init";
		}
	}
	if (infectString.indexOf("点检") >= 0) {
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

var doFinish = function(type) {

	var data = {};
	var empty = false;
	if (hasPcs) {
		empty = pcsO.valuePcs(data);
	}

	if (empty) {
		errorPop("工程检查票存在没有点检的选项，不能检测通过。");
	} else {
		data.position_id = $("#wk_position_id").val();
	
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
			complete : function(xhrobj) {
				var resInfo = null;
				try {
					// 以Object形式读取JSON
					eval('resInfo =' + xhrobj.responseText);
				} catch (e) {
					errorPop("v");
				}
	
				$("#scanner_inputer").attr("value", "");
				$("#material_details").hide();
				$("#scanner_container").show();
				$("#devicearea").hide();
				$("#pcsarea").hide();

				checkAnmlAlert();

				doInit();
			}
		});
	}
};

var doForbid = function(type) {
	var data = {};
	var empty = false;
	if (hasPcs) {
		empty = pcsO.valuePcs(data);
	}

	if (empty) {
		errorPop("工程检查票存在没有点检的选项，不能检测通过。");
	} else {
		warningConfirm("请确定是否当前维修对象未通过品保，这将会使其退回经理处要求返工。",function(){
			$.ajax({
				beforeSend : ajaxRequestType,
				async : false,
				url : servicePath + '?method=doforbid',
				cache : false,
				data : data,
				type : "post",
				dataType : "json",
				success : ajaxSuccessCheck,
				error : ajaxError,
				complete : function() {
					$("#scanner_inputer").attr("value", "");
					$("#material_details").hide();
					$("#scanner_container").show();
					$("#devicearea").hide();
					$("#pcsarea").hide();

					checkAnmlAlert();

					doInit();
				}
			});
		});
	}
};

var doPcsFinish=function(){
	var data = {};
	var empty = false;
	if (hasPcs) {
		empty = pcsO.valuePcs(data);
	}

	if (empty) {
		errorPop("存在没有点检的选项，不能确认工程检查票。");
	} else {
		// Ajax提交
		$.ajax({
			beforeSend : ajaxRequestType,
			async : false,
			url : servicePath + '?method=dopcsfinish',
			cache : false,
			data : data,
			type : "post",
			dataType : "json",
			success : ajaxSuccessCheck,
			error : ajaxError,
			complete : function(xhrobj){
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
			}
		});
	}
};

function acceptted_list(result_listdata) {

	if ($("#gbox_exd_list").length > 0) {
		$("#exd_list").jqGrid().clearGridData();
		$("#exd_list").jqGrid('setGridParam', {data : result_listdata}).trigger("reloadGrid", [{current : false}]);
	} else {
		$("#exd_list").jqGrid({
			toppager : true,
			data : result_listdata,
			// height: 461,
			width : 992,
			rowheight : 23,
			datatype : "local",
			colNames : ['受理时间', '同意时间', '总组完成时间', '品保时间', '修理单号',
					'ESAS No.', '型号 ID', '型号', '机身号', '委托处', '等级', '工程检查票'],
			colModel : [{
						name : 'reception_time',
						index : 'reception_time',
						width : 35,
						align : 'center', 
						sorttype: 'date', formatter: 'date', formatoptions: {srcformat: 'Y/m/d H:i:s', newformat: 'm-d'}
					}, {
						name : 'agreed_date',
						index : 'agreed_date',
						width : 35,
						align : 'center', 
						sorttype: 'date', formatter: 'date', formatoptions: {srcformat: 'Y/m/d', newformat: 'm-d'}
					}, {
						name : 'finish_time',
						index : 'finish_time',
						width : 65,
						align : 'center', 
						sorttype: 'date', formatter: 'date', formatoptions: {srcformat: 'Y/m/d', newformat: 'm-d'}
					}, {
						name : 'quotation_time',
						index : 'quotation_time',
						width : 65,
						align : 'center', 
						sorttype: 'date', formatter: 'date', formatoptions: {srcformat: 'Y/m/d H:i:s', newformat: 'H:i'}
					}, {
						name : 'sorc_no',
						index : 'sorc_no',
						width : 105
					}, {
						name : 'esas_no',
						index : 'esas_no',
						width : 50,
						align : 'center'
					}, {
						name : 'model_id',
						index : 'model_id',
						hidden : true
					}, {
						name : 'model_name',
						index : 'model_id',
						width : 125
					}, {
						name : 'serial_no',
						index : 'serial_no',
						width : 50,
						align : 'center'
					}, {
						name : 'ocm',
						index : 'ocm',
						width : 65, formatter: 'select', editoptions:{value: oOptions}
					}, {
						name : 'level',
						index : 'level',
						width : 35,
						align : 'center', formatter: 'select', editoptions:{value: lOptions}
					}, {
						name : 'scheduled_expedited',
						index : 'scheduled_expedited',
						width : 85,
						align : 'center',
						formatter : function(value, options, rData){
//							if (rData['level'] > 5) {
//								return "(无)";
//							} else  {
								return "<a href='javascript:downPdf(\"" + rData['sorc_no'] + "\");' >" + rData['sorc_no'] + ".zip</a>";
//							}
		   				}
					}],
			rowNum : 20,
			toppager : false,
			pager : "#exd_listpager",
			viewrecords : true,
			hidegrid : false,
			caption : "今日品保完成一览",
			gridview : true, // Speed up
			pagerpos : 'right',
			pgbuttons : true,
			pginput : false,
			recordpos : 'left',
			viewsortcols : [true, 'vertical', true],
			gridComplete : function() {
			}
		});
	}
};

var doInit_ajaxSuccess = function(xhrobj, textStatus){
	var resInfo = null;
	try {
		// 以Object形式读取JSON
		eval('resInfo =' + xhrobj.responseText);

		if (resInfo.errors.length > 0) {
			// 共通出错信息框
			treatBackMessages(null, resInfo.errors);
		} else {

			if ($("#comments_dialog:visible").length > 0) {
				$("#comments_dialog textarea").val("");
				$("#comments_dialog").hide();
			}

			lOptions = resInfo.lOptions;
			oOptions = resInfo.oOptions;

			if (typeof(opd_load) !== "function") {
				loadJs(
				"js/data/operator-detail.js",
				function(){
					opd_load($("#exd_listarea .ui-jqgrid-titlebar"));
				});
			}

			if (resInfo.workstauts == -1) {
				showBreakOfInfect(resInfo.infectString);
				return;
			}

			load_list(resInfo.waitings);
			acceptted_list(resInfo.finished);
			if (resInfo.pauseOptions) {
				pauseOptions = resInfo.pauseOptions;
				pauseComments = $.parseJSON(resInfo.pauseComments);
			}
			if (resInfo.stepOptions) stepOptions = resInfo.stepOptions;
			
			// 存在进行中作业的时候
			if(resInfo.workstauts != 0) {
				treatStart(resInfo);
			} else {
				$("#uld_listarea").addClass("waitForStart");
				allowScan = true;
				if ($("#scanner_inputer").val()) {
					doStart();
				}
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
		async : true,
		url : servicePath + '?method=jsinit',
		cache : false,
		data : {position_id : $("#wk_position_id").val()},
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : doInit_ajaxSuccess
	});
};

$(function() {
	// 画面项目效果
	$("input.ui-button").button();
	$("a.areacloser").hover(function() {
		$(this).addClass("ui-state-hover");
	}, function() {
		$(this).removeClass("ui-state-hover");
	});

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

	$("#pcscombutton").click(doPcsFinish);
	$("#passbutton").click(doFinish);
	$("#forbidbutton").click(doForbid);
	$("#pausebutton").click(makePause);
	$("#continuebutton").click(endPause);
	$("#stepbutton").click(makeStep);

	if ($("#devicearea").length > 0) {
		$("#devicearea div").removeClass("dwidth-full").addClass("dwidth-middleright");
		$("#devicearea").next().removeClass("areaencloser");
	}
	if (hasPcs) {
		pcsO.init($("#pcsarea"), true);
	}

	doInit();

	$("#comments_dialog span").on("click",function(){
		if($("#comments_dialog").width() < 100){
			$("#comments_dialog").animate({width:"576px"},300,function(){
				$("#comments_dialog span").removeClass("icon-share").addClass("icon-enter-2");
				$("#comments_dialog textarea").slideDown(200);
			});
		}else{
			$("#comments_dialog textarea").slideUp(200,function(){
				$("#comments_dialog span").removeClass("icon-enter-2").addClass("icon-share");
				$("#comments_dialog").animate({width:"30px"},300);
			});
		}
	});
});

function load_list(t_listdata) {

	if ($("#gbox_uld_list").length > 0) {
		$("#uld_list").jqGrid().clearGridData();
		$("#uld_list").jqGrid('setGridParam', {data : t_listdata}).trigger("reloadGrid", [{current : false}]);
	} else {
		var isL = ($("#forbidbutton").length > 0);

		$("#uld_list").jqGrid({
			toppager : true,
			data : t_listdata,
			// height: 461,
			width : 992,
			rowheight : 23,
			datatype : "local",
			colNames : ['受理时间', '同意时间', '总组完成时间', '修理单号', 'ESAS No.', '型号 ID', '型号', '机身号', '委托处', '等级', '加急', '特记','工程检查票出检'],
			colModel : [{
						name : 'reception_time',
						index : 'reception_time',
						width : 35,
						align : 'center', 
						sorttype: 'date', formatter: 'date', formatoptions: {srcformat: 'Y/m/d H:i:s', newformat: 'm-d'}
					}, {
						name : 'agreed_date',
						index : 'agreed_date',
						width : 35,
						align : 'center', 
						sorttype: 'date', formatter: 'date', formatoptions: {srcformat: 'Y/m/d', newformat: 'm-d'}
					}, {
						name : 'finish_time',
						index : 'finish_time',
						width : 65,
						align : 'center', 
						sorttype: 'date', formatter: 'date', formatoptions: {srcformat: 'Y/m/d', newformat: 'm-d'}
					}, {
						name : 'sorc_no',
						index : 'sorc_no',
						width : 105
					}, {
						name : 'esas_no',
						index : 'esas_no',
						width : 50,
						align : 'center'
					}, {
						name : 'model_id',
						index : 'model_id',
						hidden : true
					}, {
						name : 'model_name',
						index : 'model_id',
						width : 125
					}, {
						name : 'serial_no',
						index : 'serial_no',
						width : 50,
						align : 'center'
					}, {
						name : 'ocm',
						index : 'ocm',
						width : 65, formatter: 'select', editoptions:{value: oOptions}
					}, {
						name : 'level',
						index : 'level',
						width : 35,
						align : 'center', formatter: 'select', editoptions:{value: lOptions}
					}, {
						name : 'scheduled_expedited',
						index : 'scheduled_expedited',
						width : 35,
						align : 'center', formatter: 'select', editoptions:{value: "0:;1:加急;2:直送快速"}
					}, {
						name:'status',index:'status', width:65
					},{
						name:'qa_check_time',index:'qa_check_time', width:65,align:'center',formatter:function(data, row, record) {
							if (data == null || data == "") {
								return "";
							}
							if (isL) {
								return '<input type="button" value="确认出检" class="click_start" onclick="doStart(\''+record["material_id"]+'\')"/>';
							} else {
								return "已出检";
							}
						}
					}],
			rowNum : 20,
			toppager : false,
			pager : "#uld_listpager",
			viewrecords : true,
			caption : "待品保维修品一览",
			gridview : true, // Speed up
			pagerpos : 'right',
			pgbuttons : true,
			pginput : false,
			recordpos : 'left',
			viewsortcols : [true, 'vertical', true],
			gridComplete : function() {
				$("#uld_list").find(".click_start").button();
			}
		});
	}

};

function expeditedText(scheduled_expedited) {
	if (scheduled_expedited === '2') return "直送快速";
	if (scheduled_expedited === '1') return "加急";
	return "";
}

var treatStart = function(resInfo) {
	$("#scanner_inputer").attr("value", "");
	$("#scanner_container").hide();
	$("#material_details").show();
	$("#devicearea").show();
	$("#pcsarea").show();

	var mform = resInfo.mform;
	$("#anml_attendtion").remove();
	if (mform) {
		var $hidden_id = $("#pauseo_material_id");
		$hidden_id.val(mform.material_id);
		if (mform.anml_exp) {
			infoPop(WORKINFO.animalExpNotice);
			$hidden_id.before("<attendtion id='anml_attendtion'></attendtion>");
		}

		$("#show_model_name").text(mform.model_name);
		$("#show_serial_no").text(mform.serial_no);
		$("#show_sorc_no").text(mform.sorc_no);
		$("#show_esas_no").text(mform.esas_no);
		$("#show_agreed_date").text(mform.agreed_date);
		$("#show_finish_time").text(mform.finish_time);
		$("#show_level").text(mform.levelName);
		$("#show_scheduled_expedited").text(expeditedText(mform.scheduled_expedited));
	}

	$("#hidden_workstauts").val(resInfo.workstauts);

	// 工程检查票
	if (resInfo.pcses && resInfo.pcses.length > 0 && hasPcs) {
		pcsO.generate(resInfo.pcses, $("#forbidbutton").length > 0);
	}

	// 维修对象备注信息
	if (resInfo.material_comment) {
		$("#comments_dialog textarea").val(resInfo.material_comment);
		$("#comments_dialog").css({
			position:"fixed",
			right:0,
			top:"50%",
			backgroundColor:"#fff",
			color:"#000",
			width:"576px",
			transform:"translateY(-50%)"
		}).show();
	}

	if (resInfo.peripheralData && resInfo.peripheralData.length > 0) {
		showPeripheral(resInfo);

		if (resInfo.workstauts == 4) {
			$("#device_details table tbody").find(".manageCode").enable();
			$("#device_details table tbody").find(".manageCode").trigger("change");
			$("#pcsarea").hide();
			$("#finishcheckbutton").enable();
			if (hasPcs) {
				pcsO.clear();
			};
		} else {
			$("#device_details table tbody").find(".manageCode").disable();
			$("#device_details table tbody").find("input[type=button]").disable();
			$("#finishcheckbutton").disable();
		}
	} else {
		$("#finishcheckbutton").disable();
	}

	if (resInfo.workstauts == 1) {
		$("#pcscombutton").show();
		// if ($("#pcs_pages input").length > 0) $("#pcscombutton").disable(); // V2 disable
		$("#forbidbutton").hide();
		$("#passbutton").hide();
		$("#stepbutton").show();
		$("#pausebutton").show();
		$("#continuebutton").hide();
	} else if (resInfo.workstauts == 1.5) {
		$("#pcscombutton").hide();
		$("#forbidbutton").show();
		$("#passbutton,#stepbutton").show();
		$("#pausebutton").show();
		$("#continuebutton").hide();
		$("#devicearea").hide();
	} else if (resInfo.workstauts == 2) {
		$("#pcscombutton").show();
		// if ($("#pcs_pages input").length > 0) $("#pcscombutton").disable(); // V2 disable
		$("#forbidbutton").hide();
		$("#passbutton,#stepbutton").hide();
		$("#pausebutton").hide();
		$("#continuebutton").show();
	} else if (resInfo.workstauts == 2.5) {
		$("#pcscombutton").hide();
		$("#forbidbutton").hide();
		$("#passbutton,#stepbutton").hide();
		$("#pausebutton").hide();
		$("#continuebutton").show();
		$("#devicearea").hide();
	} else if (resInfo.workstauts == 1.9) {
		$("#pcsarea").hide();
		$("#pcscombutton").hide();
		$("#forbidbutton").show();
		$("#passbutton,#stepbutton").show();
		$("#pausebutton").hide();
		$("#continuebutton").hide();
	} else if (resInfo.workstauts == 4) {
		$("#pcscombutton").show().disable();
		$("#forbidbutton").hide();
		$("#passbutton").hide();
		$("#pausebutton").show();
		$("#continuebutton").hide();
	} else if (resInfo.workstauts == 5) {
		$("#pcscombutton").show().disable();
		$("#forbidbutton").hide();
		$("#passbutton").hide();
		$("#pausebutton").hide();
		$("#continuebutton").show();
	}

	$("#uld_listarea").removeClass("waitForStart");
};

var doStart_ajaxSuccess=function(xhrobj){
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

var doStart=function(material_id){
	if (!allowScan) return;
	var data = {
		material_id : material_id || $("#scanner_inputer").val()
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
						reason : $("#pauseo_edit_pause_reason").val(),
						workstauts : $("#hidden_workstauts").val()
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
							// doFinish_ajaxSuccess(xhr, status);
							$("#pausebutton").hide();
							$("#continuebutton").show();
							$("#pcscombutton").hide();
							$("#forbidbutton").hide();
							$("#passbutton,#stepbutton").hide();
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

			$("#pauseo_edit_pause_reason").val("").trigger("change");

			makePauseDialog(jBreakDialog);
		});
};

/** 暂停重开 */
var endPause = function() {
	// 重新读入刚才暂停的维修对象
	var data = {
		material_id : $("#pauseo_material_id").val(),
		workstauts : $("#hidden_workstauts").val()
	};

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

/** 正常中断 **/
var makeStep = function(){
	var data = {
		material_id : $("#pauseo_material_id").val()
	}
	
	var jBreakDialog = $("#break_dialog");
	if (jBreakDialog.length === 0) {
		$("body.outer").append("<div id='break_dialog'/>");
		jBreakDialog = $("#break_dialog");
	}

	jBreakDialog.hide();

	// 导入中断画面
	jBreakDialog.load("widget.do?method=breakoperator",function(responseText, textStatus, XMLHttpRequest) {

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
		makeStepDialog(jBreakDialog);
	});
	jBreakDialog.show();
};

/** 中断信息弹出框 */
var makeStepDialog = function(jBreakDialog) {
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
						comments : $("#edit_comments").val()
					}
					
					if (hasPcs) {
						pcsO.valuePcs(data, true);
					}
					
					if (parseInt($("#break_reason").val()) > 70 && $("#pcs_contents input").length > 0) {
						jBreakDialog.dialog({
							dialogClass : 'ui-warn-dialog',
							modal : true,
							width : 450,
							title : "提示信息",
							buttons :{
								"确定":function(){
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
											$("#scanner_inputer").attr("value", "");
											$("#material_details").hide();
											$("#scanner_container").show();
											$("#devicearea").hide();
											$("#pcsarea").hide();
											doInit();
											jBreakDialog.dialog("close");

											checkAnmlAlert();
										}
									});
									jBreakDialog.dialog("close");
								},
								"取消":function(){
									jBreakDialog.dialog("close");
								}
							}
						});
						jBreakDialog.html("<span class='errorarea'>" + WORKINFO.confirmPcsWhenBreak + "</span>"); // 请确定与您作业相关的工程检查票项目已经输入或点检。
					} else {
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
								$("#scanner_inputer").attr("value", "");
								$("#material_details").hide();
								$("#scanner_container").show();
								$("#devicearea").hide();
								$("#pcsarea").hide();
								doInit();
								jBreakDialog.dialog("close");

								checkAnmlAlert();
							}
						});
					}
				}
			},
			"关闭":function(){ 
				$(this).dialog("close"); 
			}
		}
	})
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