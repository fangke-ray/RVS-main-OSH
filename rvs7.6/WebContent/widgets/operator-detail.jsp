<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="framework.huiqing.common.util.CodeListUtils"%>
<%
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath()+"/";
%>
<base href="<%=basePath%>">
<body>
<style>
	.od-leak_cell 
	{
	background-color : #FFA050;
	}
</style>
	<div style="margin: auto;">

		<div id="operator_detail_basearea" class="dwidth-middle">
			<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser dwidth-middle">
				<span class="areatitle">工作日报</span>
			</div>
			<div class="ui-widget-content dwidth-middle">
				<table class="condform">
					<tbody>
						<tr>
							<td class="ui-state-default td-title">日期</td>
							<td class="td-content"><label id="label_action_time"></label></td>
							<td class="ui-state-default td-title">工程名</td>
							<td class="td-content"><label id="label_line_name"></label></td>
						<tr>
							<td class="ui-state-default td-title">工位号</td>
							<td class="td-content"><label id="label_process_code"></label></td>
							<td class="ui-state-default td-title">姓名</td>
							<td class="td-content"><label id="label_operator_name"></label></td>
						</tr>
					</tbody>
				</table>
			</div>
			<div class="clear areaencloser dwidth-middle"></div>
		</div>


		<div id="operator_detail_listarea" class="dwidth-middle">
			<table id="operator_detail_list"></table>
			<div id="operator_detail_listpager"></div>
		</div>
		
		<div id="functionarea" class="dwidth-middle" style="margin: auto;">
				<div class="ui-widget-header ui-corner-all ui-helper-clearfix areabase">
					<div id="executes" style="margin-left: 4px; margin-top: 4px;">
						<input type="button" class="ui-button" id="reportbutton" value="输出工作日报" style="float: left;left: 2px;" />
					</div>
				</div>
				<div class="clear areaencloser"></div>
			</div>

		<div id="operator_detail_zangyo" class="dwidth-middle">
			<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser dwidth-middle">
				<span class="areatitle">加班登记</span>
			</div>
			<div class="ui-widget-content dwidth-middle">
			<input type="hidden" id="can_edit_overtime">
				<table class="condform">
					<tbody>
						<tr>
							<td class="ui-state-default td-title">开始</td>
							<td class="td-content">
								<label id="label_overtime_start"></label>
								<input type="text" id="edit_overtime_start" value=""/>
							</td>
							<td class="ui-state-default td-title">结束</td>
							<td class="td-content">
								<label id="label_overtime_end"></label>
								<input type="text" id="edit_overtime_end" />
							</td>
						<tr>
							<td class="ui-state-default td-title">记号</td>
							<td class="td-content">
								<label id="label_overtime_reason"></label>
								<select id="edit_overtime_reason">
									<%=CodeListUtils.getSelectOptions("plan_overwork_reason", null, "", false) %>
								</select>
							</td>
							<td class="ui-state-default td-title">加班理由</td>
							<td class="td-content">
								<label id="label_overtime_comment"></label>
								<input type="text" id="edit_overtime_comment" />
							</td>
						</tr>
					</tbody>
				</table>
			</div>
			<div class="clear areaencloser dwidth-middle"></div>
		</div>

		<div id="operator_detail_dialog"></div>

		<div class="clear areacloser"></div>
	</div>
</body>
