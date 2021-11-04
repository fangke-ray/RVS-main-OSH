<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath()+"/";
%>
<base href="<%=basePath%>">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

<!--link rel="stylesheet" type="text/css" href="css/custom.css">
<link rel="stylesheet" type="text/css" href="css/olympus/jquery-ui-1.9.1.custom.css">

<script type="text/javascript" src="js/jquery-1.8.2.min.js"></script>
<script type="text/javascript" src="js/jquery-ui-1.9.1.custom.min.js"></script>
<script type="text/javascript" src="js/jquery.validate.min.js"></script>
<script type="text/javascript" src="js/jquery.dialog.js"></script>
<script type="text/javascript" src="js/jquery.jqGrid.min.js"></script>
<script type="text/javascript" src="js/i18n/grid.locale-cn.js"></script>
<script type="text/javascript" src="js/utils.js"></script>
<script type="text/javascript" src="js/jquery-plus.js"></script>
<script type="text/javascript" src="js/jquery.select2buttons.js"></script-->


<link rel="stylesheet" type="text/css" href="css/ui.jqgrid.css">
<link rel="stylesheet" type="text/css" href="css/olympus/select2Buttons.css">
<link rel="stylesheet" type="text/css" href="css/partial/instruction_sheets.css">

<script type="text/javascript" src="js/partial/material_part_instruct.js"></script>
<script type="text/javascript" src="js/partial/common/instruction_sheets.js"></script>

<title>工作指示单信息</title>
</head>

<div id="searcharea" class="dwidth-middleright">
	<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser dwidth-middleright">
		<span class="areatitle">检索条件</span>
		<a role="link" href="javascript:void(0)" class="HeaderButton areacloser">
			<span class="ui-icon ui-icon-circle-triangle-n"></span>
		</a>
	</div>
	<div class="ui-widget-content dwidth-middleright">
		<form id="searchform" method="POST">
			<table class="condform">
				<tr>
					<td class="ui-state-default td-title">修理单号</td>
					<td class="td-content"><input type="text" id="search_sorc_no" maxlength="15" class="ui-widget-content"></td>
					<td class="ui-state-default td-title">维修对象型号</td>
					<td class="td-content">
						<input type="text" class="ui-widget-content" readonly="readonly" id="txt_modelname">
						<input type="hidden" name="modelname" id="search_model_id">
					</td>
					<td class="ui-state-default td-title">机身号</td>
					<td class="td-content"><input type="text" id="search_serial_no" maxlength="20" class="ui-widget-content"></td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">等级</td>
					<td class="td-content">
						<select id="search_level" class="ui-widget-content">
							${lOption}
						</select>
					</td>
					<td class="ui-state-default td-title">进展</td>
					<td class="td-content">
						<select id="search_procedure" class="ui-widget-content" multiple>
							${procedureOption}
						</select>
					</td>
					<td class="ui-state-default td-title">订购状态</td>
					<td class="td-content">
						<select id="search_order_step" class="ui-widget-content">
							<option value="" >(全部)</option>
							<option value="1">未订购</option>
							<option value="2">已订购</option>
							<option value="3">仅首次订购</option>
							<option value="4">有追加订购</option>
						</select>
					</td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">维修课室</td>
					<td class="td-content">
						<select id="search_section" class="ui-widget-content">
							${sOption}
						</select>
					</td>
					<td class="ui-state-default td-title">检索范围</td>
					<td class="td-content">
						<select id="search_range" class="ui-widget-content">
							<option value="1">在线维修</option>
							<option value="2">历史</option>
						</select>
					</td>
					<td class="ui-state-default td-title"></td>
					<td class="td-content">
					</td>
				</tr>
			</table>
				<div style="height:44px">
					<input type="hidden" id="defaultCondProcedure" value="${defaultCondProcedure}">
					<input type="hidden" id="goMaterial_level" value="${goMaterial_level}">
					<input type="hidden" id="goProcedure" value="${goProcedure}">
					<input type="button" class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="resetbutton" value="清除" role="button" aria-disabled="false" style="float:right;right:2px">
					<input type="button" class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="searchbutton" value="检索" role="button" aria-disabled="false" style="float:right;right:2px">
				</div>
			</form>
	</div>
	<div class="clear areaencloser dwidth-middleright"></div>
</div>

<% 
String privacy = (String) request.getAttribute("privacy"); 
%>

<div id="listarea" class="dwidth-middleright">

	<table id="list"></table>
	<div id="listpager"></div>
<% 
if (privacy != null) {
%>
	<div class="clear"></div>
<% 
} else {
%>
	<div class="clear areaencloser"></div>
<% 
}
%>
</div>

