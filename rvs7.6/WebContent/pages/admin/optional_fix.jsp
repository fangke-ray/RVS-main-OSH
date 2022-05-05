<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
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
<script type="text/javascript" src="js/admin/optional_fix.js"></script>
<title>选择修理检索</title>
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
				<tbody>
				<tr>
					<td class="ui-state-default td-title">选择修理 ID</td>
					<td class="td-content"><input type="text" name="optional_fix_id" id="cond_optional_fix_id" maxlength="11" class="ui-widget-content"></td>
					<td class="ui-state-default td-title">检查项目</td>
					<td class="td-content"><input type="text" name="infection_item" id="cond_infection_item" maxlength="64" class="ui-widget-content"></td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">检查标准号</td>
					<td class="td-content"><input type="text" name="standard_code" id="cond_standard_code" maxlength="8" class="ui-widget-content"></td>
					<td class="ui-state-default td-title">修理等级</td>
					<td class="td-content"><select name="rank" id="cond_rank" class="ui-widget-content">${rOptions}</select></td>
				</tr>
				</tbody>
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

<div id="addarea" style="display:none;">
	<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser dwidth-middleright">
		<span class="areatitle"></span>
		<a role="link" href="javascript:void(0)" class="HeaderButton areacloser">
			<span class="ui-icon ui-icon-circle-triangle-w"></span>
		</a>
	</div>
	<div class="ui-widget-content dwidth-middleright">
		<form id="addform" method="POST" onsubmit="return false;">
			<div style="float:left;">
			<table class="condform">
				<tr>
					<td class="ui-state-default td-title">检查标准号</td>
					<td class="td-content"><input type="text" id="add_standard_code" name="standard_code" alt="检查标准号" maxlength="8" class="ui-widget-content"></td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">检查项目</td>
					<td class="td-content"><input type="text" id="add_infection_item" name="infection_item" alt="检查项目" maxlength="64" class="ui-widget-content"></td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">修理等级</td>
					<td class="td-content"><select multiple name="rank" id="add_rank" class="ui-widget-content">${rOptions}</select></td>
				</tr>
			</table>
			</div>
			<div class="clear" style="height:44px">
				<input type="button" class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="addbutton" value="新建" role="button" aria-disabled="false" style="float:left;left:4px;">
				<input type="button" class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="addcancelbutton" value="取消" role="button" aria-disabled="false" style="float:left;left:4px;">
			</div>
		</form>
	</div>
	<div class="ui-state-default ui-corner-bottom areaencloser dwidth-middleright"></div>
</div>

<div id="editarea" style="display:none;">
	<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser dwidth-middleright">
		<span class="areatitle"></span>
		<a role="link" href="javascript:void(0)" class="HeaderButton areacloser">
			<span class="ui-icon ui-icon-circle-triangle-w"></span>
		</a>
	</div>
	<div class="ui-widget-content dwidth-middleright">
		<form id="editform" method="POST" onsubmit="return false;">
			<input type="hidden" id="edit_optional_fix_id" value=""/>
			<div style="float:left;">
			<table class="condform">
				<tr>
					<td class="ui-state-default td-title">检查标准号</td>
					<td class="td-content"><input type="text" id="edit_standard_code" name="standard_code" alt="检查标准号" maxlength="8" class="ui-widget-content"></td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">检查项目</td>
					<td class="td-content"><input type="text" id="edit_infection_item" name="infection_item" alt="检查项目" maxlength="64" class="ui-widget-content"></td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">修理等级</td>
					<td class="td-content"><select multiple name="rank" id="edit_rank" class="ui-widget-content">${rOptions}</select></td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">最后更新人</td>
					<td class="td-content"><label id="label_edit_updated_by"></label></td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">最后更新时间</td>
					<td class="td-content"><label id="label_edit_updated_time"></label></td>
				</tr>
			</table>
			</div>
			<div class="clear" style="height:44px">
				<input type="button" class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="editbutton" value="修改" role="button" aria-disabled="false" style="float:left;left:4px;">
				<input type="button" class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="editcancelbutton" value="取消" role="button" aria-disabled="false" style="float:left;left:4px;">
			</div>
		</form>
	</div>
	<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser dwidth-middleright">
		<span class="areatitle">检查票样式</span>
	</div>
	<div class="ui-widget-content dwidth-middleright">
		<div id="pcs_empty" style="padding: 1.5em;line-height: 2em;font-size: 1.25em;">
			此选择维修项目暂无相关检查票，<br>请使用《工程检查票修正履历》画面功能<br>将检查票模板以检查项目名称的文件名<br>上传到“报价选择维修”的类型中。
		</div>
	</div>
	<div class="ui-state-default ui-corner-bottom areaencloser dwidth-middleright"></div>
</div>

<div class="clear areacloser"></div>

</body>
</html>
