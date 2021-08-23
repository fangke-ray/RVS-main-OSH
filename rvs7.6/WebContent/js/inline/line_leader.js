var listdata = {};
var servicePath = "lineLeader.do";
var orderPos = "241";
var receivePos = "252";
var position_counts = {};
var selectedMaterial = {};
var checked_position_id = "";
var checked_group = false;
var today_assigned = "";
var chart2 = null;

/*线长签收*/
var leader_receive = function() {

	var selectedId = $("#performance_list").getGridParam("selrow");
	var rowData = $("#performance_list").getRowData(selectedId);

	var data = {material_id : rowData["material_id"], model_name : rowData["model_name"], position_id : rowData["position_id"]};

	// Ajax提交
	$.ajax({
		beforeSend : ajaxRequestType,
		async : false,
		url : servicePath + '?method=doleaderreceive',
		cache : false,
		data : data,
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : function(xhrobj, textStatus) {
			try {
				// 以Object形式读取JSON
				eval('resInfo =' + xhrobj.responseText);

				refreshList();
			} catch (e) {
				alert("name: " + e.name + " message: " + e.message + " lineNumber: "
						+ e.lineNumber + " fileName: " + e.fileName);
			};
		}
	});
};

/*线长订购*/
var leader_order = function() {

	var selectedId = $("#performance_list").getGridParam("selrow");
	var rowData = $("#performance_list").getRowData(selectedId);

	var data = {material_id : rowData["material_id"], model_name : rowData["model_name"], position_id : rowData["position_id"]};

	// Ajax提交
	$.ajax({
		beforeSend : ajaxRequestType,
		async : false,
		url : servicePath + '?method=doleaderorder',
		cache : false,
		data : data,
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : function(xhrobj, textStatus) {
			try {
				// 以Object形式读取JSON
				eval('resInfo =' + xhrobj.responseText);

				refreshList();
			} catch (e) {
				alert("name: " + e.name + " message: " + e.message + " lineNumber: "
						+ e.lineNumber + " fileName: " + e.fileName);
			};
		}
	});
};

/*线长加急*/
var line_expedite = function() {

	var selectedId = $("#performance_list").getGridParam("selrow");
	var rowData = $("#performance_list").getRowData(selectedId);

	var data = {material_id : rowData["material_id"]};

	// Ajax提交
	$.ajax({
		beforeSend : ajaxRequestType,
		async : false,
		url : servicePath + '?method=doexpedite',
		cache : false,
		data : data,
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : function(xhrobj, textStatus) {
			try {
				// 以Object形式读取JSON
				eval('resInfo =' + xhrobj.responseText);

				// 紧急区分
				var expedited = rowData["expedited"];
				var sorc_no = rowData["sorc_no"];
				sorc_no = sorc_no.replace(/<.*>(\d*)<.*>/, "$1").trim();
				if (expedited == "2") {
					return;
				}
				if (expedited == "1") {
					griddata_update(listdata, "sorc_no", sorc_no, "expedited", 0, false);
					$("#expeditebutton").val("加急");
				} else {
					griddata_update(listdata, "sorc_no", sorc_no, "expedited", 1, false);
					$("#expeditebutton").val("取消加急");
				}

				$("#performance_list").setGridParam({data : listdata});
				$("#performance_list").trigger("reloadGrid", [{current : true}]);
			} catch (e) {
			};
		}
	});
};

