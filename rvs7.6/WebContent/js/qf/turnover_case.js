// 系统、人、环境相融合

/** 模块名 */
var modulename = "通箱库位";

/** 一览数据对象 */
var listdata = {};
/** 服务器处理路径 */
var servicePath = "turnover_case.do";
var lOptions = {};
var ocmOptions = {};
var selwip = ""; 

var findit = function() {
	var data = {
		"omr_notifi_no" : $("#cond_omr_notifi_no").data("post"),
		"model_id" : $("#cond_model_id").data("post"),
		"serial_no" : $("#cond_serial_no").data("post"),
		"location" : $("#cond_location").data("post"),
		"storage_time_start" : $("#cond_storage_time_start").data("post"),
		"storage_time_end" : $("#cond_storage_time_end").data("post"),
		"direct_flg" : $("#cond_direct_flg").data("post"),
		"bound_out_ocm" : $("#cond_bound_out_ocm").data("post")
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
var enablebuttons = function() {
	$("#placeButton").disable();
	$("#removeButton").disable();
	$("#moveButton").disable();

	var rowid = $("#list").jqGrid("getGridParam", "selrow");
	if (!rowid) {
		$("#placeButton").disable();
		$("#removeButton").disable();
		$("#moveButton").disable();
	} else {
		var rowdata = $("#list").jqGrid('getRowData', rowid);
		if (rowdata["material_id"]) {
			$("#placeButton").disable();
			$("#removeButton").enable();
			$("#moveButton").enable();
		} else {
			$("#placeButton").enable();
			$("#removeButton").disable();
			$("#moveButton").disable();
		}
	}
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
	var rowid = $("#list").jqGrid("getGridParam", "selrow");

	var rowdata = $("#list").getRowData(rowid);
	var data = {location : rowdata.location};

	if (rowdata.layer == 2) {
		doWarehousing(data);
	} else {
		// 没有在出货计划里
		warningConfirm("维修对象并未等待出货，确认要将通箱出库吗？", function(){
			doWarehousing(data);
		});
	}
}

var doWarehousing=function(data) {

	$.ajax({
		beforeSend : ajaxRequestType,
		async : false,
		url : servicePath + '?method=doWarehousing',
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

/**
 * 载入处理
 */
$(function() {
	$("input.ui-button").button();
	$("#cond_direct_flg").buttonset();

	$("a.areacloser").hover(function() {$(this).addClass("ui-state-hover");
		}, function() {$(this).removeClass("ui-state-hover");});

	$("#searcharea span.ui-icon, #wiparea span.ui-icon").bind("click", function() {
		$(this).toggleClass('ui-icon-circle-triangle-n').toggleClass('ui-icon-circle-triangle-s');
		if ($(this).hasClass('ui-icon-circle-triangle-n')) {
			$(this).parent().parent().next().show("blind");
		} else {
			$(this).parent().parent().next().hide("blind");
		}
	});

	$("#searchbutton").addClass("ui-button-primary");
	$("#searchbutton").click(function() {
		$("#cond_omr_notifi_no").data("post", $("#cond_omr_notifi_no").val());
		$("#cond_model_id").data("post", $("#cond_model_id").val());
		$("#cond_serial_no").data("post", $("#cond_serial_no").val());
		$("#cond_location").data("post", $("#cond_location").val());
		$("#cond_storage_time_start").data("post", $("#cond_storage_time_start").val());
		$("#cond_storage_time_end").data("post", $("#cond_storage_time_end").val());
		$("#cond_direct_flg").data("post", $("#cond_direct_flg").find("input:checked").val());
		$("#cond_bound_out_ocm").data("post", $("#cond_bound_out_ocm").val());

		findit();
	});

	$("#stoargeButton").click(showStoragePlan);
	$("#warehousingButton").click(showWarehousingPlan);
	$("#placeButton").click(showIdleMaterialList);
	$("#removeButton").click(warehousing);
	$("#moveButton").click(doMove);

	// 清空检索条件
	$("#resetbutton").click(function() {
		// hidden reset
		$("#cond_omr_notifi_no").val("");
		$("#cond_model_id").val("");
		$("#cond_model_name").val("");
		$("#cond_serial_no").val("");
		$("#cond_location").val("");
		$("#cond_storage_time_start").val("");
		$("#cond_storage_time_end").val("");
		$("#directflg_a").attr("checked", true).trigger("change");
		$("#cond_bound_out_ocm").val("").trigger("change");

		$("#cond_omr_notifi_no").data("post", "");
		$("#cond_model_id").data("post", "");
		$("#cond_serial_no").data("post", "");
		$("#cond_location").data("post", "");
		$("#cond_storage_time_start").data("post", "");
		$("#cond_storage_time_end").data("post", "");
		$("#cond_direct_flg").data("post", "");
		$("#cond_bound_out_ocm").data("post", "");
	});

	$("#cond_storage_time_start, #cond_storage_time_end").datepicker({
		showButtonPanel:true,
		dateFormat: "yy/mm/dd",
		currentText: "今天"
	});
	$("#cond_bound_out_ocm").select2Buttons();
	setReferChooser($("#cond_model_id"), $("#model_refer"));

	fillList();
	enablebuttons();
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
		url : servicePath + '?method=getStoargeEmpty',
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
					this_dialog.load("widgets/qf/turnover_case_storage_map.jsp", function(responseText, textStatus, XMLHttpRequest) {
					//新增
					this_dialog.dialog({
						position : [ 800, 0 ],
						title : "通箱库位选择",
						width : 1200,
						show: "blind",
						height : 640,// 'auto' ,
						resizable : false,
						modal : true,
						minHeight : 280,
						buttons : {}
					});
			
					this_dialog.find("td").addClass("storage-empty");
					for (var iheap in resInfo.heaps) {
						this_dialog.find("td[location="+resInfo.heaps[iheap]+"]").removeClass("storage-empty").addClass("ui-storage-highlight storage-heaped");
					}
			
					//this_dialog.css("cursor", "pointer");
					this_dialog.find(".ui-widget-content").click(function(e){
						if ("TD" == e.target.tagName) {
							if (!$(e.target).hasClass("storage-heaped")) {
								doChangeLocation($(e.target).attr("location"));
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

var doChangeLocation = function(location) {
	var rowid = $("#list").jqGrid("getGridParam", "selrow");
	var rowdata = $("#list").getRowData(rowid);

	var data = {material_id : rowdata.material_id ,location : location};

	$.ajax({
		beforeSend : ajaxRequestType,
		async : false,
		url : servicePath + '?method=doChangeLocation',
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
			listdata = resInfo.list;
			fillList();
		}
	} catch (e){
		alert(e);
	}
};

var fillList = function(){
	if ($("#gbox_list").length > 0) {
		$("#list").jqGrid().clearGridData();
		$("#list").jqGrid('setGridParam', {data : listdata}).trigger("reloadGrid", [{current : false}]);
	} else {
		lOptions = $("#lOptions").val();
		ocmOptions = $("#ocmOptions").val();
		$("#list").jqGrid({
			toppager : true,
			data : listdata,
			height : 483,
			width : 992,
			rowheight : 23,
			datatype : "local",
			colNames : ['', '库位位置', '修理单号', '维修状态', '型号 ID', '型号', '机身号', '等级', '发送地', '存入时间','计划','直送'],
			colModel : [{name:'material_id',index:'material_id', hidden:true}, {
						name : 'location',
						index : 'location',
						width : 60
					}, {
						name : 'omr_notifi_no',
						index : 'omr_notifi_no',
						width : 105
					}, {
						name : 'break_back_flg',
						index : 'break_back_flg',
						width : 50, align : 'center', formatter:'select', editoptions:{value:"1:转OGZ;2:未修理返还;0:正常"}
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
						name : 'bound_out_ocm',
						index : 'bound_out_ocm',
						width : 55,
						align : 'center', formatter: 'select', editoptions:{value: ocmOptions}
					}, {
						name : 'storage_time',
						index : 'storage_time',
						width : 60,
						align : 'center', formatter:'date', formatoptions:{srcformat:'Y/m/d H:i:s',newformat:'y/m/d H时'}
					}, {
						name : 'layer',
						index : 'layer',
						width : 50,
						align : 'center', formatter:'select', editoptions:{value:"1:入库;2:出库;0:"}
					}, {
						name : 'direct_flg',
						index : 'direct_flg',
						width : 50, align : 'center', formatter:'select', editoptions:{value:"1:直送;2:直送快速;0:"}
					}
	
			],
			rowNum : 21,
			toppager : false,
			pager : "#listpager",
			viewrecords : true,
			caption : modulename + "一览",
			hidegrid : false,
			multiselect : false,
			gridview : true, // Speed up
			pagerpos : 'right',
			pgbuttons : true,
			pginput : false,
			recordpos : 'left',
			viewsortcols : [true, 'vertical', true],
			onSelectRow : enablebuttons,
			onSelectAll : enablebuttons,
			gridComplete : function() {
				enablebuttons();
			}
		});
	}
}

var showIdleMaterialList = function(){
	var $this_dialog = $("#tc_idle_material_pop");
	if ($this_dialog.length === 0) {
		$("body.outer").append("<div id='tc_idle_material_pop'/>");
		$this_dialog = $("#tc_idle_material_pop");
	}
	$this_dialog.html("<table id='tc_idle_material_list'/><div id='tc_idle_material_pager'/>").hide();

	var rowid = $("#list").jqGrid("getGridParam", "selrow");
	var location = $("#list").getRowData(rowid)["location"];

	$.ajax({
		beforeSend : ajaxRequestType,
		async : false,
		url : servicePath + '?method=getIdleMaterialList',
		cache : false,
		data : null,
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : function(xhrObj){
			var resInfo = $.parseJSON(xhrObj.responseText);

			$("#tc_idle_material_list").jqGrid({
				toppager : true,
				data : resInfo.idleMaterialList,
				height : 231,
				width : 640,
				rowheight : 23,
				datatype : "local",
				colNames : ['', '修理单号',  '型号 ID', '型号', '机身号', '发送地'],
				colModel : [{name:'material_id',index:'material_id', hidden:true, key:true}, {
							name : 'omr_notifi_no',
							index : 'omr_notifi_no',
							hidden : true
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
							name : 'bound_out_ocm',
							index : 'bound_out_ocm',
							width : 55,
							align : 'center' , formatter: 'select', editoptions:{value: ocmOptions}
						}
				],
				rowNum : 21,
				toppager : false,
				pager : "#tc_idle_material_pager",
				viewrecords : true,
				caption : "通箱未入库维修对象一览",
				hidegrid : false,
				multiselect : false,
				gridview : true, // Speed up
				pagerpos : 'right',
				pgbuttons : true,
				pginput : false,
				recordpos : 'left',
				viewsortcols : [true, 'vertical', true],
				onSelectRow : null,
				onSelectAll : null,
				gridComplete : null
			});

			$this_dialog.dialog({
				position : 'auto',
				title : "通箱调整出库",
				width : 680,
				show: "blind",
				height : 440,// 'auto' ,
				resizable : false,
				modal : true,
				minHeight : 200,
				buttons : {
					"确认" : function(){
						var rowid = $("#tc_idle_material_list").jqGrid("getGridParam", "selrow");
						if (rowid == undefined) {
							$this_dialog.dialog("close");
							return;
						}
						doPutin($this_dialog, location, rowid);
					},"关闭": function(){
						$this_dialog.dialog("close");
					}
				}
			});
		}
	});
}

var doPutin = function($this_dialog, location, material_id) {
	var data = {
		location : location,
		material_id : material_id
	};

	$.ajax({
		beforeSend : ajaxRequestType,
		async : true,
		url : servicePath + '?method=doPutin',
		cache : false,
		data : data,
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : function(xhrObj){
			$this_dialog.dialog("close");
			findit();
		}
	})	
} 
