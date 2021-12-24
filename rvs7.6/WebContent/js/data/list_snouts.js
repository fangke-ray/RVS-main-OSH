var modelname = "D/E 组件信息";

/** 一览数据对象 */
var listdata = {};
/** 服务器处理路径 */
var servicePath = "snouts.do";
var lOptions = "";
var rOptions = "";

var findit = function() {
	var data = {
		model_id : $("#cond_model_id").data("post"),
		serial_no : $("#cond_serial_no").data("post"),
		status : $("#cond_used").data("post"),
		finish_time_from : $("#cond_finish_time_from").data("post"),
		finish_time_to : $("#cond_finish_time_to").data("post"),
		operator_id : $("#cond_operator_id").data("post"),
		origin_omr_notifi_no : $("#cond_origin_omr_notifi_no").data("post"),
		origin_serial_no : $("#cond_origin_serial_no").data("post")
	};

	// Ajax提交
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

var getSettings = function(){
	// Ajax提交
	$.ajax({
		beforeSend : ajaxRequestType,
		async : true,
		url : servicePath + '?method=getSettings',
		cache : false,
		data : null,
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : getSettings_handleComplete
	});
}

$(function() {

	$("input.ui-button").button();

	$("#searchbutton").addClass("ui-button-primary");

	$("#searcharea span.ui-icon,#wiparea span.ui-icon").bind("click", function() {
		$(this).toggleClass('ui-icon-circle-triangle-n').toggleClass('ui-icon-circle-triangle-s');
		if ($(this).hasClass('ui-icon-circle-triangle-n')) {
			$(this).parent().parent().next().show("blind");
		} else {
			$(this).parent().parent().next().hide("blind");
		}
	});

	$("#cond_model_id, #cond_used").select2Buttons();

	$("#cond_finish_time_from, #cond_finish_time_to").datepicker({
		showButtonPanel:true,
		currentText: "今天"
	});

	setReferChooser($("#cond_operator_id"), $("#referchooser_operator"));

	/*Tab切换*/
	$("#infoes input:radio").click(function(){
		var pageName = this.value;

		$("#body-mdl > div[for]").hide();
		$("#body-mdl > div[for=" + pageName + "]").show();
	});
	$("#infoes [checked]").trigger("click");

	// 检索处理
	$("#searchbutton").click(function() {
		// 保存检索条件
		$("#cond_model_id").data("post", $("#cond_model_id").val());
		$("#cond_serial_no").data("post", $("#cond_serial_no").val());
		$("#cond_used").data("post", $("#cond_used").val());
		$("#cond_finish_time_from").data("post", $("#cond_finish_time_from").val());
		$("#cond_finish_time_to").data("post", $("#cond_finish_time_to").val());
		$("#cond_operator_id").data("post", $("#cond_operator_id").val());
		$("#cond_origin_omr_notifi_no").data("post", $("#cond_origin_omr_notifi_no").val());
		$("#cond_origin_serial_no").data("post", $("#cond_origin_serial_no").val());
		// 查询
		findit();
	});

	$("#st_add_button").click(st_func.showAdd);
	$("#st_edit_button").click(st_func.showEdit);
	$("#st_delete_button").click(st_func.showDelete);
	$("#st_adjust_button").click(st_func.showAdjust);
	$("#st_snout_list_button").click(st_func.showSnoutList);
	$("#move_button").disable().click(moveStorage);

    setReferChooser($("#add_model_id"));

	// 清空检索条件
	$("#resetbutton").click(function() {
		$("#cond_model_id").val("").trigger("change").data("post", "");
		$("#cond_serial_no").val("").data("post", "");
		$("#cond_used").val("1").trigger("change").data("post", "");
		$("#cond_finish_time_from").val("").data("post", "");
		$("#cond_finish_time_to").val("").data("post", "");
		$("#cond_operator_id").prev("input").val("");
		$("#cond_operator_id").val("").trigger("change").data("post", "");

		$("#cond_origin_omr_notifi_no").val("").data("post", "");
		$("#cond_origin_serial_no").val("").data("post", "");

	});

	$("#cond_used").data("post", $("#cond_used").val());
	getSettings();

	findit();

});

var showDetail = function(serial_no, sorc_no){

	popSnoutDetail(serial_no, sorc_no, true);

};

var getSettings_handleComplete = function(xhrobj, textStatus){
	var resInfo = $.parseJSON(xhrobj.responseText);
	
	var listdata = resInfo.list;
	for (var idx in listdata) {
		// 合并型号选项
	}
	if ($("#gbox_snout_component_setting").length > 0) {
		$("#snout_component_setting").jqGrid().clearGridData();
		$("#snout_component_setting").jqGrid('setGridParam', {data : listdata}).trigger("reloadGrid", [{current : false}]);
	} else {
		$("#snout_component_setting").jqGrid({
			toppager : true,
			data : listdata,
			height : 461,
			width : 992,
			rowheight : 23,
			datatype : "local",
			colNames : [ 'model_id', '机种', '型号', '组件代码', '基准库存', '安全库存',
					'组装中数', '组装完成数', 
					'C 本体代码', 'C 本体<br>库存数', '目镜·软管筒代码', '目镜·软管筒<br>库存套数'],
			colModel : [ {
				name : 'model_id',
				index : 'model_id',
				hidden : true
			}, {
				name : 'category_name',
				index : 'category_name',
				width : 40
			}, {
				name : 'model_name',
				index : 'model_name',
				width : 60
			},{
				name : 'component_code',
				index : 'component_code',
				width : 35,
				align : 'center'
			}, {
				name : 'benchmark',
				index : 'benchmark',
				width : 40,
				align : 'right',
				formatter : function(value,col,rowData) {
					if (rowData['safety_lever']) {
						return rowData['safety_lever'];
					} else {
						return "-";
					}
				}
			}, {
				name : 'safety_lever',
				index : 'safety_lever',
				width : 40,
				align : 'right',
				formatter : function(value,col,rowData) {
					if (rowData['safety_lever']) {
						return Math.ceil(parseInt(rowData['safety_lever']) / 2);
					} else {
						return "-";
					}
				}
			},{
				name : 'cnt_partial_step2',
				index : 'cnt_partial_step2',
				width : 40,
				align : 'right'
			}, {
				name : 'cnt_partial_step3',
				index : 'cnt_partial_step3',
				width : 40,
				align : 'right'
			}, {
				name : 'refurbished_code',
				index : 'refurbished_code',
				width : 35,
				hidden: true
			}, {
				name : 'cnt_partial_step0', // for refurbished_code
				index : 'cnt_partial_step0',
				width : 40,
				align : 'right'
			}, {
				name : 'partial_code',
				index : 'partial_code',
				width : 120
			},{
				name : 'cnt_partial_step1',
				index : 'cnt_partial_step1',
				width : 40,
				align : 'right'
			}],
			rowNum : 20,
			toppager : false,
			pager : "#snout_component_setting_pager",
			viewrecords : true,
			ondblClickRow : function(rid, iRow, iCol, e) {
				if ($("#st_edit_button:visible").length) {
					st_func.showEdit();
				}
			},
			onSelectRow: function(rid){
				$("#executes > input:button").enable();

				var rowData = $("#snout_component_setting").getRowData(rid);
				if (rowData.cnt_partial_step1 == "--") {
					$("#st_adjust_button, #st_snout_list_button").disable();
				}

			},
			// multiselect : true, 
			gridview : true, // Speed up
			pagerpos : 'right',
			pgbuttons : true,
			pginput : false,
			recordpos : 'left',
			viewsortcols : [true, 'vertical', true],
			gridComplete: function(){
			 	// 统计是否小于安全库存
				var $trs = $("#snout_component_setting").find("tr.ui-widget-content");
				$trs.each(function(idx,ele){

					var $tr = $(ele);
					var $tdSafetyLever = $tr.find("td[aria\\-describedby=snout_component_setting_safety_lever]");
					var iSubpart = parseInt($tr.find("td[aria\\-describedby=snout_component_setting_cnt_partial_step1]").text().trim() || 0);
					if ($tdSafetyLever.text().trim() && iSubpart >= 0) {
						var iSafetyLever = parseInt($tdSafetyLever.text().trim());
						if (iSafetyLever > 0) {
							var iWorking = parseInt($tr.find("td[aria\\-describedby=snout_component_setting_cnt_partial_step2]").text().trim() || 0);
							var iWorked = parseInt($tr.find("td[aria\\-describedby=snout_component_setting_cnt_partial_step3]").text().trim() || 0);
							var iSnout = parseInt($tr.find("td[aria\\-describedby=snout_component_setting_cnt_partial_step0]").text().trim() || 0);
							var iPart = (iSnout > iSubpart ? iSubpart : iSnout); 
							if (iPart + iWorking + iWorked < iSafetyLever) {
								$tdSafetyLever.addClass("ui-state-error");
								if (iSnout > iSubpart) {
									$tr.find("td[aria\\-describedby=snout_component_setting_cnt_partial_step1]").css({"color" : "red","textDecoration": "underline blink solid red"});
								} else if (iSnout < iSubpart) {
									$tr.find("td[aria\\-describedby=snout_component_setting_cnt_partial_step0]").css({"color" : "red","textDecoration": "underline blink solid red"});
								}
							}
						}
					}
					if (iSubpart < 0) {
						$tr.children("td:gt(5)").text("--").css("textAlign", "center");
					}
				});
				$("#executes > input:button").not("#st_add_button").disable();
			}
		});
	}
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
			treatBackMessages("#searcharea", resInfo.errors);
		} else {
			listdata = resInfo.list;
			
			if ($("#gbox_list").length > 0) {
				$("#list").jqGrid().clearGridData();
				$("#list").jqGrid('setGridParam', {data : listdata}).trigger("reloadGrid", [{current : false}]);
			} else {
				$("#list").jqGrid({
					toppager : true,
					data : listdata,
					height : 461,
					width : 992,
					rowheight : 23,
					datatype : "local",
					colNames : ['origin_material_id', 'model_id', '来源修理单号', '翻新追溯', 'D/E 组件型号', 'D/E 组件序列号', '完成时间', '制造人', '检测时间', '检测人',
							'使用内镜修理编号','slot'],
					colModel : [
						{name:'origin_material_id',index:'origin_material_id', hidden:true},
						{name:'model_id',index:'model_id', hidden:true},
						{name:'origin_omr_notifi_no',index:'origin_omr_notifi_no', width:60},
						{name:'refurbished',index:'refurbished', width:35, align:'center', formatter : function(value,col,rowData) {
							if (value) {
								if (value == "-1") {
									return "-";
								}
								if (value == "0") {
									return "第 1 次";
								}
								return "<a href='javascript:showRefurb(" + rowData["origin_material_id"] + ")'>第 " + (parseInt(value) + 1) + " 次</a>";
							}
							return '-';
						}},
						{name:'model_name',index:'model_id', width:100},
						{name:'serial_no',index:'serial_no', width:75},
						{name:'finish_time',index:'finish_time', width:50, align:'center', formatter:'date', formatoptions:{srcformat:'Y/m/d H:i:s',newformat:'m-d H:i'}},
						{name:'operator_name',index:'operator_name', width:60},
						{name:'confirm_time',index:'confirm_time', width:50, align:'center', formatter:'date', formatoptions:{srcformat:'Y/m/d H:i:s',newformat:'m-d'}},
						{name:'confirmer_name',index:'confirmer_name', width:60},
						{name:'sorc_no',index:'sorc_no', width:100},
						{name:'slot',index:'slot', hidden:true}
					],
					rowNum : 50,
					toppager : false,
					pager : "#listpager",
					viewrecords : true,
					caption : "全部 " + modelname + "一览",
					ondblClickRow : function(rid, iRow, iCol, e) {
						var data = $("#list").getRowData(rid);
						var serial_no = data["serial_no"];
						var sorc_no = data["sorc_no"];
						showDetail(serial_no, sorc_no);
					},
					onSelectRow: function(rid){
						var rowData = $("#list").getRowData(rid);
						if (rowData.finish_time && rowData.finish_time.trim() && !rowData.sorc_no) {
							$("#move_button").enable();
						} else {
							$("#move_button").disable();
						}
					},
					// multiselect : true, 
					gridview : true, // Speed up
					pagerpos : 'right',
					pgbuttons : true,
					pginput : false,
					recordpos : 'left',
					viewsortcols : [true, 'vertical', true],
					gridComplete: function(){
						$("#move_button").disable();
					}
				});
				// $("#list").gridResize({minWidth:1248,maxWidth:1248,minHeight:200,
				// maxHeight:900});
			}
		}
	} catch (e) {
		console.log("name: " + e.name + " message: " + e.message + " lineNumber: "
				+ e.lineNumber + " fileName: " + e.fileName);
	};
};