/*不良处置*/
var treat_nogood = function() {
	$("#nogood_treat").hide();
	// 导入不良处置画面
	$("#nogood_treat").load("widget.do?method=nogoodedit", function(responseText, textStatus, XMLHttpRequest) {
		var selectedId = $("#performance_list").getGridParam("selrow");
		var rowData = $("#performance_list").getRowData(selectedId);
		var data = {
			material_id : rowData["material_id"],
			position_id : rowData["position_id"]
		};
		// Ajax提交
		$.ajax({
			beforeSend : ajaxRequestType,
			async : false,
			url : servicePath + '?method=getwarning',
			cache : false,
			data : data,
			type : "post",
			dataType : "json",
			success : ajaxSuccessCheck,
			error : ajaxError,
			complete : function(xhrobj, textStatus) {
				try {
					// 以Object形式读取JSON
					eval('resInfo =' + xhrobj.responseText);
					$("#nogood_id").val(resInfo.warning.id);
					$("#nogood_occur_time").text(resInfo.warning.occur_time);
					$("#nogood_sorc_no").text(resInfo.warning.sorc_no);
					$("#nogood_model_name").text(resInfo.warning.model_name);
					$("#nogood_serial_no").text(resInfo.warning.serial_no);
					$("#nogood_line_name").text(resInfo.warning.line_name);
					$("#nogood_process_code").text(resInfo.warning.process_code);
					$("#nogood_reason").text(resInfo.warning.reason);
					$("#nogood_comment_other").text(resInfo.warning.comment);
					$("#nogood_comment").val(resInfo.warning.myComment);
					selectedMaterial.sorc_no = resInfo.warning.sorc_no;
					selectedMaterial.model_name = resInfo.warning.model_name;
					selectedMaterial.serial_no = resInfo.warning.serial_no;
					selectedMaterial.material_id = rowData["material_id"];
					selectedMaterial.position_id = rowData["position_id"];
					selectedMaterial.comment = $("#nogood_comment").val();
					selectedMaterial.alarm_messsage_id = $("#nogood_id").val();

					$("#nogood_treat").dialog({
						// position : [ 800, 20 ],
						title : "不良信息及处置",
						width : 468,
						show : "blind",
						height : 'auto', //450,
						resizable : false,
						modal : true,
						minHeight : 200,
						close : function() {
							selectedMaterial.comment = $("#nogood_comment").val();
							selectedMaterial.append_parts = ($("#append_parts_y").attr("checked") ? 1 : 0);
							if ($("#nogoodfixbtn").attr("checked") === "checked") {
							} else if ($("#nogoodclosebtn").attr("checked") === "checked") {
								if (refreshList) refreshList();
							}
							$("#nogood_treat").html("");
						},
						buttons : {}
					});
					$("#nogood_treat").show();
				} catch (e) {
					alert("name: " + e.name + " message: " + e.message + " lineNumber: "
							+ e.lineNumber + " fileName: " + e.fileName);
				};
			}
		});
	});
};

/*列表展开*/
var toggleListAll = function() {
	jobj = $(this);
	jobj.unbind("click", toggleListAll);
	if ($("#storagearea").css("display") === "none") {
		$("#workarea").animate({width: '-=620'}, function(){
			jobj.bind("click", toggleListAll);
			$("#performance_list").jqGrid('hideCol', ['partical_order_date', 'partical_bo','arrival_plan_date','category_name','operate_result','scheduled_date','ocm']);
			$("#performance_list").jqGrid('showCol', 'symbol');
			$("#performance_list").jqGrid('setGridWidth', '602');
		});
	} else {
		$("#workarea").animate({width: '+=620'}, function(){
			jobj.bind("click", toggleListAll);
			$("#performance_list").jqGrid('showCol', ['partical_order_date', 'partical_bo','arrival_plan_date','category_name','operate_result','scheduled_date','ocm']);
			$("#performance_list").jqGrid('hideCol', 'symbol');
			$("#performance_list").jqGrid('setGridWidth', '1222');
		});
	}
	jobj.toggleClass('ui-icon-circle-triangle-e').toggleClass('ui-icon-circle-triangle-w');
	$("#storagearea").toggle("blind", {direction: 'right'});
};

var getFlags = function(expedited, direct_flg, light_fix, reworked) {
	if (expedited || direct_flg|| light_fix || reworked) {
		var retDiv = "";
		if (expedited >= 20) retDiv += "<span class='rapid_direct_flg'><span>直送快速</span></span>";
		else {
			retDiv += (direct_flg == 1 ? "<span class='direct_flg'>直</span>" : "");
		}
		if (light_fix == 1) {
			retDiv += "<span class='light_fix'>小</span>";
		}
		if (reworked == 1) {
			retDiv += "<span class='service_repair_flg'>返</span>";
		}
		return retDiv;
	} else {
		return "";
	}
};

