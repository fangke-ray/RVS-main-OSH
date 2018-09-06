/** 服务器处理路径 */
var servicePath = "usage_check.do";

$(function(){
	$("input.ui-button").button(); 

	/*检索*/
	$("#searchbutton").click(function(){
		$("#search_object_type").data("post", $("#search_object_type input:checked").val());
		$("#search_check_proceed").data("post", $("#search_check_proceed input:checked").val());
		$("#search_sheet_manage_no").data("post", $("#search_sheet_manage_no").val());
		$("#search_manage_no").data("post", $("#search_manage_no").val());
		$("#search_name").data("post", $("#search_name").val());
		$("#search_model_name").data("post", $("#search_model_name").val());
		$("#search_type_id").data("post", $("#search_type_id").val());
		$("#search_position_id").data("post", $("#search_position_id").val());
		$("#search_check_result").data("post", $("#search_check_result input:checked").val());
		findit();
	});

	$(".ui-buttonset").buttonset();

	var fromPosition = null;
	if ($("#search_position_id").val()) {
		fromPosition = $("#search_position_id").val();
	}
	setReferChooser($("#search_position_id"), $("#pReferChooser"));
	setReferChooser($("#search_type_id"), $("#nReferChooser"));

	reset();

	if (fromPosition) {
		$("#search_position_id").val(fromPosition).trigger("change");
		$("#check_proceed_n").attr("checked", "checked").trigger("change");
	}

	/*清空*/
	$("#resetbutton").click(function(){
		reset();
	});

	devices_usage_check_list([]);
	if (fromPosition) {
		$("#search_position_id").data("post", $("#search_position_id").val());
		$("#search_check_proceed").data("post", "2");
		findit();
	} else {
		$("#search_position_id").val(fromPosition).trigger("change");
	}
});	

var findit = function() {
	var postData = {
		object_type : $("#search_object_type").data("post"),
		check_proceed : $("#search_check_proceed").data("post"),
		sheet_manage_no : $("#search_sheet_manage_no").data("post"),
		manage_code : $("#search_manage_no").data("post"),
		model_name : $("#search_model_name").data("post"),
		devices_type_id : $("#search_type_id").data("post"),
		position_id : $("#search_position_id").data("post"),
		check_result : $("#search_check_result").data("post")
	}
	$.ajax({
		beforeSend : ajaxRequestType,
		async : true,
		url : servicePath + '?method=search',
		cache : false,
		data : postData,
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : function(xhrObj){
			var resInfo = $.parseJSON(xhrObj.responseText);
			if (resInfo.ucList)
				devices_usage_check_list(resInfo.ucList);
		}
	});
}

var doinit = function(){
	$.ajax({
		beforeSend : ajaxRequestType,
		async : true,
		url : servicePath + '?method=doJsinit',
		cache : false,
		data : null,
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : function(xhrObj){
			var resInfo = $.parseJSON(xhrObj.responseText);
			if (resInfo.ucList)
				devices_usage_check_list(resInfo.ucList);
		}
	});
}

/*清空函数*/
var reset = function(){
	$("#search_object_type").data("post", "");
	$("#search_check_proceed").data("post", "");
	$("#search_object_type input:eq(0)").attr("checked", "checked").trigger("change");
	$("#search_check_proceed input:eq(0)").attr("checked", "checked").trigger("change");
	$("#search_sheet_manage_no").val("").data("post", "");
	$("#search_name").val("").data("post", "");
	$("#search_model_name").val("").data("post", "");
	$("#search_type_id").val("").data("post", "");
	$("#search_position_id").val("").data("post", "");
	$("#search_check_result").data("post", "");
	$("#search_check_result input:eq(0)").attr("checked", "checked").trigger("change");

	$("#search_manage_no").val("");
	$("#search_position_name").val("");
};

