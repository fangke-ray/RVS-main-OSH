// Your turst our mission

/** 模块名 */
var modelname = "WIP";
/** 一览数据对象 */
var listdata = {};
/** 服务器处理路径 */
var servicePath = "wip.do";
var lOptions = {};
var selwip = ""; 


var findit = function() {
	var data = {
		"sorc_no" : $("#cond_sorc_no").data("post"),
		"model_id" : $("#cond_model_id").data("post"),
		"serial_no" : $("#cond_serial_no").data("post"),
		"esas_no" : $("#cond_esas_no").data("post"),
		"reception_time_start" : $("#cond_reception_time_start").data("post"),
		"reception_time_end" : $("#cond_reception_time_end").data("post"),
		"direct_flg" : $("#cond_direct_flg").data("post"),
		"wip_location" : $("#cond_wip_location").data("post"),
		"level": $("#cond_level").data("post")
	};

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

/** 根据条件使按钮有效/无效化 */
var enablebuttons = function(rowids) {
	if (rowids == null)
		rowids = $("#list").jqGrid("getGridParam", "selarrrow");
	if (rowids.length === 0) {
		$("#warehousingbutton").disable();
		$("#resystembutton").disable();
		$("#stopbutton").disable();
		$("#movebutton").disable();
		$("#imgcheckbutton").hide();
	} else if (rowids.length === 1) {
		$("#warehousingbutton").enable();
		var rowdata = $("#list").jqGrid('getRowData', rowids[0]);
		if (rowdata["break_back_flg"] == "0") {
			$("#resystembutton").enable();
			$("#stopbutton").enable();
		} else {
			$("#resystembutton").disable();
			$("#stopbutton").disable();
		}
		if (rowdata["operate_result"] == "1") {
			$("#imgcheckbutton").show();
		} else {
			$("#imgcheckbutton").hide();
		}
		$("#movebutton").enable();
	} else {
		$("#warehousingbutton").enable();
		$("#resystembutton").disable();
		$("#stopbutton").disable();
		$("#movebutton").disable();
		$("#imgcheckbutton").hide();
	}
};

/** 选择触发联动 */
var mappingwip = function() {
	var rowids = $("#list").jqGrid("getGridParam", "selarrrow");
	enablebuttons(rowids);

	$("td[aria\\-describedby='list_wip_location']").unbind("click");
	$("td[aria\\-describedby='list_wip_location']").click(function() {
		showLocate($(this).text());
	});
//	$(".wip-heaped-checked").removeClass("wip-heaped-checked");
//
//	for (var irowid = 0; irowid < rowids.length; irowid++) {
//		var rowid = rowids[irowid];
//		var rowdata = $("#list").jqGrid('getRowData', rowid);
//		$("#" + rowdata["wip_location"]).addClass("wip-heaped-checked");
//	}
};

var insert_handleComplete = function(xhrobj, textStatus) {
	var resInfo = null;
	try {
		// 以Object形式读取JSON
		eval('resInfo =' + xhrobj.responseText);
		if (resInfo.errors.length > 0) {
			// 共通出错信息框
			treatBackMessages("#editarea", resInfo.errors);
		} else {
			$("#wip_pop").dialog('close');	
			findit();
		}
	} catch (e) {
		alert("name: " + e.name + " message: " + e.message + " lineNumber: "
				+ e.lineNumber + " fileName: " + e.fileName);
	};
}

var showWipMap=function(rid) {
	$.ajax({
		beforeSend : ajaxRequestType,
		async : true,
		url : servicePath + '?method=getwipempty',
		cache : false,
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
				if (resInfo.errors.length > 0) {
					// 共通出错信息框
					treatBackMessages(null, resInfo.errors);
				} else {
					$("#wip_pop").hide();
					$("#wip_pop").load("widgets/qf/wip_map.jsp", function(responseText, textStatus, XMLHttpRequest) {
						 //新增
				
						$("#wip_pop").dialog({
							position : [ 800, 20 ],
							title : "WIP 入库选择",
							width : 1000,
							show: "blind",
							height : 640,// 'auto' ,
							resizable : false,
							modal : true,
							minHeight : 200,
							buttons : {}
						});

						$("#wip_pop").find("td").addClass("wip-empty");
						for (var iheap in resInfo.heaps) {
							$("#wip_pop").find("td[wipid="+resInfo.heaps[iheap]+"]").removeClass("wip-empty").addClass("ui-storage-highlight wip-heaped");
						}

						//$("#wip_pop").css("cursor", "pointer");
						$("#wip_pop").find(".ui-widget-content").click(function(e){
							if ("TD" == e.target.tagName) {
								if (!$(e.target).hasClass("wip-heaped")) {
									selwip = $(e.target).attr("wipid");
									showInput();
								}
							}
						});

						$("#wip_pop").show();
					});
				}
			} catch (e) {
				alert("name: " + e.name + " message: " + e.message + " lineNumber: "
						+ e.lineNumber + " fileName: " + e.fileName);
			};
		}
	});
}

