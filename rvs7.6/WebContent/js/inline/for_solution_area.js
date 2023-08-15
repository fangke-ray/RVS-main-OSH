var servicePath = "forSolutionArea.do";

var keepSearchData = {};

var search_handleComplete = function(xhrObj){
	var resInfo = $.parseJSON(xhrObj.responseText);
	if (resInfo.errors && resInfo.errors.length > 0) {
		treatBackMessages("#searcharea", resInfo.errors);
		return;
	}

	var cur_page = $("#list").jqGrid().getGridParam("page");
	$("#list").jqGrid().clearGridData();
	$("#list").jqGrid('setGridParam', {data: resInfo.material_list}).trigger("reloadGrid", [{current: true, page : cur_page}]);
}

var setData = function(){
	keepSearchData = {
		"sorc_no" : $("#search_sorc_no").val(),
		"category_id":$("#search_category_id").val(),
		"serial_no" : $("#search_serial_no").val(),
		"scheduled_date_start" : $("#search_scheduled_date_start").val(),
		"scheduled_date_end" : $("#search_scheduled_date_end").val(),
		"section_id" : $("#search_section").val(),
		"expedition_diff": $("#search_expedition_diff input:checked").val(),
		"scheduled_expedited" : $("#scheduled_expedited_set").children("input[checked]").val(),
		"bo_flg": $("#search_bo_flg").children("input[checked]").val(),
		"arrival_plan_date_start" : $("#search_arrival_plan_date_start").val(),
		"arrival_plan_date_end" : $("#search_arrival_plan_date_end").val(),
		"resolved" : $("#search_resolved_set").children("input[checked]").val(),
		"reason" : $("#search_reason").val(),
		"happen_time" : $("#search_happen_time").val()
	};
}

var findit = function() {
	// Ajax提交
	$.ajax({
		beforeSend: ajaxRequestType, 
		async: true, 
		url: servicePath + '?method=search', 
		cache: false, 
		data: keepSearchData, 
		type: "post", 
		dataType: "json", 
		success: ajaxSuccessCheck, 
		error: ajaxError, 
		complete: search_handleComplete
	});
};

var updateSchedule = function(lineId, scheduled_assign_date) {
	var selectedIds  = $("#list").jqGrid("getGridParam","selarrrow"); 
	var ids = [];
	// 读取排入行
	for (var i = 0; i < selectedIds.length; i++) {
		var rowData = $("#list").getRowData(selectedIds[i]);
		ids[ids.length] = rowData.material_id;
	}
	var data = keepSearchData;
	data.ids = ids.join(",");
	data.lineIds = lineId;
	data.scheduled_assign_date = $("#pick_date").val();
	data.line_id = $("#select_line").val();
	data.no_list = true;

	if ($("#unknown_comment").length > 0) {
		data.unknown_comment = $("#unknown_comment").val();
		$("#unknown_comment").val("");
	}
	if (scheduled_assign_date) {
		data.scheduled_assign_date = scheduled_assign_date;
	}

	// Ajax提交
	$.ajax({
		beforeSend: ajaxRequestType, 
		async: false, 
		url: 'schedule.do?method=doupdateSchedule', 
		cache: false, 
		data: data, 
		type: "post", 
		dataType: "json", 
		success: ajaxSuccessCheck, 
		error: ajaxError, 
		complete: planned_list_handleComplete
	});
};

var updateComPlannedScheduleForUnknown = function() {
	$confirm = $("#confirm_message");
	$confirm.html("<form><textarea name='unknown_comment' id='unknown_comment' title='未定理由' style='width: 260px;height: 112px;resize:none;'></textarea></form>");
	$confirm.children("form").validate({
		rules : {
			unknown_comment : {
				required : true
			}
		}
	});
	$confirm.dialog({
		resizable: false,
		modal: true,
		title: "输入计划日期未定理由",
		buttons: {
			"确认": function() {
				if ($confirm.children("form").valid()) {
					updateSchedule(14, '9999/12/31');
					$("#title_planned").text("未定待另行安排一览");
					$confirm.dialog("close");
				}
			},
			"取消": function() {
				$confirm.dialog("close");
			}
		}
	})
};

