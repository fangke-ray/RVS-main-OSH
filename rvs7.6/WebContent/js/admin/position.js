/** 模块名 */
var modelname = "工位";
/** 一览数据对象 */
var listdata = {};
/** 服务器处理路径 */
var servicePath = "position.do";

var light_division_flg_button='<input type="radio" name="light_division_flg" id="light_division_flg_all" class="ui-widget-content" value="">'
							 +'<label for="light_division_flg_all">(全)</label>'
							 +'<input type="radio" name="light_division_flg" id="light_division_flg_yes" class="ui-widget-content" value="1">'
							 +'<label for="light_division_flg_yes">是</label>'
							 +'<input type="radio" name="light_division_flg" id="light_division_flg_no" class="ui-widget-content" value="2" checked>'
							 +'<label for="light_division_flg_no">否</label>';
					
/**
 * 页面加载处理
 */
$(function() {
	// 适用jquery按钮
	$("input.ui-button").button();

	// 右上图标效果
	$("a.areacloser").hover(function() { $(this).addClass("ui-state-hover");},
		function() {$(this).removeClass("ui-state-hover");});
	$("#searcharea span.ui-icon").bind("click", function() {
		$(this).toggleClass('ui-icon-circle-triangle-n').toggleClass('ui-icon-circle-triangle-s');
		if ($(this).hasClass('ui-icon-circle-triangle-n')) {
			$(this).parent().parent().next().show("blind");
		} else {
			$(this).parent().parent().next().hide("blind");
		}
	});
	$("#cancelbutton, #editarea span.ui-icon").click(function() {
		showList();
	});

	// 初始状态隐藏编辑和详细显示
	$("#editarea").hide();
	$("#detailarea").hide();

	// 检索表单认证(没有前台认证也要写,为了对应后台出错时效果)
	$("#searchform").validate({});
	// ///////////////////以上共通///////////////////////

	// 检索处理
	$("#searchbutton").click(function() {
		// 保存检索条件
		$("#cond_id").data("post", $("#cond_id").val());
		$("#cond_name").data("post", $("#cond_name").val());
		$("#cond_line_id").data("post", $("#cond_line_id").val());
		$("#cond_process_code").data("post", $("#cond_process_code").val());

		// 查询
		findit();
	});

	// 清空检索条件
	$("#resetbutton").click(function() {
		$("#cond_id").val("").data("post", "");
		$("#cond_name").val("").data("post", "");
		$("#cond_line_id").val("").data("post", "").trigger("change");
		$("#cond_process_code").val("").data("post", "");
	});

	$("select").select2Buttons();

	$("#set_group_button").click(function() {
		if (this.value=="设定") {
			$("#group_content").show();
			this.value="取消";
		} else {
			$("#group_content").hide()
				.find("tbody > tr").remove();
			$("#label_set_group").text("未设定");
			this.value="设定";
		}
	});
	$("#group_content .group_content_add").click(addGroupContentInput);
	$("#group_content").on("click", ".group_content_remove", removeGroupContentInput);

	$("#mapping_button").click(setPositionMapping);
	$("#mapping_anml_button").click(setPositionUnitized);

	$("#mapping_dialog").on("click", ".subform tr", function(){
		$(this).toggleClass("ui-state-active");
	});

	findit();
});

/**
 * 检索Ajax通信成功时的处理
 */
