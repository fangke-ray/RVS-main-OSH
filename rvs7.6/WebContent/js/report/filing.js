/** 一览数据对象 */
var listdata = {};
var quotation_listdata = {};
/** 服务器处理路径 */
var servicePath = "filing.do";

//var lOptions = {};
//var oOptions = {};

/** 根据条件使按钮有效/无效化 */
var enablebuttons = function() {
	var rowids = $("#qa_list").jqGrid("getGridParam", "selarrrow");
	if (rowids.length === 0) {
		$("#filingbutton").disable();
	} else {
		$("#filingbutton").enable();
	}
}

var downPdf = function(sorc_no) {
	if ($("iframe").length > 0) {
		$("iframe").attr("src", "download.do"+"?method=output&fileName="+ sorc_no +".zip&from=pcs");
	} else {
		var iframe = document.createElement("iframe");
        iframe.src = "download.do"+"?method=output&fileName="+ sorc_no +".zip&from=pcs";
        iframe.style.display = "none";
        document.body.appendChild(iframe);
	}
}

var rePdf = function(sorc_no, material_id) {
	if (confirm("是否重新生成"+sorc_no+"的PDF文档？")) {
		// download.do?method=file&material_id=" + workingPf.getMaterial_id()
		var data = {material_id : material_id};
		$.ajax({
			beforeSend : ajaxRequestType,
			async : true,
			url : 'download.do?method=file',
			cache : false,
			data : data,
			type : "post",
			dataType : "json",
			success : ajaxSuccessCheck,
			error : ajaxError,
			complete : function() {
				alert(sorc_no+"的工程检查票重新生成完毕！");
			}
		});
	}
}
var doFiling = function(type) {

	var selectedId = $("#qa_list").getGridParam("selrow");
	var rowData = $("#qa_list").getRowData(selectedId);

	var data = {material_id : rowData["material_id"]};

	// Ajax提交
	$.ajax({
		beforeSend : ajaxRequestType,
		async : false,
		url : servicePath + '?method=dofiling',
		cache : false,
		data : data,
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : function() {
			$("#scanner_inputer").attr("value", "");
			$("#material_details").hide();
			$("#scanner_container").show();
			$("#pcsarea").hide();
			doInit();
		}
	});
};

var doInit_ajaxSuccess = function(xhrobj, textStatus){
	var resInfo = null;
	try {
		// 以Object形式读取JSON
		eval('resInfo =' + xhrobj.responseText);

		if (resInfo.errors.length > 0) {
			// 共通出错信息框
			treatBackMessages(null, resInfo.errors);
		} else {
//			lOptions = resInfo.lOptions;
//			oOptions = resInfo.oOptions;
			filed_list(resInfo.finished);
		}
	} catch (e) {
		alert("name: " + e.name + " message: " + e.message + " lineNumber: "
				+ e.lineNumber + " fileName: " + e.fileName);
	};
};

var doInit=function(){
	// Ajax提交
	$.ajax({
		beforeSend : ajaxRequestType,
		async : false,
		url : servicePath + '?method=search',
		cache : false,
		data : {finish_time_start : $("#search_finish_time_start").val()},
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : doInit_ajaxSuccess
	});
};

/** 
 * 检索处理
 */
var findit = function() {
	// 读取已记录检索条件提交给后台
	var data = {
		"category_id" : $("#search_category_id").data("post"),
		"model_id":$("#search_modelname").data("post"),
		"serial_no":$("#search_serialno").data("post"),
		"sorc_no":$("#search_sorcno").data("post"),
		"section_id":$("#search_section_id").data("post"),
		"reception_time_start":$("#reception_time_start").data("post"),
		"reception_time_end":$("#reception_time_end").data("post"),
		"agreed_date_start":$("#search_agreed_date_start").data("post"),
		"agreed_date_end":$("#search_agreed_date_end").data("post"),
		"complete_date_start":$("#qa_pass_start").data("post"),
		"complete_date_end":$("#qa_pass_end").data("post"),
		"finish_time_start":$("#search_finish_time_start").data("post"),
		"finish_time_end":$("#search_finish_time_end").data("post"),
		"scheduled_date_start":$("#scheduled_date_start").data("post"),
		"scheduled_date_end":$("#scheduled_date_end").data("post"),
		"level":$("#search_level").data("post")
	}

	// Ajax提交
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
		complete : doInit_ajaxSuccess
	});
};

var reset = function() {
	$("#search_category_id").val("").trigger("change").data("post", "");
	$("#txt_modelname").val("").data("post", "");
	$("#search_modelname").val("").data("post", "");
	$("#search_serialno").val("").data("post", "");
	$("#search_sorcno").val("").data("post", "");
	$("#search_section_id").val("").trigger("change").data("post", "");
	$("#reception_time_start").val("").data("post", "");
	$("#reception_time_end").val("").data("post", "");
	$("#qa_pass_start").val("").data("post", "");
	$("#qa_pass_end").val("").data("post", "");
	$("#scheduled_date_start").val($("#h_date_start").val()).data("post", $("#h_date_start").val());
	$("#scheduled_date_end").val("").data("post", "");
	$("#search_agreed_date_start").val("").data("post", "");
	$("#search_agreed_date_end").val("").data("post", "");
	$("#search_finish_time_start").val($("#h_date_start").val()).data("post", $("#h_date_start").val());
	$("#search_finish_time_end").val("").data("post", "");
	$("#search_level").val("").trigger("change").data("post", "");
};