var warehousing=function() {


	var rowids = $("#list").jqGrid("getGridParam", "selarrrow");

	var data = {};

	for (var i in rowids) {
		var rowdata = $("#list").getRowData(rowids[i]);
		data["material.material_id[" + i + "]"] = rowdata["material_id"];
		data["material.break_back_flg[" + i + "]"] = rowdata["break_back_flg"];
		data["material.fix_type[" + i + "]"] = rowdata["comments"].indexOf("单元") >= 0 ? "2" : "";
	}

	$.ajax({
		beforeSend : ajaxRequestType,
		async : false,
		url : servicePath + '?method=dowarehousing',
		cache : false,
		data : data,
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : function() {
			findit();
		}
	});	
}

var doResystem=function() {

	var rowid = $("#list").jqGrid("getGridParam", "selrow");
	var rowdata = $("#list").getRowData(rowid);

	var linkna = "widgets/qf/acceptance-edit.jsp";

	$("#wip_pop").hide();
		// 导入编辑画面
	$("#wip_pop").load(linkna, function(responseText, textStatus, XMLHttpRequest) {
		 //新增
		$("#inp_modelname").show();
		$("#edit_modelname").show();
		$("#edit_label_modelname").hide();
		$("#edit_serialno").show();
		$("#edit_label_serialno").hide();
		$("#ins_material tr").hide();
		$("#ins_material tr:lt(4)").show();
		$("#ins_material tr:eq(8)").show();
		$("#ins_material tr:eq(9)").show();
		$("#edit_sorcno").val(rowdata["sorc_no"]);
		$("#edit_esasno").val(rowdata["esas_no"]);
		$("#edit_modelname").val(rowdata["model_id"]);
		$("#inp_modelname").val(rowdata["model_name"]);
		$("#direct").val(rowdata["direct_flg"]).trigger("change");
		$("#service_repair").val(rowdata["service_repair_flg"]).trigger("change");
		$("#edit_serialno").val(rowdata["serial_no"]);

		$("#direct,#service_repair,#fix_type,#edit_ocm,#edit_level,#edit_storager").select2Buttons();
		$("#referchooser_edit").html( $("#model_refer1").html());
		setReferChooser($("#edit_modelname"), $("#referchooser_edit"));
		$(".ui-button[value='清空']").button();
		$("#wip_pop").dialog({
			position : [ 800, 20 ],
			title : "维修对象信息编辑",
			width : 400,
			show: "blind",
			height : 'auto' ,
			resizable : false,
			modal : true,
			minHeight : 200,
			buttons : {
				"确定":function(){
					var data = {
						"material_id":rowdata["material_id"],
						"sorc_no":$("#edit_sorcno").val(),
						"esas_no":$("#edit_esasno").val(),
						"model_id":$("#edit_modelname").val(),
						"serial_no":$("#edit_serialno").val(),
						"direct_flg":$("#direct").val() == "" ? "0" : $("#direct").val(),
						"service_repair_flg":$("#service_repair").val()
					}
					$.ajax({
						beforeSend : ajaxRequestType,
						async : false,
						url : servicePath + '?method=doresystem',
						cache : false,
						data : data,
						type : "post",
						dataType : "json",
						success : ajaxSuccessCheck,
						error : ajaxError,
						complete : insert_handleComplete
					});
				}, "关闭" : function(){ $(this).dialog("close"); }
			}
		});
		$("#wip_pop").show();
	});
};

