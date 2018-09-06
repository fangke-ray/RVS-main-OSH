/** 一览数据对象 */
var listdata = {};
var partial_base_line_value_listdata = {};
/** 服务器处理路径 */
var servicePath = "partial_base_line_value.do";

$(function(){
	$("input.ui-button").button();
	
	$("a.areacloser").hover(
		function (){$(this).addClass("ui-state-hover");},
		function (){$(this).removeClass("ui-state-hover");}
	);
	
	$("#searcharea span.ui-icon").bind("click", function() {
		$(this).toggleClass('ui-icon-circle-triangle-n').toggleClass('ui-icon-circle-triangle-s');
		if ($(this).hasClass('ui-icon-circle-triangle-n')) {
			$(this).parent().parent().next().show("blind");
		} else {
			$(this).parent().parent().next().hide("blind");
		}
	});

	$("#partial_code").data("post",	$("#partial_code").val());
	findit();
//	reset();
	
	/*检索*/
	$("#searchbutton").click(function(){
		$("#partial_code").data("post",	$("#partial_code").val());
		$("#partial_name").data("post",$("#partial_name").val());
		findit();
	});

	$("#resetbutton").click(function(){
		reset();
	});	
	
	$("#backbutton").click(function(){
		window.location.href = "partial_monitor.do"; // TODO status
	});	

	/*下载编辑基准值*/
	$("#download_button").click(function(){
		download_base_line_value();
	});
	
	/*上传编辑基准值*/
	$("#upload_button").click(function(){
		import_base_line_value();
	});
	
});

var findit=function(){
	var data={
		"partial_code":$("#partial_code").data("post"),
		"partial_name":$("#partial_name").data("post")
	};
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
			complete : search_complete
	});
};

var search_complete=function(xhrobj,textStatus){
	var resInfo = null;
	try {
		// 以Object形式读取JSON
		eval('resInfo =' + xhrobj.responseText);
		if (resInfo.errors.length > 0) {
			// 共通出错信息框
			treatBackMessages("", resInfo.errors);
		} else {
			partial_base_line_value_list(resInfo.responseFormList);
		}
	} catch (e) {
		alert("name: " + e.name + " message: " + e.message + " lineNumber: "+ e.lineNumber + " fileName: " + e.fileName);
	};
};

var reset=function(){
	$("#partial_code").val("").data("post","");
	$("#partial_name").val("").data("post","");
};


var download_base_line_value=function(){
    var data={
        "partial_code":$("#partial_code").data("post"),
        "partial_name":$("#partial_name").data("post")
    }
	$.ajax({
		beforeSend : ajaxRequestType,
		async : true,
		url : servicePath + '?method=reportPartialBaseLineValue',
		cache : false,
		data : data,
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

var import_base_line_value=function(){
	var this_dialog = $("#upload_dialog");
	if (this_dialog.length === 0) {
		$("body.outer").append("<div id='upload_dialog'/>");
		this_dialog = $("#upload_dialog");
	}
	this_dialog.html("<form id='uploadForm'><input name='file' id='partial_base_line_value' type='file'/><br>选择日期<input type='text' name='upload_end_date' id='upload_end_date' alt='有效区间开始'/></form>");
	
	$("#upload_end_date").datepicker({
			showButtonPanel : true,
			dateFormat : "yy/mm/dd",
			currentText : "今天"
	});
		
	$("#uploadForm").validate({
		rules : {
			upload_end_date : {
				required : true
			}
		}
	});
	
	this_dialog.dialog({
			title : "选择上传文件",
			position :[500, 400],	
			modal : true,
			width:300,
			minHeight : 200,
			resizable : false,
			buttons : {
				"上传":function(){
					if($("#uploadForm").valid()){
						uploadfile($("#upload_end_date").val());
					}
				},
				"关闭":function(){
					this_dialog.html("");
					this_dialog.dialog('close');
				}
			}
	});
};

var uploadfile=function(end_date){
	$.ajaxFileUpload({
		url:'upload.do?method=doUploadTotalForeboardCount&end_date='+end_date, // 需要链接到服务器地址
		secureuri : false,
		fileElementId : 'partial_base_line_value', // 文件选择框的id属性
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
					$("#upload_dialog").dialog('close');
					findit();
					treatBackMessages(null, resInfo.infos);
				}
			}catch(e){
			}
		}
	});
};


function partial_base_line_value_list(listdata){
	if ($("#gbox_partial_base_line_value_list").length > 0) {
		$("#partial_base_line_value_list").jqGrid().clearGridData();
		$("#partial_base_line_value_list").jqGrid('setGridParam',{data:listdata}).trigger("reloadGrid", [{current:false}]);
	}else{
		$("#partial_base_line_value_list").jqGrid({
			data:listdata,
			height: 470,
			width: 992,
			rowheight: 23,
			datatype: "local",
			colNames:['','零件编号','零件名称','前半年平均使用量','前三个月平均使用量','当月平均使用量','当前设定基准值'],
			colModel:[
				{
					name:'partial_id',
				    index:'partial_id',
					hidden:true
				},
				{
					name:'partial_code',
					index:'partial_code',
					width:30,
					align:'left'
				},
				{
					name:'partial_name',
					index:'partial_name',
					width:60,
					align:'left'
				},
				{
					name:'quantityOfHalfYear',
					index:'quantityOfHalfYear',
					align:'right',
					width:20,
					formatter:'number',
					sorttype:'number',
					formatoptions:{suffix:' 件',defaultValue: ' - '}
				},
				{
					name:'quantityOfThreeMonthAge',
					index:'quantityOfThreeMonthAge',
					align:'right',
					width:20,
					formatter:'number',
					sorttype:'number',
					formatoptions:{suffix:' 件',defaultValue: ' - '}
				},
				{
					name:'quantityOfOneMonthAge',
					index:'quantityOfOneMonthAge',
					align:'right',
					width:20,
					formatter:'number',
					sorttype:'number',
					formatoptions:{suffix:' 件',defaultValue: ' - '}
				},
				{
					name:'osh_foreboard_count',
					index:'osh_foreboard_count',
					align:'right',
					width:20,
					formatter:'integer',
					sorttype:'integer',
					formatoptions:{suffix:' 件',defaultValue: ''}
				}
			],
			rowNum: 20,
			rownumbers : true,
			toppager: false,
			pager: "#partial_base_line_value_listpager",
			viewrecords: true,
			pagerpos: 'right',
			pgbuttons: true,
			pginput: false,
			hidegrid: false, 
			recordpos:'left',
			ondblClickRow:function(rowid,iRow,iCol,e){
				var rowID = $("#partial_base_line_value_list").jqGrid("getGridParam", "selrow");// 得到选中行的ID	
				var rowData=$("#partial_base_line_value_list").getRowData(rowID);
				change_base_line(rowData.partial_id,rowData.partial_code,$("#simpleDateStart").val(),$("#simpleDateEnd").val(), findit);
				$("#edit_partial_base_line_value").hide();
			}
		});
	}
};