$(function() {
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
	$("#search_category_id, #search_section_id, #search_level").select2Buttons();

	$("#reception_time_start, #reception_time_end, #qa_pass_start, #qa_pass_end, #scheduled_date_start, #scheduled_date_end," +
			"#search_agreed_date_start, #search_agreed_date_end, #search_finish_time_start, #search_finish_time_end").datepicker({
		showButtonPanel:true,
		dateFormat: "yy/mm/dd",
		currentText: "今天"
	});

	$("#filingbutton").disable();
	$("#filingbutton").click(doFiling);

	$("#searchbutton").click(function() {
		$("#search_category_id").data("post", $("#search_category_id").val());
		$("#search_modelname").data("post", $("#search_modelname").val());
		$("#search_serialno").data("post", $("#search_serialno").val());
		$("#search_sorcno").data("post", $("#search_sorcno").val());
		$("#search_section_id").data("post", $("#search_section_id").val());
		$("#reception_time_start").data("post", $("#reception_time_start").val());
		$("#reception_time_end").data("post", $("#reception_time_end").val());
		$("#qa_pass_start").data("post", $("#qa_pass_start").val());
		$("#qa_pass_end").data("post", $("#qa_pass_end").val());
		$("#scheduled_date_start").data("post", $("#scheduled_date_start").val());
		$("#scheduled_date_end").data("post", $("#scheduled_date_end").val());

		$("#search_agreed_date_start").data("post", $("#search_agreed_date_start").val());
		$("#search_agreed_date_end").data("post", $("#search_agreed_date_end").val());
		$("#search_finish_time_start").data("post", $("#search_finish_time_start").val());
		$("#search_finish_time_end").data("post", $("#search_finish_time_end").val());
		$("#search_level").data("post", $("#search_level").val());

		findit();
	});

	setReferChooser($("#search_modelname"), $("#model_refer"), $("#search_category_id"));

	$("#resetbutton").click(function() {
		reset();
	}); 
	doInit();
});

function filed_list(quotation_listdata){
	if ($("#gbox_exd_list").length > 0) {
		$("#exd_list").jqGrid().clearGridData();
		$("#exd_list").jqGrid('setGridParam',{data:quotation_listdata}).trigger("reloadGrid", [{current:false}]);
	} else {
		$("#exd_list").jqGrid({
			toppager : true,
			data:quotation_listdata,
			height: 461,
			width: 992,
			rowheight: 23,
			datatype: "local",
			colNames:['','受理日','同意日','品保日'
				// ,'归档时间'
				, '修理单号', '修理结果', '型号 ID', '型号' , '机身号','维修等级', '归档文件', ''],
			colModel:[
					{name: 'material_id', index: 'material_id', hidden: true},
					{
						name : 'reception_time',
						index : 'reception_time',
						width : 50,
						align : 'center', 
						sorttype: 'date', formatter: 'date', formatoptions: {srcformat: 'Y/m/d', newformat: 'y/m/d'}
					}, {
						name : 'agreed_date',
						index : 'agreed_date',
						width : 50,
						align : 'center', 
						sorttype: 'date', formatter: 'date', formatoptions: {srcformat: 'Y/m/d', newformat: 'y/m/d'}
					}, {
						name : 'finish_time',
						index : 'finish_time',
						width : 50,
						align : 'center', 
						sorttype: 'date', formatter: 'date', formatoptions: {srcformat: 'Y/m/d', newformat: 'y/m/d'}
					},
//				{name:'quotation_time',index:'quotation_time', width:65, align:'center',
//						sorttype: 'date', formatter: 'date', formatoptions: {srcformat: 'Y/m/d H:i:s', newformat: 'H:i'}
//				},
				{name:'sorc_no',index:'sorc_no', width:105},
				{name:'break_back_flg',index:'break_back_flg',formatter:'select',editoptions:{value:"0:修理完成;2:未修理返还"}, align : 'center', width:40},
				{name:'model_id',index:'model_id', hidden:true},
				{name:'model_name',index:'model_id', width:125},
				{name:'serial_no',index:'serial_no', width:50, align:'center'},
				{name:'level',index:'level', width:40, align:'center',formatter:'select',editoptions:{value:$("#sLevel").val()}},
				{name:'pcs_pdf',index:'pcs_pdf', width:85, align:'center',
					formatter : function(value, options, rData){
					if (rData['isHistory']) 
						return "<a href='javascript:downPdf(\"" + rData['sorc_no'] + "\");' >" + rData['sorc_no'] + ".zip</a>";
					else
						return "";
   				}},
				{name:'pcs_pdf_recreate',index:'pcs_pdf', width:36, align:'center', hidden:($("#isEditor").val() != 'true'),
					formatter : function(value, options, rData){
					if (rData['isHistory']) 
						return "<a href='javascript:rePdf(\"" + rData['sorc_no'] + "\", \"" + rData['material_id'] + ", 1\");' >重新生成</a>";
					else
						return "<a href='javascript:rePdf(\"" + rData['sorc_no'] + "\", \"" + rData['material_id'] + "\");' >生成</a>";
   				}}
			],
			rowNum: 20,
			toppager: false,
			pager: "#exd_listpager",
			viewrecords: true,
			hidegrid : false,
			ondblClickRow : function(rid, iRow, iCol, e) {
				var data = $("#exd_list").getRowData(rid);
				var material_id = data["material_id"];
				if ($("#isEditor").val() != 'true' && $("#isEditor").val() != 'line') {
					showPcsDetail(material_id, true, true);
				} else {
					//V2
					showPcsDetailLeader(material_id, true, true);
				}
			},
			caption: "完成维修一览",
			gridview: true, // Speed up
			pagerpos: 'right',
			pgbuttons: true,
			pginput: false,
			recordpos: 'left',
			viewsortcols : [true,'vertical',true],
			gridComplete: function(){}
		});	
	}
};