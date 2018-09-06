/** 服务器处理路径 */
var servicePath = "echelon_allocate.do";

/**一览数据对象*/
var level_model_listdata={};
$(function(){
	$("input.ui-button").button();
	$("#detail_filter_level, #detail_filter_echelon").select2Buttons();

	//前台检索数据(双击之后，型号、等级、梯队进行检索)
	var filterList = function() {
		var detail_model_name_text = $("#detail_model_name").val();
		var detail_filter_echelon_value = $("#detail_filter_echelon option[selected]").text();
		var detail_filter_level_text = $("#detail_filter_level option[selected]").text();

		if (detail_model_name_text + detail_filter_echelon_value + detail_filter_level_text == "") {
			$("#allocate_list").find("tr").show();
		} else {
			$("#allocate_list").find("tr").show();
			$("#allocate_list").find("tr").each(function(){
				var $tr = $(this);
				if (detail_model_name_text.length > 0) {
					if ($tr.find("td[aria\\-describedby='allocate_list_model_name']").text() != detail_model_name_text) {
						$tr.hide();
					}
				}
				if (detail_filter_echelon_value.length > 0) {
					if ($tr.find("td[aria\\-describedby='allocate_list_echelon']").text() != detail_filter_echelon_value) {
						$tr.hide();
					}
				}
				if (detail_filter_level_text.length > 0) {
					if ($tr.find("td[aria\\-describedby='allocate_list_level']").text() != detail_filter_level_text) {
						$tr.hide();
					}
				}
			});
		}		
	}
	//选择下拉型号就立即进行检索
	setReferChooser($("#search_model_id"),null,null, filterList);//维修对象型号 下拉作用选择
	
	//梯队划分信息(选择型号、点击等级、点击梯队事件)
	$("#detail_filter_level,#detail_filter_echelon").change(filterList);
	
	//划分Button根据最后时间检索
	$("#searchbutton").click(calcEchelon);
	
	//梯队划分button
	$("#set_button").click(set_button_view);
	
	//初始化(查询)
	doInit();
});

/*根据型号、等级、梯队来检索梯队历史信息*/
var findit = function(){
	//选择型号、等级或者梯队进行信息检索
	$("#search_model_id").data("post",$("#search_model_id").val());
	$("#detail_filter_level").data("post",$("#detail_filter_level").val());
	$("#detail_filter_echelon").data("post",$("#detail_filter_echelon").val());
	var data ={
			"echelon_history_key":$("#hidden_echelon_history_key").val(),
			"model_id":$("#search_model_id").data("post"),
			"level":$("#detail_filter_level").data("post"),
			"echelon":$("#detail_filter_echelon").data("post")
	}
	$.ajax({
		beforeSend : ajaxRequestType,
		async : true,
		url : servicePath + '?method=echelonHistory',
		cache : false,
		data : data,
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : findit_calcEchelon_Complete
	});
};

function findit_calcEchelon_Complete (xhrobj, textStatus) {
	var resInfo = null;
	// 以Object形式读取JSON responseText获取来自服务器响应的数据
	eval('resInfo =' + xhrobj.responseText);
	if (resInfo.errors.length > 0) {
		// 共通出错信息框
		treatBackMessages(null, resInfo.errors);
	} else {
		level_model_listdata = resInfo.returnFormList;
		AllocateDetail(resInfo.returnFormList);
	}
};


/*初始化 梯队历史详细数据*/
var doInit=function(){
	$.ajax({
		beforeSend : ajaxRequestType,
		async : true,
		url : servicePath + '?method=search',
		cache : false,
		data : null,
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : search_Complete
	});
};

function search_Complete(xhrobj, textStatus) {
	var resInfo = null;
	// 以Object形式读取JSON responseText获取来自服务器响应的数据
	eval('resInfo =' + xhrobj.responseText);
	if (resInfo.errors.length > 0) {
		// 共通出错信息框
		treatBackMessages(null, resInfo.errors);
	} else {
		//取得最后时间，作为检索条件
		$("#search_start_date").val(resInfo.echelonAllocateFormEndDate);
		allocate_list(resInfo.echelonAllocateFormList);
	}
};

