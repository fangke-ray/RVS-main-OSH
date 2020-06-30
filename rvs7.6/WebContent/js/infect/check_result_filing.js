var servicePath="check_result_filing.do";
$(function(){
	$("input.ui-button").button();
	$("#edit_value_currency,#select_sheet_manage_no,#select_cycle_type,#edit_trigger_state,#edit_data_type").select2Buttons();
	/*为每一个匹配的元素的特定事件绑定一个事件处理函数*/
	$("#searcharea span.ui-icon, #detailsearcharea span.ui-icon").bind("click", function() {
		$(this).toggleClass('ui-icon-circle-triangle-n').toggleClass('ui-icon-circle-triangle-s');
		if ($(this).hasClass('ui-icon-circle-triangle-n')) {
			$(this).parent().parent().next().show("blind");
		} else {
			$(this).parent().parent().next().hide("blind");
		}
	});

	setReferChooser($("#hidden_devices_type_id"),$("#name_refer"));
	setReferChooser($("#hidden_sheet_file_name"),$("#sheet_file_name_referchooser"));
	setReferChooser($("#hidden_device_name"),$("#device_name_referchooser"));
	setReferChooser($("#hidden_detail_devices_manage_id"),$("#device_name_referchooser"));
	setReferChooser($("#hidden_detail_jig_manage_id"),$("#jig_name_referchooser"));

	$("#search_access_place,#search_cycle_type,#search_branch").select2Buttons();

	$("#search_filing_date_start, #search_filing_date_end").datepicker({
		showButtonPanel:true,
		dateFormat: "yy/mm/dd",
		currentText: "今天"
	});

	//检索button
	$("#searchbutton").click(function(){
		findit();
	});

	//清除button
	$("#resetbutton").click(function(){
		$("#search_daily_sheet_manage_no").val("");
		$("#search_daily_sheet_file").val("");
		$("#search_access_place").val("").trigger("change");
		$("#search_cycle_type").val("").trigger("change");
		$("#search_name").val("");
		$("#hidden_devices_type_id").val("");
	});

		//新建治具点检种类---取消button
	$("#come_back_button").click(function() {
		showList();
	});

	$("#detailsearchbutton").click(function(){
		findEdit();
	});

	$("#detailresetbutton").click(function(){
		$("#search_devices_manage_id").val("");
		$("#hidden_detail_devices_manage_id").val("");
		$("#search_jig_manage_id").val("");
		$("#hidden_detail_jig_manage_id").val("");
		$("#search_branch").val("").trigger("change");
		$("#search_filing_date_start").val("");
		$("#search_filing_date_end").val("");
	});

	findit();

	//上传附表
	$("#upload_branch_button").click(function(){

		$("#upload_sheet_file_name,#upload_filing_date,#upload_device_name,#upload_schedule_file,#upload_start_record_date").val("");

		$("#upload_filing_date,#upload_start_record_date").datepicker({
			showButtonPanel : true,
			dateFormat : "yy/mm/dd",
			currentText : "今天"
		});

		$("#uploadform").validate({
			rules:{
				sheet_file_name:{
					required:true
				},
				filing_date:{
					required:true
				},
				device_name:{
					required:true
				},
				file:{
					required:true
				}
			},
			ignore:''
		});

		$("#upload_schedule").dialog({
			modal : true,
			width : 360,
			height:360,
			title : "上传附表",
			buttons : {
				"确认" : function() {
					var data = {
						"check_file_manage_id":$("#hidden_sheet_file_name").val(),
						"check_manage_code":$("#upload_sheet_file_name").val(),
						"filing_date":$("#upload_filing_date").val(),
						"start_record_date":$("#upload_start_record_date").val(),
						"devices_manage_id":$("#hidden_device_name").val()
					}

					if($("#uploadform").valid()){
						$.ajaxFileUpload({
							url : servicePath + '?method=doInsert', // 需要链接到服务器地址
							secureuri : false,
							data:data,
							fileElementId : 'upload_schedule_file', // 文件选择框的id属性
							dataType : 'json', // 服务器返回的格式
							success : function(responseText, textStatus) {
								var resInfo = null;
								try {
									// 以Object形式读取JSON
									eval('resInfo =' + responseText);
									if (resInfo.errors.length > 0) {
										// 共通出错信息框
										treatBackMessages("#uploadform", resInfo.errors);
									} else {
										$("#upload_schedule").text("上传附表已经完成。");
										$("#upload_schedule").dialog({
											width : 320,
											height : 'auto',
											resizable : false,
											show : "blind",
											modal : true,
											title : "上传附表",
											buttons : {
												"关闭" : function() {
													$(this).dialog("close");
													findit();
												}
											}
										});
									}
								} catch (e) {
									alert("name: " + e.name + " message: " + e.message + " lineNumber: "+ e.lineNumber + " fileName: " + e.fileName);
								};
							}
						});
					}
				},
				"取消" : function() {
					$(this).dialog("close");
				}
			}

		});
	});
})

