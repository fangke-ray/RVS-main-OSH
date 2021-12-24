/** 一览数据对象 */
var listdata = {};

/** 服务器处理路径 */
var servicePath = "position_panel" + (parseInt(Math.random() * 5) + 1) + ".do";
var hasPcs = (typeof pcsO === "object");

// 定时处理对象
var wtInterval;

var pauseOptions = "";
var pauseComments = {};
var breakOptions = "";
var stepOptions = "";
var dryProcesses = "";

var partial_closer = true;

var isDivision = false;
var device_safety_guide = {};

/** 中断信息弹出框 */
var makeBreakDialog = function(jBreakDialog) {
	jBreakDialog.dialog({
		title : "中断生成",
		width : 480,
		show: "blind",
		height : 'auto' ,
		resizable : false,
		modal : true,
		minHeight : 200,
		close : function(){
			jBreakDialog.html("");
		},
		buttons : {
			"确定":function(){
				if ($("#breakForm").valid()) {
					var data = {
						reason : $("#break_reason").val(),
						comments : $("#edit_comments").val()
					}

					if (hasPcs) {
						pcsO.valuePcs(data, true);
					}

					// 烘干
					if ($("#break_reason").val() == 99) {
						var $drying_job_tr = jBreakDialog.find("tr.ui-state-highlight");
						if ($drying_job_tr.length > 0) {
							data.drying_job_id = $drying_job_tr.attr("drying_job_id");
							data.device_manage_id = $drying_job_tr.children("td[device_manage_id]").attr("device_manage_id");
							data.slot = jBreakDialog.find(".slot_select").val();
							if (!data.slot && jBreakDialog.find(".slot_select option").length > 0) {
								errorPop(WORKINFO.chooseDryingProcessStock);
								return;	
							}
						} else {
							errorPop(WORKINFO.chooseDryingProcess);
							return;	
						}
					}

					if (parseInt($("#break_reason").val()) > 70 && $("#pcs_contents input").length > 0) {
						var $break_confirm = $('div#break_confirm');
						if ($break_confirm.length == 0) {
							$("body").append("<div id='break_confirm'/>");
							$break_confirm = $('div#break_confirm');
						}
						$break_confirm.dialog({
							dialogClass : 'ui-warn-dialog',
							modal : true,
							width : 450,
							title : "提示信息",
							buttons :{
								"确定":function(){
									// Ajax提交
									$.ajax({
										beforeSend : ajaxRequestType,
										async : false,
										url : servicePath + '?method=dobreak',
										cache : false,
										data : data,
										type : "post",
										dataType : "json",
										success : ajaxSuccessCheck,
										error : ajaxError,
										complete : function(xhrobj, textStatus) {
											doFinish_ajaxSuccess(xhrobj, textStatus);
											try {
												// 以Object形式读取JSON
												eval('resInfo =' + xhrobj.responseText);
												if (resInfo.errors.length !== 0) {
													// 共通出错信息框
													treatBackMessages(null, resInfo.errors);
												} else {
													getWaitings();
													jBreakDialog.dialog("close");
												}
											} catch (e) {
												alert("name: " + e.name + " message: " + e.message + " lineNumber: "
														+ e.lineNumber + " fileName: " + e.fileName);
											};
										}
									});
									$break_confirm.dialog("close");
								},
								"取消":function(){
									$break_confirm.dialog("close");
								}
							}
						});
						$break_confirm.html("<span class='errorarea'>" + WORKINFO.confirmPcsWhenBreak + "</span>");
					} else {
						// Ajax提交
						$.ajax({
							beforeSend : ajaxRequestType,
							async : false,
							url : servicePath + '?method=dobreak',
							cache : false,
							data : data,
							type : "post",
							dataType : "json",
							success : ajaxSuccessCheck,
							error : ajaxError,
							complete : function(xhrobj, textStatus) {
								doFinish_ajaxSuccess(xhrobj, textStatus);
								try {
									// 以Object形式读取JSON
									eval('resInfo =' + xhrobj.responseText);
									if (resInfo.errors.length === 0) {
										getWaitings();
										jBreakDialog.dialog("close");
									}
								} catch (e) {
									alert("name: " + e.name + " message: " + e.message + " lineNumber: "
											+ e.lineNumber + " fileName: " + e.fileName);
								};
							}
						});
					}
				}
			}, "关闭" : function(){ $(this).dialog("close"); }
		}
	});
}

/** 正常中断生成 */
var makeStep = function() {

	var jBreakDialog = $("#break_dialog");
	if (jBreakDialog.length === 0) {
		$("body.outer").append("<div id='break_dialog'/>");
		jBreakDialog = $("#break_dialog");
	}

	jBreakDialog.hide();

	// 导入中断画面
	jBreakDialog.load("widget.do?method=breakoperator",
		function(responseText, textStatus, XMLHttpRequest) {

		// 设定中断理由
		var $break_reason = $("#break_reason");
		var allStepOptions = stepOptions + (dryProcesses ? ("<option value=\"99\">烘干作业 》</option>") : "")
		$break_reason.html(allStepOptions).select2Buttons();
		$break_reason.change(function() {
			if ($break_reason.val() == "99") {
				jBreakDialog.dialog( "option", "width", 800 );
				jBreakDialog.find(".ui-state-highlight").removeClass("ui-state-highlight");
				$("#breakForm .drying_jobs").show();
				$("#breakForm .drying_slots").hide();
			} else {
				jBreakDialog.dialog( "option", "width", 480 );
				$("#breakForm .drying_jobs, #breakForm .drying_slots").hide();
			}
		});

		// 烘干
		$("#breakForm .drying_jobs").hide()
			.children("td:eq(1)").html(dryProcesses)
			.find("tr").click(function(){
				var $tr = $(this);
				var drying_job_id = $tr.attr("drying_job_id");
				jBreakDialog.find(".ui-state-highlight").removeClass("ui-state-highlight");
				$tr.addClass("ui-state-highlight");
				var device_manage_id = $tr.children("td[device_manage_id]").attr("device_manage_id");
				if(device_manage_id) {
					// Ajax提交
					$.ajax({
						beforeSend : ajaxRequestType,
						async : false,
						url : 'drying_process.do?method=getSlotsByDevice',
						cache : false,
						data : {device_manage_id : device_manage_id, status : "self"},
						type : "post",
						dataType : "json",
						success : ajaxSuccessCheck,
						error : ajaxError,
						complete : function(xhr, status) {
					    	var resInfo = $.parseJSON(xhr.responseText);
					    	if (resInfo.slotsByDevice && resInfo.slotsByDevice.length) {
					    		var slotsByDevice = resInfo.slotsByDevice[0];
					    		var slot_count = slotsByDevice.slot;
					    		var using_slots = slotsByDevice.using_slots;
					    		var qualified_slots = $tr.children("td[slots]").attr("slots");
					    		var $slotSelect = $("<select class='slot_select'/>");
					    		for (var islot = 1; islot <= slot_count; islot++) {
					    			$slotSelect.append("<option value = "+ islot +">" + islot + "</option>");
					    		}
					    		if (using_slots) {
					    			var using_slot_array = using_slots.split(",");
					    			for (var iUsa in using_slot_array) {
					    				var using_slot = using_slot_array[iUsa];
										$slotSelect.children("option[value=" + using_slot + "]").attr("disabled", true);
					    			}
					    		}
					    		if (qualified_slots) {
					    			var qualified_slot_array = qualified_slots.split(",");
					    			for (var iQsa in qualified_slot_array) {
					    				var qualified_slot = qualified_slot_array[iQsa];
										$slotSelect.children("option[value=" + qualified_slot + "]").addClass("qualified_slot");
					    			}
					    			$slotSelect.children("option").not(".qualified_slot").remove();
					    		}
					    		var $usables = $slotSelect.children("option").not("[disabled]");
					    		if ($usables.length > 0) {
						    		$usables.eq(0).attr("selected", true);
					    		} else {
					    			$slotSelect.val("");
					    		}

					    		$("#breakForm .drying_slots").show()
					    			.children("td:eq(1)").html($slotSelect)
					    			.children("select").select2Buttons();
					    	}
	    				}
					});
				} else {
		    		$("#breakForm .drying_slots").hide();
				}
			});

		$("#breakForm").validate({
			rules : {
				break_reason : {
					required : true
				}
			}
		});
		makeBreakDialog(jBreakDialog);

	});
	$("#break_dialog").show();
};

