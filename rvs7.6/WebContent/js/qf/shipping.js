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

			if (resInfo.inTrolleyMaterials) {
				remapTrolley(resInfo.inTrolleyMaterials);
			}

			// 存在进行中作业的时候
			if(resInfo.workstauts == 1) {
				treatStart(resInfo);
			}
			if (resInfo.djLoaning) {
				warningConfirm(resInfo.djLoaning);
			}
		}
	} catch (e) {
		console.log("name: " + e.name + " message: " + e.message + " lineNumber: "
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

var dmLevers = {};

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

	$("#executearea .device_manage_item")
		.click(function(e){
		var item_class = $(this).attr("class");
		$("#executearea .device_manage_select").removeClass("device_manage_select");
		$(this).addClass("device_manage_select");
		if (item_class) {
			var item_classar = item_class.split(" ");
			for (var iar in item_classar) {
				if (item_classar[iar].indexOf("sty_") == 0) {
					$("#scanner_inputer").attr("class", "scanner_inputer dwidth-half")
						.addClass(item_classar[iar]);
					return;
				}
			}
		}
	});

	var $workingTd = $("#working_table tr:eq(0) > td");
	if ($workingTd.length > 2) {
		$workingTd.click(function(){
			$("#working_table td.showing").removeClass("showing");
			$(this).addClass("showing");
		});
	}

	dmLevers = $.parseJSON($("#dm_levers").val());
	for (var dmId in dmLevers) {
		var lever = dmLevers[dmId];
		var title = "上限 " + lever;
		$("#dm_" + dmId).attr({"title": title, "lever" : lever});
	}

	$(".finishbutton").click(function(){
		var $finishbutton = $(this);
		var $td = $finishbutton.closest("td");
		var forList = $td.attr("for");
		var $targetTd = $("#working_table tr:eq(0) td[for=" + forList + "]");
		if ($targetTd.length == 0 || $targetTd.find(".waiting").length == 0) {
			errorPop("此推车是空的。");
			return;
		}
		warningConfirm("是否要完成以下设备的作业：" + $("#working_table tr:eq(0) td[for=" + forList + "] .w_group").text() + "？",
			function(){
				afObj.applyProcess(132, this, doFinishForTrolley, [forList]);
			}
		);
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
	if ($("#gbox_uld_list").length > 0) {
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
			colNames:['受理时间','同意时间','完成日期','返还','修理单号', '出货单制作', '型号 ID', '型号' , '机身号','委托处', '等级', '加急', '通箱位置'],
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
						width : 70,
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
				{name:'sorc_no',index:'sorc_no', width:100},
				{name:'filing_time',index:'filing_time', width:50, align:'center',sorttype: 'date', formatter: 'date', formatoptions: {srcformat: 'Y/m/d H:i:s', newformat: 'm-d'}},
				{name:'model_id',index:'model_id', hidden:true},
				{
					name : 'model_name',
					index : 'model_name',
					width : 125, 
					formatter : function(value, options, rData){
						if (rData['scheduled_expedited'] && rData['scheduled_expedited'] >= 4) {
							return "<span class='top_speed'>" + rData['model_name'] + "</span>";
						} else {
							return rData['model_name'];
						}
					}
				},
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
					align : 'center', formatter: 'select', editoptions:{value: "0:;1:加急;2:直送快速;3:极速修理;4:极速修理"}
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
			colNames:['受理时间','同意时间','总组完成时间','出货时间', '修理单号', '型号 ID', '型号' , '机身号', '委托处', '等级'],
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
			if (resInfo.inTrolleyMaterials) {
				remapTrolley(resInfo.inTrolleyMaterials);
			} else {
				treatStart(resInfo);
			}
		}
	} catch (e) {
		alert("name: " + e.name + " message: " + e.message + " lineNumber: "
				+ e.lineNumber + " fileName: " + e.fileName);
	};
};

var doStart=function(){
	var scanClass = $("#scanner_inputer").attr("class");

	var styIdx = scanClass.indexOf("sty_");
	if (styIdx < 0) {
		errorPop("请选择使用的设备。");
		$("#scanner_inputer").val("");
		return;
	}

	var devId = scanClass.substring(styIdx + 4, styIdx + 5);

//	var checkLeverRst = checkLever($("#working_table .sty_" + devId + " .workings .waiting:visible"), $("#dm_" + devId)
//		, undefined, !$(".waiting#w_" + materialId).is(":contains('光学视管')"));
//	if (checkLeverRst == -1) {
//		errorPop("当前设备容量已至上限。");
//		return;
//	}

	var data = {
		material_id : $("#scanner_inputer").val(),
		trolley_code : devId
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

var doFinishForTrolley = function(forTrolley) {
	// Ajax提交
	$.ajax({
		beforeSend : ajaxRequestType,
		async : false,
		url : servicePath + '?method=doFinishForTrolley',
		cache : false,
		data : {trolley_code : forTrolley.replace('dm_', '')},
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : function(xhrobj, textStatus) {
			doFinishForTrolley_ajaxSuccess(xhrobj, textStatus, forTrolley);
		}
	});
}

var doFinishForTrolley_ajaxSuccess = function(xhrobj, textStatus, forTrolley){
	var resInfo = $.parseJSON(xhrobj.responseText);

	if (resInfo.errors && resInfo.errors.length) {
		// 共通出错信息框
		treatBackMessages(null, resInfo.errors);
		return;
	}

	var $target = $("#working_table td[for='" + forTrolley + "']").eq(0);

	$target.children(".workings").children().hide("drop", {direction: 'right'}, function() {
		$(this).remove();
	});

	acceptted_list(resInfo.finished);
}

var remapTrolley = function(inTrolleyMaterials) {

	var waiting_htmls = {};
	for (var iworking = 0; iworking < inTrolleyMaterials.length; iworking++) {

		var waiting = inTrolleyMaterials[iworking];

		var pI = waiting.wip_location;

		if (waiting_htmls[pI] == null) {
			waiting_htmls[pI] = "";
		}

		var item_html = '<div class="waiting tube">' +
						'<div class="tube-liquid' + expeditedColor(waiting)  + '">' +
							waiting.sorc_no + ' | ' + waiting.category_name + ' | ' + waiting.model_name + ' | ' + waiting.serial_no;
		item_html +=	'</div>';
		item_html +=	'</div>';

		waiting_htmls[pI] += item_html;
	}

	var $focTd = null;
	$(".workings").each(function(idx,ele){
		var $workings = $(ele); 
		var $td = $workings.closest("td");
		
		size = setWoringArea($workings, $td, waiting_htmls);

		if (size > 0 && $focTd == null) $focTd = $td;
	});
	if ($focTd != null) {
		$focTd.trigger("click");
	}

}

var expeditedColor = function(waiting) {
	if (waiting.break_back_flg == 2) return ' tube-red';
	return ' tube-green';
}

var setWoringArea = function($workings, $td, waiting_htmls){
	var td_for = $td.attr("for");
	if (waiting_htmls != null) $workings.html(waiting_htmls[td_for.substring(3)]);

	var textBld = [];
	var checkLeverResult = checkLever($workings.find(".waiting:visible"), $("#" + td_for), textBld);

	if (textBld.length) {
		$td.find(".working_cnt").html(textBld[0]);
	}

	return $workings.find(".waiting:visible").length;
}

var checkLever = function($countItem, $device, textBld, addNonUdi) {
	var size = $countItem.length;

	var lever = $device.attr("lever");

	if (lever) {
		if (size >= parseInt(lever)) {
			if (textBld != undefined) {
				textBld.push("<font color='red'>" + size + "</font>/" + lever);
			}
			return -1;
		} else {
			if (textBld != undefined) {
				textBld.push(size + "/" + lever);
			}
			return 1;
		}
	} else {
		if (textBld != undefined) textBld.push(size);
		return 0;
	}
}