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
<script type="text/javascript" src="js/ajaxfileupload.js"></script>
<script type="text/javascript" src="js/common/material_detail_ctrl.js"></script>
<script type="text/javascript" src="js/inline/schedule.js?v=3486"></script>
<title>Schedule Area</title>
</head>
<% 
	String role = (String) request.getAttribute("role");
	boolean isPlanner = ("planner").equals(role);
%>
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
				<div class="ui-widget-content dwidth-full">
					<form id="searchform" method="POST">
						<table class="condform">
							<tbody>
								<tr>
									<td class="ui-state-default td-title" style="height:30px;">修理单号</td>
									<td class="td-content"><input type="text" id="search_sorc_no" maxlength="15" class="ui-widget-content"></td>
									<td class="ui-state-default td-title" rowspan="4">维修对象机种</td>
									<td class="td-content" rowspan="4" colspan="3">
										<select name="category_id" id="search_category_id" class="ui-widget-content" multiple>${cOptions}</select>	
									</td>
								</tr>
								<tr>
									<td class="ui-state-default td-title">ESAS No.</td>
									<td class="td-content"><input type="text" id="search_esas_no" maxlength="8" class="ui-widget-content"></td>
								</tr>
								<tr>
									<td class="ui-state-default td-title">机身号</td>
									<td class="td-content"><input type="text" id="search_serialno" maxlength="20" class="ui-widget-content"></td>
								</tr>
								<tr>
									<td class="ui-state-default td-title">委托处</td>
									<td class="td-content"><select name="ocm" id="search_ocm" class="ui-widget-content">${oOptions}</select></td>
								</tr>
								<tr>
									<td class="ui-state-default td-title">客户同意日</td>
									<td class="td-content">
										<input type="text" id="search_agreed_date_start" maxlength="50" class="ui-widget-content" readonly="readonly">起<br/>
										<input type="text" id="search_agreed_date_end" maxlength="50" class="ui-widget-content" readonly="readonly">止</td>
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
										<select name="search_level" id="search_level" class="ui-widget-content" multiple>
											${lOptions}
										</select>
									</td>
									<td class="ui-state-default td-title">加急</td>
									<td class="td-content">
										<div id="scheduled_expedited_set" class="ui-buttonset">
											<input type="radio" name="expedited" id="scheduled_expedited_a" class="ui-widget-content ui-helper-hidden-accessible" value="" checked="checked"><label for="scheduled_expedited_a" aria-pressed="false">(全)</label>
											<input type="radio" name="expedited" id="scheduled_expedited_r" class="ui-widget-content ui-helper-hidden-accessible" value="2"><label for="scheduled_expedited_r" aria-pressed="false">直送快速</label>
											<input type="radio" name="expedited" id="scheduled_expedited_t" class="ui-widget-content ui-helper-hidden-accessible" value="1"><label for="scheduled_expedited_t" aria-pressed="false">有加急</label>
											<input type="radio" name="expedited" id="scheduled_expedited_f" class="ui-widget-content ui-helper-hidden-accessible" value="0"><label for="scheduled_expedited_f" aria-pressed="false">无加急</label>
											<input type="hidden" id="scheduled_expedited">
										</div>
									</td>
									<td class="ui-state-default td-title">零件BO</td>
									<td class="td-content">
										<div id="cond_work_procedure_order_template_flg_set" class="ui-buttonset">
											<input type="radio" name="bo" id="cond_work_procedure_order_template_a" class="ui-widget-content ui-helper-hidden-accessible" value="" checked="checked"><label for="cond_work_procedure_order_template_a" aria-pressed="false">(全)</label>
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
										<input type="text" id="search_arrival_plan_date_start" class="ui-widget-content" readonly="readonly">起<br>
										<input type="text" id="search_arrival_plan_date_end" class="ui-widget-content" readonly="readonly">止
									</td>
									<td class="ui-state-default td-title">进展工位</td>
									<td class="td-content"><input type="button" class="ui-button" id="position_eval" value="通过">
										<input type="text" class="ui-widget-content" readonly="readonly" id="inp_position_id">
										<input type="hidden" id="search_position_id">
									</td>
									<td class="ui-state-default td-title">进展工位</td>
									<td class="td-content"><input type="button" class="ui-button" id="position_eval2" value="未到">
										<input type="text" class="ui-widget-content" readonly="readonly" id="inp_position_id2">
										<input type="hidden" id="search_position_id2">
									</td>
								</tr>
								<tr>
									<td class="ui-state-default td-title">课室</td>
									<td class="td-content">
										<select name="section" id="search_section" class="ui-widget-content">
											${sOptions}
										</select>
									</td>
									<td class="ui-state-default td-title">实际出货与<br/>纳期</td>
									<td class="td-content">
										<div id="search_expedition_diff" class="ui-buttonset">
											<input type="radio" name="expedition_diff" id="search_expedition_diff_a" class="ui-widget-content ui-helper-hidden-accessible" value="" checked="checked"><label for="search_expedition_diff_a" aria-pressed="false">(全)</label>
											<input type="radio" name="expedition_diff" id="search_expedition_diff_t" class="ui-widget-content ui-helper-hidden-accessible" value="1"><label for="search_expedition_diff_t" aria-pressed="false">延误</label>
											<input type="radio" name="expedition_diff" id="search_expedition_diff_f" class="ui-widget-content ui-helper-hidden-accessible" value="0"><label for="search_expedition_diff_f" aria-pressed="false">无延误</label>
										</div>
									</td>
									<td class="ui-state-default td-title">入库预定日<br/>超纳期</td>
									<td class="td-content">
										<div id="search_arrival_delay" class="ui-buttonset">
											<input type="radio" name="arrival_delay" id="search_arrival_delay_a" class="ui-widget-content ui-helper-hidden-accessible" value="" checked="checked"><label for="search_arrival_delay_a" aria-pressed="false">(全)</label>
											<input type="radio" name="arrival_delay" id="search_arrival_delay_t" class="ui-widget-content ui-helper-hidden-accessible" value="1"><label for="search_arrival_delay_t" aria-pressed="false">超期</label>
											<input type="radio" name="arrival_delay" id="search_arrival_delay_f" class="ui-widget-content ui-helper-hidden-accessible" value="0"><label for="search_arrival_delay_f" aria-pressed="false">不超期</label>
											<input type="hidden" id="arrival_delay">
										</div>
									</td>
								</tr>
								<tr>
									<td class="ui-state-default td-title">直送状况</td>
									<td class="td-content">
										<div id="search_direct_flg" class="ui-buttonset">
											<input type="radio" name="direct_flg" id="search_direct_flg_a" class="ui-widget-content ui-helper-hidden-accessible" value="" checked="checked"><label for="search_direct_flg_a" aria-pressed="false">(全)</label>
											<input type="radio" name="direct_flg" id="search_direct_flg_t" class="ui-widget-content ui-helper-hidden-accessible" value="1"><label for="search_direct_flg_t" aria-pressed="false">直送</label>
											<input type="radio" name="direct_flg" id="search_direct_flg_f" class="ui-widget-content ui-helper-hidden-accessible" value="0"><label for="search_direct_flg_f" aria-pressed="false">分室</label>
										</div>
									</td>
									<td class="ui-state-default td-title" rowspan="2">投线时间</td>
									<td class="td-content" rowspan="2">
										<input type="text" id="search_inline_time_start" class="ui-widget-content" readonly="readonly">起<br/>
										<input type="text" id="search_inline_time_end" class="ui-widget-content" readonly="readonly">止</td>
									</td>
									<td class="ui-state-default td-title">保内返修/QIS<br>/备品</td>
									<td class="td-content">
										<select name="service_repair_flg" id="search_service_repair_flg" class="ui-widget-content" multiple>
											${srOptions}
										</select>
									</td>
								</tr>
								<tr>
									<td class="ui-state-default td-title">动物实验用</td>
									<td class="td-content" id="search_anml_exp_set">
										<input type="radio" name="anml_exp" id="search_anml_exp_a" class="ui-widget-content" checked="true" value=""><label for="search_anml_exp_a">(全)</label>
										<input type="radio" name="anml_exp" id="search_anml_exp_y" class="ui-widget-content" value="1"><label for="search_anml_exp_y">动物实验用</label>
									</td>
									<td class="ui-state-default td-title" rowspan="1">总组工程分线</td>
									<td class="td-content" rowspan="1">
										<select name="px" id="search_px" class="ui-widget-content">
											${pxOptions}
										</select>
									</td>
								</tr>
							</tbody>
						</table>
						<div style="height: 44px">
							<input type="button" class="ui-button ui-widget ui-state-default ui-corner-all" id="switchbutton" value="到 PA 画面" role="button" style="float: right; right: 2px" onclick="javascript:window.location.href='forSolutionArea.do?switch_from=schedule.do'">
							<input type="button" class="ui-button ui-widget ui-state-default ui-corner-all" id="resetbutton" value="清除" role="button" style="float: right; right: 12px">
							<input type="button" class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="searchbutton" value="检索" role="button" style="float: right; right: 12px">
