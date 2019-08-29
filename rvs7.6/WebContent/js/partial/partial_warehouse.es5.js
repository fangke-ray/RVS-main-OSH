"use strict";

var servicePath = "partial_warehouse.do";
var curProductionType = "";
var curProductionTypeName = "";
var curKey = "";

$(function () {
	$("input.ui-button").button();

	/* 为每一个匹配的元素的特定事件绑定一个事件处理函数 */
	$("#body-mdl span.ui-icon:not(.ui-icon-circle-triangle-w)").bind("click", function () {
		$(this).toggleClass('ui-icon-circle-triangle-n').toggleClass('ui-icon-circle-triangle-s');
		if ($(this).hasClass('ui-icon-circle-triangle-n')) {
			$(this).parent().parent().next().show("blind");
		} else {
			$(this).parent().parent().next().hide("blind");
		}
	});

	$("#search_warehouse_date_start,#search_warehouse_date_end,#search_finish_date_start,#search_finish_date_end").datepicker({
		showButtonPanel: true,
		dateFormat: "yy/mm/dd",
		currentText: "今天"
	});

	$("#search_step").select2Buttons();

	$("#resetbutton").click(reset);

	$("#searchbutton").click(findit);

    $("#importButton").click(function(){
		afObj.applyProcess(212, this, uploadFile, arguments);
    });

    $("#collationOnShelfButton").click(function(){
		afObj.applyProcess(213, this, onShelf, arguments);
    });
    
    $("#unpackButton").click(function(){
		afObj.applyProcess(214, this, unpack, arguments);
    });

	$("#gobackbutton,#detail span.ui-icon").click(function () {
		$("#search").show();
		$("#detail").hide();
	});

	$("#workarea span.ui-icon").click(function () {
		return $("#search").show().siblings().hide();
	});

	$("#confirmButton").click(doConfirm);

	findit();
});

function doConfirm() {
	var data = {
		"key": curKey,
		"production_type": curProductionType
	};

	$("#content tr[spec_kind]").each(function (index, tr) {
		var $tr = $(tr);
		data["fact_partial_warehouse.spec_kind[" + index + "]"] = $tr.attr("spec_kind");
		data["fact_partial_warehouse.quantity[" + index + "]"] = $tr.find("input[type='text']").val().trim();
	});

	afObj.applyProcess(curProductionType, this, doConfirmExecute, [data]);
};

function doConfirmExecute(data){
	$.ajax({
		beforeSend : ajaxRequestType,
		async : true,
		url : servicePath + '?method=doConfirm',
		cache : false,
		data : data,
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : function(xhrobj, textStatus) {
			var resInfo = null;
			try {
				// 以Object形式读取JSON
				eval('resInfo =' + xhrobj.responseText);
				if (resInfo.errors.length > 0) {
					// 共通出错信息框
					treatBackMessages(null, resInfo.errors);
				} else {
					$("#search").show().siblings().hide();
					findit();
				}
			}catch(e){
				console.log("doConfirm:" + e.message);
			}
		}
	});
}

function unpack() {
	searchByStep("0,2", "分装", function () {
		curProductionType = "214";
		curProductionTypeName = "分装";
	});
};

// 核对上架
function onShelf() {
	// 点击开始按钮时候，应该做check
	searchByStep("0,1", "核对/上架", function () {
		curProductionType = "213";
		curProductionTypeName = "核对/上架";
	});
};

