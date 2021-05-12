<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page language="java" pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" isELIgnored="false"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<base href=".">
<style>
</style>
<script>
var rent_listdata=[];

var getRentListdata = function(){
	// Ajax提交
	$.ajax({
		beforeSend : ajaxRequestType,
		async : true,
		url : 'device_jig_loan.do?method=getRentListdata',
		cache : false,
		data : null,
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : function(xhrobj) {
			var resInfo = $.parseJSON(xhrobj.responseText);
			rent_listdata=resInfo.rentListdata;
			var $showSpan = $("#device_rent_details > div > span");
			if (rent_listdata.length == 0) {
				$showSpan.html("当前没有借用任何设备 / 一般工具 / 治具用于动物实验用维修品的作业。请确认是否需要借用。");
			} else {
				var cntMaterials = 0, firstLoanTime = null, cntDevices = 0, cntJigs = 0;
				for (var i in rent_listdata) {
					var rent_item = rent_listdata[i];
					if (rent_item.object_type == 1) {
						cntDevices++;
					} else if (rent_item.object_type == 2) {
						cntJigs++;
					}
					if (firstLoanTime == null) {
						firstLoanTime = rent_item.on_loan_time;
					} else if (firstLoanTime > rent_item.on_loan_time) {
						firstLoanTime = rent_item.on_loan_time;
					}
					if (rent_item.omr_notifi_no) {
						var cntMaterial = rent_item.omr_notifi_no.split(" ").length;

						if (cntMaterials < cntMaterial) {
							cntMaterials = cntMaterial;
						}
					}
				}
				if (firstLoanTime == null) {
					firstLoanTime = "-";
				} else {
					// firstLoanTime = parseInt(((new Date()).getTime() - (new Date(firstLoanTime)).getTime()) / 60000);
				}
				$showSpan.html("当前已借用：<b>设备·一般工具 " + cntDevices 
					+ " 件</b>，<b>治工具 " + cntJigs + " 件。</b>已用于<b> " + cntMaterials + " </b>件维修品的作业。");
			}
		}
	});
}

var getRentDailog = function(){
	var $rent_dialog = $('#rent_dialog');
	if ($rent_dialog.length == 0) {
		$("body").append("<div id='rent_dialog'></div>");
		$rent_dialog = $('#rent_dialog');
	}
	return $rent_dialog;
}
function device_rent_detail_show() {
	var $rent_dialog = getRentDailog();
	$rent_dialog.html("<table id='rent_dj_list'></table><div id='rent_dj_list_pager'/>");

	$("#rent_dj_list").jqGrid({
		toppager : true,
		data : rent_listdata,
		width : 640,
		height : 461,
		rowheight : 23,
		datatype : "local",
		colNames : ['KEY', '分类', '品名', '型号规格/治具号', '管理编号', '原先所属工位', '开始借用时间', '已应用修理单号'],
		colModel : [{
					name : 'device_jig_loan_key', index : 'device_jig_loan_key', key:true, hidden: true
				},{
					name : 'object_type', index : 'object_type', width : 35, align : 'center', formatter: 'select', editoptions: {value:"1:设备工具;2:治具"}
				}, {
					name : 'type_name', index : 'type_name', width : 40, align : 'left'
				}, {
					name : 'model_name', index : 'model_name', width : 50, align : 'left'
				}, {
					name : 'manage_code', index : 'manage_code', width : 40, align : 'left'
				}, {
					name : 'process_code', index : 'process_code', width : 40, align : 'center'
				}, {
					name : 'on_loan_time', index : 'on_loan_time', width : 50, align : 'center', sorttype: 'date', formatter: 'date', formatoptions: {srcformat: 'Y/m/d H:i:s', newformat: 'm-d H:i'}
				}, {
					name : 'omr_notifi_no',
					index : 'omr_notifi_no',
					width : 75
				}],
		rowNum : 20,
		toppager : false,
		pager : "#rent_dj_list_pager",
		multiselect: true,
		viewrecords : true,
		hidegrid : false,
		caption : null,
		gridview : true, // Speed up
		pagerpos : 'right',
		pgbuttons : true,
		pginput : false,
		recordpos : 'left',
		viewsortcols : [true, 'vertical', true],
		gridComplete : function() {
		}
	});
	
	$rent_dialog.dialog({
		modal : true,
		resizable:false,
		width : 'auto',
		title : "借用设备工具清单明细",
		closeOnEscape: false,
		buttons : {"清洗返还": function(){
			var rowids = $("#rent_dj_list").jqGrid("getGridParam", "selarrrow");

			var postData = {device_jig_loan_key : rowids.toString()};

			// Ajax提交
			$.ajax({
				beforeSend : ajaxRequestType,
				async : false,
				url : 'device_jig_loan.do?method=doRevent',
				cache : false,
				data : postData,
				type : "post",
				dataType : "json",
				success : ajaxSuccessCheck,
				error : ajaxError,
				complete : function(xhrobj) {
					$rent_dialog.dialog("close");
					getRentListdata();
				}
			})
		},
		"关闭": function(){$rent_dialog.dialog("close")}}
	});
}

