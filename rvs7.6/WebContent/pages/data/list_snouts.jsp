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
<script type="text/javascript" src="js/jquery.dialog.js"></script>
<script type="text/javascript" src="js/jquery.jqGrid.min.js"></script>
<script type="text/javascript" src="js/i18n/grid.locale-cn.js"></script>
<script type="text/javascript" src="js/utils.js"></script>
<script type="text/javascript" src="js/jquery-plus.js"></script>
<script type="text/javascript" src="js/jquery.select2buttons.js"></script>
<script type="text/javascript" src="js/data/list_snouts.js"></script>
<title>先端组件信息查询</title>
</head>
<body class="outer" style="align: center;">


	<div class="width-full" style="align: center; margin: auto; margin-top: 16px;">
<div id="basearea" class="dwidth-full" style="margin: auto;">
	<jsp:include page="/header.do" flush="true">
		<jsp:param name="part" value="2"/>
	</jsp:include>
</div>
		<div class="ui-widget-panel ui-corner-all width-full" style="align: center; padding-top: 16px;" id="body-3">
	<div id="body-lft" style="width:256px;float:left;">
		<jsp:include page="/appmenu.do" flush="true">
			<jsp:param name="linkto" value="进度查询"/>
		</jsp:include>
	</div>
			<div style="width: 1012px; float: left;">
				<div id="body-mdl" class="dwidth-middleright" style="margin: auto;">
	<div style="margin:auto;">
	
<div id="searcharea" class="dwidth-middleright">
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
					<td class="ui-state-default td-title">先端组件型号</td>
					<td class="td-content" colspan="3"><select name="model_id" id="cond_model_id" class="ui-widget-content">${mOptions}</select></td>
					<td class="ui-state-default td-title">先端组件序列号</td>
					<td class="td-content"><input type="text" name="serial_no" id="cond_serial_no" maxlength="12" class="ui-widget-content"></td>
				</tr>
				<tr>
					<td class="ui-state-default td-title">状态</td>
					<td class="td-content"><select name="status" id="cond_used" class="ui-widget-content">${rOptions}</select></td>
					<td class="ui-state-default td-title">完成时间</td>
					<td class="td-content">
						<input type="text" name="finish_time_from" id="cond_finish_time_from" class="ui-widget-content">起<br/>
						<input type="text" name="finish_time_to" id="cond_finish_time_to" class="ui-widget-content">止
					</td>
					<td class="ui-state-default td-title">作业者</td>
					<td class="td-content"><input type="text" readonly class="ui-widget-content"><input type="hidden" name="operator_id" id="cond_operator_id" maxlength="14" class="ui-widget-content"></td>
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
	<div class="clear areaencloser"></div>
</div>


<div id="listarea" class="dwidth-middleright">
	<table id="list"></table>
	<div id="listpager"></div>
</div>

<div id="detail_dialog">
</div>

<div class="clear areacloser"></div>
</div>
				</div>
			</div>

			<div class="clear areaencloser dwidth-middleright"></div>
		</div>
	</div>
<div class="referchooser ui-widget-content" tabindex="-1" id="referchooser_operator">
	<table>
		<tr>
			<td width="50%">过滤字:<input type="text" tabindex="-1"/></td>	
			<td width="50%" align="right"><input type="button" class="ui-button" style="float:right;" value="清空"/></td>
		</tr>
	</table>
	
	<table class="subform"><%=request.getAttribute("oReferChooser")%></table>
</div>
</body>
</html>