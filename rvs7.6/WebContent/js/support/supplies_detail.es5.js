"use strict";

var _slicedToArray = function () { function sliceIterator(arr, i) { var _arr = []; var _n = true; var _d = false; var _e = undefined; try { for (var _i = arr[Symbol.iterator](), _s; !(_n = (_s = _i.next()).done); _n = true) { _arr.push(_s.value); if (i && _arr.length === i) break; } } catch (err) { _d = true; _e = err; } finally { try { if (!_n && _i["return"]) _i["return"](); } finally { if (_d) throw _e; } } return _arr; } return function (arr, i) { if (Array.isArray(arr)) { return arr; } else if (Symbol.iterator in Object(arr)) { return sliceIterator(arr, i); } else { throw new TypeError("Invalid attempt to destructure non-iterable instance"); } }; }();

var servicePath = "supplies_detail.do";
var waringUnitPrice = 2000;
var cacheList = {};

$(function () {
	$("input.ui-button").button();
	$("#search_step_set").buttonset();

	$("#searcharea span.ui-icon").bind("click", function () {
		$(this).toggleClass('ui-icon-circle-triangle-n').toggleClass('ui-icon-circle-triangle-s');
		if ($(this).hasClass('ui-icon-circle-triangle-n')) {
			$(this).parent().parent().next().show("blind");
		} else {
			$(this).parent().parent().next().hide("blind");
		}
	});

	$("#search_section_id").select2Buttons();
	setReferChooser($("#hidden_search_applicator_id"), $("#operator_referchooser"));
	setReferChooser($("#hidden_search_confirmer_id"), $("#operator_referchooser"));

	$("#search_applicate_date_start,#search_applicate_date_end," +
			"#search_scheduled_date_start,#search_scheduled_date_end," +
			"#search_recept_date_start,#search_recept_date_end," +
			"#search_inline_recept_date_start,#search_inline_recept_date_end").datepicker({
		showButtonPanel : true,
		dateFormat : "yy/mm/dd",
		currentText : "今天"
	});

	var today = new Date();
	$("#search_budget_month").monthpicker({
		showButtonPanel: true,
		pattern: "yyyymm",
		startYear: 2013,
		finalYear: today.getFullYear(),
		selectedMonth: today.getMonth() + 1,
		monthNames: ['1月', '2月', '3月', '4月', '5月', '6月', '7月', '8月', '9月', '10月', '11月', '12月']
	});

	$("#resetbutton").click(reset);

	$("#searchbutton").click(function () {
		$("#search_product_name").data("post", $("#search_product_name").val());
		$("#search_model_name").data("post", $("#search_model_name").val());
		$("#search_section_id").data("post", $("#search_section_id").val());
		$("#hidden_search_applicator_id").data("post", $("#hidden_search_applicator_id").val());
		$("#search_applicate_date_start").data("post", $("#search_applicate_date_start").val());
		$("#search_applicate_date_end").data("post", $("#search_applicate_date_end").val());
		$("#hidden_search_confirmer_id").data("post", $("#hidden_search_confirmer_id").val());
		$("#search_order_no").data("post", $("#search_order_no").val());
		$("#search_scheduled_date_start").data("post", $("#search_scheduled_date_start").val());
		$("#search_scheduled_date_end").data("post", $("#search_scheduled_date_end").val());
		$("#search_recept_date_start").data("post", $("#search_recept_date_start").val());
		$("#search_recept_date_end").data("post", $("#search_recept_date_end").val());
		$("#search_budget_month").data("post", $("#search_budget_month").val());
		$("#search_inline_recept_date_start").data("post", $("#search_inline_recept_date_start").val());
		$("#search_inline_recept_date_end").data("post", $("#search_inline_recept_date_end").val());
		$("#search_invoice_no").data("post", $("#search_invoice_no").val());

		var step = 0;
		$("#search_step_set > input:checked").each(function() {step+=parseInt(this.value)})
		$("#search_step_set").data("post", step);

		findit();
	});

	//申请事件
	$("#applicationButton").click(addDetailDialog);

	$("#filterSearchButton").click(filterSupplise);

	$("#filter_product_name").keyup(function (evt) {
		if (!this.value) {
			$("#filterContainer > ul li").fadeIn();
		} else if (evt.keyCode === 13) {
			$("#filterSearchButton").trigger("click");
		}
	});

	//编辑订购单
	$("#editButton").click(editSupplise);

	$("#updatearea span.ui-icon").click(function () {
		$("#searcharea").show();
		$("#updatearea").hide();
	});

	//收货
	$("#receptButton").click(recept);

	//验收
	$("#inlineReceptbutton").click(inlineRecept);

	//发票
	$("#invoiceButton").click(invoice);

	//确认订购单
	$("#confirmOrderButton").click(function () {
		confirmOrder(true);
	});

	$("#btn_self_applicator").click(function(){
		var self_id = $("#loginID").val() || $("#op_id").val();
		if (self_id) {
			var $selfTr = $("#operator_referchooser .referId:contains("+ self_id +")").parent();
			if ($selfTr.length > 0) {
				$("#search_applicator_name").val($selfTr.children(":eq(1)").text());
				$("#hidden_search_applicator_id").val(self_id);
			}
		}
	});

	// 发送通知
	$("#postDialog table tr:not(:contains('经理'))").remove();
	$("#postButton").click(selPostage);
	$("#postDialog table").on("click", function(evt){
		if(evt.target) {
			var managerId = $(evt.target).closest("tr").children(".referId").text();
			if (managerId) {
				postMessage(managerId);
				$("#postDialog").dialog("close");
			}
		}
	})

	// 进行状态
	var step = 0;
	$("#search_step_set > input:checked").each(function() {step+=parseInt(this.value)})
	$("#search_step_set").data("post", step);
	$("#step_reset").val(step);

	// 批量同意
	$("#batchOkButton").click(doBatchOk);

	findit();

	getReferList();
});

/**
 * 清除
 */
function reset() {
	$("#search_product_name").data("post", "").val("");
	$("#search_model_name").data("post", "").val("");
	$("#search_section_id").data("post", "").val("").trigger("change");
	$("#hidden_search_applicator_id").data("post", "").val("");
	$("#search_applicator_name").val("");
	$("#search_applicate_date_start").data("post", "").val("");
	$("#search_applicate_date_end").data("post", "").val("");
	$("#hidden_search_confirmer_id").data("post", "").val("");
	$("#search_confirmer_name").val("");
	$("#search_order_no").data("post", "").val("");
	$("#search_scheduled_date_start").data("post", "").val("");
	$("#search_scheduled_date_end").data("post", "").val("");
	$("#search_recept_date_start").data("post", "").val("");
	$("#search_recept_date_end").data("post", "").val("");
	$("#search_budget_month").data("post", "").val("");
	$("#search_inline_recept_date_start").data("post", "").val("");
	$("#search_inline_recept_date_end").data("post", "").val("");
	$("#search_invoice_no").data("post", "").val("");

	var step_reset = $("#step_reset").val();
	$("#search_step_set").data("post", step_reset);

	if ((step_reset | 1) == step_reset) {
		$("#search_step_set > input:checkbox:eq(0)").attr("checked", true);
	} else {
		$("#search_step_set > input:checkbox:eq(0)").removeAttr("checked");
	}

	if ((step_reset | 2) == step_reset) {
		$("#search_step_set > input:checkbox:eq(1)").attr("checked", true);
	} else {
		$("#search_step_set > input:checkbox:eq(1)").removeAttr("checked");
	}

	if ((step_reset | 4) == step_reset) {
		$("#search_step_set > input:checkbox:eq(2)").attr("checked", true);
	} else {
		$("#search_step_set > input:checkbox:eq(2)").removeAttr("checked");
	}

	if ((step_reset | 8) == step_reset) {
		$("#search_step_set > input:checkbox:eq(3)").attr("checked", true);
	} else {
		$("#search_step_set > input:checkbox:eq(3)").removeAttr("checked");
	}
	$("#search_step_set > input").trigger("change");
};
/**
 * 检索
 */
function findit() {
	var data = {
		"product_name": $("#search_product_name").data("post"),
		"model_name": $("#search_model_name").data("post"),
		"section_id": $("#search_section_id").data("post"),
		"applicator_id": $("#hidden_search_applicator_id").data("post"),
		"applicate_date_start": $("#search_applicate_date_start").data("post"),
		"applicate_date_end": $("#search_applicate_date_end").data("post"),
		"confirmer_id": $("#hidden_search_confirmer_id").data("post"),
		"order_no": $("#search_order_no").data("post"),
		"scheduled_date_start": $("#search_scheduled_date_start").data("post"),
		"scheduled_date_end": $("#search_scheduled_date_end").data("post"),
		"recept_date_start": $("#search_recept_date_start").data("post"),
		"recept_date_end": $("#search_recept_date_end").data("post"),
		"budget_month": $("#search_budget_month").data("post"),
		"inline_recept_date_start": $("#search_inline_recept_date_start").data("post"),
		"inline_recept_date_end": $("#search_inline_recept_date_end").data("post"),
		"invoice_no": $("#search_invoice_no").data("post"),
		"step": $("#search_step_set").data("post")
	};

	if (data.step == 0) {
		errorPop("请至少选择一种进行状态。");
		return;
	}

	// Ajax提交
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
		complete: function(xhrobj, textStatus) {
			var resInfo = null;
			try {
				// 以Object形式读取JSON
				eval('resInfo =' + xhrobj.responseText);
				if (resInfo.errors.length > 0) {
					// 共通出错信息框
					treatBackMessages(null, resInfo.errors);
				} else {
					var listdata = resInfo.list;
					cacheList = listdata;
					filed_list(listdata);
				}
			} catch (e) {};
		}
	});
};

/**
 * 一览
 * @param listdata 数据源
 */
