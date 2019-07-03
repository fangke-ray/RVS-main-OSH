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
<script type="text/javascript" src="js/inline/for_solution_area.js"></script>
<title>Pending Area</title>
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
					<span class="areatitle">检索条件</span> <a role="link" href="javascript:void(0)" class="HeaderButton areacloser"> <span class="ui-icon ui-icon-circle-triangle-n"></span>
					</a>
				</div>
				<% 
					String role = (String) request.getAttribute("role");
					boolean isPlanner = ("planner").equals(role);
					boolean isManager = ("manager").equals(role);
				%>
				<div class="ui-widget-content dwidth-full">
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
									<td class="td-content"><input type="text" id="search_serial_no" maxlength="20" class="ui-widget-content"></td>
								</tr>
								<tr>
									<td class="ui-state-default td-title">纳期</td>
									<td class="td-content">
										<input type="text" id="search_scheduled_date_start" maxlength="50" class="ui-widget-content" readonly="readonly">起<br/>
										<input type="text" id="search_scheduled_date_end" maxlength="50" class="ui-widget-content" readonly="readonly">止</td>
									<td class="ui-state-default td-title">课室</td>
									<td class="td-content">
										<select name="section" id="search_section" class="ui-widget-content">
											${sOptions}
										</select>
									</td>
									<td class="ui-state-default td-title">延误</td>
									<td class="td-content">
										<div id="search_expedition_diff" class="ui-buttonset">
											<input type="radio" name="expedition_diff" id="search_expedition_diff_a" class="ui-widget-content ui-helper-hidden-accessible" value="" checked="checked"><label for="search_expedition_diff_a" aria-pressed="false">(全)</label>
											<input type="radio" name="expedition_diff" id="search_expedition_diff_t" class="ui-widget-content ui-helper-hidden-accessible" value="1"><label for="search_expedition_diff_t" aria-pressed="false">延误</label>
											<input type="radio" name="expedition_diff" id="search_expedition_diff_f" class="ui-widget-content ui-helper-hidden-accessible" value="0"><label for="search_expedition_diff_f" aria-pressed="false">无延误</label>
										</div>
									</td>
								</tr>
								<tr>
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
										<div id="search_bo_flg" class="ui-buttonset">
											<input type="radio" name="bo" id="bo_flg_a" class="ui-widget-content ui-helper-hidden-accessible" value="" checked="checked"><label for="bo_flg_a" aria-pressed="false">全部</label>
											<input type="radio" name="bo" id="bo_flg_f" class="ui-widget-content ui-helper-hidden-accessible" value="0"><label for="bo_flg_f" aria-pressed="false">无BO</label>
											<input type="radio" name="bo" id="bo_flg_t" class="ui-widget-content ui-helper-hidden-accessible" value="1"><label for="bo_flg_t" aria-pressed="false">有BO</label>
											<input type="radio" name="bo" id="bo_flg_e" class="ui-widget-content ui-helper-hidden-accessible" value="2"><label for="bo_flg_e" aria-pressed="false">BO解决</label>
											<input type="hidden" id="bo_flg">
										</div>
									</td>
									<td class="ui-state-default td-title">入库预定日</td>
									<td class="td-content">
										<input type="text" id="search_arrival_plan_date_start" maxlength="50" class="ui-widget-content" readonly="readonly">起<br>
										<input type="text" id="search_arrival_plan_date_end" maxlength="50" class="ui-widget-content" readonly="readonly">止
									</td>
								</tr>
								<tr>
									<td class="ui-state-default td-title">已处理</td>
									<td class="td-content">
										<div id="search_resolved_set" class="ui-buttonset">
											<input type="radio" name="resolved" id="resolved_a" class="ui-widget-content ui-helper-hidden-accessible" value=""><label for="resolved_a" aria-pressed="false">全部</label>
											<input type="radio" name="resolved" id="resolved_t" class="ui-widget-content ui-helper-hidden-accessible" value="1"><label for="resolved_t" aria-pressed="false">已处理</label>
											<input type="radio" name="resolved" id="resolved_f" class="ui-widget-content ui-helper-hidden-accessible" value="-1" checked="checked"><label for="resolved_f" aria-pressed="false">未处理</label>
											<input type="hidden" id="scheduled_expedited">
										</div>
									</td>
									<td class="ui-state-default td-title">中断原因</td>
									<td class="td-content">
										<select name="line" id="search_reason" class="ui-widget-content">
											${reasonOptions}
										</select>
									</td>
									<td class="ui-state-default td-title">中断发生日</td>
									<td class="td-content">
										<input type="text" id="search_happen_time" maxlength="50" class="ui-widget-content" readonly="readonly" value="${today}">
									</td>
								</tr>
							</tbody>
						</table>
						<div style="height: 44px">
							<input type="button" class="ui-button ui-widget ui-state-default ui-corner-all" id="switchbutton" value="到 ${switch_name} 画面" role="button" style="float: right; right: 2px" onclick="javascript:window.location.href=$('#switch_from').val()">
							<input type="button" class="ui-button ui-widget ui-state-default ui-corner-all" id="resetbutton" value="清除" role="button" style="float: right; right: 2px">
							<input type="button" class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="searchbutton" value="检索" role="button" style="float: right; right: 2px">

							<input type="hidden" id="switch_from" value="${switch_from}" />
							<input type="hidden" id="h_level_eo" value="${h_level_eo}" /><input type="hidden" id="h_or_eo" value="${h_or_eo}" /><input type="hidden" id="h_bo_eo" value="${h_bo_eo}" />
						</div>
					</form>
				</div>
				<div class="clear areaencloser dwidth-full"></div>
			</div>

			<div id="listarea" class="dwidth-full">
				<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser dwidth-full">
					<span class="areatitle">维修对象一览</span>
				</div>
				<input type="hidden" value="<%=role%>">
				<div style="height: 44px; border-bottom: 0;" id="lines" class="dwidth-full ui-widget-content ui-buttonset">
					<input type="radio" name="lines" class="ui-button ui-corner-up" id="lineallbutton" value="" role="button" checked><label for="lineallbutton">全部</label>
					<input type="radio" name="lines" class="ui-button ui-corner-up" id="linedecbutton" value="12" role="button"><label for="linedecbutton">分解</label>
					<input type="radio" name="lines" class="ui-button ui-corner-up" id="linensbutton" value="13" role="button"><label for="linensbutton">NS</label>
					<input type="radio" name="lines" class="ui-button ui-corner-up" id="linecombutton" value="14" role="button"><label for="linecombutton">总组</label>
					<input type="hidden" id="select_line">
				</div>
				<table id="tuli" style="width: 1250px;margin: 0;
