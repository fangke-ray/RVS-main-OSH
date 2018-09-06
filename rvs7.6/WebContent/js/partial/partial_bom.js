/** 一览数据对象 */
var listdata = {};
var partial_bom_listdata = {};
/** 服务器处理路径 */
var servicePath = "partial_bom.do";

$(function(){
	setReferChooser($("#search_model_id"));
	$("#search_level_id").select2Buttons();
	
	$("a.areacloser").hover(
		function (){$(this).addClass("ui-state-hover");},
		function (){$(this).removeClass("ui-state-hover");}
	);
	
	$("input.ui-button").button();
	
	$("#searchbutton").click(function(){
		$("#search_model_id").data("post", $("#search_model_id").val());
		$("#search_level_id").data("post", $("#search_level_id").val());
		$("#code_name").data("post",$("#code_name").val());
		findit();
	});
	
	$("#resetbutton").click(function(){
		reset();
	});
	
	$("#import_bom").click(function(){
		import_bom();
	});
	
	$("#dowload_bom").click(function(){
		dowload_bom();	
	});
	
	reset();
	doInit();
});

var doInit=function(){
	partial_bom_list([]);
};

/**检索**/
var findit=function(){
	/*$("#searchform").validate({
			rules : {
				view_mode_name : {
					required : true
				},
				level_name : {
					required : true
				}
			}
	});*/
	
	//if($("#searchform").valid()){
		var data={
			"model_id":$("#search_model_id").data("post"),
			"level":$("#search_level_id").data("post"),
			"code":$("#code_name").data("post")
		};
		
		$("#label_model_id").text($("#view_mode_name").val());
		$("#label_level_id").text($("#search_level_id :selected").text());
		
		if(($("#view_mode_name").val()==null || $("#view_mode_name").val()=="") 
			&& ($("#search_level_id").val()==null || $("#search_level_id").val()=="") 
			&& $("#code_name").val()!=null && $("#code_name").val()!=""){
			$("#partial_bom_list").jqGrid("showCol",["model_name","level"]);
			$("#partial_bom_list").jqGrid('setGridWidth', '992');
		}else{
			$("#partial_bom_list").jqGrid("hideCol",["model_name","level"]);
			$("#partial_bom_list").jqGrid('setGridWidth', '992');
		}
		
		//Ajax提交
		$.ajax({
			beforeSend : ajaxRequestType,
			async : true,//异步的请求
			url : servicePath + '?method=search',//请求提交的地址
			cache : false,//缓存
			data : data,//需要提交的数据
			type : "post",//提交类型post
			dataType : "json",//数据类型是json	
			success : ajaxSuccessCheck,//请求成功以后需要执行的函数
			error : ajaxError,//请求错误后需要执行的函数
			complete : find_ajaxSuccess//请求完成需要做的事（不管是请求成功或者是错误的情况）
		});
	//}
	
};

/**清空**/
var reset=function(){
	$("#search_model_id").val("").data("post", "").prev().val("");
	$("#search_level_id").val("").trigger("change").data("post", "");
	$("#code_name").val("").data("post","");
};

/****/
var find_ajaxSuccess = function(xhrobj, textStatus){
	var resInfo = null;
	try {
		// 以Object形式读取JSON
		eval('resInfo =' + xhrobj.responseText);
		if (resInfo.errors.length > 0) {
			// 共通出错信息框
			treatBackMessages(null, resInfo.errors);
		} else {
			partial_bom_list(resInfo.finished);
		}
	} catch (e) {
		alert("name: " + e.name + " message: " + e.message + " lineNumber: " + e.lineNumber + " fileName: " + e.fileName);
	};
};


var import_bom=function(){
	var this_dialog = $("#upload_dialog");
	if (this_dialog.length === 0) {
		$("body.outer").append("<div id='upload_dialog'/>");
		this_dialog = $("#upload_dialog");
	}
	this_dialog.html("<input name='file' id='bom_file' type='file'/>");
	this_dialog.dialog({
			title : "选择上传文件",
			position :[500, 400],	
			modal : true,
			width:300,
			minHeight : 200,
			resizable : false,
			buttons : {
				"上传":function(){
					this_dialog.dialog('close');
					uploadfile();
				},
				"关闭":function(){
					this_dialog.html("");
					this_dialog.dialog('close');
				}
			}
	});
};