var search_handleComplete = function(xhrobj, textStatus) {
	var resInfo = null;
	try {
		// 以Object形式读取JSON
		eval('resInfo =' + xhrobj.responseText);

		if (resInfo.errors.length > 0) {
			// 共通出错信息框
			treatBackMessages("#searcharea", resInfo.errors);
		} else {
			// 标题修改
			top.document.title = modelname + "一览";

			// 读取一览
			listdata = resInfo.list;
			// HTML Decode
			for (var line in listdata) {
				var linedata = listdata[line];
				linedata.name = decodeText(linedata.name);
			}

			if ($("#gbox_list").length > 0) {
				// jqGrid已构建的情况下,重载数据并刷新
				$("#list").jqGrid().clearGridData();
				$("#list").jqGrid('setGridParam', {data : listdata}).trigger("reloadGrid", [{current : false}]);
			} else {
				// 构建jqGrid对象
				$("#list").jqGrid({
					data : listdata,
					height : 461,
					width : gridWidthMiddleRight,
					rowheight : 23,
					datatype : "local",
					colNames : ['', '工位 ID', '工程', '进度代码', '工位名称', '最后更新人', '最后更新时间'],
					colModel : [
					{name:'myac', width:48, fixed:true, sortable:false, resize:false, formatter:'actions', formatoptions:{keys:true, editbutton:false}},
					{ // 工位 ID
						name : 'id',
						index : 'id',
						width : 50,
						sorttype : "integer",
						hidden : true
					},
						{name:'line_name',index:'line_name', width:80},
						{name:'process_code',index:'process_code', width:40},
					{ // 工位名称
						name : 'name',
						index : 'name',
						width : 160
					}, { // 最后更新人
						name : 'updated_by',
						index : 'updated_by',
						width : 80
					}, { // 最后更新时间
						name : 'updated_time',
						index : 'updated_time',
						width : 80
					}],
					toppager : false,
					rowNum : 20,
					pager : "#listpager",
					viewrecords : true,
					caption : modelname + "一览",
					ondblClickRow : showEdit,
					gridview : true, // Speed up
					pagerpos : 'right',
					pgbuttons : true,
					pginput : false,
					recordpos : 'left',
					viewsortcols : [true, 'vertical', true]
				});
				$(".ui-jqgrid-hbox").before('<div class="ui-widget-content" style="padding:4px;">' +
						'<input type="button" class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="addbutton" value="新建'+ modelname +'" role="button" aria-disabled="false">' +
					'</div>');
				$("#addbutton").button();
				// 追加处理
				$("#addbutton").click(showAdd);
			}

		}
	} catch (e) {
		console.log("name: " + e.name + " message: " + e.message + " lineNumber: "
				+ e.lineNumber + " fileName: " + e.fileName);
	};
};

/**
 * 更新完成Ajax通信成功时的处理
 */
var update_handleComplete = function(xhrobj, textStatus) {
	var resInfo = null;
	try {
		// 以Object形式读取JSON
		resInfo = $.parseJSON(xhrobj.responseText);
		if (resInfo.errors.length > 0) {
			// 共通出错信息框
			treatBackMessages("#editarea", resInfo.errors);
			// 编辑确认按钮重新有效
			$("#editbutton").enable();
			$("#executebutton").enable();
		} else {
			// 重新查询
			findit();
			// 切回一览画面
			showList();
		}
	} catch (e) {
		console.log("name: " + e.name + " message: " + e.message + " lineNumber: "
				+ e.lineNumber + " fileName: " + e.fileName);
	};
}

/**
 * 读取修改详细信息Ajax通信成功时的处理
 */
