var servicePath ="partial_position.do";
/*型号*/
var partialCode ;

$(function(){
	$("input.ui-button").button();
    /*为每一个匹配的元素的特定事件绑定一个事件处理函数*/
    $("#searcharea span.ui-icon").bind("click", function() {
		$(this).toggleClass('ui-icon-circle-triangle-n').toggleClass('ui-icon-circle-triangle-s');
		if ($(this).hasClass('ui-icon-circle-triangle-n')) {
			$(this).parent().parent().next().show("blind");
		} else {
			$(this).parent().parent().next().hide("blind");
		}
	});   
    setReferChooser($("#search_model_id"), $("#model_refer"));//维修对象型号 下拉作用选择
    $("#search_rank").select2Buttons();
   
    $("#cancelbutton, #editarea span.ui-icon").click(function() {
		showList();
	});
 
	/*废改订button*/
	$("#waste_revision_button").click(function(){
	    waste_revision_edit();
	});	
	filed_list([]);
    /*清除button*/
    $("#resetbutton").click(function(){
		$("#search_model_id").prev().val("");
		$("#search_rank").val("").trigger("change");
		$("#search_code").val("");
	});
    
    
    /*上传button*/
    $("#uploadbutton").click(function(){
		import_upload_file();
	});
    /*参照button*/
	$("#referencebutton").addClass("ui-button-primary");
	$("#referencebutton").click(function(){
		
		 $("#label_model_name").text("");
		 $("#label_rank").text("");
		
		$("#search_model_id").data("post",$("#search_model_id").val());
		$("#txt_model_name").data("post",$("#txt_model_name").val());
		$("#search_rank").data("post",$("#search_rank").val());
		$("#search_code").data("post",$("#search_code").val());		
		
	    $("#search_code").data("post", $("#search_code").val());
	    $("#search_name").data("post", $("#search_name").val());
			$("#searchform").validate({
				rules:{		
				   model_name:{
					   required:true	    	  
				   }	       
				   }
			});
	       if ($("#searchform").valid()) {	    	   
	    	   findit();
			  if($("#search_rank").val()==''){
					var tname=['userate','bom']
					$("#list").jqGrid('hideCol',tname);
				}else{
					var tname=['userate','bom']
					$("#list").jqGrid('showCol',tname);
				}	
			$("#list").jqGrid('setGridWidth', '992');
		        
	    }
	});
	
	
	 $("#waste_revision_button").disable();
})
/*判断废改订按钮enable、disable*/
var enableButton= function(){
	
	//选择行，并获取行数
	var row = $("#list").jqGrid("getGridParam", "selrow");
	var rowData = $("#list").getRowData(row);
	if(row>0){
		//if(rowData.history_limit_date==" "){
			$("#waste_revision_button").enable();
		//}else{
			//$("#waste_revision_button").disable();
		//}		
	}else{
		$("#waste_revision_button").disable();
	} 
}
/*废改订*/
var waste_revision_edit = function(){
	var row = $("#list").jqGrid("getGridParam", "selrow");// 得到选中行的ID	
	var rowData = $("#list").getRowData(row);
	var data = {
			"model_id": rowData.model_id,
			"partial_id": rowData.partial_id,
			"position_id": rowData.position_id
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
		complete :function(xhrobj, textStatus){
			show_adit_Complete(xhrobj, textStatus, rowData.history_limit_date);
		} 
	});
}
/*废改订加载页面事件*/
var show_adit_Complete = function(xhrobj,textStatus,history_limit_date){
	    $("#confirmmessage").load("widgets/admin/partial_position_edit.jsp",function(responseText, textStatus, XMLHttpRequest) {		  
		$("#operation_id").buttonset();
	    $("input.ui-button").button();
	    var resInfo = null;
		try {
			// 以Object形式读取JSON
			eval('resInfo =' + xhrobj.responseText);
			if (resInfo.errors.length > 0) {
				// 共通出错信息框
				treatBackMessages(null, resInfo.errors);
			} else {
				$("#select_model_name").hide();
		        $("#edit_label_model_name").text(resInfo.returnForm.model_name);
				$("#edit_label_code").text(resInfo.returnForm.code);
				$("#edit_label_name").text(resInfo.returnForm.name);		
				$("#edit_model_id").val(resInfo.returnForm.model_id);
				$("#hidden_partial_id").val(resInfo.returnForm.partial_id);
				$("#edit_position_id").val(resInfo.returnForm.position_id);
				
				/////-----当该零件已经做过一次废止操作之后，不可进行第二次的废止操作------/////////
				if(history_limit_date!=" "){
					$("#edit_choose_discontinue").disable();
					$("#operation_id >label:first").hide();
					$(".discontinue_edit").hide();
					$("#edit_decided").attr("checked",true).trigger("change");
					$(".decided_edit").toggle();
				}else{
					//操作默认被选中废止
					$("#edit_choose_discontinue").attr("checked",function(){		
						$(".decided_edit").hide();
					    $(".discontinue_edit").toggle();
					});
			        //废止radio button
					$("#edit_choose_discontinue").click(function(){			
						$(".decided_edit").hide();
						$(".discontinue_edit").toggle();
						
					})
				}		
		//改定radiobutton
		$("#edit_decided").click(function(){
		    $(".discontinue_edit").hide();
		    $(".decided_edit").toggle();
		});		
		// autoComplete(零件code) 
		$.ajax({
			beforeSend : ajaxRequestType,
			async : true,
			url : 'partial.do?method=getAutocomplete',
			cache : false,
			data : null,
			type : "post",
			dataType : "json",
			success : ajaxSuccessCheck,
			error : ajaxError,
			complete : function(xhrobj) {
				var resInfo = null;
				try {
					// 以Object形式读取JSON
					eval('resInfo =' + xhrobj.responseText);
				
					partialCode = resInfo.sPartialCode;
					$("#search_partial_id").autocomplete({
						source : partialCode,
						minLength :3,
						delay : 100,
						focus: function( event, ui ) {
							 $(this).val( ui.item.label );
							 return false;
						},
						select: function( event, ui ) {
							$("#edit_partial_id").val(ui.item.value);
							$(this).val( ui.item.label );
							 return false;
						}
					});				
				} catch (e) {
				}
			}
		});		
		
		$("#edit_effective_time,#edit_change_time").datepicker({
			showButtonPanel : true,
			dateFormat : "yy/mm/dd",
			currentText : "今天"
	    });
		
		   $("#abandon_modify").validate({
				rules:{		
					history_limit_date:{
					   required:true
				   },
				   code:{
					   required:true
				   }
				}
		     });	  
		   $("#confirmmessage").dialog({
			    resizable : false,
				modal : true,
				height:330,
				title : "改废增",
				buttons : {
					"确认" : function() {
						if($("#edit_choose_discontinue").attr("checked")){
							if( $("#abandon_modify").valid()) {
								$("#confirm_dialog").text("废止已经完成！");
								$("#confirm_dialog").dialog({
									width : 320,
									height : 'auto',
									resizable : false,
									show : "blind",
									modal : true,
									title : "改废增",
									buttons : {
										"关闭" : function() {
											$(this).dialog("close");
										}
									}
								});
								$(this).dialog("close");
								update_effective_time();
							}
						}
						if($("#edit_decided").attr("checked")){
							if( $("#abandon_modify").valid()) {
								$("#confirm_dialog").text("改定已经完成！");
								$("#confirm_dialog").dialog({
									width : 320,
									height : 'auto',
									resizable : false,
									show : "blind",
									modal : true,
									title : "改废增",
									buttons : {
										"关闭" : function() {
											$(this).dialog("close");
										}
									}
								});
								$(this).dialog("close");
								update_effective_time_code();
							}
						}
					},
					"取消" : function() {
						$(this).dialog("close");
					}
				}
			});
		}
	}catch(e){}   
   });
}
/*更新有效日期*/
var update_effective_time = function(){
	var data ={
			"history_limit_date":$("#edit_effective_time").val(),
			"code":$("#search_partial_id").val(),
			"model_id":$("#edit_model_id").val(),
			"partial_id":$("#hidden_partial_id").val(),
			"position_id":$("#edit_position_id").val()
	}
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
			complete : update_handleComplete
	});
}
/*更新有效期和零件*/
var update_effective_time_code = function(){
	var data ={
			"history_limit_date":$("#edit_change_time").val(),
			"model_id":$("#edit_model_id").val(),
			"code":$("#search_partial_id").val(),
			"partial_id":$("#edit_partial_id").val(),
			"position_id":$("#edit_position_id").val(),
			"new_partial_id":$("#edit_partial_id").val(),
			"old_partial_id":$("#hidden_partial_id").val()
	}
	 $.ajax({
			beforeSend : ajaxRequestType,
			async : true,
			url : servicePath + '?method=doupdatecode',
			cache : false,
			data : data,
			type : "post",
			dataType : "json",
			success : ajaxSuccessCheck,
			error : ajaxError,
			complete : update_handleComplete
	});
}

