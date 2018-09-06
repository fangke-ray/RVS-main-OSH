var chosedPos = {};

var popMessageDetail = function(message_id, is_modal){
	if (is_modal == null) is_modal = true;

	var this_dialog = $("#nogood_treat");
	if (this_dialog.length === 0) {
		$("body.outer").append("<div id='nogood_treat'/>");
		this_dialog = $("#nogood_treat");
	}

	this_dialog.html("");
	this_dialog.hide();
	// 导入详细画面
	this_dialog.load("alarmMessage.do?method=detail&alarm_messsage_id=" + message_id , function(responseText, textStatus, XMLHttpRequest) {
		this_dialog.dialog({
			position : [400, 20],
			title : "警报详细画面",
			width : 'auto',
			show : "",
			height :  'auto',
			resizable : false,
			modal : is_modal,
			buttons : null
		});
	});

	this_dialog.show();
}

var popPostDetail = function(message_id, is_modal){
	if (is_modal == null) is_modal = true;

	var this_dialog = $("#post_confirm");
	if (this_dialog.length === 0) {
		$("body.outer").append("<div id='post_confirm'/>");
		this_dialog = $("#post_confirm");
	}

	this_dialog.html("");
	this_dialog.hide();
	// 导入详细画面
	this_dialog.load("header.do?method=detailPost&post_message_id=" + message_id , function(responseText, textStatus, XMLHttpRequest) {
		this_dialog.dialog({
			position : [400, 20],
			title : "通知详细画面",
			width : 'auto',
			show : "",
			height :  'auto',
			resizable : false,
			modal : is_modal,
			buttons : null
		});
	});

	this_dialog.show();
}

