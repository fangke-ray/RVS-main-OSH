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
<script type="text/javascript" src="js/jquery.jqGrid.min.js"></script>
<script type="text/javascript" src="js/i18n/grid.locale-cn.js"></script>
<script type="text/javascript" src="js/jquery.select2buttons.js"></script>
<script type="text/javascript" src="js/utils.js"></script>
<script type="text/javascript" src="js/jquery-plus.js"></script>
<script type="text/javascript" src="js/ajaxfileupload.js"></script>
<script type="text/javascript" src="js/partial/partial_bom.js"></script>

<title>零件BOM信息管理</title>
</head>
<% 
	String role = (String) request.getAttribute("role");
	boolean isOperator = ("operator").equals(role);
%>
<body class="outer" style="align: center;">
	<div class="width-full" style="align: center; margin: auto; margin-top: 16px;">
		<div id="basearea" class="dwidth-full" style="margin: auto;">
			<jsp:include page="/header.do" flush="true">
				<jsp:param name="part" value="2"/>
			</jsp:include>
		</div>
		
		<div id="body-2" class="ui-widget-panel ui-corner-all" style="align: center; padding-top: 16px; padding-bottom: 16px;">
			<div id="body-lft" style="width: 256px; float: left;">
				<jsp:include page="/appmenu.do?method=pinit" flush="true">
					<jsp:param name="linkto" value="零件基础数据管理"/>
				</jsp:include>
			</div>
			
			<div id="body-mdl" style="width: 994px; float: left;">
				<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser">
				   <span class="areatitle">检索条件</span>
				    <a role="link" href="javascript:void(0)" class="HeaderButton areacloser">
						<span class="ui-icon ui-icon-circle-triangle-n"></span>
					</a>
				</div>
				
				<div class="ui-widget-content">
					<form id="searchform" method="POST">
						<table class="condform">
							<tbody>
								<tr>
									<td class="ui-state-default td-title">型号</td>
									<td class="td-content">
										<input type="text" id="view_mode_name" name="view_mode_name" alt="型号" class="ui-widget-content" readonly="readonly">
										<input type="hidden" name="mode_id" id="search_model_id">
									</td>
									<td class="ui-state-default td-title">等级</td>
									<td class="td-content">
										<select id="search_level_id" class="ui-widget-content" alt="等级" name="level_name">${smMaterialLevelInline}</select>
									</td>
									<td class="ui-state-default td-title">零件编码</td>
									<td class="td-content">
										<input type="text" id="code_name" alt="零件编码" class="ui-widget-content">
									</td>
								</tr>
							</tbody>
						</table>
						<div style="height:44px">
							<input class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="resetbutton" value="清除" role="button" style="float:right;right:2px" type="button">
							<input class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all ui-state-focus" id="searchbutton" value="参照" role="button"  style="float:right;right:2px" type="button">
							<input type="hidden" id="goEechelon_code" value="${goEechelon_code}"/>
							<input type="hidden" id="goMaterial_level_inline" value="${goMaterial_level_inline}"/>	
						</div>
					</form>
				</div>
				<div class="clear areaencloser"></div>
				<div id="listarea" class="">
					<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser">
						<span class="areatitle">零件BOM信息管理</span>
					</div>
					<div class="ui-widget-content" id="search_hidden">
						<table class="condform">
							<tbody>
								<tr>
									<td class="ui-state-default td-title" rowspan="3">型号</td>
									<td class="td-content">
										<label id="label_model_id"></label>
									</td>
									<td class="ui-state-default td-title">等级</td>
									<td class="td-content">
										<label id="label_level_id"></label>
									</td>
								</tr>
							</tbody>
						</table>
					</div>
					<table id="partial_bom_list"></table>
					<div id="partial_bom_listpager"></div>
				</div>
				<div id="functionarea" style="margin:auto;height:44px;margin-top:16px;">
<% if (isOperator) { %>
					<div class="ui-widget-header ui-corner-all ui-helper-clearfix areabase bar_fixed">
						<div id="executes" style="margin-left:4px;margin-top:4px;">
							<input type="button" id="import_bom" class="ui-button ui-widget ui-state-default ui-corner-all" value="导入BOM表"/>
							<input type="button" id="dowload_bom" style="right: 4px; float: right;" class="ui-button ui-widget ui-state-default ui-corner-all" value="导出BOM表"/>
						</div>
					</div>
<% } %>
				</div>
			<div class="clear"></div>
			</div>
			<div class="clear"></div>

			<div class="referchooser ui-widget-content" id="model_refer" tabindex="-1">
				<!-- 下拉选择内容 -->
				<table>
					<tr>
						<td width="50%">过滤字:<input type="text"/></td>	
						<td width="50%" align="right"><input type="button" class="ui-button" style="float:right;" value="清空"/></td>
					</tr>
				</table>
				<table class="subform">${mReferChooser}</table>
			</div>
		</div>
	</div>
	<div class="clear"></div>
</body>
</html>