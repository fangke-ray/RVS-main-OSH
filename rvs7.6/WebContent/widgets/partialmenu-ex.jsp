<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page language="java" pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" import="java.util.Map" isELIgnored="false"%>
<html><head><meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

</head>
<body>
<% Map<String, Boolean> links = (Map<String, Boolean>) request.getAttribute("menuLinks"); %>
<div class="dwidth-left">
<h3 class="ui-accordion-header ui-helper-reset ui-state-default ui-corner-top ui-accordion-icons" tabindex="0"><span class="ui-accordion-header-icon ui-icon ui-icon-triangle-1-s"></span>零件基础数据管理</h3>
			<div class="ui-accordion-content ui-helper-reset ui-widget-content">
				<a target="_parent" href="partialManage.do">零件一览表</a><br>
				<a target="_parent" href="partial_position.do">零件BOM与定位管理</a><br>
				<a target="_parent" href="partial_bom.do">零件RANK信息管理</a><br>
<% if(links.get("partial_admin")){ %>
				<a target="_parent" href="partial_waste_modify_history.do">零件废改增履历</a>
<% } %>
		    </div>
</div>
<div class="dwidth-left">
<h3 class="ui-accordion-header ui-helper-reset ui-state-default ui-corner-top ui-accordion-icons" tabindex="0"><span class="ui-accordion-header-icon ui-icon ui-icon-triangle-1-s"></span>零件入出库</h3>
		    <div class="ui-accordion-content ui-helper-reset ui-widget-content">
	<% if(links.get("analysis")){ %>
		        <a target="_parent" href="partial_supply.do">零件补充</a><br>
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
		        <a target="_parent" href="materialPartial.do">零件订购·签收管理</a><br>
		        <a target="_parent" href="materialPartInstruct.do?from=qf">工作指示单</a>
		    </div>
</div>
<% if(links.get("analysis")){ %>
<div class="dwidth-left">
<h3 class="ui-accordion-header ui-helper-reset ui-state-default ui-corner-top ui-accordion-icons" tabindex="0"><span class="ui-accordion-header-icon ui-icon ui-icon-triangle-1-s"></span>零件辅助功能</h3>
			<div class="ui-accordion-content ui-helper-reset ui-widget-content">
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
</div>
<% } %>
<% if(links.get("loss")){ %>
<div class="dwidth-left">
<h3 class="ui-accordion-header ui-helper-reset ui-state-default ui-corner-top ui-accordion-icons" tabindex="0"><span class="ui-accordion-header-icon ui-icon ui-icon-triangle-1-s"></span>损金管理</h3>
			<div class="ui-accordion-content ui-helper-reset ui-widget-content">
				<a target="_parent" href="sorc_loss.do">损金明细</a><br>
				<a target="_parent" href="bad_loss_summary.do">损金总计</a><br>
			</div>
</div>
<% } %>

<div class="dwidth-left">
<h3 class="ui-accordion-header ui-helper-reset ui-state-default ui-corner-top ui-accordion-icons" tabindex="0"><span class="ui-accordion-header-icon ui-icon ui-icon-triangle-1-s"></span>消耗品/组件管理</h3>
		    <div class="ui-accordion-content ui-helper-reset ui-widget-content">
		        <a target="_parent" href="consumable_list.do">消耗品仓库库存一览</a><br>
		        <a target="_parent" href="consumable_manage.do">消耗品仓库管理记录</a><br>
		        <a target="_parent" href="consumable_online.do">消耗品在线一览</a><br>
		        <a target="_parent" href="consumable_supply_record.do">消耗品发放记录</a><br>
		        <a target="_parent" href="component_manage.do">NS 组件库存管理</a><br>
		        <a target="_parent" href="snouts.do?from=qf">D/E 组件库存管理</a>
		    </div>
</div>

</body></html>