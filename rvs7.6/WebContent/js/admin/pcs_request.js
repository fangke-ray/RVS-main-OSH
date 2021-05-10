var servicePath = "pcs_request.do";

var keepSearchData = {};

var posStats={};

var search_handleComplete = function(xhrObj){
	var resInfo = $.parseJSON(xhrObj.responseText);
	if (resInfo.errors && resInfo.errors.length > 0) {
		treatBackMessages("#searcharea", resInfo.errors);
		return;
	}

	var gotList = resInfo.lForms;

	var cur_page = $("#list").jqGrid().getGridParam("page");
	$("#list").jqGrid().clearGridData();
	$("#list").jqGrid('setGridParam', {data: gotList}).trigger("reloadGrid", [{current: true, page : cur_page}]);
}

var setData = function(){
	keepSearchData = {
		"request_date_start" : $("#search_request_date_start").val(),
		"request_date_end":$("#search_request_date_end").val(),
		"request_db_no" : $("#search_request_db_no").val(),
		"line_type" : $("#search_line_type").val(),
		"file_name" : $("#search_file_name").val(),
		"target_model_id" : $("#search_target_model_id").val(),
		"change_means" : $("#search_change_means").val(),
		"import_time_start" : $("#search_import_time_start").val(),
		"import_time_end" : $("#search_import_time_end").val()
	};
}

var findit = function() {
	// Ajax提交
	$.ajax({
		beforeSend: ajaxRequestType,
		async: true,
		url: servicePath + '?method=search',
		cache: false,
		data: keepSearchData,
		type: "post",
		dataType: "json",
		success: ajaxSuccessCheck,
		error: ajaxError,
		complete: search_handleComplete
	});
};

$(function() {

	$("input.ui-button").button();
	$(".ui-buttonset").buttonset();
	$("a.areacloser").hover(
		function (){$(this).addClass("ui-state-hover");},
		function (){$(this).removeClass("ui-state-hover");}
	);
	$("#searcharea span.ui-icon").bind("click", function() {
		$(this).toggleClass('ui-icon-circle-triangle-n').toggleClass('ui-icon-circle-triangle-s');
		if ($(this).hasClass('ui-icon-circle-triangle-n')) {
			$(this).parent().parent().next().show("blind");
		} else {
			$(this).parent().parent().next().hide("blind");
		}
	});

	$("#searchbutton").click(function (){
		setData();
		findit();
	});

	$("#resetbutton").click(function (){
		$("#search_request_date_start").val("");
		$("#search_request_date_end").val("");
		$("#search_request_db_no").val("");
		$("#search_description").val("");
		$("#search_line_type").val("").trigger("change");
		$("#search_file_name").val("");
		$("#search_target_model").val("").trigger("change");
		$("#search_target_model_id").val("");
		$("#search_change_means").val("").trigger("change");
		$("#search_import_time_start").val("");
		$("#search_import_time_end").val("");
	});

	$("#view_button").click(function() {
		var pcs_request_key = $("#list").jqGrid("getGridParam", "selrow");
		showPcsRequest(pcs_request_key);
	});
	$("#test_button").click(function() {
		var pcs_request_key = $("#list").jqGrid("getGridParam", "selrow");
		showTest(pcs_request_key);
	});
	$("#edit_button").click(function() {
		var pcs_request_key = $("#list").jqGrid("getGridParam", "selrow");
		showEdit(pcs_request_key);
	});
	$("#load_button").click(loadFile);
	$("#confirm_button").click(function() {
		var rowids = $("#list").jqGrid("getGridParam", "selarrrow");
		importFile(rowids);
	});
	$("#disband_button").click(function() {
		var rowids = $("#list").jqGrid("getGridParam", "selarrrow");
		remove(rowids);
	});

	$("#search_request_date_start, #search_request_date_end, #search_import_time_start, #search_import_time_end").datepicker({
		showButtonPanel:true,
		currentText: "今天"
	});

	$("#body-1 select").select2Buttons();
	setReferChooser($("#search_target_model_id"), $("#model_refer"));

	var vato = null;
	$("#detail_dialog").keypress(function(e){
	//	if ($("#old_verison_sample").length == 0) return;
		if (e.ctrlKey && !e.which) {
			if(e.keyCode == 37) {
				$("#old_verison_sample").show();
				$("#new_verison_sample").hide();
				$("#verison_alert").text("修改后").stop().show();
				clearTimeout(vato);
				vato = setTimeout(function(){$("#verison_alert").hide()}, 1000);
			}	else if(e.keyCode == 39) {
				$("#old_verison_sample").hide();
				$("#new_verison_sample").show();
				$("#verison_alert").text("修改前").stop().show();
				clearTimeout(vato);
				vato = setTimeout(function(){$("#verison_alert").hide()}, 1000);
			}
			$("#detail_dialog").focus();
		}
	});

	initGrid();
})

