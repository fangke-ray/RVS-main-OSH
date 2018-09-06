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

<script type="text/javascript" src="js/jquery-1.8.2.min.js"></script>
<script type="text/javascript" src="js/jquery-ui-1.9.1.custom.min.js"></script>
<script type="text/javascript" src="js/jquery.validate.min.js"></script>
<script type="text/javascript" src="js/jquery.dialog.js"></script>
<script type="text/javascript" src="js/jquery.select2buttons.js"></script>
<script type="text/javascript" src="js/utils.js"></script>
<script type="text/javascript" src="js/jquery-plus.js"></script>
<script type="text/javascript" src="js/qa/qa_statictis.js"></script>
<style>
#edit_value input{
	width:40px;
	text-align: right;
}
</style>

<title>品保周数据编辑</title>
</head>
<body class="outer" style="align: center;">


	<div class="width-full" style="align: center; margin: auto; margin-top: 16px;">
		<div id="basearea" class="dwidth-full" style="margin: auto;">
			<jsp:include page="/header.do" flush="true">
				<jsp:param name="part" value="2"/>
			</jsp:include>
		</div>

		<div class="ui-widget-panel ui-corner-all width-full" style="align: center; padding-top: 16px; padding-bottom: 16px;" id="body-2">
			<div id="body-lft" style="width: 256px; float: left;">
				<jsp:include page="/appmenu.do" flush="true">
					<jsp:param name="linkto" value="品保作业"/>
				</jsp:include>
			</div>
			<div id="body-mdl" style="width: 994px; float: left;">
			<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser">
				<span class="areatitle">选择月份</span>
				<a role="link" href="javascript:void(0)" class="HeaderButton areacloser">
					<span class="ui-icon ui-icon-circle-triangle-n"></span>
				</a>
			</div>
			<div class="ui-widget-content">
				<form id="selectform" method="POST">
					<table class="condform">
					<tbody>
						<tr>
							<td class="ui-state-default td-title">当前选择月份<input type="hidden" id="year_month" value="${yearMonthValue}"></td>
							<td class="td-content" id="sMonth">${sMonth}</td>
							<td class="ui-state-default td-title">选择年份</td>
							<td class="td-content"><select alt="年份" name="year" id="select_year">${yOptions}</select></td>
							<td class="ui-state-default td-title">选择月份</td>
							<td class="td-content"><select alt="月份" name="month" id="select_month">${mOptions}</select></td>
						</tr>
					</tbody>
					</table>
					<div style="height:44px">
						<input type="button" class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="selectbutton" value="改变月份" role="button" aria-disabled="false" style="float:right;right:2px">
					</div>
				</form>
			</div>

			<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser" style="margin-top:36px;">
				<span class="areatitle">当前展示数据一览</span>
			</div>
			<div id="edit_value" class="width-middleright ui-jqgrid ui-widget-content">
				<table class="" style="width:988px" role="grid" aria-labelledby="gbox_exd_list" cellspacing="0" cellpadding="0" border="0">
					<thead>
						<tr class="ui-jqgrid-labels" role="rowheader">
							<th role="columnheader" class="ui-state-default ui-th-column ui-th-ltr" style="width: 76px;">
								<div class="ui-jqgrid-sortable">时段
								</div>
							</th>
							<th role="columnheader" class="ui-state-default ui-th-column ui-th-ltr" style="width: 76px;">
								<div class="ui-jqgrid-sortable">检查总数</div>
							</th>
							<th role="columnheader" class="ui-state-default ui-th-column ui-th-ltr" style="width: 76px;">
								<div class="ui-jqgrid-sortable">不合格数</div>
							</th>
							<th role="columnheader" class="ui-state-default ui-th-column ui-th-ltr" style="width: 76px;">
								<div class="ui-jqgrid-sortable">检查合格率</div>
							</th>
							<th role="columnheader" class="ui-state-default ui-th-column ui-th-ltr" style="width: 76px;">
								<div class="ui-jqgrid-sortable">当期目标</div>
							</th>
						</tr>
					</thead>
					<tbody>
					</tbody>
				</table>
			</div>
			<div class="ui-widget-header ui-corner-all ui-helper-clearfix areabase" style="padding-top:4px;margin-top:24px;">
					<input type="button" id="saveshowbutton" class="ui-button ui-widget ui-state-default ui-corner-all" value="保存并查看效果" role="button" aria-disabled="true" disabled="">
					<input type="button" id="savebutton" class="ui-button ui-widget ui-state-default ui-corner-all" value="保存" role="button" aria-disabled="false">
			</div>
			<div class="clear dwidth-middle"></div>
		</div>

		<div class="clear"></div>
	</div>
	<div id="complete_dialog"></div>
</body>
</html>