<% if (isPlanner) { %>
							<input type="button" class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="supportbutton" value="排定计划辅助" role="button" style="float: left; left: 8px">
							<div style="padding-left: 150px; padding-top: 4px;">
								辅助针对日期 <input type="text" id="support_date" class="ui-widget-content" readonly="readonly"> 
								课室<select name="section" id="support_section" class="ui-widget-content">${sOptions}</select>
								预选产能<input type="text" id="support_count" class="ui-widget-content" value="${defaultSupportCount}">
							</div>
<% } %>
						</div>
						<input type="hidden" id="pxGridOptions" value="${pxGridOptions}">
					</form>
				</div>
				<div class="clear areaencloser dwidth-full"></div>
			</div>

<% if (isPlanner) { %>
			<div id="skkarea" class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser dwidth-full">
				<span class="areatitle">在线仕挂量</span> <a role="link" href="javascript:void(0)" class="HeaderButton areacloser"> <span class="ui-icon ui-icon-circle-triangle-n"></span>
				</a>
			</div>
			<div class="">
				<div class="ui-widget-content dwidth-full" id="sikake">
					<table class="condform">
						${sikakeTable}
					</table>
				</div>
			</div>
			<div class="clear areaencloser dwidth-full"></div>
<% } %>

			<div id="listarea" class="dwidth-full">
				<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser dwidth-full">
					<span class="areatitle">维修对象一览</span>
					<a role="link" href="javascript:void(0)" class="HeaderButton areacloser"> <span class="ui-icon ui-icon-circle-triangle-n"></span>
					</a>
					<a role="link" href="javascript:void(0)" class="HeaderButton areacloser" title="默认排序"> <span class="ui-icon ui-icon-carat-2-n-s"></span>
					</a>
				</div>
				<table id="tuli" style="width: 1250px;margin: 0;
