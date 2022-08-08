<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath()+"/";
%>
<base href="<%=basePath%>">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="Pragma" content="no-cache"> <meta http-equiv="Cache-Control" content="no-cache">

<link rel="stylesheet" type="text/css" href="css/custom.css">
<link rel="stylesheet" type="text/css" href="css/olympus/jquery-ui-1.9.1.custom.css">
<link rel="stylesheet" type="text/css" href="css/ui.jqgrid.css">

<script type="text/javascript" src="js/jquery-1.8.2.min.js"></script>
<script type="text/javascript" src="js/jquery-ui-1.9.1.custom.min.js"></script>
<script type="text/javascript" src="js/jquery.dialog.js"></script>
<script type="text/javascript" src="js/highcharts.js"></script>
<script type="text/javascript" src="js/jquery.jqGrid.min.js"></script>
<script type="text/javascript" src="js/i18n/grid.locale-cn.js"></script>
<script type="text/javascript" src="js/jquery.validate.min.js"></script>
<script type="text/javascript" src="js/utils.js"></script>
<script type="text/javascript" src="js/jquery-plus.js"></script>
<script type="text/javascript" src="js/common/material_detail_ctrl.js"></script>
<script type="text/javascript" src="js/common/pcs_editor.js"></script>
<script type="text/javascript" src="js/common/material_detail_pcs.js"></script>
<script type="text/javascript" src="js/inline/line_leader.js?v=3090"></script>
<script type="text/javascript" src="js/partial/consumable_application_edit.js"></script>

<title>${userdata.section_name} ${userdata.line_name}</title>
</head>
<body class="outer">
<div class="width-full" style="align:center;margin:auto;margin-top:16px;">
<div id="basearea" class="dwidth-full" style="margin:auto;">
	<jsp:include page="/header.do" flush="true">
		<jsp:param name="part" value="1"/>
	</jsp:include>
</div>

<div class="ui-widget-panel width-full" style="align:center;padding-top:16px;overflow-x: hidden;" id="body-3">
	
	<div class="ui-widget-header dwidth-full" style="align:center;padding-top:6px;padding-bottom:6px;margin-bottom:16px;text-align:center;">
		<span>${userdata.section_name} ${userdata.line_name}</span>
		<input class="ui-button" style="position:relative; left:10em;" id="attendance_button" value="当日出勤" type="button"/>
	</div>

<div style="width:1900px;">
<div id="workarea" class="dwidth-half" style="float:left;margin-left:24px;margin-bottom:16px;">
	<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser">
		<span class="areatitle">今日${userdata.section_name} ${userdata.line_name}计划</span>
		<style>label[for=plan_chk] .ui-button-text {font-size:12px;} </style>
		<input id="plan_chk" class="ui-button" value="1" type="checkbox"/><label for="plan_chk">全部仕挂</label>
		<a role="link" href="javascript:void(0)" class="HeaderButton areacloser">
			<span class="ui-icon ui-icon-circle-triangle-e"></span>
		</a>
	</div>
	<div class="ui-widget-content">
		<div style="padding:2px;padding-left:6px;padding-top:8px;height:36px;">
		<input id="expeditebutton" class="ui-button" value="加急" type="button"/>
		<input id="nogoodbutton" class="ui-button" value="处置不良" type="button"/>
		<input id="orderbutton" class="ui-button" value="零件订购" type="button"/>
		<input id="receivebutton" class="ui-button" value="零件签收" type="button"/>
		<input id="pcsbutton" class="ui-button" value="工程检查票查看" type="button"/>
		<input type="button" value="申请消耗品" class="ui-button" onclick="javascript:consumable_application_edit()"/>
		<input id="pxbutton" type="button" value="切线" class="ui-button" style="display:none;"/>
		</div>
		<table id="performance_list" class="leader_grid"></table>
	</div>
	<div class="ui-state-default ui-corner-bottom areaencloser"></div>
	<div class="clear"></div>
</div>

<div id="storagearea" style="float:left;margin-left:20px;margin-bottom:16px;">
	<div>
		<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser dwidth-half">
			<span class="areatitle">${userdata.section_name} ${userdata.line_name}当前仕挂台数</span>
		</div>
		<div class="ui-widget-content dwidth-half" style="text-align:center;padding-top:8px;padding-bottom:8px;cursor: pointer;">
			<span style="font-size:16px;"><label id="sikake"></label> 台</span>
		</div>
		<div class="clear areaencloser dwidth-half"></div>
	</div>

	<div>
		<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser dwidth-half">
			<span class="areatitle">${userdata.section_name} ${userdata.line_name}当前仕挂分布</span>
		</div>
		<div class="ui-widget-content dwidth-half">
			<div id="processing_container"></div>
		</div>
		<div class="ui-state-default ui-corner-bottom areaencloser dwidth-half"></div>
		</div>
	</div>
	<div class="clear"/>
	</div>

</div>
</div>

<div id="nogood_treat">
</div>

</body></html>