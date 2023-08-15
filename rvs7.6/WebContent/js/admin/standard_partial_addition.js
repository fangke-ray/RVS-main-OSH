/** 模块名 */
var modelname = "标准工时零件补正";
/** 一览数据对象 */
var listdata = {};
/** 服务器处理路径 */
var servicePath = "standard_partial_addition.do";

/**
 * 页面加载处理
 */
$(function() {
	var init = function() {
	// 适用jquery按钮
	$("input.ui-button").button();

	// 右上图标效果
	$("a.areacloser").hover(function() { $(this).addClass("ui-state-hover");},
		function() {$(this).removeClass("ui-state-hover");});
	$("#searcharea span.ui-icon").bind("click", function() {
		$(this).toggleClass('ui-icon-circle-triangle-n').toggleClass('ui-icon-circle-triangle-s');
		if ($(this).hasClass('ui-icon-circle-triangle-n')) {
			$(this).parent().parent().next().show("blind");
		} else {
			$(this).parent().parent().next().hide("blind");
		}
	});

	setReferChooser($("#search_position_id"), $("#pReferChooser"));
	setReferChooser($("#edit_position_id"), $("#pReferChooser"), null, showPositionEdit);
	setReferChooser($("#search_model_id"), $("#mReferChooser"));
	setReferChooser($("#edit_model_id"), $("#mReferChooser"), null, showModelEdit);

	// 检索处理
	$("#search_button").click(function() {
		// 保存检索条件
		$("#search_partial_code").data("post", $("#search_partial_code").val());
		$("#search_position_id").data("post", $("#search_position_id").val());
		$("#search_model_id").data("post", $("#search_model_id").val());

		// 查询
		findit();
	});

	// 清空检索条件
	$("#reset_button").click(function() {
		$("#search_partial_code").val("").data("post", "");
		$("#search_position_id").val("").data("post", "");
		$("#search_model_id").val("").data("post", "");

		$("#search_position_name").val("").data("post", "");
		$("#search_model_name").val("").data("post", "");
	});

	$("#edit_by_position_button").click(showChoosePosition);
	$("#edit_by_model_button").click(showChooseModel);

	$("#pop_window_edit > .wrapof > table > tbody").on("click", function(evt){
		var target = evt.target;
		if (target.tagName == "TD") {
			var $this = $(target);
			if ($this.hasClass("ui-widget-header")) {
				return;
			}
			if ($this.parent().children().index(target) >= 2) {
				setTdUpdate($this, $this.siblings(":eq(0)").text());
			}
		}
	});

	$("#pop_value input:text").keypress(function(evt){
		if(evt.keyCode == 13) {
			$("#pop_value").next().find("button:eq(0)").trigger("click");
		}
	});

	showList([]);
	findit();
	}

	var findit = function() {
		var postData = {
			"partial_code" : $("#search_partial_code").data("post"),
			"position_id" : $("#search_position_id").data("post"),
			"model_id" : $("#search_model_id").data("post")
		}

		// Ajax提交
		$.ajax({
			beforeSend : ajaxRequestType,
			async : true,
			url : servicePath + '?method=search',
			cache : false,
			data : postData,
			type : "post",
			dataType : "json",
			success : ajaxSuccessCheck,
			error : ajaxError,
			complete : search_handleComplete
		});

	}

	var search_handleComplete = function(xhrObj){
		var resInfo = $.parseJSON(xhrObj.responseText);
		if(resInfo.errors && resInfo.errors.length) {
			// 共通出错信息框
			treatBackMessages("#searcharea", resInfo.errors);

			return;
		}

		showList(resInfo.list);
	}

	var showList = function(listdata) {

		if ($("#gbox_list").length > 0) {
			// jqGrid已构建的情况下,重载数据并刷新
			$("#list").jqGrid().clearGridData();
			$("#list").jqGrid('setGridParam', {data : listdata}).trigger("reloadGrid", [{current : false}]);
		} else {
			// 构建jqGrid对象
			$("#list").jqGrid({
				data : listdata,
				height : 461,
				width : gridWidthMiddleRight,
				rowheight : 23,
				datatype : "local",
				colNames : ['', '零件 ID', '零件代码', '零件名称', '工位 ID', '工位代码', '补正值'],
				colModel : [
					{name:'myac', width:48, fixed:true, sortable:false, resize:false, formatter:'actions', formatoptions:{keys:true, editbutton:false}},
					{name:'partial_id',index:'partial_id', hidden:true},
					{name:'partial_code',index:'partial_code', width:55},
					{ // 零件名称
						name : 'partial_name',
						index : 'partial_name',
						width : 100
					},
					{name:'position_id',index:'position_id', hidden:true},
					{name:'process_code',index:'process_code', width:55},
					{ // 补正值
						name : 'addition',
						index : 'addition',
						sorttype : 'integer',
						formatter : 'integer',
						align : 'right',
						width : 60
					}
				],
				toppager : false,
				rowNum : 20,
				pager : "#list_pager",
				viewrecords : true,
				caption : null,
				gridview : true, // Speed up
				pagerpos : 'right',
				pgbuttons : true,
				pginput : false,
				recordpos : 'left',
				viewsortcols : [true, 'vertical', true]
			});
		}
	}

	var showChoosePosition = function() {
		$("#edit_process_code").val("");
		$("#edit_position_id").val("");

		var $pop_window = $("#pop_choose_position");
		$pop_window.dialog({
			resizable : false,
			modal : true,
			title : "选择修改设置所属工位",
			width : '640px',
			buttons : {
				"关闭" : function() {
					$pop_window.dialog("close");
				}
			}
		});
	}

	var showChooseModel = function() {
		$("#edit_model_name").val("");
		$("#edit_model_id").val("");

		var $pop_window = $("#pop_choose_model");
		$pop_window.dialog({
			resizable : false,
			modal : true,
			title : "选择修改设置相关型号",
			width : '640px',
			buttons : {
				"关闭" : function() {
					$pop_window.dialog("close");
				}
			}
		});
	}

	var setTdUpdate = function($td, p_code){
		var org_value = $td.text();
		if (!org_value || org_value == '-') {
			org_value = "0";
		}
		var upd_stat = $td.attr("upd_stat");

		var $pop_window = $("#pop_value").dialog({
			resizable : false,
			modal : true,
			title : "零件补正值设定",
			buttons : {
				"确认" : function() {
					var upd_value = $pop_window.find("input").val().trim();
					if (upd_value) {
						if (isNaN(upd_value)) {
							upd_value = 0;
						} else {
							upd_value = parseFloat(upd_value).toFixed(1);
						}
					} else {
						upd_value = 0;
					}
					if (upd_value >= 100 || upd_value <= -100) {
						errorPop("设定值超出范围。");
						$pop_window.find("input").select();
						return;
					}
					if (upd_value == 0) {
						$td.text("-");
						if (!upd_stat && org_value != "0") {
							$td.attr("upd_stat", "remv");
						} else if (upd_stat == "crea") {
							$td.removeAttr("upd_stat");
						}
					} else {
						$td.text(upd_value);
						if (!upd_stat && upd_value != org_value) {
							if (org_value == "0") {
								$td.attr("upd_stat", "crea");
							} else {
								$td.attr("upd_stat", "updt");
							}
						} else if (upd_stat != "crea" && upd_value != org_value) {
							$td.attr("upd_stat", "updt");
						}
					}
					$pop_window.dialog("close");
				},
				"清除" : function(){
					$td.text("-");
					if (!upd_stat && org_value != "0") {
						$td.attr("upd_stat", "remv");
					} else if (upd_stat == "crea") {
						$td.removeAttr("upd_stat");
					}
					$pop_window.dialog("close");
				},
				"关闭" : function() {
					$pop_window.dialog("close");
				}
			}
		});
		$pop_window.find(".td-title").text(p_code);
		$pop_window.find("input").val(org_value).select();
	}

	/*修改库存设置button事件*/
	var showPositionEdit = function() {

		var positionId = $("#edit_position_id").val();
		$("#edit_position_id").val("");
		if (!positionId) {
			return;
		}

		var postData = {
			"position_id" : positionId
		};
		$.ajax({
			beforeSend : ajaxRequestType,
			async : true,
			url : servicePath + '?method=getEditForPosition',
			cache : false,
			data : postData,
			type : "post",
			dataType : "json",
			success : ajaxSuccessCheck,
			error : ajaxError,
			complete : function(xhrobj, textStatus){
				getEditForPosition_Complete(xhrobj, textStatus, positionId)
			}
		});
	};
	var showModelEdit = function() {
		var modelId = $("#edit_model_id").val();
		$("#edit_model_id").val("");
		if (!modelId) {
			return;
		}

		var postData = {
			"model_id" : modelId
		};
		$.ajax({
			beforeSend : ajaxRequestType,
			async : true,
			url : servicePath + '?method=getEditForModel',
			cache : false,
			data : postData,
			type : "post",
			dataType : "json",
			success : ajaxSuccessCheck,
			error : ajaxError,
			complete : function(xhrobj, textStatus){
				getEditForModel_Complete(xhrobj, textStatus, modelId, $("#edit_model_name").val())
			}
		});
	};

	var getEditForPosition_Complete = function(xhrobj, textStatus, positionId){
		var resInfo = $.parseJSON(xhrobj.responseText);
		if (resInfo.errors.length > 0) {
			// 共通出错信息框
			treatBackMessages(null, resInfo.errors);
		} else {

			if (!resInfo.storageList.length) {
				errorPop("选择的工位没有零件可设置。");
				return;
			}

			var $tbody = $("#pop_window_edit > .wrapof > table > tbody");
			$("#pop_choose_position").dialog("close");

			$tbody.html(setEditForPositionTbody(resInfo.storageList));
			$tbody.attr("position_id", positionId);

			$("#pop_window_edit .process_code").text(resInfo.process_code);

			var $pop_window = $("#pop_window_edit").dialog({
				resizable : false,
				modal : true,
				title : resInfo.process_code + "工位相关零件列表",
				width : '1024px',
				buttons : {
					"更新" : function() {
						var $upd = $("#pop_window_edit > .wrapof > table > tbody td[upd_stat]");
						if ($upd.length == 0) {
							$pop_window.dialog("close");
							return;
						}
						var postData = {position_id : positionId};
						$upd.each(function(idx, ele){
							var $td = $(ele);
							var addition = $td.text();
							if (addition == "-") {
								addition = 0;
							}
							postData["update[" + idx + "].addition"] = addition;
							postData["update[" + idx + "].partial_id"] = $td.parent().attr("partial_id");
							postData["update[" + idx + "].upd"] = $td.attr("upd_stat");
							if ($td.next().length) {
								postData["update[" + idx + "].position_id"] = "0";
							}
						});
						$.ajax({
							beforeSend : ajaxRequestType,
							async : false,
							url : servicePath + '?method=doUpdate',
							cache : false,
							data : postData,
							type : "post",
							dataType : "json",
							success : ajaxSuccessCheck,
							error : ajaxError,
							complete : function(){
								$pop_window.dialog("close");
								findit();
							}
						});
					},
					"关闭" : function() {
						$pop_window.dialog("close");
					}
				}
			});
		}
	}

	var getEditForModel_Complete = function(xhrobj, textStatus, modelId, modelName) {
		var resInfo = $.parseJSON(xhrobj.responseText);
		if (resInfo.errors.length > 0) {
			// 共通出错信息框
			treatBackMessages(null, resInfo.errors);
		} else {

			if (!resInfo.storageList.length) {
				errorPop("选择的型号没有零件可设置。");
				return;
			}

			var $tbody = $("#pop_window_edit > .wrapof > table > tbody");
			$("#pop_choose_model").dialog("close");

			$("#pop_window_edit .process_code").text("特定");
			$tbody.html(setEditForModelTbody(resInfo.storageList));

			var $pop_window = $("#pop_window_edit").dialog({
				resizable : false,
				modal : true,
				title : modelName + "机型相关零件列表",
				width : '1024px',
				buttons : {
					"更新" : function() {
						var $upd = $("#pop_window_edit > .wrapof > table > tbody td[upd_stat]");
						if ($upd.length == 0) {
							$pop_window.dialog("close");
							return;
						}
						var postData = {};
						$upd.each(function(idx, ele){
							var $td = $(ele);
							var addition = $td.text();
							if (addition == "-") {
								addition = 0;
							}
							var $trt = $td.parent();
							postData["update[" + idx + "].addition"] = addition;
							postData["update[" + idx + "].partial_id"] = $trt.attr("partial_id");
							postData["update[" + idx + "].upd"] = $td.attr("upd_stat");
							postData["update[" + idx + "].position_id"] = $trt.attr("position_id");
						});
						$.ajax({
							beforeSend : ajaxRequestType,
							async : true,
							url : servicePath + '?method=doUpdate',
							cache : false,
							data : postData,
							type : "post",
							dataType : "json",
							success : ajaxSuccessCheck,
							error : ajaxError,
							complete : function(){
								$pop_window.dialog("close");
								findit();
							}
						});
					},
					"关闭" : function() {
						$pop_window.dialog("close");
					}
				}
			});
		}
	}

	var setEditForPositionTbody = function(storageList){
		var ret = "";

		if (storageList) {
			for (var i in storageList) {
				var ptl = storageList[i];
				ret += "<tr partial_id='" + ptl.partial_id + "'><td>" + ptl.partial_code +
						"</td><td>" + (ptl.partial_name || "-") +
						"</td><td>" + (ptl.base_addition ? ptl.base_addition.toFixed(1) : "-") +
						"</td><td>" + (ptl.addition ? ptl.addition.toFixed(1) : "-") + "</td></tr>";
			}
		}

		return ret;
	}

	var setEditForModelTbody = function(storageList){
		var ret = "";

		if (storageList) {
			for (var i in storageList) {
				var ptl = storageList[i];
				ret += "<tr partial_id='" + ptl.partial_id + "' position_id='" + ptl.position_id + "'><td>" + ptl.partial_code +
						"</td><td>" + (ptl.partial_name || "-");
				if (ptl.process_code) {
					ret += "</td><td class='ui-widget-header'>" + ptl.process_code + " 》";
				}
				ret += "</td><td>" + (ptl.addition ? ptl.addition : "-") + "</td></tr>";
			}
		}

		return ret;
	}

	init();
});

var showDelete = function(rid) {

	// 读取删除行
	var rowData = $("#list").getRowData(rid);
	var data = {
		"partial_id" : rowData.partial_id,
		"position_id" : rowData.position_id
	}

	warningConfirm("确认要删除当前设置吗？",
		function() {
			// Ajax提交
			$.ajax({
				beforeSend : ajaxRequestType,
				async : false,
				url : servicePath + '?method=doDelete',
				cache : false,
				data : data,
				type : "post",
				dataType : "json",
				success : ajaxSuccessCheck,
				error : ajaxError,
				complete : update_handleComplete
			});
		}, null, "删除确认"
	);
}
