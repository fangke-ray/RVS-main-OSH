/** 一览数据对象 */
var listdata = {};
var quotation_listdata = {};
/** 服务器处理路径 */
var servicePath = "shipping.do";

var lOptions = {};
var ocmOptions = {};

function expeditedText(scheduled_expedited) {
	if (scheduled_expedited === '2') return "直送快速";
	if (scheduled_expedited === '1') return "加急";
	return "";
}

var doFinish = function(type) {	
	$("#uploadform").validate({
		rules : {											
			bound_out_ocm : {
				length:2
			},											
			package_no : {
				required :true
			}
		}
	});
	if ($("#uploadform").valid()) {
		var data = {
			"bound_out_ocm" : $("#show_bound_out_ocm_select").val(),
			"package_no" : $("#show_package_no").val()
		};	
	// Ajax提交
	$.ajax({
		beforeSend : ajaxRequestType,
		async : false,
		url : servicePath + '?method=dofinish',
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
			doShippingInit();
		}
	});
	}	
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
			lOptions = resInfo.lOptions;
			ocmOptions = resInfo.oOptions;
			load_list(resInfo.waitings);
			acceptted_list(resInfo.finished);

			// 存在进行中作业的时候
			if(resInfo.workstauts == 1) {
				treatStart(resInfo);
			}
		}
	} catch (e) {
		alert("name: " + e.name + " message: " + e.message + " lineNumber: "
				+ e.lineNumber + " fileName: " + e.fileName);
	};
};

var doShippingInit=function(){
	// Ajax提交
	$.ajax({
		beforeSend : ajaxRequestType,
		async : false,
		url : servicePath + '?method=jsinit',
		cache : false,
		data : {},
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : doInit_ajaxSuccess
	});
};

$(function() {
	$("input.ui-button").button();
	$("a.areacloser").hover(
		function (){$(this).addClass("ui-state-hover");},
		function (){$(this).removeClass("ui-state-hover");}
	);

	$("#editarea").hide();
	$("#detailarea").hide();

	// 输入框触发，配合浏览器
	$("#scanner_inputer").keypress(function(){
	if (this.value.length === 11) {
		doStart();
	}
	});
	$("#scanner_inputer").keyup(function(){
	if (this.value.length >= 11) {
		doStart();
	}
	});	

	$("#shipbutton").click(function() {
		afObj.applyProcess(132, this, doFinish, arguments);
	});

	$("#reportbutton").click(makeReport);

	$("#tcWarehouseButton").click(function() {
		afObj.applyProcess(131, this, showWarehousingPlan, arguments);
	});

	doShippingInit();
});

var makeReport = function() {
	// Ajax提交
	$.ajax({
		beforeSend : ajaxRequestType,
		async : false,
		url : 'position_panel.do?method=makeReport',
		cache : false,
		data : {},
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : function() {
			infoPop("<span>生成报表的指示已发送，请到<a href='daily_work_sheet.do'>工作记录表画面</a>确认！</span>");
		}
	});	
}

/*
* Ajax通信成功時の処理
*/
function search_handleComplete(Xhrobj, textStatus) {
};

