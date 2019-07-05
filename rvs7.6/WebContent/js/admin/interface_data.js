/** 一览数据对象 */
var listdata = {};
/** 服务器处理路径 */
var servicePath = "interface_data.do";

$(function() {	
	doInit();
});

var kind="";
var if_sap_message_key="";
var seq ="";
var curpagenum = 1;

/**
 * 一览查询
 */
var doInit = function(){
	// Ajax提交
	$.ajax({
		beforeSend : ajaxRequestType,
		async : false,
		url : servicePath + '?method=searchAll',
		cache : false,
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : doInit_ajaxSuccess
	});
};

/**
 * 一览查询回调函数
 * @param {} xhrobj
 * @param {} textStatus
 */
var doInit_ajaxSuccess = function(xhrobj, textStatus){
	try {
		// 以Object形式读取JSON
		eval('resInfo =' + xhrobj.responseText);

		listsize = resInfo.finished.length;
		//countMaxPage();
		filed_list(resInfo.finished);
	} catch (e) {
		alert("name: " + e.name + " message: " + e.message + " lineNumber: " + e.lineNumber + " fileName: " + e.fileName);
	};
};

/**
 * 
 * @param {} listdata
 */
function filed_list(listdata){
	if ($("#gbox_exd_list").length > 0) {
		$("#exd_list").jqGrid().clearGridData();

		var maxpage = parseInt((listdata.length - 1) / 20) + 1;
		if (curpagenum > maxpage) {
			curpagenum = maxpage;
		}

		$("#exd_list").jqGrid('setGridParam', {data : listdata}).jqGrid('setGridParam', {page:curpagenum}).trigger("reloadGrid", [{current : false}]);
	} else {
		$("#exd_list").jqGrid({
			toppager : true,
			data: listdata,
			height: 461,
			width: 992,
			rowheight: 23,
			datatype: "local",
			colNames:['分类','KEY','SEQ','OMR通知单号','错误消息'],
			colModel:[
					{name:'kind',index:'kind',width:30,align:'center',formatter:'select', editoptions:{value:"recept:修理品接收;approve:投线;part_order:零件订购"}},
					{name: 'if_sap_message_key', index: 'if_sap_message_key', width:30, sorttype:'int'},
					{name: 'seq', index: 'seq', width:15},
					{name: 'OMRNotifiNo', index: 'OMRNotifiNo', width:30},
					{name: 'invalid_message', index: 'invalid_message'}
			],
			rowNum: 20,
			toppager: false,
			pager: "#exd_listpager",
			viewrecords: true,
			hidegrid : false,
			ondblClickRow : function(rid, iRow, iCol, e) {
				var data = $("#exd_list").getRowData(rid);
				if_sap_message_key = data["if_sap_message_key"];
				seq = data["seq"];
				kind = data["kind"];
				showContentDetail();
			},
			caption: "未处理接口数据一览",
			gridview: true, // Speed up
			pagerpos: 'right',
			pgbuttons: true,
			pginput: false,
			recordpos: 'left',
			viewsortcols : [true,'vertical',true],
			gridComplete: function(){}
		});
	}
};

var showContentDetail = function(){
	var content_dialog = $("#detail_dialog").hide();
	// 导入详细画面
	content_dialog.load("widgets/manage/interface_data_edit.jsp" , function(responseText, textStatus, XMLHttpRequest) {
		$("#recept_edit_directly_to_sorc").buttonset();
		$("#recept_edit_ocm_ship_date, #recept_edit_agree_time," +
		  "#approve_edit_item_receiver_date,#approve_edit_item_input_line_date," +
		  "#part_order_edit_parts_confrim_date").datepicker({
				showButtonPanel:true,
				dateFormat: "yymmdd",
				currentText: "今天"
		});
		
		if (kind == "recept") {
			$("#body-recept").show();
			$("#body-approve").hide();
			$("#body-part-order").hide();
		} else if (kind == "approve") {
			$("#body-approve").show();
			$("#body-recept").hide();
			$("#body-part-order").hide();
		}else if(kind=="part_order"){
			$("#body-approve").hide();
			$("#body-recept").hide();
			$("#body-part-order").show();
		}

		var data={
			"if_sap_message_key": if_sap_message_key,
			"seq" : seq
		};
		
		$.ajax({
			beforeSend : ajaxRequestType,
			async : false,
			url : servicePath + '?method=getDetial',
			cache : false,
			data : data,
			type : "post",
			dataType : "json",
			success : ajaxSuccessCheck,
			error : ajaxError,
			complete :getDetial_complete
		});
	});
};