/** 根据条件使按钮有效/无效化 */
var enablebuttons = function() {
	var rowid = $("#list").jqGrid("getGridParam", "selrow");
	var rowids = $("#list").jqGrid("getGridParam", "selarrrow");
	if (rowid) {
		var rowdata = $("#list").jqGrid('getRowData', rowid);
		if (!rowdata.import_time.trim()) {
			$("#confirm_button").enable();
			$("#disband_button").enable();
			if (rowids.length > 1) {
				$("#test_button").disable();
				$("#edit_button").disable();
				$("#view_button").disable();
				for (var i in rowids) {
					rowdata = $("#list").jqGrid('getRowData', rowids[i]);
					if (rowdata.import_time.trim()) {
						$("#confirm_button").disable();
						$("#disband_button").disable();
						break;
					}
				}
			} else {
				$("#test_button").enable();
				$("#edit_button").enable();
			}
		} else {
			$("#test_button").disable();
			$("#confirm_button").disable();
			$("#disband_button").disable();
			$("#edit_button").disable();
		}
		if (rowids.length > 1 || rowdata.change_means != 2) {
			$("#view_button").disable();
		} else {
			$("#view_button").enable();
		}
	} else {
		$("#view_button").disable();
		$("#test_button").disable();
		$("#confirm_button").disable();
		$("#disband_button").disable();
		$("#edit_button").disable();
	}
}

var showremain = function() {

	// 标记
	var pill =$("#list");

	// 得到显示到界面的id集合
	var mya = pill.getDataIDs();
	// 当前显示多少条
	var length = mya.length;

	for (var i = 0; i < length; i++) {
		var rowdata = pill.jqGrid('getRowData', mya[i]);

		// break_message
		var import_time = rowdata["import_time"];
		if (import_time.trim() == "") {
			pill.find("tr#" + mya[i] + " td:gt(0):lt(7)").css("background-color", "#F9E29F");
		}
	}
}

var initGrid = function(){
	$("#list").jqGrid({
		data: [],
		height: 461,
		width: 1248,
		rowheight: 23,
		datatype: "local",
		colNames: ['key','依赖发布日期','依赖DB号','依赖描述', '工程检查票<br>类型', '文件名称', '针对型号',
		'影响型号', '修改<br>方式', '导入者', '导入系统<br>时间', '更改描述'],
		colModel: [

			{name:'pcs_request_key', index:'pcs_request_key',hidden:true, key: true},

			{name:'request_date', index:'request_date', width:50, align:'center', formatter:'date', formatoptions:{srcformat:'Y/m/d',newformat:'y-m-d'}},
			{name:'request_db_no',index:'request_db_no', width:70},

			{name:'description',index:'description', width:100},
			{name:'line_type',index:'line_type', width:55, align:'center', formatter:'select', editoptions: {value:$("#h_line_type_eo").val()}},
			{name:'file_name',index:'file_name', width:160},
			{name:'target_model',index:'target_model', width:75},
			{name:'reacted_model',index:'reacted_model', width:150},

			{name:'change_means',index:'change_means', width:55, align:'center', formatter:'select', editoptions: {value:$("#h_change_means_eo").val()}},
			{name:'importer_name',index:'importer_name', width:40},
			{name:'import_time', index:'import_time', width:50, align:'center', formatter:'date', formatoptions:{srcformat:'Y/m/d H:i:s',newformat:'m-d'}},
			{name:'change_detail',index:'change_detail', width:100}
		],
		rowNum: 50,
		rownumbers:true,
		toppager: false,
		pager: "#listpager",
		viewrecords: true,
		caption: "",
		ondblClickRow: function(rid, iRow, iCol, e) {
			var rowData = $("#list").getRowData(rid);
			var pcs_request_key = rowData["pcs_request_key"];
			showTest(pcs_request_key);
		},
		multiselect: true,
		gridview: true, // Speed up
		pagerpos: 'right',
		pgbuttons: true,
		pginput: false,
		recordpos: 'left',
		hidegrid: false,
		viewsortcols: [true, 'vertical', true],
		onSelectRow: enablebuttons,
		gridComplete: function(){
			enablebuttons();  showremain();
		}
	});

	setData();
	findit();

//	$("#show_process").attr("checked", true).trigger("click");
	$("#show_process").attr("checked", true).trigger("click");
}