/*检索按钮事件*/
var findit = function() {
	var data={
		"check_manage_code":$("#search_daily_sheet_manage_no").val(),
		"sheet_file_name":$("#search_daily_sheet_file").val(),
		"devices_type_id":$("#hidden_devices_type_id").val(),
		"access_place":$("#search_access_place").val(),
		"cycle_type":$("#search_cycle_type").val()
	};
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

//检索完成函数
var search_handleComplete=function(xhrobj, textStatus){
	var resInfo = null;
	try {
		// 以Object形式读取JSON
		eval('resInfo =' + xhrobj.responseText);
		if (resInfo.errors.length > 0) {
			// 共通出错信息框
			treatBackMessages("", resInfo.errors);
		} else {
			filed_list(resInfo.finished);
		}
	} catch (e) {
		alert("name: " + e.name + " message: " + e.message + " lineNumber: "+ e.lineNumber + " fileName: " + e.fileName);
	};
};

/*jqgrid表格*/
function filed_list(finished){
	if ($("#gbox_list").length > 0) {
		$("#list").jqGrid().clearGridData();
		$("#list").jqGrid('setGridParam',{data:finished}).trigger("reloadGrid", [{current:false}]);
	} else {
		$("#list").jqGrid({
			data:finished,
			height: 461,
			width: 992,
			rowheight: 23,
			datatype: "local",
			colNames:['点检表管理ID','点检表管理号','点检表文件','类型','归档周期','使用设备工具品名','使用设备工具品名ID'],
			colModel:[
				{name:'check_file_manage_id',index:'check_file_manage_id',hidden:true},
				{name:'check_manage_code',index:'check_manage_code',width:130},
				{name:'sheet_file_name',index:'sheet_file_name',width:300},
				{name:'access_place',index:'access_place',width:50,align:'center',formatter:'select',
					editoptions:{
						value:$("#sAccessPlace").val()
					}
				},
				{name:'cycle_type',index:'cycle_type',width:50,align:'center',formatter:'select',
					editoptions:{
						value:$("#sCycleType").val()
					}
				},
				{name:'name',index:'name',width:100},
				{name:'devices_type_id',index:'devices_type_id',hidden:true}
			],
			rowNum: 20,
			toppager : false,
			pager : "#list_pager",
			viewrecords : true,
			gridview : true,
			pagerpos : 'right',
			pgbuttons : true,
			rownumbers : true,
			pginput : false,
			recordpos : 'left',
			hidegrid : false,
			caption:'点检结果归档一览',
			deselectAfterSort : false,
			ondblClickRow : showEdit,
			viewsortcols : [true,'vertical',true]
		});

}
};

/*编辑页面*/
var showEdit = function() {
	$("#detail,#editarea").show();
	$("#searcharea,#search_item,#searchform,#listarea").hide();
	$("#search_devices_manage_id").val("");
	$("#hidden_detail_devices_manage_id").val("");
	$("#search_jig_manage_id").val("");
	$("#hidden_detail_jig_manage_id").val("");
	$("#search_branch").val("");
	$("#search_filing_date_start").val("");
	$("#search_filing_date_end").val("");

	//双击查询详细
	var row = $("#list").jqGrid("getGridParam", "selrow");// 得到选中行的ID
	var rowData = $("#list").getRowData(row);
	var data={
		"check_file_manage_id":rowData.check_file_manage_id
	};
	if (rowData.check_file_manage_id == "00000000000") {
		$("#search_devices_manage_id").hide();
		$("#search_jig_manage_id").show();
	} else {
		$("#search_devices_manage_id").show();
		$("#search_jig_manage_id").hide();
	}
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
		complete : detail_handleComplete
	});
}
//双击详细显示
var detail_handleComplete=function(xhrobj, textStatus){
	var resInfo = null;
	try {
		// 以Object形式读取JSON
		eval('resInfo =' + xhrobj.responseText);
		if (resInfo.errors.length > 0) {
			// 共通出错信息框
			treatBackMessages("", resInfo.errors);
		} else {
			detail_list(resInfo.finished);
		}
	} catch (e) {
		alert("name: " + e.name + " message: " + e.message + " lineNumber: "+ e.lineNumber + " fileName: " + e.fileName);
	};
};

