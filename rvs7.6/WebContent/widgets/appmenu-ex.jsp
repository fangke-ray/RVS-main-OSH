<!DOCTYPE html>
<%@ page language="java" pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" import="java.util.Map" isELIgnored="false"%>
<html><head><meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

</head>
<script>
if (typeof getPositionWork === "undefined") loadJs("js/common/change_position.js");
</script>
<body>
<% Map<String, Boolean> links = (Map<String, Boolean>) request.getAttribute("menuLinks"); %>

<% if(links.get("受理报价")) { %>
<div class="dwidth-left">
<h3 class="ui-accordion-header ui-helper-reset ui-state-default ui-corner-top ui-accordion-icons" tabindex="0"><span class="ui-accordion-header-icon ui-icon ui-icon-triangle-1-s"></span>受理报价</h3>
		<div class="ui-accordion-content ui-helper-reset ui-widget-content">
	<% if(links.get("acceptance")) { %>
		        ${beforePosition}
	<% } %>
	<% if(links.get("beforeline")) { %>
		        <a href="beforeLineLeader.do" title="受理报价线长界面">投线前维修对象一览</a><br>
	<% } %>
		</div>
</div>
<% } %>

<% if(links.get("在线作业")) { %>
<div class="dwidth-left">
<h3 class="ui-accordion-header ui-helper-reset ui-state-default ui-corner-top ui-accordion-icons" tabindex="0"><span class="ui-accordion-header-icon ui-icon ui-icon-triangle-1-s"></span>在线作业</h3>
		<div class="ui-accordion-content ui-helper-reset ui-widget-content">
	<% if(links.get("decomposeline")) { %>
		        <a href="lineLeader.do?line_no=12" title="分解工程线长界面">分解工程</a><br>
	<% } %>
	<% if(links.get("nsline")) { %>
		        <a href="lineLeader.do?line_no=13" title="NS工程线长界面">NS 工程</a><br>
	<% } %>
	<% if(links.get("composeline")) { %>
		        <a href="lineLeader.do?line_no=14" title="总组工程线长界面">总组工程</a><br>
	<% } %>
	<% if(links.get("inlinePosition")) { %>
		        ${inlinePosition}
	<% } %>
	<% if(links.get("composeStorage")) { %>
		        <a href="compose_storage.do">总组签收库位</a><br>
	<% } %>
	<!-- <% if(links.get("shipping")) { %>
		        <a href="shipping.do">711 出货</a><br>
	<% } %> -->
	<% if(links.get("support")) { %>
		        <a href="support.do">辅助工作</a><br>
	<% } %>
		    </div>
</div>
<% } %>

<% if(links.get("在线作业（特殊）")) { %>
<div class="dwidth-left">
<h3 class="ui-accordion-header ui-helper-reset ui-state-default ui-corner-top ui-accordion-icons" tabindex="0"><span class="ui-accordion-header-icon ui-icon ui-icon-triangle-1-s"></span>在线作业（动物）</h3>
	<div class="ui-accordion-content ui-helper-reset ui-widget-content">
       ${inlineSpecPosition}
    </div>
</div>
<% } %>

<% if(links.get("品保作业")) { %>
<div class="dwidth-left">
<h3 class="ui-accordion-header ui-helper-reset ui-state-default ui-corner-top ui-accordion-icons" tabindex="0"><span class="ui-accordion-header-icon ui-icon ui-icon-triangle-1-s"></span>品保作业</h3>
		<div class="ui-accordion-content ui-helper-reset ui-widget-content">
	<% if(links.get("qa_work")) { %>
		${qaPosition}
	<% } %>
	<% if(links.get("qa_view")) { %>
		        <a href="qaResult.do">出检结果</a><br>
		        <a href="service_repair_manage.do">保修期内返品+QIS品管理日程表</a><br>
	<% } %>
	</div>
</div>
<% } %>

<% if(links.get("现品管理")) { %>
<div class="dwidth-left">
<h3 class="ui-accordion-header ui-helper-reset ui-state-default ui-corner-top ui-accordion-icons" tabindex="0"><span class="ui-accordion-header-icon ui-icon ui-icon-triangle-1-s"></span>现品管理</h3>
		<div class="ui-accordion-content ui-helper-reset ui-widget-content">
	<% if(links.get("wip")) { %>
		        <a href="wip.do" title="返还，WIP出库">WIP管理</a><br>
		        <a href="turnover_case.do" title="通箱库位">通箱库位管理</a><br>
	<% } %>
	<% if(links.get("fact_material")) { %>
		        <a href="materialFact.do">现品投线</a><br>
		        <a href="waste_partial_arrangement.do">废弃零件回收追溯</a><br>
				<a href="delivery_order.do" title="出货单制作">出货单制作</a><br>
				<a href="compose_storage.do?from=fact">总组匹配库位</a><br>
	<% } %>
	<% if(links.get("bo_partial")) { %>
		        <a href="materialPartial.do" title="现品零件BO管理">零件BO管理</a><br>
	<% } %>
	<% if(links.get("wash")) { %>
				<a href="steel_wire_container_wash_process.do">物料加工记录</a><br>
	<% } %>
    </div>
</div>
<% } %>

<% if(links.get("计划管理")) { %>
<div class="dwidth-left">
<h3 class="ui-accordion-header ui-helper-reset ui-state-default ui-corner-top ui-accordion-icons" tabindex="0"><span class="ui-accordion-header-icon ui-icon ui-icon-triangle-1-s"></span>计划管理</h3>
		<div class="ui-accordion-content ui-helper-reset ui-widget-content">
		        <a href="schedule.do">SA (Schedule Area) 管理</a><br>
	<% if(links.get("schedule_processing")) { %>
		        <a href="scheduleProcessing.do">RA (Racing Area) 管理</a><br>
		        <a href="forSolutionArea.do">PA (Pending Area) 管理</a><br>
	<% } %>
    </div>
</div>
<% } %>