$(function() {

	$("input.ui-button").button();
	$(".ui-buttonset").buttonset();
	$("a.areacloser").hover(
		function (){$(this).addClass("ui-state-hover");}, 
		function (){$(this).removeClass("ui-state-hover");}
	);
	$("#searcharea span.ui-icon").bind("click", function() {
		$(this).toggleClass('ui-icon-circle-triangle-n').toggleClass('ui-icon-circle-triangle-s');
		if ($(this).hasClass('ui-icon-circle-triangle-n')) {
			$(this).parent().parent().next().show("blind");
		} else {
			$(this).parent().parent().next().hide("blind");
		}
	});

	$("#searchbutton").addClass("ui-button-primary");
	$("#searchbutton").click(function (){
		setData();
		findit();
	});

	$("#resetbutton").click(function (){
		$("#search_sorc_no").val("");
		$("#search_category_id").val("").trigger("change");
		$("#search_serial_no").val("");
		$("#search_scheduled_date_start").val("");
		$("#search_scheduled_date_end").val("");
		$("#search_section").val("").trigger("change");
		$("#search_line").val("").trigger("change");
		$("#scheduled_expedited_set").children("input:eq(0)").attr("checked", true).trigger("change");
		$("#search_bo_flg").children("input:eq(0)").attr("checked", true).trigger("change");
		// 
		$("#search_arrival_plan_date_start").val("");
		$("#search_arrival_plan_date_end").val("");
		$("#search_resolved_set").children("input:eq(0)").attr("checked", true).trigger("change");
		$("#search_reason").val("").trigger("change");
		$("#search_happen_time").val("");

		$("#search_expedition_diff").val("");
		$("#search_expedition_diff_a").attr("checked","checked").trigger("change");

	});

	$("#plandecbutton, #planallbutton, #plannsbutton, #plancombutton").click(function(){
		var lineId = $(this).val();
		$("#select_line").val(lineId)
		findSchedule();
	});

	$("#removefromplanbutton").disable().click(deleteSchedule);

	$("#complannedbutton").click(function(){updateSchedule(14)});
	$("#complanned-a-button").click(updateComPlannedScheduleForUnknown);

	$("#search_scheduled_date_start, #search_scheduled_date_end, #search_arrival_plan_date_start, #search_arrival_plan_date_end, " +
			"#search_happen_time").datepicker({
		showButtonPanel:true,
		currentText: "今天"
	});

	$("#pick_date").datepicker({
		showButtonPanel:true,
		currentText: "今天",
		minDate:0,
		maxDate:'+6m'
	});
	$("#pick_date").change(function(){
		changeDate();
	});

	$("#planned_listarea span.ui-icon").bind("click", function() {
		var now = new Date();
		var day = now.getDate();
		var maxDate = new Date();//7天后
		maxDate.setDate(day+31);
		var minDate = new Date();//明天
		minDate.setDate(day); // day+1
		
		if ($(this).hasClass('ui-icon-circle-triangle-w')) { //减
			var date = $("#pick_date").val();
			if (date) {
				var dd = new Date(date);
				if (dd > minDate) {
					dd.setDate(dd.getDate()-1);
					$("#pick_date").val(dd.getFullYear()+"/"+(dd.getMonth()+1)+"/"+dd.getDate());
				}
			} else {
				$("#pick_date").val(minDate.getFullYear()+"/"+(minDate.getMonth()+1)+"/"+minDate.getDate());
			}
			changeDate();
		} else if ($(this).hasClass('ui-icon-circle-triangle-e')){ //加
			var date = $("#pick_date").val();
			if (date) {
				var dd = new Date(date);
				if (dd < maxDate) {
					dd.setDate(dd.getDate()+1);
					$("#pick_date").val(dd.getFullYear()+"/"+(dd.getMonth()+1)+"/"+dd.getDate());
				}
			} else {
				$("#pick_date").val(minDate.getFullYear()+"/"+(minDate.getMonth()+1)+"/"+minDate.getDate());
			}
			changeDate();
		}
	});

	$("#body-1 select").select2Buttons();

	$("#show_process").click(function(){
		if (this.checked) {
			$("#list").jqGrid('showCol', ['direct_flg', 'order_date', 'arrival_plan_date', 'bo_flg', 'break_message', 'scheduled_manager_comment']);
		} else {
			$("#list").jqGrid('hideCol', ['direct_flg', 'order_date', 'arrival_plan_date', 'bo_flg', 'break_message', 'scheduled_manager_comment']);
		}

		$("#list").jqGrid('setGridWidth', '1248');
	});

	$("#closebutton").click(solve);
	$("#pausebutton").click(break_plan);

	$("#downbutton").click(download);
	$("#lines input[name=lines]").click(function(){
		keepSearchData.line_id = this.value;
		findit();
	})

	initGrid();
})