function rent_more_filter() {
	var checkD = $("#rent_dj_cb_d").is(":checked");
	var checkJ = $("#rent_dj_cb_j").is(":checked");

	var filterText = $("#rent_dj_filter").val();
console.log(filterText);
	var filtedList = [];

	if (checkD && checkJ && !filterText) {
		filtedList = more_listdata;
	} else {
		for (var iMore in more_listdata) {
			var more = more_listdata[iMore];
			if (more.object_type == 1 && !checkD) {
				continue;
			}
			if (more.object_type == 2 && !checkJ) {
				continue;
			}
			if (filterText) {
				if ((more.type_name && more.type_name.indexOf(filterText) >= 0) 
					|| (more.model_name && more.model_name.indexOf(filterText) >= 0)
					|| (more.jig_no && more.jig_no.indexOf(filterText) >= 0) 
					|| (more.manage_code && more.manage_code.indexOf(filterText) >= 0)) {
					 filtedList.push(more);
				}
			} else {
				filtedList.push(more);
			}
		}
	}

	$("#rent_dj_list")
		.jqGrid().clearGridData();
	$("#rent_dj_list").jqGrid('setGridParam',{data:filtedList})
		.jqGrid('showCol', [checkD ? 'model_name' : 'jig_no'])
		.jqGrid('hideCol', [checkD ? 'jig_no' : 'model_name'])
		.trigger("reloadGrid", [{current:false}]);
}

var more_listdata = {};

function device_rent_more() {
	var $rent_dialog = getRentDailog();
	$rent_dialog.html("");
	$rent_dialog.html("<div><input type='text' id='rent_dj_filter' style='margin-right:10px;'>" +
		"<input type='radio' name='rent_dj_cb' id='rent_dj_cb_d'><label for='rent_dj_cb_d'>设备工具</label><input type='radio' name='rent_dj_cb' id='rent_dj_cb_j' checked><label for='rent_dj_cb_j'>治具</label></div>" +
		"<table id='rent_dj_list'></table><div id='rent_dj_list_pager'/>");
	$rent_dialog.find("input:radio").button().click(rent_more_filter);
	$rent_dialog.find("input:text").bind("keyup", rent_more_filter);

	// Ajax提交
	$.ajax({
		beforeSend : ajaxRequestType,
		async : false,
		url : 'device_jig_loan.do?method=getMore',
		cache : false,
		data : null,
		type : "post",
		dataType : "json",
		success : ajaxSuccessCheck,
		error : ajaxError,
		complete : function(xhrobj) {
			var resInfo = $.parseJSON(xhrobj.responseText);
			more_listdata = resInfo.moreListdata;
			device_rent_more_shown($rent_dialog);
		}
	})
}

