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
<style>
.navli {
	background-color:gray;
}
</style>

<script type="text/javascript" src="js/jquery-1.8.2.min.js"></script>
<script type="text/javascript" src="js/jquery-ui-1.9.1.custom.min.js"></script>
<script type="text/javascript" src="js/jquery.validate.min.js"></script>
<script type="text/javascript" src="js/jquery.dialog.js"></script>
<script type="text/javascript" src="js/jquery.jqGrid.min.js"></script>
<script type="text/javascript" src="js/i18n/grid.locale-cn.js"></script>
<script type="text/javascript" src="js/jquery.select2buttons.js"></script>
<script type="text/javascript" src="js/utils.js"></script>
<script type="text/javascript" src="js/jquery-plus.js"></script>
<script type="text/javascript" src="js/admin/role.js"></script>
<title>角色检索</title>
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
					<td class="ui-state-default td-title">角色 ID</td>
					<td class="td-content"><input type="text" name="id" id="cond_id" maxlength="11" class="ui-widget-content"></td>
					<td class="ui-state-default td-title">角色名称</td>
					<td class="td-content"><input type="text" name="name" id="cond_name" maxlength="50" class="ui-widget-content"></td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">拥有权限</td>
					<td class="td-content"><input type="text" class="ui-widget-content" readonly><input type="hidden" name="privacy" id="cond_privacy"></td>
					<td class="ui-state-default td-title"></td>
					<td class="td-content"></td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">角色类别</td>
					<td class="td-content" colspan="3"><select name="rank_kind" id="cond_rank_kind" class="ui-widget-content">${rkOptions}</select></td>
				</tr>
			</table>
			<div style="height:44px">
				<input type="button" class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="resetbutton" value="清除" role="button" aria-disabled="false" style="float:right;right:2px">
				<input type="button" class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="searchbutton" value="检索" role="button" aria-disabled="false" style="float:right;right:2px">
				<input type="hidden" id="rkGo" value="${rkGo}"/>
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
					<td class="ui-state-default td-title">角色 ID</td>
					<td class="td-content"><label id="label_edit_id"> - </label></td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">角色类别</td>
					<td class="td-content"><select name="rank_kind" alt="角色类别" id="input_rank_kind" class="ui-widget-content">${rkOptions}</select></td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">角色名称</td>
					<td class="td-content">
						<input type="text" name="name" id="input_name" alt="角色名称" maxlength="20" class="ui-widget-content">
						<br>
						<label for="input_name" generated="false" class="errorarea-single" style="display:none">
					</label></td>
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
			<table class="subform" id="grid_edit_privacies" style="min-width:220px;">
				<tr>
					<th class="ui-state-default td-title" colspan="4" style="min-width:220px;">拥有权限</th>
				</tr>
				<tr>
					<th class="ui-state-default td-title">系统模块</th>
					<th class="ui-state-default td-title">查看</th>
					<th class="ui-state-default td-title">处理</th>
					<th class="ui-state-default td-title">管理</th>
				</tr>
				<%=request.getAttribute("pGroupChooser")%>
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
<div class="referchooser ui-widget-content" tabindex="-1">
	<table>
		<tr>
			<td width="50%">过滤字:<input type="text" tabindex="-1"/></td>	
			<td width="50%" align="right"><input type="button" class="ui-button" style="float:right;" value="清空"/></td>
		</tr>
	</table>
	
	<table class="subform"><%=request.getAttribute("pReferChooser")%></table>
<div>
</body>
</html>
