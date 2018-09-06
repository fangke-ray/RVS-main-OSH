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

<script type="text/javascript" src="js/jquery-1.8.2.min.js"></script>
<script type="text/javascript" src="js/jquery-ui-1.9.1.custom.min.js"></script>
<script type="text/javascript" src="js/jquery.validate.min.js"></script>
<script type="text/javascript" src="js/jquery.dialog.js"></script>
<script type="text/javascript" src="js/jquery.jqGrid.min.js"></script>
<script type="text/javascript" src="js/i18n/grid.locale-cn.js"></script>
<script type="text/javascript" src="js/utils.js"></script>
<script type="text/javascript" src="js/jquery-plus.js"></script>
<script type="text/javascript" src="js/admin/privacy.js"></script>
<title>系统权限检索</title>
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
					<td class="ui-state-default td-title">系统权限 ID</td>
					<td class="td-content"><input type="text" name="id" id="cond_id" maxlength="11" class="ui-widget-content"></td>
					<td class="ui-state-default td-title">系统权限名称</td>
					<td class="td-content"><input type="text" name="name" id="cond_name" maxlength="50" class="ui-widget-content"></td>
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
	<div style="margin-top:7px"><input type="button" class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="addbutton" value="新建系统权限" role="button" aria-disabled="false"></div>
</div>

<div id="editarea">
	<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser dwidth-middleright">
		<span class="areatitle"></span>
		<a role="link" href="javascript:void(0)" class="HeaderButton areacloser">
			<span class="ui-icon ui-icon-circle-triangle-w"></span>
		</a>
	</div>
	<div class="ui-widget-content dwidth-middleright">
		<form id="editform" method="POST" onsubmit="return false;">
			<table class="condform">
				<tr>
					<td class="ui-state-default td-title">系统权限 ID</td>
					<td class="td-content"><label id="label_edit_id"> - </label></td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">系统权限名称</td>
					<td class="td-content">
						<input type="text" name="name" id="input_name" alt="权限名" maxlength="16" class="ui-widget-content">
						<br>
						<label for="input_name" generated="false" class="errorarea-single" style="display:none">
					</label></td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">权限说明</td>
					<td class="td-content">
						<textarea name="comments" id="input_comments" alt="权限说明" maxlength="100" class="ui-widget-content" style="width:200px;height:132px;overflow-y:visible;"></textarea>
						<br>
						<label for="input_comments" generated="false" class="errorarea-single" style="display:none">
					</label></td>
				</tr>
			</table>
			<div style="height:44px">
				<input type="button" class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="editbutton" value="新建" role="button" aria-disabled="false" style="float:left;left:4px;">
				<input type="button" class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="cancelbutton" value="取消" role="button" aria-disabled="false" style="float:left;left:4px;">
			</div>
		</form>
	</div>
	<div class="ui-state-default ui-corner-bottom areaencloser dwidth-middleright"></div>
</div>

<div id="detailarea">
	<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser dwidth-middleright">
		<span class="areatitle">系统权限</span>
		<a role="link" href="javascript:void(0)" class="HeaderButton areacloser">
			<span class="ui-icon ui-icon-circle-triangle-w"></span>
		</a>
	</div>
	<div class="ui-widget-content dwidth-middleright">
		<table class="condform">
			<tr>
				<td class="ui-state-default td-title">系统权限 ID</td>
				<td class="td-content"><label id="label_detail_id"></label></td>
			</tr>
			<tr>
				<td class="ui-state-default td-title">系统权限名称</td>
				<td class="td-content"><label id="label_detail_name"></label></td>
			</tr>
			<tr>
				<td class="ui-state-default td-title">权限说明</td>
				<td class="td-content"><textarea id="label_detail_comments" style="width:200px;height:132px;resize: none;" disabled></textarea></td>
			</tr>
		</table>
		<div style="height:44px">
			<input type="button" class="ui-button-success ui-button ui-widget ui-state-default ui-corner-all" id="executebutton" value="实行" role="button" aria-disabled="false" style="float:left;left:4px;">
			<input type="button" class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="backbutton" value="返回" role="button" aria-disabled="false" style="float:left;left:4px;">
		</div>
	</div>
	<div class="ui-state-default ui-corner-bottom areaencloser dwidth-middleright"></div>
</div>

</body>
</html>