var showPcsRequest = function(pcs_request_key) {
	$process_dialog = $("#detail_dialog");
	$process_dialog.hide();
	// 导入编辑画面
	$process_dialog.load("widgets/admin/pcs_request-view.html" ,
		function(responseText, textStatus, XMLHttpRequest) {
			var detail_buttons = {
				"关闭":function(){
					$process_dialog.dialog('close');
				}
			};
			var postData = {pcs_request_key: pcs_request_key};
			// Ajax提交
			$.ajax({
				beforeSend: ajaxRequestType,
				async: false,
				url: servicePath + '?method=getCompare',
				cache: false,
				data: postData,
				type: "post",
				dataType: "json",
				success: ajaxSuccessCheck,
				error: ajaxError,
				complete: function(xhrObj){
					var resInfo = $.parseJSON(xhrObj.responseText);
					$("#pcs_content_new").html(resInfo.pcs_content_new);
					$("#pcs_content_old").html(resInfo.pcs_content_old);

					$process_dialog.find("input,textarea").parent().css("background-color", "#93C3CD");
					$process_dialog.find("input:text").autosizeInput();
					$process_dialog.find("input[name^='EN'], input[name^='LN']").button();

					$process_dialog.dialog({
						title : "变更内容对比",
						width : 1200,
						show : "blind",
						height : 'auto' ,
						resizable : false,
						modal : true,
						minHeight : 200,
						buttons : detail_buttons
					});
					$process_dialog.show();
					$process_dialog.focus();
				}
			});
		});

};

var showEdit = function(pcs_request_key){
	$process_dialog = $("#detail_dialog");
	$process_dialog.hide();
	// 导入编辑画面
	$process_dialog.load("widgets/admin/pcs_request-load.html" ,
		function(responseText, textStatus, XMLHttpRequest) {
		$("#import_request_date").datepicker({
			showButtonPanel:true,
			defaultDate:"+0d",
			currentText: "今天"
		});
		setReferChooser($("#import_target_model_id"), $("#model_refer"));

		$("#import_form").validate({
			rules : {
				request_date : {
					required : true
				},
				request_db_no : {
					required : true
				},
				line_type : {
					required : true
				},
				change_means : {
					required : true
				}
			}
		});

		var detail_buttons = {
			"修改":function(){
				if ($("#import_form").valid()) {
					var postData = {
						pcs_request_key : pcs_request_key,
						request_date : $("#import_request_date").val(),
						request_db_no : $("#import_request_db_no").val(),
						description : $("#import_description").val(),
						change_detail : $("#import_change_detail").val()
					}
				}

	            $.ajaxFileUpload({
	                url : servicePath + '?method=doEdit', // 需要链接到服务器地址
	                secureuri : false,
	                data:postData,
	                fileElementId : 'import_file_name', // 文件选择框的id属性
	                dataType : 'json', // 服务器返回的格式
	                success : function(responseText, textStatus) {
	                    var resInfo = $.parseJSON(responseText);
                        if (resInfo.errors.length > 0) {
                            // 共通出错信息框
                            treatBackMessages("#import_form", resInfo.errors);
                        } else {
					        $process_dialog.dialog('close');

					        $("#search_target_model").val("").trigger("change");
							$("#search_target_model_id").val("");
	                        findit();
                        }
	                }
	             });
			},
			"取消":function(){
				$process_dialog.dialog('close');
			}
		};

		$("#import_line_type").html($("#h_line_type_op").html()).select2Buttons();
		$("#import_change_means").html($("#h_change_means_op").html()).select2Buttons();
		$("#import_target_model_plus").button().click(function(){
		});
		$("#import_target_model_plus").parent().on("load", "input:button", function(){$(this).button()});

		var postData = {pcs_request_key : pcs_request_key};

		// Ajax提交
		$.ajax({
			beforeSend: ajaxRequestType,
			async: false,
			url: servicePath + '?method=getForEdit',
			cache: false,
			data: postData,
			type: "post",
			dataType: "json",
			success: ajaxSuccessCheck,
			error: ajaxError,
			complete: function(xhrObj){
				var resInfo = $.parseJSON(xhrObj.responseText);
				if (resInfo.pcs_request) {
					$("#import_pcs_request_key").val(resInfo.pcs_request.pcs_request_key);
					$("#import_request_date").val(resInfo.pcs_request.request_date);
					$("#import_request_db_no").val(resInfo.pcs_request.request_db_no);
					$("#import_description").val(resInfo.pcs_request.description);
					$("#import_change_detail").val(resInfo.pcs_request.change_detail);
					$("#import_line_type").val(resInfo.pcs_request.line_type).trigger("change").disable();
					$("#import_line_type").parent().find("a").not(".picked").parent().hide();
					$("#import_change_means").val(resInfo.pcs_request.change_means).trigger("change").disable();
					$("#import_change_means").parent().find("a").not(".picked").parent().hide();
					$("#import_target_model").val(resInfo.pcs_request.target_model).disable();
					$("#target_model_id").val(resInfo.pcs_request.target_model_id);
					$("#import_target_model_plus").hide();
				}
				$process_dialog.dialog({
					title : "加载依赖模板文件",
					width : 'auto',
					show : "blind",
					height : 'auto' ,
					resizable : false,
					modal : true,
					minHeight : 200,
					buttons : detail_buttons
				});
				$process_dialog.show();
			}
		});

	});
};