function filed_list(listdata) {
	var isMamager = $("#isMamager").val() == "true";

	if ($("#gbox_list").length > 0) {
		$("#list").jqGrid().clearGridData();
		$("#list").jqGrid('setGridParam', { data: listdata }).trigger("reloadGrid", [{ current: false }]);
	} else {
		$("#list").jqGrid({
			data: listdata,
			height: 461,
			width: 992,
			rowheight: 23,
			datatype: "local",
			colNames: ['', '品名', '规格', '预定<br>单价', '数量', '单位', '总价', '供应商', '申购<br>人员', '申购日期', 'order_key', '申购单号', '上级<br>确认者', '加急',
			'预计到货<br>日期', '收货<br>日期', '预算月', '验收<br>日期', '发票号码', '申购人员ID'],
			colModel: [{ name: 'supplies_key', index: 'supplies_key', hidden: true, key:true }, 
				{ name: 'product_name', index: 'product_name', formatter : function formatter(value, options, rData) {
					if (rData["urgent_flg"] == "1") {
						return "<span style='background-color: gold;font-weight: bolder;color: darkblue;padding: 0 2px;'>" + value + "</span>";
					} else {
						return value;
					}
				}, width: 80 }, 
				{ name: 'model_name', index: 'model_name', width: 60 },  
				{ name: 'unit_price', index: 'unit_price', width: 50, align: 'right', sorttype: 'currency', formatter: 'currency', formatoptions: { thousandsSeparator: ',', defaultValue: '' } }, { name: 'quantity', index: 'quantity', width: 30, align: 'right', sorttype: 'integer' }, { name: 'unit_text', index: 'unit_text', width: 30 }, 
				{ name: 'total_price', index: 'total_price', width: 60, align: 'right', sorttype: 'currency', formatter: function formatter(value, options, rData) {
					if (rData.quantity && rData.unit_price) {
						return $.fmatter.util.NumberFormat(parseInt(rData.quantity) * parseFloat(rData.unit_price), { decimalPlaces: 2, thousandsSeparator: ',' });
					} else {
						return "-";
					}
				} }, { name: 'supplier', index: 'supplier', width: 50 }, { name: 'applicator_name', index: 'applicator_name', width: 40 }, 
					 { name: 'applicate_date', index: 'applicate_date', width: 50, align: 'center', sorttype: 'date', formatter:'date', formatoptions:{srcformat:'Y/m/d',newformat:'y-m-d'} }, 
					 { name: 'order_key', index: 'order_key', hidden: true },
					 { name: 'order_no', index: 'order_no', width: 70 }, 
					 { name: 'confirmer_name', index: 'confirmer_name', width: 40, formatter:function(value, options, rData){
					 	if (isMamager) {
					 		if (!rData.confirmer_name && !rData.order_no) {
							 	return rData.nesssary_reason + "<button class='list_ng' rid='" + options.rowId + "'>驳回</button><button class='list_ok' rid='" + options.rowId + "'>确认</button>";
					 		} else {
						 		return value || "";
					 		}
					 	} else {
					 		return value || "";
					 	}
					 } }, 
					 { name: 'urgent_flg', index: 'urgent_flg', hidden: true },
					 { name: 'scheduled_date', index: 'scheduled_date', width: 50, sorttype: 'date', formatter:'date', formatoptions:{srcformat:'Y/m/d',newformat:'y-m-d'} }, 
					 { name: 'recept_date', index: 'recept_date', width: 50, sorttype: 'date', formatter:'date', formatoptions:{srcformat:'Y/m/d',newformat:'y-m-d'}  }, 
					 { name: 'budget_month', index: 'budget_month', width: 40 }, 
					 { name: 'inline_recept_date', index: 'inline_recept_date', width: 50, sorttype: 'date', formatter:'date', formatoptions:{srcformat:'Y/m/d',newformat:'y-m-d'} }, { name: 'invoice_no', index: 'invoice_no', width: 60 }, { name: 'applicator_id', index: 'applicator_id', hidden: true }],
			rowNum: 40,
			toppager: false,
			pager: "#listpager",
			viewrecords: true,
			rownumbers: true,
			gridview: true,
			pagerpos: 'right',
			pgbuttons: true,
			pginput: false,
			recordpos: 'left',
			hidegrid: false,
			deselectAfterSort: false,
			multiselect: true,
			ondblClickRow: showEdit,
			onSelectRow: enableButton,
			onSelectAll: enableButton,
			viewsortcols: [true, 'vertical', true],
			gridComplete:function(){
				enableButton();
				var $cfmButtons = $("#list button");
				if ($cfmButtons.length > 0) {
					$cfmButtons.button();
					$cfmButtons.closest("td").each(function(idx, ele) {
						$(ele).attr("colspan", 6)
							.nextAll().remove();
					});
					$cfmButtons.click(managerConfirm);
				}
				var $trs = $("#list").find("tr.ui-widget-content");
				$trs.each(function(){
					var $td = $(this).find("td[aria\\-describedby=list_unit_price]");
				 	// 标记超标准单价
					if ($td.length > 0 && $td.text()) {
						if (parseFloat($td.text().replace(",","")) > waringUnitPrice) {
							$td.css({"color":"orangered","backgroundColor":"#ffc864"});
						}
					}
				});
				// 驳回的申请
				$("#list td[aria\\-describedby=\"list_order_key\"][title='00000000000']")
					.closest("tr").children("td").css("backgroundColor", "lightgray");
			}
		});
	}
};

function enableButton() {
	//当前登录者
	var loginID = $("#loginID").val();

	//选择行，并获取行数
	var rowids = $("#list").jqGrid("getGridParam", "selarrrow");
	if (rowids) {
		if (rowids.length == 1) {
			var rowid = rowids[0]; 
			var rowdata = $("#list").jqGrid('getRowData', rowid);
			//申请人自己的数据
//			if (rowdata.applicator_id == loginID) {
//				//已经有收货日期，还没有验收，验收按钮可以使用
//				if (rowdata.recept_date && !rowdata.inline_recept_date) {
//					$("#inlineReceptbutton").enable();
//				} else {
//					$("#inlineReceptbutton").disable();
//				}
//			} else {
//				$("#inlineReceptbutton").disable();
//			}
	
			if (rowdata.recept_date && !rowdata.invoice_no) {
				$("#invoiceButton").enable();
			} else {
				$("#invoiceButton").disable();
			}
		} else {
			$("#invoiceButton").disable();
		}

		$("#postButton").disable();
		if (rowids.length >= 1) {
			var selApplicate = true;
			for (var rowidx in rowids) {
				var rowdata = $("#list").jqGrid('getRowData', rowids[rowidx]);
				if (rowdata.confirmer_name) {
					selApplicate = false;
					break;
				}
			}
			if (selApplicate) {
				$("#postButton").enable();
			}
		}
	} else {
		$("#invoiceButton, #postButton").disable();
	}
}

function inlineRecept() {
//	var rowid = $("#list").jqGrid("getGridParam", "selrow");
//	var rowdata = $("#list").jqGrid('getRowData', rowid);
//
//	var content = rowdata.model_name ? 
//		("确认验收品名【" + rowdata.product_name + "】,规格【" + rowdata.model_name + "】的申购物品？</label>") : 
//		("确认验收品名【" + rowdata.product_name + "】的申购物品？</label>");

	$.ajax({
		beforeSend: ajaxRequestType,
		async: false,
		url: servicePath + '?method=getInlineRecept',
		cache: false,
		data: null,
		type: "post",
		dataType: "json",
		success: ajaxSuccessCheck,
		error: ajaxError,
		complete: function(xhrobj, textStatus) {
			var resInfo = $.parseJSON(xhrobj.responseText);

			if (resInfo.errors.length > 0) {
				// 共通出错信息框
				treatBackMessages(null, resInfo.errors);
			} else {
				if (resInfo.inlineReceptList) {
					inlineReceptForword(resInfo.inlineReceptList);
				} else {
					errorPop("还没有您能验收的申购品。");
				}
			}
		}
	});

}

function inlineReceptForword(inlineReceptList) {
	var $confirm = $("#confirmmessage");
	$confirm.html("<table id='inlineReceptTable'></table>");

	$("#inlineReceptTable").jqGrid({
		data: inlineReceptList,
		height: 231,
		width: 800,
		rowheight: 23,
		datatype: "local",
		colNames: ['', '品名', '规格', '数量', '单位', '申购人员', '申购日期', '收货日期', '用途'],
		colModel: [{ name: 'supplies_key', index: 'supplies_key', hidden: true, key: true }, 
			{ name: 'product_name', index: 'product_name', width: 80 }, 
			{ name: 'model_name', index: 'model_name', width: 60 }, 
			{ name: 'quantity', index: 'quantity', width: 30, align: 'right', sorttype: 'integer' }, 
			{ name: 'unit_text', index: 'unit_text', width: 20 }, 
			{ name: 'applicator_name', index: 'applicator_name', width: 40 }, 
			{ name: 'applicate_date', index: 'applicate_date', width: 40, align: 'center', sorttype: 'date', formatter:'date', formatoptions:{srcformat:'Y/m/d',newformat:'y-m-d'} },
			{ name: 'recept_date', index: 'recept_date', width: 40, align: 'center', sorttype: 'date', formatter:'date', formatoptions:{srcformat:'Y/m/d',newformat:'y-m-d'} },
			{ name: 'nesssary_reason', index: 'nesssary_reason', width: 120 }
		],
		rowNum: 999,
		toppager: false,
		viewrecords: true,
		rownumbers: true,
		multiselect: true,
		gridview: true,
		hidegrid: false,
		deselectAfterSort: false,
		viewsortcols: [true, 'vertical', true],
		gridComplete: function () {
			$("#cb_inlineReceptTable").remove();
		}
	});

	$confirm.dialog({
		resizable: false,
		modal: true,
		title: "申购物品验收",
		show: "blind",
		width: '824px',
		buttons: {
			"确认验收": function () {
				var supplies_keys = $("#inlineReceptTable").jqGrid("getGridParam", "selarrrow");
				if (supplies_keys.length == 0) {
					errorPop("请选择您验收的物品。")
					return;
				}

				var data = {
					"supplies_key": supplies_keys.join(";")
				};

				$.ajax({
					beforeSend: ajaxRequestType,
					async: false,
					url: servicePath + '?method=doInlineRecept',
					cache: false,
					data: data,
					type: "post",
					dataType: "json",
					success: ajaxSuccessCheck,
					error: ajaxError,
					complete: function(xhrobj, textStatus) {
						var resInfo = null;
						try {
							// 以Object形式读取JSON
							eval('resInfo =' + xhrobj.responseText);
							if (resInfo.errors.length > 0) {
								// 共通出错信息框
								treatBackMessages(null, resInfo.errors);
							} else {
								$confirm.dialog("close");
								findit();
							}
						} catch (e) {}
					}
				});
			},
			"取消": function () {
				$confirm.dialog("close");
			}
		}
	});
};

function invoice() {
	var rowid = $("#list").jqGrid("getGridParam", "selrow");
	var rowdata = $("#list").jqGrid('getRowData', rowid);

	var content = "<form>\n\t\t\t\t\t\t<div class=\"ui-widget-content\">\n\t\t\t\t\t\t\t<table class=\"condform\">\n\t\t\t\t\t\t\t\t<tr>\n\t\t\t\t\t\t\t\t\t<td class=\"ui-state-default td-title\">发票号码</td>\n\t\t\t\t\t\t\t\t\t<td class=\"td-content\">\n\t\t\t\t\t\t\t\t\t\t<input type=\"text\" id=\"edit_invoice_no\" name=\"invoice_no\" class=\"ui-widget-content\" alt=\"发票号码\">\n\t\t\t\t\t\t\t\t\t</td>\n\t\t\t\t\t\t\t\t</tr>\n\t\t\t\t\t\t\t</table>\n\t\t\t\t\t\t</div>\n\t\t\t\t\t</form>\n\t\t\t\t\t";
	var $confirm = $("#confirmmessage");
	$confirm.html(content);

	$confirm.children("form").validate({
		rules: {
			invoice_no: {
				required: true,
				maxlength: 8
			}
		}
	});

	$confirm.dialog({
		resizable: false,
		modal: true,
		title: "发票",
		width: 350,
		show: "blind",
		buttons: {
			"确认": function () {
				if ($confirm.children("form").valid()) {
					var data = {
						"supplies_key": rowdata.supplies_key,
						"invoice_no": $("#edit_invoice_no").val()
					};

					$.ajax({
						beforeSend: ajaxRequestType,
						async: false,
						url: servicePath + '?method=doInvoice',
						cache: false,
						data: data,
						type: "post",
						dataType: "json",
						success: ajaxSuccessCheck,
						error: ajaxError,
						complete: function (xhrobj, textStatus) {
							var resInfo = null;
							try {
								// 以Object形式读取JSON
								eval('resInfo =' + xhrobj.responseText);
								if (resInfo.errors.length > 0) {
									// 共通出错信息框
									treatBackMessages(null, resInfo.errors);
								} else {
									$confirm.dialog("close");
									findit();
								}
							} catch (e) {}
						}
					});
				}
			},
			"取消": function () {
				$confirm.dialog("close");
			}
		}
	});
}

function selPostage() {
	// 选择经理
	$("#postDialog").dialog({
		title: "选择通知的经理人员",
		resizable: false,
		modal: true,
		show: "blind",
		buttons: {
			"关闭": function () {
				$("#postDialog").dialog('close');
			}
		}
	});
}
function postMessage(manager_id) {
	var rowids = $("#list").jqGrid("getGridParam", "selarrrow");

	var postData = {
		supplies_key : rowids.toString(),
		confirmer_id : manager_id
	}

	$.ajax({
		beforeSend: ajaxRequestType,
		async: false,
		url: servicePath + '?method=sendPostMessage',
		cache: false,
		data: postData,
		type: "post",
		dataType: "json",
		success: ajaxSuccessCheck,
		error: ajaxError,
		complete: function (xhrobj, textStatus) {
			var resInfo = $.parseJSON(xhrobj.responseText);
			infoPop("通知邮件已发出，请等待上级确认。");
		}
	});

}

var sendUrgent = false;

