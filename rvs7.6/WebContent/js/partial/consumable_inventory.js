$(function() {
	/*验证*/
	$("#inventory_form").validate({
		rules:{
			i_reason:{
				maxlength:256
			}
		}
	});
});

var page_inventory = {
	servicePath : "consumable_inventory.do",

	reset : function() {
		$("#search_i_code").val("");
		$("#search_adjust_date_start").val("");
		$("#search_adjust_date_end").val("");
		$("#search_i_reason").val("");
	},
	
	findit : function() {
		var searchData = {
			"code" : $("#search_i_code").val(),
			"reason" : $("#search_i_reason").val(),
			"adjust_date_start" : $("#search_adjust_date_start").val(),
			"adjust_date_end" : $("#search_adjust_date_end").val()
		};
		
		$.ajax({
			beforeSend : ajaxRequestType,
			async : true,
			url : page_inventory.servicePath + '?method=search',
			cache : false,
			data : searchData,
			type : "post",
			dataType : "json",
			success : ajaxSuccessCheck,
			error : ajaxError,
			complete : page_inventory.doInit_ajaxSuccess
		});
	},
	
	doInit_ajaxSuccess : function(xhrobj, textStatus) {
		var resInfo = null;
		try {
			// 以Object形式读取JSON
			eval('resInfo =' + xhrobj.responseText);
	
			if (resInfo.errors.length > 0) {
				// 共通出错信息框
				treatBackMessages(null, resInfo.errors);
			} else {
				page_inventory.show_inventory_list(resInfo.inventory_list);
			}
		} catch (e) {
			alert("name: " + e.name + " message: " + e.message + " lineNumber: "
					+ e.lineNumber + " fileName: " + e.fileName);
		};
	},

	show_inventory_list : function(inventoryList) {
		if ($("#gbox_inventory_list").length > 0) {
			// jqGrid已构建的情况下,重载数据并刷新
			$("#inventory_list").jqGrid().clearGridData();
			$("#inventory_list").jqGrid('setGridParam', {data : inventoryList}).trigger("reloadGrid", [{current : false}]);
		} else {
			$("#inventory_list").jqGrid({
				data : inventoryList,
				height : 461,
				width : 992,
				rowheight : 23,
				datatype : "local",
				colNames : ['partial_id', '消耗品代码', '说明', '消耗品分类', '调整量', '调整日时', 'adjust_time', 'operator_id', '调整负责人', '理由'],
				colModel : [{
					name : 'partial_id',
					index : 'partial_id',
					hidden : true
				}, {
					name : 'code',
					index : 'code',
					width : 50
				}, {
					name : 'name',
					index : 'name',
					width : 120
				}, {
					name : 'type',
					index : 'type',
					width : 30,
					formatter:'select', editoptions:{value:$("#cConsumableType").val()}
				}, {
					name : 'adjust_inventory',
					index : 'adjust_inventory',
					align : 'right',
					formatter : 'integer',
					sorttype : 'int',
					width : 30,
					formatoptions: {
						defaultValue: ' - ', thousandsSeparator: ",", prefix: ""
					}
				}, {
					name : 'adjust_time',
					index : 'adjust_time',
					align : 'center',
					width : 40,
					formatter:'date', formatoptions:{srcformat:'Y/m/d H:i:s',newformat:'m-d H\\h'}
				}, {
					name : 'adjust_time_db',
					index : 'adjust_time',
					hidden : true,
					formatter: function(a,b,rowdata) {
						return rowdata.adjust_time;
					}
				}, {
					name : 'operator_id',
					index : 'operator_id',
					hidden : true
				}, {
					name : 'operator_name',
					index : 'operator_name',
					width : 40
				}, {
					name : 'reason',
					index : 'reason',
					width : 120
				}],
				rowNum : 35,
				rownumbers:true,
				toppager : false,
				pager : "#inventory_list_pager",
				viewrecords : true,
				gridview : true,
				pagerpos : 'right',
				pgbuttons : true,
				pginput : false,
				recordpos : 'left',
				hidegrid : false,
				deselectAfterSort : false,
				ondblClickRow : page_inventory.showOrderEdit,
				viewsortcols : [true, 'vertical', true]
			});
		}
	},


	/**
	 * 编辑订购单
	 */
	showOrderEdit : function() {
		var isProcess = $('#hidden_isProcess').val();
		if (isProcess == "false") {
			return;
		}

		$("#edit_i_reason").removeClass("valid errorarea-single");
		
		var row = $("#inventory_list").jqGrid("getGridParam", "selrow");// 得到选中行的ID
		var rowData = $("#inventory_list").getRowData(row);
		$("#edit_i_reason").val(rowData.reason);

		var $pop_window = $("#pop_window_inventory_edit");
		$pop_window.dialog({
			resizable : false,
			width : '600px',
			modal : true,
			title : "盘点理由编辑",
			buttons : {
				"确认" : function() {
					if ($("#inventory_form").valid()) {
						var data = {
							"reason" : $("#edit_i_reason").val(),
							"partial_id" : rowData.partial_id,
							"adjust_time" : rowData.adjust_time_db,
							"operator_id" : rowData.operator_id
						};

						$.ajax({
							beforeSend : ajaxRequestType,
							async : true,
							url : page_inventory.servicePath + '?method=doUpdate',
							cache : false,
							data : data,
							type : "post",
							dataType : "json",
							success : ajaxSuccessCheck,
							error : ajaxError,
							complete : page_inventory.doUpdate_ajaxSuccess
						});
						
						$(this).dialog("close");
					}
				},
				"取消" : function() {
					$(this).dialog("close");
				}
			}
		});
	
	},

	doUpdate_ajaxSuccess : function(xhrobj, textStatus) {
		var resInfo = null;
		try {
			// 以Object形式读取JSON
			eval('resInfo =' + xhrobj.responseText);
	
			if (resInfo.errors.length > 0) {
				// 共通出错信息框
				treatBackMessages(null, resInfo.errors);
			} else {
				page_inventory.findit();
			}
		} catch (e) {
			alert("name: " + e.name + " message: " + e.message + " lineNumber: "
					+ e.lineNumber + " fileName: " + e.fileName);
		};
	},
};
