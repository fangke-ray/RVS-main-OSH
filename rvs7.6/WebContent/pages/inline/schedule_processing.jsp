<%@page import="framework.huiqing.common.util.CodeListUtils"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath()+"/";
%>
<base href="<%=basePath%>">

<link rel="stylesheet" type="text/css" href="css/custom.css">
<link rel="stylesheet" type="text/css" href="css/olympus/jquery-ui-1.9.1.custom.css">
<link rel="stylesheet" type="text/css" href="css/ui.jqgrid.css">
<link rel="stylesheet" type="text/css" href="css/olympus/select2Buttons.css">
<script type="text/javascript" src="js/jquery-1.8.2.min.js"></script>
<script type="text/javascript" src="js/jquery-ui-1.9.1.custom.min.js"></script>
<script type="text/javascript" src="js/jquery.validate.min.js"></script>
<script type="text/javascript" src="js/jquery.dialog.js"></script>
<script type="text/javascript" src="js/jquery.jqGrid.min.js"></script>
<script type="text/javascript" src="js/i18n/grid.locale-cn.js"></script>
<script type="text/javascript" src="js/utils.js"></script>
<script type="text/javascript" src="js/jquery-plus.js"></script>
<script type="text/javascript" src="js/jquery.select2buttons.js"></script>
<script type="text/javascript" src="js/common/material_detail_ctrl.js"></script>
<script type="text/javascript" src="js/inline/schedule_processing.js"></script>
<title>Racing Area</title>
</head>
<body class="outer">
	<div class="width-full" style="align: center; margin: auto; margin-top: 16px;">

		<div id="basearea" class="dwidth-full" style="margin: auto;">
			<jsp:include page="/header.do" flush="true">
				<jsp:param name="part" value="1"/>
			</jsp:include>
		</div>

		<div class="ui-widget-panel ui-corner-all width-full" style="align: center; padding-top: 16px; padding-bottom: 16px;" id="body-1">
			<div id="searcharea" class="dwidth-full">
				<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser dwidth-full">
					<span class="areatitle">检索条件</span> <a role="link" href="javascript:void(0)" class="HeaderButton areacloser"> <span class="ui-icon ui-icon-circle-triangle-s"></span>
					</a>
				</div>
				<% 
					String role = (String) request.getAttribute("role");
					boolean isPlanner = ("planner").equals(role);
					boolean isManager = ("manager").equals(role);
				%>
				<div class="ui-widget-content dwidth-full" style="<% if (isPlanner) { %>display: none;<% } %>">
					<form id="searchform" method="POST">
						<table class="condform">
							<tbody>
								<tr>
									<td class="ui-state-default td-title">修理单号</td>
									<td class="td-content"><input type="text" id="search_sorc_no" maxlength="15" class="ui-widget-content"></td>
									<td class="ui-state-default td-title" rowspan="2">维修对象机种</td>
									<td class="td-content" rowspan="2" colspan="3">
										<select name="category_id" id="search_category_id" class="ui-widget-content">${cOptions}</select>	
									</td>
								</tr>
								<tr>
									<td class="ui-state-default td-title">机身号</td>
									<td class="td-content"><input type="text" id="search_serial_no" maxlength="12" class="ui-widget-content"></td>
								</tr>
								<tr>
									<td class="ui-state-default td-title">ESAS No.</td>
									<td class="td-content"><input type="text" id="search_esas_no" maxlength="6" class="ui-widget-content"></td>
									<td class="ui-state-default td-title">纳期</td>
									<td class="td-content">
										<input type="text" id="search_scheduled_date_start" maxlength="50" class="ui-widget-content" readonly="readonly">起<br/>
										<input type="text" id="search_scheduled_date_end" maxlength="50" class="ui-widget-content" readonly="readonly">止</td>
									<td class="ui-state-default td-title">总组出货安排</td>
									<td class="td-content">
										<input type="text" id="search_complete_date_start" maxlength="50" class="ui-widget-content" readonly="readonly">起<br/>
										<input type="text" id="search_complete_date_end" maxlength="50" class="ui-widget-content" readonly="readonly">止</td>
								</tr>
								<tr>
									<td class="ui-state-default td-title">等级</td>
									<td class="td-content">
										<select name="search_level" id="search_level" class="ui-widget-content">
											<%=CodeListUtils.getSelectOptions("material_level_inline", null, "", false) %>
										</select>
									</td>
									<td class="ui-state-default td-title">加急</td>
									<td class="td-content">
										<div id="scheduled_expedited_set" class="ui-buttonset">
											<input type="radio" name="expedited" id="scheduled_expedited_a" class="ui-widget-content ui-helper-hidden-accessible" value="" checked="checked"><label for="scheduled_expedited_a" aria-pressed="false">全部</label>
											<input type="radio" name="expedited" id="scheduled_expedited_t" class="ui-widget-content ui-helper-hidden-accessible" value="1"><label for="scheduled_expedited_t" aria-pressed="false">有加急</label>
											<input type="radio" name="expedited" id="scheduled_expedited_f" class="ui-widget-content ui-helper-hidden-accessible" value="0"><label for="scheduled_expedited_f" aria-pressed="false">无加急</label>
											<input type="hidden" id="scheduled_expedited">
										</div>
									</td>
									<td class="ui-state-default td-title">零件BO</td>
									<td class="td-content">
										<div id="cond_work_procedure_order_template_flg_set" class="ui-buttonset">
											<input type="radio" name="bo" id="cond_work_procedure_order_template_a" class="ui-widget-content ui-helper-hidden-accessible" value="" checked="checked"><label for="cond_work_procedure_order_template_a" aria-pressed="false">全部</label>
											<input type="radio" name="bo" id="cond_work_procedure_order_template_f" class="ui-widget-content ui-helper-hidden-accessible" value="0"><label for="cond_work_procedure_order_template_f" aria-pressed="false">无BO</label>
											<input type="radio" name="bo" id="cond_work_procedure_order_template_t" class="ui-widget-content ui-helper-hidden-accessible" value="1"><label for="cond_work_procedure_order_template_t" aria-pressed="false">有BO</label>
											<input type="radio" name="bo" id="cond_work_procedure_order_template_e" class="ui-widget-content ui-helper-hidden-accessible" value="2"><label for="cond_work_procedure_order_template_e" aria-pressed="false">BO解决</label>
											<input type="hidden" id="cond_work_procedure_order_template">
										</div>
									</td>
								</tr>
								<tr>
									<td class="ui-state-default td-title">入库预定日</td>
									<td class="td-content">
										<input type="text" id="search_arrival_plan_date_start" maxlength="50" class="ui-widget-content" readonly="readonly">起<br>
										<input type="text" id="search_arrival_plan_date_end" maxlength="50" class="ui-widget-content" readonly="readonly">止
									</td>
									<td class="ui-state-default td-title">进展工位</td>
									<td class="td-content"><input type="button" class="ui-button" id="position_eval" value="通过">
										<input type="text" class="ui-widget-content" readonly="readonly" id="inp_position_id">
										<input type="hidden" id="search_position_id">
									</td>
									<td class="ui-state-default td-title">课室</td>
									<td class="td-content">
										<select name="section" id="search_section" class="ui-widget-content">
											${sOptions}
										</select>
									</td>
								</tr>
							</tbody>
						</table>
						<div style="height: 44px">
							<input type="button" class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="switchbutton" value="到 PA 画面" role="button" style="float: right; right: 2px" onclick="javascript:window.location.href='forSolutionArea.do?switch_from=scheduleProcessing.do'">
							<input type="button" class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="resetbutton" value="清除" role="button" style="float: right; right: 12px">
							<input type="button" class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="searchbutton" value="检索" role="button" style="float: right; right: 2px">
							<input type="button" class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="searchpro1button" value="当日生产情况" role="button" style="float: right; right: 2px">
							<input type="button" class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="searchpro2button" value="今日总完成" role="button" style="float: right; right: 2px">
						</div>
					</form>
				</div>
				<div class="clear areaencloser dwidth-full"></div>
			</div>

			<div id="listarea" class="dwidth-full">
				<input type="hidden" value="<%=role%>">
				<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser dwidth-full">
					<span class="areatitle">维修对象一览</span> 
					<a role="link" href="javascript:void(0)" class="HeaderButton areacloser"> <span class="ui-icon <% if (isPlanner) { %>ui-icon-circle-triangle-s<% } else { %>ui-icon-circle-triangle-n<% } %>"></span>
					</a>
				</div>
				<table id="tuli" style="width: 1250px;margin: 0;
