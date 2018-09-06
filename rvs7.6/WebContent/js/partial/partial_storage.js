/** 模块名 */
var modelname = "零件库存";
var servicePath ="partial_storage.do";

$(function(){
	$("input.ui-button").button();

	$("#distributions >div:eq(0)").buttonset();
	$("#upload_identification").select2Buttons();

	/*为每一个匹配的元素的特定事件绑定一个事件处理函数*/
	$("#searcharea span.ui-icon").bind("click", function() {
		$(this).toggleClass('ui-icon-circle-triangle-n').toggleClass('ui-icon-circle-triangle-s');
		if ($(this).hasClass('ui-icon-circle-triangle-n')) {
			$(this).parent().parent().next().show("blind");
		} else {
			$(this).parent().parent().next().hide("blind");
		}
	});

	$("#upload_storage_date,#search_storage_date").datepicker({
		showButtonPanel:true,
		dateFormat: "yy/mm/dd",
		currentText: "今天"
	});

	$("#uploadbutton").click(function() {
		upload_file();
	});

	$("#search_button").click(function(){
		findit();
	});

	$("#resetbutton").click(function(){
		reset();
	});

	$("#delete_button").click(function(){
		doDelete();
	});

	reset();
	 /*frist jqgrid初始化显示*/
	storage_list([]);
});

/*清除函数*/
var reset=function(){
	$("#upload_file").val("");
	$("#upload_storage_date").val("");
	$("#upload_identification").val("").trigger("change");
	$("#search_storage_date").val("");
};

/*文件载入*/
var upload_file = function(){
	$("#uploadform").validate({
		rules : {
			upload_file : {
				required : true
			},
			upload_storage_date : {
				required : true
			},
			upload_identification : {
				required : true
			}
		}
	});

	if ($("#uploadform").valid()) {
		var postData = {
			storage_date : $("#upload_storage_date").val(),
			identification : $("#upload_identification").val()
		};
		$.ajaxFileUpload({
			url : servicePath + '?method=doUploadStorage',
			secureuri : false,
			data : postData,
			fileElementId : 'upload_file', // 文件选择框的id属性
			dataType : 'json', // 服务器返回的格式
			success : function(responseText, textStatus) {
				var resInfo = null;
				// 以Object形式读取JSON
				eval('resInfo =' + responseText);
				if (resInfo.errors.length > 0) {
					// 共通出错信息框
					treatBackMessages("#confirm_message", resInfo.errors);
				} else {
					$("#confirm_message").text("零件库存记录已导入！");
					$("#confirm_message").dialog({
						resizable : false,
						modal : true,
						title : "文件上传完毕",
						buttons:{"确认" : function(){
							$(this).dialog("close");
						}}
					});
				}
			}
		});
	}
};

var findit = function() {
	$("#searchform").validate({
		rules : {
			search_storage_date : {
				required : true
			}
		}
	});

	if ($("#searchform").valid()) {
		var postData = {
			storage_date : $("#search_storage_date").val()
		};
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
			complete : search_Complete
		});
	}
};

var search_Complete = function(xhrobj, textStatus) {
	var resInfo = null;
	// 以Object形式读取JSON responseText获取来自服务器响应的数据
	eval('resInfo =' + xhrobj.responseText);
	if (resInfo.errors.length > 0) {
		// 共通出错信息框
		treatBackMessages(null, resInfo.errors);
	} else {
		storage_list(resInfo.partialStorageList);
	}
};