var loadFile = function(){
	$process_dialog = $("#detail_dialog");
	$process_dialog.hide();
	// 导入编辑画面
	$process_dialog.load("widgets/admin/pcs_request-load.html" ,
		function(responseText, textStatus, XMLHttpRequest) {
		$("#import_request_date").datepicker({
			showButtonPanel:true,
			defaultDate:"+0d",
			currentText: "今天"
		});
		setReferChooser($("#import_target_model_id"), $("#model_refer"));

		$("#import_form").validate({
			rules : {
				request_date : {
					required : true
				},
				request_db_no : {
					required : true
				},
				line_type : {
					required : true
				},
				change_means : {
					required : true
				}
			}
		});

		var detail_buttons = {
			"上传":function(){
				if ($("#import_form").valid()) {
					var postData = {
						request_date : $("#import_request_date").val(),
						request_db_no : $("#import_request_db_no").val(),
						description : $("#import_description").val(),
						change_detail : $("#import_change_detail").val(),
						line_type : $("#import_line_type").val(),
						// target_model_id : $("#import_target_model_id").val(""),
						change_means : $("#import_change_means").val()
					}
					var iMCount = 0;
					if ($("#import_target_model_id").val()) {
						postData["models.id[" + iMCount + "]"] = $("#import_target_model_id").val();
						postData["models.name[" + iMCount + "]"] = $("#import_target_model").val();
						iMCount++;
					}
					$("#added_model_names div").each(function(idx,ele){
						postData["models.id[" + iMCount + "]"] = $(ele).attr("model_id");
						postData["models.name[" + iMCount + "]"] = $(ele).text();
						iMCount++;
					});
		            $.ajaxFileUpload({
		                url : servicePath + '?method=doCreate', // 需要链接到服务器地址
		                secureuri : false,
		                data:postData,
		                fileElementId : 'import_file_name', // 文件选择框的id属性
		                dataType : 'json', // 服务器返回的格式
		                success : function(responseText, textStatus) {
		                    var resInfo = $.parseJSON(responseText);
	                        if (resInfo.errors.length > 0) {
	                            // 共通出错信息框
	                            treatBackMessages("#import_form", resInfo.errors);
	                        } else {
	                        	if (resInfo.infoes.length > 0) {
									$process_file_dialog = $("#process_file_dialog");
									if ($process_file_dialog.length == 0) {
										$("body").append("<div id='process_file_dialog' style='width:1024px;display:none;'>");
										$process_file_dialog = $("#process_file_dialog");
									}
									var $files = $("<section/>");
									for (var iInfoer in resInfo.orgFiles) {
										$files.append("<div>"+resInfo.orgFiles[iInfoer]+"</div>");
									}
									$files.find("div").click(function(){
										$process_file_dialog.dialog("close");
										postData.org_file_name = $(this).text();
										$.ajax({
											beforeSend: ajaxRequestType,
											async: false,
											url: servicePath + '?method=doCreate',
											cache: false,
											data: postData,
											type: "post",
											dataType: "json",
											success: ajaxSuccessCheck,
											error: ajaxError,
											complete: function(xhrObj){
				 					           	$process_dialog.dialog('close');
												$("#search_target_model").val("").trigger("change");
												$("#search_target_model_id").val("");
					                            findit();
											}
										});										
									});

									$process_file_dialog.html("<section>请选择改图前的文件名：</section>");
									$process_file_dialog.append($files);
									$process_file_dialog.dialog({
										title : "选择原文件",
										width : '1024px',
										show : "blind",
										height : 'auto' ,
										resizable : false,
										modal : true,
										minHeight : 200
									});
	                        	} else {
	 					           	$process_dialog.dialog('close');
									$("#search_target_model").val("").trigger("change");
									$("#search_target_model_id").val("");
		                            findit();
	                        	}
	                        }
		                }
		             });
					
				}
 			},
			"取消":function(){
				$process_dialog.dialog('close');
			}
		};

		$("#import_line_type").html($("#h_line_type_op").html()).select2Buttons();
		$("#import_change_means").html($("#h_change_means_op").html()).select2Buttons();
		$("#import_target_model_plus").button().click(function(){
			var import_target_model_id_val = $("#import_target_model_id").val();
			if (import_target_model_id_val) {
				var added = false;
				$("#added_model_names div").each(function(idx,ele){
					if (!added) {
						if ($(ele).attr("model_id") == import_target_model_id_val) {
							added = true;
						}
					}
				});
				if (added) return;
				
				var $line = $("<div model_id='"+ import_target_model_id_val
				+"'>" + $("#import_target_model").val() + 
				"<input type='button' value='-' /></div>");
				$line.children("input").button().click(function(){$(this).parent().remove()});
				$("#added_model_names").append($line);	
			}
			$("#import_target_model_id").val("");
			$("#import_target_model").val("");
		});
		$("#import_target_model_plus").parent().on("load", "input:button", function(){$(this).button()});
//				$("#import_request_db_no").focus();
		$process_dialog.dialog({
			title : "加载依赖模板文件",
			width : 'auto',
			show : "blind",
			height : 'auto' ,
			resizable : false,
			modal : true,
			minHeight : 200,
			buttons : detail_buttons
		});
		$process_dialog.show();

	});

};

