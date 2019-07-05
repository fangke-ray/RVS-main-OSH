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
<link rel="stylesheet" type="text/css" href="css/ui.jqgrid.css">

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
.firstMatchSnout {
	background-color:lightgreen;
}
</style>
<script type="text/javascript" src="js/jquery-1.8.2.min.js"></script>
<script type="text/javascript" src="js/jquery-ui-1.9.1.custom.min.js"></script>
<script type="text/javascript" src="js/jquery.dialog.js"></script>
<script type="text/javascript" src="js/jquery.select2buttons.js"></script>
<script type="text/javascript" src="js/jquery.validate.min.js"></script>

<script type="text/javascript" src="js/jquery.jqGrid.min.js"></script>
<script type="text/javascript" src="js/i18n/grid.locale-cn.js"></script>

<script type="text/javascript" src="js/utils.js"></script>
<script type="text/javascript" src="js/jquery-plus.js"></script>
<script type="text/javascript" src="js/common/pcs_editor.js"></script>
<script type="text/javascript" src="js/inline/position_panel.js"></script>
<script type="text/javascript" src="js/common/material_detail_ctrl.js"></script>
<script type="text/javascript" src="js/partial/consumable_application_edit.js"></script>

<title>1课NS部-先端分解再生</title>
</head>
<body class="outer">

	<div class="width-full" style="align: center; margin: auto; margin-top: 16px;">

		<div id="basearea" class="dwidth-full" style="margin: auto;">
			<jsp:include page="/header.do" flush="true">
				<jsp:param name="part" value="1"/>
			</jsp:include>
		</div>

<%
String px = (String) request.getAttribute("px");
%>

		<div class="ui-widget-panel width-full" style="align: center; padding-top: 16px;" id="body-pos">

			<div id="workarea">
				<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser dwidth-full">
					<span id="position_name" class="areatitle">工作信息</span> <span id="position_status" class="areatitle working_status ${((empty px) or (px eq 0)) ? 'simple' : ''}"></span>
					${(px eq 1) ? '<span id="px_status" class="areatitle working_status" style="color: #92D050;">A 线</span>' : ''}
					${(px eq 2) ? '<span id="px_status" class="areatitle working_status" style="color: #C86BC8;">B 线</span>' : ''}
					${(px eq 3) ? '<span id="px_status" class="areatitle working_status" style="color: #2FB1BE;">C 线</span>' : ''}
<%
Integer work_count_flg = (Integer) request.getAttribute("work_count_flg");
if (work_count_flg == 1) {
%>
					<span id="user_working_status" class="areatitle working_status" style="color:#92D050;width:80px;"></span>
					<a id="btn_foundry" role="link" href="javascript:void(0)" class="areacloser" style="float:left;">
						<span class="ui-icon ui-icon-transferthick-e-w"></span>
					</a>
					<input type="hidden" id="hidden_foundry_start_time" value="${foundry_start_time}"/>

				</div>
		<div class="if_message-dialog ui-warn-dialog">
			<div class="ui-dialog-titlebar ui-widget-header" style="height:24px;"><span>代工状态</span></div>
			<div style="margin-top: 1em;margin-bottom: 1em;"></div>
		</div>
<%
} else {
%>
				</div>
<%
}
%>
				<div class="ui-widget-content dwidth-full">
					<table class="condform">
						<tbody>
							<tr>
								<td class="ui-state-default td-title">课室</td>
								<td class="td-content-text">${userdata.section_name}</td>
								<td class="ui-state-default td-title">工程名</td>
								<td class="td-content-text">${userdata.line_name}</td>
								<td class="ui-state-default td-title">工位<input type="hidden" id="g_pos_id" value="${userdata.section_id}#${userPositionId}"/><input type="hidden" id="skip_position" value="${skip_position}"/><input type="hidden" id="g_process_code" value="${userdata.process_code}"/></td>
								<td class="td-content-text">${position_name}</td>
								<td class="ui-state-default td-title">操作人员</td>
								<td class="td-content-text" id="td_of_operator" work_count_flg="${userdata.work_count_flg}">${userdata.name}</td>
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
								<td class="ui-state-default td-title">维修流程提示</td>
								<td class="td-content-text" colspan="6" id="flowtext" style="text-align: left;"></td>
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
<%@include file="/widgets/position_panel/heap_division.jsp"%>
			</div>

			<div id="usesnoutarea" style="margin-bottom: 16px;display:none;">
				<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser dwidth-full">
					<span class="areatitle">使用先端组件</span>
				</div>
				<div class="ui-widget-content dwidth-full" id="select_snout">
					<!--label>使用先端组件可使【先端 1 工程】和【先端 2 工程】各自节省15分钟的直接工时。</label-->
					<table class="condform" id="snoutpane">
						<tbody>
							<tr>
								<td class="ui-state-default td-title">当前维修型号</td>
								<td class="td-content-text"></td>
								<td class="ui-state-default td-title">可使用先端头</td>
								<td class="td-content-text"><input type="text" readonly></input><input type="hidden" name="privacy" id="input_snout"></input></td>
								<td class="ui-state-default td-title">已使用先端头</td>
								<td class="td-content-text"><label type="text" id="used_snouts" /></td>
								<td class="ui-state-default td-title">先端头来源</td>
								<td class="td-content-text"><input type="text" id="snout_origin"></input></td>
							</tr>
						</tbody>
					</table>
					<div style="height: 44px">
						<!-- input type="button" class="ui-button-primary ui-button ui-widget ui-state-default ui-corner-all" id="usesnoutbutton" value="选择使用先端头" role="button" aria-disabled="false" style="float: right; right: 2px;"-->
						<input type="button" class="ui-button ui-widget ui-state-default ui-corner-all" id="unusesnoutbutton" value="取消使用先端头" role="button" aria-disabled="false" style="float: right; right: 2px;">
					</div>
				</div>
				<div class="ui-state-default ui-corner-bottom areaencloser dwidth-full"></div>
				<div class="referchooser ui-widget-content" tabindex="-1" style="z-index:80;">
					<table class="subform" id="snouts">
					<thead><th class="ui-state-default" style="padding: 0 0.5em;">先端头来源<br>维修编号</th><th class="ui-state-default" style="padding: 0 0.5em;">先端头管理号</th></thead>
					<tbody></tbody></table>
				</div>

				<div class="clear areaencloser"></div>
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
	<div id="process_dialog"></div>
	<div id="break_dialog"></div>
	<%@include file="/widgets/position_panel/countdown_unreach.jsp"%>
	<!--div id="comments_dialog" style="display:none;">
		<div class="ui-widget-header ui-corner-top ui-helper-clearfix">
			<span class="areatitle icon-enter-2"> 维修对象相关信息</span>
		</div>
		<textarea style="width:100%;height:6em;resize:none;border:0;padding:4px;" disabled readonly>
		</textarea>
	</div-->
	<div id="comments_sidebar" style="display:none;">
		<div class="ui-widget-header ui-corner-top ui-helper-clearfix">
			<span class="areatitle icon-enter-2">提示相关信息</span>
		</div>
		<div class="comments_area">
		</div>
	</div>
	<div id="consumables_dialog">
		<style>
		#consumables_list input[type=number] {
			text-align:right;
			width:3em;
			border: 1px solid darkgray;
		}	
		</style>
		<table id="consumables_list"></table>
		<div id="consumables_listpager"></div>
	</div>
		<textarea style="width:90%;height:6em;resize:none;" disabled readonly>
		</textarea>
	</div>
	<input type="hidden" id="hidden_workstauts" value=""/>
</body>
</html>