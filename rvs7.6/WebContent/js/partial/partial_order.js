/** 模块名 */
var modelname = "零件";
var servicePath ="partial_order.do";

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

	/*作为分室追加 button*/
	$("#compartment_additional_button").click(function(){
		divided_chooseRow(2);
	});
	/*作为报价追加 button*/
	$("#offer_additional_button").click(function(){
		divided_chooseRow(3);
	});
	/*作为分解追加 button*/
	$("#decomposition_additional_button").click(function(){
		divided_chooseRow(4);
	});
	/*作为NS追加 button*/
	$("#ns_additional_button").click(function(){
		divided_chooseRow(5);
	});
	var partial_grid_options = {
		data:[],
		height: 461,
		width: 992,
		rowheight: 23,
		datatype: "local",
		colNames:['','零件编号','零件名称','订购数量','未发放数量','未签收数量', '价格','工位','SMO项目号'],
		colModel:[ {name:'material_partial_detail_key',index:'material_partial_detail_key',width:25,hidden:true},
				 {name : 'code',index : 'code',width : 40,align : 'left'},
				 {name : 'partial_name',index : 'partial_name',width : 150,align : 'left'},
				 {name : 'quantity',index : 'quantity',width : 40,align : 'right'},
				 {name : 'waiting_quantity',index : 'waiting_quantity',width : 40,align : 'right'},
				 {name : 'waiting_receive_quantity',index : 'waiting_receive_quantity',width : 40,align : 'right'},
				 {name : 'price',index : 'price',width : 40,align : 'right',sorttype:'currency',formatter:'currency',formatoptions:{thousandsSeparator: ',',prefix:'$'}},
				 {name :'process_code',index:'process_code',width:40,align:'center' },
				 {name :'smo_item_no',index:'smo_item_no',hidden:true }
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
		deselectAfterSort : false,
		viewsortcols : [true,'vertical',true],
		gridComplete : function() {
		}
	}


	var showPartialDetailBom = function(id) {
		var row =  $("#bom_exd_list").jqGrid("getGridParam", "selrow");// 得到选中行的ID
		var rowData =  $("#bom_exd_list").getRowData(row);
		//if(!rowData.process_code){
			showMaterialOrderDetail(rowData.material_partial_detail_key,rowData);
		//}
	}

	var showPartialDetailOther = function(id) {
		var row =  $("#exd_list").jqGrid("getGridParam", "selrow");// 得到选中行的ID
		var rowData =  $("#exd_list").getRowData(row);
		//if(!rowData.process_code){
			showMaterialOrderDetail(rowData.material_partial_detail_key,rowData);
		//}
	}

	//partial_grid_options.multiselect = true;
	partial_grid_options.pager = "#bom_exd_listpager";
	partial_grid_options.ondblClickRow = showPartialDetailBom;
	$("#bom_exd_list").jqGrid(partial_grid_options);

	 // partial_grid_options.multiselect = true;
	partial_grid_options.pager = "#exd_listpager";
	partial_grid_options.ondblClickRow = showPartialDetailOther; 			;
	$("#exd_list").jqGrid(partial_grid_options);

	$("#bom_exd_list").parents(".partial_list").hide();

	find();

	/*载入按钮事件*/
	$("#uploadbutton").click(function(){
	/*前台验证文件必须被选择*/
	$("#uploadform").validate({
		rules:{
			 file:{
				 required:true
			 }
		}
	});
	if ($("#uploadform").valid()) {
		 upload_file();
	}
	});

	//提交button
	$("#submit_button").click(function(){

		$.ajax({
			beforeSend : ajaxRequestType,
			async : true,
			url : servicePath + '?method=dosubmit',
			cache : false,
			data : null,
			type : "post",
			dataType : "json",
			success : ajaxSuccessCheck,
			error : ajaxError,
			complete : doinsertMaterialPartial_Complete
		});
	});

	//隐藏不良追加radio
	$(".bad_addtional").hide();

	//$("#bom_spare_parts_radio").attr("checked",true);
//	$("#compartment_additional_button").disable();
//	$("#offer_additional_button").disable();
//	$("#decomposition_additional_button").disable();
//	$("#ns_additional_button").disable();
	
	/*未定位 radio 选中事件和点击事件绑定在一起触发*/
	$("#not_assigned_radio").click(function(){
		$("#compartment_additional_button").enable();
		$("#offer_additional_button").enable();
		$("#decomposition_additional_button").enable();
		$("#ns_additional_button").enable();
		distribution(0);

	});
	/*BOM零件 radio*/
	$("#bom_spare_parts_radio").click(function(){
		$("#compartment_additional_button").enable();
		$("#offer_additional_button").enable();
		$("#decomposition_additional_button").enable();
		$("#ns_additional_button").enable();
		distribution(1);
	});
	/*分室追加 radio*/
	$("#compartment_additional_radio").click(function(){
		$("#compartment_additional_button").disable();
		$("#offer_additional_button").enable();
		$("#decomposition_additional_button").enable();
		$("#ns_additional_button").enable();
		distribution(2);
	});
	/*报价追加 radio*/
	$("#offer_additional_radio").click(function(){
		$("#compartment_additional_button").enable();
		$("#offer_additional_button").disable();
		$("#decomposition_additional_button").enable();
		$("#ns_additional_button").enable();
		distribution(3);
	});
	/*分解追加 radio*/
	$("#decomposition_additional_radio").click(function(){
		$("#compartment_additional_button").enable();
		$("#offer_additional_button").enable();
		$("#decomposition_additional_button").disable();
		$("#ns_additional_button").enable();
		distribution(4);
	});
	/*NS追加 radio*/
	$("#ns_additional_radio").click(function(){
		$("#compartment_additional_button").enable();
		$("#offer_additional_button").enable();
		$("#decomposition_additional_button").enable();
		$("#ns_additional_button").disable();
		distribution(5);
	});
	/*不良追加 radio*/
	$("#bad_additional_radio").click(function(){
		distribution(6);
	});

	/*确定按钮事件*/
	$("#confirm_button").click(function(){
		var data=  {
				"material_id":$("#hidden_material_id").val(),
				"occur_times":$("#hidden_occur_times").val(),
				"fix_type":$("#hidden_fix_type").val()
				};
		$.ajax({
			beforeSend : ajaxRequestType,
			async : true,
			url : servicePath + '?method=confirmAll',
			cache : false,
			data : data,
			type : "post",
			dataType : "json",
			success : ajaxSuccessCheck,
			error : ajaxError,
			complete : searchMaterial_Complete
		});
	});

	/*确定按钮事件*/
	$("#cancel_button").click(function(){
		var data=  {
				"material_id":$("#hidden_material_id").val(),
				"occur_times":$("#hidden_occur_times").val()
				};
		$.ajax({
			beforeSend : ajaxRequestType,
			async : true,
			url : servicePath + '?method=docancel',
			cache : false,
			data : data,
			type : "post",
			dataType : "json",
			success : ajaxSuccessCheck,
			error : ajaxError,
			complete : searchMaterial_Complete
		});
	});

	/*frist jqgrid初始化显示*/
	filed_list([]);

	/*确认 返回button事件*/
	$("#come_back_button").click(function(){
		$("#body-top").show();
		$("#body-after").hide();
	});
});

