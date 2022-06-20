var isNew = false;


function case0(){
	hideCompoment([
		"#edit_sorc_no","#edit_esas_no","#inp_modelname",
		"#edit_model_name","#edit_serial_no","#edit_ocm","#edit_package_no",
		"#edit_level","#edit_section_id","#edit_scheduled_expedited",
		"#edit_reception_time","#edit_agreed_date",
		"#edit_inline_time","#edit_outline_time","#edit_scheduled_manager_comment","#edit_am_pm",
		"#service_repair","#direct","#fix_type",
		//==//
		"#edit_bo_flg","#edit_order_date","#edit_arrival_plan_date","#edit_arrival_date","#div_no_arrival_plan_date",
		"#edit_bo_contents1","#edit_bo_contents2","#edit_bo_contents3",
		"#label_bo_contents1","#label_bo_contents2","#label_bo_contents3",
		//==//
		"#edit_dec_plan_date","#edit_dec_finish_date","#edit_ns_plan_date",
		"#edit_ns_finish_date","#edit_com_plan_date","#edit_com_finish_date",
		
		//==//
		"#tr_dec_date","#tr_ns_date","#tr_com_date"
		]);
	$("#edit_pat").parent().parent().hide();
}

function case1() {
	hideCompoment([
		"#label_sorc_no","#label_esas_no","#inp_modelname",
		"#edit_model_name","#edit_serial_no","#label_ocmName","#label_package_no",
		"#label_level_name","#label_section_name","#label_scheduled_expedited",
		"#label_remark","#label_reception_time","#label_agreed_date",
		"#label_inline_time","#label_outline_time","#label_scheduled_manager_comment",
		//==//
		//"#label_bo_flg","#label_arrival_date","#label_arrival_plan_date",//2期修改
		"#edit_order_date","#edit_arrival_plan_date","#tr_bo_contents",
		//==//
		"#edit_dec_plan_date","#label_dec_finish_date","#edit_ns_plan_date",
		"#label_ns_finish_date","#edit_com_plan_date","#label_com_finish_date"
		]);
				
		$("#edit_ocm, #edit_level, #edit_section_id, #service_repair, #edit_am_pm, #new_edit_am_pm, #direct, #fix_type, #edit_scheduled_expedited, #edit_bo_flg").select2Buttons();
		setReferChooser($("#edit_model_name"),$("#material_detial_refer"));
		
		$("#edit_reception_time, #edit_inline_time," +
			" #edit_outline_time, #edit_arrival_plan_date, #edit_arrival_date, #edit_dec_finish_date, #edit_ns_finish_date, #edit_com_finish_date").datepicker({
			showButtonPanel:true,
			dateFormat: "yy/mm/dd",
			currentText: "今天"
		});
		$("#edit_agreed_date").datepicker({
			showButtonPanel:true,
			dateFormat: "yy/mm/dd",
			currentText: "今天",
			maxDate :0
		});
		$("#div_no_arrival_plan_date").buttonset();
		var value = $("#edit_arrival_plan_date").val();
		if (value === '9999/12/31') {
			$("#edit_no_arrival_plan_date").attr("checked",true).trigger("change");
			$("#edit_arrival_plan_date").hide();
		}
		$("#edit_no_arrival_plan_date").click(function(){
			if ($(this).attr("checked")) {
				$("#edit_arrival_plan_date").hide();
				$("#edit_arrival_plan_date").val("9999/12/31");
			} else {
				$("#edit_arrival_plan_date").show();
				$("#edit_arrival_plan_date").val(value);
			}
		});
		
	$("#label_com_plan_date").text($("#label_scheduled_date").text());//总组产出安排等于纳期
	$("#edit_pat").parent().parent().hide();
}

