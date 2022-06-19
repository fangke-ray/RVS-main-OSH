var setOpfObj = (function() {

var $thisDialog = null;
var optionalFixes=[];
var opf_material_id = null;
var callbackOnChanged = null;

var getSettingByMaterial_ajaxSuccess=function(xhrobj){
	var resInfo = $.parseJSON(xhrobj.responseText)

	var $sel = $("<select multiple id='optional_fix_sel'></select>");
	for (var i in resInfo.options_list) {
		var option =  resInfo.options_list[i];
		$sel.append("<option value='" + option.optional_fix_id + "'>" + option.infection_item + "</option>");
	}
	$thisDialog.html($sel);
	for (var i in resInfo.sel_list) {
		var sel_option =  resInfo.sel_list[i];
		$sel.children("option[value='" + sel_option + "']").attr("selected", true);
	}
	$sel.select2Buttons();
	$thisDialog.dialog({
		title : "维修品选择修理项目",
		width : 'auto',
		height : 'auto',
		resizable : false,
		modal : true,
		buttons : {
			"确定": updateSettingByMaterial,
			"关闭": function(){ 
				 $thisDialog.dialog("close");
			}
		}
	});
};

var updateSettingByMaterial = function() {

	var optional_fix = $("#optional_fix_sel").val();

	var postData = {
		"material_id": opf_material_id,
		"optional_fix_id" : (optional_fix ? optional_fix.join() : null)
	};

	$.ajax({
		beforeSend : ajaxRequestType,
		async : true,
		url :'material_optional_fix.do?method=doUpdateSettingByMaterial',
		cache : false,
		data : postData,
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : updateSettingByMaterial_ajaxSuccess
	});
}

var updateSettingByMaterial_ajaxSuccess = function(xhrobj) {
	var resInfo = $.parseJSON(xhrobj.responseText)

	if (callbackOnChanged != null) {
		callbackOnChanged(resInfo);
	}
	$thisDialog.dialog("close");
}

return {
	initDialog : function($light_fix_dialog, arr_material_id, arr_level, callback){

		opf_material_id = arr_material_id;

		$thisDialog = $light_fix_dialog.hide();

		optionalFixes=[];

		callbackOnChanged = callback;

		var postData={
			"material_id": opf_material_id,
			"level" : arr_level
		}

		$.ajax({
			beforeSend : ajaxRequestType,
			async : true,
			url :'material_optional_fix.do?method=getSettingByMaterial',
			cache : false,
			data : postData,
			type : "post",
			dataType : "json",
			success : ajaxSuccessCheck,
			error : ajaxError,
			complete : getSettingByMaterial_ajaxSuccess
		});
	},
	getLabelByMaterial : function(arr_material_id, callback) {
		$.ajax({
			beforeSend : ajaxRequestType,
			async : true,
			url : 'material_optional_fix.do?method=getLabelByMaterial',
			cache : false,
			data : {material_id : arr_material_id},
			type : "post",
			dataType : "json",
			success : ajaxSuccessCheck,
			error : ajaxError,
			complete : function(xhrObj){
				if (callback && typeof callback == "function") {
					var resInfo = $.parseJSON(xhrObj.responseText)
					callback(resInfo.optionalFixLabelText);
				}
			}
		});
	}
}
})();
