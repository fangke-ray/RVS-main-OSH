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

<script type="text/javascript" src="js/jquery-1.8.2.min.js"></script>
<script type="text/javascript" src="js/jquery-ui-1.9.1.custom.min.js"></script>
<script type="text/javascript" src="js/jquery.validate.min.js"></script>
<script type="text/javascript" src="js/jquery.dialog.js"></script>
<script type="text/javascript" src="js/jquery.jqGrid.min.js"></script>
<script type="text/javascript" src="js/i18n/grid.locale-cn.js"></script>
<script type="text/javascript" src="js/utils.js"></script>
<script type="text/javascript" src="js/jquery-plus.js"></script>
<script type="text/javascript" src="js/admin/section.js"></script>
<title>课室检索</title>
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
					<td class="ui-state-default td-title">课室 ID</td>
					<td class="td-content"><input type="text" name="id" id="cond_id" maxlength="11" class="ui-widget-content"></td>
					<td class="ui-state-default td-title">课室名称</td>
					<td class="td-content"><input type="text" name="name" id="cond_name" maxlength="50" class="ui-widget-content"></td>
				</tr>
				<tr>
				<tr>
					<td class="ui-state-default td-title">是否在线维修课室</td>
					<td class="td-content" id="cond_inline_flg_set">
						<input type="radio" name="inline_flg" alt="是否在线维修课室" id="cond_inline_flg_all" value="" class="ui-widget-content" checked>
						<label for="cond_inline_flg_all" radio>(全部)</label>
						<input type="radio" name="inline_flg" alt="是否在线维修课室" id="cond_inline_flg_yes" value="1" class="ui-widget-content">
						<label for="cond_inline_flg_yes" radio>是</label>
					</td>
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
		<form id="editform" method="POST" onsubmit="return false;">
			<div style="float:left;">
			<table class="condform">
				<tr>
					<td class="ui-state-default td-title">课室 ID</td>
					<td class="td-content"><label id="label_edit_id"> - </label></td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">课室名称</td>
					<td class="td-content">
						<input type="text" name="name" id="input_name" maxlength="20" class="ui-widget-content">
					</td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">是否在线维修课室</td>
					<td class="td-content" id="input_inline_flg_set">
						<input type="radio" name="inline_flg" alt="是否在线维修课室" id="input_inline_flg_yes" value="1" class="ui-widget-content" checked>
						<label for="input_inline_flg_yes" radio>是</label>
						<input type="radio" name="inline_flg" alt="是否在线维修课室" id="input_inline_flg_no" value="0" class="ui-widget-content">
						<label for="input_inline_flg_no" radio>不是</label>
					</td>
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
			<div style="float:left;max-width:620px;">
			<table class="subform" id="grid_edit_positions">
				<tr>
					<th class="ui-state-default td-title" colspan="3" style="min-width:220px;">拥有工位</th>
				</tr>
				<%=request.getAttribute("pReferChooser")%>
			</table>
			</div>
			<div class="clear" style="height:44px">
				<input type="button" class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="editbutton" value="新建" role="button" aria-disabled="false" style="float:left;left:4px;">
				<input type="button" class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="cancelbutton" value="取消" role="button" aria-disabled="false" style="float:left;left:4px;">
			</div>
		</form>
	</div>
	<div class="ui-state-default ui-corner-bottom areaencloser dwidth-middleright"></div>
</div>
<div id="confirmmessage"></div>
</body>
</html>