// 计划编辑时
function case2(){
	hideCompoment([
		"#edit_sorc_no","#edit_esas_no","#inp_modelname",
		"#edit_model_name","#edit_serial_no","#edit_ocm","#edit_package_no",
		"#label_level_name","#label_section_name","#label_scheduled_expedited",
		"#edit_reception_time","#edit_agreed_date",
		"#edit_inline_time","#edit_outline_time","#label_scheduled_manager_comment",
		"#service_repair","#direct","#fix_type",
		//==//
		"#edit_bo_flg","#edit_order_date","#edit_arrival_plan_date","#edit_arrival_date","#div_no_arrival_plan_date",
		"#edit_bo_contents1","#edit_bo_contents2","#edit_bo_contents3",
		"#label_bo_contents1","#label_bo_contents2","#label_bo_contents3",
		//==//
		"#edit_dec_finish_date","#edit_ns_finish_date","#edit_com_finish_date"
		]);
				
	if ($("#label_dec_plan_date").text() == "" || $("#label_dec_finish_date").text() != "") { // 安排存在值，并且没有产出时可编辑
		$("#edit_dec_plan_date").hide()
	} else {
		$("#label_dec_plan_date").hide();
		$("#edit_dec_plan_date").datepicker({
			showButtonPanel:true,
			dateFormat: "yy/mm/dd",
			currentText: "今天",
			minDate :0
		});
	}
	if ($("#label_ns_plan_date").text() == "" || $("#label_ns_finish_date").text() != "") {//存在值
		$("#edit_ns_plan_date").hide()
	} else {
		$("#label_ns_plan_date").hide();
		$("#edit_ns_plan_date").datepicker({
			showButtonPanel:true,
			dateFormat: "yy/mm/dd",
			currentText: "今天",
			minDate :0
		});
	}
	if ($("#label_com_plan_date").text() == "" || $("#label_com_finish_date").text() != "")  {//存在值
		$("#edit_com_plan_date").hide()
	} else {
		$("#label_com_plan_date").hide();
		$("#edit_com_plan_date").datepicker({
			showButtonPanel:true,
			dateFormat: "yy/mm/dd",
			currentText: "今天",
			minDate :0
		});
	}
				
	$("#edit_level, #edit_section_id, #edit_am_pm, #edit_scheduled_expedited, #edit_pat").select2Buttons();
	
	//$("#label_scheduled_date").text($("#label_com_plan_date").text());//编辑时纳期等于总组产出安排
	$("#edit_pat").parent().parent().show();
}
function case3(){
	hideCompoment([
		"#edit_sorc_no","#edit_esas_no","#inp_modelname",
		"#edit_model_name","#edit_serial_no","#edit_ocm","#edit_package_no",
		"#edit_level","#edit_section_id","#edit_scheduled_expedited",
		"#edit_reception_time","#edit_agreed_date",
		"#edit_inline_time","#edit_outline_time","#edit_scheduled_manager_comment","#edit_am_pm",
		"#service_repair","#direct","#fix_type",
		//==//
		//"#label_bo_flg","#label_arrival_date","#label_arrival_plan_date",//2期修改
		"#edit_order_date","#div_no_arrival_plan_date","#edit_arrival_plan_date",
		
		"#tr_dec_date","#tr_ns_date","#tr_com_date",
		"#distributions"
		]);

		$("#edit_bo_flg").select2Buttons();
		$("#edit_arrival_plan_date, #edit_arrival_date").datepicker({
			showButtonPanel:true,
			dateFormat: "yy/mm/dd",
			currentText: "今天"
		});
		$("#div_no_arrival_plan_date").buttonset();
		var value = $("#edit_arrival_plan_date").val();
		if (value === '9999/12/31') {
			$("#edit_no_arrival_plan_date").attr("checked",true).trigger("change");
			$("#edit_arrival_plan_date").hide();
		}
		$("#edit_no_arrival_plan_date").click(function(){
			if ($(this).attr("checked")) {
				$("#edit_arrival_plan_date").hide();
				$("#edit_arrival_plan_date").val("9999/12/31");
			} else {
				$("#edit_arrival_plan_date").show();
				$("#edit_arrival_plan_date").val(value);
			}
		});
	$("#edit_pat").parent().parent().hide();
}

function case4(){
	hideCompoment([
		"#label_model_name","#label_serial_no",
		"#label_order_date",
		"#old_info",
		"#distributions"
	]);
	showCompoment([
		"#edit_serial_no","#inp_modelname","#edit_order_date","#new_info"
	]);
	
	$("#new_edit_reception_time, #new_edit_inline_time," +
			" #new_edit_outline_time, #new_edit_dec_finish_date, #new_edit_ns_finish_date, #new_edit_com_finish_date, #edit_order_date").datepicker({
		showButtonPanel:true,
		dateFormat: "yy/mm/dd",
		currentText: "今天"
	});
	
	$("#new_edit_agreed_date").datepicker({
			showButtonPanel:true,
			dateFormat: "yy/mm/dd",
			currentText: "今天",
			maxDate :0
		});
		
	//情况编辑区
	$("#edit_sorc_no").val("");
	$("#edit_esas_no").val("");
	$("#edit_model_name").val("");
	$("#edit_serial_no").val("");
	$("#edit_ocm").val("").trigger("change");
	$("#edit_package_no").val("");
	$("#edit_level").val("").trigger("change");
	$("#edit_section_id").val("").trigger("change");
	$("#edit_scheduled_expedited").val("").trigger("change");
	
	$("#direct").val("").trigger("change");
	$("#service_repair").val("").trigger("change");
	$("#fix_type").val("").trigger("change");
	
	$("#edit_bo_flg").val("0").trigger("change");
	$("#edit_order_date").val("");
	$("#edit_arrival_plan_date").val("");
	$("#edit_arrival_date").val("");
	
	$("#edit_bo_contents1").val("");
	$("#edit_bo_contents2").val("");
	$("#edit_bo_contents3").val("");
	
	$("#new_label_scheduled_date").text($("#label_com_plan_date").text());//编辑时纳期等于总组产出安排
	$("#edit_pat").parent().parent().hide();
}