var break_plan = function() {

	var rowid = $("#list").jqGrid("getGridParam", "selrow");
	var rowData = $("#list").getRowData(rowid);
	if (rowData.solved_time.length > 1) {
		errorPop("已处理中断品不能进行未修理返还操作。可以在待处理时使用未修理返还作为处理手段。");
		return;
	}

	var data = {
		id : rowData.material_id
	}

	var warningText = "未修理返还后，维修对象["+encodeText(rowData.sorc_no)+"]将会退出RVS系统中的显示，直接出货，确认操作吗？";
	if (!rowData.inline_time || !rowData.inline_time.trim()) {
		warningText = "未修理返还后，维修对象["+encodeText(rowData.sorc_no)+"]将会退出RVS系统中的显示，等待画像检查后出货，确认操作吗？";
	}
	if (rowData.levelName && rowData.levelName.charAt(0) === "E") {
		warningText = "未修理返还后，维修对象["+encodeText(rowData.sorc_no)+"]将会退出RVS系统中的显示，等待不修理处理后出货，确认操作吗？";
	}

	warningConfirm(warningText, 
	function() {
		$.ajax({
			beforeSend : ajaxRequestType,
			async : false,
			url : 'schedule.do?method=doStop',
			cache : false,
			data : data,
			type : "post",
			dataType : "json",
			success : ajaxSuccessCheck,
			error : ajaxError,
			complete : function() {
				findit();
			}
		});	
	}, null, "返还操作确认");
};

var enablebuttons = function() {
	var rowids = $("#list").jqGrid("getGridParam", "selarrrow");
	var rowid = $("#list").jqGrid("getGridParam", "selrow");
	if (rowid) {
		if (rowids.length > 1) {
			$("#closebutton").disable();
			$("#complanned-a-button").disable();
			$("#pausebutton").disable();
		} else {
			var rowdata = $("#list").jqGrid('getRowData', rowid);
			if (rowdata.solved_time.trim()) {
				$("#closebutton").disable();
			} else {
				$("#closebutton").enable();
			}
	
			if("未定" == rowdata.scheduled_assign_date) {
				$("#complanned-a-button").disable();
			} else {
				$("#complanned-a-button").enable();
			}
			if(rowdata.break_back_flg > 0) {
				$("#pausebutton").disable();
			} else {
				$("#pausebutton").enable();
			}
		}
		$("#complannedbutton").enable();

	} else {
		$("#closebutton").disable();
		$("#complannedbutton").disable();
		$("#complanned-a-button").disable();
		$("#pausebutton").disable();
	}
}
/** 根据条件使按钮有效/无效化 */
var enablebuttons2 = function() {
	var rowids = $("#planned_list").jqGrid("getGridParam", "selarrrow");
	if (rowids.length === 0) {
		$("#removefromplanbutton").disable();
	} else {
		$("#removefromplanbutton").enable();
	}
};
var solve = function() {
	var rowid = $("#list").jqGrid("getGridParam", "selrow");
	if (rowid) {
		var rowdata = $("#list").jqGrid('getRowData', rowid);
		var postData = { 
			for_solution_area_key : rowdata.for_solution_area_key,
			material_id : rowdata.material_id};

		// Ajax提交
		$.ajax({
			beforeSend: ajaxRequestType, 
			async: true, 
			url: servicePath + '?method=doSolve', 
			cache: false, 
			data: postData, 
			type: "post", 
			dataType: "json", 
			success: ajaxSuccessCheck, 
			error: ajaxError, 
			complete: function(xhrObj) {
			 	var resInfo = $.parseJSON(xhrObj.responseText);
				if (resInfo.errors && resInfo.errors.length > 0) {
					treatBackMessages(null, resInfo.errors);
					return;
				}
				var $confirm_message = $("#confirm_message");
				// $confirm_message.dialog('close');
                $confirm_message.text("维修对象问题已解决，回到流水线，请确认。");
                $confirm_message.dialog({
                    resizable : false,
                    modal : true,
                    title : "处理确认",
                    buttons : {
                        "确认" : function() {
                            $confirm_message.dialog("close");
                        }
                    }
                });
                findit();
			}
		});
	}
}

