var partial_monitor_list = {};

$(function(){

	/** 模块名 */
	var modelname = "零件订购现状";
	/** 服务器处理路径 */
	var servicePath = "partial_monitor.do";

	var initView = function() {
		$("input.ui-button").button();  

		$("#reportbobutton").disable();

		$("#search_category_id, #search_level, #search_echelon").select2Buttons();
		$("#searchbutton").addClass("ui-button-primary");
		
		$("#filter_l_high,#filter_l_low").keydown(function(evt){
			if(!((evt.keyCode >= 48 && evt.keyCode <= 57) || evt.keyCode==8 ||  evt.keyCode==46 || evt.keyCode==37 || evt.keyCode==39)){
				return false;
			}
		});

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

		$("#editbutton").click(function() {
			var $partial_code = $("#partial_button_list tr.ui-state-highlight td[aria\\-describedby='partial_button_list_partial_code']");
			if ($partial_code.length == 0) return;
			var partial_code = $partial_code.text();
			var partial_id = $partial_code.prev("td").text();
			var date_start = $("#search_order_date_start").val() || $("#search_order_date_start").attr("org");
			var date_end = $("#search_order_date_end").val() || $("#search_order_date_end").attr("org");

			change_base_line(partial_id, partial_code, date_start, date_end, function(){
				$("#partial_button_list tr.ui-state-highlight").addClass("baselined");
			});
			$("#edit_partial_base_line_value").hide();
//			var form = $("#searchform")[0];
//			form.edit_partial_code.value = partial_code;
//			form.submit();
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
			findPartial(true);
		});

		$(".top_stick").click(function() {
			if (this.value == "通常显示") {
				$(this).addClass("up-active");
				this.value = "置顶显示";
			} else {
				$(this).removeClass("up-active");
				this.value = "通常显示";
			}
		})

		partial_field_list([]);

		$("#colchooser").buttonset();

		var showColumns = function (){

			var shownames = [];
			var hidenames = [];
			if ($("#colchooser_sorcwh").attr("checked")) {
				shownames = shownames.concat(['order_num1','sorcwh_foreboard_count','order_num4','osh_turnround_rate']);
			} else {
				hidenames = hidenames.concat(['order_num1','sorcwh_foreboard_count','order_num4','osh_turnround_rate']);
			}

			if ($("#colchooser_wh2p").attr("checked")) {
				shownames = shownames.concat(['order_num3','wh2p_foreboard_count','consumable_foreboard_count','order_num5', 'wh2p_turnround_rate']);
			} else {
				hidenames = hidenames.concat(['order_num3','wh2p_foreboard_count','consumable_foreboard_count','order_num5', 'wh2p_turnround_rate']);
			}

			if ($("#colchooser_ogz").attr("checked")) {
				shownames = shownames.concat(['order_num2','ogz_foreboard_count','order_num7','ogz_turnround_rate']);
			} else {
				hidenames = hidenames.concat(['order_num2','ogz_foreboard_count','order_num7','ogz_turnround_rate']);
			}

			$("#partial_button_list").jqGrid('showCol', shownames)
			$("#partial_button_list").jqGrid('hideCol', hidenames)
			$("#partial_button_list").jqGrid('setGridWidth', '1248');
		};

		$("#colchooser input:checkbox").click(showColumns);

		// fill_out_list([]);
		findPartial(true);
	}

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

		"l_high":$("#filter_l_high").val(),
		"l_low":$("#filter_l_low").val(),
		"top_stick_high":$(".top_stick[name=higher]").val() == "置顶显示",
		"top_stick_low":$(".top_stick[name=lower]").val() == "置顶显示",
		"top_stick_bo_lt":$(".top_stick[name=ltlater]").val() == "置顶显示",

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
				partial_monitor_list = resInfo.partials;
				partial_field_list(resInfo.partials);
				// fill_out_list(resInfo.outPartials);
			}
			$("#periodMessage").text(resInfo.periodMessage);
		}
	} catch (e) {
		alert("name: " + e.name + " message: " + e.message + " lineNumber: "
				+ e.lineNumber + " fileName: " + e.fileName);
	};
};