var showedit_handleComplete = function(xhrobj, textStatus) {
	var resInfo = null;
	try {
		// 以Object形式读取JSON
		eval('resInfo =' + xhrobj.responseText);
		if (resInfo.errors.length > 0) {
			// 共通出错信息框
			treatBackMessages(null, resInfo.errors);
		} else {

			// 默认画面变化 s
			top.document.title = modelname + "修改";
			$("#searcharea").hide();
			$("#listarea").hide();
			$("#editarea span.areatitle").html(modelname + "：" + encodeText(resInfo.positionForm.name));
			$("#editarea").show();
			$("#editform tr").show();
			$("#editbutton").val("修改");
			$("#editbutton").enable();
			$("#detailarea tr:has(#label_detail_updated_by)").hide();
			$("#detailarea tr:has(#label_detail_updated_time)").hide();
			$(".errorarea-single").removeClass("errorarea-single");
			// 默认画面变化 e

			// 详细数据
			$("#label_edit_id").text(resInfo.positionForm.id);
			$("#input_name").val(resInfo.positionForm.name);
			$("#input_label_process_code").text(resInfo.positionForm.process_code);
			$("#input_line_id").val(resInfo.positionForm.line_id).trigger("change");
			$("#label_edit_updated_by").text(resInfo.positionForm.updated_by);
			$("#label_edit_updated_time").text(resInfo.positionForm.updated_time);

			$("#input_special_page").val(resInfo.positionForm.special_page).trigger("change");

			$("#input_light_worktime_rate").val(resInfo.positionForm.light_worktime_rate);
			$("#input_light_division_flg").html("").html(light_division_flg_button);
			$("#light_division_flg_all,label[for='light_division_flg_all']").remove();
			$("#input_light_division_flg").buttonset();
			var light_division_flg = resInfo.positionForm.light_division_flg;
			if(light_division_flg==1){
				$("#light_division_flg_yes").attr("checked","checked").trigger("change");
			}else if(light_division_flg==0){
				$("#light_division_flg_no").attr("checked","checked").trigger("change");
			}

			// 进度代码不能修改
			$("#input_process_code").hide();
			$("#input_label_process_code").show();

			$("#editform").validate({
				rules : {
					name : {
						required : true
					}
				}
			});

			$("#mapping_tr").show();
			if (resInfo.positionForm.mapping_position_id) {
				$("#mapping_span").text(resInfo.positionForm.mapping_position_id)
					.data("positionMappings", null)
					.show();
				$("#mapping_button").hide();
			} else {
				
				$("#mapping_button").show();
				if (resInfo.positionMappings) {
					$("#mapping_span").text("已有设置")
						.data("positionMappings", resInfo.positionMappings)
						.show();
				} else {
					$("#mapping_span").text("")
						.data("positionMappings", null)
						.hide();
				}
			}
			if (resInfo.positionForm.unitized_position_id) {
				$("#mapping_anml_span").text(resInfo.positionForm.unitized_position_id)
					.data("positionMappings", null)
					.show();
				$("#mapping_button").hide();
			} else {
				
				$("#mapping_button").show();
				if (resInfo.positionUnitizeds) {
					$("#mapping_anml_span").text("已有设置")
						.data("positionMappings", resInfo.positionUnitizeds)
						.show();
				} else {
					$("#mapping_anml_span").text("")
						.data("positionMappings", null)
						.hide();
				}
			}

			// 虚拟工位组
			if (resInfo.positionGroup) {
				$("#group_content").show()
					.find("tbody > tr").remove();
				for (var iPg in resInfo.positionGroup) {
					var subPosition = resInfo.positionGroup[iPg];
					var subPositionId = subPosition.sub_position_id;
					var nextPositionId = subPosition.next_position_id || "";
					var hit = false;
					var subPositionCode = $("#pReferChooser").find(".referId").filter(function(){
						if (!hit) {
							if (this.innerText == subPositionId) {
								hit = true;
								return true;
							}
						}
					}).next().text();
					hit = false;
					var nextPositionCode = "";
					if (nextPositionId) {
						nextPositionCode = $("#pReferChooser").find(".referId").filter(function(){
							if (!hit) {
								if (this.innerText == nextPositionId) {
									hit = true;
									return true;
								}
							}
						}).next().text();
					}
					var line = "<tr><td><input type='text' readonly value='" + subPositionCode 
						+ "'><input type='hidden' class='group_content_sub_position_id' value='" + subPositionId; 

						if (nextPositionId) {
							line += "'></td><td><input type='text' readonly value='" + nextPositionCode 
							+ "'><input type='hidden' class='group_content_next_position_id' value='" + nextPositionId + "'";
						} else {
							line += "'></td><td><input type='text' readonly><input type='hidden' class='group_content_next_position_id'";
						}

						if (subPosition.control_trigger != undefined) {
							line += "></td><td><input type='number' class='group_content_control_trigger' value='" + subPosition.control_trigger + "'";
						} else {
							line += "></td><td><input type='number' class='group_content_control_trigger'";
						}
						line += "></td><td><input type='button' class='ui-button group_content_remove' value='-'></td></tr>";
					var $line = $(line);
					$line.find("input:button").button();
					setReferChooser($line.find(".group_content_sub_position_id"), $("#pReferChooser"));
					setReferChooser($line.find(".group_content_next_position_id"), $("#pReferChooser"));
					$("#group_content > tbody").append($line);
				}
				$("#label_set_group").text("已设定");
				$("#set_group_button").val("取消");
			} else {
				$("#group_content").hide()
					.find("tbody > tr").remove();
				$("#label_set_group").text("未设定");
				$("#set_group_button").val("设定");
			}

			// 切换按钮效果
			$("#editbutton").unbind("click");
			$("#editbutton").click(function() {
				// 前台Validate
				if ($("#editform").valid()) {

					// 通过Validate,切到修改确认画面
					$("#editbutton").disable();

					warningConfirm("确认要修改[" + encodeText(resInfo.positionForm.name) + "]的记录吗？", function() {
						var data = {
							"id" : $("#label_edit_id").text(),
							"line_id" : $("#input_line_id").val(),
							"name" : $("#input_name").val(),
							"special_page" : $("#input_special_page").val(),
							"light_worktime_rate" : $("#input_light_worktime_rate").val(),
							"light_division_flg" : $("#input_light_division_flg input[type='radio']:checked").val()
						}

						var i = 0;

						$("#group_content > tbody > tr").each(function(idx, item) {
							var $tr = $(item);
							var sub_position_id = $tr.find(".group_content_sub_position_id").val();
							if (sub_position_id) {
								data["group.sub_position_id[" + i + "]"] = sub_position_id;
								var next_position_id = $tr.find(".group_content_next_position_id").val();
								if (next_position_id) {
									data["group.next_position_id[" + i + "]"] = next_position_id;
									var control_trigger = $tr.find(".group_content_control_trigger").val();
									if (control_trigger) {
										data["group.control_trigger[" + i + "]"] = control_trigger;
									}
								}
								i++;
							}
						});

						// Ajax提交
						$.ajax({
							beforeSend : ajaxRequestType,
							async : true,
							url : servicePath + '?method=doupdate',
							cache : false,
							data : data,
							type : "post",
							dataType : "json",
							success : ajaxSuccessCheck,
							error : ajaxError,
							complete : update_handleComplete
						});

					}, function(){
						$("#editbutton").enable();
					}, "修改确认");
					
				};
			});
		}
	} catch (e) {
		console.log("name: " + e.name + " message: " + e.message + " lineNumber: "
				+ e.lineNumber + " fileName: " + e.fileName);
	};
}