/** 不良中断生成 */
var makeBreak = function() {

	var jBreakDialog = $("#break_dialog");
	if (jBreakDialog.length === 0) {
		$("body.outer").append("<div id='break_dialog'/>");
		jBreakDialog = $("#break_dialog");
	}

	jBreakDialog.hide();

	// 导入中断画面
	jBreakDialog.load("widget.do?method=breakoperator",
		function(responseText, textStatus, XMLHttpRequest) {

		// 设定中断理由
		$("#break_reason").html(breakOptions);
		$("#break_reason").select2Buttons();

		$("#breakForm").validate({
			rules : {
				break_reason : {
					required : true
				},
				comments : {
					required : function() {
						return ($("#break_reason").val() != null && $("#break_reason").val() != "" && parseInt($("#break_reason").val()) < 10);
					}
				}
			}
		});
		makeBreakDialog(jBreakDialog);

	});
	$("#break_dialog").show();
};

/** 暂停信息弹出框 */
var makePauseDialog = function(jBreakDialog) {
	jBreakDialog.dialog({
		title : "暂停信息编辑",
		width : 760,
		show: "blind",
		height : 'auto' ,
		resizable : false,
		modal : true,
		minHeight : 200,
		close : function(){
			jBreakDialog.html("");
		},
		buttons : {
			"确定":function(){
				if ($("#pauseo_edit").parent().valid()) {
					var data = {
						reason : $("#pauseo_edit_pause_reason").val(),
//						comments : $("#pauseo_edit_comments").val(),
						workstauts : $("#hidden_workstauts").val()
					};

					if ($("#pauseo_edit_comments").is(":hidden")) {
						data.comments = $("#pauseo_edit_comments_select").val();
					} else {
						data.comments = $("#pauseo_edit_comments").val()
					}

					// Ajax提交
					$.ajax({
						beforeSend : ajaxRequestType,
						async : false,
						url : servicePath + '?method=dopause',
						cache : false,
						data : data,
						type : "post",
						dataType : "json",
						success : ajaxSuccessCheck,
						error : ajaxError,
						complete : function(xhr, status) {
							jBreakDialog.dialog("close");
							doFinish_ajaxSuccess(xhr, status);
						}
					});
				}
			}, "关闭" : function(){ $(this).dialog("close"); }
		}
	});
}

/** 暂停信息 */
var makePause = function() {
	var jBreakDialog = $("#break_dialog");
	if (jBreakDialog.length === 0) {
		$("body.outer").append("<div id='break_dialog'/>");
		jBreakDialog = $("#break_dialog");
	}

	jBreakDialog.hide();
	// 导入暂停画面
	jBreakDialog.load("widget.do?method=pauseoperator",
		function(responseText, textStatus, XMLHttpRequest) {
			// 设定暂停理由
			$("#pauseo_edit").show();
			$("#pauseo_show").hide();
			$("#pauseo_edit_pause_reason").html(pauseOptions);
			$("#pauseo_edit_pause_reason").select2Buttons().change(function(){
				var thisval = this.value;
				if (pauseComments[thisval]) {
					$("#pauseo_edit_comments_select")
					.next(".select2Buttons").remove()
					.end()
					.show().html(createCommentOptions(pauseComments[thisval]))
					.select2Buttons();
					$("#pauseo_edit_comments").hide();
				} else {
					$("#pauseo_edit_comments_select").hide()
					.next(".select2Buttons").remove()
					.end()
					.html("");
					$("#pauseo_edit_comments").show();
				}
			});

			$("#pauseo_edit_pause_reason").val("100").trigger("change");
			$("#pauseo_edit").parent().validate({
				rules : {
					comments : {
						required : function() {
							return "100" === $("#pauseo_edit_pause_reason").val();
						}
					}
				}
			});
			makePauseDialog(jBreakDialog);
	});
};

var createCommentOptions = function(comments){
	var commentArray = comments.split(";");
	var sRet = "";
	for (var commentIdx in commentArray) {
		var comment = commentArray[commentIdx];
		sRet += "<option value='" + comment + "'>" + comment + "</option>";
	}
	return sRet;
}

/** 暂停重开 */
var endPause = function() {

	// 作业等待时间
	if ($(".opd_re_comment").length > 0) {
		var pause_start_time = new Date($(".opd_re_comment").attr("pause_start_time"));
		var leak = $(".opd_re_comment").attr("leak");
		if((leak && leak != "0") && !header_today_holiday 
			&& new Date().getTime() - pause_start_time.getTime() > 179999) {
			errorPop(WORKINFO.needFillBreak);
			return;
		}
	}

	// 重新读入刚才暂停的维修对象
	var data = {
		material_id : $("#pauseo_material_id").val(),
		workstauts : $("#hidden_workstauts").val()
	}

	// 无论如何关闭暂停窗口
	if ($("#break_dialog").html() != "") {
		$("#break_dialog").dialog("close");
	}

	// Ajax提交
	$.ajax({
		beforeSend : ajaxRequestType,
		async : false,
		url : servicePath + '?method=doendpause',
		cache : false,
		data : data,
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : doStart_ajaxSuccess
	});
};

var treatPause = function(resInfo) {
	$("#scanner_container").hide();
	$("#material_details").show();
	$(".other_px_change_button").disable();
	$("#position_status").text("暂停中")
		.css("background-color", "#0080FF");
	$(".opd_re_comment").show();
	$("#pausebutton").hide();
	$("#finishbutton").disable();
	$("#breakbutton").disable();
	$("#stepbutton").disable();
//	$("#usesnoutbutton").disable();
	$("#unusesnoutbutton").disable();
	$("#continuebutton").show();

	if (resInfo) {
		var $hidden_id = $("#material_details td:eq(0) input:hidden");
		$hidden_id.val(resInfo.mform.material_id);
		$("#anml_attendtion").remove();
		if (resInfo.mform.anml_exp) {
			$hidden_id.before("<attendtion id='anml_attendtion'></attendtion>");
		}

		$("#material_details td:eq(1)").text(resInfo.mform.sorc_no);
		$("#material_details td:eq(3)").text(resInfo.mform.model_name);
		$("#material_details td:eq(5)").text(resInfo.mform.serial_no);

		posClockObj.setAction(resInfo.action_time);

		posClockObj.setLeagalAndSpent(resInfo.leagal_overline, resInfo.spent_mins, resInfo.spent_secs);
	
		$("#working_detail").show()
			.next().hide();

		if (resInfo.peripheralData && resInfo.peripheralData.length > 0) {
			showPeripheral(resInfo);
		}

		$("#device_details table tbody").find(".manageCode").disable();
		$("#device_details table tbody").find("input[type=button]").disable();
		$("#finishcheckbutton").disable();

		if (resInfo.workstauts != 5) {
			// 工程检查票
			if (resInfo.pcses && resInfo.pcses.length > 0 && hasPcs) {
				pcsO.generate(resInfo.pcses);
			}
		}

		if (typeof(posUseSnoutObj) === "object" && resInfo.mform && resInfo.mform.material_id) 
			posUseSnoutObj.getUsesnout(resInfo.mform.material_id);

		if (resInfo.processingPauseStart) {
			setPauseClock(resInfo.processingPauseStart);
		}
	}

	posClockObj.pauseClock();
}