function searchByStep(step, title, callback) {
	var data = {
		"step": step
	};

	$.ajax({
		beforeSend: ajaxRequestType,
		async: true,
		url: servicePath + '?method=searchByStep',
		cache: false,
		data: data,
		type: "post",
		dataType: "json",
		success: ajaxSuccessCheck,
		error: ajaxError,
		complete: function complete(xhrobj, textStatus) {
			var resInfo = null;
			try {
				// 以Object形式读取JSON
				eval('resInfo =' + xhrobj.responseText);
				if (resInfo.errors.length > 0) {
					// 共通出错信息框
					treatBackMessages(null, resInfo.errors);
				} else {
					var _list = resInfo.list;

					if (_list.length == 0) {
						infoPop("没有需要" + title + "的入库单。");
						return;
					}

					var $dialog = $("#partial_warehouse_dialog");
					var content = "<table class=\"condform\">\n\t\t\t\t\t\t\t\t\t<tbody>\n\t\t\t\t\t\t\t\t\t<tr class=\"align-center\">\n\t\t\t\t\t\t\t\t   \t\t<td class=\"ui-state-default td-title\">DN \u7F16\u53F7</td>\n\t\t\t\t\t\t\t\t   \t\t<td class=\"ui-state-default td-title\">\u5165\u5E93\u5355\u65E5\u671F</td>\n\t\t\t\t\t\t\t\t   \t\t<td class=\"ui-state-default td-title\"></td>\n\t\t\t\t\t\t\t\t    </tr>";

					_list.forEach(function (item) {
						content += "<tr key=\"" + item.key + "\"> \n\t\t\t\t\t\t\t\t\t\t<td class=\"td-content\">" + item.dn_no + "</td>\n\t\t\t\t\t\t\t\t\t\t<td class=\"td-content align-center\">" + item.warehouse_date + "</td>\n\t\t\t\t\t\t\t\t\t\t<td class=\"td-content align-center\">\n\t\t\t\t\t\t\t\t\t\t\t<input type=\"button\" class=\"ui-button\" value=\"\u9009\u62E9\">\n\t\t\t\t\t\t\t\t\t\t</td>\n\t\t\t\t\t\t\t\t  </tr>";
					});
					content += "</tbody></table>";

					$dialog.html(content);
					$dialog.find("input[type='button']").button().removeClass("ui-state-hover").each(function (index, button) {
						$(button).click(function () {
							var $tr = $(button).closest("tr");
							chooseWarehouse($tr.attr("key"), $dialog);
						});
					});

					$dialog.dialog({
						resizable: false,
						modal: true,
						title: title,
						width: 400,
						buttons: {
							"取消": function _() {
								return $dialog.dialog("close");
							}
						}
					});

					if (callback) callback();
				}
			} catch (e) {}
		}
	});
};

function chooseWarehouse(key, $dialog) {
	var data = {
		"key": key,
		"production_type": curProductionType
	};

	$.ajax({
		beforeSend: ajaxRequestType,
		async: true,
		url: servicePath + '?method=searchWorkInfo',
		cache: false,
		data: data,
		type: "post",
		dataType: "json",
		success: ajaxSuccessCheck,
		error: ajaxError,
		complete: function complete(xhrobj, textStatus) {
			var resInfo = null;
			try {
				// 以Object形式读取JSON
				eval('resInfo =' + xhrobj.responseText);
				if (resInfo.errors.length > 0) {
					// 共通出错信息框
					treatBackMessages(null, resInfo.errors);
				} else {
					if ($dialog) $dialog.dialog("close");
					if (curKey != key) curKey = key;

					setWorkContent(resInfo);
				}
			} catch (e) {}
		}
	});
};

function setWorkContent(resInfo) {
	var partialWarehouseForm = resInfo.partialWarehouseForm;
	var list = resInfo.list;
	var kindList = resInfo.kindList;
	var factList = resInfo.factList;
	var curFactList = resInfo.curFactList;

	$("#modelName").text(curProductionTypeName);

	var $content = $("#content");
	$content.find("tr:eq(1) td:eq(0)").text(partialWarehouseForm.warehouse_date);
	$content.find("tr:eq(1) td:eq(1)").text(partialWarehouseForm.dn_no);
	$content.find("tr:gt(1)").remove();

	var content = "<tr>\n\t\t\t\t\t<td class=\"ui-state-default td-title\">\u89C4\u683C\u79CD\u522B</td>\n\t\t\t\t\t<td class=\"ui-state-default td-title\">" + curProductionTypeName + "\u603B\u6570</td>\n\t\t\t\t\t<td class=\"ui-state-default td-title\">\u4E0A\u6B21" + curProductionTypeName + "\u6570\u91CF</td>\n\t\t\t\t\t<td class=\"ui-state-default td-title\">\u672C\u6B21" + curProductionTypeName + "\u6570\u91CF</td>\n\t\t\t\t</tr>";

	kindList.forEach(function (item) {
		var spec_kind = item.spec_kind;
		var spec_kind_name = item.spec_kind_name;
		var quantity = item.quantity;

		content += "<tr spec_kind = \"" + spec_kind + "\">\n\t\t\t\t\t\t<td class=\"ui-state-default td-title\">" + spec_kind_name + "</td>\n\t\t\t\t\t\t<td class=\"td-content\">" + quantity + "</td>\n\t\t\t\t\t\t<td class=\"td-content quantity\"></td>\n\t\t\t\t\t\t<td class=\"td-content\"><input type=\"text\" class=\"ui-widget-content\"></td>\n\t\t\t\t   </tr>";
	});
	$content.append(content);

	factList.forEach(function (item) {
		return $("#content tr[spec_kind='" + item.spec_kind + "']").find("td.quantity").text(item.quantity);
	});

	curFactList.forEach(function (item) {
		return $("#content tr[spec_kind='" + item.spec_kind + "']").find("input[type='text']").val(item.quantity);
	});

	worklist(list);

	var tname = ['split_quantity', 'split_total_quantity'];
	if (curProductionType == 213) {
		$("#worklist").jqGrid('hideCol', tname);
	} else {
		$("#worklist").jqGrid('showCol', tname);
	}
	$("#worklist").jqGrid('setGridWidth', '992');

	$("#workarea").show().siblings().hide();
};

