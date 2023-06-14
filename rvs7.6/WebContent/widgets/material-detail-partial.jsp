<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
	String from = (String) request.getAttribute("from");
%>

<div id="material_detail_orderarea" class="dwidth-middle" style="margin-top:22px;margin-left:9px;margin-bottom:22px;">
	<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser dwidth-middle">
		<span class="areatitle">修理品零件订购信息</span>
	</div>
	<div class="ui-widget-content dwidth-middle">
		<table class="condform">
			<tbody>
			<tr>
				<td class="ui-state-default td-title">零件BO</td>
				<td class="td-content">
					<label id="label_bo_flg">${partialForm.bo_flg}</label>
				</td>
				<td class="ui-state-default td-title">零件订购日</td>
				<td class="td-content">
<%
if ("partial".equals(from)) {
%>
					<input type="text" id="edit_order_date" value='${partialForm.order_date}' readonly>
<%
} else {
%>
					<label>${partialForm.order_date}</label>
<%
}
%>
				</td>
			</tr>
			<tr>
				<td class="ui-state-default td-title">入库预定日</td>
				<td class="td-content">
<%
if ("partial".equals(from)) {
%>
					<input type="text" id="edit_arrival_plan_date" value='${partialForm.arrival_plan_date}' readonly>
<%
} else {
%>
					<label>${partialForm.arrival_plan_date}</label>
<%
}
%>
				</td>
				<td class="ui-state-default td-title">零件到货日</td>
				<td class="td-content">
					<label id="label_arrival_date">${partialForm.arrival_date}</label>
				</td>
			</tr>

			<tr id="tr_bo_contents">
				<td class="ui-state-default td-title">零件缺品原因</td>
				<td class="td-content" colspan="3">
					<textarea id="edit_bo_contents" style="width:600px;height:80px;"
<%= ("data".equals(from) || "partial".equals(from)) ? "" : " readonly disabled"%>
					>${partialForm.bo_contents}</textarea>
				</td>
			</tr>
		</tbody>
		</table>
	</div>

</div>

<div id="material_detail_partialarea" class="dwidth-middle" style="margin-top:22px;margin-left:9px;margin-bottom:22px;">
	<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser dwidth-middle">
		<span class="areatitle">修理品使用零件一览</span>
	</div>
	<div class="ui-widget-content dwidth-middle">
		<div id="distributions"
		<%= ("data".equals(from) || "partial".equals(from)) ? " style='display:none;'" : ""%>
		>
			<input type="radio"  name="distributions" class="ui-button ui-corner-up" id="quotation_use_radio" value="11">
			<label for="quotation_use_radio">报价工程</label>

			<input type="radio"  name="distributions" class="ui-button ui-corner-up" id="decompose_use_radio" value="12" checked>
			<label for="decompose_use_radio">分解工程</label>

			<input type="radio" name="distributions" class="ui-button ui-corner-up" id="ns_use_radio" value="13">
			<label for="ns_use_radio">ＮＳ工程</label>

			<input type="radio" name="distributions" class="ui-button ui-corner-up" id="compose_use_radio" value="14">
			<label for="compose_use_radio">总组工程</label>
		</div>

		<table id="exd_list"></table>
		<div id="exd_listpager"></div>
	</div>
