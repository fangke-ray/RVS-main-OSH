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
<link rel="stylesheet" type="text/css" href="css/olympus/jquery-ui-1.9.1.custom.css">
<link rel="stylesheet" type="text/css" href="css/olympus/select2Buttons.css">
<style>
.working_status {
	background-color:white;
	padding-left:6px;
	padding-right:6px;
}
.dm_select {
	position:absolute;
	width:200%;
	bottom: -12px;
	right: -120%;
}
.dm_select * {
	float : right;
}
.dm_select .select2Buttons ul {
    margin-top: 2px;
}
.waiting.tube {
	position:relative;
}
.click_start {
	position: absolute;
	right: -36px;
	bottom: -10px;
	display: none;
}
#waitings .click_start input {
	padding: 5px;
}
#waitings .waiting {
	position: relative;
}
#waitings .waiting:hover .click_start{
	display: block;
}
</style>
<script type="text/javascript" src="js/jquery-1.8.2.min.js"></script>
<script type="text/javascript" src="js/jquery-ui-1.9.1.custom.min.js"></script>
<script type="text/javascript" src="js/jquery.dialog.js"></script>
<script type="text/javascript" src="js/jquery.select2buttons.js"></script>
<script type="text/javascript" src="js/jquery.validate.min.js"></script>
<script type="text/javascript" src="js/utils.js"></script>
<script type="text/javascript" src="js/jquery-plus.js"></script>
<script type="text/javascript" src="js/inline/position_panel_with_result.js"></script>

<title>欢迎登录RVS系统</title>
</head>
<body class="outer">

	<div class="width-full" style="align: center; margin: auto; margin-top: 16px;">

		<div id="basearea" class="dwidth-full" style="margin: auto;">
			<jsp:include page="/header.do" flush="true">
				<jsp:param name="part" value="1"/>
			</jsp:include>
		</div>

		<div class="ui-widget-panel width-full" style="align: center; padding-top: 16px;" id="body-pos">

			<div id="workarea">
				<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser dwidth-full">
					<span id="position_name" class="areatitle">工作信息</span> <span id="position_status" class="areatitle working_status">${position.status}</span>
				</div>
				<div class="ui-widget-content dwidth-full">
					<table class="condform">
						<tbody>
							<tr>
								<td class="ui-state-default td-title">课室<!--div class="roll_cell"><div class="roll_tenseconds">0 1 2 3 4 5 6</div></div><div class="roll_cell"><div class="roll_seconds">0 1 2 3 4 5 6 7 8 9</div></div--></td>
								<td class="td-content-text">${userdata.section_name}</td>
								<td class="ui-state-default td-title">工程名</td>
								<td class="td-content-text">${userdata.line_name}</td>
								<td class="ui-state-default td-title">工位<input type="hidden" id="g_pos_id" value="${userdata.section_id}#${userPositionId}"/><input type="hidden" id="g_process_code" value="${userdata.process_code}"/></td>
								<td class="td-content-text">${position_name}</td>
								<td class="ui-state-default td-title">操作人员</td>
								<td class="td-content-text">${userdata.name}</td>
							</tr>

							<tr style="display: table-row;">
								<td class="ui-state-default td-title">工作时间</td>
								<td class="td-content-text" id="p_run_cost">${position.run_cost}</td>
								<td class="ui-state-default td-title">暂停时间</td>
								<td class="td-content-text"></td>
								<td class="ui-state-default td-title">操作时间</td>
								<td class="td-content-text" id="p_operator_cost">${position.operator_cost}</td>
								<td class="ui-state-default td-title">完成件数</td>
								<td class="td-content-text" id="p_finish_count">${position.finish_count}台</td>
							</tr>
							<tr>
								<td class="ui-state-default td-title">代工件数</td>
								<td class="td-content-text">${position.support_count}台</td>
								<td class="ui-state-default td-title">暂停次数</td>
								<td class="td-content-text">${position.pause_count}台次</td>
								<td class="ui-state-default td-title">中断次数</td>
								<td class="td-content-text">${position.break_count}台次</td>
								<td class="ui-state-default td-title">等待件数</td>
								<td class="td-content-text" id="p_waiting_count">${position.waiting_count}台</td>
							</tr>
							<tr id="toInfect" style="display:none;">
								<td class="ui-state-default td-title" style="background:#f9ec54;">待处理点检项目</td>
								<td class="td-content-text" colspan="7" id="infecttext" style="text-align: left;"></td>
							</tr>
						</tbody>
					</table>
				</div>
				<div class="clear areaencloser"></div>
			</div>

			<div class="dwidth-full">
				<div id="storagearea" style="float: left;">
					<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser dwidth-half">
						<span class="areatitle">等待区信息</span>
						<input type="button" class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="packagebutton" value="通箱全部投入" style="float:right;display:none;" role="button" aria-disabled="false">
						<input type="button" class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="rcbutton" value="备品全部投入" style="float:right;display:none;" role="button" aria-disabled="false">
					</div>
					<div class="ui-widget-content dwidth-half" style="height: 215px; overflow-y: auto; overflow-x: hidden;">
						<div id="waitings" style="margin: 20px;">
						</div>
					</div>
				</div>

				<div id="manualarea" style="float: right;">
					<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser dwidth-half">
						<span class="areatitle">维修对象信息 </span>
					</div>

					<div class="ui-widget-content dwidth-half" id="scanner_container" style="min-height: 215px;">
						<div class="ui-state-default td-title">扫描录入区域</div>
						<input type="text" id="scanner_inputer" title="扫描前请点入此处" class="scanner_inputer dwidth-half"></input>
						<div style="text-align: center;">
							<img src="images/barcode.png" style="margin: auto; width: 150px; padding-top: 4px;">
						</div>
					</div>

				</div>

				<div class="clear areaencloser"></div>
			</div>

			<div id="workgrouparea" style="margin-bottom: 16px;">
				<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser dwidth-full">
					<span class="areatitle">处理中维修对象</span><span class="areatitle" id="s_t"></span>
				</div>
				<div class="ui-widget-content dwidth-full" style="overflow-x: hidden;">
					<div id="workings" style="min-height: 215px; max-height: 430px; overflow-y: auto; overflow-x: hidden; margin: 20px;">
					</div>
					<div style="height: 44px">
						<input type="button" class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="finishbutton" value="一同完成" role="button" aria-disabled="false" style="float: right; margin-right: 6px;">
