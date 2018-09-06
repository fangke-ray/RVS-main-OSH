/** 模块名 */
var modelname = "系统权限";
/** 一览数据对象 */
var listdata = {};
/** 服务器处理路径 */
var servicePath = "privacy.do";

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
	$("#backbutton, #detailarea span.ui-icon").click(function() {
		showEditBack();
	});

	// 初始状态隐藏编辑和详细显示
	$("#editarea").hide();
	$("#detailarea").hide();

	// 追加处理
	$("#addbutton").click(showAdd);

	// 检索表单认证(没有前台认证也要写,为了对应后台出错时效果)
	$("#searchform").validate({});
	// ///////////////////以上共通///////////////////////

	// 检索处理
	$("#searchbutton").click(function() {
		// 保存检索条件
		$("#cond_id").data("post", $("#cond_id").val());
		$("#cond_name").data("post", $("#cond_name").val());

		// 查询
		findit();
	});

	// 清空检索条件
	$("#resetbutton").click(function() {
		// hidden reset
		$("#cond_privacy").val("");

		$("#cond_id").data("post", "");
		$("#cond_name").data("post", "");
		$("#cond_privacy").data("post", "");
	});
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
				linedata.comments = decodeText(linedata.comments);
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
					colNames : ['', '系统权限 ID', '系统权限名称', '权限说明'],
					colModel : [
					{name:'myac', width:60, fixed:true, sortable:false, resize:false, formatter:'actions', formatoptions:{keys:true}},
					{ // 系统权限 ID
						name : 'id',
						index : 'id',
						width : 50,
						sorttype : "integer"
					}, { // 系统权限名称
						name : 'name',
						index : 'name',
						width : 160
					}, { // 权限说明
						name : 'comments',
						index : 'comments',
						width : 300
					}],
					toppager : false,
					rowNum : 20,
					pager : "#listpager",
					viewrecords : true,
					caption : modelname + "一览",
					ondblClickRow : showDetail,
					gridview : true, // Speed up
					pagerpos : 'right',
					pgbuttons : true,
					pginput : false,
					recordpos : 'left',
					viewsortcols : [true, 'vertical', true]
				});
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
			// 切回编辑画面
			showEditBack();
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
 * 读取详细信息Ajax通信成功时的处理
 */
var detail_handleComplete = function(xhrobj, textStatus) {
	var resInfo = null;
	try {
		// 以Object形式读取JSON
		eval('resInfo =' + xhrobj.responseText);
		if (resInfo.errors.length > 0) {
			// 共通出错信息框
			treatBackMessages(null, resInfo.errors);
		} else {
			// 默认画面变化 s
			top.document.title = modelname + "详细";
			$("#searcharea").hide();
			$("#listarea").hide();
			$("#editarea").hide();
			$("#detailarea span.areatitle").html(modelname + "详细信息");
			$("#detailarea").show();
			$("#detailarea tr").show();
			$("#executebutton").val("");
			$("#executebutton").hide();
			// 默认画面变化 e

			// 详细数据
			$("#label_detail_id").text(resInfo.privacyForm.id);
			$("#label_detail_name").text(resInfo.privacyForm.name);
			$("#label_detail_comments").text(resInfo.privacyForm.comments);
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
			$("#editarea span.areatitle").html(modelname + "：" + encodeText(resInfo.privacyForm.name));
			$("#editarea").show();
			$("#editform tr").show();
			$("#editbutton").val("修改");
			$("#editbutton").enable();
			$("#detailarea tr:has(#label_detail_updated_by)").hide();
			$("#detailarea tr:has(#label_detail_updated_time)").hide();
			$(".errorarea-single").removeClass("errorarea-single");
			// 默认画面变化 e
		
			// 详细数据
			$("#label_edit_id").text(resInfo.privacyForm.id);
			$("#input_name").val(resInfo.privacyForm.name);
			$("#input_comments").val(resInfo.privacyForm.comments);

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
					showEditConfirm();
				};
			});
		}
	} catch (e) {
		console.log("name: " + e.name + " message: " + e.message + " lineNumber: "
				+ e.lineNumber + " fileName: " + e.fileName);
	};
}

/**
 * 读取删除详细信息Ajax通信成功时的处理
 */
