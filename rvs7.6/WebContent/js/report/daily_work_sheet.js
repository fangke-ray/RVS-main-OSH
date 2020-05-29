var servicePath = "daily_work_sheet.do";
var nowPage = "report_accept";

var captions = {};
captions["report_inline"] = "每日投线工作记录表";
captions["report_accept"] = "每日受理工作记录表";
captions["report_sterilize"] = "每日灭菌工作记录表";
captions["report_disinfect"] = "每日消毒工作记录表";
captions["report_schedule"] = "每日计划工作记录表";
captions["report_shipping"] = "每日出货工作记录表";
captions["report_unrepaire"] = "每周未修理翻修品清点状况记录表";
captions["report_snout"] = "每月先端回收记录表";
captions["report_wash"] = "钢丝固定件清洗记录表";

$(function(){
	$("#infoes >div").buttonset();
	$("#daily_work_sheet_load").hide();
	$("a.areacloser").hover(
		function (){$(this).addClass("ui-state-hover");},
		function (){$(this).removeClass("ui-state-hover");}
	);
	$("#body-mdl span.ui-icon").bind("click", function() {
		$(this).toggleClass('ui-icon-circle-triangle-n').toggleClass('ui-icon-circle-triangle-s');
		if ($(this).hasClass('ui-icon-circle-triangle-n')) {
			$(this).parent().parent().next().show("blind");
		} else {
			$(this).parent().parent().next().hide("blind");
		}
	});	

	var today = new Date();

	$("#search_year_month_day").monthpicker({
		showButtonPanel : true,
		pattern: "yyyymm",
		startYear: 2013,
		finalYear: today.getFullYear(),
		selectedMonth: today.getMonth() + 1,
		monthNames: ['1月', '2月', '3月', '4月', '5月', '6月', '7月', '8月', '9月', '10月', '11月', '12月']
	});

	var todayString = today.getFullYear() + "-" + mdTextOfDate(today);

	$("#exd_list").jqGrid({
		data : [],
		height : 461,
		width : 990,
		rowheight : 23,
		shrinkToFit:true,
		datatype : "local",  
		colNames : ['日期', '系统生成', '确认完成','上传'],
		colModel : [ 
            {name : 'fileDayTime',index : 'fileDayTime',align : 'center'}, 
            {name : 'fileName',index : 'fileName',width : 200,align : 'center',
            	formatter : function(value, options, rData){
            		if (todayString == rData['fileDayTime'])
						return "<a href='javascript:downExcel(\"" + rData['fileName'] + "\");' >(临时) " + rData['fileName'] + "</a>";
					else
						return "<a href='javascript:downExcel(\"" + rData['fileName'] + "\");' >" + rData['fileName'] + "</a>";
   				}},
            {name : 'confirmfilename',index : 'confirmfilename',width : 200,align : 'center',
   					formatter : function(value, options, rData){
   					   return	"<a href='javascript:downConfirmExcel(\"" + rData['confirmfilename'] + "\");' >" + rData['confirmfilename'] + "</a>"
   				}} ,
            {name : 'upload',index : 'upload',width : 80,align : 'center',
				formatter : function(value, options, rData){	
					return "<a style='text-decoration:none;' href='javascript:import_upload_file(\"" +
					rData['fileDayTime'] + "\"" + (rData['fileName'].indexOf('备品') >= 0 ? ', true' : '') +
					");' ><input type='button'value='上传'class='upload-button ui-state-default '></input></a>";
   			}}],
		rowNum :20,
		toppager : false,
		pager : "#exd_listpager",
		viewrecords : true,
		caption : "每日受理工作记录表",
		multiselect : false,
		gridview : true,
		pagerpos : 'right',
		pgbuttons : true,
		rownumbers : true,
		pginput : false,
		recordpos : 'left',
		hidegrid : false,
		deselectAfterSort : false,
		ondbClickRow : function(rid, iRow, iCol, e) {},
		viewsortcols : [ true, 'vertical', true ],
		gridComplete : function() {
			
		}
	});

	/*投线*/
	$("#linebutton").click(function(){
		$("#choosebutton").show();
//		$("#monthdownbutton").hide();
		$("#monthshowbutton").hide();
		if ($("#hidden_is_inline").val()) {
			$("#exd_list").jqGrid('showCol', ["confirmfilename", "upload"]).jqGrid('setGridWidth', '990');
		} else {
			$("#exd_list").jqGrid('showCol', ["confirmfilename"]).jqGrid('hideCol', ["upload"]).jqGrid('setGridWidth', '990');
		}
		nowPage = "report_inline";
		findit();
	})

	/*受理*/
	$("#acceptbutton").click(function(){
		$("#choosebutton").show();
//		$("#monthdownbutton").hide();
		$("#monthshowbutton").hide();
		if ($("#hidden_is_accept").val()) {
			$("#exd_list").jqGrid('showCol', ["confirmfilename", "upload"]).jqGrid('setGridWidth', '990');
		} else {
			$("#exd_list").jqGrid('showCol', ["confirmfilename"]).jqGrid('hideCol', ["upload"]).jqGrid('setGridWidth', '990');
		}
		nowPage = "report_accept";
		findit();
	});
	/*灭菌*/
	$("#disinfectionbutton").click(function(){
		$("#choosebutton").show();
//		$("#monthdownbutton").hide();
		$("#monthshowbutton").hide();
		if ($("#hidden_is_disinfection").val()) {
			$("#exd_list").jqGrid('showCol', ["confirmfilename", "upload"]).jqGrid('setGridWidth', '990');
		} else {
			$("#exd_list").jqGrid('showCol', ["confirmfilename"]).jqGrid('hideCol', ["upload"]).jqGrid('setGridWidth', '990');
		}
		nowPage = "report_sterilize";
		findit();
	});
	/*消毒*/
	$("#disinfectbutton").click(function(){
		$("#choosebutton").show();
//		$("#monthdownbutton").show();
		$("#monthshowbutton").hide();
		if ($("#hidden_is_disinfect").val()) {
			$("#exd_list").jqGrid('showCol', ["confirmfilename", "upload"]).jqGrid('setGridWidth', '990');
		} else {
			$("#exd_list").jqGrid('showCol', ["confirmfilename"]).jqGrid('hideCol', ["upload"]).jqGrid('setGridWidth', '990');
		}
		nowPage = "report_disinfect";
		findit();
	});
	/*计划*/
	$("#plancombutton").click(function(){
		$("#choosebutton").show();
//		$("#monthdownbutton").hide();
		$("#monthshowbutton").hide();
		$("#exd_list").jqGrid('hideCol', ["confirmfilename", "upload"]).jqGrid('setGridWidth', '990');
		nowPage = "report_schedule";
		findit();
	});
	/*出货*/
	$("#shipmentbutton").click(function(){
		$("#choosebutton").show();
//		$("#monthdownbutton").hide();
		$("#monthshowbutton").hide();
		if ($("#hidden_is_shipment").val()) {
			$("#exd_list").jqGrid('showCol', ["confirmfilename", "upload"]).jqGrid('setGridWidth', '990');
		} else {
			$("#exd_list").jqGrid('showCol', ["confirmfilename"]).jqGrid('hideCol', ["upload"]).jqGrid('setGridWidth', '990');
		}
		nowPage = "report_shipping";
		findit();
	});
	
	//未修理
	$("#unrepairebutton").click(function(){
		$("#choosebutton").show();
//		$("#monthdownbutton").hide();
		$("#monthshowbutton").hide();
		$("#exd_list").jqGrid('hideCol', ["confirmfilename", "upload"]).jqGrid('setGridWidth', '990');
		nowPage = "report_unrepaire";
		findit();
	});
	
	//先端回收
	$("#snoutbutton").click(function(){
		$("#choosebutton").hide();
//		$("#monthdownbutton").hide();
		$("#monthshowbutton").show();
		$("#exd_list").jqGrid('hideCol', ["confirmfilename", "upload"]).jqGrid('setGridWidth', '990');
		nowPage = "report_snout";
		findit();
	});
	
	//钢丝固定件清洗
	$("#washbutton").click(function(){
		$("#choosebutton").show();
		$("#monthdownbutton").hide();
		$("#monthshowbutton").hide();
		$("#exd_list").jqGrid('hideCol', ["confirmfilename", "upload"]).jqGrid('setGridWidth', '990');
		nowPage = "report_wash";
		findit();
	});
	
	$("#daily_work_sheet_load").show();
	$("#choosebutton").button();
	$("#choosebutton").click(function(){
		findit();
	});
	$("#acceptbutton").trigger("click");

	$("#monthshowbutton").button().click(showmonth);
});

