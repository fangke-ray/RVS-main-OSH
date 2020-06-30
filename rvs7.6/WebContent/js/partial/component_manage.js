var servicePath = "component_manage.do";
var strSeqTr = '<tr><td class="td-content"><input type="text" alt="零件代码" name="partial_code" class="add_partial_code" class="ui-widget-content" style="width:215px;"/><input class="add_partial_id" name="partial_id" type="hidden"></td><td class="td-content"><input type="text" alt="零件名称" name="partial_name" class="add_partial_name" class="ui-widget-content" style="width:215px;" readonly="readonly"/></td><td class="td-content"><input type="text" name="quantity" class="add_quantity" alt="数量"></td></tr>';

$(function() {
    $("input.ui-button").button();
    enableButton();

	/* 为每一个匹配的元素的特定事件绑定一个事件处理函数 */
	$("#searcharea span.ui-icon").bind("click",
		function() {
			$(this).toggleClass('ui-icon-circle-triangle-n')
					.toggleClass('ui-icon-circle-triangle-s');
			if ($(this).hasClass('ui-icon-circle-triangle-n')) {
				$(this).parent().parent().next().show("blind");
			} else {
				$(this).parent().parent().next().hide("blind");
			}
		});

	$("#search_model_id,#search_step").select2Buttons();
	$("#search_step option[value='0']").attr("selected","selected").trigger("change");
	$("#search_step option[value='1']").attr("selected","selected").trigger("change");
	$("#search_step option[value='2']").attr("selected","selected").trigger("change");
	$("#search_step option[value='3']").attr("selected","selected").trigger("change");
	
    setReferChooser($("#add_model_id"));

	$("#search_inline_date_start,#search_finish_time_start")
		.datepicker({
			showButtonPanel : true,
			dateFormat : "yy/mm/dd",
			currentText : "今天"
		});
	//show_search_manage([]);
	 findit();
	$("#reset_button").click(function(){
		$("#searchform input[type!='button'][type!='radio'][id!='filter_l_low'], #searchform textarea").val("");
		$("#searchform select").val("").trigger("change");
	});	

	$("#search_button").click(function(){
		 findit();
	});	
	
	/*画面按钮enable、disable*/
	$("#add_button").click(showAdd);
	$("#edit_button").click(showEdit);
	$("#delete_button").click(showDelete);
	$("#add_manage_button").click(addManage);
	
	$("#partial_instock_button").click(showNSMap);
	$("#partial_outstock_button").click(partialOutstock);
	$("#partial_remove_button").click(showNSMap);
//	$("#component_outstock_button").click(componentOutstock);
	$("#cancle_button").click(cancleManage);
	$("#print_label_button").click(printNSLabelTicket);
	$("#print_info_button").click(printNSInfoTicket);
	
});

/** 组件设置追加画面处理 */
var showAdd = function() {
	var $pop_window = $("#pop_window_new");
	// 清空输入项
	$("#pop_window_new input[type!='button'][type!='radio'], #pop_window_new textarea").val("");
	//$("#add_code").next("p").remove();
	$("#pop_window_new select").val("").trigger("change");
	
	$("#add_code").unbind("keyup");
	$("#add_code").bind("keyup", function(e){
		var kcode =e.keyCode;

		if (this.value.length >= 6){
			if( kcode > 105 || kcode < 48){
				return;
			}
			$("#add_code").next().remove();
			autoSearch(null, 1);
		}
	});
	
	$("#abandon_insert > table:eq(1) tbody").html(getSeqTr());
	
	/*验证*/
	$("#abandon_insert").validate({
		rules:{	
			mode_id:{
				required:true
			},
			safety_lever:{
				number:true,
				maxlength:5
			},
			identify_code:{
				required:true
			}
		}
	});
	$pop_window.dialog({
		resizable : false,
		width : '640px',
		modal : true,
		title : "加入组件设置",
		buttons : {
			"确认" : function() {
				if($("#add_benchmark").val() <= 0 ){
					errorPop("基准库存不能小于0");
					return false;
				}
				if($("#new_partial_id").val() == "" || $("#new_partial_id").val() == null ){
					errorPop("请输入正确的组件代码");
					return false;
				}
				if($("#abandon_insert").valid()){
					var data = {
							"model_id":$("#add_model_id").val(),
							"component_partial_id" : $("#new_partial_id").val(),
							"identify_code" : $("#add_identify_code").val(),
							"safety_lever":$("#add_safety_lever ").val()
						};
					
					var details = $("#abandon_insert > table:eq(1) tbody tr").not(".addseqTr");
					if (!details.length) {
						errorPop("请设定至少一个零件。");
						return;
					}
					var errorMessage = null;
					details.each(function(idx, ele){
						var $tr = $(ele);
						var partial_id = $tr.find(".add_partial_id").val();
						if (!partial_id) {
							errorMessage = "请选择NS组件用零件。";
							return;
						}
						var quantity = $tr.find(".add_quantity").val();
						if (!quantity && !isNaN(quantity)) {
							errorMessage = "请输入正确的零件数量。";
							return;
						}
						data["partial.partial_id[" + idx + "]"] = partial_id;
						data["partial.partial_code[" + idx + "]"] = $tr.find(".add_partial_code").val() || "";
						data["partial.partial_name[" + idx + "]"] = $tr.find(".add_partial_name").val() || "";
						data["partial.quantity[" + idx + "]"] = $tr.find(".add_quantity").val() || "";
					});
					if (errorMessage != null) {
						errorPop(errorMessage);
						return;
					}
					
					$.ajax({
						beforeSend : ajaxRequestType,
						async : true,
						url : servicePath + '?method=doinsertSetting',
						cache : false,
						data : data,
						type : "post",
						dataType : "json",
						success : ajaxSuccessCheck,
						error : ajaxError,
						complete : showAdd_handleComplete
					});
				
			}
		},
			"取消" : function() {
				$pop_window.dialog("close");
			}
		}
	});
};