function hideCompoment(comps) {
	for (var i in comps) {
		$(comps[i]).hide();
	}
}
function showCompoment(comps) {
	for (var i in comps) {
		$(comps[i]).show();
	}
}

function expeditedText(scheduled_expedited) {
	if (scheduled_expedited === '2') return "直送快速";
	if (scheduled_expedited === '1') return "加急";
	return "";
}

function setLabelText(data, data2, data3, times, material_id, occur_times) {

	$("#global_material_id").val(material_id);
	$("#global_occur_times").val(occur_times);
	if (data) {
		$("#label_sorc_no").text(data.sorc_no);
		$("#label_esas_no").text(data.esas_no);
		$("#label_model_name").text(data.model_name);
		$("#label_serial_no").text(data.serial_no);
		$("#label_ocmName").text(data.ocmName);
		$("#label_package_no").text(data.package_no);
		$("#label_level_name").text(data.levelName);
		$("#label_section_name").text(data.section_name);
		$("#label_scheduled_expedited").text(expeditedText(data.scheduled_expedited));
		$("#label_remark").text(data.remark);
		$("#label_status").text(data.status);
		$("#label_processing_position").text(data.processing_position);//当前位置
		$("#label_reception_time").text(data.reception_time);
		$("#label_finish_time").text(data.finish_time);
		$("#label_agreed_date").text(data.agreed_date);
		$("#label_inline_time").text(data.inline_time);
		$("#label_scheduled_date").text(data.scheduled_date);
		$("#label_outline_time").text(data.outline_time);
		$("#label_scheduled_manager_comment").text(data.scheduled_manager_comment);

		if (data.optional_fix_id && data.optional_fix_id.length) {
			var content = '';
			var optional_fix_items = data.optional_fix_id.split("；");
			for (var iofi in optional_fix_items) {
				content += '<tr><td class="td-content">' + decodeText(optional_fix_items[iofi]) + '</td></tr>';
			}
			$("#optional_fix_items > table").html(content);
			$("#optional_fix_items").show();
			
		} else {
			$("#optional_fix_items").hide();
		}

		//====
		$("#edit_sorc_no").val(data.sorc_no);
		$("#edit_esas_no").val(data.esas_no);
		$("#edit_model_name").val(data.model_id);
		$("#edit_serial_no").val(data.serial_no);
		$("#edit_ocm").val(data.ocm).trigger("change");
		$("#edit_package_no").val(data.package_no);
		$("#edit_level").val(data.level).trigger("change");
		$("#edit_section_id").val(data.section_id).trigger("change");

		if (data.scheduled_expedited == 2) {
			$("#label_scheduled_expedited").show();
			$("#edit_scheduled_expedited").append("<option value='2'>2</option>").hide().next().hide();
		}
		$("#edit_scheduled_expedited").val(data.scheduled_expedited);

		$("#direct").val(data.direct_flg).trigger("change");
		$("#service_repair").val(data.service_repair_flg).trigger("change");
		$("#fix_type").val(data.fix_type).trigger("change");
		
		$("#edit_status").val(data.status);
		$("#edit_processing_position").val(data.processing_position);
		$("#edit_reception_time").val(data.reception_time);
		$("#edit_finish_time").val(data.finish_time);
		$("#edit_agreed_date").val(data.agreed_date);
		$("#edit_inline_time").val(data.inline_time);
		$("#edit_scheduled_date").val(data.scheduled_date);
		$("#edit_outline_time").val(data.outline_time);
		$("#edit_scheduled_manager_comment").val(data.scheduled_manager_comment);
		$("#edit_am_pm").val(data.am_pm).trigger("change");
		$("#edit_pat").val(data.pat_id).trigger("change");
	}
	if(data2){
		var boflag = "";
		if (data2.bo_flg === '1') {
			boflag = "有BO";
		} else if (data2.bo_flg === '0') {
			boflag = "无BO";
		} else if (data2.bo_flg === '2') {
			boflag = "BO解决";
		} else if (data2.bo_flg === '9') {
			boflag = "未签收";
		} else if (data2.bo_flg === '7') {
			boflag = "预提";
		}
		$("#label_bo_flg").text(boflag);
		$("#label_order_date").text(data2.order_date);
		$("#label_arrival_plan_date").text(data2.arrival_plan_date === '9999/12/31' ? '未定' : data2.arrival_plan_date);
		$("#label_arrival_date").text(data2.arrival_date);
		if (data2.process_code || data2.process_name) {
			$("#label_process_code").text(data2.process_code+" "+ data2.process_name);
		} else {
			$("#label_process_code").text("工程前订购");
		}
		if (data2.bo_contents) {//不为空
			if (data2.bo_contents.charAt(0) == "{") {
				var content = null;
				try {
					eval('content =' + data2.bo_contents);
				} catch(e) {
					
				}
				if (content) {
					$("#label_bo_contents").text("分解缺品零件:" + content.dec +",NS缺品零件:"+ content.ns +",总组缺品零件:"+content.com);
					
					$("#edit_bo_contents1").val(content.dec);
					$("#edit_bo_contents2").val(content.ns);
					$("#edit_bo_contents3").val(content.com);
				} else {
					$("#label_bo_contents").text(data2.bo_contents);
					$("#edit_bo_contents").val(data2.bo_contents);
				}			
			} else {
				$("#label_bo_contents").text(data2.bo_contents);
				$("#edit_bo_contents").val(data2.bo_contents);
			}
		} else {
			$("#label_bo_contents").text("");
			$("#edit_bo_contents").val("");
			$("#edit_bo_contents1").val("");
			$("#edit_bo_contents2").val("");
			$("#edit_bo_contents3").val("");
		}
		//==
		$("#edit_bo_flg").val(data2.bo_flg);
		$("#edit_order_date").val(data2.order_date);
		$("#edit_arrival_plan_date").val(data2.arrival_plan_date);
		$("#edit_arrival_date").val(data2.arrival_date);
	}
	if(data3){
		$("#label_dec_plan_date").text(data3.dec_plan_date);
		$("#label_dec_finish_date").text(data3.dec_finish_date);
		$("#label_ns_plan_date").text(data3.ns_plan_date);
		$("#label_ns_finish_date").text(data3.ns_finish_date);
		$("#label_com_plan_date").text(data3.com_plan_date);
		$("#label_com_finish_date").text(data3.com_finish_date);
		//==
		$("#edit_dec_plan_date").val(data3.dec_plan_date);
		$("#edit_dec_finish_date").val(data3.dec_finish_date);
		$("#edit_ns_plan_date").val(data3.ns_plan_date);
		$("#edit_ns_finish_date").val(data3.ns_finish_date);
		$("#edit_com_plan_date").val(data3.com_plan_date);
		$("#edit_com_finish_date").val(data3.com_finish_date);
	}
	
	if (times) {
//		for (var i in times) {
//			$("#edit_occur_times").append('<option value="'+times[i]+'">'+times[i]+'次</option>');
//		}
//		$("#edit_occur_times").select2Buttons();
//		changeTimes(material_id);
	}

	if ($("#edit_material_comment_other").length > 0) {
		var postData = {
			material_id : $("#global_material_id").val(),
			write : $("#edit_material_comment").length
		}
		$.ajax({
			beforeSend : ajaxRequestType,
			async : false,
			url : 'material.do?method=getMaterialComment',
			cache : false,
			data : postData,
			type : "post",
			dataType : "json",
			success : ajaxSuccessCheck,
			error : ajaxError,
			complete : function(xhrObj) {
				var resInfo = $.parseJSON(xhrObj.responseText);
				if (resInfo.errors && resInfo.errors.length > 0) {
					treatBackMessages(null, resInfo.errors);
					return;
				} else {
					if (resInfo.material_comment_other) {
						$("#edit_material_comment_other").val(resInfo.material_comment_other);
					} else {
						$("#edit_material_comment_other").prev().hide().end().hide();
					}
					if (postData.write) {
						$("#edit_material_comment").val(resInfo.material_comment);
					}
				}
			}
		});
	}
}