function doBatchOk() {
	if (!($("#hidden_search_applicator_id").data("post")
			|| $("#search_section_id").data("post"))) {
		errorPop("请选择“申请课室”或“申购人员”进行检索后，再使用此功能。");
		return;
	}

	var iLiseSize=0;
	var selectData = null;
	for (var i in cacheList) {
		var data = cacheList[i];
		if (!data.confirmer_id) {
			iLiseSize++;
			if (selectData == null) selectData = data;
			if (selectData["urgent_flg"] == "1") {
				sendUrgent = true;
			}
		}
	}

	if (selectData == null) {
		if ($("#infostring:visible").length) {
			if (sendUrgent) {
				sendUrgentNotice();
			}
			$('div#infostring').dialog("close");
		} else {
			errorPop("没有可确人的申购项目。");
		}
		return;
	}

	if ($("#infostring:visible").length) {
		$("#infostring > .informationarea").children("span:eq(0)").text(iLiseSize);
	} else {
		infoPop("正在确认当前检索条件中的申购品，待确认申购品数： " + "(<span>" + iLiseSize + "</span>/" + iLiseSize + ")");
	}

	doComfirm("OK", selectData, true);
}

/**
 * 编辑物品申购明细
 */
function showEdit(rowid) {
	var data = {
		"supplies_key": rowid
	};

	$.ajax({
		beforeSend: ajaxRequestType,
		async: true,
		url: servicePath + '?method=getDetail',
		cache: false,
		data: data,
		type: "post",
		dataType: "json",
		success: ajaxSuccessCheck,
		error: ajaxError,
		complete: function(xhrobj, textStatus) {
			var resInfo = null;
			try {
				// 以Object形式读取JSON
				eval('resInfo =' + xhrobj.responseText);
				if (resInfo.errors.length > 0) {
					// 共通出错信息框
					treatBackMessages(null, resInfo.errors);
				} else {
					//物品申购明细
					setEditContent(resInfo);
				}
			} catch (e) {}
		}
	});
};

var updateTemplate = "<table class=\"condform\">\n\t\t\t\t\t\t<tr>\n\t\t\t\t\t\t\t<td class=\"ui-state-default td-title\">品名</td>\n\t\t\t\t\t\t\t<td class=\"td-content\" colspan=\"3\">#{productName}</td>\n\t\t\t\t\t\t</tr>\n\t\t\t\t\t\t<tr>\n\t\t\t\t\t\t\t<td class=\"ui-state-default td-title\">规格</td>\n\t\t\t\t\t\t\t<td class=\"td-content\" colspan=\"3\">#{modelName}</td>\n\t\t\t\t\t\t</tr>\n\t\t\t\t\t\t<tr>\n\t\t\t\t\t\t\t<td class=\"ui-state-default td-title\">预定单价</td>\n\t\t\t\t\t\t\t<td class=\"td-content\">#{unitPrice}</td>\n\t\t\t\t\t\t\t<td class=\"ui-state-default td-title\">单位</td>\n\t\t\t\t\t\t\t<td class=\"td-content\">#{unitText}</td>\n\t\t\t\t\t\t</tr>\n\t\t\t\t\t\t<tr>\n\t\t\t\t\t\t\t<td class=\"ui-state-default td-title\">数量</td>\n\t\t\t\t\t\t\t<td class=\"td-content\">#{quantity}</td>\n\t\t\t\t\t\t\t<td class=\"ui-state-default td-title\">供应商</td>\n\t\t\t\t\t\t\t<td class=\"td-content\">#{supplier}</td>\n\t\t\t\t\t\t</tr>\n\t\t\t\t\t\t<tr>\n\t\t\t\t\t\t\t<td class=\"ui-state-default td-title\">申请课室</td>\n\t\t\t\t\t\t\t<td class=\"td-content\">\n\t\t\t\t\t\t\t\t<label id=\"update_label_section_name\"></label>\n\t\t\t\t\t\t\t</td>\n\t\t\t\t\t\t\t<td class=\"ui-state-default td-title\">申购人员</td>\n\t\t\t\t\t\t\t<td class=\"td-content\">\n\t\t\t\t\t\t\t\t<label id=\"update_label_applicator_name\"></label>\n\t\t\t\t\t\t\t</td>\n\t\t\t\t\t\t</tr>\n\t\t\t\t\t\t<tr>\n\t\t\t\t\t\t\t<td class=\"ui-state-default td-title\">申请日期</td>\n\t\t\t\t\t\t\t<td class=\"td-content\">\n\t\t\t\t\t\t\t\t<label id=\"update_label_applicate_date\"></label>\n\t\t\t\t\t\t\t</td>\n\t\t\t\t\t\t\t<td class=\"ui-state-default td-title\">申购单号</td>\n\t\t\t\t\t\t\t<td class=\"td-content\">\n\t\t\t\t\t\t\t\t<label id=\"update_label_order_no\"></label>\n\t\t\t\t\t\t\t</td>\n\t\t\t\t\t\t</tr>\n\t\t\t\t\t\t<tr>\n\t\t\t\t\t\t\t<td class=\"ui-state-default td-title\">上级确认者</td>\n\t\t\t\t\t\t\t<td class=\"td-content\">\n\t\t\t\t\t\t\t\t<label id=\"update_label_confirmer_name\"></label>\n\t\t\t\t\t\t\t</td>\n\t\t\t\t\t\t\t<td class=\"ui-state-default td-title\">确认结果</td>\n\t\t\t\t\t\t\t<td class=\"td-content\">\n\t\t\t\t\t\t\t\t<label id=\"update_label_confirmer_result\"></label>\n\t\t\t\t\t\t\t</td>\n\t\t\t\t\t\t</tr>\n\t\t\t\t\t\t<tr>\n\t\t\t\t\t\t\t<td class=\"ui-state-default td-title\">用途</td>\n\t\t\t\t\t\t\t<td class=\"td-content\" colspan=\"3\">#{nesssaryReason}</td>\n\t\t\t\t\t\t</tr>\n\t\t\t\t\t\t<tr>\n\t\t\t\t\t\t\t<td class=\"ui-state-default td-title\">备注</td>\n\t\t\t\t\t\t\t<td class=\"td-content\" colspan=\"3\">#{comments}</td>\n\t\t\t\t\t\t</tr>\n\t\t\t\t\t\t<tr>\n\t\t\t\t\t\t\t<td class=\"ui-state-default td-title\">预计到货日期</td>\n\t\t\t\t\t\t\t<td class=\"td-content\">#{scheduledDate}</td>\n\t\t\t\t\t\t\t<td class=\"ui-state-default td-title\">收货日期</td>\n\t\t\t\t\t\t\t<td class=\"td-content\">\n\t\t\t\t\t\t\t\t<label id=\"update_label_recept_date\"></label>\n\t\t\t\t\t\t\t</td>\n\t\t\t\t\t\t</tr>\n\t\t\t\t\t\t<tr>\n\t\t\t\t\t\t\t<td class=\"ui-state-default td-title\">预算月</td>\n\t\t\t\t\t\t\t<td class=\"td-content\">#{budgetMonth}</td>\n\t\t\t\t\t\t\t<td class=\"ui-state-default td-title\">验收日期</td>\n\t\t\t\t\t\t\t<td class=\"td-content\">\n\t\t\t\t\t\t\t\t<label id=\"update_label_inline_recept_date\"></label>\n\t\t\t\t\t\t\t</td>\n\t\t\t\t\t\t</tr>\n\t\t\t\t\t\t<tr>\n\t\t\t\t\t\t\t<td class=\"ui-state-default td-title\">发票号</td>\n\t\t\t\t\t\t\t<td class=\"td-content\">\n\t\t\t\t\t\t\t\t<label id=\"update_label_invoice_no\"></label>\n\t\t\t\t\t\t\t</td>\n\t\t\t\t\t\t</tr>\n\t\t\t\t\t  </table>\n\t\t\t\t\t  <div style=\"height:44px\">\n\t\t\t\t\t  \t<input class=\"ui-button\" id=\"updateGobackButton\" value=\"返回\" style=\"float:right;right:2px\" type=\"button\">\n\t\t\t\t\t\t#{updateInput}\n\t\t\t\t\t\t#{deleteInput}\n\t\t\t\t\t\t#{ngInput}\n\t\t\t\t\t\t#{okInput}\n\t\t\t\t\t  </div>";

var inputObjTemplate = {
	'productName': '<input type="text" id="update_product_name" name="product_name" class="ui-widget-content" style="width:500px;" alt="品名">',
	'modelName': '<input type="text" id="update_model_name" name="model_name" class="ui-widget-content" style="width:500px;" alt="规格">',
	'unitPrice': '<input type="text" id="update_unit_price" name="unit_price" class="ui-widget-content" alt="预定单价">',
	'unitText': '<input type="text" id="update_unit_text" name="unit_text" class="ui-widget-content" alt="单位">',
	'quantity': '<input type="text" id="update_quantity" name="quantity" class="ui-widget-content" alt="数量">',
	'supplier': '<input type="text" id="update_supplier" name="supplier" class="ui-widget-content" alt="供应商">',
	'nesssaryReason': '<textarea id="update_nesssary_reason" name="nesssary_reason" class="ui-widget-content" alt="用途"></textarea>',
	'comments': '<textarea id="update_comments" name="comments" class="ui-widget-content" alt="备注"></textarea>',
	'scheduledDate': '<input type="text" id="update_scheduled_date" name="scheduled_date" class="ui-widget-content" alt="预计到货日期" readonly="readonly">',
	'budgetMonth': '<input type="text" id="update_budget_month" name="budget_month" class="ui-widget-content" alt="预算月" readonly="readonly">'
};

var labelObjTemplate = {
	'productName': '<label id="update_label_product_name"></label>',
	'modelName': '<label id="update_label_model_name"></label>',
	'unitPrice': '<label id="update_label_unit_price"></label>',
	'unitText': '<label id="update_label_unit_text"></label>',
	'quantity': '<label id="update_label_quantity"></label>',
	'supplier': '<label id="update_label_supplier"></label>',
	'nesssaryReason': '<label id="update_label_nesssary_reason"></label>',
	'comments': '<label id="update_label_comments"></label>',
	'scheduledDate': '<label id="update_label_scheduled_date"></label>',
	'budgetMonth': '<label id="update_label_budget_month"></label>'
};

var buttonObjTemplate = {
	'updateButton': '<input class="ui-button" id="updateButton" value="更新" style="float:right;right:2px" type="button">',
	'deleteButton': '<input class="ui-button" id="deleteButton" value="删除" style="float:right;right:2px" type="button">',
	'ngButton': '<input class="ui-button" id="ngButton" value="驳回" style="float:right;right:2px" type="button">',
	'okButton': '<input class="ui-button" id="okButton" value="确认" style="float:right;right:2px" type="button">'
};

/**
 * 设置画面内容
 * @param {*} suppliesDetail
 */
