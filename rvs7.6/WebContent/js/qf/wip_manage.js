var storageServicePath = "wip_storage.do";

$(function() {

	$("#edit_for_agreed, #edit_kind, #edit_anml_exp").buttonset();

	$("#createbutton").click(createDialog);
//	$("#changebutton").click(changeDialog);
//	$("#removebutton").click(removeConfirm);
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
	var resInfo = $.parseJSON(xhrobj.responseText);
	if (resInfo.errors && resInfo.errors.length) {
		// 共通出错信息框
		treatBackMessages("#searcharea", resInfo.errors);
		
		return;
	}
	$("#editarea").dialog("close");
	findit();
}