/** 工程检查票赋值 */
var valuePcs = function(data) {
	var pcs_values = {};
	var pcs_comments = {};
	$("#pcs_detail_pcs_contents input").each(function(){
		if (this.value == "") return;
		if (this.type == "text") {
			pcs_values[this.name] = this.value;
		} else {
//			if (this.name && this.name != "") pcs_values[this.name] = "";
		}
	});
	$("#pcs_detail_pcs_contents input:checked").each(function(){
		if (this.value == "") return;
		if (this.type == "radio"){
			pcs_values[this.name] = this.value;
		} else if (this.type == "checkbox"){
			if (pcs_values[this.name] == null || pcs_values[this.name] == "") {
				pcs_values[this.name] = this.value;
			} else {
				pcs_values[this.name] = pcs_values[this.name] + "," + this.value;
			}
		}
	});
	$("#pcs_detail_pcs_contents textarea").each(function(){
		if (this.value != null && this.value != "") {
			pcs_comments[this.name] = this.value;
		} else if(pcs_comments[this.name] == null) {
			pcs_comments[this.name] = this.value;
		}
	});

	data.pcs_inputs = Json_to_String(pcs_values);
	data.pcs_comments = Json_to_String(pcs_comments);

	for (var v in pcs_values) {
		if (pcs_values[v] == null || pcs_values[v] == "") return true;
	}
	return false;
}