function showAdd_handleComplete(xhrobj, textStatus) {

	var resInfo = null;

	// 以Object形式读取JSON responseText获取来自服务器响应的数据
	eval('resInfo =' + xhrobj.responseText);

	if (resInfo.errors.length > 0) {
		// 共通出错信息框
		treatBackMessages(null, resInfo.errors);
	} else {
		$("#pop_window_new").dialog("close");
	    findit();
	}
};

var keepSearchData;
var findit = function(data) {
	if (!data) {
		keepSearchData = {
			"search_model_id" : $("#search_model_id").val(),
			"search_component_code": $("#search_component_code").val(),
			"search_partial_code" : $("#search_partial_code").val(),
			"search_serial_no" : $("#search_serial_no").val(),
			"search_stock_code" : $("#search_stock_code").val(),
			"search_step" : $("#search_step").val() && $("#search_step").val().toString(),
			"search_inline_date_start" : $("#search_inline_date_start").val(),
			"search_inline_date_end" : $("#search_inline_date_end").val(),
			"search_finish_time_start" : $("#search_finish_time_start").val(),
			"search_finish_time_end" : $("#search_finish_time_end").val()
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
		complete : searchManage_handleComplete
	});
	
	$.ajax({
		beforeSend : ajaxRequestType,
		async : true,
		url : servicePath + '?method=searchSetting',
		cache : false,
		data : keepSearchData,
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : searchSetting_handleComplete
	});

    enableButton();
};

function searchManage_handleComplete(xhrobj){
	var resInfo = null;
	try {
		// 以Object形式读取JSON
		eval('resInfo =' + xhrobj.responseText);
		if (resInfo.errors.length > 0) {
			// 共通出错信息框
			treatBackMessages("#searcharea", resInfo.errors);
		} else {
			// 标题修改
			show_search_manage(resInfo.componentManage, resInfo.inlineDateCheck);
		}
	} catch (e) {};
}


