// Your turst our mission
var modelname = "维修对象";

/** 一览数据对象 */
var listdata = {};

var servicePath = "material.do";
var caseId = 0;
var isNew = false;
var keepSearchData;
var findit = function(data) {
	if (!data) { //新查询
		keepSearchData = {
			"category_id" : $("#search_category_id").val() && $("#search_category_id").val().toString(),
			"model_id":$("#search_modelname").val(),
			"serial_no":$("#search_serialno").val(),
			"sorc_no":$("#search_sorcno").val(),
			"section_id":$("#search_section_id").val(),
			"reception_time_start":$("#reception_time_start").val(),
			"reception_time_end":$("#reception_time_end").val(),
			"inline_time_start":$("#inline_time_start").val(),
			"inline_time_end":$("#inline_time_end").val(),
			"scheduled_date_start":$("#scheduled_date_start").val(),
			"scheduled_date_end":$("#scheduled_date_end").val(),
			"status" : $("#completed_set input:checked").val(),
			"arrival_plan_date_start":$("#search_arrival_plan_date_start").val(),
			"arrival_plan_date_end":$("#search_arrival_plan_date_end").val(),
			"complete_date_start":$("#search_complete_date_start").val(),
			"complete_date_end":$("#search_complete_date_end").val(),
			"esas_no":$("#search_esas_no").val(),
			"level":$("#search_level").val(),
			"direct_flg" :$("#direct_set input:checked").val(),
			"finish_time_start":$("#search_outline_time_start").val(),
			"finish_time_end":$("#search_outline_time_end").val(),
			"agreed_date_start":$("#search_agreed_date_start").val(),
			"agreed_date_end":$("#search_agreed_date_end").val(),
			"partial_order_date_start":$("#partial_order_date_start").val(),
			"partial_order_date_end":$("#partial_order_date_end").val(),
			"ocm":$("#search_ocm").val()
		};
	} else {
		keepSearchData = data;
	}
	keepSearchData["completed"] = $("#completed_set input:checked").val();
	// data[""] = 
	$.ajax({
		beforeSend : ajaxRequestType,
		async : false,
		url : servicePath + '?method=search',
		cache : false,
		data : keepSearchData,
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : search_handleComplete
	});
};
var reset = function() {
	$("#search_category_id").val("").trigger("change");
	$("#txt_modelname").val("");
	$("#search_modelname").val("");
	$("#search_serialno").val("");
	$("#search_sorcno").val("");
	$("#search_esas_no").val("");
	$("#search_ocm").val("").trigger("change");
	$("#search_level").val("").trigger("change");
	$("#search_section_id").val("").trigger("change");
	$("#reception_time_start").val("");
	$("#reception_time_end").val("");
	$("#inline_time_start").val("");
	$("#inline_time_end").val("");
	$("#search_outline_time_start").val("");
	$("#search_outline_time_end").val("");
	$("#scheduled_date_start").val("");
	$("#scheduled_date_end").val("");
	$("#completed_set input").removeAttr("checked");
	$("#completed_set input:eq(0)").attr("checked", true).trigger("change");
	$("#direct_set input").removeAttr("checked");
	$("#direct_set input:eq(0)").attr("checked", true).trigger("change");
	$("#search_arrival_plan_date_start").val("");
	$("#search_arrival_plan_date_end").val("");
	$("#search_agreed_date_start").val("");
	$("#search_agreed_date_end").val("");
	$("#partial_order_date_start").val("");
	$("#partial_order_date_end").val("");
};

$(function() {
	
	$("input.ui-button").button();
	$("#completed_set").buttonset();
	$("#direct_set").buttonset();

	$("#searchbutton").addClass("ui-button-primary");
	$("#searchbutton").click(function() {
		findit();
	});
	
	$("#resetbutton").click(function() {
		reset();
	}); 

	$("#exportbutton").click(function() {
		if (listdata.length == 0) return;
		downResult();
	}); 

	$("#searcharea span.ui-icon,#wiparea span.ui-icon").bind("click", function() {
		$(this).toggleClass('ui-icon-circle-triangle-n').toggleClass('ui-icon-circle-triangle-s');
		if ($(this).hasClass('ui-icon-circle-triangle-n')) {
			$(this).parent().parent().next().show("blind");
		} else {
			$(this).parent().parent().next().hide("blind");
		}
	});

	$("#reception_time_start, #reception_time_end, #inline_time_start, #inline_time_end, #scheduled_date_start, #scheduled_date_end, " +
			"#search_arrival_plan_date_start, #search_arrival_plan_date_end, #search_complete_date_start, #search_complete_date_end, " +
			"#search_outline_time_start, #search_outline_time_end, #search_agreed_date_start, #search_agreed_date_end, #partial_order_date_start, #partial_order_date_end").datepicker({
		showButtonPanel:true,
		dateFormat: "yy/mm/dd",
		currentText: "今天"
	});
	
	$("#search_category_id, #search_section_id, #search_ocm, #search_level").select2Buttons();
	setReferChooser($("#search_modelname"), $("#model_refer"), $("#search_category_id"));

	$("#more_condition_button").click(function(){
		$("#more_condition_button").parent().hide().next().show().next().show().parent().nextAll().show("fade")
	});
	//init null grid
	initGrid();
	
	findit();
	
	//月档案下载
	$("#month_files_downloadbutton").click(find_month_files);
});

