/** 模块名 */
var modelname = "零件";
var servicePath ="partial_order_manage.do";

var sid ="";
$(function(){
	$("input.ui-button").button();

	$("#distributions >div:eq(0)").buttonset();

	/*为每一个匹配的元素的特定事件绑定一个事件处理函数*/
	$("#searcharea span.ui-icon").bind("click", function() {
		$(this).toggleClass('ui-icon-circle-triangle-n').toggleClass('ui-icon-circle-triangle-s');
		if ($(this).hasClass('ui-icon-circle-triangle-n')) {
			$(this).parent().parent().next().show("blind");
		} else {
			$(this).parent().parent().next().hide("blind");
		}
	});
	/*设置检索范围是在线维修*/
	$("#search_range").val("1").trigger("change");
	$("#search_range").select2Buttons();
	/* radio转换成按钮 */
	$("#bo_flg").buttonset();
	
	$("#search_order_date_start, #search_order_date_end").datepicker({
		showButtonPanel:true,
		dateFormat: "yy/mm/dd",
		currentText: "今天"
	});
	
	$("input[name=bo]").bind('click',function(){
		$("#cond_work_procedure_order_template").val($(this).val());
	});
	
	/*检索button*/
	$("#searchbutton").click(function(){
		$("#search_sorcno").data("post",$("#search_sorcno").val());
		$("#search_order_date_start").data("post",$("#search_order_date_start").val());
		$("#search_order_date_end").data("post",$("#search_order_date_end").val());
		$("#search_range").data("post",$("#search_range").val());
		findit();
	});
	
	/*清除button*/
	$("#rebutton").click(function(){
		$("#search_sorcno").val("").data("post","");
		$("#search_order_date_start").val("").data("post","");
		$("#search_order_date_end").val("").data("post","");
		$("#cond_work_procedure_order_template").val("");
		$("#cond_work_procedure_order_template_a").attr("checked","checked").trigger("change");
		$("#search_range").val("1").trigger("change");
	});
	//初始化
	findit();
	initPartialGrid();

	/*删除某一选择按钮事件*/
	$("#delete_button").click(showDelete);
	
	/*取消发放事件*/
	$("#cancel_distrubute_button").click(showDistrubute);

	/*取消订购按钮事件*/
	$("#cancel_order_button").click(showCancelOrder);
	
	/*无BO*/
	$("#no_bo_button").click(showNoBo);
	
	/*返回button事件*/
	$("#come_back_button").click(function(){
		$("#body-top").show();
		$("#body-after").hide();
	});
	$("#delete_button,#cancel_distrubute_button").disable();
});
function initPartialGrid(){
	$("#exd_list").jqGrid({
		data:[],
		height: 461,
		width: 992,
		rowheight: 23,
		datatype: "local",
		colNames:['','零件编号','零件名称','订购数量','待发放数量','未签收数量','价格','工位','追加区分'],
		colModel:[ {name:'material_partial_detail_key',index:'material_partial_detail_key',width:25,hidden:true},
				 {name : 'code',index : 'code',width : 40,align : 'left'},
				 {name : 'partial_name',index : 'partial_name',width : 150,align : 'left'},
				 {name : 'quantity',index : 'quantity',width : 40,align : 'right'},
				 {name : 'waiting_quantity',index : 'waiting_quantity',width : 40,align : 'right'},
				 {name : 'waiting_receive_quantity',index : 'waiting_receive_quantity',width : 40,align : 'right'},
				 {name : 'price',index : 'price',width : 40,align : 'right',sorttype:'currency',formatter:'currency',formatoptions:{thousandsSeparator: ',',prefix:'$'}},
				 {name :'process_code',index:'process_code',width:40,align:'center' },
				 {name :'belongs',index:'belongs',width:40,align:'center'}
				 ],
		rowNum: 20,
		toppager: false,
		pager: "#exd_listpager",
		viewrecords: true,
		hidegrid : false,
		multiselect:false,
		gridview: true,
		pagerpos: 'right',
		pginput: false,
		caption: "订购零件一览",
		recordpos: 'left',
		onSelectRow :enableButton,
		deselectAfterSort : false,
		viewsortcols : [true,'vertical',true],
		ondblClickRow: function(rid, iRow, iCol, e) {
			var row =  $("#exd_list").jqGrid("getGridParam", "selrow");// 得到选中行的ID
			var rowData =  $("#exd_list").getRowData(row);
			showMaterialOrderDetail(rowData);
		}
	});
} 
/*取消发放事件*/
var showDistrubute = function(){
	$("#confirmmessage").text("请确认是否取消发放？");
	$("#confirmmessage").dialog({
		resizable : false,
		modal : true,
		title : "取消发放确认",
		buttons : {
			"确认" : function() {
					//选择行，并获取行数
					var row = $("#exd_list").jqGrid("getGridParam", "selrow");
					var rowData = $("#exd_list").getRowData(row);
					var data ={
						"material_id":$("#hidden_material_id").val(),
						"occur_times":$("#hidden_occur_times").val(),
						"quantity":rowData.quantity,
						"material_partial_detail_key":rowData.material_partial_detail_key
					}
					$.ajax({
					beforeSend : ajaxRequestType,
					async : true,
					url : servicePath + '?method=doupdatedetail',
					cache : false,
					data : data,
					type : "post",
					dataType : "json",
					success : ajaxSuccessCheck,
					error : ajaxError,
					complete : distrubute_Complete 
				});
			},
			"取消" : function() {
				$(this).dialog("close");
			}
		}
	});

};
var distrubute_Complete=function(xhrobj,textStatus){
	var resInfo = null;
	try {
		// 以Object形式读取JSON
		eval('resInfo =' + xhrobj.responseText);
		if (resInfo.errors.length > 0) {
			// 共通出错信息框
			treatBackMessages("", resInfo.errors);
		} else {
			showOrderDetail();
			$("#confirmmessage").text("零件已经取消发放！");
			$("#confirmmessage").dialog({
				width : 320,
				height : 'auto',
				resizable : false,
				show : "blind",
				modal : true,
				title : "取消发放零件",
				buttons : {
					"关闭" : function() {
						$("#confirmmessage").dialog("close");
					}
				}
			});
		}
	} catch (e) {
		alert("name: " + e.name + " message: " + e.message + " lineNumber: "+ e.lineNumber + " fileName: " + e.fileName);
	};
};
///取消订购事件///
var showCancelOrder = function() {
	$("#confirmmessage").text("取消订购将会删除所有订购、发放、签收及使用信息！请确认是否真的要进行？");
	$("#confirmmessage").dialog({
		dialogClass : 'ui-warn-dialog',
		resizable : false,
		modal : true,
		title : "取消订购确认",
		buttons : {
			"确认" : function() {
				$("#confirmmessage").parent().removeClass('ui-warn-dialog');
				var data = {
					"material_id" : $("#hidden_material_id").val(),
					"occur_times" : $("#hidden_occur_times").val()
				}
				$.ajax({
					beforeSend : ajaxRequestType,
					async : true,
					url : servicePath + '?method=dodeletematerialpartial',
					cache : false,
					data : data,
					type : "post",
					dataType : "json",
					success : ajaxSuccessCheck,
					error : ajaxError,
					complete : cancelOrder_Complete
				});
			},
			"取消" : function() {
				$("#confirmmessage").parent().removeClass('ui-warn-dialog');
				$(this).dialog("close");
			}
		}
	});
}