function show_search_manage(componentManage, inlineDateCheck) {
	for (var iCl in componentManage) {
		var rData = componentManage[iCl];
		var count = 0;
		if (rData.released_count_quantity)
			count += parseInt(rData.released_count_quantity);
		if (rData.used_count_quantity)
			count += parseInt(rData.used_count_quantity);
		rData["cost_count_quantity"] = count;
	}
	if ($("#gbox_component_manage").length > 0) {
		$("#component_manage").jqGrid().clearGridData();
		$("#component_manage").jqGrid('setGridParam', {data : componentManage})
			.trigger("reloadGrid", [ {current : false} ]);
	} else {
		$("#component_manage").jqGrid({data : componentManage,
			height : 701,
			width : 992,
			rowheight : 23,
			datatype : "local",
			colNames : [ 'component_key','model_id', '型号', '订购来源', '进度', '子零件', '库位编号',
					'投入日期', '组件序列号', 
					'制作者', '组件代码', '组装完成时间','采用'],
			colModel : [ {
				name : 'component_key',
				index : 'component_key',
				hidden : true
			}, {
				name : 'model_id',
				index : 'model_id',
				hidden : true
			}, {
				name : 'model_name',
				index : 'model_name',
				width : 60
			}, {
				name : 'origin_omr_notifi_no',
				index : 'origin_omr_notifi_no',
				width : 60
			}, {
				name : 'step',
				index : 'step',
				width : 70,
				formatter:'select',
				editoptions : {
					value : $("#res_step").val()
				}
			}, {
				name : 'partial_code',
				index : 'partial_code',
				width : 60
			}, {
				name : 'stock_code',
				index : 'stock_code',
				width : 60
			}, {
				name : 'inline_date',
				index : 'inline_date',
				align : 'center',
						width : 50,
						formatter:'date', formatoptions:{srcformat:'Y/m/d',newformat:'Y/m/d'}
			}, {
				name : 'serial_no',
				index : 'serial_no',
				width : 70
			}, {
				name : 'operator_name',
				index : 'operator_name',
				width : 70,
				align : 'left'
			},
			{
				name : 'component_code',
				index : 'component_code',
				width : 50,
				align : 'center'
			},{
				name : 'finish_time',
				index : 'finish_time',
				align : 'center',
						width : 50,
						formatter:'date', formatoptions:{srcformat:'Y/m/d H:i:s',newformat:'m-d H\\h'}
			}, {
				name : 'target_omr_notifi_no',
				index : 'target_omr_notifi_no',
				width : 50,
				align : 'center'
			}],
			rowNum : 50,
			rownumbers:true,
			toppager : false,
			pager : "#component_manage_pager",
			viewrecords : true,
			gridview : true,
			pagerpos : 'right',
			pgbuttons : true,
			pginput : false,
			recordpos : 'left',
			hidegrid : false,
			deselectAfterSort : false,
			onSelectRow : enableButton,
			viewsortcols : [ true, 'vertical', true ],
			gridComplete : function() {
				var IDS = $("#component_manage").getDataIDs();
				// 当前显示多少条
				var length = IDS.length;
				var $exd_list = $("#component_manage");
				for (var i = 0; i < length; i++) {
					// 从上到下获取一条信息
					var rowData = $exd_list.jqGrid('getRowData', IDS[i]);
					var step = parseInt(rowData["step"]);

					// 状态为"子零件待入库"的数据，“库位编号”单元格变成error
					if (step == 0 ) {
						$exd_list.find("tr#" + IDS[i] + " td[aria\\-describedby='component_manage_stock_code']").addClass("ui-state-error");
					}
					//状态为"制作中"的数据，当前日期与投入日期相隔5个工作日的记录，把“投入日期”单元格变成highlight
					if (step == 2) {
						// 画面日期
						var date1 = new Date(inlineDateCheck);
						var date2 = new Date(rowData["inline_date"]);

						// 作差
						var temp = (date1 - date2);
						if (temp >= 0) {
							$exd_list.find("tr#" + IDS[i] + " td[aria\\-describedby='component_manage_inline_date']").addClass("ui-state-highlight");
						}
					}
				}
			}
		});
	}
}

function searchSetting_handleComplete(xhrobj){
	var resInfo = null;
	try {
		// 以Object形式读取JSON
		eval('resInfo =' + xhrobj.responseText);
		if (resInfo.errors.length > 0) {
			// 共通出错信息框
			treatBackMessages("#searcharea", resInfo.errors);
		} else {
			// 标题修改
			show_search_setting(resInfo.componentSetting);
		}
	} catch (e) {};
}


