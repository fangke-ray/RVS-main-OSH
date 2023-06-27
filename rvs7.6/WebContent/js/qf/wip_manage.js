var storageServicePath = "wip_storage.do";

$(function() {

	$("#edit_for_agreed, #edit_kind, #edit_anml_exp").buttonset();

	$("#createbutton").click(createDialog);
	$("#changebutton").click(changeDialog);
	$("#removebutton").click(removeConfirm);
	$("#viewbutton").click(function(){
		showLocate("all");
	});

});

var createDialog = function() {
	$("#editarea input:text").val("");
	$("#edit_for_agreed_a,#edit_kind_1,#edit_anml_exp_n").attr("checked", "checked").trigger("change");

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
		wip_storage_code : $("#edit_wip_storage_code").val(),
		simple_code : $("#edit_simple_code").val(),
		for_agreed : $("#edit_for_agreed > input:radio[checked]").val(),
		kind : $("#edit_kind > input:radio[checked]").val(),
		anml_exp : $("#edit_anml_exp > input:radio[checked]").val(),
		shelf : $("#edit_shelf").val(),
		layer : $("#edit_layer").val()
	}

	$.ajax({
		beforeSend : ajaxRequestType,
		async : false,
		url : storageServicePath + '?method=doCreate',
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
	var resInfo = JSON.parse(xhrobj.responseText);
	if (resInfo.errors && resInfo.errors.length) {
		// 共通出错信息框
		treatBackMessages("#searcharea", resInfo.errors);
		
		return;
	}
	$("#editarea").dialog("close");
	findit();
}

var changeDialog = function() {
	$("#editarea input:text").val("");
	$("#edit_for_agreed_a,#edit_kind_1,#edit_anml_exp_n").attr("checked", "checked").trigger("change");

	var rowid = $("#list").jqGrid("getGridParam", "selrow");
	var rowdata = $("#list").getRowData(rowid);

	$.ajax({
		beforeSend : ajaxRequestType,
		async : false,
		url : storageServicePath + '?method=getLocationDetail',
		cache : false,
		data : {wip_storage_code : rowdata.wip_location},
		type : "post",
		dataType : "json",// 服务器返回的数据类型
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : function(xhrobj, textStatus){
			var resInfo = JSON.parse(xhrobj.responseText);

			var storageDetail = resInfo.storageDetail;

			$("#edit_wip_storage_code").val(storageDetail.wip_storage_code)
				.attr("origin", storageDetail.wip_storage_code);
			$("#edit_shelf").val(storageDetail.shelf);
			$("#edit_layer").val(storageDetail.layer);
			$("#edit_simple_code").val(storageDetail.simple_code);
			
			if (storageDetail.anml_exp == 1) {
				$("#edit_anml_exp_y").attr("checked", true).trigger("change");
			} else {
				$("#edit_anml_exp_n").attr("checked", true).trigger("change");
			}

			if (storageDetail.for_agreed == 1) {
				$("#edit_for_agreed_y").attr("checked", true).trigger("change");
			} else if (storageDetail.for_agreed == -1) {
				$("#edit_for_agreed_n").attr("checked", true).trigger("change");
			} else {
				$("#edit_for_agreed_a").attr("checked", true).trigger("change");
			}

			$("#edit_kind_" + storageDetail.kind).attr("checked", true).trigger("change");

			var $dialog = $("#editarea").dialog({
				title : "修改库位",
				height :  'auto',
				resizable : false,
				modal : true,
				buttons : {
					"修改" : postUpdate,
					"关闭" : function(){
						$dialog.dialog("close");
					}
				}
			});
		}
	});	
}

var postUpdate = function(){
	var postData = {
		origin_wip_storage_code : $("#edit_wip_storage_code").attr("origin"),
		wip_storage_code : $("#edit_wip_storage_code").val(),
		simple_code : $("#edit_simple_code").val(),
		for_agreed : $("#edit_for_agreed > input:radio[checked]").val(),
		kind : $("#edit_kind > input:radio[checked]").val(),
		anml_exp : $("#edit_anml_exp > input:radio[checked]").val(),
		shelf : $("#edit_shelf").val(),
		layer : $("#edit_layer").val()
	}

	$.ajax({
		beforeSend : ajaxRequestType,
		async : false,
		url : storageServicePath + '?method=doChangeSetting',
		cache : false,
		data : postData,
		type : "post",
		dataType : "json",// 服务器返回的数据类型
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : create_handleComplete
	});	
}

var removeConfirm = function(){
	var rowid = $("#list").jqGrid("getGridParam", "selrow");
	var rowdata = $("#list").getRowData(rowid);

	var wip_location = rowdata.wip_location;

	warningConfirm("是否要删除库位：" + wip_location + "？", function(){
		$.ajax({
			beforeSend : ajaxRequestType,
			async : false,
			url : storageServicePath + '?method=doRemove',
			cache : false,
			data : {wip_storage_code : wip_location},
			type : "post",
			dataType : "json",// 服务器返回的数据类型
			success : ajaxSuccessCheck,
			error : ajaxError,
			complete : function() {
				findit();
			}
		});
	});
}