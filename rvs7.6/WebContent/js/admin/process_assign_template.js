/** 模块名 */
var modelname = "修理流程模板";
/** 一览数据对象 */
var listdata = {};
/** 服务器处理路径 */
var servicePath = "processAssignTemplate.do";

/*
* Ajax通信返回时的处理
*/
var getPositions_handleComplete = function(xhrobj, textStatus) {
	var resInfo = null;
	try {
		// 以Object形式读取JSON
		eval('resInfo =' + xhrobj.responseText);

		if (resInfo.errors.length > 0) {
			// 共通出错信息框
			treatBackMessages(null, resInfo.errors);
		} else {
			$("#base").html("");
			$("#base").flowchart({},{editable:true, selections: resInfo.list});
		}
	} catch (e) {
		console.log("name: " + e.name + " message: " + e.message + " lineNumber: "
				+ e.lineNumber + " fileName: " + e.fileName);
	};
};

/**
 * 页面加载处理
 */
$(function() {
	// 适用jquery按钮
	$("input.ui-button").button();

  // 右上图标效果
	$("a.areacloser").hover(
		function (){$(this).addClass("ui-state-hover");},
		function (){$(this).removeClass("ui-state-hover");}
	);
	$("#searcharea span.ui-icon").bind("click",
		function() {
			$(this).toggleClass('ui-icon-circle-triangle-n').toggleClass('ui-icon-circle-triangle-s');
			if ($(this).hasClass('ui-icon-circle-triangle-n')) {
				$(this).parent().parent().next().show("blind");
			} else {
				$(this).parent().parent().next().hide("blind");
			}
		});
	$("#cancelbutton, #editarea span.ui-icon").click(function (){
		showList();
	});

	// 初始状态隐藏编辑和详细显示
	$("#editarea").hide();
	$("#detailarea").hide();

	// 检索表单认证(没有前台认证也要写,为了对应后台出错时效果)
	$("#searchform").validate({});
	// ///////////////////以上共通///////////////////////

	// czdr
	$("#searchform select, #editform select, #paChooseRefer select").select2Buttons();

	$("#paChooseRefer select").change(function(){
		if (this.value) {
			var data = {
				"id" : this.value
			};

			$("#paChooseRefer").dialog("close");
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
				complete : referedit_handleComplete
			});
		}
	});

	$("#edit_derive_kind").change(function(){
		if (this.value) {
			$("#edit_derive_from_id").parent().parent().show();
		} else {
			$("#edit_derive_from_id").val("").trigger("change")
				.parent().parent().hide();
		}
	});

	$("#referbutton").click(function() {
		$("#paChooseRefer select").val("").trigger("change");
		$("#paChooseRefer").dialog({
			title : "选择参照读入的流程",
			width : 500,
			resizable : false,
			modal : true
			}
		);
	});

	// 检索处理
	$("#searchbutton").click(function() {
		// 保存检索条件
		$("#cond_id").data("post", $("#cond_id").val());
		$("#cond_name").data("post", $("#cond_name").val());
		$("#cond_derive_kind").data("post", $("#cond_derive_kind").val());

		// 查询
		findit();
	});

	$("#resetbutton").click(function() {
		// 保存检索条件
		$("#cond_id").val("").data("post", "");
		$("#cond_name").val("").data("post", "");
		$("#cond_derive_kind").val("").data("post", "").trigger("change");
	});

	// Ajax提交
	$.ajax({
		beforeSend : ajaxRequestType,
		async : true,
		url : servicePath + '?method=getPositions',
		cache : false,
		data : {},
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : getPositions_handleComplete
	});

	findit();
});