function setEditContent(resInfo) {
	var suppliesDetail = resInfo.detail;
	var order = resInfo.order;

	//经理标识
	var isMamager = $("#isMamager").val();
	//线长标识
	var isLiner = $("#isLiner").val();
	//支援课标识
	var isSupport = $("#isSupport").val();
	//当前登录者ID
	var loginID = $("#loginID").val();

	//赋值一份模板
	var template = updateTemplate;
	//申请人ID
	var applicator_id = suppliesDetail.applicator_id;
	//确认者
	var confirmer_name = suppliesDetail.confirmer_name || '';
	//收货日期
	var recept_date = suppliesDetail.recept_date || '';
	//订购单KEy
	var order_key = suppliesDetail.order_key || '';

	//已确认（确认或者驳回）
	if (confirmer_name) {
		template = template.replace("#{unitText}", labelObjTemplate.unitText);
		template = template.replace("#{quantity}", labelObjTemplate.quantity);
		template = template.replace("#{nesssaryReason}", labelObjTemplate.nesssaryReason);

		if(isSupport !== "true") {//支援课
			template = template.replace("#{productName}", labelObjTemplate.productName);
			template = template.replace("#{modelName}", labelObjTemplate.modelName);
			template = template.replace("#{unitPrice}", labelObjTemplate.unitPrice);
			template = template.replace("#{supplier}", labelObjTemplate.supplier);
		}

		//驳回明细
		if (order_key == "0" || order_key == "00000000000") {
			template = template.replace("#{comments}", labelObjTemplate.comments);
			template = template.replace("#{scheduledDate}", labelObjTemplate.scheduledDate);
			template = template.replace("#{budgetMonth}", labelObjTemplate.budgetMonth);
		} else {//确认明细
			if(isSupport === "true") {//支援课
				template = template.replace("#{comments}", inputObjTemplate.comments);
				template = template.replace("#{productName}", inputObjTemplate.productName);
				template = template.replace("#{modelName}", inputObjTemplate.modelName);
				template = template.replace("#{unitPrice}", inputObjTemplate.unitPrice);
				template = template.replace("#{supplier}", inputObjTemplate.supplier);
			} else {
				template = template.replace("#{comments}", labelObjTemplate.comments);
			}
			//登录者是支援课人员,还未收货
			if (isSupport === "true" && !recept_date) {
				//支援科上级盖章
				if (order && order.sign_manager_id && order.sign_minister_id) {
					template = template.replace("#{scheduledDate}", inputObjTemplate.scheduledDate);
					template = template.replace("#{budgetMonth}", inputObjTemplate.budgetMonth);
				} else {
					//支援科上级没有盖章
					template = template.replace("#{scheduledDate}", labelObjTemplate.scheduledDate);
					template = template.replace("#{budgetMonth}", labelObjTemplate.budgetMonth);
				}
			} else {
				template = template.replace("#{scheduledDate}", labelObjTemplate.scheduledDate);
				template = template.replace("#{budgetMonth}", labelObjTemplate.budgetMonth);
			}
		}

		template = template.replace("#{okInput}", "");
		template = template.replace("#{ngInput}", "");
		template = template.replace("#{deleteInput}", "");
		if (template.includes('type="text"') || template.includes('textarea')) {
			template = template.replace("#{updateInput}", buttonObjTemplate.updateButton);
		} else {
			template = template.replace("#{updateInput}", "");
		}
	} else {
		//上级未确认,这个时候还没有生成订购单，支援课上级还不能进行盖章
		//经理
		if (isMamager === "true") {
			//申请人是自己
			if (applicator_id == loginID) {
				//可以编辑
				template = template.replace("#{productName}", inputObjTemplate.productName);
				template = template.replace("#{modelName}", inputObjTemplate.modelName);
				template = template.replace("#{unitPrice}", inputObjTemplate.unitPrice);
				template = template.replace("#{unitText}", inputObjTemplate.unitText);
				template = template.replace("#{quantity}", inputObjTemplate.quantity);
				template = template.replace("#{supplier}", inputObjTemplate.supplier);
				template = template.replace("#{nesssaryReason}", inputObjTemplate.nesssaryReason);
				if (isSupport === "true") {
					//支援课
					template = template.replace("#{comments}", inputObjTemplate.comments);
				} else {
					template = template.replace("#{comments}", labelObjTemplate.comments);
				}
				template = template.replace("#{scheduledDate}", labelObjTemplate.scheduledDate);
				template = template.replace("#{budgetMonth}", labelObjTemplate.budgetMonth);

				template = template.replace("#{okInput}", buttonObjTemplate.okButton);
				template = template.replace("#{ngInput}", buttonObjTemplate.ngButton);
				template = template.replace("#{deleteInput}", buttonObjTemplate.deleteButton);
				if (template.includes('type="text"') || template.includes('textarea')) {
					template = template.replace("#{updateInput}", buttonObjTemplate.updateButton);
				} else {
					template = template.replace("#{updateInput}", "");
				}
			} else {
				//线长的数据
				if (suppliesDetail.isLiner == "1") {
					template = template.replace("#{productName}", inputObjTemplate.productName);
					template = template.replace("#{modelName}", inputObjTemplate.modelName);
					template = template.replace("#{unitPrice}", inputObjTemplate.unitPrice);
					template = template.replace("#{unitText}", inputObjTemplate.unitText);
					template = template.replace("#{quantity}", inputObjTemplate.quantity);
					template = template.replace("#{supplier}", inputObjTemplate.supplier);
					template = template.replace("#{nesssaryReason}", inputObjTemplate.nesssaryReason);
				} else {
					template = template.replace("#{productName}", labelObjTemplate.productName);
					template = template.replace("#{modelName}", labelObjTemplate.modelName);
					template = template.replace("#{unitPrice}", labelObjTemplate.unitPrice);
					template = template.replace("#{unitText}", labelObjTemplate.unitText);
					template = template.replace("#{quantity}", labelObjTemplate.quantity);
					template = template.replace("#{supplier}", labelObjTemplate.supplier);
					template = template.replace("#{nesssaryReason}", labelObjTemplate.nesssaryReason);
				}
				if (isSupport === "true") {
					//支援课
					template = template.replace("#{comments}", inputObjTemplate.comments);
				} else {
					template = template.replace("#{comments}", labelObjTemplate.comments);
				}
				template = template.replace("#{scheduledDate}", labelObjTemplate.scheduledDate);
				template = template.replace("#{budgetMonth}", labelObjTemplate.budgetMonth);

				template = template.replace("#{okInput}", buttonObjTemplate.okButton);
				template = template.replace("#{ngInput}", buttonObjTemplate.ngButton);
				template = template.replace("#{deleteInput}", "");
				if (template.includes('type="text"') || template.includes('textarea')) {
					template = template.replace("#{updateInput}", buttonObjTemplate.updateButton);
				} else {
					template = template.replace("#{updateInput}", "");
				}
			}
			//线长或者支援课
		} else if (isLiner === "true" || isSupport === "true") {
			//申请人是自己
			if (applicator_id == loginID) {
				//可以编辑
				template = template.replace("#{productName}", inputObjTemplate.productName);
				template = template.replace("#{modelName}", inputObjTemplate.modelName);
				template = template.replace("#{unitPrice}", inputObjTemplate.unitPrice);
				template = template.replace("#{unitText}", inputObjTemplate.unitText);
				template = template.replace("#{quantity}", inputObjTemplate.quantity);
				template = template.replace("#{supplier}", inputObjTemplate.supplier);
				template = template.replace("#{nesssaryReason}", inputObjTemplate.nesssaryReason);
				if (isSupport === "true") {
					//支援课
					template = template.replace("#{comments}", inputObjTemplate.comments);
				} else {
					template = template.replace("#{comments}", labelObjTemplate.comments);
				}
				template = template.replace("#{scheduledDate}", labelObjTemplate.scheduledDate);
				template = template.replace("#{budgetMonth}", labelObjTemplate.budgetMonth);

				template = template.replace("#{okInput}", "");
				template = template.replace("#{ngInput}", "");
				template = template.replace("#{deleteInput}", buttonObjTemplate.deleteButton);
				if (template.includes('type="text"') || template.includes('textarea')) {
					template = template.replace("#{updateInput}", buttonObjTemplate.updateButton);
				} else {
					template = template.replace("#{updateInput}", "");
				}
			} else {
				template = template.replace("#{unitText}", labelObjTemplate.unitText);
				template = template.replace("#{quantity}", labelObjTemplate.quantity);
				template = template.replace("#{nesssaryReason}", labelObjTemplate.nesssaryReason);
				if(isSupport === "true") {//支援课
					template = template.replace("#{comments}", inputObjTemplate.comments);
					template = template.replace("#{productName}", inputObjTemplate.productName);
					template = template.replace("#{modelName}", inputObjTemplate.modelName);
					template = template.replace("#{unitPrice}", inputObjTemplate.unitPrice);
					template = template.replace("#{supplier}", inputObjTemplate.supplier);
				} else {
					template = template.replace("#{comments}", labelObjTemplate.comments);
					template = template.replace("#{productName}", labelObjTemplate.productName);
					template = template.replace("#{modelName}", labelObjTemplate.modelName);
					template = template.replace("#{unitPrice}", labelObjTemplate.unitPrice);
					template = template.replace("#{supplier}", labelObjTemplate.supplier);
				}
				template = template.replace("#{scheduledDate}", labelObjTemplate.scheduledDate);
				template = template.replace("#{budgetMonth}", labelObjTemplate.budgetMonth);

				template = template.replace("#{okInput}", "");
				template = template.replace("#{ngInput}", "");
				template = template.replace("#{deleteInput}", "");
				if (template.includes('type="text"') || template.includes('textarea')) {
					template = template.replace("#{updateInput}", buttonObjTemplate.updateButton);
				} else {
					template = template.replace("#{updateInput}", "");
				}
			}
		} else {
			//其他不可编辑
			template = template.replace("#{productName}", labelObjTemplate.productName);
			template = template.replace("#{modelName}", labelObjTemplate.modelName);
			template = template.replace("#{unitPrice}", labelObjTemplate.unitPrice);
			template = template.replace("#{unitText}", labelObjTemplate.unitText);
			template = template.replace("#{quantity}", labelObjTemplate.quantity);
			template = template.replace("#{supplier}", labelObjTemplate.supplier);
			template = template.replace("#{nesssaryReason}", labelObjTemplate.nesssaryReason);
			template = template.replace("#{comments}", labelObjTemplate.comments);
			template = template.replace("#{scheduledDate}", labelObjTemplate.scheduledDate);
			template = template.replace("#{budgetMonth}", labelObjTemplate.budgetMonth);

			template = template.replace("#{okInput}", "");
			template = template.replace("#{ngInput}", "");
			template = template.replace("#{deleteInput}", "");
			template = template.replace("#{updateInput}", "");
		}
	}

	$("#updateform").html(template);
	$("#updateform input.ui-button").button();

	$("#update_scheduled_date").datepicker({
		showButtonPanel: true,
		dateFormat: "yy/mm/dd",
		currentText: "今天"
	});

	var today = new Date();
	$("#update_budget_month").monthpicker({
		showButtonPanel: true,
		pattern: "yyyymm",
		startYear: 2013,
		finalYear: today.getFullYear() + 1,
		selectedMonth: today.getMonth() + 1,
		monthNames: ['1月', '2月', '3月', '4月', '5月', '6月', '7月', '8月', '9月', '10月', '11月', '12月']
	});

	//品名
	$("#update_product_name").val(suppliesDetail.product_name);
	$("#update_label_product_name").text(suppliesDetail.product_name);
	//规格
	var model_name = suppliesDetail.model_name || '';
	$("#update_model_name").val(model_name);
	$("#update_label_model_name").text(model_name);
	//预定单价
	var unit_price = suppliesDetail.unit_price || '';
	$("#update_unit_price").val(unit_price);
	$("#update_label_unit_price").text(unit_price);
	//单位
	var unit_text = suppliesDetail.unit_text || '';
	$("#update_unit_text").val(unit_text);
	$("#update_label_unit_text").text(unit_text);
	//数量
	var quantity = suppliesDetail.quantity || '';
	$("#update_quantity").val(quantity);
	$("#update_label_quantity").text(quantity);
	//供应商
	var supplier = suppliesDetail.supplier || '';
	$("#update_supplier").val(supplier);
	$("#update_supplier").autocomplete({
		source: gArrSuppier,
		minLength: 0,
		delay: 100
	});
	$("#update_supplier").unbind("focus").bind("focus", function () {
		!this.value && $(this).autocomplete("search");
	}).unbind("blur").bind("blur", function () {
		$(this).autocomplete("close");
	});

	$("#update_label_supplier").text(supplier);
	//申请课室
	$("#update_label_section_name").text(suppliesDetail.section_name);
	//申购人员
	$("#update_label_applicator_name").text(suppliesDetail.applicator_name);
	//申请日期
	$("#update_label_applicate_date").text(suppliesDetail.applicate_date);
	//申购单号
	var order_no = suppliesDetail.order_no || '';
	$("#update_label_order_no").text(order_no);
	//上级确认者
	$("#update_label_confirmer_name").text(confirmer_name);
	//确认结果
	if (confirmer_name) {
		var _order_key = suppliesDetail.order_key;
		if (_order_key == "0" || _order_key == "00000000000") {
			$("#update_label_confirmer_result").text("驳回");
		} else {
			$("#update_label_confirmer_result").text("确认");
		}
	} else {
		$("#update_label_confirmer_result").text("");
	}
	//用途
	var nesssary_reason = suppliesDetail.nesssary_reason || '';
	$("#update_nesssary_reason").val(nesssary_reason);
	$("#update_label_nesssary_reason").html(decodeText(nesssary_reason));
	//备注
	var comments = suppliesDetail.comments || '';
	$("#update_comments").val(comments);
	$("#update_label_comments").html(decodeText(comments));
	//预计到货日期
	var scheduled_date = suppliesDetail.scheduled_date || '';
	$("#update_scheduled_date").val(scheduled_date);
	$("#update_label_scheduled_date").text(scheduled_date);
	//收货日期
	$("#update_label_recept_date").text(recept_date);
	//预算月
	var budget_month = suppliesDetail.budget_month || '';
	$("#update_label_budget_month").text(budget_month);
	$("#update_budget_month").val(budget_month);
	//验收日期
	var inline_recept_date = suppliesDetail.inline_recept_date || '';
	$("#update_label_inline_recept_date").text(inline_recept_date);
	//发票号
	var invoice_no = suppliesDetail.invoice_no || '';
	$("#update_label_invoice_no").text(invoice_no);

	$("#updateGobackButton").click(function () {
		$("#searcharea").show();
		$("#updatearea").hide();
	});

	$("#okButton").bind("click", function () {
		warningConfirm("确认后不能修改，是否要确认？", function () {
			doComfirm("OK", suppliesDetail);
		}, null);
	});

	$("#ngButton").bind("click", function () {
		warningConfirm("驳回后不能修改，是否要驳回？", function () {
			doComfirm("NG", suppliesDetail);
		}, null);
	});

	$("#deleteButton").bind("click", function () {
		warningConfirm("删除不能恢复，是否要删除？", function () {
			doDelete(suppliesDetail);
		}, null);
	});

	$("#searcharea").hide();
	$("#updatearea").show();

	//动态前台验证规则
	if ($("#updateButton").length == 1) {
		var dynamicRules = {};
		//品名
		if ($("#update_product_name").length == 1) {
			dynamicRules.product_name = {
				required: true,
				maxlength: 64
			};
		}
		//规格
		if ($("#update_model_name").length == 1) {
			dynamicRules.model_name = {
				maxlength: 32
			};
		}
		//预定单价
		if ($("#update_unit_price").length == 1) {
			dynamicRules.unit_price = {
				number: true,
				range: [0.00, 9999.99]
			};
		}
		//单位
		if ($("#update_unit_text").length == 1) {
			dynamicRules.unit_text = {
				maxlength: 3
			};
		}
		//数量
		if ($("#update_quantity").length == 1) {
			dynamicRules.quantity = {
				required: true,
				digits: true,
				maxlength: 4,
				min: 1
			};
		}
		//供应商
		if ($("#update_supplier").length == 1) {
			dynamicRules.supplier = {
				maxlength: 64
			};
		}
		//用途
		if ($("#update_nesssary_reason").length == 1) {
			dynamicRules.nesssary_reason = {
				maxlength: 256
			};
		}
		//备注
		if ($("#update_comments").length == 1) {
			dynamicRules.comments = {
				maxlength: 256
			};
		}

		$("#updateform").validate({
			rules: dynamicRules
		});
	}

	$("#updateButton").bind("click", function () {
		
		if ($("#updateform").valid()) {
			if ($("#update_unit_price").length == 1) {
				var _unit_price = $("#update_unit_price").val();
				if (_unit_price) {
					_unit_price = +(_unit_price.trim())
					if (_unit_price > waringUnitPrice) {
						warningConfirm("预定单价高于" + waringUnitPrice + "，请确认是否要更新物品申购明细？", function () {
							doUpdate(suppliesDetail);
						}, null);
						return;
					}
				} else {
					warningConfirm("如果了解预定单价，请填写。", function () {
						doUpdate(suppliesDetail);
					}, null, null, "不了解单价，就这样提交");
					return;
				}
			}

			doUpdate(suppliesDetail);
		}
	});
}