/*
* 取得流程信息返回
*/
var getFlowchart_handleComplete = function(xhrobj, textStatus, callback) {
	var resInfo = null;
	try {
		// 以Object形式读取JSON
		eval('resInfo =' + xhrobj.responseText);

		if (resInfo.errors.length > 0) {
			// 共通出错信息框
			treatBackMessages(null, resInfo.errors);
		} else {

			chosedPos = {};

			if ($("#rework_pat_id").attr("neo")) {
				$("#rework_pat_id").removeAttr("neo");
				$("#hdn_pat_id").val(resInfo.pat_id);
				$("#rework_pat_id").val(resInfo.pat_id).trigger("change");
			}

			$("#pa_red").html("");
			$("#pa_red").flowchart({},{editable:false, selections: resInfo.positions});
			$("#pa_red").flowchart("fill", resInfo.processAssigns);

			// 小修理工位
			if (resInfo.isLightFix) {
				$("#light_fix_content").text(resInfo.light_fix_content).parent().show();
				if (resInfo.isCcdModel)
					// 增加302工位选择  
					$("#pa_red").prepend('<div class="edgeposition"><div class="just"><div code="25" posid="25" nextcode="0" prevcode="0" class="pos"><span>302 CCD 盖玻璃更换</span></div></div>');
				if (resInfo.isLgModel)
					// 增加303工位选择  
					$("#pa_red").prepend('<div class="edgeposition"><div class="just"><div code="60" posid="60" nextcode="0" prevcode="0" class="pos"><span>303 LG 玻璃更换</span></div></div>');

				$("#pa_red div.pos").each(function(index,ele){
					var $div = $(ele);
					var code = $div.attr("code");
					var $span = $div.find("span");
					for(var i=0;i<resInfo.mProcessAssigns.length;i++){
						var obj = resInfo.mProcessAssigns[i];
						if(code == obj.position_id && !$span.hasClass("suceed")){
							$span.addClass("point");
							chosedPos[code]=1;
						}
					}
				});

				$("#ref_allpos").button().click(function(){
					$(".pos span").each(function(idx, ele){
						$span = $(ele);
						var pos_id = $span.parent().attr("code");
						if ($span.hasClass("rework")) {
						} else {
							$span.addClass("rework");
							chosedPos[pos_id] = 1;
						}
					});
				});
			} else {
				$("#light_fix_content").text("").parent().hide();
			}

			$("#pa_red span:empty").parent().parent().parent().each(function(){
				$(this).hide();
				if ($(this).parent().hasClass("pos")) {
					$(this).parent().hide();
				}
			});
			var resultlen = resInfo.result.length;
			for (var iresult = 0 ; iresult < resultlen ; iresult++) {
				var productionFeature = resInfo.result[iresult];
				$("#pa_red div.pos[code="+ parseInt(productionFeature.position_id, 10) +"]").find("span").addClass("suceed")
					.after("<div class=\"feature_result\">"+productionFeature.operator_name+"<br>"+productionFeature.finish_time+"</div>");
			}
			$("#pa_red div.pos[code="+ parseInt(selectedMaterial.position_id, 10) +"]").find("span").addClass("nogood");

			if (resInfo.isLightFix) {
				// 小修理不能进行工程内返工
				if ($("#rework_pat_id").length > 0) {
					$("#pa_red span").click(function() {
						var mespan = $(this);
		
						if (mespan.hasClass("rework")) {
							mespan.removeClass("rework");
						} else {
							mespan.addClass("rework");
						}
					});
				}
			} else {
				$("#pa_red span.suceed").click(function() {
					var mespan = $(this);
	
					if (mespan.hasClass("rework")) {
						mespan.removeClass("rework");
					} else {
						mespan.addClass("rework");
					}
				});
			}

			if (callback) callback();
		}

		$("#process_resign").dialog({
			// position : [ 800, 20 ],
			title : "不良信息及处置",
			width : 'auto' ,
			show : "blind",
			height : 620,// 'auto' ,
			resizable : false,
			modal : true,
			minHeight : 200,
			close : function() {
				$("#process_resign").html("");
			},
			buttons : {
				"确定" : function() {
					var data = {
						material_id : selectedMaterial.material_id,
						position_id : selectedMaterial.position_id,
						alarm_messsage_id : selectedMaterial.alarm_messsage_id,
						comment : selectedMaterial.comment,
						append_parts : $("#flowcase input[name='append_parts']:checked").val()
					};

					var reworkPositions = $("#pa_red span.rework").parent();
					if (reworkPositions.length > 0) {
					for (var iReworkPositions = 0; iReworkPositions<reworkPositions.length; iReworkPositions++) {
						var reworkPosition = reworkPositions[iReworkPositions];
						data["rework.positions["+iReworkPositions+"]"] = $(reworkPosition).attr("code");
					}
					}

					// 同时修改维修路径
					if ($("#rework_pat_id").length > 0) {
						if ($("#hdn_pat_id").val() != $("#rework_pat_id").val()) {
							data.pat_id = $("#rework_pat_id").val();
						}
						if ($("#reworked_y").attr("checked") && !data["rework.positions[0]"]) {
							data["rework.positions[0]"] = $("#pa_red span.nogood").parent().attr("code");
						}
					} else {
						// 线长线内必返工位
						if (!data["rework.positions[0]"]) {
							data["rework.positions[0]"] = $("#pa_red span.nogood").parent().attr("code");
						}
					}

					// 记载到工程检查票
					if ($("#flowcase input[name=pcs_signed]").length > 0) {
						if ($("#pcs_signed_y").attr("checked")) {
							data["pcs_signed"] = "true";
						}
					}

					// 需要追加零件
//					if ($("#flowcase input[name=append_parts]").length > 0) {
//						if ($("#append_parts_y").attr("checked")) {
//							data["append_parts"] = "true";
//						}
//					}

					// 小修理修改过流程
					if (data.pat_id || // 只是改过参考流程也需要重排
						($("#flowcase div.pos span.point").length > 0
						&& $("#flowcase div.pos span.rework").not(".point").length > 0)) {

						var count=0;
						var chainHead = null;
						var chain = {};
						var total = $("#flowcase div.pos span").length;

						$("#flowcase div.pos span").each(function(index,ele){
							var $span = $(ele);
							if($span.hasClass("point") || $span.hasClass("rework")){
								var $posData = $span.parent();
								var code = $posData.attr("code");
								if (code != "25") {
									var item = {code: code};
									if ($posData.attr("prevcode") == "0" && $posData.parents(".just-multi").length == 0) {
										chainHead = item;
									}
									item["prev"] = getPrevPos($posData);
									chain[code] = item;
								}
								data["material_process_assign.position_id[" + count + "]"] = code;
								if (count > 0) {
									data["material_process_assign.next_position_id[" + (count-1) + "]"] = code;
								}
								if (count < (total - 1)) {
									data["material_process_assign.prev_position_id[" + (count+1) + "]"] = code;
								}
								count++;
							}
						});
					}

					// Ajax提交
					$.ajax({
						beforeSend : ajaxRequestType,
						async : false,
						url : 'alarmMessage.do?method=dorework',
						cache : false,
						data : data,
						type : "post",
						dataType : "json",
						success : ajaxSuccessCheck,
						error : ajaxError,
						complete : function(xhrObj, textStatus) {
							var resInfo = $.parseJSON(xhrObj.responseText);
							if (resInfo.errors && resInfo.errors.length > 0) {
								treatBackMessages(null, resInfo.errors);
							} else {
								$("#process_resign").dialog('close');
								$("#alarm_" + $("#nogood_id").val()).hide("slide");
								try {
								if (refreshList) refreshList();
								}catch(e){
								}
							}
						}
					});
				},
				"重置" : function() {
					$("#process_resign .rework").removeClass("rework");
					$("#flowcase input[name=reworked]").enable().trigger("change");
				}, "关闭" : function(){ $(this).dialog("close"); }
			}
		});
		$("#process_resign").show();

	} catch (e) {
		console.log("name: " + e.name + " message: " + e.message + " lineNumber: "
				+ e.lineNumber + " fileName: " + e.fileName);
	};
}