//梯队划分履历一览 
function allocate_list(listdata){
	if ($("#gbox_allocate_history_list").length > 0) {
		$("#allocate_history_list").jqGrid().clearGridData();
		$("#allocate_history_list").jqGrid('setGridParam',{data:listdata}).trigger("reloadGrid", [{current:false}]);
	}else{
		$("#allocate_history_list").jqGrid({
			data:listdata,
			height: 550,
			width: 992,
			rowheight: 23,
			datatype: "local",
			colNames:['型号 ID','梯队作用开始日期','梯队作用终了日期','操作者','设定时间'],
			colModel:[
				{name:'echelon_history_key',index:'echelon_history_key',width:40,align:'center',hidden:true},
				{name:'start_date',index:'start_date',width:100,align:'center'},
				{name:'end_date',index:'end_date',width:100,align:'center',sorttype : 'date',
					formatter : 'date',
					formatter : function(value, options, rData) {
						if(value=='9999-12-31'){
							return '';
						}else{
							return value;
						}
					}
				},
				{name:'updated_by',index:'updated_by',width:40,align:'left'},
				{name:'updated_time',index:'updated_time',width:90,align:'center',formatter : 'date',
					formatoptions : {
						srcformat : 'Y/m/d',
						newformat : 'Y-m-d'
					}}
			],
			rowNum: 20,
			toppager: false,
			pager: "#allocate_history_listpager",
			viewrecords: true,
			rownumbers : true,
			multiselect: false, 
			ondblClickRow : function(rid, iRow, iCol, e) {
				getAllocateDetail();
			},
			gridview: true,
			pagerpos: 'right',
			pgbuttons: true,
			pginput: false,
			recordpos:'left'
		});
	}
}
//梯队划分履历一览(双击事件)
//双击事件
var getAllocateDetail=function(){
	
	$(".end_date,#searchbutton").hide();
	$(".db_click_search").show();
	
	/*双击进入之前先清空上一次操作的痕迹*/
	$("#detail_model_name").val("").data("post", "");
	$("#search_model_id").val("").data("post", "");
	$("#detail_filter_level").val("").data("post", "").trigger("change");
	$("#detail_filter_echelon").val("").data("post", "").trigger("change");
	
	var row =$("#allocate_history_list").jqGrid("getGridParam", "selrow");// 得到选中行的ID	
	var rowData = $("#allocate_history_list").getRowData(row);
	
	//隐藏Key
	$("#hidden_echelon_history_key").val(rowData.echelon_history_key);
	
	var data ={
			"echelon_history_key":rowData.echelon_history_key
	}
	$.ajax({
		beforeSend : ajaxRequestType,
		async : true,
		url : servicePath + '?method=echelonHistory',
		cache : false,
		data : data,
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : getAllocateDetail_Complete
	});
}
function getAllocateDetail_Complete(xhrobj, textStatus) {
	var resInfo = null;
	// 以Object形式读取JSON responseText获取来自服务器响应的数据
	eval('resInfo =' + xhrobj.responseText);
	if (resInfo.errors.length > 0) {
		// 共通出错信息框
		treatBackMessages(null, resInfo.errors);
	} else {
		//dialog
		var this_dialog = $("#echelon_detail");
		$("#allocate_filter").show();
		
		//选择下拉型号直接检索
		// $(".subform tr").click(findit);
		//梯队划分信息
		AllocateDetail(resInfo.returnFormList);
		
		this_dialog.dialog({
				title : "梯队划分信息",
				position : 'auto',
				modal : true,
				width:'auto',
				minHeight : 200,
				resizable : false,
				buttons : {
					"关闭":function(){
						this_dialog.dialog('close');
					}
				}
		});
		$("button").hide();
	}
};
//梯队划分信息
var  AllocateDetail = function(level_model_listdata){
	if ($("#gbox_allocate_list").length > 0) {
		$("#allocate_list").jqGrid().clearGridData();
		$("#allocate_list").jqGrid('setGridParam',{data:level_model_listdata}).setGridHeight(343).trigger("reloadGrid", [{current:false}]);
	}else{
		$("#allocate_list").jqGrid({
			data:level_model_listdata,
			height: 343,
			width: 480,
			rowheight: 23,
			datatype: "local",
			colNames:['型号','等级','设定梯队','同意维修数','前次比较'],
			colModel:[
					{name:'model_name',index:'model_name',width:120,align:'left'},
					{name:'level',index:'level',width:30,align:'center',formatter:'select', editoptions:{value:"1:S1;2:S2;3:S3"}},
					{name:'echelon',index:'echelon',width:40,align:'center',formatter:'select', editoptions:{value:"1:第一梯队;2:第二梯队;3:第三梯队;4:第四梯队"}},
					{name:'agreed_count',index:'agreed_count',width:50,align:'center',formatter : function(value, options, rData){
						if(value == null || value==''){
							return '';
						}else{
							return value;
						}						
					}},
					{name:'fluctuate',index:'fluctuate',width:40,align:'center'}
			],
			rowNum: 500,
			toppager: false,
			pager: "#allocate_listpager",
			viewrecords: true,
			rownumbers : true,
			multiselect: false, 
			gridview: true,
			pagerpos: 'right',
			pgbuttons: true,
			pginput: false,
			recordpos:'left'
		});
	}
}

/*梯队划分Button*/
var set_button_view = function(){
	$.ajax({
		beforeSend : ajaxRequestType,
		async : true,
		url : servicePath + '?method=searchDate',
		cache : false,
		data : null,
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete :searchDate_Complete
	});
}