function load_list(listdata){
	if ($("#gbox_uld_list").length > 0 || listdata.length === 0) {
		$("#uld_list").jqGrid().clearGridData();
		$("#uld_list").jqGrid('setGridParam',{data:listdata}).trigger("reloadGrid", [{current:false}]);
	} else {
		$("#uld_list").jqGrid({
			toppager : true,
			data:listdata,
			//height: 461,
			width: 992,
			rowheight: 23,
			datatype: "local",
			colNames:['受理时间','同意时间','完成日期','返还','修理单号', 'ESAS No.', '型号 ID', '型号' , '机身号','委托处', '等级', '加急', '通箱位置'],
			colModel:[{
						name : 'reception_time',
						index : 'reception_time',
						width : 35,
						align : 'center', 
						sorttype: 'date', formatter: 'date', formatoptions: {srcformat: 'Y/m/d H:i:s', newformat: 'm-d'}
					}, {
						name : 'agreed_date',
						index : 'agreed_date',
						width : 35,
						align : 'center', 
						sorttype: 'date', formatter: 'date', formatoptions: {srcformat: 'Y/m/d', newformat: 'm-d'}
					}, {
						name : 'finish_time',
						index : 'finish_time',
						width : 65,
						align : 'center', 
						sorttype: 'date',
						formatter: function(value,r,rData){
							if(!value){
								return '';
							}		
								if(rData.break_back_flg==2){
									return mdTextOfDate(new Date(value))+' 未修理返还';
								}	
								return mdTextOfDate(new Date(value))+' 修理完成';
						}
					},{
						name : 'break_back_flg',
						index : 'break_back_flg',
						width : 65,
						align : 'center',
						hidden : true		
					},
				{name:'sorc_no',index:'sorc_no', width:105},
				{name:'esas_no',index:'esas_no', width:50, align:'center'},
				{name:'model_id',index:'model_id', hidden:true},
				{name:'model_name',index:'model_id', width:125},
				{name:'serial_no',index:'serial_no', width:50, align:'center'},
				{
					name : 'ocm',
					index : 'ocm',
					width : 65, formatter: 'select', editoptions:{value: ocmOptions}
				}, {
					name : 'level',
					index : 'level',
					width : 35,
					align : 'center', formatter: 'select', editoptions:{value: lOptions}
				}, {
					name : 'scheduled_expedited',
					index : 'scheduled_expedited',
					width : 35,
					align : 'center', formatter: 'select', editoptions:{value: "0:;1:加急;2:直送快速"}
				}, {
					name : 'wip_location',
					index : 'wip_location',
					width : 35,
					align : 'center', formatter:  function(value,r,rData){
						if(!value){
							return '(已取出)';
						}		
						return value;
					}
				}
			],
			rowNum: 20,
			toppager: false,
			pager: "#uld_listpager",
			viewrecords: true,
			caption: "待出货维修品一览",
			gridview: true, // Speed up
			pagerpos: 'right',
			pgbuttons: true,
			pginput: false,
			recordpos: 'left',
			viewsortcols : [true,'vertical',true],
			gridComplete: function() {
				var ids = jQuery("#uld_list").jqGrid('getDataIDs');

				for(var i=0;i < ids.length;i++){
					var cl = ids[i];
					ce = "<input style='height:22px;width:20px;' type='button' value='C' onclick=\"jQuery('#uld_list').restoreRow('"+cl+"');\" />"; 
					jQuery("#uld_list").jqGrid('setRowData',ids[i],{act:ce});
				}
			}
		});
		//$("#uld_list").gridResize({minWidth:1248,maxWidth:1248,minHeight:200, maxHeight:900});
	}

};

