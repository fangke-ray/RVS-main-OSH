/** 模块名 */
var modelname = "选择修理";
/** 一览数据对象 */
var listdata = {};
/** 服务器处理路径 */
var servicePath = "optional_fix.do";

/**
 * 页面加载处理
 */
$(function() {
	// 适用jquery按钮
	$("input.ui-button").button();
	$("#cond_rank, #add_rank, #edit_rank").select2Buttons();

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

	findit();
	showList();

	// 检索处理
	$("#searchbutton").click(findit);
	$("#addbutton").click(doAdd);
	$("#editbutton").click(doEdit);
	$("#addcancelbutton, #editcancelbutton, #editarea span.ui-icon").click(showList);

	// 清空检索条件
	$("#resetbutton").click(function() {
		$("#cond_optional_fix_id").val("");
		$("#cond_infection_item").val("");
		$("#cond_standard_code").val("");
		$("#cond_rank").val("").trigger("change");
	});

});

/**
 * 切到一览画面
 */
var showList = function() {
	top.document.title = modelname + "一览";
	$("#searcharea").show();
	$("#listarea").show();
	$("#addarea").hide();
	$("#editarea").hide();
};

/** 
 * 检索处理
 */
var findit = function() {
	// 读取已记录检索条件提交给后台
	var data = {
		"optional_fix_id" : $("#cond_optional_fix_id").val(),
		"infection_item" : $("#cond_infection_item").val(),
		"standard_code" : $("#cond_standard_code").val(),
		"rank" : $("#cond_rank").val()
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
 * 检索Ajax通信成功时的处理
 */
var search_handleComplete = function(xhrobj, textStatus) {
	var resInfo = null;

	resInfo = $.parseJSON(xhrobj.responseText);

	if (resInfo.errors.length > 0) {
		// 共通出错信息框
		treatBackMessages("#searcharea", resInfo.errors);
	} else {
		// 标题修改
		top.document.title = modelname + "一览";

		// 读取一览
		listdata = resInfo.list;
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
				colNames : ['', '', '检查标准号', '检查项目', '修理等级', '最后更新人', '最后更新时间'],
				colModel : [
				{name:'myac', width:60, fixed:true, sortable:false, resize:false, formatter:'actions', formatoptions:{keys:true, editbutton:false}},
				{	name : 'optional_fix_id',
					index : 'optional_fix_id',
					hidden : true
				}, {
					name : 'standard_code',
					index : 'standard_code',
					width : 50
				}, {
					name : 'infection_item',
					index : 'infection_item',
					width : 150
				}, {
					name : 'rank',
					index : 'rank',
					width : 50
				}, {
					name : 'updated_by',
					index : 'updated_by',
					hidden : true
				}, {
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
				gridview : true,
				pagerpos : 'right',
				pgbuttons : true,
				pginput : false,
				recordpos : 'left',
				viewsortcols : [true, 'vertical', true]
			});
			$(".ui-jqgrid-hbox").before('<div class="ui-widget-content" style="padding:4px;">' +
					'<input type="button" class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="addbutton" value="新建'+ '" role="button" aria-disabled="false">' +
				'</div>');
			$("#addbutton").button();
			// 追加处理
			$("#addbutton").click(showAdd);
		}

	}

};

var showAdd = function() {
	top.document.title = "新建" + modelname;
	$("#searcharea").hide();
	$("#listarea").hide();
	$("#addarea span.areatitle:eq(0)").html("新建" + modelname);
	$("#addarea").show();
	$("#addform input[type='text']").val("");
	$("#add_rank").val("").trigger("change");
	$(".errorarea-single").removeClass("errorarea-single");
};

var doAdd = function() {
	// 前台Validate设定
	$("#addform").validate({
		rules : {
			standard_code : {
				required : true
			},
			infection_item : {
				required : true
			}
		}
	});

	// 前台Validate
	if ($("#addform").valid()) {
		// 新建画面输入项提交给后台
		var data = {
			"standard_code" : $("#add_standard_code").val(),
			"infection_item" : $("#add_infection_item").val(),
			"rank" : ($("#add_rank").val() ? $("#add_rank").val().join() : null)
		};

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
};

var update_handleComplete = function(xhrobj, textStatus) {
	var resInfo = null;
	// 以Object形式读取JSON
	resInfo = $.parseJSON(xhrobj.responseText);
	if (resInfo.errors.length > 0) {
		// 共通出错信息框
		treatBackMessages(null, resInfo.errors);
	} else {
		// 重新查询
		findit();
		// 切回一览画面
		showList();
	}
};

var showEdit = function() {
	// 读取修改行
	var row = $("#list").jqGrid("getGridParam", "selrow");
	var rowData = $("#list").getRowData(row);
	var data = {
		"optional_fix_id" : rowData.optional_fix_id
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
		complete : function(xhrobj, textStatus) {
			var resInfo = null;

			// 以Object形式读取JSON
			resInfo = $.parseJSON(xhrobj.responseText);
			if (resInfo.errors.length > 0) {
				// 共通出错信息框
				treatBackMessages(null, resInfo.errors);
			} else {
				top.document.title = "修改" + modelname;
				$("#searcharea").hide();
				$("#listarea").hide();
				$("#editarea span.areatitle:eq(0)").html("修改" + modelname);
				$("#editarea").show();
				$(".errorarea-single").removeClass("errorarea-single");

				// 详细数据
				var detail = resInfo.detail;
				$("#edit_optional_fix_id").val(detail.optional_fix_id);
				$("#edit_standard_code").val(detail.standard_code);
				$("#edit_infection_item").val(detail.infection_item);
				var ranks = detail.rank;
				if (ranks) {
					$("#edit_rank").val(ranks.split(',')).trigger("change");
				} else {
					$("#edit_rank").val("").trigger("change");
				}

				$("#label_edit_updated_by").text(detail.updated_by);
				$("#label_edit_updated_time").text(detail.updated_time);

				// 检查票
				var pcsContent = resInfo.pcsContent;
				if (pcsContent) {
					$("#pcs_empty").hide();
					
					$("#pcs_content").html(pcsContent.htmlContent).show();

					$("#pcs_content input,#pcs_contents textarea").parent().css("background-color", "#93C3CD");
					$("#pcs_content input:text").autosizeInput();
					$("#pcs_content input[name^='EN'], #pcs_content input[name^='LN']").button();

					$("#editarea span.areatitle:eq(1)").text("【" + detail.infection_item + "】 检查票样式 ");
				} else {
					$("#pcs_empty").show();
					
					$("#pcs_content").html("").hide();
					$("#editarea span.areatitle:eq(1)").text("检查票样式");
				}
			}
		}
	});
};

var doEdit = function() {
	// 前台Validate设定
	$("#editform").validate({
		rules : {
			edit_standard_code : {
				required : true
			},
			edit_infection_item : {
				required : true
			}
		}
	});

	warningConfirm("确认要修改记录吗？", function() {
		// 前台Validate
		if ($("#editform").valid()) {
			// 修改画面输入项提交给后台
			var data = {
				"optional_fix_id" : $("#edit_optional_fix_id").val(),
				"standard_code" : $("#edit_standard_code").val(),
				"infection_item" : $("#edit_infection_item").val(),
				"rank" : ($("#edit_rank").val() ? $("#edit_rank").val().join() : null)
			};

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
		};
	}, null, "修改确认");
};

/**
 * 切到删除画面
 */
var showDelete = function(rid) {
	// 读取删除行
	var rowData = $("#list").getRowData(rid);
	var data = {
		"optional_fix_id" : rowData.optional_fix_id
	};

	warningConfirm("删除不能恢复。确认要删除["+encodeText(rowData.standard_code)+"]的记录吗？", function() {
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
	}, null, "删除确认");
};
