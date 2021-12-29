// Your turst our mission
var modelname = "维修对象";

/** 一览数据对象 */
var listdata = {};
var lOptions = {};

var servicePath = "material.do";
var caseId = 0;
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
			"levels":$("#search_level").val() && $("#search_level").val().toString(),
			"direct_flg" :$("#direct_set input:checked").val(),
			"scheduled_expedited" : ($("#direct_rapid").attr("checked") ? "2" : "0"),
			"finish_time_start":$("#search_outline_time_start").val(),
			"finish_time_end":$("#search_outline_time_end").val(),
			"agreed_date_start":$("#search_agreed_date_start").val(),
			"agreed_date_end":$("#search_agreed_date_end").val(),
			"partial_order_date_start":$("#partial_order_date_start").val(),
			"partial_order_date_end":$("#partial_order_date_end").val(),
			"ocm_shipping_date_start":$("#search_ocm_shipping_date_start").val(),
			"ocm_shipping_date_end":$("#search_ocm_shipping_date_end").val(),
			"quotation_date_start":$("#search_quotation_date_start").val(),
			"quotation_date_end":$("#search_quotation_date_end").val(),
			"ocm":$("#search_ocm").val(),
			"fix_type":$("#search_fix_type").val(),
			"anml_exp":$("#anml_exp_set input:checked").val()
		};
	} else {
		keepSearchData = data;
	}
	keepSearchData["completed"] = $("#completed_set input:checked").val();

	var search_addition = 0;
	$("#searchAdditional > input:checked").each(function(idx, ele) {
		search_addition += parseInt(ele.value);
	});
	$("#searchform").data("search_addition", search_addition);
	keepSearchData["search_addition"] = search_addition;

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
	$("#search_ocm_shipping_date_start").val("");
	$("#search_ocm_shipping_date_end").val("");
	$("#search_quotation_date_start").val("");
	$("#search_quotation_date_end").val("");
	$("#search_fix_type").val("").trigger("change");
	
	$("#anml_exp_set input").removeAttr("checked");
	$("#anml_exp_set input:eq(0)").attr("checked", true).trigger("change");
};

var downResult = function() {
	$.ajax({
		beforeSend : ajaxRequestType,
		async : true,
		url : servicePath + '?method=export',
		cache : false,
		data : {search_addition : $("#searchform").data("search_addition")},
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : function(xhjObject) {
			var resInfo = null;
			eval("resInfo=" + xhjObject.responseText);
			if (resInfo && resInfo.filePath) {
				if ($("iframe").length > 0) {
					$("iframe").attr("src", "download.do"+"?method=output&filePath=" + resInfo.filePath + "&fileName=维修对象一览.xls");
				} else {
					var iframe = document.createElement("iframe");
		            iframe.src = "download.do"+"?method=output&filePath=" + resInfo.filePath + "&fileName=维修对象一览.xls";
		            iframe.style.display = "none";
		            document.body.appendChild(iframe);
				}
			} else {
				errorPop("下载失败！");
			}
		}
	});
}

$(function() {
	
	$("input.ui-button").button();
	$("#completed_set, #direct_set, #anml_exp_set").buttonset();

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

	$("#searchAdditional").buttonset();

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
			"#search_outline_time_start, #search_outline_time_end, #search_agreed_date_start, #search_agreed_date_end, " +
			"#partial_order_date_start, #partial_order_date_end, #search_ocm_shipping_date_start, #search_ocm_shipping_date_end," +
			"#search_quotation_date_start,#search_quotation_date_end").datepicker({
		showButtonPanel:true,
		dateFormat: "yy/mm/dd",
		currentText: "今天"
	});

	$("#search_category_id, #search_section_id, #search_ocm, #search_level, #search_fix_type").select2Buttons();
	setReferChooser($("#search_modelname"), $("#model_refer"), $("#search_category_id"));

	$("#more_condition_button").click(function(){
		$("#more_condition_button").parent().hide().next().show().next().show().parent().nextAll().show("fade")
	});
	lOptions = $("#h_lOptions").val();
	//init null grid
	initGrid();
	
	findit();

});

var colsname_process = ['section_name','processing_position','processing_position2'];
var colsname_quote = ['quotation_date_start','quotation_time'];
var colsname_partorder = ['partial_order_date','arrival_plan_date'];

