var modelname = "警告信息";

/** 一览数据对象 */
var listdata = {};
/** 服务器处理路径 */
var servicePath = "alarmMessage.do";
var lOptions = "";
var rOptions = "";

var findit = function() {
	var data = {
		level : $("#cond_level").data("post"),
		reason : $("#cond_reason").data("post"),
		occur_time_from : $("#cond_occur_time_from").data("post"),
		occur_time_to : $("#cond_occur_time_to").data("post"),
		sorc_no : $("#cond_sorc_no").data("post"),
		model_id : $("#cond_model_id").data("post"),
		serial_no : $("#cond_serial_no").data("post"),
		section_id : $("#cond_section_id").data("post"),
		line_id : $("#cond_line_id").data("post"),
		reciever_id : $("#cond_reciever_id").data("post")
	};

	// Ajax提交
	$.ajax({
		beforeSend : ajaxRequestType,
		async : true,
		url : servicePath + '?method=search',
		cache : false,
		data : data,
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : search_handleComplete
	});
};

$(function() {

	$("input.ui-button").button();

	$("#searchbutton").addClass("ui-button-primary");

	$("#searcharea span.ui-icon,#wiparea span.ui-icon").bind("click", function() {
		$(this).toggleClass('ui-icon-circle-triangle-n').toggleClass('ui-icon-circle-triangle-s');
		if ($(this).hasClass('ui-icon-circle-triangle-n')) {
			$(this).parent().parent().next().show("blind");
		} else {
			$(this).parent().parent().next().hide("blind");
		}
	});

	$("#cond_level, #cond_reason, #cond_section_id, #cond_line_id").select2Buttons();

	$("#cond_occur_time_from, #cond_occur_time_to").datepicker({
		showButtonPanel:true,
		currentText: "今天"
	});

	setReferChooser($("#cond_model_id"), $("#referchooser_model"));
	setReferChooser($("#cond_reciever_id"), $("#referchooser_operator"));

	// 检索处理
	$("#searchbutton").click(function() {
		// 保存检索条件
		$("#cond_level").data("post", $("#cond_level").val());
		$("#cond_reason").data("post", $("#cond_reason").val());
		$("#cond_occur_time_from").data("post", $("#cond_occur_time_from").val());
		$("#cond_occur_time_to").data("post", $("#cond_occur_time_to").val());
		$("#cond_sorc_no").data("post", $("#cond_sorc_no").val());
		$("#cond_model_id").data("post", $("#cond_model_id").val());
		$("#cond_serial_no").data("post", $("#cond_serial_no").val());
		$("#cond_section_id").data("post", $("#cond_section_id").val());
		$("#cond_line_id").data("post", $("#cond_line_id").val());
		$("#cond_reciever_id").data("post", $("#cond_reciever_id").val());
		// 查询
		findit();
	});

	// 清空检索条件
	$("#resetbutton").click(function() {
		$("#cond_level").val("").trigger("change").data("post", "");
		$("#cond_reason").val("").trigger("change").data("post", "");
		$("#cond_occur_time_from").val("").data("post", "");
		$("#cond_occur_time_to").val("").data("post", "");
		$("#cond_sorc_no").val("").data("post", "");
		$("#cond_model_id").prev("input").val("");
		$("#cond_model_id").val("").data("post", "");
		$("#cond_serial_no").val("").data("post", "");
		$("#cond_section_id").val("").trigger("change").data("post", "");
		$("#cond_line_id").val("").trigger("change").data("post", "");
		$("#cond_reciever_id").prev("input").val("");
		$("#cond_reciever_id").val("").data("post", "");
	});

	if ($("#cond_occur_time_from").val()) {
		$("#cond_occur_time_from").data("post", $("#cond_occur_time_from").val());
		findit();
	}

	// 不良新现象登记
	$("#nongood_new_phenomenonbutton").click(function() {
		var rowid = $("#list").jqGrid('getGridParam', 'selrow');
		if (rowid == null) {return;}
		var rowData = $("#list").jqGrid('getRowData', rowid);
		if (typeof(popNewPhenomenon) === "function") popNewPhenomenon(rowData.id, true);
	})
});