function show_search_setting(componentSetting) {
	for (var iCl in componentSetting) {
		var rData = componentSetting[iCl];
		var count = 0;
		if (rData.released_count_quantity)
			count += parseInt(rData.released_count_quantity);
		if (rData.used_count_quantity)
			count += parseInt(rData.used_count_quantity);
		rData["cost_count_quantity"] = count;
	}
	console.log(componentSetting);
	if ($("#gbox_component_setting").length > 0) {
		$("#component_setting").jqGrid().clearGridData();
		$("#component_setting").jqGrid('setGridParam', {data : componentSetting})
			.trigger("reloadGrid", [ {current : false} ]);
	} else {
		$("#component_setting").jqGrid({data : componentSetting,
			height : 231,
			width : 992,
			rowheight : 23,
			datatype : "local",
			colNames : [ 'model_id', '机种', '型号', '组件代码', '子零件<br>代码', '安全库存',
					'子零件<br>待入库数', '子零件<br>已入库数', '组装中数', '组装完成数'],
			colModel : [ {
				name : 'model_id',
				index : 'model_id',
				hidden : true
			}, {
				name : 'category_name',
				index : 'category_name',
				width : 60
			}, {
				name : 'model_name',
				index : 'model_name',
				width : 60
			},{
				name : 'component_code',
				index : 'component_code',
				width : 50,
				align : 'center'
			}, {
				name : 'partial_code',
				index : 'partial_code',
				width : 60
			}, {
				name : 'safety_lever',
				index : 'safety_lever',
				width : 60,
				align : 'right'
			}, {
				name : 'cnt_partial_step0',
				index : 'cnt_partial_step0',
				width : 70,
				align : 'right'
			},{
				name : 'cnt_partial_step1',
				index : 'cnt_partial_step1',
				width : 70,
				align : 'right'
			},{
				name : 'cnt_partial_step2',
				index : 'cnt_partial_step2',
				width : 70,
				align : 'right'
			}, {
				name : 'cnt_partial_step3',
				index : 'cnt_partial_step3',
				width : 70,
				align : 'right'
			}],
			rowNum : 20,
			rownumbers:true,
			toppager : false,
			pager : "#component_setting_pager",
			viewrecords : true,
			gridview : true,
			pagerpos : 'right',
			pgbuttons : true,
			pginput : false,
			recordpos : 'left',
			hidegrid : false,
			deselectAfterSort : false,
			ondblClickRow : function() {
				$("#search_model_id option[value='0']").attr("selected","selected").trigger("change");
				var row = $("#component_setting").jqGrid("getGridParam", "selrow");// 得到选中行的ID
				var rowData = $("#component_setting").getRowData(row);
				$("#search_model_id").val(rowData.model_id).trigger("change");
			},
			onSelectRow : enableButton,
			viewsortcols : [ true, 'vertical', true ],
			gridComplete : function() {
				var IDS = $("#component_setting").getDataIDs();
				// 当前显示多少条
				var length = IDS.length;
				var $exd_list = $("#component_setting");
				for (var i = 0; i < length; i++) {
					// 从上到下获取一条信息
					var rowData = $exd_list.jqGrid('getRowData', IDS[i]);
					var cnt_partial_step2 = parseInt(rowData["cnt_partial_step2"]);
					var cnt_partial_step3 = parseInt(rowData["cnt_partial_step3"]);
					var safety_lever = parseInt(rowData["safety_lever"]);

					// 安全库存有设置的情况下，（组装中数 + 组装完成数）数量小于安全库存，则安全库存单元格变成highlight。
					if ((safety_lever - cnt_partial_step2 - cnt_partial_step3) > 0 ) {
						$exd_list.find("tr#" + IDS[i] + " td[aria\\-describedby='component_setting_safety_lever']").addClass("ui-state-highlight");
					}
				}
			}
		});
	}
}

var autoSearch = function ($tr, mode){
	var component_code;
	if (mode == 1) {
		component_code = $("#add_code").val();
	} else if (mode == 2) {
		component_code = $("#edit_code").val();
	} else {
		component_code = $tr.find("input:eq(0)").val();
	}
	var ConsumableFlgdata ={
			"component_code" : component_code
		};
	$.ajax({
		beforeSend : ajaxRequestType,
		async : true,
		url : servicePath + '?method=getAutocomplete',
		cache : false,
		data : ConsumableFlgdata,
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : function(xhrobj){
			var resInfo = null;
			eval('resInfo =' + xhrobj.responseText);
			partialCode = resInfo.sPartialCode;
			reasult_flg = resInfo.reasult_flg;
			if(reasult_flg == "1"){
				if (mode == 1) {
					if(partialCode.partial_id != $("#new_partial_id").val()){
						$("#new_partial_id").val(partialCode.partial_id);
						$("#add_code").val(partialCode.component_code).blur();
					}
				} else if (mode == 2) {
					if(partialCode.partial_id != $("#edit_partial_id").val()){
						$("#edit_partial_id").val(partialCode.partial_id);
						$("#edit_code").val(partialCode.component_code).blur();
					}
				} else {
					$tr.find("input:eq(0)").val(partialCode.component_code);
					$tr.find(".add_partial_name").val(partialCode.partial_name);
					$tr.find(".add_partial_id").val(partialCode.partial_id);
				}
			}
		}
	});
};

/*修改库存设置button事件*/
var showEdit = function() {
	var isFact = $('#hidden_isFact').val();
	if (isFact == "false") {
		return;
	}

	var row = $("#component_setting").jqGrid("getGridParam", "selrow");// 得到选中行的ID
	if (row == null) {
		errorPop("请选择一条NS组件设置。");
		return;
	}
	var rowData = $("#component_setting").getRowData(row);
	var data = {
		"model_id" : rowData.model_id
	};
	$.ajax({
		beforeSend : ajaxRequestType,
		async : true,
		url : servicePath + '?method=editSetting',
		cache : false,
		data : data,
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : showEdit_Complete
	});
};