function initGrid() {
	$("#list").jqGrid({
		toppager : true,
		data : [],
		height : 461,
		width : 992,
		rowheight : 23,
		datatype : "local",
		colNames : ['维修对象ID','修理单号','型号','等级', '机身号', '委托处', 
				'维修课室' , '当前位置', 'NS<br>当前位置', 
				'消毒灭菌' , '报价完成', 
				'受理日期','同意日期',
				'纳期','总组出货<br>安排','总组出货','零件订购日','入库预定日','延误','返还','环序号'],
		colModel : [
			{name:'material_id',index:'material_id', hidden:true, key: true},
			{name:'sorc_no',index:'sorc_no', width:55},
			{name:'model_name',index:'model_name', width:105},
			{name:'level',index:'level', width:20, align:'center', formatter:'select', editoptions:{value:lOptions}},
			{name:'serial_no',index:'serial_no', width:50},
			{name:'ocmName',index:'ocmName', width:45},
			{name:'section_name',index:'section_name', width:45},
			{name:'processing_position',index:'processing_position', width:45, align:'center'},
			{name:'processing_position2',index:'processing_position2', width:45, align:'center'},
			{name:'quotation_date_start',index:'quotation_date_start', width:45, align:'center', formatter:'date', formatoptions:{srcformat:'Y/m/d H:i:s',newformat:'y-m-d'}},
			{name:'quotation_time',index:'quotation_time', width:45, align:'center', formatter:'date', formatoptions:{srcformat:'Y/m/d H:i:s',newformat:'y-m-d'}},
			{name:'reception_time',index:'reception_time', width:45, align:'center', formatter:'date', formatoptions:{srcformat:'Y/m/d H:i:s',newformat:'y-m-d'}},
			{name:'agreed_date',index:'agreed_date', width:45, align:'center', formatter:'date', formatoptions:{srcformat:'Y/m/d H:i:s',newformat:'y-m-d'}},
			{name:'scheduled_date',index:'scheduled_date', width:45, align:'center', formatter:'date', formatoptions:{srcformat:'Y/m/d H:i:s',newformat:'y-m-d'}},
			{name:'scheduled_date_end',index:'scheduled_date_end', width:45, align:'center', formatter:function(a,b,row) {
				if ("9999/12/31" == a) {
					return "另行通知";
				}

				if (a) {
					var d = new Date(a);
					return ymdTextOfDate(d);
				}

				return "";
			}},
			{name:'outline_time',index:'outline_time', width:45, align:'center', formatter:'date', formatoptions:{srcformat:'Y/m/d H:i:s',newformat:'y-m-d'}},
			{name:'partial_order_date',index:'partial_order_date', width:45, align:'center', formatter:'date', formatoptions:{srcformat:'Y/m/d',newformat:'y-m-d'}},
			{name:'arrival_plan_date',index:'arrival_plan_date', width:45, align:'center', 
				formatter:function(a,b,row) {
				if ("9999-12-31" == a || "9999/12/31" == a) {
					return "未定";
				}

				if (a) {
					var d = new Date(a);
					return ymdTextOfDate(d);
				}

				return "";
			}},
			{name:'is_late',index:'is_late', width:20},
			{name:'break_back_flg',index:'break_back_flg', hidden:true},
			{name:'ring_code',index:'ring_code',  width:40,hidden:true}
		],
		rowNum : 50,
		pager : "#listpager",
		viewrecords : true,
		caption : modelname + "一览",
		ondblClickRow : function(rid, iRow, iCol, e) {
			var data = $("#list").getRowData(rid);
			var material_id = data["material_id"];
			showDetail(material_id, true);
		},
		// multiselect : true, 
		gridview : true, // Speed up
		pagerpos : 'right',
		pgbuttons : true,
		pginput : false,
		recordpos : 'left',
		viewsortcols : [true, 'vertical', true],
		gridComplete: function() {
			var jthis = $("#list");
			var dataIds = jthis.getDataIDs();
			var length = dataIds.length;
			for (var i = 0; i < length; i++) {
				var rowdata = jthis.jqGrid('getRowData', dataIds[i]);
				var break_back_flg = rowdata.break_back_flg;
				if (break_back_flg== 1 || break_back_flg == 2) {
					jthis.find("tr#" + dataIds[i] + " td").css("background-color","lightgray");
				}
			}
		}
	});
}

/*
 * Ajax通信成功的处理
 */
function search_handleComplete(xhrobj, textStatus) {
	var listdata = null;
	var $searchList = $("#list"); 
	eval('listdata =' + xhrobj.responseText);
	if ($("#gbox_list").length > 0) {
		$searchList.jqGrid().clearGridData();
		$searchList.jqGrid('setGridParam', {data : listdata.list}).trigger("reloadGrid", [{current : false}]);
	} else {
		$searchList.jqGrid().clearGridData();
		$searchList.jqGrid('setGridParam', {data : listdata.list}).trigger("reloadGrid", [{current : false}]);
		// $("#list").gridResize({minWidth:1248,maxWidth:1248,minHeight:200,
		// maxHeight:900});
	}

	var showCols = [], hideCols = [];

	if($("#searchAdditional_process").is(":checked")) {
		Array.prototype.push.apply(showCols, colsname_process)
	} else {
		Array.prototype.push.apply(hideCols, colsname_process)
	}

	if($("#searchAdditional_quote").is(":checked")) {
		Array.prototype.push.apply(showCols, colsname_quote)
	} else {
		Array.prototype.push.apply(hideCols, colsname_quote)
	}

	if($("#searchAdditional_partorder").is(":checked")) {
		Array.prototype.push.apply(showCols, colsname_partorder)
	} else {
		Array.prototype.push.apply(hideCols, colsname_partorder)
	}

	if(keepSearchData.category_id == '00000000055'){
		Array.prototype.push.apply(showCols, ['ring_code'])
	}else{
		Array.prototype.push.apply(hideCols, ['ring_code'])
	}
	$searchList.jqGrid('showCol',showCols);
	$searchList.jqGrid('hideCol',hideCols);
	$searchList.jqGrid('setGridWidth', '992');

};