function acceptted_list(quotation_listdata){
	if ($("#gbox_exd_list").length > 0) {
		$("#exd_list").jqGrid().clearGridData();
		$("#exd_list").jqGrid('setGridParam',{data:quotation_listdata}).trigger("reloadGrid", [{current:false}]);
	} else {
		$("#exd_list").jqGrid({
			toppager : true,
			data:quotation_listdata,
			//height: 461,
			width: 992,
			rowheight: 23,
			datatype: "local",
			colNames:['受理时间','同意时间','总组完成时间','出货时间', '修理单号', 'ESAS No.', '型号 ID', '型号' , '机身号', '委托处', '等级'],
			colModel:[
				{
						name : 'reception_time',
						index : 'reception_time',
						width : 35,
						align : 'center', 
						sorttype: 'date', formatter: 'date', formatoptions: {srcformat: 'Y/m/d H:i:s', newformat: 'm-d'}
					}, {
						name : 'agreed_date',
						index : 'agreed_date',
						width : 35,
						align : 'center', 
						sorttype: 'date', formatter: 'date', formatoptions: {srcformat: 'Y/m/d', newformat: 'm-d'}
					}, {
						name : 'finish_time',
						index : 'finish_time',
						width : 65,
						align : 'center', 
						sorttype: 'date', formatter: 'date', formatoptions: {srcformat: 'Y/m/d', newformat: 'm-d'}
					},
				{name:'quotation_time',index:'quotation_time', width:65, align:'center',
						sorttype: 'date', formatter: 'date', formatoptions: {srcformat: 'Y/m/d H:i:s', newformat: 'H:i'}
				},
				{name:'sorc_no',index:'sorc_no', width:105},
				{name:'esas_no',index:'esas_no', width:50, align:'center'},
				{name:'model_id',index:'model_id', hidden:true},
				{name:'model_name',index:'model_id', width:125},
				{name:'serial_no',index:'serial_no', width:50, align:'center'},
				{
					name : 'ocm',
					index : 'ocm',
					width : 65, formatter: 'select', editoptions:{value: ocmOptions}
				}, {
					name : 'level',
					index : 'level',
					width : 35,
					align : 'center', formatter: 'select', editoptions:{value: lOptions}
				}
			],
			rowNum: 20,
			toppager: false,
			pager: "#exd_listpager",
			viewrecords: true,
			hidegrid : false,
			caption: "今日出货完成一览",
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

var treatStart = function(resInfo) {
	$("#scanner_inputer").attr("value", "");
	$("#scanner_container").hide();
	$("#material_details").show();
	$("#pcsarea").show();
	var mform = resInfo.mform;
	$("#show_model_name").text(mform.model_name);
	$("#show_serial_no").text(mform.serial_no);
	$("#show_sorc_no").text(mform.sorc_no);
	$("#show_esas_no").text(mform.esas_no);
	$("#show_agreed_date").text(mform.agreed_date);
	$("#show_finish_time").text(mform.finish_time);
	$("#show_level").text(mform.levelName);
	$("#show_scheduled_expedited").text(expeditedText(mform.scheduled_expedited));

	if (mform.direct_flg == 1) {
		$("#show_bound_out_ocm").hide();
		$("#show_bound_out_ocm_select").show();
		$("#show_bound_out_ocm_select").val(mform.bound_out_ocm || mform.ocm);
		$("#show_bound_out_ocm_select").next(".select2Buttons").remove();
		$("#show_bound_out_ocm_select").select2Buttons();
	} else {
		$("#show_bound_out_ocm").show();
		$("#show_bound_out_ocm").text($("#show_bound_out_ocm_select").find("option[value="+ mform.bound_out_ocm +"]").text());
		$("#show_bound_out_ocm_select").next(".select2Buttons").remove();
		$("#show_bound_out_ocm_select").val("").hide();
	}
//	$("#show_bound_out_ocm_select").val(mform.bound_out_ocm || mform.ocm).trigger("change");

	$("#show_package_no").val("原箱");
};

var doStart_ajaxSuccess=function(xhrobj){
	var resInfo = null;
	try {
		// 以Object形式读取JSON
		eval('resInfo =' + xhrobj.responseText);

		if (resInfo.errors.length > 0) {
			// 共通出错信息框
			treatBackMessages(null, resInfo.errors);
		} else {
			treatStart(resInfo);
		}
	} catch (e) {
		alert("name: " + e.name + " message: " + e.message + " lineNumber: "
				+ e.lineNumber + " fileName: " + e.fileName);
	};
};

var doStart=function(){
	var data = {
		material_id : $("#scanner_inputer").val()
	}

	$("#scanner_inputer").attr("value", "");

	afObj.applyProcess(132, this, doStartPost, [data]);
};

var doStartPost = function(data) {
	// Ajax提交
	$.ajax({
		beforeSend : ajaxRequestType,
		async : false,
		url : servicePath + '?method=doscan',
		cache : false,
		data : data,
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : doStart_ajaxSuccess
	});
}
