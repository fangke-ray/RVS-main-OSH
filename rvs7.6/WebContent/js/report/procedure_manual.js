/** 服务器处理路径 */
var servicePath = "procedureManual.do";

$(function() {
	$("#man_list a").attr("target", "_blank");

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

	setGrid();
	findit();

	// 检索按钮
	$("#searchbutton").click(function(){
		$("#cond_name").data("post", $("#cond_name").val());
		findit();
	})

	$("#resetbutton").click(function(){
		$("#cond_name").val("")
			.data("post", "");
	})
	// 追加处理
	$("#newbutton").click(showAdd);

	$("#removebutton").click(confirmRemove);

	$("#man_list").on("change", "input:checkbox", changeBooklist);
})

var setGrid = function() {

	// 构建jqGrid对象
	$("#man_list").jqGrid({
		data : [],
		height : 461,
		width : gridWidthMiddleRight,
		rowheight : 23,
		datatype : "local",
		colNames : ['key', '文档名称', '最终上传时间', '上传者', '本人书单'],
		colModel : [
		{
			name : 'procedure_manual_id',
			index : 'procedure_manual_id',
			sorttype : "integer",
			key : true,
			hidden : true
		},
		{ // 文档名称
			name : 'file_name',
			index : 'file_name',
			width : 210
			,formatter:function(val,b,row){
			if (val.indexOf("(不存在) ") === 0) {
				return "<span style='color:red;'>" + val + "</span>"
			}
			var id = row["procedure_manual_id"];
			return "<a target='_" + id + "' href='/docs/manual/" + id +".pdf'>" + val + "</a> " +
				"<a href='/docs/manual/" + id +".pdf?attname=" + val + ".pdf' class='ui-icon ui-icon-circle-triangle-s'></a>";}
		}, 
		{name:'update_time',index:'update_time', align:'center', width:35, formatter:'date', formatoptions:{srcformat:'Y/m/d H:i:s',newformat:'y-m-d H\\h'}},
		{name:'update_by',index:'update_by', align:'center', width:30},
		{name:'booklist',index:'booklist', align:'center', width:30
			,formatter:function(val,b,row){
			if (val && val == 1) {
				return "<input type='checkbox' class='booklist' checked>"
			}
			return "<input type='checkbox' class='booklist'>";
		}}],
		toppager : false,
		rowNum : 20,
		pager : "#man_listpager",
		viewrecords : true,
		caption : "作业要领书一览",
		ondblClickRow : showEdit,
		gridview : true, // Speed up
		pagerpos : 'right',
		pgbuttons : true,
		pginput : false,
		recordpos : 'left',
		viewsortcols : [true, 'vertical', true],
		onSelectRow: function(){$("#removebutton").enable();},
		gridComplete: function(){$("#removebutton").disable();}
	});
}

var findit = function() {
	// 读取已记录检索条件提交给后台
	var postData = {
		"file_name" : $("#cond_name").data("post")
	}

	// Ajax提交
	$.ajax({
		beforeSend : ajaxRequestType,
		async : true,
		url : servicePath + '?method=search',
		cache : false,
		data : postData,
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : search_handleComplete
	});
};

/**
 * 检索Ajax通信成功时的处理
 */
var search_handleComplete = function(xhrobj, textStatus) {
	var resInfo = $.parseJSON(xhrobj.responseText);

	if (resInfo.errors.length > 0) {
		// 共通出错信息框
		treatBackMessages("#searcharea", resInfo.errors);
	} else {
		// 标题修改
		top.document.title = "作业要领书一览";

		// 读取一览
		listdata = resInfo.list;
		// jqGrid已构建的情况下,重载数据并刷新
		$("#man_list").jqGrid().clearGridData();
		$("#man_list").jqGrid('setGridParam', {data : listdata}).trigger("reloadGrid", [{current : false}]);
	}
};

var showAdd = function(){
	var $this_dialog = $("#detail_dialog");
	$this_dialog.find("#edit_file").val("").data("id", "");
	$this_dialog.find("#edit_file_name").val("");
	$this_dialog.find("#edit_update_time").text("");

	popEditor($this_dialog);
}