/*设备工具点检种类一览*/
function devices_usage_check_list(usage_check_list){
	if ($("#gbox_usage_check_list").length > 0) {
        $("#usage_check_list").jqGrid().clearGridData();
        $("#usage_check_list").jqGrid('setGridParam',{data:usage_check_list}).trigger("reloadGrid", [{current:false}]);
    } else {
		$("#usage_check_list").jqGrid({
			data:usage_check_list,
            height: 461,
            width: 992,
            rowheight: 23,
            datatype: "local",
			colNames:['section_id','position_id','operator_id','关联工位','点检管理ID','点检管理SheetID','点检对象','管理编号','品名','型号','点检表管理号','点检分类','点检状态','点检结果'],
			colModel:[
				{name:'section_id',index:'section_id', hidden:true},
				{name:'position_id',index:'position_id', hidden:true},
				{name:'operator_id',index:'operator_id', hidden:true},
				{name:'process_code',index:'process_code',align:'center',width : 60},
				{name:'manage_id',index:'manage_id', hidden:true}, 
				{name:'check_file_manage_id',index:'check_file_manage_id', hidden:true}, 
				{name:'object_type',index:'object_type',align:'center',width:50, formatter:'select', editoptions:{value:"2:治具清点;1:设备工具"}},
				{name:'manage_code',index:'manage_code',width : 100},
				{name:'name',index:'name',width : 120},
				{name:'model_name',index:'model_name',width :100 },
				{name:'sheet_manage_no',index:'sheet_manage_no',width:180},
				{name:'cycle_type',index:'cycle_type',align:'center',width:50, formatter:'select', editoptions:{value:"0:日常点检;1:周点检;2:月点检;3:半期点检;4:半月点检;9:使用前点检"}},
				{name:'check_proceed',index:'check_proceed',align:'center',width:50, formatter:'select', editoptions:{value:"0:未点检;1:点检中;2:已点检"}},
				{name:'check_result', index:'check_result', align:'center',width:50, formatter:'select', editoptions:{value:"1:通过;2:不通过;3:遗失;4:备品"}}
      		],
			rowNum: 20,
			rownumbers:true,
            toppager : false,
            pager : "#usage_check_listpager",
            viewrecords : true,
            gridview : true,
            pagerpos : 'right',
            pgbuttons : true, 
            pginput : false,
            recordpos : 'left',
            hidegrid : false,
            deselectAfterSort : false,  
            ondblClickRow : showDetail,
            viewsortcols : [true,'vertical',true]
		});
	}
};

/* 新建设备工具点检种类 画面*/
var showCheckDefineAdd = function(){
	$("#usage_check_search").hide();
	$("#usage_check_add").show();

	$("#add_name").val("").removeClass("valid errorarea-single");//品名
	$("#add_daily_sheet_manage_no").val("");
	$("#add_daily_sheet_file").val("");
	$("#add_daily_sheet_file_align").attr("checked", "checked").trigger("change");
	$("#add_regular_sheet_manage_no").val("");
	$("#add_regular_sheet_file").val("");
	$("#add_regular_sheet_file_align").attr("checked", "checked").trigger("change");
	$("#add_user_chose_manage_code_no").attr("checked","checked").trigger("change");

	$("#add_daily_sheet_file_style,#add_regular_sheet_file_style,#add_user_chose_manage_code").buttonset();

	$("#add_usage_check_form").validate({
		rules:{
			add_name:{
				required:true,
				maxlength:32
			}
		}
	});

	/*确认*/
	$("#confirebutton").click(function(){
		if($("#add_usage_check_form").valid()){
			$("#usage_check_search").show();
			$("#usage_check_add").hide();
		}
	});

	/*返回*/
	$("#goback").click(function(){
		$("#usage_check_search").show();
		$("#usage_check_add").hide();
	});
};

var showDetail = function(rid,x,y,ev){
	var rowData = $("#usage_check_list").jqGrid("getRowData" , rid);
	if (showInfectSheet)
		showInfectSheet(rowData, $("#isLeader").val());
}
