$(function(){
	
	/** 模块名 */
	var modelname = "零件订购现状";
	/** 服务器处理路径 */
	var servicePath = "partial_order_record.do";

	// 已经按照现行检索条件查询过
	var found = [false,false,false];

	var FOUND_ECHELON=0,FOUND_LEVELMODEL=1,FOUND_PARTIAL=2;

	var initView = function() {
		$("input.ui-button").button();  

		$("#reportbobutton").disable();

		$("#search_category_id, #search_level, #search_echelon").select2Buttons();
		$("#searchbutton").addClass("ui-button-primary");

	    setReferChooser($("#search_modelname"), $("#model_refer"), $("#txt_modelname"));

	    /*为每一个匹配的元素的特定事件绑定一个事件处理函数*/
	    $("#searcharea span.ui-icon").bind("click", function() {
			$(this).toggleClass('ui-icon-circle-triangle-n').toggleClass('ui-icon-circle-triangle-s');
			if ($(this).hasClass('ui-icon-circle-triangle-n')) {
				$(this).parent().parent().next().show("blind");
			} else {
				$(this).parent().parent().next().hide("blind");
			}
		}); 
		$("#search_agreed_date_start,#search_agreed_date_end,#search_order_date_start, #search_order_date_end,#search_outline_date_start,#search_outline_date_end,#search_reach_date_start,#search_reach_date_end, #search_arrival_plan_date_start, #search_arrival_plan_date_end").datepicker({
			showButtonPanel:true,
			dateFormat: "yy/mm/dd",
			currentText: "今天"
		});
	   
		$("#resetbutton").click(function() {
			$("#search_category_id").val("").trigger("change");
			$("#search_modelname").val("");
			$("#txt_modelname").val("");
			$("#search_level").val("").trigger("change");
			$("#search_agreed_date_start").val("");
			$("#search_agreed_date_end").val("");
			$("#search_outline_date_start").val("");
			$("#search_outline_date_end").val("");
			$("#search_echelon").val("").trigger("change");
			$("#search_order_date_start").val("");
			$("#search_order_date_end").val("");
			$("#search_arrival_plan_date_start").val("");
			$("#search_arrival_plan_date_end").val("");
			$("#search_reach_date_start").val("");
			$("#search_reach_date_end").val("");
			$("#search_partial_code").val("");
			
		});
		
		$("#searchbutton").click(function(){
			found = [false,false,false];
			if ($("#echelon_contents").is(":visible")) {
				findEchelon(true);
			} else if ($("#level_model_contents").is(":visible")) {
				findLevelModel(true);
			} else if ($("#partial_contents").is(":visible")) {
				findPartial(true);
			}
			$("#reportbobutton").enable();
		});

		$("#editbobutton").click(function(){
			var rowids = $("#list").jqGrid("getGridParam", "selarrrow");
			var data = $("#list").getRowData(rowids[0]);
			var material_id = data["material_id"];
			edit_bo(material_id);
		});

		$("#echelon_button").click(function(){
			$("#level_model_contents").hide();
			$("#echelon_contents").show();
			$("#partial_contents").hide();
			if(!found[FOUND_ECHELON])
				findEchelon(false);
		});

		$("#level_model_button").click(function(){
			$("#echelon_contents").hide();
			$("#level_model_contents").show();
			$("#partial_contents").hide();
			if(!found[FOUND_LEVELMODEL])
				findLevelModel(false);
		});

		$("#partial_button").click(function(){
			$("#echelon_contents").hide();
			$("#level_model_contents").hide();
			$("#partial_contents").show();
			if(!found[FOUND_PARTIAL])
				findPartial(false);
		});

		$("#reportbobutton").click(exportReport);
		
		$("#partialbo_files_downloadbutton").click(find_partialbo_files);

		$("#level_model_contents").hide();
		$("#echelon_contents").hide();
		$("#partial_contents").show();
		found = [false,false,false];
		findPartial(true);
	}

//BO分析表显示详细
function find_partialbo_files(){
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
};

function search_files_handleComplete(xhrobj, textStatus){
	var resInfo = null;
	try {
		// 以Object形式读取JSON
		eval('resInfo =' + xhrobj.responseText);
		
		var listdata = resInfo.filesList;
		//BO分析表详细显示
		initMonthFilesGrid(listdata);	
		
		$("#month_files_area").dialog({
				resizable : false,
				modal : true,
				title : "BO分析表一览",
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
};

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
};

var searchData = {};

var setSearchData = function() {
	searchData = {
		"category_id" : $("#search_category_id").val() ? $("#search_category_id").val().toString() : "",
		"model_id" : $("#search_modelname").val(),
		"level" : $("#search_level").val(),
		"agreed_date_start" : $("#search_agreed_date_start").val(),
		"agreed_date_end" : $("#search_agreed_date_end").val(),
		"finish_date_start" : $("#search_outline_date_start").val(),
		"finish_date_end" : $("#search_outline_date_end").val(),
		"echelon" : $("#search_echelon").val()?$("#search_echelon").val().toString():"",
		"order_date_start": $("#search_order_date_start").val(),
		"order_date_end": $("#search_order_date_end").val(),
		"arrival_plan_date_start":$("#search_arrival_plan_date_start").val(),
		"arrival_plan_date_end":$("#search_arrival_plan_date_end").val(),
		"arrival_date_start":$("#search_reach_date_start").val(),
		"arrival_date_end":$("#search_reach_date_end").val(),
		"partial_code":$("#search_partial_code").val(),
		"neo" : "1"
	};
}

/*检索按钮事件 查询零件信息*/
var findPartial = function(neo_search) {
	if (neo_search) setSearchData(); else searchData.neo = "0";

	$.ajax({
		beforeSend : ajaxRequestType,
		async : true,
		url : servicePath + '?method=searchPartial',
		cache : false,
		data : searchData,
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : search_handleComplete
	});
};
var report_handleComplete = function(xhjObject) {
	var resInfo = null;
	eval("resInfo=" + xhjObject.responseText);
	if (resInfo && resInfo.fileName) {
		if ($("iframe").length > 0) {
			$("iframe").attr("src", "download.do" + "?method=output&filePath=" + resInfo.filePath+"&fileName="+resInfo.fileName);
		} else {
			var iframe = document.createElement("iframe");
			iframe.src = "download.do" + "?method=output&filePath=" + resInfo.filePath+"&fileName="+resInfo.fileName;
			iframe.style.display = "none";
			document.body.appendChild(iframe);
		}
	} else {
		alert("文件导出失败！"); // TODO dialog
	}
}
var exportReport = function() {
	$.ajax({
		beforeSend : ajaxRequestType,
		async : false,
		url : servicePath + '?method=makeReport',
		cache : false,
		data : searchData,
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : report_handleComplete
	});
}

/*检索按钮事件 查询零件信息*/
var findLevelModel = function(neo_search) {
	if (neo_search) setSearchData(); else searchData.neo = "0";
	
	$.ajax({
		beforeSend : ajaxRequestType,
		async : true,
		url : servicePath + '?method=searchLevelModel',
		cache : false,
		data : searchData,
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : search_handleComplete
	});
};
/*
 * Ajax通信成功的处理
 */
function search_handleComplete(xhrobj, textStatus) {

	var resInfo = null;
	try {
		// 以Object形式读取JSON
		eval('resInfo =' + xhrobj.responseText);
		if (resInfo.errors && resInfo.errors.length > 0) {
			// 共通出错信息框
			treatBackMessages("#editarea", resInfo.errors);
		} else {
			if (resInfo.partials) {
				partial_field_list(resInfo.partials);
				found[FOUND_PARTIAL] = true;
			}
			if (resInfo.echelons) {
				echelon_filed_list(resInfo.echelons);
				found[FOUND_ECHELON] = true;
			}
			if (resInfo.levelModels) {
				levelmodel_filed_list(resInfo.levelModels);
				found[FOUND_LEVELMODEL] = true;
			}
			$("#periodMessage").text(resInfo.periodMessage);
		}
	} catch (e) {
		alert("name: " + e.name + " message: " + e.message + " lineNumber: "
				+ e.lineNumber + " fileName: " + e.fileName);
	};
};

/*检索按钮事件 查询零件信息*/
var findEchelon = function(neo_search) {
	if (neo_search) setSearchData(); else searchData.neo = "0";
	
	$.ajax({
		beforeSend : ajaxRequestType,
		async : true,
		url : servicePath + '?method=searchEchelon',
		cache : false,
		data : searchData,
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : search_handleComplete
	});
};

/*jqgrid表格  等级模型*/
function levelmodel_filed_list(finished_list){
	if ($("#gbox_list").length > 0) {
		$("#list").jqGrid().clearGridData();
		$("#list").jqGrid('setGridParam',{data:finished_list}).trigger("reloadGrid", [{current:false}]);
	} else {
		$("#list").jqGrid({
			data:finished_list,
			height: 461,
			width: 1248,
			rowheight: 23,
			datatype: "local",
			colNames : ['等级','型号', '型号名','梯队','期间受理量','期间同意量', '期间订购数', 'BO发生数', 'BO发生率',
				'3天BO发生数', '3天BO发生率', '拉动台数安全值', '建议拉动台数', '消耗速率'],
			colModel : [{
						name: 'level',
						index: 'level',
						align : 'right',
						width : 30,
						formatter : 'select',
						editoptions : {value:"1:S1;2:S2;3:S3"}
					}, {
						name:'model_id',
						index:'model_id',
						hidden:true
					}, {
						name : 'model_name',
						index : 'model_name',
						width : 125,
						align : 'left'
					}, {
						name : 'echelon',
						index : 'echelon',
						width : 40,
						align : 'center',
						formatter : 'select',
						editoptions : {value:"1:第一梯队;2:第二梯队;3:第三梯队;4:第四梯队"}
					}, {
						name : 'recept_num',
						index : 'recept_num',
						width : 50,
						align : 'right',
						formatter : 'integer',
						sorttype : 'integer'
					}, {
						name : 'agreed_num',
						index : 'agreed_num',
						width : 50,
						align : 'right',
						formatter : 'integer',
						sorttype : 'integer'
					}, {
						name : 'order_num',
						index : 'order_num',
						width : 50,
						align : 'right',
						formatter : 'integer',
						sorttype : 'integer'
					}, {
						name : 'bo_num',
						index : 'bo_num',
						width : 50,
						align : 'right',
						formatter : 'integer',
						sorttype : 'integer'
					}, {
						name : 'bo_rate',
						index : 'bo_rate',
						width : 50,
						align : 'right',
						formatter : 'number',
						sorttype : 'number',
						formatoptions: {
					        	suffix: ' %',
							defaultValue: ' - '
					        }
					}, {
						name : 'bo3days_num',
						index : 'bo3days_num',
						width : 50,
						align : 'right',
						formatter : 'integer',
						sorttype : 'integer'
					}, {
						name : 'bo3days_rate',
						index : 'bo3days_rate',
						width : 50,
						align : 'right',
						formatter : 'number',
						sorttype : 'number',
						formatoptions: {
					        	suffix: ' %',
							defaultValue: ' - '
					        }
					}, {
						name : 'base_setting',
						index : 'base_setting',
						width : 50,
						align : 'right',
						formatter : 'integer',
						sorttype : 'integer',
						formatoptions: {
							defaultValue: ' - '
					        }
					}, {
						name : 'ld_num',
						index : 'ld_num',
						width : 50,
						align : 'right',
						formatter : 'integer',
						sorttype : 'integer',
						formatoptions: {
							defaultValue: ' - '
					        }
					},  {
						name : 'turnround_rate',
						index : 'turnround_rate',
						width : 35,
						align : 'right',
						formatter : 'number',
						sorttype : 'number',
						formatoptions: {
					        suffix: ' %',
							defaultValue: ' - '
						}
					}
			],
			rowNum : 50,
			rownumbers : true,
			toppager: false,
			pager: "#listpager",
			viewrecords: true,
			hidegrid : false,
			gridview: true,
			pagerpos: 'right',
			pgbuttons: true,
			pginput: false,
			recordpos: 'left',
			ondblClickRow : function(rid, iRow, iCol, e) {
				var data = $("#list").getRowData(rid);
				var level = data["level"];
				var model_id = data["model_id"];
				var model_name = data["model_name"];
				$("#search_level").val(level).trigger("change");;
				$("#search_modelname").val(model_id);
				$("#txt_modelname").val(model_name);
				// $("#partial_button").click();	
			},
			viewsortcols : [true,'vertical',true],
			gridComplete : function() {
				var IDS = $("#list").getDataIDs();
				// 当前显示多少条
				var length = IDS.length;
				var $exd_list = $("#list");
				for (var i = 0; i < length; i++) {
					// 从上到下获取一条信息
					var rowData = $exd_list.jqGrid('getRowData', IDS[i]);
					var turnround_rate = parseInt(rowData["turnround_rate"]);
					if (turnround_rate > 100 || turnround_rate < 20) {
						$exd_list.find("tr#" + IDS[i] + " td[aria\\-describedby='list_turnround_rate']").addClass("ui-state-active");
					}

				}
			}			
		});
	}
};

/*双击弹出画面*/
var edit_bo = function(material_id, rid) {
     
	$("#process_dialog").hide();
	// 导入零件详细画面

	var mp_page = "widgets/partial_order_record_lmdetail.html";

	$("#process_dialog").load(mp_page,function(){
		$("#edit_occur_times").select2Buttons();
		$("#process_dialog").dialog({
			title : "零件明细信息",
			width : 800,
			show : "blind",
			height : 'auto' ,
			resizable : false,
			modal : true,
			minHeight : 200,
			buttons : {
			"确定": function(){
					$("#process_dialog").dialog('close');
				}
			}
		});
		
		$("#process_dialog").show();
	});
};
/*梯队*/
function echelon_filed_list(echelon_list){
	if ($("#gbox_echelon_list").length > 0) {
		$("#echelon_list").jqGrid().clearGridData();
		$("#echelon_list").jqGrid('setGridParam',{data:echelon_list}).trigger("reloadGrid", [{current:false}]);
	} else {
		$("#echelon_list").jqGrid({
			data:echelon_list,
			height: 461,
			width: 1248,
			rowheight: 23,
			datatype: "local",
			colNames : ['梯队','期间受理量','期间同意量', '期间订购数', 'BO发生数', 'BO发生率', '3天BO发生数', '3天BO发生率', '拉动台数安全值合计', '建议拉动台数合计', '匹配率'],
			colModel : [{
						name : 'echelon',
						index : 'echelon',
						width : 50,
						align : 'center',
						formatter : 'select',
						editoptions : {value:"1:第一梯队;2:第二梯队;3:第三梯队;4:第四梯队"}
					},
					{
						name : 'recept_num',
						index : 'recept_num',
						width : 50,
						align : 'right',
						formatter : 'integer',
						sorttype : 'integer'
					}, {
						name : 'agreed_num',
						index : 'agreed_num',
						width : 50,
						align : 'right',
						formatter : 'integer',
						sorttype : 'integer'
					}, {
						name : 'order_num',
						index : 'order_num',
						width : 50,
						align : 'right',
						formatter : 'integer',
						sorttype : 'integer'
					}, {
						name : 'bo_num',
						index : 'bo_num',
						width : 50,
						align : 'right',
						formatter : 'integer',
						sorttype : 'integer'
					}, {
						name : 'bo_rate',
						index : 'bo_rate',
						width : 50,
						align : 'right',
						formatter : 'number',
						sorttype : 'number',
						formatoptions: {
					        	suffix: ' %',
							defaultValue: ' - '
					        }
					}, {
						name : 'bo3days_num',
						index : 'bo3days_num',
						width : 50,
						align : 'right',
						formatter : 'integer',
						sorttype : 'integer'
					}, {
						name : 'bo3days_rate',
						index : 'bo3days_rate',
						width : 50,
						align : 'right',
						formatter : 'number',
						sorttype : 'number',
						formatoptions: {
						suffix: ' %',
						defaultValue: ' - '
					        }
					}, {
						name : 'base_setting',
						index : 'base_setting',
						width : 50,
						align : 'right',
						formatter : 'integer',
						sorttype : 'integer',
						formatoptions: {
							defaultValue: ' - '
					        }
					}, {
						name : 'ld_num',
						index : 'ld_num',
						width : 50,
						align : 'right',
						formatter : 'integer',
						sorttype : 'integer',
						formatoptions: {
							defaultValue: ' - '
					        }
					},  {
						name : 'turnround_rate',
						index : 'turnround_rate',
						width : 35,
						align : 'right',
						formatter : 'number',
						sorttype : 'number',
						formatoptions: {
					        	suffix: ' %',
							defaultValue: ' - '
					        }
					}

			],
			rowNum : 50,
			toppager: false,
			pager: "#echelon_listpager",
			viewrecords: true,
			hidegrid : false,
			gridview: true,
			pagerpos: 'right',
			pgbuttons: true,
			pginput: false,
			recordpos: 'left',
			ondblClickRow : function(rid, iRow, iCol, e, a, b) {
				var data = $("#echelon_list").getRowData(rid);
				var echelon = data["echelon"];
				$("#search_echelon").val(echelon).trigger("change");
				// $("#level_model_button").click();
			},
			viewsortcols : [true,'vertical',true],
			gridComplete : function() {
				var IDS = $("#echelon_list").getDataIDs();
				// 当前显示多少条
				var length = IDS.length;
				var $exd_list = $("#echelon_list");
				for (var i = 0; i < length; i++) {
					// 从上到下获取一条信息
					var rowData = $exd_list.jqGrid('getRowData', IDS[i]);
					var turnround_rate = parseInt(rowData["turnround_rate"]);
					if (turnround_rate < 70) {
						$exd_list.find("tr#" + IDS[i] + " td[aria\\-describedby='echelon_list_turnround_rate']").addClass("ui-state-active");
					}

				}
			}			
		});
	}
}
/*零件*/
function partial_field_list(partial_list){
	if ($("#gbox_partial_button_list").length > 0) {
		$("#partial_button_list").jqGrid().clearGridData();
		$("#partial_button_list").jqGrid('setGridParam',{data:partial_list}).trigger("reloadGrid", [{current:false}]);
	} else {
		$("#partial_button_list").jqGrid({
			data:partial_list,
			height: 478,
			width: 1248,
			rowheight: 23,
			datatype: "local",
			colNames : ['零件编码','零件名称','期间订购数', 'BO发生数', 'BO发生率', '3天BO发生数', '3天BO发生率', '安全库存', '建议拉动台数', '消耗速率'],
			colModel : [{
						name : 'partial_code',
						index : 'partial_code',
						width : 40,
						align : 'left'
					},{
						name : 'partial_name',
						index : 'partial_name',
						width : 160,
						align : 'left'
					},{
						name : 'order_num',
						index : 'order_num',
						width : 40,
						align : 'right',
						formatter : 'integer',
						sorttype : 'integer'
					}, {
						name : 'bo_num',
						index : 'bo_num',
						width : 40,
						align : 'right',
						formatter : 'integer',
						sorttype : 'integer'
					},{
						name : 'bo_rate',
						index : 'bo_rate',
						width : 40,
						align : 'right',
						formatter : 'number',
						sorttype : 'number',
						formatoptions: {
							suffix: ' %',
							defaultValue: ' - '
					    }
					}, {
						name : 'bo3days_num',
						index : 'bo3days_num',
						width : 40,
						align : 'right',
						formatter : 'integer',
						sorttype : 'integer'
					}, {
						name : 'bo3days_rate',
						index : 'bo3days_rate',
						width : 40,
						align : 'right',
						formatter : 'number',
						sorttype : 'number',
						formatoptions: {
					        suffix: ' %',
							defaultValue: ' - '
					    }
					}, {
						name : 'base_setting',
						index : 'base_setting',
						width : 40,
						align : 'right',
						formatter : 'integer',
						sorttype : 'integer',
						formatoptions: {
							defaultValue: ' - '
					        }
					}, {
						name : 'base_num',
						index : 'base_num',
						width : 40,
						align : 'right',
						formatter : 'integer',
						sorttype : 'integer',
						formatoptions: {
							defaultValue: ' - '
					        }
					},  {
						name : 'turnround_rate',
						index : 'turnround_rate',
						width : 35,
						align : 'right',
						formatter : 'number',
						sorttype : 'number',
						formatoptions: {
					        suffix: ' %',
							defaultValue: ' - '
					    }
					}

			],
			rowNum : 50,
			rownumbers : true,
			toppager: false,
			pager: "#partial_button_listpager",
			viewrecords: true,
			hidegrid : false,
			gridview: true,
			pagerpos: 'right',
			pgbuttons: true,
			pginput: false,
			recordpos: 'left',
			ondblClickRow : function(rid, iRow, iCol, e) {
				showChart();
			},
			viewsortcols : [true,'vertical',true],
			gridComplete : function() {
				var IDS = $("#partial_button_list").getDataIDs();
				// 当前显示多少条
				var length = IDS.length;
				var $exd_list = $("#partial_button_list");
				for (var i = 0; i < length; i++) {
					// 从上到下获取一条信息
					var rowData = $exd_list.jqGrid('getRowData', IDS[i]);
					var turnround_rate = parseInt(rowData["turnround_rate"]);
					if (turnround_rate > 100 || turnround_rate < 20) {
						$exd_list.find("tr#" + IDS[i] + " td[aria\\-describedby='partial_button_list_turnround_rate']").addClass("ui-state-active");
					}

				}
			}			
		});
	}
}

function showChart(){
	return; // DUMMY
	var chart1 = new Highcharts.Chart({
		chart:{
			renderTo : 'chart_container'
		},
		title: {
			text: ''
		},
		credits:{
			position: {              
				align: 'left',       
				x: -100                           
			}
		},
		xAxis: {
			categories: ['146P 7月','　','　','　','8月','　','　','　','9月','　','　','　','10月','　','　','　','11月','　','　','　','12月','　']
		},
		yAxis: {
			title: {
				text: ''
			},
			min:0,
			plotLines: [{
				value: 0,
				width: 1
			}],
			labels : {formatter : function(){return this.value + "%";}},
			lineWidth:1,//Y轴宽度
			tickWidth:1,//刻度尺宽度
			tickLength:5,//刻度尺长度
			plotBands: [{ // low
				from: 0,
				to: 20,
				color: 'rgba(68, 170, 213, .8)'
			} , {// high
				from: 100,
				to : 200,
				color: 'rgba(218, 67, 67, .6)'
			}]
		},
		series: [{
			name: '消耗速率',
			data: [null,null,null,null,null,null,null,null,null,null]
		}]
	});

	$("#partial_order_chart").dialog({
		title:'GH143200 零件消耗速率',
		width:'auto',
		height:'auto',
		resizable:false,
		modal:true,
		buttons:{
			"关闭":function(){
				 $("#partial_order_chart").dialog("close");
			}
		}
	});
}

initView();

});

/*下载*/
function downExcel(file_name){
	if ($("iframe").length > 0) {
		$("iframe").attr("src","download.do?method=output&fileName=" + file_name + "&from=report_partial_bo");
	} else {
		var iframe = document.createElement("iframe");
        iframe.src = "download.do?method=output&fileName=" + file_name + "&from=report_partial_bo";
        iframe.style.display = "none";
        document.body.appendChild(iframe);
	}
};