var treatStart = function(resInfo) {

	$("#scanner_inputer").attr("value", "");
	$("#scanner_container").hide();
	$("#material_details").show();
	$(".other_px_change_button").disable();
	with($("#position_status")) {
		if (hasClass("simple")) {
			text("处理中");
		} else {
			text("修理中");
		}
		css("background-color", "#58b848");
	}
	$(".opd_re_comment").hide();
	clearInterval(wtInterval);
	if ($pause_clock != null) $pause_clock.text("");

	if (resInfo.breakOptions) breakOptions = resInfo.breakOptions;
	if (resInfo.stepOptions) stepOptions = resInfo.stepOptions;
	if (!stepOptions && !dryProcesses) {
		$("#stepbutton").hide();
	} else {
		$("#stepbutton").show();
	}

	$("#anml_attendtion").remove();
	var $hidden_id = $("#material_details td:eq(0) input:hidden");
	$hidden_id.val(resInfo.mform.material_id);
	if (resInfo.mform.anml_exp && $("#manualdetailarea").length > 0) { // 虚拟工位不显 专用工位不显
		if ($("#device_rent_area").length == 0) {
			infoPop(WORKINFO.animalExpNotice);
		}
		$hidden_id.before("<attendtion id='anml_attendtion'></attendtion>");
	}

	$("#material_details td:eq(1)").text(resInfo.mform.sorc_no);
	$("#material_details td:eq(3)").text(resInfo.mform.model_name);
	$("#material_details td:eq(5)").text(resInfo.mform.serial_no);

	posClockObj.setAction(resInfo.action_time);

	posClockObj.setLeagalAndSpent(resInfo.leagal_overline, resInfo.spent_mins);

	posClockObj.startClock(resInfo.spent_mins);

	posClockObj.recountTopClock();

	$("#working_detail").show()
		.next().show();

	var section_id = $("#g_pos_id").val().substring(0,11);
//	if (section_id == "00000000001") {
//		if (!$("#position_time").is(":visible")) {
//			$("#position_time").show();
//			$("#plan_process").find("td:gt(1)").remove();
//			$("#process_code_title,#acture_process").find("td:gt(0)").remove();
//	
//			if (resInfo.isExistInPlan == true) {		
//				var title="";
//				var p_process = "";
//				var positionPlanTimeList = resInfo.positionPlanTimeList;
//				for(var i = 0;i<positionPlanTimeList.length;i++){
//					var process_code = positionPlanTimeList[i].process_code;//工位代码
//					title += '<td>' + process_code +'</td>';
//					p_process += '<td process_code="' + process_code + '"></td>';
//				}
//				$("#process_code_title").append(title);
//				$("#plan_process").append(p_process);
//				$("#acture_process").append(p_process);
//				
//				refreshPositionTime(resInfo);
//				setInterval(function(){
//					refreshPositionTime(resInfo);
//				},iInterval);
//			} else {
//				var cur_process_code = $("#g_process_code").val();
//				var title = '<td>' + cur_process_code +'</td>';
//				var p_process = '<td process_code="' + cur_process_code + '"></td>';
//				$("#process_code_title").append(title);
//				$("#plan_process").append(p_process);
//				$("#acture_process").append(p_process);
//	
//				refreshCurPositionTime(cur_process_code);
//				setInterval(function(){
//					refreshCurPositionTime(cur_process_code);
//				},iInterval);
//			}
//		}
//	} else {
//		$("#p_rate").parent().parent().show();
//	}

	if ($('#partialconfirmarea:visible').length > 0) { // 
		partial_closer = false;
		$('#partialconfirmarea').dialog("close");
	}

	if ($("#sendbutton").length > 0) {
		$("#sendbutton").disable();
		try {
			if (typeof(checkProcess) === "function") checkProcess($("#skip_position").val());
		}catch(e) {
			infoPop("don't care");
		}
	}

	$("#continuebutton").hide();
	$("#breakbutton").enable();
	$("#stepbutton").enable();
	$("#pausebutton").show();
	$("#finishbutton").enable();

	$("#unusesnoutbutton").enable();
	// 3课分解无提前分支
	if (section_id == "00000000012") {
		$("#sendbutton").disable();
	}		

	$("#w_" + resInfo.mform.material_id).hide("drop", {direction: 'right'}, function() {
		var jthis = $(this);
		var jGroup = jthis.prevAll(".w_group");
		jthis.remove();
		if (jGroup.nextUntil(".w_group").length == 0) {
			jGroup.hide("fade", function() {
				jGroup.remove();
			});
		}
	});

/*	if (resInfo.material_comment) {
		$("#comments_dialog textarea").val(resInfo.material_comment).show();
		$("#comments_dialog").css({
			position:"fixed",
			right:0,
			top:"50%",
			backgroundColor:"#fff",
			color:"#000",
			width:"576px",
			transform:"translateY(-50%)",
			border:"1px solid #aaaaaa",
			opacity:"1"
		}).show();
		$("#comments_dialog span").removeClass("icon-share").addClass("icon-enter-2");
	}*/

	if (resInfo.material_comment || (device_safety_guide && device_safety_guide.length)) {
		showSidebar(resInfo.material_comment);
	} else {
		$("#comments_sidebar").hide();
	}
	
	if (resInfo.peripheralData && resInfo.peripheralData.length > 0) {
		showPeripheral(resInfo);
	}

	// New 回收
	if ($("#decsnoutarea").length) {

		$("#desnout > button").remove();
		if (resInfo.show_snout) {
			$("#decsnoutarea").show();
			$("#desnout > span").html(resInfo.show_snout);
			if (resInfo.show_snout.indexOf("[废弃]") > 0) {
				$("#desnout").append(
					$("<button>回收</button>").button().click(function(){
						// Ajax提交
						$.ajax({
							beforeSend : ajaxRequestType,
							async : true,
							url : 'snouts.do?method=doRecover',
							cache : false,
							data : {material_id : $("#pauseo_material_id").val()},
							type : "post",
							dataType : "json",
							success : ajaxSuccessCheck,
							error : ajaxError,
							complete : function(xhrObj){
								$("#desnout > button:eq(0)").after("<span> √ 已回收</span>")
								$("#desnout > button").remove();
								var resInfo = $.parseJSON(xhrObj.responseText);
								if (hasPcs && resInfo.pcses && resInfo.pcses.length > 0) {
									pcsO.append(resInfo.pcses);
								}
							}
						});
					})
				).append(
					$("<button>废弃</button>").button().click(function(){
						// Ajax提交
						$.ajax({
							beforeSend : ajaxRequestType,
							async : true,
							url : 'snouts.do?method=doAbandon',
							cache : false,
							data : {material_id : $("#pauseo_material_id").val()},
							type : "post",
							dataType : "json",
							success : ajaxSuccessCheck,
							error : ajaxError,
							complete : function(xhrObj){
								$("#desnout > button:eq(0)").after("<span> ＃ 已废弃</span>")
								$("#desnout > button").remove();
							}
						});
					})
				);
			}
		} else {
			$("#decsnoutarea").hide();
			$("#desnout > span").html("");
		}
	}

	if (resInfo.workstauts == 1) {
		$("#device_details table tbody").find(".manageCode").disable();
		$("#device_details table tbody").find("input[type=button]").disable();
		$("#finishcheckbutton").disable();
	} else {
		$("#device_details table tbody").find(".manageCode").enable();
		$("#device_details table tbody").find(".manageCode").trigger("change");
	}

	if (resInfo.workstauts == 4) {
		$("#finishbutton").disable();
		if (hasPcs) {
			pcsO.clear();
		};
	} else {
		// 工程检查票
		if (resInfo.pcses && resInfo.pcses.length > 0 && hasPcs) {
			pcsO.generate(resInfo.pcses);
		};

		if (resInfo.mform && resInfo.mform.material_id) {
			if (typeof(posUseSnoutObj) === "object" && resInfo.mform.material_id) 
				posUseSnoutObj.getUsesnout(resInfo.mform.material_id);
		}

	};
};

//var refreshPositionTime = function(resInfo){
//	var curTime = new Date().getTime();
//	var positionPlanTimeList = resInfo.positionPlanTimeList;
//	var positionActualList = resInfo.positionActualList;
//	var cur_process_code = $("#g_process_code").val();
//	var cur_process_code_end_time = 0;
//	
//	for(var item in positionPlanTimeList){
//		var plan_process_code = positionPlanTimeList[item].process_code;//工位代码
//		var plan_start_time = new Date(positionPlanTimeList[item].plan_start_time).getTime();
//		var plan_end_time = new Date(positionPlanTimeList[item].plan_end_time).getTime();
//		
//		if(plan_process_code == cur_process_code){
//			cur_process_code_start_time = plan_start_time;
//			cur_process_code_end_time = plan_end_time;
//		}
//
//		if(curTime >= plan_start_time && curTime <= plan_end_time ){
//			$("#plan_process td[process_code='"+ plan_process_code +"']").addClass("plan_half");
//		}else if(curTime > plan_end_time){
//			$("#plan_process td[process_code='"+ plan_process_code +"']").removeClass().addClass("plan_process");
//		}
//
//		for(var item_a in positionActualList){
//			var actual_process_code = positionActualList[item_a].process_code;//完成的工位代码
//			if (actual_process_code != plan_process_code) {
//				continue;
//			}
//
//			var actual_finish_time = new Date(positionActualList[item_a].finish_time).getTime();
//			if (actual_finish_time > plan_end_time) {
//				$("#acture_process td[process_code='"+ actual_process_code +"']").addClass("overtime");
//			} else {
//				$("#acture_process td[process_code='"+ actual_process_code +"']").addClass("acture_process");
//			}
//		}
//	}
//
//	$("#acture_process td[process_code='"+ cur_process_code +"']").addClass("acture_half");
//	if(curTime > cur_process_code_end_time){
//		$("#acture_process td[process_code='"+ cur_process_code +"']").removeClass().addClass("overtime_half");
//	}
//
//	$("#position_time").attr("plan", "1");
//};

