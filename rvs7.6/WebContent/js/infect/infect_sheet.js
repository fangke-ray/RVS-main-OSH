$(function(){
	var $tcs_sheet_container = $("<div/>");
	$tcs_sheet_container.load("widgets/infect_sheet.html", function(responseText, textStatus, XMLHttpRequest) {
		$tcs_sheet_container.find("#manage_replace_panel input:button").click(doExchange);

		$("body").append($tcs_sheet_container.html());
	});

	$("body").on("dragstart", ".ui-dialog", function(){
		if ((".tcs_sheet").length <= 1) return;

		var $uiDialog = $(this);
		if ($uiDialog.find(".tcs_sheet").length > 0) {
			var maZ = 0;
			$(".tcs_sheet").each(function(){
				var thisZ = parseInt($(this).closest(".ui-dialog").css("zIndex"));
				if (thisZ > maZ) {
					maZ = thisZ;
				}
			});
			if (parseInt($uiDialog.css("zIndex")) <= maZ) {
				$uiDialog.css("zIndex", maZ + 1);
			}
		}
	})
})
/** 服务器处理路径 */
var infectSheetServicePath = "usage_check.do";
var stopDblSubmit = false;

var doExchange = function(){
	var newManageNo = $("#manage_replace_panel input:text").val();
	var g_manage_id = $("dtag").attr("manage_id")
	if (newManageNo) {
		var postData = {
			devices_manage_id : g_manage_id,
			manage_code : newManageNo
		}
		$.ajax({
			beforeSend : ajaxRequestType,
			async : true,
			url : 'devices_manage.do?method=doexchange',
			cache : false,
			data : postData,
			type : "post",
			dataType : "json",
			success : ajaxSuccessCheck,
			error : ajaxError,
			complete : function(xhrObj){
				var resInfo = $.parseJSON(xhrObj.responseText);
				if (resInfo.errors.length == 0) {
					$("#manage_replace_panel").dialog("close");
					$(".tcs_sheet").dialog("close");
					if (typeof findit === "function") findit();
				}
			}
		});
	}
}

/* 设备工具检点种详细信息 画面
	object_type : 设备工具=1/治具=2 * 必须
	manage_id : 设备工具/治具ID * 必须
	manage_code : 设备工具/治具管理编号 * 必须
	position_id : 所在工位ID * 必须
	process_code : 所在工位代码 * 必须
	section_id : 所在课室ID * 治具时必须 / 设备工具(仅力矩工具/电烙铁工具)必须
	operator_id : 点检归属者ID * 治具时必须
	check_file_manage_id : 点检票单 ID * 设备工具时必须
	sheet_manage_no : 点检票单号 * 设备工具时必须
	check_while_use : 使用前点检  * 周边维修对象点检时选择
	select_date : 选择日期(yyyy/MM/dd) * 除系统管理员外，指定日期则变为表格只读
 */
