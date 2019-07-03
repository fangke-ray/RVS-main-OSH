var servicePath = "device_jig_repair_record.do";
var gridWidth = 1248;
var readNotice = false;

$(function(){
	$("input.ui-button").button();

	/*为每一个匹配的元素的特定事件绑定一个事件处理函数*/
	$("span.ui-icon,#searchform span.ui-icon").bind("click", function() {
		$(this).toggleClass('ui-icon-circle-triangle-n').toggleClass('ui-icon-circle-triangle-s');
		if ($(this).hasClass('ui-icon-circle-triangle-n')) {
			$(this).parent().parent().next().show("blind");
		} else {
			$(this).parent().parent().next().hide("blind");
		}
	});

	$("#searchform select,#submitform select").select2Buttons();


	$("#search_submit_time_start, #search_submit_time_end, #search_repair_complete_time_start, #search_repair_complete_time_end").datepicker({
		showButtonPanel:true,
		currentText: "今天"
	});

    setReferChooser($("#submit_manage_id_dev"),$("#dev_managecode_referchooser"), null, function(tr) {
    	$("#submit_object_name").val($(tr).find("td:eq(2)").text() || "");
    });

    setReferChooser($("#submit_manage_id_jig"),$("#jig_managecode_referchooser"), null, function(tr) {
    	$("#submit_object_name").val($(tr).find("td:eq(2)").text() || "");
    });

	$("#list").on("click", "img", showPhoto);

	$("#searchbutton").click(function(){
		$("#search_submit_time_start").data("post", $("#search_submit_time_start").val());
		$("#search_submit_time_end").data("post", $("#search_submit_time_end").val());
		$("#search_manage_code").data("post", $("#search_manage_code").val());
		$("#search_repair_complete_time_start").data("post", $("#search_repair_complete_time_start").val());
		$("#search_repair_complete_time_end").data("post", $("#search_repair_complete_time_end").val());
		$("#search_line_id").data("post", $("#search_line_id").val());
		$("#search_object_type").data("post", $("#search_object_type").val());
		$("#search_halt_minute").data("post", $("#search_halt_minute").val());
		$("#search_device_type_name").data("post", $("#search_device_type_name").val());
		$("#search_comment").data("post", $("#search_comment").val());

		findit();
	});

	$("#resetbutton").click(reset);

	$("#submitbutton").click(showSubmit);
	$("#repairbutton").click(showDetail);
	$("#confirmbutton").click(confirm);

	$("#submit_object_type").change(function(){
		if (this.value == 1) {
			$("#submit_manage_id_dev").val("");
			$("#submit_manage_code_dev").val("").show();
			$("#submit_manage_code_jig").hide();
			$("#submit_manage_code_free").hide();
			if (!readNotice) {
				warningConfirm("如果设备或工具在点检中，建议通过报告不合格的方式来提请维修。");
				readNotice = true;
			}
		} else if (this.value == 2) {
			$("#submit_manage_code_dev").hide();
			$("#submit_manage_id_jig").val("");
			$("#submit_manage_code_jig").val("").show();
			$("#submit_manage_code_free").hide();
			if (!readNotice) {
				warningConfirm("如果专用工具是在线清点对象，建议通过报告不合格的方式来提请维修。");
				readNotice = true;
			}
		} else {
			$("#submit_manage_code_dev").hide();
			$("#submit_manage_code_jig").hide();
			$("#submit_manage_code_free").show();
		}
	});

	showlist([]);

	$("#search_submit_time_start").data("post", $("#search_submit_time_start").val());
	findit();
});

/**
 * 清除
 *
 */
function reset(){
	$("#searchform input[type='text']").data("post", "").val("");
	$("#searchform input[type='hidden']").data("post", "").val("");
	$("#searchform select").data("post", "").val("").trigger("change");

	$("#device_jig_repair_photo").dialog("close");
};