function doUpdate(suppliesDetail) {
	var data = {
		"supplies_key": suppliesDetail.supplies_key
	};

	if ($("#update_product_name").length == 1) {
		data["product_name"] = escapeStorage($("#update_product_name").val());
	} else {
		data["product_name"] = suppliesDetail.product_name;
	}

	if ($("#update_model_name").length == 1) {
		data["model_name"] = escapeStorage($("#update_model_name").val());
	} else {
		data["model_name"] = suppliesDetail.model_name || "";
	}

	if ($("#update_unit_price").length == 1) {
		data["unit_price"] = $("#update_unit_price").val();
	} else {
		data["unit_price"] = suppliesDetail.unit_price || "";
	}

	if ($("#update_unit_text").length == 1) {
		data["unit_text"] = $("#update_unit_text").val();
	} else {
		data["unit_text"] = suppliesDetail.unit_text || "";
	}

	if ($("#update_quantity").length == 1) {
		data["quantity"] = $("#update_quantity").val();
	} else {
		data["quantity"] = suppliesDetail.quantity || "";
	}

	if ($("#update_supplier").length == 1) {
		data["supplier"] = $("#update_supplier").val();
	} else {
		data["supplier"] = suppliesDetail.supplier || "";
	}

	if ($("#update_nesssary_reason").length == 1) {
		data["nesssary_reason"] = escapeStorage($("#update_nesssary_reason").val());
	} else {
		data["nesssary_reason"] = suppliesDetail.nesssary_reason || "";
	}

	if ($("#update_comments").length == 1) {
		data["comments"] = $("#update_comments").val();
	} else {
		if ($("#isLiner").val() === "true") {
			if ($("#update_supplier").length == 1) {
				data["comments"] = $("#update_supplier").val();
			} else {
				data["comments"] = suppliesDetail.supplier || "";
			}
		} else {
			data["comments"] = suppliesDetail.comments || "";
		}
	}

	if ($("#update_scheduled_date").length == 1) {
		data["scheduled_date"] = $("#update_scheduled_date").val();
	} else {
		data["scheduled_date"] = suppliesDetail.scheduled_date || "";
	}

	if ($("#update_budget_month").length == 1) {
		data["budget_month"] = $("#update_budget_month").val();
	} else {
		data["budget_month"] = suppliesDetail.budget_month || "";
	}

	$.ajax({
		beforeSend: ajaxRequestType,
		async: false,
		url: servicePath + '?method=doUpdate',
		cache: false,
		data: data,
		type: "post",
		dataType: "json",
		success: ajaxSuccessCheck,
		error: ajaxError,
		complete: function (xhrobj, textStatus) {
			var resInfo = null;
			try {
				// 以Object形式读取JSON
				eval('resInfo =' + xhrobj.responseText);
				if (resInfo.errors.length > 0) {
					// 共通出错信息框
					treatBackMessages(null, resInfo.errors);
				} else {
					$("#searcharea").show();
					$("#updatearea").hide();
					findit();
				}
			} catch (e) {}
		}
	});
}

/**
 * 经理确认action
 * @param {*} comfirmFlag 确认、驳回标记
 * @param {*} suppliesDetail 数据
 */
function doComfirm(comfirmFlag, suppliesDetail, isBatch) {
	var data = {
		"supplies_key": suppliesDetail.supplies_key,
		"confirm_flg": comfirmFlag
	};

	$.ajax({
		beforeSend: ajaxRequestType,
		async: false,
		url: servicePath + '?method=doComfirm',
		cache: false,
		data: data,
		type: "post",
		dataType: "json",
		success: ajaxSuccessCheck,
		error: ajaxError,
		complete: function (xhrobj, textStatus) {
			var resInfo = null;
			try {
				// 以Object形式读取JSON
				eval('resInfo =' + xhrobj.responseText);
				if (resInfo.errors.length > 0) {
					// 共通出错信息框
					treatBackMessages(null, resInfo.errors);
				} else {
					$("#searcharea").show();
					$("#updatearea").hide();
					if (isBatch) {
						var confirmer = $("#loginID").val() || $("#op_id").val();
						griddata_update(cacheList, "supplies_key", suppliesDetail.supplies_key, "confirmer_id", confirmer, false);
						griddata_update(cacheList, "supplies_key", suppliesDetail.supplies_key, "confirmer_name", "OK", false);
						$("#list").jqGrid('setGridParam',{data:cacheList}).trigger("reloadGrid", [{current:true}]);
						if ($("#infostring:visible").length) {
							doBatchOk();
						}
					} else {
						findit();
					}
				}
			} catch (e) {}
		}
	});
}

/**
 * 删除明细
 * @param {} suppliesDetail 
 */
function doDelete(suppliesDetail) {
	var data = {
		"supplies_key": suppliesDetail.supplies_key
	};

	$.ajax({
		beforeSend: ajaxRequestType,
		async: false,
		url: servicePath + '?method=doDelete',
		cache: false,
		data: data,
		type: "post",
		dataType: "json",
		success: ajaxSuccessCheck,
		error: ajaxError,
		complete: function (xhrobj, textStatus) {
			var resInfo = null;
			try {
				// 以Object形式读取JSON
				eval('resInfo =' + xhrobj.responseText);
				if (resInfo.errors.length > 0) {
					// 共通出错信息框
					treatBackMessages(null, resInfo.errors);
				} else {
					$("#searcharea").show();
					$("#updatearea").hide();
					findit();
				}
			} catch (e) {}
		}
	});
};

var gSuppliesReferList = [];
var gArrSuppier = ['亚速旺', '易优佰', '京东', '心承', '定制'];

/**
 * 参考列表
 */
function getReferList() {
	$.ajax({
		beforeSend: ajaxRequestType,
		async: true,
		url: 'supplies_refer_list.do?method=search',
		cache: false,
		data: null,
		type: "post",
		dataType: "json",
		success: ajaxSuccessCheck,
		error: ajaxError,
		complete: function (xhrobj, textStatus) {
			var resInfo = null;
			try {
				// 以Object形式读取JSON
				eval('resInfo =' + xhrobj.responseText);
				if (resInfo.errors.length > 0) {
					// 共通出错信息框
					treatBackMessages(null, resInfo.errors);
				} else {
					gSuppliesReferList = resInfo.list;
					//常用采购清单
					gSuppliesReferList.map(function (item) {
						item.supplier && !gArrSuppier.includes(item.supplier) && gArrSuppier.splice(gArrSuppier.length - 1, 0, item.supplier);
					});
				}
			} catch (e) {}
		}
	});
};

/**
 * @param {*} list 常用采购清单
 */
function addDetailDialog() {
	$("#add_supplies_detail").find("input[type='text'],textarea").val("").removeClass("errorarea-single");
	setSuppliseView(gSuppliesReferList);

	$("#add_comment").autocomplete({
		source: gArrSuppier,
		minLength: 0,
		delay: 100
	});
	$("#add_comment").unbind("focus").bind("focus", function () {
		!this.value && $(this).autocomplete("search");
	}).unbind("blur").bind("blur", function () {
		$(this).autocomplete("close");
	});

	//前台验证
	$("#addForm").validate({
		rules: {
			product_name: {
				required: true,
				maxlength: 64
			},
			model_name: {
				maxlength: 32
			},
			quantity: {
				required: true,
				digits: true,
				maxlength: 4,
				min: 1
			},
			unit_text: {
				maxlength: 3
			},
			unit_price: {
//				required: true,
				number: true,
				range: [0.00, 9999.99]
			},
			comment: {
				maxlength: 256
			},
			nesssary_reason: {
				maxlength: 256
			}
		}
	});

	var now = new Date();
	//周几
	var weekday = now.getDay();
	//当前日期是周三/周四时,出提示信息
	if (weekday == 3 || weekday == 4) {
		$("#cutOffTip").show();
		$("#urgent_flg").next().show();
	} else {
		$("#cutOffTip").hide();
		$("#urgent_flg").next().hide();
	}

	//申请明细弹框
	var $thisDialg = $("#add_supplies_detail").dialog({
		title: "申请物品",
		width: 1250,
		height: 670,
		resizable: false,
		modal: true,
		show: "blind",
		position: "top",
		buttons: {
			"申请": function () {doInsertValid($thisDialg, false)},
			"申请并关闭": function () {doInsertValid($thisDialg, true)},
			"关闭": function () {
				$thisDialg.dialog('close');
			}
		}
	});
};

