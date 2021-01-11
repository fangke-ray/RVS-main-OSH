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
<link rel="stylesheet" type="text/css" href="css/olympus/jquery-ui-1.9.1.custom.css">
<link rel="stylesheet" type="text/css" href="css/olympus/select2Buttons.css">
<style>
.click_start {
	display: none;
}
.waitForStart .click_start {
	display: block;
}
</style>

<script type="text/javascript" src="js/jquery-1.8.2.min.js"></script>
<script type="text/javascript" src="js/jquery-ui-1.9.1.custom.min.js"></script>
<script type="text/javascript" src="js/jquery.dialog.js"></script>
<script type="text/javascript" src="js/jquery.jqGrid.min.js"></script>
<script type="text/javascript" src="js/i18n/grid.locale-cn.js"></script>
<script type="text/javascript" src="js/jquery.select2buttons.js"></script>
<script type="text/javascript" src="js/jquery.validate.min.js"></script>
<script type="text/javascript" src="js/utils.js"></script>
<script type="text/javascript" src="js/jquery-plus.js"></script>
<script type="text/javascript" src="js/common/pcs_editor.js"></script>
<script type="text/javascript" src="js/qa/quality_assurance.js"></script>
<script type="text/javascript" src="js/partial/consumable_application_edit.js"></script>
<script type="text/javascript">
${WORKINFO}
</script>
<title>${qs_position_name}</title>
<%
	String privacy = (String) request.getAttribute("privacy");
	boolean islineLeader = ("lineLeader").equals(privacy);
%>
</head>
<body class="outer">
	<div class="width-full" style="align: center; margin: auto; margin-top: 16px;">

		<div id="basearea" class="dwidth-full" style="margin: auto;">
			<jsp:include page="/header.do" flush="true">
				<jsp:param name="part" value="2"/>
			</jsp:include>
		</div>

		<div class="ui-widget-panel ui-corner-all width-full" style="align: center; padding-top: 16px;" id="body-2">
			<div id="body-lft" style="width: 256px; float: left;">
				<jsp:include page="/appmenu.do" flush="true">
					<jsp:param name="linkto" value="品保作业"/>
				</jsp:include>
			</div>
			<div id="body-mdl" style="width: 1022px; float: left;">
				<div class="dwidth-middleright" style="margin-left: 8px;">
		<div id="uld_listarea">
			<table id="uld_list"></table>
			<div id="uld_listpager"></div>
			<div id="uld_listedit"></div>
			<input type="hidden" id="wk_position_id" value="${qs_position_id}"/>			
		</div>
		<div class="ui-widget-content areabase dwidth-middleright">
			<div id="executes" style="margin-left: 4px; margin-top: 4px;">
				<input type="button" value="申请消耗品" class="ui-button" onclick="javascript:consumable_application_edit()"/>
			</div>
		</div>

<%
Boolean peripheral = (Boolean) request.getAttribute("peripheral");
if (peripheral!=null && peripheral) {
%>
<%@include file="/widgets/position_panel/device_infect.jsp"%>
<%
}
%>

		<div id="pcsarea" style="display: none;">
			<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser dwidth-middleright">
				<span class="areatitle">工程检查票</span>
			</div>
			<div class="ui-widget-content dwidth-middleright">
				<div id="pcs_pages">
				</div>
				<div id="pcs_contents">
				</div>
			</div>
			<div class="clear ui-widget-content dwidth-middleright" style="height: 44px;">
				<input type="button" class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="pcscombutton" value="工程检查票确认" role="button" aria-disabled="false" style="float: right; right: 2px; margin: 4px;">
			</div>
		</div>

	<div id="executearea">
		<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser dwidth-middleright">
			<span class="areatitle">品保处理</span>
		</div>
		<div class="ui-widget-content dwidth-middleright">
			<div class="ui-widget-content dwidth-middleright" id="scanner_container">
				<div class="ui-state-default td-title">扫描录入区域</div>
				<input type="text" id="scanner_inputer" title="扫描前请点入此处" class="scanner_inputer" style="width: 986px;"></input>
				<div style="text-align: center;">
						<img src="images/barcode.png" style="margin: auto; width: 150px; padding-top: 4px;">
				</div>
			</div>
			<div class="ui-widget-content dwidth-middleright" id="material_details" style="display: none;">

				<form id="uploadform" method="POST">
					<table class="condform">
						<tbody>
							<tr>
								<td class="ui-state-default td-title">型号<input id="pauseo_material_id" type="hidden"/></td>
								<td class="td-content" id="show_model_name"></td>
								<td class="ui-state-default td-title">机身号</td>
								<td class="td-content" id="show_serial_no"></td>
							</tr>
							<tr>
								<td class="ui-state-default td-title">修理单号</td>
								<td class="td-content" id="show_sorc_no"></td>
								<td class="ui-state-default td-title">ESAS No.</td>
								<td class="td-content" id="show_esas_no"></td>
							</tr>
							<tr>
								<td class="ui-state-default td-title">同意时间</td>
								<td class="td-content" id="show_agreed_date"></td>
								<td class="ui-state-default td-title">总组完成时间</td>
								<td class="td-content" id="show_finish_time"></td>
							</tr>
							<tr>
								<td class="ui-state-default td-title">等级</td>
								<td class="td-content" id="show_level"></td>
								<td class="ui-state-default td-title">加急</td>
								<td class="td-content" id="show_scheduled_expedited"></td>
							</tr>
						</tbody>
					</table>
					<div style="height: 44px">
						<%
							if(islineLeader){
						%>
						<input type="button" class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="forbidbutton" value="检测不通过" role="button" aria-disabled="false" style="float: right; right: 2px">
						<%
							}
						%>
						<input type="button" class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="passbutton" value="检测通过" role="button" aria-disabled="false" style="float: right; right: 2px">
						<input type="button" class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="pausebutton" value="暂停" role="button" aria-disabled="false" style="float: right; right: 2px">
						<input type="button" class="ui-button ui-widget ui-state-default ui-corner-all" id="continuebutton" value="重开" role="button" aria-disabled="false" style="float: right; right: 2px; display : none;">
						<input type="button" class="ui-button" id="stepbutton" value="正常中断" style="float: right; right: 2px;display : none;">
					</div>
				</form>
			</div>
		</div>
		<div class="clear dwidth-middleright"></div>
	</div>

	<div class="clear dwidth-middleright"></div>

	<div id="exd_listarea">
		<table id="exd_list"></table>
		<div id="exd_listpager"></div>
		<div id="exd_listedit"></div>
	</div>
			</div>
		</div>
		<div class="clear areaencloser"></div>
	</div>
	<div id="break_dialog"></div>

	<input type="hidden" id="hidden_workstauts" value=""/>
	<div id="comments_dialog" style="display:none;width:576px;">
		<div class="ui-widget-header ui-corner-top ui-helper-clearfix">
			<span class="areatitle icon-enter-2"> 维修对象相关信息</span>
		</div>
		<textarea style="width:100%;height:6em;resize:none;border:1px solid rgb(170, 170, 170);padding:4px;" disabled readonly>
		</textarea>
	</div>
</body>
</html>