//双击零件操作
var showMaterialOrderDetail  = function(material_partial_detail_key,rowData){
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
		complete :  function(xhrobj, textStatus) {
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
		var materialPartialDetail = resInfo.materialPartialDetailForm;
		var positionOptionsMap = resInfo.positionOptionsMap;

		var tbodyContent = "";

		var quantity = materialPartialDetail.quantity;
		var position_id = positionOptionsMap[materialPartialDetail.partial_id];
		var waiting_quantity = materialPartialDetail.waiting_quantity;
		var waiting_receive_quantity = materialPartialDetail.waiting_receive_quantity;
		
		if(quantity==1){
			tbodyContent += '<tr class="ui-widget-content jqgrow ui-row-ltr">'
				+ '<td style="text-align:center;border:1px solid #aaaaaa;""><input type="hidden" class="hidden_material_partial_detail_key" value='+materialPartialDetail.material_partial_detail_key+'> '+ materialPartialDetail.code +'</td>'
				+ '<td class="label_quantity" style="text-align:center;border-top:1px solid #aaaaaa;border-bottom:1px solid #aaaaaa;""><input class="input_quantity" readonly="readonly" style="width:40px;border:0px" type="text" value='+quantity+'><input type="hidden" class="hidden_waiting_quantity" style="width:40px;" type="text" value='+waiting_quantity+'><input type="hidden" class="hidden_waiting_receive_quantity" style="width:40px;" type="text" value='+waiting_receive_quantity+'></td>'
				+ '<td style="text-align:center;border:1px solid #aaaaaa;""><select class="select_position_id" class="ui-widget-content"> '+position_id+'</select></td>'
				+ '</tr>';
		}else{
			tbodyContent += '<tr class="ui-widget-content jqgrow ui-row-ltr">'
				+ '<td style="text-align:center;border:1px solid #aaaaaa;""><input type="hidden" class="hidden_material_partial_detail_key" value='+materialPartialDetail.material_partial_detail_key+'> '+ materialPartialDetail.code +'</td>'
				+ '<td class="label_quantity" style="text-align:center;border-top:1px solid #aaaaaa;border-bottom:1px solid #aaaaaa;""><input class="input_quantity" style="width:40px;" type="text" value='+quantity+'><input type="hidden" class="hidden_waiting_quantity" style="width:40px;" type="text" value='+waiting_quantity+'><input type="hidden" class="hidden_waiting_receive_quantity" style="width:40px;" type="text" value='+waiting_receive_quantity+'></td>'
				+ '<td style="text-align:center;border:1px solid #aaaaaa;""><select class="select_position_id" class="ui-widget-content"> '+position_id+'</select></td>'
				+ '</tr>';
		}
		
		$tbodyContent = $(tbodyContent);
		$tbodyContent.find(".text_quantity[max=1]").val("1").attr("readonly", true);
		$tbodyContent.find(".select_position_id").find("option:contains('"+ rowData.process_code +"')").attr("selected", true);

		// TODO 当追加到其他订购者时，判断如果工位不为空的时候,才弹出追加编辑dialog
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
						update_processCode(quantity,materialPartialDetail.position_id,materialPartialDetail.partial_id,materialPartialDetail.price,materialPartialDetail.smo_item_no);
						var current_page = $("input:radio[name='distributions']:checked").val();
						distribution(current_page);
					},
					"关闭" : function() {
						$("#addtional_Manage").dialog("close");
					}
				}
			});
		if(''==tbodyContent) {
			$("#confirmmessage").text("定位已经完成!");
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
			var current_page = $("input:radio[name='distributions']:checked").val();
			distribution(current_page);
		}
	}
};