var doStop=function() {

	var rowid = $("#list").jqGrid("getGridParam", "selrow");
	var rowdata = $("#list").getRowData(rowid);

	$("#confirmmessage").text("未修理返还后，维修对象["+encodeText(rowdata.sorc_no)+"]将会退出RVS系统中的显示，在图象检查后直接出货，确认操作吗？");
	$("#confirmmessage").dialog({
		resizable : false,
		modal : true,
		title : "返还操作确认",
		buttons : {
			"确认" : function() {
				afObj.applyProcess(242, this, doStopExecute, [rowdata]);
			},
			"取消" : function() {
				$(this).dialog("close");
			}
		}
	});
}

var doStopExecute=function(rowdata) {
	$("#confirmmessage").dialog("close");

	var data = {material_id : rowdata["material_id"]};

	$.ajax({
		beforeSend : ajaxRequestType,
		async : false,
		url : servicePath + '?method=dostop',
		cache : false,
		data : data,
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : function() {
			findit();
		}
	});	
}

var reimgcheck=function() {

	var rowid = $("#list").jqGrid("getGridParam", "selrow");
	var rowdata = $("#list").getRowData(rowid);

	var data = {material_id : rowdata["material_id"]};

	$.ajax({
		beforeSend : ajaxRequestType,
		async : false,
		url : servicePath + '?method=doImgCheck',
		cache : false,
		data : data,
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : function(xhrObj) {
			var resInfo = null;
			try {
				// 以Object形式读取JSON
				eval('resInfo =' + xhrObj.responseText);
				if (resInfo.errors.length > 0) {
					// 共通出错信息框
					treatBackMessages(null, resInfo.errors);
				}
			} catch(e) {
			}
		}
	});	
}

var showInput=function() {

	var linkna = "widgets/qf/acceptance-edit.jsp";

	$("#wip_pop").hide();
		// 导入编辑画面
	$("#wip_pop").load(linkna, function(responseText, textStatus, XMLHttpRequest) {
		 //新增
		$("#inp_modelname").show();
		$("#edit_modelname").show();
		$("#edit_label_modelname").hide();
		$("#edit_serialno").show();
		$("#edit_label_serialno").hide();

		$("#direct,#service_repair,#fix_type,#edit_ocm,#edit_level,#edit_storager").select2Buttons();
		setReferChooser($("#edit_modelname"), $("#model_refer1"));
		$(".ui-button[value='清空']").button();
		$("#fix_type").next().remove();
		$("#fix_type").html("<option value=\"3\" selected>检测备品</option>").select2Buttons();
		$("#wip_pop").dialog({
			position : [ 800, 20 ],
			title : "维修对象信息编辑",
			width : 400,
			show: "blind",
			height : 550,// 'auto' ,
			resizable : false,
			modal : true,
			minHeight : 200,
			buttons : {
				"确定":function(){
					var data = {
						"material_id":$("#material_id").val(),
						"sorc_no":$("#edit_sorcno").val(),
						"esas_no":$("#edit_esasno").val(),
						"model_id":$("#edit_modelname").val(),
						"serial_no":$("#edit_serialno").val(),
						"ocm":$("#edit_ocm").val(),
						"level":$("#edit_level").val(),
						"package_no":$("#edit_package_no").val(),
						"storager":$("#edit_storager").val(),
						"direct_flg":$("#direct").val() == "" ? "0" : $("#direct").val(),
						"service_repair_flg":$("#service_repair").val(),
						"fix_type":$("#fix_type").val(),
						"wip_location":selwip
					}
					$.ajax({
						beforeSend : ajaxRequestType,
						async : false,
						url : servicePath + '?method=doinsert',
						cache : false,
						data : data,
						type : "post",
						dataType : "json",
						success : ajaxSuccessCheck,
						error : ajaxError,
						complete : insert_handleComplete
					});
				}, "关闭" : function(){ $(this).dialog("close"); }
			}
		});
		$("#wip_pop").show();
	});
};

/**
 * 载入处理
 */
