/* 提交路径 */
var servicePath = "disassemble_storage.do";

/* 入口 */
$(function() {
	/* 将Select转换成Button */
	$("#searchform select").select2Buttons();
	$("#editform select").select2Buttons();

	$("#edit_auto_arrange").buttonset();

	/* Button */
	$("input.ui-button").button();
	/* 检索条件的右上角的超链接将鼠标移动上去和移走鼠标后的样式 */
	$("a.areacloser").hover(function() {
		$(this).addClass("ui-state-hover");
	}, function() {
		$(this).removeClass("ui-state-hover");
	});

	$("#body-mdl span.ui-icon").bind("click", function() {
		$(this).toggleClass('ui-icon-circle-triangle-n').toggleClass('ui-icon-circle-triangle-s');
		if ($(this).hasClass('ui-icon-circle-triangle-n')) {
			$(this).parent().parent().next().show("blind");
		} else {
			$(this).parent().parent().next().hide("blind");
		}
	});

	/* 点击检索按钮执行查询 */
	$("#searchbutton").click(function() {
		$("#search_category_id").data("post", $("#search_category_id").val());
		$("#search_position_id").data("post", $("#search_position_id").val());
		$("#search_omr_notifi_no").data("post", $("#search_omr_notifi_no").val());
		$("#search_level").data("post", $("#search_level").val());
		$("#search_case_code").data("post", $("#search_case_code").val());

		findit();// 调用检索方法
	});

	/* 点击重置按钮将检索条件清空 */
	$("#resetbutton").click(reset);

	$("#createbutton").click(createDialog);
	$("#changebutton").click(changeDialog);
	$("#removebutton").click(removeConfirm);

	$("#movebutton").click(getLocationMap);
	$("#warehousebutton").click(warehouseConfirm);

	$("#pos_table").on("click", ".wip-empty", function(){
		doChangeLocation($("#pos_table").attr("material_id"), 
				$("#pos_table").attr("position_id"),
				this.innerText);
	})

	findit();
});

/* 检索方法 */
var findit = function() {

	var data = {
		"category_id" : $("#search_category_id").data("post"),
		"position_id" : $("#search_position_id").data("post"),
		"omr_notifi_no" : $("#search_omr_notifi_no").data("post"),
		"level" : $("#search_level").data("post"),
		"case_code" : $("#search_case_code").data("post")
	}

	$.ajax({
		beforeSend : ajaxRequestType,
		async : true,// 异步请求
		url : servicePath + '?method=search',// 提交地址
		cache : false,// 是否缓存
		data : data,// 提交的数据
		type : "post",
		dataType : "json",// 提交的数据类型
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : findit_ajaxSuccess
	});
};

var findit_ajaxSuccess = function(xhrobj, textStatus) {
	var resInfo = $.parseJSON(xhrobj.responseText);
	if (resInfo.errors && resInfo.errors.length) {
		// 共通出错信息框
		treatBackMessages("#searcharea", resInfo.errors);
		
		return;
	}
	setGridList(resInfo.list);
}

/* 重置 */
var reset = function() {
	$("#search_category_id").val("").trigger("change").data("post", "");
	$("#search_position_id").val("").data("post", "");
	$("#search_omr_notifi_no").val("").data("post", "");
	$("#search_level").val("").trigger("change").data("post", "");
	$("#search_case_code").val("").data("post", "");
};

var setGridList = function(listdata) {
	if ($("#gbox_exd_list").length > 0) {
		$("#exd_list").jqGrid().clearGridData();// 清空数据
		$("#exd_list").jqGrid("setGridParam", {data : listdata})
			.trigger("reloadGrid", [{current : false}]);// 重新加载数据
	} else {
		$("#exd_list").jqGrid({
			data : listdata,
			width : 992,
			height : 461,
			rowhight : 23,// 行高
			datatype : 'local',// 从服务器端返回的数据类型
			colNames : ['material_id', 'position_id', '所属工位', '货架', '库位', 'auto_arrange' ,'layer','机种', '修理单号', '机身号'],
			colModel : [{
						name : 'material_id',
						index : 'material_id',
						hidden : true
					}, {
						name : 'position_id',
						index : 'position_id',
						hidden : true
					}, {
						name : 'process_code',
						index : 'process_code',
						width : 35,
						align : 'left'
					}, {
						name : 'shelf',
						index : 'shelf',
						width : 25,
						align : 'left'
					}, {
						name : 'case_code',
						index : 'case_code',
						width : 40,
						align : 'left'
					}, {
						name : 'auto_arrange',
						index : 'auto_arrange',
						hidden : true
					}, {
						name : 'layer',
						index : 'layer',
						hidden : true
					}, {
						name : 'category_name',
						index : 'category_name',
						width : 60,
						algin : 'left'
					}, {
						name : 'omr_notifi_no',
						index : 'omr_notifi_no',
						width : 60,
						align : 'left'
					}, {
						name : 'serial_no',
						index : 'serial_no',
						width : 40,
						align : 'left'
					}],
			pager : "#exd_listpager",
			viewrecords : true,// 设置是否在page bar 显示所有记录的总数
			rowNum : 20,
			hidegrid : false,//
			gridview : true, // 
			pagerpos : 'right',// 指定分页栏的位置
			pgbuttons : true,// 是否显示翻页按钮
			pginput : false,// 是否显示跳转页面的输入框
			recordpos : 'left',// 显示记录的位置
			onSelectRow : enablebuttons,
			gridComplete: enablebuttons
		});
	}
}