//dialog窗口 追加到操作
var update_processCode= function(quantity,position_id,partial_id,price,smo_item_no){
	var data = {
			 "material_partial_detail_key":$(".hidden_material_partial_detail_key").val(),
			 "quantity":quantity,//修改之前的订购数量
			 "position_id":position_id,//修改之前的工位
			 "belongs":$("input:radio[name='distributions']:checked").val(),			
			 
			 "material_id":$("#hidden_material_id").val(),
			 "occur_times":$("#hidden_occur_times").val(),
			 "partial_id":partial_id,
			 "price":price,
			 "smo_item_no":smo_item_no,
			 
			 "new_quantity":$(".input_quantity").val(),//修改之后的订购数量
			 "new_position_id":$(".select_position_id").val(),
			 "waiting_quantity":$(".hidden_waiting_quantity").val(),
			 "waiting_receive_quantity":$(".hidden_waiting_receive_quantity").val()
			// "new_position_id":$(".select_position_id").val()//修改之后的定位工位
		};
	//判断当时二次订购时只有不良追加，并且不良追加进行零件定位时，设置belongs==6
	if($("#hidden_occur_times").val()>1){
		data.belongs=6;
	}else{
		data.belongs = $("input:radio[name='distributions']:checked").val();
	}
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
		var current_page = $("input:radio[name='distributions']:checked").val();
		distribution(current_page);
		$("#addtional_Manage").dialog("close");
	}
};