/*修改库存设置加载页面事件*/
var showEdit_Complete = function(xhrobj,textStatus){
	try {
		var resInfo = null;
		// 以Object形式读取JSON
		eval('resInfo =' + xhrobj.responseText);
		if (resInfo.errors.length > 0) {
			// 共通出错信息框
			treatBackMessages(null, resInfo.errors);
		} else {
			// 修改库存设置弹出框初期化
			
			$("#pop_window_edit input[type='text']").removeClass("valid errorarea-single");
			$("#label_modelname").text(resInfo.returnForm.model_name);
			$("#edit_model_id").val(resInfo.returnForm.model_id);
			$("#edit_partial_id").val(resInfo.returnForm.component_partial_id);
			$("#edit_code").val(resInfo.returnForm.component_code);
			$("#label_identify_code").text(resInfo.returnForm.identify_code);	
			$("#edit_identify_code").val(resInfo.returnForm.identify_code);
			$("#edit_safety_lever").val(resInfo.returnForm.safety_lever);
			
			$("#edit_code").unbind("keyup");
			$("#edit_code").bind("keyup", function(e){
				var kcode =e.keyCode;

				if (this.value.length >= 6){
					if( kcode > 105 || kcode < 48){
						return;
					}
					$("#edit_code").next().remove();
					autoSearch(null, 2);
				}
			});
			
			$("#abandon_modify > table:eq(1) tbody").html(getSeqTr(resInfo.premakePartials));
			
			/*验证*/
			$("#abandon_modify").validate({
				rules:{	
					safety_lever:{
						digits:true,
						maxlength:5
					}
				}
			});

			var $pop_window = $("#pop_window_edit");
			$pop_window.dialog({
			    resizable : false,
				modal : true,
				title : "修改库存设置",
				width : '640px',
				buttons : {
					"确认" : function() {
						if( $("#abandon_modify").valid()) {
							update_consumable_manage();
						}
					},
					"取消" : function() {
						$(this).dialog("close");
					}
				}
			});
		}
		
	} catch (e) {}
};

/*更新consumable_manage表*/
var update_consumable_manage = function(){
	var data = {
			"model_id":$("#edit_model_id").val(),
			"component_partial_id" : $("#edit_partial_id").val(),
			"identify_code" : $("#edit_identify_code").val(),
			"safety_lever":$("#edit_safety_lever ").val()
		};
	
	var details = $("#abandon_modify > table:eq(1) tbody tr").not(".addseqTr");
	if (!details.length) {
		errorPop("请设定至少一个零件。");
		return;
	}
	var errorMessage = null;
	details.each(function(idx, ele){
		var $tr = $(ele);
		var partial_id = $tr.find(".add_partial_id").val();
		if (!partial_id) {
			errorMessage = "请选择NS组件用零件。";
			return;
		}
		var quantity = $tr.find(".add_quantity").val();
		if (!quantity && !isNaN(quantity)) {
			errorMessage = "请输入正确的零件数量。";
			return;
		}
		data["partial.partial_id[" + idx + "]"] = partial_id;
		data["partial.partial_code[" + idx + "]"] = $tr.find(".add_partial_code").val() || "";
		data["partial.partial_name[" + idx + "]"] = $tr.find(".add_partial_name").val() || "";
		data["partial.quantity[" + idx + "]"] = $tr.find(".add_quantity").val() || "";
	});
	if (errorMessage != null) {
		errorPop(errorMessage);
		return;
	}
	$.ajax({
			beforeSend : ajaxRequestType,
			async : true,
			url : servicePath + '?method=doUpdateSetting',
			cache : false,
			data : data,
			type : "post",
			dataType : "json",
			success : ajaxSuccessCheck,
			error : ajaxError,
			complete : update_handleComplete
	});
};

function update_handleComplete(xhrobj, textStatus) {

	var resInfo = null;

	// 以Object形式读取JSON responseText获取来自服务器响应的数据
	eval('resInfo =' + xhrobj.responseText);

	if (resInfo.errors.length > 0) {
		// 共通出错信息框
		treatBackMessages(null, resInfo.errors);
	} else {
		$("#pop_window_edit").dialog("close");
	    findit();
	}
};

/*删除*/
var showDelete = function(rid) {
	var isFact = $('#hidden_isFact').val();
	if (isFact == "false") {
		return;
	}

	var row = $("#component_setting").jqGrid("getGridParam", "selrow");// 得到选中行的ID
	var rowData = $("#component_setting").getRowData(row);
	var data = {
		"model_id" : rowData.model_id
	};
	var cnt_partial_step0 = parseInt(rowData.cnt_partial_step0);
	var cnt_partial_step1 = parseInt(rowData.cnt_partial_step1);
	if ((cnt_partial_step0 + cnt_partial_step1) > 0 ) {
		warningConfirm("库存中还有子零件。确认要取消（" + encodeText(rowData.model_name) + "）的组件组装作业吗？", function() {
			// Ajax提交
			$.ajax({
				beforeSend : ajaxRequestType,
				async : true,
				url : servicePath + '?method=doDeleteSetting',
				cache : false,
				data : data,
				type : "post",
				dataType : "json",
				success : ajaxSuccessCheck,
				error : ajaxError,
				complete : delete_handleComplete
			});
		
		}, null, "删除确认");
	} else {
		warningConfirm("删除不能恢复。确认要取消（" + encodeText(rowData.model_name) + "）的组件组装作业吗？", function() {
			// Ajax提交
			$.ajax({
				beforeSend : ajaxRequestType,
				async : true,
				url : servicePath + '?method=doDeleteSetting',
				cache : false,
				data : data,
				type : "post",
				dataType : "json",
				success : ajaxSuccessCheck,
				error : ajaxError,
				complete : delete_handleComplete
			});
		}, null, "删除确认");
	}
};