</div>
<script type="text/javascript">
$(function() {
	$("#material_detail_orderarea input[readonly]").datepicker({
		showButtonPanel:true,
		dateFormat: "yy/mm/dd",
		currentText: "今天"
	});

	$("#distributions").buttonset();

	var room = {};

	var eapdEditable = false;

	//TODO JSP
	eapdEditable = ($("#distributions").is(":hidden"));

	var findit = function(line_id){
		var postData = {
			"line_id": line_id
		}

		$.ajax({
			url: "materialPartial.do?method=searchMaterialPartialDetail",
			data: postData,
			type : "post",
			complete : function(xhrobj, textStatus){
				search_handleComplete(xhrobj, textStatus, line_id);
			}
		});
	};
	
	/*
	 * Ajax通信成功的处理
	 */
	function search_handleComplete(xhrobj, textStatus, line_id) {
		var resInfo = null;
		try {
			// 以Object形式读取JSON
			eval('resInfo =' + xhrobj.responseText);
		} catch (e) {
			alert("name: " + e.name + " message: " + e.message + " lineNumber: "
					+ e.lineNumber + " fileName: " + e.fileName);
		};
				
		$("#exd_list").jqGrid().clearGridData();
		$("#exd_list").jqGrid('setGridParam', {data : resInfo.list}).trigger("reloadGrid", [{current : false}]);
		room[line_id] = resInfo.list;
	};

	function initGrid(){

		$("#exd_list").jqGrid({
			data:[],
			height: 200,
			width: 768,
			rowheight: 23,
			datatype: "local",
			colNames:['','零件编号','零件名称','数量','工位','消耗品<br>库存','使用日期','入库预定日'],
			colModel:[
				{name:'material_partial_detail_key',index:'material_partial_detail_key', hidden:true},
				{name : 'partial_code',index : 'partial_code',width : 60,align : 'left'},
				{name : 'partial_name',index : 'partial_name',width : 300,align : 'left'},
				{name : 'quantity',index : 'quantity',width : 60,align : 'right', formatter : function(value, options, rData) {
					if (rData["quantity"]) {
						return (rData["cur_quantity"] || "0") + "/" + rData["quantity"];
					}
					return (rData["cur_quantity"] || "0") + "(" + (rData["quantity"] || '消耗品') + ")";
				}},
				{name :'process_code',index:'process_code',width:60,align:'center' },
				{name :'available_inventory',index:'available_inventory',width:50,align:'center', sorttype : 'integer', hidden: true},
				{name :'recent_signin_time',index:'recent_signin_time',width:100,align:'center', sorttype : 'date',
					formatter : 'date',
					formatoptions : {
						srcformat : 'Y/m/d H:i:s',
						newformat : 'm-d'
					}},
				{name :'arrival_plan_date',index:'arrival_plan_date',width:100,align:'center', sorttype : 'date', formatter : function(value, options, rData) {
		       		if (eapdEditable) {
		       			if (rData["status"] == 3 || rData["status"] == 1) {
		       				if (value == null || value=="9999/12/31" || value=="9999-12-31") {
		       				return "<input type='text' class='fix_arrival_plan_date grid_input' value='' org='' readonly>"
		       				} else {
		       				return "<input type='text' class='fix_arrival_plan_date grid_input' value='"+ (value || "") +"' org='"+ (value || "") +"' readonly>"
		       				}
		       			} else if (rData["status"] == 4) {
		       				if (value == null || value=="9999/12/31" || value=="9999-12-31") {
		       					return "";
		       				} else {
		       					var d = new Date(value);
								return mdTextOfDate(d);
		       				}
		       			}
		       		} else {
			       		if (rData["status"] == 3 && (value == null || value=="9999/12/31" || value=="9999-12-31")) {
			       			return "未定";
			       		}
			       		if (rData["status"] == 5) {
			       			return "消耗品替代";
			       		}
			       		if (rData["status"] == 6) {
			       			return "组件替代";
			       		}
			       		if (value) {
							var d = new Date(value);
							return mdTextOfDate(d);
						}
		       		}
		       		return "";
				}}
			],
			rowNum: 100,
			toppager: false,
			pager: "#exd_listpager",
			viewrecords: true,
			//multiselect: true,
			hidegrid : false,
			gridview: true,
			pagerpos: 'right',
			pgbuttons: true,			
			pginput: false,
			recordpos: 'left',
			viewsortcols : [true,'vertical',true],
			gridComplete:function(){
				// 得到显示到界面的id集合
				var IDS = $("#exd_list").getDataIDs();
				// 当前显示多少条
				var length = IDS.length;
				var $exd_list = $("#exd_list");
				for (var i = 0; i < length; i++) {
					// 从上到下获取一条信息
					var rowData = $exd_list.jqGrid('getRowData', IDS[i]);
					var quantity = rowData["quantity"];
					if (quantities) {
						var quantities = quantity.split("/");
						if (quantities.length == 2) {
							if(quantities[1]>quantities[0]){
								$exd_list.find("tr#" + IDS[i] + " td[aria\\-describedby='exd_list_quantity']").addClass("ui-state-active");
							}
						}
					}
				}
				if (eapdEditable) {
					$("#exd_list").find(".fix_arrival_plan_date").datepicker({
						showButtonPanel:true,
						dateFormat: "yy/mm/dd",
						currentText: "今天"
					});
				}
			}
		});
	}

	function initview() {
		$("input[name=distributions]").bind('click',function(){
			var line_id = this.value;
			if (room[line_id]) {
				$("#exd_list").jqGrid().clearGridData();
				$("#exd_list").jqGrid('setGridParam', {data : room[line_id]}).trigger("reloadGrid", [{current : false}]);
			} else {
				findit(line_id);
			}
		});

		initGrid();

		if (eapdEditable) {
			findit();
		} else {
			$("#decompose_use_radio").attr("checked", true).trigger("change");
			$("#decompose_use_radio").click();
		}
	}
	initview();
});
</script>