var showremain = function() {

	// 标记
	var pill =$("#list");

	// 得到显示到界面的id集合
	var mya = pill.getDataIDs();
	// 当前显示多少条
	var length = mya.length;

	for (var i = 0; i < length; i++) {
		var rowdata = pill.jqGrid('getRowData', mya[i]);

		// break_message
		var break_message = rowdata["break_level"];
		if (break_message.trim() != "") {
			pill.find("tr#" + mya[i] + " td").css("color", "red");
		}

		var remain_days = rowdata["remain_days"];
		var remainColor = null;
		var remainFColor = null;
		if (remain_days) {
			if (remain_days < -7) {
				remainColor = "#E43838";
				remainFColor = "white";
			} else if (remain_days < -3) {
				remainColor = "#E48E38";
				remainFColor = "white";
			} else if (remain_days < 0) {
				remainColor = "#F9E29F";
				remainFColor = "#333333";
			} else if (remain_days < 2) {
				remainColor = "#E4F438";
				remainFColor = "#333333";
			}
		}
		if (remainColor) {
			pill.find("tr#" + mya[i] + " td[aria\\-describedby='list_sorc_no']").css("background-color", remainColor).css("color", remainFColor);
		}
	}
}

var checkout_complete = function(){
	enablebuttons2();
	$("#planned_list tr:has(td[aria\\-describedby='planned_list_processing_position'][title='已完成']) td").css("background-color", "#50FF64");

		// 标记
	var pill =$("#planned_list");

	// 得到显示到界面的id集合
	var mya = pill.getDataIDs();
	// 当前显示多少条
	var length = mya.length;

	for (var i = 0; i < length; i++) {
		var rowdata = pill.jqGrid('getRowData', mya[i]);

		var remain_days = rowdata["remain_days"];
		var remainColor = null;
		var remainFColor = null;
		if (remain_days) {
			if (remain_days < -7) {
				remainColor = "#E43838";
				remainFColor = "white";
			} else if (remain_days < -3) {
				remainColor = "#E48E38";
				remainFColor = "white";
			} else if (remain_days < 0) {
				remainColor = "#F9E29F";
				remainFColor = "#333333";
			} else if (remain_days < 2) {
				remainColor = "#E4F438";
				remainFColor = "#333333";
			}
		}
		if (remainColor) {
			pill.find("tr#" + mya[i] + " td[aria\\-describedby='planned_list_sorc_no']").css("background-color", remainColor).css("color", remainFColor);
		}
	}
}