/** 
 * 检索处理
 */
var findit = function() {
	// 读取已记录检索条件提交给后台
	var data = {
		"id" : $("#cond_id").data("post"),
		"name" : $("#cond_name").data("post"),
		"line_id" : $("#cond_line_id").data("post"),
		"process_code" : $("#cond_process_code").data("post")
	}

	// Ajax提交
	$.ajax({
		beforeSend : ajaxRequestType,
		async : true,
		url : servicePath + '?method=search',
		cache : false,
		data : data,
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : search_handleComplete
	});
};

/**
 * 切到一览画面
 */
var showList = function() {
	top.document.title = modelname + "一览";
	$("#searcharea").show();
	$("#listarea").show();
	$("#editarea").hide();
	$("#detailarea").hide();
};

/**
 * 切到新建画面
 */
var showAdd = function() {
	// 默认画面变化 s
	top.document.title = "新建" + modelname;
	$("#searcharea").hide();
	$("#listarea").hide();
	$("#editarea span.areatitle").html("新建" + modelname);
	$("#editarea").show();
	$("#editform .condform tr:not(:has(input,textarea,select))").hide();
	$("#editform input[type!='button'], #editform textarea").val("");
	$("#editform select").val("").trigger("change");
	$("#editform label").html("");
	$("#editbutton").val("新建");
	$("#editbutton").enable();
	$(".errorarea-single").removeClass("errorarea-single");
	// 默认画面变化 e

	$("#input_process_code").show();
	$("#input_label_process_code").hide();
	
	$("#input_light_division_flg").parent().show();
	$("#input_light_division_flg").html("").html(light_division_flg_button);
	$("#input_light_division_flg").buttonset();
	$("#light_division_flg_all").attr("checked","checked").trigger("change");

	$("#mapping_tr").hide();

	$("#group_content").hide()
		.find("tbody > tr").remove();
	$("#label_set_group").text("未设定");
	$("#set_group_button").val("设定");

	// 前台Validate设定
	$("#editform").validate({
		rules : {
			name : {
				required : true
			},
			process_code : {
				required : true
//				,
//				number : true
			},
			light_worktime_rate:{
				digits:true,
				range:[0,500]
			}
		}
	});
	// 切换按钮效果
	$("#editbutton").unbind("click");
	$("#editbutton").click(function() {
		// 前台Validate
		if ($("#editform").valid()) {
			// 通过Validate,切到新建确认画面
			$("#editbutton").disable();
			// 新建画面输入项提交给后台
			var data = {
				"name" : $("#input_name").val(),
				"line_id" : $("#input_line_id").val(),
				"process_code" : $("#input_process_code").val(),
				"special_page" : $("#input_special_page").val(),
				"light_worktime_rate":$("#input_light_worktime_rate").val(),
				"light_division_flg":$("#input_light_division_flg input[type='radio']:checked").val()
			}

			var i = 0;

			$("#group_content > tbody > tr").each(function(idx,item){
				var $tr = $(item);
				var sub_position_id = $tr.find(".group_content_sub_position_id").val();
				if (sub_position_id) {
					data["group.sub_position_id[" + i + "]"] = sub_position_id;
					var next_position_id = $tr.find(".group_content_next_position_id").val();
					if (next_position_id) {
						data["group.next_position_id[" + i + "]"] = next_position_id;
						var control_trigger = $tr.find(".group_content_control_trigger").val();
						if (control_trigger) {
							data["group.control_trigger[" + i + "]"] = control_trigger;
						}
					}
					i++;
				}
			});

			// Ajax提交
			$.ajax({
				beforeSend : ajaxRequestType,
				async : true,
				url : servicePath + '?method=doinsert',
				cache : false,
				data : data,
				type : "post",
				dataType : "json",
				success : ajaxSuccessCheck,
				error : ajaxError,
				complete : function(xhrobj){
					var resInfo = $.parseJSON(xhrobj.responseText);
					if (resInfo.errors.length > 0) {
						// 编辑确认按钮重新有效
						$("#editbutton").enable();
						if (resInfo.errors.length === 1 && resInfo.errors[0].errcode === "info.master.position.columnNotUniqueSetTemp") {
							warningConfirm(resInfo.errors[0].errmsg,
								function() {
									data["delete_flg"] = 2;
									$.ajax({
										beforeSend : ajaxRequestType,
										async : true,
										url : servicePath + '?method=doinsert',
										cache : false,
										data : data,
										type : "post",
										dataType : "json",
										success : ajaxSuccessCheck,
										error : ajaxError,
										complete : update_handleComplete
									});
								}
							);
						} else {
							// 共通出错信息框
							treatBackMessages("#editarea", resInfo.errors);
						}

					} else {
						// 重新查询
						findit();
						// 切回一览画面
						showList();
					}
				}
			});
		};
	});
};