/*//如果未分配里面没有数据时，确定按钮则enable,有数据的话则disable
var enbutton=function(){
	 var ids= $("#exd_list").jqGrid("getGridParam", "getDataIDs");//得到一个特定的jqgrid里所有数据的ID(数组)
	 alert(ids.length)
	 if(ids.length==0){
		 $("#confirm_button").enable();
	 }else{
		 $("#confirm_button").disable();
	 }
}*/

////未分配完成的订购零件////
var  find= function(data){
	$.ajax({
		beforeSend : ajaxRequestType,
		async : true,
		url : servicePath + '?method=search',
		cache : false,
		data : null,
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : search_Complete
	});
}

function search_Complete(xhrobj, textStatus) {
	var resInfo = null;
	// 以Object形式读取JSON responseText获取来自服务器响应的数据
	eval('resInfo =' + xhrobj.responseText);
	if (resInfo.errors.length > 0) {
		// 共通出错信息框
		treatBackMessages(null, resInfo.errors);
	} else {
		filed_list(resInfo.partialFormList);
	}
};

//////由belongs进行分别显示//////
var distribution = function(data){
	var data ={
			"material_id":$("#hidden_material_id").val(),
			"occur_times":$("#hidden_occur_times").val(),
			"belongs":data,
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
		complete : detail_Complete
	});
}


function doinsertMaterialPartial_Complete(xhrobj, textStatus){
	var resInfo = null;
	// 以Object形式读取JSON responseText获取来自服务器响应的数据
	eval('resInfo =' + xhrobj.responseText);
	if (resInfo.errors.length > 0) {
		// 共通出错信息框
		treatBackMessages(null, resInfo.errors);
	} else {
		filed_list(resInfo.partialFormList);
	}
}
function searchMaterial_Complete(xhrobj, textStatus) {
	var resInfo = null;
	// 以Object形式读取JSON responseText获取来自服务器响应的数据
	eval('resInfo =' + xhrobj.responseText);
	if (resInfo.errors.length > 0) {
		// 共通出错信息框
		treatBackMessages(null, resInfo.errors);
	} else {

		$("#body-after").hide();
		$("#body-top").show();

		$("#submit_button").enable();

		filed_list(resInfo.returnFormList);
	}
};