/**
 *
 * @param {*} list 绘制常用采购清单列表
 */
function setSuppliseView(list) {
	if (list && list.length > 0) {
		(function () {
			var map = new Map();
			list.forEach(function (item, index) {
				var productName = item.product_name;
				var arr = [];
				if (map.has(productName)) {
					arr = map.get(productName);
					arr.push(item);
					map.set(productName, arr);
				} else {
					arr.push(item);
					map.set(productName, arr);
				}
			});

			//模板
			var template = "<li class=\"item\">\n\t\t\t\t\t\t<div class=\"grid-container\">\n\t\t\t\t\t\t\t<div>#{img}</div>\n\t\t\t\t\t\t\t<div>\n\t\t\t\t\t\t\t\t<div class=\"grid-item\">\n\t\t\t\t\t\t\t\t\t<div class=\"title\">品名</div>\n\t\t\t\t\t\t\t\t\t<div>#{productionName}</div>\n\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t<div class=\"grid-item\">\n\t\t\t\t\t\t\t\t\t<div class=\"title\">规格</div>\n\t\t\t\t\t\t\t\t\t<div class=\"model-name\"><select>#{modelName}<select></div>\n\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t<div class=\"grid-item\">\n\t\t\t\t\t\t\t\t\t<div class=\"title\">预定单价</div>\n\t\t\t\t\t\t\t\t\t<div class=\"price\">#{unitPrice}</div>\n\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t<div class=\"grid-item\">\n\t\t\t\t\t\t\t\t\t<div class=\"title\">单位</div>\n\t\t\t\t\t\t\t\t\t<div class=\"unit-text\">#{unitText}<span class=\"capacity\">#{capacity}</span></div>\n\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t<div class=\"grid-item supplier-item\">\n\t\t\t\t\t\t\t\t\t<div class=\"title\">供应商</div>\n\t\t\t\t\t\t\t\t\t<div class=\"supplier\">#{supplier}<span class=\"goods_serial\">#{goods_serial}</span></div>\n\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t\t<div class=\"grid-item add\">\n\t\t\t\t\t\t\t\t\t<input type=\"button\" class=\"ui-button\" value=\"设到申购明细\">\n\t\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t\t</div>\n\t\t\t\t\t\t</div>\n\t\t\t\t\t</li>";
			var $content = $('<content/>');
			var _iteratorNormalCompletion = true;
			var _didIteratorError = false;
			var _iteratorError = undefined;

			try {
				for (var _iterator = map[Symbol.iterator](), _step; !(_iteratorNormalCompletion = (_step = _iterator.next()).done); _iteratorNormalCompletion = true) {
					var _step$value = _slicedToArray(_step.value, 2);

					var key = _step$value[0];
					var arr = _step$value[1];

					var li = template;
					li = li.replace("#{productionName}", key);

					var index = 0;
					var options = "";
					var oriData = [];
					var _iteratorNormalCompletion2 = true;
					var _didIteratorError2 = false;
					var _iteratorError2 = undefined;

					try {
						for (var _iterator2 = arr[Symbol.iterator](), _step2; !(_iteratorNormalCompletion2 = (_step2 = _iterator2.next()).done); _iteratorNormalCompletion2 = true) {
							var itemObj = _step2.value;

							if (!itemObj["imgSrc"]) {
								var imgSrc = "images/noimage128x128.gif";
								if (itemObj.photo_uuid) {
									imgSrc = "http://" + document.location.hostname + "/photos/supplies_refer_list/" + itemObj.photo_uuid + "?_s=" + Date.now();
								}
								itemObj["imgSrc"] = imgSrc;
								delete itemObj.photo_uuid;delete itemObj.servletWrapper;delete itemObj.refer_key;
							}

							if (index == 0) {
								oriData.push(itemObj);
								var img = "<img src=" + itemObj["imgSrc"] + " width=\"128\" height=\"128\" />";
								li = li.replace("#{img}", img);
								li = li.replace("#{unitPrice}", itemObj.unit_price || '');
								li = li.replace("#{unitText}", itemObj.package_unit_text || '');
								li = li.replace("#{supplier}", itemObj.supplier || '');
								options += "<option selected value=" + index + ">" + (itemObj.model_name || '') + "</option>";
								if (!itemObj.supplier) {
									li = li.replace("supplier-item", "supplier-item-hidden");
									li = li.replace("#{goods_serial}", '');
								} else {
									li = li.replace("#{goods_serial}", itemObj.goods_serial || '');
								}
								if (!itemObj.capacity || itemObj.capacity == 0 || itemObj.capacity == 1) {
									li = li.replace("#{capacity}", '');
								} else {
									li = li.replace("#{capacity}", '= ' + itemObj.capacity + ' ' + (itemObj.unit_text || ''));
								}
							} else {
								oriData.push(itemObj);
								options += "<option value=" + index + ">" + (itemObj.model_name || '') + "</option>";
							}
							index++;
						}
					} catch (err) {
						_didIteratorError2 = true;
						_iteratorError2 = err;
					} finally {
						try {
							if (!_iteratorNormalCompletion2 && _iterator2.callback) {
								_iterator2.callback();
							}
						} finally {
							if (_didIteratorError2) {
								throw _iteratorError2;
							}
						}
					}

					li = li.replace("#{modelName}", options);
					var $li = $(li);
					$li.data({ "oriData": oriData, index: 0 });
					$content.append($li);
				}
			} catch (err) {
				_didIteratorError = true;
				_iteratorError = err;
			} finally {
				try {
					if (!_iteratorNormalCompletion && _iterator.callback) {
						_iterator.callback();
					}
				} finally {
					if (_didIteratorError) {
						throw _iteratorError;
					}
				}
			}

			$("#filterContainer > ul").html($content.children());
			$("#filterContainer > ul select").change(function () {
				var index = this.value;

				var $li = $(this).closest("li");
				var oriData = $li.data("oriData");

				var selectedItem = oriData[index];
				$li.data("index",index);

				$li.find("img").attr("src",selectedItem.imgSrc).hide().slideDown(150);
				$li.find(".price").text(selectedItem.unit_price || '');
				var unitTextHtml = selectedItem.package_unit_text || '';
				var capacity = selectedItem.capacity;
				if (!capacity || capacity == 0 || capacity == 1) {
					unitTextHtml += '<span class="capacity"></span>';
				} else {
					unitTextHtml += '<span class="capacity">= ' + capacity + ' ' + (selectedItem.unit_text || '') + '</span>';
				}
				$li.find(".unit-text").html(unitTextHtml);
	
				var supplier = selectedItem.supplier;
				if (supplier) {
					$li.find(".supplier-item-hidden").removeClass("supplier-item-hidden").addClass("supplier-item");
				} else {
					$li.find(".supplier-item").removeClass("supplier-item").addClass("supplier-item-hidden");
				}
				var supplierHtml = selectedItem.supplier || '';
				$li.find(".supplier").html(supplierHtml + '<span class="goods_serial">' + (selectedItem.goods_serial || '') + '</span>');
			});

			$("#filterContainer > ul input.ui-button").button().click(function () {
				var $this = $(this),
				    $li = $this.closest("li"),
				    $target = $("#addForm"),
				    scrollTop = $(document).scrollTop(),
				    fromX = $li.offset().left,
				    fromY = $li.offset().top - scrollTop,
				    targetX = $target.offset().left + 150,
				    targetY = $target.offset().top - scrollTop;

				var $ball = $("#ball");
				$ball.html($li.clone());

				$ball.css({
					"top": fromY + "px",
					"left": fromX + "px",
					"width": $li.width() + "px",
					"height": $li.height() + "px"
				});
				$ball.animate({
					"top": targetY + "px",
					"left": targetX + "px",
					"width": "0px",
					"height": "0px"
				}, 250, "swing", function () {
					$ball.html("");
					chooseSupplise($li.data("oriData")[$li.data("index")]);
				});
			});
		})();
	} else {
		$("#filterContainer > ul").html('<div style="width:190px;margin:100px auto;font-size:20px;">无常用采购清单</div>');
	}
};

function doInsertValid($thisDialg, close) {
	if ($("#addForm").valid()) {
		var unit_price = $("#add_unit_price").val();
		if (unit_price) {
			unit_price = +(unit_price.trim())
			if (unit_price > waringUnitPrice) {
				warningConfirm("预定单价高于" + waringUnitPrice + "，请确认是否要创建物品申购明细？", function () {
					doInsert($thisDialg, close);
				}, null);
				return;
			}
		} else {
			warningConfirm("如果了解预定单价，请填写。", function () {
				doInsert($thisDialg, close);
			}, null, null, "不了解单价，就这样提交");
			return;
		}

		doInsert($thisDialg, close);
	}
}