/*上传文件*/ 
var import_upload_file = function(date, isSpare) {
	$("#upload_file").hide();
	$("#upload_file").html("<input name='file' id='make_upload_file' type='file'/>");		
	$("#upload_file").dialog({
		title : "选择上传文件",
		width : 280,
		show: "blind",
		height : 180,
		resizable : false,
		modal : true,
		minHeight : 200,
		close : function(){
			$("#upload_file").html("");
		},
		buttons : {
			"上传":function(){
				  uploadfile(date, isSpare);
			}, "关闭" : function(){ $(this).dialog("close"); }
		}
		
	});
	$("#upload_file").show();	
};

var searchdata;
var findit  = function(data){
	//$("#search_year_month_day").data("post",$("#search_year_month_day").val());
	if(!data){
		searchdata= { "year_month":$("#search_year_month_day").val(), type : nowPage};
	}else{
		searchdata = data;
	}
	$.ajax({
		beforeSend: ajaxRequestType, 
		async: true, 
		url: servicePath + '?method=searchDailyWorkSheet', 
		cache: false, 
		data: searchdata, 
		type: "post", 
		dataType: "json", 
		success: ajaxSuccessCheck, 
		error: ajaxError,
		complete: search_handleComplete
	});
}

var showmonth = function(data){
	if (nowPage == "report_snout") {
		$.ajax({
			beforeSend: ajaxRequestType, 
			async: true, 
			url: 'snouts.do?method=searchSnoutsOnMonth', 
			cache: false, 
			data: {"year_month":$("#search_year_month_day").val()}, 
			type: "post", 
			dataType: "json", 
			success: ajaxSuccessCheck, 
			error: ajaxError,
			complete: showmonth_handleComplete
		});
	}
}