<% 
	String oManageNo = (String) request.getAttribute("oManageNo");
	if (oManageNo != null) {
 %>
<style>
	.device_manage_select {
		padding: 0 4px;float: right; margin-right: 20px; height: 32px;line-height:32px;border: 1px solid rgb(147, 195, 205);
	}
	.device_manage_item {
		padding: 0 4px;float: right; height: 32px;line-height:32px;
	}
</style>
<div id="dm_select_all">
<%=oManageNo%>
</div>
<%
	}
 %>
					</div>
				</div>
				<div class="clear areaencloser"></div>
			</div>

			<!-- div id="resultlistarea" style="margin-bottom: 16px;">
				<table id="resultlist"></table>
				<div id="resultlistpager"></div>
			</div -->

<div id="functionarea" class="dwidth-full" style="margin:auto;">
	<div class="ui-widget-header ui-corner-all ui-helper-clearfix areabase bar_fixed">
		<div id="executes" style="margin-left:4px;margin-top:4px;">
			<input type="button" class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="reportbutton" value="临时生成当日报表" role="button" aria-disabled="false" style="float: right; right: 2px">
		</div>
	</div>
	<div class="clear areaencloser"></div>
</div>

		</div>
	</div>
	<div id="break_dialog"></div>
	<div id="pop_detail"></div>
	<div id="comments_sidebar" style="display:none;">
		<div class="ui-widget-header ui-corner-top ui-helper-clearfix">
			<span class="areatitle icon-enter-2">提示相关信息</span>
		</div>
		<div class="comments_area">
		</div>
	</div>
</body>
</html>