//////由belongs进行分别显示//////
function detail_Complete(xhrobj, textStatus) {
	var resInfo = null;
	// 以Object形式读取JSON responseText获取来自服务器响应的数据
	eval('resInfo =' + xhrobj.responseText);
	if (resInfo.errors.length > 0) {
		// 共通出错信息框
		treatBackMessages(null, resInfo.errors);
	} else {
		/*//确定按钮的显示和不可选
		 if(resInfo.partialOrderFormNoAssginList.length==0){
			$("#confirm_button").enable();
		 }else{
			$("#confirm_button").disable();
		 }*/
		 var detailPartialOrder=resInfo.partialOrderFormList;
		 if($("#bom_spare_parts_radio").attr("checked")){
			show_exd_list(detailPartialOrder, true, true);
		 }else if($("#compartment_additional_radio").attr("checked")){
			show_exd_list(detailPartialOrder, true, true);
		 }else if($("#offer_additional_radio").attr("checked")){
			show_exd_list(detailPartialOrder, true, true);
		 }else if($("#decomposition_additional_radio").attr("checked")){
			show_exd_list(detailPartialOrder, true, true);
		 }else if($("#ns_additional_radio").attr("checked")){
			show_exd_list(detailPartialOrder, true, true);
		 }else if($("#bad_additional_radio").attr("checked")){
			show_exd_list(detailPartialOrder, true, true);
		 }
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

/*文件载入*/
var upload_file =function(){
	$.ajaxFileUpload({
		url : 'upload.do?method=doorderdetail', // 需要链接到服务器地址
		secureuri : false,
		fileElementId : 'order_detail_file', // 文件选择框的id属性
		dataType : 'json', // 服务器返回的格式
		success : function(responseText, textStatus) {
			var resInfo = null;
			try {
				// 以Object形式读取JSON
				eval('resInfo =' + responseText);
				if (resInfo.errors.length > 0) {
					// 共通出错信息框
					treatBackMessages("#confirmmessage", resInfo.errors);
				} else {
					filed_list(resInfo.materialDetailList);
				}
			} catch(e) {

			}
		}
	});
};
//选择多选提交到后台
function divided_chooseRow(belongs){
	var data = {};
	data["belongs"]=belongs;
	data["modelId"]=$("#hidden_model_id").val();
	var rowId =  $("#exd_list").jqGrid("getGridParam", "selrow");
	if (rowId == null) return ;
	var rowData = $("#exd_list").getRowData(rowId);
	data["keys.material_partial_detail_key[0]"] = rowData["material_partial_detail_key"];

//    if (row.length == 0) return;
//    for(var i=0;i<row.length;i++){
//    	var rowData = $("#exd_list").getRowData(row[i]);
//		data["keys.material_partial_detail_key[" + i + "]"] = rowData["material_partial_detail_key"];
//    }
	$.ajax({
		beforeSend : ajaxRequestType,
		async : true,
		url : servicePath + '?method=doAssagin',
		cache : false,
		data : data,
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : function(xhrobj, textStatus){
			show_divided_Complete(xhrobj, textStatus, belongs);
		}
	});
};

//追加 弹出dialog
var show_divided_Complete= function(xhrobj, textStatus, belongs) {
	var resInfo = null;
	// 以Object形式读取JSON responseText获取来自服务器响应的数据
	eval('resInfo =' + xhrobj.responseText);
	if (resInfo.errors.length > 0) {
		// 共通出错信息框
		treatBackMessages(null, resInfo.errors);
	} else {
		var materialPartialDetailList = resInfo.materialPartialDetailFormList;
		var positionOptionsMap = resInfo.positionOptionsMap;

		var tbodyContent = "";

		for(var i=0;i<materialPartialDetailList.length;i++){
			var detail  = materialPartialDetailList[i];
			tbodyContent += '<tr class="ui-widget-content jqgrow ui-row-ltr">'
				+ '<td style="text-align:center;border:1px solid #aaaaaa;""><input type="hidden" class="hidden_material_partial_detail_key" value='+detail.material_partial_detail_key+'> '+ detail.code +'</td>'
				+ '<td style="text-align:right;border-top:1px solid #aaaaaa;border-bottom:1px solid #aaaaaa;""><input class="text_quantity" style="text-align:right;width:30px" max="' + detail.quantity + '"></input>/'+detail.quantity+'</td>'
				+ '<td style="text-align:center;border:1px solid #aaaaaa;""><select class="select_position_id" class="ui-widget-content"> '+positionOptionsMap[detail.partial_id]+'</select></td>'
				+ '</tr>';
		}

		$tbodyContent = $(tbodyContent);
		$tbodyContent.find(".text_quantity[max=1]").val("1").attr("readonly", true);

		// TODO 当追加到其他订购者时，判断如果工位不为空的时候,才弹出追加编辑dialog
		if (resInfo.materialPartialDetailFormList.length>0) {
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
						update_processCode_quantity(belongs);
						/*var current_page = $("input:radio[name='distributions']:checked").val();
						current_page_refurbish(current_page);*/
						var current_page = $("input:radio[name='distributions']:checked").val();
						distribution(current_page);
					},
					"关闭" : function() {
						$("#addtional_Manage").dialog("close");
					}
				}
			});
		}
		/*if(''==tbodyContent) {
			$("#confirmmessage").text("改变分类已经完成!");
			$("#confirmmessage").dialog({
				width : 320,
				height : 'auto',
				resizable : false,
				show : "blind",
				modal : true,
				title : "改变分类",
				buttons : {
					"关闭" : function() {
						$("#confirmmessage").dialog("close");
					}
				}
			});
		/*	var current_page = $("input:radio[name='distributions']:checked").val();
			current_page_refurbish(current_page);} */
			var current_page = $("input:radio[name='distributions']:checked").val();
			distribution(current_page);

	}
}


