<%@page import="framework.huiqing.common.util.CodeListUtils"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath()+"/";
%>
<base href="<%=basePath%>">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
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
<script type="text/javascript" src="js/partial/bo_partial.js"></script>
<script type="text/javascript" src="js/common/material_detail_ctrl.js"></script>

<title>零件订购信息</title>
</head>
<body class="outer">
<div class="width-full" style="align:center;margin:auto;margin-top:16px;">
	<div id="basearea" class="dwidth-full" style="margin: auto;">
			<jsp:include page="/header.do" flush="true">
				<jsp:param name="part" value="1"/>
				<jsp:param name="sub" value="p"/>
			</jsp:include>
	</div>

<div class="ui-widget-panel ui-corner-all width-full" style="align:center;padding-top:16px;padding-bottom:16px;" id="body-1">

<div id="searcharea" class="dwidth-full">
	<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser dwidth-full">
		<span class="areatitle">检索条件</span>
		<a role="link" href="javascript:void(0)" class="HeaderButton areacloser">
			<span class="ui-icon ui-icon-circle-triangle-n"></span>
		</a>
	</div>
	<div class="ui-widget-content dwidth-full">
		<form id="searchform" method="POST">
			<table class="condform">
				<tr>
					<td class="ui-state-default td-title">修理单号</td>
					<td class="td-content"><input type="text" id="search_sorc_no" maxlength="15" class="ui-widget-content"></td>
					<td class="ui-state-default td-title">维修对象型号</td>
					<td class="td-content">
						<input type="text" class="ui-widget-content" readonly="readonly" id="txt_modelname">
						<input type="hidden" name="modelname" id="search_modelname">
					</td>
					<td class="ui-state-default td-title">机身号</td>
					<td class="td-content"><input type="text" id="search_serial_no" maxlength="20" class="ui-widget-content"></td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">等级</td>
					<td class="td-content" id="cond_level">
						<select id="search_level" class="ui-widget-content">
							<%=CodeListUtils.getSelectOptions("material_level_inline", null, "全部", false) %>
						</select>
					</td>
					<td class="ui-state-default td-title">零件订购信息</td>
						<td class="td-content">
						<!-- 
							<div id="cond_work_procedure_order_template_flg_set" class="ui-buttonset">
								<input type="radio" name="bo" id="cond_work_procedure_order_template_a" class="ui-widget-content ui-helper-hidden-accessible" value="" checked="checked"><label for="cond_work_procedure_order_template_a" aria-pressed="false">全部</label>
								<input type="radio" name="bo" id="cond_work_procedure_order_template_b" class="ui-widget-content ui-helper-hidden-accessible" value="9"><label for="cond_work_procedure_order_template_b" aria-pressed="false">未签收 </label> 
								<input type="radio" name="bo" id="cond_work_procedure_order_template_c" class="ui-widget-content ui-helper-hidden-accessible" value="1"><label for="cond_work_procedure_order_template_c" aria-pressed="false">有BO </label> 
								<input type="radio" name="bo" id="cond_work_procedure_order_template_d" class="ui-widget-content ui-helper-hidden-accessible" value="0"><label for="cond_work_procedure_order_template_d" aria-pressed="false">无BO </label> 
								<input type="radio" name="bo" id="cond_work_procedure_order_template_e" class="ui-widget-content ui-helper-hidden-accessible" value="2"><label for="cond_work_procedure_order_template_e" aria-pressed="false">BO解决 </label> 
								<input type="hidden" id="cond_work_procedure_order_template">
							</div>
							 -->
							<select id="search_partial_reception_flg" class="ui-widget-content">
								<option value="" >(全部)</option>
								<option value="9">未签收</option>
								<option value="1">有BO</option>
								<option value="0">无BO</option>
								<option value="2">BO解决</option>
							</select>
					</td>
					<td class="ui-state-default td-title">检索范围</td>
					<td class="td-content">
						<select id="search_range" class="ui-widget-content">
								<option value="">全部</option>
								<option value="1">在线维修</option>
								<option value="2">历史</option>
						</select>
					</td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">零件订购日</td>
					<td class="td-content">
						<input type="text" id="search_order_date_start" maxlength="50" class="ui-widget-content" readonly="readonly">起<br>
						<input type="text" id="search_order_date_end" maxlength="50" class="ui-widget-content" readonly="readonly">止
					</td>
					<td class="ui-state-default td-title">零件到货日</td>
					<td class="td-content">
						<input type="text" id="search_arrival_date_start" maxlength="50" class="ui-widget-content" readonly="readonly">起<br>
						<input type="text" id="search_arrival_date_end" maxlength="50" class="ui-widget-content" readonly="readonly">止
					</td>
					<td class="ui-state-default td-title">入库预定日</td>
					<td class="td-content">
						<input type="text" id="search_arrival_plan_date_start" maxlength="50" class="ui-widget-content" readonly="readonly">起<br>
						<input type="text" id="search_arrival_plan_date_end" maxlength="50" class="ui-widget-content" readonly="readonly">止
					</td>
				</tr>

			<tr>
					<td class="ui-state-default td-title">所属梯队</td>
					<td class="td-content">
						<select id="search_echelon" class="ui-widget-content">
							<option value="" >(全部)</option>
							<option value="1">第一梯队</option>
							<option value="2">第二梯队</option>
							<option value="3">第三梯队</option>
							<option value="4">第四梯队</option>
						</select>
					</td>
					<td class="ui-state-default td-title">课室</td>
					<td class="td-content">
						<select name="section" id="search_section" class="ui-widget-content">
							${sOptions}
						</select>
					</td>
					<td class="ui-state-default td-title">出货日</td>
					<td class="td-content">
						<input type="text" id="search_outline_date_start" maxlength="50" class="ui-widget-content" readonly="readonly">起<br>
						<input type="text" id="search_outline_date_end" maxlength="50" class="ui-widget-content" readonly="readonly">止
					</td>
					</td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">发生BO工程</td>
					<td class="td-content">
						<select id="search_bo_occur_line" class="ui-widget-content">
						    <option value="">(全部)</option>
							<option value="12">分解</option>
							<option value="13">NS</option>
							<option value="14">总组</option>
						</select>
					</td>
					<td class="ui-state-default td-title">发生缺品零件</td>
					<td class="td-content">
						<input type="text" id="search_partial_code" maxlength="50" class="ui-widget-content">
						<input type="hidden" id="search_partial_id" class="ui-widget-content">
					</td>
					<td class="ui-state-default td-title">订单次数</td>
					<td class="td-content">
						<div id="occur_times_set" class="ui-buttonset">
							<input type="radio" name="occur_times" id="cond_occur_times_a" class="ui-widget-content ui-helper-hidden-accessible" value="" checked="checked"><label for="cond_occur_times_a" aria-pressed="false">全部</label>
							<input type="radio" name="occur_times" id="cond_occur_times_1" class="ui-widget-content ui-helper-hidden-accessible" value="1"><label for="cond_occur_times_1" aria-pressed="false">１次 </label> 
							<input type="radio" name="occur_times" id="cond_occur_times_2" class="ui-widget-content ui-helper-hidden-accessible" value="2"><label for="cond_occur_times_2" aria-pressed="false">追加 </label> 
						</div>
					</td>
				</tr>
			</table>
					<div style="height:44px">
						<input type="button" class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="resetbutton" value="清除" role="button" aria-disabled="false" style="float:right;right:2px">
						<input type="button" class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="searchbutton" value="检索" role="button" aria-disabled="false" style="float:right;right:2px">
					</div>
			</form>
	</div>
	<div class="clear areaencloser dwidth-full"></div>
