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
<script type="text/javascript" src="js/admin/light_fix.js"></script>
<title>中小修理标准编制检索</title>
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
					<td class="ui-state-default td-title">中小修理标准编制 ID</td>
					<td class="td-content"><input type="text" name="light_fix_id" id="cond_light_fix_id" maxlength="11" class="ui-widget-content"></td>
					<td class="ui-state-default td-title">名称</td>
					<td class="td-content"><input type="text" name="description" id="cond_description" maxlength="64" class="ui-widget-content"></td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">修理代码</td>
					<td class="td-content"><input type="text" name="activity_code" id="cond_activity_code" maxlength="4" class="ui-widget-content"></td>
					<td class="ui-state-default td-title">修理等级</td>
					<td class="td-content"><input type="text" name="rank" id="cond_rank" maxlength="10" class="ui-widget-content"></td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">机种分类</td>
					<td class="td-content"><select name="kind" id="cond_kind" class="ui-widget-content">${kOptions}</select></td>
					<td class="ui-state-default td-title">工位名称</td>
					<td class="td-content">
						<input type="text" name="position_name" id="cond_position_name" readonly="readonly" class="ui-widget-content">
						<input type="hidden" id="cond_position_id">
					</td>
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
					<td class="ui-state-default td-title">修理代码</td>
					<td class="td-content"><input type="text" id="add_activity_code" name="add_activity_code" alt="修理代码" maxlength="4" class="ui-widget-content"></td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">名称</td>
					<td class="td-content"><input type="text" id="add_description" name="add_description" alt="名称" maxlength="64" class="ui-widget-content"></td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">修理等级</td>
					<td class="td-content"><input type="text" id="add_rank" name="add_rank" alt="修理等级" maxlength="10" class="ui-widget-content"></td>
				</tr>
			</table>
			</div>
			<div style="float:left;max-width:620px;">
				<table class="subform" id="grid_add_kinds">
					<tr>
						<th class="ui-state-default td-title" colspan="3" style="min-width:220px;">机种</th>
					</tr>
					<%=request.getAttribute("kReferChooser")%>
				</table>
			</div>
			<div style="float:left;max-width:620px;">
				<table class="subform" id="grid_add_positions">
					<tr>
						<th class="ui-state-default td-title" colspan="3" style="min-width:220px;">工位</th>
					</tr>
					<%=request.getAttribute("pReferChooser")%>
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
			<input type="hidden" id="edit_light_fix_id" value=""/>
			<div style="float:left;">
			<table class="condform">
				<tr>
					<td class="ui-state-default td-title">修理代码</td>
					<td class="td-content"><input type="text" id="edit_activity_code" name="edit_activity_code" alt="修理代码" maxlength="4" class="ui-widget-content"></td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">名称</td>
					<td class="td-content"><input type="text" id="edit_description" name="edit_description" alt="名称" maxlength="64" class="ui-widget-content"></td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">修理等级</td>
					<td class="td-content"><input type="text" id="edit_rank" name="edit_rank" alt="修理等级" maxlength="10" class="ui-widget-content"></td>
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
				<table class="subform" id="grid_edit_kinds">
					<tr>
						<th class="ui-state-default td-title" colspan="3" style="min-width:220px;">机种</th>
					</tr>
					<%=request.getAttribute("kReferChooser")%>
				</table>
			</div>
			<div style="float:left;max-width:620px;">
				<table class="subform" id="grid_edit_positions">
					<tr>
						<th class="ui-state-default td-title" colspan="3" style="min-width:220px;">工位</th>
					</tr>
					<%=request.getAttribute("pReferChooser")%>
				</table>
			</div>
			<div class="clear" style="height:44px">
				<input type="button" class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="editbutton" value="修改" role="button" aria-disabled="false" style="float:left;left:4px;">
				<input type="button" class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="editcancelbutton" value="取消" role="button" aria-disabled="false" style="float:left;left:4px;">
			</div>
		</form>
	</div>
	<div class="ui-state-default ui-corner-bottom areaencloser dwidth-middleright"></div>
</div>

<div id="confirmmessage"></div>

<div class="clear areacloser"></div>
<div class="referchooser ui-widget-content" id="position_refer" tabindex="-1">
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