function searchDate_Complete (xhrobj, textStatus){
	var resInfo = null;
	eval('resInfo =' + xhrobj.responseText);
	
	if (resInfo.errors.length > 0) {
		
		treatBackMessages(null, resInfo.errors);
		
	} else {
		
		$("#search_start_date,#search_end_date").datepicker({
			showButtonPanel : true,
			dateFormat : "yy/mm/dd",
			currentText : "今天"
		});
	
		$("#searchbutton").focus();//onblur onkeypress onfocus

		//jqGrid
		calcEchelonComplete([]);

		$("#echelon_detail").dialog({
			title : "梯队划分信息",
			position : 'auto',
			modal : true,
			width:'auto',
			minHeight : 200,
			resizable : false,
			buttons : {
				"确定":function(){
					update_echelon_history_set();
					$("#echelon_detail").dialog('close');
					$("#search_start_date").val("");
					$("#search_end_date").val("");
				},
				"取消":function(){
					$("#echelon_detail").dialog('close');
					$("#search_start_date").val("");
					$("#search_end_date").val("");
				}
			},
			open: function( event, ui ) {
				//检索开始时间
				$("#search_start_date").val(resInfo.startDate);
				//获取当前时间，作为检索区间结束
				$("#search_end_date").val(resInfo.currentDate);
				//隐藏弹出日期
				$("#ui-datepicker-div").hide();
				$("#search_start_date").one("click",function(){
					$("#ui-datepicker-div").show("fade", 200);
				});
			}
		});
		
		$(".ui-dialog-buttonpane button:first-child").hide();
	}
}

/*检索button事件*/
var calcEchelon = function(){
	var data={
			"start_date":$("#search_start_date").val(),
			"end_date":$("#search_end_date").val()
	}
	$.ajax({
		beforeSend : ajaxRequestType,
		async : true,
		url : servicePath + '?method=modelLevelSetDetail',
		cache : false,
		data : data,
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : calcEchelon_Complete
	});
}

function calcEchelon_Complete (xhrobj, textStatus){
	var resInfo = null;
	eval('resInfo =' + xhrobj.responseText);
	
	if (resInfo.errors.length > 0) {
		
		treatBackMessages(null, resInfo.errors);
		
	} else {
		//获取当前时间，作为检索区间结束
		$("#search_end_date").val(resInfo.currentDate);
		
		calcEchelonComplete(resInfo.returnFormList);	
	}
}
//梯队划分信息(梯队划分button触发)
/*梯队划分信息*/
var calcEchelonComplete = function(level_model_listdata){
	var this_dialog = $("#echelon_detail");
	$(".end_date,#allocate_filter,#searchbutton").show();
	$(".db_click_search").hide();
	
	$("button:first-child").show();
	
	if ($("#gbox_allocate_list").length > 0) {
		$("#allocate_list").jqGrid().clearGridData();
		$("#allocate_list").jqGrid('setGridParam',{data:level_model_listdata}).setGridHeight(461).trigger("reloadGrid", [{current:false}]);
	}else{
		$("#allocate_list").jqGrid({
			data:level_model_listdata,
			height: 461,
			width: 480,
			rowheight: 23,
			datatype: "local",
			colNames:['型号','等级','设定梯队','同意维修数','前次比较'],
			colModel:[
				{name:'model_name',index:'model_name',width:120,align:'left'},
				{name:'level',index:'level',width:30,align:'center',formatter:'select', editoptions:{value:"1:S1;2:S2;3:S3"}},
				{name:'echelon',index:'echelon',width:40,align:'center',formatter:'select', editoptions:{value:"1:第一梯队;2:第二梯队;3:第三梯队;4:第四梯队"}},
				{name:'agreed_count',index:'agreed_count',width:90,align:'center',formatter : function(value, options, rData){
					if(value == null || value==''){
						return '';
					}else{
						return value;
					}						
				}},
				{name:'fluctuate',index:'fluctuate',width:40,align:'center'}
			],
			rowNum: 500,
			toppager: false,
			pager: "#allocate_listpager",
			viewrecords: true,
			rownumbers : true,
			multiselect: false, 
			gridview: true,
			pagerpos: 'right',
			pgbuttons: true,
			pginput: false,
			recordpos:'left'
		});
	}
}
//点击确定更新所有梯队
var update_echelon_history_set = function(){
	
	$.ajax({
		beforeSend : ajaxRequestType,
		async : true,
		url : servicePath + '?method=doUpdateEchelon',
		cache : false,
		data : null,
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : doUpdateEchelon_Complete
	});
}

function doUpdateEchelon_Complete (xhrobj, textStatus){
	var resInfo = null;
	eval('resInfo =' + xhrobj.responseText);
	if (resInfo.errors.length > 0) {
		treatBackMessages(null, resInfo.errors);
	} else {
		
		$("#update_success_dialog").text("更新梯队已经完成!");
		$("#update_success_dialog").dialog({
			width : 320,
			height : 'auto',
			resizable : false,
			show : "blind",
			modal : true,
			title : "更新梯队",
			buttons : {
				"关闭" : function() {
					$("#update_success_dialog").dialog("close");
				}
			}
		});
		//初始化载查询一次
		doInit();
	}
}