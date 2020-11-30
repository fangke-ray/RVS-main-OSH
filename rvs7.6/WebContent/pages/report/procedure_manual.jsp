<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page language="java" pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" isELIgnored="false"%>
<html>
<head>
<%
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath()+"/";
%>
<base href="<%=basePath%>">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

<link rel="stylesheet" type="text/css" href="css/custom.css">
<link rel="stylesheet" type="text/css" href="css/ui.jqgrid.css">
<link rel="stylesheet" type="text/css" href="css/olympus/select2Buttons.css">
<link rel="stylesheet" type="text/css" href="css/olympus/jquery-ui-1.9.1.custom.css">

<style>
#man_list a {
	font-size: 18px;
}
#man_list a.ui-icon {
	float: left;
	margin-right:.5em;
	margin-top:.2em;
}
</style>

<script type="text/javascript" src="js/jquery-1.8.2.min.js"></script>
<script type="text/javascript" src="js/jquery-ui-1.9.1.custom.min.js"></script>
<script type="text/javascript" src="js/jquery.dialog.js"></script>
<script type="text/javascript" src="js/jquery.jqGrid.min.js"></script>
<script type="text/javascript" src="js/i18n/grid.locale-cn.js"></script>
<script type="text/javascript" src="js/ajaxfileupload.js"></script>
<script type="text/javascript" src="js/utils.js"></script>
<script type="text/javascript" src="js/jquery-plus.js"></script>
<script type="text/javascript" src="js/report/procedure_manual.js"></script>

<title>作业手顺书</title>
</head>
<body class="outer" style="align: center;">

	<div class="width-full" style="align: center; margin: auto; margin-top: 16px;">
<%
boolean isTech = (request.getAttribute("tech") != null);
%>
		<div id="basearea" class="dwidth-full" style="margin: auto;">
			<jsp:include page="/header.do" flush="true">
				<jsp:param name="part" value="2"/>
			</jsp:include>
		</div>

		<div class="ui-widget-panel ui-corner-all width-full" style="align: center; padding-top: 16px; padding-bottom: 16px;" id="body-2">
			<div id="body-lft" style="width: 256px; float: left;">
				<jsp:include page="/appmenu.do" flush="true">
					<jsp:param name="linkto" value="文档管理"/>
				</jsp:include>
			</div>
			<div id="body-mdl" style="width: 994px; float: left;">
				<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser" id="searcharea">
					<span class="areatitle">检索条件</span>
					<a role="link" href="javascript:void(0)" class="HeaderButton areacloser">
						<span class="ui-icon ui-icon-circle-triangle-n"></span>
					</a>
				</div>
				<div class="ui-widget-content dwidth-middleright">
					<form id="searchform" method="POST" onsubmit="return false;">
						<table class="condform">
							<tr>
								<td class="ui-state-default td-title">文档名称</td>
								<td class="td-content"><input type="text" name="file_name" id="cond_name" maxlength="75" class="ui-widget-content"></td>
								<td class="ui-state-default td-title"></td>
								<td class="td-content"></td>
							</tr>
						</table>
					</form>
					<div style="height:44px">
						<input type="button" class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="resetbutton" value="清除" role="button" aria-disabled="false" style="float:right;right:2px">
						<input type="button" class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="searchbutton" value="检索" role="button" aria-disabled="false" style="float:right;right:2px">
					</div>
				</div>
				<div class="clear areaencloser"></div>

				<div id="man_listarea" class="width-middleright">
					<table id="man_list" class="ui-jqgrid-btable" style="width: 100%;">
					</table>
					<div id="man_list_pager"></div>
				</div>
<%
	if (isTech) {
%>
				<div id="functionarea" class="dwidth-full" style="margin: auto;">
					<div class="ui-widget-header ui-corner-all ui-helper-clearfix areabase">
						<div id="executes" style="margin-left: 4px; margin-top: 4px;">
							<input type="button" class="ui-button" id="newbutton" value="新建上传要领书" />
							<input type="button" class="ui-button" id="removebutton" value="移除要领书" />
						</div>
					</div>
					<div class="clear areaencloser"></div>
				</div>
<%
	}
%>
			</div>
			<div class="clear dwidth-middle"></div>
			</div>
		</div>
		<div class="clear"></div>
	</div>

<%
	if (isTech) {
%>
<div id="detail_dialog" style="display:none;">
	<form id="editform" method="POST" onsubmit="return false;">
		<table class="condform">
			<tr>
				<td class="ui-state-default td-title">文档上传</td>
				<td class="td-content"><input type="file" name="file" id="edit_file" class="ui-widget-content"></td>
			</tr>
			<tr>
				<td class="ui-state-default td-title">文档名称</td>
				<td class="td-content"><input type="text" name="file_name" id="edit_file_name" maxlength="75" size="75" class="ui-widget-content"></td>
			</tr>
			<tr>
				<td class="ui-state-default td-title">上传时间</td>
				<td class="td-content"><label id="edit_update_time"></td>
			</tr>
		</table>
	</form>
</div>
<%
	}
%>
</body>
</html>