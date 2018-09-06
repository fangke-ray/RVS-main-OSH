/** 模块名 */
var modelname = "工程";
/** 一览数据对象 */
var listdata = {};
/** 服务器处理路径 */
var servicePath = "line.do";

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
		$("#cond_inline_flg_set").data("post", $("#cond_inline_flg_set input:checked").val());
		// 查询
		findit();
	});

	// 清空检索条件
	$("#resetbutton").click(function() {
		$("#cond_id").data("post", "");
		$("#cond_name").data("post", "");
		$("#cond_inline_flg_set").data("post", "");
	});

	$("#cond_inline_flg_set,#input_inline_flg_set").buttonset();

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
					colNames : ['', '工程 ID', '工程名称', '计划管理对象', '最后更新人', '最后更新时间'],
					colModel : [
					{name:'myac', width:48, fixed:true, sortable:false, resize:false, formatter:'actions', formatoptions:{keys:true, editbutton:false}},
					{ // 工程 ID
						name : 'id',
						index : 'id',
						width : 50,
						sorttype : "integer",
						hidden : true
					}, { // 工程名称
						name : 'name',
						index : 'name',
						width : 160
					}, { // 计划管理对象
						name : 'inline_flg',
						index : 'inline_flg',
						width : 40,
						formatter : 'select',
						editoptions:{value:"0:;1:是"}
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
			$("#editarea span.areatitle").html(modelname + "：" + encodeText(resInfo.lineForm.name));
			$("#editarea").show();
			$("#editform tr").show();
			$("#editbutton").val("修改");
			$("#editbutton").enable();
			$("#detailarea tr:has(#label_detail_updated_by)").hide();
			$("#detailarea tr:has(#label_detail_updated_time)").hide();
			$(".errorarea-single").removeClass("errorarea-single");
			// 默认画面变化 e

			// 详细数据
			$("#label_edit_id").text(resInfo.lineForm.id);
			$("#input_name").val(resInfo.lineForm.name);
			$("#input_inline_flg_set input[value='"+ resInfo.lineForm.inline_flg +"']").attr("checked", true).trigger("change");
			$("#label_edit_updated_by").text(resInfo.lineForm.updated_by);
			$("#label_edit_updated_time").text(resInfo.lineForm.updated_time);

			$("#editform").validate({
				rules : {
					name : {
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

					$("#confirmmessage").text("确认要修改记录吗？");
				 	$("#confirmmessage").dialog({
						resizable : false,
						modal : true,
						title : "修改确认",
						close: function() {
							$("#editbutton").enable();
						},
						buttons : {
							"确认" : function() {
								var data = {
									"id" : $("#label_edit_id").text(),
									"inline_flg" : $("#input_inline_flg_set input:checked").val(),
									"name" : $("#input_name").val()
								}
								$(this).dialog("close");
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
							},
							"取消" : function() {
								$(this).dialog("close");
							}
						}
					});
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
		"inline_flg" : $("#cond_inline_flg_set").data("post")
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
	$("#editform input[type!='button'][type!='radio'], #editform select, #editform textarea").val("");
	$("#editform select").val("").trigger("change");
	$("#editform label:not([radio])").html("");
	$("#editbutton").val("新建");
	$("#editbutton").enable();
	$(".errorarea-single").removeClass("errorarea-single");
	// 默认画面变化 e

	// 前台Validate设定
	$("#editform").validate({
		rules : {
			name : {
				required : true
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
				"inline_flg" : $("#input_inline_flg_set input:checked").val(),
				"name" : $("#input_name").val()
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