var cancelOrder_Complete = function(xhrobj,textStatus){
	var resInfo = null;
	try {
		// 以Object形式读取JSON
		eval('resInfo =' + xhrobj.responseText);
		if (resInfo.errors.length > 0) {
			// 共通出错信息框
			treatBackMessages("", resInfo.errors);
		} else {
			$("#body-top").show();
			$("#body-after").hide();
			findit();			
			$("#confirmmessage").text("零件已经取消订购！");
			$("#confirmmessage").dialog({
				width : 320,
				height : 'auto',
				resizable : false,
				show : "blind",
				modal : true,
				title : "取消订购",
				buttons : {
					"关闭" : function() {
						$("#confirmmessage").dialog("close");
					}
				}
			});
		}
	} catch (e) {
		alert("name: " + e.name + " message: " + e.message + " lineNumber: "+ e.lineNumber + " fileName: " + e.fileName);
	};
};

/*删除事件*/
var showDelete = function(rid) {
	var row =$("#exd_list").jqGrid("getGridParam", "selrow");// 得到选中行的ID
	var rowData = $("#exd_list").getRowData(row);
	var data=  {
			"material_id":$("#hidden_material_id").val(),
			"occur_times":$("#hidden_occur_times").val(),
			"material_partial_detail_key":rowData.material_partial_detail_key
		}

	$("#confirmmessage").text("请确认是否能删除？");
	$("#confirmmessage").dialog({
		resizable : false,
		modal : true,
		title : "删除确认",
		buttons : {
			"确认" : function() {
					$.ajax({
					beforeSend : ajaxRequestType,
					async : true,
					url : servicePath + '?method=dodeletePartial',
					cache : false,
					data : data,
					type : "post",
					dataType : "json",
					success : ajaxSuccessCheck,
					error : ajaxError,
					complete :delete_Complete
				});
			},
			"取消" : function() {
				$(this).dialog("close");
			}
		}
	});
	
};
var delete_Complete=function(xhrobj,textStatus,material_partial_detail_key){
	var resInfo = null;
	try {
		// 以Object形式读取JSON
		eval('resInfo =' + xhrobj.responseText);
		if (resInfo.errors.length > 0) {
			// 共通出错信息框
			treatBackMessages("", resInfo.errors);
		} else {
			delete_button_after();
		}
	} catch (e) {
		alert("name: " + e.name + " message: " + e.message + " lineNumber: "+ e.lineNumber + " fileName: " + e.fileName);
	};
};
//删除之后的刷新
var delete_button_after  = function(){
	var data = {
			"material_id":$("#hidden_material_id").val(),
			"occur_times":$("#hidden_occur_times").val(),
			"append":false
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
		complete : function(xhrObj, textStatus) {
			delete_button_after_Complete(xhrObj, textStatus);
		}
	});
}