</div>

<div id="listarea" class="dwidth-full">

	<table id="list"></table>
	<div id="listpager"></div>
	<div class="clear areaencloser"></div>
</div>

<% 
String privacy = (String) request.getAttribute("privacy"); 
if (privacy != null) {
%>
<div id="functionarea" class="dwidth-full" style="margin:auto;">
	<div class="ui-widget-header ui-corner-all ui-helper-clearfix areabase">
		<div id="executes" style="margin-left:4px;margin-top:4px;">
		<% if("pm".equals(privacy)) { %>
			<input type="button" class="ui-button" id="reportbobutton2" value="BO零件一览表导出" style="float:right;margin-right: 6px;"/>
			<input type="button" class="ui-button" id="reportbobutton" value="维修对象订购一览表导出" style="float:right;margin-right: 6px;"/>
			<input type="button" class="ui-button" id="reportappendbutton" value="零件追加明细表导出" style="float:right;margin-right: 6px;"/>
			<input type="button" class="ui-button" id="snoutbutton" value="预制替代"/>
		<% } else if("fact".equals(privacy)) { %>
			<input type="button" class="ui-button" id="editbobutton" value="零件信息"/>
			<input type="button" class="ui-button" id="consumablebutton" value="消耗品替代"/>
			<!--input type="button" class="ui-button" id="drawbutton" value="临时调配"/-->
			<input type="button" class="ui-button" id="reportbobutton2" value="BO零件一览表导出" style="float:right;margin-right: 6px;"/>
			<input type="button" class="ui-button" id="reportbobutton" value="维修对象订购一览表导出" style="float:right;margin-right: 6px;"/>
		<% } else if("line".equals(privacy)) { %>
			<input type="button" class="ui-button" id="consumablebutton" value="消耗品替代"/>
			<!--input type="button" class="ui-button" id="drawbutton" value="临时调配"/-->
		<% } %>
			<span style="margin-right:10px;float:right;height:44px;line-height: 36px;display:hidden;" id="label_rate">
			</span>
			<span style="margin-right:10px;float:right;height:44px;line-height: 36px;display:hidden;" id="label_rate_w">
			</span>
		</div>
	</div>
</div>

<% 
}
%>

<div id="process_dialog">
</div>
<div id="consumables_dialog">
<style>
#consumables_list input[type=number] {
	text-align:right;
	width:3em;
	border: 1px solid darkgray;
}	
</style>
		<table id="consumables_list"></table>
		<div id="consumables_listpager"></div>
</div>
</div>

<div id="finish_dialog">
</div>

<div id="footarea">
</div>

<div class="referchooser ui-widget-content" tabindex="-1">
	<table>
		<tr>
			<td width="50%">过滤字:<input type="text"/></td>	
			<td width="50%" align="right"><input type="button" class="ui-button" style="float:right;" value="清空"/></td>
		</tr>
	</table>
	
	<table class="subform">${mReferChooser}</table>
</div>

</div>
</body>

</html>