var setValue = function(detailForm){
	if (!detailForm.content) return;
	
	var content=null;
	eval('content =' + detailForm.content);
	
	if(kind=="approve"){//投线
		$("#approve_edit_omr_notifi_no").text(content.OMRNotifiNo);
		$("#approve_edit_sorc_code").text(content.SORCCode);
		$("#approve_edit_item_input_line_date").val(content.itemInputLineDate);
		$("#approve_edit_item_input_line_time").val(content.itemInputLineTime);
	}else if(kind=="recept"){
		$("#recept_edit_omr_notifi_no").text(content.OMRNotifiNo);
		$("#recept_edit_sorc_code").text(content.SORCCode);
		$("#recept_edit_rc_code").val(content.RCCode);
		$("#recept_edit_item_code").val(content.itemCode);
		$("#recept_edit_series_no").val(content.serialNo);
		$("#recept_edit_repair_method").val(content.repairMethod);
		$("#recept_edit_ocm_rank").val(content.OCMRank);
		$("#recept_edit_ocm_ship_date").val(content.OCMShipDate);
		$("#recept_edit_osh_rank").val(content.OSHRank);
		$("#recept_edit_agree_time").val(content.agreeTime);
		$("#recept_edit_hospital").val(content.hospital);
		if (content.directlyToSORC == "X" || content.directlyToSORC == "1") {
			$("#direct_flg_yes").attr("checked","checked").trigger("change");
		} else {
			$("#direct_flg_no").attr("checked","checked").trigger("change");
		}
		$("#recept_edit_item_receiver_flag").val(content.itemReceiverFlag);
		$("#recept_edit_item_receiver_date").val(content.itemReceiverDate);
		$("#recept_edit_item_receiver_time").val(content.itemReceiverTime);
		$("#recept_edit_item_receiver_person").val(content.itemReceiverPerson);
	}else if(kind=="part_order"){
		$("#part_order_edit_omr_notifi_no").text(content.OMRNotifiNo);//SAP修理通知单No.
		$("#part_order_edit_sorc_code").text(content.SORCCode);//SORC代码
		$("#part_order_edit_parts_confrim_date").val(content.partsConfirmDate);//零件发到修理单日期
		$("#part_order_edit_parts_confrim_time").val(content.partsConfirmTime);//零件发到修理单时间
		
		var htmlContent="";
		
		$.each(content.SMOItem,function(index,SMOItem){//
			var icount=index+1;
			var STOItemContent="";
			if (SMOItem.STOItem) {
				$.each(SMOItem.STOItem,function(i,STOItem){
					
					if(STOItem.STONo!=null){
						STOItemContent +='<tr class="three" for="'+icount +'" index="'+i+'">'
											+'<td class="ui-state-default td-title">STO号</td>'
											+'<td class="td-content"><label name="STONo">' + STOItem.STONo + '</label></td>'
										+'</tr>';
					}
					
					if(STOItem.STOItemNo!=null) {
						STOItemContent +='<tr class="three" for="'+icount +'" index="'+i+'">'
											+'<td class="ui-state-default td-title">STO行项目号</td>'
											+'<td class="td-content"><label name="STOItemNo">' + STOItem.STOItemNo + '</label></td>'
										+'</tr>';
					}
					
					if(STOItem.orderQty!=null && STOItem.STOItemNo!=null){
						STOItemContent +='<tr class="three" for="'+icount +'" index="'+i+'">'
											+'<td class="ui-state-default td-title">订购数量</td>'
											+'<td class="td-content"><input type="text" class="ui-widget-content" name="orderQty" value="'+ STOItem.orderQty + '"></td>'
										+'</tr>';
					}
					
					if(STOItem.deliveryDate!=null) {
						STOItemContent +='<tr class="three" for="'+icount +'" index="'+i+'">'
											+'<td class="ui-state-default td-title">交期</td>'
											+'<td class="td-content"><input type="text" class="ui-widget-content date" name="deliveryDate" value="'+ STOItem.deliveryDate + '"></td>'
										+'</tr>';
					}
				
				});
			}
				
			htmlContent +='<tr class="two" index="'+ icount +'">'
							+'<td class="ui-state-default td-title">SMO号</td>'
							+'<td class="td-content"><label name="SMONo">' + SMOItem.SMONo + '</label></td>'
						+'</tr>'
						+'<tr class="two" index="'+ icount +'">'
							+'<td class="ui-state-default td-title">SMO行项目号</td>'
							+'<td class="td-content"><label name="SMOItemNo">' + SMOItem.SMOItemNo + '</label></td>'
						+'</tr>'
						+'<tr class="two" index="'+ icount +'">'
							+'<td class="ui-state-default td-title">零件物料号</td>'
							+'<td class="td-content"><input type="text" class="ui-widget-content" name="partsMaterialNo" value="'+ SMOItem.partsMaterialNo +'"></td>'
						+'</tr>'
						+'<tr class="two" index="'+ icount +'">'
							+'<td class="ui-state-default td-title">零件登记批次</td>'
							+'<td class="td-content"><input type="text" class="ui-widget-content" name="times" value="'+ SMOItem.times +'"></td>'
						+'</tr>'
					    +'<tr class="two" index="'+ icount +'">'
							+'<td class="ui-state-default td-title">追加理由</td>'
							+'<td class="td-content"><input type="text" class="ui-widget-content" name="appendReason" value="'+ SMOItem.appendReason +'"></td>'
						+'</tr>'
						+'<tr class="two" index="'+ icount +'">'
							+'<td class="ui-state-default td-title">需求数量</td>'
							+'<td class="td-content"><input type="text" class="ui-widget-content" name="requirementQty" value="'+ SMOItem.requirementQty +'"></td>'
						+'</tr>'
						+'<tr class="two" index="'+ icount +'">'
							+'<td class="ui-state-default td-title">单价</td>'
							+'<td class="td-content"><input type="text" class="ui-widget-content" name="unitPrice" value="'+ SMOItem.unitPrice +'"></td>'
						+'</tr>';
						
			if(SMOItem.currency!=null){
				htmlContent +='<tr class="two" index="'+ icount +'">'
								+'<td class="ui-state-default td-title">通货</td>'
								+'<td class="td-content"><input type="text" class="ui-widget-content" name="currency" value="'+ SMOItem.currency +'"></td>'
							+'</tr>';
			}else{
				htmlContent +='<tr class="two" index="'+ icount +'">'
								+'<td class="ui-state-default td-title">通货</td>'
								+'<td class="td-content"><input type="text" class="ui-widget-content" name="currency" value=""></td>'
							+'</tr>';
			}
			htmlContent += STOItemContent;
							
		});
		
		$("#body-part-order table tbody tr:gt(3)").remove();
		$("#body-part-order table tbody").append(htmlContent);
		$("input.date").datepicker({
			showButtonPanel:true,
			dateFormat: "yymmdd",
			currentText: "今天"
		});
	}
};

