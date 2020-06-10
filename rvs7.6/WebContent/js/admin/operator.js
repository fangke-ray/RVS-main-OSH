/** 模块名 */
var modelname = "用户";
/** 一览数据对象 */
var listdata = {};
/** 服务器处理路径 */
var servicePath = "operator.do";

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

	setReferChooser($("#hidden_position_id"),$("#search_position_name_referchooser"));
	setReferChooser($("#hidden_af_ability_code"),$("#search_af_ability_referchooser"));

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
		$("#cond_rank_kind").data("post", $("#cond_rank_kind").val());
		$("#cond_job_no").data("post", $("#cond_job_no").val());
		$("#cond_section_id").data("post", $("#cond_section_id").val());
		$("#cond_line_id").data("post", $("#cond_line_id").val());

		// 查询
		findit();
	});

	// 清空检索条件
	$("#resetbutton").click(function() {
		// hidden reset
		$("#cond_rank_kind").val("").trigger("change");
		$("#cond_section_id").val("").trigger("change");
		$("#cond_line_id").val("").trigger("change");
		$("#cond_id").val("");
		$("#cond_name").val("");
		$("#cond_job_no").val("");
		$("#cond_position_name").val("");
		$("#hidden_position_id").val("");

		$("#cond_af_ability").val("");
		$("#hidden_af_ability_code").val("");

		$("#cond_id").data("post", "");
		$("#cond_name").data("post", "");
		$("#cond_rank_kind").data("post", "");
		$("#cond_job_no").data("post", "");
		$("#cond_section_id").data("post", "");
		$("#cond_line_id").data("post", "");

	});

	// 关联工位参照
	setReferChooser($("#input_position"));

	// 编辑工位
	$("#grid_edit_positions tr:not(:first)").click(function(evt){
		var $tr = $(this);

		var actived = $tr.hasClass("ui-state-active");

		if (actived) {
			var $evtTarget = $(evt.target);
			if ($tr.children("td").index($evtTarget) == 2) {
				var notice = $evtTarget.hasClass("ui-state-highlight");

				if (notice) {
					$evtTarget.removeClass("ui-state-highlight");
				} else {
					if ($("#grid_edit_main_position .ui-state-highlight").length + $("#grid_edit_positions .ui-state-highlight").length == 3) {
						errorPop("只能选择3个以内的关注工位。");
					} else {
						$evtTarget.addClass("ui-state-highlight");
					}
				}
				
			} else {
				$tr.removeClass("ui-state-active");
				$tr.find("td.ui-state-highlight").removeClass("ui-state-highlight");
			}
		} else {
			$tr.addClass("ui-state-active");

			if ($tr.children("td").index($(evt.target)) == 2) {
				if ($("#grid_edit_main_position .ui-state-highlight").length + $("#grid_edit_positions .ui-state-highlight").length == 3) {
					errorPop("只能选择3个以内的关注工位。");
				} else {
					$evtTarget.addClass("ui-state-highlight");
				}
			}

			var main_referId = $tr.find("td:first-child").text();
			$("#grid_edit_main_position").find("tr:has(.referId:contains('" + main_referId + "'))").removeClass("ui-state-active");
		}
	});
	$("#grid_edit_main_position tr:not(:first)").click(function(evt){
		var $tr = $(this);

		var actived = $tr.hasClass("ui-state-active");

		if (actived) {
			var $evtTarget = $(evt.target);
			if ($tr.children("td").index($evtTarget) == 2) {
				var notice = $evtTarget.hasClass("ui-state-highlight");

				if (notice) {
					$evtTarget.removeClass("ui-state-highlight");
				} else {
					if ($("#grid_edit_main_position .ui-state-highlight").length + $("#grid_edit_positions .ui-state-highlight").length == 3) {
						errorPop("只能选择3个以内的关注工位。");
					} else {
						$evtTarget.addClass("ui-state-highlight");
					}
				}
				
			} else {
				$tr.removeClass("ui-state-active");
				$tr.find("td.ui-state-highlight").removeClass("ui-state-highlight");
			}
		} else {
			$tr.addClass("ui-state-active");

			if ($tr.children("td").index($(evt.target)) == 2) {
				if ($("#grid_edit_main_position .ui-state-highlight").length + $("#grid_edit_positions .ui-state-highlight").length == 3) {
					errorPop("只能选择3个以内的关注工位。");
				} else {
					$evtTarget.addClass("ui-state-highlight");
				}
			}

			var main_referId = $tr.find("td:first-child").text();
			$("#grid_edit_positions").find("tr:has(.referId:contains('" + main_referId + "'))").removeClass("ui-state-active");
		}
	});
	$("#grid_edit_af_abilities tr:not(:first)").click(function(){
		$(this).toggleClass("ui-state-active");
	});

	$("select").select2Buttons();
	$("#work_count_flg_set").buttonset();

	$("#img_job_no").bind("error", function(){
		$(this).hide();
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
				linedata.role_name = decodeText(linedata.role_name);
				linedata.section_name = decodeText(linedata.section_name);
				linedata.line_name = decodeText(linedata.line_name);
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
					colNames : ['', '用户 ID', '角色', '工号', '用户姓名', '', '所在课室', '所在工程','邮箱地址','分线','最后更新人', '最后更新时间'],
					colModel : [
					{name:'myac', width:60, fixed:true, sortable:false, resize:false, formatter:'actions', formatoptions:{keys:true}},
					{ // 用户 ID
						name : 'id',
						index : 'id',
						width : 50,
						sorttype : "integer",
						hidden:true
					},
					{name:'rank_kind',index:'rank_kind', formatter: 'select',
						editoptions:{value : $("#rkGo").val()}},
					{name:'job_no',index:'job_no', width:80},
					{ // 用户姓名
						name : 'name',
						index : 'name',
						width : 80
					},
					{name:'work_count_flg', hidden:true},
					{name:'section_name',index:'section_name', width:60},
					{name:'line_name',index:'line_name', width:60},
					{name:'email',index:'email', width:130},
					{name:'px',index:'px', align:"center", formatter: 'select',
						editoptions:{value:"0:全部;1:A;2:B;3:C;4:D"}, width:30},
					{ // 最后更新人
						name : 'updated_by',
						index : 'updated_by',
						width : 80, hidden:true
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
					ondblClickRow : showEditS,
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
		eval('resInfo =' + xhrobj.responseText);
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
			$("#editarea span.areatitle").html(modelname + "：" + encodeText(resInfo.operatorForm.name));
			$("#editarea").show();
			$("#editform tr").show();
			$("#editbutton").val("修改");
			$("#editbutton").enable();
			$("#detailarea tr:has(#label_detail_updated_by)").hide();
			$("#detailarea tr:has(#label_detail_updated_time)").hide();
			$(".errorarea-single").removeClass("errorarea-single");
			// 默认画面变化 e
		
			// 详细数据
			$("#label_edit_id").text(resInfo.operatorForm.id);
			$("#input_name").val(resInfo.operatorForm.name);
			$("#input_job_no").val(resInfo.operatorForm.job_no);
			$("#input_label_job_no").text(resInfo.operatorForm.job_no);
			$("#input_line_id").val(resInfo.operatorForm.line_id).trigger('change');
			$("#input_role_id").val(resInfo.operatorForm.role_id).trigger('change');
			$("#input_section_id").val(resInfo.operatorForm.section_id).trigger('change');
			$("#input_account_type").val(resInfo.operatorForm.work_count_flg).trigger('change');
			//$("#work_count_flg_set input[value="+resInfo.operatorForm.work_count_flg+"]").attr("checked", true).trigger('change');
			$("#input_email").val(resInfo.operatorForm.email);
			$("#input_px").val(resInfo.operatorForm.px).trigger('change');
			$("#label_edit_updated_by").text(resInfo.operatorForm.updated_by);
			$("#label_edit_updated_time").text(resInfo.operatorForm.updated_time);
//			$("#grid_edit_main_position").find("tr").removeClass("ui-state-active");
//			$("#grid_edit_main_position tr:has(.referId:contains('"+resInfo.operatorForm.position_id+"'))")
//				.addClass("ui-state-active");

			// 工号不能修改
			$("#input_job_no").hide();
			$("#input_label_job_no").show();
			$("#img_job_no").attr("src", "/images/sign/" + resInfo.operatorForm.job_no).show();

			// 明细数据
			var positions = resInfo.operatorForm.abilities;
			var grid_detail_positions = $("#grid_edit_positions");

			grid_detail_positions.find("tr").removeClass("ui-state-active")
				.find(".ui-state-highlight").removeClass("ui-state-highlight");

			for (var iposition in positions) {
				grid_detail_positions.find("tr:has(.referId:contains('"+positions[iposition]+"'))")
					.addClass("ui-state-active");
			}

			var main_positions = resInfo.operatorForm.main_positions;
			$("#grid_edit_main_position").find("tr").removeClass("ui-state-active")
				.find(".ui-state-highlight").removeClass("ui-state-highlight");

			for (var iposition in main_positions) {
				$("#grid_edit_main_position").find("tr:has(.referId:contains('"+main_positions[iposition]+"'))")
					.addClass("ui-state-active");
			}

			var af_abilities = resInfo.operatorForm.af_abilities;
			$("#grid_edit_af_abilities").find("tr").removeClass("ui-state-active");

			for (var iability in af_abilities) {
				$("#grid_edit_af_abilities").find("tr:has(.referId:contains('"+af_abilities[iability]+"'))")
					.addClass("ui-state-active");
			}

			var notice_positions = resInfo.operatorForm.notice_positions;
			for (var iposition in notice_positions) {
				grid_detail_positions.find("tr.ui-state-active:has(.referId:contains('"+positions[iposition]+"')) td:last-child")
					.addClass("ui-state-highlight");
				$("#grid_edit_main_position").find("tr.ui-state-active:has(.referId:contains('"+main_positions[iposition]+"')) td:last-child")
					.addClass("ui-state-highlight");
			}

			var temp_roles = resInfo.operatorForm.temp_role;
			var roles_id = [];
			for (var irole in temp_roles) {
				roles_id.push(temp_roles[irole]);
			}
			$("#input_roles_id").val(roles_id).trigger('change');

			$("#editform").validate({
				rules : {
					name : {
						required : true
					},
					job_no : {
						required : true
					},
					role_id : {
						required : true
					}
				}
			});

			// 切换按钮效果
			$("#editbutton").unbind("click");
			$("#editbutton").click(function() {
				// 前台Validate
				if ($("#editform").valid()) {
					// 通过Validate,切到修改确认画面
					$("#editbutton").disable();

					warningConfirm("确认要修改记录吗？",
						function() {
							var data = {
								"id" : $("#label_edit_id").text(),
								"name" : $("#input_name").val(),
								"job_no" : $("#input_job_no").val(),
								"work_count_flg" : $("#input_account_type").val(),
								"section_id" : $("#input_section_id").val(),
								"role_id" : $("#input_role_id").val(),
								"line_id" : $("#input_line_id").val(),
//									"position_id" : $("#grid_edit_main_position tr.ui-state-active").find(".referId").html(),
								"email" : $("#input_email").val(),
								"px" : $("#input_px").val()
							}

							$("#grid_edit_positions tr.ui-state-active").each(function(i,item){
								data["abilities[" + i + "]"] = $(item).find(".referId").html();
							});

							$("#grid_edit_main_position tr.ui-state-active").each(function(i,item){
								data["main_positions[" + i + "]"] = $(item).find(".referId").html();
								if (i == 0) {
									data.position_id = data["main_positions[" + i + "]"];
								}
							});

							$("#grid_edit_af_abilities tr.ui-state-active").each(function(i,item){
								data["af_abilities[" + i + "]"] = $(item).find(".referId").html();
							});

							var upd_temp_roles = $("#input_roles_id").val();
							for (var irole in upd_temp_roles) {
								data["temp_role[" + irole + "]"] = upd_temp_roles[irole];
							}

							$("#grid_edit_main_position .ui-state-highlight, #grid_edit_positions .ui-state-highlight").each(function(i,item){
								data["notice_positions[" + i + "]"] = $(item).prev().prev().html();
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
						}, function() {
							$("#editbutton").enable();
						}, "修改确认"
					);
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
		"rank_kind" : $("#cond_rank_kind").data("post"),
		"job_no" : $("#cond_job_no").data("post"),
		"section_id" : $("#cond_section_id").data("post"),
		"line_id" : $("#cond_line_id").data("post"),
		"position_id" : $("#hidden_position_id").val(),
		"af_ability" : $("#hidden_af_ability_code").val()
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
	$("#editform label:not([radio])").html("");
	$("#editbutton").val("新建");
	$("#editbutton").enable();
	$(".errorarea-single").removeClass("errorarea-single");
	// 默认画面变化 e

	// 工位全部清空
	$("#grid_edit_main_position tr").removeClass("ui-state-active");
	$("#grid_edit_positions tr").removeClass("ui-state-active");
	$("#grid_edit_af_abilities tr").removeClass("ui-state-active");

	$("#input_job_no").show();
	$("#input_label_job_no").hide();
	$("#img_job_no").hide();

	// 前台Validate设定
	$("#editform").validate({
		rules : {
			name : {
				required : true
			},
			job_no : {
				required : true
			},
			role_id : {
				required : true
			}
		},
		ignore:""
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
				"job_no" : $("#input_job_no").val(),
				"work_count_flg" : $("#input_account_type").val(),
				"section_id" : $("#input_section_id").val(),
				"role_id" : $("#input_role_id").val(),
				"line_id" : $("#input_line_id").val(),
//				"position_id" : $("#grid_edit_main_position tr.ui-state-active").find(".referId").html(),
				"email" : $("#input_email").val(),
				"px" : $("#input_px").val()
			}

			$("#editarea #grid_edit_positions tr.ui-state-active").each(function(i,item){
				data["abilities[" + i + "]"] = $(item).find(".referId").html();
			});

			$("#editarea #grid_edit_main_position tr.ui-state-active").each(function(i,item){
				data["main_positions[" + i + "]"] = $(item).find(".referId").html();
				if (i == 0) {
					data.position_id = data["main_positions[" + i + "]"];
				}
			});

			$("#editarea #grid_edit_af_abilities tr.ui-state-active").each(function(i,item){
				data["af_abilities[" + i + "]"] = $(item).find(".referId").html();
			});

			$("#grid_edit_main_position .ui-state-highlight, #grid_edit_positions .ui-state-highlight").each(function(i,item){
				data["notice_positions[" + i + "]"] = $(item).prev().prev().html();
			});

			var ist_temp_roles = $("#input_roles_id").val();
			for (var irole in ist_temp_roles) {
				data["temp_role[" + irole + "]"] = ist_temp_roles[irole];
			}

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
				complete : update_handleComplete
			});
		};
	});
};

/**
 * 切到修改画面
 */
var showEditS = function(rid) {
	// 读取修改行
	var rowData = $("#list").getRowData(rid);
	var data = {
		"id" : rowData.id
	}

	// Ajax提交
	$.ajax({
		beforeSend : ajaxRequestType,
		async : true,
		url : servicePath + '?method=showedit',
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

/**
 * 发布密码
 */
var showEdit = function(rid) {

	// 读取删除行
	var rowData = $("#list").getRowData(rid);
	var date = new Date();
	var newpwd = "Day" + date.getFullYear() + fillZero(date.getMonth() + 1) + fillZero(date.getDate());
	var data = {
		"id" : rowData.id,
		"pwd" : newpwd,
		"job_no" : rowData.job_no
	}

	warningConfirm("你要帮["+encodeText(rowData.name)+"]变更登录密码为：“"+newpwd+"”吗？",
		function() {
			// Ajax提交
			$.ajax({
				beforeSend : ajaxRequestType,
				async : false,
				url : servicePath + '?method=dogeneratepasswd',
				cache : false,
				data : data,
				type : "post",
				dataType : "json",
				success : ajaxSuccessCheck,
				error : ajaxError,
				complete : update_handleComplete
			});
		}, null, "重设密码"
	);
};
