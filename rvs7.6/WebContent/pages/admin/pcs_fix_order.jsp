<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
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
<link rel="stylesheet" type="text/css" href="css/olympus/select2Buttons.css">
<link rel="stylesheet" type="text/css" href="css/ui.jqgrid.css">

<script type="text/javascript" src="js/jquery-1.8.2.min.js"></script>
<script type="text/javascript" src="js/jquery-ui-1.9.1.custom.min.js"></script>
<script type="text/javascript" src="js/jquery.validate.min.js"></script>
<script type="text/javascript" src="js/jquery.dialog.js"></script>
<script type="text/javascript" src="js/jquery.jqGrid.min.js"></script>
<script type="text/javascript" src="js/i18n/grid.locale-cn.js"></script>
<script type="text/javascript" src="js/utils.js"></script>
<script type="text/javascript" src="js/jquery.select2buttons.js"></script>
<script type="text/javascript" src="js/jquery-plus.js"></script>
<script type="text/javascript" src="js/common/pcs_editor.js"></script>
<script type="text/javascript" src="js/common/material_detail_pcs_fix.js"></script>
<script type="text/javascript" src="js/admin/pcs_fix_order.js"></script>
<title>工程检查票输入修正</title>
</head>
<body>

<div id="waiting_tab">

<div id="choosearea">

	<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser dwidth-middleright">
		<span class="areatitle">工程指定</span>
		<a role="link" href="javascript:void(0)" class="HeaderButton areacloser">
			<span class="ui-icon ui-icon-circle-triangle-n"></span>
		</a>
	</div>
	<div class="ui-widget-content dwidth-middleright">
		<form id="lineform" method="POST" onsubmit="return false;">
			<table class="condform">
				<tr>
					<td class="td-content" style="width:100%;">
						<div id="sel_line_set" class="ui-buttonset">
							<input type="radio" name="status" id="sel_line_n" class="ui-widget-content ui-helper-hidden-accessible" value="" checked="checked"><label for="sel_line_n" aria-pressed="false">(按提交者工程)</label>
							<input type="radio" name="status" id="sel_line_12" class="ui-widget-content ui-helper-hidden-accessible" value="00000000012"><label for="sel_line_12" aria-pressed="false">分解工程</label>
							<input type="radio" name="status" id="sel_line_13" class="ui-widget-content ui-helper-hidden-accessible" value="00000000013"><label for="sel_line_13" aria-pressed="false">ＮＳ工程</label>
							<input type="radio" name="status" id="sel_line_14" class="ui-widget-content ui-helper-hidden-accessible" value="00000000014"><label for="sel_line_14" aria-pressed="false">总组工程</label>
							<input type="radio" name="status" id="sel_line_15" class="ui-widget-content ui-helper-hidden-accessible" value="00000000015"><label for="sel_line_15" aria-pressed="false">品保工程</label>
						</div>
					</td>
				</tr>
			</table>
		</form>
	</div>
	<div class="clear dwidth-middleright"></div>

</div>

<div id="listarea" class="width-middleright">
	<table id="waitinglist"></table>
	<div id="waitinglistpager"></div>
</div>

</div>
<div id="complete_tab" style="display:none;">
<div id="searcharea">

	<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser dwidth-middleright">
		<span class="areatitle">检索条件</span>
		<a role="link" href="javascript:void(0)" class="HeaderButton areacloser">
			<span class="ui-icon ui-icon-circle-triangle-n"></span>
		</a>
	</div>
	<div class="ui-widget-content dwidth-middleright">
		<form id="searchform" method="POST" onsubmit="return false;">
			<table class="condform">
				<tr>
					<td class="ui-state-default td-title">修理单号</td>
					<td class="td-content"><input type="text" name="sorc_no" id="cond_sorc_no" maxlength="15" class="ui-widget-content"></select></td>
					<td class="ui-state-default td-title">修正需求</td>
					<td class="td-content"><input type="text" name="comment" id="cond_comment" maxlength="200" class="ui-widget-content"></td>
					<td class="ui-state-default td-title">提出人</td>
					<td class="td-content">
						<input type="hidden" name="sender_id" id="cond_sender_id" maxlength="200" class="ui-widget-content">
						<input type="text" id="text_sender_name" readonly class="ui-widget-content">
					</td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">处理方式</td>
					<td class="td-content">
						<div id="cond_status_set" class="ui-buttonset">
							<input type="radio" name="status" id="cond_status_r" class="ui-widget-content ui-helper-hidden-accessible" value="1" checked="checked"><label for="cond_status_r" aria-pressed="false">已处理</label>
							<input type="radio" name="status" id="cond_status_f" class="ui-widget-content ui-helper-hidden-accessible" value="2"><label for="cond_status_f" aria-pressed="false">拒绝处理</label>
						</div>
					</td>
					<td class="ui-state-default td-title">处理时间</td>
					<td class="td-content">
						<input type="text" id="cond_update_time_start" maxlength="50" class="ui-widget-content" readonly="readonly" value="${today_date}">起<br/>
						<input type="text" id="cond_update_time_end" maxlength="50" class="ui-widget-content" readonly="readonly">止</td>
					<td class="ui-state-default td-title"></td>
					<td class="td-content"></td>
				</tr>
			</table>
			<div style="height:44px">
				<input type="button" class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="resetbutton" value="清除" role="button" aria-disabled="false" style="float:right;right:2px">
				<input type="button" class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="searchbutton" value="检索" role="button" aria-disabled="false" style="float:right;right:2px">
			</div>
		</form>
	</div>
	<div class="clear dwidth-middleright"></div>

</div>
</div>

<div id="listarea_c" class="width-middleright">
	<table id="completelist"></table>
	<div id="completelistpager"></div>
</div>

</div>

</body>
</html>