var showmonth_handleComplete = function(xhrObj) {
	var resInfo = $.parseJSON(xhrObj.responseText);
	if (resInfo.errors.length == 0) {
		var $snout_renege_sheet = $("#snout_renege_sheet");
		if ($snout_renege_sheet.length == 0) {
			$("body").append("<div id='snout_renege_sheet'>");
			$snout_renege_sheet = $("#snout_renege_sheet");
		}

		var tableHtml = "<style>#snout_renege_sheet td {border: 1px solid rgb(170, 170, 170); text-align:center;} #snout_renege_sheet tr td:nth-child(2) {text-align:left; padding-left:0.5em;}</style>" +
				"<table style='border-collapse: collapse;'><tr><th class='ui-state-default'>日期</th><th class='ui-state-default' style='min-width: 136px;'>型号</th><th class='ui-state-default'>机身号</th>" +
				"<th class='ui-state-default'>来源修理编号</th><th class='ui-state-default'>管理编号</th><th class='ui-state-default'>用于内镜修理编号</th></tr>";

		for (var ilist in resInfo.list) {
			var snout = resInfo.list[ilist];
			tableHtml += "<tr><td>" + snout.finish_time.substring(5, 10) + "</td><td>" + snout.model_name + "</td><td>" + snout.origin_serial_no + "</td>" +
					"<td>" + snout.origin_omr_notifi_no + "</td><td>" + snout.serial_no + "</td><td>" + (snout.sorc_no || "(未使用)") + "</td></tr>"
		}

		tableHtml += "</table>";
		$snout_renege_sheet.html(tableHtml);
		$snout_renege_sheet.dialog({
			title : "先端回收记录表",
			width : 500,
			show: "blind",
			height : 500,
			resizable : false,
			modal : true,
			minHeight : 200,
			close : function(){
				$snout_renege_sheet.html("");
			},
			buttons : {
				"关闭" : function(){$snout_renege_sheet.dialog("close"); }
			}
		});
	}
}

var uploadfile = function(date, isSpare) {
	// 覆盖层
panelOverlay++;
makeWindowOverlay();

// ajax enctype="multipart/form-data"
$.ajaxFileUpload({
	url : 'upload.do?method=confirm&filepath=' + nowPage + (isSpare ? "-spare" : "") + '&date=' + date, // 需要链接到服务器地址
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
				treatBackMessages(null, resInfo.errors);
			} else {
				$("#upload_file").dialog('close');
				$("#upload_file").text("上传文件完成。");
				$("#upload_file").dialog({
					resizable : false,
					modal : true,
					title : "导入文件确认",
					buttons : {
						"确认" : function() {
							$(this).dialog("close");
						}
					}
				});
				findit();
			}
		} catch(e) {
			
		}
	}
});
};
/*下载*/
var downExcel = function(fileName) {
	if ($("iframe").length > 0) {
		$("iframe").attr("src", "download.do"+"?method=output&fileName="+ fileName +"&from=" + nowPage);
	} else {
		var iframe = document.createElement("iframe");
        iframe.src = "download.do"+"?method=output&fileName="+ fileName +"&from=" + nowPage;
        iframe.style.display = "none";
        document.body.appendChild(iframe);
	}
}
/*下载确认*/
var downConfirmExcel = function(fileName) {
	if ($("iframe").length > 0) {
		$("iframe").attr("src", "download.do"+"?method=output&fileName="+ fileName +"&from=" + nowPage + "_confirm");
	} else {
		var iframe = document.createElement("iframe");
        iframe.src = "download.do"+"?method=output&fileName="+ fileName +"&from=" + nowPage + "_confirm";
        iframe.style.display = "none";
        document.body.appendChild(iframe);
	}
}

/*投线jrgrid*/
var search_handleComplete = function(xhrobj, textStatus) {
var resInfo = null;
try {
	// 以Object形式读取JSON
	eval('resInfo =' + xhrobj.responseText);
	if (resInfo.errors.length > 0) {
		// 共通出错信息框
		treatBackMessages("#daily_work_sheet_load", resInfo.errors);
	} else {
	    var listdata = resInfo.fileNameList;

		if ($("#gbox_exd_list").length > 0) {
			$("#exd_list").jqGrid().clearGridData();
			$("#exd_list").jqGrid('setGridParam', {data : listdata}).trigger("reloadGrid", [{current : false}]);
			$("#exd_list").setCaption(captions[nowPage]);
		} else {}
	}
					
}catch (e) {
	alert("name: " + e.name + " message: " + e.message + " lineNumber: "+ e.lineNumber + " fileName: " + e.fileName);
};
}