/*零件*/
function partial_field_list(partial_list){
	if ($("#gbox_partial_button_list").length > 0) {
		$("#partial_button_list").jqGrid().clearGridData();
		$("#partial_button_list").jqGrid('setGridParam',{data:partial_list}).trigger("reloadGrid", [{current:false}]);
	} else {
		$("#partial_button_list").jqGrid({
			data:partial_list,
			height: 576,
			width: 1248,
			rowheight: 23,
			datatype: "local",
			colNames : ['', '零件编码','零件名称','单价','消耗品'
			,'SORCWH<br>期间补充数', 'SORCWH<br>安全库存', '期间折算<BR>基准值', 'SORCWH<br>消耗速率'
			,'WH2P<br>期间补充数', 'WH2P<br>安全库存', '消耗品仓库<br>安全库存', '期间折算<BR>基准值', 'WH2P<br>消耗速率'
			,'OGZ<br>期间补充数', 'OGZ<br>安全库存', '期间折算<BR>基准值', 'OGZ<br>消耗速率'
			,'期间补充数', '安全库存', '期间折算<BR>基准值', '总计<BR>消耗速率'],
			colModel : [{
					name : 'partial_id',
					index : 'partial_id',
					hidden : true
				},{
					name : 'partial_code',
					index : 'partial_code',
					width : 40,
					align : 'left'
				},{
					name : 'partial_name',
					index : 'partial_name',
					width : 120,
					align : 'left',
					hidden:true
				},  {
					name : 'price',
					index : 'price',
					width : 35,
					align : 'right',
					formatter : 'number',
					sorttype : 'number',
					formatoptions: {
						prefix: '$ ',
						defaultValue: ' - '
				    }
				},{
					name : 'consumable_flg',
					index : 'consumable_flg',
					width : 20,
					align : 'center',
					formatter : 'select',
					editoptions:{value:"0:;1:消耗品"}
				}
				
				
				,{
					name : 'order_num1',
					index : 'order_num1',
					width : 40,
					align : 'right',
					formatter : 'integer',
					sorttype : 'integer',
					hidden:true,
					formatoptions: {
						defaultValue: ' - ', thousandsSeparator: ",", prefix: ""
				    }
				}, {
					name : 'sorcwh_foreboard_count',
					index : 'sorcwh_foreboard_count',
					width : 40,
					align : 'right',
					formatter : 'integer',
					sorttype : 'integer',
					hidden:true,
					formatoptions: {
						defaultValue: ' - '
				        }
				}, {
					name : 'order_num4',
					index : 'order_num4',
					width : 40,
					align : 'right',
					formatter : 'integer',
					sorttype : 'integer',
					formatoptions: {
						defaultValue: ' - ', thousandsSeparator: ",", prefix: ""
				    }
				},  {
					name : 'osh_turnround_rate',
					index : 'osh_turnround_rate',
					width : 35,
					align : 'right',
					formatter : 'number',
					sorttype : 'number',
					hidden:true,
					formatoptions: {
				        suffix: ' %',
						defaultValue: ' - '
				    }
				}
				
				
				,{
					name : 'order_num3',
					index : 'order_num3',
					width : 40,
					align : 'right',
					formatter : 'integer',
					sorttype : 'integer',
					formatoptions: {
						defaultValue: ' - ', thousandsSeparator: ",", prefix: ""
				    }
				}, {
					name : 'wh2p_foreboard_count',
					index : 'wh2p_foreboard_count',
					width : 40,
					align : 'right',
					formatter : 'integer',
					sorttype : 'integer',
					hidden:true,
					formatoptions: {
						defaultValue: ' - '
				        }
				}, {
					name : 'consumable_foreboard_count',
					index : 'consumable_foreboard_count',
					width : 40,
					align : 'right',
					formatter : 'integer',
					sorttype : 'integer',
					hidden:true,
					formatoptions: {
						defaultValue: ' - '
				        }
				}, {
					name : 'order_num5',
					index : 'order_num5',
					width : 40,
					align : 'right',
					formatter : 'integer',
					sorttype : 'integer',
					hidden:true,
					formatoptions: {
						defaultValue: ' - ', thousandsSeparator: ",", prefix: ""
				    }
				},  {
					name : 'wh2p_turnround_rate',
					index : 'wh2p_turnround_rate',
					width : 35,
					align : 'right',
					formatter : 'number',
					sorttype : 'number',
					hidden:true,
					formatoptions: {
				        suffix: ' %',
						defaultValue: ' - '
				    }
				}

				
				,{
					name : 'order_num2',
					index : 'order_num2',
					width : 40,
					align : 'right',
					formatter : 'integer',
					sorttype : 'integer',
					hidden:true,
					formatoptions: {
						defaultValue: ' - ', thousandsSeparator: ",", prefix: ""
				    }
				}, {
					name : 'ogz_foreboard_count',
					index : 'ogz_foreboard_count',
					width : 40,
					align : 'right',
					formatter : 'integer',
					sorttype : 'integer',
					hidden:true,
					formatoptions: {
						defaultValue: ' - '
				        }
				}, {
					name : 'order_num7',
					index : 'order_num7',
					width : 40,
					align : 'right',
					formatter : 'integer',
					sorttype : 'integer',
					hidden:true,
					formatoptions: {
						defaultValue: ' - ', thousandsSeparator: ",", prefix: ""
				    }
				},  {
					name : 'ogz_turnround_rate',
					index : 'ogz_turnround_rate',
					width : 35,
					align : 'right',
					formatter : 'number',
					sorttype : 'number',
					hidden:true,
					formatoptions: {
				        suffix: ' %',
						defaultValue: ' - '
				    }
				}

				,{
					name : 'order_num',
					index : 'order_num',
					width : 40,
					align : 'right',
					formatter : 'integer',
					sorttype : 'integer',
					formatoptions: {
						defaultValue: ' - ', thousandsSeparator: ",", prefix: ""
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
						defaultValue: ' - ', thousandsSeparator: ",", prefix: ""
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
			rowNum : 200,
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
				getChartData(rid);
			},
			viewsortcols : [true,'vertical',true],
			gridComplete : function() {
				var isEight = $("#periodMessage").text().indexOf("间隔 8 个工作日") >= 0; 
				var IDS = $("#partial_button_list").getDataIDs();
				// 当前显示多少条
				var length = IDS.length;
				var $partial_button_list_container = $("#partial_button_list").parent();
				var $exd_list = $("#partial_button_list").detach();
				var hval = $("#filter_l_high").val();
				var lval = $("#filter_l_low").val();
				for (var i = 0; i < length; i++) {
					// 从上到下获取一条信息
					var rowData = $exd_list.jqGrid('getRowData', IDS[i]);
					var order_num = parseInt(rowData["order_num"]);
					var base_setting = parseInt(rowData["base_setting"]);
					if (base_setting && base_setting > 0) {
						var turnround_rate = parseInt(rowData["turnround_rate"]);
						var osh_turnround_rate = parseInt(rowData["osh_turnround_rate"]);
						var wh2p_turnround_rate = parseInt(rowData["wh2p_turnround_rate"]);
						var ogz_turnround_rate = parseInt(rowData["ogz_turnround_rate"]);
						var $worstr = $exd_list.find("tr#" + IDS[i]);

						if (turnround_rate > hval || turnround_rate < lval) {
							$worstr.addClass("tofix");
							$worstr.find("td[aria\\-describedby='partial_button_list_turnround_rate']").addClass("up-active");
						} if (osh_turnround_rate > hval || osh_turnround_rate < lval) {
							$worstr.addClass("tofix");
							$worstr.find("td[aria\\-describedby='partial_button_list_osh_turnround_rate']").addClass("up-active-less");
						} if (wh2p_turnround_rate > hval || wh2p_turnround_rate < lval) {
							$worstr.addClass("tofix");
							$worstr.find("td[aria\\-describedby='partial_button_list_wh2p_turnround_rate']").addClass("up-active-less");
						} if (ogz_turnround_rate > hval || ogz_turnround_rate < lval) {
							$worstr.addClass("tofix");
							$worstr.find("td[aria\\-describedby='partial_button_list_ogz_turnround_rate']").addClass("up-active-less");
						}

						if ((isEight > 0) && order_num && order_num > base_setting) {
							$exd_list.find("tr#" + IDS[i]).addClass("periodover");
						}
					} else {
						if (order_num && order_num > 0) {
							$exd_list.find("tr#" + IDS[i]).addClass("outsetting");
						} else {
							$exd_list.find("tr#" + IDS[i]).addClass("slept");
						}
						if (base_setting === "0") {
							$exd_list.find("tr#" + IDS[i] + " td[aria\\-describedby='partial_button_list_turnround_rate']").addClass("up-active");
						}
					}
				}
				$partial_button_list_container.html($exd_list);
			}
		});
	}
}
/*
function fill_out_list(out_list){
	if ($("#gbox_out_list").length > 0) {
		$("#out_list").jqGrid().clearGridData();
		$("#out_list").jqGrid('setGridParam',{data:out_list}).trigger("reloadGrid", [{current:false}]);
	} else {
		$("#out_list").jqGrid({
			data:out_list,
			height: 478,
			width: 1248,
			rowheight: 23,
			datatype: "local",
			colNames : ['零件编码','零件名称','期间订购数', 'BO发生数', 'BO发生率', '3天BO发生数', '3天BO发生率', '安全库存', '期间折算基准值', '消耗速率'],
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
			pager: "#out_listpager",
			viewrecords: true,
			hidegrid : false,
			gridview: true,
			pagerpos: 'right',
			pgbuttons: true,
			pginput: false,
			recordpos: 'left',
			viewsortcols : [true,'vertical',true],
			gridComplete : function() {
				var IDS = $("#out_list").getDataIDs();
				// 当前显示多少条
				var length = IDS.length;
				var $exd_list = $("#out_list");
				for (var i = 0; i < length; i++) {
					// 从上到下获取一条信息
					var rowData = $exd_list.jqGrid('getRowData', IDS[i]);
					var turnround_rate = parseInt(rowData["turnround_rate"]);
					if (turnround_rate > $("#filter_l_high").val() || turnround_rate < $("#filter_l_low").val()) {
						$exd_list.find("tr#" + IDS[i] + " td[aria\\-describedby='partial_button_list_turnround_rate']").addClass("ui-state-active");
					}
				}
			}			
		});
	}
}
*/
function getChartData(rid) {
	var rowData = $("#partial_button_list").jqGrid('getRowData', rid);

	$.ajax({
		beforeSend : ajaxRequestType,
		async : true,
		url : servicePath + '?method=getChartData',
		cache : false,
		data : {partial_id: rowData["partial_id"]},
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : function (xhrobj) {
			var resInfo = null;
			try {
				// 以Object形式读取JSON
				eval('resInfo =' + xhrobj.responseText);
				if (resInfo.errors && resInfo.errors.length > 0) {
					// 共通出错信息框
					treatBackMessages(null, resInfo.errors);
				} else {
					showChart(resInfo, rowData["partial_code"]);
					var $rate_history = $("#rate_history");
					$rate_history.find("td.td-content:eq(0)").html(resInfo.count6);
					$rate_history.find("td.td-content:eq(1)").html(resInfo.count3);
					$rate_history.find("td.td-content:eq(2)").html(resInfo.countM);
					showDetail(rowData["partial_id"]);
				}
			} catch (e) {
				alert("getChartData 错误" + " message: " + (e.message || e.toString()));
			};
		}
	});
}

var showDetail = function(partial_id){

	var gridlistsize = partial_monitor_list.length;
	for (var igridlist = 0; igridlist < gridlistsize; igridlist++) {
		var gridvalue = partial_monitor_list[igridlist];
		if (gridvalue["partial_id"] == partial_id) {

			var hval = $("#filter_l_high").val();
			var lval = $("#filter_l_low").val();

			var $rate_detail = $("#rate_detail");
			$rate_detail.find("td.td-content:eq(0)").text(gridvalue["order_num"]);
			$rate_detail.find("td.td-content:eq(1)").text(gridvalue["base_num"]);
			var turnround_rate = parseFloat(gridvalue["turnround_rate"]);
			$rate_detail.find("td.td-content:eq(2)").text((turnround_rate || " - ") + " %")
				.attr("class", ((turnround_rate > hval || turnround_rate < lval) ? "td-content up-active" : "td-content"));

			$rate_detail.find("td.td-content:eq(3)").text(toNum(gridvalue["order_num1"]) + toNum(gridvalue["order_num3"]));
			$rate_detail.find("td.td-content:eq(4)").text(toNum(gridvalue["order_num4"]) + toNum(gridvalue["order_num5"]));
			turnround_rate = parseFloat(gridvalue["osh_turnround_rate"]);
			$rate_detail.find("td.td-content:eq(5)").text((gridvalue["osh_turnround_rate"] || " - ") + " %")
				.attr("class", ((turnround_rate > hval || turnround_rate < lval) ? "td-content up-active" : "td-content"));

			$rate_detail.find("td.td-content:eq(6)").text(gridvalue["order_num2"]);
			$rate_detail.find("td.td-content:eq(7)").text(gridvalue["order_num7"]);
			turnround_rate = parseFloat(gridvalue["ogz_turnround_rate"]);
			$rate_detail.find("td.td-content:eq(8)").text((gridvalue["ogz_turnround_rate"] || " - ") + " %")
				.attr("class", ((turnround_rate > hval || turnround_rate < lval) ? "td-content up-active" : "td-content"));
	
			return;
		}
	}	
};

var toNum = function(nText) {
	var num = parseInt(nText);
	if (num == null || num == undefined) {
		return 0;
	}
	return num;
}

function showChart(chartData, partial_code){

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
		plotOptions:{
			spline:{
				connectNulls:true,
				dataLabels: {
					formatter: function(){return this.y.toFixed(1) + '%'},
					backgroundColor: 'rgba(252, 255, 197, 0.4)',
					borderWidth: 1,
					borderColor: '#AAA',
					y:-10,
					enabled :false
				},
				animation:false,
				connectNulls : true
			}
		},
		tooltip:{
			formatter: function(){return this.y.toFixed(1) + '%'},
			enabled: true
		},
		xAxis: {
			categories: chartData.weekCategories,
			plotBands: { // 
				id:'vp_osh',
				from: -2,
				to: (chartData.availbleArea || -2),
				color: 'rgba(170, 170, 170, .4)',
				label:{
			        text:'最后调整\n(OSH)',
			        align:'right',
			        x:60
			    }
			}
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
				from: -($("#filter_l_low").val() || 20),
				to: ($("#filter_l_low").val() || 20),
				color: 'rgba(68, 170, 213, .6)'
			} , {// high
				from: ($("#filter_l_high").val() || 100),
				to : 2000,
				color: 'rgba(218, 67, 67, .6)'
			}]
		},
		series: [{
			name: '总消耗速率',
			type:'spline',
			data: chartData.weekConsumeRates
		},{
			name: 'OSH 消耗速率',
			type:'spline',
			data: chartData.weekConsumeRatesOsh
		},{
			name: 'OGZ 消耗速率',
			type:'spline',
			data: chartData.weekConsumeRatesOgz
		}]
	});

	$("#partial_order_chart").dialog({
		title: partial_code + ' 零件消耗速率历史',
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

/*当前结果导出*/
$("#currentResultbutton").click(function() {
	$.ajax({
		beforeSend : ajaxRequestType,
		async : true,
		url : servicePath + '?method=reportCurPage',
		cache : false,
		data : null,
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : function(xhjObject) {
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
				alert("文件导出失败！"); 
			}
		}
	});
});

});