function worklist(listdata) {
	if ($("#gbox_worklist").length > 0) {
		$("#worklist").jqGrid().clearGridData(); // 清除
		$("#worklist").jqGrid('setGridParam', { data: listdata }).trigger("reloadGrid", [{ current: false }]); // 刷新列表
	} else {
		$("#worklist").jqGrid({
			data: listdata, // 数据
			height: 691, // rowheight*rowNum+1
			width: 992,
			rowheight: 23,
			datatype: "local",
			colNames: ['零件编号', '零件名称', '规格种别', '数量', '分装数量', '分装总数'],
			colModel: [{ name: 'code', index: 'code', width: 50 }, { name: 'partial_name', index: 'partial_name', width: 200 }, { name: 'spec_kind_name', index: 'spec_kind_name', align: 'center', width: 50 }, { name: 'quantity', index: 'quantity', sorttype: 'integer', width: 50, align: 'right' }, { name: 'split_quantity', index: 'split_quantity', hidden: true, sorttype: 'integer', width: 50, align: 'right' }, { name: 'split_total_quantity', index: 'split_total_quantity', hidden: true, sorttype: 'integer', width: 50, align: 'right', formatter: function formatter(value, options, rData) {
					return Math.ceil(rData.quantity * 1 / (rData.split_quantity * 1));
				} }],
			rowNum: 30,
			toppager: false,
			pager: "#worklistpager",
			viewrecords: true,
			caption: "",
			multiselect: false,
			gridview: true,
			pagerpos: 'right',
			pgbuttons: true, // 翻页按钮
			rownumbers: true,
			pginput: false,
			recordpos: 'left',
			hidegrid: false,
			deselectAfterSort: false,
			onSelectRow: null, // 当选择行时触发此事件。
			ondblClickRow: function ondblClickRow(rid, iRow, iCol, e) {},
			viewsortcols: [true, 'vertical', true],
			gridComplete: function gridComplete() {}
		});
	}
};

function reset() {
	$("#search_dn_no,#search_warehouse_date_start,#search_warehouse_date_end,#search_finish_date_start,#search_finish_date_end").val("");
	$("#search_step").val("").trigger("change");
};

function findit() {
	var data = {
		"dn_no": $("#search_dn_no").val(),
		"warehouse_date_start": $("#search_warehouse_date_start").val(),
		"warehouse_date_end": $("#search_warehouse_date_end").val(),
		"finish_date_start": $("#search_finish_date_start").val(),
		"finish_date_end": $("#search_finish_date_end").val(),
		"step": $("#search_step").val()
	};

	$.ajax({
		beforeSend: ajaxRequestType,
		async: true,
		url: servicePath + '?method=search',
		cache: false,
		data: data,
		type: "post",
		dataType: "json",
		success: ajaxSuccessCheck,
		error: ajaxError,
		complete: function complete(xhrobj, textStatus) {
			var resInfo = null;
			try {
				// 以Object形式读取JSON
				eval('resInfo =' + xhrobj.responseText);
				if (resInfo.errors.length > 0) {
					// 共通出错信息框
					treatBackMessages(null, resInfo.errors);
				} else {
					list(resInfo.finish);
				}
			} catch (e) {}
		}
	});
};

function list(listdata) {
	if ($("#gbox_list").length > 0) {
		$("#list").jqGrid().clearGridData(); // 清除
		$("#list").jqGrid('setGridParam', { data: listdata }).trigger("reloadGrid", [{ current: false }]); // 刷新列表
	} else {
		$("#list").jqGrid({
			data: listdata, // 数据
			height: 461, // rowheight*rowNum+1
			width: 992,
			rowheight: 23,
			shrinkToFit: true,
			datatype: "local",
			colNames: ['', '入库单日期', 'DN 编号', '入库单总数量', '核对总数量', '入库进展', '核对上架一致'],
			colModel: [{ name: 'key', index: 'key', hidden: true }, { name: 'warehouse_date', index: 'warehouse_date', width: 30, align: 'center' }, { name: 'dn_no', index: 'dn_no', width: 50 }, { name: 'quantity', index: 'quantity', align: 'right', width: 50, formatter: 'integer', sorttype: 'integer', formatoptions: { thousandsSeparator: ',' } }, { name: 'collation_quantity', index: 'collation_quantity', align: 'right', width: 50, formatter: 'integer', sorttype: 'integer', formatoptions: { thousandsSeparator: ',' } }, { name: 'step', index: 'step', align: 'center', width: 30, formatter: 'select', editoptions: { value: $("#goStep").val() } }, { name: 'match', index: 'match', align: 'center', formatter: function formatter(value, options, rData) {
					var step = rData.step;
					if (step != 0) {
						if (rData.quantity == rData.collation_quantity) {
							return '一致';
						} else {
							return '差异';
						}
					} else {
						return '';
					}
				}, width: 30 }],
			rowNum: 20,
			toppager: false,
			pager: "#listpager",
			viewrecords: true,
			caption: "",
			multiselect: false,
			gridview: true,
			pagerpos: 'right',
			pgbuttons: true, // 翻页按钮
			rownumbers: true,
			pginput: false,
			recordpos: 'left',
			hidegrid: false,
			deselectAfterSort: false,
			onSelectRow: function onSelectRow() {},
			ondblClickRow: function ondblClickRow(rid, iRow, iCol, e) {
				var rowData = $("#list").getRowData(rid);
				showDetail(rowData.key);
			},
			viewsortcols: [true, 'vertical', true],
			gridComplete: function gridComplete() {}
		});
	}
};