$(function() {
	$("input.ui-button").button();
	$("#cond_direct_flg").buttonset();
	$("#cond_level").select2Buttons();

	$("a.areacloser").hover(function() {$(this).addClass("ui-state-hover");
		}, function() {$(this).removeClass("ui-state-hover");});

	$("#searchbutton").addClass("ui-button-primary");
	$("#searchbutton").click(function() {
		$("#cond_sorc_no").data("post", $("#cond_sorc_no").val());
		$("#cond_model_id").data("post", $("#cond_model_id").val());
		$("#cond_serial_no").data("post", $("#cond_serial_no").val());
		$("#cond_esas_no").data("post", $("#cond_esas_no").val());
		$("#cond_reception_time_start").data("post", $("#cond_reception_time_start").val());
		$("#cond_reception_time_end").data("post", $("#cond_reception_time_end").val());
		$("#cond_direct_flg").data("post", $("#cond_direct_flg").find("input:checked").val());
		$("#cond_wip_location").data("post", $("#cond_wip_location").val());
		$("#cond_level").data("post", $("#cond_level").val());

		findit();
	});

	$("#searcharea span.ui-icon, #wiparea span.ui-icon").bind("click", function() {
		$(this).toggleClass('ui-icon-circle-triangle-n').toggleClass('ui-icon-circle-triangle-s');
		if ($(this).hasClass('ui-icon-circle-triangle-n')) {
			$(this).parent().parent().next().show("blind");
		} else {
			$(this).parent().parent().next().hide("blind");
		}
	});

	$("#searchinlinebutton").click(function() {
		$("#wiparea").show();
	});

	$("#cachebutton").click(showWipMap);
	$("#warehousingbutton").click(warehousing);
	$("#resystembutton").click(doResystem);
	$("#stopbutton").click(function(){
		afObj.applyProcess(242, this, doStop, arguments);
	});
	$("#movebutton").click(doMove);
	$("#imgcheckbutton").click(reimgcheck);

	// 清空检索条件
	$("#resetbutton").click(function() {
		// hidden reset
		$("#cond_sorc_no").val("");
		$("#cond_model_id").val("");
		$("#cond_model_name").val("");
		$("#cond_serial_no").val("");
		$("#cond_esas_no").val("");
		$("#cond_reception_time_start").val("");
		$("#cond_reception_time_end").val("");
		$("#directflg_a").attr("checked", true).trigger("change");
		$("#cond_wip_location").val("");
		$("#cond_level").val("").trigger("change");

		$("#cond_sorc_no").data("post", "");
		$("#cond_model_id").data("post", "");
		$("#cond_serial_no").data("post", "");
		$("#cond_esas_no").data("post", "");
		$("#cond_reception_time_start").data("post", "");
		$("#cond_reception_time_end").data("post", "");
		$("#cond_direct_flg").data("post", "");
		$("#cond_wip_location").data("post", "");
		$("#cond_level").data("post", "");

	});

	$("#cond_reception_time_start, #cond_reception_time_end").datepicker({
		showButtonPanel:true,
		dateFormat: "yy/mm/dd",
		currentText: "今天"
	});
	setReferChooser($("#cond_model_id"), $("#model_refer"));

	findit();

});

var doMove = function() {
	var this_dialog = $("#wip_pop");
	if (this_dialog.length === 0) {
		$("body.outer").append("<div id='wip_pop'/>");
		this_dialog = $("#wip_pop");
	}

	$.ajax({
		beforeSend : ajaxRequestType,
		async : true,
		url : "wip.do" + '?method=getwipempty',
		cache : false,
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
				if (resInfo.errors.length > 0) {
					// 共通出错信息框
					treatBackMessages(null, resInfo.errors);
				} else {
					this_dialog.hide();
					this_dialog.load("widgets/qf/wip_map.jsp", function(responseText, textStatus, XMLHttpRequest) {
					//新增
					this_dialog.dialog({
						position : [ 800, 20 ],
						title : "WIP 入库选择",
						width : 1000,
						show: "blind",
						height : 640,// 'auto' ,
						resizable : false,
						modal : true,
						minHeight : 200,
						buttons : {}
					});
			
					this_dialog.find("td").addClass("wip-empty");
					for (var iheap in resInfo.heaps) {
						this_dialog.find("td[wipid="+resInfo.heaps[iheap]+"]").removeClass("wip-empty").addClass("ui-storage-highlight wip-heaped");
					}
			
					//this_dialog.css("cursor", "pointer");
					this_dialog.find(".ui-widget-content").click(function(e){
						if ("TD" == e.target.tagName) {
							if (!$(e.target).hasClass("wip-heaped")) {
								doChangeLocation($(e.target).attr("wipid"));
								this_dialog.dialog("close");
							}
						}
					});
			
					this_dialog.show();
					});
				}
			} catch(e) {
				
			}
		}
	});
}