var showDetail = function(alarm_messsage_id){

	popMessageDetail(alarm_messsage_id, true);

};

/*
 * Ajax通信成功的处理
 */
function search_handleComplete(xhrobj, textStatus) {

	var resInfo = null;
	try {
		// 以Object形式读取JSON
		eval('resInfo =' + xhrobj.responseText);

		if (resInfo.errors.length > 0) {
			// 共通出错信息框
			treatBackMessages("#searcharea", resInfo.errors);
		} else {
			listdata = resInfo.list;
			lOptions = resInfo.lOptions;
			rOptions = resInfo.rOptions;
			
			if ($("#gbox_list").length > 0) {
				$("#list").jqGrid().clearGridData();
				$("#list").jqGrid('setGridParam', {data : listdata}).trigger("reloadGrid", [{current : false}]);
			} else {
				$("#list").jqGrid({
					toppager : true,
					data : listdata,
					height : 461,
					width : 992,
					rowheight : 23,
					datatype : "local",
					colNames : ['','警报等级', '原因', '发生时间', '修理单号', '维修对象型号',
							'担当人', '课室', '工程', '工位', '处理人', '处理时间'],
					colModel : [
						{name:'id',index:'id', hidden: true, key: true},
						{name:'level',index:'level', width:60, formatter:'select', editoptions:{value:lOptions}},
						{name:'reason',index:'reason', width:60, formatter:'select', editoptions:{value:rOptions}},
						{name:'occur_time',index:'occur_time', width:50, align:'center', formatter:'date', formatoptions:{srcformat:'Y/m/d H:i:s',newformat:'m-d H:i'}},
						{name:'sorc_no',index:'sorc_no', width:95},
						{name:'model_name',index:'model_id', width:125},
						{name:'operator_name',index:'operator_name', width:60},
						{name:'section_name',index:'section_name', width:35},
						{name:'line_name',index:'line_name', width:65},
						{name:'process_code',index:'process_code', width:35},
						{name:'resolver_name',index:'resolver_id', width:50},
						{name:'resolve_time',index:'resolve_time', width:50, align:'center', formatter:'date', formatoptions:{srcformat:'Y/m/d H:i:s',newformat:'m-d H:i'}}
					],
					rowNum : 50,
					toppager : false,
					pager : "#listpager",
					viewrecords : true,
					caption : modelname + "一览",
					onSelectRow: enablebuttons, 
					ondblClickRow : function(rid, iRow, iCol, e) {
						var data = $("#list").getRowData(rid);
						var alarm_messsage_id = data["id"];
						showDetail(alarm_messsage_id);
					},
					// multiselect : true, 
					gridview : true, // Speed up
					pagerpos : 'right',
					pgbuttons : true,
					pginput : false,
					recordpos : 'left',
					viewsortcols : [true, 'vertical', true],
					gridComplete: enablebuttons
				});
				// $("#list").gridResize({minWidth:1248,maxWidth:1248,minHeight:200,
				// maxHeight:900});
			}
		}
	} catch (e) {
		alert("name: " + e.name + " message: " + e.message + " lineNumber: "
				+ e.lineNumber + " fileName: " + e.fileName);
	};
};

var enablebuttons = function() {
	var rowid = $("#list").jqGrid("getGridParam", "selrow");
	if (rowid == null) {
		$("#nongood_new_phenomenonbutton").disable();
	} else {
		var rowdata = $("#list").getRowData(rowid);
		if(rowdata.reason != 1 && rowdata.reason != 5) {
			$("#nongood_new_phenomenonbutton").disable();
		} else {
			$("#nongood_new_phenomenonbutton").enable();
		}
	}
}