//追加之后刷新操作
var current_page_refurbish= function(data){
	$("#body-after").show();
	$("#body-top").hide();
	var row =$("#list").jqGrid("getGridParam", "selrow");// 得到选中行的ID
	var rowData = $("#list").getRowData(row);

	var data = {
			"material_id": rowData.material_id,
			"belongs":data,
			"append":false
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
		complete : current_page_refurbish_Complete
	});
}

function current_page_refurbish_Complete(xhrobj, textStatus) {
	var resInfo = null;
	// 以Object形式读取JSON responseText获取来自服务器响应的数据
	eval('resInfo =' + xhrobj.responseText);
	if (resInfo.errors.length > 0) {
		// 共通出错信息框
		treatBackMessages(null, resInfo.errors);
	} else {
		//确定按钮的显示和不可选
		/* if(resInfo.partialOrderFormNoAssginList.length==0){
			$("#confirm_button").enable();
		 }else{
			$("#confirm_button").disable();
		 }*/
		 show_exd_list(resInfo.partialOrderFormList, true);
	}
};

//dialog窗口 追加到操作
var update_processCode_quantity = function(belongs){
	var data = {
			"belongs":belongs,
			 materialPartialDetail: {}
		};
	$(".text_quantity").each(function(idx, ele) {
		data["materialPartialDetail.quantity["+idx+"]"] = this.value;
	});

	$(".select_position_id").each(function(idx, ele) {
		data["materialPartialDetail.position_id["+idx+"]"] = this.value;
	});
	$(".hidden_material_partial_detail_key").each(function(idx, ele) {
		data["materialPartialDetail.material_partial_detail_key["+idx+"]"] = this.value;
	});

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
		complete :update_Complete
	});
}

function update_Complete(xhrobj, textStatus) {
	var resInfo = null;
	// 以Object形式读取JSON responseText获取来自服务器响应的数据
	eval('resInfo =' + xhrobj.responseText);
	if (resInfo.errors.length > 0) {
		// 共通出错信息框
		treatBackMessages(null, resInfo.errors);
	} else {
		$("#addtional_Manage").dialog("close");
	}
};


