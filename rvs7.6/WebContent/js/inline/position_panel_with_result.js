/** 一览数据对象 */
var listdata = {};

/** 服务器处理路径 */
var servicePath = "position_panel.do";

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
var pauseOptions = "";
var breakOptions = "";

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
						comments : $("#edit_comments").val()
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

var remapworking = function(workingPfs) {

	var $dmSelect = null;
	if ($("#dm_select_all").length > 0) {
		$dmSelect = $($("#dm_select_all").html());
		$dmSelect.find(".select2Buttons").remove();
		$dmSelect.find("option").each(function(idx, ele){
			var text = $(ele).text();
			$(ele).text(text.replace(/[^\u00-\uFF]\s*/g,''));
		});
	}

	var waiting_html = "";
	for (var iworking = 0; iworking < workingPfs.length; iworking++) {
		var waiting = workingPfs[iworking];
		var tube_id = "w_" + waiting.material_id;
		if (waiting.operate_result == 5) {
			tube_id = tube_id + "_5";
		}
		waiting_html += '<div class="waiting tube" id="' + tube_id + '">' +
							'<div class="tube-liquid' + expeditedColor(waiting.expedited)  + '">' +
								(waiting.sorc_no == null ? "" : waiting.sorc_no + ' | ') + waiting.category_name + ' | ' + waiting.model_name + ' | ' + waiting.serial_no;
		if (waiting.operate_result == 5) {
			waiting_html +=	'<div class="material_flags"><div class="package">箱</div></div>';
		} else if (waiting.fix_type == 3) {
			waiting_html +=	'<div class="material_flags"><div class="rc">备</div></div>';
		}								
		waiting_html +=	'</div>' + '<div class="dm_select"></div>';
		waiting_html +=	'</div>';
	}
	var $waiting_html = $(waiting_html);

	if ($dmSelect != null) {
		$waiting_html.find(".dm_select").html($dmSelect);
		$waiting_html.each(function(idx, ele){
			$(ele).find(".dm_select .manager_no").attr("id", $(ele).attr("id") + "se").select2Buttons();
		});
	}

	$("#workings").html($waiting_html);

}

var treatStart = function(resInfo) {
	$("#scanner_inputer").attr("value", "");
	$("#position_status").text("处理中");
	$("#position_status").css("color", "#58b848");

	if (resInfo.action_time) {
		$("#s_t").text(resInfo.action_time+"开始");
	} else {
		var thistime=new Date();
		var hours=thistime.getHours() 
		var minutes=thistime.getMinutes() 

		$("#s_t").text(prevzero(hours) + ":" + prevzero(minutes)+"开始");
	}
	$("#material_details td:eq(9)").text(minuteFormat(resInfo.leagal_overline));
	leagal_overline = resInfo.leagal_overline;

	// 暂时的
	if (resInfo.mform) {
		var tube_id = "w_" + resInfo.mform.material_id;
		if (resInfo.mform.operate_result == "5") {
			tube_id = tube_id + "_5";
		}
		$("#" + tube_id).hide("drop", {direction: 'right'}, function() {
			var jthis = $(this);
			var jGroup = jthis.prevAll(".w_group");
			jthis.remove();
			if (jGroup.nextUntil(".w_group").length == 0) {
				jGroup.hide("fade", function() {
					jGroup.remove();
				})
			}
		});
	}

	if (resInfo.material_comment || (device_safety_guide && device_safety_guide.length)) {
		showSidebar(resInfo.material_comment);
	} else {
		$("#comments_sidebar").hide();
	}

	$("#finishbutton").enable();
	remapworking(resInfo.workingPfs)
}