var initGrid = function(){
	$("#list").jqGrid({
		data: [], 
		height: 461, 
		width: 1248, 
		rowheight: 23, 
		datatype: "local", 
		colNames: ['keu','维修对象ID','修理单号','机种', '型号 ID', '型号', '机身号',
		'发生时间', '发生<br>原因', '待解决情况', '解决时间', '解决者',
		'维修<br>等级', '直送','投线<br>日期','零件订<br>购安排','入库<br>预定日',
		'零件<br>BO', '维修课','进展<br>工位','纳期','总组出<br>货安排','加急','remain_days','中断信息','备注','break_back_flg','break_level'],
		colModel: [

			{name:'for_solution_area_key', index:'for_solution_area_key',hidden:true, key: true},
			{name:'material_id', index:'material_id',hidden:true},
			{name:'sorc_no',index:'sorc_no', width:70},
			{name:'category_name',index:'category_name', width:60},	
			{name:'model_id',index:'model_id',hidden:true},				
			{name:'model_name',index:'model_name', width:115},
			{name:'serial_no',index:'serial_no', width:40},

			{name:'happen_time',index:'happen_time', width:50, align:'center', formatter:'date', formatoptions:{srcformat:'Y/m/d H:i:s',newformat:'m-d H:i'}},
			{name:'reason',index:'reason', width:40, align:'center', formatter:'select', editoptions: {value:$("#h_or_eo").val()}},
			{name:'comment',index:'comment', width:115},
			{name:'solved_time',index:'solved_time', width:50, align:'center', formatter:'date', formatoptions:{srcformat:'Y/m/d H:i:s',newformat:'m-d H:i'}},
			{name:'resolver_name',index:'resolver_name', width:40},

			{name:'level',index:'level', width:25, align:'center', formatter:'select', editoptions: {value:$("#h_level_eo").val()}},
			{name:'direct_flg',index:'direct_flg', align:'center', width:30, formatter:'select', editoptions:{value:"1:直送;0:"},hidden:true},
			{name:'inline_time',index:'inline_time', width:35, align:'center', formatter:'date', formatoptions:{srcformat:'Y/m/d H:i:s',newformat:'m-d'},hidden:true},
			{name:'order_date',index:'order_date', width:35, align:'center', formatter:'date', formatoptions:{srcformat:'Y/m/d',newformat:'m-d'},hidden:true},
			{name:'arrival_plan_date',index:'arrival_plan_date', width:35, align:'center', 
				formatter:function(a,b,row) {
					if ("9999/12/31" == a) {
						return "未定";
					}
					if (a) {
						var d = new Date(a);
						return mdTextOfDate(d);
					}
					return "";
				},hidden:true
			},
			{name:'bo_flg',index:'bo_flg', width:50, align:'center',formatter:'select', editoptions: {value:$("#h_bo_eo").val()},hidden:true},
			{name:'section_name',index:'section_name', width:35},
			{name:'process_code',index:'process_code', width:30, align:'center'},
			{name:'scheduled_date',index:'scheduled_date', width:35, align:'center', formatter:'date', formatoptions:{srcformat:'Y/m/d H:i:s',newformat:'m-d'}},
			{name:'scheduled_assign_date',index:'scheduled_assign_date', width:35, align:'center', formatter:function(a,b,row) {
				if ("9999/12/31" == a) {
					return "未定";
				}
				if (a) {
					var d = new Date(a);
					return mdTextOfDate(d);
				}
				return "";
			}},
			{name:'scheduled_expedited',index:'scheduled_expedited', width:30, align:'center',formatter:function(a,b,row){
				if (a && a==="2") {
					return "直送快速"
				}
				if (a && a==="1") {
					return "加急"
				}
				return "";
			}},
			{name:'remain_days',index:'remain_days', width:80,hidden:true},
			{name:'break_message',index:'break_message', width:80,hidden:true},
			{name:'scheduled_manager_comment', index:'scheduled_manager_comment', width:80, hidden:true},
			{name:'break_back_flg',index:'break_back_flg', hidden:true},
			{name:'break_level',index:'break_level', hidden:true}
		],
		rowNum: 50, 
		rownumbers:true,
		toppager: false, 
		pager: "#listpager", 
		viewrecords: true, 
		caption: "", 
		ondblClickRow: function(rid, iRow, iCol, e) {
			var rowData = $("#list").getRowData(rid);
			var material_id = rowData["material_id"];
			showMaterial(material_id, rowData["reason"], rowData["comment"]);
		}, 
		multiselect: true, 
		gridview: true, // Speed up
		pagerpos: 'right', 
		pgbuttons: true, 
		pginput: false, 
		recordpos: 'left', 
		hidegrid: false, 
		viewsortcols: [true, 'vertical', true], 
		onSelectRow: enablebuttons,
		gridComplete: function(){
			enablebuttons(); showremain();
		}
	});

	$("#planned_list").jqGrid({
		toppager: true, 
		data: [], 
		height: 231, 
		width: 1248, 
		rowheight: 23, 
		datatype: "local", 
		colNames: [ '维修对象ID','','计划工程', '修理单号', '机种', '型号 ID', '型号', '机身号', '等级', '直送', '客户<br>同意', '委托处', '投线日期', '入库<br>预定日', '零件<br>BO', '加急', '维修课', '进展<br>工位', '6天<br>纳期', '总组出<br>货安排','remain_days'], 
		colModel: [
			{name: 'material_id', index: 'material_id', hidden: true},
			{name: 'line_id', index: 'line_id', hidden: true},
			{name: 'line_name', index: 'line_name', width: 60},
			{name: 'sorc_no', index: 'sorc_no', width: 105},
			{name:'category_name',index:'category_name', width:50},	
			{name: 'model_id', index: 'model_id', hidden: true},
			{name: 'model_name', index: 'model_name', width: 125},
			{name: 'serial_no', index: 'serial_no', width: 50},
			{name: 'levelName', index: 'levelName', width: 35, align: 'center'},
			{name:'direct_flg',index:'direct_flg', align:'center', width:30, formatter:'select', editoptions:{value:"1:直送;0:"}},
			{name: 'agreed_date', index: 'agreed_date', width: 50, align: 'center', formatter:'date', formatoptions:{srcformat:'Y/m/d H:i:s',newformat:'m-d'}},
			{name:'ocmName',index:'ocmName', width:65},
			{name: 'inline_time', index: 'inline_time', width: 50, align: 'center', hidden: true, formatter:'date', formatoptions:{srcformat:'Y/m/d H:i:s',newformat:'m-d'}},
			{name:'arrival_plan_date',index:'arrival_plan_date', width:50, align:'center', 
			formatter:function(a,b,row) {
				if ("9999/12/31" == a) {
					return "未定";
				}
				
				if (a) {
					var d = new Date(a);
					return mdTextOfDate(d);
				}
				
				return "";
			}},
			{name:'bo_flg',index:'bo_flg', width:50, align:'center',formatter:function(a,b,row){
				if (a && a==="1") {
					return "BO"
				}
				return "";
			}},
			{name:'scheduled_expedited',index:'scheduled_expedited', width:35, align:'center',formatter:function(a,b,row){
				if (a && a==="2") {
					return "直送快速"
				}
				if (a && a==="1") {
					return "加急"
				}
				return "";
			}},
			{name:'section_name',index:'section_name', width:35},
			{name: 'processing_position', index: 'processing_position', width: 35, align: 'center'},
			{name: 'com_plan_date', index: 'com_plan_date', width: 35, align: 'center', formatter:'date', formatoptions:{srcformat:'Y/m/d H:i:s',newformat:'m-d'}},
			{name:'com_finish_date',index:'com_finish_date', width:35, align:'center', formatter:function(a,b,row) {
				if ("9999/12/31" == a) {
					return "未定";
				}
				
				if (a) {
					var d = new Date(a);
					return mdTextOfDate(d);
				}
				
				return "";
			}},
			{name:'remain_days',index:'remain_days', width:80, hidden:true}
		], 
		rowNum: 50, 
		toppager: false, 
		pager: "#planned_listpager", 
		viewrecords: true, 
		caption: "", 
		ondblClickRow: function(rid, iRow, iCol, e) {
			var data = $("#planned_list").getRowData(rid);
			var material_id = data["material_id"];
			// var break_message_level = data["break_message_level"];
			showMaterial(material_id, null, "");
		}, 
		multiselect: true, 
		gridview: true, // Speed up
		pagerpos: 'right', 
		pgbuttons: true, 
		rownumbers : true,
		pginput: false, 
		recordpos: 'left', 
		hidegrid: false, 
		deselectAfterSort: false, 
		viewsortcols: [true, 'vertical', true], 
		onSelectRow: enablebuttons2, 
		onSelectAll: enablebuttons2, 
		gridComplete: checkout_complete
	});

	setData();
	findit();
	findSchedule();

//	$("#show_process").attr("checked", true).trigger("click");
	$("#show_process").attr("checked", true).trigger("click");
}

