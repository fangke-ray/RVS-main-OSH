// Your turst our mission
var opd_modelname = "工作日报";

var opd_load = function($target, callback) {
	var isTd = ($target.length > 0 && $target[0].tagName == "TD");
	var $setRelax = $("<span id='opd_loader' class='menulink icon-printer' title='设定空余工时' style='position:absolute;top: .1em;right: .1em;'/>" +
			(isTd ? "<div class='opd_re_comment'></div>" : ""))
		.click(opd_pop);
	$target.css({"position" : "relative", "display": "block"})
		.append($setRelax);
	if (!isTd) {$setRelax.text("日报填写");}
	if (callback) callback();
}

var opd_pop = function(evt, operator_id, action_time){
	if (typeof(operator_id) != "string") operator_id = null;
	var this_dialog = $("#od_detail_dialog");
	if (this_dialog.length === 0) {
		$("body.outer").append("<div id='od_detail_dialog'/>");
		this_dialog = $("#od_detail_dialog");
	}
	this_dialog.hide();

	if (evt == null) {
		evt = arguments.callee.caller.arguments[0];
	}

	if (evt && evt.target.id && evt.target.id.indexOf("opd_loader") == 0) {

		this_dialog.load("widgets/operator-detail.jsp", function(
						responseText, textStatus, XMLHttpRequest) {
			this_dialog.dialog({
				position : [400, 20],
				title : "操作人员作业信息",
				width : 800,
				show : "",
				height : 'auto',
				resizable : false,
				modal : true,
				close : function(){
					fixleakpausing = false;
				},
				buttons : {
					"确定":function(){
						doopd_Ok(operator_id, action_time, this_dialog);
					},
					"取消":function(){
						this_dialog.dialog('close');
					}
				}
			});
			this_dialog.show();
			
			initDetailView();
			opd_finddetail(operator_id, action_time)
		});
	} else {
		opd_listdata = null;
		var set_comments = "";
		var text_comments = evt.target.innerText;
		if (text_comments) {
			set_comments = text_comments.substring(text_comments.indexOf(":") + 1);
			var secondCommentIndex = set_comments.indexOf(":") + 1;
			if (secondCommentIndex) {
				set_comments = set_comments.substring(secondCommentIndex);
			} else {
				set_comments = "";
			}
		}
		
		var set_object = {
			pause_start_time : evt.target.getAttribute("pause_start_time"),
			reason : evt.target.getAttribute("reason"),
			comments : set_comments
		};
		fixleakpausing = false;
		fixleakpause(evt, set_object);		
	}
}

var opd_finddetail = function(operator_id, action_time) {
	opd_postData = {
		"operator_id": operator_id,
		"action_time" : action_time
	};
	$.ajax({
		beforeSend : ajaxRequestType,
		async : true,
		data : opd_postData,
		cache : false,
		url : "operatorProduction.do?method=getDetail",
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : function(xhrobj, textStatus) {
			opd_search_handleComplete(xhrobj, textStatus);
		}
	});
};

/** 一览数据对象 */
var opd_listdata = {};
var opd_postData = {};
var main_process_code;

var leakinput = function() {
	var $commentcells = $("td[aria\\-describedby='operator_detail_list_leak'][title='true']").prev("td");
	$commentcells.addClass("od-leak_cell");

	$commentcells.click(fixleakpause);

	var $last_finish_time = $("td[aria\\-describedby='operator_detail_list_pause_finish_time'][title='']").last();
	if ($last_finish_time.siblings("td[aria\\-describedby='operator_detail_list_sorc_no']").text().length <= 1) {
		$last_finish_time.addClass("od-leak_cell");
		$last_finish_time.click(finishToday);
	}
}

var finishToday = function(){
	var $td = $(this);
	warningConfirm("您是否要结束当日的工作？", function(){
		var rowData = $("#operator_detail_list").getRowData();
		var last = rowData[rowData.length-1];

		for (var i in rowData) {
			if (rowData[i].leak === 'true') {
				if (!rowData[i].reason) {
					treatBackMessages("#searcharea", [{errmsg:"如果完成当日日常点检，请补充全部的暂停理由。当前第"+(parseInt(i)+1)+"行未补充。"}]);
					return;
				}
			}
		}

		$td.text("下班").removeClass("od-leak_cell")
			.next().text(last.pause_start_time_hidden.substring(0, 10) + " 23:59:59");
	})
}

var countmins = function(datetime) {
	var hour = datetime.substring(11,13);
	var minute = datetime.substring(14,16);
	return hour * 60 + parseInt(minute, 10);
}