var refreshCurPositionTime = function(cur_process_code){
	var overline = $("#material_details td:eq(9)").text();
	if (overline.indexOf("超出") >= 0) {
		$("#plan_process td[process_code='"+ cur_process_code +"']").removeClass().addClass("plan_process");
		$("#acture_process td[process_code='"+ cur_process_code +"']").removeClass().addClass("overtime_half");
	} else {
		$("#plan_process td[process_code='"+ cur_process_code +"']").addClass("plan_half");
		$("#acture_process td[process_code='"+ cur_process_code +"']").addClass("acture_half");
	}
	$("#scanner_inputer").focus();
	$("#position_time").attr("plan", "2");
};

var doInit_ajaxSuccess = function(xhrobj, textStatus){
	//return;
	var resInfo = null;
//	try {
		// 以Object形式读取JSON
		eval('resInfo =' + xhrobj.responseText);

		if (resInfo.errors.length > 0) {
			// 共通出错信息框
			treatBackMessages(null, resInfo.errors);

		} else {
			$("#hidden_workstauts").val(resInfo.workstauts);
			if (resInfo.workstauts == -1) {
				if (resInfo.infectString.indexOf("javascript:opd_pop") >= 0) {
					loadJs("js/data/operator-detail.js");
				}
				showBreakOfInfect(resInfo.infectString);
				return;
			}
			// 建立等待区一览
			if (resInfo.waitings) {
				showWaitings(resInfo.waitings);
			}
			if (resInfo.completes) {
				showCompletes(resInfo.completes);
			}

			posClockObj.initTopClock();

			if (resInfo.infectString) {
				$("#toInfect").show()
				.find("td:eq(1)").html(decodeText(resInfo.infectString));
			} else {
				$("#toInfect").hide();
			}

			if (resInfo.jsinitInfect) {
				setTimeout(function(){
					// Ajax提交
					$.ajax({
						beforeSend : ajaxRequestType,
						async : true,
						url : servicePath + '?method=jsinitInfect',
						cache : false,
						data : null,
						type : "post",
						dataType : "json",
						success : ajaxSuccessCheck,
						error : ajaxError,
						complete : function(xhrObj){
							var resInfoIn = $.parseJSON(xhrObj.responseText);
							if (resInfoIn.infectString) {
								$("#toInfect").show()
								.find("td:eq(1)").html(decodeText(resInfoIn.infectString));
							} else {
								$("#toInfect").hide();
							}
						}
					});
				}, 10);
			}

			// 暂停理由
			pauseOptions = resInfo.pauseOptions;
			pauseComments = $.parseJSON(resInfo.pauseComments);

			breakOptions = resInfo.breakOptions;
			stepOptions = resInfo.stepOptions;
			dryProcesses = getDryProcessesTable(resInfo.dryProcesses);
			if (!stepOptions && !dryProcesses) {
				$("#stepbutton").hide();
			} else {
				$("#stepbutton").show();
			}
			if (breakOptions == "") {
				$("#breakbutton").hide();
			}

//			if(resInfo.waitings && resInfo.waitings.length >= 1) {
//				$("#pauseo_material_id").val(resInfo.waitings[0].material_id);
//			}

			// 设备安全手册信息
			if (resInfo.position_hcsgs) device_safety_guide = resInfo.position_hcsgs;

			if (resInfo.djLoaning) {
				warningConfirm(resInfo.djLoaning);
			}

			loadJs(
			"js/data/operator-detail.js",
			function(){
				opd_load($("#workarea table td:contains('暂停时间')"),
				function(){
					// 间隔工作时间
					if (resInfo.processingPauseStart) {
						setPauseClock(resInfo.processingPauseStart);
					}

					if (resInfo.workstauts == 1 || resInfo.workstauts == 4) {
						treatStart(resInfo);
					} else if (resInfo.workstauts == 2 || resInfo.workstauts == 5) {
						treatPause(resInfo);
					} else if (resInfo.workstauts == 3) {
						showPartialRecept(resInfo);
					} else {
						$("#position_status").text("准备中")
							.css("background-color", "#0080FF");
						
						if (device_safety_guide && device_safety_guide.length) {
							showSidebar(null);
						} else {
							$("#comments_sidebar").hide();
						}
					}

					// 如果打开作业中但是没有
					var flowtext = resInfo.past_fingers;
					if (!resInfo.fingers && $("#material_details").is(":visible")) {
						getJustWorkingFingers(resInfo.mform.material_id);
					} else {
						if (resInfo.lightFix) flowtext = resInfo.lightFix + (flowtext ? "<br>" + flowtext : "");
						if (resInfo.fingers) flowtext = resInfo.fingers + (flowtext ? "<br>" + flowtext : "");
						if (flowtext) $("#flowtext").html(flowtext);
					}
				});
			});
		}
//	} catch (e) {
//		alert("name: " + e.name + " message: " + e.message + " lineNumber: "
//				+ e.lineNumber + " fileName: " + e.fileName);
//	};
};

var showBreakOfInfect = function(infectString) {
	var $break_dialog = $('#break_dialog');
	$break_dialog.html(decodeText(infectString));
	if ($break_dialog.html().indexOf("opd_pop") >= 0) {
		$break_dialog.find("span").attr("id", "opd_loader_past");
	}

	var closeButtons = {
		"退出回首页":function() {
				window.location.href = "./panel.do?method=init";
		}
	}
	if (infectString.indexOf("点检") >= 0) {
		closeButtons ={
			"进行点检":function() {
				window.location.href = "./usage_check.do?from=position";
			},
			"退出回首页":function() {
					window.location.href = "./panel.do?method=init";
			}
		}
	}

	$break_dialog.dialog({
		modal : true,
		resizable:false,
		dialogClass : 'ui-error-dialog',
		width : 'auto',
		title : "工位工作不能进行",
		closeOnEscape: false,
		close: function(){
			window.location.href = "./panel.do?method=init";
		},
		buttons : closeButtons
	});
}

var getJustWorkingFingers = function(material_id) {
	// Ajax提交
	$.ajax({
		beforeSend : ajaxRequestType,
		async : true,
		url : servicePath + '?method=doPointOut',
		cache : false,
		data : {material_id : material_id},
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : function(xhrobj) {
			var resInfo = null;
//			try {
				// 以Object形式读取JSON
				eval('resInfo =' + xhrobj.responseText);

				if (resInfo.fingers) flowtext = resInfo.fingers + (resInfo.past_fingers ? "<br>" + resInfo.past_fingers : "");
				$("#flowtext").html(flowtext);
//			} catch (e) {
//				alert("doPointOut error");
//			};
		}
	});
}

var expeditedColor = function(expedited, today, reason) {
	if (today == 1) return ' tube-green'; // 当日
	if (reason == "待投入" || reason == "保留中") return '';
//	if (expedited >= 10) return ' tube-yellow'; // 加急
//	if (expedited == 1) return ' tube-blue'; // 加急
	return ' tube-gray'; // 普通 
}

var getFlags = function(expedited, direct_flg, light_fix,reworked, imbalance, anml_exp) {
	if (expedited || direct_flg || light_fix || reworked || imbalance || anml_exp) {
		var retDiv = "<div class='material_flags'>";
		if (expedited >= 20) retDiv += "<div class='rapid_direct_flg'><span>直送快速</span></div>";
		else {
			if (expedited >= 10) retDiv += "<div class='tube-yellow'>急</div>";
			else if (expedited == 1) retDiv += "<div class='tube-blue'>急</div>";
			retDiv += (direct_flg ? "<div class='direct_flg'>直</div>" : "");
		}
		if (light_fix == 1) {
			retDiv += "<div class='light_fix'>小</div>";
		}
		if (reworked == 1) {
			retDiv += "<div class='service_repair_flg'>返</div>";
		}
		if (imbalance == 2) {
			retDiv += "<div class='b_flg group_up'>▲</div>"; // ↑
		}
		if (anml_exp == 1) {
			retDiv += "<div class='rapid_direct_flg anml_exp'><span>动物实验</span></div>"
		}
		retDiv += "</div>";
		return retDiv;
	} else {
		return "";
	}
}