/*jqgrid表格*/
var storage_list = function(finished_list_data){
	if ($("#gbox_list").length > 0) {
		$("#list").jqGrid().clearGridData();
		$("#list").jqGrid('setGridParam',{data:finished_list_data}).trigger("reloadGrid", [{current:false}]);
	} else {
		$("#list").jqGrid({
			data:finished_list_data,
			height: 461,
			width: 992,
			rowheight: 23,
			datatype: "local",
			colNames:['','','','库存分类','零件号','零件说明','数量'],
			colModel:[
				{name:'partial_id',index:'partial_id', hidden:true},
				{name:'storage_date',index:'storage_date', hidden:true},
				{name:'identification',index:'identification', hidden:true},
				{name:'used_by',index:'identification',width : 40,align : 'center',formatter:function(v,c,data){
					if (data.identification == 1) {
						return "OSH";
					} else if (data.identification == 2) {
						return "OGZ";
					}
				}},
				{name:'code',index : 'code',width : 40,align : 'left'},
				{name:'partial_name',index : 'partial_name',width : 160,align : 'left'},
				{name:'quantity',index : 'quantity',width : 40,align : 'right'}
			],
			rowNum: 20,
			toppager: false,
			pager: "#listpager",
			viewrecords: true,
			hidegrid : false,
			multiselect:true,
			caption: "零件库存一览",
			gridview: true,
			pagerpos: 'right',
			pginput: false,
			rownumbers : true,
			recordpos: 'left',
			deselectAfterSort : false,
			viewsortcols : [true,'vertical',true],
			ondblClickRow:function(rid, iRow, iCol, e){
				var rowData=$("#list").getRowData(rid);
				showDetail(rowData);
			},
			onSelectRow : enablebuttons,
			onSelectAll : enablebuttons,
			gridComplete : function() {
				enablebuttons();
			}
		});
	}
};

var enablebuttons = function() {
	var rowids = $("#list").jqGrid("getGridParam", "selarrrow");
	if (rowids.length > 0) {
		$("#delete_button").enable();
	} else {
		$("#delete_button").disable();
	}
};

var showDetail = function(rowData){
	$(".errorarea-single").removeClass("errorarea-single");
	$("#edit_identification").text(rowData.used_by);
	$("#edit_code").text(rowData.code);
	$("#edit_partial_name").text(rowData.partial_name);
	$("#edit_quantity").val(rowData.quantity);

	$("#editform").validate({
		rules : {
			edit_quantity : {
				required : true,
				digits:true,
				range:[1,99999]
			}
		}
	});

	var $pop_window = $("#editarea");
	$pop_window.dialog({
		resizable : false,
		width : '400px',
		maxHeight : 300,
		modal : true,
		title : "零件库存详细信息",
		buttons : {
			"确认" : function() {
				if ($("#editform").valid()) {
					var data={
						"partial_id":rowData.partial_id,
						"storage_date":rowData.storage_date,
						"identification":rowData.identification,
						"quantity":$("#edit_quantity").val()
					};
					$.ajax({
						beforeSend : ajaxRequestType,
						async : true,
						url : servicePath + '?method=doUpdateQuantity',
						cache : false,
						data : data,
						type : "post",
						dataType : "json",
						success : ajaxSuccessCheck,
						error : ajaxError,
						complete : update_complete
					});
					$(this).dialog("close");
				}
			},
			"取消" : function() {
				$(this).dialog("close");
			}
		}
	});
};

var update_complete = function(xhrobj,textStatus){
	// 以Object形式读取JSON
	eval('resInfo =' + xhrobj.responseText);
	if (resInfo.errors.length > 0) {
		// 共通出错信息框
		treatBackMessages("", resInfo.errors);
	} else {
		findit();
	}
};

var doDelete = function(){
    $("#confirm_message").text("删除不能恢复。确认要删除记录吗?");
    $("#confirm_message").dialog({
        resizable:false,
        modal : true,
        title : "删除确认",
        buttons : {
            "确认" : function() { 
				$(this).dialog("close");
				var rowids = $("#list").jqGrid("getGridParam", "selarrrow");
				var data = {};
			
				for (var ii=0;ii < rowids.length;ii++){
					var rowData = $("#list").getRowData(rowids[ii]);
					data["delete.partial_id[" + ii + "]"] = rowData.partial_id;
					data["delete.storage_date[" + ii + "]"] = rowData.storage_date;
					data["delete.identification[" + ii + "]"] = rowData.identification;
				}
			
				$.ajax({
					beforeSend : ajaxRequestType,
					async : true,
					url : servicePath + '?method=doDelete',
					cache : false,
					data : data,
					type : "post",
					dataType : "json",
					success : ajaxSuccessCheck,
					error : ajaxError,
					complete : update_complete
				});
            },
            "取消" : function() {
                $(this).dialog("close");
            }
        }
    });
};
