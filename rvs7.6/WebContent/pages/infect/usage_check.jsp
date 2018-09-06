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
	<script type="text/javascript" src="js/jquery-ui-1.9.1.custom.js"></script>
	<script type="text/javascript" src="js/jquery.dialog.js"></script>
	<script type="text/javascript" src="js/jquery.validate.js"></script>
	<script type="text/javascript" src="js/jquery.jqGrid.min.js"></script>
	<script type="text/javascript" src="js/i18n/grid.locale-cn.js"></script>
	<script type="text/javascript" src="js/jquery.select2buttons.js"></script>
	<script type="text/javascript" src="js/utils.js"></script>
	<script type="text/javascript" src="js/jquery-plus.js?v=1300"></script>
	<script type="text/javascript" src="js/infect/usage_check.js"></script>
	<script type="text/javascript" src="js/infect/infect_sheet.js"></script>
<title>点检画面</title>
</head>
<body class="outer" style="align: center;">
	<div class="width-full" style="align: center; margin: auto; margin-top: 16px;">
		<div id="basearea" class="dwidth-full" style="margin: auto;">
			<jsp:include page="/header.do" flush="true">
				<jsp:param name="part" value="2" />
			</jsp:include>
		</div>
		
		<div class="ui-widget-panel ui-corner-all width-full" style="align: center; padding-top: 16px; padding-bottom: 16px; width: 1266px;" id="body-2">
			<div id="body-lft" style="width: 256px; float: left;">
				<jsp:include page="/appmenu.do?method=tinit" flush="true">
					<jsp:param name="linkto" value="设备工具/治具点检" />
				</jsp:include>
			</div>


<!---------------------start----------------------------->
<!--点检画面开始-->
<div id="body-mdl">
<div id="usage_check_search" style="width: 994px; float: left;">
	<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser">
		<span class="areatitle">点检画面</span>
		<a target="_parent" role="link" href="javascript:void(0)" class="HeaderButton areacloser">
			<span class="ui-icon ui-icon-circle-triangle-n"></span>
		</a>
	</div>
	<div class="ui-widget-content">
		<form id="searchform" method="POST">
			<table class="condform">
				<tbody>
					<tr>
						<td class="ui-state-default td-title" style="width: 111px;">点检对象</td>
						<td class="td-content">
								<div id="search_object_type" class="ui-buttonset">
									<input type="radio" name="object_type" id="object_type_a" class="ui-widget-content ui-helper-hidden-accessible" value="" checked="checked"><label for="object_type_a" aria-pressed="false">(全)</label>
									<input type="radio" name="object_type" id="object_type_d" class="ui-widget-content ui-helper-hidden-accessible" value="1"><label for="object_type_d" aria-pressed="false">设备</label>
									<input type="radio" name="object_type" id="object_type_t" class="ui-widget-content ui-helper-hidden-accessible" value="2"><label for="object_type_t" aria-pressed="false">治具</label>
								</div>
						</td>
						<td class="ui-state-default td-title">点检状态</td>
						<td class="td-content">
								<div id="search_check_proceed" class="ui-buttonset">
									<input type="radio" name="check_proceed" id="check_proceed_a" class="ui-widget-content ui-helper-hidden-accessible" value="" checked="checked"><label for="check_proceed_a" aria-pressed="false">(全)</label>
									<input type="radio" name="check_proceed" id="check_proceed_y" class="ui-widget-content ui-helper-hidden-accessible" value="1"><label for="check_proceed_y" aria-pressed="false">已点检</label>
									<input type="radio" name="check_proceed" id="check_proceed_n" class="ui-widget-content ui-helper-hidden-accessible" value="2"><label for="check_proceed_n" aria-pressed="false">未点检</label>
								</div>
						</td>
						<td class="ui-state-default td-title"  style="width: 111px;">点检表管理号</td>
						<td class="td-content">
							<input type="text" id="search_sheet_manage_no" maxlength="120" class="ui-widget-content">
						</td>
					</tr>
					<tr>
						<td class="ui-state-default td-title" style="width: 130px;">设备/治具管理编号</td>
						<td class="td-content">
							<input type="text" id="search_manage_no" maxlength="120" class="ui-widget-content">
						</td>
						<td class="ui-state-default td-title">品名</td>
						<td class="td-content">
							<input type="text" id="search_name" class="ui-widget-content"/>
							<input type="hidden" id="search_type_id"/>
						</td>
						<td class="ui-state-default td-title">型号</td>
						<td class="td-content">
							<input type="text" id="search_model_name" class="ui-widget-content"/>
						</td>
					</tr>
					<tr>
						<td class="ui-state-default td-title" style="width: 111px;">工位</td>
						<td class="td-content">
							<input type="text" id="search_position_name" maxlength="120" class="ui-widget-content">
							<input type="hidden" id="search_position_id" value="${selectedPosition}"/>
						</td>
						<td class="ui-state-default td-title">点检结果</td>
						<td class="td-content">
								<div id="search_check_result" class="ui-buttonset">
									<input type="radio" name="check_result" id="check_result_a" class="ui-widget-content ui-helper-hidden-accessible" value="" checked="checked"><label for="check_result_a" aria-pressed="false">(全)</label>
									<input type="radio" name="check_result" id="check_result_y" class="ui-widget-content ui-helper-hidden-accessible" value="1"><label for="check_result_y" aria-pressed="false">通过</label>
									<input type="radio" name="check_result" id="check_result_n" class="ui-widget-content ui-helper-hidden-accessible" value="2"><label for="check_result_n" aria-pressed="false">不通过</label>
								</div>
						</td>
						<td class="ui-state-default td-title"></td>
						<td class="td-content">
							
						</td>
					</tr>
				</tbody>
			</table>
			<div style="height:44px">
				<input class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="resetbutton" value="清除" role="button" aria-disabled="false" style="float:right;right:2px" type="button">
				<input class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all ui-state-focus" id="searchbutton" value="检索" role="button" aria-disabled="false" style="float:right;right:2px" type="button">
				<input id="isLeader" type="hidden" value="${isLeader}"/>
			</div>
		</form>
	</div>

	<div class="clear areaencloser"></div>

	<div id="listarea" class="">
		<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser">
			<span class="areatitle">点检对象一览</span>
			<a target="_parent" role="link" href="javascript:void(0)" class="HeaderButton areacloser">
				<span class="ui-icon ui-icon-circle-triangle-n"></span>
			</a>
		</div>
		<table id="usage_check_list"></table>
		<div id="usage_check_listpager"></div>
	</div>
	<div class="clear"></div>
</div>
<!--设备检点种类一览开始-->

	<div class="referchooser ui-widget-content" id="pReferChooser" tabindex="-1">
		<table>
			<tr>
				<td width="50%">过滤字:<input type="text"/></td>	
				<td width="50%" align="right"><input type="button" class="ui-button" style="float:right;" value="清空"/></td>
			</tr>
		</table>
		
		<table class="subform">${pReferChooser}</table>
	</div>
	<div class="referchooser ui-widget-content" id="nReferChooser" tabindex="-1">
		<table>
			<tr>
				<td width="50%">过滤字:<input type="text"/></td>	
				<td width="50%" align="right"><input type="button" class="ui-button" style="float:right;" value="清空"/></td>
			</tr>
		</table>
		
		<table class="subform">${nReferChooser}</table>
	</div>
<div class="clear"></div>
</div>
<!--双击一栏画面结束-->

<!----------------------end----------------------------->
</div>
</div>