var getBlock = function(block_status) {
	if (block_status) {
		var retDiv = "<div class='pa_flags'>";
		if (block_status == 1) retDiv += "<div class='bo_flg'><span>BO</span></div>";
		if (block_status == 2) retDiv += "<div class='bo_flg'><span>PA</span></div>";
		retDiv += "</div>";
		return retDiv;
	} else {
		return "";
	}
}

var getLevel = function(level) {
	if (level) {
		var levelText = "S" + level; // TODO
		if (level == 9 || level == 91 || level == 92 || level == 93 || level == 99) levelText = "D";
		if (level == 96 || level == 97 || level == 98 ) levelText = "M";
		if (level == 56 || level == 57 || level == 58 || level == 59) levelText = "E";
		return "<span class='level level_" + levelText + "'>" + levelText + "</span>";
	}
	return "";
}

var doInit=function(){
	// Ajax提交
	$.ajax({
		beforeSend : ajaxRequestType,
		async : false,
		url : servicePath + '?method=jsinit',
		cache : false,
		data : {},
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : doInit_ajaxSuccess
	});
};

//$(document).ready(function() {
$(function() {
	isDivision = !$("#position_status").hasClass("simple");

	$("input.ui-button").button();

	$("#workarea a.areacloser").hover(function() {$(this).addClass("ui-state-hover");
		}, function() {$(this).removeClass("ui-state-hover");});

	var foundry_start_time = $("#hidden_foundry_start_time").val();
	if (foundry_start_time != undefined) {
		if (foundry_start_time !=  "") {
			$("#user_working_status").text("代工中");
		} else {
			$("#user_working_status").text("本岗工作中");
		}
	}
	$("#btn_foundry").bind("click", function() {
		foundryChange();
	});

	$("#material_details").hide();
	$(".other_px_change_button").enable();
	$("#continuebutton").hide();
	$("#manualdetailarea").hide();

	$("#working_detail").hide().click(function(){
		var material_id = $("#pauseo_material_id").val();
		showMaterial(material_id);
	}).end().next().hide();

	$("#position_status").css("color", "white");

	posClockObj.init($("#material_details td:eq(7)"), $("#material_details td:eq(9)"), $("#dtl_process_time"), $("#p_rate"));
	if (typeof(posUseSnoutObj) === "object") posUseSnoutObj.init(hasPcs, $("#usesnoutarea")); 

	if (hasPcs) {
		pcsO.init($("#manualdetailarea"), false);
	}

	doInit();

	// 输入框触发，配合浏览器
	$("#scanner_inputer").keypress(function(){
	if (this.value.length === 11) {
		doStart();
	}
	});
	$("#scanner_inputer").keyup(function(){
	if (this.value.length >= 11) {
		doStart();
	}
	});

	$("#finishbutton").click(doFinish);
	$("#breakbutton").click(makeBreak);
	$("#stepbutton").click(makeStep);
	$("#pausebutton").click(makePause);
	$("#continuebutton").click(endPause);

	$("#waitings").on('dblclick', ".waiting", function(){
		var material_id = this.id.replace("w_", "");
		showMaterial(material_id);
	});

	$("#storagearea div:eq(0)").on('dblclick', function(){
		// alert("refresh");
	}).on("click", ".other_px_change_button", pxChange);

	takeWs();
	
	$("#comments_sidebar .ui-widget-header span").on("click",function(){
		if($("#comments_sidebar").hasClass("shown")){
			$("#comments_sidebar .tip_pages").css("overflow-y", "hidden");
			$("#comments_sidebar img").hide();
			$("#comments_sidebar .comments_area").slideUp(200,function(){
				$("#comments_sidebar .ui-widget-header span").removeClass("icon-enter-2").addClass("icon-share");
				$("#comments_sidebar").animate({width:"30px",opacity:".5"},300);
			});
			$("#comments_sidebar").removeClass("shown");
		}else{
			$("#comments_sidebar").animate({width:"1024px",opacity:"1"},300,function(){
				$("#comments_sidebar .ui-widget-header span").removeClass("icon-share").addClass("icon-enter-2");
				$("#comments_sidebar .comments_area").slideDown(200,function(){
					$("#comments_sidebar img").show();
					$("#comments_sidebar .tip_pages").css("overflow-y", "auto");
				});
			});
			$("#comments_sidebar").addClass("shown");
		}
	});
});

var doStart_ajaxSuccess = function(xhrobj, textStatus, postData){
	var resInfo = null;
	try {
		// 以Object形式读取JSON
		eval('resInfo =' + xhrobj.responseText);

		if (resInfo.errors.length > 0) {
			var error1 = resInfo.errors[0];
			if (error1.errcode === "info.dryingJob.finishDryingProcess") {
				warningConfirm(error1.errmsg, function() {
					postData.confirmed = true;
					// Ajax提交
					$.ajax({
						beforeSend : ajaxRequestType,
						async : false,
						url : servicePath + '?method=doscan',
						cache : false,
						data : postData,
						type : "post",
						dataType : "json",
						success : ajaxSuccessCheck,
						error : ajaxError,
						complete : doStart_ajaxSuccess
					});
				});
			} else if (resInfo.infectString) {
				showBreakOfInfect(resInfo.infectString);
				return;
			} else {
				// 共通出错信息框
				treatBackMessages(null, resInfo.errors);
			}
		} else {
			$("#hidden_workstauts").val(resInfo.workstauts);
			if (resInfo.workstauts == 1 || resInfo.workstauts == 4) {
				treatStart(resInfo);
				if (typeof(operator_ws) === "object") operator_ws.send("callLight:");
				showFoundry();
				getJustWorkingFingers(resInfo.mform.material_id);
				if (typeof(getRentListdata) === "function") getRentListdata();
			} else if (resInfo.workstauts == 3) {
				showPartialRecept(resInfo);
			} else if (resInfo.workstauts == 3.9) {
				doFinish();
			}
			dryProcesses = getDryProcessesTable(resInfo.dryProcesses);
		}
	} catch (e) {
		alert("name: " + e.name + " message: " + e.message + " lineNumber: "
				+ e.lineNumber + " fileName: " + e.fileName);
	};
};

var showFoundry = function(){
	var $dialog = $(".if_message-dialog");
	if ($dialog.length > 0) {
		var ss = "<span>请注意您的当前状态在</span><span style='color:red;'>" + $("#user_working_status").text() + 
			"</span><span>，<br>如果与实际情况不符，请在完成作业前切换至正确状态。</span>"
		$dialog.find(".ui-widget-header").next().html(ss);
		$dialog.addClass("show");
		setTimeout(function(){
			$dialog.removeClass("show");
		}, 10500);
	}
}

