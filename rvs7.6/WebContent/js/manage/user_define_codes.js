/** 服务器处理路径 */
var servicePath = "user_define_codes.do";
 
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

$(function(){
	$("input[type='button'], button").button();

	$("p\\:shaperange").hide();
	$("#page_radios").buttonset().click(function(evt){
		var id = $(evt.target).attr("id");
		if (id === "page_ud") {
			$("p\\:shaperange").hide();
			$("#listarea").show();
		} else if (id === "page_ia") {
			$("p\\:shaperange:eq(0)").show();
			$("p\\:shaperange:eq(1)").hide();
			$("#listarea").hide();
		} else if (id === "page_if") {
			$("p\\:shaperange:eq(0)").hide();
			$("p\\:shaperange:eq(1)").show();
			$("#listarea").hide();
		}
	});

	$("p\\:shaperange input:text")
		.attr("maxlength", "4")
		.change(function(){
			$(this).attr("updated", true);
			$(this).closest("p\\:shaperange").find("button").enable();
		});

	$("p\\:shaperange button").click(function(){
		updateStandards.apply(this, []);
	});

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
		var otherList = [];
		for (var idx in resInfo.userDefineCodesFormList) {
			var userDefineCodesForm = resInfo.userDefineCodesFormList[idx];
			if (userDefineCodesForm.code.length > 5 && userDefineCodesForm.code.substring(0,5) === "AFST-") {
				var code = userDefineCodesForm.code.substring(5);
				$("p\\:shaperange input[name=\"" + code + "\"]").val(userDefineCodesForm.value || "");
			} else {
				otherList.push(userDefineCodesForm);
			}
		}

		user_define_codes_list(otherList);

		$("p\\:shaperange input:text").removeAttr("updated");
		$("p\\:shaperange button").disable();
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

var updateStandards = function(){
	var updatedInputs = $(this).closest("p\\:shaperange").find("input[updated]");

	var updCodes = "", updValues = "", error = false;
	updatedInputs.each(function(idx, ele){
		updCodes += "AFST-" + $(ele).attr("name") + ";";
		value = $(ele).val();
		if (value.trim() == "" || isNaN(value)) {
			error = true;
		}
		updValues += value + ";";
	});

	if (error) {
		errorPop("请检查是否有非数值的输入。");
		return;
	}

	if (updCodes.length == 0) return;

	var data = {
		"code" : updCodes,
		"value": updValues
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
		infoPop("更新设定值已经完成!", null, "更新设定值");

		//初始化载查询一次
		doInit();
	}
};