var delete_handleComplete = function(xhrobj, textStatus) {
	var resInfo = null;
	try {
		// 以Object形式读取JSON
		eval('resInfo =' + xhrobj.responseText);
		if (resInfo.errors.length > 0) {
			// 共通出错信息框
			treatBackMessages("#searcharea", resInfo.errors);
		} else {
			infoPop("删除已经完成。", null, "删除");
			findit();
		}
	}catch (e) {};
};

/*新建*/
var addManage = function() {
	var is105 = $('#hidden_is105').val();
	console.log(is105);
	if (is105 == "false") {
		return;
	}

	var row = $("#component_setting").jqGrid("getGridParam", "selrow");// 得到选中行的ID
	var rowData = $("#component_setting").getRowData(row);
	var data = {
		"model_id" : rowData.model_id
	};

	warningConfirm("是否要为（（" + encodeText(rowData.model_name) + "）虚拟订购一套子零件？", function() {
		// Ajax提交
		$.ajax({
			beforeSend : ajaxRequestType,
			async : true,
			url : servicePath + '?method=doInsertManage',
			cache : false,
			data : data,
			type : "post",
			dataType : "json",
			success : ajaxSuccessCheck,
			error : ajaxError,
			complete : addManage_handleComplete
		});
	
	}, null, "新建确认");
};

var addManage_handleComplete = function(xhrobj, textStatus) {
	var resInfo = null;
	try {
		// 以Object形式读取JSON
		eval('resInfo =' + xhrobj.responseText);
		if (resInfo.errors.length > 0) {
			// 共通出错信息框
			treatBackMessages("#searcharea", resInfo.errors);
		} else {
			findit();
		}
	}catch (e) {};
};

var showNSMap=function() {
	$.ajax({
		beforeSend : ajaxRequestType,
		async : true,
		url : servicePath + '?method=getNSempty',
		cache : false,
		data : null,
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : function(xhrobj) {
			var resInfo = null;
			try {
				console.log("OK");
				// 以Object形式读取JSON
				eval('resInfo =' + xhrobj.responseText);
				if (resInfo.errors.length > 0) {
					// 共通出错信息框
					treatBackMessages(null, resInfo.errors);
				} else {
					$("#ns_pop").hide();
					$("#ns_pop").load("widgets/partial/ns_map.jsp", function(responseText, textStatus, XMLHttpRequest) {
						 //新增
				
						$("#ns_pop").dialog({
							position : [ 800, 20 ],
							title : "NS子零件入库选择",
							width : 1000,
							show: "blind",
							height : 640,// 'auto' ,
							resizable : false,
							modal : true,
							minHeight : 200,
							buttons : {}
						});

						$("#ns_pop").find("td").addClass("wip-empty");
						for (var iheap in resInfo.heaps) {
							$("#ns_pop").find("td[nsid="+resInfo.heaps[iheap]+"]").removeClass("wip-empty").addClass("ui-storage-highlight wip-heaped");
						}

						//$("#wip_pop").css("cursor", "pointer");
						$("#ns_pop").find(".ui-widget-content").click(function(e){
							if ("TD" == e.target.tagName) {
								if (!$(e.target).hasClass("wip-heaped")) {
									selnsid = $(e.target).attr("nsid");
									partialInstock(selnsid);
								}
							}
						});

						$("#ns_pop").show();
					});
				}
			} catch (e) {
				alert("name: " + e.name + " message: " + e.message + " lineNumber: "
						+ e.lineNumber + " fileName: " + e.fileName);
			};
		}
	});
};

/* 子零件入库处理 */
var partialInstock = function(selnsid) {

	var row = $("#component_manage").jqGrid("getGridParam", "selrow");// 得到选中行的ID
	var rowData = $("#component_manage").getRowData(row);
	var data = {
		"component_key" : rowData.component_key,
		"model_id" : rowData.model_id,
		"stock_code" : selnsid
	};

	// Ajax提交
	$.ajax({
		beforeSend : ajaxRequestType,
		async : true,
		url : servicePath + '?method=doPartialInstock',
		cache : false,
		data : data,
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : partialInstock_handleComplete
	});
};