function new_handleComplete(xhrobj, textStatus) {
	showList();
}

var showList = function() {
	$(document)[0].title = modelname + "一览";
	$("#searcharea").show();
	$("#listarea").show();
	$("#editarea").hide();
	$("#detailarea").hide();
};

var ticket = function(material_id) {

	var data = {
		material_id : material_id
	}
	// Ajax提交
	$.ajax({
		beforeSend: ajaxRequestType, 
		async: false, 
		url: 'material.do?method=printTicket', 
		cache: false, 
		data: data, 
		type: "post", 
		dataType: "json", 
		success: ajaxSuccessCheck, 
		error: ajaxError, 
		complete:  function(xhrobj, textStatus){
			var resInfo = null;

			try {
				// 以Object形式读取JSON
				eval('resInfo =' + xhrobj.responseText);

				if (resInfo.errors.length > 0) {
					// 共通出错信息框
					treatBackMessages(null, resInfo.errors);
				} else {
					if ($("iframe").length > 0) {
						$("iframe").attr("src", "download.do"+"?method=output&fileName="+ $("#label_model_name").text() + "-" + $("#edit_serial_no").val() +"-ticket.pdf&filePath=" + resInfo.tempFile);
					} else {
						var iframe = document.createElement("iframe");
			            iframe.src = "download.do"+"?method=output&fileName="+ $("#label_model_name").text() + "-" + $("#edit_serial_no").val() +"-ticket.pdf&filePath=" + resInfo.tempFile;
			            iframe.style.display = "none";
			            document.body.appendChild(iframe);
					}
				}
			} catch(e) {
				
			}
		}
	});
};



var showDetail = function(material_id, is_modal){
	var this_dialog = $("#detail_dialog");
	if (this_dialog.length === 0) {
		$("body.outer").append("<div id='detail_dialog'/>");
		this_dialog = $("#detail_dialog");
	}

	this_dialog.html("");
	this_dialog.hide();
	// 导入详细画面
	this_dialog.load("widget.do?method=materialDetail&material_id="+material_id , function(responseText, textStatus, XMLHttpRequest) {
		$.ajax({
			beforeSend : ajaxRequestType,
			data:{
				"id": material_id		
			},
			url : "material.do?method=getDetial",
			type : "post",
			complete : function(xhrobj, textStatus){
				var buttons = {
					"小票下载":function(){
						ticket(material_id);
					},
					"取消":function(){
						this_dialog.dialog('close');
					}
				};
				var resInfo = null;
				try {
					// 以Object形式读取JSON
					eval('resInfo =' + xhrobj.responseText);
					caseId = resInfo.caseId;
					if (caseId == 0) {
						case0();
						// var buttons = {};
					} else if (caseId == 1){
						case1();
					} else if (caseId == 2){
						case2();
						buttons = {
							"补打小票":function(){
								ticket(material_id);
							},
							"确定":function(){
								doMaterialUpdate(material_id);
							},
							"取消":function(){
								this_dialog.dialog('close');
							}
						};
					} else if (caseId == 3){
						case3();
					} else if (caseId == 4){
						case1();
						$("#distributions").hide();
						buttons = {
//							"补充记录":function(){
//								case4();
//								isNew = true;
//							},
							"补打小票":function(){
								ticket(material_id);
							},
							"确定":function(){
								doMaterialUpdate(material_id);
							},
							"取消":function(){
								this_dialog.dialog('close');
							}
						};
					}
					setLabelText(resInfo.materialForm, resInfo.partialForm, resInfo.processForm,resInfo.timesOptions, material_id);
				} catch (e) {
					alert("name: " + e.name + " message: " + e.message + " lineNumber: "
							+ e.lineNumber + " fileName: " + e.fileName);
				};
				
				this_dialog.dialog({
					position : [400, 20],
					title : "维修对象详细画面",
					width : 820,
					show : "",
					height :  'auto',
					resizable : false,
					modal : is_modal,
					buttons : buttons
				});
			
				this_dialog.show();
			}
		});
	});
};