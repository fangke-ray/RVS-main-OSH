/** 模块名 */
var modelname = "零件补充";
var servicePath ="partial_supply.do";

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

	$("#uploadform").validate({
		rules : {
			file : {
				required : true
			}
		}
	});

	$("#searchform").validate({
		rules : {
			supply_date : {
				required : true
			}
		}
	});

	/* 载入按钮事件 */
	$("#uploadbutton").click(function() {
		/* 前台验证文件必须被选择 */

		if ($("#uploadform").valid()) {
			upload_file();
		}
	});

	$("#supply_date").datepicker({
		showButtonPanel:true,
		dateFormat: "yy/mm/dd",
		currentText: "今天"
	});

	/*确定按钮事件*/
	$("#select_button").click(function(){
		if ($("#searchform").valid()) {
			var supply_date_val = $("#supply_date").val();
			$("#supply_date").data("post", supply_date_val);
			var postData = {supply_date : supply_date_val};
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
				complete : searchSupply_Complete
			});
		}
	});

	/*确定按钮事件*/
	$("#delete_button").click(function(){
		var rowids = $("#list").jqGrid("getGridParam", "selarrrow");
		
		var data = {supply_date: $("#supply_date").data("post")};
		for (var ii=0;ii < rowids.length;ii++){
			var rowData = $("#list").getRowData(rowids[ii]);
			data["delete.partial_id[" + ii + "]"] = rowData.partial_id;
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
			complete : searchSupply_Complete
		});

	});

	 /*frist jqgrid初始化显示*/
	supply_list([]);
});

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
var find= function(data){
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
		supply_list(resInfo.partialFormList);
	}
};

function searchSupply_Complete(xhrobj, textStatus) {
	var resInfo = null;
	// 以Object形式读取JSON responseText获取来自服务器响应的数据
	eval('resInfo =' + xhrobj.responseText);
	if (resInfo.errors.length > 0) {
		// 共通出错信息框
		treatBackMessages(null, resInfo.errors);
	} else {
		$("#submit_button").enable();

		/*提交button*/
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

		supply_list(resInfo.partialSupplyList);
	}
};

/*文件载入*/
var upload_file =function(){
	$.ajaxFileUpload({
		url : 'partialUpload.do?method=doUploadSupply', // 需要链接到服务器地址
		secureuri : false,
		fileElementId : 'supply_file', // 文件选择框的id属性
		dataType : 'json', // 服务器返回的格式
		success : function(responseText, textStatus) {
			var resInfo = null;
			try {
				// 以Object形式读取JSON
				eval('resInfo =' + responseText);
				if (resInfo.errors.length > 0) {
					// 共通出错信息框
					treatBackMessages("#confirm_message", resInfo.errors);
				} else {
					$("#confirm_message").dialog({
						resizable : false,
						modal : true,
						title : "文件上传完毕",
						buttons:{"确认" : function(){
							$("#confirm_message").dialog("close");
						}}
					});
				}
			} catch(e) {

			}
		}
	});
};

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
		 if(resInfo.partialOrderFormNoAssginList.length==0){
			$("#confirm_button").enable();
		 }else{
			$("#confirm_button").disable();
		 }
		 show_exd_list(resInfo.partialOrderFormList, false, true);
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
		$("#addtional_Manage").dialog("close")
	}
};

/** 根据条件使按钮有效/无效化 */
var enablebuttons = function() {

	var rowids = $("#list").jqGrid("getGridParam", "selarrrow");
	if (rowids.length > 0) {
		$("#delete_button").enable();
	} else {
		$("#delete_button").disable();
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
function supply_list(finished_list_data){
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
			colNames:['','','订购用途','零件号','零件说明','数量'],
			colModel:[
				{name:'partial_id',index:'partial_id', hidden:true},
				{name:'identification',index:'identification', hidden:true},
				{name:'used_by',index:'identification',width : 40,align : 'center',formatter:function(v,c,data){
					if (data.identification == 9) {
						return "(监测对象以外)";
					} else if (data.identification == 1) {
						return "SORCWH";
					} else if (data.identification == 2) {
						return "OGZ";
					} else if (data.identification == 4) {
						return "WH2P";
					}
					return "unknown";
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
			caption: "补充零件一览",
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

var showDetail=function(rowData){
		var path="widgets/partial/partial_supply_show.jsp";
		$("#edit_partial").load(path,function(responseText, textStatus, XMLHttpRequest){
			$("#partial_id").val(rowData.partial_id);
		 	$("#identification").val(rowData.identification);
		 	
		 	var used_by=rowData.identification
		 	var user_by_label;
		 	if(used_by==9){
		 		user_by_label="监测对象以外";
		 	}else if(used_by==1){
		 		user_by_label="OSH";
		 	}else if(used_by==2){
		 		user_by_label="OGZ";
		 	}else{
		 		user_by_label="unknown";
		 	}
		 	$("#used_by").text(user_by_label);
		 	$("#code").text(rowData.code);
		 	$("#partial_name").text(rowData.partial_name);
		 	$("#quantity").val(rowData.quantity);
		 	$("#quantity").keydown(function(evt){
				if(!((evt.keyCode >= 48 && evt.keyCode <= 57) || evt.keyCode==8 ||  evt.keyCode==46 || evt.keyCode==37 || evt.keyCode==39)){
					return false;
				}
			});
		 	
		 	$("#edit_partial").dialog({
				title:'补充零件详细信息',
				width:400,
				height:300,
				resizable:false,
				modal:true,
				buttons:{
					"确认":function(){
						 update_quantity($("#partial_id").val(),$("#supply_date").val(),$("#identification").val(),$("#quantity").val());
						 $("#edit_partial").dialog("close");
					},
					"取消":function(){
						 $("#edit_partial").dialog("close");
					}
				}
		    });
	    });
	    
	    /**
		 * 
		 * @param {} partial_id 零件ID
		 * @param {} supply_date 日期
		 * @param {} identification 识别
		 * @param {} quantity 数量
		 */
		var update_quantity=function(partial_id,supply_date,identification,quantity){
			var data={
				"partial_id":partial_id,
				"supply_date":supply_date,
				"identification":identification,
				"quantity":quantity
			}
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
		};
		
		var update_complete=function(xhrobj,textStatus){
			try {
				// 以Object形式读取JSON
				eval('resInfo =' + xhrobj.responseText);
				if (resInfo.errors.length > 0) {
					// 共通出错信息框
					treatBackMessages("", resInfo.errors);
				} else {
					var postData = {supply_date : $("#supply_date").val()};
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
						complete : searchSupply_Complete
					});
				}
			} catch (e) {
				alert("name: " + e.name + " message: " + e.message + " lineNumber: "+ e.lineNumber + " fileName: " + e.fileName);
			};
		};
};
