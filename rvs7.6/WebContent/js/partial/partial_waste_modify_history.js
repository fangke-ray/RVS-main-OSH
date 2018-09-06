/** 模块名 */
var modelname = "零件";
var servicePath = "partial_waste_modify_history.do";
$(function(){
    $("input.ui-button").button();  
    $("#edit_value_currency").select2Buttons();
    $("#search_operate_type").buttonset();

   /*为每一个匹配的元素的特定事件绑定一个事件处理函数*/
    $("#searcharea span.ui-icon").bind("click", function() {
		$(this).toggleClass('ui-icon-circle-triangle-n').toggleClass('ui-icon-circle-triangle-s');
		if ($(this).hasClass('ui-icon-circle-triangle-n')) {
			$(this).parent().parent().next().show("blind");
		} else {
			$(this).parent().parent().next().hide("blind");
		}
	}); 
    setReferChooser($("#hidden_model_name"),$("#model_name_referchooser"));
    setReferChooser($("#hidden_updated_by"),$("#opertor_name_referchooser"));
    
	$("#search_active_date_start,#search_active_date_end").datepicker({
		showButtonPanel : true,
		dateFormat : "yy/mm/dd",
		currentText : "今天"
	});
	$("#resetbutton").click(function(){
		$("#search_code").val("");
		$("#search_name").val("");
		$("#search_model_name").val("");
		$("#search_active_date_start").val("");
		$("#search_active_date_end").val("");
		$("#search_updated_by").val("");
		$("#search_operate_type").val("");
		$("#search_operate_type_no").attr("checked","checked").trigger("change");
	});
	findit();
	$("#searchbutton").click(function(){
		 findit();
	});	
});

/*检索按钮事件*/
var findit = function() {
	var data ={
			    "code":$("#search_code").val(),               
				"name":$("#search_name").val(),             
				"model_name":$("#search_model_name").val(),       
				"active_date_start":$("#search_active_date_start").val(),
				"active_date_end":$("#search_active_date_end").val(),
				"operator_id":$("#hidden_updated_by").val(),
				"operator_type":$("#search_operate_type input:checked").val()
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
		complete : show_search_Complete
	})
};
function show_search_Complete(xhrobj, textStatus) {
	var resInfo = null;
	// 以Object形式读取JSON responseText获取来自服务器响应的数据
	eval('resInfo =' + xhrobj.responseText);
	if (resInfo.errors.length > 0) {
		// 共通出错信息框
		treatBackMessages(null, resInfo.errors);
	} else {
		filed_list(resInfo.partialWasteModifyHistoryFormList);
	}
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
			colNames:['型号ID','原零件ID','新零件ID','型号','原零件编码','原零件名称','改订','新零件编码','新零件名称','操作者','起效日期','处理日期'],
			colModel:[ 
				       {name:'model_id',index:'model_id',hidden:true},	
				       {name:'old_partial_id',index:'old_partial_id',hidden:true},	
				       {name:'new_partial_id',index:'new_partial_id',hidden:true},	
					   {name:'model_name',index:'model_name',width:80,align:'left'},
			           {name:'old_code',index:'old_code',width:45,align:'left'},		   
					   {name:'old_name',index:'old_name',width : 130,align:'left'},
					   {name:'modification_waste',index:'modification_waste',width : 30,align:'center',formatter : function(value, options, rData) {
						   if(rData.old_partial_id==rData.new_partial_id){
							   return "废";
						   }else {
							   return "改";	
						   }												
						}},
				       {name:'new_code',index:'new_code',width:45,align:'left',formatter : function(value, options, rData) {
						   if(rData.old_partial_id==rData.new_partial_id){
							   return "";
						   }else {
							   return value;	
						   }												
						}},
				       {name:'new_name',index:'new_name',width:130,align:'left',formatter : function(value, options, rData) {
						   if(rData.old_partial_id==rData.new_partial_id){
							   return "";
						   }else {
							   return value;	
						   }												
						}},
				       {name:'operator',index:'operator',width:30,align:'left'},
				       {name:'active_date',index:'active_date',width:55,align:'center'},
				       {name:'updated_time',index:'updated_time',width:55,align:'center'}
					 ],
			rowNum: 20,
			toppager: false,
			pager: "#listpager",
			viewrecords: true,
			hidegrid : false,
			caption: "零件改废增历史管理一览",
			gridview: true,
			pagerpos: 'right',
			pgbuttons: true,
			pginput: false,
			recordpos: 'left',
			viewsortcols : [true,'vertical',true]			
		});
	}
};