var partialInstock_handleComplete = function(xhrobj, textStatus) {
	var resInfo = null;
	try {
		// 以Object形式读取JSON
		eval('resInfo =' + xhrobj.responseText);
		if (resInfo.errors.length > 0) {
			// 共通出错信息框
			treatBackMessages("#searcharea", resInfo.errors);
		} else {
			$("#ns_pop").dialog('close');	
			findit();
		}
	}catch (e) {};
};

/* 子零件出库处理 */
var partialOutstock = function() {

	var row = $("#component_manage").jqGrid("getGridParam", "selrow");// 得到选中行的ID
	var rowData = $("#component_manage").getRowData(row);
	var data = {
		"component_key" : rowData.component_key,
		"model_id" : rowData.model_id
	};

	// Ajax提交
	$.ajax({
		beforeSend : ajaxRequestType,
		async : true,
		url : servicePath + '?method=doPartialOutstock',
		cache : false,
		data : data,
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : partialOutstock_handleComplete
	});
};

var partialOutstock_handleComplete = function(xhrobj, textStatus) {
	var resInfo = null;
	try {
		// 以Object形式读取JSON
		eval('resInfo =' + xhrobj.responseText);
		if (resInfo.errors.length > 0) {
			// 共通出错信息框
			treatBackMessages("#searcharea", resInfo.errors);
		} else {
			infoPop(resInfo.resultMsg, null, "子零件出库");
			findit();
		}
	}catch (e) {};
};

/*删除*/
var cancleManage = function(rid) {
	var isFact = $('#hidden_isFact').val();
	if (isFact == "false") {
		return;
	}

	var row = $("#component_manage").jqGrid("getGridParam", "selrow");// 得到选中行的ID
	var rowData = $("#component_manage").getRowData(row);
	var data = {
		"component_key" : rowData.component_key,
		"model_id" : rowData.model_id,
		"step" : rowData.step,
		"serial_no" : rowData.serial_no
	};
	
	warningConfirm("是否要为废弃此组件？", function() {
		// Ajax提交
		$.ajax({
			beforeSend : ajaxRequestType,
			async : true,
			url : servicePath + '?method=doCancleManage',
			cache : false,
			data : data,
			type : "post",
			dataType : "json",
			success : ajaxSuccessCheck,
			error : ajaxError,
			complete : cancleManage_handleComplete
		});
	}, null, "废弃确认");
};

var cancleManage_handleComplete = function(xhrobj) {
	var resInfo = null;
	try {
		// 以Object形式读取JSON
		eval('resInfo =' + xhrobj.responseText);
		if (resInfo.errors.length > 0) {
			// 共通出错信息框
			treatBackMessages("#searcharea", resInfo.errors);
		} else {
			infoPop("组件废弃已经完成。", null, "废弃");
			findit();
		}
	}catch (e) {};
};

var printNSInfoTicket = function() {
	
	var row = $("#component_manage").jqGrid("getGridParam", "selrow");// 得到选中行的ID
	var rowData = $("#component_manage").getRowData(row);
	var model_name = rowData.model_name;
	var serial_no = rowData.serial_no;
	var data = {
		"component_key" : rowData.component_key,
		"model_id" : rowData.model_id,
		"model_name" : model_name,
		"partial_code" : rowData.partial_code,
		"serial_no" : serial_no
	};

	// Ajax提交
	$.ajax({
		beforeSend: ajaxRequestType, 
		async: false, 
		url: servicePath + '?method=printNSInfoTicket', 
		cache: false, 
		data: data, 
		type: "post", 
		dataType: "json", 
		success: ajaxSuccessCheck, 
		error: ajaxError, 
		complete:  function(xhrobj){
			var resInfo = null;

			try {
				// 以Object形式读取JSON
				eval('resInfo =' + xhrobj.responseText);
			
				if (resInfo.errors.length > 0) {
					// 共通出错信息框
					treatBackMessages(null, resInfo.errors);
				} else {
					var iframe = document.createElement("iframe");
		            iframe.src = "download.do"+"?method=output&fileName="+ model_name + "-" + serial_no +"-ticket.pdf&filePath=" + resInfo.tempFile;
		            iframe.style.display = "none";
		            document.body.appendChild(iframe);
				}
			} catch(e) {
				
			}
		}
	});
};