function findit(){
	$("#device_jig_repair_photo").dialog("close");

	var data ={
		"submit_time_start":$("#search_submit_time_start").data("post"),
		"submit_time_end":$("#search_submit_time_end").data("post"),
		"manage_code":$("#search_manage_code").data("post"),
		"repair_complete_time_start":$("#search_repair_complete_time_start").data("post"),
		"repair_complete_time_end":$("#search_repair_complete_time_end").data("post"),
		"line_id":$("#search_line_id").data("post"),
		"object_type":$("#search_object_type").data("post"),
		"device_halt":$("#search_halt_minute").data("post"),
		"device_type_name":$("#search_device_type_name").data("post"),
		"comment":$("#search_comment").data("post")
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
		complete : function(xhrobj, textStatus) {
			var resInfo = $.parseJSON(xhrobj.responseText);
			if (resInfo.errors.length > 0) {
				// 共通出错信息框
				treatBackMessages(null, resInfo.errors);
			} else {
				showlist(resInfo.recordList);
			}
		}
	});
};

var showlist = function(listdata){
	if($("#gbox_list").length > 0) {
		$("#list").jqGrid().clearGridData();
		$("#list").jqGrid('setGridParam',{data:listdata}).trigger("reloadGrid", [{current:false}]);
	} else {
		$("#list").jqGrid({
			data:listdata,
			height: 461,
			width:gridWidth,
			rowheight: 23,
			datatype: "local",
			colNames:['device_jig_repair_record_key','责任<br>工程','修理<br>依赖者','报修<br>时间','管理<br>编号','设备工具名','故障现象','更换部件','修理<br>担当','修理完毕<br>时间','故障<br>和原因','修理对策<br>和方向'
						,'验收<br>担当','验收<br>结果','备注','图片'],
			colModel:[
				{name:'device_jig_repair_record_key',index:'device_jig_repair_record_key',hidden:true},
				{name:'line_name',index:'line_name',width:50},
				{name:'submitter_name',index:'submitter_name',width:45},
				{name:'submit_time',index:'submit_time',width:70,formatter:'date',formatoptions:{srcformat:'Y/m/d H:i:s', newformat : 'y-n-j H:i'}},
				{name:'manage_code',index:'manage_code',width:50},
				{name:'object_name',index:'object_name',width:90},
				{name:'phenomenon',index:'phenomenon',width:115},
				{name:'consumable',index:'consumableconsumable',width:115},
				{name:'maintainer_name',index:'maintainer_name',width:65},
				{name:'repair_complete_time',index:'repair_complete_time',width:75,formatter:'date',formatoptions:{srcformat:'Y/m/d H:i:s', newformat : 'y-n-j H:i'}},
				{name:'fault_causes',index:'fault_causes',width:80},
				{name:'countermeasure',index:'countermeasure',width:110},
				{name:'confirmer_name',index:'confirmer_name',width:45, formatter:function(value, i, rowdata){
					if (value) {
						return value;
					} else {
						return rowdata.submitter_name;
					}
				}},
				{name:'confirm_status',index:'confirm_status',width:45, formatter:function(value, i, rowdata){
					if (rowdata.confirmer_name) {
						return 'OK';
					} else {
						return '';
					}
				}},
				{name:'comment',index:'comment',width:110},
				{name:'photo_flg',index:'photo_flg',width:40,align:'center',formatter:function(value, i, rowdata){
					if (value) {
						return '<img src="http://' + document.location.hostname + '/photos/dj_repair/' + rowdata.device_jig_repair_record_key + '" />'
					} else {
						return '无';
					}
				}}
			],
			rownumbers:true,
			toppager : false,
			rowNum : 20,
			sortorder:"asc",
			sortname:"id",
			multiselect: false,
			pager : "#listpager",
			viewrecords : true,
			ondblClickRow : showDetail,
			onSelectRow: enableButtons,
			gridview : true,
			pagerpos : 'right',
			pgbuttons : true,
			pginput : false,
			recordpos : 'left',
			viewsortcols : [true, 'vertical', true],
			gridComplete:function(){
				enableButtons();
			}
		});
	}
}

var enableButtons = function(rowid){
	$("#confirmbutton").disable();
	if (!rowid) {
		$("#repairbutton").disable();
		return;
	}
	$("#repairbutton").enable();
	var rowdata = $("#list").getRowData(rowid);
	if ((rowdata["repair_complete_time"] && rowdata["repair_complete_time"].trim())
			&& !rowdata["confirm_status"]) {
		$("#confirmbutton").enable();
	}
}