var importFile = function(keys){
	$process_dialog = $("#detail_dialog");
	$process_dialog.hide();
	// 导入编辑画面
	$process_dialog.load("widgets/admin/pcs_request-import.html" ,
		function(responseText, textStatus, XMLHttpRequest) {
		var postData = {};
		for (var iKey = 0; iKey < keys.length; iKey++) {
			var pcs_request_key = keys[iKey];
			postData["keys.pcs_request_key[" + iKey + "]"] = pcs_request_key;
		}

		var detail_buttons = {
			"确认":function(){
				if ($("#material_list").html()) {
					var m_rowids = $("#material_list").jqGrid("getGridParam", "selarrrow");
					for (var ii =0; ii< m_rowids.length;ii++) {
						postData["ignore.materiai_id[" + ii + "]"] = m_rowids[ii];
						var rowdata = $("#material_list").jqGrid('getRowData', m_rowids[ii]);
						postData["ignore.pcs_request_key[" + ii + "]"] = rowdata["pat_id"];
					}
				}
				// postData
				$.ajax({
					beforeSend: ajaxRequestType,
					async: false,
					url: servicePath + '?method=doImport',
					cache: false,
					data: postData,
					type: "post",
					dataType: "json",
					success: ajaxSuccessCheck,
					error: ajaxError,
					complete: function(xhrObj){
						infoPop("已经成功导入系统并生效！", null, "导入完成");
						findit();
					}
				});

				$process_dialog.dialog('close');
			},
			"关闭":function(){
				$process_dialog.dialog('close');
			}
		};

		$.ajax({
			beforeSend: ajaxRequestType,
			async: false,
			url: servicePath + '?method=getWorkings',
			cache: false,
			data: postData,
			type: "post",
			dataType: "json",
			success: ajaxSuccessCheck,
			error: ajaxError,
			complete: function(xhrObj){
				var resInfo = $.parseJSON(xhrObj.responseText);
				if (resInfo.materials.length > 0) {
					$("#import_choose_area").show();
					$("#import_direct_area").hide();
					$("#material_list").jqGrid({
						data: resInfo.materials,
						height: 461,
						width: 461,
						rowheight: 23,
						datatype: "local",
						colNames: ['material_id','维修对象','型号', '等级', '当前进度', 'pcs_request_key'],
						colModel: [
							{name:'material_id', index:'material_id',hidden:true, key: true},
							{name:'sorc_no',index:'sorc_no', width:50},
							{name:'model_name',index:'model_name', width:80},
							{name:'level',index:'level', width:20, 
								formatter: "select", editoptions:{value:$("#h_m_level_eo").val()},
							align:'center'},
							{name:'processing_position',index:'processing_position', width:70},
							{name:'pat_id', index:'pat_id',hidden:true}
						],
						rowNum: 50,
						rownumbers:true,
						toppager: false,
						pager: "#material_listpager",
						viewrecords: true,
						caption: "",
						ondblClickRow: function(rid, iRow, iCol, e) {},
						multiselect: true,
						gridview: true, // Speed up
						pagerpos: 'right',
						pgbuttons: true,
						pginput: false,
						recordpos: 'left',
						hidegrid: false,
						viewsortcols: [true, 'vertical', true],
						onSelectRow: enablebuttons,
						gridComplete: function(){}
					});
            	} else {
             		$("#import_choose_area").hide();
            		$("#import_direct_area").show();
         	  	}

				$process_dialog.dialog({
					title : "加载依赖模板文件",
					width : 'auto',
					show : "blind",
					height : 'auto' ,
					resizable : false,
					modal : true,
					minHeight : 200,
					buttons : detail_buttons
				});
				$process_dialog.show();
			}
		});
	});
};