border-collapse: collapse;border-width: 0 1px;border-color: rgb(170, 170, 170);border-style: solid;color:white;">
					<tr>
						<td width="20%" style="background-color:#FFFFFF;color:#333333;border-top:1px solid #F9E29F;">距纳期2天以上</td>
						<td width="20%" style="background-color:#E4F438;color:#333333;">离纳期2天以内</td>
						<td width="20%" style="background-color:#F9E29F;color:#333333;">已超出纳期</td>
						<td width="20%" style="background-color:#E48E38;">超出纳期3天以上</td>
						<td width="20%" style="background-color:#E43838;">超出纳期7天以上</td>
					</tr>
					<tr style="display:none;">
						<td style="background-color:white;" colspan="5"><input type="checkbox" class="ui-button" id="show_process" /><label for="show_process" style="font-size:14px;padding:0">进度信息显示</label></td>
					</tr>		
				</table>

				<table id="list"></table>
				<div id="listpager"></div>
				<div class="clear areaencloser"></div>
			</div>

			<div id="functionarea" class="dwidth-full" style="margin: auto;">
				<div class="ui-widget-header ui-corner-all ui-helper-clearfix areabase">
					<% if (isPlanner) { %>
					<div id="executes" style="margin-left: 4px; margin-top: 4px;">
						<input type="button" class="ui-button" id="closebutton" value="问题解决" />
						<input type="button" class="ui-button" id="complannedbutton"value="排入总组计划" />
						<input type="button" class="ui-button" id="complanned-a-button" style="left: -7px;" value="计划未定↓" />
						<input type="button" class="ui-button" id="pausebutton" value="未修理返还" />
						<input type="button" class="ui-button" id="downbutton" style="float:right;right:2px;" value="一览导出" />
					</div>
					<% } %>
					<% if (isManager) { %>
					<div id="executes" style="margin-left: 4px; margin-top: 4px;">
<script type="text/javascript" src="js/inline/manager_processing.js"></script>
						<input id="nogoodbutton" class="ui-button" value="处置不良" type="button"/>
						<input type="button" class="ui-button" id="closebutton" value="问题解决" />
						<input type="button" class="ui-button" id="downbutton" style="float:right;right:2px;" value="一览导出" />
					</div>
					<% } %>
				</div>
				<div class="clear areaencloser"></div>
			</div>

<% if (isPlanner) { %>

			<div id="planned_listarea" class="dwidth-full">
				<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser dwidth-full" style="height:36px;">
					<span class="areatitle" style="margin-right: 250px"><label id="title_planned">明日计划一览</label></span>

					<a role="link" href="javascript:void(0)" style="float: left;margin-top:8px;" class="HeaderButton areacloser"> 
						<span class="ui-icon ui-icon-circle-triangle-w"></span>
					</a>
					<span style="float: left;" class="areatitle">
						<input type="text" id="pick_date" style="height:20px;">
					</span>
					<a role="link" href="javascript:void(0)" style="float: left;margin-top:8px;" class="HeaderButton areacloser"> 
						<span class="ui-icon ui-icon-circle-triangle-e"></span>
					</a>
				</div>
				<div style="height: 44px; border-bottom: 0;" id="infoes" class="dwidth-full ui-widget-content">
				<div class="ui-buttonset">
					<input type="radio" name="infoes" class="ui-button ui-corner-up" id="planallbutton" value="" role="button" checked><label for="planallbutton">全部</label>
					<input type="radio" name="infoes" class="ui-button ui-corner-up" id="plandecbutton" value="12" role="button"><label for="plandecbutton">分解</label>
					<input type="radio" name="infoes" class="ui-button ui-corner-up" id="plannsbutton" value="13" role="button"><label for="plannsbutton">NS</label>
					<input type="radio" name="infoes" class="ui-button ui-corner-up" id="plancombutton" value="14" role="button"><label for="plancombutton">总组</label>
					<input type="hidden" id="select_line">
				</div>
				</div>
				<table id="planned_list"></table>
				<div id="planned_listpager"></div>
				<div class="clear areaencloser"></div>
			</div>

			<div id="planned_functionarea" class="dwidth-full" style="margin: auto;">
				<div class="ui-widget-header ui-corner-all ui-helper-clearfix areabase">
					<div id="executes" style="margin-left: 4px; margin-top: 4px;">
						<input type="button" class="ui-button" id="removefromplanbutton" value="删除" /> <input type="button" class="ui-button" id="todayreportbutton" value="输出已排计划表" style="float: right; right: 2px" />
					</div>
				</div>
			</div>
<% } %>
			<div class="clear"></div>
		</div>

		<div id="nogood_treat"></div>
		<div id="detail_dialog"></div>
		<div id="confirm_message"></div>
	</div>
</body>
</html>