var popSnoutDetail = function(serial_no, sorc_no) {
	var this_dialog = $("#snout_edit");
	if (this_dialog.length === 0) {
		$("body.outer").append("<div id='snout_edit'/>");
		this_dialog = $("#snout_edit");
	}

	this_dialog.html("");
	this_dialog.hide();
	// 导入详细画面
	this_dialog.load("snouts.do?method=detail&position_id=00000000024&serial_no=" + serial_no , function(responseText, textStatus, XMLHttpRequest) {
		$("#snout_detail_model_id").select2Buttons();

		$("#pcs_detail_pcs_contents input[name^='L'],#pcs_detail_pcs_contents textarea[name^='L']").parent().css("background-color", "#93C3CD");
		$("#pcs_detail_pcs_contents input[name^='E'],#pcs_detail_pcs_contents textarea[name^='E']").parent().css("background-color", "#F8FB84");
		$("#pcs_detail_pcs_contents input[name^='LN'], #pcs_detail_pcs_contents input[name^='EN']").button();
		$("#pcs_detail_pcs_contents input:text").autosizeInput();

		this_dialog.dialog({
			position : [400, 20],
			title : "D/E 组件详细画面",
			width : 'auto',
			show : "",
			height :  'auto',
			resizable : false,
			modal : true,
			buttons : {
				"确定" : function() {
					var detail_data = {};
					detail_data["serial_no"] = $("#snout_detail_serial_no_org").val();
					valuePcs(detail_data);
					var writed = false;
					if (!(detail_data.pcs_inputs == null || detail_data.pcs_inputs.length == 0 || detail_data.pcs_inputs == "{}")
						|| !(!detail_data.pcs_comments || detail_data.pcs_comments.length == 0 || detail_data.pcs_comments == "{}")){
						for (var iinputs in detail_data.pcs_inputs) {
							var input = detail_data.pcs_inputs[iinputs];
							if (input.trim() != "") {
								writed = true;
								break;
							}
						}
						for (var iinputs in detail_data.pcs_comments) {
							var input = detail_data.pcs_comments[iinputs];
							if (input.trim() != "") {
								writed = true;
								break;
							}
						}
						if (!writed) {
							// 插入线长信息
							detail_data.pcs_inputs = null;
							detail_data.pcs_comments = null;
						}
					} else {
						detail_data.pcs_inputs = null;
						detail_data.pcs_comments = null;
					}
					// 更新C 本体信息
					var model_id = $("#snout_detail_model_id").val();
					var serial_no = $("#snout_detail_serial_no").val();
					if (model_id != $("#snout_detail_model_id_org").val()
						|| serial_no != $("#snout_detail_serial_no_org").val()) {
						detail_data.new_model_id = model_id;
						detail_data.new_serial_no = serial_no;
						writed = true;
					}
					
					if (writed) {
						detail_data.sorc_no = sorc_no;
						// Ajax提交
						$.ajax({
							beforeSend : ajaxRequestType,
							async : true,
							url : servicePath + '?method=doUpdate',
							cache : false,
							data : detail_data,
							type : "post",
							dataType : "json",
							success : ajaxSuccessCheck,
							error : ajaxError,
							complete : function(xhrObj) {
								var resInfo = null;
								try {
									eval("resInfo=" + xhrObj.responseText);
								} catch (e) {
								}
							}
						});
	
						findit();
					}
					this_dialog.dialog("close");
				},
				"废弃" : function() {
					warningConfirm("该操作不可恢复，是否真的要废弃？", function () {
						$("#process_resign .rework").removeClass("rework");
						// Ajax提交
						$.ajax({
							beforeSend : ajaxRequestType,
							async : true,
							url : servicePath + '?method=doDelete',
							cache : false,
							data : {position_id: "00000000024", serial_no: serial_no, model_id : $("#snout_detail_model_id_org").val()},
							type : "post",
							dataType : "json",
							success : ajaxSuccessCheck,
							error : ajaxError,
							complete : function(xhrObj) {
								findit();
								this_dialog.dialog("close");
							}
						});
					})
				}, 
				"关闭" : function(){ this_dialog.dialog("close"); }
			}
		});
	});

	this_dialog.show();
}