/**
 * 切到修改画面
 */
var showEdit = function(rid) {
	// 读取修改行
	var rowData = $("#list").getRowData(rid);
	var data = {
		"id" : rowData.id
	}

	// Ajax提交
	$.ajax({
		beforeSend : ajaxRequestType,
		async : true,
		url : servicePath + '?method=detail',
		cache : false,
		data : data,
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : showedit_handleComplete
	});
};

/**
 * 切到删除画面
 */
var showDelete = function(rid) {

	// 读取删除行
	var rowData = $("#list").getRowData(rid);
	var data = {
		"id" : rowData.id
	}

	warningConfirm("删除不能恢复。确认要删除["+encodeText(rowData.name)+"]的记录吗？",
		function() {
			// Ajax提交
			$.ajax({
				beforeSend : ajaxRequestType,
				async : false,
				url : servicePath + '?method=dodelete',
				cache : false,
				data : data,
				type : "post",
				dataType : "json",
				success : ajaxSuccessCheck,
				error : ajaxError,
				complete : update_handleComplete
			});
		}, null, "删除确认"
	);
};

var addGroupContentInput = function() {
	var $line = $("<tr><td><input type='text' readonly><input type='hidden' class='group_content_sub_position_id'></td><td><input type='text' readonly><input type='hidden' class='group_content_next_position_id'></td><td><input type='number' class='group_content_control_trigger'></td><td><input type='button' class='ui-button group_content_remove' value='-'></td></tr>");
	$line.find("input:button").button();
	setReferChooser($line.find(".group_content_sub_position_id"), $("#pReferChooser"));
	setReferChooser($line.find(".group_content_next_position_id"), $("#pReferChooser"));
	$("#group_content > tbody").append($line);
	$("#label_set_group").text("设定中");
};
var removeGroupContentInput = function() {
	$(this).closest("tr").remove();
	if ($("#group_content > tbody > tr").length == 0) {
		$("#label_set_group").text("未设定");
	}
}