var printNSLabelTicket = function() {
	
	var row = $("#component_manage").jqGrid("getGridParam", "selrow");// 得到选中行的ID
	var rowData = $("#component_manage").getRowData(row);
	var model_name = rowData.model_name;
	var serial_no = rowData.serial_no;
	var data = {
		"component_key" : rowData.component_key,
		"model_id" : rowData.model_id,
		"model_name" : model_name,
		"partial_code" : rowData.partial_code,
		"serial_no" : serial_no
	};

	// Ajax提交
	$.ajax({
		beforeSend: ajaxRequestType, 
		async: false, 
		url: servicePath + '?method=printNSLabelTicket', 
		cache: false, 
		data: data, 
		type: "post", 
		dataType: "json", 
		success: ajaxSuccessCheck, 
		error: ajaxError, 
		complete:  function(xhrobj){
			var resInfo = null;

			try {
				// 以Object形式读取JSON
				eval('resInfo =' + xhrobj.responseText);
			
				if (resInfo.errors.length > 0) {
					// 共通出错信息框
					treatBackMessages(null, resInfo.errors);
				} else {
					var iframe = document.createElement("iframe");
		            iframe.src = "download.do"+"?method=output&fileName="+ model_name + "-" + serial_no +"-ticket.pdf&filePath=" + resInfo.tempFile;
		            iframe.style.display = "none";
		            document.body.appendChild(iframe);
				}
			} catch(e) {
				
			}
		}
	});
};

/*判断修改库存设置，NS组件库存管理按钮enable、disable(当选择了消耗品之后enable；否则是disable)*/
var enableButton = function() {

	// 权限判定并设置按钮显示
	var isFact = $('#hidden_isFact').val();
	if (isFact == "true") {
		$("#add_button").disable();
		$("#edit_button").disable();
		$("#delete_button").disable();
		$("#partial_instock_button").disable();
		$("#partial_outstock_button").disable();
		$("#partial_remove_button").disable();
		$("#component_outstock_button").disable();
		$("#cancle_button").disable();
	}
	var is105 = $('#hidden_is105').val();
	if (is105 == "true") {
		$("#add_manage_button").disable();
	}
	var is107 = $('#hidden_is107').val();
	if (is107 == "true") {
		$("#print_label_button").disable();
		$("#print_info_button").disable();
	}
	// 选择NS组件库存管理行，并获取行数
	var rowSetting = $("#component_setting").jqGrid("getGridParam", "selrow");
	if (rowSetting !=null) {
		$("#add_button").enable();
		$("#edit_button").enable();
		$("#delete_button").enable();
		$("#add_manage_button").enable();
	}
	// 选择NS组件库存管理行，并获取行数
	var row = $("#component_manage").jqGrid("getGridParam", "selrow");
	if (row !=null) {
		var rowdata = $("#component_manage").getRowData(row);
		var step = parseInt(rowdata["step"]);
		if (step == 0) {
			$("#partial_instock_button").enable();
		} 
		if (step == 1) {
			$("#partial_outstock_button").enable();
			$("#partial_remove_button").enable();
		} 
		if (step == 3) {
			$("#partial_remove_button").enable();
			$("#component_outstock_button").enable();
		} 
		if (step < 4) {
			$("#cancle_button").enable();
		} 
		if (step > 2) {
			$("#print_label_button").enable();
			$("#print_info_button").enable();
		} 
	}
};

var getSeqTr = function(bean){
	if (bean && bean.length > 0) {
		var $trs = $("");
		for (var i in bean) {
			var en = bean[i];
			var $tr = $(strSeqTr);
			$tr.append("<td><input type='button' class='seqdel' value='×'></td>");
			$tr.find(".add_partial_code").val(en.code);
			$tr.find(".add_partial_id").val(en.partial_id);
			$tr.find(".add_partial_name").val(en.partial_name);
			$tr.find(".add_quantity").val(en.quantity);
			$trs.after($tr);
		}
		$trs.after(getSeqTr());
		$trs.find(".seqdel").click(function(){$(this).parent().parent().remove()});
		return $trs;
	} else {
		$tr = $(strSeqTr);
		$tr.append("<td><input type='button' class='seqadd' value='＋'></td>");
		$tr.addClass("addseqTr");
		$tr.find(".seqadd").click(function(){
			$(this).removeClass("seqadd").addClass("seqdel").val('×')
				.unbind("click").click(function(){$tr.remove()});
			$tr.removeClass("addseqTr").after(getSeqTr());
		});
		$tr.find(".add_partial_code").unbind("keyup");
		$tr.find(".add_partial_code").bind("keyup", function(e){
			var kcode =e.keyCode;

			if (this.value.length >= 6){
				if( kcode > 105 || kcode < 48){
					return;
				}
				$("#add_partial_code").next().remove();
				autoSearch($tr, 0);
			}
		});
		return $tr;
	}
}

