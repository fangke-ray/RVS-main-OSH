<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page language="java" pageEncoding="UTF-8"
	contentType="text/html;charset=UTF-8" isELIgnored="false"%>
<html>
<head>
<%
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath()+"/";
%>
<base href="<%=basePath%>">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

<link rel="stylesheet" type="text/css" href="css/custom.css">
<link rel="stylesheet" type="text/css" href="css/ui.jqgrid.css">
<link rel="stylesheet" type="text/css"
	href="css/olympus/select2Buttons.css">
<link rel="stylesheet" type="text/css"
	href="css/olympus/jquery-ui-1.9.1.custom.css">

<script type="text/javascript" src="js/jquery-1.8.2.min.js"></script>
<script type="text/javascript" src="js/jquery-ui-1.9.1.custom.min.js"></script>
<script type="text/javascript" src="js/jquery.validate.min.js"></script>
<script type="text/javascript" src="js/jquery.dialog.js"></script>
<script type="text/javascript" src="js/highcharts.js"></script>
<script type="text/javascript" src="js/ajaxfileupload.js"></script>
<script type="text/javascript" src="js/jquery.jqGrid.min.js"></script>
<script type="text/javascript" src="js/i18n/grid.locale-cn.js"></script>
<script type="text/javascript" src="js/jquery.select2buttons.js"></script>
<script type="text/javascript" src="js/utils.js"></script>
<script type="text/javascript" src="js/jquery-plus.js"></script>
<script type="text/javascript" src="js/partial/partial_base_line_value.js"></script>
<script type="text/javascript" src="js/partial/common/change_base_value.js"></script>
<title>零件基准值设定</title>
</head>
<body class="outer" style="align: center;" id="searcharea">
	<div class="width-full"
		style="align: center; margin: auto; margin-top: 16px;">
		<div id="basearea" class="dwidth-full" style="margin: auto;">
			<jsp:include page="/header.do" flush="true">
				<jsp:param name="part" value="2" />
			</jsp:include>
		</div>

		<div id="body-2" class="ui-widget-panel ui-corner-all"
			style="align: center; padding-top: 16px; padding-bottom: 16px;">
			<div id="body-lft" style="width: 256px; float: left;">
				<jsp:include page="/appmenu.do?method=pinit" flush="true">
					<jsp:param name="linkto" value="零件辅助功能" />
				</jsp:include>
			</div>

			<div id="body-mdl" style="width: 994px; float: left;">
				<div
					class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser">
					<span class="areatitle">检索条件</span> <a role="link"
						href="javascript:void(0)" class="HeaderButton areacloser"> <span
						class="ui-icon ui-icon-circle-triangle-n"></span>
					</a>
				</div>

				<div class="ui-widget-content dwidth-middleright">
					<form id="searchform" method="POST">
						<table class="condform">
							<tbody>
								<tr>
									<td class="ui-state-default td-title">零件编号</td>
									<td class="td-content"><input type="text" name="partial_code" id="partial_code" alt="零件编号" class="ui-widget-content" value="${edit_partial_code}"/></td>
									<td class="ui-state-default td-title">零件名称</td>
									<td class="td-content"><input type="text" name="partial_name" id="partial_name" alt="零件名称" class="ui-widget-content"/></td>
								</tr>
							</tbody>
						</table>
						<div style="height: 44px">
		<% 
			String edit_partial_code = (String) request.getAttribute("edit_partial_code"); 
			if (edit_partial_code != null) {
		%>
							<input id="backbutton" class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" type="button" style="float: right; right: 2px" aria-disabled="false" role="button" value="返回">
		<% 
			}
		%>
							<input id="resetbutton"
								class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all"
								type="button" style="float: right; right: 2px"
								aria-disabled="false" role="button" value="清除"> <input
								id="searchbutton"
								class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all"
								type="button" style="float: right; right: 2px"
								aria-disabled="false" role="button" value="检索">
								<input type="hidden" id="simpleDateStart" value="${simpleDateStart }">
								<input type="hidden" id="simpleDateEnd" value="${simpleDateEnd }">
						</div>
					</form>
				</div>

				<div class="clear areaencloser"></div>

				<div id="listarea">
					<div
						class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser">
						<span class="areatitle">零件基准值一览</span> <a target="_parent"
							role="link" href="javascript:void(0)"
							class="HeaderButton areacloser"> <span
							class="ui-icon ui-icon-circle-triangle-n"></span>
						</a>
					</div>
					<table id="partial_base_line_value_list"></table>
					<div id="partial_base_line_value_listpager"></div>
					
				</div>

				<div class="ui-widget-header areabase" style="padding-top: 4px; margin-top: 0px;">
					<div id="executes" style="margin-left: 4px; margin-top: 3px;">
						<input type="button" id="download_button" class="ui-button ui-widget ui-state-default ui-corner-all" value="下载编辑基准值" role="button" style="right: 4px; float: right;">
						<input type="button" id="upload_button" class="ui-button ui-widget ui-state-default ui-corner-all" value="上传基准值设定" role="button" style="left: 4px;"> 
						<input type="hidden" id="goMaterial_level" value="${goMaterial_level}" />
					</div>
				</div>
			</div>
			
			<div class="clear"></div>
		</div>
	</div>
</body>
</html>