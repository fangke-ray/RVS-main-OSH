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
<script type="text/javascript" src="js/admin/model.js"></script>
<title>型号检索</title>
</head>
<style>
.overdue_row{
	background:#ccc;
}
.overdue{
	background:red;
}

.button_add:hover {
	background: #FFFFFF;
	color: #F8BB14;
	cursor: pointer;
}

.button_add:active {
	overflow: visible;
	border: 1px solid #ff6b7f;
	background: #db4865 url(images/ui-bg_diagonals-small_40_db4865_40x40.png) 50% 50% repeat;
	font-weight: bold;
	color: #ffffff;
}
</style>
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
					<td class="ui-state-default td-title">维修对象机种</td>
					<td class="td-content" colspan="3"><select name="category_id" id="cond_category_id"><%=request.getAttribute("cOptions")%></select></td>
					<!-- td class="ui-state-default td-title">维修对象型号 ID</td>
					<td class="td-content"><input type="text" name="id" id="cond_id" maxlength="11" class="ui-widget-content"></td-->
				</tr>
				<tr>
					<td class="ui-state-default td-title">维修对象型号名称</td>
					<td class="td-content"><input type="text" name="name" id="cond_name" maxlength="50" class="ui-widget-content"></td>
					<td class="ui-state-default td-title">备注1</td>
					<td class="td-content"><input type="text" name="feature1" id="cond_feature1" maxlength="16" class="ui-widget-content"></td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">备注2</td>
					<td class="td-content"><input type="text" name="feature2" id="cond_feature2" maxlength="18" class="ui-widget-content"></td>
					<td class="ui-state-default td-title">系列</td>
					<td class="td-content"><input type="text" name="series" id="cond_series" maxlength="16" class="ui-widget-content"></td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">EL 座类别</td>
					<td class="td-content"><input type="text" name="el_base_type" id="cond_el_base_type" maxlength="16" class="ui-widget-content"></td>
					<td class="ui-state-default td-title">S 连接座类别</td>
					<td class="td-content"><input type="text" name="s_connector_base_type" id="cond_s_connector_base_type" maxlength="16" class="ui-widget-content"></td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">操作部类别</td>
					<td class="td-content"><input type="text" name="operate_part_type" id="cond_operate_part_type" maxlength="16" class="ui-widget-content"></td>
					<td class="ui-state-default td-title">接眼类别</td>
					<td class="td-content"><input type="text" name="ocular_type" id="cond_ocular_type" maxlength="16" class="ui-widget-content"></td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">不平衡机型</td>
					<td class="td-content" colspan="3">
						<select name="imbalance_line_id" id="cond_imbalance_line_id" class="ui-widget-content"><%=request.getAttribute("lOptions")%></select>
					</td>
				</tr>
			</table>
			<div style="height:44px">
				<input type="button" class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="resetbutton" value="清除" role="button" aria-disabled="false" style="float:right;right:2px">
				<input type="button" class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="searchbutton" value="检索" role="button" aria-disabled="false" style="float:right;right:2px">
				<input type="hidden" id="goMaterial_level"  value="${goMaterial_level}" />
				<input type="hidden" id="goEchelon_code"  value="${goEchelon_code }" />
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
		<span class="areatitle" id="title_header"></span>
		<a role="link" href="javascript:void(0)" class="HeaderButton areacloser">
			<span class="ui-icon ui-icon-circle-triangle-w"></span>
		</a>
	</div>
	<div class="ui-widget-content dwidth-middleright">
		<form id="editform" method="POST" onsubmit="return false;">
			<table class="condform">
				<tr>
					<td class="ui-state-default td-title">维修对象机种</td>
					<td class="td-content" colspan="3"><select alt="维修对象机种" name="category_id" id="input_category_id" class="ui-widget-content"><%=request.getAttribute("cOptions")%></select></td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">维修对象型号 ID</td>
					<td class="td-content" colspan="3"><label id="label_edit_id"></label></td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">维修对象型号名称</td>
					<td class="td-content">
						<input type="text" alt="维修对象型号名称" name="name" id="input_name" maxlength="50" class="ui-widget-content"/>
					</td>
					<td class="ui-state-default td-title">维修对象型号代码</td>
					<td class="td-content"><input type="text" alt="维修对象型号代码" name="item_code" id="input_item_code" maxlength="50" class="ui-widget-content"></td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">维修对象型号描述</td>
					<td class="td-content">
						<textarea id="input_description" style="resize:none" rows="4" cols="25" class="ui-widget-content"></textarea>
					</td>
					<td class="ui-state-default td-title">系列</td>
					<td class="td-content"><input type="text" alt="系列" name="series" id="input_series" maxlength="11" class="ui-widget-content"></td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">备注1</td>
					<td class="td-content"><input type="text" alt="备注1" name="feature1" id="input_feature1" maxlength="11" class="ui-widget-content"></td>
					<td class="ui-state-default td-title">备注2</td>
					<td class="td-content"><input type="text" alt="备注2" name="feature2" id="input_feature2" maxlength="11" class="ui-widget-content"></td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">EL 座类别</td>
					<td class="td-content"><input type="text" alt="EL 座类别" name="el_base_type" id="input_el_base_type" maxlength="16" class="ui-widget-content"></td>
					<td class="ui-state-default td-title">S 连接座类别</td>
					<td class="td-content"><input type="text" alt="S 连接座类别" name="s_connector_base_type" id="input_s_connector_base_type" maxlength="16" class="ui-widget-content"></td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">操作部类别</td>
					<td class="td-content"><input type="text" alt="操作部类别" name="operate_part_type" id="input_operate_part_type" maxlength="16" class="ui-widget-content"></td>
					<td class="ui-state-default td-title">接眼类别</td>
					<td class="td-content"><input type="text" alt="接眼类别" name="ocular_type" id="input_ocular_type" maxlength="16" class="ui-widget-content"></td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">是否选择性报价</td>
					<td class="td-content" id="selectable">
						<input type="radio" name="selectable" id="selectable_yes" class="ui-widget-content ui-helper-hidden-accessible" value="1"><label for="selectable_yes" aria-pressed="false">是</label>
						<input type="radio" name="selectable" id="selectable_no"  checked="checked" class="ui-widget-content ui-helper-hidden-accessible" value="2"><label for="selectable_no" aria-pressed="false">否</label>
					</td>
					<td class="ui-state-default td-title">不平衡机型</td>
					<td class="td-content">
						<select alt="不平衡机型" name="imbalance_line_id" id="input_imbalance_line_id" class="ui-widget-content" multiple="multiple"><%=request.getAttribute("lOptions")%></select>
						<span id="b_idx"><input type="radio" name="b_idx" id="b_idx_1" value="1"><label for="b_idx_1">B1 线</label><input type="radio" name="b_idx" id="b_idx_2" value="3"><label for="b_idx_2">B2 线</label></span>
					</td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">默认流程</td>
					<td class="td-content"  colspan="3"><select name="default_pat_id" alt="默认流程" id="input_default_pat_id" class="ui-widget-content">${patOptions}</select></td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">最后更新人</td>
					<td class="td-content"><label id="label_edit_updated_by"/></td>
					<td class="ui-state-default td-title">最后更新时间</td>
					<td class="td-content"><label id="label_edit_updated_time"/></td>
				</tr>
			</table>
			<div style="height:44px">
				<input type="button" class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="editbutton" value="新建" role="button" aria-disabled="false" style="float:left;left:4px;">
				<input type="button" class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="cancelbutton" value="取消" role="button" aria-disabled="false" style="float:left;left:4px;">
			</div>
		</form>
	</div>
	
	<div id="type" class="width-middleright">
		<table id="typelist"></table>
		<div id="typelist_pager"></div>
		<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser dwidth-middleright">
			<span class="areatitle" style="float:right;">※维修同意总数从本系统上线日(2013年4月1日)计算起</span>
		</div>
	</div>
	
	<div id="update_avaliable_end_date_dialog">
	</div>
</div>

<div id="confirmmessage"></div>
<div id="change_echelon_dialog" style="display: none">
	<form id="echelonForm" method="POST" onsubmit="return false;">
		<table class="condform">
			<tr>
				<td class="ui-state-default td-title">梯队</td>
				<td class="td-content" style="width: 316px;"><select alt="梯队" name="echelon" id="input_echelon" class="ui-widget-content">${sEchelon_code}</select></td>
			</tr>
		</table>
	</form>
</div>

</body>
</html>