var doChangeLocation = function(wip_location) {
	var rowid = $("#list").jqGrid("getGridParam", "selrow");
	var rowdata = $("#list").getRowData(rowid);

	var data = {material_id : rowdata.material_id ,wip_location : wip_location};

	$.ajax({
		beforeSend : ajaxRequestType,
		async : false,
		url : servicePath + '?method=doChangelocation',
		cache : false,
		data : data,
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : insert_handleComplete
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
			treatBackMessages(null, resInfo.errors);
		} else {
			lOptions = resInfo.lOptions;
			listdata = resInfo.list;
			if ($("#gbox_list").length > 0) {
				$("#list").jqGrid().clearGridData();
				$("#list").jqGrid('setGridParam', {data : listdata}).trigger("reloadGrid", [{current : false}]);
				for (var ilistdata in listdata) {
					$("#wiparea").find("td[wipid="+listdata[ilistdata].wip_location+"]").addClass("ui-storage-highlight wip-heaped");
				}
			} else {
				$("#list").jqGrid({
					toppager : true,
					data : listdata,
					height : 231,
					width : 992,
					rowheight : 23,
					datatype : "local",
					colNames : ['', '修理单号', 'ESAS No.', '型号 ID', '型号', '机身号', '等级', '受理时间','返还要求','备注', 'WIP货架位置','','','','','wip_overceed'],
					colModel : [{name:'material_id',index:'material_id', hidden:true},
							{
								name : 'sorc_no',
								index : 'sorc_no',
								width : 105
							}, {
								name : 'esas_no',
								index : 'esas_no',
								width : 50
							}, {
								name : 'model_id',
								index : 'model_id',
								hidden : true
							}, {
								name : 'model_name',
								index : 'model_id',
								width : 125
							}, {
								name : 'serial_no',
								index : 'serial_no',
								width : 50
							}, {
								name : 'level',
								index : 'level',
								width : 35,
								align : 'center', formatter: 'select', editoptions:{value: lOptions}
							}, {
								name : 'reception_time',
								index : 'reception_time',
								width : 50,
								align : 'center', formatter:'date', formatoptions:{srcformat:'Y/m/d H:i:s',newformat:'m-d'}
							}, {
								name : 'unrepair_flg',
								index : 'unrepair_flg',
								width : 50,
								align : 'center', formatter:'select', editoptions:{value:"1:Unrepair;0:"}
							}, {
								name : 'comments',
								index : 'comments',
								width : 50, formatter : function(value, options, rData){
									return rData['remark'];
		   						}
							}, {
								name : 'wip_location',
								index : 'wip_location',
								width : 80
							}, {
								name : 'wip_date',
								index : 'wip_date',
								hidden : true
							}, {
								name : 'break_back_flg',
								index : 'break_back_flg',
								hidden : true
							}, {
								name : 'direct_flg',
								index : 'direct_flg',
								hidden : true
							}, {
								name : 'service_repair_flg',
								index : 'service_repair_flg',
								hidden : true
							}, {
								name : 'operate_result',
								index : 'operate_result',
								hidden : true
							}
		
					],
					rowNum : 50,
					toppager : false,
					pager : "#listpager",
					viewrecords : true,
					caption : modelname + "一览",
					hidegrid : false,
					multiselect : true,
					gridview : true, // Speed up
					pagerpos : 'right',
					pgbuttons : true,
					pginput : false,
					recordpos : 'left',
					viewsortcols : [true, 'vertical', true],
					ondblClickRow : function(rid, iRow, iCol, e) {
						popMaterialDetail(rid, true);
					},
					onSelectRow : mappingwip,
					onSelectAll : mappingwip,
					gridComplete : function() {
						mappingwip()

						var jthis = $("#list");
						var dataIds = jthis.getDataIDs();
						var length = dataIds.length;
						for (var i = 0; i < length; i++) {
							var rowdata = jthis.jqGrid('getRowData', dataIds[i]);
							var vunrepair_flg = rowdata["unrepair_flg"];
								if (vunrepair_flg == "1") {
									$("#list tr#" + dataIds[i] + " td[aria\\-describedby='list_unrepair_flg']").css("background-color", "#FFC000");
							}
						}
					}
		
				});
			}
		}
	} catch (e){
		errorPop(e);
	}
};
