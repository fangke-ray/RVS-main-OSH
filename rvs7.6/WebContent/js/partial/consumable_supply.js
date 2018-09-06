$(function() {
	$("#edit_s_code").bind("keyup", function(e){
		var kcode = e.keyCode;
		if (kcode == 8 || kcode == 46){
			return false;
		}

		if ($(this).val().length >= 6 && $(this).val().length <= 9){
			if( kcode > 105 || kcode < 48){
				return;
			}
			
			var data = {
				"code" : $(this).val()
			};

			$.ajax({
				beforeSend : ajaxRequestType,
				async : true,
				url : page_supply.servicePath + '?method=getAutocomplete',
				cache : false,
				data : data,
				type : "post",
				dataType : "json",
				success : ajaxSuccessCheck,
				error : ajaxError,
				complete : function(xhrobj){
					var resInfo = null;
					try {
						$("#s_msg").html("");
						eval('resInfo =' + xhrobj.responseText);							
						if (resInfo.reasult_flg == "1") {
							var partial = resInfo.partial;
							$("#edit_s_name").text(partial.name);
							$("#hidden_partial_id").val(partial.partial_id);
							if (!partial.partial_id) {
								$("#s_msg").html("<br>该零件尚未作为消耗品");
							} else {
								$("#edit_s_code").blur();
							}
						} else {
							$("#hidden_partial_id").val("");
							$("#s_msg").html("<br>该零件不存在");
						}
					} catch (e) {
					}
				}
			});			
		}
	});

	/*验证*/
	$("#supply_form").validate({
		rules:{
			s_code:{
				required:true,
				maxlength:8
			},
			s_quantity:{
				required:true,
				digits:true,
				maxlength:5
			}
		}
	});

	/*入库*/
	$("#add_supply_button").click(function() {
		var partial_id = $("#hidden_partial_id").val();
		if (partial_id != null && partial_id != "" && $("#supply_form").valid()) {
			var data = {
					"code" : $("#edit_s_code").val(),
					"partial_id" : partial_id,
					"quantity": $("#edit_s_quantity").val()
				};

			$.ajax({
				beforeSend : ajaxRequestType,
				async : true,
				url : page_supply.servicePath + '?method=doAdd',
				cache : false,
				data : data,
				type : "post",
				dataType : "json",
				success : ajaxSuccessCheck,
				error : ajaxError,
				complete : function(xhrobj, textStatus) {
					var resInfo = null;
					try {
						// 以Object形式读取JSON
						eval('resInfo =' + xhrobj.responseText);
				
						if (resInfo.errors.length > 0) {
							// 共通出错信息框
							treatBackMessages(null, resInfo.errors);
						} else {
							page_supply.upd_cnt++;
							page_supply.clearInput();
						}
					} catch (e) {
						alert("name: " + e.name + " message: " + e.message + " lineNumber: "
								+ e.lineNumber + " fileName: " + e.fileName);
					};
				}
			});
		}			
	});
});

var page_supply = {
	servicePath : "consumable_supply.do",
	upd_cnt : 0,

	reset : function() {
		$("#search_s_code").val("");
		$("#search_supply_date_start").val("");
		$("#search_supply_date_end").val("");
	},
	
	findit : function() {
		var searchData = {
			"supply_date" : $("#hidden_key").val(),
			"code" : $("#search_s_code").val(),
			"supply_date_start" : $("#search_supply_date_start").val(),
			"supply_date_end" : $("#search_supply_date_end").val()
		};
		
		$.ajax({
			beforeSend : ajaxRequestType,
			async : true,
			url : page_supply.servicePath + '?method=search',
			cache : false,
			data : searchData,
			type : "post",
			dataType : "json",
			success : ajaxSuccessCheck,
			error : ajaxError,
			complete : page_supply.doInit_ajaxSuccess
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
				page_supply.show_supply_list(resInfo.supply_list);
			}
		} catch (e) {
			alert("name: " + e.name + " message: " + e.message + " lineNumber: "
					+ e.lineNumber + " fileName: " + e.fileName);
		};
	},
	
	show_supply_list : function(supplyList) {
		if ($("#gbox_supply_list").length > 0) {
			// jqGrid已构建的情况下,重载数据并刷新
			$("#supply_list").jqGrid().clearGridData();
			$("#supply_list").jqGrid('setGridParam', {data : supplyList}).trigger("reloadGrid", [{current : false}]);
		} else {
			$("#supply_list").jqGrid({
				data : supplyList,
				height : 461,
				width : 992,
				rowheight : 23,
				datatype : "local",
				colNames : ['partial_id', '消耗品代码', '说明', '消耗品分类', '入库数量', '入库日期'],
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
					width : 80
				}, {
					name : 'type',
					index : 'type',
					width : 50,
					formatter:'select', editoptions:{value:$("#cConsumableType").val()}
				}, {
					name : 'quantity',
					index : 'quantity',
					width : 50,
					align : 'right',
					formatter : 'integer',
					sorttype : 'int',
					width : 60,
					formatoptions: {
						defaultValue: ' - ', thousandsSeparator: ",", prefix: ""
					}
				}, {
					name : 'supply_date',
					index : 'supply_date',
					align : 'center',
							width : 50,
							formatter:'date', formatoptions:{srcformat:'Y/m/d',newformat:'m-d'}
				}],
				rowNum : 35,
				rownumbers:true,
				toppager : false,
				pager : "#supply_list_pager",
				viewrecords : true,
				gridview : true,
				pagerpos : 'right',
				pgbuttons : true,
				pginput : false,
				recordpos : 'left',
				hidegrid : false,
				deselectAfterSort : false,
				viewsortcols : [true, 'vertical', true]
			});
		}
	},

	/**
	 * 入库零件
	 */
	showSupplyAdd : function() {
		page_supply.clearInput();

		var $pop_window = $("#pop_window_supply_add");
		$pop_window.dialog({
			resizable : false,
			width : '640px',
			modal : true,
			title : "入库操作",
			buttons : {
				"取消" : function() {
					$(this).dialog("close");
				}
			},
			close: function() {
				if (page_supply.upd_cnt > 0) {
					page_supply.upd_cnt = 0;
					page_supply.findit();
				}
			}
		});
	},

	clearInput : function() {
		// 清空输入项
		$("#pop_window_supply_add input[type!='button']").val("");
		$("#pop_window_supply_add input[type!='button']").removeClass("valid errorarea-single");
		$("#pop_window_supply_add label").text("");
		$("#s_msg").html("");
	}

};