var showDetail = function(rowid){
	if (typeof(rowid) !== "string") {
		rowid=$("#list").jqGrid("getGridParam","selrow");
	}
	var rowData=$("#list").getRowData(rowid);

	showDjrEdit(rowData.device_jig_repair_record_key);
}

// 报修
var showSubmit = function(){

	$.ajax({
		beforeSend : ajaxRequestType,
		async : true,
		url : servicePath + '?method=checkForSubmit',
		cache : false,
		data : null,
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : function(xhrobj, textStatus) {
			var resInfo = $.parseJSON(xhrobj.responseText);
			if (resInfo.errors.length > 0) {
				// 共通出错信息框
				treatBackMessages(null, resInfo.errors);
			} else {
				var limitted = resInfo.limitted;
				// 发生工程
				$("#submit_line_id").val(resInfo.line_id).trigger("change");
				if (limitted == "true") {
					$("#submit_line_id").disable();
				} else {
					$("#submit_line_id").enable();
				}
				$("#submit_object_type").val("9").trigger("change");

				$("#submit_manage_code_dev, #submit_manage_id_dev, #submit_manage_code_jig, #submit_manage_id_jig").val("").hide();
				$("#submit_manage_code_free").show();
				$("#submit_object_name, #submit_phenomenon").val("");

				$("#submitarea").dialog({
					title : "故障报修",
					width : 560,
					height : 'auto',
					resizable : false,
					modal : true,
					buttons : {
						"确定" : doSubmit,
						"关闭" : function(){$(this).dialog('close');}
					}
				});
			}
		}
	});
}

var doSubmit = function(){
	var postData = {
		line_id : $("#submit_line_id").val(),
		object_type : $("#submit_object_type").val(),
		object_name : $("#submit_object_name").val(),
		phenomenon : $("#submit_phenomenon").val()
	}
	if (postData.object_type == 1) {
		postData["manage_id"] = $("#submit_manage_id_dev").val();
		if (!postData["manage_id"]) {
			errorPop("请选择设备•一般工具的管理编号。如果不存在管理编号，请使用“其他(无管理编号)”的类型提交。");
			return;
		}
	} else if (postData.object_type == 2) {
		postData["manage_id"] = $("#submit_manage_id_jig").val();
		if (!postData["manage_id"]) {
			errorPop("请选择专用工具的管理编号。如果不存在管理编号，请使用“其他(无管理编号)”的类型提交。");
			return;
		}
	}

	$.ajax({
		beforeSend : ajaxRequestType,
		async : false,
		url : servicePath + '?method=doSubmit',
		cache : false,
		data : postData,
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : function(xhrobj, textStatus) {
			var resInfo = $.parseJSON(xhrobj.responseText);
			if (resInfo.errors.length > 0) {
				// 共通出错信息框
				treatBackMessages(null, resInfo.errors);
			} else {
				infoPop("报修完成");
				findit();
				$("#submitarea").dialog('close');
			}
		}
	})
}

var confirm = function(){
	var rowid = $("#list").jqGrid("getGridParam","selrow");
	var rowdata = $("#list").getRowData(rowid);
	var postData = {
		device_jig_repair_record_key : rowdata.device_jig_repair_record_key
	}

	$.ajax({
		beforeSend : ajaxRequestType,
		async : false,
		url : servicePath + '?method=doConfirm',
		cache : false,
		data : postData,
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : function(xhrobj, textStatus) {
			var resInfo = $.parseJSON(xhrobj.responseText);
			if (resInfo.errors.length > 0) {
				// 共通出错信息框
				treatBackMessages(null, resInfo.errors);
			} else {
				infoPop("验收完成");
				findit();
			}
		}
	})
}

var showPhoto = function() {
	var imageSrc = this.src;
	if (!imageSrc) return;

	var $dialogDjrPhoto = $("#device_jig_repair_photo");
	if ($dialogDjrPhoto.length == 0) {
		$("body").append("<div id='device_jig_repair_photo'/>");
		$dialogDjrPhoto = $("#device_jig_repair_photo");
	}

	$dialogDjrPhoto.html("<img src='" + imageSrc + "' style='max-height: 600px;'>");

	$dialogDjrPhoto.dialog({
		dialogClass : 'ui-info-dialog',
		title : "故障照片",
		width : 'auto',
		height : 'auto',
		resizable : false
	});

	return false;
}