var popEditor = function($this_dialog){
	$("#edit_file").off("change").on("change", editFileChange);

	$this_dialog.dialog({
		title : "作业要领书设置",
		width : 920,
		height :  'auto',
		resizable : false,
		modal : true,
		buttons : {
		"提交" : function(){
			var postData = {file_name : $("#edit_file_name").val()};

			var procedure_manual_id = $("#edit_file").data("id");
			if (procedure_manual_id) { // update
				postData["procedure_manual_id"] = procedure_manual_id;
			} else { // new
				if (!$("#edit_file").val()) {
					errorPop("请选择上传的文件。");
					return;
				}
			}
			uploadfile(postData);
		}
		,"关闭" : function(){
			$this_dialog.dialog("close");
		}}
	});
}

var uploadfile = function(postData) {

	// 覆盖层
	panelOverlay++;
	makeWindowOverlay();

	// ajax enctype="multipart/form-data"
	$.ajaxFileUpload({
		url : servicePath + '?method=doFilePost', // 需要链接到服务器地址
		secureuri : false,
		fileElementId : 'edit_file', // 文件选择框的id属性
		data : postData,
		dataType : 'json', // 服务器返回的格式
		success : function(responseText, textStatus) {
			panelOverlay--;
			killWindowOverlay();

			var resInfo = $.parseJSON(responseText);

			if (resInfo.errors.length > 0) {
				// 共通出错信息框
				treatBackMessages($("#editform"), resInfo.errors);
			} else {
				$("#detail_dialog").dialog("close");
				findit();
			}
		}
	});
};

var showEdit = function(rid, iRow, iCol, e){
	var rowData = $("#man_list").getRowData(rid);

	var id = rowData.procedure_manual_id;
	var list = $("#man_list").jqGrid('getGridParam', "data");
	for (var rowIdx in list) {
		if (list[rowIdx].procedure_manual_id === id) {
			rowData = list[rowIdx];
			break;
		}
	}

	var $this_dialog = $("#detail_dialog");
	$this_dialog.find("#edit_file").val("").data("id", rowData.procedure_manual_id);
	$this_dialog.find("#edit_file_name").val(rowData.file_name);
	$this_dialog.find("#edit_update_time").text(rowData.update_time);

	popEditor($this_dialog);
}

var editFileChange = function(){
	var path = $(this).val();
	var filename = path;
	if (path.indexOf('\\') >= 0) {
		filename = path.substring(path.lastIndexOf('\\')+1);
	}
	var extDot = filename.lastIndexOf('.');
	var extWarn = null;
	if (extDot >= 0) {
		if (filename.substring(extDot + 1).toUpperCase() !== "PDF") {
			extWarn = "请确认文件是否为PDF格式。";
		}
		filename = filename.substring(0, extDot);
	} else {
		extWarn = "请确认文件是否为PDF格式。";
	}
	if (!$("#edit_file_name").val()) {
		$("#edit_file_name").val(filename);
	}
	if (extWarn) {
		infoPop(extWarn);
	}
}

var confirmRemove = function(){
	var rowid = $("#man_list").jqGrid('getGridParam', "selrow");
	if (rowid) {
		var $file_name = $("#man_list").find("a[target='_" + rowid + "']");

		warningConfirm("是否确定要删除作业要领书【" + $file_name.text() + "】？",
			function(){
				var postData = {
					procedure_manual_id : rowid
				}

				// Ajax提交
				$.ajax({
					beforeSend : ajaxRequestType,
					async : false,
					url : servicePath + '?method=doRemove',
					cache : false,
					data : postData,
					type : "post",
					dataType : "json",
					success : ajaxSuccessCheck,
					error : ajaxError,
					complete : function(){
						findit();
					}
				});
			}, 
			null, "要领书删除确认");
	}
}

var changeBooklist = function(){

	var id = $(this).closest("tr").attr("id");

	var postData = {
		procedure_manual_id : id,
		booklist : (this.checked ? "1" : "0")
	}

	// Ajax提交
	$.ajax({
		beforeSend : ajaxRequestType,
		async : false,
		url : servicePath + '?method=doSetBooklist',
		cache : false,
		data : postData,
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : function(){
			findit();
		}
	});
}