//月档案显示详细
function find_month_files(){
	$.ajax({
		beforeSend : ajaxRequestType,
		async : false,
		url : servicePath + '?method=searchMonthFiles',
		cache : false,
		data : null,
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : search_files_handleComplete
	});	
}

function search_files_handleComplete(xhrobj, textStatus){
	var resInfo = null;
	try {
		// 以Object形式读取JSON
		eval('resInfo =' + xhrobj.responseText);
		
		var listdata = resInfo.filesList;
		//月档案详细显示
		initMonthFilesGrid(listdata);	
		
		$("#month_files_area").dialog({
				resizable : false,
				modal : true,
				title : "月档案详细一览",
				width : 424,
				buttons : {
					"确认":function(){
						$(this).dialog("close");
					},
					"返回":function(){
						$(this).dialog("close");
					}
				}
		});
		
	} catch (e) {
	}
}

function initMonthFilesGrid(listdata){
	$("#month_files_list").jqGrid({
		toppager : true,
		data : listdata,
		height : 300,
		width : 400,
		rowheight :20,
		datatype : "local",
		colNames:['文件名','文件大小'],
		colModel:[
		         {name:'file_name',index:'file_name',width:60,align:'left',
		            	formatter : function(value, options, rData){
							return "<a href='javascript:downExcel(\"" + rData['file_name'] + "\");' >" + rData['file_name'] + "</a>";
		   				}},
		         {name:'file_size',index:'file_size',width:40,align:'right'}
		         ],
        rowNum : 50,
 		toppager : false,
 		pager : "#listpager",
 		viewrecords : true,
 		gridview : true, // Speed up
		pagerpos : 'right',
		pgbuttons : true,
		pginput : false,
		recordpos : 'left',
		viewsortcols : [true, 'vertical', true] 		
	});
}

/*下载*/
var downExcel = function(file_name) {
	if ($("iframe").length > 0) {
		$("iframe").attr("src",servicePath+"?method=output&fileName="+ file_name);
	} else {
		var iframe = document.createElement("iframe");
        iframe.src = servicePath+"?method=output&fileName="+ file_name;
        iframe.style.display = "none";
        document.body.appendChild(iframe);
	}
}

function initGrid() {
	$("#list").jqGrid({
		toppager : true,
		data : [],
		height : 461,
		width : 992,
		rowheight : 23,
		datatype : "local",
		colNames : ['维修对象ID','修理单号','型号', '机身号', '委托处', '维修课室' , '当前位置','受理日期','同意日期'],
		colModel : [
			{name:'material_id',index:'material_id', hidden:true},
			{name:'sorc_no',index:'sorc_no', width:105, key: true},
			{name:'model_name',index:'model_name', width:125},
			{name:'serial_no',index:'serial_no', width:50},
			{name:'ocmName',index:'ocmName', width:65},
			{name:'section_name',index:'section_name', width:35},
			{name:'processing_position',index:'processing_position', width:35, align:'center'},
			{name:'reception_time',index:'reception_time', width:50, align:'center', formatter:'date', formatoptions:{srcformat:'Y/m/d H:i:s',newformat:'m-d'}},
			{name:'agreed_date',index:'agreed_date', width:50, align:'center', formatter:'date', formatoptions:{srcformat:'Y/m/d H:i:s',newformat:'m-d'}}
		],
		rowNum : 50,
		toppager : false,
		pager : "#listpager",
		viewrecords : true,
		caption : modelname + "一览",
		ondblClickRow : function(rid, iRow, iCol, e) {
			var data = $("#list").getRowData(rid);
			var material_id = data["material_id"];
			showPcsDetail(material_id, true);
		},
		// multiselect : true, 
		gridview : true, // Speed up
		pagerpos : 'right',
		pgbuttons : true,
		pginput : false,
		recordpos : 'left',
		viewsortcols : [true, 'vertical', true]
	});
}

/*
 * Ajax通信成功的处理
 */
function search_handleComplete(xhrobj, textStatus) {
	var listdata = null;
	eval('listdata =' + xhrobj.responseText);
	if ($("#gbox_list").length > 0) { 
		$("#list").jqGrid().clearGridData();
		$("#list").jqGrid('setGridParam', {data : listdata.list}).trigger("reloadGrid", [{current : false}]);
	} else {
		$("#list").jqGrid().clearGridData();
		$("#list").jqGrid('setGridParam', {data : listdata.list}).trigger("reloadGrid", [{current : false}]);
		// $("#list").gridResize({minWidth:1248,maxWidth:1248,minHeight:200,
		// maxHeight:900});
	}

};