var showdelete_handleComplete = function(xhrobj, textStatus) {
	var resInfo = null;
	try {
		// 以Object形式读取JSON
		eval('resInfo =' + xhrobj.responseText);
		if (resInfo.errors.length > 0) {
			// 共通出错信息框
			treatBackMessages(null, resInfo.errors);
		} else {

			// 默认画面变化 s
			top.document.title = modelname + "删除";
			$("#searcharea").hide();
			$("#listarea").hide();
			$("#editarea").hide();
			$("#detailarea span.areatitle").html(modelname + "：" + encodeText(resInfo.privacyForm.name) + "删除");
			$("#detailarea").show();
			$("#detailarea tr").show();
			$("#executebutton").val("实行删除");
			$("#executebutton").enable();
			$("#executebutton").show();
			// 默认画面变化 e

			// 详细数据
			$("#label_detail_id").text(resInfo.privacyForm.id);
			$("#label_detail_name").text(resInfo.privacyForm.name);
			$("#label_detail_comments").text(resInfo.privacyForm.comments);

			$("#executebutton").unbind("click");
			$("#executebutton").click(function() {
				$("#executebutton").disable();
				var data = {
					"id" : $("#label_detail_id").text()
				}
		
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
		"name" : $("#cond_name").data("post")
	}

	// Ajax提交
	$.ajax({
		beforeSend : ajaxRequestType,
		async : false,
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
	$("#editform tr:not(:has(input,textarea,select))").hide();
	$("#editform input[type!='button'], #editform select, #editform textarea").val("");
	$("#editform label").html("");
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
			showAddConfirm();
		};
	});
};

/**
 * 切到新建确认画面
 */
var showAddConfirm = function() {

	// 默认画面变化 s
	top.document.title = "新建" + modelname;
	$("#searcharea").hide();
	$("#listarea").hide();
	$("#editarea").hide();
	$("#detailarea span.areatitle").html("新建" + modelname + "确认");
	$("#detailarea").show();
	$("#executebutton").val("实行新建");
	$("#executebutton").enable();
	$("#executebutton").show();
	$("#detailarea tr:has(#label_detail_updated_by)").hide();
	$("#detailarea tr:has(#label_detail_updated_time)").hide();
	// 默认画面变化 e

	// 详细数据
	$("#label_detail_id").text(" - ");
	$("#label_detail_comments").text(toLabelValue($("#input_comments")));
	$("#label_detail_name").text(toLabelValue($("#input_name")));

	$("#executebutton").unbind("click");
	$("#executebutton").click(function() {

		$("#executebutton").disable();

		// 新建画面输入项提交给后台
		var data = {
			"comments" : $("#input_comments").val(),
			"name" : $("#input_name").val()
		}

		// Ajax提交
		$.ajax({
			beforeSend : ajaxRequestType,
			async : false,
			url : servicePath + '?method=doinsert',
			cache : false,
			data : data,
			type : "post",
			dataType : "json",
			success : ajaxSuccessCheck,
			error : ajaxError,
			complete : update_handleComplete
		});
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
		async : false,
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
 * 切到修改确认画面
 */
var showEditConfirm = function() {

	// 默认画面变化 s
	top.document.title = modelname + "修改";
	$("#searcharea").hide();
	$("#listarea").hide();
	$("#editarea").hide();
	$("#detailarea span.areatitle").html(modelname + "修改确认");
	$("#detailarea").show();
	$("#executebutton").val("实行修改");
	$("#executebutton").enable();
	$("#executebutton").show();
	$("#detailarea tr:has(#label_detail_updated_by)").hide();
	$("#detailarea tr:has(#label_detail_updated_time)").hide();
	// 默认画面变化 e

	// 详细数据
	$("#label_detail_id").text($("#label_edit_id").text());
	$("#label_detail_comments").text(toLabelValue($("#input_comments")));
	$("#label_detail_name").text(toLabelValue($("#input_name")));

	$("#executebutton").unbind("click");
	$("#executebutton").click(function() {
		$("#executebutton").disable();
		var data = {
			"id" : $("#label_edit_id").text(),
			"comments" : $("#input_comments").val(),
			"name" : $("#input_name").val()
		}

		// Ajax提交
		$.ajax({
			beforeSend : ajaxRequestType,
			async : false,
			url : servicePath + '?method=doupdate',
			cache : false,
			data : data,
			type : "post",
			dataType : "json",
			success : ajaxSuccessCheck,
			error : ajaxError,
			complete : update_handleComplete
		});
	});
};

/**
 * 切到详细画面
 */
var showDetail = function(rid) {
	var rowData = $("#list").getRowData(rid);
	var data = {
		"id" : rowData.id
	}

	// Ajax提交
	$.ajax({
		beforeSend : ajaxRequestType,
		async : false,
		url : servicePath + '?method=detail',
		cache : false,
		data : data,
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : detail_handleComplete
	});

};

/**
 * 从确认/详细画面切回
 */
var showEditBack = function() {
	// 详细画面直接退回到一览
	if ($("#executebutton").val() == "" || $("#executebutton").val() == "实行删除") {
		showList();
		return;
	}

	// 确认画面退回到编辑
	// 默认画面变化 s
	$("#searcharea").hide();
	$("#listarea").hide();
	$("#editarea").show();
	$("#detailarea").hide();
	$("#editbutton").enable();
	// 默认画面变化 e
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

	// Ajax提交
	$.ajax({
		beforeSend : ajaxRequestType,
		async : false,
		url : servicePath + '?method=detail',
		cache : false,
		data : data,
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : showdelete_handleComplete
	});
};
