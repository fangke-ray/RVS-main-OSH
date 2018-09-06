<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="framework.huiqing.common.util.CodeListUtils"%>
<%
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath()+"/";
%>
<base href="<%=basePath%>">
<script type="text/javascript" src="js/data/position-detail.js"></script>
<body>
	<div style="margin: auto;">

		<div id="position_detail_basearea" class="dwidth-middle">
			<div class="ui-widget-header ui-corner-top ui-helper-clearfix areaencloser dwidth-middle">
				<span class="areatitle">工位信息</span>
			</div>
			<div class="ui-widget-content dwidth-middle">
				<table class="condform">
					<tbody>
						<tr>
							<td class="ui-state-default td-title">课室</td>
							<td class="td-content-text"><label id="label_section_name"></label></td>
							<td class="ui-state-default td-title">工程</td>
							<td class="td-content-text"><label id="label_line_name"></label></td>
						<tr>
							<td class="ui-state-default td-title">工位号</td>
							<td class="td-content-text"><label id="label_process_code"></label></td>
							<td class="ui-state-default td-title">工位名称</td>
							<td class="td-content-text"><label id="label_position_name"></label></td>
						</tr>
						<tr>
							<td class="ui-state-default td-title">等待处理台数</td>
							<td class="td-content-text"><label id="label_waiting_count"></label></td>
							<td class="ui-state-default td-title"><label id="title_processed_count">年/月/日完成台数</label></td>
							<td class="td-content-text"><label id="label_processed_count"></label></td>
						</tr>
						<tr>
							<td class="ui-state-default td-title"><label id="title_oem_count">年/月/日代工台数</label></td>
							<td class="td-content-text"><label id="label_oem_count"></label></td>
							<td class="ui-state-default td-title"><label id="title_stop_count">年/月/日中止次数</label></td>
							<td class="td-content-text"><label id="label_stop_count"></label></td>
						</tr>
						<tr id="message_batch">
							<td class="ui-state-default td-title"><label>工位计时区分</label></td>
							<td class="td-content-text" colspan="3"><label>本工位的工时计时是采用标准平均工时。</label></td>
						</tr>
						<tr id="message_leader">
							<td class="ui-state-default td-title"><label>工位计时区分</label></td>
							<td class="td-content-text" colspan="3"><label>当由线长以上人员处理时，本工位的工时计时是采用标准平均工时。<br>当由操作人员处理时，采用实际作业时间计时。</label></td>
						</tr>
					</tbody>
				</table>
			</div>
			<div class="clear areaencloser dwidth-middle"></div>
		</div>
		
		<div class="dwidth-middle">
			<select id="search_operator_list" class="ui-widget-content"></select>
		</div>
		<div class="clear areacloser"></div>

		<div id="position_detail_listarea" class="dwidth-middle">
			<table id="position_detail_list"></table>
			<div id="position_detail_listpager"></div>
		</div>

		<div id="position_detail_dialog"></div>

		<div class="clear areacloser"></div>
	</div>

</body>