function update_handleComplete(xhrobj, textStatus) {

	var resInfo = null;

	// 以Object形式读取JSON responseText获取来自服务器响应的数据
	eval('resInfo =' + xhrobj.responseText);

	if (resInfo.errors.length > 0) {
		// 共通出错信息框
		treatBackMessages(null, resInfo.errors);
	} else {
	    findit();
	}
};


var keepSearchData;
var findit = function(data) {
	if(!data){
		 keepSearchData = {
					"model_id":$("#search_model_id").data("post"),
					"rank":$("#search_rank").data("post"),
					"code":$("#search_code").data("post"),
					"level":$("#search_rank").data("post")
			};
	 }else{
		  keepSearchData = data;
	 }	 
	 $.ajax({
			beforeSend : ajaxRequestType,
			async : true,
			url : servicePath + '?method=search',
			cache : false,
			data : keepSearchData,
			type : "post",
			dataType : "json",
			success : ajaxSuccessCheck,
			error : ajaxError,
			complete : search_handleComplete
	});
};

function search_handleComplete(xhrobj, textStatus) {

	var resInfo = null;
	try {
		// 以Object形式读取JSON
		eval('resInfo =' + xhrobj.responseText);
		if (resInfo.errors.length > 0) {
			// 共通出错信息框
			treatBackMessages("#searcharea", resInfo.errors);
		} else {
		filed_list(resInfo.partialPosition);
		if(resInfo.partialPosition.length>0){
			 $("#label_model_name").text($("#txt_model_name").data("post"));
			 if($("#search_rank").val()==''){
				  $("#label_rank").text("");
				 }else{
				   $("#label_rank").text($("#search_rank").find("option:selected").text());
				 } 
		}
	  }
	}catch(e){
		
	}
};