var process_resign = function() {
	$("#process_resign").hide();
	// µ¼Èë·µ¹¤»­Ãæ
	$("#process_resign").load("widget.do?method=rework", function(responseText, textStatus, XMLHttpRequest) {

		$("#rework_sorc_no").text(selectedMaterial.sorc_no);
		$("#rework_model_name").text(selectedMaterial.model_name);
		$("#rework_serial_no").text(selectedMaterial.serial_no);
		$("#rework_line_name").text(selectedMaterial.line_name);

		$("#flowcase input[name='append_parts'][value=" + (selectedMaterial.append_parts || 0) + "]")
			.attr("checked", true).trigger("change");

		// AjaxÌá½»
		$.ajax({
			beforeSend : ajaxRequestType,
			async : true,
			url : 'material.do?method=getFlowchart',
			cache : false,
			data : {material_id : selectedMaterial.material_id},
			type : "post",
			dataType : "json",
			success : ajaxSuccessCheck,
			error : ajaxError,
			complete : getFlowchart_handleComplete
		});
	});
};

function getPrevPos($posData){
	var posid = $posData.attr("posid");
	var code = $posData.attr("code");
	var prevcode = $posData.attr("prevcode");
	var nextcode = $posData.attr("nextcode");
	if (code == null) return null;

	if (posid == code) {
		if (prevcode == "0") {
			var $justMulti = $posData.parents(".just-multi:first");
			if ($justMulti.length == 0) {
				return "0";
			} else {
				return getPrevPos($justMulti.parent().parent());
			}
		}
		var $thePrev = $("#pa_main").find(".div[code="+code+"]");
		if ($thePrev.children("span").hasClass("point") || $thePrev.children("span").hasClass("suceed")) {
			return $thePrev.attr("code");
		} else {
			return getPrevPos($thePrev);
		}
	}
	return null;
}