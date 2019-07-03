<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false"%>
<style>
.djr_comment {
	width: 50em;
	height: 6em;
}
#djr_photo {
	max-width:320px;
	max-height:276px;
}
#djr_repairarea .ui-widget-content textarea.ui-state-disabled {
	opacity : 1;
}
</style>
<div id="djr_repairarea">
<%
	String privacy = (String) request.getAttribute("privacy");
	boolean isTech = ("technology").equals(privacy);
%>
	<div class="ui-widget-content">
		<form method="POST">
			<table class="condform">
				<tbody>
					<tr>
						<td class="ui-state-default td-title">责任工程</td>
				   		<td class="td-content">
							<label id="djr_line_name"></label>
						</td>
						<td class="ui-state-default td-title">修理依赖者</td>
				   		<td class="td-content">
							<label id="djr_submitter_name"></label>
						</td>
						<td class="ui-state-default td-title">报修时间</td>
				   		<td class="td-content">
							<label id="djr_submit_time"></label>
						</td>
					</tr>
					<tr>
						<td class="ui-state-default td-title">管理编号</td>
				   		<td class="td-content">
							<label id="djr_manage_code"></label>
						</td>
						<td class="ui-state-default td-title">设备工具名</td>
				   		<td class="td-content">
							<label id="djr_object_name"></label>
						</td>
						<td class="ui-state-default td-title">型号</td>
						<td class="td-content">
							<label id="djr_model_name"></label>
						</td>
					</tr>
					<tr>
						<td class="ui-state-default td-title">故障现象</td>
				   		<td class="td-content" colspan="3">
							<textarea class="djr_comment ui-widget-content" id="djr_phenomenon"></textarea>
						</td>
						<td class="ui-state-default td-title">修理担当</td>
						<td class="td-content">
							<label id="djr_maintainer_name"></label>
						</td>
					</tr>
					<tr id="djr_costs">
						<td class="ui-state-default td-title">设备更换部件</td>
				   		<td class="td-content" colspan="3">
							<textarea class="djr_comment ui-widget-content" id="djr_consumable"></textarea>
						</td>
						<td class="ui-state-default td-title" colspan="2">
							故障相关照片
							<input type="button" class="ui-button" id="djr_uploadphotobutton" value="上传" style="padding:0 0.2em;">
							<input type="file" name="file" id="djr_update_photo" style="display:none;">
							<input type="button" class="ui-button" id="djr_delphotobutton" value="删除" style="padding:0 0.2em;">
						</td>
					</tr>
					<tr>
						<td class="ui-state-default td-title">故障和原因</td>
				   		<td class="td-content" colspan="3">
							<textarea class="djr_comment ui-widget-content" id="djr_fault_causes">
							</textarea>
						</td>
						<td class="td-content" colspan="2" rowspan="3">
							<img id="djr_photo"/>
						</td>
					</tr>
					<tr>
						<td class="ui-state-default td-title">修理对策和方向</td>
				   		<td class="td-content" colspan="3">
							<textarea id="djr_countermeasure" class="djr_comment ui-widget-content">
							</textarea>
						</td>
					</tr>
					<tr>
						<td class="ui-state-default td-title">备注</td>
				   		<td class="td-content" colspan="3">
							<textarea id="djr_comment" class="djr_comment ui-widget-content">
							</textarea>
						</td>
					</tr>
				</tbody>
			</table>
			<div style="height:44px">
				<input type="button" class="ui-button" id="djr_cancelbutton" value="取消" style="float:right;right:2px;">
<%
	if (isTech) {
%>
				<input type="button" class="ui-button" id="djr_submitbutton" value="提交" style="float:right;right:2px;">
<%
	}
%>
			</div>
		</form>
	</div>
</div>