var showPartialRecept = function(resInfo, finish){
	var $partialconfirmarea = $('#partialconfirmarea');
	if ($partialconfirmarea.length == 0) {
		$("body").append("<div id='partialconfirmarea'><table id='partialConfirmList'/><div id='partialConfirmComponent' class='ui-state-default'/></div>");
		$partialconfirmarea = $('#partialconfirmarea');
		$("#partialConfirmList").jqGrid({
			data:{},
			height: 346,
			width: 800,
			rowheight: 23,
			datatype: "local",
			colNames:['','','零件编号','零件名称','可签收数量','已签收数量','缺品数量','入库预订日','消耗品','bom'],
			colModel:[
				{
					name:'material_partial_detail_key',
					index:'material_partial_detail_key',
					hidden:true
				},
				{
					name:'partial_id',
					index:'partial_id',
					hidden:true
				},
				{
					name:'code',
					index:'code',
					width:40
				},
				{
					name:'partial_name',
					index:'partial_name',
					width:60
				},
				{
					name:'recept_quantity',
					index:'recept_quantity',
					align:'right',
					width:30,
					formatter:'integer',
					sorttype:'integer'
				},
				{
					name:'cur_quantity',
					index:'cur_quantity',
					width:30,
					align:'right',
					formatter:'integer',
					sorttype:'integer'
				},
				{
					name:'waiting_receive_quantity',
					index:'waiting_receive_quantity',
					width:30,
					align:'right',
					formatter:'integer',
					sorttype:'integer'
				},
				{
					name:'arrival_plan_date',
					index:'arrival_plan_date',
					width:50,
					align:'center',
					formatter:function(cellValue,options,rowData){
						if(rowData.status==3){
							if ("9999/12/31" == cellValue) {
								return "未定";
							}
							return cellValue || "";
						}
						return "";
					}
				},
				{
					name:'append',
					index:'append',
					width:20,
					formatter:'select',
					align:'center',
					editoptions:{value:':;1:消耗品;7:组装组件'}
				},
				{
					name:'bom_quantity',
					index:'bom_quantity',
					hidden:true
				}
			],
			rowNum: 69,
			toppager: false,
			pager: null,
			viewrecords: true,
			pagerpos: 'right',
			pgbuttons: true,
			pginput: false,
			hidegrid: false, 
			recordpos:'left',
			multiselect:true,
			onSelectRow:function(rowid,statue){
				var pill = $("#recept_partial_list");
				var $row = pill.find("tr#" + rowid);
				var $cb = $row.find("td[aria\\-describedby='partialConfirmList_cb'] input");
				if ($cb.is(":hidden")) {
					$cb.removeAttr("checked");
					$row.removeClass("ui-state-highlight");
				}
			},
			onSelectAll:function(rowids,statue){
				var pill = $("#recept_partial_list");
				var $rows = pill.find("tr");
				$rows.each(function(){
					var $cb = $(this).find("td[aria\\-describedby='partialConfirmList_cb'] input");
					if ($cb.is(":hidden")) {
						$cb.removeAttr("checked");
						$(this).removeClass("ui-state-highlight");
					}
				});
			},
			gridComplete:function(){
				// 得到显示到界面的id集合
				var IDS = $("#partialConfirmList").getDataIDs();
				// 当前显示多少条
				var length = IDS.length;
				var pill = $("#partialConfirmList");

				for (var i = 0; i < length; i++) {
					// 从上到下获取一条信息
					var rowData = pill.jqGrid('getRowData', IDS[i]);
					var not_quantity_data = rowData["recept_quantity"];
					if(not_quantity_data==0){
						pill.find("tr#" + IDS[i] + " td[aria\\-describedby='partialConfirmList_cb']").find("input").hide();
					}
					var bom_quantity = rowData["bom_quantity"];
					var code = rowData["code"];
					if(bom_quantity && bom_quantity > 0 && code.indexOf("*") < 0){
						pill.find("tr#" + IDS[i] + " td").css("background-color", "lightblue");
					}
				}

				// 不要全选按钮
				$("#jqgh_partialConfirmList_cb").hide();
			}
		});
	}

	$("#partialConfirmList").jqGrid().clearGridData();
	$("#partialConfirmList").jqGrid('setGridParam',{data:resInfo.mpds}).trigger("reloadGrid", [{current:false}]);
	var strComponent = null;
	for (var mpdi in resInfo.mpds) {
		var linepart = resInfo.mpds[mpdi];
		if (linepart.append == "7") {
			if (linepart.line_name) {
				strComponent = "组件 " + linepart.code + " 现分配的序列号是：<span id='partialConfirmTarget'>" + linepart.line_name 
						+ "</span>。请在右边扫描NS 组件信息单确认：" + "<input class='mpds_iscanner'></input>"; 
			} else {
				strComponent = "组件 " + linepart.code + " 现未分配的序列号，还不能对其签收。"; 
			}
			;
			break;
		}
	}
	if (strComponent) {
		$("#partialConfirmComponent").html(strComponent);
		$("#partialConfirmComponent .mpds_iscanner").on("focus", function(){this.select();});
	} else {
		$("#partialConfirmComponent").html("");
	}

	partial_closer = true;
	// TODO div infect
	$partialconfirmarea.dialog({
		modal : true,
		resizable:false,
		width : 'auto',
		title : "清点本工位使用零件",
		closeOnEscape: false,
		close: function(){
			if (partial_closer && !finish) window.location.href = "./panel.do?method=init";
		},
		buttons :{
			"退出工位":function() {
				$partialconfirmarea.dialog("close");
			},
			"报告线长":function() {
				reportAndBreak();
			},
			"确定":function() {
				if ($("#partialConfirmComponent").html()) {
					if ($("#partialConfirmTarget").length) {
						var scannerValue = $(".mpds_iscanner").val();
						if ($("#partialConfirmTarget").text().indexOf(scannerValue) >= 0
							&& scannerValue.length === 13) {
							commitPartialConfirm($partialconfirmarea, finish);
						} else {
							errorPop("扫描的 NS 组件序列号不符合要求。");
						}
					} else {
						errorPop($("#partialConfirmComponent").text());
					}
				} else {
					commitPartialConfirm($partialconfirmarea, finish);
				}
			}
		},
		create: function( event, ui ) {
			var $buttonpane = $partialconfirmarea.next(".ui-dialog-buttonpane").find(".ui-dialog-buttonset");
			$buttonpane.css("width", "98%");
			$buttonpane.find("button").css("float", "right")
			.eq(1).css("float", "left");
		}
	});

	if (resInfo.notMatch) {
		errorPop("您在本工位清点的零件情况与定位设定不符。<br>请确认您的点检结果，或者报告线长处理。");
	}
}

var doCallLeader_ajaxSuccess = function(xhrObj){
	partial_closer = false;
	$('#partialconfirmarea').dialog("close");
}

var reportAndBreak = function() {
	var postData = {};

	// Ajax提交
	$.ajax({
		beforeSend : ajaxRequestType,
		async : false,
		url : servicePath + '?method=doCallLeaderOfPartialMismatch',
		cache : false,
		data : postData,
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : doCallLeader_ajaxSuccess
	});
}

var commitPartialConfirm = function($target, finish) {
	var postData = {};

    var rows = $("#partialConfirmList").find("tr:has('input[type=checkbox][checked]:visible')");
    for(var i=0;i<rows.length;i++){
	    var rowData = $("#partialConfirmList").getRowData(rows[i].id);
	  	postData["keys.material_partial_detail_key[" + i + "]"] = rowData["material_partial_detail_key"];
	  	postData["keys.recept_quantity[" + i + "]"] = rowData["recept_quantity"];
    }
    postData.finish = finish;

	// Ajax提交
	$.ajax({
		beforeSend : ajaxRequestType,
		async : false,
		url : servicePath + '?method=doPartialUse',
		cache : false,
		data : postData,
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : doStart_ajaxSuccess
	});
}

var doStart=function(){
	var data = {
		material_id : $("#scanner_inputer").val()
	}
	var posi = $("#w_" + data.material_id).find(".processCode");
	if (posi.length > 0) {
		data.position_id = posi.attr("p_id")
	}
	$("#scanner_inputer").attr("value", "");

	// 作业等待时间
	if ($(".opd_re_comment").length > 0) {
		var pause_start_time = new Date($(".opd_re_comment").attr("pause_start_time"));
		var leak = $(".opd_re_comment").attr("leak");
		if((leak && leak != "0") && !header_today_holiday 
			&& new Date().getTime() - pause_start_time.getTime() > 179999) {
			errorPop(WORKINFO.needFillBreak);
			return;
		}
	}

	// 小修理等待提醒
	var $lightBox_overtime = $(".lightBox.overtime");
	if ($lightBox_overtime.length > 0) {
		var hitOutDate = false;
		var $tdWait = $lightBox_overtime.find("td[material_id="+ data.material_id +"]");
		if ($tdWait.length > 0) {
			if ($tdWait.next().next().hasClass("overtime")) {
				hitOutDate = true;
			}
		}
		if (!hitOutDate) {
			warningConfirm("如果有条件，请选择等待作业相关中小修理中超时的维修对象进行作业。<BR>点击取消则继续当前维修。"
			, function(){$("#scanner_inputer").focus();}
			, function(){doStartForward(data);}
			);
		} else {
			doStartForward(data);
		} 
	} else {
		doStartForward(data);
	}

};

