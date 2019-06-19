var dbServicePath = "device_backup.do";
var obj_manage_level = {};
var obj_status = {};

$(function(){
//	$("input.ui-button").button();

//	setReferChooser($("#add_device_type_id"),$("#device_type_id_referchooser"));

//	$("#setbutton").click(showDetail);

	// 导出按钮
	$("#backupreportbutton").click(dbMakeReport);

	obj_manage_level = selecOptions2Object($("#hidden_goManage_level").val());
	obj_status = selecOptions2Object($("#hidden_goStatus").val());

});

var showDeviceBackup = function(manage_id, device_type_id, model_name, line_name){

	var postData = {
		manage_id : manage_id,
		device_type_id : device_type_id,
		model_name : model_name,
		line_name : line_name
	}

	$.ajax({
		beforeSend : ajaxRequestType,
		async : true,
		url : dbServicePath + '?method=getBackups',
		cache : false,
		data : postData,
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : function(xhrobj, textStatus){showDeviceBackupComplete(xhrobj)}
	});

}

var showDeviceBackupComplete = function(xhrobj) {
	var resInfo = $.parseJSON(xhrobj.responseText);
	if (resInfo.errors.length > 0) {
		// 共通出错信息框
		treatBackMessages(null, resInfo.errors);
	} else {
		
		var $dialogDbckEdit = $("#device_backup_edit");
		if ($dialogDbckEdit.length == 0) {
			$("body").append("<div id='device_backup_edit'/>");
			$dialogDbckEdit = $("#device_backup_edit");
		}

		$dialogDbckEdit.load("device_backup.do?method=detail", function(responseText, textStatus, XMLHttpRequest) {
			showDeviceBackupDialog(resInfo);
		})

	}
}

var showDeviceBackupDialog = function(resInfo) {
	var rowData = resInfo.rowData || {};

	$("#set_manage_code").text(rowData.manage_code);
	$("#set_manage_id").val(rowData.devices_manage_id);
	$("#set_manage_level").text(obj_manage_level[rowData.manage_level]);
	$("#set_status").text(obj_status[rowData.status]);
	$("#set_device_type_name").text(rowData.name);
	$("#set_model_name").text(rowData.model_name);
	$("#set_line_name").text(rowData.line_name);

	$("#set_evaluation").text(rowData.evaluation);
	$("#set_corresponding").val(rowData.corresponding);
	if (rowData.manage_content) {
		$("#set_corresponding").parent().append("<div>当前此设备工具正代替" + rowData.manage_content + "使用中。</div>");
	}

	var $piresBody = $("#pires tbody");
	$piresBody.html("");

	if (resInfo.lSameModel.length + resInfo.lOtherModel.length == 0) {
		$piresBody.append("<tr><td colspan=6>没有同样品名的设备！</tr>");
	} else {
		$piresBody.append(drawPiresBody(rowData.manage_code, rowData.line_name, resInfo.lSameModel));
		$piresBody.append("<tr><td colspan=6>——此处以上为同型号————————————————————————以下为不同型号——</tr>");
		$piresBody.append(drawPiresBody(rowData.manage_code, rowData.line_name, resInfo.lOtherModel));
	}
	if (resInfo.lOtherType && resInfo.lOtherType.length > 0) {
		$piresBody.append("<tr><td colspan=6>——此处以上为同品类————————————————————————以下为不同品类——</tr>");
		$piresBody.append(drawPiresBody(rowData.manage_code, rowData.line_name, resInfo.lOtherType));
	}

	if (typeof(setReferChooser) === "function") {
		$piresBody.append('<tr><td colspan="6"><input class="ui-button ui-widget ui-state-default ui-corner-all" id="stbutton" value="选择其他品类设备代替" style="float:left;left:2px" role="button" aria-disabled="false" type="button">' +
				'<input placeholder="选择品名" style="float:left;margin-left:4px;margin-top:4px;" type="text"><input type="hidden" id="st_device_type_id"/></td></tr>');
		$piresBody.find("#stbutton").button().click(loadOtherDT);
		if ($("#name_referchooser").length > 0) {
			setReferChooser($piresBody.find("#st_device_type_id"), $("#name_referchooser"));
		}
	}
	for (var iR in resInfo.relations) {
		var relation = resInfo.relations[iR];
		if (relation["manage_id"] == rowData.devices_manage_id) {
			var $target = $("#pires tr[manage_id='" + relation["backup_manage_id"] + "'] .ub button");
			var free_displace_flg = relation["free_displace_flg"];
			$target.attr("org", free_displace_flg);
			if (free_displace_flg) {
				$target.attr("usage", "1")
					.text("可以");
			} else {
				$target.attr("usage", "0")
					.text("限定条件");
			}
		} else {
			var $target = $("#pires tr[manage_id='" + relation["manage_id"] + "'] .bu button");
			var free_displace_flg = relation["free_displace_flg"];
			$target.attr("org", free_displace_flg);
			if (free_displace_flg) {
				$target.attr("usage", "1")
					.text("可以");
			} else {
				$target.attr("usage", "0")
					.text("限定条件");
			}
		}
	}

	checkEvaluation();

	$("#pires").on("click", "button", function(){
		var $button = $(this);
		if ($button.attr("usage") == undefined) {
			$button.attr("usage", "1")
				.text("可以");
		} else if ($button.attr("usage") == "1") {
			$button.attr("usage", "0")
				.text("限定条件");
		} else {
			$button.removeAttr("usage")
				.text("不可以");
		}
		checkEvaluation();
		return false;
	});

	var $submitbutton = $("#db_submitbutton").button();
	if ($submitbutton.length == 0) {
//		$("#setbutton").val("查看");
	} else {
		$submitbutton.click(submit);
	}

	$("#device_backup_edit").dialog({
		position : 'center',
		title : "设定设备代替关系",
		width :800,
		height : 720,
		resizable : false,
		modal : true,
		buttons : {
			"关闭" : function() {
				$("#device_backup_edit").dialog("close");
			}
		}
	});
}

