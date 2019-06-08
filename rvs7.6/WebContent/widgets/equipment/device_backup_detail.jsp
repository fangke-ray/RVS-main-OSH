<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false"%>
<div id="setarea">
	<div class="ui-widget-content">
		<div class="ui-widget-content" style="
		    background: lightyellow;
		">
			<span style="padding-left:2em;">评价说明：<span style="font-size:larger;">◎</span>=本工程有替代品 <span style="font-size:larger;">○</span>=本工程以外有替代品 <span style="font-size:larger;">△</span>=限定条件下有替代 ×=无替代品重点管理对象</span>
		</div>
		<form id="addform" method="POST">
			<table class="condform">
				<tbody>
					<tr>
						<td class="ui-state-default td-title">管理编号</td>
				   		<td class="td-content">
							<label id="set_manage_code"></label>
							<input type="hidden" id="set_manage_id"/>
						</td>
						<td class="ui-state-default td-title">管理等级/状态</td>
				   		<td class="td-content">
							<label id="set_manage_level"></label>
							<label id="set_status"></label>
						</td>
					</tr>
					<tr>
						<td class="ui-state-default td-title">品名</td>
				   		<td class="td-content">
							<label id="set_device_type_name"/>
						</td>
						<td class="ui-state-default td-title">型号</td>
						<td class="td-content">
							<label id="set_model_name"/>
						</td>
					</tr>
					<tr>
						<td class="ui-state-default td-title">责任工程</td>
				   		<td class="td-content">
							<label id="set_line_name"/>
						</td>
						<td class="ui-state-default td-title">当前评价</td>
						<td class="td-content">
							<label id="set_evaluation"/>
						</td>
					</tr>
					<tr>
						<td class="ui-state-default td-title">对应</td>
						<td class="td-content" colspan="3">
							<textarea id="set_corresponding" class="ui-widget-content" style="width:90%;">
							</textarea>
						</td>
					</tr>
				</tbody>
			</table>
			<div style="height:44px">
<%
	String privacy = (String) request.getAttribute("privacy");
	boolean isTech = ("technology").equals(privacy);
	if (isTech) {
%>
				<input type="button" class="ui-button" id="db_submitbutton" value="提交更新" style="float:right;right:2px">
<%
	}
%>
			</div>
			<style>
				#pires{width:710px;}
				#pires th,#pires td {padding: 0 2px;}
				#pires button{background-color:white;border-color:black;width:72px;padding:0 0;}
				#pires button[usage="1"]{background-color:lightgreen;}
				#pires button[usage="0"]{background-color:yellow;}
<%
	if (!isTech) {
%>
				#pires button{pointer-events: none;border:0;}
<%
	}
%>
			</style>
			<table id="pires" class="condform">
				<thead>
					<th class="ui-state-default">管理编号</th><th class="ui-state-default">管理等级/状态</th><th class="ui-state-default">型号</th><th class="ui-state-default">责任工程</th><th class="ui-state-default">替换关系</th><th class="ui-state-default">反向替换关系</th>
				</thead>
				<tbody>
				</tbody>
			</table>
		</form>
	</div>
</div>