function changeTimes(material_id) {
	$("#edit_occur_times").change(function(){
		var value = $(this).val();
		$.ajax({
			data : {id:material_id, times: value},
			url : 'materialPartial.do?method=loadByTimes',
			complete : function(xhrobj, textStatus){
				var resInfo = null;
				try {
					// 以Object形式读取JSON
					eval('resInfo =' + xhrobj.responseText);
					setLabelText(null,resInfo.partialForm);
				} catch (e) {
				};
			}
		})
	});
}

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

/*function dateFormat(date) {
	if (date) {
		var d = new Date(date);
		var year = d.getFullYear();
		var month = d.getMonth() + 1;
		var day = d.getDate();
		
		return year + "/" + month + "/" + day;
	}
	return "";
}
*/
function doMaterialUpdate(material_id){ //TODO: new
	var material = {
		"material_id" : isNew ? "" : material_id,
		"sorc_no": $("#edit_sorc_no").val(),
		"esas_no": $("#edit_esas_no").val(),
		"model_id": $("#edit_model_name").val(),
		"serial_no":$("#edit_serial_no").val(),
		"ocm":$("#edit_ocm").val(),
		"package_no":$("#edit_package_no").val(),
		"level":$("#edit_level").val(),
		"section_id": $("#edit_section_id").val(),
		"scheduled_expedited": $("#edit_scheduled_expedited").val(),
		"direct_flg":$("#direct").val(),
		"service_repair_flg":$("#service_repair").val(),
		"fix_type":$("#fix_type").val(),
		"pat_id":$("#edit_pat").val(),
		
		"reception_time": isNew ? $("#new_edit_reception_time").val() : $("#edit_reception_time").val(),
		"agreed_date": isNew ? $("#new_edit_agreed_date").val() : $("#edit_agreed_date").val(),
		"inline_time": isNew ? $("#new_edit_inline_time").val() : $("#edit_inline_time").val(),
		"outline_time": isNew ? $("#new_edit_outline_time").val() : $("#edit_outline_time").val(),
		"scheduled_manager_comment": isNew ? $("#new_edit_scheduled_manager_comment").val() : $("#edit_scheduled_manager_comment").val(),
		"am_pm": isNew ? $("#new_edit_am_pm").val() : $("#edit_am_pm").val()
	}
	var partial = {//2期修改
		"material_id" : isNew ? "" : material_id,
		"sorc_no": $("#edit_sorc_no").val(),
		"occur_times":$("#edit_occur_times").val() || $("#global_occur_times").val(),
		//"bo_flg": $("#edit_bo_flg").val(),
		//"order_date": $("#edit_order_date").val(),
		"arrival_plan_date": $("#edit_arrival_plan_date").val(),
		//"arrival_date": $("#edit_arrival_date").val(),
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
		"dec_finish_date": isNew ? $("#new_edit_dec_finish_date").val() :$("#edit_dec_finish_date").val(),
		"ns_finish_date": isNew ? $("#new_edit_ns_finish_date").val() :$("#edit_ns_finish_date").val(),
		"com_finish_date": isNew ? $("#new_edit_com_finish_date").val() :$("#edit_com_finish_date").val(),
		"dec_plan_date": $("#edit_dec_plan_date").val(),
		"ns_plan_date": $("#edit_ns_plan_date").val(),
		"com_plan_date": $("#edit_com_plan_date").val()
	}
	
	if (caseId == 1 || caseId == 4) { //汇总操作
		if (doDetailCheck(caseId)) {
			doUpdate("material.do?method=doUpdate", material, function(){
				//doUpdate("materialPartial.do?method=doUpdate", partial, function(){  //2期修改
					doUpdate("materialProcess.do?method=doUpdate", process, function(){
						var this_dialog = $("#detail_dialog");
						this_dialog.dialog('close');
						findit(keepSearchData);
					});
					//});
			});
		}
	} else if (caseId == 2) { //计划操作
		if (doDetailCheck(caseId)) {
			doUpdate("material.do?method=doUpdate", material, function(){
				doUpdate("materialProcess.do?method=doUpdate", process, function(){
					var this_dialog = $("#detail_dialog");
					this_dialog.dialog('close');
					findit(keepSearchData);
				});
			});
		}
	} else if (caseId == 3) { //现品操作
		if (doDetailCheck(caseId)) {
			doUpdate("materialPartial.do?method=doUpdate", partial, function(resInfo){
				if (resInfo.conflexError) {
					alert(resInfo.conflexError);
					// partial.confirmed = "1"; // DE100084
					// doUpdate("materialPartial.do?method=doUpdate", partial, function(resInfo){
//						var this_dialog = $("#detail_dialog");
//						this_dialog.dialog('close');
//						findit(keepSearchData);
					// }
				} else {
					var this_dialog = $("#detail_dialog");
					this_dialog.dialog('close');
					findit(keepSearchData);
				}

			});
		}
	} else if (isNew) {
		//TODO:insert new 
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