var showBreakOfInfect = function(infectString) {
	var $break_dialog = $('#break_dialog');
	$break_dialog.html(decodeText(infectString));
	if ($break_dialog.html().indexOf("opd_pop") >= 0) {
		$break_dialog.find("span").attr("id", "opd_loader_past");
	}

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
				+ (device_type.safety_guide ? "<img src='/photos/safety_guide/" + device_type.devices_type_id + "'></img>" : "")
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
			if (resInfo.workstauts == -1) {
				showBreakOfInfect(resInfo.infectString);
				return;
			}

			// 建立等待区一览
			var reason = "";
			var waiting_html = "";
			for (var iwaiting = 0; iwaiting < resInfo.waitings.length; iwaiting++) {
				var waiting = resInfo.waitings[iwaiting];
				var block_status = waiting.block_status;
				var tube_id = "w_" + waiting.material_id;
				if (waiting.waitingat == "通箱") {
					tube_id = tube_id + "_5";
				}

				if (reason != waiting.waitingat) {
					reason = waiting.waitingat;
					waiting_html += '<div class="ui-state-default w_group" style="width: 420px; margin-top: 12px; margin-bottom: 8px; padding: 2px;">' + reason + '</div>';
				}
				waiting_html += '<div class="waiting tube" id="' + tube_id + '">' +
									'<div class="tube-liquid' + expeditedColor(waiting.expedited)  + '">' +
										(waiting.sorc_no == null ? "" : waiting.sorc_no + ' | ') + waiting.category_name + ' | ' + waiting.model_name + ' | ' + waiting.serial_no;
				if (reason == "通箱") {
					waiting_html +=	'<div class="material_flags"><div class="package">箱</div></div>';
				} else if (block_status == 3) {
					waiting_html +=	'<div class="material_flags"><div class="rc">备</div></div>';
				}
				waiting_html +=	'</div>';
				if (block_status == 3 || reason == "通箱") {
					waiting_html += '<div class="click_start"><input type="button" value="》开始"></div>';
				}
				waiting_html +=	'</div>';
			}
			var $waiting_html = $(waiting_html);
			
			$waiting_html.find("input:button").button().click(function(){
				var $tube = $(this).parent().parent();
				var material_id = $tube.attr("id").replace("w_","");
				doStart(material_id);
			});
			
			$waiting_html.filter(".w_group").each(function(){
				var $w_group = $(this);
				$w_group.text($w_group.text() + " " + $w_group.nextUntil(".w_group").length + " 台");
			});
			$("#waitings").html($waiting_html);

			// 设备危险归类/安全手册信息
			if (resInfo.position_hcsgs) device_safety_guide = resInfo.position_hcsgs;

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
				}
			}

			if (resInfo.infectString) {
				$("#toInfect").show()
				.find("td:eq(1)").html(resInfo.infectString);
			} else {
				$("#toInfect").hide();
			}
			// 暂停理由
			pauseOptions = resInfo.pauseOptions;
			breakOptions = resInfo.breakOptions;

			if (resInfo.workstauts == 1) {
				treatStart(resInfo);
			} else {
				if (device_safety_guide && device_safety_guide.length) {
					showSidebar(null);
				} else {
					$("#comments_sidebar").hide();
				}
			}
		}
	} catch (e) {
		alert("name: " + e.name + " message: " + e.message + " lineNumber: "
				+ e.lineNumber + " fileName: " + e.fileName);
	};
};

var expeditedColor = function(expedited) {
	if (expedited == -1) return ' tube-gray';
	if (expedited == 1) return ' tube-red';
	return ' tube-green';
}

