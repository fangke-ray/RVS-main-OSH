<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="framework.huiqing.common.util.CodeListUtils"%>
<%
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath()+"/";
%>
<base href="<%=basePath%>">
<style>
.ui-slider {
	position: relative;
	text-align: left;
}
.ui-slider .ui-slider-handle {
	position: absolute;
	z-index: 2;
	width: 1.2em;
	height: 1.2em;
	cursor: default;
}
.ui-slider-horizontal {
	height: .2em;
	margin-top: .2em;
}
.ui-slider-horizontal .ui-slider-handle {
	top: -.5em;
	margin-left: -.6em;
}
</style>
	<form>
		<table class="condform" id="pause-workreport-reason">
			<tbody>
				<tr>
				<td class="ui-state-default td-title">暂停理由</td>
				<td class="td-content" style="width:615px;">
					<select name="pause_reason" id="pause_reason" class="ui-widget-content">
${pause_reasons}
					</select>
				</td>
				</tr>
				<tr>
				<td class="ui-state-default td-title">备注</td>
				<td class="td-content">
					<input type="hidden" id="pause_comments" value='${pause_comments}'/>
					<select id="edit_comments_select" class="ui-widget-content" style="display:none;">
					</select>
					<textarea class="ui-widget-content" id="edit_comments" style="width:300px;height:150px;"></textarea>
				</td>
				</tr>
			</tbody>
		</table>
		<div style="height:24px;" id="pause-workreport-split">
			<div>
				进行时段分割：
			</div>
			<div id="splitter" style="width:80%;margin-left:16px;">
			</div>
			<div style="height: 32px; line-height: 32px;">
				分割时间： <span id="selectHM"></span> <span id="spareHM"></span>
			</div>
		</div>
	</form>
	

	