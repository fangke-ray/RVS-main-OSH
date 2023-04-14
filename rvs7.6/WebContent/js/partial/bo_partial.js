$(function() {

/** 一览数据对象 */
var listdata = {};
/** 服务器处理路径 */
var servicePath = "materialPartial.do";

var partialCodeAc = null;

var edit_bo = function(material_id, occur_times) {

	var $process_dialog = $("#process_dialog").hide();
	// 导入编辑画面
	$process_dialog.load("widget.do?method=materialDetail&from=partial&material_id=" + material_id + "&occur_times=" + occur_times,
		function(responseText, textStatus, XMLHttpRequest) {
			$process_dialog.dialog({
				title : "零件订购信息信息编辑",
				width : 800,
				show : "blind",
				height : 'auto' ,
				resizable : false,
				modal : true,
				minHeight : 200,
				buttons : {
				"确定":function(){
						doOk(material_id, occur_times);
					},
				"取消": function(){
						$process_dialog.dialog('close');
					}
				}
			});
	});
};

/** 根据条件使按钮有效/无效化 */
var enablebuttons = function() {
	var rowid = $("#list").jqGrid("getGridParam", "selrow");
	if (rowid != null) {
		$("#editbobutton").enable();
		var row = $("#list").getRowData(rowid);
		// Updated by Gonglm 2014/1/14 Start
		// 修改判断方式
//		if (row["bo_flg"] == 9 || row["bo_flg"] == 1) {
//			$("#consumablebutton").enable();
//			$("#drawbutton").enable();
//		} else {
//			$("#consumablebutton").disable();
//			$("#drawbutton").disable();
//		}
		if (row["bo_contents"] == "" || row["bo_contents"].trim() == "") {
			$("#consumablebutton").disable();
			$("#snoutbutton").disable();
			$("#drawbutton").disable();
		} else {
			$("#consumablebutton").enable();
			$("#snoutbutton").enable();
			$("#drawbutton").enable();
		}
		// Updated by Gonglm 2014/1/14 End
	} else {
		$("#editbobutton").disable();
		$("#consumablebutton").disable();
		$("#snoutbutton").disable();
		$("#drawbutton").disable();
	}
};

function initGrid() {
		$("#list").jqGrid({
			toppager : true,
			data : [],
			height : 461,
			width : 1248,
			rowheight : 23,
			datatype : "local",
			colNames : ['维修对象ID','修理单号', '发生次数', '型号 ID', '型号', '机身号', '等级', '纳期', '零件订购日', '入库预定日', '零件到货', '零件BO', '零件BO'
			, '零件缺品详细', '零件缺品原因', '加急', '发生工位' ,'进展工位'],
			colModel : [{
						name: 'material_id',
						index: 'material_id',
						hidden : true
					}, {
						name : 'sorc_no',
						index : 'sorc_no',
						width : 55
					},{
						name : 'occur_times',
						index : 'occur_times',
						width : 35,
						align : 'center',
						formatter:function(a,b,row){
							return a + " 次";
						}
					}, {
						name : 'model_id',
						index : 'model_id',
						hidden : true
					}, {
						name : 'model_name',
						index : 'model_name',
						width : 115
					}, {
						name : 'serial_no',
						index : 'serial_no',
						width : 50
					}, {
						name : 'levelName',
						index : 'levelName',
						width : 20,
						align : 'center'
					}, {
						name : 'scheduled_date',
						index : 'scheduled_date',
						width : 40,
						align : 'center',
						formatter:'date', 
						formatoptions:{srcformat:'Y/m/d',newformat:'m-d'}
					}, {
						name : 'order_date',
						index : 'order_date',
						width : 40,
						align : 'center',
						formatter:'date', 
						formatoptions:{srcformat:'Y/m/d H:i:s',newformat:'m-d'}
					}, {
						name : 'arrival_plan_date',
						index : 'arrival_plan_date',
						width : 40,
						align : 'center',
						formatter:function(a,b,row) {
							if ("9999/12/31" == a) {
								return "未定";
							}
							
							if (a) {
								var d = new Date(a);
								return mdTextOfDate(d);
							}
							
							return "";
						}
					}, {
						name : 'arrival_date',
						index : 'arrival_date',
						width : 40,
						align : 'center',
						formatter:'date', 
						formatoptions:{srcformat:'Y/m/d H:i:s',newformat:'m-d'}
					}, {
						name : 'bo_flg_show',
						index : 'bo_flg',
						width : 30,
						align : 'center',
						formatter:function(a,b,row){
							var bo = row["bo_flg"]
							if (bo && bo==="9") {
								return " - ";
							}
							if (bo && bo==="1") {
								return "BO";
							}
							if (bo && bo==="2") {
								return "BO解决";
							}
							if (bo && bo==="7") {
								return "预提";
							}
							return "";
						}
					}, {
						name : 'bo_flg',
						index : 'bo_flg',
						width : 50,
						hidden : true
					}, {
						name : 'bo_contents_new',
						index : 'bo_contents_new',
						width : 150
					}, {
						name : 'bo_contents',
						index : 'bo_contents_new',
						width : 150,
						formatter:function(val, row, record) {
							var content = null;
							try {
								if (val && val.length && val.charAt(0) == "{") {
									eval('content =' + val);
//									return "分解缺品零件:" + content.dec +",NS缺品零件:"+ content.ns +",总组缺品零件:"+content.com;
									return content.dec  +content.ns + content.com;
								} else {
									return val || "";
								}								
							} catch (e) {
								return "";
							}
							
							return "";
						}
					}, {
						name : 'scheduled_expedited',
						index : 'scheduled_expedited',
						width : 20,
						align : 'center',
						formatter:function(a,b,row){
							if (a && a==="2") {
								return "直送快速"
							}
							if (a && a==="1") {
								return "加急"
							}
							return "";
						}
					}, {
						name : 'line_name',
						index : 'line_name',
						width : 55,
						align : 'center'
					}, {
						name : 'process_name',
						index : 'process_name',
						width : 55,
						align : 'center'
					}
			],
			rowNum : 50,
			rownumbers : true,
			toppager : false,
			pager : "#listpager",
			viewrecords : true,
			caption : "零件订购信息一览",
			hidegrid : false,
			gridview : true, // Speed up
			pagerpos : 'right',
			pgbuttons : true,
			ondblClickRow : function(rid, iRow, iCol, e) {
				var data = $("#list").getRowData(rid);
				var material_id = data["material_id"];
				var occur_times = parseInt(data["occur_times"]);
				edit_bo(material_id, occur_times);
			},
			pginput : false,
			recordpos : 'left',
			viewsortcols : [true, 'vertical', true],
			onSelectRow : enablebuttons,
			onSelectAll : enablebuttons,
			gridComplete : enablebuttons

		});
	}
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
			treatBackMessages("#editarea", resInfo.errors);
		} else {
			if (resInfo.list) {
				loadData(resInfo.list);
			}

			if (resInfo.rate) {
				$("#label_rate").text(resInfo.rate).show();
				$("#label_rate_w").hide();
			} else if (resInfo.rate_w) {
				$("#label_rate_w").text(resInfo.rate_w).show();
				$("#label_rate").hide();
			} else {
				$("#label_rate").show();
				$("#label_rate_w").hide();
			}

		}
	} catch (e) {
		alert("name: " + e.name + " message: " + e.message + " lineNumber: "
				+ e.lineNumber + " fileName: " + e.fileName);
	};
};