var getDetial_complete = function(xhrobj, textStatus){
	var resInfo = null;
	try {
		// 以Object形式读取JSON
		eval('resInfo =' + xhrobj.responseText);

		var detailForm = resInfo.detailForm;
		setValue(detailForm);
		
		var content_dialog = $("#detail_dialog").dialog({
	        position : 'center',
			title : "数据详细画面",
			width : 'auto',
			height : 600,
			resizable : false,
			modal : true,
			buttons : {
				"确定":function(){
					comfirm_f();
				},
				"删除":function(){
					delete_f();
				},
				"忽略":function(){
					ignore_f();
				},
				"关闭":function(){
					$(this).dialog("close");
				}
			}
		});
		content_dialog.show();
		
	} catch (e) {
		alert("name: " + e.name + " message: " + e.message + " lineNumber: " + e.lineNumber + " fileName: " + e.fileName);
	};
};

var comfirm_f = function(){
	curpagenum = $("#exd_list").jqGrid('getGridParam', 'page');   //当前页码
	var data={
		"if_sap_message_key":if_sap_message_key,
		"seq":seq,
		"kind":kind
	};
	if(kind=="approve"){
		data.edit_omr_notifi_no = $("#approve_edit_omr_notifi_no").text();
		data.edit_sorc_code = $("#approve_edit_sorc_code").text();
		data.itemInputLineDate = $("#approve_edit_item_input_line_date").val();
		data.itemInputLineTime = $("#approve_edit_item_input_line_time").val();
	}else if(kind=="recept"){
		data.edit_omr_notifi_no = $("#recept_edit_omr_notifi_no").text();
		data.edit_sorc_code = $("#recept_edit_sorc_code").text();
		data.edit_rc_code = $("#recept_edit_rc_code").val();
		data.edit_item_code = $("#recept_edit_item_code").val();
		data.edit_series_no = $("#recept_edit_series_no").val();
		data.edit_repair_method = $("#recept_edit_repair_method").val();
		data.edit_ocm_rank = $("#recept_edit_ocm_rank").val();
		data.edit_ocm_ship_date = $("#recept_edit_ocm_ship_date").val();
		data.edit_osh_rank = $("#recept_edit_osh_rank").val();
		data.edit_agree_time = $("#recept_edit_agree_time").val();
		data.edit_hospital = $("#recept_edit_hospital").val();
		data.edit_directly_to_sorc = $("#recept_edit_directly_to_sorc input:checked").val();
		data.edit_item_receiver_flag = $("#recept_edit_item_receiver_flag").val();
		data.edit_item_receiver_date = $("#recept_edit_item_receiver_date").val();
		data.edit_item_receiver_time = $("#recept_edit_item_receiver_time").val();
		data.edit_item_receiver_person = $("#recept_edit_item_receiver_person").val();
	}else if(kind="part_order"){
		data.OMRNotifiNo = $("#part_order_edit_omr_notifi_no").text();
		data.SORCCode = $("#part_order_edit_sorc_code").text();
		data.partsConfirmDate = $("#part_order_edit_parts_confrim_date").val();//零件发到修理单日期
		data.partsConfirmTime = $("#part_order_edit_parts_confrim_time").val();//零件发到修理单时间
		
		var count=-1;
		var tempIndex=0;
		$("#body-part-order table tr.two").each(function(i,ele){
			var $tr = $(ele);
			var index = $tr.attr("index");
			
			var $input = $tr.find("td:last-child").find(":first-child");
			var column=$input.attr("name");
			var value="";
			if($input.is("input")){
				value =  $input.val().trim();
			}else if($input.is("label")){
				value =  $input.text().trim();
			}
			
			if(tempIndex!=index){
				tempIndex=index;
				data["if_sap_message_content_two.id["+ (count+1) +"]"] = index;
				data["if_sap_message_content_two."+ column +"["+  (count+1) +"]"] = value;
				count++;
			}else{
				data["if_sap_message_content_two."+ column +"["+ count +"]"] = value;
			}
		});
		
		tempIndex=-1;
		$("#body-part-order table tr.three").each(function(i,ele){
			var $tr = $(ele);
			var index = $tr.attr("index");
			var id= $tr.attr("for");
			var $input = $tr.find("td:last-child").find(":first-child");
			var column=$input.attr("name");
			var value="";
			if($input.is("input")){
				value =  $input.val().trim();
			}else if($input.is("label")){
				value =  $input.text().trim();
			}
			
			if(tempIndex!=index){
				tempIndex=index;
				data["if_sap_message_content_three.id["+ index +"]"] = id;
				data["if_sap_message_content_three."+ column +"["+  index +"]"] = value;
			}else{
				data["if_sap_message_content_three."+ column +"["+ index +"]"] = value;
			}
		});
	}
	$.ajax({
		beforeSend : ajaxRequestType,
		async : false,
		url : servicePath + '?method=doSave',
		cache : false,
		data : data,
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : doSave_ajaxSuccess
	});
};

