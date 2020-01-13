var servicePath = "consumable_list.do";
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

	$("#leak_set, #add_popular_item_set, #edit_popular_item_set,#add_hazardous_flg,#edit_hazardous_flg").buttonset();
	
	
	$("#edit_in_shelf_cost,#edit_out_shelf_cost").find("option:eq(0)").remove();
	
	$("#search_type," 
	+ "#add_type, #add_supply_cycle, #add_supply_day,"
	+ "#edit_type, #edit_supply_cycle, #edit_supply_day,#add_in_shelf_cost,#add_out_shelf_cost,#edit_in_shelf_cost,#edit_out_shelf_cost").select2Buttons();

	$("#search_count_period_start,#search_count_period_end")
		.datepicker({
			showButtonPanel : true,
			dateFormat : "yy/mm/dd",
			currentText : "今天"
		});
	show_consumable_list([]);
	findit();
	$("#reset_button").click(function(){
		$("#searchform input[type!='button'][type!='radio'][id!='filter_l_low'], #searchform textarea").val("");
		$("#searchform select").val("").trigger("change");
		$("#leak_a").attr("checked","checked").trigger("change");
	});	
	
	$("#reason_list").on("click", "td" , function(){
		$("#adjust_reason").val(this.innerText);
	});

	/* 上传button */
	$("#uploadbutton").click(function() {
		import_upload_file();
	});

	$("#search_button").click(function(){
		 findit();
	});	
    
    /*修改库存设置，移出消耗品库存按钮enable、disable*/
	$("#add_button").click(showAdd);
	$("#edit_button").click(showEdit);
	$("#remove_button").click(
			function(){
				warningConfirm("确认移出消耗品库存？", function(){delConsumable();}, null);
			}
	);
//	$("#image_load_button").click(showImgLoad);
	$("#adjust_button").click(showAdjust);
	$("#measuring_set_button").click(showSet);
	$("#post_clipboard_button").bind("click", postClipboard);
	$("#download_button").bind("click", download);
	$("#heatshrinkable_tube_button").hide().bind("click", getHeatshrinkableLength);
});

/*判断修改库存设置，移出消耗品库存按钮enable、disable(当选择了消耗品之后enable；否则是disable)*/
var enableButton = function() {

	// 选择行，并获取行数
	var row = $("#consumable_list").jqGrid("getGridParam", "selrow");
	if (row !=null) {
		$("#edit_button").enable();
		$("#remove_button").enable();
		$("#measuring_set_button").enable();
		var rowdata = $("#consumable_list").getRowData(row);

		if (rowdata['type'] == "6") {
			$("#heatshrinkable_tube_button").show();
		} else {
			$("#heatshrinkable_tube_button").hide();
		}
	} else {
		$("#edit_button").disable();
		$("#remove_button").disable();
		$("#measuring_set_button").disable();
		$("#heatshrinkable_tube_button").hide();
	}
};