var showSidebar = function(material_comment) {

	var $ul = $("<ul/>");
	var $content = $("<div class='tip_pages'/>");
	
	if (material_comment) {
		$ul.append("<li><input type='radio' id='st_material_comment' name='showTips'><label for='st_material_comment'>维修对象备注<label></li>");
		$content.append("<div class='tip_page' for='st_material_comment'><textarea readonly>" + material_comment + "</textarea></div>");
	}
	if (device_safety_guide) {
		for (var idsg in device_safety_guide) {
			var device_type = device_safety_guide[idsg];
			$ul.append("<li><input type='radio' id='st_" + device_type.devices_type_id 
				+ "' name='showTips'><label for='st_" + device_type.devices_type_id + "'>" + device_type.name + "<label></li>");
			$content.append("<div class='tip_page' for='st_" + device_type.devices_type_id + "'>" 
				+ (device_type.safety_guide ? "<img src='http://" + document.location.hostname + "/photos/safety_guide/" + device_type.devices_type_id + "'></img>" : "")
				+ "</div>");
		}
	}
	$content.children().hide();

	$("#comments_sidebar .comments_area").html("")
		.append($content)
		.append($ul)
		.find("ul").buttonset()
		.find("input:radio").change(function(){
			$("#comments_sidebar .tip_page").hide();
			$("#comments_sidebar .tip_page[for='" + $(this).attr("id") + "']").show();
		})
		.end().find("input:radio:eq(0)").attr("checked", "checked").trigger("change");
	$("#comments_sidebar .comments_area").hide();
	$("#comments_sidebar").removeClass("shown").css({width:"30px",opacity:".5"});
	$("#comments_sidebar .ui-widget-header span").removeClass("icon-enter-2").addClass("icon-share");

	$("#comments_sidebar").show();

	if (material_comment && !$("#comments_sidebar").hasClass("shown")) {
		$("#comments_sidebar .ui-widget-header span").trigger("click");
	}
}

var doStartForward=function(data){
	// Ajax提交
	$.ajax({
		beforeSend : ajaxRequestType,
		async : false,
		url : servicePath + '?method=doscan',
		cache : false,
		data : data,
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : function(xhrobj, textStatus) {
			doStart_ajaxSuccess(xhrobj, textStatus, data);
		}
	});
};

var doFinish_ajaxSuccess = function(xhrobj, textStatus){
	var resInfo = null;
	try {
		// 以Object形式读取JSON
		eval('resInfo =' + xhrobj.responseText);

		if (resInfo.errors.length > 0) {
			// 共通出错信息框
			if (resInfo.workstauts == 3) {
				showPartialRecept(resInfo, 1);
			} 
			var error1 = resInfo.errors[0];
			if (error1.errcode === "info.countdown.unreach") {
				if (typeof(showDelayReasonDialog) === "function") showDelayReasonDialog(resInfo.causes, doFinish);
				return;
			}
			treatBackMessages(null, resInfo.errors);
		} else {
			$("#hidden_workstauts").val(resInfo.workstauts);
			if (resInfo.workstauts == 2 || resInfo.workstauts == 5) {
				treatPause(resInfo);
			} else if (xhrobj.status == 200) {
				$("#scanner_inputer").attr("value", "");
				$("#material_details").hide();
				$(".other_px_change_button").enable();
				$("#scanner_container").show();
				$("#position_status").text("准备中")
					.css("background-color", "#0080FF");
				$(".opd_re_comment").show();
				$("#manualdetailarea").hide();
				$("#devicearea").hide();

				if (resInfo.past_fingers) $("#flowtext").text(resInfo.past_fingers);

				$("#working_detail").hide()
					.next().hide();

				posClockObj.stopClock();

				$("#pauseo_edit").hide();
				$("#pauseo_show").show();
				$("#pauseo_show_sorc_no").text($("#material_details td:eq(1)").text());
				$("#pauseo_show_pause_reason").text(toLabelValue($("#pauseo_edit_pause_reason")));
				$("#pauseo_show_comments").text(toLabelValue($("#pauseo_edit_comments")));
				if (!$("#break_dialog").is(":empty")) {
					$("#break_dialog").dialog("option", "buttons", {
							"再开":function(){
								endPause();
							}
						}
					);
				}
				$("#working").text("");
				$("#scanner_inputer").focus();
				$("#usesnoutarea").hide();
				if ($('#partialconfirmarea:visible').length > 0) {
					partial_closer = false;
					$('#partialconfirmarea').dialog("close");
				}
/*				if ($("#comments_dialog:visible").length > 0) {
					$("#comments_dialog textarea").val("");
					$("#comments_dialog").hide();
				}*/
				if (device_safety_guide && device_safety_guide.length) {
					$("#comments_sidebar .comments_area").val("");
					$("#comments_sidebar").hide();
				}

				if (resInfo.processingPauseStart) {
					setPauseClock(resInfo.processingPauseStart);
				}

				if (device_safety_guide && device_safety_guide.length) {
					showSidebar(null);
					if ($("#comments_sidebar").hasClass("shown")) {
						$("#comments_sidebar .ui-widget-header span").trigger("click");
					}
				} else {
					$("#comments_sidebar").hide();
				}

				checkAnmlAlert();

				if ($("#decsnoutarea").length) {
					$("#decsnoutarea").hide();
				}
			}
		}
	} catch (e) {
		console.log("name: " + e.name + " message: " + e.message + " lineNumber: "
				+ e.lineNumber + " fileName: " + e.fileName);
	};
}

var doFinish=function(evt, hr){
	if ($("#desnout:visible").length > 0) {
		if ($("#desnout").children("button").length > 0) {
			errorPop("请选择 C 本体需要回收还是废弃。");
			return;
		}
	}

	var data = {};
	var empty = false;
	if (hasPcs) {
		empty = pcsO.valuePcs(data);
	}

	if (empty) {
		errorPop(WORKINFO.pcsCheck);
	}

	if (!empty) {
		if (hr != null) {
			for (var it in hr) {
				data[it] = hr[it];
			}
		}

		// Ajax提交
		$.ajax({
			beforeSend : ajaxRequestType,
			async : false,
			url : servicePath + '?method=dofinish',
			cache : false,
			data : data,
			type : "post",
			dataType : "json",
			success : ajaxSuccessCheck,
			error : ajaxError,
			complete : doFinish_ajaxSuccess
		});
	}
};

var showMaterial = function(material_id) {
	$process_dialog = $("#process_dialog");
	$process_dialog.hide();
	// 导入编辑画面
	$process_dialog.load("widget.do?method=materialDetail&material_id=" + material_id,
		function(responseText, textStatus, XMLHttpRequest) {
			$.ajax({
			data:{
				"id": material_id // , occur_times: occur_times
			},
			url : "material.do?method=getDetial",
			type : "post",
			complete : function(xhrobj, textStatus){
				var resInfo = null;
				try {
					// 以Object形式读取JSON
					eval('resInfo =' + xhrobj.responseText);
					setLabelText(resInfo.materialForm, resInfo.partialForm, resInfo.processForm, resInfo.timesOptions, material_id);
					if (resInfo.caseId == 3) {
						case3();
					} else {
						case0();
					}
					
				} catch (e) {
					alert("name: " + e.name + " message: " + e.message + " lineNumber: "
							+ e.lineNumber + " fileName: " + e.fileName);
				};
				
				$process_dialog.dialog({
					title : "维修对象详细信息",
					width : 800,
					show : "blind",
					height : 'auto' ,
					resizable : false,
					modal : true,
					minHeight : 200,
					buttons : {
						"关闭": function(){
							$process_dialog.dialog('close');
						}
					}
				});
				$process_dialog.show();
			}
		});
	});
};

var getTubeBody = function(waiting) {
	if (isDivision) 
		return waiting.sorc_no + ' | ' + waiting.model_name + ' | ' + waiting.scheduled_monthday;
	else 
		return (waiting.sorc_no == null ? "" : waiting.sorc_no + ' | ') + waiting.category_name + ' | ' + waiting.model_name + ' | ' + waiting.serial_no
			+ (waiting.scheduled_monthday ?  ' | ' + waiting.scheduled_monthday : "") 
			+ (waiting.shelf_name ? ('<span class="shelf_name">' + waiting.shelf_name + "</span>") : '');
}

var getWaitingHtml = function(waitings, other) {
	var reason = "";
	var waiting_html = "";
	var waitingsLength = 0;
	for (var iwaiting = 0; iwaiting < waitings.length; iwaiting++) {
		var waiting = waitings[iwaiting];
		if (reason != waiting.waitingat) {
			reason = waiting.waitingat;
			waiting_html += '<div class="ui-state-default w_group">'+ reason +':</div>';
		}
		waiting_html += '<div class="waiting tube' + (waiting.imbalance ? "" : " other_px") + '"' +
							' id="w_' + waiting.material_id + '">' +
							'<div class="tube-liquid' + expeditedColor(waiting.expedited, waiting.today, reason)  + '">'
								+ getLevel(waiting.level)
								+ getTubeBody(waiting) 
								+ getFlags(waiting.expedited, waiting.direct_flg, waiting.light_fix, waiting.reworked, waiting.imbalance, waiting.anml_exp) +
								getBlock(waiting.block_status) +
								// getInPlaceTime(waiting.in_place_time) +   
								getDryingTime(waiting.drying_process) +   
								getProcessCode(waiting.position_id, waiting.process_code) +   
							'</div>' +
						'</div>';
		if (other && !waiting.imbalance) {
			other.push(waiting);
		}
		if (reason != "待投入") {
			waitingsLength++;
		}
	}

	$("#p_waiting_count").text(waitingsLength + "台");
	return waiting_html;
}