var showInfectSheet =function(infectDetailData, isLeader){
	var $check_sheet = $(".tcs_sheet:hidden").eq(0);

	if ($check_sheet.length == 0) {
		var newTcsSheetId = "check_sheet" + new Date().getTime();
		$("body").append('<div id="' + newTcsSheetId + '" class="tcs_sheet"></div>');
		$check_sheet = $("#" + newTcsSheetId);
	}
	$check_sheet.hide();

	var postData = {
		section_id : infectDetailData.section_id,
		position_id : infectDetailData.position_id,
		operator_id : infectDetailData.operator_id,
		manage_id : infectDetailData.manage_id,
		check_file_manage_id : infectDetailData.check_file_manage_id,
		object_type : infectDetailData.object_type,
		select_date : infectDetailData.select_date
	}
	$.ajax({
		beforeSend : ajaxRequestType,
		async : true,
		url : infectSheetServicePath + "?method=getChecksheet",
		cache : false,
		data : postData,
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : function(xhrObj){
			var resInfo = $.parseJSON(xhrObj.responseText);
			if (resInfo.check_sheet) {
				$check_sheet.html(resInfo.check_sheet);
				$check_sheet.find("input,textarea").not("[inputted]").not(".t_comment").parent().addClass("tcs_input")
				.each(function(){
					var $tcs_input = $(this);
					if ($tcs_input.find("input[lock]").length > 0) {
						$tcs_input.attr("lock", "1");
					}
				});
				// alert($("#check_sheet input:radio").length);
				// 使用期限
				var $expireTr = $check_sheet.find("span.useEnd[expire='1']").parent().parent();
				$expireTr.attr("stat", "change")
					.find("input:radio[value='1']").remove().end()
					.find("input:radio[value='2']").attr("checked", "checked");
				$check_sheet.find("input:radio").not(".input_type").button();
				$check_sheet.find("input.ui-button").button();
			}
			if(isLeader == "true") {
				$check_sheet.find("input:radio, input:text").not(".t_comment").not(".input_type").click(function(idx, ele) {
					$(this).parent().parent().attr("stat", "change");
				});
				$check_sheet.find("input:text").not(".t_comment").not(".input_type").keydown(function(event) {
					$(this).parent().parent().attr("stat", "change");
					if (event.keyCode==73) {
						this.value= "∞";
						return false;
					};
				});
			} else {
				$check_sheet.find("input:radio, input:text").not(".t_comment").not(".input_type").change(function(idx, ele) {
					$(this).parent().parent().attr("stat", "change");
				}).keydown(function(event) {
					if (event.keyCode==73) {
						this.value= "∞";
						$(this).parent().parent().attr("stat", "change");
						return false;
					};
				});

				// ∞
				;
			}
			$check_sheet.find("input:radio").not("[inputted]").not(".t_comment").parent().bind("dblclick",function(idx, ele) {
				$(this).find("input:radio").removeAttr("checked").trigger("change");
				$(this).parent().removeAttr("stat");
			});
			$check_sheet.find("input:text").not("[inputted]").parent().bind("dblclick",function(idx, ele) {
				$(this).find("input:text").val("");
				$(this).parent().removeAttr("stat");
			});
			$(".manage_replace").click(function(){
				$("#manage_replace_panel").dialog({
				title : null,
				show: "blind",
				resizable : false,
				modal : true,
				minHeight : 200,
				buttons : {}});
			});
			$("#upper_check").button();
			if ($(".tcs_sheet:visible").length == 0) {
				$(window).overlay();
				$("div.overlay").attr("id", "ifoverlay");
			}
			$check_sheet.show();

			var okButton = (isLeader == "true" ? "再点检确认" : "点检完成" )
			$check_sheet.dialog({
				title : "进行点检： " + infectDetailData.manage_code + "  " + infectDetailData.sheet_manage_no,
				width : 1136,
				show: "blind",
				height : 640,
				position : [0 + $(".tcs_sheet:visible").length * 40, 40],
				resizable : false,
				modal : false,
				minHeight : 200,
				close : function(){
					if ($(".tcs_sheet:visible").length <= 0) {
						$("#ifoverlay").remove();
						$("body").css('overflow', 'auto');
					}
					$check_sheet.html("");
				},
				buttons : {
					"备注":function(){
						var $check_comment = $("#c_comment");
						
						var pCommentData = {
							manage_id : infectDetailData.manage_id,
							check_file_manage_id : infectDetailData.check_file_manage_id,
							object_type : infectDetailData.object_type,
							select_date : infectDetailData.select_date
						}
						// 多个
						if ($(".t_comment").length > 0) {
							if ($(".t_comment:checked").length == 0) {
								errorPop("需要选择要做备注的对象");
								return;
							} else {
								pCommentData.manage_id = $(".t_comment:checked").val();
							}
						}
						$.ajax({
							beforeSend : ajaxRequestType,
							async : true,
							url : infectSheetServicePath + "?method=getCheckComment",
							cache : false,
							data : pCommentData,
							type : "post",
							dataType : "json",
							success : ajaxSuccessCheck,
							error : ajaxError,
							complete : function(xhrObj){
								var resInfo = $.parseJSON(xhrObj.responseText);
								if (resInfo.errors.length == 0) {
									$check_comment.html("<div style='height:400px;'>"+decodeText(resInfo.comments)+"</div><div style='height:120px;'><textarea style='width:80%;height:4em;'/></div>");
									$check_comment.dialog({
										title : "记载备注",
										width : 800,
										show: "blind",
										height : 640 ,
										resizable : false,
										modal : true,
										minHeight : 200,
										close : function(){
											$check_comment.html("");
										},
										buttons : {
											"确定" : function(){
												pCommentData.comment = $check_comment.find("textarea").val();
												if (pCommentData.comment)
													$.ajax({
														beforeSend : ajaxRequestType,
														async : true,
														url : infectSheetServicePath + "?method=doInputCheckComment",
														cache : false,
														data : pCommentData,
														type : "post",
														dataType : "json",
														success : ajaxSuccessCheck,
														error : ajaxError,
														complete : function(xhrObj){
															$(".t_comment:checked").next("label").attr("exists", true);
															$check_comment.dialog("close");
														}
													})
											},
											"关闭" : function(){
												$check_comment.dialog("close");
											}
										}
									});
								}
							}
						})
					},
					okButton:function(){
						var noerror = true, noNan = true;
						var postDataR = {section_id : infectDetailData.section_id,
							position_id : infectDetailData.position_id, process_code : infectDetailData.process_code};

						var object_type = infectDetailData.object_type;
						postDataR.object_type = object_type;

						var ii = 0;
						if (object_type == 2) {
							$check_sheet.find(".tcs_content[stat=change]").each(function() {
								var $input = $(this).find("input:radio:checked").not(".t_comment");
								var status = $input.val();
								var manage_id = $(this).attr("manage_id");
								postDataR["submit.status[" + ii + "]"] = status;
								postDataR["submit.manage_id[" + ii + "]"] = manage_id;
								ii++;
								if (status != 1) {
									noerror = false;
								}
								$input.parent().attr("status", status);
							})
						} else if (object_type == 1) {
							postDataR.check_file_manage_id = infectDetailData.check_file_manage_id;
							// 必须填写参考内容
							var refered = true;
							$(".input_type:text").each(function() {
								if (!this.value) {
									refered = false;
								}
							});
							$(".input_type:radio").each(function() {
								if ($(".input_type[name="+this.name+"]:checked").length == 0) {
									refered = false;
								}
							});
							if (refered == false) {
								errorPop("有参照值未输入!"); // TODO
								return;
							}

							$check_sheet.find("tr[stat=change]").each(function() {
								var $input = $(this).find("input:radio:checked").not(".input_type").not(".t_comment");

								$input.each(function(idx, ele){
									var inputVal = ele.value;
									var msco = ele.name.split("_");

									postDataR["submit.manage_id[" + ii + "]"] = msco[1];
									postDataR["submit.item_seq[" + ii + "]"] = msco[2];
									postDataR["submit.status[" + ii + "]"] = inputVal;

									if (inputVal != 1) {
										noerror = false;
									}
									$(ele).parent().attr("status", inputVal);
									ii++;
								});

								$input = $(this).find("input[type='text']").not(".input_type").not(".t_comment");
								$input.each(function(idx, ele){
									var inputVal = ele.value;
									var msco = ele.name.split("_");
									var $ele = $(ele);

									postDataR["submit.manage_id[" + ii + "]"] = msco[1];
									postDataR["submit.item_seq[" + ii + "]"] = msco[2];
									if ("∞" == inputVal) {
										inputVal = 999999.999;
									}
									if (!inputVal) {
										return;
									}
									if(typeof(inputVal) === "string") {
										inputVal = parseFloat(inputVal.trim());
										if(!isNaN(inputVal)) ele.value = inputVal;
									}
									postDataR["submit.digit[" + ii + "]"] = inputVal;

									var upper_limit = $ele.attr("upper_limit");
									var lower_limit = $ele.attr("lower_limit");
									var refer_upper_from = $ele.attr("refer_upper_from");
									var refer_lower_from = $ele.attr("refer_lower_from");
									if (refer_upper_from) {
										if (isNaN(refer_upper_from)) {
											var modelMap = refer_upper_from.split(";");
											var $dtag = $check_sheet.find("dtag");
											var model_name = $dtag.attr("model_name");
											for (var modelI in modelMap) {
												var modelVal = modelMap[modelI];
												if (modelVal.indexOf(model_name) == 0) {
													upper_limit = modelVal.substring(modelVal.indexOf(":") + 1);
													break;
												}
											}
										} else {
											var $refer_upper_from = $(".input_type[seq="+ refer_upper_from +"]:text");
											if ($refer_upper_from.length == 0) {
												$refer_upper_from = $(".input_type[seq="+ refer_upper_from +"]:radio:checked");
											}
											upper_limit = parseFloat(upper_limit) + parseFloat($refer_upper_from.attr("upper_limit") ? $refer_upper_from.attr("upper_limit") : $refer_upper_from.attr("value"));
										}
									}
									if (refer_lower_from) {
										if (isNaN(refer_lower_from)) {
											var modelMap = refer_lower_from.split(";");
											var $dtag = $check_sheet.find("dtag");
											var model_name = $dtag.attr("model_name");
											for (var modelI in modelMap) {
												var modelVal = modelMap[modelI];
												if (modelVal.indexOf(model_name) == 0) {
													lower_limit = modelVal.substring(modelVal.indexOf(":") + 1);
													break;
												}
											}
										} else {
											var $refer_lower_from = $(".input_type[seq="+ refer_lower_from +"]:text");
											if ($refer_lower_from.length == 0) {
												$refer_lower_from = $(".input_type[seq="+ refer_lower_from +"]:radio:checked");
											}
											lower_limit = parseFloat(lower_limit) + parseFloat($refer_lower_from.attr("lower_limit") ? $refer_lower_from.attr("lower_limit") : $refer_lower_from.attr("value"));
										}
									}

									var status = 1;
									if (isNaN(inputVal)) {
										status = -1;
										noNan = false;
									} else {
										if (upper_limit && parseFloat(inputVal) > upper_limit) {
											status = 2;
											noerror = false;
										}
										if (lower_limit && parseFloat(inputVal) < lower_limit) {
											status = 2;
											noerror = false;
										}
									}
									postDataR["submit.status[" + ii + "]"] = status;
									$ele.parent().attr("status", status);
									ii++;
								});
							});
							ii = 0;
							// 参照信息
							$(".input_type").not("[inputted]").each(function(idx, ele){
								var $ele = $(ele);
								if ($ele.attr("type") == "text") {
									postDataR["refer.manage_id[" + ii + "]"] = $ele.attr("manage_id");
									postDataR["refer.item_seq[" + ii + "]"] = $ele.attr("seq");
									var inputVal = $ele.attr("value").trim()
									if ("∞" == inputVal) {
										inputVal = 999999.999;
									}
									postDataR["refer.digit[" + ii + "]"] = inputVal;
									ii++;
								} else if ($ele.attr("type") == "radio") {
									if ($ele.attr("checked")) {
										postDataR["refer.manage_id[" + ii + "]"] = $ele.attr("manage_id");
										postDataR["refer.item_seq[" + ii + "]"] = $ele.attr("seq");
										postDataR["refer.digit[" + ii + "]"] = $ele.attr("value");
										ii++;
									}
								}
							})
						}

						var $confirmmessage = $("#confirmmessage");
						if (!noNan) {
							warningConfirm("请检查数值输入内容。");
							return;
						} else 	if (!noerror) {
							if(isLeader == "true") {
								var $husei = $check_sheet.find("span:contains('×'), span:contains('△')").parent().filter(".ui-state-active").parent();
								var batsu = $husei.length;
								var $sankaku = $check_sheet.find("span:contains('△')").parent().filter(".ui-state-active").length;
								var $batsu = $check_sheet.find("span:contains('×')").parent().filter(".ui-state-active").length;

								var $ttr = $husei.parent().filter("[stat=change]");

								var nov = "<table id='coz' class='condform'><tr><th class='ui-state-default'>管理番号</th><th class='ui-state-default'>品名</th><th class='ui-state-default'>不合格项目</th><th class='ui-state-default'>不合格状况</th></tr>";

								if (object_type == 2) {
									$ttr.each(function(idx,ele){
										var $me = $(ele);
										nov += "<tr manage_id='"+ $me.attr("manage_id") +"'><td>"+$me.find("td:eq(1)").text()+"</td><td>"+$me.find("td:eq(3)").text()+"</td><td><input type='text'/></td><td><textarea></textarea></td></tr>";
									});
								} else if (object_type == 1) {
									if (infectDetailData.check_file_manage_id == "00000000098") {
										$(".tcs_input[status=2]").each(function(idx,ele){
											var $me = $(ele);
											var $papa = $me.parent();
											var tag = $me.find("input")[0].name;
											nov += "<tr manage_id='"+ tag.split("_")[1] +"'><td>"+$papa.find("td:eq(9)").text()+"</td><td>力矩工具</td><td><input type='text'/></td><td><textarea></textarea></td></tr>";
										});
									} else if (infectDetailData.check_file_manage_id == "00000000053") {
										$(".tcs_input[status=2]").each(function(idx,ele){
											var $me = $(ele);
											var $papa = $me.parent();
											var tag = $me.find("input")[0].name;
											nov += "<tr manage_id='"+ tag.split("_")[1] +"'><td>"+$papa.find("td:eq(0)").text()+"</td><td>电烙铁工具</td><td><input type='text'/></td><td><textarea></textarea></td></tr>";
										});
									} else {
										var $dtag = $check_sheet.find("dtag");
										nov += "<tr manage_id='"+ $dtag.attr("manage_id") +"'><td>"+$dtag.attr("manage_code")+"</td><td>"+$dtag.attr("device_name")+"</td><td><input type='text'/></td><td><textarea></textarea></td></tr>";
									}
									$batsu = $check_sheet.find("tr[stat=change] td.tcs_input[status=2]").length;
								}

								nov += "</table>";
								$confirmmessage.html("点检中有不合格项目" + $batsu + "件"+ ($sankaku > 0 ? "，遗失项目" + $sankaku + "件" : "") +"，是否关闭"+infectDetailData.process_code+"工位的作业并提交给上级？");
								$confirmmessage.append(nov);
								$confirmmessage.dialog({
									title : "进行点检",
									dialogClass : 'ui-error-dialog',
									width : 'auto',
									show: "blind",
									height : 'auto' ,
									resizable : false,
									modal : true,
									minHeight : 200,
									close : function(){
										$confirmmessage.html("");
									},
									buttons: {
										"确认":function(){
											var empty = false;
											$coz = $("#coz");
											$coz.find("input,textarea").each(function(){
												if (!empty && $(this).val()) {
												} else {
													empty = true;
												}
											})
											if (empty) {
												errorPop("请写全相关的信息！");
											} else {
												ii = 0;
												// 加入不合格信息
												$coz.find("tr").each(function(idx, ele){
													var $ele = $(ele);
													if ($ele.find("input").length > 0) {
														postDataR["unqualified.manage_id[" + ii + "]"] = $ele.attr("manage_id");
														postDataR["unqualified.object_type[" + ii + "]"] = 2;
														postDataR["unqualified.check_item[" + ii + "]"] = $ele.find("input").val();
														postDataR["unqualified.unqualified_status[" + ii + "]"] = $ele.find("textarea").val();
														ii++;
													}
												})
												if ($("#upper_check").length>0 && $("#upper_check").attr("checked"))
													postDataR.upper_check = 1;
												if(stopDblSubmit) return;
												stopDblSubmit = true;
												doCheckPoint(postDataR, $confirmmessage, $check_sheet);
												if (infectDetailData.check_while_use) infectDetailData.check_while_use(false);
											}
										},
										"取消":function(){
											$confirmmessage.dialog("close");
										}
									}
								})
							} else {
								$confirmmessage.text("点检中有不合格项目，是否关闭"+infectDetailData.process_code+"工位的作业并提交给上级？")
								$confirmmessage.dialog({
									title : "进行点检",
									dialogClass : 'ui-error-dialog',
									width : 'auto',
									show: "blind",
									height : 'auto' ,
									resizable : false,
									modal : true,
									minHeight : 200,
									close : function(){
										$confirmmessage.html("");
									},
									buttons: {
										"确认":function(){
											// TODO this button disable
											if(stopDblSubmit) return;
											stopDblSubmit = true;
											doCheckPoint(postDataR, $confirmmessage, $check_sheet);
											if (infectDetailData.check_while_use) infectDetailData.check_while_use(false);
										},
										"取消":function(){
											$confirmmessage.dialog("close");
										}
									}
								})
							}
						} else {
							$confirmmessage.text("点检中没有不合格项目，确认本次点检完成？")
							$confirmmessage.dialog({
								title : "进行点检",
								dialogClass : 'ui-warn-dialog',
								width : 'auto',
								show: "blind",
								height : 'auto' ,
								resizable : false,
								modal : true,
								minHeight : 200,
								close : function(){
									$confirmmessage.html("");
								},
								buttons: {
									"确认":function(){
										// 使用前点检需要全部点检
										if (infectDetailData.check_while_use) {
											if ($check_sheet.find("td.tcs_input").parents("tr").length != $check_sheet.find("tr[stat=change]").length) {
												errorPop("对“使用前点检”的设备工具需要对所有项目同时点检。");
												return;
											}
										}
										postDataR.refix = 1; // TODO
										postDataR.position_id = infectDetailData.position_id;
										if ($("#upper_check").length>0 && $("#upper_check").attr("checked"))
											postDataR.upper_check = 1;
											
										if(stopDblSubmit) return;
										stopDblSubmit = true;
										doCheckPoint(postDataR, $confirmmessage, $check_sheet);
										if (infectDetailData.check_while_use) infectDetailData.check_while_use(true);
									},
									"取消":function(){
										$confirmmessage.dialog("close");
									}
								}
							})
						}
					}, "关闭" : function(){ $(this).dialog("close"); }
				}
			});
			setTimeout(function(){
				$(".ui-dialog-buttonpane button:contains('okButton') span").text(okButton);
				if ($check_sheet.find("comment[exists]").length > 0) {
					$(".ui-dialog-buttonpane button:contains('备注')").attr("exists", true);
				}
			} , 200);

		}
	});
};

var doCheckPoint = function (postDataR, $confirmmessage, $check_sheet) {
	$.ajax({
		beforeSend : ajaxRequestType,
		async : true,
		url : infectSheetServicePath + "?method=doCheckPoint",
		cache : false,
		data : postDataR,
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : function(xhrObj){
			stopDblSubmit = false;
			if ($confirmmessage) $confirmmessage.dialog("close");
			if ($check_sheet) $check_sheet.dialog("close");
			if (typeof findit === "function") findit();
		}
	})
}