function delete_button_after_Complete(xhrobj, textStatus) {
	var resInfo = null;
	// 以Object形式读取JSON responseText获取来自服务器响应的数据
	eval('resInfo =' + xhrobj.responseText);
	if (resInfo.errors.length > 0) {
		// 共通出错信息框
		treatBackMessages(null, resInfo.errors);
	} else {
		show_exd_list(resInfo.partialOrderManageFormList, true);
		$("#body-after").show();
		$("#body-top").hide();
		
		//维修对象下的订购零件 --删除按钮事件  -当删除了全部的零件之后，将当前操作的订购维修对象取消(也就是删除当前维修对象)
		var list = resInfo.partialOrderManageFormList;
		if(list.length==0){
			showCancelOrder();
		}else{
			$("#confirmmessage").text("零件已经删除！");
			$("#confirmmessage").dialog({
				width : 320,
				height : 'auto',
				resizable : false,
				show : "blind",
				modal : true,
				title : "删除零件",
				buttons : {
					"关闭" : function() {
						$("#confirmmessage").dialog("close");
					}
				}
			});
		}		
	}
};

/*判断取消发放、删除按钮的enable、disable*/
var enableButton= function(){
	//选择行，并获取行数
	var row = $("#exd_list").jqGrid("getGridParam", "selrow");
	var rowData = $("#exd_list").getRowData(row);

	if(row>0){
		$("#delete_button").enable();
		if(rowData.quantity != rowData.waiting_quantity){
			$("#cancel_distrubute_button").enable();
		}else{
			$("#cancel_distrubute_button").disable();
		}
	}else{
		$("#delete_button,#cancel_distrubute_button").disable();
	}   
}
/*检索事件*/
var findit=function(){
	var data={
		"sorc_no":$("#search_sorcno").data("post"),
		"order_date_start":$("#search_order_date_start").data("post"),
		"order_date_end":$("#search_order_date_end").data("post"),
		"bo_flg":$("#cond_work_procedure_order_template").val(),
		"range": $("#search_range").val()
	}
	
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
			complete : search_Complete
	});
};

var search_Complete=function(xhrobj,textStatus){
	var resInfo = null;
	try {
		// 以Object形式读取JSON
		eval('resInfo =' + xhrobj.responseText);
		if (resInfo.errors.length > 0) {
			// 共通出错信息框
			treatBackMessages("", resInfo.errors);
		} else {
			//初始订购对象(查询未完成和已经完成的维修对象);
			filed_list(resInfo.partialOrderManageFormList);
		}
	} catch (e) {
		alert("name: " + e.name + " message: " + e.message + " lineNumber: "+ e.lineNumber + " fileName: " + e.fileName);
	};
};

