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

<script type="text/javascript" src="js/jquery.imagePreview.1.0.js"></script>

<script type="text/javascript" src="js/jquery.validate.min.js"></script>
<script type="text/javascript" src="js/jquery.dialog.js"></script>
<script type="text/javascript" src="js/jquery.jqGrid.min.js"></script>
<script type="text/javascript" src="js/i18n/grid.locale-cn.js"></script>
<script type="text/javascript" src="js/jquery.select2buttons.js"></script>
<script type="text/javascript" src="js/utils.js"></script>
<script type="text/javascript" src="js/jquery-plus.js"></script>
<script type="text/javascript" src="js/ajaxfileupload.js"></script>

<script type="text/javascript" src="js/admin/system_image_manage.js"></script>
<title>系统图片管理</title>
</head>
<body>

<div id="searcharea">
	<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser dwidth-middleright">
		<span class="areatitle">检索条件</span>
		<a role="link" href="javascript:void(0)" class="HeaderButton areacloser">
			<span class="ui-icon ui-icon-circle-triangle-n"></span>
		</a>
	</div>
	
	<div style="width: 991px; height: 44px; border-bottom: 0;"
			id="infoes" class="dwidth-full ui-widget-content">
			<div>
				<input type="radio" name="infoes" class="ui-button ui-corner-up" id="pcs_button" value="pcs" role="button" checked="checked">
				<label for="pcs_button">工程检查票内图</label>
				<input type="radio" name="infoes" class="ui-button ui-corner-up" id="sign_button" value="sign" role="button">
				<label for="sign_button">员工姓名章</label>
				<input type="radio" name="infoes" class="ui-button ui-corner-up" id="tcs_button" value="tcs" role="button">
				<label for="tcs_button">点检单内图</label>
			</div>
			
			<input type="hidden" id="image_classify" value="${gClassify}"/>
		</div>

	<div class="ui-widget-content dwidth-middleright">
		<form id="searchform" method="POST" onsubmit="return false;">
			<table class="condform">
				<tr>
					<td class="ui-state-default td-title">文件名</td>
					<td class="td-content"><input type="text" name="file_name" id="search_file_name" class="ui-widget-content"></td>
					<td class="ui-state-default td-title">说明</td>
					<td class="td-content"><input type="text" name="description" id="search_description" class="ui-widget-content"></td>
				</tr>
			</table>
			<div style="height:44px">
				<input type="button" class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="resetbutton" value="清除" role="button" aria-disabled="false" style="float:right;right:2px">
				<input type="button" class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="searchbutton" value="检索" role="button" aria-disabled="false" style="float:right;right:2px">
			</div>
		</form>
	</div>
	<div class="clear areaencloser"></div>
</div>

<div id="listarea" class="width-middleright">
    <input type="hidden" id="hidden_choose_classify"/>
    
	<table id="list"></table>
	<div id="listpager"></div>
</div>

<div id="import_pic" style="display:none;">
	<div class="ui-widget-content dwidth-middleright" style="width:360px;">
		<form id="import_form" method="POST" onsubmit="return false;">
			<table class="condform">
				<tr>
					<td class="ui-state-default td-title">文件类型</td>
					<td class="td-content"><select name="classify" id="file_classify" alt="文件类型">${sClassify}</select></td>
				</tr>
				
				<tr>
					<td class="ui-state-default td-title">文件名</td>
					<td class="td-content"><input type="text" name="file_name" id="import_fileName" alt="文件名" class="ui-widget-content"></td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">上传文件</td>
					<td class="td-content"><input type="file" name="file" id="import_file" alt="上传文件" class="ui-widget-content"></td>
				</tr>
			</table>
		</form>
	</div>
</div>

<div id="edit_pic" style="display:none;">
	<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser dwidth-middleright">
		<span class="areatitle">修改系统图片说明</span>
		<a class="HeaderButton areacloser" href="javascript:void(0)" role="link">
			<span class="ui-icon ui-icon-circle-triangle-w"></span>
		</a>
	</div>
	<div class="ui-widget-content dwidth-middleright">
		<form id="edit_form" method="POST" onsubmit="return false;">
			<table class="condform">
				<tr>
					<td class="ui-state-default td-title">文件名</td>
					<td class="td-content"><label id="label_file_name"></label></td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">文件分类</td>
					<td class="td-content">
					     <label id="label_classify"></label>
					     <input type="hidden" id="hidden_classify"/>
					</td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">图片显示</td>
					<td class="td-content">
					    <img id="edit_image"/>
					</td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">说明</td>
					<td class="td-content"><textarea id="edit_description" class="ui-widget-content" style="height:240px;width:300px;resize: none;"></textarea></td>
				</tr>
			</table>
			<div style="height:60px">
			    <input type="button" class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="confirmbutton" value="确认" role="button" aria-disabled="false" style="left:4px;top:10px;">
				<input type="button" class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="backbutton" value="返回" role="button" aria-disabled="false" style="left:4px;top:10px;">
			</div>
		</form>
	</div>
	<div class="clear dwidth-middleright"></div>
</div>


<div id="confirmmessage"></div>
</body>
</html>