function escapeStorage(postValue){
	if (!postValue) {
		return postValue;
	} else {
		return postValue.replace(/</g,"＜").replace(/>/g,"＞").replace(/\"/g,"＂");
	}
}

/**
 * 新规action
 * @param {*} $thisDialg 弹窗
 */
function doInsert($thisDialg, close) {
	var data = {
		"product_name": escapeStorage($("#add_product_name").val()),
		"model_name": escapeStorage($("#add_model_name").val()),
		"unit_price": $("#add_unit_price").val(),
		"unit_text": $("#add_unit_text").val(),
		"quantity": $("#add_quantity").val(),
		"nesssary_reason": escapeStorage($("#add_nesssary_reason").val()),
		"comments": $("#add_comment").val()
	};
	if ($("#urgent_flg+label").is(":visible") && $("#urgent_flg").attr("checked")) {
		data["urgent_flg"] = "1";
	} else {
		data["urgent_flg"] = "0";
	}

	$.ajax({
		beforeSend: ajaxRequestType,
		async: false,
		url: servicePath + '?method=doInsert',
		cache: false,
		data: data,
		type: "post",
		dataType: "json",
		success: ajaxSuccessCheck,
		error: ajaxError,
		complete: function (xhrobj, textStatus) {
			var resInfo = null;
			try {
				// 以Object形式读取JSON
				eval('resInfo =' + xhrobj.responseText);
				if (resInfo.errors.length > 0) {
					// 共通出错信息框
					treatBackMessages(null, resInfo.errors);
				} else {
					if (close) {
						$thisDialg.dialog('close');
					} else {
						$("#add_product_name").val("");
						$("#add_model_name").val("");
						$("#add_unit_price").val("");
						$("#add_unit_text").val("");
						$("#add_quantity").val("");
						//$("#add_nesssary_reason").val("");
						$("#add_comment").val("");
						$("#urgent_flg").removeAttr("checked").trigger("change");
					}
					findit();
				}
			} catch (e) {}
		}
	});
}

/**
 * 筛选常用采购清单
 */
function filterSupplise() {
	var productName = $("#filter_product_name").val();
	if (!productName) return;

	$("#filterContainer > ul li").each(function (index, item) {
		var $li = $(item);
		var suppliesObj = $li.data("oriData")[$li.data("index")];
		suppliesObj.product_name.indexOf(productName) >= 0 ? $li.fadeIn() : $li.fadeOut();
	});
};

/**
 * 选择常用采购清单到明细
 */
function chooseSupplise(suppliesObj) {
	$("#add_product_name").val(suppliesObj.product_name);
	$("#add_model_name").val(suppliesObj.model_name || '');
	$("#add_unit_text").val(suppliesObj.package_unit_text || '');
	$("#add_unit_price").val(suppliesObj.unit_price || '');
	$("#add_comment").val(suppliesObj.supplier || '');
};
/**
 * 编辑订购单
 */
function editSupplise() {
	$.ajax({
		beforeSend: ajaxRequestType,
		async: true,
		url: servicePath + '?method=searchComfirmAndNoOrderKey',
		cache: false,
		data: null,
		type: "post",
		dataType: "json",
		success: ajaxSuccessCheck,
		error: ajaxError,
		complete: function (xhrobj, textStatus) {
			var resInfo = null;
			try {
				// 以Object形式读取JSON
				eval('resInfo =' + xhrobj.responseText);
				if (resInfo.errors.length > 0) {
					// 共通出错信息框
					treatBackMessages(null, resInfo.errors);
				} else {
					var listdata = resInfo.list;
					showEditSuppliseDialog(listdata);
				}
			} catch (e) {}
		}
	});
}

function showEditSuppliseDialog(listdata) {
	var now = new Date();
	$("#order_no_prefix").text(getFyYear(now));
	$("#add_order_no").val("").removeClass("errorarea-single");

	if ($("#gbox_edit_list").length > 0) {
		$("#edit_list").jqGrid().clearGridData();
		$("#edit_list").jqGrid('setGridParam', { data: listdata }).trigger("reloadGrid", [{ current: false }]);
	} else {
		$("#edit_list").jqGrid({
			data: listdata,
			height: 231,
			width: 992,
			rowheight: 23,
			datatype: "local",
			colNames: ['', '品名', '规格', '数量', '预定单价', '单位', '总价', '申购人员', '申购日期', '上级确认者', '供应商', '用途', '加急'],
			colModel: [{ name: 'supplies_key', index: 'supplies_key', hidden: true }, 
				{ name: 'product_name', index: 'product_name', formatter : function formatter(value, options, rData) {
					if (rData["urgent_flg"] == "1") {
						return "<span style='background-color: gold;font-weight: bolder;color: darkblue;padding: 0 2px;'>" + value + "</span>";
					} else {
						return value;
					}
				}, width: 80 }, 
				{ name: 'model_name', index: 'model_name', width: 60 }, { name: 'quantity', index: 'quantity', width: 30, align: 'right', sorttype: 'integer' }, { name: 'unit_price', index: 'unit_price', width: 40, align: 'right', sorttype: 'currency', formatter: 'currency', formatoptions: { thousandsSeparator: ',', defaultValue: '' } }, { name: 'unit_text', index: 'unit_text', width: 30 }, { name: 'total_price', index: 'total_price', width: 40, align: 'right', formatter: function formatter(value, options, rData) {
					if (rData.quantity && rData.unit_price) {
						return $.fmatter.util.NumberFormat(parseInt(rData.quantity) * parseFloat(rData.unit_price), { decimalPlaces: 2, thousandsSeparator: ',' });
					} else {
						return "-";
					}
				} }, { name: 'applicator_name', index: 'applicator_name', width: 40 }, 
				{ name: 'applicate_date', index: 'applicate_date', width: 50, align: 'center', sorttype: 'date' }, { name: 'confirmer_name', index: 'confirmer_name', width: 40 }, { name: 'supplier', index: 'supplier', width: 60 }, { name: 'nesssary_reason', index: 'nesssary_reason', width: 100 },
			 { name: 'urgent_flg', index: 'urgent_flg', formatter:'select', width: 20, editoptions:{value: "1:加急"}}
			],
			rowNum: 100,
			toppager: false,
			pager: "#edit_listpager",
			viewrecords: true,
			rownumbers: true,
			multiselect: true,
			gridview: true,
			pagerpos: 'right',
			pgbuttons: true,
			pginput: false,
			recordpos: 'left',
			hidegrid: false,
			deselectAfterSort: false,
			ondblClickRow: null,
			viewsortcols: [true, 'vertical', true],
			gridComplete: function () {
				var $jthis = $("#edit_list");
				var dataIds = $jthis.getDataIDs();
				var length = dataIds.length;

				var wednesday = getDayOfWeek(new Date(), 3);
				var thursday = getDayOfWeek(new Date(), 4);

				for (var i = 0; i < length; i++) {
					var rowdata = $jthis.jqGrid('getRowData', dataIds[i]);
					var applicate_date = rowdata["applicate_date"];

					//申请日期是本周三本周四的默认不勾选，其他默认选上
					if (applicate_date != wednesday && applicate_date != thursday) {
						$jthis.jqGrid("setSelection", dataIds[i]);
					}
				}
			}
		});
	}

	$("#addOrderForm").validate({
		rules: {
			order_no: {
				required: true,
				maxlength: 3
			}
		}
	});

	var $thisDialg = $("#edit_supplise").dialog({
		title: "编辑订购单（一般物品申购单）",
		width: 1020,
		height: 460,
		resizable: false,
		modal: true,
		show: "blind",
		buttons: {
			"确认": function () {
				if ($("#addOrderForm").valid()) {
					var selectedIds = $('#edit_list').jqGrid('getGridParam', 'selarrrow');
					if (selectedIds.length == 0) {
						errorPop("请至少选中一条申购明细。");
						return;
					}

					var data = {
						"order_no": "RC-FY" + getFyYear(now) + "-" + $("#add_order_no").val()
					};

					var rowsData = [];
					for (var i = 0; i < selectedIds.length; i++) {
						var rowData = $("#edit_list").getRowData(selectedIds[i]);
						data["keys.supplies_key[" + i + "]"] = rowData.supplies_key;
						rowsData.push(rowData);
					}
					var exists = rowsData.some(function (item) {
						return item.supplier.includes("易优佰");
					});
					if (exists) {
						data["spec"] = "1";
					} else {
						data["spec"] = "0";
					}

					$.ajax({
						beforeSend: ajaxRequestType,
						async: false,
						url: 'supplies_order.do?method=doInsert',
						cache: false,
						data: data,
						type: "post",
						dataType: "json",
						success: ajaxSuccessCheck,
						error: ajaxError,
						complete: function (xhrobj, textStatus) {
							var resInfo = null;
							try {
								// 以Object形式读取JSON
								eval('resInfo =' + xhrobj.responseText);
								if (resInfo.errors.length > 0) {
									// 共通出错信息框
									treatBackMessages(null, resInfo.errors);
								} else {
									$thisDialg.dialog('close');
									findit();
								}
							} catch (e) {}
						}
					});
				}
			},
			"关闭": function () {
				$(this).dialog('close');
			}
		}
	});
}

/**
 * 获取当前周指定星期几
 * @param {*} date 日期 Date
 * @param {*} week 周几(1~7)星期天使用7
 * @returns 返回yyyy/mm/dd形式的日期
 */
function getDayOfWeek(date, week) {
	var weekday = date.getDay() || 7; //获取星期几,getDay()返回值是 0（周日） 到 6（周六） 之间的一个整数。0||7为7，即weekday的值为1-7
	date.setDate(date.getDate() - weekday + week); //往前算（weekday-1）天，年份、月份会自动变化

	var year = date.getFullYear(); //年
	var month = date.getMonth() + 1; //月
	var d = date.getDate(); //日

	return year + "/" + fillZero(month) + "/" + fillZero(d);
}

/**
 * 收货
 */
function recept() {
	$.ajax({
		beforeSend: ajaxRequestType,
		async: true,
		url: servicePath + '?method=searchWaittingRecept',
		cache: false,
		data: null,
		type: "post",
		dataType: "json",
		success: ajaxSuccessCheck,
		error: ajaxError,
		complete: function (xhrobj, textStatus) {
			var resInfo = null;
			try {
				// 以Object形式读取JSON
				eval('resInfo =' + xhrobj.responseText);
				if (resInfo.errors.length > 0) {
					// 共通出错信息框
					treatBackMessages(null, resInfo.errors);
				} else {
					var listdata = resInfo.list;
					showReceptDialog(listdata);
				}
			} catch (e) {}
		}
	});
};

function showReceptDialog(listdata) {
	if ($("#gbox_recept_list").length > 0) {
		$("#recept_list").jqGrid().clearGridData();
		$("#recept_list").jqGrid('setGridParam', { data: listdata }).trigger("reloadGrid", [{ current: false }]);
	} else {
		$("#recept_list").jqGrid({
			data: listdata,
			height: 231,
			width: 1100,
			rowheight: 23,
			datatype: "local",
			colNames: ['', '品名', '规格', '数量', '预定单价', '单位', '总价', '申购人员', '申购日期', '申购单号', '上级确认者', '供应商', '用途'],
			colModel: [{ name: 'supplies_key', index: 'supplies_key', hidden: true }, { name: 'product_name', index: 'product_name', width: 80 }, { name: 'model_name', index: 'model_name', width: 60 }, { name: 'quantity', index: 'quantity', width: 30, align: 'right', sorttype: 'integer' }, { name: 'unit_price', index: 'unit_price', width: 40, align: 'right', sorttype: 'currency', formatter: 'currency', formatoptions: { thousandsSeparator: ',', defaultValue: '' } }, { name: 'unit_text', index: 'unit_text', width: 30 }, { name: 'total_price', index: 'total_price', width: 40, align: 'right', formatter: function formatter(value, options, rData) {
					if (rData.quantity && rData.unit_price) {
						return $.fmatter.util.NumberFormat(parseInt(rData.quantity) * parseFloat(rData.unit_price), { decimalPlaces: 2, thousandsSeparator: ',' });
					} else {
						return "-";
					}
				} }, { name: 'applicator_name', index: 'applicator_name', width: 40 }, { name: 'applicate_date', index: 'applicate_date', width: 50, align: 'center', sorttype: 'date' }, { name: 'order_no', index: 'order_no', width: 60 }, { name: 'confirmer_name', index: 'confirmer_name', width: 50 }, { name: 'supplier', index: 'supplier', width: 60 }, { name: 'nesssary_reason', index: 'nesssary_reason', width: 100 }],
			rowNum: 100,
			toppager: false,
			pager: "#recept_listpager",
			viewrecords: true,
			rownumbers: true,
			multiselect: true,
			gridview: true,
			pagerpos: 'right',
			pgbuttons: true,
			pginput: false,
			recordpos: 'left',
			hidegrid: false,
			deselectAfterSort: false,
			ondblClickRow: null,
			viewsortcols: [true, 'vertical', true],
			gridComplete: function () {}
		});
	}

	var $thisDialg = $("#edit_recept").dialog({
		title: "待收货物品申购明细",
		width: 'auto',
		height: 400,
		resizable: false,
		modal: true,
		show: "blind",
		buttons: {
			"确认": function () {
				var selectedIds = $('#recept_list').jqGrid('getGridParam', 'selarrrow');
				if (selectedIds.length == 0) {
					errorPop("请至少选中一条申购明细。");
					return;
				}
				var data = {};
				for (var i = 0; i < selectedIds.length; i++) {
					var rowData = $("#recept_list").getRowData(selectedIds[i]);
					data["keys.supplies_key[" + i + "]"] = rowData.supplies_key;
				}

				$.ajax({
					beforeSend: ajaxRequestType,
					async: false,
					url: servicePath + '?method=doRecept',
					cache: false,
					data: data,
					type: "post",
					dataType: "json",
					success: ajaxSuccessCheck,
					error: ajaxError,
					complete: function (xhrobj, textStatus) {
						var resInfo = null;
						try {
							// 以Object形式读取JSON
							eval('resInfo =' + xhrobj.responseText);
							if (resInfo.errors.length > 0) {
								// 共通出错信息框
								treatBackMessages(null, resInfo.errors);
							} else {
								$thisDialg.dialog('close');
								findit();
							}
						} catch (e) {}
					}
				});
			},
			"关闭": function() {
				$(this).dialog('close');
			}
		}
	});
}

/**
 *  @param {是否弹窗} showDialog
 * 确认订购单
 */
function confirmOrder(showDialog) {
	$.ajax({
		beforeSend: ajaxRequestType,
		async: true,
		url: 'supplies_order.do?method=search',
		cache: false,
		data: null,
		type: "post",
		dataType: "json",
		success: ajaxSuccessCheck,
		error: ajaxError,
		complete: function (xhrobj, textStatus) {
			var resInfo = null;
			try {
				// 以Object形式读取JSON
				eval('resInfo =' + xhrobj.responseText);
				if (resInfo.errors.length > 0) {
					// 共通出错信息框
					treatBackMessages(null, resInfo.errors);
				} else {
					var listdata = resInfo.list;
					orderList(listdata);

					if (showDialog) {
						$("#confirm_order").dialog({
							title: "物品申购单一览",
							width: 'auto',
							resizable: false,
							modal: true,
							show: "blind",
							buttons: {
								"关闭": function () {
									$(this).dialog('close');
								}
							}
						});
					}
				}
			} catch (e) {}
		}
	});
};

function orderList(listdata) {
	if ($("#gbox_order_list").length > 0) {
		$("#order_list").jqGrid().clearGridData();
		$("#order_list").jqGrid('setGridParam', { data: listdata }).trigger("reloadGrid", [{ current: false }]);
	} else {
		$("#order_list").jqGrid({
			data: listdata,
			height: 461,
			width: 700,
			rowheight: 23,
			datatype: "local",
			colNames: ['', '申购单号', '提交日期', '申购人员', '批准经理', '批准部长', '文件名'],
			colModel: [{ name: 'order_key', index: 'order_key', hidden: true }, { name: 'order_no', index: 'order_no', width: 25, align: 'center' }, { name: 'order_date', index: 'order_date', width: 20, align: 'center', sorttype: 'date' }, { name: 'operator_name', index: 'operator_name', width: 20 }, { name: 'manager_name', index: 'manager_name', width: 20 }, { name: 'minister_name', index: 'minister_name', width: 20 }, { name: 'file_names', index: 'file_names', width: 70, formatter: function formatter(value, options, rData) {
					if (value && value.length > 0) {
						var content = "";
						var _iteratorNormalCompletion3 = true;
						var _didIteratorError3 = false;
						var _iteratorError3 = undefined;

						try {
							for (var _iterator3 = value[Symbol.iterator](), _step3; !(_iteratorNormalCompletion3 = (_step3 = _iterator3.next()).done); _iteratorNormalCompletion3 = true) {
								var fileName = _step3.value;

								if (content) {
									content += "<br>";
								}
								content += "<a href='javascript:downPdf(\"" + rData.order_key + "\",\"" + fileName + "\");'>" + fileName + "</a>";
							}
						} catch (err) {
							_didIteratorError3 = true;
							_iteratorError3 = err;
						} finally {
							try {
								if (!_iteratorNormalCompletion3 && _iterator3.callback) {
									_iterator3.callback();
								}
							} finally {
								if (_didIteratorError3) {
									throw _iteratorError3;
								}
							}
						}

						return content;
					} else {
						return "";
					}
				} }],
			rowNum: 20,
			toppager: false,
			pager: "#order_listpager",
			viewrecords: true,
			rownumbers: true,
			gridview: true,
			pagerpos: 'right',
			pgbuttons: true,
			pginput: false,
			recordpos: 'left',
			hidegrid: false,
			deselectAfterSort: false,
			ondblClickRow: showOrderDetail,
			viewsortcols: [true, 'vertical', true],
			gridComplete: null
		});
	}
};

/**
 * @param {申购单KEY} orderKey
 * @param {文件名称} fileName
 */
function downPdf(orderKey, fileName) {
	if (fileName.indexOf(" ") >= 0) {
		fileName = fileName.replace(" ", "%20");
	}

	if ($("iframe").length > 0) {
		$("iframe").attr("src", "supplies_order.do?method=output&orderKey=" + orderKey + "&fileName=" + fileName);
	} else {
		var iframe = document.createElement("iframe");
		iframe.src = "supplies_order.do?method=output&orderKey=" + orderKey + "&fileName=" + fileName;
		iframe.style.display = "none";
		document.body.appendChild(iframe);
	}
};

function showOrderDetail() {
	var rowid = $("#order_list").jqGrid("getGridParam", "selrow"); //得到选中的行ID
	var rowData = $("#order_list").getRowData(rowid);

	var data = {
		"order_key": rowData.order_key
	};

	$.ajax({
		beforeSend: ajaxRequestType,
		async: true,
		url: 'supplies_order.do?method=getDetail',
		cache: false,
		data: data,
		type: "post",
		dataType: "json",
		success: ajaxSuccessCheck,
		error: ajaxError,
		complete: function (xhrobj, textStatus) {
			var resInfo = null;
			try {
				// 以Object形式读取JSON
				eval('resInfo =' + xhrobj.responseText);
				if (resInfo.errors.length > 0) {
					// 共通出错信息框
					treatBackMessages(null, resInfo.errors);
				} else {
					showOrderConfirmDialog(resInfo);
				}
			} catch (e) {}
		}
	});
};

function showOrderConfirmDialog(resInfo) {
	var order = resInfo.order;
	var listdata = resInfo.orderDetails;

	//经理印
	if (order.sign_manager_id) {
		//已盖章
		$("#confirm_sign_manager_id").hide();
		var imgUrl = "http://" + document.location.hostname + "/images/sign/" + order.manager_job_no + "?_s=" + new Date().getTime();
		$("#confirm_sign_manager_id_pic").attr("src", imgUrl).show();
	} else {
		$("#confirm_sign_manager_id").show();
		$("#confirm_sign_manager_id_pic").hide();
	}

	//部长印
	if (order.sign_minister_id) {
		//已盖章
		$("#confirm_sign_minister_id").hide();
		var _imgUrl = "http://" + document.location.hostname + "/images/sign/" + order.minister_job_no + "?_s=" + new Date().getTime();
		$("#confirm_sign_minister_id_pic").attr("src", _imgUrl).show();
	} else {
		$("#confirm_sign_minister_id").show();
		$("#confirm_sign_minister_id_pic").hide();
	}

	if ($("#gbox_order_detail_list").length > 0) {
		$("#order_detail_list").jqGrid().clearGridData();
		$("#order_detail_list").jqGrid('setGridParam', { data: listdata }).trigger("reloadGrid", [{ current: false }]);
	} else {
		$("#order_detail_list").jqGrid({
			data: listdata,
			height: 461,
			width: 1200,
			rowheight: 23,
			datatype: "local",
			colNames: ['品名', '规格', '数量', '预定<br>单价', '单位', '总价', '供应商', '用途', '申购<br>人员', '申购日期', '上级<br>确认者', '预计到货<br>日期', '收货<br>日期', '预算月', '验收<br>日期', '发票号码'],
			colModel: [{ name: 'product_name', index: 'product_name', width: 80 }, { name: 'model_name', index: 'model_name', width: 60 }, { name: 'quantity', index: 'quantity', width: 30, align: 'right', sorttype: 'integer' }, { name: 'unit_price', index: 'unit_price', width: 40, align: 'right', sorttype: 'currency', formatter: 'currency', formatoptions: { thousandsSeparator: ',', defaultValue: '' } }, { name: 'unit_text', index: 'unit_text', width: 30 }, { name: 'total_price', index: 'total_price', width: 60, align: 'right', sorttype: 'currency', formatter: function formatter(value, options, rData) {
					if (rData.quantity && rData.unit_price) {
						return $.fmatter.util.NumberFormat(parseInt(rData.quantity) * parseFloat(rData.unit_price), { decimalPlaces: 2, thousandsSeparator: ',' });
					} else {
						return "-";
					}
				} }, { name: 'supplier', index: 'supplier', width: 40 }, { name: 'nesssary_reason', index: 'nesssary_reason', width: 100 }, { name: 'applicator_name', index: 'applicator_name', width: 40 }, { name: 'applicate_date', index: 'applicate_date', width: 60, align: 'center', sorttype: 'date' }, { name: 'confirmer_name', index: 'confirmer_name', width: 40 }, { name: 'scheduled_date', index: 'scheduled_date', width: 60, sorttype: 'date' }, { name: 'recept_date', index: 'recept_date', width: 60, sorttype: 'date' }, { name: 'budget_month', index: 'budget_month', width: 40 }, { name: 'inline_recept_date', index: 'inline_recept_date', width: 60, sorttype: 'date' }, { name: 'invoice_no', index: 'invoice_no', width: 60 }],
			rowNum: 40,
			toppager: false,
			pager: "#order_detail_listpager",
			viewrecords: true,
			rownumbers: true,
			gridview: true,
			pagerpos: 'right',
			pgbuttons: true,
			pginput: false,
			recordpos: 'left',
			hidegrid: false,
			deselectAfterSort: false,
			ondblClickRow: null,
			onSelectRow: null,
			viewsortcols: [true, 'vertical', true],
			gridComplete: function gridComplete() {}
		});
	}

	$("#confirm_sign_manager_id").unbind("click").bind("click", function () {
		var data = {
			"order_key": order.order_key,
			"confirm_flg": "1"
		};
		doOrderConfirm(data);
	});

	$("#confirm_sign_minister_id").unbind("click").bind("click", function () {
		var data = {
			"order_key": order.order_key,
			"confirm_flg": "2"
		};
		doOrderConfirm(data);
	});

	$("#sign_order").dialog({
		title: "物品申购单确认",
		width: 'auto',
		resizable: false,
		modal: true,
		close: function() {
			if ($("#signEdit").val() === "true") {
				confirmOrder(false);
			}
		},
		buttons: {
			"关闭": function () {
				$(this).dialog('close');
			}
		}
	});
};

function doOrderConfirm(data) {
	$.ajax({
		beforeSend: ajaxRequestType,
		async: false,
		url: 'supplies_order.do?method=doSign',
		cache: false,
		data: data,
		type: "post",
		dataType: "json",
		success: ajaxSuccessCheck,
		error: ajaxError,
		complete: function (xhrobj, textStatus) {
			var resInfo = null;
			try {
				// 以Object形式读取JSON
				eval('resInfo =' + xhrobj.responseText);
				if (resInfo.errors.length > 0) {
					// 共通出错信息框
					treatBackMessages(null, resInfo.errors);
				} else {
					var today = new Date();
					var imgUrl = "http://" + document.location.hostname + "/images/sign/" + $("#jobNo").val() + "?_s=" + today.getTime();
					if (data.confirm_flg == 1) {
						$("#confirm_sign_manager_id").hide();
						$("#confirm_sign_manager_id_pic").attr("src", imgUrl).show();
					} else {
						$("#confirm_sign_minister_id").hide();
						$("#confirm_sign_minister_id_pic").attr("src", imgUrl).show();
					}
				}
			} catch (e) {}
		}
	});
};

var getFyYear = function(now) {
	var year = now.getFullYear();
	if (now.getMonth() >= 3) {
		return year + 1;
	} else {
		return year;
	}
}

var managerConfirm = function(evt){
	var thisClass = this.className;
	var thisKey = this.getAttribute("rid");
	if (thisClass && thisKey) {
		if (thisClass.indexOf("list_ok") >= 0) {
			warningConfirm("是否要确认此申购？", function () {
				doComfirm("OK", {"supplies_key" : thisKey});
				
				if ($("#list tr[id=" + thisKey + "] > td[aria\\-describedby=list_urgent_flg]").attr("title") == "1") {
					sendUrgentNotice();
				}
			});
		} else if (thisClass.indexOf("list_ng") >= 0) {
			warningConfirm("驳回后不能取消，是否要驳回？", function () {
				doComfirm("NG", {"supplies_key" : thisKey});
			});
		}
		evt.preventDefault();
		return false;
	}
}

var sendUrgentNotice = function() {
	$.ajax({
		beforeSend: ajaxRequestType,
		async: false,
		url: servicePath + '?method=sendUrgentNotice',
		cache: false,
		data: null,
		type: "post",
		dataType: "json",
		success: ajaxSuccessCheck,
		error: ajaxError,
		complete: function (xhrobj, textStatus) {
			sendUrgent = false;
		}
	});
}