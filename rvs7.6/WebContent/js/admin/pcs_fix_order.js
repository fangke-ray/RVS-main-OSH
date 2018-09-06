var g_waiting = {};
/** 服务器处理路径 */
var servicePath = "pcsFixOrder.do";

var doInit_ajaxSuccess = function(xhrobj, textStatus){
	var resInfo = null;
	try {
		// 以Object形式读取JSON
		eval('resInfo =' + xhrobj.responseText);
		if (resInfo.errors.length > 0) {
			// 共通出错信息框
			treatBackMessages(null, resInfo.errors);
		} else {
			waiting_list(resInfo.waiting);
			// finished_list(resInfo.finished);
		}
	} catch (e) {
		console.log("name: " + e.name + " message: " + e.message + " lineNumber: "
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
		data : {complete_date_start : $("#search_qa_pass_start").val()},
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : doInit_ajaxSuccess
	});
};

var findFixList=function(){
	// Ajax提交
	$.ajax({
		beforeSend : ajaxRequestType,
		async : false,
		url : servicePath + '?method=jsinit',
		cache : false,
		data : {complete_date_start : $("#search_qa_pass_start").val()},
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : doInit_ajaxSuccess
	});
};

/** 
 * 检索处理
 */
var findit = function() {
	// 读取已记录检索条件提交给后台
	var data = {
		"category_id" : $("#search_category_id").data("post"),
		"model_id":$("#search_model_id").data("post"),
		"serial_no":$("#search_serialno").data("post"),
		"sorc_no":$("#search_sorcno").data("post"),
		"section_id":$("#search_section_id").data("post"),
		"reception_time_start":$("#search_reception_time_start").data("post"),
		"reception_time_end":$("#search_reception_time_end").data("post"),
		"complete_date_start":$("#search_qa_pass_start").val(),
		"complete_date_end":$("#search_qa_pass_end").val(),
		"scheduled_date_start":$("#search_scheduled_date_start").data("post"),
		"scheduled_date_end":$("#search_scheduled_date_end").data("post"),
		"fix_type":$("#search_fix_id").data("post"),
		"service_repair_flg":$("#search_repair_id").data("post"),
		"direct_flg":$("#search_direct_id").data("post"),
		"operator_id":$("#search_person_id").data("post")
	}

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
		complete : doInit_ajaxSuccess
	});
};

//重置值为空
var reset = function() {
	$("#search_category_id").val("").trigger("change").data("post", "");
	$("#search_txt_modelname").val("").data("post", "");
	$("#search_model_id").val("").data("post", "").prev().val("");
	$("#search_serialno").val("").data("post", "");
	$("#search_sorcno").val("").data("post", "");
	$("#search_section_id").val("").trigger("change").data("post", "");
	$("#search_reception_time_start").val("").data("post", "");
	$("#search_reception_time_end").val("").data("post", "");
	$("#search_qa_pass_start").val($("#h_date_start").val()).data("post", $("#h_date_start").val());
	$("#search_qa_pass_end").val("").data("post", "");
	$("#search_scheduled_date_start").val("").data("post", "");
	$("#search_scheduled_date_end").val("").data("post", "");
	$("#search_person_id").val("").trigger("change").data("post", "");
	$("#search_fix_id").val("").trigger("change").data("post", "");
	$("#search_repair_id").val("").trigger("change").data("post", "");
	$("#search_direct_id").val("").trigger("change").data("post", "");
	$("#search_person_id").val("").data("post","");
};

//页面加载完毕需要进行的操作
$(function() {	
	$("input.ui-button").button();//检索和清除点击事件
	$("a.areacloser").hover(//检索条件的右上角的超链接将鼠标移动上去和移走鼠标后的样式
		function (){$(this).addClass("ui-state-hover");},
		function (){$(this).removeClass("ui-state-hover");}
	);
	$("#body-mdl span.ui-icon").bind("click", function() {
		$(this).toggleClass('ui-icon-circle-triangle-n').toggleClass('ui-icon-circle-triangle-s');
		if ($(this).hasClass('ui-icon-circle-triangle-n')) {
			$(this).parent().parent().next().show("blind");
		} else {
			$(this).parent().parent().next().hide("blind");
		}
	});

	$(".ui-buttonset").buttonset();

	$("#cond_update_time_start, #cond_update_time_end").datepicker({
		showButtonPanel:true,
		dateFormat: "yy/mm/dd",
		currentText: "今天"
	});

	
	//当开始点击检索按钮时触发的事件
	$("#searchbutton").click(function() {//按照按钮的ID的获取其值，再将值使用post提交
		$("#search_category_id").data("post", $("#search_category_id").val());
		$("#search_model_id").data("post", $("#search_model_id").val());
		$("#search_serialno").data("post", $("#search_serialno").val());
		findit();//检索开始并提交给后台
	});

	$("#resetbutton").click(function() {
		reset();//点击清除将执行reset();将所有的检索值清空
	}); 
	doInit();
});