var getPxDefine = function(px) {
	if (px == "1") return "A";
	if (px == "2") return "B";
	if (px == "3") return "A";
}

var showWaitings = function(waitings){
	var waitingsOtherPx = [];
	$("#waitings").html(getWaitingHtml(waitings, waitingsOtherPx));

	if (waitingsOtherPx && isDivision) {
		var waitingsOtherHtml = "";

		var waitingsOfpx = waitingsOtherPx;
		var waitingsOfpxLength = waitingsOfpx.length;
		var px = $("#otherPx").val();
		var pxName = getPxDefine(px);
		waitingsOtherHtml += '<span class="other_px_count">' +
				'<input class="other_px_change_button" type="button" px="' + px + '" pxName="' + pxName + '" count="' + waitingsOfpxLength + '"' +
				'class="ui-button" value="' + pxName + ' 线等待数 ' + waitingsOfpxLength + '">' +
//				'<div class="other_px_waiting ui-widget-content dwidth-half">' + getWaitingHtml(waitingsOfpx) + '</div>' +
				"</span>";

		if (waitingsOtherHtml) {
			var $waitingsOtherHtml = $(waitingsOtherHtml);
			$waitingsOtherHtml.find(".other_px_change_button").button()
				.hover(function(){if (!this.disabled) {this.value = "切换到 " + this.getAttribute("pxName") + " 线";}},
					function(){if (!this.disabled) {this.value = this.getAttribute("pxName") + ' 线等待数 ' + this.getAttribute("count");}});
			$("#other_px_area").html($waitingsOtherHtml || "");
		}
	}

 	// 计算剩余时间
	var $divs = $("#waitings, .other_px_waiting").find("div.finish_time_left");
	$divs.each(function(){
		var $this = $(this);
		var id = $this.parent().parent().attr("id");

//		var inPlaceTime_val = $this.attr("inPlaceTime");
//		if (inPlaceTime_val != null && inPlaceTime_val != "") {
//			date_time_list.add({id: id, target:$this, date_time:inPlaceTime_val, permitMinutes:permitMinutes});
//		}
		var start_time = $this.attr("start_time");
		if (start_time != null && start_time != "") {
			date_time_list.add({id: id, target:$this, date_time:start_time, naturaltime:true, drying_time:$this.attr("drying_time")});
		}
	});

	refreshTargetTimeLeft(1);
};

var showCompletes = function(completes) {
	var $completes = $(getWaitingHtml(completes));
	$("#completes").html($completes.filter(":not(.w_group)"));
}
var getInPlaceTime = function(inPlaceTime) {
	var time_html = "";
	if (inPlaceTime) {
		time_html += '<div inPlaceTime="' + inPlaceTime + '" class="finish_time_left"></div>';
	}
	return time_html;
};

var getDryingTime = function(dryingProcess) {
	var time_html = "";
	if (dryingProcess) {
		var title = "烘干详细信息：" + dryingProcess.content + " 硬化条件：" + dryingProcess.hardening_condition;
		if (dryingProcess.manage_code) {
			title += " 烘干设备：" + dryingProcess.manage_code + " " + dryingProcess.slot + "号库位";
		}
		time_html += '<div start_time="' + dryingProcess.start_time + '" class="finish_time_left" title="' + title + '"' 
			+ "drying_time='" + dryingProcess.drying_time + "'></div>";
	}
	return time_html;
}

var getProcessCode = function(positionId, processCode) {
	if (processCode) {
		var hasPrivacy = true;
		if (positionId < 0) {
			positionId = positionId.substring(1);
			hasPrivacy = false;
		}
		return "<div class='processCode' p_id='" + positionId + "'" + (hasPrivacy ? "" : " noPrivacy") + ">" + processCode + "</div>";
	} else {
		return "";
	}
}
var getWaitings = function() {
	$.ajax({
		data: null,
		url : servicePath + "?method=refreshWaitings",
		type : "post",
		cache : false,
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : function(xhrobj, textStatus){
			var resInfo = null;
	    	try {
	    		resInfo = $.parseJSON(xhrobj.responseText);
				showWaitings(resInfo.waitings);
				if (resInfo.completes) {
					showCompletes(resInfo.completes);
				}
	    	} catch(e) {
	    	}
		}
	});
};

// 工位后台推送
function takeWs() {
	var g_pos_id = $("#g_pos_id").val(); 

	if (g_pos_id) {
//		try {
		// 创建WebSocket  
		var position_ws = new WebSocket(wsPath + "/position");
		// 收到消息时做相应反应
		position_ws.onmessage = function(evt) {
	    	var resInfo = {};
	    	try {
	    		resInfo = $.parseJSON(evt.data);
	    	} catch(e) {
	    	}
    		if ("refreshWaiting" == resInfo.method) {
    			getWaitings();
    		} else if ("message" == resInfo.method) {
    			warningConfirm(resInfo.message);
    		}
		};  
		// 连接上时走这个方法  
		position_ws.onopen = function() {     
			position_ws.send("entach:" + g_pos_id + "#"+$("#op_id").val());
		}; 
//	} catch(e) {
//	}
	}
};


var pxChange = function() {
	if ($("#material_details").is(":visible")) {
		return;
	}
	$.ajax({
		data: {set_px : this.getAttribute("px")},
		url : servicePath + "?method=pxChange",
		type : "post",
		cache : false,
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError
	})
}

var getDryProcessesTable = function(dryProcesses){
	if (dryProcesses && dryProcesses.length) {
		if ("follow" === dryProcesses) return window.dryProcesses;

		var tableStr = "<table class='dryProcessesTable subform'>";
		for (var iDp in dryProcesses) {
			var dryProcess = dryProcesses[iDp];
			tableStr += "<tr drying_job_id='" + dryProcess.drying_job_id + "'>"
				 + "<td>" + dryProcess.content + "</td>"
				 + "<td>" + dryProcess.hardening_condition + "</td>"
				 + (dryProcess.device_manage_id ? 
				 "<td device_manage_id='" + dryProcess.device_manage_id + "'>" + dryProcess.manage_code + " " + dryProcess.model_name + "</td>"
				 + "<td>" + dryProcess.section_name + " " + dryProcess.process_code + "</td>"
				 : "<td colspan=2>自然干燥</td>")
				 + "<td " + (dryProcess.slots ? "slots='" + dryProcess.slots + "'" : "") + "></td>"
				 + "</tr>";
		}
		tableStr += "</table>";
		return tableStr;
	} else {
		return "";
	}
};

var foundryChange = function(){
	var working = $("#user_working_status").data("working");
	if (working) {
		return;
	}
	$("#user_working_status").data("working", "working");

	var foundry_start_time = $("#hidden_foundry_start_time").val();
	if (foundry_start_time ==  "") {
		// Ajax提交
		$.ajax({
			beforeSend : ajaxRequestType,
			async : true,
			url : servicePath + '?method=doFoundryChange',
			cache : false,
			data : {"user_working_status":1},
			type : "post",
			dataType : "json",
			success : ajaxSuccessCheck,
			error : ajaxError,
			complete : function(xhrobj, textStatus) {
				$("#user_working_status").text("代工中");
				
				// 以Object形式读取JSON
				eval('resInfo =' + xhrobj.responseText);
				$("#hidden_foundry_start_time").val(resInfo.foundry_start_time);
				$("#user_working_status").data("working", null);
			}
		});
	} else {
		var postData = {
			"user_working_status":2,
			"foundry_start_time":foundry_start_time
		};

		// Ajax提交
		$.ajax({
			beforeSend : ajaxRequestType,
			async : true,
			url : servicePath + '?method=doFoundryChange',
			cache : false,
			data : postData,
			type : "post",
			dataType : "json",
			success : ajaxSuccessCheck,
			error : ajaxError,
			complete : function(xhrobj, textStatus) {
				$("#user_working_status").text("本岗工作中");
				$("#hidden_foundry_start_time").val("");
				$("#user_working_status").data("working", null);
			}
		});
	}
};

var checkAnmlAlert = function() {
	if ($("#anml_attendtion").length > 0) {
		errorPop(WORKINFO.animalExpClean);
	}
}