//双击零件操作
var showMaterialOrderDetail  = function(rowData){
	var material_partial_detail_key = rowData.material_partial_detail_key; 
	var data = {
		"modelID":$("#hidden_model_id").val(),
		"material_partial_detail_key":material_partial_detail_key
	};
	$.ajax({
		beforeSend : ajaxRequestType,
		async : true,
		url : servicePath + '?method=materialPartialDetail',
		cache : false,
		data : data,
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : function(xhrobj, textStatus) {
			getPartialOrder_Complete(xhrobj, rowData);
		} 
	});
}
function getPartialOrder_Complete(xhrobj, rowData) {
	var resInfo = null;
	// 以Object形式读取JSON responseText获取来自服务器响应的数据
	eval('resInfo =' + xhrobj.responseText);
	if (resInfo.errors.length > 0) {
		// 共通出错信息框
		treatBackMessages(null, resInfo.errors);
	} else {
		var materialPartialDetail = resInfo.partialOrderManageForm;
		var positionOptionsMap = resInfo.positionOptionsMap;
		var belongsOptionsMap = resInfo.belongsOptionsMap;

		var tbodyContent = "";

		var quantity=materialPartialDetail.quantity;
		var position_id=positionOptionsMap[materialPartialDetail.partial_id];
		var belongs=belongsOptionsMap[materialPartialDetail.partial_id];
		var waiting_quantity=materialPartialDetail.waiting_quantity;
		var waiting_receive_quantity =materialPartialDetail.waiting_receive_quantity;
		//零件订购数量大于1个时，可修改成小于当前数量的数字
		if(quantity==1){
			tbodyContent += '<tr class="ui-widget-content jqgrow ui-row-ltr">'
				+ '<td style="text-align:center;border:1px solid #aaaaaa;""><input type="hidden" class="hidden_material_partial_detail_key" value='+materialPartialDetail.material_partial_detail_key+'> '+ materialPartialDetail.code +'</td>'
				+ '<td style="text-align:center;border-top:1px solid #aaaaaa;border-bottom:1px solid #aaaaaa;""><input class="input_quantity" readonly="readonly" style="width:40px;border:0px" type="text" value='+quantity+'><input type="hidden" class="input_waiting_quantity" style="width:40px;" type="text" value='+waiting_quantity+'><input type="hidden" class="input_waiting_receive_quantity" style="width:40px;" type="text" value='+waiting_receive_quantity+'></td>'
				+ '<td style="text-align:center;border:1px solid #aaaaaa;""><select class="select_position_id" class="ui-widget-content"> '+position_id+'</select></td>'
				+ '<td style="text-align:center;border:1px solid #aaaaaa;""><select class="select_belongs" class="ui-widget-content"> '+belongs+'</select></td>'
				+ '</tr>';
		}else{
			tbodyContent += '<tr class="ui-widget-content jqgrow ui-row-ltr">'
				+ '<td style="text-align:center;border:1px solid #aaaaaa;""><input type="hidden" class="hidden_material_partial_detail_key" value='+materialPartialDetail.material_partial_detail_key+'> '+ materialPartialDetail.code +'</td>'
				+ '<td style="text-align:center;border-top:1px solid #aaaaaa;border-bottom:1px solid #aaaaaa;""><input class="input_quantity" style="width:40px;" type="text" value='+quantity+'><input type="hidden" class="input_waiting_quantity" style="width:40px;" type="text" value='+waiting_quantity+'><input type="hidden" class="input_waiting_receive_quantity" style="width:40px;" type="text" value='+waiting_receive_quantity+'></td>'
				+ '<td style="text-align:center;border:1px solid #aaaaaa;""><select class="select_position_id" class="ui-widget-content"> '+position_id+'</select></td>'
				+ '<td style="text-align:center;border:1px solid #aaaaaa;""><select class="select_belongs" class="ui-widget-content"> '+belongs+'</select></td>'
				+ '</tr>';
		}
		$tbodyContent = $(tbodyContent);
		$tbodyContent.find(".text_quantity[max=1]").val("1").attr("readonly", true);
		$tbodyContent.find(".select_position_id").find("option:contains('"+ rowData.process_code +"')").attr("selected", true);
		$tbodyContent.find(".select_belongs").find("option:contains('"+ rowData.belongs +"')").attr("selected", true);

			//当追加到其他订购者时，判断如果工位不为空的时候,才弹出追加编辑dialog
			$("#addtional_Manage> table > tbody").html($tbodyContent);

			$("#addtional_Manage").dialog({
				width : 320,
				height : 'auto',
				resizable : false,
				show : "blind",
				modal : true,
				title : "定位",
				buttons : {
					"确认" : function() {
						$(this).dialog("close");
						update_processCode(quantity,materialPartialDetail.position_id,materialPartialDetail.belongs,materialPartialDetail.partial_id,materialPartialDetail.price);
					},
					"关闭" : function() {
						$("#addtional_Manage").dialog("close");
					}
				}
			});
		if(''==tbodyContent) {
			$("#confirmmessage").text("定位已经修改!");
			$("#confirmmessage").dialog({
				width : 320,
				height : 'auto',
				resizable : false,
				show : "blind",
				modal : true,
				title : "定位",
				buttons : {
					"关闭" : function() {
						$("#confirmmessage").dialog("close");
					}
				}
			});
			showOrderDetail();
		}
	}
};