border-collapse: collapse;border-width: 0 1px;border-color: rgb(170, 170, 170);border-style: solid;color:white;">
					<tr>
						<td width="20%" style="background-color:#FFFFFF;color:#333333;border-bottom:1px solid #1E1C4F;">未投线或距纳期2天以上</td>
						<td width="20%" style="background-color:#E4F438;color:#333333;">离纳期2天以内</td>
						<td width="20%" style="background-color:#F9E29F;color:#333333;">已超出纳期</td>
						<td width="20%" style="background-color:#E48E38;">超出纳期3天以上</td>
						<td width="20%" style="background-color:#E43838;">超出纳期7天以上</td>
					</tr>
				</table>
				<div id="chooser" class="dwidth-full ui-widget-content" style="border-bottom: 0;">
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

<% if (isPlanner) { %>
			<div id="functionarea" class="dwidth-full" style="margin: auto;">
				<div class="ui-widget-header ui-corner-all ui-helper-clearfix areabase">
					<div id="executes" style="margin-left: 4px; margin-top: 4px;">
						<!-- input type="button" class="ui-button" id="importbutton" value="导入维修对象入库预定日" />
						<input type="button" class="ui-button" id="importpartialbutton" value="导入零件入库预定日" / -->
						<!--input type="button" class="ui-button" id="decplannedbutton" value="排入分解计划" />
						<input type="button" class="ui-button" id="nsplannedbutton" value="排入NS计划" /-->
						<input type="button" class="ui-button" id="complannedbutton"value="排入总组计划" />
						<input type="button" class="ui-button" id="complanned-a-button" style="left: -7px;" value="计划未定↓" />
						<!-- input type="button" class="ui-button" id="resignbutton" value="全工程内返工" / -->
						<input type="button" class="ui-button" id="pausebutton" value="未修理返还" />
						<input type="button" class="ui-button" id="forbutton" value="移出维修流水线" />
						<input type="button" class="ui-button" id="reportbutton" value="输出总览计划表 " style="float: right; right: 2px" />
						<input id="pxbutton" type="button" value="切线" class="ui-button"/>
					</div>
				</div>
				<div class="clear areaencloser"></div>
			</div>
<% } %>
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
					<!--span style="float: left;margin:0 0 0 40px;" class="areatitle">
						<input type="button" id="tuukou" class="ui-button" value="》未定待另行安排">
					</span-->
				</div>
				<table id="tuli_2" style="width: 1250px;margin: 0;background-color:white;
					border-collapse: collapse;border-width: 0 1px;border-color: rgb(170, 170, 170);border-style: solid;color:black;">
					<tr>
						<td width="20%">计划时段说明:</td>
						<td width="20%">A : 8:45 ~ 10:45</td>
						<td width="20%">B : 10:45 ~ 13:30</td>
						<td width="20%">C : 13:30 ~ 15:15</td>
						<td width="20%">D : 15:15 ~ 17:20 (或加班结束)</td>
					</tr>
				</table>
				<div style="height: 44px; border-bottom: 0;" id="infoes" class="dwidth-full ui-widget-content">
				<div>
					<input type="radio" name="schedule_line" class="ui-button ui-corner-up" id="planallbutton" value="" role="button" checked><label for="planallbutton">全部工程</label>
					<input type="radio" name="schedule_line" class="ui-button ui-corner-up" id="plandecbutton" value="12" role="button"><label for="plandecbutton">分解</label>
					<input type="radio" name="schedule_line" class="ui-button ui-corner-up" id="plannsbutton" value="13" role="button"><label for="plannsbutton">NS</label>
					<input type="radio" name="schedule_line" class="ui-button ui-corner-up" id="plancombutton" value="14" role="button"><label for="plancombutton">总组</label>
					<input type="radio" name="schedule_section" class="ui-button ui-corner-up" id="section_all_button" value="" role="button" checked><label for="section_all_button">全部课室</label>
					${scheduleSections}
					<input type="hidden" id="select_line">
				</div>
				</div>
				<table id="planned_list"></table>
				<div id="planned_listpager"></div>
				<div class="clear areaencloser"></div>
			</div>

<% if (isPlanner) { %>
			<div id="planned_functionarea" class="dwidth-full" style="margin: auto;">
				<div class="ui-widget-header ui-corner-all ui-helper-clearfix areabase">
					<div id="executes" style="margin-left: 4px; margin-top: 4px;position:relative;">
						<input type="button" class="ui-button" id="removefromplanbutton" value="删除" />
						<input type="button" class="ui-button" id="resetPeriodButton" value="重设计划时段" />
						<input type="button" class="ui-button" id="todayreportbutton" value="输出已排计划表" style="float: right; right: 2px" />
						<div id="resetPeriodContent" style="position:absolute;display:none;top: 0;left: 180px;">
							<select id="sel_period">
								<option value="0">(无时段)</option>
								<option value="1">A</option>
								<option value="2">B</option>
								<option value="3">C</option>
								<option value="4">D</option>
							</select>
						</div>
					</div>
				</div>
			</div>
<% } %>
		</div>

		<div id="process_dialog"></div>
		<div id="nogood_treat"></div>
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
	<div class="referchooser ui-widget-content" id="pReferChooser2" tabindex="-1">
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