function device_rent_more_shown($rent_dialog) {

	$("#rent_dj_list").jqGrid({
		toppager : true,
		data : more_listdata,
		width : 640,
		height : 461,
		rowheight : 23,
		datatype : "local",
		colNames : ['manage_id', '分类', '品名', '型号规格', '治具号', '管理编号', '所属工位', '点检状态'],
		colModel : [{
					name : 'manage_id', index : 'manage_id', hidden: true
				},{
					name : 'object_type', index : 'object_type', width : 35, align : 'center', hidden: true
				}, {
					name : 'type_name', index : 'type_name', width : 75, align : 'left'
				}, {
					name : 'model_name', index : 'model_name', width : 50, align : 'left'
				}, {
					name : 'jig_no', index : 'jig_no', width : 50, align : 'left', hidden: true
				}, {
					name : 'manage_code', index : 'manage_code', width : 40, align : 'left'
				}, {
					name : 'process_code', index : 'process_code', width : 40, align : 'center'
				}, {
					name : 'check_status',
					index : 'check_status',
					width : 40
				}],
		rowNum : 100,
		toppager : false,
		pager : "#rent_dj_list_pager",
		multiselect: true,
		viewrecords : true,
		hidegrid : false,
		caption : null,
		gridview : true, // Speed up
		pagerpos : 'right',
		pgbuttons : true,
		pginput : false,
		recordpos : 'left',
		viewsortcols : [true, 'vertical', true],
		gridComplete : function() {
			$("#rent_dj_list").find("td:contains('WAIT')").css({"backgroundColor": "gray","color":"white"});
		}
	});

	$rent_dialog.dialog({
		modal : true,
		resizable:false,
		width : 'auto',
		title : "追加借用设备工具",
		closeOnEscape: false,
		buttons : {"借用": function(){
			var rowids = $("#rent_dj_list").jqGrid("getGridParam", "selarrrow");

			var devices = [];
			var jigs = [];

			for (var i in rowids) {
				var rowdata = $("#rent_dj_list").getRowData(rowids[i]);
				if (rowdata.check_status === "WAIT") {
					errorPop("选择的设备工具或治具存在未点检的。");
					return;
				}
				if (rowdata.object_type == 1) {
					devices.push(rowdata.manage_id.toString());
				} else if (rowdata.object_type == 2) {
					jigs.push(rowdata.manage_id.toString());
				}
			}

			if (devices.length == 0 && jigs.length == 0) {
				errorPop("未选取任何要借用的设备工具或治具。");
				return;
			}

			var postData = {manage_id : "{\"devices\":\"" + devices.toString() + "\",\"jigs\":\"" + jigs.toString() + "\"}"};
			
			// Ajax提交
			$.ajax({
				beforeSend : ajaxRequestType,
				async : false,
				url : 'device_jig_loan.do?method=doLoan',
				cache : false,
				data : postData,
				type : "post",
				dataType : "json",
				success : ajaxSuccessCheck,
				error : ajaxError,
				complete : function(xhrobj) {
					$rent_dialog.dialog("close");
					getRentListdata();
				}
			})
		},
		"关闭": function(){$rent_dialog.dialog("close")}}
	});
	rent_more_filter();
}

getRentListdata();

</script>

<div id="device_rent_area">
	<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser">
		<span class="areatitle">借用设备工具清单</span>
	</div>
	<div id="device_rent_details" class="ui-widget-content">
		<div style="padding:2px;display: flow-root;">
			<span>
			
			</span>
			<input type="button" value="详细" class="ui-button" onclick="device_rent_detail_show();">
			<input type="button" class="ui-button" id="anml_rent_button" value="追加借用" style="float: right; right: 2px;" onclick="device_rent_more();">
		</div>
	</div>
</div>
<div class="clear areaencloser"></div>