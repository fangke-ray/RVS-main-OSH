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
<link rel="stylesheet" type="text/css" href="css/ui.jqgrid.css">
<link rel="stylesheet" type="text/css" href="css/flowchart.css">
<link rel="stylesheet" type="text/css" href="css/olympus/select2Buttons.css">
<script type="text/javascript" src="js/jquery-1.8.2.min.js"></script>
<script type="text/javascript" src="js/jquery-ui-1.9.1.custom.min.js"></script>
<script type="text/javascript" src="js/jquery.validate.min.js"></script>
<script type="text/javascript" src="js/jquery.dialog.js"></script>
<script type="text/javascript" src="js/jquery.jqGrid.min.js"></script>
<script type="text/javascript" src="js/jquery.flowchart.js"></script>
<script type="text/javascript" src="js/jquery.select2buttons.js"></script>
<script type="text/javascript" src="js/i18n/grid.locale-cn.js"></script>
<script type="text/javascript" src="js/utils.js"></script>
<script type="text/javascript" src="js/jquery-plus.js"></script>
<script type="text/javascript" src="js/admin/process_assign_template.js"></script>
<title>工序指派模板管理</title>
</head>
<body>

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
					<td class="ui-state-default td-title">模板名称</td>
					<td class="td-content"><input type="text" name="name" alt="模板名称" id="cond_name" maxlength="50" class="ui-widget-content"></td>
					<td class="ui-state-default td-title">模板派生类</td>
					<td class="td-content"><select id="cond_derive_kind" name="derive_kind">${dkOptions}</select></td>
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

<div id="listarea" class="width-middleright">
	<table id="list"></table>
	<div id="listpager"></div>
</div>

<div id="editarea" style="display:none;">
	<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser dwidth-middleright">
		<span class="areatitle"></span>
		<a role="link" href="javascript:void(0)" class="HeaderButton areacloser">
			<span class="ui-icon ui-icon-circle-triangle-w"></span>
		</a>
	</div>
	<div class="ui-widget-content dwidth-middleright">
		<form id="editform">
			<table class="condform" style="width:100%;">
				<tr>
					<td class="ui-state-default td-title">模板ID</td>
					<td class="td-content"><label id="label_edit_id"></td>
					<td class="ui-state-default td-title">模板名称</td>
					<td class="td-content" colspan="3"><input type="text" name="name" alt="模板名称" id="edit_name" maxlength="50" class="ui-widget-content"></td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">模板派生类</td>
					<td class="td-content" colspan="3"><select id="edit_derive_kind" name="derive_kind">${dkOptions}</select></td>
					<td class="ui-state-default td-title">流程使用方式</td>
					<td class="td-content"><select id="edit_fix_type" name="fix_type">${ftOptions}</select></td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">模板派生来源</td>
					<td class="td-content" colspan="7"><select id="edit_derive_from_id" name="derive_from_id">${paOptions}</select></td>
				</tr>
			</table>
			<div id="base">
			</div>
		</form>
		<div style="height:44px">
			<input type="button" class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="savebutton" value="保存" role="button" aria-disabled="false" style="float:right;right:2px">
			<input type="button" class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="referbutton" value="参照读入" role="button" aria-disabled="false" style="float:right;right:2px">
		</div>
	</div>

	<div class="ui-state-default ui-corner-bottom areaencloser dwidth-middleright"></div>
</div>

<div id="paChooseRefer" style="display:none;">
	<select>${paOptions}</select>
</div>
<div id="confirmmessage"/>

</body></html>