//dialog窗口 追加到操作
var update_processCode= function(quantity,position_id,belongs,partial_id,price){
	var data = {
			 "quantity":quantity,//修改之前的订购数量
			 "position_id":position_id,//修改之前的定位工位
			 "belongs":belongs,//修改之前的订购者
			 
			 "waiting_quantity":$(".input_waiting_quantity").val(),//未发放数量
			 "waiting_receive_quantity":$(".input_waiting_receive_quantity").val(),//未签收数量
			 
			 "material_id":$("#hidden_material_id").val(),
			 "occur_times":$("#hidden_occur_times").val(),
			 "partial_id":partial_id,
			 "price":price,
			 
			 "new_quantity":$(".input_quantity").val(),//修改之后的订购数量
			 "new_position_id":$(".select_position_id").val(),//修改之后的定位工位
			 "material_partial_detail_key":$(".hidden_material_partial_detail_key").val(),
			 "new_belongs":$(".select_belongs").val()//修改之后的订购者
		};
	$.ajax({
		beforeSend : ajaxRequestType,
		async : true,
		url : servicePath + '?method=doupdatePosition',
		cache : false,
		data : data,
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete :updatePosition_Complete
	});
}

function updatePosition_Complete(xhrobj, textStatus) {
	var resInfo = null;
	// 以Object形式读取JSON responseText获取来自服务器响应的数据
	eval('resInfo =' + xhrobj.responseText);
	if (resInfo.errors.length > 0) {
		// 共通出错信息框
		treatBackMessages(null, resInfo.errors);
	} else {
		$("#confirmmessage").text("定位已经修改！");
		$("#confirmmessage").dialog({
			width : 320,
			height : 'auto',
			resizable : false,
			show : "blind",
			modal : true,
			title : "定位",
			buttons : {
				"关闭" : function() {
					$("#confirmmessage").dialog("close");
				}
			}
		});
		showOrderDetail();
		$("#addtional_Manage").dialog("close");
	}
};

/*jqgrid表格  未定位分配*/
function show_exd_list(finished_list_addtional, hasPosition,viewrecords){
	var $exd_list = $("#exd_list");

	$exd_list.jqGrid().clearGridData();
	$exd_list.jqGrid('setGridParam',{data:finished_list_addtional,viewrecords:viewrecords});
	if(hasPosition){
		$exd_list.jqGrid('showCol','process_code');
	}else{
		$exd_list.jqGrid('hideCol','process_code');
	}
	// $("#cb_exd_list").parent().parent().show();
	$exd_list.jqGrid('setGridWidth', '992');
	$exd_list.trigger("reloadGrid", [{current:false}]);

	$(".partial_list").hide();

	$exd_list.parents(".partial_list").show();
};

/*jqgrid表格,零件订购对象(未完成和已经完成的维修对象)*/
function filed_list(finished_list_data){
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
			colNames:['维修对象ID','型号','机身号','修理单号','订购次数','等级', '发放状态'],
			colModel:[
						{name:'material_id',index:'material_id', hidden:true},
						{name:'model_name',index : 'model_name',width : 40,align : 'left'},
						{name:'serial_no',index : 'serial_no',width : 40,align : 'left'},
						{name:'sorc_no',index : 'sorc_no',width : 40,align : 'left',formatter : function(value, options, rData) {
							if(rData.occur_times==1){
								return value;
							}else {
								return value+"/"+rData.occur_times;
							}
						}},
						{name:'occur_times',hidden:true,index : 'occur_times',width : 40,align : 'center',formatter : function(value, options, rData) {
							return value+"次";
						}},
						{name:'level',index : 'level',width : 40,align : 'center',formatter:function(value,options,rData){
							if(value=='1'){
								return 'S1';
							}else if(value=='2'){
								return 'S2';
							}else if(value=='3'){
								return 'S3';
							}else if(value && value.substring(0,1) == '9'){
								return 'D';
							}else {
								return '';
							}
						}},
						{name:'bo_flg',index : 'bo_flg',hidden:false,width:40,align : 'center',formatter:'select',editoptions:{value:'0:无BO;1:有BO;2:BO解决;9:未签收'}}
					 ],
			rowNum: 20,
			toppager: false,
			pager: "#listpager",
			viewrecords: true,
			hidegrid : false,
			caption: "订购维修对象一览",
			gridview: true,
			pagerpos: 'right',
			ondblClickRow : function() {
				showOrderDetail();
			},
			pginput: false,
			rownumbers : true,
			recordpos: 'left',
			deselectAfterSort : false,
			viewsortcols : [true,'vertical',true],
			gridComplete:function(){}
		});
	}
};