/*
* Ajax通信返回时的处理
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
				$("#list").jqGrid('setGridParam',{data:listdata}).trigger("reloadGrid", [{current:false}]);
			} else {
				// 构建jqGrid对象
				$("#list").jqGrid({
					data:listdata,
					height: 461,
					width : gridWidthMiddleRight,
					rowheight: 23,
					datatype: "local",
					colNames:['', 'id', '工序指派模板名称', '最后更新人','最后更新时间'],
					colModel:[
						{name:'myac', width:48, fixed:true, sortable:false, resize:false, formatter:'actions', formatoptions:{keys:true, editbutton:false}},
						{name:'id',index:'id', hidden:true},
						{name:'name',index:'name', width:100},
						{name:'updated_by',index:'updated_by', width:60},
						{name:'updated_time',index:'updated_time', width:80}
					],
					toppager : false,
					rowNum: 20,
					pager: "#listpager",
					viewrecords: true,
					caption: modelname + "一览",
					ondblClickRow : showEdit,
					gridview: true, // Speed up
					pagerpos: 'right',
					pgbuttons: true,
					pginput: false,
					recordpos: 'left',
					viewsortcols : [true,'vertical',true]
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

function new_handleComplete(xhrobj, textStatus) {
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
			// 修理流程模板可选项刷新
			$("#paChooseRefer select, #edit_derive_from_id").html(resInfo.paOptions)
				.select2Buttons();
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

function insert() {
	var data = {
		name : $("#edit_name").val(),
		derive_kind : $("#edit_derive_kind").val(),
		fix_type : $("#edit_fix_type").val()
	};

	var derive_from_id = $("#edit_derive_from_id").val();
	if (derive_from_id) data.derive_from_id = derive_from_id;

	var iAvariPos = 0;
	$(".pos").each(function(i, item) {
		var pos = $(item);
		var thiscode = pos.attr("code");
		if (thiscode != "0") {
			data["process_assign.line_id[" + iAvariPos + "]"] = pos.parent().parent().parent().attr("code");
			data["process_assign.position_id[" + iAvariPos + "]"] = pos.attr("code");
			data["process_assign.sign_position_id[" + iAvariPos + "]"] = pos.attr("posid");
			data["process_assign.prev_position_id[" + iAvariPos + "]"] = pos.attr("prevcode");
			data["process_assign.next_position_id[" + iAvariPos + "]"] = pos.attr("nextcode");
			iAvariPos++;
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
		complete : new_handleComplete
	});
	;
}

function update() {
	var data = {"id" : $("#label_edit_id").text(),
		name : $("#edit_name").val(),
		derive_kind : $("#edit_derive_kind").val(),
		fix_type : $("#edit_fix_type").val()
	};
	var derive_from_id = $("#edit_derive_from_id").val();
	if (derive_from_id) data.derive_from_id = derive_from_id;

	var iAvariPos = 0;
	$(".pos").each(function(i, item) {
		var pos = $(item);
		var thiscode = pos.attr("code");
		if (thiscode != "0") {
			data["process_assign.line_id[" + iAvariPos + "]"] = pos.parent().parent().parent().attr("code");
			data["process_assign.position_id[" + iAvariPos + "]"] = pos.attr("code");
			data["process_assign.sign_position_id[" + iAvariPos + "]"] = pos.attr("posid");
			data["process_assign.prev_position_id[" + iAvariPos + "]"] = pos.attr("prevcode");
			data["process_assign.next_position_id[" + iAvariPos + "]"] = pos.attr("nextcode");
			iAvariPos++;
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
		complete : new_handleComplete
	});
	;
}
var findit = function() {
	var data = {"id" : $("#cond_id").data("post"),
			"name" : $("#cond_name").data("post"),
			"derive_kind" : $("#cond_derive_kind").data("post")
		};

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

	top.document.title = "新建" + modelname;
	$("#searcharea").hide();
	$("#listarea").hide();
	$("#editarea span.areatitle").html("新建" + modelname);
	$("#editarea").show();

	$("#label_edit_id").text(" -");
	$("#edit_name").val("");
	$("#edit_derive_from_id").val("").trigger("change");
	$("#edit_derive_kind").val("").trigger("change");
	$("#edit_fix_type").val("").trigger("change");
	$("#base").flowchart("reset");

	$("#editform").validate({
		rules: {
			name: {
				required: true
			}
		}
	});
	$("#savebutton").unbind("click");
	$("#savebutton").click(function (){
		if ($("#editform").valid()) {
			insert();
		}
	});
};

function showedit_handleComplete(xhrobj, textStatus) {
	var resInfo = null;
	try {
		// 以Object形式读取JSON
		eval('resInfo =' + xhrobj.responseText);
		if (resInfo.errors.length > 0) {
			// 共通出错信息框
			treatBackMessages(null, resInfo.errors);
		} else {
			top.document.title = modelname + "编辑";
			$("#searcharea").hide();
			$("#listarea").hide();
			$("#editarea span.areatitle").html(modelname + "编辑");
			$("#editarea").show();

			$("#label_edit_id").text(resInfo.templateForm.id);
			$("#edit_name").val(resInfo.templateForm.name);
			$("#edit_derive_from_id").val(resInfo.templateForm.derive_from_id).trigger("change");
			$("#edit_derive_kind").val(resInfo.templateForm.derive_kind).trigger("change");
			$("#edit_fix_type").val(resInfo.templateForm.fix_type).trigger("change");

			$("#base").flowchart("fill", resInfo.processAssigns);

			$("#editform").validate({
				rules : {
					name : {
						required : true
					}
				}
			});
			$("#savebutton").unbind("click");
			$("#savebutton").click(function() {
				if ($("#editform").valid()) {
					update();
				}
			});
		}
	} catch (e) {
		console.log("name: " + e.name + " message: " + e.message + " lineNumber: "
				+ e.lineNumber + " fileName: " + e.fileName);
	};
}

function referedit_handleComplete(xhrobj, textStatus) {
	var resInfo = null;
	try {
		// 以Object形式读取JSON
		eval('resInfo =' + xhrobj.responseText);
		if (resInfo.errors.length > 0) {
			// 共通出错信息框
			treatBackMessages(null, resInfo.errors);
		} else {
			$("#base").flowchart("fill", resInfo.processAssigns);
		}
	} catch (e) {
		alert("name: " + e.name + " message: " + e.message + " lineNumber: "
				+ e.lineNumber + " fileName: " + e.fileName);
	};
}

/**
 * 切到修改画面
 */
var showEdit = function(rid) {
	// 读取修改行
	var rowData = $("#list").getRowData(rid);
	var data = {
		"id" : rowData.id
	};

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
	};

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
				complete : new_handleComplete
			});
		}, null, "删除确认"
	);
};