var setPositionMapping = function() {
	$mappingDialog = $("#mapping_dialog");
	if (!$mappingDialog.html()) {
		$mappingDialog.html($("#pReferChooser .subform").clone());
	} else {
		$("#mapping_dialog tr.ui-state-active").removeClass("ui-state-active");
	}

	var positionMappings = $("#mapping_span").data("positionMappings");
	if (positionMappings) {
		for (var idx in positionMappings) {
			$mappingDialog.find("tr:has(.referId:contains('"+positionMappings[idx]+"'))")
				.addClass("ui-state-active");
		}
	}
	$mappingDialog.dialog({
		title : $("#input_label_process_code").text() + "工位对应大修理工位列表",
		height : 640,
		resizable : false,
		modal : true,
		minHeight : 200,
		buttons : {
			"确认" : function(){
				var postData = {"id" : $("#label_edit_id").text()};

				positionMappings = []; 

				$("#mapping_dialog tr.ui-state-active").each(function(i,item){
					var mappingId = $(item).find(".referId").html();
					postData["mappings[" + i + "]"] = mappingId;
					positionMappings.push(mappingId);
				});

				$("#mapping_span").data("positionMappings", positionMappings);
	
				// Ajax提交
				$.ajax({
					beforeSend : ajaxRequestType,
					async : true,
					url : servicePath + '?method=domapping',
					cache : false,
					data : postData,
					type : "post",
					dataType : "json",
					success : ajaxSuccessCheck,
					error : ajaxError,
					complete : function(xhrObj) {
						var resInfo = $.parseJSON(xhrObj.responseText);
						if (resInfo.errors && resInfo.errors.length > 0) {
							// 共通出错信息框
							treatBackMessages("#editarea", resInfo.errors);
						} else {
							$mappingDialog.dialog("close");
						}
					}
				});
			},
			"清除" : function(){
				warningConfirm("是否要清除" + $("#input_label_process_code").text() + "工位的全部对应关系，使得此工位不能对应大修理的工作岗位？", function(){
					$("#mapping_span").data("positionMappings", null);
					var postData = {"id" : $("#label_edit_id").text()};

					// Ajax提交
					$.ajax({
						beforeSend : ajaxRequestType,
						async : true,
						url : servicePath + '?method=domapping',
						cache : false,
						data : postData,
						type : "post",
						dataType : "json",
						success : ajaxSuccessCheck,
						error : ajaxError,
						complete : function(xhrObj) {
							var resInfo = $.parseJSON(xhrObj.responseText);
							if (resInfo.errors && resInfo.errors.length > 0) {
								// 共通出错信息框
								treatBackMessages("#editarea", resInfo.errors);
							} else {
								$mappingDialog.dialog("close");
							}
						}
					});					
				});
			},
			"关闭" : function(){
				$mappingDialog.dialog("close");
			}
		}
	});
}