var keepSearchData;
var findit = function(data) {
	if (!data) {
		keepSearchData = {
			"code" : $("#search_code").val(),
			"type": $("#search_type").val(),
			"stock_code" : $("#search_stock_code").val(),
			"leak" : $("#leak_set input:checked").val(),
			"consumed_rate_alarmline" : $("#filter_l_low").val(),
			"search_count_period_start" : $("#search_count_period_start").val(),
			"search_count_period_end" : $("#search_count_period_end").val()
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

function search_handleComplete(xhrobj, textStatus){
	var resInfo = null;
	try {
		// 以Object形式读取JSON
		eval('resInfo =' + xhrobj.responseText);
		if (resInfo.errors.length > 0) {
			// 共通出错信息框
			treatBackMessages("#searcharea", resInfo.errors);
		} else {
			// 标题修改
			show_consumable_list(resInfo.consumableList);
		}
	} catch (e) {};
    enableButton();
}


var showAdd = function() {
	var $pop_window = $("#pop_window_new");
	// 清空输入项
	$("#pop_window_new input[type!='button'][type!='radio'], #pop_window_new textarea").val("");
	$("#add_code").next("p").remove();
	$("#pop_window_new select").val("").trigger("change");
	$("#add_popular_item_n").attr("checked","checked").trigger("change");
	$("#add_hazardous_flg_n").attr("checked","checked").trigger("change");
	
	$("#add_code").unbind("keyup");
	$("#add_code").bind("keyup", function(e){
		var kcode =e.keyCode;

		if (this.value.length >= 6){
			if( kcode > 105 || kcode < 48){
				return;
			}
			$("#add_code").next().remove();
			autoSearch();
		}
	});
	
	/*验证*/
	$("#abandon_insert").validate({
		rules:{	
			type:{
				required:true
			},
			benchmark:{
				number:true,
				maxlength:5
			},
			safety_lever:{
				number:true,
				maxlength:5
			},
			stock_code:{
				maxlength:20
			},
			popular_item:{
				required:true
			},
			in_shelf_cost:{
				required:true
			},
			out_shelf_cost:{
				required:true
			}
		}
	});
	$pop_window.dialog({
		resizable : false,
		width : '640px',
		modal : true,
		title : "加入库存设置",
		buttons : {
			"确认" : function() {
				if($("#add_benchmark").val() <= 0 ){
					errorPop("基准库存不能小于0");
					return false;
				}
				if($("#new_partial_id").val() == "" || $("#new_partial_id").val() == null ){
					errorPop("请输入正确的消耗品代码");
					return false;
				}
				if($("#abandon_insert").valid()){
					if($("#delete_flg").val() == "1"){
						var data ={
								"partial_id":$("#new_partial_id").val(),
								"type":$("#add_type").val(),
								"benchmark":$("#add_benchmark").val(),
								"safety_lever":$("#add_safety_lever").val(),
								"supply_cycle":$("#add_supply_cycle").val(),
								"supply_day":$("#add_supply_day").val(),
								"popular_item":$("#add_popular_item_set input:checked").val(),
								"stock_code":$("#add_stock_code").val(),
								"consumpt_quota":$("#add_consumpt_quota").val(),
								"in_shelf_cost" : $("#add_in_shelf_cost").val(),
								"out_shelf_cost" : $("#add_out_shelf_cost").val(),
								"hazardous_flg" : $("#add_hazardous_flg input:checked").val(),
							};
						 $.ajax({
								beforeSend : ajaxRequestType,
								async : true,
								url : servicePath + '?method=doupdateConsumableManage',
								cache : false,
								data : data,
								type : "post",
								dataType : "json",
								success : ajaxSuccessCheck,
								error : ajaxError,
								complete : insert_handleComplete
						});
						
					}else{
					var data = {
							"partial_id":$("#new_partial_id").val(),
							"code" : $("#add_code").val(),
							"type" : $("#add_type ").val(),
							"benchmark":$("#add_benchmark").val(),
							"safety_lever":$("#add_safety_lever ").val(),
							"supply_cycle":$("#add_supply_cycle").val(),
							"supply_day":$("#add_supply_day").val(),
							"popular_item":$("#add_popular_item_set input:checked").val(),
							"stock_code":$("#add_stock_code").val(),
							"consumpt_quota":$("#add_consumpt_quota").val(),
							"in_shelf_cost" : $("#add_in_shelf_cost").val(),
							"out_shelf_cost" : $("#add_out_shelf_cost").val(),
							"hazardous_flg" : $("#add_hazardous_flg input:checked").val(),
						};
					$.ajax({
						beforeSend : ajaxRequestType,
						async : true,
						url : servicePath + '?method=doinsert',
						cache : false,
						data : data,
						type : "post",
						dataType : "json",
						success : ajaxSuccessCheck,
						error : ajaxError,
						complete : insert_handleComplete
					});
				}
			}
		},
			"取消" : function() {
				$pop_window.dialog("close");
			}
		}
	});
};

/*消耗品计量单位设置button事件*/
var showSet = function() {
	var row = $("#consumable_list").jqGrid("getGridParam", "selrow");// 得到选中行的ID
	var rowData = $("#consumable_list").getRowData(row);
	var data = {
		"partial_id" : rowData.partial_id
	};
	$.ajax({
		beforeSend : ajaxRequestType,
		async : true,
		url : servicePath + '?method=getMeasuring',
		cache : false,
		data : data,
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : showSet_Complete
	});
};

/*消耗品计量单位设置加载页面事件*/
var showSet_Complete = function(xhrobj,textStatus){
	try {
		var resInfo = null;
		var $pop_window = $("#pop_window_set");
		// 以Object形式读取JSON
		eval('resInfo =' + xhrobj.responseText);
		if (resInfo.errors.length > 0) {
			// 共通出错信息框
			treatBackMessages(null, resInfo.errors);
		} else {
			// 修改库存设置弹出框初期化
			$("#set_partial_id").val(resInfo.returnForm.partial_id);
			$("#label_code").text(resInfo.returnForm.code);
			$("#set_unit_name").val(resInfo.returnForm.unit_name);
			$("#set_content").val(resInfo.returnForm.content);
			// 内容量隐藏
			$("#packing").removeAttr("checked");  
			$("#set_content").show(); 
			if($("#set_content").val() == 1){
				$("#packing").attr("checked", true).trigger("change");
				$("#set_content").hide();
			}
			$("#packing").click(function(){	
				if ($(this).attr("checked")) {
					$("#set_content").hide();
				} else {
					$("#set_content").show();
				}
			});
			
			/*验证*/
			$("#measuring_set").validate({
				rules:{	
					unit_name:{
						required:true
					},
					content:{
						required:true,
						digits:true,
						min:2
					}
				}
			});
			$pop_window.dialog({
			    resizable : false,
				modal : true,
				title : "计量单位设置",
				width : '640px',
				buttons : {
					"确认" : function() {
						if( $("#measuring_set").valid()) {
							update_measurement_unit();
						}
					},
					"取消" : function() {
						$pop_window.dialog("close");
					}
				}
			});
		}
		
	} catch (e) {}
};

/*更新消耗品计量单位表*/
var update_measurement_unit = function(){
	var content = $("#set_content").val();
	var checked = $("#packing").attr("checked");
	if (checked == "checked") {
		content = 1;
	}
	var data ={
			"partial_id":$("#set_partial_id").val(),
			"unit_name":$("#set_unit_name").val(),
			"content":content
		};
	 $.ajax({
			beforeSend : ajaxRequestType,
			async : true,
			url : servicePath + '?method=doupdateMeasurementUnit',
			cache : false,
			data : data,
			type : "post",
			dataType : "json",
			success : ajaxSuccessCheck,
			error : ajaxError,
			complete : updateMeasure_handleComplete
	});
};

function updateMeasure_handleComplete(xhrobj, textStatus) {

	var resInfo = null;

	// 以Object形式读取JSON responseText获取来自服务器响应的数据
	eval('resInfo =' + xhrobj.responseText);

	if (resInfo.errors.length > 0) {
		// 共通出错信息框
		treatBackMessages(null, resInfo.errors);
	} else {
		$("#pop_window_set").dialog("close");
	}
};

/*移出消耗品库存button事件*/
var delConsumable = function () {
	var row = $("#consumable_list").jqGrid("getGridParam", "selrow");// 得到选中行的ID
	var rowData = $("#consumable_list").getRowData(row);
	var data = {
		"partial_id" : rowData.partial_id
	};
	$.ajax({
		beforeSend : ajaxRequestType,
		async : true,
		url : servicePath + '?method=dodelConsumable',
		cache : false,
		data : data,
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : delConsumable_Complete
	});
};

function delConsumable_Complete(xhrobj, textStatus) {

	var resInfo = null;

	// 以Object形式读取JSON responseText获取来自服务器响应的数据
	eval('resInfo =' + xhrobj.responseText);

	if (resInfo.errors.length > 0) {
		// 共通出错信息框
		treatBackMessages(null, resInfo.errors);
	} else {
	    findit();
	}
};

/*修改库存设置button事件*/
var showEdit = function() {
	var isFact = $('#hidden_isFact').val();
	if (isFact == "false") {
		return;
	}

	var row = $("#consumable_list").jqGrid("getGridParam", "selrow");// 得到选中行的ID
	var rowData = $("#consumable_list").getRowData(row);
	var data = {
		"partial_id" : rowData.partial_id
	};
	$.ajax({
		beforeSend : ajaxRequestType,
		async : true,
		url : servicePath + '?method=edit',
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
			$("#label_modelname").text(resInfo.returnForm.code);
			$("#edit_type").val(resInfo.returnForm.type).trigger("change");
			$("#edit_benchmark").val(resInfo.returnForm.benchmark);
			$("#edit_safety_lever").val(resInfo.returnForm.safety_lever);	
			$("#edit_supply_cycle").val(resInfo.returnForm.supply_cycle).trigger("change");
			$("#edit_supply_day").val(resInfo.returnForm.supply_day).trigger("change");
			if(resInfo.returnForm.popular_item == 0){
				$("#edit_popular_item_set").children("input:eq(0)").attr("checked", true).trigger("change");
			}else{
				$("#edit_popular_item_set").children("input:eq(1)").attr("checked", true).trigger("change");
			}
			$("#edit_stock_code").val(resInfo.returnForm.stock_code);
			$("#edit_consumpt_quota").val(resInfo.returnForm.consumpt_quota);
			$("#edit_partial_id").val(resInfo.returnForm.partial_id);
			
			$("#edit_in_shelf_cost").val(resInfo.returnForm.in_shelf_cost).trigger("change");
			$("#edit_out_shelf_cost").val(resInfo.returnForm.out_shelf_cost).trigger("change");
			
			if(resInfo.returnForm.hazardous_flg == 0){
				$("#edit_hazardous_flg").children("input:eq(0)").attr("checked", true).trigger("change");
			}else{
				$("#edit_hazardous_flg").children("input:eq(1)").attr("checked", true).trigger("change");
			}
			
			/*验证*/
			$("#abandon_modify").validate({
				rules:{	
					type:{
						required:true
					},
					benchmark:{
						digits:true,
						maxlength:5
					},
					safety_lever:{
						digits:true,
						maxlength:5
					},
					stock_code:{
						maxlength:20
					},
					popular_item:{
						required:true
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
	var data ={
			"partial_id":$("#edit_partial_id").val(),
			"type":$("#edit_type").val(),
			"benchmark":$("#edit_benchmark").val(),
			"safety_lever":$("#edit_safety_lever").val(),
			"supply_cycle":$("#edit_supply_cycle").val(),
			"supply_day":$("#edit_supply_day").val(),
			"popular_item":$("#edit_popular_item_set input:checked").val(),
			"stock_code":$("#edit_stock_code").val(),
			"consumpt_quota":$("#edit_consumpt_quota").val(),
			"in_shelf_cost" : $("#edit_in_shelf_cost").val(),
			"out_shelf_cost" : $("#edit_out_shelf_cost").val(),
			"hazardous_flg" : $("#edit_hazardous_flg input:checked").val(),
		};
	 $.ajax({
			beforeSend : ajaxRequestType,
			async : true,
			url : servicePath + '?method=doupdateConsumableManage',
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

function insert_handleComplete(xhrobj, textStatus) {

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

var showAdjust = function() {
	var $pop_window = $("#pop_window_adjust");
	// 清空输入项
	$("#pop_window_adjust input[type!='button'][type!='radio'], #pop_window_new textarea").val("");
	$("#adjust_reason").val("");
	$("#adjust_code").bind("keyup", function(e){
		var kcode =e.keyCode;
		if (kcode ==8 || kcode == 46){
			return false;
		}
		if (this.value.length >= 6){
			if( kcode > 105 || kcode < 48){
				return;
			}
			adjustSearch();
		}
	});

	$pop_window.dialog({
		resizable : false,
		width : '640px',
		modal : true,
		title : "盘点库存",		
		buttons : {
			"确认" : function() {
				if($("#adjust_partial_id").val() == "" || $("#adjust_partial_id").val() == null){
					errorPop("请选择正确的零件");
				return false;
				}
				var available_inventory_temp = parseInt($("#adjust_available_inventory").val()) -parseInt( $("#current_available_inventory").text());
				var on_passage_temp =parseInt( $("#adjust_on_passage").val()) - parseInt($("#current_on_passage").text());
				if(isNaN(available_inventory_temp) || isNaN(on_passage_temp)){
					errorPop("请填写正确的数字");
					return false;
				}
				if(available_inventory_temp != 0){
					if($("#adjust_reason").val() == ""){
						errorPop("请填写修正理由");
						return false;
					}
				}
				doAdjust(false,available_inventory_temp,on_passage_temp);
			},
			"确认并关闭" : function() {
				if($("#adjust_partial_id").val() == "" || $("#adjust_partial_id").val() == null){
					errorPop("请选择正确的零件");
				return false;
				}
				var available_inventory_temp = parseInt($("#adjust_available_inventory").val()) -parseInt( $("#current_available_inventory").text());
				var on_passage_temp =parseInt( $("#adjust_on_passage").val()) - parseInt($("#current_on_passage").text());
				if(isNaN(available_inventory_temp) || isNaN(on_passage_temp)){
					errorPop("请填写正确的数字");
					return false;
				}
				if(available_inventory_temp != 0){
					if($("#adjust_reason").val() == ""){
						errorPop("请填写修正理由");
						return false;
					}
				}
				doAdjust(true,available_inventory_temp,on_passage_temp);
			},
			"关闭" : function() {
				$pop_window.dialog("close");
			}
		}
	});
};

var adjustSearch =function (){
	var Adjustdata ={
			"code" : $("#adjust_code").val()
		};
	$.ajax({
		beforeSend : ajaxRequestType,
		async : true,
		url : servicePath + '?method=getAdjustSearch',
		cache : false,
		data : Adjustdata,
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : function(xhrobj){
			var resInfo = null;
			try {
				eval('resInfo =' + xhrobj.responseText);
				adjustsearch = resInfo.adjustsearch;
				adjustflg = resInfo.adjustflg;
				if(adjustflg == "0"){
					$("#adjust_partial_id").val("");
					$("#current_available_inventory").text("");
					$("#current_on_passage").text("");
				}else{
					$("#adjust_code").val(adjustsearch.code);
					$("#adjust_partial_id").val(adjustsearch.partial_id);
					$("#current_available_inventory").text(adjustsearch.available_inventory);
					$("#current_on_passage").text(adjustsearch.on_passage);
					$("#adjust_available_inventory").val(adjustsearch.available_inventory);
					$("#adjust_on_passage").val(adjustsearch.on_passage);
					$("#adjust_code").blur();
				}
				
			}
			catch(e){
				
			}
		}
	});	
};

var doAdjust = function(needClose,inventory_temp,on_passage_temp) {
	var	doAdjustdata = {
			"partial_id": $("#adjust_partial_id").val(),
			"available_inventory_temp": inventory_temp,
			"on_passage_temp": on_passage_temp,
			"adjust_reason": $("#adjust_reason").val() 
	};
	if(needClose){
		$.ajax({
			beforeSend : ajaxRequestType,
			async : true,
			url : servicePath + '?method=doadjust',
			cache : false,
			data : doAdjustdata,
			type : "post",
			dataType : "json",
			success : ajaxSuccessCheck,
			error : ajaxError,
			complete : function(xhrobj){
				var resInfo = null;
				try {
					eval('resInfo =' + xhrobj.responseText);
					if (resInfo.errors.length > 0) {
						// 共通出错信息框
						treatBackMessages(null, resInfo.errors);
					}
						$("#pop_window_adjust").dialog("close");
						findit();
				}
				catch (e){
				}
			}
		});
	}else{
		$.ajax({
			beforeSend : ajaxRequestType,
			async : true,
			url : servicePath + '?method=doadjust',
			cache : false,
			data : doAdjustdata,
			type : "post",
			dataType : "json",
			success : ajaxSuccessCheck,
			error : ajaxError,
			complete : function(xhrobj){
				var resInfo = null;
				try {
					eval('resInfo =' + xhrobj.responseText);
					if (resInfo.errors.length > 0) {
						// 共通出错信息框
						treatBackMessages(null, resInfo.errors);
					}
				}
				catch (e){
				}
			}
		});
	}
	$("#adjust_partial_id").val("");
	$("#adjust_code").val("");
	$("#current_available_inventory").text("");
	$("#current_on_passage").text("");
	$("#adjust_available_inventory").val("");
	$("#adjust_on_passage").val("");
};


var autoSearch = function (){
	var ConsumableFlgdata ={
			"code" : $("#add_code").val(),
			"partial_id": $("#new_partial_id").val()
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
			try {
				eval('resInfo =' + xhrobj.responseText);
				partialCode = resInfo.sPartialCode;
				reasult_flg = resInfo.reasult_flg;
				DeleteFlgdata = {
						"code" : partialCode.code,
						"partial_id": partialCode.partial_id
				};
				if(reasult_flg == "1"){
					if(partialCode.partial_id != $("#new_partial_id").val()){
						$.ajax({
							beforeSend : ajaxRequestType,
							async : true,
							url : servicePath + '?method=getConsumableFlg',
							cache : false,
							data : DeleteFlgdata,
							type : "post",
							dataType : "json",
							success : ajaxSuccessCheck,
							error : ajaxError,
							complete : function(xhrobj){
								var resInfo = null;
								try {
									eval('resInfo =' + xhrobj.responseText);
									consumableflg = resInfo.consumableflg;
									consumabledetail = resInfo.consumabledetail;
									if(consumableflg == '0'){
										$("#new_partial_id").val(partialCode.partial_id);
										$("#add_code").val(partialCode.code).blur();
									}else{
										if(consumabledetail.delete_flg == '0'){
											$("#add_code").val(partialCode.code).blur();
											$("#code_text").append("<p>该零件已加入消耗品</p>");
											$("#new_partial_id").val("");
										}else{
											$("#delete_flg").val(consumabledetail.delete_flg);
											$("#add_code").val(consumabledetail.code).blur();
											$("#new_partial_id").val(consumabledetail.partial_id);
											$("#add_type").val(consumabledetail.type).trigger("change");
											$("#add_benchmark").val(consumabledetail.benchmark);
											$("#add_safety_lever").val(consumabledetail.safety_lever);
											$("#add_supply_cycle").val(consumabledetail.supply_cycle).trigger("change");
											$("#add_supply_day").val(consumabledetail.supply_day).trigger("change");
											if(consumabledetail.popular_item == 0){
												$("#add_popular_item_set").children("input:eq(0)").attr("checked", true).trigger("change");
											}else{
												$("#add_popular_item_set").children("input:eq(1)").attr("checked", true).trigger("change");
											}
											$("#add_stock_code").val(consumabledetail.stock_code);
										}
									}
								} catch (e) {
								}
							}
						});
					}
				}
				
			} catch (e) {
			}
		}
	});
};
function show_consumable_list(consumableList) {
	for (var iCl in consumableList) {
		var rData = consumableList[iCl];
		var count = 0;
		if (rData.released_count_quantity)
			count += parseInt(rData.released_count_quantity);
		if (rData.used_count_quantity)
			count += parseInt(rData.used_count_quantity);
		rData["cost_count_quantity"] = count;
	}
	if ($("#gbox_consumable_list").length > 0) {
		$("#consumable_list").jqGrid().clearGridData();
		$("#consumable_list").jqGrid('setGridParam', {data : consumableList})
			.trigger("reloadGrid", [ {current : false} ]);
	} else {
		$("#consumable_list").jqGrid({data : consumableList,
			height : 701,
			width : 992,
			rowheight : 23,
			datatype : "local",
			colNames : [ 'partial_id', '消耗品代码', '说明', '消耗品<br>分类', '基准在库', '安全库存',
					'当前<br>有效库存', '补充<br>在途量', '期间内<br>入库量', 
					'期间内<br>消耗总量', '期间内<br>替代发放', '期间内<br>在线补充',
					'消耗率', '库位','上架<br>耗时','下架<br>耗时','有无图片','消耗率警报','库存不足','hazardous_flg'],
			colModel : [ {
				name : 'partial_id',
				index : 'partial_id',
				hidden : true
			}, {
				name : 'code',
				index : 'code',
				width : 60
			}, {
				name : 'description',
				index : 'description'
			}, {
				name : 'type',
				index : 'type',
				width : 50,
				formatter:'select',
				editoptions : {
					value : $("#res_type").val()
				}
			}, {
				name : 'benchmark',
				index : 'benchmark',
				align : 'right',
				formatter : 'integer',
				sorttype : 'int',
				width : 60,
				formatoptions: {
					defaultValue: ' - ', thousandsSeparator: ",", prefix: ""
		    }
			}, {
				name : 'safety_lever',
				index : 'safety_lever',
				align : 'right',
				formatter : 'integer',
				sorttype : 'int',
				width : 60,
				formatoptions: {
					defaultValue: ' - ', thousandsSeparator: ",", prefix: ""
		    }
			}, {
				name : 'available_inventory',
				index : 'available_inventory',
				align : 'right',
				formatter : 'integer',
				sorttype : 'int',
				width : 60,
				formatoptions: {
					defaultValue: ' - ', thousandsSeparator: ",", prefix: ""
		    }
			}, {
				name : 'on_passage',
				index : 'on_passage',
				align : 'right',
				formatter : 'integer',
				sorttype : 'int',
				width : 50,
				formatoptions: {
					defaultValue: ' - ', thousandsSeparator: ",", prefix: ""
		    }
			}, {
				name : 'supply_count_quantity',
				index : 'unseal_items',
				align : 'right',
				formatter : 'integer',
				sorttype : 'int',
				width : 60,
				formatoptions: {
					defaultValue: ' - ', thousandsSeparator: ",", prefix: ""
		    }
			}, {
				name : 'cost_count_quantity',
				index : 'cost_count_quantity',
				align : 'right',
				formatter : 'integer',
				sorttype : 'int',
				width : 50,
				formatoptions: {
					defaultValue: ' - ', thousandsSeparator: ",", prefix: ""
				}
			}, {
				name : 'released_count_quantity',
				index : 'released_count_quantity',
				align : 'right',
				formatter : 'integer',
				sorttype : 'int',
				width : 50,
				formatoptions: {
					defaultValue: ' - ', thousandsSeparator: ",", prefix: ""
		    	}
			}, {
				name : 'used_count_quantity',
				index : 'used_count_quantity',
				align : 'right',
				formatter : 'integer',
				sorttype : 'int',
				width : 50,
				formatoptions: {
					defaultValue: ' - ', thousandsSeparator: ",", prefix: ""
		    	}
			}, {
				name : 'consumed_rate',
				index : 'consumed_rate',
				width : 65,
				align : 'right',
				formatter : 'number',
				sorttype : 'number',
				formatter : function(value, options, rData) {
					if (rData.consumed_rate)
						return rData.consumed_rate + " %";
					else
						return ' - ';
				}
			}, {
				name : 'stock_code',
				index : 'stock_code',
				width : 80,
				align : 'left'
			},
			{
				name : 'in_shelf_cost',
				index : 'in_shelf_cost',
				width : 50,
				align : 'center',
				formatter:'select',
				editoptions : {
					value : $("#hide_shelf_cost").val()
				}
			},
			{
				name : 'out_shelf_cost',
				index : 'out_shelf_cost',
				width : 50,
				align : 'center',
				formatter:'select',
				editoptions : {
					value : $("#hide_shelf_cost").val()
				}
			},
			{
				name : 'image_uploaded_flg',
				index : 'image_uploaded_flg',
				hidden : true
			},{
				name : 'consumed_rate_alarm',
				index : 'consumed_rate_alarm',
				hidden : true
			},{
				name : 'leak',
				index : 'leak',
				hidden : true
			},{
				name : 'hazardous_flg',
				index : 'hazardous_flg',
				hidden : true
			}],
			rowNum : 30,
			rownumbers:true,
			toppager : false,
			pager : "#consumable_list_pager",
			viewrecords : true,
			gridview : true,
			pagerpos : 'right',
			pgbuttons : true,
			pginput : false,
			recordpos : 'left',
			hidegrid : false,
			deselectAfterSort : false,
			ondblClickRow : showEdit,
			onSelectRow : enableButton,
			viewsortcols : [ true, 'vertical', true ],
			gridComplete : function() {
				var IDS = $("#consumable_list").getDataIDs();
				// 当前显示多少条
				var length = IDS.length;
				var $exd_list = $("#consumable_list");
				for (var i = 0; i < length; i++) {
					// 从上到下获取一条信息
					var rowData = $exd_list.jqGrid('getRowData', IDS[i]);
					var leak = parseInt(rowData["leak"]);
					var consumed_rate_alarm = parseInt(rowData["consumed_rate_alarm"]);
					var hazardous_flg = parseInt(rowData["hazardous_flg"]);
					if (leak == 1 ) {
						$exd_list.find("tr#" + IDS[i] + " td[aria\\-describedby='consumable_list_available_inventory']").addClass("ui-state-highlight");
					}else if(leak == 2){
						$exd_list.find("tr#" + IDS[i] + " td[aria\\-describedby='consumable_list_available_inventory']").addClass("ui-state-error");
					}
					if (consumed_rate_alarm == 1 ) {
						$exd_list.find("tr#" + IDS[i] + " td[aria\\-describedby='consumable_list_consumed_rate']").addClass("ui-state-highlight");
					}

					if (hazardous_flg == 1 ) {
						$exd_list.find("tr#" + IDS[i] + " td[aria\\-describedby='consumable_list_code']").addClass("ui-state-error");
					}
				}
			}
		});
		var tname=['myac'];
		//当不是系统管理员和零件管理员时隐藏所有可以修改和更新功能
		if($("#hidden_isOperator").val()=='other'){
			$("#consumable_list").jqGrid('hideCol',tname);
			$("#gbox_list").next().hide();
			$("#edit_price,#edit_name,#edit_code").attr("readonly",true);
			$("#list").jqGrid('setGridWidth', '992');
		}else if($("#hidden_isOperator").val()=='operator'){
			
			$(".ui-jqgrid-hbox").before('<div class="ui-widget-content" style="padding:4px;">' +
					'<input type="button" class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="addbutton" value="新建'+ modelname +'" role="button" aria-disabled="false">' +
				'</div>');

			$("#addbutton").button();
			// 追加处理
			$("#addbutton").click(showAdd);

			$("#list").jqGrid('showCol',tname);
			$("#addbutton").show();
			$("#gbox_list").next().show();
		}
	}
}

var postClipboard = function(e){
	$.ajax({
		beforeSend : ajaxRequestType,
		async : true,
		url : servicePath + '?method=postClipboard',
		cache : false,
		data : null,
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : function(xhrObj){
			var resInfo = $.parseJSON(xhrObj.responseText);
			if (resInfo.errors.length == 0) {
				var clipboardData = undefined;

				if (window.clipboardData && window.clipboardData.getData) { // IE
					clipboardData = window.clipboardData;
					clipboardData.setData("Text", resInfo.clipboardObject);
				} else if (typeof(copy) == "function") {
					copy(resInfo.clipboardObject); // 万一哪一天
				} else {
					var $re = $("<pre contenteditable='true' style='background-color:white;position:fixed;width:600px;height:400px;top:100px;left:50%;margin-left:-300px;overflow:hidden;'>"+resInfo.clipboardObject+"</pre>");
					$("body").append($re);
					$re.focus();
					$re.keyup(function(){
						$re.remove();
					});
					document.execCommand('SelectAll');
					document.execCommand('Copy');
				}
			}
		}
	});

}

var download = function() {
	// Ajax提交
	$.ajax({
		beforeSend: ajaxRequestType, 
		async: true, 
		url: servicePath + '?method=searchDownload', 
		cache: false, 
		data: keepSearchData, 
		type: "post", 
		dataType: "json", 
		success: ajaxSuccessCheck, 
		error: ajaxError, 
		complete: function(xhrObj){
		 	var resInfo = $.parseJSON(xhrObj.responseText);
			if (resInfo.errors && resInfo.errors.length > 0) {
				treatBackMessages(null, resInfo.errors);
				return;
			}
    		if ($("iframe").length > 0) {
				$("iframe").attr("src", "download.do" + "?method=output&filePath=" + resInfo.filePath+"&fileName="+resInfo.fileName);
			} else {
				var iframe = document.createElement("iframe");
				iframe.src = "download.do" + "?method=output&filePath=" + resInfo.filePath+"&fileName="+resInfo.fileName;
				iframe.style.display = "none";
				document.body.appendChild(iframe);
			}
		}
	});
};

/*剪裁长度设置button事件*/
var getHeatshrinkableLength = function(){

	var row = $("#consumable_list").jqGrid("getGridParam", "selrow");// 得到选中行的ID
	var rowData = $("#consumable_list").getRowData(row);
	var data = {
		"partial_id" : rowData.partial_id
	};
	$.ajax({
		beforeSend : ajaxRequestType,
		async : true,
		url : servicePath + '?method=getHeatshrinkableLength',
		cache : false,
		data : data,
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : function(xhjObj){
			getHeatshrinkableLength_Complete(xhjObj, rowData.code, rowData.description);
		}
	});
};
var getHeatshrinkableLength_Complete = function(xhjObj, code, description) {
	var $pop_window = $("#pop_heatshrink_length");
	// 以Object形式读取JSON
	var resInfo = $.parseJSON(xhjObj.responseText);
	if (resInfo.errors.length > 0) {
		// 共通出错信息框
		treatBackMessages(null, resInfo.errors);
	} else {
		// 修改库存设置弹出框初期化
		$("#hshl_partial_id").val(resInfo.returnForm.partial_id);
		$("#label_hshl_code").text(code);
		$("#label_hshl_description").text(description);
		$("#hshl_cut_lengths").val(resInfo.returnForm.content || "");
		
		$pop_window.dialog({
		    resizable : false,
			modal : true,
			title : "计量单位设置",
			width : '640px',
			buttons : {
				"确认" : function() {
					setHeatshrinkableLength(resInfo.returnForm.partial_id);
				},
				"取消" : function() {
					$pop_window.dialog("close");
				}
			}
		});
	}

}
var setHeatshrinkableLength = function(partail_id){
	var postData = {"partail_id" : partail_id, "content": $("#hshl_cut_lengths").val()};
	$.ajax({
		beforeSend : ajaxRequestType,
		async : true,
		url : servicePath + '?method=doSetHeatshrinkableLength',
		cache : false,
		data : postData,
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : function(xhjObj){
			var resInfo = $.parseJSON(xhjObj.responseText);
			if (resInfo.errors.length > 0) {
				// 共通出错信息框
				treatBackMessages(null, resInfo.errors);
			} else {
				$("#pop_heatshrink_length").dialog("close");
			}
		}
	});
}