var doInit=function(){
	// Ajax提交
	$.ajax({
		beforeSend : ajaxRequestType,
		async : false,
		url : servicePath + '?method=jsinitf',
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

	$("#finishbutton").disable();
	$(".manager_no").select2Buttons();

	loadJs(
	"js/data/operator-detail.js",
	function(){
		opd_load($("#workarea table td:contains('暂停时间')"), function(){$(".opd_re_comment").remove()});
	});

	doInit();

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
	$("#finishbutton").click(doFinish);
	$("#reportbutton").click(makeReport);

	if ($("#dm_select_all").length > 0) {
		$("#dm_select_all").find(".manager_no").change(function() {
			var code= $(this).attr("code");
			var val = $(this).val();
			$("#workings .dm_select .manager_no[code="+ code +"]").val(val).trigger("change");
		});
	}

	$("#comments_sidebar .ui-widget-header span").on("click",function(){

		if($("#comments_sidebar").hasClass("shown")){
			$("#comments_sidebar .tip_pages").css("overflow-y", "hidden");
			$("#comments_sidebar img").hide();
			$("#comments_sidebar .comments_area").slideUp(200,function(){
				$("#comments_sidebar .ui-widget-header span").removeClass("icon-enter-2").addClass("icon-share");
				$("#comments_sidebar").animate({width:"30px",opacity:".5"},300);
			});
			$("#comments_sidebar").removeClass("shown");
		}else{

			$("#comments_sidebar").animate({width:"1024px",opacity:"1"},300,function(){
				$("#comments_sidebar .ui-widget-header span").removeClass("icon-share").addClass("icon-enter-2");
				$("#comments_sidebar .comments_area").slideDown(200,function(){
					$("#comments_sidebar img").show();
					$("#comments_sidebar .tip_pages").css("overflow-y", "auto");
				});
			});
			$("#comments_sidebar").addClass("shown");
		}
	});
});

var makeReport = function() {
	// Ajax提交
	$.ajax({
		beforeSend : ajaxRequestType,
		async : false,
		url : servicePath + '?method=makeReport',
		cache : false,
		data : {},
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : function() {
			infoPop("<span>生成报表的指示已发送，请到<a href='daily_work_sheet.do'>工作记录表画面</a>确认！</span>");
		}
	});	
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
			treatStart(resInfo);
		}
	} catch (e) {
		alert("name: " + e.name + " message: " + e.message + " lineNumber: "
				+ e.lineNumber + " fileName: " + e.fileName);
	};
};

var doStart=function(materialId){
	var data = {};
	var processType = 111; // 维修品消毒
	if ($("#g_process_code").val() == "131") {
		processType = 121; // 维修品灭菌 
	}
	if (materialId) {
		data["material_id"] = materialId;

		if (processType == 111) {
			if (materialId.indexOf("_5") >= 0) {
				processType = 113; // 镜箱消毒
			} else {
				processType = 112; // 备品消毒
			}
		} else {
			processType = 122; // 备品灭菌
		}
	}else{
		data["material_id"] = $("#scanner_inputer").val();
	}

	$("#scanner_inputer").attr("value", "");

	afObj.applyProcess(processType, this, doStartPost, [data]);
};

var doStartPost = function(data) {
	// Ajax提交
	$.ajax({
		beforeSend : ajaxRequestType,
		async : false,
		url : servicePath + '?method=doscanf',
		cache : false,
		data : data,
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : doStart_ajaxSuccess
	});
}