// 显示追溯记录
var showRefurb = function(material_id){
	var cropCustomer = function(customer){
		if (!customer) return "-";
		if (customer.length > 12) {
			customer = customer.substring(0, 11) + "..."
		}
		return customer;
	}
	var postData = {material_id: material_id};
	// Ajax提交
	$.ajax({
		beforeSend : ajaxRequestType,
		async : true,
		url : servicePath + '?method=getSnoutHeadHistory',
		cache : false,
		data : postData,
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : function(xhrobj) {
			var resInfo = $.parseJSON(xhrobj.responseText);
			if (resInfo.list) {
				$("#history_list > tbody").html("");
				var tbodyHtml = "";
				var nextO = null, outline_time = null, history = null;

				for (var il in resInfo.list) {
					var hist = resInfo.list[il];
					var find_history = hist.package_no.split("|||");
					var customer_name = hist.customer_name.split("|||");
					var origin_omr_notifi_no = hist.esas_no || "-";
					if (nextO) {
						origin_omr_notifi_no = nextO + "<br>" + origin_omr_notifi_no;
					}
					tbodyHtml += "<tr><td>" + (il == 0 ? "源头" : (il) + "次") + "</td>";
					tbodyHtml += "<td>" + find_history[0] + "</td>";
					tbodyHtml += "<td>" + origin_omr_notifi_no + "</td>";
					tbodyHtml += "<td>" + cropCustomer(customer_name[0]) + "</td>";
					// tbodyHtml += "<td>" + hist.serial_no + "</td>";
					tbodyHtml += "<td>" + (outline_time || "-") + "</td></tr>";

					nextO = hist.sorc_no;
					outline_time = (hist.outline_time || "(未完成)");
					history = find_history[1];
				}

				if (nextO) {
					tbodyHtml += "<tr><td>" + ((resInfo.list.length + 1) + "次") + "</td>";
					tbodyHtml += "<td>" + history + "</td>";
					tbodyHtml += "<td>" + nextO + "</td>";
					tbodyHtml += "<td>" + cropCustomer(history) + "</td>";
					// tbodyHtml += "<td>" + hist.serial_no + "</td>";
					tbodyHtml += "<td>" + (outline_time || "-") + "</td></tr>";
				}

				$("#history_list > tbody").html(tbodyHtml);
			}
		}
	});
	$("#st_pop_history").dialog({
			resizable : false,
		width : 'auto',
		modal : true,
		title : "C 本体追溯",
		buttons : {
			"关闭" : function() {
				$("#st_pop_history").dialog("close");
			}
		}
	})
}