//双击操作
var showOrderDetail  = function(){
	//删除、取消发放按钮在双击之后初始disable();
	$("#delete_button,#cancel_distrubute_button").disable();

	var row =$("#list").jqGrid("getGridParam", "selrow");// 得到选中行的ID
	var rowData = $("#list").getRowData(row);
	
	//截取occur_times中数字(1/2/*次)
	var paddleft = rowData.occur_times.replace(/[^0-9]/ig, "");
	var num=parseInt(paddleft);
	var bo_flg = rowData.bo_flg;//发放状态
	if(bo_flg==2){//BO解决
		$("#no_bo_button").show();
	}else{
		$("#no_bo_button").hide();
	}

	// 维修对象详细信息
	var data = {
			"material_id": rowData.material_id,
			"occur_times":num,
			"append":true
			};
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
		complete : function(xhrObj, textStatus) {
			show_detail_Complete(xhrObj, textStatus, rowData, num);
		}
	});
}

function show_detail_Complete(xhrobj, textStatus, rowData, occur_times) {
//	var material_id = rowData.material_id;
	var resInfo = null;
	// 以Object形式读取JSON responseText获取来自服务器响应的数据
	eval('resInfo =' + xhrobj.responseText);
	if (resInfo.errors.length > 0) {
		// 共通出错信息框
		treatBackMessages(null, resInfo.errors);
	} else {
		
		$("#delete_button").enable();
		
		$("#qa_reception_time").text(resInfo.materialDetail.reception_time);
		$("#customer").text(resInfo.materialDetail.ocm);
		$("#agree_date").text(resInfo.materialDetail.agreed_date);
		$("#sorc_no").text(resInfo.materialDetail.sorc_no);
		$("#model_name").text(resInfo.materialDetail.model_name);
		$("#serial_no").text(resInfo.materialDetail.serial_no);
		$("#level").text(resInfo.materialDetail.level);
		$("#service_free_flg").text(resInfo.materialDetail.service_free_flg);
		$("#arrival_plan_date").text(resInfo.materialDetail.arrival_plan_date);

		//页面隐藏的material_id
		$("#hidden_material_id").val(resInfo.materialDetail.material_id);
		$("#hidden_model_id").val(resInfo.materialDetail.model_id)
		$("#hidden_occur_times").val(occur_times);
		
		if($("#hidden_occur_times").val()>1){
			$("#bad_additional_radio").attr("checked","checked").trigger("change");
		}else{
			$("#bom_spare_parts_radio").attr("checked","checked").trigger("change");
		}
		show_exd_list(resInfo.partialOrderManageFormList, true);

		$("#body-after").show();
		$("#body-top").hide();
	}
};

var showNoBo = function(){
	var data=  {
		"material_id":$("#hidden_material_id").val(),
		"occur_times":$("#hidden_occur_times").val()
	};

	$("#confirmmessage").text("请确认是否变成无BO？");
	$("#confirmmessage").dialog({
		resizable : false,
		modal : true,
		title : "无BO确认",
		buttons : {
			"确认" : function() {
					$(this).dialog("close");
					$.ajax({
					beforeSend : ajaxRequestType,
					async : true,
					url : servicePath + '?method=doNoBo',
					cache : false,
					data : data,
					type : "post",
					dataType : "json",
					success : ajaxSuccessCheck,
					error : ajaxError,
					complete :nobo_Complete
				});
			},
			"取消" : function() {
				$(this).dialog("close");
			}
		}
	});
};

var nobo_Complete=function(xhrobj,textStatus){
	var resInfo = null;
	try {
		// 以Object形式读取JSON
		eval('resInfo =' + xhrobj.responseText);
		if (resInfo.errors.length > 0) {
			// 共通出错信息框
			treatBackMessages("", resInfo.errors);
		} else {
			findit();
			$("#body-after").hide();
			$("#body-top").show();
		}
	} catch (e) {
		alert("name: " + e.name + " message: " + e.message + " lineNumber: "+ e.lineNumber + " fileName: " + e.fileName);
	};
};