function loadData(listdata){
	$("#list").jqGrid().clearGridData();
	$("#list").jqGrid('setGridParam', {data : listdata})
		.trigger("reloadGrid", [{
			current : false
		}]);
}

var keepSearchData;
var findit = function(data) {
	if (!data) { //新查询
		keepSearchData = {
			"sorc_no" : $("#search_sorc_no").val(),
			"serial_no" : $("#search_serial_no").val(),
			"model_id" : $("#search_modelname").val(),
			"level" : $("#search_level").val(),
			"order_date_start" : $("#search_order_date_start").val(),
			"order_date_end" : $("#search_order_date_end").val(),
			"arrival_plan_date_start" : $("#search_arrival_plan_date_start").val(),
			"arrival_plan_date_end" : $("#search_arrival_plan_date_end").val(),
			"arrival_date_start" : $("#search_arrival_date_start").val(),
			"arrival_date_end" : $("#search_arrival_date_end").val(),
			"outline_date_start" : $("#search_outline_date_start").val(),
			"outline_date_end" : $("#search_outline_date_end").val(),
			"bo_flg": $("#search_partial_reception_flg").val(),
			"range": $("#search_range").val(),
			"section_id": $("#search_section").val(),
			"bo_occur_line":$("#search_bo_occur_line").val(),
			"echelon":$("#search_echelon").val(),
			"occur_times":$("#occur_times_set").find("input:checked").val(),
			"bo_partial":$("#search_partial_code").val()
		};
	} else {
		keepSearchData = data;
	}
	
	$.ajax({
		beforeSend : ajaxRequestType,
		async : true,
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

function doOk(material_id, occur_times){
	var partial = {
		"material_id" : material_id,
		"occur_times": occur_times,
		"sorc_no": $("#material_detail_basearea input[name='sorc_no']").val() || $("#material_detail_basearea td.td-content:eq(0) > label").text(),
//		"bo_flg": $("#edit_bo_flg").val(),
//		"order_date": $("#edit_order_date").val(),
//		"arrival_plan_date": $("#edit_arrival_plan_date").val(),
//		"arrival_date": $("#edit_arrival_date").val(),
//		"bo_contents": "{'dec':'" + $("#edit_bo_contents1").val()+"','ns':'" + $("#edit_bo_contents2").val()+"','com':'"+$("#edit_bo_contents3").val()+"'}"
		"bo_contents": $("#edit_bo_contents").val()
	}
	var listIdx = 0;
	$("#exd_list").find(".fix_arrival_plan_date").each(
		function(idx,ele) {
			$ele = $(this);
			if (ele.value != $ele.attr("org")) {
				partial["update.arrival_plan_date[" + listIdx + "]"] = ele.value;
				partial["update.material_partial_detail_key[" + listIdx + "]"] = 
					$ele.parent().parent().find("td[aria\\-describedby='exd_list_material_partial_detail_key']").text();
				listIdx++;
			}
		}	
	);

	doUpdate("materialPartial.do?method=doUpdate", partial, function(resInfo){
		if (resInfo.conflexError) {
			partial.confirmed = "1";
			var $pop_overcom = $("#pop_overcom");
			if ($pop_overcom.length == 0) {
				$("body").append("<div id='pop_overcom'/>");
				$pop_overcom = $("#pop_overcom");
			}
			$pop_overcom.html(resInfo.conflexError);
			$pop_overcom.dialog({
				title : "入库预定日更新确认",
				width : 'auto',
				show : "blind",
				height : 'auto' ,
				resizable : false,
				modal : true,
				minHeight : 200,
				buttons : {
					"确定":function(){
						 doUpdate("materialPartial.do?method=doUpdate", partial, function(resInfo){
							var this_dialog = $("#detail_dialog");
							$pop_overcom.dialog("close");
							this_dialog.dialog('close');
							findit(keepSearchData);
						 });
					},
					"取消":function(){
						$pop_overcom.dialog("close");
					}
				}
			});
		} else {
			$("#process_dialog").dialog('close');
			findit(keepSearchData);
		}
	});
	
}

function exportReport(url,method) {
	keepSearchData.range=$("#search_range").val();
	// Ajax提交
	$.ajax({
		beforeSend: ajaxRequestType, 
		async: true, 
		url: url, 
		cache: false, 
		data: keepSearchData, 
		type: "post", 
		dataType: "json", 
		success: ajaxSuccessCheck, 
		error: ajaxError, 
		complete: function(xhrobj, textStatus){
			var resInfo = null;

			// 以Object形式读取JSON
			eval('resInfo =' + xhrobj.responseText);

			if (resInfo.errors.length > 0) {
				// 共通出错信息框
				treatBackMessages("#searcharea", resInfo.errors);
			} else {
				if ($("iframe").length > 0) {
					$("iframe").attr("src", "download.do?method=output&from=cache&filePath=" + resInfo.filePath+"&fileName="+resInfo.fileName);
				} else {
					var iframe = document.createElement("iframe");
					iframe.src = "download.do?method=output&from=cache&filePath=" + resInfo.filePath+"&fileName="+resInfo.fileName;
					iframe.style.display = "none";
					document.body.appendChild(iframe);
				}
			}
		}
	});
}

/*双击弹出画面*/
var edit_consumable = function(material_id, occur_times, sorc_no) {
     
	$("#consumables_dialog").hide();
	// 编辑画面
	var data = {
		material_id: material_id,
		occur_times: occur_times
	};
	$.ajax({
		beforeSend : ajaxRequestType,
		async : true,
		url : servicePath + '?method=getConsumables',
		cache : false,
		data : data,
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : function(xhrObj) {
			edit_consumable_Complete(xhrObj, material_id, occur_times, sorc_no);
		}
	});
};

var edit_snout = function(material_id, occur_times, sorc_no) {
     
	$("#consumables_dialog").hide();
	// 编辑画面
	var data = {
		material_id: material_id,
		occur_times: occur_times
	};
	$.ajax({
		beforeSend : ajaxRequestType,
		async : true,
		url : servicePath + '?method=getSnouts',
		cache : false,
		data : data,
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : function(xhrObj) {
			edit_snout_Complete(xhrObj, material_id, occur_times, sorc_no);
		}
	});
};

var doUpdateConsumables_handleComplete = function(xhrObj) {
	var resInfo = null;
	try {
		// 以Object形式读取JSON
		eval('resInfo =' + xhrObj.responseText);
		if (resInfo.errors.length > 0) {
		} else {
			$("finish_dialog").text("消耗品替代完成");
			$("finish_dialog").dialog({
				title : "消耗品替代完成",
				width : 800,
				show : "blind",
				height : 'auto' ,
				resizable : false,
				modal : true,
				minHeight : 200,
				buttons : {
				"确定":function(){
					$("finish_dialog").dialog("close");
				}}});
			findit();
		}
	} catch (e) {
		alert("name: " + e.name + " message: " + e.message + " lineNumber: "
				+ e.lineNumber + " fileName: " + e.fileName);
	};
}

var doUpdateSnouts_handleComplete = function(xhrObj) {
	var resInfo = null;
	try {
		// 以Object形式读取JSON
		eval('resInfo =' + xhrObj.responseText);
		if (resInfo.errors.length > 0) {
		} else {
			$("finish_dialog").text("预制替代完成");
			$("finish_dialog").dialog({
				title : "预制替代完成",
				width : 800,
				show : "blind",
				height : 'auto' ,
				resizable : false,
				modal : true,
				minHeight : 200,
				buttons : {
				"确定":function(){
					$("finish_dialog").dialog("close");
				}}});
			findit();
		}
	} catch (e) {
		alert("name: " + e.name + " message: " + e.message + " lineNumber: "
				+ e.lineNumber + " fileName: " + e.fileName);
	};
}

var edit_consumable_Complete = function(xhrObj, material_id, occur_times, sorc_no) {
	var resInfo = null;
	try {
		// 以Object形式读取JSON
		eval('resInfo =' + xhrObj.responseText);
		if (resInfo.errors && resInfo.errors.length > 0) {
			treatBackMessages(null, resInfo.errors);
			return;
		}
		setInsteadList(resInfo.consumables_list);
		$("#consumables_dialog").dialog({
			title : "消耗品替代",
			width : 800,
			show : "blind",
			height : 'auto' ,
			resizable : false,
			modal : true,
			minHeight : 200,
			buttons : {
			"确定":function(){
					$("#consumables_dialog").dialog('close');

					var postData = {material_id:material_id, occur_times:occur_times,
					sorc_no:sorc_no
					};
					var iii = 0;
					$("#consumables_list").find("tr").each(function(idx, ele) {
						
						$input = $(ele).find("input[type=number]");
						if ($input && $input.val()) {
							var ival = $input.val();
							var ilimit = $input.attr("limit");
							var iTotal = $input.attr("total");
							if (ival.match(/^0*$/) == null && ival.match(/^[0-9]*$/) != null) {
								if (ival > ilimit) $input.val(ilimit);
								postData["exchange.quantity[" + iii + "]"] = $input.val();
								postData["exchange.total[" + iii + "]"] = iTotal;
								postData["exchange.material_partial_detail_key[" + iii + "]"] = $(ele).find("td[aria\\-describedby=consumables_list_material_partial_detail_key]").text();
								iii ++;
							}
						};
					});
					if (postData["exchange.material_partial_detail_key[0]"] != null)
					$.ajax({
						beforeSend : ajaxRequestType,
						async : false,
						url : servicePath + '?method=doUpdateConsumables',
						cache : false,
						data : postData,
						type : "post",
						dataType : "json",
						success : ajaxSuccessCheck,
						error : ajaxError,
						complete : doUpdateConsumables_handleComplete
					});
				},
			"取消": function(){
					$("#consumables_dialog").dialog('close');
				}
			}
		});
		
		$("#consumables_dialog").show();
	} catch (e) {
		alert("name: " + e.name + " message: " + e.message + " lineNumber: "
				+ e.lineNumber + " fileName: " + e.fileName);
	};

}

var edit_snout_Complete = function(xhrObj, material_id, occur_times, sorc_no) {
	var resInfo = null;
	try {
		// 以Object形式读取JSON
		eval('resInfo =' + xhrObj.responseText);
		if (resInfo.errors && resInfo.errors.length > 0) {
			treatBackMessages(null, resInfo.errors);
			return;
		}
		setInsteadList(resInfo.Snouts_list);
		$("#consumables_dialog").dialog({
			title : "预制替代",
			width : 800,
			show : "blind",
			height : 'auto' ,
			resizable : false,
			modal : true,
			minHeight : 200,
			buttons : {
			"确定":function(){
					$("#consumables_dialog").dialog('close');

					var postData = {material_id:material_id, occur_times:occur_times,
					sorc_no:sorc_no};
					var iii = 0;
					$("#consumables_list").find("tr").each(function(idx, ele) {
						
						$input = $(ele).find("input[type=number]");
						if ($input && $input.val()) {
							var ival = $input.val();
							var ilimit = $input.attr("limit");
							var iTotal = $input.attr("total");
							if (ival.match(/^0*$/) == null && ival.match(/^[0-9]*$/) != null) {
								if (ival > ilimit) $input.val(ilimit);
								postData["exchange.quantity[" + iii + "]"] = $input.val();
								postData["exchange.total[" + iii + "]"] = iTotal;
								postData["exchange.material_partial_detail_key[" + iii + "]"] = $(ele).find("td[aria\\-describedby=consumables_list_material_partial_detail_key]").text();
								iii ++;
							}
						};
					});
					if (postData["exchange.material_partial_detail_key[0]"] != null)
					$.ajax({
						beforeSend : ajaxRequestType,
						async : false,
						url : servicePath + '?method=doUpdateSnouts',
						cache : false,
						data : postData,
						type : "post",
						dataType : "json",
						success : ajaxSuccessCheck,
						error : ajaxError,
						complete : doUpdateSnouts_handleComplete
					});
				},
			"取消": function(){
					$("#consumables_dialog").dialog('close');
				}
			}
		});
		
		$("#consumables_dialog").show();
	} catch (e) {
		alert("name: " + e.name + " message: " + e.message + " lineNumber: "
				+ e.lineNumber + " fileName: " + e.fileName);
	};

}

function initView(){
	$("input.ui-button").button();
	$("#cond_work_procedure_order_template_flg_set, #occur_times_set").buttonset();

	$("a.areacloser").hover(
		function (){$(this).addClass("ui-state-hover");},
		function (){$(this).removeClass("ui-state-hover");}
	);

	$("#searchbutton").addClass("ui-button-primary");
	$("#searchbutton").click(function() {
		findit();
	});

	$("#resetbutton").click(function() {
		$("#search_sorc_no").val("");
		$("#search_serial_no").val("");
		$("#search_modelname").val("");
		$("#txt_modelname").val("");
		$("#search_level").val("").trigger("change");
		$("#search_partial_reception_flg").val("").trigger("change");
		$("#search_order_date_start").val("");
		$("#search_order_date_end").val("");
		$("#search_arrival_plan_date_start").val("");
		$("#search_arrival_plan_date_end").val("");
		$("#search_arrival_date_start").val("");
		$("#search_arrival_date_end").val("");
		$("#search_outline_date_start").val("");
		$("#search_outline_date_end").val("");
		$("#cond_work_procedure_order_template").val("");
		$("#cond_work_procedure_order_template_a").attr("checked","checked").trigger("change");
		$("#search_section").val("").trigger("change");
		$("#search_range").val("1").trigger("change");
		$("#search_bo_occur_line").val("").trigger("change");
		$("#search_partial_code").val("");
		$("#search_partial_id").val("");
		$("#occur_times_set input:eq(0)").attr("checked","checked").trigger("change");
	});
	$("#search_range").val("1").trigger("change");

	$("input[name=bo]").bind('click',function(){
		$("#cond_work_procedure_order_template").val($(this).val());
	});

	// autoComplete(零件code) 
	$.ajax({
		beforeSend : ajaxRequestType,
		async : true,
		url : 'partial.do?method=getAutocomplete',
		cache : true,
		data : null,
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : function(xhrobj) {
			var resInfo = null;
			try {
				// 以Object形式读取JSON
				eval('resInfo =' + xhrobj.responseText);
			
				partialCodeAc = resInfo.sPartialCode;
				$("#search_partial_code").autocomplete({
					source : partialCodeAc,
					minLength :3,
					delay : 100,
					focus: function( event, ui ) {
						 $(this).val( ui.item.label );
						 return false;
					},
					select: function( event, ui ) {
						$("#search_partial_id").val(ui.item.value);
						$(this).val( ui.item.label );
						 return false;
					}
				});				
			} catch (e) {
			}
		}
	});		

	$("#searcharea span.ui-icon").bind("click",function() {
		$(this).toggleClass('ui-icon-circle-triangle-n').toggleClass('ui-icon-circle-triangle-s');
		if ($(this).hasClass('ui-icon-circle-triangle-n')) {
			$(this).parent().parent().next().show("blind");
		} else {
			$(this).parent().parent().next().hide("blind");
		}
	});
	
	$("#search_order_date_start, #search_order_date_end, #search_arrival_plan_date_start, #search_arrival_plan_date_end, " +
			" #search_arrival_date_start, #search_arrival_date_end, #search_outline_date_start, #search_outline_date_end").datepicker({
		showButtonPanel:true,
		dateFormat: "yy/mm/dd",
		currentText: "今天"
	});
	
	$("#search_level, #search_range, #search_bo_occur_line, #search_echelon, #search_partial_reception_flg, #search_section").select2Buttons();
	setReferChooser($("#search_modelname"));

	$("#editbobutton").click(function(){
		var rowid = $("#list").jqGrid("getGridParam", "selrow");
		if (rowid == null) return;
		var data = $("#list").getRowData(rowid);
		var material_id = data["material_id"];
		edit_bo(material_id);
	});

	$("#consumablebutton").click(function(){
		var rowid = $("#list").jqGrid("getGridParam", "selrow");
		if (rowid == null) return;
		var data = $("#list").getRowData(rowid);
		var material_id = data["material_id"];
		var sorc_no = data["sorc_no"];
		var occur_times = parseInt(data["occur_times"]);
		edit_consumable(material_id, occur_times, sorc_no);
	});

	$("#snoutbutton").click(function(){
		var rowid = $("#list").jqGrid("getGridParam", "selrow");
		if (rowid == null) return;
		var data = $("#list").getRowData(rowid);
		var material_id = data["material_id"];
		var occur_times = parseInt(data["occur_times"]);
		edit_snout(material_id, occur_times);
	});

	$("#label_rate").hide();
	
	$("#reportbobutton").click(function(){
		exportReport(servicePath + '?method=reportPartialOrder', "exportOrder");
		});
	$("#reportbobutton2").click(function(){
		exportReport(servicePath + '?method=reportBo',"exportBo");
	});
	$("#reportappendbutton").click(function(){
		exportReport(servicePath + '?method=reportAppendOrder',"exportAppendOrder");
	});

	$("#consumablebutton").disable();
	$("#drawbutton").disable();

	initGrid();

	$.ajax({
		beforeSend : ajaxRequestType,
		async : true,
		url : servicePath + '?method=getBoRateInWeek',
		cache : false,
		data : keepSearchData,
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : search_handleComplete
	});

	findit();
}

	initView();
});

var changevalue = function(rowid,status,e){
	if (rowid instanceof Array) {
		// 全选
		var $inum = $("#consumables_list").find("input[type=number]");
		$inum.each(function(){
			if (status) {
				$(this).val($(this).attr("limit"));
			} else {
				$(this).val(0);
			}
		});
	} else {
		var $inum = $("#consumables_list").find("tr#"+rowid).find("input[type=number]");
		if (status) {
			$inum.val($inum.attr("limit"));
		} else {
			$inum.val(0);
		}
	}
}

/*jqgrid表格*/
function setInsteadList(consumables_list){
	if ($("#gbox_consumables_list").length > 0) {
		$("#consumables_list").jqGrid().clearGridData();
		$("#consumables_list").jqGrid('setGridParam',{data:consumables_list}).trigger("reloadGrid", [{current:false}]);
	} else {
		$("#consumables_list").jqGrid({
			data:consumables_list,
			height: 461,
			width: 768,
			rowheight: 23,
			datatype: "local",
			colNames:['','零件编号','零件名称','可签收数量','工位','目前签收状态','签收日期','消耗品'],
			colModel:[
				{name:'material_partial_detail_key',index:'material_partial_detail_key', hidden:true},		   
				{name : 'code',index : 'code',width : 60,align : 'left'},
				{name : 'partial_name',index : 'partial_name',width : 300,align : 'left'},
				{name : 'waiting_quantity',index : 'waiting_quantity',width : 100,align : 'right',formatter:function(r,i,rowData){
					return '<input type="number" value="0" limit="'+r+'" total="'+rowData.quantity+'" > / ' + r;
				}},
				{name :'process_code',index:'process_code',width:60,align:'center' },
				{name :'status',index:'status',width:60,align:'center', formatter:'select', editoptions:{value:"1:未发放;2:已签收无BO;3:BO中;4:BO解决;5:消耗品签收"} },
				{name :'recent_receive_time',index:'recent_receive_time',width:60,align:'center',
						formatoptions:{srcformat:'Y/m/d H:i:s',newformat:'m-d'}},
				{name:'append',index:'append', hidden:true}		   
			 ],
			rowNum: 100,
			toppager: false,
			pager: "#consumables_listpager",
			viewrecords: true,
			multiselect: true,
			hidegrid : false,
			gridview: true,
			pagerpos: 'right',
			pgbuttons: true,			
			pginput: false,
			recordpos: 'left',
			viewsortcols : [true,'vertical',true],
			onSelectRow : changevalue,
			onSelectAll : changevalue,
			gridComplete:function(){
				// 得到显示到界面的id集合
				var IDS = $("#exd_list").getDataIDs();
				// 当前显示多少条
				var length = IDS.length;
				var $exd_list = $("#exd_list");
				changevalue([] ,false);
			}
		});
	}
};