var remove = function(keys){
	$process_dialog = $("#detail_dialog");
	$process_dialog.hide();

//	$process_dialog.html("确定要取消这些变更依赖？");

	var postData = {};
	for (var iKey = 0; iKey < keys.length; iKey++) {
		var pcs_request_key = keys[iKey];
		postData["keys.pcs_request_key[" + iKey + "]"] = pcs_request_key;
	}

	var detail_buttons = {
		"确认":function(){
			$process_dialog.dialog('close');
		},
		"关闭":function(){
			$process_dialog.dialog('close');
		}
	};

	$.ajax({
		beforeSend: ajaxRequestType,
		async: false,
		url: servicePath + '?method=doRemove',
		cache: false,
		data: postData,
		type: "post",
		dataType: "json",
		success: ajaxSuccessCheck,
		error: ajaxError,
		complete: function(xhrObj){
			$("#confirm_message").html("已经移除变更依赖！");
			$("#confirm_message").dialog({
				title : "处理完成",
				width : 'auto',
				show : "blind",
				height : 'auto' ,
				resizable : false,
				modal : true,
				minHeight : 200,
				buttons : {
					"确认":function(){
						$("#confirm_message").dialog('close');
						findit();
					}
				}
			});
		}
	});
};

/** 工程检查票赋值 */
var valuePcs = function(data) {
	var pcs_values = {};
	var pcs_comments = {};
	$("#pcs_content_test input").each(function(){
		if (this.type == "text") {
			pcs_values[this.name] = this.value;
		} else {
			if (this.className == "i_total_hidden")
				pcs_values[this.name] = this.value;
			else
				if (this.name && this.name != "") pcs_values[this.name] = "";
		}
	});
	$("#pcs_content_test input:checked").each(function(){
		if (this.type == "radio"){
			pcs_values[this.name] = this.value;
		} else if (this.type == "checkbox"){
			if (pcs_values[this.name] == null || pcs_values[this.name] == "") {
				pcs_values[this.name] = this.value;
			} else {
				pcs_values[this.name] = pcs_values[this.name] + "," + this.value;
			}
		}
	});
	$("#pcs_content_test textarea").each(function(){
		if (this.value) {
		pcs_comments[this.name] = this.value;
		}
	});

	data.pcs_inputs = Json_to_String(pcs_values);
	data.pcs_comments = Json_to_String(pcs_comments);

	for (var v in pcs_values) {
		if (pcs_values[v] == null || pcs_values[v] == "") return true;
	}
	return false;
}