var uploadfile=function(){
	$.ajaxFileUpload({
		url:'upload.do?method=doUploadBom', // 需要链接到服务器地址
		secureuri : false,
		fileElementId : 'bom_file', // 文件选择框的id属性
		dataType : 'json', // 服务器返回的格式
		success:function(responseText, textStatus){//服务器成功响应处理函数
			var resInfo = null;
			try{
				// 以Object形式读取JSON
				eval('resInfo =' + responseText);
				if (resInfo.errors.length > 0) {
					// 共通出错信息框
					treatBackMessages(null, resInfo.errors);
				}else{
					var this_dialog = $("#upload_dialog");
					if (this_dialog.length === 0) {
						$("body.outer").append("<div id='upload_dialog'/>");
						this_dialog = $("#upload_dialog");
					}
					this_dialog.text("BOM零件表已经导入！");
					this_dialog.dialog({
							title : "上传文件完成确认",
							position :[500, 400],	
							modal : true,
							width:300,
							minHeight : 200,
							resizable : false,
							buttons : {
								"确认":function(){
									this_dialog.html("");
									this_dialog.dialog('close');
								}
							}
					});
				}
			}catch(e){
			}
		}
	});
};


/**
 * 导出BOM表
 */
var dowload_bom=function(){
	$.ajax({
		beforeSend : ajaxRequestType,
		async : true,
		url : servicePath + '?method=reportBom',
		cache : false,
		data : null,
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : function(xhjObject) {
			var resInfo = null;
			eval("resInfo=" + xhjObject.responseText);
			if (resInfo && resInfo.fileName) {
				if ($("iframe").length > 0) {
					$("iframe").attr("src", "download.do" + "?method=output&filePath=" + resInfo.filePath+"&fileName="+resInfo.fileName);
				} else {
					var iframe = document.createElement("iframe");
					iframe.src = "download.do" + "?method=output&filePath=" + resInfo.filePath+"&fileName="+resInfo.fileName;
					iframe.style.display = "none";
					document.body.appendChild(iframe);
				}
			} else {
				alert("文件导出失败！"); 
			}
		}
	});
};

var partial_bom_list=function(partial_bom_listdata){
	if ($("#gbox_partial_bom_list").length > 0) {
		$("#partial_bom_list").jqGrid().clearGridData();//清除
		$("#partial_bom_list").jqGrid('setGridParam',{data:partial_bom_listdata}).trigger("reloadGrid", [{current:false}]);//刷新列表
	}else{
		$("#partial_bom_list").jqGrid({
			data:partial_bom_listdata,
			height: 461,
			width: 992,
			rowheight: 23,
			datatype: "local",
			colNames:['','型号','等级','零件编码','零件名称','数量','梯队'],
			colModel:[
				{
					name:'partial_id',
					index:'partial_id',
					hidden:true
				},
				{
					name:'model_name',
					index:'model_name',
					width:100,
					align:'center',
					hidden:true
				},
				{
					name:'level',
					index:'level',
					width:100,
					align:'center',
					hidden:true,
					formatter:'select',
					editoptions:{value:$("#goMaterial_level_inline").val()}
				}, 
				{
					name:'code',
					index:'code',
					width:60
				},
				{
					name:'partial_name',
					index:'partial_name',
					width:400
				},
				{
					name:'quantity',
					index:'quantity',
					width:40,
					formatter:'integer',
					sorttype:'integer',
					align:'right'
				},
				{
					name:'echelon',
					index:'echelon',
					width:90,
					align:'center',
					formatter:'select',
					editoptions:{value:$("#goEechelon_code").val()}
				}
			],
			rowNum: 20,
			toppager: false,
			pager: "#partial_bom_listpager",
			viewrecords: true,
			multiselect: false, 
			gridview: true,
			pagerpos: 'right',
			pgbuttons: true,
			pginput: false,
			recordpos:'left'
		})
	}
};