/** 根据条件使按钮有效/无效化 */
var enablebuttons = function() {
	var rowid = $("#exd_list").jqGrid("getGridParam", "selrow");
	if (!rowid) {
		$("#changebutton, #removebutton, #warehousebutton, #movebutton").disable();
	} else {
		$("#changebutton, #removebutton").enable();
		var rowData = $("#exd_list").getRowData(rowid);
		if (rowData.material_id) {
			$("#warehousebutton, #movebutton").enable();
		} else {
			$("#warehousebutton, #movebutton").disable();
		}
	}
};

var getLocationMap = function() {
	var rowid = $("#exd_list").jqGrid("getGridParam", "selrow");
	var rowData = $("#exd_list").getRowData(rowid);

	var postData = {
		material_id : rowData.material_id,
		position_id : rowData.position_id
	};

	$.ajax({
		beforeSend : ajaxRequestType,
		async : false,
		url : servicePath + '?method=getLocationMap',
		cache : false,
		data : postData,
		type : "post",
		dataType : "json",// 服务器返回的数据类型
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : function(xhrobj, textStatus){
			getLocationMap_handleComplete(xhrobj, rowData);
		}
	});
}

var getLocationMap_handleComplete = function(xhrobj, rowData) {
	var resInfo = $.parseJSON(xhrobj.responseText);
	if (resInfo.errors && resInfo.errors.length) {
		// 共通出错信息框
		treatBackMessages(null, resInfo.errors);
		
		return;
	}

	$("#pos_storage").text(rowData.process_code + " 库位一览");
	$("#pos_table").html(resInfo.storageHtml)
		.attr({material_id : rowData.material_id,
			position_id : rowData.position_id});

	var $dialog = $("#storage_pop").dialog({
		title : "移动库位",
		width : 920,
		height : 480,
		resizable : false,
		modal : true,
		buttons : {
			"关闭" : function(){
				$dialog.dialog("close");
			}
		}
	});
}

/** 改变库位* */
var doChangeLocation = function(material_id, position_id, case_code) {
	var postData = {
		material_id : material_id,
		position_id : position_id,
		case_code : case_code
	};
	$.ajax({
		beforeSend : ajaxRequestType,
		async : false,
		url : servicePath + '?method=doChangelocation',
		cache : false,
		data : postData,
		type : "post",
		dataType : "json",// 服务器返回的数据类型
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : doChangeLocation_handleComplete
	});
};

var doChangeLocation_handleComplete = function(xhrobj, textStatus) {
	var resInfo = $.parseJSON(xhrobj.responseText);
	if (resInfo.errors && resInfo.errors.length) {
		// 共通出错信息框
		treatBackMessages(null, resInfo.errors);
		
		return;
	}
	$("#storage_pop").dialog("close");
	findit();
}

var createDialog = function() {
	$("#edit_position_id").val("").trigger("change");
	$("#editarea input:text").val("");
	$("#edit_auto_arrange_y").attr("checked", "checked").trigger("change");

	var $dialog = $("#editarea").dialog({
		title : "建立库位",
		height :  'auto',
		resizable : false,
		modal : true,
		buttons : {
			"建立" : postCreate,
			"关闭" : function(){
				$dialog.dialog("close");
			}
		}
	});
}

var postCreate = function(){
	var postData = {
		position_id : $("#edit_position_id").val(),
		case_code : $("#edit_case_code").val(),
		auto_arrange : $("#edit_auto_arrange > input:radio[checked]").val(),
		shelf : $("#edit_shelf").val(),
		layer : $("#edit_layer").val()
	}

	$.ajax({
		beforeSend : ajaxRequestType,
		async : false,
		url : servicePath + '?method=doCreate',
		cache : false,
		data : postData,
		type : "post",
		dataType : "json",// 服务器返回的数据类型
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : create_handleComplete
	});	
}