var pentities = null;

var downPdf = function(temp_file) {
	if ($("iframe").length > 0) {
		$("iframe").attr("src", "download.do?method=output&from=cache&filePath=" + temp_file+"&fileName=test.pdf");
	} else {
		var iframe = document.createElement("iframe");
		iframe.src = "download.do?method=output&from=cache&filePath=" + temp_file+"&fileName=test.pdf";
		iframe.style.display = "none";
		document.body.appendChild(iframe);
	}
}

var showTest = function(pcs_request_key){

	$process_dialog = $("#detail_dialog");
	$process_dialog.hide();
	// 导入编辑画面
	$process_dialog.load("widgets/admin/pcs_request-test.html" ,
		function(responseText, textStatus, XMLHttpRequest) {
			var detail_buttons = {
				"完成并且归档":function(){
					var postData = {
						pcs_request_key : pcs_request_key
					}

					var hasBlank = valuePcs(postData);
					var mapPc2Jn = {};
					for (var i_pentities in pentities) {
						var pentity = pentities[i_pentities];
						mapPc2Jn[pentity.process_code] = pentity.line_name;
					}
					postData.mapPc2Jn = Json_to_String(mapPc2Jn);

					if (hasBlank) {
						if ($('div#errstring').length == 0) {
							$("body").append("<div id='errstring'/>");
						}
						$('div#errstring').show();
						$('div#errstring').html("<span class='errorarea'>还有未输入的项目，就按照当前输入内容生成吗？</span>");
						$('div#errstring').dialog({
							dialogClass : 'ui-warn-dialog',
							modal : true,
							width : 450,
							title : "提示信息",
							buttons :{
								"按此生成":function(){
									// Ajax提交
									$.ajax({
										beforeSend: ajaxRequestType,
										async: false,
										url: servicePath + '?method=makeTestPdf',
										cache: false,
										data: postData,
										type: "post",
										dataType: "json",
										success: ajaxSuccessCheck,
										error: ajaxError,
										complete: function(xhrObj){
											var resInfo = $.parseJSON(xhrObj.responseText);
											downPdf(resInfo.temp_file);
										}
									});
									$('div#errstring').dialog("close");
									$process_dialog.dialog('close');
								},
								"取消":function(){
									$('div#errstring').dialog("close");
								}
							}
						});
					} else {
						$.ajax({
							beforeSend: ajaxRequestType,
							async: false,
							url: servicePath + '?method=makeTestPdf',
							cache: false,
							data: postData,
							type: "post",
							dataType: "json",
							success: ajaxSuccessCheck,
							error: ajaxError,
							complete: function(xhrObj){
								var resInfo = $.parseJSON(xhrObj.responseText);
								downPdf(resInfo.temp_file);
							}
						});
						$process_dialog.dialog('close');
					}
				},
				"取消":function(){
					$process_dialog.dialog('close');
				}
			};

			var postData = {pcs_request_key: pcs_request_key};

			// Ajax提交
			$.ajax({
				beforeSend: ajaxRequestType,
				async: false,
				url: servicePath + '?method=getTest',
				cache: false,
				data: postData,
				type: "post",
				dataType: "json",
				success: ajaxSuccessCheck,
				error: ajaxError,
				complete: function(xhrObj){
					var resInfo = $.parseJSON(xhrObj.responseText);
					$("#pcs_content_test").html(resInfo.pcs_content_test);

					$process_dialog.find("input,textarea").parent().css("background-color", "#93C3CD");
					$process_dialog.find("input:text").autosizeInput();
					$process_dialog.find("input[name^='EN'], input[name^='LN']").button();
					$process_dialog.find(".tag_m").click(function(){
						var code = $(this).attr("code");
						if($process_dialog.find(".tag_m[code="+ code +"][value=-1]:checked").length) {
							$process_dialog.find(".i_total[code="+ code +"]").text("不合格")
							.addClass("forbid")
							.next().val("-1");
						} else {
							$process_dialog.find(".i_total[code="+ code +"]").text("合格")
							.removeClass("forbid")
							.next().val("1");
						}
					});

					pentities = resInfo.pentities;
					var options = "<option code='emp' value='emp' selected='selected'>(查看)</option>";
					for (var i_pentities in pentities) {
						var pentity = pentities[i_pentities];
						options += "<option value='"+pentity.position_id+"' code='"+pentity.process_code+"' " +
								"opt='"+pentity.line_id+"' job_no='"+pentity.line_name+"'>"
							+pentity.process_code+"	"+pentity.name+"</option>"
					}

					$("#test_position").html(options).select2Buttons().change(function(){
						$process_dialog.find("section.icom[locate^='EM']").text("");
						var $option = $(this).children("option:selected");
						if ($option.length > 0) {
							var process_code = $option.attr("code");
							if (process_code == posStats.process_code) {
								return;
							}
							$process_dialog.find(".i_com[code="+process_code+"]").hide();
							$process_dialog.find(".i_act[code="+process_code+"]").show().each(function(idx, ele){
								$process_dialog.find("label[for=" + ele.id + "]").show();
							});

							$process_dialog.find(".i_com[code="+posStats.process_code+"]").show();
							$process_dialog.find(".i_act[code="+posStats.process_code+"]").each(function(idx, ele){
								var $ele = $(ele);
								if ($ele.attr("type") == "text") {
									$ele.parent().find("label[locate="+ele.name+"]").text(ele.value);
								} else if ($ele.hasClass("tag_n")) {
									if ($ele.is(":checked")) {
										if (ele.value == "0") {
											$ele.parent().find("section[locate="+ele.name+"]").text("");
										} else if (ele.value == "1") {
											$ele.parent().find("section[locate="+ele.name+"]")
												.html("<img src='/images/sign/"+posStats.job_no+"'></img>");
										} else if (ele.value == "-1") {
											$ele.parent().find("section[locate="+ele.name+"]").text("不操作");
										}
									}
								} else if ($ele.attr("type") == "radio") {
									var $section = $ele.parent().find("section.i_com");
									if ($section.length == 0) {
										if ($ele.is(":checked")) {
											$ele.parent().find("label[locate="+ele.name+"][value="+ele.value+"]").text("√");
										} else {
											$ele.parent().find("label[locate="+ele.name+"][value="+ele.value+"]").text("　");
										}
									} else {
										if ($ele.is(":checked")) {
											$section.text($ele.next().text());
										}
									}
								}

								$process_dialog.find("label[for=" + ele.id + "]").hide();
								$ele.hide();
							});
							$process_dialog.find("textarea[code]").each(function(idx,ele){
								var $ele = $(ele);
								var eleVal = $ele.val();
								if (eleVal) {
									var $targ = $ele.parent().find("textarea[name=" + ele.name + "_container]");
									$targ.text($targ.text() + "\n@" + posStats.process_code + ": " + eleVal);
									$ele.val("");
								}
								var thisCode = $ele.attr("code");
								if (thisCode == "000" || thisCode == process_code) {
									$ele.removeAttr("readonly");
								} else {
									$ele.attr("readonly", "true");
								}
							});

							posStats.process_code = process_code;
							posStats.position_id = $option.attr("value");
							posStats.operator_id = $option.attr("opt");
							posStats.job_no = $option.attr("job_no");
						}	
					});
					$process_dialog.find(".i_act").hide().each(function(idx, ele){
						$process_dialog.find("label[for=" + ele.id + "]").hide();
					});
					$process_dialog.find(".i_com").show();

					$process_dialog.dialog({
						title : "测试新模板",
						width : 1240,
						show : "blind",
						height : 600 ,
						resizable : false,
						modal : true,
						minHeight : 200,
						buttons : detail_buttons
					});
					$process_dialog.show();
					$process_dialog.focus();
				}
			});
		});
};