border-collapse: collapse;border-width: 0 1px;border-color: rgb(170, 170, 170);border-style: solid;color:white;<% if (isPlanner) { %>display: none;<% } %>">
					<tr>
						<td width="20%" style="background-color:#FFFFFF;color:#333333;border-bottom:1px solid #1E1C4F;">距纳期2天以上</td>
						<td width="20%" style="background-color:#E4F438;color:#333333;">离纳期2天以内</td>
						<td width="20%" style="background-color:#F9E29F;color:#333333;">已超出纳期</td>
						<td width="20%" style="background-color:#E48E38;">超出纳期3天以上</td>
						<td width="20%" style="background-color:#E43838;">超出纳期7天以上</td>
					</tr>
				</table>
				<div id="chooser" class="dwidth-full ui-widget-content" style="border-bottom: 0;<% if (isPlanner) { %>display: none;<% } %>">
					<div id="colchooser">
						<input type="checkbox" id="colchooser_rec"></input><label for="colchooser_rec">受理报价</label>
						<input type="checkbox" id="colchooser_par"></input><label for="colchooser_par">现品零件</label>
						<input type="checkbox" id="colchooser_prc"></input><label for="colchooser_prc">工程进度</label>
						<input type="checkbox" id="colchooser_she"></input><label for="colchooser_she">计划管理</label>
					</div>
				</div>
				<table id="list"></table>
				<div id="listpager"></div>
				<div class="clear areaencloser"></div>
			</div>

			<div id="functionarea" class="dwidth-full" style="margin: auto;<% if (isPlanner) { %>display: none;<% } %>">
				<% if (isPlanner || isManager) { %>
				<div class="ui-widget-header ui-corner-all ui-helper-clearfix areabase">
					<% if (isPlanner) { %>
<script type="text/javascript" src="js/inline/manager_processing.js"></script>
					<div id="executes" style="margin-left: 4px; margin-top: 4px;">
						<!-- input type="button" class="ui-button" id="importbutton" value="导入入库预定日" / -->
						<input type="button" class="ui-button" id="decplannedbutton" value="排入分解计划" />
						<input type="button" class="ui-button" id="nsplannedbutton" value="排入NS计划" />
						<input type="button" class="ui-button" id="complannedbutton" value="排入总组计划" />
						<!-- input type="button" class="ui-button" id="resignbutton" value="全工程内返工" / -->
						<!-- input type="button" class="ui-button" id="pausebutton" value="暂停" / -->
						<input type="button" class="ui-button" id="reportbutton" value="输出总览计划表 " style="float: right; right: 2px" />
						<input id="daily_report_button" class="ui-button" value="日报信息确认" type="button" style="float: right; right: 2px">
					</div>
						<div id="nogood_treat"></div><div id="daily_report"></div><div id="capacity_setting"></div>
					<% } else if (isManager) { %>
<script type="text/javascript" src="js/inline/manager_processing.js"></script>
					<div id="executes" style="margin-left: 4px; margin-top: 4px;">
						<input id="nogoodbutton" class="ui-button" value="处置不良" type="button"/>
						<input id="reccdbutton" class="ui-button" value="重新更换CCD盖玻璃" type="button"/>
						<input id="capacity_setting_button" class="ui-button" value="产能设定" type="button"/>
						<!--input id="cleanbutton" class="ui-button" value="工位作业取消申请" type="button"/>
						<input id="capacitybutton" class="ui-button" value="设定产能" type="button"/-->
						<input id="daily_report_button" class="ui-button" value="日报信息确认" type="button" style="float: right; right: 2px">
					</div>
						<div id="nogood_treat"></div><div id="daily_report"></div><div id="capacity_setting"></div>
					<% } %>
				</div>
				<div class="clear areaencloser"></div>
				<% } %>
			</div>

			<div id="planned_listarea" class="dwidth-full">
				<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser dwidth-full">
					<span class="areatitle" style="margin-right: 250px">当日进度一览</span>
				</div>
				<div style="height: 44px; border-bottom: 0;" id="infoes" class="dwidth-full ui-widget-content">
					<input type="radio" name="infoes" class="ui-button ui-corner-up" id="planallbutton" value="" role="button" checked><label for="planallbutton">全部</label>
					<input type="radio" name="infoes" class="ui-button ui-corner-up" id="plandecbutton" value="12" role="button"><label for="plandecbutton">分解</label>
					<input type="radio" name="infoes" class="ui-button ui-corner-up" id="plannsbutton" value="13" role="button"><label for="plannsbutton">NS</label>
					<input type="radio" name="infoes" class="ui-button ui-corner-up" id="plancombutton" value="14" role="button"><label for="plancombutton">总组</label>
					<input type="radio" name="infoes" class="ui-button ui-corner-up" id="planoutbutton" role="button"><label for="planoutbutton">计划外</label>
					<input type="hidden" id="select_line">
				</div>
				<table id="planned_list"></table>
				<div id="planned_listpager"></div>
				<div class="clear areaencloser"></div>
			</div>

<% if (isPlanner) { %>
			<div id="planned_functionarea" class="dwidth-full" style="margin: auto;">
				<div class="ui-widget-header ui-corner-all ui-helper-clearfix areabase">
					<div id="executes" style="margin-left: 4px; margin-top: 4px;">
						<input type="button" class="ui-button" id="removefromplanbutton" value="删除" /> <input type="button" class="ui-button" id="todayreportbutton" value="输出当日计划表" style="float: right; right: 2px" />
					</div>
				</div>
			</div>
<% } %>

				<div class="clear"></div>
		</div>

		<div id="process_dialog"></div>
		<div id="confirmmessage"></div>
	</div>
	<div class="referchooser ui-widget-content" id="pReferChooser" tabindex="-1">
		<table>
			<tr>
				<td width="50%">过滤字:<input type="text"/></td>	
				<td width="50%" align="right"><input type="button" class="ui-button" style="float:right;" value="清空"/></td>
			</tr>
		</table>
		
		<table class="subform">${pReferChooser}</table>
	</div>
</body>
</html>
