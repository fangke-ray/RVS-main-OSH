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
	text-align: center;
}
.anim_pause {
	animation-play-state: paused;
	-webkit-animation-play-state: paused;
	-moz-animation-play-state: paused;
}

@keyframes moveseconds {
	0% {top: 0;}
	100% {top: -160px;} 
}

@-webkit-keyframes moveseconds {
	0% {top: 0;}
	100% {top: -160px;} 
}

@-moz-keyframes moveseconds {
	0% {top: 0;}
	100% {top: -160px;} 
}

@keyframes movetenseconds {
	0% {top: 0;}
	100% {top: -96px;} 
}

@-webkit-keyframes movetenseconds {
	0% {top: 0;}
	100% {top: -96px;} 
}

@-moz-keyframes movetenseconds {
	0% {top: 0;}
	100% {top: -96px;} 
}

.roll_cell {
	top: 1px;
	height: 16px;
	overflow: hidden;
	position: relative;
	float: right;
}

.roll_seconds {
	line-height: 16px;
	width: 7px;
	text-align: center;
	position: absolute;
	top: 0;
	left: 0;
	-webkit-animation: moveseconds 10s steps(10, end) infinite;
	-moz-animation: moveseconds 10s steps(10, end) infinite;
	animation: moveseconds 10s steps(10, end) infinite;
}

.roll_tenseconds {
	line-height: 16px;
	width: 7px;
	text-align: center;
	position: absolute;
	top: 0;
	left: 0;
	-webkit-animation: movetenseconds 60s steps(6, end) infinite;
	-moz-animation: movetenseconds 60s steps(6, end) infinite;
	animation: movetenseconds 60s steps(6, end) infinite;
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
<script type="text/javascript" src="js/common/pcs_editor.js"></script>
<script type="text/javascript" src="js/inline/position_panel_snout.js"></script>
<script type="text/javascript" src="js/partial/consumable_application_edit.js"></script>
<script type="text/javascript">
${WORKINFO}
</script>
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
								<td class="ui-state-default td-title">课室</td>
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
							<tr>
								<td class="td-content-text" colspan="7" id="flowtext" style="text-align: left;"></td>
								<td class="ui-state-default td-title" style="text-align: center;">
									<input type="button" value="申请消耗品" class="ui-button" onclick="javascript:consumable_application_edit()"/>
								</td>
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
						<span class="areatitle">中断中作业信息</span>
					</div>
					<div class="ui-widget-content dwidth-half" style="height: 247px; overflow-y: auto; overflow-x: hidden;">
						<div id="waitings" style="margin: 20px;">
						</div>
					</div>
				</div>

				<div id="manualarea" style="float: right;">
					<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser dwidth-half">
						<span class="areatitle">开始新先端预制作业</span>
					</div>

					<div class="ui-widget-content dwidth-half" id="scanner_container" style="min-height: 247px;">
						<div class="ui-state-default td-title">扫描先端头来源</div>
						<input type="text" id="scanner_inputer" title="扫描前请点入此处" class="dwidth-half"></input>
						<div class="ui-state-default td-title">选择型号</div>
						<div style="height:88px; padding: 4px;"><select id="input_model_id" readonly></select></div>
						<div class="ui-state-default td-title">输入先端头序列号</div>
						<input type="text" id="input_snout_no" title="输入先端头序列号" class="dwidth-half"></input>
						<div style="height: 44px">
							<input type="button" class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="startbutton" value="开始" role="button" aria-disabled="false" style="float: right; right: 2px;">
						</div>
					</div>

				</div>

				<div class="clear areaencloser"></div>
			</div>

			<div class="ui-widget-content dwidth-full" id="material_details" style="min-height: 215px;">
					<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser dwidth-full">
						<span class="areatitle">作业中信息</span>
					</div>
				<table class="condform">
					<tbody>
						<tr>
							<td class="ui-state-default td-title">先端头来源</td>
							<td class="td-content-text"></td>
							<td class="ui-state-default td-title">型号</td>
							<td class="td-content-text"></td>
							<td class="ui-state-default td-title">先端头序列号</td>
							<td class="td-content-text"></td>
						</tr>
						<tr style="display: table-row;">
							<td class="ui-state-default td-title">开始时间</td>
							<td class="td-content-text"></td>
							<td class="ui-state-default td-title">作业标准时间</td>
							<td class="td-content-text"></td>
							<td class="ui-state-default td-title">作业经过时间</td>
							<td class="td-content-text" id="dtl_process_time"><div class="roll_cell"><div class="roll_seconds">0 1 2 3 4 5 6 7 8 9</div></div><div class="roll_cell"><div class="roll_tenseconds">0 1 2 3 4 5 6</div></div><label style="float:right;"></label></td>
						</tr>
						<tr>
							<td class="ui-state-default td-title">完成度</td>
							<td colspan="5" class="td-content-text">
								<div class="waiting tube" id="p_rate" style="height: 20px; margin: auto;"></div>
							</td>
						</tr>
						<tr>
						</tr>
					</tbody>
				</table>
				<!--table class="condform">
					<tr>
						<td colspan="2" class="ui-widget-header td-title">暂停信息</td>
					</tr>
					<tr>
						<td class="ui-state-default td-title">暂停时间</td>
						<td class="ui-state-default td-title" style="width: 660px;">暂停理由</td>
					</tr>
					<tr>
						<td class="td-content-text" style="width: 110px; text-align: center;">8:37 ～ 8:48</td>
						<td class="td-content-text">M6:教育培训</td>
					</tr>
					<tr>
						<td class="td-content-text" style="width: 110px; text-align: center;">8:48 ～ 9:02</td>
						<td class="td-content-text">T1:烘干</td>
					</tr>
				</table-->
				<div style="height: 44px">
					<input type="button" class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="finishbutton" value="完成" role="button" aria-disabled="false" style="float: right; right: 2px;">
					<input type="button" class="ui-button ui-widget ui-state-default ui-corner-all" id="breakbutton" value="异常中断" role="button" aria-disabled="false" style="float: right; right: 2px;">
					<input type="button" class="ui-button ui-widget ui-state-default ui-corner-all" id="stepbutton" value="正常中断" role="button" aria-disabled="false" style="float: right; right: 2px;">
					<input type="button" class="ui-button ui-widget ui-state-default ui-corner-all" id="pausebutton" value="暂停" role="button" aria-disabled="false" style="float: right; right: 2px;">
					<input type="button" class="ui-button ui-widget ui-state-default ui-corner-all" id="continuebutton" value="重开" role="button" aria-disabled="false" style="float: right; right: 2px;">
				</div>
			</div>

			<div id="manualdetailarea" style="margin-bottom: 16px;">
				<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser dwidth-full">
					<span class="areatitle">工程检查票</span>
				</div>
				<div class="ui-widget-content dwidth-full" id="ns_pcs">
					<div id="pcs_pages">
					</div>
					<div id="pcs_contents">
					</div>
				</div>
				<div class="ui-state-default ui-corner-bottom areaencloser dwidth-full"></div>
			</div>

		</div>
	</div>
	<div id="break_dialog"></div>
	<div id="comments_sidebar" style="display:none;">
		<div class="ui-widget-header ui-corner-top ui-helper-clearfix">
			<span class="areatitle icon-enter-2">提示相关信息</span>
		</div>
		<div class="comments_area">
		</div>
	</div>

</body>
</html>