var modelname = "先端组件信息";

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
		operator_id : $("#cond_operator_id").data("post")
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

	// 检索处理
	$("#searchbutton").click(function() {
		// 保存检索条件
		$("#cond_model_id").data("post", $("#cond_model_id").val());
		$("#cond_serial_no").data("post", $("#cond_serial_no").val());
		$("#cond_used").data("post", $("#cond_used").val());
		$("#cond_finish_time_from").data("post", $("#cond_finish_time_from").val());
		$("#cond_finish_time_to").data("post", $("#cond_finish_time_to").val());
		$("#cond_operator_id").data("post", $("#cond_operator_id").val());
		// 查询
		findit();
	});

	// 清空检索条件
	$("#resetbutton").click(function() {
		$("#cond_model_id").val("").trigger("change").data("post", "");
		$("#cond_serial_no").val("").data("post", "");
		$("#cond_used").val("1").trigger("change").data("post", "");
		$("#cond_finish_time_from").val("").data("post", "");
		$("#cond_finish_time_to").val("").data("post", "");
		$("#cond_operator_id").prev("input").val("");
		$("#cond_operator_id").val("").trigger("change").data("post", "");
	});

	$("#cond_used").data("post", $("#cond_used").val());
	findit();

});

var showDetail = function(serial_no, sorc_no){

	popSnoutDetail(serial_no, sorc_no, true);

};

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
					colNames : ['来源修理编号', '先端组件型号', '先端组件序列号', '完成时间', '制造人', '检测时间', '检测人',
							'使用内镜修理编号'],
					colModel : [
						{name:'origin_omr_notifi_no',index:'origin_omr_notifi_no', width:75},
						{name:'model_name',index:'model_id', width:110},
						{name:'serial_no',index:'serial_no', width:75},
						{name:'finish_time',index:'finish_time', width:50, align:'center', formatter:'date', formatoptions:{srcformat:'Y/m/d H:i:s',newformat:'m-d H:i'}},
						{name:'operator_name',index:'operator_name', width:60},
						{name:'confirm_time',index:'confirm_time', width:50, align:'center', formatter:'date', formatoptions:{srcformat:'Y/m/d H:i:s',newformat:'m-d'}},
						{name:'confirmer_name',index:'confirmer_name', width:60},
						{name:'sorc_no',index:'sorc_no', width:105}
					],
					rowNum : 50,
					toppager : false,
					pager : "#listpager",
					viewrecords : true,
					caption : modelname + "一览",
					ondblClickRow : function(rid, iRow, iCol, e) {
						var data = $("#list").getRowData(rid);
						var serial_no = data["serial_no"];
						var sorc_no = data["sorc_no"];
						showDetail(serial_no, sorc_no);
					},
					// multiselect : true, 
					gridview : true, // Speed up
					pagerpos : 'right',
					pgbuttons : true,
					pginput : false,
					recordpos : 'left',
					viewsortcols : [true, 'vertical', true]
				});
				// $("#list").gridResize({minWidth:1248,maxWidth:1248,minHeight:200,
				// maxHeight:900});
			}
		}
	} catch (e) {
		alert("name: " + e.name + " message: " + e.message + " lineNumber: "
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
			title : "先端组件详细画面",
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
					// 更新先端头信息
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
