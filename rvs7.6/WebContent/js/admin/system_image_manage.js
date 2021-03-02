var hostname = document.location.hostname;

/** 一览数据对象 */
var listdata = {};

var nowPage = "pcs";

/** 服务器处理路径 */
var servicePath = "system_image_manage.do";

var findit = function(data) {
	if(!data){
		searchdata= {
			"classify" : nowPage
		};
	}else{
		searchdata = data;
	}
	
	// Ajax提交
	$.ajax({
		beforeSend : ajaxRequestType,
		async : true,
		url : servicePath + '?method=search',
		cache : false,
		data : searchdata,
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : search_handleComplete
	});
};

/**
 * 页面加载处理
 */
$(function() {
	$("#infoes >div").buttonset();
	
	// 适用jquery按钮
	$("input.ui-button").button();

	// 右上图标效果
	$("a.areacloser").hover(function() { $(this).addClass("ui-state-hover");},
		function() {$(this).removeClass("ui-state-hover");});
	
	$("#searcharea span.ui-icon").bind("click", function() {
		$(this).toggleClass('ui-icon-circle-triangle-n').toggleClass('ui-icon-circle-triangle-s');
		if ($(this).hasClass('ui-icon-circle-triangle-n')) {
			$(this).parent().parent().next().show("blind");
		} else {
			$(this).parent().parent().next().hide("blind");
		}
	});

	// 检索处理--前台检索
	$("#searchbutton").click(function(){
		showSearch();
	});
	
	$("#file_classify").select2Buttons();
	
	//前台检索数据(双击之后，型号、等级、梯队进行检索)
	var filterList = function() {}

	// 清空检索条件
	$("#resetbutton").click(function() {
		$("#search_file_name").val("");
		$("#search_description").val("");
	});
	
	
    //初始化详细信息一览
	$("#list").jqGrid({
		data:listdata,
		height : 461,
		width : gridWidthMiddleRight,
		rowheight : 23,
		rownumbers : true,
		datatype: "local",
		colNames:['文件名','隐藏系统图片名称','文件分类','说明'],
		colModel:[
		           {name:'file_name',index:'file_name',width : 40,align:'center',
		            	formatter : function(value, options, rData){
							return "<a href='javascript:downExcel(\"" + rData['file_name'] + "\");' >" + rData['file_name'] + "</a>";
		   				}},
		   		   {name:'hidden_file_name',index:'hidden_file_name',hidden:true,
			            	formatter : function(value, options, rData){
								return  rData.file_name;
			            }},
			       {name:'classify',index:'classify',width : 30,hidden:true},
				   {name:'description',index:'description',width : 240}
		         ],
		rowNum: 100,
		toppager : false,
		pager : "#listpager",
		viewrecords : true,
		caption : "系统图片信息一览",
		viewrecords : true,
		gridview : true,
		pagerpos : 'right',
		pgbuttons : true, 
		pginput : false,					
		recordpos : 'left',
		hidegrid : false,
		ondblClickRow : showEdit,
		deselectAfterSort : false,
		gridComplete:function(){
			// 得到显示到界面的id集合
			var IDS = $("#list").getDataIDs();
			// 当前显示多少条
			var length = IDS.length;
			var pill = $("#list");
			for (var i = 0; i < length; i++) {
				// 从上到下获取一条信息
				var rowData = pill.jqGrid('getRowData', IDS[i]);
				var fileName = rowData["hidden_file_name"];
				var classify = rowData["hidden_classify"];
				
				if($("#pcs_button").attr("checked")=="checked"){
					classify = "pcs";
				}else if($("#sign_button").attr("checked")=="checked"){
					classify = "sign";
				}else if($("#tcs_button").attr("checked")=="checked"){
					classify = "tcs";
				}
				
				pill.find("tr#" + IDS[i] +  " td:nth-child(2) ").attr("href","/images/"+classify+"/"+fileName);
			}
			
			$("#list tr td:nth-child(2)").preview();
		}
	});
	
	$(".ui-jqgrid-hbox").before('<div class="ui-widget-content" style="padding:4px;">' +
			'<input type="button" class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="importbutton" value="上传系统图片" role="button" aria-disabled="false">' +
		'</div>');

	$("#importbutton").button();
	// 追加处理
	$("#importbutton").click(importPcs);

	
	//工程检查票内图
	$("#pcs_button").click(function(){
		$("#searchbutton").unbind("click");
		
		$("#hidden_choose_classify").val("pcs");
		
		$("#list").jqGrid('setGridWidth', gridWidthMiddleRight);
		$("#list").find("tr").find("td[aria\\-describedby='list_rn']").attr("width","20px");
		$("#list").find("tr").find("td[aria\\-describedby='list_file_name']").attr("width","132px");
		nowPage = "pcs";
		
		$("#searchbutton").click(function(){
			showSearch();
		});
		findit();
	});
	
	//员工姓名章
	$("#sign_button").click(function(){
		$("#searchbutton").unbind("click");
		
		$("#hidden_choose_classify").val("sign");
		
		$("#list").jqGrid('setGridWidth', gridWidthMiddleRight);
		$("#list").find("tr").find("td[aria\\-describedby='list_rn']").attr("width","20px");
		$("#list").find("tr").find("td[aria\\-describedby='list_file_name']").attr("width","132px");
		nowPage = "sign";
		
		$("#searchbutton").click(function(){
			showSearch();
		});
		findit();
	});
	
	//点检内单图
	$("#tcs_button").click(function(){
		$("#searchbutton").unbind("click");
		
		$("#hidden_choose_classify").val("tcs");
		
		$("#list").jqGrid('setGridWidth', gridWidthMiddleRight);
		$("#list").find("tr").find("td[aria\\-describedby='list_rn']").attr("width","20px");
		$("#list").find("tr").find("td[aria\\-describedby='list_file_name']").attr("width","132px");
		nowPage = "tcs";
		
		$("#searchbutton").click(function(){
			showSearch();
		});
		findit();
	});
	
	
	//双击编辑 -- 返回按钮
	$("#backbutton,#edit_pic span.ui-icon").click(function(){
		 $("#searcharea,#listarea").show();
		 $("#edit_pic,#import_pic").hide();
	});
	
	//双击编辑 -- 确认按钮
	$("#confirmbutton").click(function(){
		
		var data = {
				"file_name":$("#label_file_name").text(),
				"description":$("#edit_description").val(),
				"classify":$("#label_classify").text()
		}
		
		// Ajax提交
		$.ajax({
			beforeSend : ajaxRequestType,
			async : true,
			url : servicePath + '?method=doReplace',
			cache : false,
			data : data,
			type : "post",
			dataType : "json",
			success : ajaxSuccessCheck,
			error : ajaxError,
			complete : replace_handleComplete
		});
	});
	
	if($("#pcs_button").attr("checked")=="checked"){
		$("#hidden_choose_classify").val("pcs");
	}else if($("#sign_button").attr("checked")=="checked"){
		$("#hidden_choose_classify").val("sign");
	}else if($("#tcs_button").attr("checked")=="checked"){
		$("#hidden_choose_classify").val("tcs");
	}
	
	//初始化
	findit();
});