var doFinish_ajaxSuccess = function(xhrobj, textStatus, pcs_inputs){
	var resInfo = null;
	try {
		// 以Object形式读取JSON
		eval('resInfo =' + xhrobj.responseText);

		if (resInfo.errors.length > 0) {
			// 共通出错信息框
			treatBackMessages(null, resInfo.errors);
		} else {
			$("#scanner_inputer").attr("value", "");
			$("#position_status").text("准备中");
			$("#position_status").css("color", "#0080FF");
			$("#manualdetailarea").hide();

			if (pcs_inputs) {
				for (var input in pcs_inputs) {
					$("#workings").find("#w_" + input).addClass("finished").hide("drop", {direction: 'right'}, function() {});
				}
				if ($("#workings .tube").length == $("#workings .tube.finished").length) {
					$("#finishbutton").disable();
				}
			} else {
				$("#workings .tube").hide("drop", {direction: 'right'}, function() {});
				$("#finishbutton").disable();
			}

			$("#s_t").text("");

			if (device_safety_guide && device_safety_guide.length) {
				$("#comments_sidebar .comments_area").val("");
				$("#comments_sidebar").hide();
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
	} catch (e) {
		console.log("name: " + e.name + " message: " + e.message + " lineNumber: "
				+ e.lineNumber + " fileName: " + e.fileName);
	};
};

var doFinish=function(){
	var pcs_inputses = {};
	var pcs_rcs = {};
	var pcs_package = {};

	$("#workings > div:not(.finished)").each(function(idx, ele){
		var $wk = $(ele);
		var wk_id = $wk.attr("id").replace("w_", "");
		var pcs_inputs = {};

		var $manager_nos = $wk.find(".manager_no");
		$manager_nos.each(function(idx, ele){
			pcs_inputs[$(ele).attr("code")] = ele.value;
		});

		if ($wk.find(".rc").length > 0) {
			pcs_rcs[wk_id] = pcs_inputs;
		} else if ($wk.find(".package").length > 0) {
			pcs_package[wk_id] = pcs_inputs;
		} else {
			pcs_inputses[wk_id] = pcs_inputs;
		}
	});

	if (!$.isEmptyObject(pcs_inputses) && !$.isEmptyObject(pcs_rcs)) {
		var warningText = null;
		$("#pop_detail").text("请选择要完成的作业对象。");
		$("#pop_detail").dialog({
			dialogClass:'ui-warn-dialog',
			width : 450,
	        resizable:false,
	        modal:true,
	        title:"提示信息",
	        buttons:{
	            "维修品" : function() {
	                $(this).dialog("close");
	                if ($("#g_process_code").val() == "131") {
		                afObj.applyProcess(121, this, doFinish_ajax, [pcs_inputses]);
	                } else {
		                afObj.applyProcess(111, this, doFinish_ajax, [pcs_inputses]);
	                }
	            },
	            "备品" : function() {
	                $(this).dialog("close");
	                if ($("#g_process_code").val() == "131") {
		                afObj.applyProcess(122, this, doFinish_ajax, [pcs_rcs]);
	                } else {
		                afObj.applyProcess(112, this, doFinish_ajax, [pcs_rcs]);
	                }
	            },
	            "通箱" : function() {
	                $(this).dialog("close");
	                afObj.applyProcess(112, this, doFinish_ajax, [pcs_package]);
	            }
	        }
		});
	} else if (!$.isEmptyObject(pcs_inputses)) {
        if ($("#g_process_code").val() == "131") {
            afObj.applyProcess(121, this, doFinish_ajax, [pcs_inputses]);
        } else {
            afObj.applyProcess(111, this, doFinish_ajax, [pcs_inputses]);
        }
	} else if (!$.isEmptyObject(pcs_rcs)) {
        if ($("#g_process_code").val() == "131") {
            afObj.applyProcess(122, this, doFinish_ajax, [pcs_rcs]);
        } else {
            afObj.applyProcess(112, this, doFinish_ajax, [pcs_rcs]);
        }
	} else if (!$.isEmptyObject(pcs_package)) {
		afObj.applyProcess(112, this, doFinish_ajax, [pcs_package]);
	}
};

var doFinish_ajax = function(pcs_inputs) {
	var data = {};
	if ($.isEmptyObject(pcs_inputs)) {
		return false;
	}
	data.pcs_inputs = Json_to_String(pcs_inputs);
	
	// Ajax提交
	$.ajax({
		beforeSend : ajaxRequestType,
		async : false,
		url : servicePath + '?method=dofinishf',
		cache : false,
		data : data,
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : function(xhrobj, textStatus){
			doFinish_ajaxSuccess(xhrobj, textStatus, pcs_inputs);
		}
	});
};

var prevzero =function(i) {
	if (i < 10) {
		return "0" + i; 
	} else {
		return "" + i; 
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
}