<% 
if (privacy != null) {
%>
<div id="functionarea" class="dwidth-middleright" style="margin:auto;">
	<div class="ui-widget-header ui-corner-all ui-helper-clearfix areabase">
		<div id="executes" style="margin-left:4px;margin-top:4px;">
		<% if("pm".equals(privacy)) { %>
			<input type="button" class="ui-button" id="report_button" value="零件订购单导出" style="float:right;margin-right: 6px;"/>
			<input type="button" class="ui-button" id="focus_button" value="关注零件设置"/>
			<input type="button" class="ui-button" id="additional_order_button" value="零件追加单确认" style="float:right;margin-right: 6px;"/>
		<% } else if("fact".equals(privacy)) { %>
			<input type="button" class="ui-button" id="report_button" value="零件订购单导出" style="float:right;margin-right: 6px;"/>
		<% } else if("line".equals(privacy)) { %>
			<input type="button" class="ui-button" id="focus_button" value="关注零件设置"/>
			<input type="button" class="ui-button" id="additional_order_button" value="零件追加单确认" style="float:right;margin-right: 6px;"/>
		<% } %>
		</div>
	</div>
</div>

<% 
}
%>

<%
if ("pm".equals(privacy) || "line".equals(privacy)) {
%>
<div id='instruct_confirm_dialog' style="display:none;">
	<table class="condform">
		<tr>
			<td class="ui-state-default td-title">修理单号</td>
			<td class="td-content" style="width:110px"><label id="confirm_sorc_no"></td>
			<td class="ui-state-default td-title">维修对象型号</td>
			<td class="td-content" colspan=3><label id="confirm_model_name"></td>
		</tr>
		<tr>
			<td class="ui-state-default td-title">机身号</td>
			<td class="td-content" style="width:110px"><label id="confirm_serial_no"></td>
			<td class="ui-state-default td-title" rowspan=2>报价追加零件</td>
			<td class="td-content"colspan=3 rowspan=2 style="width:550px"><textarea readonly style="resize: none;width: 90%;border: none;"></textarea></td>
		</tr>
		<tr>
			<td class="ui-state-default td-title">修理等级</td>
			<td class="td-content" style="width:110px"><label id="confirm_level"></td>
		</tr>
		<tr>
			<td class="ui-state-default td-title">损金预估</td>
			<td class="td-content" style="width:110px" colspan=5><label id="confirm_loss_detail"></td>
		</tr>
	</table>

	<style>
		#instruct_confirm_inline_addition tr[consumable] td {
			background-color:#FFA890;
		}
		#instruct_confirm_inline_addition tr[consumable] td[confirm] {
			background-color:transparent;
		}
		#instruct_confirm_inline_addition td {
			border : 1px solid #aaaaaa;
			padding: 0 2px;
		}
		#instruct_confirm_inline_addition td[align='r'] {
			text-align:right;
		}
		#instruct_confirm_inline_addition td[align='c'] {
			text-align:center;
		}
		#instruct_confirm_inline_addition td.noticeprice {
			text-decoration: underline double crimson;
		}
		#instruct_confirm_inline_addition td.focus {
			position: relative;
		}
		#instruct_confirm_inline_addition td.focus:before {
			position: absolute;
			content: '★';
			color: gold;
			left: -1em;
		}
	</style>
	<div class="ui-widget-header ui-corner-top ui-helper-clearfix">
		<span class="ui-dialog-title" class="padding:2px 1em;">在线追加零件一览</span>
	</div>
	<table id="instruct_confirm_inline_addition" cellspacing="0" cellpadding="0" border="0">
		<thead>
			<tr class="ui-jqgrid-labels" role="rowheader">
				<th for="code" class="ui-state-default ui-th-column" style="width: 60px;">零件代码</th>
				<th for="name" class="ui-state-default ui-th-column" style="width: 150px;">说明</th>
				<th for="quantity" class="ui-state-default ui-th-column" style="width: 30px">数量</th>
				<th for="price" class="ui-state-default ui-th-column" style="width: 60px">单价</th>
				<th for="inline_job_no" class="ui-state-default ui-th-column" style="width: 80px">生产线编辑者</th>
				<th for="process_code" class="ui-state-default ui-th-column" style="width: 60px">使用定位</th>
				<th for="inline_comment" class="ui-state-default ui-th-column" style="width: 180px">追加/取消理由</th>
				<th for="inline_comment" class="ui-state-default ui-th-column" style="width: 70px">确认者</th>
			</tr>
		</thead>
		<tbody>
		</tbody>
	</table>
</div>
<% } %>

<div class="referchooser ui-widget-content" tabindex="-1">
	<table>
		<tr>
			<td width="50%">过滤字:<input type="text"/></td>	
			<td width="50%" align="right"><input type="button" class="ui-button" style="float:right;" value="清空"/></td>
		</tr>
	</table>
	<table class="subform">${mReferChooser}</table>
</div>

</html>