var import_upload_file = function(date) {
	$("#confirmmessage").hide();
	$("#confirmmessage").html("<input name='file' id='make_upload_file' type='file'/>");	
	
	$("#confirmmessage").dialog({
		title : "选择上传文件",
		width : 280,
		show: "blind",
		height : 180,
		resizable:false,
		modal : true,
		minHeight : 200,
		close : function(){
			$("#confirmmessage").html("");
		},
		buttons : {
			"上传":function(value){     
					uploadfile();				
			}, "关闭" : function(){ 
				$(this).dialog("close");		
			}
		}
		
	});
	$("#confirmmessage").show();	
};

var uploadfile = function() {
	// 覆盖层
panelOverlay++;
makeWindowOverlay();
// ajax enctype="multipart/form-data"
$.ajaxFileUpload({
	url : 'upload.do?method=docodeposition', // 需要链接到服务器地址
	secureuri : false,
	fileElementId : 'make_upload_file', // 文件选择框的id属性
	dataType : 'json', // 服务器返回的格式
	success : function(responseText, textStatus) {
		panelOverlay--;
		killWindowOverlay();

		var resInfo = null;

		try {
			// 以Object形式读取JSON
			eval('resInfo =' + responseText);
		
			if (resInfo.errors.length > 0) {
				// 共通出错信息框
				treatBackMessages("#confirmmessage", resInfo.errors);
			} else {
				$("#confirmmessage").dialog('close');
				$("#confirmmessage").text("导入零件定位信息完成。");
				$("#confirmmessage").dialog({
					resizable : false,
					modal : true,
					title : "导入确认",
					buttons : {
						"确认" : function() {
							$(this).dialog("close");
						}
					}
				});
			}
		} catch(e) {
			
		}
	}
});
};

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
			colNames:['','型号ID','零件ID','工位ID','零件编码','名称','工位','有效期','active_date','使用率','BOM', '最后更新人','最后更新时间'],
			colModel:[  
				       {name:'myac', width:35, fixed:true, sortable:false, resize:false, formatter:'actions', formatoptions:{keys:true,delbutton:false }, hidden:true},
			           {name:'model_id',index:'model_id',hidden:true},
			           {name:'partial_id',index:'partial_id',hidden:true},
			           {name:'position_id',index:'position_id',hidden:true},
					   {name:'code',index:'code',width:40,align:'left'},
					   {name:'name',index:'name',width: 150,align:'left'},
					   {name:'process_code',index:'process_code',width:20,align:'center',formatter : function(value, options, rData){
							if(value==null || value=='0'){
								return '' ;
							}else{
								return value;
							}							
						}},
					   {name:'history_limit_date',index:'history_limit_date',width:60,align:'center',formatter : function(value, options, rData){
							if(value=='9999/12/31'){
								if(rData.active_date){
									return rData.active_date+" ～"
								}
								    return ' ' ;
							}else{
								if(rData.active_date){
									return rData.active_date+" ～ "+value
								}else{
									return "～ "+value;
								}							   
							}
						}},
					   {name:'active_date',index:'active_date',width:40,align:'center',hidden:true,formatter : function(value, options, rData){
							if(value){
								return value+" ～" ;
							}else{
								return '';
							}
						}},
					   {name:'userate',index:'userate',hidden:true,width:40,align:'right',formatter:'integer', sorttype:'integer', formatoptions:{suffix:'%',defaultValue: ' - '}},
				       {name:'bom',index:'bom',width:25,align:'center',hidden:true,formatter : function(value, options, rData){
							if(value=='1'){
								return 'BOM' ;
							}else{
								return '非BOM';
							}							
						}},
				       {name:'updated_by',index:'updated_by',width:30,align:'left'},
				       {name:'updated_time',index:'updated_time',width:60,align:'center'}
					 ],
			rowNum : 35,
			toppager : false,
			pager : "#listpager",
			viewrecords : true,
			gridview : true,
			pagerpos : 'right',
			pgbuttons : true, 
			pginput : false,					
			recordpos : 'left',
			hidegrid : false,
			deselectAfterSort : false,			
			//caption: "零件定位数据管理一览",
			ondblClickRow : showEdit,
			onSelectRow : enableButton,
			viewsortcols : [true,'vertical',true]				
		});
			
	}
};
var showEdit = function(){
	var row = $("#list").jqGrid("getGridParam", "selrow");// 得到选中行的ID	
	var rowData = $("#list").getRowData(row);
	var data = {
			"model_id": rowData.model_id,
			"partial_id": rowData.partial_id,
			"position_id": rowData.position_id
			};
	// Ajax提交
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
		complete : dbclick_show_adit_Complete
	});
}
var dbclick_show_adit_Complete = function(xhrobj, textStatus){		
	 var resInfo = null;
		try {
			// 以Object形式读取JSON
			eval('resInfo =' + xhrobj.responseText);
			if (resInfo.errors.length > 0) {
				// 共通出错信息框
				treatBackMessages(null, resInfo.errors);
			} else {			
					$("#label_modelname").text(resInfo.returnForm.model_name);
					$("#label_level").text($("#label_rank").text());
					$("#label_parent_partial").text(resInfo.returnForm.parent_partial_id);					
					$("#label_code").text(resInfo.returnForm.code);
					$("#label_name").text(resInfo.returnForm.name);
					$("#label_parent_partial").text(resInfo.returnForm.parent_partial_code);
					$("#label_position").text(resInfo.returnForm.process_code);					
					
					show_edit_list(resInfo.returnCodeActiveDate);
					$("#show_detail_message").dialog({
								width : 550,
								height: 'auto',
								show: "blind",
								modal : true,
								title : "零件信息",
								resizable:false,
								buttons : {
									"取消" : function() { $("#show_detail_message").dialog("close")}
								}
					 });
		     }
	}catch(e){
		
	}
};