var slideTime = function(datetime) {
	var timevalue = $("#splitter").slider( "option", "value" );
	var minutes0 = parseInt(timevalue % 60, 10),
    	hours0 = parseInt(timevalue / 60, 10);
    if (minutes0 < 10) minutes0 = "0" + minutes0;

	var minm = $("#splitter").slider( "option", "min");
	var maxm = $("#splitter").slider( "option", "max");

    $("#selectHM").text(hours0 + ":" + minutes0);
    $("#spareHM").text("距开始时间" + (timevalue - minm) + "分，距结束时间" + (maxm - timevalue) + "分");
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

var fixleakpausing = false;
var fixleakpause = function(evt, set_object) {
	if (fixleakpausing) return;
	fixleakpausing = true;

	var cell = null, line_no = null;

	if (set_object == null && opd_listdata != null) {
		cell = $(evt.target);
		line_no = cell.parent().find("td:first").text() - 1;
		set_object = opd_listdata[line_no];
	}

	var this_dialog = $("#operator_detail_dialog");
	if (this_dialog.length == 0) {
		$("body.outer").append("<div id='operator_detail_dialog'/>");
		this_dialog = $("#operator_detail_dialog");
	}
	this_dialog.hide();
	this_dialog.load("widget.do?method=pauseWorkreport&s="+new Date().getMilliseconds(), function() {
		$("#pause-workreport-reason").show();
		$("#pause-workreport-split").hide();

		var pause_comments = $.parseJSON($("#pause_comments").val());

		$("#pause_reason").select2Buttons().val(set_object.reason)
			.change(function(){
				var thisval = this.value;
				if (pause_comments[thisval]) {
					$("#edit_comments_select")
					.next(".select2Buttons").remove()
					.end()
					.show().html(createCommentOptions(pause_comments[thisval]))
					.val(set_object.comments)
					.select2Buttons();
					$("#edit_comments").hide();
				} else {
					$("#edit_comments_select").hide()
					.next(".select2Buttons").remove()
					.end()
					.html("");
					$("#edit_comments").show().val(
						(pause_comments[set_object.reason] ? "" : set_object.comments)
					);
				}
			}).trigger("change");

		if ($("#splitter").slider) {
			$("#splitter").slider({slide: slideTime})
				.next().find("input.ui-button").button()
				.click(function(){this_dialog.dialog('close')});
		} else {
			loadJs(
			"js/jquery.slider.js",
			function() {
				$("#splitter").slider({slide: slideTime})
					.next().find("input.ui-button").button()
					.click(function(){this_dialog.dialog('close')});
			}
			);
		}

		popInput(this_dialog, opd_listdata, line_no, cell, set_object);

		if ($("div#operator_detail_dialog").length > 1) {
			$("div#operator_detail_dialog:eq(0)").remove();// TODO BUG??
		}
	});
};

var popInput = function(this_dialog, opd_listdata, line_no, cell, set_object){
	var fromList = opd_listdata != null && opd_listdata.length > 0;

	var pause_start_time = null;
	if (set_object) pause_start_time = set_object.pause_start_time;
	var pause_finish_time = null;
	if (fromList) {
		pause_finish_time = set_object.pause_finish_time;
	}
	var title = pause_start_time + ' ～' + (pause_finish_time || "");
	if (pause_finish_time == null) {
		var now = new Date();
		pause_finish_time= pause_start_time.substring(0, 10) + 
			" " + fillZero(now.getHours()) + ":" + fillZero(now.getMinutes()) + ":" + fillZero(now.getSeconds()); 
	}

	var buttonActions = {
		"分割时段": function(){
			this_dialog.dialog("option", "height", 200);
			this_dialog.next(".ui-dialog-buttonpane").find("button:eq(0)").hide();

			$("#splitter").slider( "option", "min", countmins(pause_start_time) );
			$("#splitter").slider( "option", "max", countmins(pause_finish_time) );
			$("#splitter").slider( "option", "value", countmins(pause_start_time) + 1 );

			$("#pause-workreport-reason").hide();
			$("#pause-workreport-split").show();
			// this_dialog.dialog('close');
		},
		"确定": function(){
			if (this_dialog.next(".ui-dialog-buttonpane").find("button:eq(0)").is(":hidden")) {
				// 分割时段
				if ($("#selectHM").text() != "") {

					var finish_time = pause_finish_time;
					var spare_time = finish_time.substring(0, 11) + fillZero($("#selectHM").text(), 5) + ":00";

					if (fromList) {
						opd_listdata[line_no].pause_finish_time = spare_time;

						var neodata = {};
						neodata.pause_start_time = spare_time;
						if (line_no < opd_listdata.length - 1) {
							neodata.pause_finish_time = finish_time;
						}
						neodata.leak = true;

						opd_listdata.splice(line_no + 1, 0, neodata);

						opd_loadData();
					}
				}
			} else {
				editPauseFeature(fromList, cell, pause_start_time, pause_finish_time);
			}

			this_dialog.dialog('close');
		}
	};

	if (!fromList) {
		delete buttonActions["分割时段"];
		if (set_object.reason) {
			buttonActions["时段结束"] = function() {
				editPauseFeature(fromList, null, pause_start_time, pause_finish_time, "时段结束");
				this_dialog.dialog('close');
			}
			buttonActions["下班"] = function() {
				warningConfirm("您是否要结束当日的工作？", function(){
					editPauseFeature(fromList, null, pause_start_time, pause_start_time.substring(0, 10) + " 23:59:59", "下班");
					this_dialog.dialog('close');
				});
			}
		}
	}
	
	buttonActions["取消"] = function(){
		this_dialog.dialog('close');
	}

	this_dialog.dialog({
		position : [220, 40],
		title : title,
		width : 800,
		show : "",
		height : 'auto',
		resizable : false,
		modal : true,
		close : function(){
			fixleakpausing = false;
		},
		buttons : buttonActions
	});
}

function editPauseFeature(fromList, cell, pause_start_time, pause_finish_time, forLeak) {
	var reasonText = $("#pause_reason").find("option:selected").text();
	var reason = $("#pause_reason").val();
	var comments = null;

	if ($("#edit_comments").is(":hidden")) {
		comments = $("#edit_comments_select").val();
	} else {
		comments = $("#edit_comments").val();
	}

	if (reason === '100' && !comments) {
		treatBackMessages("#searcharea", [{errmsg:"请填写备注!"}]);
		return;
	}
	if (comments && reason == '') {
		reason = '100';
	}

	if (fromList) {
		// 更新一览
		cell.text(reasonText +" " +comments);
		cell.prev("td").text(comments).prev("td").text(reason);
	} else {
		var full_comments = reasonText + (comments ? ":" + comments : "");
		var postData = {
			"operator_id" : null,
			"reason" : reason,
			"comments" : comments,
			"action_time" : pause_finish_time.substring(0, 10), // 当前日期
			"pause_start_time" : pause_start_time,
			"pause_finish_time" : (("下班" == forLeak) ? pause_finish_time : null),
			full_comments : full_comments,
			leak : forLeak
		}
		// 填写当前进行项
		saveLeakData(postData);
	}
}

function opd_initGrid($grid) {
	$grid.jqGrid({
		toppager : true,
		data : [],
		height : 461,
		width : 768,
		rowheight : 23,
		datatype : "local",
		colNames : ['开始时间', '','结束时间','', '处理对象', '机型', '工位', '原因','备注', '特记事项','leak'],//注意不要随意改变表头顺序，特别是后四位
		colModel : [{
					name : 'pause_start_time',
					sortable : false,
					width : 85,
					align : 'center',
					formatter:'date', 
					formatoptions:{srcformat:'Y/m/d H:i:s',newformat:'H:i'}
				}, {
					name: 'pause_start_time_hidden',
					hidden: true,
					formatter:function(a,b,c){return c.pause_start_time}
				},{
					name : 'pause_finish_time',
					sortable : false,
					width : 85,
					align : 'center',
					formatter:'date', 
					formatoptions:{srcformat:'Y/m/d H:i:s',newformat:'H:i'}
				}, {
					name: 'pause_finish_time_hidden',
					hidden: true,
					formatter:function(a,b,c){return c.pause_finish_time}
				}, {
					name : 'sorc_no',
					sortable : false,
					width : 120
				}, {
					name : 'model_name',
					sortable : false,
					width : 120
				}, {
					name : 'process_code',
					sortable : false,
					width : 80
				}, {
					name: 'reason',
					hidden: true
				}, {
					name: 'comments',
					hidden: true
				}, {
					name: 'leak_comment',
					width : 160,
					formatter:function(a,b,c) {
						var rt = "";
						if (c.reasonText) {
							rt += c.reasonText+" ";
						}
						if(c.comments) {
							rt += c.comments;
						}
						
						if (!rt && c.process_code && c.operate_result) {
							if (c.operate_result == "5") {
								return "辅助工作"
//							} else if (c.operate_result == "2" && c.pace && c.pace == "0") {
//								return "全代工";
//							} else if (c.operate_result == "2" && c.pace && c.pace> 0) {
//								return "半代工";
							} else if (c.pace == "1") {
								return "代工";
							}
						}
						return rt;
					}
				}, {
					name : 'leak',
					hidden : true
				}],
		rowNum : 100,
		rownumbers : true,
		toppager : false,
		pager : "#operator_detail_listpager",
		viewrecords : true,
		caption : opd_modelname + "一览",
		hidegrid : false, // 启用或者禁用控制表格显示、隐藏的按钮
		gridview : true, // Speed up
		pagerpos : 'right',
		pgbuttons : true,
		// forceFit : true,// 调整列宽度不会改变表格的宽度。
		pginput : false,
		recordpos : 'left',
		gridComplete : leakinput
	});
}
/*
 * Ajax通信成功的处理
 */
function opd_search_handleComplete(xhrobj, textStatus) {

	var resInfo = null;
	try {
		// 以Object形式读取JSON
		eval('resInfo =' + xhrobj.responseText);
		setLabel(resInfo.detail);
		opd_listdata = resInfo.list;
		opd_loadData();

		$("#can_edit_overtime").val(resInfo.editable);

		setOvertime(resInfo.overtime, resInfo.editable);
		if (resInfo.isAdmin === true) {
			$("#reportbutton").show();
		} else {
			$("#reportbutton").hide();
		}
	} catch (e) {
		alert("name: " + e.name + " message: " + e.message
				+ " lineNumber: " + e.lineNumber + " fileName: "
				+ e.fileName);
	}
};

function setOvertime(overtime, editable) {
	if (overtime) {
		if (overtime.pause_start_time) {
			var start = new Date(overtime.pause_start_time);
			var data = start.getHours()+":"+((start.getMinutes().toString().length==1) ? ("0"+start.getMinutes()):start.getMinutes());
			$("#edit_overtime_start").val(data);
			$("#label_overtime_start").text(data);
		}
		if (overtime.pause_finish_time) {
			var end = new Date(overtime.pause_finish_time);
			var data = end.getHours()+":"+((end.getMinutes().toString().length==1) ? ("0"+end.getMinutes()):end.getMinutes());
			$("#edit_overtime_end").val(data);
			$("#label_overtime_end").text(data);
		}
		$("#edit_overtime_reason").val(overtime.overwork_reason);
		$("#edit_overtime_comment").val(overtime.comments);
		$("#label_overtime_reason").text(overtime.overwork_reason_name);
		$("#label_overtime_comment").text(overtime.comments);

		if (editable === true) {
			$("#label_overtime_start").hide();
			$("#label_overtime_end").hide();
			$("#label_overtime_reason").hide();
			$("#label_overtime_comment").hide();
			$("#edit_overtime_reason").select2Buttons();
		} else {
			$("#edit_overtime_start").hide();
			$("#edit_overtime_end").hide();
			$("#edit_overtime_reason").hide();
			$("#edit_overtime_comment").hide();
		}
	}
}
function setLabel(data) {
	$("#label_action_time").text(data.action_time);
	$("#label_line_name").text(data.line_name);
	$("#label_process_code").text(data.process_code);
	$("#label_operator_name").text(data.name);
	main_process_code = data.process_code;
}
function opd_loadData() {
	$("#operator_detail_list").jqGrid().clearGridData();
	$("#operator_detail_list").jqGrid('setGridParam', {data : opd_listdata})
		.trigger("reloadGrid", [{current : false}]);
}

function exportReport() {
	var rowData = $("#operator_detail_list").getRowData();
	for (var i in rowData) {
		if (rowData[i].leak === 'true') {
			if (!rowData[i].reason) {
				treatBackMessages("#searcharea", [{errmsg:"工作日报信息不完整,请确认信息完整后再进行导出操作!"}]);
				return;
			}
		}
	}
	// Ajax提交
	$.ajax({
		beforeSend: ajaxRequestType, 
		async: true, 
		url: 'operatorProduction.do?method=report', 
		cache: false, 
		data: opd_postData, 
		type: "post", 
		dataType: "json", 
		success: ajaxSuccessCheck, 
		error: ajaxError, 
		complete: function(xhrobj, textStatus){
			var resInfo = null;

			// 以Object形式读取JSON
			eval('resInfo =' + xhrobj.responseText);
		
			if (resInfo.errors.length > 0) {
				// 共通出错信息框
				treatBackMessages("#searcharea", resInfo.errors);
			} else {
				var iframe = document.createElement("iframe");
	            iframe.src = "operatorProduction.do?method=export&addition="+
	            	"-"+$("#label_action_time").text().replace("-","")
	            	+ "&filePath=" + resInfo.filePath;
	            iframe.style.display = "none";
	            document.body.appendChild(iframe);
			}
						
		}
	});
}

var initDetailView = function() {
	$("input.ui-button").button();
	$("#reportbutton").click(exportReport);
	var $grid = $("#operator_detail_list");
	if ($grid.jqGrid) {
		opd_initGrid($grid);
	} else {
		loadCss(
		"css/ui.jqgrid.css",
		function() {
		loadJs(
		"js/jquery.jqGrid.min.js",
		function() {
			loadJs(
			"js/i18n/grid.locale-cn.js",
			function() {
				opd_initGrid($grid);
			});
		});
		});
	}
}

function doopd_Ok(operator_id, action_time, this_dialog) {
	var rowData = $("#operator_detail_list").getRowData();
//	var last = rowData[rowData.length-1];
	
//	if (last.leak === 'true' && last.reason != "") { //最后一条记录是虚拟记录 并且填写了理由
//		
//		if(confirm("您是否要结束当日的工作?")){
//			for (var i in rowData) {
//				if (rowData[i].leak === 'true') {
//					if (!rowData[i].reason) {
//						treatBackMessages("#searcharea", [{errmsg:"如果完成当日日常点检，请补充全部的暂停理由。当前第"+(i+1)+"行未补充。"}]);
//						return;
//					}
//				}
//			}
//		} else {
//			return ;
//		}
//	}
	
	var edit = $("#can_edit_overtime").val();

	if (edit === 'true') {
		saveOverwork(operator_id, action_time, function(){
			for (var i in rowData) {
				if (rowData[i].leak === 'true' && (rowData[i].reason || rowData[i].comments)) {
					var postData = {
						"operator_id" : operator_id,
						"reason" : rowData[i]["reason"],
						"comments" : rowData[i]["comments"],
						"action_time" : action_time,
						"pause_start_time" : rowData[i]["pause_start_time_hidden"],
						"pause_finish_time" : rowData[i]["pause_finish_time_hidden"]
					}

					saveLeakData(postData);
				}
			}
			// 排除限制
			if (!operator_id && action_time) {
				window.top.location.reload();
			}
			this_dialog.dialog('close');
		});
	} else {
		this_dialog.dialog('close');
	}
}

function saveOverwork(operator_id, action_time, callback) {
	if (!action_time) {
		action_time = "";
	}
	var over_start = $("#edit_overtime_start").val();
	var over_end = $("#edit_overtime_end").val();
	var start = over_start ? action_time+" "+over_start+":00" : "";
	var end = over_end ? action_time+" "+over_end+':00' : "";

	var data = {
		"operator_id" : operator_id,
		"action_time" : action_time,
		"pause_start_time": start,
		"pause_finish_time": end,
		"overwork_reason" : $("#edit_overtime_reason").val(),
		"comments" : $("#edit_overtime_comment").val()
	}
	
	$.ajax({
		beforeSend : ajaxRequestType,
		async : false,
		data : data,
		cache : false,
		url : "operatorProduction.do?method=dosaveoverwork",
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : function(xhrobj, textStatus) {
			var resInfo = null;
			// 以Object形式读取JSON
			eval('resInfo =' + xhrobj.responseText);
			if (resInfo.errors.length > 0) {
				// 共通出错信息框
				treatBackMessages("#searcharea", resInfo.errors);
			} else {
				callback();
			}
		}
	});
}

function saveLeakData(data) {

	$.ajax({
		beforeSend : ajaxRequestType,
		async : false,
		data : data,
		cache : false,
		url : "operatorProduction.do?method=dosavepause",
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : function(xhrObj, textStatus) {
			var resInfo = $.parseJSON(xhrObj.responseText);
			if (resInfo.errors && resInfo.errors.length > 0) {
				treatBackMessages("#searcharea", resInfo.errors);
			} else {
				if (!data.pause_finish_time
					&& typeof(saveLeakDataCallback) === "function") {
					saveLeakDataCallback(data);
				}
				fixleakpausing = false;
			}
		}
	});
}

/*function deletePause(rowData, operator_id) {
	var data = {
		"operator_id" : operator_id,
		"pause_start_time" : rowData["pause_start_time_hidden"]
	}
	$.ajax({
		beforeSend : ajaxRequestType,
		async : true,
		data : data,
		cache : false,
		url : "operatorProduction.do?method=dodeletepause",
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : function(xhrobj, textStatus) {
			
		}
	});
}*/

$(function() {

});