var jsinit_ajaxSuccess = function(xhrobj, textStatus){
	var resInfo = null;
	try {
		// 以Object形式读取JSON
		eval('resInfo =' + xhrobj.responseText);

		if (resInfo.errors.length > 0) {
			// 共通出错信息框
			treatBackMessages(null, resInfo.errors);
		} else {
			receivePos = resInfo.receivePos;
			if ("400" == receivePos) {
				$("#receivebutton").val("总组接受");
			} else {
				$("#receivebutton").val("零件签收");
			}

			listdata = resInfo.performance;

			if ($("#gbox_performance_list").length > 0) {
				$("#performance_list").jqGrid().clearGridData();
				$("#performance_list").jqGrid('setGridParam', {data : listdata}).trigger("reloadGrid", [{current : false}]);
			} else {
				$("#performance_list").jqGrid({
					toppager : true,
					data : listdata,
					height : 507,
					width : 602,
					rowheight : 23,
					datatype : "local",
					colNames : ['产出安排', '同意日期', '加急', '修理单号','维修站', '零件订购日','零件BO','入库预定日','剩余时间', '不良', '等级', '机身号', '机种', '型号', '状态', '位置', '', 'ns_partial_order' ,'他工程位置',
							'material_id', 'position_id', 'is_reworking', 'is_today', 'light_fix','overtime'],
					colModel : [
							{
								name:'scheduled_date',
								index:'scheduled_date',
								width:50, align:'center',
								formatter:'date', formatoptions:{srcformat:'Y/m/d', newformat:'m-d'},
								hidden:true
							},
							{
								name : 'agreed_date',
								index : 'agreed_date',
								width : 50,
								align : 'center', formatter:'date', formatoptions:{srcformat:'Y/m/d',newformat:'m-d'}
							},
							{
								name : 'expedited',
								index : 'expedited',
								hidden : true
							}, {
								name : 'sorc_no',
								index : 'sorc_no',
								width : 90, formatter:function(value,b,row) {
									return "<span class='sorc_no'>" + value + "</span>  " + 
										getFlags(row.expedited, row.direct_flg, row.light_fix, row.is_reworking);
//										(row.direct_flg == 1 ? " <span class='direct_flg'>直</span>" :"");
								}
							}, {
								name : 'ocm',
								index : 'ocm',
								width : 40, align:'right', formatter:'select', editoptions:{value:"1:SHRC;2:BJRC;3:GZRC;4:SYRC",
								hidden : true} // codelist 简短
							}, {
								name : 'partical_order_date',
								index : 'partical_order_date',
								width : 70,
								align:'center',
								hidden : true, formatter:'date', formatoptions:{srcformat:'Y/m/d',newformat:'m-d'}
							}, {
								name : 'partical_bo',
								index : 'partical_bo',
								formatter: "select", editoptions:{value:"0:;1:BO"},
								align:'center',
								width : 65,
								hidden : true},
						{name:'arrival_plan_date',index:'arrival_plan_date', width:70, align:'center', hidden : true,
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
						{name:'expected_finish_time',index:'expected_finish_time', width:55, align : 'center',
							formatter:function(value,b,row) {
								if (value == null || value == "") {
									return "";
								} else {
									return "<div class='finish_time_left' finish_time='" + value + "'></div>";
								}
							}
						},
						{
								name : 'symbol',
								index : 'symbol',
								width : 45,
								align : 'center'
							}, {
								name : 'level',
								index : 'level',
								formatter: "select", editoptions:{value:resInfo.opt_level},
								width : 35,
								align : 'center'
							}, {
								name : 'serial_no',
								index : 'serial_no',
								width : 50
							},
						{name:'category_name',index:'category_name', width:50, hidden : true},
							 {
								name : 'model_name',
								index : 'model_name',
								width : 105
							},
							{
								name : 'operate_result',
								index : 'operate_result',
								width : 50,
								formatter: "select", editoptions:{value:resInfo.opt_operate_result},
								hidden : true
							}, {
								name : 'process_code',
								index : 'process_code',
								width : 50,
								align : 'center'
							}, {
								name : 'px',
								index : 'px',
								width : 20, align:'center', formatter:'select', editoptions:{value:resInfo.opt_px},
								hidden : !resInfo.division_flg
							}, {
								name : 'ns_partial_order',
								hidden : true
							},
						{name:'otherline_process_code',index:'otherline_process_code', width:50, align:'center'},
						{name : 'material_id', index : 'material_id', hidden : true, key: true},
						{name : 'position_id', index : 'position_id', hidden : true},
						{name : 'is_reworking', index : 'is_reworking', hidden : true},
						{name : 'is_today', index : 'is_today', hidden : true},
						{name : 'light_fix', index : 'light_fix', hidden : true},
						{name : 'overtime', index : 'overtime', hidden : true}
							],
					rowNum : 200,
					rownumbers : true,
					toppager : false,
					viewrecords : true,
					caption : "",
					gridview : true, // Speed up
					pagerpos : 'right',
					pgbuttons : true,
					pginput : false,
					recordpos : 'left',
					ondblClickRow : function(rid, iRow, iCol, e) {
						var data = $("#performance_list").getRowData(rid);
						var material_id = data["material_id"];
						showMaterial(material_id);
					},
					onSelectRow : function(id) {
						var rowdata = $(this).jqGrid('getRowData', id);

						// 紧急区分
						var expedited = rowdata["expedited"];
						if (expedited == "10" || expedited == "11") {
							$("#expeditebutton").hide();
						} else if (expedited == "1") {
							//$("#expeditebutton").val("取消加急");
							$("#expeditebutton").hide();
						} else {
							$("#expeditebutton").val("加急");
							$("#expeditebutton").show();
							$("#expeditebutton").enable();
						}

						// 不良
						var symbol = rowdata["symbol"];
						if (symbol != "") {
							$("#nogoodbutton").enable();
						} else {
							$("#nogoodbutton").disable();
						}
		
						// 零件订购
						if (rowdata["operate_result"] === "0" && rowdata["process_code"] === orderPos) { // 分解线长零件订购
							$("#orderbutton").show();
							$("#orderbutton").val("零件订购");
						} else if (rowdata["ns_partial_order"] != null && "0" == rowdata["ns_partial_order"].trim()) { // NS线长零件订购单
							$("#orderbutton").show();
							$("#orderbutton").val("零件订购");
						} else {
							$("#orderbutton").hide();
						}			

						// 零件签收/总组接受
						if ((rowdata["operate_result"] === "0" && rowdata["process_code"] === receivePos)) { // 签收
							$("#receivebutton").show();
						} else {
							$("#receivebutton").hide();
						}	

						// 切平行线
						var px = rowdata["px"];
						if (rowdata["operate_result"] === "0" && px != "") {
							$("#pxbutton").enable();
						} else {
							$("#pxbutton").disable();
						}

						// 工程检查票
						$("#pcsbutton").enable();
					},
					viewsortcols : [true, 'vertical', true],
					gridComplete : function() {
						// ②在gridComplete调用合并方法
						var gridName = "performance_list";
					 	Merger(gridName, 'agreed_date');

					 	// 计算剩余时间
						var $trs = $("#performance_list").find("tr.ui-widget-content");
						$trs.each(function(){
							var $td = $(this).find("td[aria\\-describedby=performance_list_expected_finish_time]");
							var $target_obj = $td.find("div.finish_time_left");
							var expected_finish_time_val = $target_obj.attr("finish_time");
							if (expected_finish_time_val != null && expected_finish_time_val != "") {
								date_time_list.add({id: this.id, target:$target_obj, date_time:expected_finish_time_val});
							}
						});
						refreshTargetTimeLeft(1);
					}
				});

			$("#plan_chk").click(function(){
				if (this.checked) {
					$(this).next().find("span").text("当日计划");
					today_assigned = "1";
				} else {
					$(this).next().find("span").text("全部仕挂");
					today_assigned = "";
				}
				refreshList();
			});


			}

			if (resInfo.division_flg) {
				$("#pxbutton").show().disable();
			}

			$("#workarea .HeaderButton span.ui-icon").bind("click", toggleListAll);

			$("#sikake").text(listdata.length); // TODO real sikake

			setChart(resInfo);
		}

		$("#processing_container").on("mouseover mouseout", "tspan",
			function(event) {
				var jthis = $(this);
				if (event.type == "mouseover"){
					jthis.parent().css("fill", "navy");
				} else if(event.type == "mouseout") {
					jthis.parent().css("fill", "#666");
				}
			}
		);
	
	} catch (e) {
		alert("name: " + e.name + " message: " + e.message + " lineNumber: "
				+ e.lineNumber + " fileName: " + e.fileName);
	};
};

var refreshChart = function() {
	// Ajax提交
	$.ajax({
		beforeSend : ajaxRequestType,
		async : true,
		url : servicePath + '?method=refreshChart',
		cache : false,
		data : null,
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : function(xhrObj){
			var resInfo = $.parseJSON(xhrObj.responseText);
			setChart(resInfo);
		}
	});
}

var setChart = function(resInfo) {
	if (chart2 == null) {
	chart2 = new Highcharts.Chart({
		colors : ['#cc76cc', '#92D050'], // DFE3E8
		chart : {
			renderTo : 'processing_container',
			type : 'bar',
			marginRight : 10,
			backgroundColor : {
				linearGradient : [0, 0, 0, 0],
				stops : [[0, 'rgba(0, 0, 0, 0)'], [1, 'rgba(0, 0, 0, 0)']]
			},
			height : 473,
			width : 604
		},
		credits : {
			enabled : false
		},
		title : {
			text : ''
		},
		xAxis : {
			categories : resInfo.categories,
			labels : {
				style : {
					fontWeight : 'bold',
					fontSize : '16px'
				}
			}
		},
		yAxis : {
			title : {
				text : '台数',
				style : {
					fontWeight : 'bold',
					fontSize : '16px'
				}
			},
			allowDecimals:false
		},
		tooltip : {
			animation : false,
			formatter : function() {
				return '<b>' + this.series.name + '</b><br/>' + this.y + '台<br/>';
			},
			style : {
				fontWeight : 'bold',
				fontSize : '12px'

			},
			labels : {
				style : {
					fontWeight : 'bold',
					fontSize : '18px'
				}
			}
		},
		plotOptions : {
			series : {
				point : {
					events : {
//								click : function() {
//									if (this.x === parseInt(this.x)) {
//										window.location.href = "position_panel.do";
//									}
//								}
					}
				}
			},
			bar : {
				dataLabels : {
					enabled: true,
					color : 'green'
				},
				stacking : 'normal'
			}
		},
		legend : {
			enabled : true,
			floating : true,
			x : -110,
			y : 10
		},
		exporting : {
			enabled : false
		},
		series : [{
			type : 'bar',
			name : '中小修理台数',
			data : resInfo.light_fix_counts,
			zIndex : 2
		},{
			type : 'bar',
			name : '大修理台数',
			data : resInfo.counts,
			zIndex : 2
		}]
	});
	} else {
		chart2.xAxis[0].setCategories(resInfo.categories, false);
		chart2.series[0].setData(resInfo.light_fix_counts, false);
		chart2.series[1].setData(resInfo.counts);
	}
}

$(document).ready(function() {
	$("input.ui-button").button();
	$("#expeditebutton").disable();
	$("#nogoodbutton").disable();
	$("#orderbutton").hide();
	$("#receivebutton").hide();
	$("#pcsbutton").disable();

	$("#expeditebutton").click(line_expedite);
	$("#nogoodbutton").click(treat_nogood);
	$("#orderbutton").click(leader_order);
	$("#receivebutton").click(leader_receive);
	$("#pcsbutton").click(function(){
		var selectedId = $("#performance_list").getGridParam("selrow");
		var rowData = $("#performance_list").getRowData(selectedId);

		//V2
		showPcsDetailLeader(rowData["material_id"]);
	});

	$("#sikake").parent().parent().click(function () {positionFilter("");});

	$("#pxbutton").click(px_exchange);

	// Ajax提交
	$.ajax({
		beforeSend : ajaxRequestType,
		async : false,
		url : servicePath + '?method=jsinit',
		cache : false,
		data : null,
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : jsinit_ajaxSuccess
	});

	setInterval(refreshChart, 60000);
});

// 公共调用方法
function Merger(gridName, CellName) {
	// 得到显示到界面的id集合
	var mya = $("#" + gridName + "").getDataIDs();
	// 当前显示多少条
	var length = mya.length;

	var pill = $("#" + gridName + "");

	for (var i = 0; i < length; i++) {
		// 从上到下获取一条信息
		var before = pill.jqGrid('getRowData', mya[i]);
		// 定义合并行数
		var rowSpanTaxCount = 1;
		// 操作中区分
		var status = before["operate_result"];

		// 待解决
		if (before["in_pa"] != null) {
			pill.find("tr#" + mya[i] + " td").css("background-color", "gray");
		}

		if (status == "0" || status == "4" || status == "7") {
			pill.find("tr#" + mya[i] + " td[aria\\-describedby='" + gridName + "_process_code']").css("color", "#0080FF");
		} else if (status == "1") {
			pill.find("tr#" + mya[i] + " td[aria\\-describedby='" + gridName + "_process_code']").css("color", "#58b848");
		} else if (status == "3") {
			// 不良
			pill.find("tr#" + mya[i] + " td[aria\\-describedby='" + gridName + "_process_code']").css("color", "red");
			pill.find("tr#" + mya[i] + " td[aria\\-describedby='" + gridName + "_process_code']").css("font-weight", "bolder");
			pill.find("tr#" + mya[i] + " td[aria\\-describedby='" + gridName + "_symbol']").css("background-color", "orange").css("font-weight", "bolder"); // .text("※")
		}

		var reworking = before["is_reworking"];
		var overtime = before["overtime"];		
		if(reworking != "1" && overtime == "1"){//超时,返工除外
			pill.find("tr#" + mya[i] + " td[aria\\-describedby='" + gridName + "_process_code']").css("box-shadow", "inset 1px 1px 6px orange");
		}
		
		// 当日计划
		var today = before["is_today"];
		if (today == "1") {
			pill.find("tr#" + mya[i] + " td[aria\\-describedby='" + gridName + "_sorc_no']").css("color", "green");
			pill.find("tr#" + mya[i] + " td[aria\\-describedby='" + gridName + "_sorc_no']").css("font-weight", "bolder");
		}
		// 紧急区分
		var expedited = before["expedited"];
		if (expedited == "1") {
			pill.find("tr#" + mya[i] + " td[aria\\-describedby='" + gridName + "_sorc_no']").css("color", "blue");
			pill.find("tr#" + mya[i] + " td[aria\\-describedby='" + gridName + "_sorc_no']").css("font-weight", "bolder");
		} else
		if (expedited == "10" || expedited == "11") {
			pill.find("tr#" + mya[i] + " td[aria\\-describedby='" + gridName + "_sorc_no']").css("color", "#F0C800;");
			pill.find("tr#" + mya[i] + " td[aria\\-describedby='" + gridName + "_sorc_no']").css("font-weight", "bolder");
		}
		// 返工中
		if (reworking == "1") {
			pill.find("tr#" + mya[i] + " td[aria\\-describedby='" + gridName + "_process_code']").css("color", "pink");
		}

// 入库预定日
//	if (before["arrival_plan_date"] != "")
//							alert(before["arrival_plan_date"]);

//		for (var j = i + 1; j <= length; j++) {
//			// 和上边的信息对比 如果值一样就合并行数+1 然后设置rowspan 让当前单元格隐藏
//			var end = pill.jqGrid('getRowData', mya[j]);
//			if (before[CellName] == end[CellName]) {
//				rowSpanTaxCount++;
//				pill.setCell(mya[j], CellName, '', {display : 'none'});
//			} else {
//				rowSpanTaxCount = 1;
//				break;
//			}
//			pill.find("tr#" + mya[i] + " td[aria\\-describedby='" + gridName + "_"
//					+ CellName + "']").attr("rowspan", rowSpanTaxCount);
//		}
	}
}

var positionFilter = function(position_id, checkGroup) {
	if (checked_position_id == position_id) return;

	checked_position_id = position_id;
	checked_group = checkGroup;

	refreshList();
}

var refreshList = function() {
	// Ajax提交
	$.ajax({
		beforeSend : ajaxRequestType,
		async : true,
		url : servicePath + '?method=refreshList',
		cache : false,
		data : {position_id : checked_position_id, today: today_assigned, checked_group : checked_group},
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : function(xhrobj, textStatus) {
			try {
				// 以Object形式读取JSON
				eval('resInfo =' + xhrobj.responseText);

				listdata = resInfo.performance;
	
				$("#performance_list").jqGrid().clearGridData();
				$("#performance_list").jqGrid('setGridParam', {data : listdata}).trigger("reloadGrid", [{current : false}]);

				$("#sikake").text(listdata.length);
			} catch (e) {
				alert("name: " + e.name + " message: " + e.message + " lineNumber: "
						+ e.lineNumber + " fileName: " + e.fileName);
			};
		}
	});
}

var showMaterial = function(material_id) {
	var $material_detail = $("#material_detail");
	if ($material_detail.length == 0) {
		$('body').append('<div id="material_detail" />');
		if (jQuery.fn.select2Buttons == undefined) {
			loadCss("css/olympus/select2Buttons.css",
				function(){loadJs("js/jquery.select2buttons.js")});
		}
		$material_detail = $("#material_detail");
	}

	$material_detail.hide();
	// 导入编辑画面
	$material_detail.load("widget.do?method=materialDetail&material_id=" + material_id,
		function(responseText, textStatus, XMLHttpRequest) {
			$.ajax({
			data:{
				"id": material_id // , occur_times: occur_times
			},
			url : "material.do?method=getDetial",
			type : "post",
			complete : function(xhrobj, textStatus){
				var resInfo = null;
				try {
					// 以Object形式读取JSON
					eval('resInfo =' + xhrobj.responseText);
					case0();
					setLabelText(resInfo.materialForm, resInfo.partialForm, resInfo.processForm, resInfo.timesOptions, material_id);
					
				} catch (e) {
					alert("name: " + e.name + " message: " + e.message + " lineNumber: "
							+ e.lineNumber + " fileName: " + e.fileName);
				};
				
				$material_detail.dialog({
					title : "维修对象详细信息",
					width : 800,
					show : "blind",
					height : 'auto' ,
					resizable : false,
					modal : true,
					minHeight : 200,
					buttons : {
						"关闭": function(){
							$material_detail.dialog('close');
						}
					}
				});
				$material_detail.show();
			}
		});
	});

};

/* 切换分线 */
var px_exchange = function() {

	var selectedId = $("#performance_list").getGridParam("selrow");
	var rowData = $("#performance_list").getRowData(selectedId);

	var data = {material_id : rowData["material_id"], position_id : rowData["position_id"]};

	// Ajax提交
	$.ajax({
		beforeSend : ajaxRequestType,
		async : false,
		url : 'material.do?method=doPxExchange',
		cache : false,
		data : data,
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : function(xhrObj, textStatus) {
			// 以Object形式读取JSON
			var resInfo = $.parseJSON(xhrObj.responseText);
			refreshList();
			refreshChart();
		}
	});
};