var setPositionUnitized = function() {
	$mappingDialog = $("#mapping_dialog");
	if (!$mappingDialog.html()) {
		$mappingDialog.html($("#pReferChooser .subform").clone());
	} else {
		$("#mapping_dialog tr.ui-state-active").removeClass("ui-state-active");
	}

	var positionMappings = $("#mapping_anml_span").data("positionMappings");
	if (positionMappings) {
		for (var idx in positionMappings) {
			$mappingDialog.find("tr:has(.referId:contains('"+positionMappings[idx]+"'))")
				.addClass("ui-state-active");
		}
	}
	$mappingDialog.dialog({
		title : $("#input_label_process_code").text() + "工位对应流水线工位列表",
		height : 640,
		resizable : false,
		modal : true,
		minHeight : 200,
		buttons : {
			"确认" : function(){
				var postData = {"id" : $("#label_edit_id").text()};

				positionMappings = []; 

				$("#mapping_dialog tr.ui-state-active").each(function(i,item){
					var mappingId = $(item).find(".referId").html();
					postData["mappings[" + i + "]"] = mappingId;
					positionMappings.push(mappingId);
				});

				$("#mapping_anml_span").data("positionMappings", positionMappings);
	
				// Ajax提交
				$.ajax({
					beforeSend : ajaxRequestType,
					async : true,
					url : servicePath + '?method=domappingUnitized',
					cache : false,
					data : postData,
					type : "post",
					dataType : "json",
					success : ajaxSuccessCheck,
					error : ajaxError,
					complete : function(xhrObj) {
						var resInfo = $.parseJSON(xhrObj.responseText);
						if (resInfo.errors && resInfo.errors.length > 0) {
							// 共通出错信息框
							treatBackMessages("#editarea", resInfo.errors);
						} else {
							$mappingDialog.dialog("close");
						}
					}
				});
			},
			"清除" : function(){
				warningConfirm("是否要清除" + $("#input_label_process_code").text() + "工位的全部对应关系，使得此工位不能对应流水线工作岗位？", function(){
					$("#mapping_anml_span").data("positionMappings", null);
					var postData = {"id" : $("#label_edit_id").text()};

					// Ajax提交
					$.ajax({
						beforeSend : ajaxRequestType,
						async : true,
						url : servicePath + '?method=domappingUnitized',
						cache : false,
						data : postData,
						type : "post",
						dataType : "json",
						success : ajaxSuccessCheck,
						error : ajaxError,
						complete : function(xhrObj) {
							var resInfo = $.parseJSON(xhrObj.responseText);
							if (resInfo.errors && resInfo.errors.length > 0) {
								// 共通出错信息框
								treatBackMessages("#editarea", resInfo.errors);
							} else {
								$mappingDialog.dialog("close");
							}
						}
					});					
				});
			},
			"关闭" : function(){
				$mappingDialog.dialog("close");
			}
		}
	});
}