var showMaterial = function(material_id, reason, comment) {
	$process_dialog = $("#detail_dialog");
	$process_dialog.hide();
	// 导入编辑画面
	$process_dialog.load("widget.do?method=materialDetail&from=process&material_id=" + material_id , 
		function(responseText, textStatus, XMLHttpRequest) {
			var detail_buttons = {
				"确定":function(){
						doMaterialUpdate(material_id);
				},
				"取消":function(){
					$process_dialog.dialog('close');
				}
			};

			if ($process_dialog.find("editable").length == 0) {
				detail_buttons = {"关闭":function(){
					$process_dialog.dialog('close');
				}};		
			}

			if (reason == 1 && comment) {
				addLeakComment(comment);
			}

			$process_dialog.dialog({
				title : "维修对象详细信息",
				width : 800,
				show : "blind",
				height : 'auto' ,
				resizable : false,
				modal : true,
				minHeight : 200,
				close : function(){
					if (cmmtTO != null) {
						clearTimeout(cmmtTO);
						cmmtTO = null;
					}
				},
				buttons : detail_buttons
			});
	});

};

var cmmtTO = null;

var addLeakComment = function(comment) {
	var $commetTg = $("#edit_scheduled_manager_comment");
	if ($commetTg.length == 0) {
		cmmtTO = setTimeout(function(){
			addLeakComment(comment);
		}, 500);
	} else {
		if ($commetTg.val().indexOf(comment) < 0) {
			$commetTg.val("缺品零件：" + comment + "\n" + $commetTg.val());
		}
	}
}

// TODO like schedule start

