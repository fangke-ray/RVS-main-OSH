/** 服务器处理路径 */
var servicePath = "user_define_codes.do";

$(function(){
	 $("input[type='button']").button();
	 
	 /*初始化 用户定义数值详细数据*/
	var doInit=function(){
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
	};
	 
	//初始化(查询)
	doInit();
});


function search_Complete(xhrobj, textStatus) {
	var resInfo = null;
	// 以Object形式读取JSON responseText获取来自服务器响应的数据
	eval('resInfo =' + xhrobj.responseText);
	if (resInfo.errors.length > 0) {
		// 共通出错信息框
		treatBackMessages(null, resInfo.errors);
	} else {
		user_define_codes_list(resInfo.userDefineCodesFormList);
	}
};

//用户定义数值一览 
function user_define_codes_list(listdata){
	if ($("#gbox_user_define_codes_list").length > 0) {
		$("#user_define_codes_list").jqGrid().clearGridData();
		$("#user_define_codes_list").jqGrid('setGridParam',{data:listdata}).trigger("reloadGrid", [{current:false}]);
	}else{
		$("#user_define_codes_list").jqGrid({
			data:listdata,
			height: 550,
			width: 992,
			rowheight: 23,
			datatype: "local",
			colNames:['代码','项目','设定值','更新设定值','使用说明'],
			colModel:[
				{name:'code',index:'code',width:40,align:'center',hidden:true},
				{name:'description',index:'description',width:80,align:'left',formatter:function(value,options,rData){
					return "<label>"+value+" </label>";
				}},
				{name:'value',index:'value',width:60,align:'center',
					formatter : function(value, options, rData){	
						return  "<input type='text' value='"+value+"'id='"+ rData["code"] +"'>";
	   			}},
	   			{name:'update',index:'update',width:30,align:'center',
					formatter : function(value, options, rData){	
						return "<a style='text-decoration:none;' href='javascript:update_user_define_codes(\""+
						rData['code'] +"\");' ><input type='button'value='更新'class='reset-button  ui-state-default'></input></a>";
	   			}},
	   			{name:'manual',index:'manual',width:190}
			],
			rowNum: 20,
			toppager: false,
			pager: "#user_define_codes_listpager",
			viewrecords: true,
			rownumbers : true,
			multiselect: false, 
			gridview: true,
			pagerpos: 'right',
			pgbuttons: true,
			pginput: false,
			recordpos:'left'
		});
	}
}

var update_user_define_codes = function(code,value){
	var data = {
			"code":code,
			"value":  $("#"+code+"").val()
	};

	$.ajax({
		beforeSend : ajaxRequestType,
		async : true,
		url : servicePath + '?method=doUpdate',
		cache : false,
		data : data,
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : update_Complete
	});
};

function update_Complete(xhrobj, textStatus) {
	var resInfo = null;
	// 以Object形式读取JSON responseText获取来自服务器响应的数据
	eval('resInfo =' + xhrobj.responseText);
	if (resInfo.errors.length > 0) {
		// 共通出错信息框
		treatBackMessages(null, resInfo.errors);
	} else {
		$("#update_success_dialog").text("更新设定值已经完成!");
		$("#update_success_dialog").dialog({
			width : 320,
			height : 'auto',
			resizable : false,
			show : "blind",
			modal : true,
			title : "更新设定值",
			buttons : {
				"关闭" : function() {
					$("#update_success_dialog").dialog("close");
				}
			}
		});
		//初始化载查询一次
		doInit();
	}
};