/*初始页面显示*/
var showList = function() {
	top.document.title = modelname + "一览";
	$("#condition_view,#searchform").show();
	$("#listarea").show();
	$("#editarea").hide();
}
/*jqgrid表格*/
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
			colNames:['维修对象ID','型号','机身号','修理单号','订购次数','等级', '定位完毕','状态',''],
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
						{name:'occur_times',index : 'occur_times',width : 40,align : 'center',hidden:true},
						{name:'level',index : 'level',width : 40,align : 'center',formatter:'select',editoptions:{value:$("#oMateriaLevel").val()}},
						{name:'positioning',index : 'positioning',width:40,align : 'center',formatter:'select',editoptions:{value:':;1:OK'}},
						{name:'bo_flg',index : 'bo_flg',width:40,align : 'center',formatter:'select',editoptions:{value:':;7:预提;8:待定位;9:待发放;0:无BO;1:有BO;2:BO解决'}},
						{name:'fix_type',index : 'fix_type',hidden:true}
					 ],
			rowNum: 20,
			toppager: false,
			pager: "#listpager",
			viewrecords: true,
			hidegrid : false,
			caption: "订购维修对象一览",
			gridview: true,
			pagerpos: 'right',
			ondblClickRow : function(rid, iRow, iCol, e) {
                var rowData =  $("#list").jqGrid('getRowData', rid);
                
                if($("#judge_status").val()=="partialManager"){ //  Remove at 15.11.10 : && rowData.bo_flg == 8
                     showOrderDetail();
                }
            },
			pginput: false,
			rownumbers : true,
			recordpos: 'left',
			deselectAfterSort : false,
			viewsortcols : [true,'vertical',true],
			gridComplete:function(){
                var IDS = $("#list").getDataIDs();
               
                var length = IDS.length;
                var pill = $("#list");
                for (var i = 0; i < length; i++) {
                    var rowData = pill.jqGrid('getRowData', IDS[i]);
                    var bo_flg = rowData.bo_flg;
                    if(bo_flg == 7){
                        pill.find("tr#" +IDS[i] +" td").css("background-color","#C0C0C0");
                    }
                }                                                                                    
            }
		});
	}
};

//双击操作
var showOrderDetail  = function(){
	var row =$("#list").jqGrid("getGridParam", "selrow");// 得到选中行的ID
	var rowData = $("#list").getRowData(row);

	var showBelongs = 1;
	//判断如果订购次数大于1时，零件分配类型只有不良
	if(rowData.occur_times>1){
		//不良追加radio被默认选中
		$("#bad_additional_radio").attr("checked",true);
		$(".bad_addtional,#bad_addtional_radio").show();		
		$(".normal_addtional ,#compartment_additional_button,#offer_additional_button,#decomposition_additional_button,#ns_additional_button").hide();
		showBelongs = 6;
	}else{
		//BOM零件radio默认被选中
		//$("#bom_spare_parts_radio").attr("checked",true);
		$(".bad_addtional,#bad_addtional_radio").hide();
		$(".normal_addtional").show();
		$(".normal_addtional ,#compartment_additional_button,#offer_additional_button,#decomposition_additional_button,#ns_additional_button").show();
	}

// 维修对象详细信息
	var data = {
			"material_id": rowData.material_id,
			"occur_times":rowData.occur_times,
			"belongs":showBelongs,
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
			show_detail_Complete(xhrObj, textStatus, rowData.material_id, rowData.occur_times,rowData.fix_type);
		}
	});
}

function show_detail_Complete(xhrobj, textStatus, material_id, occur_times,fix_type) {
	var resInfo = null;
	// 以Object形式读取JSON responseText获取来自服务器响应的数据
	eval('resInfo =' + xhrobj.responseText);
	if (resInfo.errors.length > 0) {
		// 共通出错信息框
		treatBackMessages(null, resInfo.errors);
	} else {
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
		$("#hidden_model_id").val(resInfo.materialDetail.model_id);
		$("#hidden_occur_times").val(occur_times);
		$("#hidden_fix_type").val(fix_type);
 
		if($("#hidden_occur_times").val()>1){
			$("#bad_additional_radio").attr("checked",true).trigger("change");
		}else{
			$("#bom_spare_parts_radio").attr("checked",true).trigger("change");
		}
		//show_exd_list(resInfo.partialOrderFormList, false, true);
		show_exd_list(resInfo.partialOrderFormList, true);

		//确定buttond的显示与否
	 /*   if(resInfo.partialOrderFormList.length==0){
				$("#confirm_button").enable();
			}else{
				$("#confirm_button").disable();
			}*/

		$("#body-after").show();
		$("#body-top").hide();
	}
};