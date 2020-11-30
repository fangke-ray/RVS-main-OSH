var modelname = "不良新现象";

/** 一览数据对象 */
var listdata = {};
/** 服务器处理路径 */
var servicePath = "new_phenomenon.do";

var findit = function() {
	var data = {
		omr_notifi_no : $("#omr_notifi_no").data("post"),
		kind : $("#kind").data("post"),
		occur_time_from : $("#occur_time_from").data("post"),
		occur_time_to : $("#occur_time_to").data("post"),
		description : $("#description").data("post"),
		return_status : $("#return_status_set").data("post"),
		last_determine_date_from : $("#last_determine_date_from").data("post"),
		last_determine_date_to : $("#last_determine_date_to").data("post"),
		line_id : $("#line_id").data("post")
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

	$("#kind, #line_id").select2Buttons();
	$("#return_status_set").buttonset();

	$("#occur_time_from, #occur_time_to, #last_determine_date_from, #last_determine_date_to").datepicker({
		showButtonPanel:true,
		currentText: "今天"
	});

	// 检索处理
	$("#searchbutton").click(function() {
		// 保存检索条件
		$("#omr_notifi_no").data("post", $("#omr_notifi_no").val());
		$("#kind").data("post", $("#kind").val());
		$("#occur_time_from").data("post", $("#occur_time_from").val());
		$("#occur_time_to").data("post", $("#occur_time_to").val());
		$("#line_id").data("post", $("#line_id").val());
		$("#description").data("post", $("#description").val());
		$("#last_determine_date_from").data("post", $("#last_determine_date_from").val());
		$("#last_determine_date_to").data("post", $("#last_determine_date_to").val());
		$("#return_status_set").data("post"
			, $("#return_status_set > input:radio:eq(1)").is(":checked") ? "1" : null);
		// 查询
		findit();
	});

	// 清空检索条件
	$("#resetbutton").click(function() {
		$("#omr_notifi_no").val("").data("post", "");
		$("#kind").val("").trigger("change").data("post", "");
		$("#occur_time_from").val("").data("post", "");
		$("#occur_time_to").val("").data("post", "");
		$("#line_id").val("").trigger("change").data("post", "");
		$("#description").val("").data("post", "");
		$("#last_determine_date_from").val("").data("post", "");
		$("#last_determine_date_to").val("").data("post", "");
		$("#return_status_set").data("post", "")
			.children("input:radio:eq(0)").attr("checked", true)
			.trigger("change");
	});

	initGrid([]);
	findit();
});

/*
 * Ajax通信成功的处理
 */
function search_handleComplete(xhrobj, textStatus) {

	try {
		// 以Object形式读取JSON
		var resInfo = $.parseJSON(xhrobj.responseText);

		if (resInfo.errors.length > 0) {
			// 共通出错信息框
			treatBackMessages("#searcharea", resInfo.errors);
		} else {
			listdata = resInfo.list;

			if ($("#gbox_list").length > 0) {
				$("#list").jqGrid().clearGridData();
				$("#list").jqGrid('setGridParam', {data : listdata}).trigger("reloadGrid", [{current : false}]);
			} else {
				initGrid(listdata);
			}
		}
	} catch (e) {
		console.log("name: " + e.name + " message: " + e.message + " lineNumber: "
				+ e.lineNumber + " fileName: " + e.fileName);
	};
};

var initGrid = function(listdata){

	$("#list").jqGrid({
		toppager : true,
		data : listdata,
		height : 461,
		width : 992,
		rowheight : 23,
		datatype : "local",
		colNames : ['','维修单号', '机种', '机身号', '不良发生日', '发生工程', '故障部件组',
				'故障部位', '故障描述', '报告记录者', '报告发送者', '最后决定日', '发送回应'],
		colModel : [
			{name:'key',index:'key', hidden: true, key: true},
			{name:'omr_notifi_no',index:'omr_notifi_no', width:50},
			{name:'category_name',index:'category_name', width:60},
			{name:'serial_no',index:'serial_no', width:50},
			{name:'occur_time',index:'occur_time', width:45, align:'center', formatter:'date', formatoptions:{srcformat:'Y/m/d',newformat:'y-m-d'}},
			{name:'line_name',index:'line_name', width:50},
			{name:'location_group_desc',index:'location_group_desc', width:60},
			{name:'location_desc',index:'location_desc', width:60},
			{name:'description',index:'description', width:80},
			{name:'operator_name',index:'operator_name', width:50},
			{name:'determine_operator_name',index:'determine_operator_name', width:50},
			{name:'last_determine_date',index:'last_determine_date', width:45, align:'center', formatter:'date', formatoptions:{srcformat:'Y/m/d',newformat:'m-d'}},
			{name:'return_status',index:'return_status', width:40}
		],
		rowNum : 50,
		toppager : false,
		pager : "#listpager",
		viewrecords : true,
		caption : modelname + "一览",
		ondblClickRow : function(rid, iRow, iCol, e) {
			showDetail(rid);
		},
		gridview : true, // Speed up
		pagerpos : 'right',
		pgbuttons : true,
		pginput : false,
		recordpos : 'left',
		viewsortcols : [true, 'vertical', true]
	});
}

var showDetail = function(key){

	popNewPhenomenon(key, true, findit);
};