var drawPiresBody = function(manage_code, line_name, devices, checkExist){
	var retString = "";
	var existId = [];
	if (checkExist) {
		existId = $("#pires tbody").find("tr[manage_id]").map(function(){return this.getAttribute("manage_id")}).get();
	}
	for (var i in devices) {
		var device = devices[i];
		if (!checkExist || existId.indexOf(device.devices_manage_id) < 0) {
			retString += "<tr manage_id='" + device.devices_manage_id + "'" + (device.manage_content ? "title='正代替" + device.manage_content + "使用中'" : "")
				+ "><td>" + device.manage_code + "</td><td>" + getStatus(device.manage_level, device.status) 
				+ "</td><td>" + (device.model_name || "-") + "</td><td>" + getLine(device.section_name, device.line_name)
				+ "</td><td class='ub'><button" + ((line_name == device.line_name) ? " coline" : "") + ">不可以</button> 代替" + manage_code 
				+ "作业</td><td class='bu'><button>不可以</button> 用" + manage_code + "代替作业</td></tr>";
		}
	}

	return retString;
}

var getStatus = function(manage_level, status) {
	var rets = "";
	rets += (obj_manage_level[manage_level]) + " ";
	if (status == 1) {
		rets += "使用中";
	} else if (status == 4) {
		rets += "保管中";
	} else if (status == 5) {
		rets += "使用中(周边)";
	}
	return rets;
}

var getLine = function(section_name, line_name) {
	if (line_name) {
		return line_name;
	} else if (section_name) {
		return "(" + section_name + ")"
	} else {
		return "未知";
	}
}

var checkEvaluation = function(){
	var $buttons = $("#pires .ub button");
	
	if ($buttons.filter("[usage='1'][coline]").length > 0) {
		$("#set_evaluation").text("◎");
	} else if ($buttons.filter("[usage='1']").length > 0) {
		$("#set_evaluation").text("○");
	} else if ($buttons.filter("[usage='0']").length > 0) {
		$("#set_evaluation").text("△");
	} else {
		$("#set_evaluation").text("×");
	}
}

