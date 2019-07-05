var djrEditServicePath = "device_jig_repair_record.do";

var consumHtml = "<input type=\"button\" value=\"选择备件\" class=\"djr_consum_button\">";

var showDjrEdit = function(device_jig_repair_record_key, check_unqualified_record_key){

	var detailMethod = "showDetail";
	if (!device_jig_repair_record_key) {
		detailMethod = "doCheckUnqualified"
	}

	var postData = {
		device_jig_repair_record_key : device_jig_repair_record_key,
		check_unqualified_record_key : check_unqualified_record_key
	}

	$.ajax({
		beforeSend : ajaxRequestType,
		async : false,
		url : djrEditServicePath + '?method=' + detailMethod,
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
				var retForm = resInfo.retForm;
				var $dialogDjrEdit = $("#device_jig_repair_edit");
				if ($dialogDjrEdit.length == 0) {
					$("body").append("<div id='device_jig_repair_edit'/>");
					$dialogDjrEdit = $("#device_jig_repair_edit");
				}

				$dialogDjrEdit.load("device_jig_repair_record.do?method=detail", function(responseText, textStatus, XMLHttpRequest) {
					var djrrKey = retForm.device_jig_repair_record_key;
					var objectType = retForm.object_type || 9;
					var repairCompleted = retForm.repair_complete_time;

					$dialogDjrEdit.find("#djr_line_name").text(retForm.line_name || "");
					$dialogDjrEdit.find("#djr_submitter_name").text(retForm.submitter_name);
					$dialogDjrEdit.find("#djr_submit_time").text(retForm.submit_time);
					$dialogDjrEdit.find("#djr_manage_code").text(retForm.manage_code || "").attr("key", djrrKey);
					$dialogDjrEdit.find("#djr_object_name").text(retForm.object_name || "");
					$dialogDjrEdit.find("#djr_model_name").text(retForm.model_name || "");
					$dialogDjrEdit.find("#djr_phenomenon").val(retForm.phenomenon);
					$dialogDjrEdit.find("#djr_maintainer_name").text(retForm.maintainer_name)
						.attr("ids", retForm.maintainer_id || "");
					$dialogDjrEdit.find("#djr_fault_causes").val(retForm.fault_causes || "");
					$dialogDjrEdit.find("#djr_countermeasure").val(retForm.countermeasure || "");
					$dialogDjrEdit.find("#djr_comment").val(retForm.comment || "");
					$dialogDjrEdit.find("#djr_consumable").val(retForm.comment || "");
					if (repairCompleted) {
						$dialogDjrEdit.find("#djr_phenomenon, #djr_fault_causes").attr("readonly", true).disable();
						$("#djr_delphotobutton").hide();
					} else {
						$("#djr_delphotobutton").click(function(){
							var postData = {device_jig_repair_record_key : djrrKey};
	
							$("#djr_photo").hide();
							$.ajax({
								beforeSend : ajaxRequestType,
								async : false,
								url : djrEditServicePath + '?method=delImage',
								cache : false,
								data : postData,
								type : "post",
								dataType : "json",
								success : ajaxSuccessCheck,
								error : ajaxError
							})
						});
					}

					$dialogDjrEdit.find("input.ui-button").button();

					$("#djr_cancelbutton").click(function(){
						$dialogDjrEdit.dialog("close");
					});

					$('#djr_uploadphotobutton').click(function(){
						$('#djr_update_photo').click();
					});

					$("#djr_submitbutton").click(function(){
						if (!repairCompleted) {
							warningConfirm("修理是否完成？", 
								function(){doDjrUpdate(djrrKey, true);},
								function(){doDjrUpdate(djrrKey, false);},
								null, "修理完成", "暂未完成"
								);
						} else {
							doDjrUpdate(djrrKey, false);
						}
					});


					$("#djr_photo").on("error", function(){
						$("#djr_photo").hide();
					});

					$("#djr_photo").show()
						.attr("src", "http://" + document.location.hostname + "/photos/dj_repair/" + djrrKey + "?_s=" + new Date().getTime());
					if (typeof(showPhoto)==="function") {
						$("#djr_photo").css("cursor", "pointer").click(showPhoto);
					}

					$("#djr_update_photo").parent().on("change", "#djr_update_photo", callDjrUploadPhoto);

					$dialogDjrEdit.dialog({
						title : "设备工具维修",
						width : 1080,
						height : 'auto',
						resizable : false,
						modal : true
					});
				});
			}
		}
	});
}

var doDjrUpdate = function(key, isRepairFinish) {

	var phenomenon = $("#djr_phenomenon").val();
	var fault_causes = $("#djr_fault_causes").val();

	var postData = {device_jig_repair_record_key : key,
		maintainer_id : $("#djr_maintainer_name").attr("ids"),
		object_name : $("#djr_object_name").text(),
		countermeasure : $("#djr_countermeasure").val(),
		comment : $("#djr_comment").val(),
		consumable : $("#djr_consumable").val()
	}

	if (isRepairFinish) {
		if (!phenomenon) {
			errorPop("请输入现象描述。");
			return;
		}
		if (!fault_causes) {
			errorPop("请输入故障原因。");
			return;
		}
		postData.repair_complete_time = '2000-01-01 01:01:01';
	}

	if (!$("#djr_phenomenon").attr("disabled")) {
		postData.phenomenon = phenomenon;
	}
	if (!$("#djr_fault_causes").attr("disabled")) {
		postData.fault_causes = fault_causes;
	}

	$.ajax({
		beforeSend : ajaxRequestType,
		async : false,
		url : djrEditServicePath + '?method=doRepair',
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
				if(typeof(findit) === "function") findit();
				$("#device_jig_repair_edit").dialog('close');
			}
		}
	})

}

var callDjrUploadPhoto = function(){
	if(typeof($.ajaxFileUpload)==="undefined") {
		loadJs(
			"js/ajaxfileupload.js",
			djrUploadPhoto
		);
	} else {
		djrUploadPhoto();
	}
}

var djrUploadPhoto = function(){

	var djrr_key = $("#djr_manage_code").attr("key");
	$.ajaxFileUpload({
		url : djrEditServicePath + "?method=sourceImage", // 需要链接到服务器地址
		secureuri : false,
		data: {device_jig_repair_record_key : djrr_key},
		fileElementId : 'djr_update_photo', // 文件选择框的id属性
		dataType : 'json', // 服务器返回的格式
		success : function(responseText, textStatus) {
			var resInfo = $.parseJSON(responseText);	

			if (resInfo.errors.length > 0) {
				// 共通出错信息框
				treatBackMessages(null, resInfo.errors);
			} else {
				$("#djr_update_photo").val("");
				
				$("#djr_photo").show()
					.attr("src", "http://" + document.location.hostname + "/photos/dj_repair/" + djrr_key + "?_s=" + new Date().getTime()).show();
			}
		}
	});
}