//检索
function showSearch(){
	var file_name_text = $("#search_file_name").val();
	var description_text = $("#search_description").val();

	if (file_name_text + description_text == "") {
		$("#list").find("tr").show();
	} else {
		$("#list").find("tr").show();
		$("#list").find("tr").each(function(){
			var $tr = $(this);
			if (file_name_text.length > 0) {
				if ($tr.find("td[aria\\-describedby='list_file_name']").text().trim() != file_name_text.toUpperCase()) {
					$tr.hide();
				}
			}
			if (description_text.length > 0) {
				if (!$tr.find("td[aria\\-describedby='list_description']").text().trim().contains(description_text)) {
					$tr.hide();
				}
			}
			$tr.find("td[aria\\-describedby='list_rn']").attr("width","20px");
			$tr.find("td[aria\\-describedby='list_file_name']").attr("width","132px");
		});
	}		
}


function showList(){
	 $("#searcharea,#listarea,#import_pic").show();
	 $("#edit_pic").hide();
}

var replace_handleComplete = function(xhrobj, textStatus) {
	var resInfo = null;
	try {
		// 以Object形式读取JSON
		eval('resInfo =' + xhrobj.responseText);

		if (resInfo.errors.length > 0) {
			// 共通出错信息框
			treatBackMessages("#edit_form", resInfo.errors);
		} else {
			$("#confirmmessage").text("编辑已经完成！");
			$("#confirmmessage").dialog({
				width : 320,
				height :'auto',
				resizable :false,
				show : "blind",
				modal : true,
				title : "提示信息",
				buttons : {
				    "关闭": function() {
				          $(this).dialog("close");
				     }
				}
			});
			$("#searcharea,#listarea").show();
			$("#import_pic,#edit_pic").hide();
			findit();
		}
	} catch (e) {
		console.log("160 name: " + e.name + " message: " + e.message + " lineNumber: "
				+ e.lineNumber + " fileName: " + e.fileName);
	};
};

/** 
 * 检索处理
 */
var searchdata;

/**
 * 检索Ajax通信成功时的处理
 */