<% if(links.get("info")) { %>
<div class="dwidth-left">
<h3 class="ui-accordion-header ui-helper-reset ui-state-default ui-corner-top ui-accordion-icons" tabindex="0"><span class="ui-accordion-header-icon ui-icon ui-icon-triangle-1-s"></span>进度查询</h3>
		<div class="ui-accordion-content ui-helper-reset ui-widget-content">
		        <a href="material.do">维修对象</a><br>
		        <a href="positionProduction.do">工位</a><br>
		        <a href="operatorProduction.do">工时</a><br>
		        <a href="alarmMessage.do">警报</a><br>
		        <a href="new_phenomenon.do">不良现象</a><br>
	<% if(links.get("nsline")) { %>
		        <a href="snouts.do">先端组件</a><br>
	<% } %>
		        <a href="drying_process.do">烘干作业</a><br>
		        <a href="materialPcs.do">工程检查票</a>
		    </div>
</div>
<% } %>

<% if(links.get("文档管理")) { %>
<div class="dwidth-left">
<h3 class="ui-accordion-header ui-helper-reset ui-state-default ui-corner-top ui-accordion-icons" tabindex="0"><span class="ui-accordion-header-icon ui-icon ui-icon-triangle-1-s"></span>文档管理</h3>
		<div class="ui-accordion-content ui-helper-reset ui-widget-content">
		        <a href="filing.do">维修作业归档</a><br>
		        <a href="operatorProduction.do?method=monthly">SORC 月报</a><br>
		        <a href="procedureManual.do">作业要领书</a><br>
		        <a href="daily_work_sheet.do">工作记录表</a><br>
		        <a href="worktime_analysis.do">工时分析</a><br>
		        <a href="productivity.do">生产效率</a><br>
		        <a href="line_balance_rate.do">流水线平衡率分析</a><br>
		        <a href="foundry.do">代工时间统计</a><br>
		        <a href="waitting_time_report.do">等待时间、中断时间统计</a><br>
		        <a href="remain_time_report.do">倒计时达成状况</a><br>
				<a href="work_duration.do">作业时间分析</a>
	</div>
</div>
<% } %>

<% if(links.get("viewer")) { %>
<div class="dwidth-left">
<h3 class="ui-accordion-header ui-helper-reset ui-state-default ui-corner-top ui-accordion-icons" tabindex="0"><span class="ui-accordion-header-icon ui-icon ui-icon-triangle-1-s"></span>展示一览</h3>
		<div class="ui-accordion-content ui-helper-reset ui-widget-content">
        <a target="_parent" href="show.do#globalProgress">全工程</a><br>
        <a target="_parent" href="show.do#acceptFact">受理报价展示</a><br>
        <a target="_parent" href="show.do#wipProgress">WIP库位</a><br>
        <a title="分解工程线长界面" target="_parent" href="show.do#lineLeaderD1">内镜维修分解展示</a><br>
        <a title="NS工程线长界面" target="_parent" href="show.do#lineLeaderN1">内镜维修 NS 展示</a><br>
        <!--a title="" target="_parent" href="show.do#comMatch">内镜维修总组接收匹配展示</a><br-->
        <a title="总组工程线长界面" target="_parent" href="show.do#lineSituationP1">内镜维修总组展示</a><br>
        <a title="3课维修工程" target="_parent" href="show.do#lineRepair3">周边+ENDOEYE 维修展示</a><br>
        <a title="" target="_parent" href="show.do#allPositions">全工位状况展示</a><br>
        <a title="" target="_parent" href="show.do#service_repair_manage">保内返品+QIS分析展示</a><br>
        <a title="" target="_parent" href="show.do#finalCheck">最终检查展示</a><br>
        <a title="" target="_parent" href="show.do#free_qis_cost">无偿QIS展示</a><br>
        <a title="" target="_parent" href="show.do#consumable_list">消耗品库存展示</a><br>
        <a title="" target="_parent" href="show.do#snout_list">先端预制看板</a><br>
        <a title="" target="_parent" href="show.do#turnoverCase">通箱库位展示</a><br>
        <a title="Pending Area" target="_parent" href="show.do#devicesPendingArea">设备工具PA展示</a><br>
	间接作业人员工时图: <br>
        <a title="受理人员工时图" target="_parent" href="show.do#afProductionFeature11">受理</a> <a title="物料人员工时图" target="_parent" href="show.do#afProductionFeature20">物料</a>

</div>
</div>
<% } %>

<div class="dwidth-left">
<h3 class="ui-accordion-header ui-helper-reset ui-state-default ui-corner-top ui-accordion-icons" tabindex="0"><span class="ui-accordion-header-icon ui-icon ui-icon-triangle-1-s"></span>资源功能</h3>
		<div class="ui-accordion-content ui-helper-reset ui-widget-content">
	<% if(links.get("admin")) { %>
		        <a href="adminmenu.do">系统信息管理</a><br>
	<% } %>
	<% if(links.get("tech")) { %>
		        <a href="pcs_request.do">工程检查票改废订</a><br>
	<% } %>
		        <a href="standard_work_time.do">标准工时参考</a><br>
		        <a href="pcsTemplate.do">工程检查票模板</a><br>
		        <a href="password.do">用户密码修改</a>
		    </div>
		</div>
</div>

</body></html>