var moveStorage = function(){
	var row =	$("#list").jqGrid("getGridParam", "selrow");// 得到选中行的ID
	var rowData = $("#list").getRowData(row);

	var postData = {"origin_material_id" : rowData.origin_material_id,
					"model_id": rowData.model_id};

	$.ajax({
		beforeSend : ajaxRequestType,
		async : true,
		url : servicePath + '?method=getStorage',
		cache : false,
		data : postData,
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : function(xhrObj){
			showMoveStorage(xhrObj, rowData);
		}
	});
}

var showMoveStorage = function(xhrobj, rowData){
	var resInfo = $.parseJSON(xhrobj.responseText);

	var $popDialog = $("#shelf_pop"); 
	$popDialog.hide();
	$popDialog.html(resInfo.snoutStorageHtml);
		
	$popDialog.dialog({
		title : "D/E 组件移位",
		width : 512,
		show: "blind",
		height : 'auto' ,
		resizable : false,
		modal : true,
		buttons : {"关闭" : function(){$popDialog.dialog("close")}}
	});

	$popDialog.find("td").addClass("wip-empty");
	for (var iheap in resInfo.slots) {
		$popDialog.find("td[slot="+resInfo.slots[iheap]+"]").removeClass("wip-empty").addClass("ui-storage-highlight wip-heaped");
	}

	if (rowData.slot) {
		$popDialog.find("td[slot="+rowData.slot+"]").text("原").css("backgroundColor", "lightgreen");
	}

	$popDialog.find(".ui-widget-content .wip-table").not(".close").click(function(e){
		if ("TD" == e.target.tagName) {
			if (!$(e.target).hasClass("wip-heaped")) {
				var selslot = $(e.target).attr("slot");
				if (selslot) {
					doMoveStorage(rowData.serial_no, selslot, rowData.model_id);
				}
			}
		}
	});
}

var doMoveStorage = function(manage_serial_no, slot, model_id) {
	var postData = {"serial_no" : manage_serial_no,
					"slot": slot,
					"model_id": model_id};

	$.ajax({
		beforeSend : ajaxRequestType,
		async : true,
		url : servicePath + '?method=doMoveStorage',
		cache : false,
		data : postData,
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : function(xhrObj){
			$("#shelf_pop").dialog("close");
		}
	});
}