var search_handleComplete = function(xhrobj, textStatus) {
	var resInfo = null;
	try {
		// 以Object形式读取JSON
		eval('resInfo =' + xhrobj.responseText);

		if (resInfo.errors.length > 0) {
			// 共通出错信息框
			treatBackMessages("#searcharea", resInfo.errors);
		} else {
			// 读取一览
			listdata = resInfo.fileNameList;
			show_ext_list(listdata);
		}
	} catch (e) {
		console.log("328 name: " + e.name + " message: " + e.message + " lineNumber: "
				+ e.lineNumber + " fileName: " + e.fileName);
	};
};

/*下载*/
var downExcel = function(fileName) {
	if ($("iframe").length > 0) {
		$("iframe").attr("src", "system_image_manage.do"+"?method=output&fileName="+ fileName +"&from=" + nowPage);
	} else {
		var iframe = document.createElement("iframe");
        iframe.src = "system_image_manage.do"+"?method=output&fileName="+ fileName +"&from=" + nowPage;
        iframe.style.display = "none";
        document.body.appendChild(iframe);
	}
}

function show_ext_list(listdata){
	if ($("#gbox_list").length > 0) {
		$("#list").jqGrid().clearGridData();
		$("#list").jqGrid('setGridParam',{data:listdata}).trigger("reloadGrid", [{current:false}]);
	} else {}
}

function importPcs(){
	
	$("#file_classify").val($("#infoes input:checked").val()).trigger("change");
	$("#import_fileName").val("").removeClass("errorarea-single");
	$("#import_file").val("").removeClass("errorarea-single");

	$("#import_pic").dialog({
		width : 400,
		height: 300,
		show: "blind",
		resizable : false,
		modal : true,
		title : "上传图片",
		close: function() {
			$(this).dialog("close");
		},
		buttons : {
			"确认" : function() {
				
				var data = {
					 "classify":$("#file_classify").val(),
				     "file_name":$("#import_fileName").val()
				}				
				$("#import_form").validate({
			        rules:{
			        	file_name:{
			                required:true
			            },
			            file:{
			                required:true
			            }
			        }
			    });
				if($("#import_form").valid()){ 
				    $.ajaxFileUpload({
				        url : servicePath + "?method=importPic", // 需要链接到服务器地址
				        secureuri : false,
				        data:data,
				        fileElementId : 'import_file', // 文件选择框的id属性
				        dataType : 'json', // 服务器返回的格式
				        success : function(responseText, textStatus) {
		                    var resInfo = null;
		                    try {
		                        // 以Object形式读取JSON
		                        eval('resInfo =' + responseText);
		                        
		                        if (resInfo.errors.length > 0) {
		                            // 共通出错信息框
		                            treatBackMessages("#import_form", resInfo.errors);
		                        } else if(resInfo.fileExistsErrors.length > 0){
		                        	//treatBackMessages("#import_form", resInfo.fileExistsErrors);
		                        	
		                        	warningConfirm("该文件名已经存在,是否覆盖?", function() {
            						$.ajax({
            							beforeSend : ajaxRequestType,
            							async : true,
            							url : servicePath + '?method=coverPic',
            							cache : false,
            							data : null,
            							type : "post",
            							dataType : "json",
            							success : ajaxSuccessCheck,
            							error : ajaxError,
            							complete : function(){
            								findit();
            								$("#import_pic").dialog("close");
            								infoPop("上传图片已经完成。");
            							}
            						});
            					});
		                        } else {
		                        	findit();
		                        	$("#import_pic").dialog("close");
		            				infoPop("上传图片已经完成。");
		                        }
		                    } catch (e) {
		                        console.log("457 name: " + e.name + " message: " + e.message + " lineNumber: "+ e.lineNumber + " fileName: " + e.fileName);
		                    };
		                }
			      });
				   // $(this).dialog("close");
				}
			},
			"取消" : function() {
				$(this).dialog("close");
			}
		}
	});
}

function showEdit(){
	
	$("#edit_description").val("");
	$("#edit_image").attr("src","");
    $("#searcharea,#listarea,#import_pic").hide();
    $("#edit_pic").show();    
    
    var row = $("#list").jqGrid("getGridParam", "selrow");
    var rowData = $("#list").getRowData(row);
    
    //双击获取值
    $("#label_file_name").text(rowData.hidden_file_name);
    
    $("#label_classify").text($("#hidden_choose_classify").val());
    //$("#hidden_classify").val(rowData.hidden_classify);
    $("#edit_image").attr("src","http://"+hostname+"/images/"+$("#hidden_choose_classify").val()+"/"+rowData.hidden_file_name);
    $("#edit_description").val(rowData.description);
}