var submit = function(){
	if ($("#set_evaluation").text() === "×") {
		if (!$("#set_corresponding").val()) {
			errorPop("无可替换品的设备，需要填写当此设备异常时的应急措施。");
			return;
		}
	}

	var postData = {
		"manage_id" : $("#set_manage_id").val(),
		"corresponding" : $("#set_corresponding").val()
	}
	var idx = 0;
	$("#pires .ub button").each(function(i, ele){
		var $ele = $(ele);
		var usage = $ele.attr("usage");
		var org = $ele.attr("org");
		if (usage == undefined) {
			if (org != undefined) {
				postData["updates.backup_manage_id[" + idx + "]"] = $(ele).closest("tr").attr("manage_id");
				postData["updates.status[" + idx + "]"] = "3"; // delete
				idx++;
			}
		} else {
			if (org != undefined) {
				if (usage != org) {
					postData["updates.backup_manage_id[" + idx + "]"] = $(ele).closest("tr").attr("manage_id");
					postData["updates.free_displace_flg[" + idx + "]"] = $(ele).attr("usage");
					postData["updates.status[" + idx + "]"] = "2"; // update
					idx++;
				}
			} else {
				postData["updates.backup_manage_id[" + idx + "]"] = $(ele).closest("tr").attr("manage_id");
				postData["updates.free_displace_flg[" + idx + "]"] = $(ele).attr("usage");
				postData["updates.status[" + idx + "]"] = "1"; // insert
				idx++;
			}
		}
	});

	$("#pires .bu button").each(function(i, ele){
		var $ele = $(ele);
		var usage = $ele.attr("usage");
		var org = $ele.attr("org");
		if (usage == undefined) {
			if (org != undefined) {
				postData["updates.manage_id[" + idx + "]"] = $(ele).closest("tr").attr("manage_id");
				postData["updates.status[" + idx + "]"] = "3"; // delete
				idx++;
			}
		} else {
			if (org != undefined) {
				if (usage != org) {
					postData["updates.manage_id[" + idx + "]"] = $(ele).closest("tr").attr("manage_id");
					postData["updates.free_displace_flg[" + idx + "]"] = $(ele).attr("usage");
					postData["updates.status[" + idx + "]"] = "2"; // update
					idx++;
				}
			} else {
				postData["updates.manage_id[" + idx + "]"] = $(ele).closest("tr").attr("manage_id");
				postData["updates.free_displace_flg[" + idx + "]"] = $(ele).attr("usage");
				postData["updates.status[" + idx + "]"] = "1"; // insert
				idx++;
			}
		}
	});

	$.ajax({
		beforeSend : ajaxRequestType,
		async : true,
		url : dbServicePath + '?method=doSubmit',
		cache : false,
		data : postData,
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : function(xhrobj, textStatus){
			var resInfo = $.parseJSON(xhrobj.responseText);
			if (resInfo.errors.length > 0) {
				// 共通出错信息框
				treatBackMessages(null, resInfo.errors);
			} else {
				$("#device_backup_edit").dialog("close");
				if (typeof(findit) === "function") findit();
			}
		}
	});
}

var dbMakeReport = function() {
	$.ajax({
		beforeSend : ajaxRequestType,
		async : true,
		url : dbServicePath + '?method=makeReport',
		cache : false,
		data : null,
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : function(xhrobj, textStatus){
			var resInfo = $.parseJSON(xhrobj.responseText);
			if (resInfo.errors.length > 0) {
				// 共通出错信息框
				treatBackMessages(null, resInfo.errors);
			} else {
				if ($("iframe").length > 0) {
					$("iframe").attr("src", dbServicePath+"?method=export&filePath=" + resInfo.filePath);
				} else {
					var iframe = document.createElement("iframe");
		            iframe.src = dbServicePath+"?method=export&filePath=" + resInfo.filePath;
		            iframe.style.display = "none";
		            document.body.appendChild(iframe);
				}
			}
		}
	});
}

var loadOtherDT = function(){
	var otherDevicesTypeId = $("#st_device_type_id").val();
	if (!otherDevicesTypeId) return;

	$.ajax({
		beforeSend : ajaxRequestType,
		async : true,
		url : 'devices_manage.do?method=search',
		cache : false,
		data : {devices_type_id : otherDevicesTypeId, status : '1,4,5'},
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : function(xhrobj, textStatus){
			var resInfo = $.parseJSON(xhrobj.responseText);
			if (resInfo.errors.length > 0) {
				// 共通出错信息框
				treatBackMessages(null, resInfo.errors);
			} else {
				var $piresBody = $("#pires tbody");
				var devicesManageForms = resInfo.devicesManageForms;

				var line_name = $("#set_line_name").text();
				var manage_code = $("#set_manage_code").text();

				$piresBody.append(drawPiresBody(manage_code, line_name, devicesManageForms, true));
			}
		}
	});
}