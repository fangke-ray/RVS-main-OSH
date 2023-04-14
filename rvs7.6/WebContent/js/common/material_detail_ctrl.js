var isNew = false;

function doUpdate(url,data,callback) {
	$.ajax({
		beforeSend: ajaxRequestType, 
		async: true, 
		data: data,
		url: url,
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : function(xhrobj, textStatus){
			var resInfo = null;
			try {
				// 以Object形式读取JSON
				eval('resInfo =' + xhrobj.responseText);
				if (resInfo.errors.length > 0) {
					// 共通出错信息框
					treatBackMessages("#editarea", resInfo.errors);
				} else {
					if (callback)
						callback(resInfo);
				}
			} catch (e) {
				alert("name: " + e.name + " message: " + e.message + " lineNumber: "
						+ e.lineNumber + " fileName: " + e.fileName);
			};
		}
	});
}

function doMaterialUpdate(material_id){
	var $mainContainer = $("#material_detail_basearea");

	var material = {
		"material_id" : isNew ? "" : material_id,
		"sorc_no": $mainContainer.find("input[name='sorc_no']").val(),
		"esas_no": $mainContainer.find("input[name='esas_no']").val(),
//		"model_id": $("#edit_model_name").val(),
//		"serial_no":$("#edit_serial_no").val(),
		"ocm": $mainContainer.find("select[name='ocm']").val(),
		"package_no": $mainContainer.find("input[name='package_no']").val(),
		"level": $mainContainer.find("select[name='level']").val(),
		"section_id": $mainContainer.find("select[name='section_id']").val(),
		"pat_id": $mainContainer.find("select[name='pat_id']").val(),
		"scheduled_expedited": $mainContainer.find("select[name='scheduled_expedited']").val(),

		"direct_flg": $mainContainer.find("select[name='direct_flg']").val(),
		"service_repair_flg": $mainContainer.find("select[name='service_repair_flg']").val(),
		"fix_type": $mainContainer.find("select[name='fix_type']").val(),

		
//		"reception_time": isNew ? $("#new_edit_reception_time").val() : $("#edit_reception_time").val(),
//		"agreed_date": isNew ? $("#new_edit_agreed_date").val() : $("#edit_agreed_date").val(),
//		"inline_time": isNew ? $("#new_edit_inline_time").val() : $("#edit_inline_time").val(),
//		"outline_time": isNew ? $("#new_edit_outline_time").val() : $("#edit_outline_time").val(),

		"scheduled_date": $mainContainer.find("select[name='scheduled_date']").val(),

		"scheduled_manager_comment": isNew ? $("#new_edit_scheduled_manager_comment").val() : $("#edit_scheduled_manager_comment").val(),
		"am_pm": $mainContainer.find("select[name='am_pm']").val()
	}
	var partial = {//2期修改
		"material_id" : isNew ? "" : material_id,
		"sorc_no": $mainContainer.find("input[name='sorc_no']").val(),
		"occur_times":$("#edit_occur_times").val() || $("#global_occur_times").val(),
		"arrival_plan_date": $("#edit_arrival_plan_date").val(),
		"bo_contents": $("#edit_bo_contents").val()
	}
	var listIdx = 0;
	$("#exd_list").find(".fix_arrival_plan_date").each(
		function(idx,ele) {
			$ele = $(this);
			if (ele.value != $ele.attr("org")) {
				partial["update.arrival_plan_date[" + listIdx + "]"] = ele.value;
				partial["update.material_partial_detail_key[" + listIdx + "]"] = 
					$ele.parent().parent().find("td[aria\\-describedby='exd_list_material_partial_detail_key']").text();
				listIdx++;
			}
		}	
	);
	var process = {
		"material_id" : isNew ? "" : material_id,
//		"dec_finish_date": isNew ? $("#new_edit_dec_finish_date").val() :$("#edit_dec_finish_date").val(),
//		"ns_finish_date": isNew ? $("#new_edit_ns_finish_date").val() :$("#edit_ns_finish_date").val(),
//		"com_finish_date": isNew ? $("#new_edit_com_finish_date").val() :$("#edit_com_finish_date").val(),
		"dec_plan_date": $mainContainer.find("input[name='dec_plan_date']").val(),
		"ns_plan_date": $mainContainer.find("input[name='ns_plan_date']").val(),
		"com_plan_date": $mainContainer.find("input[name='com_plan_date']").val()
	}

	var this_dialog = $("#detail_dialog");
	if (this_dialog.length == 0) {
		this_dialog = $(".ui-dialog-content:visible").eq(0);
	}
	if (this_dialog.length == 0) {
		return;
	}
	var from = this_dialog.find("editable").attr("from");
	if (!from) {
		return;
	}

	switch (from) {
		case "data" : //汇总操作 
		{
			if (doDetailCheck(1)) {
				doUpdate("material.do?method=doUpdate", material, function(){
					doUpdate("materialProcess.do?method=doUpdate", process, function(){
//						doUpdate("materialPartial.do?method=doUpdate", partial, function(){  //2期修改
							this_dialog.dialog('close');
							findit(keepSearchData);
//						});
					});
				});
			}			
			break;
		}
		case "process" : //汇总操作
		{
			if (doDetailCheck(2)) {
				doUpdate("material.do?method=doUpdate", material, function(){
					doUpdate("materialProcess.do?method=doUpdate", process, function(){
						this_dialog.dialog('close');
						findit(keepSearchData);
					});
				});
			}		
			break;
		}
		case "partial" : //汇总操作
		{
			if (doDetailCheck(3)) {
				doUpdate("materialPartial.do?method=doUpdate", partial, function(resInfo){
					if (resInfo.conflexError) {
						alert(resInfo.conflexError);
					} else {
						this_dialog.dialog('close');
						findit(keepSearchData);
					}
				});
			}
			break;
		}
	}
}

var doDetailCheck = function(caseId){
	var message = "";
	if (caseId == 2) {
		// 分解产出安排 NS产出安排 总组产出安排
		if ($("#edit_dec_plan_date").is(":visible") && $("#edit_dec_plan_date").val().trim() == "") {
			message += "请为分解产出安排输入一个日期<br/>";	
		}
		if ($("#edit_ns_plan_date").is(":visible") && $("#edit_ns_plan_date").val().trim() == "") {
			message += "请为ＮＳ产出安排输入一个日期<br/>";
		}
		if ($("#edit_com_plan_date").is(":visible") && $("#edit_com_plan_date").val().trim() == "") {
			message += "请为总组产出安排输入一个日期<br/>";
		}
	}
	if (message == "") {
		return true;
	} else {
		errorPop(message, null);
		return false;
	}
};