/*下载*/
var downExcel = function(fileName,work_period,check_manage_code) {
	var dlSrc = "filingdownload.do"+"?method=output&filePath=%2F"+work_period +"%2F"+ check_manage_code+"%2F&fileName="+ fileName;
	if ($("iframe").length > 0) {
		$("iframe").attr("src", dlSrc);
	} else {
		var iframe = document.createElement("iframe");
		iframe.src = dlSrc;
		iframe.style.display = "none";
		document.body.appendChild(iframe);
	}
}

/*点检归档文件一览*/
function detail_list(item_list){
	if ($("#gbox_detail_list").length > 0) {
		$("#detail_list").jqGrid().clearGridData();
		$("#detail_list").jqGrid('setGridParam',{data:item_list}).trigger("reloadGrid", [{current:false}]);
	} else {
		$("#detail_list").jqGrid({
			data:item_list,
			height: 231,
			width: 990,
			rowheight: 23,
			datatype: "local",
			colNames:['治具点检ID','点检表管理号','文件从属','点检表文件','归档期间','归档期间'],
			colModel:[
				{name:'tools_check_id',index:'tools_check_id',width : 70,hidden:true},
				{name:'check_manage_code',index:'check_manage_code',width : 60,align:'left'},
				{name:'branch',index:'branch',width : 30,align:'center',formatter : 'select',
					editoptions : {
						value : $("#h_branch").val()
					}},
				{name:'storage_file_name',index:'storage_file_name',width : 100,align:'left',
					formatter : function(value, options, rData){
						return "<a href='javascript:downExcel(\""+ value+".pdf"+"\",\""+rData['work_period'].substring(0,4)+"\""+",\""+rData['check_manage_code']+"\");' >" + value + "</a>";
					}
				},
				{name:'filing_date',index:'filing_date',hidden:true},
				{name:'work_period',index:'work_period',width : 40,align:'center'}
			],
			rowNum: 20,
			toppager : false,
			pager : "#detail_list_listpager",
			viewrecords : true,
			gridview : true,
			pagerpos : 'right',
			pgbuttons : true,
			pginput : false,
			rownumbers : true,
			recordpos : 'left',
			hidegrid : false,
			caption:'点检归档文件一览',
			deselectAfterSort : false,
			viewsortcols : [true,'vertical',true]
		});

	}
};

/*编辑页面*/
var findEdit = function() {
	//查询详细
	var row = $("#list").jqGrid("getGridParam", "selrow");
	var rowData = $("#list").getRowData(row);
	var data={
		"check_file_manage_id":rowData.check_file_manage_id,
		"devices_manage_id":$("#hidden_detail_devices_manage_id").val(),
		"branch":$("#search_branch").val(),
		"filing_date_start":$("#search_filing_date_start").val(),
		"filing_date_end":$("#search_filing_date_end").val()
	};
	if (rowData.check_file_manage_id == "00000000000") {
		data["devices_manage_id"] = $("#hidden_detail_jig_manage_id").val()
	}
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
		complete : detail_handleComplete
	});
};

/*删除*/
var showDelete = function(rid) {
	var rowData = $("#list").getRowData(rid);
	$("#confirmmessage").text("删除不能恢复。确认要删除["+rowData.daily_sheet_manage_no+"]的记录吗？");
	$("#confirmmessage").dialog({
		resizable:false,
		modal : true,
		title : "删除确认",
		buttons : {
			"确认" : function() {
				$(this).dialog("close");
				$("#confirmmessage").text("删除已经完成。");
				$("#confirmmessage").dialog({
					width : 320,
					height : 'auto',
					resizable : false,
					show : "blind",
					modal : true,
					title : "删除",
					buttons : {
						"关闭" : function() {
							$(this).dialog("close");
						}
					}
				});
			},
			"取消" : function() {
				$(this).dialog("close");
			}
		}
	});
};

/*初始页面显示*/
var showList = function() {
	$("#editarea").hide();
	$("#searcharea,#search_item,#searchform,#listarea").show();
	$("#listarea").show();
	$("#detail").show();
}