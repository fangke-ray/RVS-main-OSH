<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page language="java" pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" import="java.util.Map" isELIgnored="false"%>
<html><head><meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

</head>
<script>
$(function() {
	$("#modelmenuarea a").attr("target", "_parent");
	var accordion_idx = $("#accordion h3").index($("#accordion h3:contains('" + $("#linkto").val() + "')"));
	if (accordion_idx < 0) accordion_idx = 0;
	$("#accordion" ).accordion({active: accordion_idx});
	$('#datepicker').datepicker({
		inline: true,
		width: 224
	});
	$('#datepicker div.ui-datepicker-inline').css("width","219px");

	$("#menucontainner").hide();
});
</script>
<body>
<% Map<String, Boolean> links = (Map<String, Boolean>) request.getAttribute("menuLinks"); %>
<div id="modelmenuarea">
	<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser dwidth-left">
		<span class="areatitle">功能菜单</span>
		<input type="hidden" id="linkto" value="${linkto}">
	</div>
	<div class="ui-widget-content dwidth-left">
		<div id="accordion">
			<h3 style="padding-left:30px;">零件基础数据管理</h3>
			<div>
				<a target="_parent" href="partialManage.do">零件一览表</a><br>
				<a target="_parent" href="partial_position.do">零件BOM与定位管理</a><br>
				<a target="_parent" href="partial_bom.do">零件RANK信息管理</a><br>
<% if(links.get("partial_admin")){ %>
				<a target="_parent" href="partial_waste_modify_history.do">零件废改增履历</a><br>
				<a target="_parent" href="premake_partial.do">预制零件设定</a><br>
<% } %>
		    </div>
		    <h3 style="padding-left:30px;">零件入出库</h3>
		    <div>
	<% if(links.get("analysis")){ %>
		        <a target="_parent" href="partial_supply.do">零件补充</a><br>
		        <a target="_parent" href="partial_storage.do">零件库存</a><br>
				<a target="_parent" href="partial_warehouse.do">零件入库</a><br>
		        <a target="_parent" href="partial_order.do">零件订购</a><br>
		        <!--a target="_parent" href="partial_assign.do">零件到货导入</a><br-->
		        <a target="_parent" href="partial_distrubute.do">零件发放</a><br>
		        <a target="_parent" href="partial_recept.do">零件签收</a><br>
	<% } else if(links.get("fact")){ %>
				<a target="_parent" href="partial_warehouse.do">零件入库</a><br>
		        <a target="_parent" href="partial_order.do">零件订购</a><br>
		        <!--a target="_parent" href="partial_assign.do">零件到货导入</a><br-->
		        <a target="_parent" href="partial_distrubute.do">零件发放</a><br>
	<% } else if(links.get("line")){ %>
		        <a target="_parent" href="partial_recept.do">零件签收</a><br>
	<% } %>
		        <a target="_parent" href="materialPartial.do">零件订购·签收管理</a>
		    </div>
<% if(links.get("analysis")){ %>
		    <h3 style="padding-left:30px;">零件辅助功能</h3>
			<div>
				<a target="_parent" href="partial_order_record.do">零件订购现状</a><br>
				<a target="_parent" href="partial_monitor.do">零件基准设定监控</a><br>
				<a target="_parent" href="echelon_allocate.do">梯队划分</a><br>
				<a target="_parent" href="levelmodel_leeds.do">型号等级拉动台数设置</a><br>
				<a target="_parent" href="partial_base_line_value.do">零件基准值设置</a><br>
				<!--a target="_parent" href="partial_filing.do">零件周报记录</a><br>
				<a target="_parent" href="user_define_codes.do">用户参数设定</a><br-->
				<a target="_parent" href="partial_order_manage.do">零件订购维护</a><br>
				<a target="_parent" href="partial_base_value_adjust_history.do">零件基准调整履历</a><br>
				<a target="_parent" href="model_level_set_history.do">型号终止维修履历</a>
			</div>
<% } %>
<% if(links.get("loss")){ %>
		    <h3 style="padding-left:30px;">损金管理</h3>
			<div>
				<a target="_parent" href="sorc_loss.do">损金明细</a><br>
				<a target="_parent" href="bad_loss_summary.do">损金总计</a><br>
			</div>
<% } %>

		    <h3 style="padding-left:30px;">消耗品/组件管理</h3>
		    <div>
		        <a target="_parent" href="consumable_list.do">消耗品仓库库存一览</a><br>
		        <a target="_parent" href="consumable_manage.do">消耗品仓库管理记录</a><br>
		        <a target="_parent" href="consumable_online.do">消耗品在线一览</a><br>
		        <a target="_parent" href="consumable_supply_record.do">消耗品发放记录</a><br>
		        <a target="_parent" href="component_manage.do">NS组件库存管理</a>
		    </div>

		</div>
	</div>
	<div id="datepicker" class="dwidth-left"></div>
	<div class="clear areaencloser"></div>
</div>



</body></html>