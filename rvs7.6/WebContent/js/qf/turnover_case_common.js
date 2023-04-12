/** 服务器处理路径 */
var tcServicePath = "turnover_case.do";

var showWarehousingPlan = function(){
	var $this_dialog = $("#tc_warehousing_pop");
	if ($this_dialog.length === 0) {
		$("body.outer").append("<div id='tc_warehousing_pop'/>");
		$this_dialog = $("#tc_warehousing_pop");
	}
	$this_dialog.html("<table id='tc_warehousing_list'/><div id='tc_warehousing_list_pager'/>").hide();

	$.ajax({
		beforeSend : ajaxRequestType,
		async : true,
		url : tcServicePath + '?method=getWarehousingPlanList',
		cache : false,
		data : null,
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : function(xhrObj){
			var resInfo = $.parseJSON(xhrObj.responseText);

			$("#tc_warehousing_list").jqGrid({
				toppager : true,
				data : resInfo.warehousingPlanList,
				height : 231,
				width : 640,
				rowheight : 23,
				datatype : "local",
				colNames : ['', '库位位置',  '型号 ID', '型号', '机身号', '发送地', '修理结果'],
				colModel : [{name:'material_id',index:'material_id', hidden:true}, {
							name : 'location',
							index : 'location',
							width : 60
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
							align : 'center'// , formatter: 'select', editoptions:{value: ocmOptions}
						}, {
							name : 'execute',
							index : 'execute',
							width : 50,
							align : 'center', formatter:'select', editoptions:{value:"2:未修理返还;11:1课完成;12:2课完成;13:周边｜硬性镜;0:修理完成"}
						}
				],
				rowNum : 21,
				toppager : false,
				pager : "#tc_warehousing_list_pager",
				viewrecords : true,
				caption : "计划出货通箱一览",
				hidegrid : false,
				multiselect : true,
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
				title : "通箱计划出库",
				width : 680,
				show: "blind",
				height : 440,// 'auto' ,
				resizable : false,
				modal : true,
				minHeight : 200,
				buttons : {
					"确认" : function(){
						var rowids = $("#tc_warehousing_list").jqGrid("getGridParam", "selarrrow");
						if (rowids.length == 0) {
							$this_dialog.dialog("close");
							return;
						}
						doWarehousingPlanned($this_dialog, rowids);
					},"关闭": function(){
						$this_dialog.dialog("close");
					}
				}
			});
		}
	});
}

var doWarehousingPlanned = function($this_dialog, rowids) {
	var data = {};
	for (var iId in rowids) {
		var rowdata = $("#tc_warehousing_list").getRowData(rowids[iId]);
		data["turnover_case.location[" + iId + "]"] = rowdata["location"];
	}

	$.ajax({
		beforeSend : ajaxRequestType,
		async : true,
		url : tcServicePath + '?method=doWarehousingPlanned',
		cache : false,
		data : data,
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : function(xhrObj){
			$this_dialog.dialog("close");
			if(typeof findit === "function") findit();
			if(typeof doShippingInit === "function") doShippingInit();
		}
	})	
}

var showStoragePlan = function(){
	var $this_dialog = $("#tc_storage_pop");
	if ($this_dialog.length === 0) {
		$("body.outer").append("<div id='tc_storage_pop'/>");
		$this_dialog = $("#tc_storage_pop");
	}
	$this_dialog.html("<table id='tc_storage_list'/><div id='tc_storage_list_pager'/>").hide();

	$.ajax({
		beforeSend : ajaxRequestType,
		async : true,
		url : tcServicePath + '?method=getStoragePlanList',
		cache : false,
		data : null,
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : function(xhrObj){
			var resInfo = $.parseJSON(xhrObj.responseText);

			$("#tc_storage_list").jqGrid({
				toppager : true,
				data : resInfo.storagePlanList,
				height : 231,
				width : 640,
				rowheight : 23,
				datatype : "local",
				colNames : ['', '预定库位位置',  '型号 ID', '型号', '机身号', '发送地'],
				colModel : [{name:'material_id',index:'material_id', hidden:true}, {
							name : 'location',
							index : 'location',
							width : 60
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
							align : 'center'// , formatter: 'select', editoptions:{value: ocmOptions}
						}
				],
				rowNum : 21,
				toppager : false,
				pager : "#tc_storage_list_pager",
				viewrecords : true,
				caption : "计划入库通箱一览",
				hidegrid : false,
				multiselect : true,
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
				title : "通箱计划入库",
				width : 680,
				show: "blind",
				height : 440,// 'auto' ,
				resizable : false,
				modal : true,
				minHeight : 200,
				buttons : {
					"确认入库" : function(){
						var rowids = $("#tc_storage_list").jqGrid("getGridParam", "selarrrow");
						if (rowids.length == 0) {
							$this_dialog.dialog("close");
							return;
						}
						doStoragePlanned($this_dialog, rowids);
					},"关闭": function(){
						$this_dialog.dialog("close");
					}
				}
			});
		}
	});
}

var doStoragePlanned = function($this_dialog, rowids) {
	var data = {};
	for (var iId in rowids) {
		var rowdata = $("#tc_storage_list").getRowData(rowids[iId]);
		data["turnover_case.location[" + iId + "]"] = rowdata["location"];
	}

	$.ajax({
		beforeSend : ajaxRequestType,
		async : true,
		url : tcServicePath + '?method=doStoragePlanned',
		cache : false,
		data : data,
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : function(xhrObj){
			$this_dialog.dialog("close");
			if(typeof findit === "function") findit();
			if(typeof doShippingInit === "function") doShippingInit();
		}
	})	
}