function uploadFile() {
	$("#file").val("");
	$("#file_upload").dialog({
		resizable: false,
		modal: true,
		title: "上传文件",
		width: 400,
		buttons: {
			"上传": function _() {
				afObj.applyProcess(212, this, function(){
					$.ajaxFileUpload({
						url: servicePath + '?method=doUpload', // 需要链接到服务器地址
						secureuri: false,
						fileElementId: 'file', // 文件选择框的id属性
						dataType: 'json', // 服务器返回的格式
						success: function success(responseText, textStatus) {
							var resInfo = null;
							try {
								// 以Object形式读取JSON
								eval('resInfo =' + responseText);
								if (resInfo.errors.length > 0) {
									// 共通出错信息框
									treatBackMessages(null, resInfo.errors);
								} else {
									$("#file_upload").dialog("close");
									findit();
									infoPop("文件上传成功。");
								}
							} catch (e) {}
						}
					});
				}, arguments);
			},
			"取消": function _() {
				$(this).dialog("close");
			}
		}
	});
};

function showDetail(key) {
	var data = {
		"key": key
	};

	$.ajax({
		beforeSend: ajaxRequestType,
		async: true,
		url: servicePath + '?method=detail',
		cache: false,
		data: data,
		type: "post",
		dataType: "json",
		success: ajaxSuccessCheck,
		error: ajaxError,
		complete: function complete(xhrobj, textStatus) {
			var resInfo = null;
			try {
				// 以Object形式读取JSON
				eval('resInfo =' + xhrobj.responseText);
				if (resInfo.errors.length > 0) {
					// 共通出错信息框
					treatBackMessages(null, resInfo.errors);
				} else {
					$("#search").hide();
					$("#detail").show();
					detaillist(resInfo.list);
				}
			} catch (e) {}
		}
	});
};

function detaillist(listdata) {
	if ($("#gbox_detaillist").length > 0) {
		$("#detaillist").jqGrid().clearGridData(); // 清除
		$("#detaillist").jqGrid('setGridParam', { data: listdata }).trigger("reloadGrid", [{ current: false }]); // 刷新列表
	} else {
		$("#detaillist").jqGrid({
			data: listdata, // 数据
			height: 691, // rowheight*rowNum+1
			width: 992,
			rowheight: 23,
			shrinkToFit: true,
			datatype: "local",
			colNames: ['零件编号', '零件名称', '规格种别', '是否分裝', '数量'],
			colModel: [{ name: 'code', index: 'code', width: 50 }, { name: 'partial_name', index: 'partial_name', width: 200 }, { name: 'spec_kind_name', index: 'spec_kind_name', align: 'center', width: 50 }, { name: 'unpack_flg', index: 'unpack_flg', align: 'center', width: 50, formatter: function formatter(value, options, rData) {
					if (value == 1) {
						return "是";
					} else {
						return "否";
					}
				} }, { name: 'quantity', index: 'quantity', sorttype: 'integer', width: 50, align: 'right' }],
			rowNum: 30,
			toppager: false,
			pager: "#detaillistpager",
			viewrecords: true,
			caption: "",
			multiselect: false,
			gridview: true,
			pagerpos: 'right',
			pgbuttons: true, // 翻页按钮
			rownumbers: true,
			pginput: false,
			recordpos: 'left',
			hidegrid: false,
			deselectAfterSort: false,
			onSelectRow: null, // 当选择行时触发此事件。
			ondblClickRow: function ondblClickRow(rid, iRow, iCol, e) {},
			viewsortcols: [true, 'vertical', true],
			gridComplete: function gridComplete() {}
		});
	}
};