var treatPlanedList = function(listdata) {
	$("#planned_list").jqGrid().clearGridData();
	$("#planned_list").jqGrid('setGridParam', {data: listdata}).trigger("reloadGrid", [{current: false}]);
}

function planned_list_handleComplete(xhrobj, textStatus) {

	var resInfo = null;

	// 以Object形式读取JSON
	eval('resInfo =' + xhrobj.responseText);

	if (resInfo.errors.length > 0) {
		// 共通出错信息框
		treatBackMessages("#searcharea", resInfo.errors);
	} else {
		// 读取一览 TODO
//		var listdata = resInfo.material_list;
//		if (listdata) {
//			treatInline(listdata);
//		}
		// 读取一览
		var planned_listdata = resInfo.schedule_list;
		treatPlanedList(planned_listdata);
		$("#list").jqGrid("resetSelection");
		enablebuttons();
		$("#planned_list").jqGrid("resetSelection");
		enablebuttons2();
	}
};

var findSchedule = function(lineId){
	var data = {
		scheduled_assign_date:$("#pick_date").val(),
		line_id: $("#select_line").val()
	}
	// Ajax提交
	$.ajax({
		beforeSend: ajaxRequestType, 
		async: true, 
		url: 'schedule.do?method=searchSchedule', 
		cache: false, 
		data: data, 
		type: "post", 
		dataType: "json", 
		success: ajaxSuccessCheck, 
		error: ajaxError, 
		complete: planned_list_handleComplete
	});
}

/**
 * 切到删除画面
 */
var deleteSchedule = function(rid) {

	// 读取删除行
	var selectedIds  = $("#planned_list").jqGrid("getGridParam","selarrrow"); 

	var ids = [];
	var lineIds = [];
	
	// 读取排入行
	for (var i = 0; i < selectedIds.length; i++) {
		var rowData = $("#planned_list").getRowData(selectedIds[i]);
		ids[ids.length] = rowData.material_id;
		lineIds[lineIds.length] = rowData.line_id;
	}
	var data = {
		ids:ids.join(","),
		lineIds:lineIds.join(","),
		scheduled_assign_date:$("#pick_date").val(),
		line_id: $("#select_line").val()
	}
	var now = new Date();
	var dd= new Date($("#pick_date").val());
	var day = ($("#pick_date").val() == "" || (now.getDate()+1) == dd.getDate()) ? "明" : dd.getDate(); 
		
	$("#confirm_message").text("确认要把这"+selectedIds.length+"台维修对象从"+ day +"日计划中移出吗？");
	$("#confirm_message").dialog({
		resizable: false,
		modal: true,
		title: "删除确认",
		buttons: {
			"确认": function() {
				$(this).dialog("close");
				// Ajax提交
				$.ajax({
					beforeSend: ajaxRequestType,
					async: false,
					url: 'schedule.do?method=dodeleteSchedule',
					cache: false,
					data: data,
					type: "post",
					dataType: "json",
					success: ajaxSuccessCheck,
					error: ajaxError,
					complete: planned_list_handleComplete
				});
			},
			"取消": function() {
				$(this).dialog("close");
			}
		}
	});
};

function changeDate(){
	var now = new Date();
	var dd= new Date($("#pick_date").val());
	var day = (now.getDate()+1) == dd.getDate() ? "明" : dd.getDate(); 
	$("#title_planned").text(day+"日计划一览");
	
	findSchedule();	
}

// TODO like schedule end

var download = function() {
	// Ajax提交
	$.ajax({
		beforeSend: ajaxRequestType, 
		async: true, 
		url: servicePath + '?method=searchDownload', 
		cache: false, 
		data: keepSearchData, 
		type: "post", 
		dataType: "json", 
		success: ajaxSuccessCheck, 
		error: ajaxError, 
		complete: function(xhrObj){
		 	var resInfo = $.parseJSON(xhrObj.responseText);
			if (resInfo.errors && resInfo.errors.length > 0) {
				treatBackMessages(null, resInfo.errors);
				return;
			}
    		if ($("iframe").length > 0) {
				$("iframe").attr("src", "download.do" + "?method=output&filePath=" + resInfo.filePath+"&fileName="+resInfo.fileName);
			} else {
				var iframe = document.createElement("iframe");
				iframe.src = "download.do" + "?method=output&filePath=" + resInfo.filePath+"&fileName="+resInfo.fileName;
				iframe.style.display = "none";
				document.body.appendChild(iframe);
			}
		}
	});
};