var doSave_ajaxSuccess = function(xhrobj, textStatus){
	try {
		// 以Object形式读取JSON
		eval('resInfo =' + xhrobj.responseText);

		if (resInfo.errors.length > 0) {
			// 共通出错信息框
			treatBackMessages(null, resInfo.errors);
		} else {
			$("#detail_dialog").dialog("close");
			doInit();
//			$("#exd_list").jqGrid('setGridParam', {page:curpagenum}).trigger("reloadGrid");
		}
	} catch (e) {
		alert("name: " + e.name + " message: " + e.message + " lineNumber: " + e.lineNumber + " fileName: " + e.fileName);
	};
};

var delete_f=function(){
	curpagenum = $("#exd_list").jqGrid('getGridParam', 'page');   //当前页码
	var data={
		"if_sap_message_key": if_sap_message_key,
		"seq" : seq
	};
	$("#confirmmessage").text("删除不能恢复。确认要删除记录吗？");
	$("#confirmmessage").dialog({
		resizable : false,
		modal : true,
		title : "删除确认",
		buttons : {
			"确认" : function() {
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
					complete :doDelete_ajaxSuccess
				});
			},
			"取消" : function() {
				$(this).dialog("close");
			}
		}
	});
};

var doDelete_ajaxSuccess = function(xhrobj, textStatus){
	try {
		// 以Object形式读取JSON
		eval('resInfo =' + xhrobj.responseText);

		if (resInfo.errors.length > 0) {
			// 共通出错信息框
			treatBackMessages(null, resInfo.errors);
		} else {
			$("#confirmmessage").dialog("close");
			$("#detail_dialog").dialog("close");
			doInit();
//			$("#exd_list").jqGrid('setGridParam', {page:curpagenum}).trigger("reloadGrid");
		}
	} catch (e) {
		alert("name: " + e.name + " message: " + e.message + " lineNumber: " + e.lineNumber + " fileName: " + e.fileName);
	};
};

var ignore_f=function(){
	curpagenum = $("#exd_list").jqGrid('getGridParam', 'page');   //当前页码
	var data={
		"if_sap_message_key": if_sap_message_key,
		"seq" : seq
	};
	$("#confirmmessage").text("确认要忽略记录吗？");
	$("#confirmmessage").dialog({
		resizable : false,
		modal : true,
		title : "忽略确认",
		buttons : {
			"确认" : function() {
				$.ajax({
					beforeSend : ajaxRequestType,
					async : true,
					url : servicePath + '?method=doIgnore',
					cache : false,
					data : data,
					type : "post",
					dataType : "json",
					success : ajaxSuccessCheck,
					error : ajaxError,
					complete :doDelete_ajaxSuccess
				});
			},
			"取消" : function() {
				$(this).dialog("close");
			}
		}
	});
};