var create_handleComplete = function(xhrobj, textStatus) {
	var resInfo = $.parseJSON(xhrobj.responseText);
	if (resInfo.errors && resInfo.errors.length) {
		// 共通出错信息框
		treatBackMessages("#searcharea", resInfo.errors);
		
		return;
	}
	$("#editarea").dialog("close");
	findit();
}

var changeDialog = function() {
	var rowid = $("#exd_list").jqGrid("getGridParam", "selrow");
	var rowData = $("#exd_list").getRowData(rowid);

	$("#edit_position_id").val(rowData.position_id).trigger("change");
	$("#edit_case_code").val(rowData.case_code);
	if (rowData.auto_arrange == 1) {
		$("#edit_auto_arrange_y").attr("checked", true).trigger("change");
	} else {
		$("#edit_auto_arrange_n").attr("checked", true).trigger("change");
	}
	$("#edit_material_id").val(rowData.material_id);
	$("#edit_shelf").val(rowData.shelf);
	$("#edit_layer").val(rowData.layer);

	var $dialog = $("#editarea").dialog({
		title : "调整库位",
		height :  'auto',
		resizable : false,
		modal : true,
		buttons : {
			"修改" : postChange,
			"关闭" : function(){
				$dialog.dialog("close");
			}
		}
	});
}

var postChange = function(){
	var postData = {
		position_id : $("#edit_position_id").val(),
		case_code : $("#edit_case_code").val(),
		auto_arrange : $("#edit_auto_arrange > input:radio[checked]").val(),
		shelf : $("#edit_shelf").val(),
		layer : $("#edit_layer").val()
	}

	$.ajax({
		beforeSend : ajaxRequestType,
		async : false,
		url : servicePath + '?method=doChange',
		cache : false,
		data : postData,
		type : "post",
		dataType : "json",// 服务器返回的数据类型
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : change_handleComplete
	});	
}

var change_handleComplete = function(xhrobj, textStatus) {
	var resInfo = $.parseJSON(xhrobj.responseText);
	if (resInfo.errors && resInfo.errors.length) {
		// 共通出错信息框
		treatBackMessages("#searcharea", resInfo.errors);
		
		return;
	}
	$("#editarea").dialog("close");
	findit();
}

var removeConfirm = function() {
	var rowid = $("#exd_list").jqGrid("getGridParam", "selrow");
	var rowData = $("#exd_list").getRowData(rowid);

	if(rowData.material_id.length == 0) {
		warningConfirm("确认要删除库位【" + rowData.case_code + "】？", 
			function() {
				postRemove(rowData);
			},	null, "确认删除");
	} else {
		errorPop("请先移出存放在此库位的维修品。");
	}
}

var postRemove = function(postData){

	$.ajax({
		beforeSend : ajaxRequestType,
		async : false,
		url : servicePath + '?method=doRemove',
		cache : false,
		data : postData,
		type : "post",
		dataType : "json",// 服务器返回的数据类型
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : remove_handleComplete
	});	
}

var remove_handleComplete = function(xhrobj, textStatus) {
	var resInfo = $.parseJSON(xhrobj.responseText);
	if (resInfo.errors && resInfo.errors.length) {
		// 共通出错信息框
		treatBackMessages("#searcharea", resInfo.errors);
		
		return;
	}
	findit();
}

var warehouseConfirm = function() {
	var rowid = $("#exd_list").jqGrid("getGridParam", "selrow");
	var rowData = $("#exd_list").getRowData(rowid);

	if(rowData.material_id.length > 0) {
		warningConfirm("确认要将维修品【" + rowData.omr_notifi_no + "】从【" + rowData.process_code + "】库位中取出？", 
			function() {
				postWarehouse(rowData);
			},	null, "确认出库");
	}
}

var postWarehouse = function(postData){

	$.ajax({
		beforeSend : ajaxRequestType,
		async : false,
		url : servicePath + '?method=doWarehouse',
		cache : false,
		data : postData,
		type : "post",
		dataType : "json",// 服务器返回的数据类型
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : warehouse_handleComplete
	});	
}

var warehouse_handleComplete = function(xhrobj, textStatus) {
	var resInfo = $.parseJSON(xhrobj.responseText);
	if (resInfo.errors && resInfo.errors.length) {
		// 共通出错信息框
		treatBackMessages("#searcharea", resInfo.errors);
		
		return;
	}
	findit();
}