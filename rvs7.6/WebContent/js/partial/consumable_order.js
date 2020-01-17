var page_order = {	
	servicePath : "consumable_order.do",
	//导出订购单
	output_export : function(refresh_flg){

		var row = $("#order_list").jqGrid("getGridParam", "selrow");
		var rowData = $("#order_list").getRowData(row);
		
		var data ={
			"consumable_order_key":rowData.consumable_order_key,
			"order_no":rowData.order_no
		};
		
		$.ajax({
			beforeSend : ajaxRequestType,
			async : true,
			url : page_order.servicePath + '?method=report',
			cache : false,
			data : data,
			type : "post",
			dataType : "json",
			success : ajaxSuccessCheck,
			error : ajaxError,
			complete : function(xhjObject) {
				var resInfo = null;
				eval("resInfo=" + xhjObject.responseText);
				if (resInfo && resInfo.fileName) {
					if ($("iframe").length > 0) {
						$("iframe").attr("src", "download.do" + "?method=output&filePath=" + resInfo.filePath+"&fileName="+resInfo.fileName);
					} else {
						var iframe = document.createElement("iframe");
						iframe.src = "download.do" + "?method=output&filePath=" + resInfo.filePath+"&fileName="+resInfo.fileName;
						iframe.style.display = "none";
						document.body.appendChild(iframe);
					}
					if (refresh_flg == "1") {
						//按原条件检索, 更新一览
						page_order.findit();
					}
				} else {
					alert("文件导出失败！"); 
				}
			}
		});
	},
		
	reset : function() {
		$("#search_o_code").val("");
		$("#search_o_order_no").val("");
		$("#search_order_date_start").val("");
		$("#search_order_date_end").val("");
	},
	
	findit : function() {
		var searchData = {
			"consumable_order_key" : $("#hidden_key").val(),
			"code" : $("#search_o_code").val(),
			"order_no" : $("#search_o_order_no").val(),
			"order_date_start" : $("#search_order_date_start").val(),
			"order_date_end" : $("#search_order_date_end").val()
		};

		$.ajax({
			beforeSend : ajaxRequestType,
			async : true,
			url : page_order.servicePath + '?method=search',
			cache : false,
			data : searchData,
			type : "post",
			dataType : "json",
			success : ajaxSuccessCheck,
			error : ajaxError,
			complete : page_order.doInit_ajaxSuccess
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
				page_order.show_order_list(resInfo.order_list);
			}
		} catch (e) {
			alert("name: " + e.name + " message: " + e.message + " lineNumber: "
					+ e.lineNumber + " fileName: " + e.fileName);
		};
	},
	
	show_order_list : function(orderList) {
		if ($("#gbox_order_list").length > 0) {
			// jqGrid已构建的情况下,重载数据并刷新
			$("#order_list").jqGrid().clearGridData();
			$("#order_list").jqGrid('setGridParam', {data : orderList}).trigger("reloadGrid", [{current : false}]);
		} else {
			$("#order_list").jqGrid({
				data : orderList,
				height : 461,
				width : 992,
				rowheight : 23,
				datatype : "local",
				colNames : ['consumable_order_key', '订购单编号', '订购品数', '生成日时', '发布状态'],
				colModel : [{
					name : 'consumable_order_key',
					index : 'consumable_order_key',
					hidden : true
				}, {
					name : 'order_no',
					index : 'order_no',
					width : 60
				}, {
					name : 'sum_order_quantity',
					index : 'sum_order_quantity',
					align : 'right',
					formatter : 'integer',
					sorttype : 'int',
					width : 60,
					formatoptions: {
						defaultValue: ' - ', thousandsSeparator: ",", prefix: ""
					}
				}, {
					name : 'create_time',
					index : 'create_time',
					align : 'center',
							width : 50,
							formatter:'date', formatoptions:{srcformat:'Y/m/d H:i:s',newformat:'m-d'}
				}, {
					name : 'sent',
					index : 'sent',
					align : 'center',
					width : 50,
					formatter:'select', editoptions:{value:"1:已发布;0:未发布"}
				}],
				rowNum : 35,
				rownumbers:true,
				toppager : false,
				pager : "#order_list_pager",
				viewrecords : true,
				gridview : true,
				pagerpos : 'right',
				pgbuttons : true,
				pginput : false,
				recordpos : 'left',
				hidegrid : false,
				deselectAfterSort : false,
				ondblClickRow : page_order.showOrderEdit,
				onSelectRow : page_order.enableButton,
				viewsortcols : [true, 'vertical', true]
			});
		}
	},

	/* 判断废改订按钮enable、disable */
	enableButton : function() {
		// 选择行，并获取行数
		var row = $("#order_list").jqGrid("getGridParam", "selrow");
		var rowData = $("#order_list").getRowData(row);
		if (rowData != null) {
			if (rowData.sent == 0) {
				$("#remove_o_button").enable();
			} else {
				$("#remove_o_button").disable();
			}
			$("#output_export_button").enable();
		} else {
			$("#remove_o_button,#output_export_button").disable();
		}
	},

	showOrderAdd : function() {
		$.ajax({
			beforeSend : ajaxRequestType,
			async : false,
			url : page_order.servicePath + '?method=createOrder',
			cache : false,
			data : null,
			type : "post",
			dataType : "json",
			success : ajaxSuccessCheck,
			error : ajaxError,
			complete : page_order.findit
		});
	},

	/**
	 * 编辑订购单
	 */
	showOrderEdit : function() {
		var isFact = $('#hidden_isFact').val();
		if (isFact == "false") {
			return;
		}

		$("#pop_window_order_edit tr:gt(0)").remove();
		
		var row = $("#order_list").jqGrid("getGridParam", "selrow");// 得到选中行的ID
		var rowData = $("#order_list").getRowData(row);
		var data = {
			"consumable_order_key" : rowData.consumable_order_key,
			"sent" : rowData.sent
		};
		$.ajax({
			beforeSend : ajaxRequestType,
			async : true,
			url : page_order.servicePath + '?method=getDetail',
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

						var $pop_window = $("#pop_window_order_edit");
						if (rowData.sent == "1") {
							page_order.setDetailValueRead(resInfo.detail_list);

							$pop_window.dialog({
								resizable : false,
								width : 460,
								modal : true,
								title : "订购单详细",
								buttons : {
									"取消" : function() {
										$(this).dialog("close");
									}
								}
							});
						} else {
							page_order.setDetailValue(resInfo.detail_list);

							$pop_window.dialog({
								resizable : false,
								width : 860,
								height : 600,
								modal : true,
								title : "订购单编辑",
								buttons : {
									"确认" : function() {										
										var data = {};
										page_order.setUpdateValue(data, rowData.consumable_order_key, "");

										$.ajax({
											beforeSend : ajaxRequestType,
											async : true,
											url : page_order.servicePath + '?method=doUpdate',
											cache : false,
											data : data,
											type : "post",
											dataType : "json",
											success : ajaxSuccessCheck,
											error : ajaxError,
											complete : page_order.doUpdate_ajaxSuccess
										});
									},
									"确认并导出" : function() {
										var data = {};
										page_order.setUpdateValue(data, rowData.consumable_order_key, "1");

										$.ajax({
											beforeSend : ajaxRequestType,
											async : true,
											url : page_order.servicePath + '?method=doUpdate',
											cache : false,
											data : data,
											type : "post",
											dataType : "json",
											success : ajaxSuccessCheck,
											error : ajaxError,
											complete : page_order.doUpdateExp_ajaxSuccess
										});
									},
									"取消" : function() {
										$(this).dialog("close");
									}
								}
							});
						}
					}
				} catch (e) {
					alert("name: " + e.name + " message: " + e.message + " lineNumber: "
							+ e.lineNumber + " fileName: " + e.fileName);
				};
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
				$("#pop_window_order_edit").dialog("close");
				page_order.findit();
			}
		} catch (e) {
			alert("name: " + e.name + " message: " + e.message + " lineNumber: "
					+ e.lineNumber + " fileName: " + e.fileName);
		};
	},

	doUpdateExp_ajaxSuccess : function(xhrobj, textStatus) {
		var resInfo = null;
		try {
			// 以Object形式读取JSON
			eval('resInfo =' + xhrobj.responseText);
	
			if (resInfo.errors.length > 0) {
				// 共通出错信息框
				treatBackMessages(null, resInfo.errors);
			} else {
				$("#pop_window_order_edit").dialog("close");
				page_order.output_export("1");
			}
		} catch (e) {
			alert("name: " + e.name + " message: " + e.message + " lineNumber: "
					+ e.lineNumber + " fileName: " + e.fileName);
		};
	},

	setDetailValueRead : function(detail_list) {
		var content = "";
		content = content + "<tr>"
						+ 	"<td class='ui-state-default td-title'>消耗品代码</td>"
						+ 	"<td class='ui-state-default td-title'>消耗品说明</td>"
						+ 	"<td class='ui-state-default td-title'>订购数量</td>"
						+ "</tr>";

		for (var i = 0; i < detail_list.length; i++) {
			var one = detail_list[i];

			$("#o_order_no").text(one.order_no);
					content += "<tr>";
					content += "<td class='td-content'>";
					if(one.hazardous_flg == 1){
						content +=	"<label style='color:red;'>" + one.code + "</label>";
					}else{
						content +=	"<label>" + one.code + "</label>";
					}
					content +=   "</td>";
					content += 	"<td class='td-content'>";
					content +=		"<label>" + (one.description || "") + "</label>";
					content +=   "</td>";
					content += 	"<td class='td-content'>";
					content +=		"<label>" + one.order_quantity + "</label>";
					content +=   "</td>";
					content += "</tr>";
		}

		$("#pop_window_order_edit tbody").append(content);
	},

	setDetailValue : function(detail_list) {		
		var content = "";
		content = content + "<tr>"
						+ 	"<td class='ui-state-default td-title'>消耗品代码</td>"
						+ 	"<td class='ui-state-default td-title'>消耗品说明</td>"
						+ 	"<td class='ui-state-default td-title'>订购数量</td>"
						+ 	"<td class='ui-state-default td-title'>基准  / 安全</td>"
						+ 	"<td class='ui-state-default td-title'>有效  + 在途</td>"
						+ 	"<td class='ui-state-default td-title'>操作</td>"
						+ "</tr>";

		for (var i = 0; i < detail_list.length; i++) {
			var one = detail_list[i];

			$("#o_order_no").text(one.order_no);
				content += "<tr partial_id='" + one.partial_id + "' db_flg='" + one.db_flg + "'>";
				content += 	"<td class='td-content'>";
				if(one.hazardous_flg == 1){
					content += "<label style='color:red;'>" + one.code + "</label>";
				}else{
					content += "<label>" + one.code + "</label>";
				}
				content += 	"</td>";
				content += 	"<td class='td-content'>";
				content += 		"<label>" + (one.description || "") + "</label>";
				content += 	"</td>";
				content += 	"<td class='td-content'>";
				content += 		"<input type='number' value='" + one.order_quantity + "' min='0'></input>";
				content += 	"</td>";
				content += 	"<td class='td-content'>";
				content += 		"<label>" + one.benchmark + '/' + one.safety_lever + "</label>";
				content += 	"</td>";
				content += 	"<td class='td-content'>";
				content += 		"<label>" + one.available_inventory + '+' + one.on_passage + "</label>";
				content += 	"</td>";
				content += 	"<td class='td-content'>";
				content += 		"<input type='button' value='-'></input>";
				content += 	"</td>";
				content += "</tr>";
		}

		$("#pop_window_order_edit tbody").append(content);

		page_order.addNewRow();
	},

	deleteRow : function(obj) {
		var $tr = $(obj).parent().parent();
		var db_flg = $tr.attr("db_flg");
		if (db_flg != null && db_flg != "") {
			$tr.hide();
		} else {
			$tr.remove();
		}		
	},
  
	addNewRow : function() {
		var content = "<tr partial_id='' db_flg=''>"
		+ 	"<td class='td-content'>"
		+		"<input type='text' value='' class='ui-widget-content'></input>"
		+   	"<font color='red'><label></label></font>"
		+   "</td>"
		+ 	"<td class='td-content'></td>"
		+ 	"<td class='td-content'>"
		+		"<input type='number' value='' min='0'></input>"
		+   "</td>"
		+ 	"<td class='td-content'></td>"
		+ 	"<td class='td-content'></td>"
		+ 	"<td class='td-content'>"
		+		"<input type='button' value='+'></input>"
		+   "</td>"
		+ "</tr>";

		$("#pop_window_order_edit tbody").append(content);

		$("#pop_window_order_edit input[type='text']").unbind("keyup");
		$("#pop_window_order_edit input[type='text']").bind("keyup", function(e){
			var kcode = e.keyCode;
			if (kcode == 8 || kcode == 46){
				return false;
			}

			var $tr = $(this).parent().parent();
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
					url : page_order.servicePath + '?method=getAutocomplete',
					cache : false,
					data : data,
					type : "post",
					dataType : "json",
					success : ajaxSuccessCheck,
					error : ajaxError,
					complete : function(xhrobj){
						var resInfo = null;
						try {
							$tr.find("td:first-child label").html("");

							eval('resInfo =' + xhrobj.responseText);
							if (resInfo.reasult_flg == "1") {
								var partial = resInfo.partial;

								$tr.attr("partial_id", partial.partial_id);
								$tr.find("td:nth-child(2)").text(partial.description || "");
								$tr.find("td:nth-child(4)").text(partial.benchmark + '/' + partial.safety_lever);
								$tr.find("td:nth-child(5)").text(partial.available_inventory + '+' + partial.on_passage);
								if (partial.cm_partial_id) {
									$("#pop_window_order_edit input[type='text']").blur();
									if (!page_order.repeatCheck(partial.partial_id)) {
										$tr.find("td:first-child label").html("<br>已有订购该消耗品, 请前去修改其数量");
									}
								} else {
									$tr.find("td:first-child label").html("<br>该零件尚未作为消耗品");
								}
							}
						} catch (e) {
						}
					}
				});			
			}
		});

		$("#pop_window_order_edit :button").unbind("click");
		$("#pop_window_order_edit :button").bind("click", function(e){
			if ($(this).val() == "+") {
				var $tr = $(this).parent().parent();
				var partial_id = $tr.attr("partial_id");
				if (partial_id != null && partial_id != "") {
					$(this).val("-");
					page_order.addNewRow();
				}
			} else {
				page_order.deleteRow(this);
			}
		});
	},

	repeatCheck : function(new_partial_id) {
		var check = true;
		var i = 0;
		$("#pop_window_order_edit tbody tr:not(:first):not(:last)").each(function(index, ele) {
			var $tr = $(ele);
			var partial_id = $tr.attr("partial_id");
			if (partial_id == new_partial_id) {
				check = false;
			}
			
			i++;
		});

		return check;
	},

	setUpdateValue : function(data, key, report_flg) {
		var i = 0;
		$("#pop_window_order_edit tbody tr:gt(1)").each(function(index, ele) {
			var $tr = $(ele);

			var btn_name = $tr.find("td:nth-child(6) input").val();
			if (btn_name == "-") {
				var partial_id = $tr.attr("partial_id");
				var db_flg = $tr.attr("db_flg");
				if (partial_id != null && partial_id != "") {
					if (db_flg == "1") {
						if ($tr.is(":hidden")) {
							db_flg = "delete";
						} else {
							db_flg = "update";
						}
					} else {
						db_flg = "insert";
					}
				}
				
				var code = $tr.find("td:first-child").text().trim();//消耗品代码(Code)			
				var order_quantity = $tr.find("td:nth-child(3) input").val();//订购数量
	
				data["consumable_order.consumable_order_key["+ i +"]"] = key;
				data["consumable_order.partial_id["+ i +"]"] = partial_id;
				data["consumable_order.code["+ i +"]"] = code;
				data["consumable_order.order_quantity["+ i +"]"] = order_quantity;
				data["consumable_order.db_flg["+ i +"]"] = db_flg;
				data["consumable_order.report_flg["+ i +"]"] = report_flg;
			}

			i++;
		});
	},

	showDelete : function() {
		// 读取删除行
		var row =$("#order_list").jqGrid("getGridParam", "selrow");
		var rowData = $("#order_list").getRowData(row);
		var data = {
			"code" : rowData.consumable_order_key
		};

		$("#confirmmessage").text("删除不能恢复。确认要删除["+encodeText(rowData.order_no)+"]的记录吗？");
	 	$("#confirmmessage").dialog({
			resizable : false,
			modal : true,
			title : "删除确认",
			close: function() {
				$("#remove_o_button").disable();
			},
			buttons : {
				"确认" : function() {
					$(this).dialog("close");
					$.ajax({
						beforeSend : ajaxRequestType,
						async : true,
						url : "consumable_order.do?method=doDelete",
						cache : false,
						data : data,
						type : "post",
						dataType : "json",
						success : ajaxSuccessCheck,
						error : ajaxError,
						complete : page_order.findit
					});
				},
				"取消" : function() {
					$(this).dialog("close");
				}
			}
		});
	}
};
