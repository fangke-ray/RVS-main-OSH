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
<script type="text/javascript" src="js/jquery-1.8.2.min.js"></script>
<script type="text/javascript" src="js/jquery-ui-1.9.1.custom.min.js"></script>
<script type="text/javascript" src="js/jquery.validate.min.js"></script>
<script type="text/javascript" src="js/jquery.dialog.js"></script>
<script type="text/javascript" src="js/jquery.jqGrid.min.js"></script>
<script type="text/javascript" src="js/i18n/grid.locale-cn.js"></script>
<script type="text/javascript" src="js/ajaxfileupload.js"></script>
<script type="text/javascript" src="js/jquery.select2buttons.js"></script>
<script type="text/javascript" src="js/utils.js"></script>
<script type="text/javascript" src="js/jquery-plus.js"></script>
<script type="text/javascript" src="js/admin/pcs_request.js"></script>
<title>工程检查票模板变更改定履历</title>
</head>
<body class="outer">
<div class="width-full" style="align:center;margin:auto;margin-top:16px;">
	<div id="basearea" class="dwidth-full" style="margin: auto;">
			<jsp:include page="/header.do" flush="true">
				<jsp:param name="part" value="1"/>
			</jsp:include>
	</div>



		</div>

		<div class="ui-widget-panel ui-corner-all width-full" style="align: center; padding-top: 16px; padding-bottom: 16px;" id="body-1">
			<div id="searcharea" class="dwidth-full">
				<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser dwidth-full">
					<span class="areatitle">检索条件</span> <a role="link" href="javascript:void(0)" class="HeaderButton areacloser"> <span class="ui-icon ui-icon-circle-triangle-n"></span>
					</a>
				</div>
				
				<div class="ui-widget-content dwidth-full">
					<form id="searchform" method="POST">
						<table class="condform">
							<tbody>
								<tr>
									<td class="ui-state-default td-title">依赖发布日期</td>
									<td class="td-content">
										<input type="text" id="search_request_date_start" maxlength="15" class="ui-widget-content" readonly="readonly">起<br>
										<input type="text" id="search_request_date_end" maxlength="15" class="ui-widget-content" readonly="readonly">止
									</td>
									<td class="ui-state-default td-title">依赖DB号</td>
									<td class="td-content">
										<input type="text" id="search_request_db_no" maxlength="12" class="ui-widget-content">
									</td>
									<td class="ui-state-default td-title">描述</td>
									<td class="td-content"><input type="text" id="search_description" maxlength="120" class="ui-widget-content"></td>
								</tr>
								<tr>
									<td class="ui-state-default td-title" rowspan="2">工程检查票类型</td>
									<td class="td-content" rowspan="2" style="width: 300px;">
										<select name="section" id="search_line_type" class="ui-widget-content" style="display: none;">
							${lLineType}
										</select>
									</td>
									<td class="ui-state-default td-title">文件名称</td>
										<td class="td-content"><input type="text" id="search_file_name" maxlength="160" class="ui-widget-content"></td>
										<td class="ui-state-default td-title">针对型号</td>
									<td class="td-content">
										<input type="text" id="search_target_model" maxlength="100" class="ui-widget-content">
										<input type="hidden" id="search_target_model_id">
									</td>
								</tr>
								<tr>
									<td class="ui-state-default td-title">修改方式</td>
									<td class="td-content">
										<select name="change_means" id="search_change_means" class="ui-widget-content" style="display: none;">
									${lChangeMean}
										</select>
									</td>
									<td class="ui-state-default td-title">导入系统时间</td>
									<td class="td-content">
										<input type="text" id="search_import_time_start" maxlength="50" class="ui-widget-content" readonly="readonly">起<br>
										<input type="text" id="search_import_time_end" maxlength="50" class="ui-widget-content" readonly="readonly">止
									</td>
								</tr>
							</tbody>
						</table>
						<div style="height: 44px">
							<input type="button" class="ui-button" id="resetbutton" value="清除" role="button" style="float: right; right: 2px" aria-disabled="false">
							<input type="button" class="ui-button-primary ui-button" id="searchbutton" value="检索" role="button" style="float: right; right: 2px" aria-disabled="false">

							<input type="hidden" id="h_line_type_eo" value="${hLineTypeEo}">
							<input type="hidden" id="h_change_means_eo" value="${hChangeMeansEo}">
							<input type="hidden" id="h_m_level_eo" value="${hMLevelEo}">
							<section id="h_line_type_op" style="display:none;">${lLineType}</section>
							<section id="h_change_means_op" style="display:none;">${lChangeMean}</section>
						</div>
					</form>
				</div>
				<div class="clear areaencloser dwidth-full"></div>
			</div>

			<div id="listarea" class="dwidth-full">
				<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser dwidth-full">
					<span class="areatitle">修改记录一览</span>
				</div>
				<div class="ui-widget-content dwidth-full" style="padding:4px 0;">
						<input type="button" class="ui-button" id="load_button" value="新建模板文件" style="margin-left:6px;">
				</div>
	<table id="list"></table>
	<div id="listpager"></div>
				<div class="clear"></div>
			</div>

			<div id="functionarea" class="dwidth-full" style="margin: auto;">
				<div class="ui-widget-header ui-corner-all ui-helper-clearfix areabase">

					<div id="executes" style="margin-left: 4px; margin-top: 4px;">
						<input type="button" class="ui-button ui-button-primary" id="view_button" value="查看版本差别">
						<input type="button" class="ui-button" id="test_button" value="测试生成">
<% 
String isAdmin = (String) request.getAttribute("admin");
if ("true".equals(isAdmin)) { 
%>
						<input type="button" class="ui-button" id="confirm_button" value="导入到系统">
<% } %>
						<input type="button" class="ui-button" id="edit_button" value="编辑依赖" style="margin-left:8px;">
						<input type="button" class="ui-button" id="disband_button" value="取消依赖">
					</div>
					
					
				</div>
				<div class="clear"></div>
			</div>



		</div>

	</div>


		<div id="detail_dialog" tabindex="128"></div>
		<div id="confirm_message"></div>

	<div class="referchooser ui-widget-content" id="model_refer" tabindex="-1" style="z-index:1100;">
	<table>
		<tr>
			<td width="50%">过滤字:<input type="text"/></td>	
			<td width="50%" align="right"><input type="button" class="ui-button" style="float:right;" value="清空"/></td>
		</tr>
	</table>

	<table class="subform">${mReferChooser}</table>
	</div>
	<style>
	#process_file_dialog div {
		border : 1px solid rgb(147, 195, 205);
		cursor : pointer;
		transition-duration: 0.3s;
	}
	#process_file_dialog div:hover {
		background-color: #F9E29F;
	}
	</style>
</body></html>