var doPcResolve = function(pcs_fix_order_key, dispatch) {
	// alert(dispatch);
// Ajax提交
	var data = {dispatch:dispatch, pcs_fix_order_key:pcs_fix_order_key };
	$.ajax({
		beforeSend : ajaxRequestType,
		async : true,
		url : servicePath + '?method=doPcResolve',
		cache : false,
		data : data,
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : doInit_ajaxSuccess
	});
}

var showPcResolve = function(pcs_fix_order_key) {
	this_dialog = $("#detail_dialog");
	if (this_dialog.length === 0) {
		$("body.outer").append("<div id='detail_dialog'/>");
		this_dialog = $("#detail_dialog");
	}
	this_dialog.load("pcsFixOrder.do?method=pcpage&pcs_fix_order_key=" + pcs_fix_order_key , function(responseText, textStatus, XMLHttpRequest) {
		this_dialog.find("input[type=button]").button().click(function(){
			doPcResolve(pcs_fix_order_key, this.id);
		});
		this_dialog.dialog({
			dialogClass : 'ui-warn-dialog',
			title : "警报详细画面",
			width : 'auto',
			show : "",
			height :  'auto',
			resizable : false,
			modal : true,
			buttons : null
		});
	});
}

//JqGrid表格
function waiting_list(listdata){
	g_waiting = listdata;
	if ($("#gbox_waitinglist").length > 0) {
		$("#waitinglist").jqGrid().clearGridData();//清除
		$("#waitinglist").jqGrid('setGridParam',{data:g_waiting}).trigger("reloadGrid", [{current:false}]);//刷新列表
	} else {
		$("#waitinglist").jqGrid({
			toppager : true,
			data:g_waiting,//表中数据
			width: 992,
			height: 461,
			rowheight: 23,
			datatype: "local",//设定将要得到的数据类型
			colNames:['','material_id','line_id','修理单号', '发送者' , '修正需求','申请时间',''],//字符串数组，用于指定各列的题头文本，与列的顺序是对应的
			colModel:[//最重要的数组之一，用于设定各列的参数，可以用对象进行替换
				{name:'pcs_fix_order_key',index:'pcs_fix_order_key', hidden:true},
				{name:'material_id',index:'material_id', hidden:true},
				{name:'line_id',index:'line_id', hidden:true},
				{name:'sorc_no',index:'sorc_no', width:75,align:'left'},
				{name:'sender_name',index:'sender_name', width:50,align:'left'},
				{name:'comment',index:'comment', width:390, align:'left'},
				{
					name : 'update_time',
					index : 'update_time',
					width : 40,
					align : 'center', 
					sorttype: 'date', formatter: 'date', formatoptions: {srcformat: 'Y/m/d H:i:s', newformat: 'm-d h时'}
				},
				{name:'status',index:'status', width:390, align:'left', hidden:true}
			],
			rowNum: 20,
			toppager: false,
			pager: "#waitinglistpager",
			viewrecords: true,//设置是否在page bar 显示所有记录的总数
			hidegrid : false,//
			caption: "等待修改申请一览",//标题
			gridview: true, // 
			pagerpos: 'right',
			pgbuttons: true,
			pginput: false,
			recordpos: 'left',
			viewsortcols : [true,'vertical',true],
			ondblClickRow : function(rid, iRow, iCol, e) {
				var data = $("#waitinglist").getRowData(rid);
				var material_id = data["material_id"];
				var status = data["status"];
				if (status == 5) {
					showPcResolve(data["pcs_fix_order_key"]);
				} else {
					showPcsDetailManager(material_id, $("#sel_line_set input:checked").val() || data["line_id"], data["pcs_fix_order_key"], true, data["comment"]);
				}
			},
			gridComplete: function() {
			}
		});	
   }
};