function show_edit_list(finished){
	if ($("#gbox_ext_list").length > 0) {
		$("#ext_list").jqGrid().clearGridData();
		$("#ext_list").jqGrid('setGridParam',{data:finished}).trigger("reloadGrid", [{current:false}]);
	} else {
		$("#ext_list").jqGrid({
			data:finished,
			height:200,
			width: 520,
			rowheight: 23,
			datatype: "local",
			colNames:['零件ID','零件编码','有效时段'],
			colModel:[  
			           {name:'partial_id',index:'partial_id',hidden:true},
					   {name:'code',index:'code',width:60,align:'left'},
					   {name:'active_date',index:'active_date',width:60,align:'center',formatter : function(value, options, rData){
							if(value){
								return value+" ～"  ;
							}else{
								return '';
							}							
						}}					 
					 ],
			rowNum : 35,
			toppager : false,
			pager : "#ext_listpager",
			viewrecords : true,
			gridview : true,
			pagerpos : 'right',
			pgbuttons : true, 
			pginput : false,					
			recordpos : 'left',
			hidegrid : false,
			deselectAfterSort : false				
		});
			
	}
};


var showDelete = function(rid) {
	
	$("#confirmmessage").text("删除不能恢复。确认要删除该条记录吗？");
	$("#confirmmessage").dialog({
		resizable : false,
		modal : true,
		title : "删除确认",
		buttons : {
			"确认" : function() {
				$(this).dialog("close");
			},
			"取消" : function() {
				$(this).dialog("close");
			}
		}
	});
};

var showList = function() {
	$("#condition_view,#searchform").show();
	$("#listarea").show();
	$("#editarea").hide();
}